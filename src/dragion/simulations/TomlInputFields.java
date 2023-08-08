package dragion.simulations;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dragion.physics.UniformFieldOscillatingKernel;
import dragion.util.TomlUtil;
import lestat.kernels.Kernel;
import lestat.solvers.CompositeSolver;
import lestat.solvers.FloatingConstraints;
import lestat.surfaces.BiasedConductor;
import lestat.surfaces.Dielectric;
import lestat.surfaces.ElectrostaticObject;
import lestat.surfaces.FloatingConductor;
import lestat.surfaces.SegmentedConductor;
import mowgli.Constants;
import mowgli.diagnostics.Diagnostic;
import mowgli.diagnostics.ScalarDiagnostic;
import mowgli.diagnostics.VectorDiagnostic;
import mowgli.surfaces.ArraySurface.NonTriangleFaceException;
import mowgli.util.MathUtil;
import mowgli.util.PolyDataModifier;
import mowgli.util.VtkUtil;
import vtk.vtkCellData;
import vtk.vtkDoubleArray;
import vtk.vtkNativeLibrary;
import vtk.vtkPolyData;

public class TomlInputFields
{

	protected static final String OBJECTS = "objects";
	protected static final String GEOMETRY = "geometry";
	protected static final String SCALE = "scale";
	protected static final String ROTATION = "rotation";
	protected static final String POSITION = "position";
	protected static final String TYPE = "type";
	protected static final String BIASED = "biased";
	protected static final String FLOATING = "floating";
	protected static final String DIELECTRIC = "dielectric";
	protected static final String SEGMENTED = "segmented";
	protected static final String FOLLOWING = "following";
	protected static final String POTENTIAL = "potential";
	protected static final String CHARGE = "charge";
	//	protected static final String NOACCUM = "noaccum";
	protected static final String EPSR = "epsr";
	protected static final String DOMAIN = "domain";
	protected static final String EEXT = "eext";
	protected static final String DIAGNOSTICS = "diagnostics";
	protected static final String POINT = "point";
	protected static final String LINE = "line";
	//	static final String PLANE = "plane";
	//	static final String BOX = "volume";	// note that volume is probably not a viable diagnostic type given difficulties with visualization; plane is probably highest-dimensional geometry to sample on and output
	protected static final String VALUE = "value";
	protected static final String EFIELD = "efield";
	protected static final String START = "start";
	protected static final String END = "end";
	protected static final String NPTS = "npts";
	//	static final String SPECIAL = "special";

	//	static final String TEMPORAL = "temporal";
	//	static final String PARTICLECOUNT = "particlecount";

	//	static final String SOURCE = "source";
	//	static final String TRANSFORM = "transform";
	//	static final String OUTPUT = "output";
	//	static final String CONSOLE = "console";

	static final public String phisegArrayName = "phiseg";

	static
	{
		vtkNativeLibrary.LoadAllNativeLibraries();
	}

	//	final String entrypoint;
	protected final BiMap<String, ElectrostaticObject> objects = HashBiMap.create();
	protected final FloatingConstraints constraints = new FloatingConstraints();
	protected final List<Kernel> externalElectricFields = new ArrayList<>();
	protected final BiMap<String, Diagnostic<?>> diagnostics = HashBiMap.create();
	//	final Map<String, String> diagOutType = new HashMap<>();
	//	final Map<String, vtkPolyData> geometry = new HashMap<>();	
	protected final CompositeSolver solver = new CompositeSolver();
	//	double dt = 1;

	protected final TomlParseResult parseResult;

	public TomlInputFields(Path inputfile)
	{
		try
		{
			parseResult = Toml.parse(inputfile);
			parseResult.errors().forEach(error -> System.err.println(error.toString()));
			//			//
			//			entrypoint = parseResult.getString("simulation.entrypoint");
			//
			initObjects();
			initFields();
			initDiagnostics(solver);
		}
		catch (IOException e)
		{
			throw new Error(e);
		}
	}

	public String getString(String key)
	{
		return parseResult.getString(key);
	}

	public double getDouble(String key)
	{
		return TomlUtil.scalar(parseResult.get(key));
	}

	public boolean getBoolean(String key)
	{
		return TomlUtil.bool(key);
	}

	public int getInt(String key)
	{
		return TomlUtil.integer(parseResult.get(key));
	}

	public Vector3D getVector(String key)
	{
		return TomlUtil.vector(parseResult.get(key));
	}

	public boolean has(String key)
	{
		return parseResult.contains(key);
	}

	//	public String getEntryPoint()
	//	{
	//		return entrypoint;
	//	//	}
	//	public void initGeometry(TomlParseResult parseResult)
	//	{
	//		TomlTable allGeometrySpecs = parseResult.getTable(GEOMETRY);
	//		
	//		for (String name : allGeometrySpecs.keySet())
	//		{
	//			TomlTable geomSpec = allGeometrySpecs.getTable(name);
	//			
	//			String source = geomSpec.getString(SOURCE);
	//			
	//			final PolyDataModifier builder;
	//			if (VtkUtil.isIcosphereString(source))
	//			{
	//				builder = PolyDataModifier.from(VtkUtil.icosphere(source));
	//			}
	//			else
	//			{
	//				builder = PolyDataModifier.from(Paths.get(source));
	//			}
	//
	//			TomlArray transform = geomSpec.getArray(TRANSFORM);
	//			for (int i =0; i<transform.size(); i++)
	//			{
	//				
	////				//
	////				final Vector3D scale;
	////				if (objectSpec.contains(SCALE))
	////				{
	////					Object scaleSpec = objectSpec.get(SCALE);
	////					scale = TomlUtil.vector(scaleSpec);
	////				}
	////				else
	////				{
	////					scale = new Vector3D(1, 1, 1);
	////				}
	//
	//			}
	//			
	//		}
	//		
	//	}

	public void initDiagnostics(CompositeSolver solver)
	{
		TomlTable allDiagnosticsSpecs = parseResult.getTable(DIAGNOSTICS);

		if (allDiagnosticsSpecs == null)
		{
			return;
		}

		for (String name : allDiagnosticsSpecs.keySet())
		{
			TomlTable diagSpec = allDiagnosticsSpecs.getTable(name);

			List<Vector3D> positions = new ArrayList<>();

			String type = diagSpec.getString(TYPE);
			if (type.equals(POINT))
			{
				positions.add(TomlUtil.vector(diagSpec.get(POSITION)));
			}
			else if (type.equals(LINE))
			{
				Vector3D start = TomlUtil.vector(diagSpec.get(START));
				Vector3D end = TomlUtil.vector(diagSpec.get(END));
				int npts = TomlUtil.integer(diagSpec.get(NPTS));

				//
				Vector3D[] pts = MathUtil.linspace(start, end, npts);
				for (int i = 0; i < npts; i++)
				{
					positions.add(pts[i]);
				}
			}
			//			else if (type.equals(TEMPORAL))
			//			{
			//				// do nothing; no points to add
			//			}
			else
			{
				throw new Error("Unknown diagnostic type: " + type);
			}

			String value = diagSpec.getString(VALUE);
			if (value.equals(POTENTIAL))
			{
				diagnostics.put(name, new ScalarDiagnostic(positions, (r) -> solver.synthesizeElectricPotential(r)));
			}
			else if (value.equals(EFIELD))
			{
				diagnostics.put(name, new VectorDiagnostic(positions, (r) -> solver.synthesizeElectricField(r)));
			}
			//			else if (value.equals(PARTICLECOUNT))	// note type comparison here not value
			//			{
			//				if (!type.equals(TEMPORAL))
			//				{
			//					throw new Error("Diagnostic value="+PARTICLECOUNT+" must have type="+TEMPORAL+" not type="+type+" as specified");
			//				}
			//				diagnostics.put(name, )
			//			}
			else
			{
				throw new Error("Unknown diagnostic value: " + value);
			}

			//			if (!diagSpec.contains(OUTPUT))
			//			{
			//				diagOutType.put(name, CONSOLE);
			//			}
			//			else
			//			{
			//				String output = diagSpec.getString(OUTPUT);
			//				if (output.equals(CONSOLE))
			//				{
			//					diagOutType.put(name, output);
			//				}
			//				else
			//				{
			//					throw new Error("Unknown diagnostic output: " + output);
			//				}
			//
			//			}
		}
	}

	//	public void initDt(TomlParseResult parseResult)
	//	{
	//		dt = TomlUtil.scalar(parseResult.get("time.dt"));
	//	}

	public void initObjects()
	{
		TomlTable allObjectSpecs = parseResult.getTable(OBJECTS);
		//
		for (String name : allObjectSpecs.keySet())
		{
			TomlTable objectSpec = allObjectSpecs.getTable(name);
			//

			List<String> geomstrings = new ArrayList<>();
			if (objectSpec.get(GEOMETRY) instanceof TomlArray)
			{
				TomlArray arr = (TomlArray) objectSpec.get(GEOMETRY);
				for (int i = 0; i < arr.size(); i++)
				{
					geomstrings.add(arr.getString(i));
				}
			}
			else
			{
				geomstrings.add(objectSpec.getString(GEOMETRY));
			}

			vtkPolyData[] parts = new vtkPolyData[geomstrings.size()];
			for (int i = 0; i < parts.length; i++)
			{
				String geomSpec = geomstrings.get(i);
				if (VtkUtil.isIcosphereString(geomSpec))
				{
					parts[i] = VtkUtil.icosphere(geomSpec);
				}
				else
				{
					parts[i] = VtkUtil.read(Paths.get(geomSpec));
				}
			}

			//
			final Vector3D scale;
			if (objectSpec.contains(SCALE))
			{
				Object scaleSpec = objectSpec.get(SCALE);
				scale = TomlUtil.vector(scaleSpec);
			}
			else
			{
				scale = new Vector3D(1, 1, 1);
			}
			//
			final Vector3D rotaxis;
			final double rotangle;
			final Vector3D rotorigin;
			if (objectSpec.contains(ROTATION))
			{
				TomlArray rotaxisSpec = objectSpec.getArray(ROTATION);
				double axisx = TomlUtil.scalar(rotaxisSpec.get(0));
				double axisy = TomlUtil.scalar(rotaxisSpec.get(1));
				double axisz = TomlUtil.scalar(rotaxisSpec.get(2));
				double angle = TomlUtil.scalar(rotaxisSpec.get(3));
				rotaxis = new Vector3D(axisx, axisy, axisz);
				rotangle = angle;
				if (rotaxisSpec.size() > 4)
				{
					double ox = TomlUtil.scalar(rotaxisSpec.get(4));
					double oy = TomlUtil.scalar(rotaxisSpec.get(5));
					double oz = TomlUtil.scalar(rotaxisSpec.get(6));
					rotorigin = new Vector3D(ox, oy, oz);
				}
				else
				{
					rotorigin = Vector3D.ZERO;
				}
			}
			else
			{
				rotaxis = Vector3D.PLUS_K;
				rotangle = 0;
				rotorigin = Vector3D.ZERO;
			}
			//
			final Vector3D position;
			if (objectSpec.contains(POSITION))
			{
				TomlArray positionSpec = objectSpec.getArray(POSITION);
				double tx = TomlUtil.scalar(positionSpec.get(0));
				double ty = TomlUtil.scalar(positionSpec.get(1));
				double tz = TomlUtil.scalar(positionSpec.get(2));
				position = new Vector3D(tx, ty, tz);
			}
			else
			{
				position = Vector3D.ZERO;
			}
			//
			vtkPolyData polydata = PolyDataModifier.from(VtkUtil.aggregate(parts)).scale(scale).rotate(rotaxis, rotangle, rotorigin).translate(position).build();

			try
			{
				//
				String typeSpec = objectSpec.getString(TYPE);
				final ElectrostaticObject obj;
				if (typeSpec.equals(FLOATING))
				{
					obj = new FloatingConductor(polydata);

					boolean hasPotential = objectSpec.contains(POTENTIAL);
					boolean hasCharge = objectSpec.contains(CHARGE);
					//				boolean hasNoaccum = objectSpec.contains(NOACCUM);

					if (!hasPotential && !hasCharge)
					{
						((FloatingConductor) obj).setTotalCharge(0);
					}
					else if (!hasPotential && hasCharge)
					{
						double charge = TomlUtil.scalar(objectSpec.get(CHARGE));
						((FloatingConductor) obj).setTotalCharge(charge);
					}
					else if (hasPotential && !hasCharge)
					{
						double potential = TomlUtil.scalar(objectSpec.get(POTENTIAL));
						constraints.add(((FloatingConductor) obj), potential);
					}
					else
					{
						throw new Error("Cannot specify both potential and charge for a floating conductor");
					}

					//				if (hasNoaccum)
					//				{
					//					boolean noaccum = objectSpec.getBoolean(NOACCUM);
					//					if (noaccum)
					//					{
					//						((FloatingConductor)obj).disableAccumulation();
					//					}
					//				}
				}
				else if (typeSpec.equals(BIASED))
				{
					obj = new BiasedConductor(polydata);
					if (objectSpec.contains(POTENTIAL))
					{
						double phi = TomlUtil.scalar(objectSpec.get(POTENTIAL));
						((BiasedConductor) obj).setPotential(phi);
					}
					else
					{
						((BiasedConductor) obj).setPotential(0);
					}
				}
				else if (typeSpec.equals(DIELECTRIC))
				{
					final double epsr;
					if (objectSpec.contains(EPSR))
					{
						epsr = TomlUtil.scalar(objectSpec.get(EPSR));
					}
					else
					{
						epsr = 1;
					}

					obj = new Dielectric(polydata, epsr * Constants.eps0);
				}
				else if (typeSpec.equals(SEGMENTED))
				{
					vtkCellData celldata = polydata.GetCellData();
					if (celldata.HasArray(phisegArrayName) == 0)
					{
						throw new Error("Segmented conductor geometry file must have a cell array named \"" + phisegArrayName + "\"; run PotFile on raw geometry.");
					}

					vtkDoubleArray phiseg = (vtkDoubleArray) celldata.GetArray(phisegArrayName);

					obj = new SegmentedConductor(polydata);
					for (int f = 0; f < obj.getNumberOfFaces(); f++)
					{
						((SegmentedConductor) obj).setFacePotential(f, phiseg.GetValue(f));
					}

				}
				else
				{
					throw new Error("Unknown object type: " + typeSpec);
				}

				objects.put(name, obj);
			}
			catch (NonTriangleFaceException e)
			{
				throw new Error("Surface \"" + name + "\"", e);
			}
		}

		for (String followerName : allObjectSpecs.keySet())
		{
			TomlTable objectSpec = allObjectSpecs.getTable(followerName);

			if (objectSpec.contains(FOLLOWING))
			{
				String leaderName = objectSpec.getString(FOLLOWING);

				if (followerName == leaderName)
				{
					throw new Error("Object '" + followerName + "' cannot follow its own voltage");
				}

				//				if (leader != null)
				//				{
				//					throw new Error("Only one leader/follower pair is allowed, for now");
				//				}

				ElectrostaticObject leaderObj = objects.get(leaderName);
				ElectrostaticObject followerObj = objects.get(followerName);

				if (!(leaderObj instanceof FloatingConductor))
				{
					throw new Error("Voltage follower '" + followerName + "' cannot follow '" + leaderName + "' which is a " + leaderObj.getClass().getName() + "; it can only follow a " + FloatingConductor.class.getName());
				}

				if (!(followerObj instanceof BiasedConductor))
				{
					throw new Error("Voltage follower '" + followerName + "' must be a " + BiasedConductor.class.getName());
				}

				followers.add(new LeaderFollowerPair((FloatingConductor) leaderObj, (BiasedConductor) followerObj));
			}
		}

	}

	List<LeaderFollowerPair> followers = new ArrayList<>();

	public int getNumberOfFollowers()
	{
		return followers.size();
	}

	public LeaderFollowerPair getFollower(int i)
	{
		return followers.get(i);
	}

	public static class LeaderFollowerPair
	{
		FloatingConductor leader;
		BiasedConductor follower;

		public LeaderFollowerPair(FloatingConductor leader, BiasedConductor follower)
		{
			this.leader = leader;
			this.follower = follower;
		}

		public BiasedConductor getFollower()
		{
			return follower;
		}

		public FloatingConductor getLeader()
		{
			return leader;
		}
	}

	public boolean hasFollowers()
	{
		return !followers.isEmpty();
	}

	static class FieldComponent
	{
		Vector3D magnitude;
		Vector3D gaugept;
		double frequency;

		public FieldComponent(TomlArray arr)
		{
			if (arr.size() >= 3)
			{
				magnitude = TomlUtil.vector(arr);
				gaugept = Vector3D.ZERO;
				frequency = 0;
			}

			if (arr.size() == 4)
			{
				frequency = TomlUtil.scalar(arr.get(3));
			}

			if (arr.size() >= 6)
			{
				double gx = TomlUtil.scalar(arr.get(3));
				double gy = TomlUtil.scalar(arr.get(4));
				double gz = TomlUtil.scalar(arr.get(5));
				gaugept = new Vector3D(gx, gy, gz);
			}

			if (arr.size() == 7)
			{
				frequency = TomlUtil.scalar(arr.get(6));
			}

			if (arr.size() != 3 && arr.size() != 4 && arr.size() != 6 && arr.size() != 7)
			{
				throw new Error("Need 3 values to specify external field vector, or 4 for field and electrostatic frequency, or 6 for field and gauge point, or 7 or field, gauge, and frequency");
			}
		}

		public double getFrequency()
		{
			return frequency;
		}

		public Vector3D getGaugePoint()
		{
			return gaugept;
		}

		public Vector3D getMagnitude()
		{
			return magnitude;
		}
	}

	public void initFields()
	{
		if (parseResult.contains(DOMAIN))
		{

			TomlTable allFieldSpecs = parseResult.getTable(DOMAIN);
			//
			if (allFieldSpecs.contains(EEXT))
			{
				TomlArray arr = allFieldSpecs.getArray(EEXT);

				FieldComponent comp = new FieldComponent(arr);

				Kernel eext = new UniformFieldOscillatingKernel(comp.getMagnitude(), comp.getGaugePoint(), comp.getFrequency());

				externalElectricFields.add(eext);
			}
			else
			{
				//eext = new ZeroFieldKernel();
			}
		}
		else
		{

		}
	}

	//	public Map<String, String> getDiagnosticOuputTypes()
	//	{
	//		return diagOutType;
	//	}

	public BiMap<String, ElectrostaticObject> getObjects()
	{
		return objects;
	}

	public FloatingConstraints getConstraints()
	{
		return constraints;
	}

	public List<Kernel> getExternalElectricFields()
	{
		return externalElectricFields;
	}

	public CompositeSolver getSolver()
	{
		return solver;
	}

	public BiMap<String, Diagnostic<?>> getDiagnostics()
	{
		return diagnostics;
	}

	//	public static void main(String[] args) throws Exception
	//	{
	//		Path tomlfile = Paths.get(TomlInputFields.class.getResource("test.toml").getPath());
	//		TomlInputFields input = new TomlInputFields(tomlfile);
	//
	//		BiMap<String, ElectrostaticObject> objs = input.getObjects();
	//		FloatingConstraints constraints = input.getConstraints();
	//		List<Kernel> eext = input.getExternalElectricFields();
	//
	//		CompositeSolver solver = new CompositeSolver();
	//		for (ElectrostaticObject obj : objs.values())
	//		{
	//			solver.add(obj);
	//		}
	//		for (Kernel kern : eext)
	//		{
	//			solver.add(kern);
	//		}
	//
	//		double[] qguess = new double[] { 0, 0, 0 };
	//		double rel = 1e-6;
	//		double abs = 1e-6;
	//		FloatingPotentialOptimizer optim = new FloatingPotentialOptimizer(constraints, solver, qguess, abs);
	//		optim.solve();
	//
	//		for (int i = 0; i < constraints.size(); i++)
	//		{
	//			FloatingConductor obj = constraints.getObject(i);
	//			String name = objs.inverse().get(obj);
	//			System.out.println(name + " " + solver.synthesizeMeanSurfacePotential(obj) + "±" + solver.synthesizeStdSurfacePotential(obj) + " V");
	//		}
	//
	//	}

}

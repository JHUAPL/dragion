package dragion.simulations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import com.google.common.collect.BiMap;

import dragion.util.Version;
import dragion.simulations.TomlInputFields.LeaderFollowerPair;
import dragion.util.OptionUtil;
import dragion.util.io.Checkpoint;
import dragion.util.io.Writers;
import lestat.kernels.Kernel;
import lestat.solvers.CompositeSolver;
import lestat.solvers.FloatingConstraints;
import lestat.solvers.FloatingPotentialOptimizer;
import lestat.surfaces.BiasedConductor;
import lestat.surfaces.ElectrostaticObject;
import lestat.surfaces.FloatingConductor;
import mowgli.shapes.Box;
import mowgli.util.DiagnosticUtil;
import vtk.vtkNativeLibrary;

public class DragionFields
{

	static class GridSpec
	{

		int[] gridres = null;
		double[] gridbounds = null;

		public GridSpec(OptionUtil opts, TomlInputFields input)
		{
			if (opts.isSpecified(gridresOption))
			{
				gridres = opts.getCommaSeparatedInts(gridresOption);
			}
			else
			{
				gridres = new int[] { 32, 32, 32 };
			}

			if (opts.isSpecified(gridboundsOption))
			{
				gridbounds = opts.getCommaSeparatedDoubles(gridboundsOption);
			}
			else
			{
				Box[] bboxes = new Box[input.getObjects().size()];
				int cnt = 0;
				for (ElectrostaticObject obj : input.getObjects().values())
				{
					bboxes[cnt] = obj.getBoundingBox();
					cnt++;
				}
				gridbounds = Box.asDoubleArray(Box.of(bboxes));
			}

			if (gridres.length == 1)
			{
				int[] oldres = gridres;
				gridres = new int[3];
				gridres[0] = oldres[0];
				gridres[1] = oldres[1];
				gridres[2] = oldres[2];
			}
			else if (gridres.length != 3)
			{
				throw new Error("Must specify 1 or 3 values for -gridres; got " + gridres.length + "; argument was " + opts.getString(gridresOption));
			}

			if (gridbounds.length != 6)
			{
				throw new Error("Must specify 6 comma-separated values for -gridbounds; got " + gridbounds.length + "; argument was " + opts.getString(gridboundsOption));
			}
			// TODO Auto-generated constructor stub
		}
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */

	static Option tomlfileOption = new Option("tomlfile", true, "Input .toml file");
//	static Option nogridfileOption = new Option("nogridfile", false, "Don't output a .vtr grid file");
	static Option outfileOption = new Option("gridfile", true, "Output .vtr file");
	static Option gridresOption = new Option("gridres", true, "Grid resolution");
	static Option gridboundsOption = new Option("gridbounds", true, "Grid bounds");
	static Option reltolOption = new Option("reltol", true, "Relative tolerance for solver");
	static Option abstolOption = new Option("abstol", true, "Absolute tolerance for solver");

	public DragionFields(String[] args)
	{
		vtkNativeLibrary.LoadAllNativeLibraries();

		//		Option writeObjectsOption = new Option("writeobjs", false, "Write a .vtp file for each object");
		OptionUtil opts = new OptionUtil(args, tomlfileOption, outfileOption, gridresOption, gridboundsOption, reltolOption, abstolOption);

		if (args.length == 0)
		{
			Version.printHelp(opts);
			return;
		}

		// toml input file
		Path tomlfile = Paths.get(opts.getString(tomlfileOption));
		TomlInputFields input = new TomlInputFields(tomlfile);

		String entrypoint = input.getString("simulation.entrypoint");
		if (!entrypoint.equals(getClass().getName()))
		{
			throw new Error("Entrypoint in input file " + entrypoint + " does not equal " + getClass().getName());
		}

		Path outfile = null;
		if (opts.isSpecified(outfileOption))
		{
			outfile = Paths.get(opts.getString(outfileOption));
		}
//		else
//		{
//			outfile = Paths.get("grid.vtr");
//		}


		//double rel = opts.getDoubleOrDefault(reltolOption, 1e-3);
		double abs = opts.getDoubleOrDefault(abstolOption, 1e-3);

		//	System.out.println(Arrays.toString(gridres) + " " + Arrays.toString(gridbounds));

		BiMap<String, ElectrostaticObject> objs = input.getObjects();
		FloatingConstraints constraints = input.getConstraints();
		List<Kernel> extfields = input.getExternalElectricFields();

		CompositeSolver solver = input.getSolver();
		for (ElectrostaticObject obj : objs.values())
		{
			solver.add(obj);
		}
		for (Kernel eext : extfields)
		{
			solver.add(eext);
		}

		new FloatingPotentialOptimizer(constraints, solver, abs).solve();

		if (input.getNumberOfFollowers() > 0) // get leader potential and optimize follower
		{

			int ntries = 1000;
			double[] phiOld = new double[input.getNumberOfFollowers()];
			double[] deltaphi = new double[input.getNumberOfFollowers()];
			for (int i = 0; i < ntries; i++)
			{
				for (int m = 0; m < input.getNumberOfFollowers(); m++)
				{
					LeaderFollowerPair pair = input.getFollower(m);
					phiOld[m] = solver.synthesizeMeanSurfacePotential(pair.leader);
					pair.follower.setPotential(phiOld[m]);
				}

				new FloatingPotentialOptimizer(constraints, solver, abs).solve();

				for (int m = 0; m < input.getNumberOfFollowers(); m++)
				{
					LeaderFollowerPair pair = input.getFollower(m);
					double phiNew = solver.synthesizeMeanSurfacePotential(pair.leader);

					deltaphi[m] = Math.abs(phiNew - phiOld[m]);

					phiOld[m] = phiNew;
				}

				boolean stop = true;
				for (int m = 0; m < input.getNumberOfFollowers(); m++)
				{
					if (deltaphi[m] > abs)
					{
						stop = false;
					}
				}
				
				if (stop)
				{
					break;
				}
				
				if (i == ntries - 1)
				{
					String msg = "Max iterations reached in leader/follower relaxation; deltaphi = (";
					for (int m = 0; m < input.getNumberOfFollowers(); m++)
					{
						LeaderFollowerPair pair = input.getFollower(m);
						String leaderName = input.objects.inverse().get(pair.leader);
						msg += "  " + leaderName + " " + deltaphi[m];
						if (m < input.getNumberOfFollowers()-1)
						{
							msg+= "; ";
						}
					}
					msg += ")";
					System.out.println("Warning: " + msg);
				}

			}

			for (int m = 0; m < input.getNumberOfFollowers(); m++)
			{
				if (deltaphi[m] > abs)
				{
					LeaderFollowerPair pair = input.getFollower(m);
					String leaderName = input.objects.inverse().get(pair.leader);
					System.out.println("Warning: leader/follower relaxation not converged (" + leaderName +"): deltaphi = " + deltaphi[m]);
				}
			}
		}

		for (String diagname : input.getDiagnostics().keySet())
		{
			DiagnosticUtil.writeDiagnosticFile(input.getDiagnostics().get(diagname), diagname);
		}

		//		if (opts.isSpecified(outfileOption))
		//		{

		//			Box[] bboxes = new Box[objs.size()];
		//			int cnt = 0;
		//			for (ElectrostaticObject obj : objs.values())
		//			{
		//				bboxes[cnt] = Box.of(obj);
		//				cnt++;
		//			}
		//			Box bbox = Box.of(bboxes);

		//			final double boxfac;
		//			final int nsamp;
		//			if (input.has("output.grid"))
		//			{
		//				boxfac = input.getDouble("output.grid.boxfac");
		//				nsamp = input.getInt("output.grid.nsamp");
		//			}
		//			else
		//			{
		//				boxfac = 2;
		//				nsamp = 32;
		//			}
		//			double boxlen = bbox.getNorm() * boxfac;

		if (outfile != null)
		{
			GridSpec gridspec = null;
			gridspec = new GridSpec(opts, input);

			if (gridspec != null)
			{
				Box gridbox = new Box(gridspec.gridbounds);

				Writers.sampleGrid(gridbox, gridspec.gridres, solver, outfile);
			}
		}

		Checkpoint cp = new Checkpoint();
		for (String name : objs.keySet())
		{
			cp.write(objs.get(name), solver, name);
		}

	}

	public static void main(String[] args)
	{
		new DragionFields(args);
	}

}

package dragion.simulations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.Option;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.google.common.collect.BiMap;

import dragion.physics.DriftDiffusionAdvancer;
import dragion.physics.FluidAdvancer;
import dragion.physics.SideFluxInjector;
import dragion.physics.UniformFieldOscillatingKernel;
import dragion.simulations.TomlInputCarriers.UniformCarriers;
import dragion.util.OptionUtil;
import dragion.util.Version;
import dragion.util.io.Checkpoint;
import dragion.util.io.Writers;
import lestat.kernels.Kernel;
import lestat.particles.Advancer;
import lestat.particles.Particle;
import lestat.particles.Species;
import lestat.particles.emitcollect.Emitter;
import lestat.particles.maxwellian.MaxwellianSampler;
import lestat.solvers.CompositeSolver;
import lestat.solvers.FloatingConstraints;
import lestat.solvers.FloatingPotentialOptimizer;
import lestat.surfaces.BiasedConductor;
import lestat.surfaces.ElectrostaticObject;
import mowgli.Constants;
import mowgli.samplers.BoxUniformVolumeSampler;
import mowgli.shapes.Box;
import mowgli.surfaces.ArraySurface;
import mowgli.surfaces.EmbreeIntersector;
import mowgli.surfaces.IntersectionPoint;
import mowgli.surfaces.Surface;
import mowgli.util.DiagnosticUtil;
import vtk.vtkNativeLibrary;
import vtk.vtkObject;
import vtk.vtkPolyData;

public class DragionCarriers
{
	
	
//	public static boolean isInside(Collection<ElectrostaticObject> objs, Vector3D pos)
//	{
//		for (ElectrostaticObject obj : objs)
//		{
//			if (obj.isInside(pos))
//			{
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public static IntersectionPoint intersection(Collection<ElectrostaticObject> objs, Vector3D start, Vector3D end)
//	{
//		for (ElectrostaticObject obj : objs)
//		{
//			IntersectionPoint ipt = obj.getFirstIntersection(start, end);
//			if (ipt != null)
//			{
//				return ipt;
//			}
//		}
//		return null;
//	}

	public static Path getGridFilePath(int istep)
	{
		return Paths.get("grid_" + istep + ".vtr");
	}

//	static class Special
//	{
//		static class PhiLeader extends FloatingConductor
//		{
//
//			public PhiLeader(vtkPolyData rawpolydata)
//			{
//				super(rawpolydata);
//			}
//			
//			
//		}
//	}
	
	public DragionCarriers(String[] args)
	{
		vtkNativeLibrary.LoadAllNativeLibraries();

		Option tomlfileOption = new Option("tomlfile", true, "Input .toml file");
		Option nstepsOption = new Option("nsteps", true, "Total number of simulation steps");
		Option dmpstrideOption = new Option("dmpstride", true, "Number of steps between output, 0 if no output");
		Option diagstrideOption = new Option("diagstride", true, "Number of steps between diagnostic output, 0 if none");

		Option restartOption = new Option("restart", true, "Step to restart from, 0 if none");
		Option writeGridOption = new Option("writegrid", true, "Write grid file on output; argument is # pts on a side");
		Option gcstrideOption = new Option("gcstride", true, "Number of steps between garbage collect, 0 if no gc");		
		Option helpOption = new Option("help", false, "Print help");
		OptionUtil opts = new OptionUtil(args, tomlfileOption, nstepsOption, dmpstrideOption, diagstrideOption, restartOption, writeGridOption, gcstrideOption, helpOption);

		if (args.length == 0 || opts.isSpecified(helpOption))
		{
			Version.printHelp(opts);
			return;
		}

		// cli options
		int restartStep = 0;
		if (opts.isSpecified(restartOption))
		{
			restartStep = opts.getInt(restartOption);
		}

		int dmpstride = 0;
		if (opts.isSpecified(dmpstrideOption))
		{
			dmpstride = opts.getInt(dmpstrideOption);
		}

		int diagstride = 0;
		if (opts.isSpecified(diagstrideOption))
		{
			diagstride = opts.getInt(diagstrideOption);
		}
		
		int nsteps = 1;
		if (opts.isSpecified(nstepsOption))
		{
			nsteps = opts.getInt(nstepsOption);
		}

		int nsamp = 0;
		if (opts.isSpecified(writeGridOption))
		{
			nsamp = opts.getInt(writeGridOption);
		}
		
		int gcstride = 0;
		if (opts.isSpecified(gcstrideOption))
		{
			gcstride = opts.getInt(gcstrideOption);
		}

		// toml input file
		Path tomlfile = Paths.get(opts.getString(tomlfileOption));
		TomlInputCarriers input = new TomlInputCarriers(tomlfile);

		String entrypoint = input.getString("simulation.entrypoint");
		if (!entrypoint.equals(getClass().getName()))
		{
			throw new Error("Entrypoint in input file " + entrypoint + " does not equal " + getClass().getName());
		}

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
			((UniformFieldOscillatingKernel)eext).setTime(0);
		}
//		if (!efields.isEmpty())
//		{
//			throw new Error("External electric field not yet supported (boundary conditions for eext not implemented)");
//		}

		double rel = 1e-6;
		double abs = 1e-6;
		new FloatingPotentialOptimizer(constraints, solver, abs).solve();

		Box domainBox = input.getDomainBox();


		final Vector3D windvel;
		if (input.has("domain.windvel"))
		{
			windvel = input.getVector("domain.windvel");
		}
		else
		{
			windvel = Vector3D.ZERO;
		}
		
		UniformCarriers carriers = input.getCarriers();

		List<Particle> particles = new ArrayList<>();
		Species species = new Species(Constants.qe * carriers.chargestate, Constants.mp * carriers.amu, carriers.np2c, "carriers"); // mass doesn't matter here (velocity is achieved through mobility)
		double vth = MaxwellianSampler.getVthFromTemperature(carriers.temperature, species.getM());
		MaxwellianSampler velsamp = new MaxwellianSampler(vth, windvel);

		EmbreeIntersector intersector = new EmbreeIntersector();
		for (Surface srf : objs.values())
		{
			intersector.register((ArraySurface)srf);
		}
		
		if (restartStep == 0)
		{

			BoxUniformVolumeSampler boxsamp = new BoxUniformVolumeSampler(domainBox);
			int ntoload = (int) (carriers.concentration * domainBox.getVolume() / carriers.np2c);
			for (int i = 0; i < ntoload; i++)
			{
				Vector3D pos = boxsamp.get();
				if (!intersector.isInside(pos))
				{
					Vector3D vel = velsamp.get();
					particles.add(new Particle(pos, vel, species));
				}
			}
		}
		else
		{
			throw new Error("Restarting from a checkpoint is not yet implemented");
			//			particles.addAll(new Checkpoint(basedir).read(species, restartStep));
		}


		//Advancer advancer = new DriftDiffusionAdvancer(carriers.mobility, carriers.temperature); //, windvel);
		Advancer advancer = new FluidAdvancer(windvel, carriers.mobility, species.getQ(), species.getM());
		double dt = input.getDouble("simulation.dt");

		List<Emitter> injectors = new ArrayList<>();
		if (input.has("domain.injectdiv"))
		{
			int subdiv = input.getInt("domain.injectdiv");
			SideFluxInjector sideflux = new SideFluxInjector(domainBox, solver, species, subdiv, (r) -> carriers.concentration, carriers.mobility, intersector, (r) -> windvel);
			injectors.add(sideflux);
		}
		
		System.out.println("Step #\ttime [s]\t# Particles");
		for (int itime = restartStep; itime <= nsteps; itime++)
		{

			// console message
			double time = dt * itime;
			System.out.println(itime + " " + time + " " + particles.size());

			// field solve 
			for (Kernel eext : extfields)	// set time for oscillating external fields (everything else is DC)
			{
				((UniformFieldOscillatingKernel)eext).setTime(time);
			}
			solver.solveAndBroadcast();	// this sets induced surface charge densities

			// set particle electric fields
			{
				Stream<Particle> stream = particles.parallelStream();
				stream.forEach((p) -> {
					p.setEfld(solver.synthesizeElectricField(p.getPos()));
				});
			}

			// write checkpoint
			if ((dmpstride > 0) && (itime % dmpstride) == 0)
			{
				// set particle potentials
				Stream<Particle> stream = particles.parallelStream();
				stream.forEach((p) -> {
					p.setPhi(solver.synthesizeElectricPotential(p.getPos()));
				});
				// write checkpoint files
				Checkpoint chk = new Checkpoint(Paths.get(""));
				chk.write(particles, species, itime);
				for (String name : objs.keySet())
				{
					chk.write(objs.get(name), solver, name, itime);
				}
				if (nsamp > 0)
				{
					Writers.sampleGrid(domainBox, nsamp, solver, getGridFilePath(itime));
				}
				//
				gc();
			}

			// write diagnostics
			if ((diagstride > 0) && (itime % diagstride) == 0)
			{
				// write diagnostics
				for (String diagname : input.getDiagnostics().keySet())
				{
					DiagnosticUtil.writeDiagnosticFile(input.getDiagnostics().get(diagname), diagname, itime);
				}

			}

			
			// raw particle advance
			advancer.advance(particles, dt);

			// remove out-of-domain particles
			Iterator<Particle> pit = particles.iterator();
			while (pit.hasNext())
			{
				Particle p = pit.next();
				if (!domainBox.contains(p.getPos()))
				{
					pit.remove();
				}
			}

			// accumulate charge after advance
			pit = particles.iterator();
			while (pit.hasNext())
			{
				Particle p = pit.next();
				IntersectionPoint ipt = intersector.getFirstIntersection(p.getOldPos(), p.getPos());
				if (ipt != null)
				{
					ElectrostaticObject obj = (ElectrostaticObject) ipt.getSurface();
					obj.accumulateCharge(p, ipt.getFaceId());
					pit.remove();
				}
			}

			// garbage collect
			if ((gcstride > 0) && (itime % gcstride) == 0)
			{
				gc();
			}
			
			for (Emitter emitter : injectors)
			{
				particles.addAll(emitter.emit(dt));
			}
			
		}

	}

	static void gc()
	{
		System.gc();
		vtkObject.JAVA_OBJECT_MANAGER.gc(false);
	}

	public static void main(String[] args)
	{
		new DragionCarriers(args);
	}
}

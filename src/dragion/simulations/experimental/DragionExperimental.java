package dragion.simulations.experimental;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.cli.Option;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.google.common.collect.BiMap;

import dragion.physics.DriftDiffusionAdvancer;
import dragion.physics.FluidAdvancer;
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
import lestat.surfaces.FloatingConductor;
import mowgli.Constants;
import mowgli.samplers.BoxUniformVolumeSampler;
import mowgli.shapes.Box;
import mowgli.surfaces.ArraySurface;
import mowgli.surfaces.EmbreeIntersector;
import mowgli.surfaces.IntersectionPoint;
import mowgli.surfaces.Surface;
import mowgli.util.DiagnosticUtil;
import mowgli.util.VtkUtil;
import vtk.vtkNativeLibrary;
import vtk.vtkObject;
import vtk.vtkPolyData;

public class DragionExperimental
{

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

	public DragionExperimental(String[] args)
	{
		vtkNativeLibrary.LoadAllNativeLibraries();

		Option tomlfileOption = new Option("tomlfile", true, "Input .toml file");
		Option nstepsOption = new Option("nsteps", true, "Total number of simulation steps");
		Option dmpstrideOption = new Option("dmpstride", true, "Number of steps between checkpoints, 0 if no output");
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

		int diagstride = 0;
		if (opts.isSpecified(diagstrideOption))
		{
			diagstride = opts.getInt(diagstrideOption);
		}

		// toml input file
		Path tomlfile = Paths.get(opts.getString(tomlfileOption));
		TomlInputExperimental input = new TomlInputExperimental(tomlfile);

		String entrypoint = input.getString("simulation.entrypoint");
		if (!entrypoint.equals(getClass().getName()))
		{
			throw new Error("Entrypoint in input file " + entrypoint + " does not equal " + getClass().getName());
		}

		BiMap<String, ElectrostaticObject> objs = input.getObjects();
		List<Kernel> efields = input.getExternalElectricFields();

		CompositeSolver solver = input.getSolver();
		for (ElectrostaticObject obj : objs.values())
		{
			solver.add(obj);
		}

		//		for (Kernel eext : efields)
		//		{
		//			solver.add(eext);
		//		}
		if (!efields.isEmpty())
		{
			throw new Error("External electric field not yet supported (boundary conditions for eext not implemented)");
		}

		double floatingChargeTolerance = 1e-11;
		{
			FloatingConstraints constraints = input.getConstraints();
			new FloatingPotentialOptimizer(constraints, solver, floatingChargeTolerance).solve();
		}

		Box domainBox = input.getDomainBox();

		UniformCarriers carriers = input.getCarriers();

		List<Particle> particles = new ArrayList<>();
		Species species = new Species(Constants.qe * carriers.chargestate, Constants.mp * carriers.amu, carriers.np2c, "carriers");
		double vth = MaxwellianSampler.getVthFromTemperature(carriers.temperature, species.getM());
		MaxwellianSampler velsamp = new MaxwellianSampler(vth, Vector3D.ZERO);

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

		final Vector3D windvel;
		if (input.has("domain.windvel"))
		{
			windvel = input.getVector("domain.windvel");
			throw new Error("Wind velocity not currently used.");
		}
		else
		{
			windvel = Vector3D.ZERO;
		}

		//Advancer advancer = new DriftDiffusionAdvancer(carriers.mobility, carriers.temperature);//, windvel);
		Advancer advancer = new FluidAdvancer(windvel, carriers.mobility, species.getQ(), species.getM());
		double dt = input.getDouble("simulation.dt");

		System.out.println("Step #\ttime [s]\t# Particles");
		//		final int np0 = particles.size();
		//		final double q0 = input.getObjects().get("ball").getTotalCharge();
		for (int itime = restartStep; itime <= nsteps; itime++)
		{

			// console message
			double time = dt * itime;

//			input.getFollower().disableAccumulation();
			
			// field solve (and set surface charge densities)
			solver.solveAndBroadcast();

			
//			// ball only; test to optimize its own potential to itself (should give identical results without optimization)
//			{
//				FloatingConductor ball = (FloatingConductor) objs.get("ball");
//				double qtotOrig = ball.getTotalCharge();
//
//				double phiOrig = solver.synthesizeMeanSurfacePotential(ball);
//				FloatingConstraints followingConstraints = new FloatingConstraints();
//				followingConstraints.add(ball, phiOrig);
//				double[] qguess = FloatingPotentialOptimizer.currentValueGuess(followingConstraints);
//				new FloatingPotentialOptimizer(followingConstraints, solver, qguess, rel, abs).solve();
//
//				double qtotNew = ball.getTotalCharge();
//				double phiNew = solver.synthesizeMeanSurfacePotential(ball);
//
//				//			input.getFollower().setPotential(phi0);
//
//				System.out.println(itime + " " + time + " " + particles.size() + " " + qtotOrig + " " + qtotNew + " " + phiOrig + " " + phiNew);
//			}

			// leader and follower
			{

				FloatingConductor leader = input.getLeader();
				BiasedConductor follower = input.getFollower();
				
				double qtotOrig = leader.getTotalCharge();
				double phiOrig = solver.synthesizeMeanSurfacePotential(leader);
				follower.setPotential(phiOrig);
				
				FloatingConstraints constraints = new FloatingConstraints();
				constraints.add(leader, phiOrig);
				double[] qguess = FloatingPotentialOptimizer.currentValueGuess(constraints);
				new FloatingPotentialOptimizer(constraints, solver, qguess, floatingChargeTolerance).solve();
				
				double qtotNew = leader.getTotalCharge();
				double phiNew = solver.synthesizeMeanSurfacePotential(leader);
				
				System.out.println(itime + " " + time + " " + particles.size() + " " + qtotOrig + " " + qtotNew + " " + phiOrig + " " + phiNew);

			}
			
			//System.out.println(itime + " " + time + " " + particles.size();

			//
////			if ((itime % 10) == 0)
////			{
//
//			System.out.println(phi0);
//			System.out.println(ball.getTotalCharge());
//			
//				FloatingConstraints followingConstraints = new FloatingConstraints();
//				followingConstraints.add(input.getLeader(), phi0);
//				//followingConstraints.add(input.getFollower(), phi0);
////
//				double[] qguess = FloatingPotentialOptimizer.currentValueGuess(followingConstraints);
//				new FloatingPotentialOptimizer(constraints, solver, qguess, rel, abs).solve();
//
//				double phinew = solver.synthesizeMeanSurfacePotential(ball);
//				System.out.println(phinew);
//				System.out.println(ball.getTotalCharge());

			//			}

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

		}

	}

	static void gc()
	{
		System.gc();
		vtkObject.JAVA_OBJECT_MANAGER.gc(false);
	}

	public static void main(String[] args)
	{
		new DragionExperimental(args);
	}
}

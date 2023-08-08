package dragion.util.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.google.common.collect.Lists;

import dragion.physics.CollisionalSinkUnipolar;
import lestat.particles.Particle;
import lestat.particles.Species;
import lestat.solvers.CompositeSolver;
import lestat.surfaces.ElectrostaticObject;
import mowgli.grid.SampleGrid;
import mowgli.util.VtkUtil;
import vtk.vtkDoubleArray;
import vtk.vtkPolyData;

public class Checkpoint
{
	final Path basedir;

	public Checkpoint()
	{
		this.basedir = Paths.get("");
	}

	public Checkpoint(Path basedir)
	{
		this.basedir = basedir;
	}

	protected static Path getParticleFilePath(Species species, int istep)
	{
		return Paths.get(species.getName() + "_" + istep + ".vtp");
	}

	protected static Path getSurfaceFilePath(String name, int istep)
	{
		return Paths.get(name + "_" + istep + ".vtp");
	}

	protected static Path getSurfaceFilePath(String name)
	{
		return Paths.get(name + ".vtp");
	}

	protected static Path getSampleGridFilePath(int istep)
	{
		return Paths.get("samp" + "_" + istep + ".vtr");
	}

	protected static Path getCollisionSinkGridPath(int istep)
	{
		return Paths.get("collgrid_" + istep + ".vtr");
	}

	public Checkpoint write(List<Particle> particles, Species species, int istep)
	{
		Writers.polydata(particles, species, getParticleFilePath(species, istep));
		return this;
	}

	public Checkpoint write(ElectrostaticObject surface, CompositeSolver solver, String name)
	{
		Writers.electrostaticSurface(surface, solver, getSurfaceFilePath(name));
		return this;
	}

	public Checkpoint write(ElectrostaticObject surface, CompositeSolver solver, String name, int istep)
	{
		Writers.electrostaticSurface(surface, solver, getSurfaceFilePath(name, istep));
		return this;
	}

	public Checkpoint write(ElectrostaticObject surface, String name, int istep)
	{
		Writers.electrostaticSurface(surface, null, getSurfaceFilePath(name, istep));
		return this;
	}

	//	public Checkpoint write(Dielectric surface, String name, int istep)
	//	{
	//		Writers.dielectric(surface, getSurfaceFilePath(name, istep));
	//		return this;
	//	}

	public Checkpoint write(SampleGrid grid, int istep)
	{
		throw new Error("Not implemented");
		//		Writers.rectilinearGrid(grid., getSampleGridFilePath(istep));
		//		return this;
	}

	public Checkpoint write(CollisionalSinkUnipolar grid, int istep)
	{
		Writers.rectilineargrid(grid.getGrid(), Lists.newArrayList(grid.getDensity(), grid.getSinkrate()), getCollisionSinkGridPath(istep));
		return this;
	}

	public List<Particle> read(Species species, int istep)
	{
		vtkPolyData polydata = VtkUtil.read(getParticleFilePath(species, istep));
		vtkDoubleArray velarr = (vtkDoubleArray) polydata.GetCellData().GetArray(Writers.velarrname);
		vtkDoubleArray efldarr = (vtkDoubleArray) polydata.GetCellData().GetArray(Writers.efldarrname);
		vtkDoubleArray phiarr = (vtkDoubleArray) polydata.GetCellData().GetArray(Writers.phiArrayName);

		List<Particle> particles = new ArrayList<>();
		for (int i = 0; i < polydata.GetNumberOfCells(); i++)
		{
			Vector3D pos = new Vector3D(polydata.GetPoints().GetPoint(i));
			Vector3D vel = new Vector3D(velarr.GetTuple3(i));
			Vector3D efld = new Vector3D(efldarr.GetTuple3(i));
			double phi = phiarr.GetValue(i);
			Particle p = new Particle(pos, vel, species);
			p.setEfld(efld);
			p.setPhi(phi);
			particles.add(p);
		}

		return particles;
	}

}

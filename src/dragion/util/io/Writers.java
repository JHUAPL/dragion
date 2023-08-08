package dragion.util.io;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import lestat.particles.Particle;
import lestat.particles.Species;
import lestat.solvers.CompositeSolver;
import lestat.surfaces.BiasedConductor;
import lestat.surfaces.Dielectric;
import lestat.surfaces.ElectrostaticObject;
import lestat.surfaces.FloatingConductor;
import mowgli.grid.CellScalar;
import mowgli.grid.Grid;
import mowgli.grid.SampleGrid;
import mowgli.shapes.Box;
import mowgli.util.VtkUtil;
import vtk.vtkCellArray;
import vtk.vtkDoubleArray;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkRectilinearGrid;
import vtk.vtkVertex;
import vtk.vtkXMLPolyDataWriter;
import vtk.vtkXMLRectilinearGridWriter;

public class Writers
{

	public static void polydata(vtkPolyData polydata, Path outfile)
	{
		VtkUtil.write(polydata, outfile);
	}

	public static vtkDoubleArray makeCellArray(String name, vtkPolyData polydata)
	{
		vtkDoubleArray array = new vtkDoubleArray();
		array.SetNumberOfValues(polydata.GetNumberOfCells());
		array.SetName(name);
		return array;
	}

	public static void rectilinearGrid(vtkRectilinearGrid grid, Path outfile)
	{
		vtkXMLRectilinearGridWriter writer = new vtkXMLRectilinearGridWriter();
		writer.SetInputData(grid);
		writer.SetDataModeToBinary();
		writer.SetFileName(outfile.toString());
		writer.Write();
	}

	public static void rectilineargrid(Grid grid, Collection<CellScalar> cellscalars, Path outfile)
	{
		vtkRectilinearGrid rgrid = new vtkRectilinearGrid();
		rgrid.SetDimensions(grid.getNxp(), grid.getNyp(), grid.getNzp());

		vtkDoubleArray xcoords = new vtkDoubleArray();
		vtkDoubleArray ycoords = new vtkDoubleArray();
		vtkDoubleArray zcoords = new vtkDoubleArray();
		for (int i = 0; i < grid.getNxp(); i++)
		{
			xcoords.InsertNextValue(grid.getX(i));
		}
		for (int j = 0; j < grid.getNyp(); j++)
		{
			ycoords.InsertNextValue(grid.getY(j));
		}
		for (int k = 0; k < grid.getNzp(); k++)
		{
			zcoords.InsertNextValue(grid.getZ(k));
		}
		rgrid.SetXCoordinates(xcoords);
		rgrid.SetYCoordinates(ycoords);
		rgrid.SetZCoordinates(zcoords);

		for (CellScalar scalar : cellscalars)
		{
			vtkDoubleArray arr = new vtkDoubleArray();
			arr.SetName(scalar.getName());
			double[][][] data = scalar.getData();
			for (int k = 0; k < grid.getNzc(); k++)
			{
				for (int j = 0; j < grid.getNyc(); j++)
				{
					for (int i = 0; i < grid.getNxc(); i++)
					{
						arr.InsertNextValue(data[i][j][k]);
					}
				}
			}
			rgrid.GetCellData().AddArray(arr);
		}

		vtkXMLRectilinearGridWriter writer = new vtkXMLRectilinearGridWriter();
		writer.SetInputData(rgrid);
		writer.SetDataModeToBinary();
		writer.SetFileName(outfile.toString());
		writer.Update();
	}

	public static final String velarrname = "velocity";
	public static final String efldarrname = "efield";
	public static final String phiArrayName = "phi";
	public static final String sigiArrayName = "sigi";
	public static final String sigaArrayName = "siga";
	public static final String phibiasArrayName = "phibias";

	public static void polydata(Collection<Particle> particles, Species speciesToWrite, Path outfile)
	{
		vtkPoints points = new vtkPoints();
		vtkCellArray cells = new vtkCellArray();

		vtkDoubleArray velArr = new vtkDoubleArray();
		vtkDoubleArray efieldArr = new vtkDoubleArray();
		vtkDoubleArray phiArr = new vtkDoubleArray();
		velArr.SetName(velarrname);
		efieldArr.SetName(efldarrname);
		phiArr.SetName(phiArrayName);
		velArr.SetNumberOfComponents(3);
		efieldArr.SetNumberOfComponents(3);

		Iterator<Particle> it = particles.iterator();
		while (it.hasNext())
		{
			Particle p = it.next();
			if (p.getSpecies().equals(speciesToWrite))
			{
				int id = points.InsertNextPoint(p.getPos().toArray());
				vtkVertex vert = new vtkVertex();
				vert.GetPointIds().SetId(0, id);
				cells.InsertNextCell(vert);

				Vector3D vel = p.getVel();
				Vector3D efield = p.getEfld();
				double phi = p.getPhi();
				velArr.InsertNextTuple3(vel.getX(), vel.getY(), vel.getZ());
				efieldArr.InsertNextTuple3(efield.getX(), efield.getY(), efield.getZ());
				phiArr.InsertNextValue(phi);
			}

		}

		vtkPolyData polydata = new vtkPolyData();
		polydata.SetPoints(points);
		polydata.SetVerts(cells);
		polydata.GetCellData().AddArray(velArr);
		polydata.GetCellData().AddArray(efieldArr);
		polydata.GetCellData().AddArray(phiArr);

		vtkXMLPolyDataWriter writer = new vtkXMLPolyDataWriter();
		writer.SetInputData(polydata);
		writer.SetDataModeToBinary();
		writer.SetFileName(outfile.toString());
		writer.Update();

	}

	public static void electrostaticSurface(ElectrostaticObject surface, CompositeSolver solver, Path outfile)
	{
		vtkPolyData polydata = new vtkPolyData();
		polydata.DeepCopy(surface.getPolyData());

		if (surface instanceof Dielectric)
		{
			Dielectric diel = (Dielectric) surface;
			vtkDoubleArray sigaarr = new vtkDoubleArray();
			vtkDoubleArray sigiarr = new vtkDoubleArray();
			sigaarr.SetName(sigaArrayName);
			sigiarr.SetName(sigiArrayName);
			for (int i = 0; i < diel.getNumberOfFaces(); i++)
			{
				sigaarr.InsertNextValue(diel.getSiga(i));
				sigiarr.InsertNextValue(diel.getSigInduced(i));
			}
			polydata.GetCellData().AddArray(sigaarr);
			polydata.GetCellData().AddArray(sigiarr);
		}
		else if (surface instanceof BiasedConductor)
		{
			BiasedConductor cond = (BiasedConductor) surface;
			vtkDoubleArray sigiarr = new vtkDoubleArray();
			vtkDoubleArray phibiasarr = new vtkDoubleArray();
			sigiarr.SetName(sigiArrayName);
			phibiasarr.SetName(phibiasArrayName);
			for (int i = 0; i < cond.getNumberOfFaces(); i++)
			{
				sigiarr.InsertNextValue(cond.getSigInduced(i));
				phibiasarr.InsertNextValue(cond.getPotential());
			}
			polydata.GetCellData().AddArray(sigiarr);
			polydata.GetCellData().AddArray(phibiasarr);
		}
		else if (surface instanceof FloatingConductor)
		{
			FloatingConductor cond = (FloatingConductor) surface;
			vtkDoubleArray sigiarr = new vtkDoubleArray();
			sigiarr.SetName(sigiArrayName);
			for (int i = 0; i < cond.getNumberOfFaces(); i++)
			{
				sigiarr.InsertNextValue(cond.getSigInduced(i));
			}
			polydata.GetCellData().AddArray(sigiarr);
		}

		if (solver != null)
		{
			vtkDoubleArray potarr = new vtkDoubleArray();
			vtkDoubleArray efldarr = new vtkDoubleArray();
			efldarr.SetNumberOfComponents(3);
			potarr.SetName(phiArrayName);
			efldarr.SetName(efldarrname);

			for (int i = 0; i < surface.getNumberOfFaces(); i++)
			{
				Vector3D pos = surface.getFaceCenter(i);
				double phi = solver.synthesizeElectricPotential(pos);
				Vector3D efld = solver.synthesizeElectricField(pos);
				//
				potarr.InsertNextValue(phi);
				efldarr.InsertNextTuple3(efld.getX(), efld.getY(), efld.getZ());
			}

			polydata.GetCellData().AddArray(potarr);
			polydata.GetCellData().AddArray(efldarr);
		}

		vtkXMLPolyDataWriter writer = new vtkXMLPolyDataWriter();
		writer.SetInputData(polydata);
		writer.SetDataModeToBinary();
		writer.SetFileName(outfile.toString());
		writer.Update();

	}

	public static void sampleGrid(Box bbox, int[] nsamp, CompositeSolver solver, Path outfile)
	{

		SampleGrid grid = new SampleGrid(bbox, nsamp[0], nsamp[1], nsamp[2]);
		double[][][] phi = grid.sampleScalar((r) -> solver.synthesizeElectricPotential(r));
		Vector3D[][][] efld = grid.sampleVector((r) -> solver.synthesizeElectricField(r));
		vtkRectilinearGrid rgrid = VtkUtil.of(grid);
		vtkDoubleArray phiarr = VtkUtil.vtkDoubleArrayOf(phi, grid.getNx(), grid.getNy(), grid.getNz(), Writers.phiArrayName);
		vtkDoubleArray efldarr = VtkUtil.vtkDoubleArrayOf(efld, grid.getNx(), grid.getNy(), grid.getNz(), Writers.efldarrname);
		rgrid.GetPointData().AddArray(phiarr);
		rgrid.GetPointData().AddArray(efldarr);

		Writers.rectilinearGrid(rgrid, outfile);
		
	}
	public static void sampleGrid(Box bbox, int nsamp, CompositeSolver solver, Path outfile)
	{
		sampleGrid(bbox, new int[] {nsamp,nsamp,nsamp}, solver, outfile);
	}
}

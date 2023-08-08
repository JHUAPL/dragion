package dragion.simulations;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.Option;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import dragion.util.OptionUtil;
import mowgli.util.VtkUtil;
import vtk.vtkArrayCalculator;
import vtk.vtkDoubleArray;
import vtk.vtkIdList;
import vtk.vtkNativeLibrary;
import vtk.vtkPolyData;
import vtk.vtkUnstructuredGrid;
import vtk.vtkXMLPolyDataWriter;

public class PotFile
{

	public static void main(String[] args) throws IOException
	{
		vtkNativeLibrary.LoadAllNativeLibraries();

		Option infileOption = new Option("infile", true, "Input geometry file");
		Option outfileOption = new Option("outfile", true, "Output .pot file");
		Option funcOption = new Option("func", true, "Function of scalars posX, posY, posZ, nmlX, nmlY, nmlZ, and vectors pos and nml, defining face potentials");
		OptionUtil opts = new OptionUtil(args, infileOption, outfileOption, funcOption);

		//		if (args.length == 0)
		//		{
		////			Version.printHelp(opts);
		//			return;
		//		}

		if (!opts.isSpecified(infileOption))
		{
			throw new Error("Must specify -infile");
		}

		String infile = opts.getString(infileOption);

		if (!opts.isSpecified(outfileOption))
		{
			throw new Error("Must specify -outfile");
		}
		String outfile = opts.getString(outfileOption);

		if (!opts.isSpecified(funcOption))
		{
			throw new Error("Must specify -func");
		}
		String funcstr = opts.getString(funcOption);

		vtkPolyData polydata = VtkUtil.read(Paths.get(infile));

		for (int f = 0; f < polydata.GetNumberOfCells(); f++)
		{
			if (polydata.GetCell(f).GetNumberOfPoints() != 3)
			{
				throw new Error("Only triangles are allowed");
			}
		}
		

		vtkDoubleArray posarr = new vtkDoubleArray();
		posarr.SetName("pos");
		posarr.SetNumberOfComponents(3);

		vtkDoubleArray nmlarr = new vtkDoubleArray();
		nmlarr.SetName("nml");
		nmlarr.SetNumberOfComponents(3);

		for (int i = 0; i < polydata.GetNumberOfCells(); i++)
		{
			vtkIdList ids = new vtkIdList();
			polydata.GetCellPoints(i, ids);

			double[] pt0 = polydata.GetPoint(ids.GetId(0));
			double[] pt1 = polydata.GetPoint(ids.GetId(1));
			double[] pt2 = polydata.GetPoint(ids.GetId(2));

			Vector3D v0 = new Vector3D(pt0);
			Vector3D v1 = new Vector3D(pt1);
			Vector3D v2 = new Vector3D(pt2);

			Vector3D e01 = v1.subtract(v0);
			Vector3D e02 = v2.subtract(v0);

			Vector3D ctr = v0.add(v1).add(v2).scalarMultiply(1. / 3.);
			Vector3D nml = e01.crossProduct(e02).normalize();

			posarr.InsertNextTuple3(ctr.getX(), ctr.getY(), ctr.getZ());
			nmlarr.InsertNextTuple3(nml.getX(), nml.getY(), nml.getZ());
		}

		polydata.GetCellData().AddArray(posarr);
		polydata.GetCellData().AddArray(nmlarr);

		vtkArrayCalculator calc = new vtkArrayCalculator();
		calc.SetInputData(polydata);
		calc.SetAttributeTypeToCellData();
		calc.AddScalarVariable("posX", "pos", 0);
		calc.AddScalarVariable("posY", "pos", 1);
		calc.AddScalarVariable("posZ", "pos", 2);
		calc.AddScalarVariable("nmlX", "nml", 0);
		calc.AddScalarVariable("nmlY", "nml", 1);
		calc.AddScalarVariable("nmlZ", "nml", 2);
		calc.AddVectorArrayName("pos", 0, 1, 2);
		calc.AddVectorArrayName("nml", 0, 1, 2);
		calc.SetFunction(funcstr);
		calc.SetResultArrayName(TomlInputFields.phisegArrayName);
		calc.Update();

		vtkXMLPolyDataWriter writer = new vtkXMLPolyDataWriter();
		writer.SetInputData(calc.GetPolyDataOutput());
		writer.SetFileName(outfile);
		writer.Update();

		//		vtkDoubleArray arr = (vtkDoubleArray) calc.GetPolyDataOutput().GetCellData().GetArray("result");
		//		if (arr.GetNumberOfComponents()>1)
		//		{
		//			throw new Error("Result is a vector; func must evaluate to a scalar: "+ funcstr);
		//		}
		//
		//		System.out.println("# " + infile);
		//		System.out.println("# " + polydata.GetNumberOfCells() + " triangles");
		//		for (int i = 0; i < polydata.GetNumberOfCells(); i++)
		//		{
		//			System.out.println(i + " " + arr.GetValue(i));
		//		}

	}
}

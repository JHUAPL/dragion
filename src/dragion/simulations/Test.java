package dragion.simulations;

import java.util.Arrays;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Test
{
	public static void main(String[] args)
	{
		Vector3D ballpt = new Vector3D(0.598711, -1.59151, 1.39768);
		Vector3D rootpt = new Vector3D(0.584372, -1.5271, 1.31787);

		//		Rotation rot = new Rotation(Vector3D.PLUS_I, ballpt.subtract(rootpt));
		//		double[] angles = rot.getAngles(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR);
		//		System.out.println(Math.toDegrees(angles[0]) + " " + Math.toDegrees(angles[1]) + " " + Math.toDegrees(angles[2]));

		Vector3D ballpt2 = new Vector3D(-0.615405, 1.07189, 0.588896);
		Vector3D rootpt2 = new Vector3D(-0.450092, 0.998457, 0.674219);

		//		Rotation rot2 = new Rotation(Vector3D.PLUS_I, ballpt2.subtract(rootpt2));
		//		double[] angles2 = rot2.getAngles(RotationOrder.XYZ, RotationConvention.VECTOR_OPERATOR);
		//		System.out.println(Math.toDegrees(angles2[0]) + " " + Math.toDegrees(angles2[1]) + " " + Math.toDegrees(angles2[2]));

		for (double len = 0; len <= 0.20; len += 0.04)
		{
			Vector3D nml = ballpt.subtract(rootpt).normalize();
			Vector3D pt = rootpt.add(nml.scalarMultiply(len));
			Vector3D nml2 = ballpt2.subtract(rootpt2).normalize();
			Vector3D pt2 = rootpt2.add(nml2.scalarMultiply(len));

//			System.out.println(len + " " + pt.getX() + " " + pt.getY() + " " + pt.getZ() + " " + pt2.getX() + " " + pt2.getY() + " " +pt2.getZ());
			
			Vector3D dr = pt2.subtract(pt);
			double th = Math.acos(-dr.getZ()/dr.getNorm());
			double phi = Math.atan2(-dr.getY(), -dr.getX());
			System.out.println(len + " " + Math.toDegrees(th)+ " " + Math.toDegrees(phi));
		}

		//		Vector3D nml = new Vector3D(1.1508, -2.7737, 0.9379);
		//		Vector3D e1 = nml.orthogonal();
		//		Vector3D e2 = nml.crossProduct(e1);
		//		
		//		Vector3D origin = new Vector3D(0.5726, -1.5758, 1.3801);
		//		System.out.println(e1.normalize().add(origin.add(nml.subtract(origin)).scalarMultiply(0.5)));
		//		System.out.println(e2.normalize().add(origin.add(nml.subtract(origin)).scalarMultiply(0.5)));
		//
		//		System.out.println(nml.getNorm());
		//		
		//		Rotation rot = new Rotation(Vector3D.PLUS_K, nml);
		//		double[] angles = rot.getAngles(RotationOrder.XYZ);
		//		System.out.println(Math.toDegrees(angles[0]) + " " + Math.toDegrees(angles[1]) + " " + Math.toDegrees(angles[2]));
	}
}

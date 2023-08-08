package dragion.util;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.tomlj.TomlArray;

public class TomlUtil
{
	public static double scalar(Object obj)
	{
		return Double.valueOf(obj.toString());
	}

	public static int integer(Object obj)
	{
		return Integer.valueOf(obj.toString());
	}
	
	public static boolean bool(Object obj)
	{
		return Boolean.valueOf(obj.toString());
	}

	public static double[] array(Object obj)
	{
		double[] result;
		if (obj instanceof TomlArray)
		{
			TomlArray arr = (TomlArray) obj;
			result = new double[arr.size()];
			for (int i = 0; i < arr.size(); i++)
			{
				result[i] = scalar(arr.get(i));
			}
		}
		else
		{
			throw new Error("Cannot convert " + obj.getClass().toString() + " to double[]");
		}
		return result;
	}

	static final String zero_key = "ZERO";
	static final String plusx_key = "PLUS_X";
	static final String plusy_key = "PLUS_Y";
	static final String plusz_key = "PLUS_Z";
	static final String minusx_key = "MINUS_X";
	static final String minusy_key = "MINUS_Y";
	static final String minusz_key = "MINUS_Z";

	public static Vector3D vector(Object obj)
	{
		if (obj instanceof TomlArray)
		{
			TomlArray arr = (TomlArray) obj;
			double x = scalar(arr.get(0));
			double y = scalar(arr.get(1));
			double z = scalar(arr.get(2));
			return new Vector3D(x, y, z);
		}
		//		else if (obj instanceof Double || obj instanceof Long)
		//		{
		//			double val = scalar(obj);
		//			return new Vector3D(val, val, val);
		//		}
		//		else if (obj instanceof String)
		//		{
		//			String str = (String) obj;
		//			if (str.equals(zero_key))
		//			{
		//				return Vector3D.ZERO;
		//			}
		//			else if (str.equals(plusx_key))
		//			{
		//				return Vector3D.PLUS_I;
		//			}
		//			else if (str.equals(plusy_key))
		//			{
		//				return Vector3D.PLUS_J;
		//			}
		//			else if (str.equals(plusz_key))
		//			{
		//				return Vector3D.PLUS_K;
		//			}
		//			else if (str.equals(minusx_key))
		//			{
		//				return Vector3D.MINUS_I;
		//			}
		//			else if (str.equals(minusy_key))
		//			{
		//				return Vector3D.MINUS_J;
		//			}
		//			else if (str.equals(minusz_key))
		//			{
		//				return Vector3D.MINUS_K;
		//			}
		//			else
		//			{
		//				throw new Error("Invalid string value for vector: '" + str + "'; expected one of " + zero_key + ", "+plusx_key+", ..., "+minusx_key+", ...");
		//			}
		//		}
		else
		{
			throw new Error("Cannot convert " + obj.getClass().toString() + " to Vector3D");
		}
	}

}

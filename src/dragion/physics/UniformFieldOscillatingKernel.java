package dragion.physics;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import lestat.kernels.UniformFieldKernel;

public class UniformFieldOscillatingKernel extends UniformFieldKernel
{
	final double frequency;
	double time = 0;
	
	public void setTime(double time)
	{
		this.time = time;
	}
	
	public UniformFieldOscillatingKernel(Vector3D efld, Vector3D gaugept, double frequency)
	{
		super(efld, gaugept);
		this.frequency = frequency;
	}

	public UniformFieldOscillatingKernel(Vector3D efld, double frequency)
	{
		super(efld);
		this.frequency = frequency;
	}

	protected double getCosFac()
	{
		return Math.cos(2.*Math.PI*frequency*time);
	}
	
	@Override
	public double computePotential(Vector3D pos)
	{
		return super.computePotential(pos) * getCosFac();
	}

	@Override
	public Vector3D computeField(Vector3D pos)
	{
		return super.computeField(pos).scalarMultiply(getCosFac());
	}

	
}

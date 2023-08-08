package dragion.physics;

import java.util.Collection;
import java.util.stream.Stream;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.special.Erf;

import lestat.particles.Advancer;
import lestat.particles.Particle;
import mowgli.Constants;

public class DriftDiffusionAdvancer implements Advancer
{

	final double mobility;
	final double temperature;
	//final Vector3D windvel;

	final boolean usetemp;
	//final boolean usewind;

	public DriftDiffusionAdvancer(double mobility, double temperature)//, Vector3D windvel)
	{
		this.mobility = mobility;
		this.temperature = temperature;
	//	this.windvel = windvel;
		this.usetemp = (mobility != 0) || (temperature != 0);
	//	this.usewind = !windvel.equals(Vector3D.ZERO);
	}

	@Override
	public void advance(Collection<Particle> particles, double dt)
	{
		Stream<Particle> stream = particles.parallelStream();
		stream.forEach((p) ->
		{
			//
			// drift part
			Vector3D vnew = p.getEfld().scalarMultiply(mobility);
			//
			// diffusion part... lots of Math.* method calls here so maybe some room for optimization
			if (usetemp)
			{
				double D = mobility * Constants.boltzmann * temperature / Constants.qe; // diffusion coefficient
				double lam = Math.sqrt(4 * D * dt); // characteristic diffusive jump length
				double c = 2 * Math.random() - 1;
				double dr = Math.pow(lam * Math.sqrt(Math.PI), 3) * Erf.erfInv(c);
				double phi = 2 * Math.PI * Math.random();
				double th = Math.acos(1 - 2 * Math.random());
				double dxp = dr * Math.cos(phi) * Math.sin(th);
				double dyp = dr * Math.sin(phi) * Math.sin(th);
				double dzp = dr * Math.cos(th);
				Vector3D jumplen = new Vector3D(dxp, dyp, dzp);
				Vector3D vdiff = jumplen.scalarMultiply(1. / dt);
				vnew = vnew.add(vdiff);
			}
			//
			// wind advection part
		//	if (usewind)
		//	{
		//		vnew = vnew.add(windvel);
		//	}
			//
			p.setVel(vnew);
			//
			Vector3D xold = p.getPos();
			p.setOldPos(xold);
			//
			Vector3D xnew = xold.add(vnew.scalarMultiply(dt));
			p.setPos(xnew);
		});
	}

}

package dragion.physics;

import java.util.Collection;
import java.util.stream.Stream;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import lestat.particles.Advancer;
import lestat.particles.Particle;

public class FluidAdvancer implements Advancer
{
	final Vector3D windvel;
	final double mobility;
	final double charge;
	final double mass;
	
	public FluidAdvancer(Vector3D windvel, double mobility, double charge, double mass)
	{
		this.windvel = windvel;
		this.mobility = mobility;
		this.charge = charge;
		this.mass = mass;
	}

	@Override
	public void advance(Collection<Particle> plist, double dt)
	{
		double tauc = mass * mobility / charge;
		double expterm = Math.exp(-dt/tauc);
		Stream<Particle> stream = plist.parallelStream();
		stream.forEach((p) ->
		{
			Vector3D vold = p.getVel();
			Vector3D euterm = p.getEfld().scalarMultiply(mobility).add(windvel);
			Vector3D vnew = vold.scalarMultiply(expterm).add(euterm.scalarMultiply(1-expterm));
			//
			Vector3D xold = p.getPos();
			Vector3D xnew = xold.add(vnew.scalarMultiply(dt));
			//
			p.setVel(vnew);
			p.setOldPos(xold);
			p.setPos(xnew);
		});
		
	}

	
	
}

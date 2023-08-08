package dragion.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import lestat.particles.Particle;
import lestat.particles.Species;
import lestat.particles.emitcollect.Emitter;
import lestat.particles.emitcollect.RateDiscretizer;
import lestat.solvers.CompositeSolver;
import mowgli.samplers.QuadUniformSampler;
import mowgli.shapes.Box;
import mowgli.shapes.Box.Side;
import mowgli.shapes.Quad;
import mowgli.surfaces.EmbreeIntersector;
import mowgli.surfaces.Surface;

public class SideFluxInjector implements Emitter
{
	final Box bbox;
	final CompositeSolver solver;
	final Species species;
	final Function<Vector3D, Double> density;
	final Function<Vector3D, Vector3D> windvel;
	final double mobility;

	Multimap<Side, Quad> allquads = ArrayListMultimap.create();
	Map<Quad, QuadUniformSampler> samplers = new HashMap<>();
	Map<Quad, RateDiscretizer> discretizers = new HashMap<>();

	protected boolean checkInside(EmbreeIntersector intersector, Quad quad)
	{
		return (intersector.isInside(quad.getP1()) || intersector.isInside(quad.getP2()) || intersector.isInside(quad.getP3()) || intersector.isInside(quad.getP4()) || intersector.isInside(quad.getCenter()));
	}

	public SideFluxInjector(Box bbox, CompositeSolver solver, Species species, int subdiv, Function<Vector3D, Double> density, double mobility, EmbreeIntersector intersector, Function<Vector3D, Vector3D> windvel)
	{
		this.bbox = bbox;
		this.solver = solver;
		this.species = species;
		this.density = density;
		this.mobility = mobility;
		this.windvel = windvel;

		Map<Side, Quad> sides = Box.inwardPointingSides(bbox); // normals are inward pointing
		for (Side side : sides.keySet())
		{
			Quad parent = sides.get(side);
			//			int subdiv = (int) Math.floor(Math.log(parent.getLength() / approxPanelSize) / Math.log(2));	// log2(parent length / panel size)
			Quad[] children = Quad.subdivide(parent, subdiv);
			for (Quad child : children)
			{
				if (!checkInside(intersector, child))
				{
					allquads.put(side, child);
					samplers.put(child, new QuadUniformSampler(child));
					discretizers.put(child, new RateDiscretizer());
				}
			}
		}

	}

	@Override
	public Collection<Particle> emit(double dt)
	{
		List<Particle> particles = new ArrayList<>();
		for (Quad quad : allquads.values())
		{
			// estimate flux as (mobility * efld + windvel) * area at quad center
			Vector3D ctr = quad.getCenter();
			Vector3D ctrefld = solver.synthesizeElectricField(ctr);	
			double n0 = density.apply(ctr);
			Vector3D ctrwindvel = windvel.apply(ctr);
			Vector3D ctrvel = ctrefld.scalarMultiply(mobility).add(ctrwindvel);
			double ctrvelprp = ctrvel.dotProduct(quad.getNormal());
			if (ctrvelprp > 0) // normals are inward-pointing
			{
				RateDiscretizer discretizer = discretizers.get(quad);
				discretizer.setRate(n0 * ctrvelprp * quad.getArea() / species.getNp2c());
				int ntoemit = discretizer.update(dt);
				//
				for (int i = 0; i < ntoemit; i++)
				{
					double dtpart = dt * Math.random();
					Vector3D oldpos = samplers.get(quad).get();
					Vector3D newpos = oldpos.add(ctrvel.scalarMultiply(dtpart));
					Particle p = new Particle(newpos, ctrvel, species);
					p.setOldPos(oldpos);
					particles.add(p);
				}
			}
		}

		return particles;
	}
}

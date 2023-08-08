package dragion.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import lestat.particles.Particle;
import lestat.particles.Species;
import lestat.particles.emitcollect.Emitter;
import mowgli.grid.CellScalar;
import mowgli.grid.Grid;
import mowgli.samplers.VelocitySampler;
import mowgli.shapes.Box;

public class VolumeSource implements Emitter
{
	final SourceFunction sourcefunc; // [#/m^3/s]; space- and time-dependent (ignoring time-dependence for now)
	final Species species;
	final VelocitySampler velsamp;

	final Grid grid;
	final CellScalar ntoemit;

	public CellScalar getNtoemit()
	{
		return ntoemit;
	}

	public VolumeSource(Species species, VelocitySampler velsamp, SourceFunction sourcefunc, Box bbox, int gridres)
	{
		this.sourcefunc = sourcefunc;
		this.species = species;
		this.velsamp = velsamp;
		this.grid = Grid.approximateSubdivide(bbox, gridres);
		this.ntoemit = new CellScalar(grid);
	}

	void updateNumberToEmit(double dt)
	{
		for (int i = 0; i < grid.getNxp() - 1; i++)
		{
			for (int j = 0; j < grid.getNyp() - 1; j++)
			{
				for (int k = 0; k < grid.getNzp() - 1; k++)
				{
					ntoemit.getData()[i][j][k] += sourcefunc.apply(grid.getCellCenter(i, j, k), 0.) * grid.getCellVolume() * dt;
				}
			}
		}
	}

	@Override
	public Collection<Particle> emit(double dt)
	{

		updateNumberToEmit(dt);
		double[][][] ntoemit = this.ntoemit.getData();

		List<Particle> particles = new ArrayList<>();

		for (int i = 0; i < grid.getNxp() - 1; i++)
		{
			for (int j = 0; j < grid.getNyp() - 1; j++)
			{
				for (int k = 0; k < grid.getNzp() - 1; k++)
				{
					while (ntoemit[i][j][k] > species.getNp2c())
					{
						particles.add(createParticle(i, j, k));
						ntoemit[i][j][k] -= species.getNp2c();
					}
					if (ntoemit[i][j][k] > 0)
					{
						if (Math.random() < ntoemit[i][j][k] / species.getNp2c())
						{
							particles.add(createParticle(i, j, k));
							ntoemit[i][j][k] -= species.getNp2c();
						}
					}
				}
			}
		}

		return particles;
	}

	Particle createParticle(int i, int j, int k)
	{
		Vector3D pos = grid.sampleCell(i, j, k);
		Vector3D vel = velsamp.get();
		return new Particle(pos, vel, species);
	}

}

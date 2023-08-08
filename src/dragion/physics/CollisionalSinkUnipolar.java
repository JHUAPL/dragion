package dragion.physics;

import java.util.Iterator;
import java.util.List;

import lestat.particles.Particle;
import mowgli.grid.CellScalar;
import mowgli.grid.Grid;
import mowgli.grid.InterpolationFactor;
import mowgli.shapes.Box;

public class CollisionalSinkUnipolar
{

	final Grid grid;
	final CellScalar sinkrate; // number / m^3 / s
	final CellScalar density; // number / m^3
	final CellScalar numberToLose; // number per cell; soy un perdedor so why don't you kill me...

	public Grid getGrid()
	{
		return grid;
	}

	public CellScalar getSinkrate()
	{
		return sinkrate;
	}

	//	public CellScalar getNumberToLose()
	//	{
	//		return numberToLose;
	//	}

	public CellScalar getDensity()
	{
		return density;
	}

	public static final double alpha = 1e-12;

	public CollisionalSinkUnipolar(Box bbox, int gridres)
	{
		this.grid = Grid.approximateSubdivide(bbox, gridres);
		this.sinkrate = new CellScalar(grid, "sinkrate [#/m^3/s]");
		this.density = new CellScalar(grid, "density [#/m^3]");
		this.numberToLose = new CellScalar(grid, "numberToLose [#/timestep]");
	}
	
	public double checkPPC(double den, double np2c)
	{
		double cellvol = grid.getDx() * grid.getDy() * grid.getDz();
		double ptclvol = np2c/den;
		double ptclPerCell = cellvol/ptclvol;
		return ptclPerCell;
	}

	public void collide(List<Particle> particles, double dt)
	{
		density.zero();

		double[][][] density = this.density.getData();

		Iterator<Particle> it = particles.iterator();
		while (it.hasNext())
		{
			Particle p = it.next();
			InterpolationFactor fac = grid.computeInterpolationFactor(p.getPos());
			int i = fac.ilower;
			int j = fac.jlower;
			int k = fac.klower;
			if (fac.isGood())
			{
				density[i][j][k] += p.getSpecies().getNp2c() / grid.getCellVolume(); // number density contribution per particle
			}
			else
			{
				throw new Error("Bad interpolation factor: position = " + p.getPos());
			}
		}

		sinkrate.zero();
		double[][][] sinkrate = this.sinkrate.getData();

		//numberToLose.zero();
		double[][][] numberToLose = this.numberToLose.getData();

		for (int i = 0; i < grid.getNxc(); i++)
		{
			for (int j = 0; j < grid.getNyc(); j++)
			{
				for (int k = 0; k < grid.getNzc(); k++)
				{
					sinkrate[i][j][k] = density[i][j][k] * density[i][j][k] * alpha; // number / m^3 / s
					numberToLose[i][j][k] += sinkrate[i][j][k] * dt * grid.getCellVolume();
				}
			}
		}

		it = particles.iterator();
		while (it.hasNext())
		{
			Particle p = it.next();
			InterpolationFactor fac = grid.computeInterpolationFactor(p.getPos());
			int i = fac.ilower;
			int j = fac.jlower;
			int k = fac.klower;
			if (fac.isGood())
			{
				if (numberToLose[i][j][k] > 0)
				{
					if (numberToLose[i][j][k] > p.getSpecies().getNp2c())
					{
						it.remove();
						numberToLose[i][j][k] -= p.getSpecies().getNp2c();
					}
					else
					{
						if (Math.random() < numberToLose[i][j][k] / p.getSpecies().getNp2c())
						{
							it.remove();
							numberToLose[i][j][k] -= p.getSpecies().getNp2c();
						}
					}
				}

			}
			else
			{
				throw new Error("Bad interpolation factor: position = " + p.getPos());
			}
		}

	}

}

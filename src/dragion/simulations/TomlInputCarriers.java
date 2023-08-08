package dragion.simulations;

import java.nio.file.Path;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import dragion.physics.CollisionalSinkUnipolar;
import dragion.physics.SideFluxInjector;
import dragion.util.TomlUtil;
import lestat.particles.emitcollect.Emitter;
import mowgli.Constants;
import mowgli.shapes.Box;

public class TomlInputCarriers extends TomlInputFields
{
	protected static final String SIZE = "size";
	protected static final String CENTER = "center";
	//
	protected static final String CARRIERS = "carriers";
	//	static final String NAME = "name";
	protected static final String NP2C = "np2c";
	protected static final String CHARGESTATE = "chargestate";
	//	static final String SOURCERATE = "sourcerate";
	protected static final String CONCENTRATION = "concentration";
	//	static final String MOBILITY = "mobility";
	protected static final String CONDUCTIVITY = "conductivity";
	protected static final String TEMPERATURE = "temperature";
	protected static final String AMU = "amu";
	
//	protected static final String INJECTDIV = "injectdiv"; 

	protected final Box domainbox;
//	protected final Emitter sideflux;
	protected final UniformCarriers carriers;

	public static class UniformCarriers
	{
		//		String name;
		public double np2c;
		public double chargestate;
		public double concentration;
		public double mobility;
		public double temperature;
		public double amu;
	}

	public Box getDomainBox()
	{
		return domainbox;
	}
	
	public UniformCarriers getCarriers()
	{
		return carriers;
	}

	public TomlInputCarriers(Path inputfile)
	{
		super(inputfile);

		domainbox = initDomainBox(parseResult);
		carriers = initCarriers(parseResult);
//		sideflux = initSideFlux(parseResult, domainbox, carriers);
	}

//	public static Emitter initSideFlux(TomlParseResult parseResult, Box domainBox, UniformCarriers carriers)
//	{
//		TomlTable domainSpec = parseResult.getTable(DOMAIN);
//		if (domainSpec.contains(INJECT))
//		{
//			return new SideFluxInjector(domainBox, getsolver, null, 0, null, 0)
//		}
//		else
//		{
//			return Emitter.none();
//		}
//	}
	
	public static Box initDomainBox(TomlParseResult parseResult)
	{
		TomlTable domainSpec = parseResult.getTable(DOMAIN);

		final Vector3D center;
		final Vector3D size;

		if (domainSpec.contains(CENTER))
		{
			center = TomlUtil.vector(domainSpec.get(CENTER));
		}
		else
		{
			center = Vector3D.ZERO;
		}

		size = TomlUtil.vector(domainSpec.get(SIZE));

		return new Box(center, size.getX(), size.getY(), size.getZ());

	}

	public static UniformCarriers initCarriers(TomlParseResult parseResult)
	{
		TomlTable carrierSpec = parseResult.getTable(CARRIERS);

		UniformCarriers carriers = new UniformCarriers();

		carriers.np2c = TomlUtil.scalar(carrierSpec.get(NP2C));
		carriers.chargestate = TomlUtil.scalar(carrierSpec.get(CHARGESTATE));

		carriers.concentration = TomlUtil.scalar(carrierSpec.get(CONCENTRATION));
//		double sourcerate = concentration * concentration * CollisionalSinkUnipolar.alpha;

		double conductivity = TomlUtil.scalar(carrierSpec.get(CONDUCTIVITY));
		carriers.mobility = conductivity / (carriers.concentration * carriers.chargestate * Constants.qe);

		carriers.temperature = TomlUtil.scalar(carrierSpec.get(TEMPERATURE));

		carriers.amu = TomlUtil.scalar(carrierSpec.get(AMU));
		
//		
//		System.out.println("np2c = " + carriers.np2c);
//		System.out.println("chargestate = " + carriers.chargestate);
//		System.out.println("concentration = " + carriers.concentration);
//		System.out.println("conductivity = " + conductivity);
//		System.out.println("mobility = " + carriers.mobility);
//		System.out.println("temperature = " + carriers.temperature);
		return carriers;

	}
}

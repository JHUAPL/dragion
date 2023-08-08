package dragion.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class OptionUtil
{

	// TODO: add required/optional distinction to options?

	final CommandLine cli;
	final Options opts;

	public OptionUtil(String[] args, Option... options)
	{
		opts = new Options();
		for (Option opt : options)
		{
			opts.addOption(opt);
		}

		CommandLine cli = null;
		try
		{
			cli = new DefaultParser().parse(opts, args);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		this.cli = cli;
	}

	public void printHelp()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(" ", opts);
	}

	public boolean isSpecified(Option opt)
	{
		return cli.hasOption(opt);//.getOptionValue(opt) != null;
	}

	public String getString(Option opt)
	{
		return cli.getOptionValue(opt);
	}

	public String getString(Option opt, int i)
	{
		return cli.getOptionValues(opt)[i];
	}

	public double getDouble(Option opt)
	{
		return Double.valueOf(getString(opt));
	}

	public double getDouble(Option opt, int i)
	{
		return Double.valueOf(getString(opt, i));
	}

	public int getInt(Option opt)
	{
		return Integer.valueOf(getString(opt));
	}

	public int getInt(Option opt, int i)
	{
		return Integer.valueOf(getString(opt, i));
	}

	public boolean getBoolean(Option opt)
	{
		return Boolean.valueOf(getString(opt));
	}

	public double getDoubleOrDefault(Option opt, double val)
	{
		if (isSpecified(opt))
		{
			return getDouble(opt);
		}
		else
		{
			return val;
		}
	}

	public boolean getBooleanOrDefault(Option opt, boolean val)
	{
		if (isSpecified(opt))
		{
			return getBoolean(opt);
		}
		else
		{
			return val;
		}
	}

	public int getIntOrDefault(Option opt, int val)
	{
		if (isSpecified(opt))
		{
			return getInt(opt);
		}
		else
		{
			return val;
		}
	}

	public String[] getCommaSeparatedStrings(Option opt)
	{
		String str = getString(opt);
		return str.split(",");
	}

	public double[] getCommaSeparatedDoubles(Option opt)
	{
		String[] strs = getCommaSeparatedStrings(opt);
		double[] arr = new double[strs.length];
		for (int i = 0; i < strs.length; i++)
		{
			arr[i] = Double.valueOf(strs[i]);
		}
		return arr;
	}
	
	public int[] getCommaSeparatedInts(Option opt)
	{
		String[] strs = getCommaSeparatedStrings(opt);
		int[] arr = new int[strs.length];
		for (int i = 0; i < strs.length; i++)
		{
			arr[i] = Integer.valueOf(strs[i]);
		}
		return arr;
	}

}
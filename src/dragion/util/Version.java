package dragion.util;

public class Version
{
	public static String version = "1";
	
//		public static String flowsnake2="            ________\n"
//				+ "            \\       \\\n"
//				+ "  ________   \\____   \\\n"
//				+ "  \\       \\      /   /\n"
//				+ "   \\____   \\____/   /   ____\n"
//				+ "       /            \\   \\   \\\n"
//				+ "  ____/   ________   \\   \\   \\\n"
//				+ " /        \\       \\   \\  /\n"
//				+ "/   ____   \\____   \\   \\/\n"
//				+ "\\   \\   \\      /   /\n"
//				+ " \\   \\   \\____/   /   ____\n"
//				+ "  \\  /            \\  /   /\n"
//				+ "   \\/   ________   \\/   /\n"
//				+ "        \\       \\      /\n"
//				+ "         \\____   \\____/\n"
//				+ "             /\n"
//				+ "        ____/";	// from https://ascii.co.uk/art/tiling
		

		public static String flowsnake2version="              ________\n"
				+ "              \\       \\\n"
				+ "    ________   \\____   \\\n"
				+ "    \\       \\      /   /\n"
				+ "     \\____   \\____/   /   ____\n"
				+ "         /  dragion   \\   \\   \\\n"
				+ "    ____/   ________   \\   \\   \\\n"
				+ "   /        \\   v1  \\   \\  /   /\n"
				+ "  /   ____   \\____   \\   \\/   /\n"
				+ "  \\   \\   \\      /   /       / \n"
				+ " ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n";
//				+ " \\   \\   \\____/   /   ____\n"
//				+ "  \\  /            \\  /   /\n"
//				+ "   \\/   ________   \\/   /\n"
//				+ "        \\       \\      /\n"
//				+ "         \\____   \\____/\n"
//				+ "             /\n"
//				+ "        ____/";	// from https://ascii.co.uk/art/tiling
		
//		public static final String ANSI_RED = "\u001B[31m";
//		public static final String ANSI_BLUE = "\u001B[34m";

		public static void printHelp(OptionUtil opts)
		{
			System.out.println();
//			System.out.println("dragion v" + version);
//			System.out.println();
			System.out.println(flowsnake2version);
			if (opts != null)
			{
				opts.printHelp();
			}
			System.out.println();
		}

		public static void main(String[] args)
		{
			printHelp(null);
		}
}

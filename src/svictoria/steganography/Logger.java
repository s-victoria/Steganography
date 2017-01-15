package svictoria.steganography;

public class Logger {

	public static boolean DEBUG = true;

	public static void output(String text) {
		System.out.println(text);
	}

	public static void print(String text) {
		if (DEBUG)
		{
			System.out.print(text);
		}
	}

	public static void print(char c) {
		if (DEBUG)
		{
			System.out.print(c);
		}
	}

	public static void printBit(boolean b) {
		if (DEBUG)
		{
			System.out.print((b) ? "1" : "0");
		}
	}

	public static void println(String text) {
		if (DEBUG)
		{
			System.out.println(text);
		}
	}

	public static void println() {
		if (DEBUG)
		{
			System.out.println();
		}
	}
}

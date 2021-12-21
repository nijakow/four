package nijakow.four.client.utils;

public class StringHelper {
	public static String generateFilledString(char with, int length) {
		String ret = "";
		for (int i = 0; i < length; i++)
			ret += with;
		return ret;
	}
}
package nijakow.four.client.utils;

public class StringHelper {
	public static String generateFilledString(char with, int length) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < length; i++)
			ret.append(with);
		return ret.toString();
	}
}
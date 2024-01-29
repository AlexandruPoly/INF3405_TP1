package app;

public class Utils {
	public static boolean isIPValid(String serverAddress) {

		// Vérification
		String[] segments = serverAddress.split("\\.");
		if (segments.length != 4) {
			return false;
		}
		for (String segment : segments) {
			int value = Integer.parseInt(segment);
			if (value < 0 || value > 255) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPortValid(int port) {
		
		// Vérification
		return port>=5000&&port<5050;
	}
}
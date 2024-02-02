package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {
	private static final String FILENAME = "database.csv";
	
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
		return port>=5000&&port<=5050;
	}
	
	public static void writeToDatabase(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Utilisateur readUserFromDatabase(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
        	String line;
            while ((line = reader.readLine()) != null) {
            	System.out.println(line);
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(username)) {
                    return new Utilisateur(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) {

        try {
            // Create a File object
            File file = new File(FILENAME);

            // Check if the file already exists
            if (file.exists()) {
                System.out.println("File " + FILENAME + " already exists.");
            } else {
                // Create a new file
                if (file.createNewFile()) {
                    System.out.println("File " + FILENAME + " created successfully.");
                } else {
                    System.out.println("Failed to create file " + FILENAME);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
    }
}


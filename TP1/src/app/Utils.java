package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class Utils {
	private static final String USERSFILENAME = "usersDataBase.csv";
	private static final String MESSAGESFILENAME = "messagesDataBase.csv";

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

	public static void writeToDatabase(String data, String filename) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
			writer.write(data);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Utilisateur readUserFromDatabase(String username, String filename) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 2 && parts[0].equals(username)) {
					return new Utilisateur(parts[0], parts[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Deque<String> readMessagesFromDatabase(String filename) {
		Deque<String> messagesList = new ArrayDeque<String>(15);
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String log;
			while ((log = reader.readLine()) != null) {
				messagesList.add(log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return messagesList;
	}

	public static void readDatabase(String filename) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			System.out.println("\n---Printing database---");
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println("---End of database---\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			// Create a File object
			File usersFile = new File(USERSFILENAME);
			File messagesFile = new File(MESSAGESFILENAME);
			File[] files = new File[]{usersFile, messagesFile};

			for(File file : files)
				// Check if the file already exists
				if (file.exists()) {
					System.out.println("File " + file.getName() + " already exists.");
					readDatabase(file.getName());
				} else {
					// Create a new file
					if (file.createNewFile()) {
						System.out.println("File " + file.getName() + " created successfully.");
					} else {
						System.out.println("Failed to create file " + file.getName());
					}
				}
		} catch (IOException e) {
			System.out.println("An error occurred while creating the file +file.getName()+: " + e.getMessage());
		}
	}
}


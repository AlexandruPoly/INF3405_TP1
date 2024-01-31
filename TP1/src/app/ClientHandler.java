package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private Socket socket; 
	private int clientNumber; 
	private String username = "Alexnov23";
	private String password = "JavaGoat";
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}

	public void run() { // Création de thread qui envoi un message à un client si identification valide
		// Identification
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String usernameEntry = in.readUTF();
			String passwordEntry = in.readUTF();

			DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi

			if(usernameEntry.equals(username)&&passwordEntry.equals(password)){
				try {
					out.writeUTF("Hello from server - you are client#" + clientNumber); // envoi de message
					boolean connect = true;
					while(connect ) { // Attente de réception des messages
						String message = in.readUTF();
						if(message == "/disconnect") {
							connect = false;
						}else {
							System.out.println(message);							
						}
					}
				} catch (IOException e) {
					System.out.println("Error handling client# " + clientNumber + ": " + e);
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						System.out.println("Couldn't close a socket, what's going on?");}
					System.out.println("Connection with client# " + clientNumber+ " closed");
				}
			}else {
				out.writeUTF("Erreur dans la saisie du mot de passe");
			}
		} catch (IOException e) {
			System.out.println("Erreur de lecture des données du client: " + e.getMessage());
		}
	}
}
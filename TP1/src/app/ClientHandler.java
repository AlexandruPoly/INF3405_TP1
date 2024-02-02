package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private Socket socket; 
	private int clientNumber; 
	private List<String> messageList;
	private List<ClientHandler> clientsList;

	public ClientHandler(Socket socket, int clientNumber, List<String> messageList, List<ClientHandler> clients) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		this.messageList = messageList;
		this.clientsList = clients;
		clients.add(this);
		System.out.println(this.clientsList + "|  |" + clients);
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}

	public void run() { // Création de thread qui envoi un message à un client si identification valide
		// Identification
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String usernameEntry = in.readUTF();
			String passwordEntry = in.readUTF();
			
			Utilisateur entry = new Utilisateur(usernameEntry,passwordEntry);
			Utilisateur existant = Utils.readUserFromDatabase(usernameEntry); // Cherche l'utilisateur dans la BD
			
			if(existant==null) { // Si n'existe pas l'ajoute à la BD
				existant = entry;
				Utils.writeToDatabase(existant.getUsername()+","+existant.getPassword());
				System.out.println("Nouvel utilisateur créé." + entry);
			}

			DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi

			if(entry.equals(existant)){
				try {
					out.writeUTF("Hello from server - you are client#" + clientNumber); // envoi de message
					//for (String messages : messageList)
					//out.writeUTF(messages);

					boolean connect = true;
					while(connect) { // Attente de réception des messages
						String message = in.readUTF();
						if(message.equals("/disconnect")) {
							connect = false;
						}else {	
							System.out.println("[Utilisateur " + clientNumber + " - " + message); //Print côté serveur
							messageList.add(message);
							int lastIndex = messageList.size() - 1;
							String lastMessage = messageList.get(lastIndex);
							printToEachClient("[Utilisateur " + clientNumber + " - " + lastMessage);
						}
					}
				} catch (IOException e) {
					System.out.println("Error handling client# " + clientNumber + ": " + e);
				} finally {
					try {
						socket.close();
						System.out.println("Socket closed.");
					} catch (IOException e) {
						System.out.println("Couldn't close a socket, what's going on?");}
					System.out.println("Connection with client# " + clientNumber+ " closed");
				}
			}else {
				out.writeUTF("Erreur dans la saisie du mot de passe");
				socket.close();
			}
		} catch (IOException e) {
			System.out.println("Erreur de lecture des données du client: " + e.getMessage());
		}
	}

	private void printToEachClient(String message) {
		// Broadcast the message to all connected clients
		for (ClientHandler client : clientsList) {
			System.out.println(client);
			if (client != this) {
				client.writeMessage(message);
			}
		}
	}

	private void writeMessage(String message) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(message);
		} catch (IOException e) {
			System.out.println("Error sending message to client# " + clientNumber + ": " + e);
		}
	}
}
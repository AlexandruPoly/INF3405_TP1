package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.Deque;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private static final String USERSFILENAME = "usersDataBase.csv";
	private static final String MESSAGESFILENAME = "messagesDataBase.csv";
	private Socket socket;
	private int clientNumber; 
	private Deque<String> messagesList;
	private List<ClientHandler> clientsList;

	public ClientHandler(Socket socket, int clientNumber, Deque<String> messageList, List<ClientHandler> clients) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		this.messagesList = messageList;
		this.clientsList = clients;
		clients.add(this);
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}

	public void run() { // Création de thread qui envoi un message à un client si identification valide
		// Identification
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String usernameEntry = in.readUTF();
			String passwordEntry = in.readUTF();

			Utilisateur entry = new Utilisateur(usernameEntry,passwordEntry);
			Utilisateur existant = Utils.readUserFromDatabase(usernameEntry,USERSFILENAME); // Cherche l'utilisateur dans la BD

			DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi

			if(existant==null) { // Si n'existe pas l'ajoute à la BD
				existant = entry;
				Utils.writeToDatabase(existant.getUsername()+","+existant.getPassword(),USERSFILENAME);
				System.out.println("Nouvel utilisateur créé: " + entry.getUsername());
				out.writeUTF("Bienvenue " + entry.getUsername() + ", pour votre première connexion, votre mot de passe à été enregistré.");
			}

			if(entry.equals(existant)){
				try {
					out.writeUTF("Hello from server - you are client#" + clientNumber); // envoi de message
					displayOldMessages(out);
					serverClientInteraction(out, in, entry);
				} catch (IOException e) {
					System.out.println("Error handling client# " + clientNumber + ": " + e);
				} finally {
					try {
						socket.close();
						System.out.println("Socket closed.");
						clientsList.remove(this);
					} catch (IOException e) {
						System.out.println("Couldn't close a socket, what's going on?");}
					System.out.println("Connection with client# " + clientNumber+ " closed");
				}
			}else {
				out.writeUTF("Erreur dans la saisie du mot de passe");
				socket.close();
				clientsList.remove(this);
			}
		} catch (IOException e) {
			System.out.println("Erreur de lecture des données du client: " + e.getMessage());
		}
	}
	
	private void serverClientInteraction(DataOutputStream out, DataInputStream in, Utilisateur entry) throws IOException {
		boolean connect = true;
		while(connect) { 
			String message = in.readUTF();
			if(message.equals("/disconnect")) {
				connect = false;
			}
			else if(message.length()>200){
				out.writeUTF("Attention: La taille des messages est limitée à 200 caractères. Le message n'a pas été envoyé");
			}
			else {
				displayMessage(entry, message);
			}
		}
	}
	
	private void displayOldMessages(DataOutputStream out) throws IOException {
		List<String> lastMessagesList = new ArrayList<>();
		int count = 0;
		for (Iterator<String> iterator = messagesList.descendingIterator(); iterator.hasNext() && count < 15; ) {
		    String message = iterator.next();
		    lastMessagesList.add(message);
		    //out.writeUTF(message);
		    count++;
		}
		for (int i = lastMessagesList.size() - 1; i >= 0; i--) {
			out.writeUTF(lastMessagesList.get(i));
		}
	}
	
	private void displayMessage(Utilisateur entry, String message) {
		printToEachClient("["+entry.getUsername() + "]:" + message);
		Calendar cal = Calendar.getInstance();
		message = "[" + entry.getUsername() + " - " + 
				socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " - "+
				cal.get(Calendar.YEAR)+ "-" +cal.get(Calendar.MONTH)+ "-" +cal.get(Calendar.DAY_OF_MONTH)+ "@" +
				cal.get(Calendar.HOUR)+ ":" +cal.get(Calendar.MINUTE)+ ":" +cal.get(Calendar.SECOND)+"]: " + 
				message;
		System.out.println(message);
		messagesList.add(message);
		Utils.writeToDatabase(message, MESSAGESFILENAME);
	}

	private void printToEachClient(String message) {
		// Broadcast the message to all connected clients
		for (ClientHandler client : clientsList) {
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
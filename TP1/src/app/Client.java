package app;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
// Application client
public class Client {
	private static Socket socket;
	public static void main(String[] args) throws Exception {

		// Creation d'un scanner pour lire la console
		Scanner scanner = new Scanner(System.in);

		// Adresse et port du serveur
		System.out.println("Entrez l'adresse IP du poste sur lequel s'exécute le serveur:");
		String serverAddress = scanner.nextLine(); //127.0.0.1


		// Vérifier les données et throw error si mauvaises
		if(!Utils.isIPValid(serverAddress)) {
			System.out.println("Erreur! Adresse IP invalide");

		}else {
			System.out.println("Entrez le port d'écoute:");
			int port = scanner.nextInt();
			scanner.nextLine();

			if(!Utils.isPortValid(port)) {
				System.out.println("Erreur! Port d'écoute invalide");
			}else {
				// Création d'une nouvelle connexion avec le serveur
				socket = new Socket();

				socket.connect(new InetSocketAddress(serverAddress, port), 5000);
				boolean connected = true;

				// Envoi des données d'identification au serveur
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				System.out.println("Entrez votre nom d'utilisateur :");
				String username = scanner.nextLine();
				out.writeUTF(username);
				System.out.println("Entrez votre mot de passe :");
				String password = scanner.nextLine();
				out.writeUTF(password);

				System.out.format("Serveur lancé sur [%s:%d]\n", serverAddress, port);
				// Céatien d'un canal entrant pour recevoir les messages envoyés, par le serveur
				DataInputStream in = new DataInputStream(socket.getInputStream());

				// Attente de la réception d'un message envoyé par le, server sur le canal
				String helloMessageFromServer = in.readUTF();
				System.out.println(helloMessageFromServer);
				if(helloMessageFromServer.substring(0,3).equals("Err")) { // Détectes une erreur dans username ou password
					connected = false;
				}
				//String oldMessages = in.readUTF();
				//System.out.println(oldMessages);

				Thread readThread = new Thread(() -> {
					try {
						DataInputStream readMessages = new DataInputStream(socket.getInputStream());
						while (true) {
							String receivedMessage = readMessages.readUTF();
							System.out.println(receivedMessage);
						}
					} catch (Exception e) {
						System.out.println("Disconnected from server.");
					}
				});
				readThread.start();
				
				String message = "";

				try {
					while(connected) {// Envoie de message
						message = scanner.nextLine();
						if(message.equals("/disconnect")) {
							connected = false;
						}
						out.writeUTF(serverAddress + ":" + port + " - 2024-1-31@13:02:01]: " + message); //important d'envoyer le disconnect au serveur
					}
				} catch (Exception e) {
					System.out.println("Disconnected from server.");
				}
				// Fermeture de La connexion avec le serveur
				socket.close();
				System.out.println("Socket closed");

				scanner.close();
			}
		}
	}
}
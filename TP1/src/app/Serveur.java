package app;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Serveur {
	private static ServerSocket Listener;
	private static Deque<String> messageList;
	private static List<ClientHandler> clientHandlers = new ArrayList<>();
	private static final String MESSAGESFILENAME = "messagesDataBase.csv";
	// Application Serveur
	public static void main(String[] args) throws Exception {
		// Compteur incrémenté à chaque connexion d'un client au serveur
		int clientNumber = 0;

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
			int serverPort = scanner.nextInt();
			scanner.nextLine();
			scanner.close();

			if(!Utils.isPortValid(serverPort)) {
				System.out.println("Erreur! Port d'écoute invalide");
			}else {

				// Création de la connexien pour communiquer avec les, clients
				Listener = new ServerSocket();
				Listener.setReuseAddress(true);
				InetAddress serverIP = InetAddress.getByName(serverAddress);

				// Association de l'adresse et du port à la connexion
				Listener.bind(new InetSocketAddress(serverIP, serverPort));
				System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
				
				messageList = Utils.readMessagesFromDatabase(MESSAGESFILENAME);
				try {
					// À chaque fois qu'un nouveau client se, connecte, on exécute la fonction 
					// run() de l'objet ClientHandler
					// new ClientHandler(Listener.accept(), clientNumber).run();
					
					while (true) {
						// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
						// Une nouvelle connection : on incrémente le compteur clientNumber
						ClientHandler clientHandler = new ClientHandler(Listener.accept(), clientNumber++, messageList, clientHandlers);
						clientHandler.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					// Fermeture de la connexion
					Listener.close();
				}
			}
		}
	}
	
	public static List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
}


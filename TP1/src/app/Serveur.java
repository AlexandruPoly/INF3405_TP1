package app;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Serveur {
	private static ServerSocket Listener;
	// Application Serveur
	public static void main(String[] args) throws Exception {
		// Compteur incrémenté à chaque connexion d'un client au serveur
		int clientNumber = 0;
		
		// Creation d'un scanner pour lire la console
		Scanner scanner = new Scanner(System.in);
		
		// Adresse et port du serveur
		System.out.println("Entrez l'adresse IP du poste sur lequel s'exécute le serveur:");
		String serverAddress = scanner.nextLine(); //127.0.0.1
		
		System.out.println("Entrez le port d'écoute:");
		int serverPort = scanner.nextInt();
		scanner.close();
		// Vérifier les données et throw error si mauvaises
		
		
		// Création de la connexien pour communiquer ave les, clients
		Listener = new ServerSocket();
		Listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		
		// Association de l'adresse et du port à la connexion
		Listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		try {
			// À chaque fois qu'un nouveau client se, connecte, on exécute la fonction 
			// run() de l'objet ClientHandler
			while (true) {
				// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
				// Une nouvelle connection : on incrémente le compteur clientNumber
				new ClientHandler(Listener.accept(), clientNumber++).start();
			}
		} finally {
			// Fermeture de la connexion
			Listener.close();
		}
	}
}


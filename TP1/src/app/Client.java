package app;
import java.io.DataInputStream; 
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

		System.out.println("Entrez le port d'écoute:");
		int port = scanner.nextInt();
		scanner.close();
		// Vérifier les données et throw error si mauvaises
		

		// Création d'une nouvelle connexion aves le serveur
		socket = new Socket(serverAddress, port);
		System.out.format("Serveur lancé sur [%s:%d]", serverAddress, port);

		// Céatien d'un canal entrant pour recevoir les messages envoyés, par le serveur 
		DataInputStream in = new DataInputStream(socket.getInputStream());

		// Attente de la réception d'un message envoyé par le, server sur le canal
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);

		// Fermeture de La connexion avec le serveur
		socket.close();
	}
}
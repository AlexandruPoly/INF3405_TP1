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
				
				// Détectes une erreur dans username ou password
				if(helloMessageFromServer.substring(0,3).equals("Err")) {
					connected = false;
				}else if(helloMessageFromServer.substring(0,9).equals("Bienvenue")) {
					String hello = in.readUTF();
					System.out.println(hello);
				}
				//String oldMessages = in.readUTF();
				//System.out.println(oldMessages);

				Thread readThread = new Thread(() -> {
					try {
						DataInputStream readMessages = new DataInputStream(socket.getInputStream());
						String infos = "["+username+"(moi)]:";
						while (true) {
							String receivedMessage = readMessages.readUTF();
							System.out.print("\r");
							for(int i = 0;i<infos.length();i++) {
								System.out.print(" ");
							}
							System.out.print("\r"+receivedMessage+"\n");
							System.out.print(infos);
							
						}
					} catch (Exception e) {
						System.out.println("Disconnected from server.");
					}
				});
				readThread.start();
				
				String message = "";

				try {
					while(connected) {// Envoie de message
						System.out.print("["+username+"(moi)]:");
						message = scanner.nextLine();
						if(message.equals("/disconnect")) {
							connected = false;
						}else if(message.length()>200) {
							System.out.println("Attention: La taille des messages est limitée à 200 caractères. "
									+ "Le message n'a pas été envoyé");
						}
						out.writeUTF(message); //important d'envoyer le disconnect au serveur
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
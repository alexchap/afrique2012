/**
 * 
 */
package org.tomcat.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;

/**
 * Classe permettant de g�rer les fichiers
 * 
 * @author alex
 * 
 */
public class FileManager {

	// Dossier o� sera enregistr� de fa�on temporaire les album des utilisateurs
	//public static final String DEFAULT_DB_PATH = "C:\\Users\\alex\\Pictures\\PicsApp\\";
	public static final String DEFAULT_DB_PATH = "/home/chappuis/";

	/**
	 * 
	 */
	public FileManager() {
	}

	/**
	 * Cr�e un dossier dans le repertoire par d�faut
	 * 
	 * @param name
	 *            Nom du dossier � cr�er
	 * @return Oui, si le dossier est cr�e avec succ�s et non dans le cas
	 *         contraire
	 */
	public boolean createDirectory(String name) {
		String folderPath = DEFAULT_DB_PATH + name;
		return new File(folderPath).mkdir();
	}

	/**
	 * v�rifie si un dossier existe
	 * 
	 * @param directory
	 * 			Nom du dossier 
	 * @return
	 * 		"oui" si le dossier existe et "non" sinon
	 */
	public boolean exists(String directory) {
		return new File(DEFAULT_DB_PATH + directory).exists();
	}

	/**
	 * Sauvegarde l'image dans le disque
	 * 
	 * @param item
	 *            L'image
	 * @param sender
	 *            Exp�diteur
	 * @param path
	 * 			  Chemin absolu du fichier
	 * 
	 *  @return
	 *  		 "oui" Si l'enregistrement s'est fait avec succ�s
	 */
	public boolean saveImageToDisk(DiskFileItem item, String sender, String path) {
		try {
			File file = new File(path);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(item.get());
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
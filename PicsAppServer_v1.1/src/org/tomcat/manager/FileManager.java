/**
 * 
 */
package org.tomcat.manager;

import java.io.File;

/**
 * Classe permettant de g�rer les fichiers
 * @author alex
 *
 */
public class FileManager {
	
	// dossiers des photos du syst�me
		public static final String DEFAULT_DB_PATH = "C:\\Users\\alex\\Pictures\\";
		// Dossier o� sera enregistr� de fa�on temporaire les album des utilisateurs
		public static final String DEFAULT_DB_FOLDER = "PicsApp\\";

		/**
		 * 
		 */
		public FileManager() {
			// TODO Auto-generated constructor stub
		}
		
		/**
		 * Cr�e un dossier dans le repertoire par d�faut 
		 * @param name
		 * 			Nom du dossier � cr�er
		 * @return
		 * 		Oui, si le dossier est cr�e avec succ�s 
		 * 		et non dans le cas contraire
		 */
		public boolean createDirectory(String name) {
			String folderPath = DEFAULT_DB_PATH + DEFAULT_DB_FOLDER + name;
			return new File(folderPath).mkdir();
		}
		


}

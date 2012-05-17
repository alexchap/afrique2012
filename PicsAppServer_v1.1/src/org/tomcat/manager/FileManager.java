/**
 * 
 */
package org.tomcat.manager;

import java.io.File;

/**
 * Classe permettant de gérer les fichiers
 * @author alex
 *
 */
public class FileManager {
	
	// dossiers des photos du système
		public static final String DEFAULT_DB_PATH = "C:\\Users\\alex\\Pictures\\";
		// Dossier où sera enregistré de façon temporaire les album des utilisateurs
		public static final String DEFAULT_DB_FOLDER = "PicsApp\\";

		/**
		 * 
		 */
		public FileManager() {
			// TODO Auto-generated constructor stub
		}
		
		/**
		 * Crée un dossier dans le repertoire par défaut 
		 * @param name
		 * 			Nom du dossier à créer
		 * @return
		 * 		Oui, si le dossier est crée avec succès 
		 * 		et non dans le cas contraire
		 */
		public boolean createDirectory(String name) {
			String folderPath = DEFAULT_DB_PATH + DEFAULT_DB_FOLDER + name;
			return new File(folderPath).mkdir();
		}
		


}

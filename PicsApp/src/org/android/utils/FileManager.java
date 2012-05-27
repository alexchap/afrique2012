package org.android.utils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

/**
 * Gestionnaire de fichiers de l'application. Prend en charge toutes les
 * opérations sur les albums telles que sauvegarder un album, supprimer un
 * album, vérifier qu'un album d'un certain nom existe, ...
 * 
 * @author Elodie
 * @author Oriane
 * @author Alex
 * 
 */
public class FileManager {
	/** Dossier principal de l'application */
	public final static String APP_FOLDER_PATH = "/sdcard/PicsApp/";

	/** Dossier contenant les albums envoyés par l'utilisateur */
	public final static String SENT_FOLDER_PATH = APP_FOLDER_PATH + "sent/";

	/** Dossier contenant les albums reçus par l'utilisateur */
	public final static String RECEIVED_FOLDER_PATH = APP_FOLDER_PATH
			+ "received/";

	/**
	 * Constructeur créant le dossier principal de l'application s'il n'existe
	 * pas encore.
	 */
	public FileManager() {
		// create a File object for the parent directory
		File picsAppDirectory = new File(APP_FOLDER_PATH);
		picsAppDirectory.mkdirs();
	}

	/**
	 * * Enregistre une image dans le dossier passé en paramètre.
	 * 
	 * @param folderName Le dossier dans lequel enregistrer l'image
	 * @param user L'utilisateur a qui on envoie l'image
	 * @param path Le chemin d'accès de l'image
	 */
	public void savePicture(String folderName, String user, String path) {
		// Vérifie si le chemin correspond à l'un de nos dossiers.
		verifyPath(folderName);

		// Le dossier où enregistrer le fichier.
		File directory = new File(folderName);
		directory.mkdirs();

		// Le fichier à créer / ouvrir.
		File newFile = null;

		if (alreadyExists(user, folderName)) {
			Log.d("FileManager", "Already exists (save image)");
			newFile = new File(directory.getPath() + "/" + user);
		} else {
			Log.d("FileManager", "Doesn't exist (save image)");
			newFile = new File(directory.getPath(), user);
		}

		try {
			FileWriter fstream = new FileWriter(newFile.getPath(), true);
			BufferedWriter writer = new BufferedWriter(fstream);
			writer.write("\n" + path + "\n");
			writer.flush();
			writer.close();

		} catch (IOException e) {
			Log.d("FileManager", "Io Exception while writing");
			e.printStackTrace();
		}
	}
	
	/**
	 * Cette méthode télécharge une image grâce à la connexion conn
	 * et la sauve sur la carte SD
	 * @param conn
	 * @param filename
	 * @return le chemin complet vers l'image
	 */
	public String savePicturetoSD(URLConnection conn, String filename){
		
		File sdcard = Environment.getExternalStorageDirectory();
		File pictureDir = new File(sdcard, "PicsApp");
		
		String picturePath = pictureDir.getAbsolutePath() + "/" + filename;
		
		pictureDir.mkdirs();
		try {
			
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(picturePath);
	
			byte[] b = new byte[2048];
			int length;
	
			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}
	
			is.close();
			os.close();	
		} catch (IOException e) {
			return null;
		} finally {
			
		}
		return picturePath;
	}

	/**
	 * Supprimer un album du dossier spécifié en paramètre.
	 * 
	 * @param name
	 *            Le nom de l'album à supprimer.
	 * @param folderName
	 *            Le nom du dossier où se trouve l'album à supprimer.
	 * @return true si l'album a bien été supprimé, false sinon.
	 */
	public boolean deleteAlbum(String name, String folderName) {
		verifyPath(folderName);
		File directory = new File(folderName);
		directory.mkdirs();

		File toDelete = new File(directory.getPath(), name);
		if (!toDelete.exists()) {
			return true;
		} else {
			// supprime toutes les images du disque pour ce dossier
			ArrayList<String> picts = retrievePictures(name,folderName);
			for(int i=0;i<picts.size();i++){
				File img = new File(picts.get(i));
				boolean ok = img.delete();
				Log.d("delete","image "+ picts.get(i) + " -> " + ok);
			}
		}
		// finalement on supprime le fichier de référence pour l'utilisateur
		return toDelete.delete();
	}

	/**
	 * Récupère tous les albums pour un dossier donné.
	 * 
	 * @param name
	 *            Le nom du dossier dont on doit récupèrer les albums.
	 * @return la liste de noms des albums du dossier
	 */
	public ArrayList<String> retrievePicturesInFolder(String folderName) {
		verifyPath(folderName);
		ArrayList<String> albumList = new ArrayList<String>();

		File directory = new File(folderName);
		boolean created = directory.mkdirs();

		if (!created) {
			String[] children = directory.list();

			if (children != null) {
				for (String child : children) {
					albumList.add(child);
				}
			}
		}

		return albumList;
	}

	/**
	 * Récupère l'album avec le nom spécifié.
	 * 
	 * @param albumName Le nom de l'album à récupérer.
	 * @return L'album.
	 */
	public ArrayList<String> retrievePictures(String userName, String folderName) {
		verifyPath(folderName);

		ArrayList<String> pictures = new ArrayList<String>();

		File directory = new File(folderName);
		directory.mkdirs();

		File toRetrieve = new File(directory.getPath(), userName);

		if (toRetrieve.exists()) {
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			DataInputStream dis = null;

			try {
				fis = new FileInputStream(toRetrieve);

				//  BufferedInputStream utilisé pour une lecture rapide
				bis = new BufferedInputStream(fis);
				dis = new DataInputStream(bis);

				// dis.available() retourne 0 s'il n'y a plus de lignes à lire dans le fichier
				while (dis.available() != 0) {

					String picturePath = dis.readLine();
					if (!picturePath.equals("")) {
						pictures.add(picturePath);
					}
				}

				// on libère les ressources
				fis.close();
				bis.close();
				dis.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return pictures;
	}

	/**
	 * Vérifie si un album du même nom existe déjà
	 * 
	 * @param name Le nom de l'utilisateur dont on veut vérifier l'existance
	 * @return true si un dossier d'utilisateur du même nom de trouve dans le
	 *         dossier saved, false sinon.
	 */
	public boolean alreadyExists(String name, String folderName) {
		boolean alreadyExists = false;
		File savedDirectory = new File(folderName);
		boolean created = savedDirectory.mkdirs();

		if (!created) {
			String[] children = savedDirectory.list();

			if (children != null) {
				for (String child : children) {
					if (child.equals(name)) {
						alreadyExists = true;
					}
				}
			}
		}

		return alreadyExists;
	}

	/**
	 * Vérifie si le chemin passé en paramètre correspond bien à un des dossiers
	 * de l'application.
	 * 
	 * @param folderName Le chemin à vérifier.
	 */
	public void verifyPath(String folderName) {
		if (!(folderName.equals(SENT_FOLDER_PATH) || folderName
				.equals(RECEIVED_FOLDER_PATH))) {
			throw new IllegalArgumentException();
		}
	}
}

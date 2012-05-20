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
import java.io.PrintWriter;
import java.util.ArrayList;

import android.util.Log;

/**
 * Gestionnaire de fichiers de l'application. Prend en charge toutes les
 * opérations sur les albums telles que sauvegarder un album, supprimer un
 * album, vérifier qu'un album d'un certain nom existe, ...
 * 
 * @author Elodie
 * @author Oriane
 * 
 */
public class FileManager {
	/** Dossier principal de l'application */
	public final static String APP_FOLDER_PATH = "/sdcard/PicsApp/";
	//
	// /**
	// * Dossier contenant les albums créés par l'utilisateur mais qui n'ont pas
	// * encore été envoyés.
	// */
	// public final static String SAVED_FOLDER_PATH = APP_FOLDER_PATH +
	// "saved/";

	/** Dossier contenant les albums envoyés par l'utilisateur */
	public final static String SENT_FOLDER_PATH = APP_FOLDER_PATH + "sent/";

	/** Dossier contenant les albums reçus par l'utilisateur */
	public final static String RECEIVED_FOLDER_PATH = APP_FOLDER_PATH
			+ "received/";

	/** The printer writing to the files */
	private PrintWriter mPrinter;

	/**
	 * Constructeur créant le dossier principal de l'application s'il n'existe
	 * pas encore.
	 */
	public FileManager() {
		// create a File object for the parent directory
		File picsAppDirectory = new File(APP_FOLDER_PATH);
		boolean created = picsAppDirectory.mkdirs();

		if (!created) {
			// String[] children = picsAppDirectory.list();
		}
	}

	/**
	 * * Enregistre une image dans le dossier passé en paramètre.
	 * 
	 * @param folderName
	 *            Le dossier dans lequel enregistrer l'image
	 * @param user
	 *            L'utilisateur a qui on envoie l'image
	 * @param path
	 *            Le chemin d'accès de l'image
	 */
	public void savePicture(String folderName, String user, String path) {
		// Vérifie si le chemin correspond à l'un de nos dossiers.
		verifyPath(folderName);

		// Le dossier où enregistrer le fichierx.
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

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(newFile);
		} catch (FileNotFoundException e1) {
			Log.d("FileManager",
					"IO Exeption while creating fileOutPutStream (save new image)");
			e1.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				Log.d("FileManager",
						"IO Exeption while closing file (save new album)");
				e.printStackTrace();
			}
		}

		try {
			// mPrinter = new PrintWriter(new FileWriter(newFile, true));
			// mPrinter.append(path);
			// mPrinter.flush();
			// mPrinter.close();

			// Writer output = new BufferedWriter(new OutputStreamWriter(
			// new FileOutputStream(newFile, true), "UTF-8"));
			//
			// output.append(path);
			// output.close();

			BufferedWriter writer = new BufferedWriter(new FileWriter(
					newFile.getPath(), true));
			writer.write(path);
			writer.close();

		} catch (IOException e) {
			Log.d("FileManager", "Io Exception while writing");
			e.printStackTrace();
		}
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
		}
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
	 * @param albumName
	 *            Le nom de l'album à récupérer.
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

				// Here BufferedInputStream is added for fast reading.
				bis = new BufferedInputStream(fis);
				dis = new DataInputStream(bis);

				// dis.available() returns 0 if the file does not have more
				// lines.
				while (dis.available() != 0) {

					// this statement reads the line from the file and print it
					// to the console.
					String picturePath = dis.readLine();
					pictures.add(picturePath);
				}

				// dispose all the resources after using them.
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
	 * @param name
	 *            Le nom de l'album pour lequel on veut vérifier l'existance
	 * @return true si un album du même nom de trouve dans le dossier saved,
	 *         false sinon.
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
	 * @param folderName
	 *            Le chemin à vérifier.
	 */
	public void verifyPath(String folderName) {
		if (!(/*
			 * folderName.equals(SAVED_FOLDER_PATH) ||
			 */folderName.equals(SENT_FOLDER_PATH) || folderName
				.equals(RECEIVED_FOLDER_PATH))) {
			throw new IllegalArgumentException();
		}
	}
}

package org.android.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

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

	/**
	 * Dossier contenant les albums créés par l'utilisateur mais qui n'ont pas
	 * encore été envoyés.
	 */
	public final static String SAVED_FOLDER_PATH = APP_FOLDER_PATH + "saved/";

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

//	/**
//	 * Enregistre un album dans le dossier "saved"
//	 * 
//	 * @param album
//	 *            L'album à enregistrer
//	 */
//	public void saveAlbum(Album album, String folderName) {
//		// Vérifie si le chemin correspond à l'un de nos dossiers.
//		verifyPath(folderName);
//
//		// Le dossier où enregistrer l'album.
//		File directory = new File(folderName);
//		directory.mkdirs();
//
//		// L'album à sauver.
//		File newAlbum = new File(directory.getPath(), album.getTitle());
//
//		FileOutputStream fos = null;
//
//		try {
//			fos = new FileOutputStream(newAlbum);
//		} catch (FileNotFoundException e1) {
//			Log.d("FILEMANAGER",
//					"IO Exeption while creating fileOutPutStream (save new album)");
//			e1.printStackTrace();
//		} finally {
//			try {
//				if (fos != null) {
//					fos.close();
//				}
//			} catch (IOException e) {
//				Log.d("FILEMANAGER",
//						"IO Exeption while closing file (save new album)");
//				e.printStackTrace();
//			}
//		}
//
//		try {
//			mPrinter = new PrintWriter(new FileWriter(newAlbum));
//
//			for (Picture p : album.getPictures()) {
//				mPrinter.println(p.getPath() + "::" + p.getDescription());
//				mPrinter.flush();
//			}
//
//		} catch (IOException e) {
//			Log.d("FILEMANAGER", "Io Exception while writing");
//			e.printStackTrace();
//		}
//	}

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
	public ArrayList<String> retrieveAlbumsInFolder(String folderName) {
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

	// /**
	// * Récupère l'album avec le nom spécifié.
	// *
	// * @param albumName
	// * Le nom de l'album à récupérer.
	// * @return L'album.
	// */
	// public Album retrieveAlbum(String albumName, String folderName) {
	// verifyPath(folderName);
	// File directory = new File(folderName);
	// directory.mkdirs();
	//
	// File toRetrieve = new File(directory.getPath(), albumName);
	//
	// Album newAlbum = new Album(albumName, PicsAppActivity.mPhoneId);
	//
	// if (toRetrieve.exists()) {
	// FileInputStream fis = null;
	// BufferedInputStream bis = null;
	// DataInputStream dis = null;
	//
	// try {
	// fis = new FileInputStream(toRetrieve);
	//
	// // Here BufferedInputStream is added for fast reading.
	// bis = new BufferedInputStream(fis);
	// dis = new DataInputStream(bis);
	//
	// // dis.available() returns 0 if the file does not have more
	// // lines.
	// while (dis.available() != 0) {
	//
	// // this statement reads the line from the file and print it
	// // to the console.
	// String line = dis.readLine();
	// String[] picture = line.split("::");
	// try {
	// if (picture.length == 1) {
	// newAlbum.addPicture(new Picture(picture[0]));
	// } else {
	// if (picture[1].equals("null")) {
	// picture[1] = "";
	// }
	// newAlbum.addPicture(new Picture(picture[0],
	// picture[1]));
	// }
	// } catch (ArrayIndexOutOfBoundsException aiobe) {
	// // TODO: album pourri.
	// }
	// }
	//
	// // dispose all the resources after using them.
	// fis.close();
	// bis.close();
	// dis.close();
	//
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// return newAlbum;
	// }

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
		if (!(folderName.equals(SAVED_FOLDER_PATH)
				|| folderName.equals(SENT_FOLDER_PATH) || folderName
					.equals(RECEIVED_FOLDER_PATH))) {
			throw new IllegalArgumentException();
		}
	}
}

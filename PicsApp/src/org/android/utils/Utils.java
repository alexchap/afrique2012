package org.android.utils;

import java.io.IOException;

import org.android.R;
import org.android.communication.PictureReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
/**
 * Cette class rassemble plusieurs informations/méthodes utiles
 * à l'intégralité du projet PicsApp
 * 
 * @author Elodie
 * @author Oriane
 * @author Alex
 *
 */
public class Utils {

	/** URL du serveur à contacter */
	public static final String SERVER_URL = "http://128.178.75.23:8080/PicsAppServer_v1.1/";
	// public static final String SERVER_URL = "http://192.168.1.46:8080/PicsAppServer_v1.1/";
	/** Code pour appeler la galerie */
	public static final int SELECT_PICTURE = 1;

	/** Code pour appeler l'appareil photo */
	public static final int TAKE_PICTURE = 0;

	/** Exif tag pour le commentaire d'une image */
	final static String EXIF_TAG = "UserComment";

	/**
	 *  Récupère l'identificateur du téléphone
	 * @param mContext
	 * @return l'ID du téléphone
	 */
	public static String getPhoneId(Context mContext) {
		TelephonyManager tManager = (TelephonyManager) mContext
		.getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getDeviceId();
	}
	
	/**
	 * Cette méthode écrit un commentaire dans une image en ajoutant un tag
	 * EXIF.
	 * Limitations : image JPEG et sans tag UserComment prédéfini
	 */
	public static boolean setComment(String imagePath, String comment) {

		try {
			ExifInterface exif = new ExifInterface(imagePath);
			exif.setAttribute(EXIF_TAG, comment);
			exif.saveAttributes();
			// FIXME: this only works if the tag is not already defined ! //  bug 2415 and 14772 android
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	/**
	 * Cette méthode lit le tag EXIF représentant le commentaire inclu dans
	 * l'image
	 */
	public static String getComment(String imageName) {
		String comment = null;
		try {
			ExifInterface exif = new ExifInterface(imageName);
			comment = exif.getAttribute(EXIF_TAG);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return comment;
	}

	/**
	 * Crée un dialogue ayant un titre et un bouton pour valider l'input
	 * 
	 * @param dialogTitle  le titre à assigner au dialogue
	 * @param context le contexte (activité)
	 * @return le dialogue créé
	 */
	public static AlertDialog createInputDialog(String dialogTitle, Context context) {

		LayoutInflater li = LayoutInflater.from(context);
		/*
		 * Crée un dialogue avec un EditText à partir du xml
		 */
		View simpleDialogView = li.inflate(R.layout.input_dialog, null);

		AlertDialog.Builder inputDialogBuilder = new AlertDialog.Builder(
				context);
		inputDialogBuilder.setTitle(dialogTitle);
		inputDialogBuilder.setView(simpleDialogView);
		AlertDialog inputDialog = inputDialogBuilder.create();
		return inputDialog;
	}

	/**
	 * Méthode principale pour vérifier si de nouvelles images sont disponibles
	 * Si oui, elle sont téléchargées et une notification est affichée
	 * @param c le contexte
	 * @param mPhoneId l'ID du téléphone
	 */
	public static void checkAndDownloadPicts(final Context c, final String mPhoneId){
		new Thread((new Runnable() {
			public void run() {
				PictureReceiver pictureReceiver = new PictureReceiver(mPhoneId);

				if (pictureReceiver.checkReceivedPictures()) {

					int numberReceived = pictureReceiver.getSize();

					String[] senders = pictureReceiver.getImages();

					for (int i = 0; i < numberReceived; i++) {
						pictureReceiver.createNotification(c, senders[i]);
					}
				}
			}
		})).start();
	}
	
	/**
	 * Transforme l'Uri retournée par les activités en un chemin vers l'image
	 * sur la carte SD
	 * 
	 * @param uri   L'Uri a transformer en chemin.
	 */
	public static String getPathFromUri(Activity ac, Uri uri) {
		String path = "";
		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = ac.managedQuery(uri, projection, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);
		} catch (NullPointerException npe) {
			path = uri.getPath();
		}
		return path;
	}

}


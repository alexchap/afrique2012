package org.android.utils;

import java.io.IOException;

import org.android.R;
import org.android.communication.PictureReceiver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;

public class Utils {

	/** URL du serveur à contacter */
	public static final String SERVER_URL = "http://128.178.75.23:8080/PicsAppServer_v1.1/";

	/** Code pour appeler la galerie */
	public static final int SELECT_PICTURE = 1;

	/** Code pour appeler l'appareil photo */
	public static final int TAKE_PICTURE = 0;

	/** Exif tag pour le commentaire d'une image */
	final static String EXIF_TAG = "UserComment";

	/**
	 *  Récupère l'identificateur du téléphone
	 * @param mContext
	 * @return
	 */
	public static String getPhoneId(Context mContext) {
		TelephonyManager tManager = (TelephonyManager) mContext
		.getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getDeviceId();
	}
	
	/**
	 * Cette méthode écrit un commentaire dans une image en ajoutant un tag
	 * EXIF.
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

	public static void refreshSDcard(Context c){
		c.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

	}

	/**
	 * Crée un dialogue ayant un titre et un bouton pour valider l'input
	 * 
	 * @param dialogTitle
	 *            le titre à assigner au dialogue
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
	 * @param c
	 * @param mPhoneId
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
}


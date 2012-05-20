package org.android.utils;

import java.io.IOException;

import android.content.Context;
import android.media.ExifInterface;
import android.telephony.TelephonyManager;

public class Utils {
	// Récupère l'identificateur du téléphone
	public static String getPhoneId(Context mContext) {
		TelephonyManager tManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getDeviceId();
	}

	// URL du serveur à contacter
	public static final String SERVER_URL = "http://128.178.75.23:8080/PicsAppServer_v1.1/";

	/** Code pour appeler la galerie */
	public static final int SELECT_PICTURE = 1;

	/** Code pour appeler l'appareil photo */
	public static final int TAKE_PICTURE = 0;

	/** Exif tag pour le commentaire d'une image */
	final static String EXIF_TAG = "UserComment";

	/**
	 * Cette méthode écrit un commentaire dans une image en ajoutant un tag
	 * EXIF.
	 */
	public static boolean setComment(String imagePath, String comment) {

		try {
			ExifInterface exif = new ExifInterface(imagePath);
			exif.setAttribute(EXIF_TAG, comment);
			exif.saveAttributes();
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
}

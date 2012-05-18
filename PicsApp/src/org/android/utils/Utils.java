package org.android.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Utils {
	//Récupère l'identificateur du téléphone
	public static String getPhoneId(Context mContext) {
		TelephonyManager tManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getDeviceId();
	}
	
	//URL du serveur à contacter
	public static final String SERVER_URL = "http://128.178.75.23:8080/PicsAppServer_v1.1/";
	
	/** Code pour appeler la galerie */
	public static final int SELECT_PICTURE = 1;

	/** Code pour appeler l'appareil photo */
	public static final int TAKE_PICTURE = 0;

}

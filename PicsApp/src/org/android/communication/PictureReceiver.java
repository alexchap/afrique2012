package org.android.communication;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class PictureReceiver {
	private CommunicationHandler mCommHandler;
	private String mPhoneId;
	private ArrayList<String> mPaths;

	public PictureReceiver(String phoneId) {
		mCommHandler = CommunicationHandler.getInstance();
		mPhoneId = phoneId;
	}

	/**
	 * Contacter le serveur qui va vérifier s'il y a de nouvelles images à
	 * recevoir pour cet utilisateur particulier
	 */

	public boolean checkReceivedPictures() {
		mPaths = mCommHandler.checkReceivedPicture(mPhoneId);
		if (mPaths == null) {
			return false;
		} else {
			return true;
		}
	}

	public Bitmap getImages() {
		int size = mPaths.size();
		for (int i = 0; i < size; i++) {
			mCommHandler.getImage(mPhoneId);
		}
		return null;
	}

	public int getSize() {
		if (mPaths != null) {
			return mPaths.size();
		} else {
			return 0;
		}
	}
}
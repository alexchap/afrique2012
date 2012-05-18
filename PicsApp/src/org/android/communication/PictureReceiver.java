//package org.android.communication;
//
//
//import com.google.gson.Gson;
//
//public class PictureReceiver {
//	private CommunicationHandler mCommHandler;
//	private String mPhoneId;
//
//	public PictureReceiver(String phoneId) {
//		mCommHandler = CommunicationHandler.getInstance();
//		mPhoneId = phoneId;
//	}
//
//	/**
//	 * Contacter le serveur qui va vérifier s'il y a de nouveaux albums à
//	 * recevoir pour cet utilisateur particulier
//	 */
//
//	public boolean checkReceivedPictures() {
//		return mCommHandler.checkReceivedPicture(mPhoneId);
//	}
//
//	public Picture getAlbum() {
//		mCommHandler.getAlbum(mPhoneId);
//	}
//	
//}
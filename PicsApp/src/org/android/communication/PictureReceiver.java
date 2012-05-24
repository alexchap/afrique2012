package org.android.communication;

import java.util.ArrayList;

import org.android.R;
import org.android.activities.ViewFolderContentActivity;
import org.android.activities.ViewFoldersActivity;
import org.android.utils.FileManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
/**
 * Cette classe permet de recevoir de gérer le processus qui reçoit les images
 * depuis le serveur.
 *
 */
public class PictureReceiver {
	private CommunicationHandler mCommHandler;
	private String mPhoneId;
	private ArrayList<String> mPaths;

	/**
	 * Constructeur
	 */
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

	/**
	 * Boucle sur toutes les images reçus et utilise le communicationHandler
	 * pour télécharger chaque image
	 */
	public void getImages() {
		int size = mPaths.size();
		for (int i = 0; i < size; i++) {
			mCommHandler.getImage(mPhoneId);
		}
	}
	/**
	 * Cette méthode retourne le nombre de nouvelles image reçues
	 * @return le nombre d'images
	 */
	public int getSize() {
		if (mPaths != null) {
			return mPaths.size();
		} else {
			return 0;
		}
	}
	/**
	 * Cette méthode affiche une notification à l'utilisateur, pour lui indiquer qu'une nouvelle
	 * image a été reçue !
	 * TODO: dire qui a envoyé une image
	 * @param context le contexte de l'activité pour laquelle il faut afficher une notification
	 */
	public void createNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_app, 
				context.getString(R.string.recu_nouvelle_image_short), System.currentTimeMillis());

		// Cacher la notification lorsque l'utilisateur a cliqué dessus
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent viewFolderIntent = new Intent(context,
				ViewFolderContentActivity.class);
		viewFolderIntent.putExtra(ViewFoldersActivity.TO_DISPLAY_FOLDER_CODE,
				FileManager.RECEIVED_FOLDER_PATH);
		viewFolderIntent.putExtra("CallingActivity", "Notification");

		// on ouvre le dossier des fichiers reçus !
		// TODO: ouvrir le bon dossier !!
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				viewFolderIntent, 0);
		notification.setLatestEventInfo(context,context.getString(R.string.app_name),
				context.getString(R.string.recu_nouvelle_image), pendingIntent);
		notificationManager.notify(0, notification);
	}

}
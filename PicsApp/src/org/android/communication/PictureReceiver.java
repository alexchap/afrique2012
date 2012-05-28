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
import android.net.Uri;
import android.os.SystemClock;
/**
 * Cette classe permet de recevoir de gérer le processus qui reçoit les images
 * depuis le serveur.
 * 
 * @author Elodie
 * @author Oriane
 * @author Alex
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
	 * @return : la liste des utilisateurs qui ont envoyé une image
	 */
	public String[] getImages() {
		int size = mPaths.size();
		String[] senders = new String[size];

		for (int i = 0; i < size; i++) {
			senders[i] = mCommHandler.getImage(mPhoneId);
		}
		return senders;
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
	 * @param context le contexte de l'activité pour laquelle il faut afficher une notification
	 * @param sender l'utlisateur qui a envoyé l'image
	 */
	public void createNotification(Context context, String sender) {

		String newimgtxt = sender + " " + context.getString(R.string.recu_nouvelle_image);

		NotificationManager notificationManager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_app, newimgtxt, System.currentTimeMillis());

		// Cacher la notification lorsque l'utilisateur a cliqué dessus
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent viewFolderIntent = new Intent(context,ViewFolderContentActivity.class);
		viewFolderIntent.putExtra(ViewFoldersActivity.TO_DISPLAY_FOLDER_CODE, FileManager.RECEIVED_FOLDER_PATH);
		viewFolderIntent.putExtra("sender", sender);
		// la ligne suivante est indispensable pour avoir des intents différents à chaque fois...
		// utilité : pour envoyer le nom du sender -> aller directement au bon endroit !
		viewFolderIntent.setData((Uri.parse("foobar://"+SystemClock.elapsedRealtime()))); 


		// on ouvre le dossier des fichiers reçus !
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,viewFolderIntent, 0);
		notification.setLatestEventInfo(context,context.getString(R.string.app_name),newimgtxt, pendingIntent);
		notificationManager.notify(0, notification);
	}

}
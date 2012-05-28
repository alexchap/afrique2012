package org.android.activities;

import org.android.R;
import org.android.utils.FileManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Activité qui affiche les albums reçus et envoyés par un utilisateur.
 * 
 * @author Elodie
 * @author Oriane
 * @author Alex
 * 
 */
public class ViewFoldersActivity extends Activity {

	/** Pour l'extra */
	public static String TO_DISPLAY_FOLDER_CODE = "DISPLAY_FOLDER";
	public static String SENDER_CODE = "NEW_IMAGE_FROM";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_folders_activity);
	}

	/**
	 * Appelée lorsque l'utilisateur clique sur le dossier des albums envoyés
	 */
	public void handleSentClick(View v) {
		Log.d("VIEWFOLDERS", "Clicked on Sent folder");
		retrieveAndDisplay(FileManager.SENT_FOLDER_PATH,null);
	}

	/**
	 * Appelée lorsque l'utilisateur clique sur le dossier des albums reçus
	 */
	public void handleReceivedClick(View v) {
		Log.d("VIEWFOLDERS", "Clicked on Received folder");
		retrieveAndDisplay(FileManager.RECEIVED_FOLDER_PATH,null);
	}

	/**
	 * Cette méthode appelle l'activité qui va montrer la liste des albums pour
	 * un dossier (reçu / envoyé)
	 * @param folderName Le dossier à afficher.
	 */
	private void retrieveAndDisplay(String folderName, String sender) {
		Intent viewFolderIntent = new Intent(ViewFoldersActivity.this,
				ViewFolderContentActivity.class);
		viewFolderIntent.putExtra(TO_DISPLAY_FOLDER_CODE, folderName);
		startActivity(viewFolderIntent);
	}
}
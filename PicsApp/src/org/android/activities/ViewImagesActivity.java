package org.android.activities;

import java.io.File;
import java.util.ArrayList;

import org.android.R;
import org.android.utils.FileManager;
import org.android.utils.ImageAdapter;
import org.android.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.TextView;

/**
 * 
 * Classe qui s'occupe de l'affichage d'un album
 * 
 * @author Elodie
 * @author Oriane
 * @author Alex
 * 
 */
public class ViewImagesActivity extends Activity {
	/** Le champs de texte pour la description de l'image */
	private TextView mText;
	/** Le texte centré pour afficher une information */
	private TextView mCenteredText;
	/** Galerie d'images */
	private Gallery mGallery;
	/** Gestionnaire de fichiers servant à enregistrer un nouvel album */
	private FileManager mFileManager;
	/** Dossier courant */
	private String mCurrentFolder = "";
	/** Utilisateur dont les images sont affichées */
	private String mUser = "";
	/** L'album que représente la galerie */
	private ArrayList<String> mPictures;
	/** Valeur booléenne si nouvelle image reçue **/
	private boolean newImage = false;

	/** Appelée à la création de l'activité */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_images_activity);
		initialize();
	}

	/** Initialise la vue */
	private void initialize() {
		handleExtras();

		mText = (TextView) findViewById(R.id.view_images_activity_description);
		mCenteredText = (TextView) findViewById(R.id.view_images_activity_empty);

		mFileManager = new FileManager();
		mPictures = mFileManager.retrievePictures(mUser, mCurrentFolder);
		
		if(newImage){
			// on va à la fin de la liste
			refreshView(-1);
		} else{
			// on va au début de la liste
			refreshView(0);
		}
		
	}

	/** Rafraîchit la vue */
	public void refreshView(int positionToDisplay) {
		if (mPictures != null && !mPictures.isEmpty()) {

			mGallery = (Gallery) findViewById(R.id.view_images_activity_gallery);
			ImageAdapter imageAdapter = new ImageAdapter(this, mPictures);
			mGallery.setAdapter(imageAdapter);

			mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					mText.setText(Utils.getComment(mPictures.get(position)));
					mText.invalidate();
				}

				public void onNothingSelected(AdapterView<?> arg0) {

				}

			});
			
			if(positionToDisplay < 0){
				// on va directement à la fin de l'album
				mGallery.setSelection(mGallery.getCount() - 1);
			} else {
				// on affiche le début de l'album
				mGallery.setSelection(positionToDisplay);
			}
			mText.setVisibility(View.VISIBLE);
			mCenteredText.setVisibility(View.GONE);
		} else {
			mText.setVisibility(View.GONE);
			mCenteredText.setVisibility(View.VISIBLE);
		}
}

	/**
	 * Prend en charge les extras envoyés par l'activité appelante, comme le
	 * dossier courant (envoyées ou reçues) et l'utilisateur dont les images
	 * sont affichées.
	 */
	public void handleExtras() {
		Bundle extras = getIntent().getExtras();
		mUser = (String) extras.get("USER_NAME");
		newImage = (boolean) extras.getBoolean("newImage");
		Log.d("ViewImages", "User : " + mUser);
		mCurrentFolder = (String) extras.get("CURRENT_FOLDER");
		Log.d("ViewImages", "Current folder : " + mCurrentFolder);
	}

	
	/** 
	 * Menus pour rafraîchir les albums et télécharger les éventuelles
	 * nouvelles photos
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.viewimagesmenu , menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(mGallery == null){
			return false;
		}
		// Récupération du chemin de l'image sélectionnée actuellement
		int selectedItemPos = mGallery.getSelectedItemPosition();
		String selectedImg = mPictures.get(selectedItemPos);
		Uri selectedImgUri = Uri.fromFile(new File(selectedImg));
		
		switch (item.getItemId()) {
		case R.id.menu_partager:
			// on partage l'image courante avec une autre application
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/jpeg");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, selectedImgUri);
            startActivity(Intent.createChooser(sharingIntent, "Partager l'image avec ")); 
			return true;
		case R.id.menu_envoyer:
			// on envoi l'image à un autre utilisateur
			Intent sendPictureIntent = new Intent(this,
					SendPictureActivity.class);

			sendPictureIntent.putExtra("SelectedImage", selectedImg);
			startActivity(sendPictureIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
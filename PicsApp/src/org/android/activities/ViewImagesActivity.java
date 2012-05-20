package org.android.activities;

import java.util.ArrayList;

import org.android.R;
import org.android.utils.FileManager;
import org.android.utils.ImageAdapter;
import org.android.utils.Utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.TextView;

/**
 * 
 * Classe qui s'occupe de la création d'un nouvel album.
 * 
 * @author Elodie
 * @author Oriane
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

		refreshView(0);
	}

	/** Rafraîchit la view */
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

			mGallery.setSelection(positionToDisplay);
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
		Log.d("ViewImages", "User : " + mUser);
		mCurrentFolder = (String) extras.get("CURRENT_FOLDER");
		Log.d("ViewImages", "Current folder : " + mCurrentFolder);
	}

	/**
	 * Transforme l'Uri retournée par les activités en un chemin vers l'image
	 * sur la carte SD
	 * 
	 * @param uri
	 *            L'Uri a transformer en chemin.
	 */
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

}
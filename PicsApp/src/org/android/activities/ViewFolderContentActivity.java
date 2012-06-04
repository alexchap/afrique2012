package org.android.activities;

import java.util.ArrayList;

import org.android.R;
import org.android.utils.FileManager;
import org.android.utils.Utils;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activité qui affiche les albums contenus dans un dossier spécifique.
 * Il y a précisément un album par nom d'utilisateur = un fichier texte
 * qui stocke les chemins des images qui forment l'album.
 * 
 * @author Elodie
 * @author Oriane
 * @author Alex
 * 
 */
public class ViewFolderContentActivity extends ListActivity {
	/** Utilisateurs dans ce dossier */
	private ArrayList<String> mUsers;

	/** La liste des albums à afficher */
	private ListView mListView;

	/** Le gestionnaire de fichiers */
	private FileManager mFileManager;

	/** Le dossier courant */
	private String mCurrentFolder;

	/** l'utilisateur qui a envoyé une nouvelle image */
	private String mSender;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	/**
	 * Initialise les dossiers et les actions lorsque l'utilisateur clique sur
	 * l'un d'entre eux.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		mFileManager = new FileManager();
		mUsers = new ArrayList<String>();

		// on récupère les données envoyée depuis la dernière activité
		handleExtras();
		mUsers = mFileManager.retrievePicturesInFolder(mCurrentFolder);

		if (!mUsers.isEmpty()) {
			// Si nouvelle photo -> on va directement dans la galerie
			if(mSender != null){
				viewImageForUser(mSender);

			} else { // sinon on affiche la liste des utilisateurs
				ArrayAdapter arrayAdapter = new ArrayAdapter(this,
						R.layout.view_folder_activity_list_item, mUsers);

				setListAdapter(arrayAdapter);

				mListView = getListView();
				mListView.setTextFilterEnabled(true);
				mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				mListView.setOnItemClickListener(onItemClickListener);
				mListView.setOnItemLongClickListener(onItemLongClickListener);
			}


		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.aucune_photo),
					Toast.LENGTH_SHORT).show();
		}

	}
	
	/**
	 * Récupère les données reçues avec l'Intent :
	 * - Le dossier à afficher (envoyés vs. reçus)
	 * - La personne qui a envoyé une nouvelle photo (= null si
	 * ce n'est pas le cas)
	 */
	private void handleExtras() {

		Bundle extras = getIntent().getExtras();
		mCurrentFolder = (String) extras
		.get(ViewFoldersActivity.TO_DISPLAY_FOLDER_CODE);
		mSender = extras.getString("sender"); 

	}

	/**
	 * Listener sur chaque ligne de la liste d'albums. Lors d'un clic simple,
	 * on passe à un nouvelle activité qui montre le contenu de l'album pour 
	 * l'utilisateur
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		/**
		 * Ouvre la galerie de l'album sur lequel l'utilisateur a cliqué
		 */
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long arg3) {

			String userName = (String) adapter.getItemAtPosition(position);
			viewImageForUser(userName);

		}
	};

	/**
	 * Listener sur chaque ligne de la liste d'albums. Lors que l'utilisateur
	 * clique longtemps sur un élément, on lui propose de supprimer l'album
	 */
	OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> adapter, View view,
				int position, long arg3) {
			String userToDelete = (String) adapter.getItemAtPosition(position);
			deleteUserImages(userToDelete);
			return false;
		}
	};

	/**
	 * Cette méthode instancie l'activité qui va montrer les images de l'album
	 * correspondant à l'utilisateur userName pour le dossier courant (reçus vs. envoyé)
	 * @param userName
	 */
	private void viewImageForUser(String userName){
		Intent viewImagesIntent;

		viewImagesIntent = new Intent(ViewFolderContentActivity.this,
				ViewImagesActivity.class);
		viewImagesIntent.putExtra("CURRENT_FOLDER", mCurrentFolder);
		viewImagesIntent.putExtra("USER_NAME", userName);
		viewImagesIntent.putExtra("newImage", true);
		startActivity(viewImagesIntent);
	}

	

	/**
	 * Cette méthode montre un dialogue à l'utilisateur, qui va lui demander
	 * s'il est sûr de vraiment supprimer l'album sélectionné.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteUserImages(String name) {
		final String userName = name;
		final Context mContext = ViewFolderContentActivity.this;

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(
		getResources().getString(R.string.supprimer_images_confirmation_début) + " "
		+ (mCurrentFolder.equals(FileManager.SENT_FOLDER_PATH) ? "envoyées à": "reçues par")+ " " +
		 getResources().getString( R.string.supprimer_images_confirmation_fin))
				// Bouton pour supprimer les images
				.setPositiveButton(
						mContext.getResources().getString(
								R.string.supprimer_images_confirmer),
								new DialogInterface.OnClickListener() {

							/** Supprime l'album */
							public void onClick(DialogInterface dialog, int id) {
								if (mFileManager.deleteAlbum(userName,
										mCurrentFolder)) {
									mUsers.remove(userName);
									setListAdapter(new ArrayAdapter(
											ViewFolderContentActivity.this,
											R.layout.view_folder_activity_list_item,
											mUsers));

									mListView.invalidate();
									Toast.makeText(
											getApplicationContext(),
											getResources()
											.getString(
													R.string.supprimer_images_succes),
													Toast.LENGTH_SHORT).show();
								}
							}
						})
						// Bouton pour annuler la suppression de l'album
						.setNegativeButton(
								mContext.getResources().getString(R.string.annuler),
								new DialogInterface.OnClickListener() {

									/** Annule la suppression */
									public void onClick(DialogInterface dialog, int id) {
										dialog.dismiss();
									}
								});
		// Crée le dialogue
		AlertDialog deleteAlbumDialog = builder.create();
		// Affiche le dialogue à l'écran
		deleteAlbumDialog.show();
	}

	/** 
	 * Menus pour rafraîchir les albums et télécharger les éventuelles
	 * nouvelles photos
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.viewfoldercontentmenu , menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			// On vérifie si des nouvelles photos sont disponibles
			Utils.checkAndDownloadPicts(ViewFolderContentActivity.this, Utils.getPhoneId(ViewFolderContentActivity.this));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}



}
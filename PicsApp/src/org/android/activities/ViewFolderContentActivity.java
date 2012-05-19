package org.android.activities;

import java.util.ArrayList;

import org.android.R;
import org.android.utils.FileManager;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activité qui affiche les albums contenus dans un dossier spécifique.
 * 
 * @author Elodie
 * @author Oriane
 * 
 */
public class ViewFolderContentActivity extends ListActivity {
	/** Utilisateurs dans ce dossier */
	private ArrayList<String> mUsers;

	/** La liste des albums à afficher */
	private ListView mListView;

	/** File Manager */
	private FileManager mFileManager;

	/** Representing the current folder */
	private String mCurrentFolder;

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

		handleExtras();

		if (!mUsers.isEmpty()) {
			ArrayAdapter arrayAdapter = new ArrayAdapter(this,
					R.layout.view_folder_activity_list_item, mUsers);
			setListAdapter(arrayAdapter);

			mListView = getListView();
			mListView.setOnItemClickListener(onItemClickListener);
			mListView.setOnItemLongClickListener(onItemLongClickListener);
		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.aucune_photo),
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Listener sur chaque ligne de la liste d'albums. Pour l'instant permet
	 * d'éditer l'album comme s'il était nouveau, quel que soit le dossier dans
	 * lequel on se trouve.
	 * 
	 * TODO: seulement éditer si on était dans drafts, sinon afficher l'album
	 * normalement.
	 * 
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		/**
		 * Ouvre la galerie de l'album sur lequel l'utilisateur a cliqué
		 */
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long arg3) {
			Intent viewImagesIntent;

			viewImagesIntent = new Intent(ViewFolderContentActivity.this,
					ViewImagesActivity.class);
			viewImagesIntent.putExtra("CURRENT_FOLDER", mCurrentFolder);

			String userName = (String) adapter.getItemAtPosition(position);
			viewImagesIntent.putExtra("USER_NAME", userName);
			startActivity(viewImagesIntent);

		}
	};

	/**
	 * 
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
	 * Récupère les données reçues avec l'<code>Intent</code>
	 * 
	 * @return Les albums reçus
	 */
	private void handleExtras() {
		Bundle extras = getIntent().getExtras();
		mCurrentFolder = (String) extras
				.get(ViewFoldersActivity.TO_DISPLAY_FOLDER_CODE);

		mUsers = mFileManager.retrieveAlbumsInFolder(mCurrentFolder);
	}

	/**
	 * Dialog
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteUserImages(String name) {
		final String userName = name;
		final Context mContext = ViewFolderContentActivity.this;

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(
				getResources().getString(
						R.string.supprimer_images_confirmation_début)
						+ " "
						+ (mCurrentFolder.equals(FileManager.SENT_FOLDER_PATH) ? "envoyées à"
								: "reçues par")
						+ " "
						+ getResources().getString(
								R.string.supprimer_images_confirmation_fin))
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

}
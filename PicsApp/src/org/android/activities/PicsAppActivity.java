package org.android.activities;

import java.io.File;
import java.util.Calendar;

import org.android.R;
import org.android.communication.CommunicationHandler;
import org.android.utils.FileManager;
import org.android.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activité principale de l'application, qui propose à l'utilisateur d'envoyer
 * une photo
 * 
 * @author Elodie
 * @author Oriane
 * 
 */
public class PicsAppActivity extends Activity {
	/** File manager */
	private FileManager mFileManager;

	/** Communication Handler */
	private CommunicationHandler mCommHandler;

	/** Tag pour l'album */
	public static String ALBUM_EXTRA = "ALBUM_EXTRA";

	/** id du téléphone */
	public static String mPhoneId;

	/** Appelée à la création de l'activité. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mPhoneId = Utils.getPhoneId(this);
		initialize();
	}

	/** Initialise la vue */
	private void initialize() {
		// File Manager
		mFileManager = new FileManager();
		mCommHandler = CommunicationHandler.getInstance();

		// Vérifie que l'utilisateur est enregistré
		if (!mCommHandler.isUser(mPhoneId)) {
			showRegistrationDialog();

			Toast.makeText(getApplicationContext(), "Is Not User",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Is User",
					Toast.LENGTH_SHORT).show();
			// PictureReceiver albumReceiver = new AlbumReceiver(mPhoneId);

			// if (albumReceiver.checkReceivedAlbums()) {
			// Toast.makeText(this, albumReceiver.getAlbum().toString(),
			// Toast.LENGTH_SHORT).show();
			//
			// createNotification(this, "plop", albumReceiver.getAlbum());
			// }
		}
	}

	/** Le chemin d'accès de l'image sélectionnée */
	private String mSelectedImagePath;

	/**
	 * Méthode appelée lors d'un clic sur le bouton pour envoyer une nouvelle
	 * image
	 * 
	 * @param v
	 */
	public void addPicture() {
		final Context mContext = PicsAppActivity.this;

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(getResources().getString(R.string.ajouter_photo))
				// Bouton pour prendre une nouvelle photo
				.setPositiveButton(
						mContext.getResources().getString(
								R.string.prendre_photo),
						new DialogInterface.OnClickListener() {

							/** Lance l'appareil photo Android */
							public void onClick(DialogInterface dialog, int id) {
								// Intent ouvrant l'appareil photo

								Intent imageCaptureIntent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								mSelectedImagePath = "/sdcard/PicsApp/IMG"
										+ Calendar.getInstance()
												.getTimeInMillis() + ".jpg";
								imageCaptureIntent.putExtra(
										MediaStore.EXTRA_OUTPUT,

										Uri.fromFile(new File(
												mSelectedImagePath)));
								startActivityForResult(imageCaptureIntent,
										Utils.TAKE_PICTURE);

							}
						})
				// Bouton pour choisir une photo dans la galerie
				.setNegativeButton(
						mContext.getResources().getString(R.string.galerie),
						new DialogInterface.OnClickListener() {
							/** Ouvre la galerie Android */
							public void onClick(DialogInterface dialog, int id) {
								// Intent ouvrant la galerie
								Intent intent = new Intent();
								intent.setType("image/*");
								intent.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(Intent.createChooser(
										intent,
										getResources().getString(
												R.string.ajouter_photo)),
										Utils.SELECT_PICTURE);
							}
						});
		// Crée le dialogue
		AlertDialog newAlbumDialog = builder.create();
		// Affiche le dialogue à l'écran
		newAlbumDialog.show();
	}

	/**
	 * Traite les résultats retournés par les activités externes appelées,
	 * notamment l'appareil photo et la galerie du téléphone.
	 * 
	 * @param requestCode
	 *            Le code avec lequel l'activité a été appelée.
	 * @param resultCode
	 *            Le code retourné par l'activité appelée.
	 * @param data
	 *            Les données resultant de l'appel à l'activité.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// Résultat de la part de l'appareil photo ou de la galerie
			if (requestCode == Utils.TAKE_PICTURE
					|| requestCode == Utils.SELECT_PICTURE) {
				mSelectedImagePath = getPathFromIntent(data);
			}

			// if (mAlbum.containsPicture(new Picture(mSelectedImagePath))) {
			// Toast.makeText(getApplicationContext(), "Deja dedans",
			// Toast.LENGTH_SHORT).show();
			// } else {
			// Ajoute la nouvelle image à l'album
			if (mSelectedImagePath != null) {
				Toast.makeText(getApplicationContext(), mSelectedImagePath,
						Toast.LENGTH_SHORT).show();
				// addPicture(mSelectedImagePath);
				// mSelectedImagePath = null;
			}
			// }
		}
	}

	/**
	 * Récupère le chemin vers la photo depuis l'intent
	 * 
	 * @param data
	 *            l'intent retourné par la galerie ou l'appareil photo
	 * @return le chemin vers la photo
	 */
	private String getPathFromIntent(Intent data) {
		String selectedImagePath = "";

		try {
			Uri selectedImageUri = data.getData();
			selectedImagePath = getPathFromUri(selectedImageUri);
		} catch (NullPointerException npe) {
			if (data == null) {
				return mSelectedImagePath;
			}
		}
		return selectedImagePath;
	}

	/**
	 * Transforme l'Uri retournée par les activités en un chemin vers l'image
	 * sur la carte SD
	 * 
	 * @param uri
	 *            L'Uri a transformer en chemin.
	 */
	public String getPathFromUri(Uri uri) {
		String path = "";
		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, projection, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);
		} catch (NullPointerException npe) {
			path = uri.getPath();
		}
		return path;
	}

	private AlertDialog createInputDialog(String dialogTitle) {
		Context context = PicsAppActivity.this;
		LayoutInflater li = LayoutInflater.from(context);
		/*
		 * Crée un dialogue avec un EditText à partir du xml
		 */
		View simpleDialogView = li.inflate(R.layout.input_dialog, null);

		AlertDialog.Builder inputDialogBuilder = new AlertDialog.Builder(
				context);
		inputDialogBuilder.setTitle(dialogTitle);
		inputDialogBuilder.setView(simpleDialogView);
		AlertDialog inputDialog = inputDialogBuilder.create();
		return inputDialog;
	}

	/**
	 * Crée et affiche le dialogue d'enregistrement
	 */
	private void showRegistrationDialog() {
		final Context mContext = PicsAppActivity.this;
		AlertDialog registrationDialog = createInputDialog(getResources()
				.getString(R.string.registration_dialog_title));

		registrationDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						// Do nothing
					}
				});

		// Crée le premier bouton, pour valider le nom d'utilisateur
		registrationDialog.setButton(
				mContext.getResources().getString(R.string.confirmer),
				new DialogInterface.OnClickListener() {

					// S'enregistrer
					public void onClick(DialogInterface dialog, int which) {
						CommunicationHandler communicationHandler = CommunicationHandler
								.getInstance();

						AlertDialog newAlbumDialog = (AlertDialog) dialog;
						EditText inputDialogEditText = (EditText) newAlbumDialog
								.findViewById(R.id.input_dialog_edittext);

						if (inputDialogEditText == null
								|| inputDialogEditText.equals("")) {
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.accountcreate_invalidusername),
									Toast.LENGTH_LONG).show();
						} else {
							String userName = inputDialogEditText.getText()
									.toString();

							if (communicationHandler.registerUser(userName,
									mPhoneId)) {
								Toast.makeText(getApplicationContext(),
										"Enregistrement réussi!",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(
										getApplicationContext(),
										getString(R.string.accountcreate_username_taken),
										Toast.LENGTH_LONG).show();
								showRegistrationDialog();
							}
						}
					}
				});

		// Affiche le dialogue
		registrationDialog.show();
	}
}
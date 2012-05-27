package org.android.activities;

import java.io.File;
import java.util.Calendar;

import org.android.R;
import org.android.communication.CommunicationHandler;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activité principale de l'application, qui propose à l'utilisateur d'envoyer
 * une photo ou d'afficher les photos envoyées/reçues
 * 
 * @author Elodie
 * @author Oriane
 * 
 */
public class PicsAppActivity extends Activity {
	/** Communication Handler */
	private CommunicationHandler mCommHandler;

	/** id du téléphone */
	public static String mPhoneId;

	/** Le chemin d'accès de l'image sélectionnée */
	private String mSelectedImagePath;

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
		mCommHandler = CommunicationHandler.getInstance();

		// Vérifie que l'utilisateur est enregistré
		if (!mCommHandler.isUser(mPhoneId)) {
			showRegistrationDialog();
		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.login_success),
					Toast.LENGTH_SHORT).show();
			Utils.checkAndDownloadPicts(PicsAppActivity.this, mPhoneId);
			
		}
	}


	/**
	 * Méthode appelée lors d'un clic sur le bouton pour envoyer une nouvelle
	 * image
	 * 
	 * @param v
	 */
	public void addPicture(View v) {
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
		AlertDialog newPictureDialog = builder.create();
		// Affiche le dialogue à l'écran
		newPictureDialog.show();
	}

	/**
	 * Méthode appelée lors d'un clic sur le bouton pour voir les images
	 * 
	 * @param v
	 */
	public void seePictures(View v) {
		Intent i = new Intent(getApplicationContext(),
				ViewFoldersActivity.class);
		startActivity(i);
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
			/* Résultat de la part de la galerie (si prise par l'appareil photo, 
			 * alors le chemin vers l'image est déjà défini !)
			 */
			if (requestCode == Utils.SELECT_PICTURE) {
				mSelectedImagePath = getPathFromIntent(data);
			}

			// Lance une intent qui va afficher l'image et permettre à
			// l'utilisateur de rajouter un commentaire
			if (mSelectedImagePath != null) {
				Toast.makeText(getApplicationContext(), mSelectedImagePath,
						Toast.LENGTH_SHORT).show();
				Intent sendPictureIntent = new Intent(this,
						SendPictureActivity.class);

				sendPictureIntent.putExtra("SelectedImage", mSelectedImagePath);
				startActivity(sendPictureIntent);
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.image_introuvable),
						Toast.LENGTH_SHORT).show();
			}
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

	

	/**
	 * Crée et affiche le dialogue d'enregistrement
	 */
	private void showRegistrationDialog() {
		final Context mContext = PicsAppActivity.this;
		AlertDialog registrationDialog = Utils.createInputDialog(getResources()
				.getString(R.string.registration_dialog_title),PicsAppActivity.this);

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

						AlertDialog newPictureDialog = (AlertDialog) dialog;
						EditText inputDialogEditText = (EditText) newPictureDialog
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
										getString(R.string.login_success),
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
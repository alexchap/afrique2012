package org.android.activities;

import java.util.ArrayList;

import org.android.R;
import org.android.communication.CommunicationHandler;
import org.android.utils.FileManager;
import org.android.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Activité qui permet d'éditer les informations d'une image avant de
 * pouvoir l'envoyer à un utilisateur
 * 
 * @author Elodie
 * @author Oriane
 * 
 */
public class SendPictureActivity extends Activity {
	/** File manager */
	private FileManager mFileManager;

	/** Communication Handler */
	private CommunicationHandler mCommHandler;

	/** id du téléphone */
	public static String mPhoneId;

	/** Le chemin d'accès de l'image sélectionnée */
	private String mSelectedImagePath;
	
	/** La liste déroulante pour afficher sélectionner un utilisateur */
	private Spinner spinner;
	
	/** Champ de texte éditable pour le commentaire */
	private EditText mEditComment;

	protected ProgressDialog mProgressDialog;
	public static final int MSG_ERR = 0;
	public static final int MSG_SUC = 1;

	/** Appelée à la création de l'activité. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFileManager = new FileManager();

		mPhoneId = Utils.getPhoneId(this);

		mCommHandler = CommunicationHandler.getInstance();
		mSelectedImagePath = (String) this.getIntent().getExtras()
				.get("SelectedImage");

		if (mSelectedImagePath != null) {
			editPictureBeforeSending();
		}
	}

	/**
	 * Montre l'image avec son commentaire, édite le commentaire et liste
	 * déroulante pour choisir l'utilisateur à qui envoyer l'image
	 */
	public void editPictureBeforeSending() {
		setContentView(R.layout.edit_before_sending);
		mEditComment = (EditText) findViewById(R.id.edit_comment);
		ImageView editImage = (ImageView) findViewById(R.id.edit_imageview);
		Bitmap bmImg = BitmapFactory.decodeFile(mSelectedImagePath);

		editImage.setImageBitmap(bmImg);
		String comment = Utils.getComment(mSelectedImagePath);
		if (comment != null) {
			mEditComment.setText(comment);
		}

		final CommunicationHandler communicationHandler = CommunicationHandler
				.getInstance();

		ArrayList<String> users = communicationHandler.getUsers(Utils
				.getPhoneId(this));

		spinner = (Spinner) findViewById(R.id.edit_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, users);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

	}

	/**
	 * Envoie l'image au serveur
	 */
	public void sendPicture(View v) {
		mProgressDialog = ProgressDialog.show(this,
				getResources().getString(R.string.patienter), getResources()
						.getString(R.string.envoi_en_cours), true);

		Utils.setComment(mSelectedImagePath, mEditComment.getText().toString());

		// Récupère le destinataire choisit dans le Spinner
		final String dest = (String) spinner.getSelectedItem();

		// 1. send to the server
		new Thread((new Runnable() {
			public void run() {
				Message msg = null;

				if (mCommHandler.send(mPhoneId, dest, mSelectedImagePath)) {
					// Sauvegarde l'image sur le téléphone
					mFileManager.savePicture(FileManager.SENT_FOLDER_PATH,
							dest, mSelectedImagePath);
					msg = mHandler.obtainMessage(MSG_SUC);

				} else {
					msg = mHandler.obtainMessage(MSG_ERR);

				}
				mHandler.sendMessage(msg);

			}
		})).start();

	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUC:
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();

					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.envoi_reussi),
							Toast.LENGTH_SHORT).show();
					// on revient à l'application de base
					Intent sendPictureIntent = new Intent(SendPictureActivity.this,	PicsAppActivity.class);
					startActivity(sendPictureIntent);
				}
				break;
			case MSG_ERR:
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();

					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.envoi_failed),
							Toast.LENGTH_SHORT).show();
				}
			default: // should never happen
				break;
			}
		}
	};

}
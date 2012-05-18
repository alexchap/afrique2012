package org.android.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.android.R;
import org.android.communication.CommunicationHandler;
import org.android.utils.FileManager;
import org.android.utils.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

/**
 * Activité principale de l'application, qui propose à l'utilisateur d'envoyer
 * une photo
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

	/** Exif tag pour le commentaire d'une image */
	final String EXIF_TAG = "UserComment";

	/** Le chemin d'accès de l'image sélectionnée */
	private String mSelectedImagePath;

	private Spinner spinner;

	/** Appelée à la création de l'activité. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPhoneId = Utils.getPhoneId(this);

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
		EditText editComment = (EditText) findViewById(R.id.edit_comment);
		ImageView editImage = (ImageView) findViewById(R.id.edit_imageview);
		Bitmap bmImg = BitmapFactory.decodeFile(mSelectedImagePath);

		editImage.setImageBitmap(bmImg);
		String comment = getComment(mSelectedImagePath);
		if (comment != null) {
			editComment.setText(comment);
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
	 * Envoie l'image au serveur...
	 */
	public void sendPicture(View v) {
		// TODO
		// 1. save last comment entered by the user

		String dest = (String) spinner.getSelectedItem();

	}

	/**
	 * Cette méthode écrit un commentaire dans une image en ajoutant un tag
	 * EXIF.
	 */
	public boolean setComment(String imageName, String comment) {

		try {
			ExifInterface exif = new ExifInterface(imageName);
			exif.setAttribute(EXIF_TAG, comment);
			exif.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	/**
	 * Cette méthode lit le tag EXIF représentant le commentaire inclu dans
	 * l'image
	 */
	public String getComment(String imageName) {
		String comment = null;
		try {
			ExifInterface exif = new ExifInterface(imageName);
			comment = exif.getAttribute(EXIF_TAG);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return comment;
	}
}
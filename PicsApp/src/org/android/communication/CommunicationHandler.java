package org.android.communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.android.utils.FileManager;
import org.android.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Classe qui s'occupe de la communication avec le serveur.
 * 
 * @author Elodie
 * 
 */
public class CommunicationHandler {

	/** Servlet pour envoyer un album */
	public static final String RECEIVE_IMAGE_SERVLET = "ReceiveImage";
	/** Servlet pour récupérer la liste des utilisateurs */
	public static final String USERS_SERVLET = "GetUsers";
	/** Servlet pour vérifier l'existance d'un utilisateur */
	public static final String IS_USER_SERVLET = "ValidateUser";
	/** Servlet pour vérifier la réception d'albums */
	public static final String CHECK_NEW_PICTURES_SERVLET = "CheckNewPictures";
	/** Servlet pour récupérer une image */
	public static final String GET_IMAGE_SERVLET = "GetImage";

	/** Servlet pour télécharger un album */
	public static final String SEND_IMAGE = "SendImage";

	/** Servlet pour enregistrer un utilisateur */
	public static final String USER_SERVLET = "RegisterUser";

	/** Tag pour l'album */
	public static final String ALBUM_TAG = "ALBUM";

	/** Tag pour le nom d'utilisateur */
	public static final String USERNAME_TAG = "USERNAME";

	/** Tag pour l'id du téléphone */
	public static final String PHONEID_TAG = "PHONEID";

	/** Tag pour le destinataire */
	public static final String RECEIVER_TAG = "RECEIVER";

	/** Tag pour les photos */
	public static final String PICTURES_TAG = "PICTURES";

	private static final int VALID_OLD_USER = 1010;
	private static final int VALID_NEW_USER = 200;
	// private static final int INVALID_USER = 1030;
	// private static final int NOT_REGISTERED = 1040;

	private FileManager mFileManager;

	/**
	 * Constructeur vide
	 */
	private CommunicationHandler() {
		mFileManager = new FileManager();
	}

	private static CommunicationHandler communicationHandler = new CommunicationHandler();

	public static CommunicationHandler getInstance() {
		return communicationHandler;
	}

	/**
	 * Retourne la liste des noms d'utilisateurs de l'application
	 * 
	 * @param owner
	 *            l'utilisateur courant de l'application
	 * @return la liste des utilisateurs de l'application stockée sur le serveur
	 */
	public ArrayList<String> getUsers(String phoneId) {
		BufferedReader in = null;
		ArrayList<String> users = null;

		try {
			HttpClient client = new DefaultHttpClient();

			String url = Utils.SERVER_URL + USERS_SERVLET;
			if (!url.endsWith("?"))
				url += "?";

			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += paramString;
			HttpGet request = new HttpGet(url);

			// Execute la requête
			HttpResponse response = client.execute(request);
			users = getListFromResponse(response);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return users;
	}

	/**
	 * Envoi d'une photo
	 * 
	 * @param toSend
	 *            la photo à envoyer
	 * @return le status d'envoi de la photo; true si envoyée avec succès, false
	 *         si un problème est survenu
	 */
	public boolean send(String phoneID, String dest, String toSend) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL
				+ RECEIVE_IMAGE_SERVLET);

		MultipartEntity entity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		try {
			// Ajoute l'id de l'expéditeur comme première partie de l'entité
			entity.addPart(PHONEID_TAG, new StringBody(phoneID));
			// Ajoute le destinataire comme deuxième partie de l'entité
			entity.addPart(RECEIVER_TAG, new StringBody(dest));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return false;
		}

		// Ajoute la photo comme partie de l'entité
		File toSendFile = new File(toSend);
		entity.addPart(toSendFile.getName(), new FileBody(toSendFile));

		httpPost.setEntity(entity);
		try {
			httpClient.execute(httpPost, localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Envoie le login vers le serveur.
	 * 
	 * @param userToSend
	 *            L'utilisateur à envoyer vers le serveur.
	 * @return le status de l'envoi vers le serveur
	 */
	public boolean registerUser(String username, String phoneId) {
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL + USER_SERVLET);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// Nom d'utilisateur
		params.add(new BasicNameValuePair(USERNAME_TAG, username));

		// id du téléphone
		params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));

		UrlEncodedFormEntity entity;

		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return false;
		}

		httpPost.setEntity(entity);

		HttpResponse httpResponse = executePost(httpPost);
		int result = httpResponse.getStatusLine().getStatusCode();

		if (result == VALID_NEW_USER) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Envoi le login vers le serveur.
	 * 
	 * @param userToSend
	 *            L'utilisateur à envoyer vers le serveur.
	 * @return le status de l'envoi vers le serveur
	 */
	public boolean isUser(String phoneId) {
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL + IS_USER_SERVLET);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// id du téléphone
		params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));

		int result = executePostWithParams(httpPost, params);

		Log.d("Response", "" + result);
		if (result == VALID_OLD_USER) {
			return true;
		} else {
			return false;
		}
	}

	public int executePostWithParams(HttpPost httpPost,
			List<NameValuePair> params) {
		UrlEncodedFormEntity entity;

		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return -1;
		}

		httpPost.setEntity(entity);

		HttpResponse httpResponse = executePost(httpPost);
		if (httpResponse == null) {
			return -1;
		}
		return httpResponse.getStatusLine().getStatusCode();
	}

	public HttpResponse executePost(HttpPost httpPost) {
		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpResponse response = httpClient.execute(httpPost);

			return response;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Récupère le nom d'utilisateur du serveur
	 * 
	 * @param phoneId
	 * @return
	 */
	public String getUsername(String phoneId) {

		return "";
	}

	public ArrayList<String> checkReceivedPicture(String phoneId) {
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL
				+ CHECK_NEW_PICTURES_SERVLET);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// id du téléphone
		params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));

		UrlEncodedFormEntity entity = null;

		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		httpPost.setEntity(entity);

		HttpResponse response = executePost(httpPost);

		ArrayList<String> paths = getListFromResponse(response);
		int result = response.getStatusLine().getStatusCode();

		if (result > 1000) {
			return paths;
		}

		return null;
	}

	/**
	 * Gets a list from an HttpResponse
	 * 
	 * @param response
	 *            the response to get the list from
	 * @return the list
	 */
	public ArrayList<String> getListFromResponse(HttpResponse response) {
		BufferedReader in = null;
		ArrayList<String> list = null;
		try {
			// Récupère les informations renvoyées par le serveur
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();

			// Désérialise la liste reçue depuis le serveur
			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<String>>() {
			}.getType();
			list = gson.fromJson(page, type);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;

	}

	public String getImage(String phoneId) {
		// BufferedReader in = null;
		String responseString = "";

		String url = Utils.SERVER_URL + SEND_IMAGE;
		if (!url.endsWith("?"))
			url += "?";

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));

		String paramString = URLEncodedUtils.format(params, "utf-8");
		url += paramString;

		try {

			URL urle = new URL(url);
			URLConnection conn = urle.openConnection();

			String contentDisposition = conn
					.getHeaderField("Content-disposition");

			// Récupère l'expéditeur
			String senderSep = "sender=";
			String sender = contentDisposition.substring(contentDisposition
					.indexOf(senderSep) + senderSep.length());
			Log.d("sender", sender);

			String filenameSep = "filename=";
			String end = ", ";
			contentDisposition = contentDisposition.substring(
					contentDisposition.indexOf(filenameSep)
							+ filenameSep.length(),
					contentDisposition.indexOf(end));

			File sdcard = Environment.getExternalStorageDirectory();
			File pictureDir = new File(sdcard, "PicsApp");
			pictureDir.mkdirs();

			InputStream is = conn.getInputStream();

			String picturePath = pictureDir.getAbsolutePath() + "/"
					+ contentDisposition;
			OutputStream os = new FileOutputStream(picturePath);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();

			mFileManager.savePicture(FileManager.RECEIVED_FOLDER_PATH, sender,
					picturePath);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return responseString;
	}
}

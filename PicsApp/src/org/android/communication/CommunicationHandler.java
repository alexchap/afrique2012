package org.android.communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.http.util.EntityUtils;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Classe qui s'occupe de la communication avec le serveur.
 * 
 * @author Elodie
 * @author Oriane
 * @author Alex
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
	
	
	/***********************************
	 *  Méthodes liées aux UTILISATEUR *
	 *  - getUsers()                   *
	 *  - registerUser()               *
	 *  - isUser()                     *
	 ***********************************/
	
	
	/**
	 * Retourne la liste des noms d'utilisateurs de l'application
	 * Méthode utilisée : GET de la classe HttpClient
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

			// 1. On prépare l'URL cible
			String url = Utils.SERVER_URL + USERS_SERVLET;
			if (!url.endsWith("?"))
				url += "?";

			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += paramString;
			HttpGet request = new HttpGet(url);

			// 2. On exécute la requête
			HttpResponse response = client.execute(request);
			
			// 3. On récupère la liste depuis la réponse
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
	 * Envoie le login vers le serveur
	 * Méthode utilisée : POST de la classe HttpClient
	 * 
	 * @param userToSend L'utilisateur à envoyer vers le serveur.
	 * @return le status de l'envoi vers le serveur
	 */
	public boolean registerUser(String username, String phoneId) {
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL + USER_SERVLET);
		
		// 1. On ajoute les paramètres à une list clés-valeur
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// Nom d'utilisateur
		params.add(new BasicNameValuePair(USERNAME_TAG, username));
		// id du téléphone
		params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));

		// 2. On exécute la requête avec ces paramètres
		int result = executePostWithParams(httpPost, params).getStatusLine().getStatusCode();

		if (result == VALID_NEW_USER) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Envoi le login vers le serveur.
	 * Méthode utilisée : POST de la classe HttpClient
	 * 
	 * @param userToSend L'utilisateur à envoyer vers le serveur.
	 * @return le status de l'envoi vers le serveur
	 */
	public boolean isUser(String phoneId) {
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL + IS_USER_SERVLET);
		
		// 1. On définit les paramètres
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// id du téléphone
		params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));

		// 2. On exécute la requête avec ces paramètres
		int result = executePostWithParams(httpPost, params).getStatusLine().getStatusCode();

		Log.d("Response", "" + result);
		if (result == VALID_OLD_USER) {
			return true;
		} else {
			return false;
		}
	}

	/******************************
	 * Méthodes liées aux IMAGES  *
	 * - sendImage()              *
	 * - getImage()               *
	 * - checkReceivedPicture()   *
	 ******************************/
	
	/**
	 * Envoi d'une photo
	 * Méthode utilisée : Multipart Post de la class HttpClient
	 * 
	 * @param toSend la photo à envoyer
	 * @return le statut d'envoi de la photo; true si envoyée avec succès, false
	 *         si un problème est survenu
	 */
	public boolean sendImage(String phoneID, String dest, String toSend) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		// 1. On prépare la requête POST et l'entité multipart
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL + RECEIVE_IMAGE_SERVLET);
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		// 2. On ajoute les champs : id expéditeur, destinataire puis l'image
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

		// 3. On définit l'entité et on exécute le POST
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
	 * Cette méthode vérifie si de nouvelles images doivent être téléchargées
	 * depuis le serveur
	 * Méthode utilisée : POST de la classe HttpClient
	 * @param phoneId
	 * @return
	 */
	public ArrayList<String> checkReceivedPicture(String phoneId) {
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL + CHECK_NEW_PICTURES_SERVLET);
		// 1. On prépare les paramètres
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// id du téléphone
		params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));

		// 2. On exécute la requête
		HttpResponse response = executePostWithParams(httpPost, params);

		// 3. On parse le résultat et on retourne la liste si statut OK
		ArrayList<String> paths = getListFromResponse(response);
		int result = response.getStatusLine().getStatusCode();

		if (result > 1000) { // TODO: constant above ?
			return paths;
		}

		return null;
	}

	
	/**
	 * Cette méhode télécharge une image depuis le serveur
	 * Méthode utilisée : Connexion à une URL et téléchargement direct
	 * @param phoneId
	 * @return
	 */
	// TODO: possible de simplifier cette méthode ?
	public String getImage(String phoneId) {
		
		String sender = null;

		// 1. Préparation de l'URL pour demander l'image "suivante" au serveur
		String url = Utils.SERVER_URL + SEND_IMAGE;
		if (!url.endsWith("?"))
			url += "?";

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(PHONEID_TAG, phoneId));

		String paramString = URLEncodedUtils.format(params, "utf-8");
		url += paramString;

		try {
			
			// 2. On ouvre la connexion
			URL urle = new URL(url);
			URLConnection conn = urle.openConnection();
			
			// 3. On parse le header pour obtenir des informations
			
			String contentDisposition = conn.getHeaderField("Content-disposition");

			// expéditeur et le nom du fichier
			String senderSep = "sender=";
			sender = contentDisposition.substring(contentDisposition.indexOf(senderSep) + senderSep.length());
			Log.d("sender", sender);

			String filenameSep = "filename=";
			String end = ", ";
			contentDisposition = contentDisposition.substring(
					contentDisposition.indexOf(filenameSep) + filenameSep.length(), contentDisposition.indexOf(end));

			// 4. On télécharge le fichier
			File sdcard = Environment.getExternalStorageDirectory();
			File pictureDir = new File(sdcard, "PicsApp");
			pictureDir.mkdirs();

			InputStream is = conn.getInputStream();

			String picturePath = pictureDir.getAbsolutePath() + "/" + contentDisposition;
			OutputStream os = new FileOutputStream(picturePath);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();
			
			// 5. On le met au bon endroit grâce à notre gestionnaire de fichiers
			mFileManager.savePicture(FileManager.RECEIVED_FOLDER_PATH, sender, picturePath);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return sender;
	}

	/*********************************************
	 *  Méthodes pour exécuter des requêtes POST *
	 *********************************************/
	
	/**
	 * Cette méthode importante prépare une requête POST avec des paramètres
	 * données, exécute la requête et retourne le statut de la réponse.
	 * @param httpPost la requête POST
	 * @param params une liste de clés-valeur
	 * @return la réponse
	 */
	public HttpResponse executePostWithParams(HttpPost httpPost, List<NameValuePair> params) {
		UrlEncodedFormEntity entity;

		// 1. On prépare l'entité avec une URL contenant les paramètres
		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
		// 2. On assigne cette entité à la requête
		httpPost.setEntity(entity);

		// 3. On exécute la requête et retourne la réponse
		HttpResponse httpResponse = executePost(httpPost);
		return httpResponse;
	}
	
	/**
	 * Cette méthode exécute la requête POST avec un client
	 * Http par défaut
	 * @param httpPost la requête à exécuter
	 * @return la réponse ou null si échec
	 */
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
	
	/*********************************************
	 *  Méthodes pour lire des réponses Http     *
	 *********************************************/

	/**
	 * Cette méthode retourne une liste de chaines de caractères
	 * depuis une réponse du serveur. Elle parse l'entité de la
	 * réponse HTTP.
	 * 
	 * @param response la réponse HTTP à parser
	 * @return une liste de strings
	 */
	public ArrayList<String> getListFromResponse(HttpResponse response) {
		BufferedReader in = null;
		ArrayList<String> list = null;
		try {

			// 1. Récupère les informations renvoyées par le serveur
			String page = EntityUtils.toString(response.getEntity());

			// 2. Désérialise la liste reçue depuis le serveur
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
	
}

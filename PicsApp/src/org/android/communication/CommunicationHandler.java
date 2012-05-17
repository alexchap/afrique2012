package org.android.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.android.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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
	public static final String ALBUM_SERVLET = "ReceiveAlbum";
	/** Servlet pour récupérer la liste des utilisateurs */
	public static final String USERS_SERVLET = "GetUsers";
	/** Servlet pour vérifier l'existance d'un utilisateur */
	public static final String IS_USER_SERVLET = "ValidateUser";
	/** Servlet pour vérifier la réception d'albums */
	public static final String CHECK_NEW_ALBUMS_SERVLET = "CheckNewAlbums";
	/** Servlet pour récupérer une image */
	public static final String GET_IMAGE_SERVLET = "GetImage";

	/** Servlet pour télécharger un album */
	public static final String SEND_ALBUM = "SendAlbum";

	/** Servlet pour enregistrer un utilisateur */
	public static final String USER_SERVLET = "RegisterUser";

	/** Tag pour l'album */
	public static final String ALBUM_TAG = "ALBUM";

	/** Tag pour le nom d'utilisateur */
	public static final String USERNAME_TAG = "USERNAME";

	/** Tag pour l'id du téléphone */
	public static final String PHONE_ID_TAG = "PHONEID";

	/** Tag pour les photos */
	public static final String PICTURES_TAG = "PICTURES";

	private static final int VALID_OLD_USER = 1010;
	private static final int VALID_NEW_USER = 200;
	private static final int INVALID_USER = 1030;
	private static final int NOT_REGISTERED = 1040;

	/**
	 * Constructeur vide
	 */
	private CommunicationHandler() {
	}

	private static CommunicationHandler communicationHandler = new CommunicationHandler();

	public static CommunicationHandler getInstance() {
		return communicationHandler;
	}

	//
	// /**
	// * Envoi d'un album
	// *
	// * @param toSend
	// * l'album à envoyer
	// * @return le status d'envoi de l'album; true si envoyé avec succès, false
	// * si un problème est survenu
	// */
	// public boolean send(Album toSend) {
	//
	// Gson gson = new Gson();
	// String jsonAlbum = "album";
	// ArrayList<Picture> pictures = toSend.getPictures();
	//
	// String path = "";
	// for (Picture p : pictures) {
	// path = p.getPath();
	// int end = path.lastIndexOf("/");
	// p.setPath(path.substring(end, path.length() - 1));
	// }
	//
	// try {
	// // Passe l'objet en Json
	// jsonAlbum = gson.toJson(toSend);
	// } catch (JsonSyntaxException jse) {
	// jse.printStackTrace();
	// return false;
	// }
	//
	// // Envoie l'album
	// postAlbum(jsonAlbum, toSend.getPictures());
	//
	// return true;
	// }

	/**
	 * Retourne la liste des noms d'utilisateurs de l'application
	 * 
	 * @param owner
	 *            l'utilisateur courant de l'application
	 * @return la liste des utilisateurs de l'application stockée sur le serveur
	 */
	public CharSequence[] getUsers(String phoneId) {
		BufferedReader in = null;
		ArrayList<String> users = null;

		try {
			HttpClient client = new DefaultHttpClient();

			String url = Utils.SERVER_URL + USERS_SERVLET;
			if (!url.endsWith("?"))
				url += "?";

			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair(PHONE_ID_TAG, phoneId));
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += paramString;
			HttpGet request = new HttpGet(url);

			// Execute la requête et récupère les informations renvoyées par le
			// serveur
			HttpResponse response = client.execute(request);
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
			users = gson.fromJson(page, type);

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

		// Transforme la liste en CharSequence pour être représentée dans un
		// dialogue
		int length = users.size();
		CharSequence[] userCharSequence = new CharSequence[length];

		for (int i = 0; i < length; i++) {
			userCharSequence[i] = users.get(i);
		}

		return userCharSequence;
	}

	// /**
	// * Post vers le serveur.
	// *
	// * @param album
	// * String représentant l'album à envoyer vers le serveur
	// * @param nameValuePairs
	// * Liste des chemins vers les images avec leur commentaire
	// * associé
	// * @return status d'envoi de l'album
	// */
	// public boolean postAlbum(String album, List<Picture> nameValuePairs) {
	// HttpClient httpClient = new DefaultHttpClient();
	// HttpContext localContext = new BasicHttpContext();
	// HttpPost httpPost = new HttpPost(Utils.SERVER_URL + ALBUM_SERVLET);
	//
	// MultipartEntity entity = new MultipartEntity(
	// HttpMultipartMode.BROWSER_COMPATIBLE);
	//
	// try {
	// // Ajoute l'album comme première partie de l'entité
	// entity.addPart(ALBUM_TAG, new StringBody(album));
	// } catch (UnsupportedEncodingException e1) {
	// e1.printStackTrace();
	// return false;
	// }
	//
	// // Ajoute chacune des photos comme partie de l'entité
	// for (Picture picturePath : nameValuePairs) {
	// File toSend = new File(picturePath.getPath());
	// entity.addPart(toSend.getName(), new FileBody(toSend));
	// }
	//
	// httpPost.setEntity(entity);
	// try {
	// HttpResponse response = httpClient.execute(httpPost, localContext);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// return false;
	// } catch (IOException e) {
	// e.printStackTrace();
	// return false;
	// }
	// return true;
	// }

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
		params.add(new BasicNameValuePair(PHONE_ID_TAG, phoneId));

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
		params.add(new BasicNameValuePair(PHONE_ID_TAG, phoneId));

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

	public boolean checkReceivedPicture(String phoneId) {
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL
				+ CHECK_NEW_ALBUMS_SERVLET);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// id du téléphone
		params.add(new BasicNameValuePair(PHONE_ID_TAG, phoneId));

		UrlEncodedFormEntity entity = null;

		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		httpPost.setEntity(entity);
		int result = executePost(httpPost).getStatusLine().getStatusCode();

		if (result > 1000) {
			return true;
		}
		return false;
	}

	public String getAlbum(String phoneId) {
		HttpPost httpPost = new HttpPost(Utils.SERVER_URL + SEND_ALBUM);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// id du téléphone
		params.add(new BasicNameValuePair(PHONE_ID_TAG, phoneId));

		UrlEncodedFormEntity entity = null;

		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		httpPost.setEntity(entity);

		HttpResponse httpResponse = executePost(httpPost);

		InputStream is;
		try {
			is = httpResponse.getEntity().getContent();

			String line = "";
			StringBuilder total = new StringBuilder();

			// Wrap a BufferedReader around the InputStream
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			// Read response until the end
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
			return total.toString();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// public String getPictures(Album album) {
	// HttpPost httpPost = new HttpPost(SERVER_URL + GET_IMAGE_SERVLET);
	//
	// ArrayList<Picture> pictures = album.getPictures();
	//
	// for (Picture picture : pictures) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	//
	// params.add(new BasicNameValuePair("RECEIVER", album.getReceiver()));
	// params.add(new BasicNameValuePair("SENDER", album.getSender()));
	// params.add(new BasicNameValuePair("PICTURE", picture.getPath()));
	// params.add(new BasicNameValuePair("ALBUM", album.getTitle()));
	//
	// UrlEncodedFormEntity entity = null;
	//
	// try {
	// entity = new UrlEncodedFormEntity(params);
	// } catch (UnsupportedEncodingException e1) {
	// e1.printStackTrace();
	// }
	//
	// httpPost.setEntity(entity);
	//
	// HttpResponse httpResponse = executePost(httpPost);
	//
	// InputStream is;
	// try {
	// is = httpResponse.getEntity().getContent();
	//
	// String line = "";
	// StringBuilder total = new StringBuilder();
	//
	// // Wrap a BufferedReader around the InputStream
	// BufferedReader rd = new BufferedReader(
	// new InputStreamReader(is));
	//
	// // Read response until the end
	// while ((line = rd.readLine()) != null) {
	// total.append(line);
	// }
	// return total.toString();
	// } catch (IllegalStateException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return "";
	// }

}

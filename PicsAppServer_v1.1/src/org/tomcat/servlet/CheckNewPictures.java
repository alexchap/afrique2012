package org.tomcat.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tomcat.manager.DbManager;

import com.google.gson.Gson;

/**
 * Servlet qui vérifie si un utilisateur donnée a reçu de nouvelles photos
 */
public class CheckNewPictures extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DbManager mDbManager = new DbManager();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CheckNewPictures() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		System.out.println("Checking new pictures");
		//1-Extraction de l'identifiant du téléphone
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);
		//2-Obtention du pseudo associé à cet identifiant
		String pseudo = mDbManager.getUserPseudo(phoneId);
		//3-Obtention de la liste de photo disponible pour cet utilisateur
		ArrayList<String> paths = mDbManager.getNewPictures(pseudo);
		//4-Recupération du nombre de photo de la liste
		int total = paths.size();
		//5-Ajout du nombre de photo dans l'entête de la réponse
		response.setStatus(total + 1000);
		//6-Transformation de la liste de photo en chaine de caractère
		Gson gson = new Gson();
		String pathsString = gson.toJson(paths);
		//7-Envoie de la chaîne de caractère
		PrintWriter out = response.getWriter();
		out.write(pathsString);
	}
}

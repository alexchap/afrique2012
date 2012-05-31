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
 * Servlet qui v�rifie si un utilisateur donn�e a re�u de nouvelles photos
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
		//1-Extraction de l'identifiant du t�l�phone
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);
		//2-Obtention du pseudo associ� � cet identifiant
		String pseudo = mDbManager.getUserPseudo(phoneId);
		//3-Obtention de la liste de photo disponible pour cet utilisateur
		ArrayList<String> paths = mDbManager.getNewPictures(pseudo);
		//4-Recup�ration du nombre de photo de la liste
		int total = paths.size();
		//5-Ajout du nombre de photo dans l'ent�te de la r�ponse
		response.setStatus(total + 1000);
		//6-Transformation de la liste de photo en chaine de caract�re
		Gson gson = new Gson();
		String pathsString = gson.toJson(paths);
		//7-Envoie de la cha�ne de caract�re
		PrintWriter out = response.getWriter();
		out.write(pathsString);
	}
}

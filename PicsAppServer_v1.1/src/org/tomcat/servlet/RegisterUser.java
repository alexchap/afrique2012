package org.tomcat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tomcat.manager.DbManager;
import org.tomcat.manager.FileManager;

/**
 * Servlet permettant d'enregistrer les identifiants des utilisateurs dans la
 * base de données
 */
public class RegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private DbManager mDbManager = new DbManager();
	private FileManager mFileManager = new FileManager();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterUser() {
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
		int nbLine = -1;
		boolean isCreate = false;
		System.out.println("Register server");
		//1-Extraction des données de l'utilisateur
		String user = request.getParameter(DbManager.USERNAME_TAG);
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);

		//2-Vérifie si la paire (phoneId,user) n'existe pas déjà dans la base de
		// données et enregistrement des identifiants de utilisateur
		if (!mDbManager.isInDbPseudo(user)) {
			// Mis à jour des identifiants de l'utilisateur dans la base de
			// données
			nbLine = mDbManager.addUser(user, phoneId);
			// Création du dossier dans lequel seront enregistrés
			// temporairement la photos envoyée par l'utilisateur
			isCreate = mFileManager.createDirectory(user);
		}

		//3-Formulation de la réponse
		if (isCreate && nbLine == 1) {
			// Cas lorsque l'utilisateur n'existait pas et a été créé
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			// Cas lorsque l'utilisateur est déjà présent dans la base de
			// données
			response.setStatus(HttpServletResponse.SC_CONFLICT);
		}
	}
}

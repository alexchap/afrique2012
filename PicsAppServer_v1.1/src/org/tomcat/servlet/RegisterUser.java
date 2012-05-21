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
		// Extraction des données de l'utilisateur
		String user = request.getParameter(DbManager.USERNAME_TAG);
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);
		System.out.println(user);
		System.out.println(phoneId);

		// Vérifie si la paire phoneId et user existe déjà dans la base de
		// données
		if (!mDbManager.isInDbPseudo(user)) {
			// Ajoute l'utilisateur dans la base de données
			nbLine = mDbManager.addUser(user, phoneId);
			// Création du dossier dans lequel seront enregistrés
			// temporairement les albums envoyés par l'utilisateur
			isCreate = mFileManager.createDirectory(user);
		}

		// Se mettre d'accord sur les messages de confirmation
		if (isCreate && nbLine == 1) {
			// Cas existait pas et a pu être créé
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			// Cas n'a pas pu être créé
			response.setStatus(HttpServletResponse.SC_CONFLICT);
		}
	}
}

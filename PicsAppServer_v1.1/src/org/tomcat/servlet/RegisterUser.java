package org.tomcat.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tomcat.manager.DbManager;
import org.tomcat.manager.FileManager;

/**
 * Servlet permettant d'enregistrer les identifiants des utilisateurs dans la base de données
 */
public class RegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/** Tag pour le nom d'utilisateur */
	private static final String USERNAME_TAG = "USERNAME";

	/** Tag pour l'id du téléphone */
	private static final String PHONE_ID_TAG = "PHONEID";
	
	private DbManager dbManager=new DbManager();
	private FileManager fileManager=new FileManager(); 

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterUser() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int nbLine = -1;
		boolean isCreate = false;
		// Extraction des données de l'utilisateur
		String user = request.getParameter(USERNAME_TAG);
		String phoneId = request.getParameter(PHONE_ID_TAG);
		System.out.println(user);
		System.out.println(phoneId);

		// Vérifie si la paire phoneId et user existe déjà dans la base de
		// données
		if (!dbManager.isInDbPseudo(user)) {
			// Ajoute l'utilisateur dans la base de données
			nbLine = dbManager.addUser(user, phoneId);
			// Création du dossier dans lequel seront enregistrés
			// temporairement les albums envoyés par l'utilisateur
			isCreate = fileManager.createDirectory(user);
		}

		// Se mettre d'accord sur les messages de confirmation
		if (isCreate && nbLine == 1) {
			// Cas existait pas et a pu être créé
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			// Cas n'a pas pu être créé
			response.setStatus(HttpServletResponse.SC_CONFLICT);
		}
		dbManager.close();
	}

}

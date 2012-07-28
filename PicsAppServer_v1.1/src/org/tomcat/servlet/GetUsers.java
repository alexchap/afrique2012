package org.tomcat.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tomcat.manager.DbManager;

import com.google.gson.Gson;

/**
 * Servlet qui retourne tous les utilisateurs pr�sents dans la base de donn�es
 */
public class GetUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/** Tag pour l'id du t�l�phone */

	private DbManager dbManager = new DbManager();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetUsers() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Send users list to client");
		//1-Extraction de l'identifiant du t�l�phone
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);
		//2-Obtention de la liste des utilisateurs
		ArrayList<String> users = dbManager.getUsers(phoneId);
		users.add("ECHO");
		//trie
		Collections.sort(users);
		//3-Transformation de la liste en cha�ne de caract�res 
		Gson gson = new Gson();
		String usersString = gson.toJson(users);
		//4-Envoie de la cha�ne de caract�res
		PrintWriter out = response.getWriter();
		out.write(usersString);
	}
}

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
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);
		ArrayList<String> users = dbManager.getUsers(phoneId);
		Collections.sort(users);
		Gson gson = new Gson();
		String usersString = gson.toJson(users);

		PrintWriter out = response.getWriter();
		out.write(usersString);
	}
}

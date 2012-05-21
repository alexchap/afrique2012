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
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);
		String pseudo = mDbManager.getUserPseudo(phoneId);

		ArrayList<String> paths = mDbManager.getNewPictures(pseudo);

		int total = paths.size();
		response.setStatus(total + 1000);

		Gson gson = new Gson();
		String pathsString = gson.toJson(paths);

		PrintWriter out = response.getWriter();
		out.write(pathsString);
	}
}

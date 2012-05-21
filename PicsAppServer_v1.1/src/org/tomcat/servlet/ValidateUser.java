package org.tomcat.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tomcat.manager.DbManager;

/**
 * Servlet implementation class ValidateUser
 */
public class ValidateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DbManager dbManager = new DbManager();
	private static final int VALID_OLD_USER = 1010;
	private static final int NOT_REGISTERED = 1040;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ValidateUser() {
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

		System.out.println("Validation de l'utilisateur");
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);

		if (dbManager.isRegistered(DbManager.USER_TABLE,
				DbManager.USER_PHONEID_FIELD, phoneId)) {
			response.setStatus(VALID_OLD_USER);
			// System.out.println(VALID_OLD_USER);

		} else {
			response.setStatus(NOT_REGISTERED);
			// System.out.println(NOT_REGISTERED);
		}
	}
}

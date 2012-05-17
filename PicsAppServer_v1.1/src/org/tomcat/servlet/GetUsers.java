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
import org.tomcat.manager.FileManager;

import com.google.gson.Gson;

/**
 * Servlet qui retourne tous les utilisateurs présents dans la base de données
 */
public class GetUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/** Tag pour l'id du téléphone */
	private static final String PHONE_ID_TAG = "PHONEID";
	
	private DbManager dbManager=new DbManager();
	private FileManager fileManager=new FileManager();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUsers() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String phoneId = request.getParameter("PHONEID");
		ArrayList<String> users = dbManager.getUsers(phoneId);
		Collections.sort(users);
		Gson gson = new Gson();
		String usersString = gson.toJson(users);
		

		PrintWriter out = response.getWriter();
		out.write(usersString);
		
		dbManager.close();
	}

}

package org.tomcat.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tomcat.manager.DbManager;

/**
 * Servlet implementation class SendImage
 */
public class SendImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DbManager mDbManager = new DbManager();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SendImage() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Send image to client");
		String filePath = null;
		String sender = null;
		// 1-Extraction de l'identifiant du téléphone
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);
		// 2-Recupération du pseudo de l'utilisateur en fonction de
		// l'identifiant du téléphone
		String receiver = mDbManager.getUserPseudo(phoneId);
		// 3-résultat de la requête sur la liste des photos reçues par un
		// utilisateur
		ResultSet rset = mDbManager.getPictureToSend(receiver);
		try {
			//4-Extraction de l'expéditeur et du chemin absolu de la photo
			if (rset.next()) {
				sender = rset.getString(DbManager.PICTURE_SENDER_FIELD);
				filePath = rset.getString(DbManager.PICTURE_PATH_FIELD);
			}
			
			

			//5-Recupération de chemin absolue de l'image (Pourquoi déjà connu)
			File file = new File(filePath);
			ServletContext sc = getServletContext();
			String filename = sc.getRealPath(filePath);

			// 6-Récupération du type de MIME de l'image
			String mimeType = sc.getMimeType(filename);
			System.out.println(mimeType);
			if (mimeType == null) {
				sc.log("Could not get MIME type of " + filename);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

			// 7-Ajout des entêtes de la réponse
			// Ajout du type de mime à l'entête
			response.setContentType(mimeType);
			response.addHeader("Content-Disposition", "attachment; filename="
					+ file.getName() + ", sender=" + sender);

			// Ajout de la taille du fichier à l'entête
			response.setContentLength((int) file.length());

			// 8-Ouverture du ficier et du flux de sortie
			FileInputStream in = new FileInputStream(file);
			OutputStream out = response.getOutputStream();

			// 9-Copie du contenu du fichier dans le flux de sortie
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = in.read(buf)) >= 0) {
				out.write(buf, 0, count);
			}
			in.close();
			out.close();
			// 10- Mis à jour de la base de données
			mDbManager.setPictureSent(sender, receiver, filePath);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
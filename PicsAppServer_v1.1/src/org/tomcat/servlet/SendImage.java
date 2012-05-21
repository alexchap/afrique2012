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
		System.out.println("Send image servlet");
		String filePath = null;
		String sender = null;
		String phoneId = request.getParameter(DbManager.PHONEID_TAG);
		String receiver = mDbManager.getUserPseudo(phoneId);

		ResultSet rset = mDbManager.getPictureToSend(receiver);
		try {
			if (rset.next()) {
				sender = rset.getString(DbManager.PICTURE_SENDER_FIELD);
				filePath = rset.getString(DbManager.PICTURE_PATH_FIELD);
			}
			File file = new File(filePath);

			// Get the absolute path of the image
			ServletContext sc = getServletContext();
			String filename = sc.getRealPath(filePath);

			// Get the MIME type of the image
			String mimeType = sc.getMimeType(filename);
			System.out.println(mimeType);
			if (mimeType == null) {
				sc.log("Could not get MIME type of " + filename);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

			// Set content type
			response.setContentType(mimeType);

			response.addHeader("Content-Disposition", "attachment; filename="
					+ file.getName() + ", sender=" + sender);

			// Set content size
			response.setContentLength((int) file.length());

			// Open the file and output streams
			FileInputStream in = new FileInputStream(file);
			OutputStream out = response.getOutputStream();

			// Copy the contents of the file to the output stream
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = in.read(buf)) >= 0) {
				out.write(buf, 0, count);
			}
			in.close();
			out.close();

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
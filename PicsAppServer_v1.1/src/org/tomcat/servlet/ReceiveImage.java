package org.tomcat.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.tomcat.manager.DbManager;
import org.tomcat.manager.FileManager;

/**
 * Servlet implementation class ReceiveImage
 */
public class ReceiveImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DbManager mDbManager = new DbManager();
	private FileManager mFileManager = new FileManager();
	private static final int RECEIVE = 1020;
	private static final int NOT_RECEIVE = 1050;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReceiveImage() {
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

		DiskFileItem item = null;
		DiskFileItem it = null;
		String sender = null;
		String phoneId = null;
		String receiver = null;
		String fileName = null;
		boolean statut = false;
		int nbLine = -1;

		System.out.println("Receiving Picture");
		try {
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request into a list of DiskFileItems
			List<FileItem> items = upload.parseRequest(request);

			// Iterates through the list of DiskFileItems and extracts item and
			// album
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				item = (DiskFileItem) iter.next();
				if (item.isFormField()) {
					if (item.getFieldName().equals(DbManager.PHONEID_TAG)) {
						phoneId = item.getString();
					}
					if (item.getFieldName().equals(DbManager.RECEIVER_TAG)) {
						receiver = item.getString();
					}
				} else {
					it = item;
					fileName = item.getName();
				}
			}

			// V�rifie si l'exp�diteur et le destinataire sont bien dans la
			// base de donn�es
			sender = mDbManager.getUserPseudo(phoneId);
			boolean validSender = mDbManager.isRegistered(DbManager.USER_TABLE,
					DbManager.USER_NAME_FIELD, sender);
			boolean validReceiver = mDbManager.isRegistered(
					DbManager.USER_TABLE, DbManager.USER_NAME_FIELD, receiver) || receiver.equalsIgnoreCase("echo");

			if (validSender && validReceiver) {
				// V�rifie si le dossier correspondant � l'exp�diteur existe
				// pour la sauvegarde de la photo dans la BD

				if (! mFileManager.exists(sender)) {
					mFileManager.createDirectory(sender);
					System.out.println("Répertoire " + sender + " créé");
				}

				// Sauvegarde de l'image dans la base de donn�es
				String path = FileManager.DEFAULT_DB_PATH + sender + File.separator
						+ receiver + "-" + fileName;
				nbLine = mDbManager.saveImageInDb(sender, receiver, path);

				// Enregistrement de la photo
				statut = mFileManager.saveImageToDisk(it, sender, path);
				
				// Si envoi à utilisateur ECHO, on ajoute une photo aléatoire
				// dans la DB qui sera renvoyée à l'utilisateur
				if(receiver.equalsIgnoreCase("echo")){
					System.out.println("ECHO user - assign random image to " + sender);
					String random_path = getRandomImage();
					System.out.println("ECHO: Image aléatoire choisie : " + random_path);
					nbLine = mDbManager.saveImageInDb("echo",sender, random_path);
				}
				
			}
			

		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		if (statut && nbLine == 1)
			response.setStatus(RECEIVE);
		else
			response.setStatus(NOT_RECEIVE);

	}
	
	// cette fonction retourne une image aléatoire dans le dossier img
	private String getRandomImage(){
		
		ServletContext ctxt = getServletContext();
		File mydir = new File(ctxt.getRealPath("/")+"WEB-INF/img/");
		
		File[] myfiles = mydir.listFiles();
		Random r = new Random();
		String imgpath = myfiles[r.nextInt(myfiles.length)].getAbsolutePath();
		return imgpath;
		
	}
}

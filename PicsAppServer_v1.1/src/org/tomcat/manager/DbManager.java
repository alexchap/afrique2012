/**
 * 
 */
package org.tomcat.manager;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * Classes permettant de faire toutes les acc�s � la base de donn�es
 * 
 * @author alex
 * 
 */
public class DbManager {
	private static final String DATABASE_SERVER = "jdbc:mysql://localhost:";
	private static final String DATABASE_PORT = "3306/";
	private static final String DATABASE_NAME = "PicsAppBD";
	private static final String DATABASE_USER = "root";
	private static final String DATABASE_USER_PASSWD = "picsapp";

	/** Champs dans la base de donn�es */
	public static final String USER_PHONEID_FIELD = "phoneId";
	public static final String USER_NAME_FIELD = "pseudo";
	public static final String PICTURE_RECEIVER_FIELD = "receiver";
	public static final String PICTURE_SENDER_FIELD = "sender";
	public static final String PICTURE_PATH_FIELD = "path";

	/** Tables dans la base de donn�es */
	public static final String USER_TABLE = "user";
	public static final String PICTURE_TABLE = "picture";

	/** Tag des param�tres dans les requ�tes */
	public static final String PHONEID_TAG = "PHONEID";
	public static final String USERNAME_TAG = "USERNAME";
	public static final String RECEIVER_TAG = "RECEIVER";

	/**
	 * Constructeur
	 */
	public DbManager() {

	}

	/**
	 * Etablie la connexion � la base de donn�e
	 * 
	 * @return Connection l'object Connection li� � la connexion �tablie
	 */
	public Connection dbConnection() {
		Connection connection = null;
		try {
			// Enregistrer le driver JDBC aupr�s du responsable des drivers
			Class.forName("com.mysql.jdbc.Driver");

			// Connection � la base de donn�e avec les informations de login
			connection = (Connection) DriverManager.getConnection(
					DATABASE_SERVER + DATABASE_PORT + DATABASE_NAME,
					DATABASE_USER, DATABASE_USER_PASSWD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Connection � la base de donn�es...");
		return connection;
	}

	/**
	 * Ex�cute la requ�te de mis � jour fournie par l'object statement
	 * 
	 * @param statement
	 *            Statement � executer
	 * @return Le nombre de ligne mise � jour dans la base de donn�es
	 */
	public int dbUpdateQuery(PreparedStatement statement) {
		int numLineDb = 0;
		try {
			numLineDb = statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(numLineDb);
		return numLineDb;
	}

	/**
	 * Ex�cute la requ�te fournie par le statement
	 * 
	 * @param statement
	 *            � ex�cuter
	 * @return ResultSet 
	 * 		le r�sultat retourn� par la base de donn�e
	 */
	public ResultSet dbExecuteQuery(PreparedStatement statement) {
		ResultSet rset = null;
		try {
			rset = statement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rset;
	}

	/**
	 * Ex�cute une requ�te avec un crit�re de recherche
	 * 
	 * @param table
	 *            la table dans laquelle on doit acc�der
	 * @param field
	 *            le champ voulu
	 * @param value
	 *            La valeur recherch�e
	 * @return ResultSet R�sultat de la requ�te
	 */
	public ResultSet getDbResultSet(String table, String field, String value) {
		PreparedStatement stmnt = null;
		ResultSet result = null;
		String req = "select * from " + table + " where " + field + " = ?";
		Connection con = dbConnection();
		try {
			stmnt = (PreparedStatement) con.prepareStatement(req);
			stmnt.setString(1, value);
			result = stmnt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * V�rifie si un utilisateur est pr�sent dans la base de donn�e
	 * 
	 * @param	String
	 * 			Nom de l'utilisateur recherch�
	 * 
	 * @return Boolean 
	 * 			"oui" si l'utilisateur est pr�sent dans la base de donn�e
	 */
	public boolean isInDbPseudo(String user) {
		try {
			ResultSet result = getDbResultSet(USER_TABLE, USER_NAME_FIELD, user);
			while (result.next()) {
				if (result.getString(USER_NAME_FIELD).equals(user)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * Ajoute un utilisateur dans la base de donn�es
	 * 
	 * @param user
	 *            Nom de l'utilisateur � ajouter
	 * @param phoneId
	 *            l'id du t�l�phone de l'utilisateur � ajouter
	 * @return
	 * 			Nombre de ligne mis � jour dans la base de donn�es
	 */		
	public int addUser(String user, String phoneId) {

		String req = "insert into user (phoneId,pseudo) values (?,?)";
		Connection con = dbConnection();
		PreparedStatement stmnt = null;
		try {
			stmnt = (PreparedStatement) con.prepareStatement(req);
			stmnt.setString(1, phoneId);
			stmnt.setString(2, user);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return dbUpdateQuery(stmnt);
	}

	/**
	 * Ferme la connection
	 */
	public void close(Connection connection) {

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retourne vrai si le champ <code>field</code> de la table
	 * <code>table</code> a la valeur <code>value</code>
	 * 
	 * @param table
	 *            la table � laquelle on doit acc�der
	 * @param field
	 *            le champ voulu
	 * @param value
	 *            la valeur recherch�e
	 * @return <code>true</code> si l'utilisateur est enregistr�,
	 *         <code>false</code> sinon
	 */
	public boolean isRegistered(String table, String field, String value) {
		PreparedStatement stmnt = null;
		String req = "select * from " + table + " where " + field + " = ?";
		Connection con = dbConnection();
		try {
			stmnt = (PreparedStatement) con.prepareStatement(req);
			stmnt.setString(1, value);
			System.out.println(value);
			ResultSet rs = dbExecuteQuery(stmnt);
			while (rs.next()) {
				if (rs.getString(field).equals(value)) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * recherche des enregistrements de la table user
	 * 
	 * @param phoneId
	 *            Crit�re de recherche
	 * @return Resultat de la recherche
	 */
	public ArrayList<String> getUsers(String phoneId) {
		Connection connection = dbConnection();
		String req = "select pseudo from user where phoneId not like ?";

		// Connection con = dbManager.dbConnection();
		PreparedStatement statement = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(req);
			statement.setString(1, phoneId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet rset = dbExecuteQuery(statement);

		ArrayList<String> users = new ArrayList<String>();

		try {
			while (rset.next()) {
				users.add(rset.getString(USER_NAME_FIELD));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return users;
	}

	/**
	 * Sauvegarder l'image dans la base de donn�es
	 * 
	 * @param sender
	 *            l'exp�diteur de la photo
	 * 
	 * @param receiver
	 *            le destinataire de la photo
	 * 
	 * @param path
	 *            Emplacement dans le disque o� sera sauvegard�e la photo
	 * @return Nombre de ligne mis � jour
	 */
	public int saveImageInDb(String sender, String receiver, String path) {
		String req = "insert into picture (sender,receiver,path) values (?,?,?)";

		Connection con = dbConnection();
		PreparedStatement statement = null;
		try {
			statement = (PreparedStatement) con.prepareStatement(req);

			statement.setString(1, sender);
			statement.setString(2, receiver);
			statement.setString(3, path);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dbUpdateQuery(statement);
	}

	/**
	 * Recherche les informations dans la table album li�es � l'utilisateur
	 * sp�cifi� pour lesquelles le champs status  est 0
	 * 
	 * @param pseudo
	 *            Utilisateur dont on recherche les informations
	 * @return ArrayList<String>
	 * 				Liste du champs "path" correspondant au crit�re de recherche
	 */
	public ArrayList<String> getNewPictures(String pseudo) {
		PreparedStatement stmnt = null;
		ResultSet result = null;
		String req = "select * from picture where receiver = ? and status = 0";
		Connection con = dbConnection();

		ArrayList<String> paths = new ArrayList<String>();

		try {
			stmnt = (PreparedStatement) con.prepareStatement(req);
			stmnt.setString(1, pseudo);
			result = stmnt.executeQuery();

			while (result.next()) {
				paths.add(result.getString("path"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return paths;
	}

	/**
	 * Recherche une ligne sp�cique dans la table album li�es � l'utilisateur
	 * sp�cifi�
	 * 
	 * @param pseudo
	 *            Utilisateur dont on recherche les informations
	 * @return ResultSet Resultat de la requ�te
	 */

	public ResultSet getPictureToSend(String pseudo) {
		PreparedStatement stmnt = null;
		ResultSet result = null;
		String req = "select * from picture where receiver = ? and status = 0 limit 1";
		Connection con = dbConnection();
		try {
			stmnt = (PreparedStatement) con.prepareStatement(req);
			stmnt.setString(1, pseudo);
			result = stmnt.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * Met � jour le champ "status" d'un enregistrement de la table picture 
	 * 
	 * @param sender
	 * 			Exp�diteur
	 * @param receiver
	 * 			Destinataire
	 * @param path
	 * 			Chemin absolu du fichier
	 * @return
	 * 			Nombre de ligne mise � jour
	 */
	public int setPictureSent(String sender, String receiver, String path) {
		String req = "update picture set status=1 where sender=? and receiver=? and path=?";
		Connection con = dbConnection();
		PreparedStatement statement = null;
		try {
			statement = (PreparedStatement) con.prepareStatement(req);

			System.out.println("Setting picture sent " + sender + " "
					+ receiver + " " + path);
			statement.setString(1, sender);
			statement.setString(2, receiver);
			statement.setString(3, path);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dbUpdateQuery(statement);
	}

	/**
	 * Obtenir le nom d'un utilisateur en fonction de l'id de son t�l�phone
	 * 
	 * @param phoneId
	 * 			id du t�l�phone
	 * @return String
	 * 			Nom de l'utilisateur
	 */
	public String getUserPseudo(String phoneId) {
		ResultSet rs = getDbResultSet(USER_TABLE, USER_PHONEID_FIELD, phoneId);
		String name = "";

		try {
			if (rs.next())
				name = rs.getString(USER_NAME_FIELD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return name;
	}

}

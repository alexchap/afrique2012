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
 * Classes permettant de faire toutes les accès à la base de données
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

	/** Champs dans la base de données */
	public static final String USER_PHONEID_FIELD = "phoneId";
	public static final String USER_NAME_FIELD = "pseudo";
	public static final String PICTURE_RECEIVER_FIELD = "receiver";
	public static final String PICTURE_SENDER_FIELD = "sender";
	public static final String PICTURE_PATH_FIELD = "path";

	/** Tables dans la base de données */
	public static final String USER_TABLE = "user";
	public static final String PICTURE_TABLE = "picture";

	/** Tag des paramètres dans les requêtes */
	public static final String PHONEID_TAG = "PHONEID";
	public static final String USERNAME_TAG = "USERNAME";
	public static final String RECEIVER_TAG = "RECEIVER";

	/**
	 * Constructeur
	 */
	public DbManager() {

	}

	/**
	 * Etablie la connexion à la base de donnée
	 * 
	 * @return Connection l'object Connection lié à la connexion établie
	 */
	public Connection dbConnection() {
		Connection connection = null;
		try {
			// Enregistrer le driver JDBC auprès du responsable des drivers
			Class.forName("com.mysql.jdbc.Driver");

			// Connection à la base de donnée avec les informations de login
			connection = (Connection) DriverManager.getConnection(
					DATABASE_SERVER + DATABASE_PORT + DATABASE_NAME,
					DATABASE_USER, DATABASE_USER_PASSWD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Connection à la base de données...");
		return connection;
	}

	/**
	 * Exécute la requête de mis à jour fournie par l'object statement
	 * 
	 * @param statement
	 *            Statement à executer
	 * @return Le nombre de ligne mise à jour dans la base de données
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
	 * Exécute la requête fournie par le statement
	 * 
	 * @param statement
	 *            à exécuter
	 * @return ResultSet 
	 * 		le résultat retourné par la base de donnée
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
	 * Exécute une requête avec un critère de recherche
	 * 
	 * @param table
	 *            la table dans laquelle on doit accéder
	 * @param field
	 *            le champ voulu
	 * @param value
	 *            La valeur recherchée
	 * @return ResultSet Résultat de la requête
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
	 * Vérifie si un utilisateur est présent dans la base de donnée
	 * 
	 * @param	String
	 * 			Nom de l'utilisateur recherché
	 * 
	 * @return Boolean 
	 * 			"oui" si l'utilisateur est présent dans la base de donnée
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
	 * Ajoute un utilisateur dans la base de données
	 * 
	 * @param user
	 *            Nom de l'utilisateur à ajouter
	 * @param phoneId
	 *            l'id du téléphone de l'utilisateur à ajouter
	 * @return
	 * 			Nombre de ligne mis à jour dans la base de données
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
	 *            la table à laquelle on doit accéder
	 * @param field
	 *            le champ voulu
	 * @param value
	 *            la valeur recherchée
	 * @return <code>true</code> si l'utilisateur est enregistré,
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
	 *            Critère de recherche
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
	 * Sauvegarder l'image dans la base de données
	 * 
	 * @param sender
	 *            l'expéditeur de la photo
	 * 
	 * @param receiver
	 *            le destinataire de la photo
	 * 
	 * @param path
	 *            Emplacement dans le disque où sera sauvegardée la photo
	 * @return Nombre de ligne mis à jour
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
	 * Recherche les informations dans la table album liées à l'utilisateur
	 * spécifié pour lesquelles le champs status  est 0
	 * 
	 * @param pseudo
	 *            Utilisateur dont on recherche les informations
	 * @return ArrayList<String>
	 * 				Liste du champs "path" correspondant au critère de recherche
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
	 * Recherche une ligne spécique dans la table album liées à l'utilisateur
	 * spécifié
	 * 
	 * @param pseudo
	 *            Utilisateur dont on recherche les informations
	 * @return ResultSet Resultat de la requête
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
	 * Met à jour le champ "status" d'un enregistrement de la table picture 
	 * 
	 * @param sender
	 * 			Expéditeur
	 * @param receiver
	 * 			Destinataire
	 * @param path
	 * 			Chemin absolu du fichier
	 * @return
	 * 			Nombre de ligne mise à jour
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
	 * Obtenir le nom d'un utilisateur en fonction de l'id de son téléphone
	 * 
	 * @param phoneId
	 * 			id du téléphone
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

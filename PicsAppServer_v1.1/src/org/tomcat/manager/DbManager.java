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
 * Classe permettant de faire toutes les accès à la base de données 
 * @author alex
 *
 */
public class DbManager {
	private static final String DATABASE_SERVER = "jdbc:mysql://localhost:";
	private static final String DATABASE_PORT = "3306/";
	private static final String DATABASE_NAME = "PicsAppBD";
	private static final String DATABASE_USER = "root";
	private static final String DATABASE_USER_PASSWD = "picsapp";
	private static final String USER_PHONEID_FIELD = "phoneId";
	private static final String USER_NAME_FIELD = "pseudo";
	private static final String USER_TABLE = "user";
	private static final String ALBUM_TABLE = "album";
	private Connection connection=null;

	/**
	 * Constructeur
	 */
	public DbManager() {
		connection=dbConnection();
		System.out.println("Connection établie avec MySql ");
	}
	
	/** 
	 * Crée la connexion à la base de donnée
	 * 
	 * @return Connexion
	 * 			l'object Connection lié à la connexion établie
	 */
	public Connection dbConnection() {

		try {
			// Enregistrer le driver JDBC auprès du responsable des drivers
			Class.forName("com.mysql.jdbc.Driver");
			// Connection à la base de donnée avec les informations de login
			connection = (Connection) DriverManager
					.getConnection(DATABASE_SERVER + DATABASE_PORT
							+ DATABASE_NAME, DATABASE_USER,
							DATABASE_USER_PASSWD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * Exécute la requête de mis à jour 
	 * fournie par l'object statement
	 * @param statement 
	 * 					Statement à executer
	 * @return int 
	 * 			Le nombre de ligne mise à jour dans la base de donnée
	 */
	public int dbUpdateQuery(PreparedStatement statement) {
		int numLineDb = 0;
		try {
			numLineDb = statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numLineDb;
	}

	/**
	 * Exécute une requête
	 * 
	 * @param statement à exécuter
	 * @return les résultats retournés par la base de donnée
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
	 * Retourne l'ensemble des informations liées au champs associé à
	 * la valeur donné
	 * 
	 * @param table
	 * 			   la table à laquelle on doit accéder
	 * @param field
	 * 			   le champ voulu
	 * @param value
	 * 			   La valeur recherchée
	 * @return   ResultSet
	 * 					Résultat de la requête
	 */
	public ResultSet getDbResultSet(String table, String field, String value) {
		PreparedStatement stmnt = null;
		ResultSet result = null;
		String req = "select * from " + table + " where " + field + " = ?";
		//Connection con = dbConnection();
		try {
			stmnt = (PreparedStatement) connection.prepareStatement(req);
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
	 * @return Boolean
	 * 			"oui" si l'utilisateur est présent dans la base de donnée
	 */
	public boolean isInDbPseudo(String user) {
		try {
			ResultSet result = getDbResultSet(USER_TABLE,USER_NAME_FIELD , user);
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
	 *            l'utilisateur à ajouter
	 * @param phoneId
	 *            l'id du téléphone de l'utilisateur à ajouter
	 * @return
	 */
	public int addUser(String user, String phoneId) {

		String req = "insert into user (phoneId,pseudo) values (?,?)";
		//Connection con = dbConnection();
		PreparedStatement stmnt = null;
		try {
			stmnt = (PreparedStatement) connection.prepareStatement(req);
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
	public void close(){
		
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
		connection = dbConnection();
		try {
			stmnt = (PreparedStatement) connection.prepareStatement(req);
			stmnt.setString(1, value);
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
	 * @param phoneId
	 * 			Critère de recherche
	 * @return
	 * 		Resultat de la recherche
	 */
	public ArrayList<String> getUsers(String phoneId) {
		String req = "select pseudo from user where phoneId not like ?";

		//Connection con = dbManager.dbConnection();
		PreparedStatement statement = null;
		try {
			statement = (PreparedStatement) connection.prepareStatement(req);
			statement.setString(1, phoneId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet rset =dbExecuteQuery(statement);

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



	
	
	
	
	
	

}

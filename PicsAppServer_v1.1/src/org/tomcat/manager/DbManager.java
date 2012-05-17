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
 * Classe permettant de faire toutes les acc�s � la base de donn�es 
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
		System.out.println("Connection �tablie avec MySql ");
	}
	
	/** 
	 * Cr�e la connexion � la base de donn�e
	 * 
	 * @return Connexion
	 * 			l'object Connection li� � la connexion �tablie
	 */
	public Connection dbConnection() {

		try {
			// Enregistrer le driver JDBC aupr�s du responsable des drivers
			Class.forName("com.mysql.jdbc.Driver");
			// Connection � la base de donn�e avec les informations de login
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
	 * Ex�cute la requ�te de mis � jour 
	 * fournie par l'object statement
	 * @param statement 
	 * 					Statement � executer
	 * @return int 
	 * 			Le nombre de ligne mise � jour dans la base de donn�e
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
	 * Ex�cute une requ�te
	 * 
	 * @param statement � ex�cuter
	 * @return les r�sultats retourn�s par la base de donn�e
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
	 * Retourne l'ensemble des informations li�es au champs associ� �
	 * la valeur donn�
	 * 
	 * @param table
	 * 			   la table � laquelle on doit acc�der
	 * @param field
	 * 			   le champ voulu
	 * @param value
	 * 			   La valeur recherch�e
	 * @return   ResultSet
	 * 					R�sultat de la requ�te
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
	 * V�rifie si un utilisateur est pr�sent dans la base de donn�e
	 * 
	 * @return Boolean
	 * 			"oui" si l'utilisateur est pr�sent dans la base de donn�e
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
	 * Ajoute un utilisateur dans la base de donn�es
	 * 
	 * @param user
	 *            l'utilisateur � ajouter
	 * @param phoneId
	 *            l'id du t�l�phone de l'utilisateur � ajouter
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
	 * 			Crit�re de recherche
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

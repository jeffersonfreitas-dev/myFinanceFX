package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import database.exceptions.DatabaseException;

public class DatabaseTest {
	
	private static Connection con = null;
	
	public static Connection getConnection() {
		if(con == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				con = DriverManager.getConnection(url, props);
			}catch(SQLException e) {
				e.printStackTrace();
				throw new DatabaseException("Erro ao gerar uma conexão ao Banco de dados: -> " + e.getMessage());
			}
		}
		return con;
	}
	
	
	public static void closeConnection() {
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatabaseException("Erro ao fechar a conexão do Banco de dados: -> " + e.getMessage());
			}
		}
	}
	
	
	public static void closeStatement(Statement st) {
		if(st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatabaseException("Erro ao fechar o Statement da conexão: -> " + e.getMessage());
			}
		}
	}
	

	public static void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatabaseException("Erro ao fechar o ResultSet da conexão: -> " + e.getMessage());
			}
		}		
	}
	
	
	private static Properties loadProperties() {
		try (FileInputStream fs = new FileInputStream("database-dev.properties")){
			Properties props = new Properties();
			props.load(fs);
			return props;
		} catch (IOException e) {
			e.printStackTrace();
			throw new DatabaseException("Erro ao carregar o arquivo properties do Banco de dados: -> " + e.getMessage());
		}
	}

}

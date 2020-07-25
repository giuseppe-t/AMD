package it.valtellina.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCpostgreSQLconnect 
{	
    private Connection connection ;
    private Statement stmt;

    public JDBCpostgreSQLconnect (String url, String user, String password) {
		try {   
				Properties props = new Properties();
				props.setProperty("user",user);
				props.setProperty("password",password);
				props.setProperty("connectTimeout","0");
				Connection con = DriverManager.getConnection(url, props);
				
			if (con != null) {
				connection = con;
				stmt = con.createStatement();
				System.out.println("Connected to Cloud DB PostgreSQL");
			} else {
				System.out.println("Failed to connect to Cloud DB");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Connection getConn() {
		return this.connection;
	}
	public Statement getStatement() {
		return this.stmt;
	}
}

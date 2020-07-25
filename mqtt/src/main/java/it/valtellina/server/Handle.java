package it.valtellina.server;

import java.sql.SQLException;
import java.sql.Statement;


public abstract class Handle {

	public Boolean insert() 
	{
		Statement db;
		try {
			db = App.connect.getConn().createStatement();
			String sql = getSql(db);

			if (sql == null || sql.length()==0) {
				return true;
			}
			db.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}
	
	protected abstract String getSql(Statement db);

}

package it.valtellina.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Util {
     private static Util istance=null;
     private static Connection conn = null;
     private static Statement db = null;
     private static int logLevel = 0;

 	 private static final String appConfigPath = "Application.property";
	 private static Properties appProps=null;
	      
     private Util() 
     {
    	 	appProps = new Properties();
    	 	try {
    				appProps.load(new FileInputStream(appConfigPath));
    			} catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	 	if (this.getProp("LOG_LEVEL").equals("INFO")) logLevel = 1;
    	 	if (this.getProp("LOG_LEVEL").equals("WARNING")) logLevel = 2;
    	 	if (this.getProp("LOG_LEVEL").equals("ERROR")) logLevel = 3;    	 	
     }
     
     public static Util getInstance ()
     {
    	 if (istance==null) 
    		 istance = new Util();
    	 return istance;
     }
     
     public boolean initLogging(Connection connection) 
     {
    	 if (connection==null) return false;
    	 conn=connection;
    	 try {
			db = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}
    	return true;
     }
 
     public String getProp(String key)
     {
    	 return appProps.getProperty(key);
     }
     
     public int log(String level, String app, String description)
     {
    	 int localLevel=0;
    	 if (level.toUpperCase().equals("INFO")) localLevel=1;
    	 if (level.toUpperCase().equals("WARNING")) localLevel=2;
    	 if (level.toUpperCase().equals("ERROR")) localLevel=3;
    	 if (localLevel==0) return 1;
    	 if (localLevel<logLevel) return 2;
    	     	 
    	 String sql = "INSERT INTO CLOUD_VALTELLINA.AMD_TW_LogApplication (";
    	 sql=sql+"CD_LogType, CD_Application, DS_Log, TS_update) values ('";
    	 sql=sql+level+"', '"+app+"','"+description+"', current_timestamp);";

    	 try {
 			db.execute(sql);
    	 } catch (SQLException e) {
    		 e.printStackTrace();
    		 return 3;
    	 }
    	 return 0;
     }
}

package it.valtellina.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import it.valtellina.util.Util;

/* struttura messaggio:
[0] uid (String): identificativo utente,
[1] dateTime (String): timestamp del dispositivo in formato UTC (quindi da localizzare sul fuso orario),
[2] accuracy (Float): accuratezza della rilevazione GPS
[3] lat (Float): latitudine,
[4] lon (Float): longitudine

 */

public class HandleGPS extends Handle {

	private String uid ;

	private String timestamp;
	private String accuracy;
	private String lat; 
	private String lon;

	HandleGPS(String [] msg) 
	{
		if (msg.length != 5) {
			Util.getInstance().log("ERROR", App.APP_NAME, "Error in GPS topic structure");
		} else 
		{
			uid = msg[0];
			DateTimeMqtt utc = new DateTimeMqtt(msg[1]);
			timestamp = utc.getLocalized();
			
			accuracy = msg[2];
			lat = msg[3];
			lon = msg[4];
		}
		
	}


	@Override
	protected String getSql(Statement db) {
		String sql = "SELECT ID_Device, TS_Send FROM CLOUD_VALTELLINA.AMD_TT_RealTime_GPS ";
		sql = sql + "WHERE ID_Device = '"+uid+"' AND TS_Update = TO_TIMESTAMP('"+ timestamp + "', 'YYYY/MM/DD HH24:MI:SS')";
		ResultSet rs = null;

		try {
			rs = db.executeQuery(sql);
		} catch (SQLException e) {
			Util.getInstance().log("ERROR", App.APP_NAME, "DB Error executing select on AMD_TTRealTime_GPS");
			e.printStackTrace();
		}
		try {
			if (!rs.next()) {
				sql ="INSERT INTO CLOUD_VALTELLINA.AMD_TT_RealTime_GPS (ID_Device, CD_Longitudine, ";
				sql = sql + "CD_Latitudine, TS_Send, TS_Update, DS_Accuracy) values (";
				sql = sql + "'"+ uid + "', '" + lon + "', '" + lat + "', TO_TIMESTAMP('"+timestamp+"', 'YYYY/MM/DD HH24:MI:SS'), ";
				sql = sql + "current_timestamp, '"+accuracy+"');";
				return sql;
			} else {
				return null;
			}
		} catch (SQLException e) {
			Util.getInstance().log("ERROR", App.APP_NAME, "DB Error on ResultSet of AMD_TTRealTime_GPS");
			e.printStackTrace();
		}
		return sql;
	}
}

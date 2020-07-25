package it.valtellina.server;

import java.sql.Statement;

import it.valtellina.util.Util;

/*
 Messaggio così formato: 
uid (String): identificativo utente,
dateTime (String): timestamp del dispositivo in formato UTC (quindi da localizzare sul fuso orario),
status (Int): codici di stato:
	1.	Login effettuato (avvio app, 1 volta sola per “giornata”)
	2.	Logout effettuato (chiuso applicazione) 
	3.	GPS disattivato (chiusa la rilevazione posizione)
	4.	GPS attivato (nel senso di riattivata la rilevazione) 
5.	Riconnessione (in caso di perdita segnale, in fase di riconnessione) 
*/ 
public class HandleStatus extends Handle {
	
	private String uid;
	private String timestamp;
	private int status; 
	
	HandleStatus (String [] msg) 
	{
		uid = msg[0];
		DateTimeMqtt utc = new DateTimeMqtt(msg[1]);
		timestamp = utc.getLocalized();
	
		try {
			   status = Integer.parseInt(msg[2]);
			}
			catch (NumberFormatException e)
			{
			  Util.getInstance().log("ERROR", App.APP_NAME, "Error parsing status in Mqtt appStatus message");
			   status = 0;
			}
		
	}
	
	@Override
	protected String getSql(Statement db) {
		String sql = "INSERT INTO CLOUD_VALTELLINA.AMD_TT_RealTime_Status (ID_Device, ID_Value, TS_Send, TS_Update) ";
			   sql = sql + "values ('"+uid+"', "+Integer.toString(status)+", TO_TIMESTAMP('"+timestamp+"', 'YYYY/MM/DD HH24:MI:SS'), "; 
			   		 sql = sql + "current_timestamp );";
					   
		return sql;
	}

	
}

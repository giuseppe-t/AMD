package it.valtellina.server;

import java.sql.Statement;

import it.valtellina.util.Util;

import org.json.*;
/*
 * {
"uid":"AB-1234",
"dateTime": "2020-07-14T16:18:12.123Z",
"realTimeData": { 
   "teta": 55, 
   "min": 0, 
   "max": 22, 
   "micNoise": 2, 
   "accSeries": {
     "x”: [0.123456, 0.123456, 0.123456],
     "y": [0.123456, 0.123456, 0.123456],
     "z": [0.123456, 0.123456, 0.123456]
    }, 
   "gyroSeries": {
      "x": [0.123456, 0.123456, 0.123456],
      "y": [0.123456, 0.123456, 0.123456],
      "z": [0.123456, 0.123456, 0.123456]
    }, 
},
"isRealAlarm": true, 
"alarmType": 0
}	 */
public class HandleAlarm extends Handle {

	private String uid;
	private String timestamp; 
	private JSONObject realTimeData;
	private int NM_Teta;
	private int NM_Min; 
	private int NM_Max;
	private int NM_MicNoise; 
	private JSONObject DS_AccSeries;
	private JSONObject DS_GyroSeries;
	private boolean FL_isRealAlarm;
	private int alarmType;

	
	HandleAlarm(String json) 
	{
		JSONObject obj = new JSONObject(json);
		
		uid = obj.getString("uid");
		if (uid == null || uid.length()<5) {
			Util.getInstance().log("ERROR", App.APP_NAME, "Error in ALARM topic structure");
			Util.getInstance().log("ERROR", App.APP_NAME, json.toString());
		}
		DateTimeMqtt utc = new DateTimeMqtt(obj.getString("dateTime"));
		timestamp = utc.getLocalized();

		realTimeData = obj.getJSONObject("realTimeData");
		if (realTimeData == null ) {
			Util.getInstance().log("ERROR", App.APP_NAME, "Error in ALARM topic - realTimeData missing");
		} else 	{
			NM_Teta 	 = realTimeData.getInt("teta");
			NM_Min  	 = realTimeData.getInt("min");
			NM_Max  	 = realTimeData.getInt("max");
			NM_MicNoise  = realTimeData.getInt("micNoise"); 
			DS_AccSeries = realTimeData.getJSONObject("accSeries");
			DS_GyroSeries= realTimeData.getJSONObject("gyroSeries");
		}
		FL_isRealAlarm = obj.getBoolean("isRealAlarm");
		
		alarmType = obj.getInt("alarmType");
		if (alarmType < 0 || alarmType > 3) 
			Util.getInstance().log("ERROR", App.APP_NAME, "Error in ALARM topic - alarmType wrong");
				
	}

    private String fields; 

	@Override
	protected String getSql(Statement db) 
	{
		fields = "ID_Device, NM_Teta, NM_Min, NM_Max, NM_MicNoise, DS_AccSeries, ";
		fields = fields + "DS_GyroSeries, FL_isRealAlarm, ID_Value, TS_Send, TS_Update";
		
		String sql = "INSERT INTO CLOUD_VALTELLINA.AMD_TT_RealTime_Alarm ("+fields+") ";
		sql = sql + "values ('"+uid+"', "+Integer.toString(NM_Teta)+", "+Integer.toString(NM_Min)+", "; 
		sql = sql + Integer.toString(NM_Max) + ", " + Integer.toString(NM_MicNoise)+ ", ";
		sql = sql + "'" + DS_AccSeries.toString() + "', '" + DS_GyroSeries.toString() + "', '" + FL_isRealAlarm + "', ";
		sql = sql + Integer.toString(alarmType);
		sql = sql + ", TO_TIMESTAMP('"+timestamp+"', 'YYYY/MM/DD HH24:MI:SS'), current_timestamp );";
				   
	return sql;
	}

}

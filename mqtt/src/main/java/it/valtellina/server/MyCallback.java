package it.valtellina.server;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import it.valtellina.util.Util;

public class MyCallback implements MqttCallback
{
	private String GPS_TOPIC;
	private String APP_STAT_TOPIC;
	private String ALARM_TOPIC;
	
	public void setTopic(String gpsTopic, String appStatTopic, String alarmTopic) 
	{
		GPS_TOPIC = gpsTopic;
		APP_STAT_TOPIC = appStatTopic;
		ALARM_TOPIC = alarmTopic;
	}
	@Override
	public void connectionLost(Throwable cause) {
 		Util.getInstance().log("ERROR", App.APP_NAME, "Error, server lost connection MQTT"); 		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String [] msgToken = message.toString().split("\\;");
		Handle handle;
		
		if (topic.compareTo(GPS_TOPIC) == 0) {
			handle = new HandleGPS(msgToken);
		} else {
			if (topic.compareTo(APP_STAT_TOPIC) == 0) {
				handle = new HandleStatus(msgToken);
			} else {
				   if (topic.compareTo(ALARM_TOPIC) == 0) {
					handle = new HandleAlarm(message.toString());
				   } else handle = null; 
			  };
		  }
		if (handle != null) {
			handle.insert();
		} else {
			Util.getInstance().log("ERROR", App.APP_NAME, "Error in MQTT topic, arrived: "+topic);

		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

}

package it.valtellina.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import it.valtellina.util.Util;

public class App 
{
	public static final String APP_NAME="MQTT_SUBSCRIBER";
	public static String LOCAL_TIME ;
	public static JDBCpostgreSQLconnect connect ;
	
	private static MqttConnectOptions connOpts;
	public static MqttAsyncClient myClient;
	
    public static void main( String[] args ) throws MqttException
    {
    	Util util = Util.getInstance();    	
    	connect = new JDBCpostgreSQLconnect(util.getProp("url"), 
    										util.getProp("db_user"), 
    										util.getProp("db_pwd"));
    	
    	util.initLogging(connect.getConn());
        myClient = new MqttAsyncClient(util.getProp("Mqtt_url"), MqttClient.generateClientId());
        MyCallback myCallback = new MyCallback();
        myCallback.setTopic(util.getProp("GPS_TOPIC"), 
        					util.getProp("APP_STAT_TOPIC"),
        					util.getProp("ALARM_TOPIC"));
        myClient.setCallback(myCallback);
        
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(util.getProp("Mqtt_uname"));
        connOpts.setPassword(util.getProp("Mqtt_pwd").toCharArray());

        connOpts.setKeepAliveInterval(60);
        InputStream keyStoreInputStream;
		try {
			keyStoreInputStream = new FileInputStream("./keystore.p12");
	        connOpts.setSocketFactory(getSSLSocketFactory(keyStoreInputStream, "13579valtellina"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        IMqttToken token = myClient.connect(connOpts);
        token.waitForCompletion();
        
        myClient.subscribe(util.getProp("GPS_TOPIC"), 
        			Integer.valueOf(util.getProp("Mqtt_qos")));
        myClient.subscribe(util.getProp("APP_STAT_TOPIC"), 
        			Integer.valueOf(util.getProp("Mqtt_qos")));
        myClient.subscribe(util.getProp("ALARM_TOPIC"), 
    			Integer.valueOf(util.getProp("Mqtt_qos")));

        util.log("INFO", APP_NAME, "== mqtt up - subscribed to GPS/appStatus/Alarm ==");
                
     }
    
    
    public static SSLSocketFactory getSSLSocketFactory (InputStream keyStore, String password) 
    		throws MqttSecurityException {
        try{

            SSLContext ctx = null;
            SSLSocketFactory sslSockFactory=null;

            KeyStore ks;
            ks = KeyStore.getInstance("PKCS12");
            ks.load(keyStore, password.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(ks);
            TrustManager[] tm = tmf.getTrustManagers();
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tm, null);
            sslSockFactory = ctx.getSocketFactory();
            return sslSockFactory;

        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | KeyManagementException | java.security.cert.CertificateException e) {
            throw new MqttSecurityException(e);
        }
    }
}

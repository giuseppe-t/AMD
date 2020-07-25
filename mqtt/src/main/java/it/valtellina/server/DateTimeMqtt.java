package it.valtellina.server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeMqtt {
	
	private ZonedDateTime target;
	private String formatTarget;
	
	DateTimeMqtt(String utcString) 
	{
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		LocalDateTime utc = LocalDateTime.parse(utcString, format);
		ZoneId utcZone = ZoneId.of("UTC"); 
		ZonedDateTime departure = ZonedDateTime.of(utc, utcZone);
		ZoneId localZone = ZoneId.of("Europe/Rome"); 
		target = departure.withZoneSameInstant(localZone);
		
		formatTarget = target.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	
	public String getLocalized ()
	{
		return formatTarget;
	
	}
}


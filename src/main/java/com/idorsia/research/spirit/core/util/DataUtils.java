package com.idorsia.research.spirit.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class DataUtils {

	public static boolean isNumeric(String string) {
		if (string != null) {
			return string.matches("^[-+]?\\d+(\\.\\d+)?$");
		}
		return false;
	}

	public static Date parseUTCDateWithOffset(String date) {
		DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		OffsetDateTime odtInstanceAtOffset = OffsetDateTime.parse(date, DATE_TIME_FORMATTER);
		return Date.from(odtInstanceAtOffset.toInstant());
	}

	public static int booleanToInt(boolean boolVal) {
		int val = (boolVal) ? 1 : 0;
		return val;
	}
	
	public static boolean intToBolean(int val) {
		boolean bol = val==1? true : false;
		return bol;
	}
	
	public static Properties fetchProperties() {
		Properties properties = new Properties();
		try {
			InputStream inputStream = DataUtils.class.getResourceAsStream("/application.properties");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
			InputStream is = new ByteArrayInputStream(contents.getBytes());
			properties.load(is);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return properties;
	}
	
	public static String fetchInInt(Collection<Integer> obj) {
		StringBuilder sb = new StringBuilder(" IN (");
		Boolean isFirst=true;
		for(Integer i : obj) {
			if(isFirst) {
				isFirst=false;
			}else {
				sb.append(",");
			}
			sb.append(i);
		}
		sb.append(")");
		return sb.toString();
	}
	
	public static String fetchInClause(Collection<String> obj) {
		StringBuilder sb = new StringBuilder(" IN (");
		Boolean isFirst=true;
		for(String i : obj) {
			if(isFirst) {
				isFirst=false;
			}else {
				sb.append(",");
			}
			sb.append("'"+i+"'");
		}
		sb.append(")");
		return sb.toString();
	}
	
	public static LocalDate convertToLocalDate(Date dateToConvert) {
		LocalDate localDate = Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	    return localDate;
	}
	
	public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
		LocalDateTime localDateTime = dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	    return localDateTime;
	}
	
	public static String makeQueryJPLCompatible(String jpql) {
		StringTokenizer st = new StringTokenizer(jpql, "?'", true);
		StringBuilder sb = new StringBuilder();
		boolean inBrace = false;
		int index = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if(token.equals("\'")) {
				inBrace = !inBrace;
				sb.append(token);
			} else if(token.equals("?") && !inBrace) {
				sb.append("?" + (++index));
			} else {
				sb.append(token);
			}
		}
		return sb.toString();
	}
}

package com.konnect.jpms.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.konnect.jpms.select.FillSources;

public class UtilityFunctions implements ServletRequestAware, IConstants {
 
	String CURRENCY = "AUD$";
	
	DecimalFormat twoDecimal = new DecimalFormat("##,##,##0.00");
	DecimalFormat fourDecimal = new DecimalFormat("##,##,##0.0000");
	DecimalFormat oneDecimal = new DecimalFormat("##,##,##0.0");
	DecimalFormat zeroDecimal = new DecimalFormat("##,##,##,##0");
	DecimalFormat zeroDecimalWithOutComma = new DecimalFormat("########0");
	DecimalFormat twoDecimalWithOutComma = new DecimalFormat("#####0.00"); 
	DecimalFormat fourDecimalWithOutComma = new DecimalFormat("#####0.0000");
	DecimalFormat oneDecimalWithOutComma = new DecimalFormat("#####0.0"); 

	private static Logger log = Logger.getLogger(UtilityFunctions.class);
	
	public int parseToInt(String strArg) {
		try {
  
			if (strArg != null && strArg.length() > 0 && !strArg.equalsIgnoreCase("NULL")) {
				strArg = strArg.trim();
				strArg = strArg.replaceAll(" ", "");
				return Integer.parseInt(strArg);
			} else {  
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	public long parseToLong(String strArg) {
		try {

			if (strArg != null && strArg.length() > 0) {
				strArg = strArg.trim();
				strArg = strArg.replaceAll(" ", "");
				return Long.parseLong(strArg);
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	public double parseToDouble(String strArg) {
		try {
//			System.out.println("strArg ===>> " + strArg);
			if (strArg != null && !strArg.trim().equalsIgnoreCase("NULL") && !strArg.trim().equalsIgnoreCase("NaN")) {
				strArg = strArg.replaceAll(",", "");
				return Double.parseDouble(strArg);
			}
			else
				return 0.0d;
		} catch (Exception e) {
			return 0.0d;
		}
	}

	public boolean parseToBoolean(String strArg) {
		try {
			if (strArg != null) {
				strArg = strArg.trim();
				if (strArg.equalsIgnoreCase("YES")) {
					return true;
				} else if (strArg.equalsIgnoreCase("NO")) {
					return false;
				} else if (strArg.equalsIgnoreCase("T")) {
					return true;
				} else if (strArg.equalsIgnoreCase("F")) {
					return false;
				} else if (strArg.equalsIgnoreCase("TRUE")) {
					return true;
				} else if (strArg.equalsIgnoreCase("FALSE")) {
					return false;
				} else if (strArg.equalsIgnoreCase("ON")) {
					return true;
				} else if (strArg.equalsIgnoreCase("1")) {
					return true;
				} else if (strArg.equalsIgnoreCase("0")) {
					return false;
				} else if (strArg.equalsIgnoreCase("-1")) {
					return false;
				} else {
					return Boolean.parseBoolean(strArg);
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	// get current time
	public String timeNow() {

		Calendar now = Calendar.getInstance();
		int hrs = now.get(Calendar.HOUR_OF_DAY);
		int min = now.get(Calendar.MINUTE);
		int sec = now.get(Calendar.SECOND);

		String time = zero(hrs) + ":" + zero(min) + ":" + zero(sec);

		return time;
	}
	 
	public String timeNow(String strTimeZone) {
		
		Calendar now = Calendar.getInstance();
		if(strTimeZone!=null){
			now = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
		} 
		int hrs = now.get(Calendar.HOUR_OF_DAY);
		int min = now.get(Calendar.MINUTE);
		int sec = now.get(Calendar.SECOND);

		String time = zero(hrs) + ":" + zero(min) + ":" + zero(sec);

		return time;
	}

	public String zero(int num) {

		String number = (num < 10) ? ("0" + num) : ("" + num);
		return number; // Add leading zero if needed

	}

	// Obtain the image URL

	public Image createImage(String path, String description, Class _Class) {
		URL imageURL = _Class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}

	}

	public java.sql.Date getDateFormat(String strDate, String strFormat) {
		
		java.util.Date utdt = null;
		try {
			if(strDate==null || strDate=="" || strDate.length()==0 || strDate.equals("null") || strDate.equals("-")) {
				return null;
			}
			SimpleDateFormat smft = new SimpleDateFormat(strFormat);
			utdt = smft.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new java.sql.Date((utdt.getTime()));
	}

	public Timestamp getTimeStamp(String strDate, String strFormat) {
		java.util.Date utdt = null;
		try {
			SimpleDateFormat smft = new SimpleDateFormat(strFormat);
			utdt = smft.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new java.sql.Timestamp((utdt.getTime()));
	}

	public String getDateFormatUtil(java.util.Date strDate, String strFormat) {
		String utdt = null;
		try {
			if(strDate==null)
				return null;
			SimpleDateFormat smft = new SimpleDateFormat(strFormat);
			
			utdt = smft.format(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return utdt;
	}
	
	
	public java.util.Date getDateFormatUtil(String strDate, String strFormat) {
		java.util.Date utdt = null;
		try {
			if(strDate==null)
				return null;
			SimpleDateFormat smft = new SimpleDateFormat(strFormat);
			
			utdt = smft.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new java.util.Date((utdt.getTime()));
	}
	
	public java.util.Date getDateFormatUtil(String strDate, String strFormat,String strTimeZone) {
		java.util.Date utdt = null;
		try {
			if(strDate==null)
				return null;
			SimpleDateFormat smft = new SimpleDateFormat(strFormat);
			smft.setTimeZone(TimeZone.getTimeZone(strTimeZone));
			utdt = smft.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new java.util.Date((utdt.getTime()));
	}

	public String getDateFormat(String strDate, String inputFormat, String outputFormat) {
		java.util.Date utdt = null;
		String outputDate = null;
		try { 
			if(strDate==null)
				return "-";
//			System.out.println("strDate ===>> " +strDate+ " -- outputFormat ===>> " + outputFormat+ " -- inputFormat ===>> " + inputFormat);
			SimpleDateFormat smft = new SimpleDateFormat(inputFormat);
			utdt = smft.parse(strDate);
			smft = new SimpleDateFormat(outputFormat);
			outputDate = smft.format(utdt);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return outputDate;
	}

	
	public String getTimeFormatStr(String strDate, String inputFormat, String outputFormat) {
		java.util.Date utdt = null;
		String outputTime = null;
		try {
			if(strDate!=null && !strDate.equals("")){
				SimpleDateFormat smft = new SimpleDateFormat(inputFormat);
				utdt = smft.parse(strDate);
				smft = new SimpleDateFormat(outputFormat);
				outputTime = smft.format(utdt);
			} else {
				return "-";
			}
		} catch (Exception e) {
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			e.printStackTrace();
		}
			return outputTime;	
	}
	
	
	public java.sql.Time getTimeFormat(String strDate, String strFormat) {
		java.util.Date utdt = null;
		try {
			if(strDate!=null && !strDate.equals("")){
				SimpleDateFormat smft = new SimpleDateFormat(strFormat);
				utdt = smft.parse(strDate);
			}
		} catch (Exception e) {
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			e.printStackTrace();
		}
		if(utdt!=null){
			return new java.sql.Time((utdt.getTime()));	
		}else{
			return null;
		}
	}

	public java.sql.Time getTimeFormat(String strTime) {
		long lTime = 0;
		try {
			lTime = parseToLong(strTime);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return new java.sql.Time(lTime);
	}

	public java.sql.Date getCurrentDate(String strTimeZone) {
		
		if(strTimeZone!=null){
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			java.util.Date dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900 , cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			return new java.sql.Date((dt.getTime()));
		}else{
			return new java.sql.Date((new java.util.Date().getTime()));
		}
		
	}
	
	public java.sql.Date getPrevDate(String strTimeZone) {
		java.util.Date dt = null;
		if(strTimeZone!=null){
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			
		}else{
			dt = new java.util.Date();
		}
		
		long currDate = dt.getTime();
		long prevDate = currDate - (24*3600*1000);
		
		return new java.sql.Date(prevDate);
	}
	
	public java.sql.Date getPrevDate(String strTimeZone, int nPreviousDays) {
		java.util.Date dt = null;
		if(strTimeZone!=null){
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			cal.add(Calendar.DATE, -nPreviousDays);
			dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			
		}
		
		long currDate = dt.getTime();
		return new java.sql.Date(currDate);
	} 
	
	public java.sql.Date getPrevDate(Date utDate, int nPreviousDays) {
		java.util.Date dt = null;
		if(utDate!=null){
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(utDate);
			cal.add(Calendar.DATE, -nPreviousDays);
			dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			
		}
		
		long currDate = dt.getTime();
		return new java.sql.Date(currDate);
	}

	public java.sql.Date getFutureDate(String strTimeZone, int nFutureDays) {
		java.util.Date dt = null;
		if(strTimeZone!=null){
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			cal.add(Calendar.DATE, nFutureDays);
			dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			
		}
		
		long currDate = dt.getTime();
		return new java.sql.Date(currDate);
	}
	
	
	public java.sql.Date getFutureDate(Date utDate, int nFutureDays) {
		java.util.Date dt = null;
		if(utDate!=null){
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(utDate);
			cal.add(Calendar.DATE, nFutureDays);
			dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		}
		
		long currDate = dt.getTime();
		return new java.sql.Date(currDate);
	}
	
	public java.sql.Time getCurrentTime(String strTimeZone) {
		if(strTimeZone!=null){
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			java.util.Date dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
//			java.util.Date dt = cal.getTime(); 
			return new java.sql.Time((dt.getTime()));
		}else{
			return new java.sql.Time((new java.util.Date().getTime()));
		}
	}

	public long getTimeDifference(long IN, long OUT){
		long tDiff = (OUT - IN);
		
		if(tDiff < 0){
			OUT += 24 * 3600 * 1000; 
		}
		tDiff = (OUT - IN);
		
		return tDiff;
	}
	
	public String getTimeDiffInHoursMins(long IN, long OUT) {

		long tDiff = (OUT - IN);
		
		if(tDiff < 0){
			OUT += 24 * 3600 * 1000; 
		}
		tDiff = (OUT - IN);

		long diffHours = tDiff / (1000 * 60 * 60);
		long diffMinutes = (tDiff % (1000 * 60 * 60)) / (1000 * 60);

//		String minutesFormat = ((diffHours > 0) ? diffHours : "0") + ((diffMinutes > 0) ? ((diffMinutes < 10) ? ".0" + diffMinutes * (100d / 60d) : "." + diffMinutes * (100d / 60d)) : "");
//		minutesFormat = ((diffHours > 0) ? diffHours : "0") + ((diffMinutes > 0) ? ((diffMinutes < 10) ? ".0" + (int) (diffMinutes * (100d / 60)) : "." + (int) (diffMinutes * (100d / 60))) : "");

		
		String minutesFormat = ((diffHours > 0) ? diffHours : "0") + ((diffMinutes > 0) ? ((diffMinutes < 10) ? ".0" + diffMinutes : "." + diffMinutes) : "");
		minutesFormat = ((diffHours > 0) ? diffHours : "0") + ((diffMinutes > 0) ? ((diffMinutes < 10) ? ".0" + (int) (diffMinutes ) : "." + (int) (diffMinutes )) : "");
		
//		log.debug("minutesFormat="+minutesFormat);
//		log.debug("formatIntoTwoDecimal="+formatIntoTwoDecimal(parseToDouble(minutesFormat)));
		
		
		return formatIntoTwoDecimal(parseToDouble(minutesFormat));

	}
	
	public String roundOffInTimeInHoursMins(double totalTime) {

		String strTime = totalTime+"";
		
		if(strTime!=null && strTime.indexOf(".")>0) {
			
			String strHour = strTime.substring(0, strTime.indexOf("."));
			String strMinute = strTime.substring(strTime.indexOf(".")+1);
//			System.out.println("strHour ===>> " + strHour + " -- strMinute ===>> " + strMinute);
			
			int indexOfZero = -1;
			boolean isZero = false;
			if(strMinute!=null) {
				indexOfZero = strMinute.indexOf("0");
				if(indexOfZero==0) {
					isZero = true;
				}
			}
			
			int nHour = parseToInt(strHour);
			int nMinute = parseToInt(strMinute);
			
			if(indexOfZero<0 && nMinute<10) {
				nMinute = nMinute*10;
			}
			
			if(nMinute/60>=1) {
				nHour++;
				nMinute = nMinute-60;
			}
			//strTime = nHour+"."+((indexOfZero==0)?"0":"")+((nMinute<10 && !isZero)?"0"+nMinute:nMinute);
			strTime = nHour+"."+((indexOfZero==0)?"0":"")+((nMinute<10 && !isZero)?"0"+nMinute:((nMinute<10 && nMinute>0)?"0"+nMinute:nMinute));
			
//			System.out.println("nMinute==>"+nMinute);
			
		}
		return strTime;
	}

	
	public String getTimeDiffInHoursMins(double dblTime) {

		// int nTime = (int) dblTime;
		double nTime = dblTime;

		if (nTime > 60) {
//			return ((nTime * 1.0d / 60 < 10) ? "0" + nTime / 60 : nTime / 60) + "h" + ((nTime % 60 < 10) ? "0" + nTime % 60 : nTime % 60) + "m";

		} else {
//			return ((nTime * 1.0d < 10) ? "0" + nTime : nTime) + "m";
		}
		
		return dblTime+"hrs";
	}
 
	public String getPasswordString(String strPassword) {
		if(strPassword!=null) {
			return "******";
		}
		return "-";
	}
	
	
	public String charMappingMaleFemale(String strChar) {

		char c;
		if (strChar != null && strChar.length() > 0) {
			c = strChar.charAt(0);
		} else {
			return "-";
		}

		switch (c) {
		case 'M':
			return "Male";
		case 'F':
			return "Female";
		default:
			return "-";

		}

	}
	
	public String charMapping(String strChar) {

		char c;
		if (strChar != null && strChar.length() > 0) {
			c = strChar.charAt(0);
		} else
			return "-";

		switch (c) {
			case 'A':
				return "Amount";
			case 'D':
				return "Daily";
			case 'F':
				return "Fortnightly";
			case 'H':
				return "Hourly";
			case 'M':
				return "Monthly";	
			case 'P':
				return "Percent";
			case 'W':
				return "Weekly";
			case 'X':
				return "Fixed";
			default:
				return "-";

		}

	}

	public String stringMapping(String str) {
		if (str != null && str.equalsIgnoreCase("PT")) {
			return "Part Time";
		} else if (str != null && str.equalsIgnoreCase("FT")) {
			return "Full Time";
		} else if (str != null && str.equalsIgnoreCase("AT")) {
			return "Article";
		} else if (str != null && str.equalsIgnoreCase("ORAT")) {
			return "Outside Registered Article";
		} else if (str != null && str.equalsIgnoreCase("IHAT")) {
			return "Inhouse Article";
		} else if (str != null && str.equalsIgnoreCase("CO")) {
			return "Consultant";
		} else if (str != null && str.equalsIgnoreCase("P")) {
			return "Partner";
		} else if (str != null && str.equalsIgnoreCase("C")) {
			return "Temporary";
		} else if (str != null && str.equalsIgnoreCase("CON")) {
			return "Contractual";
		} else if (str != null && str.equalsIgnoreCase("I")) {
			return "Intern";
		} else if (str != null && str.equalsIgnoreCase("CT")) {
			return "Contract";
		} else if (str != null && str.equalsIgnoreCase("R")) {
			return "Regular";
		} else if (str != null && str.equalsIgnoreCase("PF")) {
			return "Professional";
		} else if (str != null && str.equalsIgnoreCase("ST")) {
			return "Stipend";
		} else if (str != null && str.equalsIgnoreCase("SCH")) {
			return "Scholarship";	
		} else {
			return "-";
		}
	}

	
	public String getEmploymentTypeCode(String str) {
		if (str != null && (str.equalsIgnoreCase("PT") || str.equalsIgnoreCase("Part Time"))) {
			return "PT";
		} else if (str != null && (str.equalsIgnoreCase("FT") || str.equalsIgnoreCase("Full Time"))) {
			return "FT";
		} else if (str != null && (str.equalsIgnoreCase("AT") || str.equalsIgnoreCase("Article"))) {
			return "AT";
		} else if (str != null && (str.equalsIgnoreCase("CO") || str.equalsIgnoreCase("Consultant"))) {
			return "CO";
		} else if (str != null && (str.equalsIgnoreCase("P") || str.equalsIgnoreCase("Partner"))) {
			return "P";
		} else if (str != null && (str.equalsIgnoreCase("C") || str.equalsIgnoreCase("Temporary"))) {
			return "C";
		} else if (str != null && (str.equalsIgnoreCase("CON") || str.equalsIgnoreCase("Contractual"))) {
			return "CON";
		} else if (str != null && (str.equalsIgnoreCase("I") || str.equalsIgnoreCase("Intern"))) {
			return "I";
		} else if (str != null && (str.equalsIgnoreCase("CT") || str.equalsIgnoreCase("Contract"))) {
			return "CT";
		} else if (str != null && (str.equalsIgnoreCase("R") || str.equalsIgnoreCase("Regular"))) {
			return "R";
		} else if (str != null && (str.equalsIgnoreCase("PF") || str.equalsIgnoreCase("Professional"))) {
			return "PF";
		} else if (str != null && (str.equalsIgnoreCase("ST") || str.equalsIgnoreCase("Stipend"))) {
			return "Stipend";
		} else if (str != null && (str.equalsIgnoreCase("SCH") || str.equalsIgnoreCase("Scholarship"))) {
			return "SCH";
		} else {
			return "-";
		}
	}
	
	
	public String charMappingForPerkType(String strChar) {
		char c;
		if (strChar != null && strChar.length() > 0) {
			c = strChar.charAt(0);
		} else
			return "-";

		switch (c) {
			case 'E':
				return "Earning";
			case 'D':
				return "Deduction";
			default:
				return "-";

		}

	}
	
	public String charMappingForPerkPaymentCycle(String strChar) {

		char c;
		if (strChar != null && strChar.length() > 0) {
			c = strChar.charAt(0);
		} else
			return "-";

		switch (c) {
			case 'A':
				return "Annual";
			case 'M':
				return "Monthly";	
			default:
				return "-";

		}

	}
	
	public String charMappingForAmountType(String strChar) {

		char c;
		if (strChar != null && strChar.length() > 0) {
			c = strChar.charAt(0);
		} else
			return "-";

		switch (c) {
			case '%':
				return "Percent";
			case 'A':
				return "Amount";	
			default:
				return "-";

		}

	}
	
	public String getMonthsListSeprated(String str){
		StringBuilder strMonths = new StringBuilder();
		UtilityFunctions uF = new UtilityFunctions();
		
		if(str!=null){
			str = str.replaceAll("\\[", "");
			str = str.replaceAll("\\]", "");
			
			String []arrMonth = str.split(",");
			
			for(int i=0; arrMonth!=null && i<arrMonth.length; i++){
				strMonths.append(uF.getMonth(uF.parseToInt(arrMonth[i])));
				if(i<arrMonth.length-1){
					strMonths.append(", ");
				}
			}
			
		}
		return strMonths.toString();
	}
	 
	public String formatIntoTwoDecimal(double dblVal) {
//		CommonFunctions CF = new CommonFunctions();
//		System.out.println("request ===>> " + request);
//		String strShortCurr = CF.getSettingsShortCurrency(request);
		String strShortCurr = "";
		if(strShortCurr !=null && strShortCurr.equals("$")) {
			return twoDecimal.format(dblVal);
		} else {
		    if(dblVal < 1000) {
		        return decimalFormat("###.##", dblVal);
		    } else {
		        double hundreds = dblVal % 1000;
		        int other = (int) (dblVal / 1000);
		        return decimalFormat(",##", other) + ',' + decimalFormat("000.##", hundreds);
		    }
		}
//		return twoDecimal.format(dblVal);
	}

	public String formatIntoOneDecimal(double dblVal) {
//		CommonFunctions CF = new CommonFunctions();
//		String strShortCurr = CF.getSettingsShortCurrency(request);
		String strShortCurr = "";
		if(strShortCurr !=null && strShortCurr.equals("$")) {
			return oneDecimal.format(dblVal);
		} else {
			if(dblVal < 1000) {
		        return decimalFormat("###.#", dblVal);
		    } else {
		        double hundreds = dblVal % 1000;
		        int other = (int) (dblVal / 1000);
		        return decimalFormat(",##", other) + ',' + decimalFormat("000.#", hundreds);
		    }
		}
//		return oneDecimal.format(dblVal);
	}
	
	public String formatIntoOneDecimalIfDecimalValIsThere(double dblVal) {
		String strVal = dblVal+"";
		if(strVal!=null && strVal.contains(".") && strVal.indexOf(".")>0) {
			String[] strTmp = strVal.replace(".", ",").split(",");
			if(parseToInt(strTmp[1])>0) {
				if(dblVal < 1000) {
			        return decimalFormat("###.#", dblVal);
			    } else {
			        double hundreds = dblVal % 1000;
			        int other = (int) (dblVal / 1000);
			        return decimalFormat(",##", other) + ',' + decimalFormat("000.#", hundreds);
			    }
			} else {
				return strTmp[0];
			}
		}
		return oneDecimal.format(dblVal);
	}
	
	public String formatIntoComma(double dblVal) {
//		CommonFunctions CF = new CommonFunctions();
//		String strShortCurr = CF.getSettingsShortCurrency(request);
		String strShortCurr = "";
		if(strShortCurr !=null && strShortCurr.equals("$")) {
			return zeroDecimal.format(dblVal);
		} else {
			if(dblVal < 1000) {
		        return decimalFormat("###", dblVal);
		    } else {
		        double hundreds = dblVal % 1000;
		        int other = (int) (dblVal / 1000);
		        return decimalFormat(",##", other) + ',' + decimalFormat("000", hundreds);
		    }
		}
//		return zeroDecimal.format(dblVal);
	}
	
	public String formatIntoFourDecimal(double dblVal) {
		String s = "" + dblVal;
		String[] result = s.split("\\.");
//		CommonFunctions CF = new CommonFunctions();
//		String strShortCurr = CF.getSettingsShortCurrency(request);
		String strShortCurr = "";
	    if(result[1].length()>3) {
	    	if(strShortCurr !=null && strShortCurr.equals("$")) {
	    		return fourDecimal.format(dblVal);
	    	} else {
		    	if(dblVal < 1000) {
			        return decimalFormat("###.####", dblVal);
			    } else {
			        double hundreds = dblVal % 1000;
			        int other = (int) (dblVal / 1000);
			        return decimalFormat(",##", other) + ',' + decimalFormat("000.####", hundreds);
			    }
	    	}
//	    	return fourDecimal.format(dblVal);
	    } else {
	    	if(strShortCurr !=null && strShortCurr.equals("$")) {
	    		return twoDecimal.format(dblVal);
	    	} else {
		    	if(dblVal < 1000) {
			        return decimalFormat("###.##", dblVal);
			    } else {
			        double hundreds = dblVal % 1000;
			        int other = (int) (dblVal / 1000);
			        return decimalFormat(",##", other) + ',' + decimalFormat("000.##", hundreds);
			    }
	    	}
//	    	return twoDecimal.format(dblVal);
	    }
	}
	
	public String formatIntoZeroWithOutComma(double dblVal) {
		return zeroDecimalWithOutComma.format(dblVal);
	}
	
	public String formatIntoTwoDecimalWithOutComma(double dblVal) {
		return twoDecimalWithOutComma.format(dblVal);
	}
	
	public String formatIntoFourDecimalWithOutComma(double dblVal) {
		String s = "" + dblVal;
		String[] result = s.split("\\.");
//	    System.out.println(result[0].length() + " " + result[1].length());
	    if(result[1].length()>3) {
	    	return fourDecimalWithOutComma.format(dblVal);
	    } else {
	    	return twoDecimalWithOutComma.format(dblVal);
	    }
	}
	
	public String formatIntoOneDecimalWithOutComma(double dblVal) {
		return oneDecimalWithOutComma.format(dblVal);
	}

	
	private String decimalFormat(String pattern, Object value) {
	    return new DecimalFormat(pattern).format(value);
	}
	
	
	public String showData(String str, String showValue) {

		if (str == null) {
			return showValue;
		} else if (str != null && str.trim().equalsIgnoreCase("NULL")) {
			return showValue;
		} else if (str != null && str.trim().equalsIgnoreCase("N/A")) {
			return showValue;
		} else if (str != null && str.trim().equalsIgnoreCase("NaN")) {
			return showValue;
		} else if (str != null && str.trim().equalsIgnoreCase("")) {
			return showValue;
		} else if (str != null && str.trim().equalsIgnoreCase("-")) {
			return showValue;
		} else if (str != null && str.trim().equalsIgnoreCase("0")) {
			return showValue;
		} else {
			return str;
		}
	}

	public String removeNull(String str) {
		if (str != null)
			return str;
		else
			return "";
	}

	public double convertHoursIntoMinutes(double val) {

		String str = val + "";
		int hours = 0;
		int minutes = 0;

		String[] arr = str.split("\\.");
		if (arr != null && arr.length > 1) {
			hours = parseToInt(arr[0]);
			minutes = parseToInt(((arr[1].length() == 2) ? arr[1] : arr[1] + "0"));
		}

		// return (hours * 60 + minutes)/60d;
		return val;

	}
	
	public double convertHoursIntoMinutes1(double val) {

		String str = val + "";
		int hours = 0;
		int minutes = 0;

		String[] arr = str.split("\\.");
		if (arr != null && arr.length > 1) {
			hours = parseToInt(arr[0]);
			minutes = parseToInt(((arr[1].length() == 2) ? arr[1] : arr[1] + "0"));
			
			 if(hours < 0){
			    minutes = -minutes;
			}
		}

		 return ((hours * 60) + minutes); 
		//return val;

	}

	public double convertMinutesIntoHours(double val) {
		double dblHrs = 0.0d;
		try {			
			double hours = val/60d;
			double min = 0.0d;
			
			String strTotal= ""+formatIntoTwoDecimalWithOutComma(hours);
			if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
				String str = strTotal.replace(".", ":");
				String[] tempTotal = str.split(":");
				double dblHr = parseToDouble(tempTotal[1]);
				if(dblHr > 0){
					min = (dblHr * 60d)/100;
				}
			}
			String strHours = ((int)hours)+"."+(zero((int)min));
			dblHrs = parseToDouble(strHours);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return dblHrs;
	}
	
	
	public String showTimeFormat(String strValue) {

		if (strValue != null) {
			return strValue;
		} else {
//			return "00:00AM";
			return NO_TIME_RECORD;
		}
	}

	public String showYesNo(String strValue) {

		if (strValue != null && (strValue.trim().equalsIgnoreCase("TRUE") || strValue.trim().equalsIgnoreCase("T") || strValue.trim().equalsIgnoreCase("1"))) {
			return "Yes";
		} else if (strValue != null && (strValue.trim().equalsIgnoreCase("FALSE") || strValue.trim().equalsIgnoreCase("F") || strValue.trim().equalsIgnoreCase("0"))) {
			return "No";
		} else {
			return "No";
		}
	}

	public String showRoundOffTime(String strValue, int RoundOff, CommonFunctions CF) {

		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));

		cal.set(Calendar.YEAR, parseToInt(getDateFormat(strValue, CF.getStrReportDateFormat() + CF.getStrReportTimeFormat(), "yyyyy")));
		cal.set(Calendar.MONTH, parseToInt(getDateFormat(strValue, CF.getStrReportDateFormat() + CF.getStrReportTimeFormat(), "M")));
		cal.set(Calendar.DAY_OF_MONTH, parseToInt(getDateFormat(strValue, CF.getStrReportDateFormat() + CF.getStrReportTimeFormat(), "d")));
		cal.set(Calendar.HOUR_OF_DAY, parseToInt(getDateFormat(strValue, CF.getStrReportDateFormat() + CF.getStrReportTimeFormat(), "H")));
		cal.set(Calendar.MINUTE, parseToInt(getDateFormat(strValue, CF.getStrReportDateFormat() + CF.getStrReportTimeFormat(), "m")));

		int YEAR = cal.get(Calendar.YEAR);
		int MONTH = cal.get(Calendar.MONTH);
		int DAY = cal.get(Calendar.DAY_OF_MONTH);

		int HOUR_A = cal.get(Calendar.HOUR_OF_DAY);
		int MINUTE_A = cal.get(Calendar.MINUTE);
		int MINUTE = cal.get(Calendar.MINUTE);
		int HOUR = cal.get(Calendar.HOUR_OF_DAY);

		int mode = MINUTE % RoundOff;
		MINUTE = RoundOff - mode;
		cal.add(Calendar.MINUTE, MINUTE);

		HOUR = cal.get(Calendar.HOUR_OF_DAY);
		MINUTE = cal.get(Calendar.MINUTE);

		return getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", "yyyy-MM-dd") + "" + getTimeFormat(HOUR + ":" + MINUTE, "hh:mm");

	}

	public boolean isNumber(String str) {
		try {

			Double.parseDouble(str);
			return true;

		} catch (Exception e) {
			return false;
		}

	}

	public boolean containsOnlyNumbers1(String str) {

		// It can't contain only numbers if it's null or empty...
		if (str == null || str.length() == 0)
			return false;

		for (int i = 0; i < str.length(); i++) {

			// If we find a non-digit character we return false.
			if (!Character.isDigit(str.charAt(i)))
				return false;
		}

		return true;
	}

	public String limitContent(String str, int limit) {

		if (str != null && str.length() > limit && limit > 0) {
			return str.substring(0, limit) + "...";
		} else if (str != null) {
			return str;
		} else {
			return "";
		}
	}

	public String strEncoding(String str) {

		if (str != null && str.length() > 0) {
			str = str.replace("'", "\\'");
			return str;
		} else {
			return str;
		} 
	}
	
	public String strDecoding(String str) {

		if (str != null && str.length() > 0) {
			str = str.replace("\\'", "'");
			return str;
		} else {
			return str;
		} 
	}     
	
	public String dateDifference(String strStartDate,String strStartDateFormat, String strEndDate, String strEndDateFormat) {
		long diffInDays=0;
		try {
			java.util.Date lastDate=null;
			java.util.Date startDate=null;
			
			lastDate = getDateFormatUtil(strEndDate, strEndDateFormat);
			startDate = getDateFormatUtil(strStartDate, strStartDateFormat);
			
			if(lastDate==null){
				lastDate = startDate;
			}
			
			if(lastDate!=null && startDate!=null){
				diffInDays = (lastDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
				diffInDays = diffInDays+1;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return diffInDays+"";
	}
	
	public String dateDifference(String strStartDate,String strStartDateFormat, String strEndDate, String strEndDateFormat,String strTimeZone) {
		long diffInDays=0;
		try {
			java.util.Date lastDate=null;
			java.util.Date startDate=null;
			
			lastDate = getDateFormatUtil(strEndDate, strEndDateFormat, strTimeZone);
			startDate = getDateFormatUtil(strStartDate, strStartDateFormat, strTimeZone);
			
			if(lastDate==null){
				lastDate = startDate;
			}
			
			if(lastDate!=null && startDate!=null){
				diffInDays = (lastDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
				diffInDays = diffInDays+1;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return diffInDays+"";
	}
	
	public int getYear(){
		  Calendar cal=Calendar.getInstance();
		  int year=cal.get(Calendar.YEAR);
		  return year;
	}
	
	public boolean isDateBetween(Date startDate, Date endDate, Date betweenDate) {
		
		if(startDate!=null && endDate!=null && betweenDate!=null)		
			return (betweenDate.after(startDate) && betweenDate.before(endDate)) || betweenDate.equals(startDate) || betweenDate.equals(endDate);
		else
			return false;
	
	}
	
	public String getMonth(int nMonth){
		String strMonth = null;
		switch(nMonth){
			case 1:
				strMonth = "January";
				break;
			case 2:
				strMonth = "February";
				break;
			case 3:
				strMonth = "March";
				break;
			case 4:
				strMonth = "April";
				break;
			case 5:
				strMonth = "May";
				break;
			case 6:
				strMonth = "June";
				break;
			case 7:
				strMonth = "July";
				break;
			case 8:
				strMonth = "August";
				break;
			case 9:
				strMonth = "September";
				break;
			case 10:
				strMonth = "October";
				break;
			case 11:
				strMonth = "November";
				break;
			case 12:
				strMonth = "December";
				break;
			case 0:
				strMonth = "Month not defined";
				break;
		}
		return strMonth; 
		
	}
	
	public String getShortMonth(int nMonth){   
		String strMonth = null;
		switch(nMonth){
			case 1:
				strMonth = "Jan";
				break;
			case 2:
				strMonth = "Feb";
				break;
			case 3:
				strMonth = "Mar";
				break;
			case 4:
				strMonth = "Apr";
				break;
			case 5:
				strMonth = "May";
				break;
			case 6:
				strMonth = "Jun";
				break;
			case 7:
				strMonth = "Jul";
				break;
			case 8:
				strMonth = "Aug";
				break;
			case 9:
				strMonth = "Sep";
				break;
			case 10:
				strMonth = "Oct";
				break;
			case 11:
				strMonth = "Nov";
				break;
			case 12:
				strMonth = "Dec";
				break;
		}
		return strMonth; 
		
	}

	public String uploadFile(HttpServletRequest request, String strFolderWithLocation, File fileWithLocation){
		String fileName1 = "";
		
		try {
			
			String filePath1 = request.getRealPath(strFolderWithLocation);
			int random1 = new Random().nextInt();
			fileName1 = random1 + fileWithLocation.getName();
			File fileToCreate = new File(filePath1, fileName1);
			FileUtils.copyFile(fileWithLocation, fileToCreate);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName1;
	}
	
	
	public String uploadFile(HttpServletRequest request,String strFolderWithLocation,File file,String fileFileName) throws Exception {
		 double randomname=Math.random();
		 String random=randomname+"";
		 random=random.replace("0.", "");
		 if(fileFileName!=null && fileFileName.contains(" ")){
			 fileFileName=fileFileName.replace(" ", "");
		 }
		// the directory to upload to
		String uploadDir = ServletActionContext.getServletContext().getRealPath("/"+strFolderWithLocation) + "/";
		
//		.getRealPath(strFolderWithLocation) ;

		// write the file to the file specified
		File dirPath = new File(uploadDir);

		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
		
		// retrieve the file data
		InputStream stream = new FileInputStream(file);

		// write the file to the file specified
		OutputStream bos = new FileOutputStream(uploadDir + random+fileFileName);
		int bytesRead;
		byte[] buffer = new byte[8192];

		while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}

		bos.close();
		stream.close();

		// place the data into the request for retrieval on next page
		request.setAttribute("location",
				dirPath.getAbsolutePath() + "/" + fileFileName);
//		log.debug("location" + dirPath.getAbsolutePath() + "/" + fileFileName);

		String link = request.getContextPath() + "/"+strFolderWithLocation + "/";

		request.setAttribute("link", link + fileFileName);
//		log.debug("link" + link + fileFileName);

		return random+ fileFileName;
	}
	
	
	public String uploadFile(HttpServletRequest request,String strFolderWithLocation,File file,String fileFileName, String strFileName) throws Exception {
		 if(fileFileName!=null && fileFileName.contains(" ")){
			 fileFileName=fileFileName.replace(" ", "");
		 }

		 // the directory to upload to
		String uploadDir = ServletActionContext.getServletContext().getRealPath("/"+strFolderWithLocation) + "/";

		// write the file to the file specified
		File dirPath = new File(uploadDir);

		if (!dirPath.exists()) {
			dirPath.mkdirs(); 
		}

		
		// retrieve the file data
		InputStream stream = new FileInputStream(file);

		// write the file to the file specified
		OutputStream bos = new FileOutputStream(uploadDir + strFileName);
		int bytesRead;
		byte[] buffer = new byte[8192];

		while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}

		bos.close();
		stream.close();

		// place the data into the request for retrieval on next page
		request.setAttribute("location", dirPath.getAbsolutePath() + "/" + strFileName);
//		log.debug("location" + dirPath.getAbsolutePath() + "/" + fileFileName);

		String link = request.getContextPath() + "/"+strFolderWithLocation + "/";

		request.setAttribute("link", link + strFileName);
//		log.debug("link" + link + fileFileName);

		return strFileName;
	}
	
	
	
	public String uploadFile(HttpServletRequest request,String strFolderWithLocation,File file,String fileFileName, boolean isRemoteLocation, CommonFunctions CF) throws Exception {
		 double randomname=Math.random();
		 String random=randomname+"";
		 random=random.replace("0.", "");
		 if(fileFileName!=null && fileFileName.contains(" ")){
			 fileFileName=fileFileName.replace(" ", "");
		 }
		// the directory to upload to
		String uploadDir = ServletActionContext.getServletContext().getRealPath("/" + strFolderWithLocation) + "/";   
		System.out.println("before uploadDir======>"+uploadDir);
		if(isRemoteLocation){
			uploadDir = strFolderWithLocation;	
		}
		System.out.println("after uploadDir======>"+uploadDir);
		
//		.getRealPath(strFolderWithLocation) ;

		// write the file to the file specified
		File dirPath = new File(uploadDir);

		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}

		
		// retrieve the file data
		InputStream stream = new FileInputStream(file);

		// write the file to the file specified
		OutputStream bos = new FileOutputStream(uploadDir + random+fileFileName);
		int bytesRead;
		byte[] buffer = new byte[8192];

		while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}

		bos.close();
		stream.close();

		// place the data into the request for retrieval on next page
		request.setAttribute("location", dirPath.getAbsolutePath() + "/" + fileFileName);
//		log.debug("location" + dirPath.getAbsolutePath() + "/" + fileFileName);

		String link = request.getContextPath() + "/"+strFolderWithLocation + "/";

		request.setAttribute("link", link + fileFileName);
//		log.debug("link" + link + fileFileName);

		return random+ fileFileName;
	}
	
	
	public String uploadFile(HttpServletRequest request,String strFolderWithLocation,File file,String fileFileName, String strFileName, boolean isRemoteLocation, CommonFunctions CF) throws Exception {
		 if(fileFileName!=null && fileFileName.contains(" ")){
			 fileFileName=fileFileName.replace(" ", "");
		 }

		 // the directory to upload to
		String uploadDir = ServletActionContext.getServletContext().getRealPath("/"+strFolderWithLocation) + "/";

		if(isRemoteLocation){
			// To upload the file on a different location 
			uploadDir = strFolderWithLocation;

		}
		
		
		// write the file to the file specified
		File dirPath = new File(uploadDir);

		if (!dirPath.exists()) {
			dirPath.mkdirs(); 
		}

		
		// retrieve the file data
		InputStream stream = new FileInputStream(file);

		// write the file to the file specified
		OutputStream bos = new FileOutputStream(uploadDir + strFileName);
		int bytesRead;
		byte[] buffer = new byte[8192];

		while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}

		bos.close();
		stream.close();

		// place the data into the request for retrieval on next page
		request.setAttribute("location",dirPath.getAbsolutePath() + "/" + strFileName);
//		log.debug("location" + dirPath.getAbsolutePath() + "/" + fileFileName);

		String link = request.getContextPath() + "/"+strFolderWithLocation + "/";

		request.setAttribute("link", link + strFileName);
//		log.debug("link" + link + fileFileName);

		return strFileName;
	}
	
	public void getTimeDuration(String strJoiningDate, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request){
		
		
		try {
			
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strJoiningDate, DBDATE, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strJoiningDate, DBDATE, "MM")), 
					uF.parseToInt(uF.getDateFormat(strJoiningDate, DBDATE, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM")), 
					uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			StringBuilder sbTimeDuration = new StringBuilder();
			
			
			if(period.getYears()>0 && period.getYears()==1){
				sbTimeDuration.append("<span>"+period.getYears()+"</span> year ");
			}else if(period.getYears()>0){
				sbTimeDuration.append("<span>"+period.getYears()+"</span> years ");
			}
			if(period.getYears()>0 && period.getMonths()>0){
				sbTimeDuration.append("and ");
			}
			
			if(period.getMonths()>0 && period.getMonths()==1){
				sbTimeDuration.append("<span>"+period.getMonths()+"</span> month ");
			}else if(period.getMonths()>0){
				sbTimeDuration.append("<span>"+period.getMonths()+"</span> months ");
			}
			
			if(period.getDays()>0 && period.getMonths()>0){
				sbTimeDuration.append("and ");
			}
			
			if(period.getDays()>0 && period.getDays()==1){
				sbTimeDuration.append("<span>"+period.getDays()+"</span> day ");
			}else if(period.getDays()>0){
				sbTimeDuration.append("<span>"+period.getDays()+"</span> days ");
			}
			
			if(sbTimeDuration.toString()!=null && !sbTimeDuration.toString().equalsIgnoreCase("NULL")){
				request.setAttribute("TIME_DURATION", sbTimeDuration.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
public String getTimeDurationWithNoSpan(String strJoiningDate, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request){
		
	StringBuilder sbTimeDuration = new StringBuilder();
		try {
			
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strJoiningDate, DBDATE, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strJoiningDate, DBDATE, "MM")), 
					uF.parseToInt(uF.getDateFormat(strJoiningDate, DBDATE, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM")), 
					uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
			
			if(period.getYears()>0 && period.getYears()==1){
				sbTimeDuration.append(period.getYears()+" year ");
			}else if(period.getYears()>0){
				sbTimeDuration.append(period.getYears()+" years ");
			}
			if(period.getYears()>0 && period.getMonths()>0){
				sbTimeDuration.append("and ");
			}
			
			if(period.getMonths()>0 && period.getMonths()==1){
				sbTimeDuration.append(period.getMonths()+" month ");
			}else if(period.getMonths()>0){
				sbTimeDuration.append(period.getMonths()+" months ");
			}
			
			if(period.getDays()>0 && period.getMonths()>0){
				sbTimeDuration.append("and ");
			}
			
			if(period.getDays()>0 && period.getDays()==1){
				sbTimeDuration.append(period.getDays()+" day ");
			}else if(period.getDays()>0){
				sbTimeDuration.append(period.getDays()+" days ");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbTimeDuration.toString();
	} 

public String getTimeDurationWithNoSpan(String strStartDate,String strEndDate, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request){
	
	StringBuilder sbTimeDuration = new StringBuilder();
		try {
			
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, DBDATE, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, DBDATE, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, DBDATE, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, DBDATE, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, DBDATE, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, DBDATE, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
			
			if(period.getYears()>0 && period.getYears()==1){
				sbTimeDuration.append(period.getYears()+" year ");
			}else if(period.getYears()>0){
				sbTimeDuration.append(period.getYears()+" years ");
			}
			if(period.getYears()>0 && period.getMonths()>0){
				sbTimeDuration.append("and ");
			}
			
			if(period.getMonths()>0 && period.getMonths()==1){
				sbTimeDuration.append(period.getMonths()+" month ");
			}else if(period.getMonths()>0){
				sbTimeDuration.append(period.getMonths()+" months ");
			}
			
			if(period.getDays()>0 && period.getMonths()>0){
				sbTimeDuration.append("and ");
			}
			
			if(period.getDays()>0 && period.getDays()==1){
				sbTimeDuration.append(period.getDays()+" day ");
			}else if(period.getDays()>0){
				sbTimeDuration.append(period.getDays()+" days ");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbTimeDuration.toString();
	} 
	
	public String getTimeDurationBetweenDates(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request){
		
		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
			if(period.getYears()>0 && period.getYears()==1){
				sbTimeDuration.append("<span>"+period.getYears()+"</span> year ");
			}else if(period.getYears()>0){
				sbTimeDuration.append("<span>"+period.getYears()+"</span> years ");
			}
			if(period.getDays()==0){
				sbTimeDuration.append("and ");
			}
			
			if(period.getMonths()>0 && period.getMonths()==1){
				sbTimeDuration.append("<span>"+period.getMonths()+"</span> month ");
			}else if(period.getMonths()>0){
				sbTimeDuration.append("<span>"+period.getMonths()+"</span> months ");
			}
			
			if(period.getMonths()>0){
				sbTimeDuration.append("and ");
			}
			if(period.getDays()>0 && period.getDays()==1){
				sbTimeDuration.append("<span>"+period.getDays()+"</span> day ");
			}else if(period.getDays()>0){
				sbTimeDuration.append("<span>"+period.getDays()+"</span> days ");
			}
			
			/*if(sbTimeDuration.toString()!=null && !sbTimeDuration.toString().equalsIgnoreCase("NULL")){
				request.setAttribute("TIME_DURATION", );
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		
		return sbTimeDuration.toString();
	}
	
	
	public String getTimeDurationBetweenDates(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request, boolean isYear, boolean isMonth, boolean isDays){
		
		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
		    if(isYear){
		    	if(period.getYears()>0 && period.getYears()==1){
					sbTimeDuration.append("<span>"+period.getYears()+"</span> year ");
				}else if(period.getYears()>0){
					sbTimeDuration.append("<span>"+period.getYears()+"</span> years ");
				}
				if(period.getDays()==0){
					sbTimeDuration.append("and ");
				}
		    }
		    
			
		    if(isMonth){
		    	if(period.getMonths()>0 && period.getMonths()==1){
					sbTimeDuration.append("<span>"+period.getMonths()+"</span> month ");
				}else if(period.getMonths()>0){
					sbTimeDuration.append("<span>"+period.getMonths()+"</span> months ");
				}
		    }
			
			if(isDays){
				if(period.getDays()>0 && period.getDays()==1){
					sbTimeDuration.append("and <span>"+period.getDays()+"</span> day ");
				}else if(period.getDays()>0){
					sbTimeDuration.append("and <span>"+period.getDays()+"</span> days ");
				}
			}
			
			/*if(sbTimeDuration.toString()!=null && !sbTimeDuration.toString().equalsIgnoreCase("NULL")){
				request.setAttribute("TIME_DURATION", );
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		
		return sbTimeDuration.toString();
	}

	
//	public boolean getEmpFilledStatus(String strEmpId) {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		UtilityFunctions uF = new UtilityFunctions();
//		boolean isFilledStatus = true;
//		
//		try {
//			
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select emp_filled_flag from employee_personal_details WHERE emp_per_id = ?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			ResultSet rs = pst.executeQuery();
//			
//			while(rs.next()){
//				isFilledStatus = rs.getBoolean("emp_filled_flag");
//			}
//			
//			rs.close();
//			pst.close();
//			
//		} catch (Exception e) {
//				e.printStackTrace();
//		}finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//		}
//		return isFilledStatus;
//	}
	
	
	public int getWeekEndCount(Map hmWeekEndList, String strWLocationId, String strD1, String strD2){
		
		int weekends = 0;
		
		if(hmWeekEndList==null)hmWeekEndList = new HashMap();
		
		java.util.Date dtD1 = getDateFormatUtil(strD1, DATE_FORMAT);
		java.util.Date dtD2 = getDateFormatUtil(strD2, DATE_FORMAT);
		
		Calendar calendar1=Calendar.getInstance();
        calendar1.setTime(dtD1);
        
        Calendar calendar2=Calendar.getInstance();
        calendar2.setTime(dtD2);
        
        while(calendar1!=null && calendar2!=null && (calendar1.before(calendar2) || calendar1.equals(calendar2))){
        	if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY  && hmWeekEndList.containsKey(SUNDAY+"_"+strWLocationId) ){
	        	weekends++;
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY  && hmWeekEndList.containsKey(MONDAY+"_"+strWLocationId)){
	        	weekends++;
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY  && hmWeekEndList.containsKey(TUESDAY+"_"+strWLocationId)){
	        	weekends++;
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY  && hmWeekEndList.containsKey(WEDNESDAY+"_"+strWLocationId)){
	        	weekends++;
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY  && hmWeekEndList.containsKey(THURSDAY+"_"+strWLocationId)){
	        	weekends++;
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY  && hmWeekEndList.containsKey(FRIDAY+"_"+strWLocationId)){
	        	weekends++;
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY  && hmWeekEndList.containsKey(SATURDAY+"_"+strWLocationId)){
	        	weekends++;
        	}
        	calendar1.add(Calendar.DATE,1);
        }
       	return weekends;
	}
	
	public int getWeekEndDateCount(Map hmWeekEndList, String strWLocationId, String strD1, String strD2){
		
		int weekends = 0;
		
		if(hmWeekEndList==null)hmWeekEndList = new HashMap();
		
		java.util.Date dtD1 = getDateFormatUtil(strD1, DATE_FORMAT);
		java.util.Date dtD2 = getDateFormatUtil(strD2, DATE_FORMAT);
		
		        
        int nNoOfDays = parseToInt(dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT));
        
        Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DATE, parseToInt(getDateFormat(strD1, DATE_FORMAT, "dd")));
		cal.set(Calendar.MONTH, parseToInt(getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
		cal.set(Calendar.YEAR, parseToInt(getDateFormat(strD1, DATE_FORMAT, "yyyy")));
        
		for(int i=0; i<nNoOfDays; i++){
        	
    		String strDate = cal.get(Calendar.DATE)+"/"+
    				(cal.get(Calendar.MONTH) + 1)+"/" + cal.get(Calendar.YEAR);
    		
    		
    		if(hmWeekEndList!=null && hmWeekEndList.containsKey(getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT)+"_"+strWLocationId)){
    			weekends++;
    			
//    			System.out.println(weekends+" Weekend= "+strDate);
    		}
    		cal.add(Calendar.DATE, 1);
        }
       	return weekends;
	}
	
	
	public int getWeekEndCount(Map hmWeekEndList, Map hmWeekEndListDates,UtilityFunctions uF, String strWLocationId, String strD1, String strD2){
		
		int weekends = 0;
		
		if(hmWeekEndList==null)hmWeekEndList = new HashMap();
		
		java.util.Date dtD1 = getDateFormatUtil(strD1, DATE_FORMAT);
		java.util.Date dtD2 = getDateFormatUtil(strD2, DATE_FORMAT);
		
		Calendar calendar1=Calendar.getInstance();
        calendar1.setTime(dtD1);
        
        Calendar calendar2=Calendar.getInstance();
        calendar2.setTime(dtD2);
        
        while(calendar1!=null && calendar2!=null && (calendar1.before(calendar2) || calendar1.equals(calendar2))){
        	if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY  && hmWeekEndList.containsKey(SUNDAY+"_"+strWLocationId) ){
	        	weekends++;
	        	hmWeekEndListDates.put(uF.getDateFormat(calendar1.get(Calendar.DATE)+"/"+(calendar1.get(Calendar.MONTH)+1)+"/"+calendar1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT), SUNDAY);
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY  && hmWeekEndList.containsKey(MONDAY+"_"+strWLocationId)){
	        	weekends++;
	        	hmWeekEndListDates.put(uF.getDateFormat(calendar1.get(Calendar.DATE)+"/"+(calendar1.get(Calendar.MONTH)+1)+"/"+calendar1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT), MONDAY);
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY  && hmWeekEndList.containsKey(TUESDAY+"_"+strWLocationId)){
	        	weekends++;
	        	hmWeekEndListDates.put(uF.getDateFormat(calendar1.get(Calendar.DATE)+"/"+(calendar1.get(Calendar.MONTH)+1)+"/"+calendar1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT), TUESDAY);
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY  && hmWeekEndList.containsKey(WEDNESDAY+"_"+strWLocationId)){
	        	weekends++;
	        	hmWeekEndListDates.put(uF.getDateFormat(calendar1.get(Calendar.DATE)+"/"+(calendar1.get(Calendar.MONTH)+1)+"/"+calendar1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT), WEDNESDAY);
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY  && hmWeekEndList.containsKey(THURSDAY+"_"+strWLocationId)){
	        	weekends++;
	        	hmWeekEndListDates.put(uF.getDateFormat(calendar1.get(Calendar.DATE)+"/"+(calendar1.get(Calendar.MONTH)+1)+"/"+calendar1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT), THURSDAY);
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY  && hmWeekEndList.containsKey(FRIDAY+"_"+strWLocationId)){
	        	weekends++;
	        	hmWeekEndListDates.put(uF.getDateFormat(calendar1.get(Calendar.DATE)+"/"+(calendar1.get(Calendar.MONTH)+1)+"/"+calendar1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT), FRIDAY);
        	}else if(calendar1.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY  && hmWeekEndList.containsKey(SATURDAY+"_"+strWLocationId)){
	        	weekends++;
	        	hmWeekEndListDates.put(uF.getDateFormat(calendar1.get(Calendar.DATE)+"/"+(calendar1.get(Calendar.MONTH)+1)+"/"+calendar1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT), SATURDAY);
        	}
        	calendar1.add(Calendar.DATE,1);
        }
       	return weekends;
	}
	

//	public double getEMI(double dblP, double dblROI, double dblDuration){
//		
//		double dblTotalAmount = 0;
//		dblTotalAmount = (dblP * Math.pow((1 + ((dblROI/100)/12)), dblDuration/12)); 
//		return dblTotalAmount;
//	} 
	
	public double getEMI(double dblP, double dblROI, double dblDuration){
		
		double dblTotalAmount = 0;
		dblTotalAmount = (dblP * (1 + ((dblROI/100)*(((int)dblDuration)/12)))); 
		return dblTotalAmount;
	} 
	
	
	public String digitsToWords(int nNumber){
		
		int nlen, q=0, r=0;
	    String strltr = " ";
	    String strAmountString = "";
	    
		try{
		    
		    if (nNumber <= 0) log.debug("Zero or Negative number not for conversion");
		    while (nNumber>0){
		    	nlen = numberCount(nNumber);
		       //Take the length of the number and do letter conversion
		       switch (nlen){
		       
		            case 8:
		                    q=nNumber/10000000;
		                    r=nNumber%10000000;
		                    strltr = twonum(q);
		                    strAmountString = strAmountString+strltr+arrDigit[4];
		                    nNumber = r;
		                    break;
		            case 7:
		            case 6:
		                    q=nNumber/100000;
		                    r=nNumber%100000;
		                    strltr = twonum(q);
		                    strAmountString = strAmountString+strltr+arrDigit[3];
		                    nNumber = r;
		                    break;
		
		            case 5:
		            case 4:
		                     q=nNumber/1000;
		                     r=nNumber%1000;
		                     strltr = twonum(q);
		                     strAmountString= strAmountString+strltr+arrDigit[2];
		                     nNumber = r;
		                     break;
		            case 3:
		                      if (nlen == 3)
		                          r = nNumber;
		                      strltr = threenum(r);
		                      strAmountString = strAmountString + strltr;
		                      nNumber = 0;
		                      break;
		            case 2:
		            	strltr = twonum(nNumber);
		            	strAmountString = strAmountString + strltr;
		            	nNumber=0;
		                     break;
		
		            case 1:
		            	strAmountString = strAmountString + arrUnitdo[nNumber];
		            	nNumber=0;
		                     break;
		            default:
		            	nNumber=0;
		                    break;
		        }
	            if (nNumber==0)	                    	                    	
	            	strAmountString.concat(strAmountString);
		      }
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		return strAmountString;
	
	}
	
	String[] arrUnitdo ={"", " One", " Two", " Three", " Four", " Five"," Six", " Seven", " Eight", " Nine", " Ten", " Eleven", " Twelve"," Thirteen", " Fourteen", " Fifteen",  " Sixteen", " Seventeen", " Eighteen", " Nineteen"};
    String[] arrTens =  {"", "Ten", " Twenty", " Thirty", " Forty", " Fifty"," Sixty", " Seventy", " Eighty"," Ninety"};
    String[] arrDigit = {"", " Hundred", " Thousand", " Lakh", " Crore"};
    int nseprate;
	
	int numberCount(int num){
	    int cnt=0;
	    while (num>0){
	      nseprate = num%10;
	      cnt++;
	      num = num / 10;
	    }
	      return cnt;
	}

	String twonum(int numq){
	     int numr, nq;
	     String ltr="";

	     nq = numq / 10;
	     numr = numq % 10;
	     if (numq>19){
	    	 ltr=ltr+arrTens[nq]+arrUnitdo[numr];
	     }else{
	    	 ltr = ltr+arrUnitdo[numq];
	     }
	     return ltr;
	}

	String threenum(int numq){
	       int numr, nq;
	       String ltr = "";
	       nq = numq / 100;
	       numr = numq % 100;
	       if (numr == 0){
	    	   ltr = ltr + arrUnitdo[nq]+arrDigit[1];
	       }else{
	    	   ltr = ltr +arrUnitdo[nq]+arrDigit[1]+" and "+twonum(numr);
	       }
	       return ltr;
	}
	
	
	public String parseToHTML(String strContent){
		if(strContent!=null){
			strContent = strContent.replace("\n", "<br/>");
		}
		return strContent;
	}
	
	public boolean isInteger(String s) 
    {
       boolean isInteger = true;
       for(int i=0;i<s.length() && isInteger; i++) 
       {
         char c = s.charAt(i);
         isInteger = isInteger & ((c>='0' && c<='9'));
       }
      return isInteger;
   }
	
	public String getAmountInCrAndLksFormat( Double dblAmount) {
		StringBuilder finalStrAmount = new StringBuilder();
		try {
			
			if(dblAmount >= 100000000){
				double dblBlns = dblAmount / 100000000;
				finalStrAmount.append(formatIntoTwoDecimalWithOutComma(dblBlns)+" B");
			}else if(dblAmount >= 1000000){
				double dblMln = dblAmount / 1000000;
				finalStrAmount.append(formatIntoTwoDecimalWithOutComma(dblMln)+" M");
			}else{
				finalStrAmount.append(formatIntoTwoDecimalWithOutComma(dblAmount)+""); 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		return finalStrAmount.toString();
	}
	public String getLoadingWeekDayCode(String str) {
		if (str != null) {
			if (str.equalsIgnoreCase("SUNDAY")) {
				return "SUN_LOADING";
			} else if (str.equalsIgnoreCase("MONDAY")) {
				return "MON_LOADING";
			} else if (str.equalsIgnoreCase("TUESDAY")) {
				return "TUE_LOADING";
			} else if (str.equalsIgnoreCase("WEDNESDAY")) {
				return "WED_LOADING";
			} else if (str.equalsIgnoreCase("THURSDAY")) {
				return "THURS_LOADING";
			} else if (str.equalsIgnoreCase("FRIDAY")) {
				return "FRI_LOADING";
			} else if (str.equalsIgnoreCase("SATURDAY")) {
				return "SAT_LOADING";
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	public String getDay(int day ){
		 switch(day){
		  case 1: return SUNDAY;
//		  break;
		  case 2: return MONDAY;
//		  break;
		  case 3: return TUESDAY;
//		  break;
		  case 4: return WEDNESDAY;
//		  break;
		  case 5:  return THURSDAY ;
//		  break;
		  case 6:  return FRIDAY;
//		  break;
		  default: return SATURDAY;
//		  break;
		  }
//		  System.out.print(".");
		  
	}
	
	public String getPaymentMode(String ptype) {
		String pMode = "";
		if (ptype != null && ptype.equals("1")) {
			pMode = "Bank Transfer";
		} else if (ptype != null && ptype.equals("2")) {
			pMode = "Cash";
		} else if (ptype != null && ptype.equals("3")) {
			pMode = "Cheque";
		}
		return pMode;
	}

	public String getGender(String string) {
		String gender = "-";
		if (string != null && string.equals("M")) {
			gender = "Male";
		} else if (string != null && string.equals("F")) {
			gender = "Female";
		} else if (string != null && string.equals("O")) {
			gender = "Other";
		}
		return gender;
	}

	
	public String getGenderCode(String string) {
		String gender = "-";
		if (string != null && (string.equalsIgnoreCase("M") || string.equalsIgnoreCase("Male"))) {
			gender = "M";
		} else if (string != null && (string.equalsIgnoreCase("F") || string.equalsIgnoreCase("Female"))) {
			gender = "F";
		} else if (string != null && (string.equalsIgnoreCase("O") || string.equalsIgnoreCase("Other"))) {
			gender = "O";
		} else if (string != null && (string.equalsIgnoreCase("0") || string.equalsIgnoreCase("Any"))) {
			gender = "0";
		}
		return gender;
	}
	
	
	public String getMaritalStatus(String string) {
		String maritalStatus = "-";
		if (string != null && string.equals("M")) {
			maritalStatus = "Married";
		} else if (string != null && string.equals("U")) {
			maritalStatus = "Unmarried";
		}else if(string != null && string.equals("D")){
			maritalStatus = "Divorced";
		}else if(string != null && string.equals("W")){
			maritalStatus = "Widow";
		}
		
		return maritalStatus;
	}

	public String getDigitPosition(int day) {
		switch(day){
			  case 1: return day+"st";
			  case 2: return day+"nd";
			  case 3: return day+"rd";
			  case 4: return day+"th";
			  case 5: return day+"th";
			  case 6: return day+"th";
			  case 7: return day+"th";
			  case 8: return day+"th";
			  case 9: return day+"th";
			  case 10: return day+"th";
			  case 11: return day+"th";
			  case 12: return day+"th";
			  case 13: return day+"th";
			  case 14: return day+"th";
			  case 15: return day+"th";
			  case 16: return day+"th";
			  case 17: return day+"th";
			  case 18: return day+"th";
			  case 19: return day+"th";
			  case 20: return day+"th";
		}
		return null;
	}

	
	public int getMonthsDifference(Date startDate, Date endDate) {
		if(startDate != null && endDate != null) {
		    int month1 = startDate.getYear() * 12 + startDate.getMonth();
		    int month2 = endDate.getYear() * 12 + endDate.getMonth();
		    return month2 - month1 + 1;
		} else {
			return 0;
		}
	}


	public String getConcateData(String[] data) {
		StringBuilder sb=new StringBuilder();
		
		for(int i=0;i<data.length;i++){
			if(i==0){
				sb.append("'"+data[i]+"'");
			}else{
				sb.append(",'"+data[i]+"'");
			}
		}
		return sb.toString();
	}
	
	public boolean isThisDateValid(String dateToValidate, String dateFromat){
		 
		if(dateToValidate == null  || dateToValidate.trim().equals("") || dateToValidate.trim().length()==0 || dateToValidate.equals("null") || dateToValidate.equals("-")){
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);
		try {
			Date date = sdf.parse(dateToValidate);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
 
		return true;
	}
	
	public boolean isThisDatePatternMatch(String dateToValidate){
		 boolean isDate = false;
	     String datePattern = "\\d{1,2}/\\d{1,2}/\\d{4}";
	     isDate = dateToValidate.matches(datePattern);
	     
	     return isDate;
	}
	
	public boolean isThisDateTimePatternMatch(String dateTimeToValidate){
		 boolean isDateTime = false;
	     String dateTimePattern = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{2}:\\d{2}:\\d{2}";
	     isDateTime = dateTimeToValidate.matches(dateTimePattern);
	     if(isDateTime) {
	    	 String dateTimePattern1 = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{2}:\\d{2}";
	    	 isDateTime = dateTimeToValidate.matches(dateTimePattern1);
	     }
	     
	     return isDateTime;
	}
	
	public String uploadProjectDocuments(HttpServletRequest request, String strFolderWithLocation, File file, String fileFileName, String strFileName, CommonFunctions CF) throws Exception {
		 
		if(strFileName!=null && strFileName.contains(" ")){
			strFileName=strFileName.replace(" ", "");
		 }

		if(strFileName!=null && strFileName.contains("+")) {
			strFileName=strFileName.replace("+", "_");
		 }
		 // the directory to upload to
//		String uploadDir = ServletActionContext.getServletContext()
//				.getRealPath("/"+strFolderWithLocation) + "/";
//
//		if(isRemoteLocation){
//			// To upload the file on a different location 
//			uploadDir = strFolderWithLocation;
//
//		}
		String uploadDir = strFolderWithLocation+"/";
		
		// write the file to the file specified
		File dirPath = new File(uploadDir);
		if (!dirPath.exists()) {
			dirPath.mkdirs(); 
		} else {
//			System.out.println("File Already Available ....... !");
		}
		
		// retrieve the file data
		InputStream stream = new FileInputStream(file);
		
		// write the file to the file specified
		OutputStream bos = new FileOutputStream(uploadDir + strFileName);
		int bytesRead;
		byte[] buffer = new byte[8192];

		while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}

		bos.close();
		stream.close();

		// place the data into the request for retrieval on next page
		request.setAttribute("location", dirPath.getAbsolutePath() + "/" + strFileName);
//		log.debug("location" + dirPath.getAbsolutePath() + "/" + fileFileName);

		String link = request.getContextPath() + "/" + strFolderWithLocation + "/";

		request.setAttribute("link", link + strFileName);
//		log.debug("link" + link + fileFileName);

		return strFileName;
		
	}
	
	public String uploadImageDocuments(HttpServletRequest request, String strFolderWithLocation, File file, String fileFileName, String strFileName, CommonFunctions CF) throws Exception {
		  
		String uploadDir = strFolderWithLocation+"/";
		
		// write the file to the file specified
//		System.out.println("strFolderWithLocation ===>>> " +strFolderWithLocation);
		File dirPath = new File(uploadDir);
		if (!dirPath.exists()) {
			dirPath.mkdirs(); 
		} else {
//			System.out.println("Folder Already Available ....... !");
		}
		
		int random = new Random().nextInt();
		strFileName = random+strFileName;
		
		// retrieve the file data
		InputStream stream = new FileInputStream(file);

		// write the file to the file specified
//		System.out.println("uploadDir + strFileName ===>>> " +uploadDir + strFileName);
		
		OutputStream bos = new FileOutputStream(uploadDir + strFileName);
		int bytesRead;
		byte[] buffer = new byte[8192];

		while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
//			System.out.println("insert Image ===>>> ");
		}

		bos.close();
		stream.close();

		// place the data into the request for retrieval on next page
		request.setAttribute("location", dirPath.getAbsolutePath() + "/" + strFileName);

		String link = request.getContextPath() + "/" + strFolderWithLocation + "/";

		request.setAttribute("link", link + strFileName);
//		System.out.println("strFileName ===>>> " + strFileName);
		return strFileName;
		
	}

	public String getFileTypeOnExtension(String ext) {
		String fileType = "Other";
		String subExt = ext.length() > 3 ? ext.substring(0, 3) : ext;
//		System.out.println("subExt ====>>>> " + subExt);
		
		if(subExt != null && subExt.equalsIgnoreCase("xls")) {
			fileType = "Excel spreadsheet";
		} else if(subExt != null && subExt.equalsIgnoreCase("doc")) {
			fileType = "Word document";
		} else if(subExt != null && subExt.equalsIgnoreCase("ppt")) {
			fileType = "Power Point document";
		} else if(subExt != null && subExt.equalsIgnoreCase("png")) {
			fileType = "PNG image";
		} else if(subExt != null && subExt.equalsIgnoreCase("jpg")) {
			fileType = "JPEG image";
		} else if(subExt != null && subExt.equalsIgnoreCase("jpe")) {
			fileType = "JPEG image";
		} else if(subExt != null && subExt.equalsIgnoreCase("gif")) {
			fileType = "GIF image";
		} else if(subExt != null && subExt.equalsIgnoreCase("txt")) {
			fileType = "Plain text document";
		} else if(subExt != null && subExt.equalsIgnoreCase("pdf")) {
			fileType = "PDF document";
		} else if(subExt != null && (subExt.equalsIgnoreCase("jsp") || subExt.equalsIgnoreCase("htm"))) {
			fileType = "HTML document";
		} 
		
		return fileType;
	}

	public String getFileTypeSize(double lengthBytes) {
		
		double kilobytes = 0;
		double megabytes = 0;
		double gigabytes = 0;
		double terabytes = 0;
//		double petabytes = 0;
//		double exabytes = 0;
//		double zettabytes = 0;
//		double yottabytes = 0;
		
		String fileSize = "0";
		if(lengthBytes > 1024) {
			kilobytes = (lengthBytes / 1024);
			fileSize = formatIntoOneDecimalWithOutComma(kilobytes)+" KB";
		} if(kilobytes > 1024) {
			megabytes = (kilobytes / 1024);
			fileSize = formatIntoOneDecimalWithOutComma(megabytes)+" MB";
		} if(megabytes > 1024) {
			gigabytes = (megabytes / 1024);
			fileSize = formatIntoOneDecimalWithOutComma(gigabytes)+" GB";
		} if(gigabytes > 1024) {
			terabytes = (gigabytes / 1024);
			fileSize = formatIntoOneDecimalWithOutComma(terabytes)+" TB";
		} if(lengthBytes <= 1024) {
			fileSize = formatIntoOneDecimalWithOutComma(lengthBytes)+" bytes";
		}
		
//		gigabytes = (megabytes / 1024);
//		terabytes = (gigabytes / 1024);
//		petabytes = (terabytes / 1024);
//		exabytes = (petabytes / 1024);
//		zettabytes = (exabytes / 1024);
//		yottabytes = (zettabytes / 1024);
		
		return fileSize;
	}
	
	
public String getFileOrImageSize(double lengthBytes) {
		
		double kilobytes = 0;
		double megabytes = 0;
		double gigabytes = 0;
		double terabytes = 0;
		
		String fileSize = "0";
		if(lengthBytes > 1024) {
			kilobytes = (lengthBytes / 1024);
			fileSize = formatIntoOneDecimalWithOutComma(kilobytes);
		} if(kilobytes > 1024) {
			megabytes = (kilobytes / 1024);
			fileSize = formatIntoOneDecimalWithOutComma(megabytes);
		} if(megabytes > 1024) {
			gigabytes = (megabytes / 1024);
			fileSize = formatIntoOneDecimalWithOutComma(gigabytes);
		} if(gigabytes > 1024) {
			terabytes = (gigabytes / 1024);
			fileSize = formatIntoOneDecimalWithOutComma(terabytes);
		} if(lengthBytes <= 1024) {
			fileSize = formatIntoOneDecimalWithOutComma(lengthBytes);
		}
		
		return fileSize;
	}


public String getSharingType(String sharingTypeId) {
	String sharingTypeName = null;
	
	if(sharingTypeId != null) {
		if(sharingTypeId.equals("0")) {
			sharingTypeName = "Public";
		} else if(sharingTypeId.equals("1")) {
			sharingTypeName = "Private Team";
		} else if(sharingTypeId.equals("2")) {
			sharingTypeName = "";
		}
	}
	return sharingTypeName;
}


public Map<String, String> getBillingHeadDataType() {
	Map<String, String> hmBHDataType = new HashMap<String, String>();
	
	hmBHDataType.put(DT_FIXED+"", 				"Fixed");
	hmBHDataType.put(DT_PRORATA_INDIVIDUAL+"", 	"Pro-rata(Individual)");
	hmBHDataType.put(DT_PRORATA_OVERALL+"", 	"Pro-rata(Cumulative)");
	hmBHDataType.put(DT_OPE_INDIVIDUAL+"", 		"OPE(Individual)");
	hmBHDataType.put(DT_OPE_OVERALL+"", 		"OPE(Cumulative)");
	hmBHDataType.put(DT_MILESTONE+"", 			"Milestone");
	hmBHDataType.put(DT_OPE+"", 				"OPE");
	
	return hmBHDataType;
}


public Map<String, String> getBillingHeadOtherVariables() {
	Map<String, String> hmBHOtherVariable = new HashMap<String, String>();
	
	hmBHOtherVariable.put(OV_ONLY_RESOURCE+"", 	"Only Resouce");
	hmBHOtherVariable.put(OV_ONLY_TASK+"", 		"Only Task");
	hmBHOtherVariable.put(OV_BOTH+"", 			"Both");
	
	return hmBHOtherVariable;
}


public Map<String, String> getTaxDeductionType() {
	Map<String, String> hmTaxDeductionType = new HashMap<String, String>();
	
	hmTaxDeductionType.put(TD_INVOICE+"",	 	"Invoice");
	hmTaxDeductionType.put(TD_CUSTOMER+"", 		"Customer Deduction");
//	hmTaxDeductionType.put(TD_BOTH+"", 			"Both");
	
	return hmTaxDeductionType;
}


public String getTaskResourcesAvgCostOrBillRate(Map<String, String> empCostOrRateMp, String resourceIds) {
	
	double dblAvgAmount = 0;
	List<String> resourceList = new ArrayList<String>();
	if(resourceIds != null) {
		resourceList = Arrays.asList(resourceIds.split(","));
	}
	double dblResourceAmt = 0;
	int resourceCnt = 0;
	for(int i=0; resourceList != null && !resourceList.isEmpty() && i<resourceList.size(); i++) {
		if(resourceList.get(i) != null && !resourceList.get(i).equals("") && parseToInt(resourceList.get(i))> 0) {
			dblResourceAmt += parseToDouble(empCostOrRateMp.get(resourceList.get(i)));
			resourceCnt++;
		}
	}
	if(resourceCnt > 0 && dblResourceAmt > 0) {
		dblAvgAmount = dblResourceAmt / resourceCnt;
	}
	return dblAvgAmount+"";
}

public String getAdHocBillingType(String billingTypeId) {
	String billingTypeName = null;
	
	if(billingTypeId != null) {
		if(billingTypeId.equals("1")) {
			billingTypeName = "General";
		} else if(billingTypeId.equals("2")) {
			billingTypeName = "Prorata";
		}
	}
	return billingTypeName;
}


public String getProrataBillDayHourMonthById(String billDHMId) {
	String billDHMName = null;
	
	if(billDHMId != null) {
		if(billDHMId.equals("1")) {
			billDHMName = "Days";
		} else if(billDHMId.equals("2")) {
			billDHMName = "Hours";
		} else if(billDHMId.equals("3")) {
			billDHMName = "Months";
		}
	}
	return billDHMName;
}


public java.sql.Date getFutureMonthDate(String currentDate, int nFutureMonth) {
	java.util.Date dt = null;
	Calendar cal = GregorianCalendar.getInstance();
	
	try {
		int intDate = parseToInt(getDateFormat(currentDate, DATE_FORMAT, "dd")); 
		int intMnth = parseToInt(getDateFormat(currentDate, DATE_FORMAT, "MM")); 
		int intYear = parseToInt(getDateFormat(currentDate, DATE_FORMAT, "yyyy"));
		nFutureMonth = nFutureMonth - 1;
		
//		System.out.println("intMnth ===>> " + intMnth);
		/*if(intDate > 28) {
			DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			Date startdate = (Date)formatter.parse(currentDate);
		    Calendar cal1 = Calendar.getInstance();
			cal1.setTime(startdate);
		    cal1.add(Calendar.MONTH, 1);
//		    System.out.println("cal1.MONTH ===>> " + cal1.get(Calendar.MONTH));
		    intDate = cal1.getActualMaximum(Calendar.DATE);
		}*/
		if(intDate == 31 && intMnth == 1) {
			if(intYear % 4 == 0) {
				intDate = 29;
			} else {
				intDate = 28;
			}
		} else if(intDate == 31 && intMnth != 1) {
			intDate = 30;
		}
		cal.set(Calendar.DATE, intDate);
		cal.set(Calendar.MONTH, intMnth + nFutureMonth);
		cal.set(Calendar.YEAR, intYear);
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
	
	long currDate = dt.getTime();
	return new java.sql.Date(currDate);
}


public java.sql.Date getDateOfPassedDay(String startDate, String weekday) {
	java.util.Date dt = null;
	Calendar cal = GregorianCalendar.getInstance();
	
	int intDate = parseToInt(getDateFormat(startDate, DATE_FORMAT, "dd")); 
	int intMnth = parseToInt(getDateFormat(startDate, DATE_FORMAT, "MM")); 
	int intYear = parseToInt(getDateFormat(startDate, DATE_FORMAT, "yyyy"));
	
	cal.set(Calendar.DATE, intDate);
	cal.set(Calendar.MONTH, intMnth - 1);
	cal.set(Calendar.YEAR, intYear);
	int currDateWeekDay = cal.get(Calendar.DAY_OF_WEEK);
	//System.out.println("currDateWeekDay ===>> " + currDateWeekDay);
	
	int intweekDay = 1;
	if(weekday.equalsIgnoreCase(SUNDAY)) {
		intweekDay = 1;
	} else if(weekday.equalsIgnoreCase(MONDAY)) {
		intweekDay = 2;
	} else if(weekday.equalsIgnoreCase(TUESDAY)) {
		intweekDay = 3;
	} else if(weekday.equalsIgnoreCase(WEDNESDAY)) {
		intweekDay = 4;
	} else if(weekday.equalsIgnoreCase(THURSDAY)) {
		intweekDay = 5;
	} else if(weekday.equalsIgnoreCase(FRIDAY)) {
		intweekDay = 6;
	} else if(weekday.equalsIgnoreCase(SATURDAY)) {
		intweekDay = 7;
	}
	if(currDateWeekDay == intweekDay) {
		cal.add(Calendar.DATE, 7);
	} else {
		while (cal.get(Calendar.DAY_OF_WEEK) != intweekDay) {
			cal.add(Calendar.DATE, 1);
		}
	}
	dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
	
	long currDate = dt.getTime();
	return new java.sql.Date(currDate);
}


public String getWeekOfMonthOnPassedDate(String startDate) {
	
	String strWeekName = "";
	Calendar cal = GregorianCalendar.getInstance();
	
	int intDate = parseToInt(getDateFormat(startDate, DATE_FORMAT, "dd")); 
	int intMnth = parseToInt(getDateFormat(startDate, DATE_FORMAT, "MM")); 
	int intYear = parseToInt(getDateFormat(startDate, DATE_FORMAT, "yyyy"));
	
	cal.set(Calendar.DATE, intDate);
	cal.set(Calendar.MONTH, intMnth - 1);
	cal.set(Calendar.YEAR, intYear);
	int currWeek = cal.get(Calendar.WEEK_OF_MONTH);
	//System.out.println("currDateWeekDay ===>> " + currDateWeekDay);
	
	if(currWeek == 1) {
		strWeekName = "First";
	} else if(currWeek == 2) {
		strWeekName = "Second";
	} else if(currWeek == 3) {
		strWeekName = "Third";
	} else if(currWeek == 4) {
		strWeekName = "Fourth";
	} else if(currWeek == 5) {
		strWeekName = "Fifth";
	} else  if(currWeek == 6) {
		strWeekName = "Sixth";
	}
	return strWeekName;
}


public String getTotalTimeMinutes60To100(String strTime) {
	String actualTime = "0.0";
	
	if(strTime !=null && strTime.indexOf(".") > 0) {
		String strHour = strTime.substring(0, strTime.indexOf("."));
		String strMinute = strTime.substring(strTime.indexOf(".")+1);
	
		double dblTime = 0;
		if (strMinute != null && !strMinute.trim().equals("") && !strMinute.trim().equalsIgnoreCase("NULL")) {
			strMinute = "."+strMinute;
			strMinute = strMinute.replaceAll(",", "");
			dblTime = parseToDouble(strMinute);
		}
		
		if(dblTime < 0.60) {
			double dbl100Min = (dblTime * 100) /60;
			actualTime = formatIntoTwoDecimalWithOutComma((parseToDouble(strHour)+dbl100Min)); 
		} else {
			actualTime = formatIntoTwoDecimalWithOutComma((parseToDouble(strHour)+dblTime));
		}
	} else if(strTime !=null) {
		actualTime = formatIntoTwoDecimalWithOutComma(parseToDouble(strTime));
	}
	return actualTime;
}


public String getTotalTimeMinutes100To60(String strTime) {
	String actualTime = "0.0";
	
	if(strTime !=null && strTime.indexOf(".") > 0) {
		String strHour = strTime.substring(0, strTime.indexOf("."));
		String strMinute = strTime.substring(strTime.indexOf(".")+1);
	
		double dblTime = 0;
		if (strMinute != null && !strMinute.trim().equals("") && !strMinute.trim().equalsIgnoreCase("NULL")) {
			strMinute = "."+strMinute;
			strMinute = strMinute.replaceAll(",", "");
			dblTime = parseToDouble(strMinute);
		}
		double dbl60Min = (dblTime * 60) /100;
		actualTime = formatIntoTwoDecimalWithOutComma((parseToDouble(strHour)+dbl60Min)); 
	} else if(strTime !=null) {
		actualTime = formatIntoTwoDecimalWithOutComma(parseToDouble(strTime));
	}
	return actualTime;
}


public String getNextMonthMinMaxDate(String startPeriod, String dateFormat) {
	 
	  DateFormat formatter = new SimpleDateFormat(dateFormat);
	  SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	      String newStartPeriod = null;
	      String newEndPeriod = null;
	   try {
		    Date startdate = (Date)formatter.parse(startPeriod);
		    Calendar cal=Calendar.getInstance();
			cal.setTime(startdate);
		     
		    cal.add(Calendar.MONTH, 1);
		    int minDays = cal.getActualMinimum(Calendar.DATE);
		    cal.set(Calendar.DAY_OF_MONTH, minDays);
		    newStartPeriod = sdf.format(cal.getTime());
		    
		    int maxDays = cal.getActualMaximum(Calendar.DATE);
		    cal.set(Calendar.DAY_OF_MONTH, maxDays);
		    newEndPeriod = sdf.format(cal.getTime());
	    } catch(ParseException ex) {
	    System.out.println("Exception :: " + ex);
	    }
	   return (newStartPeriod + "::::" + newEndPeriod);
	}


	public String getNextORPrevMonthMinMaxDate(int intMonth, String startPeriod, String dateFormat) {
	 
	  DateFormat formatter = new SimpleDateFormat(dateFormat);
	  SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	      String newStartPeriod = null;
	      String newEndPeriod = null;
	   try {
		    Date startdate = (Date)formatter.parse(startPeriod);
		    Calendar cal=Calendar.getInstance();
			cal.setTime(startdate);
		     
		    cal.add(Calendar.MONTH, intMonth);
		    int minDays = cal.getActualMinimum(Calendar.DATE);
		    cal.set(Calendar.DAY_OF_MONTH, minDays);
		    newStartPeriod = sdf.format(cal.getTime());
		    
		    int maxDays = cal.getActualMaximum(Calendar.DATE);
		    cal.set(Calendar.DAY_OF_MONTH, maxDays);
		    newEndPeriod = sdf.format(cal.getTime());
	    } catch(ParseException ex) {
	    System.out.println("Exception :: " + ex);
	    }
	   return (newStartPeriod + "::::" + newEndPeriod);
	}

	
	public String getCurrentMonthMinMaxDate(String startPeriod, String dateFormat) {
	 
	  DateFormat formatter = new SimpleDateFormat(dateFormat);
	  SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	      String newStartPeriod = null;
	      String newEndPeriod = null;
	   try {
//		   System.out.println("startPeriod ===>> " + startPeriod);
		    Date startdate = (Date)formatter.parse(startPeriod);
		    Calendar cal=Calendar.getInstance();
			cal.setTime(startdate);
		     
		    cal.add(Calendar.MONTH, 0);
		    int minDays = cal.getActualMinimum(Calendar.DATE);
		    cal.set(Calendar.DAY_OF_MONTH, minDays);
		    newStartPeriod = sdf.format(cal.getTime());
		    
		    int maxDays = cal.getActualMaximum(Calendar.DATE);
		    cal.set(Calendar.DAY_OF_MONTH, maxDays);
		    newEndPeriod = sdf.format(cal.getTime());
	    } catch(ParseException ex) {
	    System.out.println("Exception :: " + ex);
	    }
	   return (newStartPeriod + "::::" + newEndPeriod);
	}

//	public String uploadImage(HttpServletRequest request, String strFolderWithLocation, String strFolderRetriveWithLocation, File file, String fileFileName, String strFileName, CommonFunctions CF, boolean isSaveLocation) throws Exception {
//		 FileOutputStream fileOutputStream = null;
//		 InputStream stream = new FileInputStream(file);
//		 byte[] outputArray = IOUtils.toByteArray(stream);
//		 
//		 String imagename = null;
//		 if(outputArray.length>0){
//			
//			BufferedImage image = ImageIO.read(file);
//			int width = image.getWidth();
//			int height= image.getHeight();
//			System.out.println("width=====>"+width);
//			System.out.println("height=====>"+height);
//			System.out.println("strFileName=====>"+strFileName);
//			
//			String extenstion=FilenameUtils.getExtension(strFileName);
//			int random = new Random().nextInt();
//			imagename=random+strFileName;
//			System.out.println("imagename=====>"+imagename);
//			System.out.println("extenstion=====>"+extenstion);
//			
//			String directory = strFolderWithLocation;
//			File imageFile = new File(directory + File.separator + imagename);
//			System.out.println("directory + File.separator + imagename=====>"+directory + File.separator + imagename);
//			
//			// Stores the Image
//			FileUtils.forceMkdir(new File(directory));			
//			fileOutputStream = new FileOutputStream(imageFile);
//			fileOutputStream.write(outputArray);
//			
//			if(isSaveLocation && (extenstion.equalsIgnoreCase("jpg") || extenstion.equalsIgnoreCase("jpeg") || extenstion.equalsIgnoreCase("png") || extenstion.equalsIgnoreCase("tiff") || extenstion.equalsIgnoreCase("gif") || extenstion.equalsIgnoreCase("bmp"))){
//				//Create a thumb nail 100x100
//				String thumbnailDirectory = strFolderWithLocation+File.separator + I_100x100;
//				FileUtils.forceMkdir(new File(thumbnailDirectory));
////				String sourceFile = imageFile.getAbsolutePath();
//				String sourceFile = strFolderRetriveWithLocation + File.separator + imagename;
//				String destFile = thumbnailDirectory+ File.separator + imagename;
//				File dirPath = new File(sourceFile);
//				if (!dirPath.exists()) {
//					System.out.println("exist=====>");
//				} else {
//					System.out.println("not exist=====>");
//				}
//				
//				System.out.println("sourceFile=====>"+sourceFile);
//				System.out.println("destFile=====>"+destFile);
//				System.out.println("I_100x100=====>"+I_100x100);
////				Runtime.getRuntime().exec("convert -thumbnail 100 -quality 100 "+sourceFile +" "+destFile );
//				Runtime.getRuntime().exec("convert -define "+extenstion+":size="+width+"x"+height+" "+sourceFile +"  -thumbnail "+ I_100x100 +" "+destFile );
//	//			Runtime.getRuntime().exec("convert -define "+extenstion+":size="+width+"x"+height+" "+sourceFile +" -auto-orient -thumbnail 100x100 "+destFile );
//				
//				
////				//Create a thumb nail 60x60
////				String thumbnailDirectory1 = strFolderWithLocation+File.separator + I_60x60;
////				FileUtils.forceMkdir(new File(thumbnailDirectory1));
////				String destFile1 = thumbnailDirectory1+ File.separator + imagename;
////				System.out.println("destFile1=====>"+destFile1);
//////				Runtime.getRuntime().exec("convert -thumbnail 60 -quality 100 "+sourceFile +" "+destFile );
////				Runtime.getRuntime().exec("convert -define "+extenstion+":size="+width+"x"+height+" "+sourceFile +" -thumbnail "+ I_60x60 +" "+destFile1 );
////				
////				//Create a thumb nail 22x22
////				String thumbnailDirectory2 = strFolderWithLocation+File.separator + I_22x22;
////				FileUtils.forceMkdir(new File(thumbnailDirectory2));
////				String destFile2 = thumbnailDirectory2+ File.separator + imagename;
////				System.out.println("destFile2=====>"+destFile2);
//////				Runtime.getRuntime().exec("convert -thumbnail 22 -quality 100 "+sourceFile +" "+destFile );
////				Runtime.getRuntime().exec("convert -define "+extenstion+":size="+width+"x"+height+" "+sourceFile +" -thumbnail "+ I_22x22 +" "+destFile2 );
////				
////				//Create a thumb nail 16x16
////				String thumbnailDirectory3 = strFolderWithLocation+File.separator + I_16x16;
////				FileUtils.forceMkdir(new File(thumbnailDirectory3));
////				String destFile3 = thumbnailDirectory3+ File.separator + imagename;
////				System.out.println("destFile3=====>"+destFile3);
//////				Runtime.getRuntime().exec("convert -thumbnail 16 -quality 100 "+sourceFile +" "+destFile );
////				Runtime.getRuntime().exec("convert -define "+extenstion+":size="+width+"x"+height+" "+sourceFile +" -thumbnail "+I_16x16+" "+destFile3 );
//			}
//		 }
//		 return imagename;
//	}
	
	
	public String uploadImage(HttpServletRequest request, String strFolderWithLocation,  File file, String fileFileName, String strFileName, CommonFunctions CF, boolean isSaveLocation) throws Exception {
		 FileOutputStream fileOutputStream = null;
		 InputStream stream = new FileInputStream(file);
		 byte[] outputArray = IOUtils.toByteArray(stream);
		 
		 String imagename = null;
		 //System.out.println("outputArray.length==="+outputArray.length);
		 //System.out.println("file==="+file);
		 //System.out.println("fileFileName==="+fileFileName);
		 if(outputArray.length>0){
			
			BufferedImage image = ImageIO.read(file);
			int width = image.getWidth();
			int height= image.getHeight();
			//System.out.println("image==="+image);
			// String extenstion=FilenameUtils.getExtension(strFileName);
			String extenstion = "jpg";
			//System.out.println("extenstion==="+extenstion);
			int random = new Random().nextInt();
			imagename=random+strFileName+".jpg";
			//System.out.println("imagename==="+imagename);
			String directory = strFolderWithLocation;
			//System.out.println("directory==="+directory); 
			File imageFile = new File(directory + File.separator + imagename);
			//System.out.println("imageFile==="+imageFile);
			// Stores the Image
			FileUtils.forceMkdir(new File(directory));			
			fileOutputStream = new FileOutputStream(imageFile);
			fileOutputStream.write(outputArray);
			
			if(isSaveLocation && (extenstion.equalsIgnoreCase("jpg") || extenstion.equalsIgnoreCase("jpeg") || extenstion.equalsIgnoreCase("png") || extenstion.equalsIgnoreCase("tiff") || extenstion.equalsIgnoreCase("gif") || extenstion.equalsIgnoreCase("bmp"))){
				//Create a thumb nail 100x100
				String thumbnailDirectory1 = strFolderWithLocation+File.separator + I_100x100;
				FileUtils.forceMkdir(new File(thumbnailDirectory1));
				String destFile1 = thumbnailDirectory1+ File.separator + imagename;
				File imageFile1 = new File(destFile1);
				FileUtils.forceMkdir(new File(thumbnailDirectory1));			
				fileOutputStream = new FileOutputStream(imageFile1);
				byte[] outputArray1 = scale(outputArray, 100, 0, extenstion);
				fileOutputStream.write(outputArray1);
				
//				//Create a thumb nail 60x60
				String thumbnailDirectory2 = strFolderWithLocation+File.separator + I_60x60;
				FileUtils.forceMkdir(new File(thumbnailDirectory2));
				String destFile2 = thumbnailDirectory2+ File.separator + imagename;
				File imageFile2 = new File(destFile2);
				FileUtils.forceMkdir(new File(thumbnailDirectory2));			
				fileOutputStream = new FileOutputStream(imageFile2);
				byte[] outputArray2 = scale(outputArray, 60, 0, extenstion);
				fileOutputStream.write(outputArray2);
				
//				//Create a thumb nail 22x22
				String thumbnailDirectory3 = strFolderWithLocation+File.separator + I_22x22;
				FileUtils.forceMkdir(new File(thumbnailDirectory3));
				String destFile3 = thumbnailDirectory3+ File.separator + imagename;
				File imageFile3 = new File(destFile3);
				FileUtils.forceMkdir(new File(thumbnailDirectory3));			
				fileOutputStream = new FileOutputStream(imageFile3);
				byte[] outputArray3 = scale(outputArray, 22, 0, extenstion);
				fileOutputStream.write(outputArray3);
				
//				//Create a thumb nail 16x16
				String thumbnailDirectory4 = strFolderWithLocation+File.separator + I_16x16;
				FileUtils.forceMkdir(new File(thumbnailDirectory4));
				String destFile4 = thumbnailDirectory4+ File.separator + imagename;
				File imageFile4 = new File(destFile4);
				FileUtils.forceMkdir(new File(thumbnailDirectory4));			
				fileOutputStream = new FileOutputStream(imageFile4);
				byte[] outputArray4 = scale(outputArray, 16, 0, extenstion);
				fileOutputStream.write(outputArray4);
				
			}
		 }
		 return imagename;
	}
	
	public byte[] scale(byte[] fileData, int width, int height, String extenstion) {
    	ByteArrayInputStream in = new ByteArrayInputStream(fileData);
    	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    	try {
    		BufferedImage img = ImageIO.read(in);
//    		System.out.println("height=====>"+width+"=====height=====>"+height);
    		if(height == 0) {
    			height = (width * img.getHeight())/ img.getWidth(); 
    		}
    		if(width == 0) {
    			width = (height * img.getWidth())/ img.getHeight();
    		}
//    		System.out.println("img.getWidth()=====>"+img.getWidth()+"=====img.getHeight()=====>"+img.getHeight());
//    		System.out.println("height=====>"+width+"=====height=====>"+height);
    		Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    		BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    		imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);

    		ImageIO.write(imageBuff, extenstion, buffer);

    		return buffer.toByteArray();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return buffer.toByteArray();
    }
	
	public String getTimeDurationBetweenDatesNoSpan(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request){
		
		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
			if(period.getYears()>0 && period.getYears()==1){
				sbTimeDuration.append(period.getYears()+" year ");
			}else if(period.getYears()>0){
				sbTimeDuration.append(period.getYears()+" years ");
			}
			if(period.getYears()>0){
				sbTimeDuration.append("and ");
			}
			
			if(period.getMonths()>0 && period.getMonths()==1){
				sbTimeDuration.append(period.getMonths()+" month ");
			}else if(period.getMonths()>0){
				sbTimeDuration.append(period.getMonths()+" months ");
			}
			
			if(period.getMonths()>0 && period.getDays()>0){
				sbTimeDuration.append("and ");
			}
			if(period.getDays()>0 && period.getDays()==1){
				sbTimeDuration.append(period.getDays()+" day ");
			}else if(period.getDays()>0){
				sbTimeDuration.append(period.getDays()+" days ");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sbTimeDuration.toString();
	}
	
	
	public String getTimeDurationBetweenDatesInYearsOnly(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request){
		
		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
			if(period.getYears()>0 && period.getYears()==1){
				sbTimeDuration.append(period.getYears()+" year ");
			}else if(period.getYears()>0){
				sbTimeDuration.append(period.getYears()+" years ");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sbTimeDuration.toString();
	}

	
	public String getTimeDurationBetweenDatesNoSpanSmall(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request){
		
		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
			if(period.getYears()>0 && period.getYears()==1){
				sbTimeDuration.append(period.getYears()+"y");
			}else if(period.getYears()>0){
				sbTimeDuration.append(period.getYears()+"y");
			}
			if(period.getYears()>0){
				sbTimeDuration.append(", ");
			}
			
			if(period.getMonths()>0 && period.getMonths()==1){
				sbTimeDuration.append(period.getMonths()+"m");
			}else if(period.getMonths()>0){
				sbTimeDuration.append(period.getMonths()+"m");
			}
			
			if(period.getMonths()>0 && period.getDays()>0){
				sbTimeDuration.append(", ");
			}
			if(period.getDays()>0 && period.getDays()==1){
				sbTimeDuration.append(period.getDays()+"d");
			}else if(period.getDays()>0){
				sbTimeDuration.append(period.getDays()+"d");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sbTimeDuration.toString();
	}
	
	public boolean isFileExist(String strFilePath) {
		try {

			File f = new File(strFilePath);
		    if(f.isFile() && f.exists()){
		    	return true;
		    }

		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	public static LocalDate getNDayOfMonth(int dayweek,int nthweek,int month,int year)  {
	   LocalDate d = new LocalDate(year, month, 1).withDayOfWeek(dayweek);
	   if(d.getMonthOfYear() != month) d = d.plusWeeks(1);
	   return d.plusWeeks(nthweek-1);
	}

	public static LocalDate getLastWeekdayOfMonth(int dayweek,int month,int year) {
	   LocalDate d = new LocalDate(year, month, 1).plusMonths(1).withDayOfWeek(dayweek);
	   if(d.getMonthOfYear() != month) d = d.minusWeeks(1);
	  return d;
	}

	
	public String getPostAlignedWith(int alignType) {
		switch(alignType) {
			  case PROJECT: return "project";
			  case TASK: return "task";
			  case PRO_TIMESHEET: return "project timesheet";
			  case DOCUMENT: return "document";
		}
		return null;
	}
	
	
	public String getPostSharedWith(int shareType) {
		switch(shareType) {
			  case S_PUBLIC: return "Public";
			  case S_TEAM: return "Team";
			  case S_RESOURCE: return "Resource";
			  case S_CUSTOMER: return "Customer";
		}
		return null;
	}
	

	public void getMonthWeeksDate(List<List<String>> weekdates,String strMonth, String strYear, String dateFormat) {
		try{
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.MONTH, parseToInt(strMonth) - 1);
			cal.set(Calendar.YEAR, parseToInt(strYear));
			
            int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			int nMonth = cal.get(Calendar.MONTH);
			
			String strStartDate =  (nMonthStart < 10 ? "0"+nMonthStart : nMonthStart) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
			String strEndDate =  (nMonthEnd < 10 ? "0"+nMonthEnd : nMonthEnd) + "/" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0"+(cal.get(Calendar.MONTH) + 1):(cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
//			System.out.println("strStartDate======>"+strStartDate+"------strEndDate=====>"+strEndDate);

			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		    List<String> dates;
			while (cal.get(Calendar.MONTH) == nMonth) {
				dates = new ArrayList<String>();
				while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					cal.add(Calendar.DAY_OF_MONTH, -1);
				}
				if (cal.getTime().before(format.parse(strStartDate))) {
					dates.add(strStartDate);
				} else {
					dates.add(format.format(cal.getTime()));
				}

				cal.add(Calendar.DAY_OF_MONTH, 6);
				if (cal.getTime().after(format.parse(strEndDate))) {
					dates.add(strEndDate);
				} else {
					dates.add(format.format(cal.getTime()));
				}
				
				weekdates.add(dates);
				cal.add(Calendar.DAY_OF_MONTH, 1);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String getCopyFile(String strFileName,String proNameFolder, String fileName, int cnt) {
		File f = new File(strFileName);
		boolean flag = false;
	    if(f.isFile() && f.exists()) {
	    	cnt++;
	    	strFileName = proNameFolder+"/Copy("+cnt+")_"+fileName;
	    	flag = true;
	    }
		return flag ? getCopyFile(strFileName,proNameFolder,fileName,cnt) : strFileName;
	}
	
	
	public String getCreateNewFileName(String strFileName, String proNameFolder, String fileName, int cnt) {
		File f = new File(proNameFolder+"/"+strFileName);
		boolean flag = false;
	    if(f.isFile() && f.exists()) {
	    	cnt++;
	    	strFileName = "Copy("+cnt+")_"+fileName;
	    	flag = true;
	    }
		return flag ? getCreateNewFileName(strFileName, proNameFolder, fileName, cnt) : strFileName;
	}

	
	public String getGradeStandard(UtilityFunctions uF, String gradeStand) {
		String gradeStandard = null;
		if(uF.parseToInt(gradeStand) == 1) {
			gradeStandard = "Typical Grade";
		} else if(uF.parseToInt(gradeStand) == 2) {
			gradeStandard = "Standard Grade";
		} else if(uF.parseToInt(gradeStand) == 3) {
			gradeStandard = "International Grade";
		}
		return gradeStandard;
	}
	
	
	public String getGradeType(UtilityFunctions uF, String gradeTyp) {
		String gradeType = null;
		if(uF.parseToInt(gradeTyp) == 1) {
			gradeType = "Numerical";
		} else if(uF.parseToInt(gradeTyp) == 2) {
			gradeType = "Alphabetical";
		}
		return gradeType;
	}

	public String getTimesToAttempt(int timesAttempt) {
		switch (timesAttempt) {
			case 1 :
				return "Once";
			case 2 :
				return "Twice";
			case 3 :
				return "Thrice";
			case 4 :
				return "Four Times";
			case 5 :
				return "Five Times";
			case 6 :
				return "Six Times";
			case 7 :
				return "Seven Times";
			case 8 :
				return "Eight Times";
			case 9 :
				return "Nine Times";
			case 10 :
				return "Ten Times";
		}
		return null;
	}
	
	public String getTimeVariance(UtilityFunctions uF, String strTimeZone, String strFirstTime, String strSecondTime) {
		String strVariance = "0.00";
		try{
			double dblFirstTime = uF.parseToDouble(strFirstTime);
			double dblSecondTime = uF.parseToDouble(strSecondTime);
			if(dblFirstTime == 0.0d && dblSecondTime == 0.0d){
				return uF.formatIntoTwoDecimal(dblFirstTime);
			} else if(dblFirstTime == 0.0d && dblSecondTime > 0.0d){
				return uF.formatIntoTwoDecimal(dblSecondTime);
			} else if(dblFirstTime < dblSecondTime){
				String strFrom = uF.formatIntoTwoDecimal(dblFirstTime);
				String strTo = uF.formatIntoTwoDecimal(dblSecondTime);
				
				strFrom = strFrom.replace(".",":");
				strTo = strTo.replace(".",":");
				
				strFrom +=":00";
				strTo +=":00";
				
				Time frmTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+strFrom, DBDATE+DBTIME);
				Time toTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+strTo, DBDATE+DBTIME);
				
				return uF.getTimeDiffInHoursMins(frmTime.getTime(), toTime.getTime());
			} else if(dblFirstTime > dblSecondTime){
				String strFrom = uF.formatIntoTwoDecimal(dblFirstTime);
				String strTo = uF.formatIntoTwoDecimal(dblSecondTime);
				
				strFrom = strFrom.replace(".",":");
				strTo = strTo.replace(".",":");
				
				strFrom +=":00";
				strTo +=":00";
				
				Time frmTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+strFrom, DBDATE+DBTIME);
				Time toTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+strTo, DBDATE+DBTIME);
				
				return "-"+uF.getTimeDiffInHoursMins(toTime.getTime(), frmTime.getTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strVariance;
	}

	
	public String getHourInAMorPM(int intHour) {
		switch (intHour) {
			case 1 :
				return "01:00 AM";
			case 2 :
				return "02:00 AM";
			case 3 :
				return "03:00 AM";
			case 4 :
				return "04:00 AM";
			case 5 :
				return "05:00 AM";
			case 6 :
				return "06:00 AM";
			case 7 :
				return "07:00 AM";
			case 8 :
				return "08:00 AM";
			case 9 :
				return "09:00 AM";
			case 10 :
				return "10:00 AM";
			case 11 :
				return "11:00 AM";
			case 12 :
				return "12:00 PM";
			case 13 :
				return "01:00 PM";
			case 14 :
				return "02:00 PM";
			case 15 :
				return "03:00 PM";
			case 16 :
				return "04:00 PM";
			case 17 :
				return "05:00 PM";
			case 18 :
				return "06:00 PM";
			case 19 :
				return "07:00 PM";
			case 20 :
				return "08:00 PM";
			case 21 :
				return "09:00 PM";
			case 22 :
				return "10:00 PM";
			case 23 :
				return "11:00 PM";
			case 0 :
				return "12:00 AM";
			
		}
		return null;
	}
	public long getDateDiffinWeekDays( Date d1, Date d2) {
		int calUnit = Calendar.WEEK_OF_YEAR;
	      if( d1.after(d2) ) {    
	         Date temp = d1;
	         d1 = d2;
	         d2 = temp;
	      }
	      GregorianCalendar c1 = new GregorianCalendar();
	      c1.setTime(d1);
	      GregorianCalendar c2 = new GregorianCalendar();
	      c2.setTime(d2);
	      for( long i=1; ; i++ ) {        
	         c1.add( calUnit, 1 );   // add one day, week, year, etc.
	         if( c1.after(c2) )
	            return i-1;
	      }
	   }
		
	public long getDateDiffinDays( Date d1, Date d2) {
		int calUnit = Calendar.DATE;
	      if( d1.after(d2) ) {    
	         Date temp = d1;
	         d1 = d2;
	         d2 = temp;
	      }
	      GregorianCalendar c1 = new GregorianCalendar();
	      c1.setTime(d1);
	      GregorianCalendar c2 = new GregorianCalendar();
	      c2.setTime(d2);
	      for( long i=1; ; i++ ) {        
	         c1.add( calUnit, 1 );   // add one day, week, year, etc.
	         if( c1.after(c2) )
	            return i-1;
	      }
	   }
		
	
	public java.sql.Date getBiweeklyDate(String date,int days){
		java.util.Date dt = null;
		GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(getDateFormatUtil(date,DATE_FORMAT));
        cal.add(Calendar.DATE, days);
        dt = cal.getTime();
        
        long currDate = dt.getTime();
        return new java.sql.Date(currDate);
	}
	
	public int getNoOfDaysMonth(int year,int month){
		 Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.YEAR, year);
	        calendar.set(Calendar.MONTH, month);
	        int numDays = calendar.getActualMaximum(Calendar.DATE);
//	        System.out.println("\nnum of days :\t"+numDays);
	        return numDays;
	}
	
	public int getRandomDayNumber(){
		
		int[] dayNo = new int[366];
		int j = 1,i = 0;
		while(j<=366){
			dayNo[i] = j;
			j++;
			i++;
		}
		
		int rnd = new Random().nextInt(366);
		    return dayNo[rnd];
	}
	
	public java.util.Date getNextDate(Date utDate, int nFutureDays) {
		java.util.Date dt = null;
		if(utDate!=null){
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(utDate);
			cal.add(Calendar.DATE, nFutureDays);
			dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			
		}
		return dt;
	}
	
	public String getDateFormatUtil(java.sql.Date strDate, String strFormat) {
		String utdt = null;
		try {
			if(strDate==null)
				return null;
			SimpleDateFormat smft = new SimpleDateFormat(strFormat);
			
			utdt = smft.format(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return utdt;
	}

	
	public String getFrequencyEndDate(UtilityFunctions uF, Date freqDate, int intMonths, String freqEndDt, String proStrtDate, Date freqEdDate, int cnt) {
//		String freqEndDt = null; 
		
//		System.out.println(" Recurtion FUN == freqDate ===>> " + freqDate+ " -- freqEndDt ===>> " + freqEndDt +" -- intMonths ===>> "+intMonths);
		
		String strDate = uF.getDateFormatUtil(freqDate, DATE_FORMAT);
		int strDateMnth = uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "MM")); 
		int strDateYr = uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "yyyy"));
		
	    int freqEndMnth = uF.parseToInt(uF.getDateFormat(freqEndDt, DATE_FORMAT, "MM")); 
		int freqEndYr = uF.parseToInt(uF.getDateFormat(freqEndDt, DATE_FORMAT, "yyyy"));
		
		Date strPrevdt = uF.getDateFormatUtil(uF.getFutureDate(uF.getDateFormatUtil(proStrtDate, DATE_FORMAT), -32)+"", DBDATE);
//		System.out.println("strPrevdt ===>> " + strPrevdt);
		if(cnt == 0 && (freqEdDate.equals(uF.getDateFormatUtil(proStrtDate, DATE_FORMAT)) || (freqDate.before(uF.getDateFormatUtil(proStrtDate, DATE_FORMAT)) && freqDate.before(strPrevdt))) ) {
			freqEndDt = uF.getDateFormat(uF.getFutureMonthDate(proStrtDate, -1)+"", DBDATE, DATE_FORMAT);
			if(intMonths == 2 || intMonths == 5 || intMonths == 11) {
				intMonths++;
			}
//			proStartDate = freqEndDt;
			cnt++;
			freqEndDt = getFrequencyEndDate(uF, freqDate, intMonths, freqEndDt, proStrtDate, freqEdDate, cnt);
		} else if(cnt == 0 && freqDate.before(uF.getDateFormatUtil(proStrtDate, DATE_FORMAT)) && freqDate.after(strPrevdt) ) {
			freqEndDt = uF.getDateFormat(uF.getFutureMonthDate(proStrtDate, intMonths)+"", DBDATE, DATE_FORMAT);
			if(intMonths == 2 || intMonths == 5 || intMonths == 11) {
				intMonths++;
			}
//			proStartDate = freqEndDt;
			cnt++;
			freqEndDt = getFrequencyEndDate(uF, freqDate, intMonths, freqEndDt, proStrtDate, freqEdDate, cnt);
		} else if(cnt == 0 && freqDate.after(uF.getDateFormatUtil(freqEndDt, DATE_FORMAT)) && ((strDateMnth>freqEndMnth && strDateYr == freqEndYr) || (strDateYr > freqEndYr))) {
			freqEndDt = uF.getDateFormat(uF.getFutureMonthDate(freqEndDt, intMonths)+"", DBDATE, DATE_FORMAT);
			if(intMonths == 2 || intMonths == 5 || intMonths == 11) {
				intMonths++;
			}
//			proStartDate = freqEndDt;
			freqEndDt = getFrequencyEndDate(uF, freqDate, intMonths, freqEndDt, proStrtDate, freqEdDate, cnt);
		} else if(cnt == 0 && strDateMnth ==freqEndMnth && strDateYr == freqEndYr) {
			freqEndDt = uF.getDateFormat(uF.getFutureMonthDate(freqEndDt, intMonths)+"", DBDATE, DATE_FORMAT);
			if(intMonths == 2 || intMonths == 5 || intMonths == 11) {
				intMonths++;
			}
//			proStartDate = freqEndDt;
			freqEndDt = getFrequencyEndDate(uF, freqDate, intMonths, freqEndDt, proStrtDate, freqEdDate, cnt);
		}
//		System.out.println("freqEndDt ===>> " + freqEndDt);
		return freqEndDt;
	}
	
	public String getTravelMode(int tMode) {
		String strMode = "";
		switch(tMode){
			
			case 1:
				strMode = "Air";
				break;
			
			case 2:
				strMode = "Rail";
				break;
				
			case 3:
				strMode = "Taxi";
				break;
				
			case 4:
				strMode = "Bus";
				break;
			
		}
		return strMode;
	}
	
	public List<List<String>> getWeekDates(int month, int year, String strFormat) {
		 SimpleDateFormat format = new SimpleDateFormat(strFormat);
	        List<List<String>> weekdates = new ArrayList<List<String>>();
	        List<String> dates;
	        Calendar c = Calendar.getInstance();
	        c.set(Calendar.YEAR, year);
	        c.set(Calendar.MONTH, (month-1));
	        c.set(Calendar.DAY_OF_MONTH, 1);
	        
	        int nMonthStart = c.getActualMinimum(Calendar.DATE);
			int nMonthEnd = c.getActualMaximum(Calendar.DATE);
			String strStartDate =  (nMonthStart < 10 ? "0"+nMonthStart : nMonthStart) + "/" + ((c.get(Calendar.MONTH) + 1) < 10 ? "0"+(c.get(Calendar.MONTH) + 1):(c.get(Calendar.MONTH) + 1)) + "/" + c.get(Calendar.YEAR);
			String strEndDate =  (nMonthEnd < 10 ? "0"+nMonthEnd : nMonthEnd) + "/" + ((c.get(Calendar.MONTH) + 1) < 10 ? "0"+(c.get(Calendar.MONTH) + 1):(c.get(Calendar.MONTH) + 1)) + "/" + c.get(Calendar.YEAR);
	        
	        while (c.get(Calendar.MONTH) == (month-1)) {
	              dates = new ArrayList<String>();
	              while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
	                c.add(Calendar.DAY_OF_MONTH, -1);
	              }
	              try {
					if(c.getTime().before(format.parse(strStartDate))){
						dates.add(strStartDate);
					  } else {
						 dates.add(format.format(c.getTime()));
					  }
				} catch (ParseException e) {
					e.printStackTrace();
				}
	              
	              c.add(Calendar.DAY_OF_MONTH, 6);
	              try {
					if(c.getTime().after(format.parse(strEndDate))){
						  dates.add(strEndDate);
					  } else{
						  dates.add(format.format(c.getTime()));
					  }
				} catch (ParseException e) {
					e.printStackTrace();
				}
	              
	              c.add(Calendar.DAY_OF_MONTH, 1);
	              weekdates.add(dates);
	        }
		return weekdates;
	}
	
	public String convertInHoursMins(double totalTime) {

		String strTotal = formatIntoTwoDecimalWithOutComma(totalTime);
		
		if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
			String str = strTotal.replace(".", ":");
			String[] tempTotal = str.split(":");
			double dblHr = parseToDouble(tempTotal[1]);
			if(dblHr > 60){
				double dblPrecision = dblHr - 60;
				double dblMain = (parseToDouble(tempTotal[0])) + 1;
				strTotal = ""+((int)dblMain)+":"+((int)dblPrecision);
				strTotal = strTotal.replace(".", ":");
			}
		} else {
			strTotal = strTotal.replace(".", ":");
		}
		return strTotal;
	}
	
	public double convertHoursMinsInDouble(double totalTime) {
		double dbl = 0.0d;
		String strTotal = formatIntoTwoDecimalWithOutComma(totalTime);
		
		if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
			String str = strTotal.replace(".", ":");
			String[] tempTotal = str.split(":");
			double dblHr = parseToDouble(tempTotal[1]);
			if(dblHr > 60){
				double dblPrecision = dblHr - 60;
				double dblMain = (parseToDouble(tempTotal[0])) + 1;
				strTotal = ""+((int)dblMain)+"."+((int)dblPrecision);
			}
		}
		dbl = parseToDouble(strTotal);
		return dbl;
	} 
	
	public String showTime(String strTime) {
		String strTimeVal = "00:00";
		String strTotal = formatIntoTwoDecimalWithOutComma(parseToDouble(strTime));
		
		if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
			String str = strTotal.replace(".", ":");
			String[] tempTotal = str.split(":");
			double dblMain = (parseToDouble(tempTotal[0]));
			String strNegativeSign="";
			
			if(dblMain < 0.0d){
				dblMain = Math.abs(dblMain);
				strNegativeSign = "-";
			}
			
			double dblPrecision =  parseToDouble(tempTotal[1]);
			strTimeVal = ""+((int)dblMain < 10 ? strNegativeSign+"0"+(int)dblMain : strNegativeSign+(int)dblMain)+":"+((int)dblPrecision < 10 ? "0"+(int)dblPrecision : (int)dblPrecision);
		} else {
			double dblMain = parseToDouble(strTotal);
			String strNegativeSign="";
			if(dblMain < 0.0d){
				dblMain = Math.abs(dblMain);
				strNegativeSign = "-";
			}
			strTimeVal = ""+((int)dblMain < 10 ? strNegativeSign+"0"+(int)dblMain : strNegativeSign+(int)dblMain)+":00";
		}
		
		return strTimeVal;
	}

	public String getRoundOffValue(int roundOffCondition, double dblAmount) {
		String roundOffVal = "";
		if(roundOffCondition == 1){
			roundOffVal = formatIntoOneDecimalWithOutComma(dblAmount);
		} else if(roundOffCondition == 2){
			roundOffVal = formatIntoTwoDecimalWithOutComma(dblAmount);
		} else {
			roundOffVal = ""+Math.round(dblAmount);
		}
		
		return roundOffVal;
	}

	public boolean isThisTimeValid(String dataType, SimpleDateFormat format) {
		
		boolean status=false;
		
		if(dataType == null  || dataType=="" || dataType.length()==0 || dataType.equals("null") || dataType.equals("-")){
			status=false;
		}
		try {
			
			if(dataType.contains(":")){
				Date time = format.parse(dataType);
				String result = format.format(time);
				status = true;
			}else{
				status = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			status=false;
		}
		return status;
	} 

	public String compareShiftTime(String strFromTime, String strFromTimeFormat, String strToTime, String strToTimeFormat) {
		String strShiftTime = "Regular"; 
		if(strFromTime != null && !strFromTime.trim().equals("") && !strFromTime.trim().equalsIgnoreCase("NULL") 
				&& strToTime != null && !strToTime.trim().equals("") && !strToTime.trim().equalsIgnoreCase("NULL")){
			long fromTime = getTimeFormat(strFromTime, strFromTimeFormat).getTime();
			long toTime = getTimeFormat(strToTime, strToTimeFormat).getTime();
//			System.out.println("UF/3651--fromTime=="+fromTime+"--toTime=="+toTime);
			if(fromTime > toTime){
				strShiftTime = "Over Night";
			} else if(fromTime < toTime){
				strShiftTime = "Regular";
			}
//			System.out.println("UF/3657--strShiftTime=="+strShiftTime);
		}
		return strShiftTime;
	}
	
	
	public double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }
	        
	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
//	         expression = term | expression '+' term | expression '-' term
//	         term = factor | term '*' factor | term '/' factor
//	         factor = '+' factor | '-' factor | '(' expression ')'
//	                | number | functionName factor | factor '^' factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
	
	
	public int getShortMonthInNumber(String strMonth){ 	
		if(strMonth.equalsIgnoreCase("Jan")) {
			return 1;
		}else if(strMonth.equalsIgnoreCase("Feb")) {
			return 2;
		}else if(strMonth.equalsIgnoreCase("Mar")) {
			return 3;
		}else if(strMonth.equalsIgnoreCase("Apr")) {
			return 4;
		}else if(strMonth.equalsIgnoreCase("May")) {
			return 5;
		}else if(strMonth.equalsIgnoreCase("Jun")) {
			return 6;
		}else if(strMonth.equalsIgnoreCase("Jul")) {
			return 7;
		}else if(strMonth.equalsIgnoreCase("Aug")) {
			return 8;
		}else if(strMonth.equalsIgnoreCase("Sep")) {
			return 9;
		}else if(strMonth.equalsIgnoreCase("Oct")) {
			return 10;
		}else if(strMonth.equalsIgnoreCase("Nov")) {
			return 11;
		}else if(strMonth.equalsIgnoreCase("Dec")) {
			return 12;
		}
		return 0;
	}

	
	public String getPaymentModeExpenses(int tMode) {
		String strMode = "-";
		switch(tMode) {
			case 1:
				strMode = "Cash";
				break;
			
			case 2:
				strMode = "Debit Card";
				break;
				
			case 3:
				strMode = "Credit Card";
				break;
				
			case 4:
				strMode = "Cheque";
				break;
		}
		return strMode;
	}
	
	
	public String getTimeDurationBetweenDatesWithYearMonthDays(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, CommonFunctions CF, UtilityFunctions uF, HttpServletRequest request){
		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
			sbTimeDuration.append(period.getYears()+"::::"+period.getMonths()+"::::"+period.getDays());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sbTimeDuration.toString();
	}

	
	public List<String> getWeekDays() {
		// TODO Auto-generated method stub
		List<String> alDays = new ArrayList<String>();
		alDays.add("MON");
		alDays.add("TUE");
		alDays.add("WED");
		alDays.add("THU");
		alDays.add("FRI");
		return alDays;
	}
	
	
	public List<String> getWeekEndsFullName() {
		// TODO Auto-generated method stub
		List<String> alDays = new ArrayList<String>();
		alDays.add("SATURDAY");
		alDays.add("SUNDAY");
		return alDays;
	}
	
	public List<String> getWeekDaysFullName() {
		// TODO Auto-generated method stub
		List<String> alDays = new ArrayList<String>();
		alDays.add("MONDAY");
		alDays.add("TUESDAY");
		alDays.add("WEDNESDAY");
		alDays.add("THURSDAY");
		alDays.add("FRIDAY");
		return alDays;
	}
	
	
	public List<String> getWeekEnds() {
		// TODO Auto-generated method stub
		List<String> alDays = new ArrayList<String>();
		alDays.add("SAT");
		alDays.add("SUN");
		return alDays;
	}

	
	public String getWeekNoOfTheDateInMonth(String strStartDt, String strD1, String strDateFormat) {
		UtilityFunctions uF = new UtilityFunctions();
		 
		String diff = uF.dateDifference(strStartDt, strDateFormat, strD1, strDateFormat);
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DATE, parseToInt(getDateFormat(strD1, strDateFormat, "dd")));
		cal.set(Calendar.MONTH, parseToInt(getDateFormat(strD1, strDateFormat, "MM"))-1);
		cal.set(Calendar.YEAR, parseToInt(getDateFormat(strD1, strDateFormat, "yyyy")));
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int currentWeekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);
		
		if(uF.parseToInt(diff)<=35 && currentWeekOfMonth==6) {
			currentWeekOfMonth = currentWeekOfMonth-1;
		} else if(uF.parseToInt(diff)<=28 && currentWeekOfMonth==5) {
			currentWeekOfMonth = currentWeekOfMonth-1;
		} else if(uF.parseToInt(diff)<=21 && currentWeekOfMonth==4) {
			currentWeekOfMonth = currentWeekOfMonth-1;
		} else if(uF.parseToInt(diff)<=14 && currentWeekOfMonth==3) {
			currentWeekOfMonth = currentWeekOfMonth-1;
		} else if(uF.parseToInt(diff)<=7 && currentWeekOfMonth==2) {
			currentWeekOfMonth = currentWeekOfMonth-1;
		}
		
		return currentWeekOfMonth+"";
	}
	
	
	public String getStringValue(String str) {
		try {
			if (str.contains(".")) {
				str = String.valueOf(Double.valueOf(str).longValue());
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return str;
	}

	
	public String getAlignedSlabTypeName(Integer slabType) {
		switch (slabType) {
			case 0:
				return "Standard";
			case 1:
				return "New";
			default:
				return "-";
		}
	}

	
	public List<String> getQuarterFirstAndLastMonth(int quarterNo, int yearType) {
		List<String> alMonths = new ArrayList<String>();
		switch (quarterNo) {
			case 1:
				if(yearType == 1) {
					alMonths.add("1");
					alMonths.add("3");
				} else {
					alMonths.add("4");
					alMonths.add("6");
				}
				return alMonths;

			case 2:
				if(yearType == 1) {
					alMonths.add("4");
					alMonths.add("6");
				} else {
					alMonths.add("7");
					alMonths.add("9");
				}
				return alMonths;

			case 3:
				if(yearType == 1) {
					alMonths.add("7");
					alMonths.add("9");
				} else {
					alMonths.add("10");
					alMonths.add("12");
				}
				return alMonths;

			case 4:
				if(yearType == 1) {
					alMonths.add("10");
					alMonths.add("12");
				} else {
					alMonths.add("1");
					alMonths.add("3");
				}
				return alMonths;
			default:
				return alMonths;
		}
	}
	
	
	public List<String> getHalfYearFirstAndLastMonth(int quarterNo, int yearType) {
		List<String> alMonths = new ArrayList<String>();
		switch (quarterNo) {
			case 1:
				if(yearType == 1) {
					alMonths.add("1");
					alMonths.add("6");
				} else {
					alMonths.add("4");
					alMonths.add("9");
				}
				return alMonths;

			case 2:
				if(yearType == 1) {
					alMonths.add("7");
					alMonths.add("12");
				} else {
					alMonths.add("10");
					alMonths.add("3");
				}
				return alMonths;
			default:
				return alMonths;
		}
	}

	
	public String getMaskedNo(String strNo) {
		StringBuilder sbNo = new StringBuilder();
		try {
			if(strNo==null)
				return null;
			int strLen = strNo.length();
			for(int i=0; i<strLen; i++) {
				if(i==0 || i>(strLen-4)) {
					sbNo.append(strNo.substring(i, (i+1)));
				} else {
					sbNo.append("*");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbNo.toString();
	}
	

	public String getAppendData(String[] strData) {
		StringBuilder sbData = new StringBuilder();
		try {
			if(strData!=null) {
				for(int i = 0; i < strData.length; i++) {
					if (i == 0) {
						sbData.append(strData[i].trim());
					} else {
						sbData.append(","+strData[i].trim());
					}
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbData.toString();
	}
	
	
	public String getAppendData(String strEmpIds, Map<String, String> hmData) {
		StringBuilder sb = new StringBuilder();
		if (strEmpIds != null) {
			String[] temp = strEmpIds.split(",");
			for (int i = 1; i < temp.length; i++) {
				if(parseToInt(temp[i].trim())>0) {
					if (i == 1) {
						sb.append(hmData.get(temp[i].trim()));
					} else {
						sb.append("," + hmData.get(temp[i].trim()));
					}
				}
			}
		}
		return sb.toString();
	}
	

	/**
	 * @author Dattatray
	 * @since 23-Apr-2021
	 * 
	 * @param strDate
	 * @param strFNFStartDay
	 * @return
	 * @throws ParseException
	 */
	public String getFNFAttendanceStartDate(String strDate, String strFNFStartDay) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		java.util.Date parse = sdf.parse(strDate);
		Calendar c = Calendar.getInstance();
		c.setTime(parse);
		c.set(Calendar.DAY_OF_MONTH, parseToInt(strFNFStartDay));

		SimpleDateFormat format1 = new SimpleDateFormat(DATE_FORMAT);
		String formatted = format1.format(c.getTime());
		return formatted;
	}

	/**
	 * @author Dattatray
	 * @since 24-Apr-2021
	 * 
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public int getMonthLastDay(String strDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		java.util.Date parse = sdf.parse(strDate);
		Calendar c = Calendar.getInstance();
		c.setTime(parse);
		int lastDate = c.getActualMaximum(Calendar.DATE);
		System.out.println("Last Date : " + lastDate);
		return lastDate;
	}
	
	/**
	 * @author Dattatray
	 * @since 30-Apr-2021
	 * 
	 * @param cell
	 * @param timeFormat
	 * @param dateFormat
	 * @return String Value
	 */
	public String getCellString(HSSFCell cell, HSSFWorkbook workbook,String dateFormat,String timeFormat) {
		SimpleDateFormat formatTime 	= new SimpleDateFormat(timeFormat);
		SimpleDateFormat formatDate 	= new SimpleDateFormat(dateFormat);
		SimpleDateFormat formatYearOnly = new SimpleDateFormat("yyyy");
		
		String valueStr = "";
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			valueStr = cell.getStringCellValue();
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date cellDate = cell.getDateCellValue();
				System.out.println("Date : " + cellDate);
				String dateStamp = formatYearOnly.format(cellDate);
				if (dateStamp.equals("1899")) {
					valueStr = formatTime.format(cellDate);
				} else {
					valueStr = formatDate.format(cellDate);
				}
			} else {
				valueStr = cell.toString();
			}
		} else if(cell.getCellType()== HSSFCell.CELL_TYPE_BOOLEAN) {
			valueStr = String.valueOf(cell.getBooleanCellValue());
			
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(cell);
            if (cellValue.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
//            	System.out.println("cell.toString() ===>> " + cell.toString());
				if(cell.toString().length()>0 && cell.toString().substring(0, 1).equals("0")) {
					valueStr = cell.toString();
				} else {
					valueStr = String.valueOf(cellValue.getNumberValue());
				}
//				System.out.println("valueStr ===>> " + valueStr);
            	
			}else if (cellValue.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            	valueStr = String.valueOf(cellValue.getStringValue());
			}
		}
		return valueStr;
	}

	
	
	//====start parvez on 01/06/2021=====
	public String getExcelImportDataString(XSSFCell cell, XSSFWorkbook workbook) {
		SimpleDateFormat formatTime 	= new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat formatDate 	= new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatYearOnly = new SimpleDateFormat("yyyy");
		
		String valueStr = "";
		if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
			valueStr = cell.getStringCellValue().trim();
		} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
			if (DateUtil.isCellDateFormatted(cell)) {
				Date cellDate = cell.getDateCellValue();
				String dateStamp = formatYearOnly.format(cellDate);
				if (dateStamp.equals("1899")) {
					valueStr = formatTime.format(cellDate).trim();
				} else {
					valueStr = formatDate.format(cellDate).trim();
				}
			} else {
				valueStr = String.valueOf(formatIntoDecimalIfDecimalValIsThere(cell.getNumericCellValue()).trim());
			}
		} else if(cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
			valueStr = String.valueOf(cell.getBooleanCellValue()).trim();
		} else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(cell);
            if (cellValue.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
//            	valueStr = String.valueOf(cellValue.getNumberValue()).trim();
            	valueStr = String.valueOf(formatIntoDecimalIfDecimalValIsThere(cellValue.getNumberValue()).trim());
			} else if (cellValue.getCellType() == XSSFCell.CELL_TYPE_STRING) {
            	valueStr = String.valueOf(cellValue.getStringValue()).trim();
			}
		} else {
			valueStr = String.valueOf(cell);
		}
		return valueStr;
	}
	//====end parvez on 01/06/2021=====
	
	
	
	/**
	 * Created By Dattatray
	 * @param startRating 
	 * @return
	 */
	public String getReportStatus(double startRating) {
		String message="";
		if (startRating<=1) {
			//message = "Does not meet Expectations";
			message = "<h3 style=\"color: #d20010;font-weight: bold;\">Does not meet Expectations</h3>";
		}else if (startRating > 1 && startRating <= 2) {
			//message = "Needs Improvement";
			message = "<h3 style=\"color: #efae26;font-weight: bold;\">Needs Improvement</h3>";
		}else if (startRating > 2 && startRating <= 3) {
			//message = "Meets Expectations";
			message = "<h3 style=\"color: #f6d83c;font-weight: bold;\">Meets Expectations</h3>";
		}else if (startRating > 3 && startRating <= 4) {
			//message = "Exceeds Expectations";
			message = "<h3 style=\"color: #3fce4b;font-weight: bold;\">Exceeds Expectations</h3>";
		}else if (startRating > 4 && (startRating <= 5 || startRating > 5 )) {
			//message = "Outstanding";
			message = "<h3 style=\"color: #00602e;font-weight: bold;\">Outstanding</h3>";
		}
		return message;
	}
	
//===created by parvez date: 03-04-2023===	
	public String getReportStatusForTenStarRating(double startRating) {
		String message="";
		System.out.println("startRating=="+startRating);
		if (startRating<=1) {
			//message = "Does not meet Expectations";
			message = "<h3 style=\"color: #d20010;font-weight: bold;\">Below expectations for their current level</h3>";
		}else if (startRating > 1 && startRating <= 3) {
			//message = "Needs Improvement";
			message = "<h3 style=\"color: #e87b00;font-weight: bold;\">Somewhat meets or below expectations for their current level</h3>";
		}else if (startRating > 3 && startRating <= 4) {
			//message = "Meets Expectations";
			message = "<h3 style=\"color: #f6d83c;font-weight: bold;\">Meets expectations for their current level</h3>";
		}else if (startRating > 3 && startRating <= 4) {
			//message = "Exceeds Expectations";
			message = "<h3 style=\"color: #f6d83c;font-weight: bold;\">Exceeds Expectations</h3>";
		}else if (startRating > 4 && startRating <= 5) {
			//message = "Outstanding";
			message = "<h3 style=\"color: #ffc000;font-weight: bold;\">Meets expectations for a subset of the level above</h3>";
		}else if (startRating > 5 && startRating <= 8) {
			//message = "Outstanding";
			message = "<h3 style=\"color: #3fce4b;font-weight: bold;\">Meets expectations and most of the times exceeds expectation across the majority of the specified criteria for the level above their current level</h3>";
		}else if (startRating > 8 && (startRating <= 10 || startRating > 10 )) {
			//message = "Outstanding";
			message = "<h3 style=\"color: #00602e;font-weight: bold;\">Always exceeds expectations across all specified criteria for the level above their current level, demonstrates role model behaviour</h3>";
		}
		return message;
	}
	
	/**
	 * Created By Dattatray
	 * @param startRating
	 * @return
	 */
	public String getRatingStatus(double startRating) {
		String message="";
		if (startRating<=1) {
			message = "Poor";
		}else if (startRating > 1 && startRating <= 2) {
			message = "Fair";
		}else if (startRating > 2 && startRating <= 3) {
			message = "Good";
		}else if (startRating > 3 && startRating <= 4) {
			message = "Very Good";
		}else if (startRating > 4 && (startRating <= 5)) {
			message = "Excellent";
		}
		return message;
	}

	/**
	 *  Created By Dattatray
	 * @param id
	 * @return
	 */
	public Map<String, String> getAppraisalReportColor(int id){
		Map<String, String> hmColor = new HashMap<String, String>();
		hmColor.put("2", "#00c7fd");
		hmColor.put("3", "#008df7");
		hmColor.put("4", "#00bbfd");
		//hmColor.put("5", "");
		hmColor.put("6", "#004e8f");
		//hmColor.put("7", "");
		hmColor.put("8", "#008df7");
		//hmColor.put("10", "");
		//hmColor.put("13", "");
		//hmColor.put("14", "");
		return hmColor;
	}

	public String getPreviousYear(Date date) {
	    Calendar prevYear = Calendar.getInstance();
	    prevYear.setTime(date);
	    prevYear.add(Calendar.YEAR, -1);
	    String fYr = prevYear.get(Calendar.YEAR)+"- "+new UtilityFunctions().getDateFormatUtil(date, "yy");
	    return fYr;
	}
	
	public boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}

	
	public String getVacancyType(String str) {
		if (str != null && str.equalsIgnoreCase("New Requirement")) {
			return "1";
		} else if (str != null && str.equalsIgnoreCase("Replacement")) {
			return "2";
		} else if (str != null && str.equalsIgnoreCase("Staffing Requirement")) {
			return "3";
		} else {
			return "";
		}
	}
	
	
	public String formatIntoDecimalIfDecimalValIsThere(double dblVal) {
		String strVal = dblVal+"";
		if(strVal!=null && strVal.contains(".") && strVal.indexOf(".")>0) {
			String[] strTmp = strVal.replace(".", ",").split(",");
			if(parseToInt(strTmp[1])>0) {
				return strVal;
			} else {
				return strTmp[0];
			}
		}
		return oneDecimal.format(dblVal);
	}
	

	public Map<String, String> getSourcesIdMap(){
	
		Map<String, String> hmSourceId = new HashMap<String, String>();
		hmSourceId.put(SOURCE_HR_LBL, ""+SOURCE_HR);
		hmSourceId.put(SOURCE_CONSULTANT_LBL, ""+SOURCE_CONSULTANT);
		hmSourceId.put(SOURCE_REFERENCE_LBL, ""+SOURCE_REFERENCE);
		hmSourceId.put(SOURCE_WEBSITE_LBL, ""+SOURCE_WEBSITE);
		hmSourceId.put(SOURCE_WALK_IN_LBL, ""+SOURCE_WALK_IN);
		hmSourceId.put(SOURCE_OTHER_LBL, ""+SOURCE_OTHER);
		hmSourceId.put(SOURCE_CONSULTANT_LBL, ""+SOURCE_CONSULTANT);
		hmSourceId.put(SOURCE_JOB_PORTAL_LBL, ""+SOURCE_JOB_PORTAL);
		hmSourceId.put(SOURCE_SOCIAL_SITES_LBL, ""+SOURCE_SOCIAL_SITES);
		return hmSourceId;
	}


	/**
	  * 
	  * @author Dattatray
	  * @since 31-07-21
	  * 
	 * @param str
	 * @param start
	 * @param end
	 * @param startInclusive
	 * @param endInclusive
	 * @param replaceWith
	 * @return
	 */
	public String replaceBetweenTwoString(String str,String start, String end,boolean startInclusive,boolean endInclusive,String replaceWith) {
      int i = str.indexOf(start);
      while (i != -1) {
          int j = str.indexOf(end, i + 1);
          if (j != -1) {
              String data = (startInclusive ? str.substring(0, i) : str.substring(0, i + start.length())) +
                      replaceWith;
              String temp = (endInclusive ? str.substring(j + end.length()) : str.substring(j));
              data += temp;
              str = data;
              i = str.indexOf(start, i + replaceWith.length() + end.length() + 1);
          } else {
              break;
          }
      }
      return str;
	}
	
	public Timestamp getEndDateTime(String strStartDateTime) {
		if(strStartDateTime!=null) {
//			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(strTimeZone));
			Timestamp tDateTime = getTimeStamp(strStartDateTime, DBTIMESTAMP);
			Calendar cal = GregorianCalendar.getInstance();
	        cal.setTime(tDateTime);
	        cal.add(java.util.Calendar.MINUTE, 30);
			Timestamp dt = new Timestamp(cal.get(Calendar.YEAR) - 1900 , cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), 0);
			return new Timestamp((dt.getTime()));
		}else{
			return null;
		}
	}


	/**
	 * @author Dattatray 
	 * @since  13-09-21
	 * 
	 * @param address1
	 * @param address2
	 * @param city
	 * @param newline
	 * @param uF
	 * @return Address field
	 */
	public String getAddress(String address1, String address2, String city,String newline,UtilityFunctions uF) {
		if (!uF.showData(address1, "").isEmpty() && uF.showData(address2, "").isEmpty() && uF.showData(city, "").isEmpty()) {
			return address1;
		}else if (!uF.showData(address1, "").isEmpty() && !uF.showData(address2, "").isEmpty() && uF.showData(city, "").isEmpty()) {
			return address1+","+newline+address2;
		}else if (uF.showData(address1, "").isEmpty() && !uF.showData(address2, "").isEmpty() && uF.showData(city, "").isEmpty()) {
			return address2;
		}else if (uF.showData(address1, "").isEmpty() && !uF.showData(address2, "").isEmpty() && !uF.showData(city, "").isEmpty()) {
			return address2+","+newline+city;
		}else  if (uF.showData(address1, "").isEmpty() && uF.showData(address2, "").isEmpty() && !uF.showData(city, "").isEmpty()) {
			return city;
		}else if (!uF.showData(address1, "").isEmpty() && uF.showData(address2, "").isEmpty() && !uF.showData(city, "").isEmpty()) {
			return address1+","+newline+city;
		}else if (uF.showData(address1, "").isEmpty() && uF.showData(address2, "").isEmpty() && uF.showData(city, "").isEmpty()) {
			return "";
		}else{
			return address1+","+newline+address2+","+newline+city;
		}
	}
	
	
	/**
	 * @author Dattatray
	 * @since 08-10-21
	 * @param uF
	 * @param strDate
	 * @param strFirstTime
	 * @param strSecondTime
	 * @return
	 */
	public Timestamp getGreaterDateTimeException(UtilityFunctions uF,String strDate, String strFirstTime, String strSecondTime) {
		String plusOutDate = null;
		try{
			Time in_time = uF.getTimeFormat(strDate+ " " + strFirstTime,DBDATE + " " + DBTIME);
			Time out_time = uF.getTimeFormat(strDate+ " " + strSecondTime,DBDATE + " " + DBTIME);
			
			long lnInTime = in_time.getTime();
			long lnOutTime = out_time.getTime();
			
			if(lnInTime > lnOutTime) {
				plusOutDate = uF.getDateFormatUtil(uF.getNextDate(uF.getDateFormatUtil(strDate, DBDATE), 1), DBDATE)+" "+strSecondTime;
			}else {
				plusOutDate = uF.getTimeStamp(strDate + " " + strSecondTime, DBDATE + " " + DBTIME)+"";
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uF.getTimeStamp(plusOutDate, DBDATE + " " + DBTIME);
	}

	
	/**
	 * @author Dattatray
	 * @since 21-10-21
	 * 
	 * @param date
	 * @return
	 */
	public int getMonthInt(Date date) {

	    SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
	    return Integer.parseInt(dateFormat.format(date));
	}

	/**
	 * @author Dattatray
	 * @since 09-12-21
	 * 
	 * @param start_date
	 * @param end_date
	 * @param uF
	 * @return
	 */
	public String findOTDateTimeDifference(String start_date, String end_date,UtilityFunctions uF) {
		String i="0.0";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		try {

			Date d1 = sdf.parse(start_date);
			Date d2 = sdf.parse(end_date);

			long difference_In_Time = uF.getFutureDate(d2, 1).getTime() - d1.getTime();
			long difference_In_Seconds = (difference_In_Time / 1000) % 60;
			long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
			long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
			long difference_In_Years = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
			long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

			System.out.print("Difference " + "between two dates is: ");
			System.out.println(difference_In_Years + " years, " + difference_In_Days + " days, " + difference_In_Hours
					+ " hours, " + difference_In_Minutes + " minutes, " + difference_In_Seconds + " seconds");
			i = difference_In_Hours+"."+difference_In_Minutes;
			System.out.println("OT Hours : "+i);
		}

		// Catch the Exception
		catch (ParseException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

		
}


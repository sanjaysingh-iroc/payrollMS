package com.konnect.jpms.export;

 
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
 
public class Excelstyle {
	HSSFWorkbook hwb;
	HSSFFont newFont;
	HSSFFont fontDay;
	HSSFFont fontBold;
	HSSFFont fontDate;
	HSSFFont fontDateB;
	HSSFFont fontDateBB;
	HSSFFont fontTime;
	HSSFFont fontTotalWorkRow;
	HSSFFont fontAddedWorkRow;
	HSSFFont fontDataRow;
	HSSFFont fontHeader;
	HSSFFont fontWorkHour;
	HSSFFont fontPayHour;
	HSSFFont fontHeadingRow;
	HSSFFont fontHead;
	HSSFFont fontFoot;
	HSSFFont fontHeadRowCellData;
	HSSFFont hyperLink;
	HSSFFont fontSign;
	HSSFFont fontFinal;
	
	public Excelstyle(HSSFWorkbook hwb){
		this.hwb = hwb;
		fontDay = hwb.createFont();
		fontBold = hwb.createFont();
		fontDate = hwb.createFont();
		fontDateB = hwb.createFont();
		fontDateBB = hwb.createFont();
		fontTime = hwb.createFont();
		fontTotalWorkRow = hwb.createFont();
		fontAddedWorkRow = hwb.createFont();
		fontDataRow = hwb.createFont();
		fontHeader = hwb.createFont();
		fontWorkHour = hwb.createFont();
		fontPayHour = hwb.createFont();
		fontHeadingRow = hwb.createFont();
		fontHead = hwb.createFont();
		fontFoot = hwb.createFont();
		fontHeadRowCellData = hwb.createFont();
		hyperLink = hwb.createFont();
		fontSign = hwb.createFont();
		fontFinal = hwb.createFont();
		
		

		
	}

	public HSSFCellStyle dayRowStyle(){
		
		HSSFCellStyle dayRowcellStyle= hwb.createCellStyle();
//		HSSFFont dayRowfont = hwb.createFont();
		HSSFFont dayRowfont = fontDay;
		dayRowfont.setFontHeightInPoints((short)10);
		dayRowfont.setFontName("Century Gothic");
		dayRowfont.setColor(HSSFColor.WHITE.index);
		dayRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		dayRowcellStyle.setFont(dayRowfont);		
		
		dayRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		
		
		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.AQUA.index,
		        (byte) 128,  //RGB red (0-255)
		        (byte) 128,    //RGB green
		        (byte) 128     //RGB blue
		);
		dayRowcellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
		dayRowcellStyle.setBorderTop((short) 1);
		dayRowcellStyle.setBorderBottom((short)1);
		dayRowcellStyle.setBorderLeft((short)1);
		dayRowcellStyle.setBorderRight((short)1);
		dayRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		dayRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		return dayRowcellStyle;
	}

	public HSSFCellStyle bold(boolean isTop, boolean isBottom, boolean isRightAlign, int boderSize){
		
		HSSFCellStyle cellStyle= hwb.createCellStyle();
		HSSFFont dayRowfont = fontBold;
//		dayRowfont.setFontHeightInPoints((short)10);
		dayRowfont.setFontName("Century Gothic");
		dayRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellStyle.setFont(dayRowfont);
		cellStyle.setWrapText(true);
		
		if(isTop){
			cellStyle.setBorderTop((short) boderSize);
		}
		if(isBottom){
			cellStyle.setBorderBottom((short) boderSize);
		}
		if(isRightAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		
		
		return cellStyle;
	}

	public HSSFCellStyle yellowColour(boolean isTop, boolean isBottom, boolean isRightAlign){
		
		HSSFCellStyle cellStyle= hwb.createCellStyle();
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		
		if(isTop){
			cellStyle.setBorderTop((short) 2);
		}
		if(isBottom){
			cellStyle.setBorderBottom((short)2);
		}
		if(isRightAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		
		return cellStyle;
	}
	
	public HSSFCellStyle whiteColour(boolean isTop, boolean isBottom, boolean isRightAlign){
		
		HSSFCellStyle cellStyle= hwb.createCellStyle();
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		
		if(isTop){
			cellStyle.setBorderTop((short) 2);
		}
		if(isBottom){
			cellStyle.setBorderBottom((short)2);
		}
		if(isRightAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		
		return cellStyle;
	}
	
	
	public HSSFCellStyle redColour(boolean isTop, boolean isBottom, boolean isRightAlign){
		
		HSSFCellStyle cellStyle= hwb.createCellStyle();
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		cellStyle.setFillForegroundColor(HSSFColor.RED.index);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		
		if(isTop){
			cellStyle.setBorderTop((short) 1);
		}
		if(isBottom){
			cellStyle.setBorderBottom((short)1);
		}
		if(isRightAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		
		return cellStyle;
	}

	public HSSFCellStyle greyColour(boolean isTop, boolean isBottom, boolean isRightAlign){
	
		HSSFCellStyle cellStyle= hwb.createCellStyle();
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		
		if(isTop){
			cellStyle.setBorderTop((short) 2);
		}
		if(isBottom){
			cellStyle.setBorderBottom((short)2);
		}
		if(isRightAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		
		return cellStyle;
	}
	
	public HSSFCellStyle pinkColour(boolean isTop, boolean isBottom, boolean isRightAlign){
		
		HSSFCellStyle cellStyle= hwb.createCellStyle();
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		
		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.PINK.index,
				
				(byte) 255,  //RGB red (0-255)
		        (byte) 153,    //RGB green
		        (byte) 255     //RGB blue
		);
		
		cellStyle.setFillForegroundColor(HSSFColor.PINK.index);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		
		if(isTop){
			cellStyle.setBorderTop((short) 2);
		}
		if(isBottom){
			cellStyle.setBorderBottom((short)2);
		}
		if(isRightAlign){
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		
		return cellStyle;
	}

	
	public HSSFCellStyle dateRowStyle(){
		
		HSSFCellStyle dateRowcellStyle= hwb.createCellStyle();
//		HSSFFont dateRowfont = hwb.createFont();
		HSSFFont dateRowfont = fontDate;
		dateRowfont.setColor(HSSFColor.BLACK.index);
		dateRowfont.setFontName("Century Gothic");
		dateRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		dateRowcellStyle.setFont(dateRowfont);		
		
		dateRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );

		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.SKY_BLUE.index,
		        (byte) 197,  //RGB red (0-255)
		        (byte) 217,    //RGB green
		        (byte) 241     //RGB blue
		);
		
		dateRowcellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		dateRowcellStyle.setBorderTop((short) 1);
		dateRowcellStyle.setBorderBottom((short)1);
		dateRowcellStyle.setBorderLeft((short)1);
		dateRowcellStyle.setBorderRight((short)1);
		dateRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		dateRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		
		return dateRowcellStyle;
	}
	
	
	public HSSFCellStyle dateRowStyleBold(){
		
		HSSFCellStyle dateRowcellStyle= hwb.createCellStyle();
//		HSSFFont dateRowfont = hwb.createFont();
		HSSFFont dateRowfont = fontDateB;
		dateRowfont.setColor(HSSFColor.BLACK.index);
		dateRowfont.setFontName("Century Gothic");
		dateRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		dateRowcellStyle.setFont(dateRowfont);		
		
		dateRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );

		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.SKY_BLUE.index,
		        (byte) 197,  //RGB red (0-255)
		        (byte) 217,    //RGB green
		        (byte) 241     //RGB blue
		);
		
		dateRowcellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		dateRowcellStyle.setBorderTop((short) 1);
		dateRowcellStyle.setBorderBottom((short)1);
		dateRowcellStyle.setBorderLeft((short)1);
		dateRowcellStyle.setBorderRight((short)1);
		dateRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		dateRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		
		return dateRowcellStyle;
	}
	
public HSSFCellStyle dateRowStyleBoldBlue(){
		
		HSSFCellStyle dateRowcellStyle= hwb.createCellStyle();
//		HSSFFont dateRowfont = hwb.createFont();
		HSSFFont dateRowfont = fontDateBB;
		dateRowfont.setColor(HSSFColor.BLUE.index);
		dateRowfont.setFontName("Century Gothic");
		dateRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		dateRowcellStyle.setFont(dateRowfont);		
		
		dateRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );

		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.SKY_BLUE.index,
		        (byte) 197,  //RGB red (0-255)
		        (byte) 217,    //RGB green
		        (byte) 241     //RGB blue
		);
		
		dateRowcellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		dateRowcellStyle.setBorderTop((short) 1);
		dateRowcellStyle.setBorderBottom((short)1);
		dateRowcellStyle.setBorderLeft((short)1);
		dateRowcellStyle.setBorderRight((short)1);
		dateRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		dateRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		
		return dateRowcellStyle;
	}
	
	
	
	
	public HSSFCellStyle startimeRowStyle(){
		
		HSSFCellStyle startimeRowcellStyle= hwb.createCellStyle();
//		HSSFFont startimeRowfont = hwb.createFont();
		HSSFFont startimeRowfont = fontTime;
		startimeRowfont.setColor(HSSFColor.BLACK.index);
		startimeRowfont.setFontName("Century Gothic");
		startimeRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		
		startimeRowcellStyle.setFont(startimeRowfont);		
		
		startimeRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		startimeRowcellStyle.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
		startimeRowcellStyle.setBorderTop((short) 1);
		startimeRowcellStyle.setBorderBottom((short)1);
		startimeRowcellStyle.setBorderLeft((short)1);
		startimeRowcellStyle.setBorderRight((short)1);
		startimeRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		startimeRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		return startimeRowcellStyle;
	}
 	
	public HSSFCellStyle startTimeRowStylePublicHoliday(){
		
		HSSFCellStyle startimeRowcellStyle= hwb.createCellStyle();
//		HSSFFont startimeRowfont = hwb.createFont();
		HSSFFont startimeRowfont = fontTime;
		startimeRowfont.setColor(HSSFColor.BLACK.index);
		startimeRowfont.setFontName("Century Gothic");
		startimeRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		
		startimeRowcellStyle.setFont(startimeRowfont);		
		
		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.ORANGE.index,
				(byte) 255,  //RGB red (0-255)
		        (byte) 192,    //RGB green
		        (byte) 0     //RGB blue
		);
		
		startimeRowcellStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
		startimeRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		startimeRowcellStyle.setBorderTop((short) 1);
		startimeRowcellStyle.setBorderBottom((short)1);
		startimeRowcellStyle.setBorderLeft((short)1);
		startimeRowcellStyle.setBorderRight((short)1);
		startimeRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		startimeRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		return startimeRowcellStyle;
	}
	
	
	public HSSFCellStyle startTimeRowStyleSickLeave(){
		
		HSSFCellStyle startimeRowcellStyle= hwb.createCellStyle();
//		HSSFFont startimeRowfont = hwb.createFont();
		HSSFFont startimeRowfont = fontTime;
		startimeRowfont.setColor(HSSFColor.BLACK.index);
		startimeRowfont.setFontName("Century Gothic");
		startimeRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		
		startimeRowcellStyle.setFont(startimeRowfont);	
		
		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.BLUE_GREY.index,
				
				(byte) 0,  //RGB red (0-255)
		        (byte) 204,    //RGB green
		        (byte) 255     //RGB blue
		);
		
		startimeRowcellStyle.setFillForegroundColor(HSSFColor.BLUE_GREY.index);
		startimeRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		startimeRowcellStyle.setBorderTop((short) 1);
		startimeRowcellStyle.setBorderBottom((short)1);
		startimeRowcellStyle.setBorderLeft((short)1);
		startimeRowcellStyle.setBorderRight((short)1);
		startimeRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		startimeRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		return startimeRowcellStyle;
	}
	
	public HSSFCellStyle startTimeRowStyleAnnualLeave(){
		
		HSSFCellStyle startimeRowcellStyle= hwb.createCellStyle();
//		HSSFFont startimeRowfont = hwb.createFont();
		HSSFFont startimeRowfont = fontTime;
		startimeRowfont.setColor(HSSFColor.BLACK.index);
		startimeRowfont.setFontName("Century Gothic");
		startimeRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		
		startimeRowcellStyle.setFont(startimeRowfont);		
			
		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.GREEN.index,
				
				(byte) 146,  //RGB red (0-255)
		        (byte) 208,    //RGB green
		        (byte) 80     //RGB blue
		);
		
		startimeRowcellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		startimeRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		startimeRowcellStyle.setBorderTop((short) 1);
		startimeRowcellStyle.setBorderBottom((short)1);
		startimeRowcellStyle.setBorderLeft((short)1);
		startimeRowcellStyle.setBorderRight((short)1);
		startimeRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		startimeRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		return startimeRowcellStyle;
	}


	
 	public HSSFCellStyle TotalWorkRowStyle(){
		
		HSSFCellStyle TotalWorkRowcellStyle= hwb.createCellStyle();
//		HSSFFont TotalWorkRowfont = hwb.createFont();
		HSSFFont TotalWorkRowfont = fontTotalWorkRow;
		TotalWorkRowfont.setColor(HSSFColor.BLACK.index);
		TotalWorkRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		TotalWorkRowcellStyle.setFont(TotalWorkRowfont);			
		
		TotalWorkRowcellStyle.setBorderTop((short) 1);
		TotalWorkRowcellStyle.setBorderBottom((short)1);
		TotalWorkRowcellStyle.setBorderLeft((short)1);
		TotalWorkRowcellStyle.setBorderRight((short)1);
		TotalWorkRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		TotalWorkRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		TotalWorkRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );

		

		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index,
//		        (byte) 216,  //RGB red (0-255)
//		        (byte) 216,    //RGB green
//		        (byte) 216     //RGB blue
				
				(byte) 234,  //RGB red (0-255)
		        (byte) 234,    //RGB green
		        (byte) 234     //RGB blue
		);
		
		TotalWorkRowcellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		return TotalWorkRowcellStyle;
	}
 	
 	public HSSFCellStyle addedWorkRowStyle(){
		
		HSSFCellStyle TotalWorkRowcellStyle= hwb.createCellStyle();
//		HSSFFont TotalWorkRowfont = hwb.createFont();
		HSSFFont TotalWorkRowfont = fontAddedWorkRow;
		
		TotalWorkRowfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		TotalWorkRowfont.setColor(HSSFColor.WHITE.index);
		
		TotalWorkRowcellStyle.setFont(TotalWorkRowfont);			
		
		TotalWorkRowcellStyle.setBorderTop((short) 1);
		TotalWorkRowcellStyle.setBorderBottom((short)1);
		TotalWorkRowcellStyle.setBorderLeft((short)1);
		TotalWorkRowcellStyle.setBorderRight((short)1);
		TotalWorkRowcellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		TotalWorkRowcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		TotalWorkRowcellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );

		

		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.GREY_40_PERCENT.index,
		        (byte) 0,  //RGB red (0-255)
		        (byte) 0,    //RGB green
		        (byte) 128     //RGB blue
		);
		
		TotalWorkRowcellStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
		return TotalWorkRowcellStyle;
	}
	
 	public HSSFCellStyle setDataRowStyle(){
		
//		HSSFFont datafont = hwb.createFont();
 		HSSFFont datafont = fontDataRow;
		HSSFCellStyle DatacellStyle= hwb.createCellStyle();
		
		datafont.setFontHeightInPoints((short)10);
    	datafont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
    	datafont.setColor(HSSFColor.BLUE_GREY.index);
    	datafont.setFontName("Century Gothic");
    	DatacellStyle.setFont(datafont);  
    	DatacellStyle.setAlignment(HSSFCellStyle.ALIGN_JUSTIFY);
		DatacellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
    	DatacellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
    	DatacellStyle.setWrapText(true);    
    	HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.CORAL.index,
		        (byte) 207,  //RGB red (0-255)
		        (byte) 207,    //RGB green
		        (byte) 207     //RGB blue
		);
    	DatacellStyle.setFillForegroundColor(HSSFColor.CORAL.index);
    	return DatacellStyle;   	
  }
 	
 	public HSSFCellStyle setHeaderStyle(){
	
//		HSSFFont Headerfont = hwb.createFont();
 		HSSFFont Headerfont = fontHeader;
		HSSFCellStyle HeadercellStyle= hwb.createCellStyle();
		
		Headerfont.setFontHeightInPoints((short)12);
		Headerfont.setFontName("Century Gothic");
      	Headerfont.setColor(HSSFColor.BLUE.index);
    	HeadercellStyle.setFont(Headerfont);  
    	
    	HeadercellStyle.setBorderTop((short) 1);
    	HeadercellStyle.setBorderBottom((short)1);
    	HeadercellStyle.setBorderLeft((short)1);
    	HeadercellStyle.setBorderRight((short)1);
    	HeadercellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    	HeadercellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
    	HeadercellStyle.setFillForegroundColor(new HSSFColor.GREY_50_PERCENT().getIndex());
    	return HeadercellStyle; 
    	
    }

 	public HSSFCellStyle setWorkHourStyle(){
		
//		HSSFFont  setWorkHourfont = hwb.createFont();
 		HSSFFont  setWorkHourfont = fontWorkHour;
 		
		HSSFCellStyle  setWorkHourStyle= hwb.createCellStyle();
		
		setWorkHourfont.setFontHeightInPoints((short)10);
		setWorkHourfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		setWorkHourfont.setColor(HSSFColor.WHITE.index);
		setWorkHourStyle.setFont(setWorkHourfont);  
        
		setWorkHourStyle.setBorderTop((short) 1);
		setWorkHourStyle.setBorderLeft((short)1);
		setWorkHourStyle.setBorderRight((short)1);
		setWorkHourStyle.setAlignment(HSSFCellStyle.ALIGN_JUSTIFY);
		setWorkHourStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		setWorkHourStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
	
    	
		setWorkHourStyle.setFillForegroundColor(new HSSFColor.GREY_80_PERCENT().getIndex());
    	return setWorkHourStyle;   	
  }

 	public HSSFCellStyle setMainRowStyle(){
 		 		
		HSSFCellStyle setMainRowStyle= hwb.createCellStyle();
		setMainRowStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		setMainRowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );    	
		setMainRowStyle.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
 		return setMainRowStyle;
 	}

 	public HSSFCellStyle PayPerDayRowStyle(){
		
//		HSSFFont PayPerDayfont = hwb.createFont();
 		HSSFFont PayPerDayfont = fontPayHour;
		HSSFCellStyle PayPerDaycellStyle= hwb.createCellStyle();
		
		PayPerDayfont.setFontHeightInPoints((short)10);
		PayPerDayfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		
		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.BLUE.index,
		        (byte) 51,  //RGB red (0-255)
		        (byte) 102,    //RGB green
		        (byte) 255     //RGB blue
		);
		
		
		PayPerDayfont.setColor(HSSFColor.BLUE.index);
		PayPerDaycellStyle.setFont(PayPerDayfont);  
        PayPerDaycellStyle.setBorderLeft((short)1);
        PayPerDaycellStyle.setBorderRight((short)1);
		PayPerDaycellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		PayPerDaycellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		PayPerDaycellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		    	
		PayPerDaycellStyle.setFillForegroundColor(new HSSFColor.GREY_25_PERCENT().getIndex());
    	return PayPerDaycellStyle;   	
  }
 	
 	public HSSFCellStyle HeadingRowStyle(){
	 	
 		HSSFCellStyle setHeadingRowStyle= hwb.createCellStyle();
// 		HSSFFont  setHeadingfont = hwb.createFont();
 		HSSFFont  setHeadingfont = fontHeadingRow;
 		
 		setHeadingfont.setFontHeightInPoints((short)18);
		setHeadingfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFPalette palette = hwb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.BRIGHT_GREEN.index,
//		        (byte) 128,  //RGB red (0-255)
//		        (byte) 0,    //RGB green
//		        (byte) 128     //RGB blue
				
				(byte) 188,  //RGB red (0-255)
		        (byte) 188,    //RGB green
		        (byte) 188     //RGB blue
		);
		setHeadingfont.setColor(HSSFColor.BRIGHT_GREEN.index);
		
		setHeadingRowStyle.setFont(setHeadingfont); 
		
		setHeadingRowStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		setHeadingRowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );    	
		setHeadingRowStyle.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
 		return setHeadingRowStyle;
 	}

 	public HSSFCellStyle headRowcellStyle(){
	 	
// 		HSSFFont Headfont = hwb.createFont();
 		HSSFFont Headfont = fontHead;
		HSSFCellStyle HeadcellStyle= hwb.createCellStyle();
 		
		
		Headfont.setFontName("Century Gothic");
		ExtendedFormatRecord e = new ExtendedFormatRecord();
		HeadcellStyle.setDataFormat(e.getFormatIndex());
		e.setShrinkToFit(true);
		
		
		
		Headfont.setFontHeightInPoints((short)10);
		Headfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		Headfont.setColor(HSSFColor.GREY_50_PERCENT.index);		
		HeadcellStyle.setFont(Headfont);		
		HeadcellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HeadcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
	
    	return HeadcellStyle;   	
 	}
 	
 	public HSSFCellStyle HeadRowcellDataStyle(){
	 	
// 		HSSFFont Headfont = hwb.createFont();
 		HSSFFont Headfont = fontHeadRowCellData;
		HSSFCellStyle HeadcellStyle= hwb.createCellStyle();
 		
		
		Headfont.setFontName("Century Gothic");
		Headfont.setFontHeightInPoints((short)10);
		Headfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		Headfont.setColor(HSSFColor.BLACK.index);		
		HeadcellStyle.setFont(Headfont);
		HeadcellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		HeadcellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		HeadcellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HeadcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
	
    	return HeadcellStyle;   	
 	}
 	
 	
 	public HSSFCellStyle hyperLinkStyle(){
	 	
 		HSSFFont Headfont = hyperLink;
		HSSFCellStyle HeadcellStyle= hwb.createCellStyle();
 		
		
		Headfont.setFontName("Century Gothic");
		Headfont.setFontHeightInPoints((short)12);
		Headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		Headfont.setUnderline(HSSFFont.U_SINGLE);
		Headfont.setColor(HSSFColor.BLACK.index);		
		HeadcellStyle.setFont(Headfont);
		HeadcellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
	
    	return HeadcellStyle;   	
 	}

 	public HSSFCellStyle FootRowcellStyle(){
	 	
// 		HSSFFont Footfont = hwb.createFont();
 		HSSFFont Footfont = fontFoot;
		HSSFCellStyle FootcellStyle= hwb.createCellStyle();
		Footfont.setFontName("Century Gothic");
		Footfont.setFontHeightInPoints((short)10);
		FootcellStyle.setFont(Footfont);  
		FootcellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	
    	return FootcellStyle;   	
 	}
 	public HSSFCellStyle FootRowcellStyleBold(){
	 	
// 		HSSFFont Footfont = hwb.createFont();
 		HSSFFont Footfont = fontFoot;
		HSSFCellStyle FootcellStyle= hwb.createCellStyle();
		Footfont.setFontName("Century Gothic");
		Footfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		Footfont.setFontHeightInPoints((short)10);
		FootcellStyle.setFont(Footfont);  
		FootcellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
	
    	return FootcellStyle;   	
 	}
 	
 	public HSSFCellStyle SigncellStyle(){
	 	
// 		HSSFFont Signfont = hwb.createFont();
 		HSSFFont Signfont = fontSign;
		HSSFCellStyle SigncellStyle= hwb.createCellStyle();
		
		Signfont.setFontHeightInPoints((short)7);
		SigncellStyle.setFont(Signfont);  
		SigncellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		
    	return SigncellStyle;   	
 	}
 	
 	 	
 	public HSSFCellStyle FinalCellStyle(){
		
//		HSSFFont FinalCellfont = hwb.createFont();
 		HSSFFont FinalCellfont = fontFinal;
		HSSFCellStyle FinalStyle= hwb.createCellStyle();
		
		FinalCellfont.setFontHeightInPoints((short)10);
		FinalCellfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		FinalStyle.setFont(FinalCellfont);  
		
		FinalStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		//FinalStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
		FinalStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
		    	
		FinalStyle.setFillForegroundColor(new HSSFColor.YELLOW().getIndex());
    	return FinalStyle;   	
  }
 	
 	
 	
 	
}

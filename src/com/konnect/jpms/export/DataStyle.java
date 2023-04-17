package com.konnect.jpms.export;

import com.itextpdf.text.BaseColor;
public class DataStyle {

	private String strData;
	private int strAlign;
	private String strFont;
	private int strFontSize;
	private String strLeftPadding;
	private String strRightPadding;
	private BaseColor backRoundColor;
	
	private short borderStyle;
	private short HSSFbackRoundColor;
	private short fillPattern;
	private short cellDataAlign;
	//private short borderColor;

	
	public DataStyle(){
		
	}
	
	public DataStyle(String strData,int strAlign,String strFont,int strFontSize,String strLeftPadding,String strRightPadding,BaseColor backRound){
		this.strData = strData;
		this.strAlign = strAlign;
		this.strFont = strFont;
		this.strFontSize = strFontSize;
		this.strLeftPadding = strLeftPadding;
		this.strRightPadding = strRightPadding;
		this.backRoundColor = backRound;
	}
	
	public DataStyle(String strData,short borderStyle,short cellDataAlign,short backGroundColor,short fillPattern){
		this.strData = strData;
		this.borderStyle = borderStyle;
		this.cellDataAlign = cellDataAlign; 
		this.HSSFbackRoundColor = backGroundColor;
		this.fillPattern = fillPattern;
		//this.borderColor = borderColor;
	}
	

	public String getStrData() {
		return strData;
	}

	public int getStrAlign() {
		return strAlign;
	}

	public String getStrFont() {
		return strFont;
	}

	public int getStrFontSize() {
		return strFontSize;
	}

	public String getStrLeftPadding() {
		return strLeftPadding;
	}

	public String getStrRightPadding() {
		return strRightPadding;
	}

	public BaseColor getBackRoundColor() {
		return backRoundColor;
	}

	public Short getBorderStyle() {
		return borderStyle;
	}

	public Short getHSSFbackRoundColor() {
		return HSSFbackRoundColor;
	}

	public short getFillPattern() {
		return fillPattern;
	}

	public short getCellDataAlign() {
		return cellDataAlign;
	}
}

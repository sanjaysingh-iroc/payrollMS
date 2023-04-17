package com.konnect.jpms.util;

public class Navigation {

	String strLabel;
	String strLabelSelected;
	String strLabelUnSelected;
	String strAction;
	String strParent;
	String strChild;
	String strNavId;
	String strPosition;
	String strDescription; 
	String strVisibility;
	String strLabelCode;
	
	public String getStrLabel() {
		return strLabel;
	}
	
	public void setStrLabel(String strLabel) { 
		this.strLabel = strLabel;
	}
	
	public String getStrAction() {
		return strAction;
	}
	
	public void setStrAction(String strAction) {
		this.strAction = strAction;
	}
	
	public Navigation(String strNavId, String strLabel, String strLabelSelected, String strLabelUnSelected, String strAction, String strParent, String strChild, String strPosition, String strDescription, String strVisibility, String strLabelCode) {
		this.strNavId = strNavId;
		this.strLabel = strLabel;
		this.strLabelSelected = strLabelSelected;
		this.strLabelUnSelected = strLabelUnSelected;
		this.strAction = strAction;   
		this.strParent = strParent;
		this.strChild = strChild;
		this.strPosition = strPosition;
		this.strDescription = strDescription;
		this.strVisibility = strVisibility;
		this.strLabelCode = strLabelCode;
	}
	public String getStrParent() {
		return strParent;
	}
	public void setStrParent(String strParent) {
		this.strParent = strParent;
	}
	public String getStrChild() {
		return strChild;
	}
	public void setStrChild(String strChild) {
		this.strChild = strChild;
	}
	public String getStrNavId() {
		return strNavId;
	}
	public void setStrNavId(String strNavId) {
		this.strNavId = strNavId;
	}
	public String getStrPosition() {
		return strPosition;
	}
	public void setStrPosition(String strPosition) {
		this.strPosition = strPosition;
	}
	public String getStrDescription() {
		return strDescription;
	}
	public void setStrDescription(String strDescription) {
		this.strDescription = strDescription;
	}
	public String getStrLabelSelected() {
		return strLabelSelected;
	}
	public void setStrLabelSelected(String strLabelSelected) {
		this.strLabelSelected = strLabelSelected;
	}
	public String getStrLabelUnSelected() {
		return strLabelUnSelected;
	}
	public void setStrLabelUnSelected(String strLabelUnSelected) {
		this.strLabelUnSelected = strLabelUnSelected;
	}
	public String getStrVisibility() {
		return strVisibility;
	}
	public void setStrVisibility(String strVisibility) {
		this.strVisibility = strVisibility;
	}
	public String getStrLabelCode() {
		return strLabelCode;
	}
	public void setStrLabelCode(String strLabelCode) {
		this.strLabelCode = strLabelCode;
	}
	
}

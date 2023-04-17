package com.konnect.jpms.reports;

import org.apache.log4j.Logger;

import com.konnect.jpms.policies.AddAllowance;

public class UserInfo {

	String strName;
	String strContactNo;
	String strDeptname;
	String strEmpCode;
	String strEmpType;
	String strFirstAidAllowance;
	String strLink;
	
	private static Logger log = Logger.getLogger(UserInfo.class);
	
	UserInfo(String strName,
	String strContactNo,
	String strDeptname,
	String strEmpCode,
	String strEmpType,
	String strFirstAidAllowance, 
	String strLink){
		
		this.strName=strName;
		this.strContactNo=strContactNo;
		this.strDeptname=strDeptname;
		this.strEmpCode=strEmpCode;
		this.strEmpType=strEmpType;
		this.strFirstAidAllowance=strFirstAidAllowance;
		this.strLink=strLink;
		
	}
	
	
	public String getStrName() {
		return strName;
	}
	public void setStrName(String strName) {
		this.strName = strName;
	}
	public String getStrContactNo() {
		return strContactNo;
	}
	public void setStrContactNo(String strContactNo) {
		this.strContactNo = strContactNo;
	}
	public String getStrDeptname() {
		return strDeptname;
	}
	public void setStrDeptname(String strDeptname) {
		this.strDeptname = strDeptname;
	}
	public String getStrEmpCode() {
		return strEmpCode;
	}
	public void setStrEmpCode(String strEmpCode) {
		this.strEmpCode = strEmpCode;
	}
	public String getStrEmpType() {
		return strEmpType;
	}
	public void setStrEmpType(String strEmpType) {
		this.strEmpType = strEmpType;
	}
	public String getStrFirstAidAllowance() {
		return strFirstAidAllowance;
	}
	public void setStrFirstAidAllowance(String strFirstAidAllowance) {
		this.strFirstAidAllowance = strFirstAidAllowance;
	}
	public String getStrLink() {
		return strLink;
	}
	public void setStrLink(String strLink) {
		this.strLink = strLink;
	}
	
	
}

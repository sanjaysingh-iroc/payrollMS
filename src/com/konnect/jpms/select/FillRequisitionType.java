package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.IStatements;

public class FillRequisitionType  implements IStatements  {

	String requiTypeId;
	String requiTypeName;
	
	private FillRequisitionType(String requiTypeId, String requiTypeName) {
		this.requiTypeId = requiTypeId;
		this.requiTypeName = requiTypeName;
	}
	
	HttpServletRequest request;
	public FillRequisitionType(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillRequisitionType() {
	}

	public List<FillRequisitionType> fillRequisitionType(){
		List<FillRequisitionType> al = new ArrayList<FillRequisitionType>();
	
		try {

			al.add(new FillRequisitionType(""+R_N_REQUI_DOCUMENT, R_S_REQUI_DOCUMENT));
			al.add(new FillRequisitionType(""+R_N_REQUI_INFRASTRUCTURE, R_S_REQUI_INFRASTRUCTURE));
			al.add(new FillRequisitionType(""+R_N_REQUI_OTHER, R_S_REQUI_OTHER));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getRequiTypeId() {
		return requiTypeId;
	}

	public void setRequiTypeId(String requiTypeId) {
		this.requiTypeId = requiTypeId;
	}

	public String getRequiTypeName() {
		return requiTypeName;
	}

	public void setRequiTypeName(String requiTypeName) {
		this.requiTypeName = requiTypeName;
	}

}

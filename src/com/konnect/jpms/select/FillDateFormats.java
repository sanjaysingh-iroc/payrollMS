package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;

public class FillDateFormats implements IStatements {

	String dateFormatId;
	String dateFormatName;

	private FillDateFormats(String dateFormatId, String dateFormatName) {
		this.dateFormatId = dateFormatId;
		this.dateFormatName = dateFormatName;

	}

	public FillDateFormats() { 
	}

	public List<FillDateFormats> fillDateFormats() {

		List<FillDateFormats> al = new ArrayList<FillDateFormats>();
		try {

			al.add(new FillDateFormats("dd-MM-yyyy", "12-12-2000"));
			al.add(new FillDateFormats("dd/MM/yyyy", "12/12/2000"));
			al.add(new FillDateFormats("dd-MMM-yyyy", "12-Dec-2000"));
			al.add(new FillDateFormats("dd/MMM/yyyy", "12/Dec/2000"));
			al.add(new FillDateFormats("dd-MMM-yy", "12-Dec-00"));
			al.add(new FillDateFormats("dd/MMM/yy", "12/Dec/00"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getDateFormatId() {
		return dateFormatId;
	}

	public void setDateFormatId(String dateFormatId) {
		this.dateFormatId = dateFormatId;
	}

	public String getDateFormatName() {
		return dateFormatName;
	}

	public void setDateFormatName(String dateFormatName) {
		this.dateFormatName = dateFormatName;
	}

}

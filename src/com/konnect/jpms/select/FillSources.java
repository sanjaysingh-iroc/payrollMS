package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;
import com.konnect.jpms.util.IConstants;

public class FillSources implements IConstants {

	
	String sourceId;
	String sourceName;
	
	private FillSources(String sourceId, String sourceName) {
		this.sourceId = sourceId;
		this.sourceName = sourceName;
	}
	
	public FillSources(){
		
	}
	
	public List<FillSources> fillSourcesDetails(){
		List<FillSources> al = new ArrayList<FillSources>();
	
		try {
			
			al.add(new FillSources(""+SOURCE_CONSULTANT, SOURCE_CONSULTANT_LBL));
			al.add(new FillSources(""+SOURCE_REFERENCE, SOURCE_REFERENCE_LBL));
			al.add(new FillSources(""+SOURCE_WEBSITE, SOURCE_WEBSITE_LBL));
			al.add(new FillSources(""+SOURCE_WALK_IN, SOURCE_WALK_IN_LBL));
			al.add(new FillSources(""+SOURCE_OTHER, SOURCE_OTHER_LBL));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
}  


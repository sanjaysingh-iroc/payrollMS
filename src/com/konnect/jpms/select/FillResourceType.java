package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FillResourceType {

	
	String resourceTypeId;
	String resourceTypeName;
	
	private FillResourceType(String resourceTypeId, String resourceTypeName) {
		this.resourceTypeId = resourceTypeId;
		this.resourceTypeName = resourceTypeName;
	}
	
	public FillResourceType() {
	}
	
	public List<FillResourceType> fillResourceType(){
		List<FillResourceType> al = new ArrayList<FillResourceType>();
		try {
			al.add(new FillResourceType("1", "Resource"));
			al.add(new FillResourceType("2", "Project Owner"));
			al.add(new FillResourceType("3", "CEO"));
			al.add(new FillResourceType("4", "Human Resource"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	public String getResourceTypeName() {
		return resourceTypeName;
	}

	public void setResourceTypeName(String resourceTypeName) {
		this.resourceTypeName = resourceTypeName;
	}

	public static Map<String, String> getResourceData() {
		Map<String, String> hmEmpResource = new HashMap<String, String>();
		hmEmpResource.put("1", "Resource");
		hmEmpResource.put("2", "Project Owner");
		hmEmpResource.put("3", "CEO");
		hmEmpResource.put("4", "Human Resource");
		return hmEmpResource;
	}
	
	
}

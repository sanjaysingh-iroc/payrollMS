package com.konnect.jpms.task;



	
	import java.util.ArrayList;
import java.util.List;

	
	public class GetDependancyTypeList {

		String dependancyTypeId;
		String dependancyTypeName;

		

		public GetDependancyTypeList(String dependancyTypeId,
				String dependancyTypeName) {
		
			this.dependancyTypeId = dependancyTypeId;
			this.dependancyTypeName = dependancyTypeName;
		}

		public String getDependancyTypeId() {
			return dependancyTypeId;
		}

		public void setDependancyTypeId(String dependancyTypeId) {
			this.dependancyTypeId = dependancyTypeId;
		}

		public String getDependancyTypeName() {
			return dependancyTypeName;
		}

		public void setDependancyTypeName(String dependancyTypeName) {
			this.dependancyTypeName = dependancyTypeName;
		}

		public GetDependancyTypeList() {
		}

		public List<GetDependancyTypeList> fillDependancyTypeList() {
			ArrayList<GetDependancyTypeList> dependancyTypeList = new ArrayList<GetDependancyTypeList>();
			// UtilityFunctions uF = new UtilityFunctions();
			dependancyTypeList.add(new GetDependancyTypeList("0", "Start-Start"));
			dependancyTypeList.add(new GetDependancyTypeList("1", "Finish-Start"));
//			dependancyList.add(new GetDependancyTypeList("2", "High"));
			return dependancyTypeList;
		}
		
		public String getDependancy(int nDependancy) {
			
			String strDependancy="";
			switch(nDependancy){
			case 0:
				strDependancy = "Start-Start";
				break;
			case 1:
				strDependancy = "Finish-Start";
				break;
			
			}
			return strDependancy;
					
			
		}
		
	}

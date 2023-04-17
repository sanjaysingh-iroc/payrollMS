<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>

<%
 UtilityFunctions uF = new UtilityFunctions();

    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String []arrEnabledModules = CF.getArrEnabledModules();
	 String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
    Map<String, String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
  	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
  	
  	Map<String, List<String>> hmProspectiveData = (Map<String, List<String>>) request.getAttribute("hmProspectiveData");
  	if(hmProspectiveData == null) hmProspectiveData = new LinkedHashMap<String, List<String>>();
  	
  	Map<String, Map<String, Map<String, List<String>>>> hmPerspectivewiseGoalDetails = (Map<String, Map<String, Map<String, List<String>>>>) request.getAttribute("hmPerspectivewiseGoalDetails");
  	if(hmPerspectivewiseGoalDetails == null) hmPerspectivewiseGoalDetails = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
  	
  	Map<String, List<List<String>>> hmGoalKRADetails = (Map<String, List<List<String>>>) request.getAttribute("hmGoalKRADetails");
  	if(hmGoalKRADetails == null) hmGoalKRADetails = new LinkedHashMap<String, List<List<String>>>();
  	
  	Map<String, List<List<String>>> hmKRATasks = (Map<String, List<List<String>>>) request.getAttribute("hmKRATasks");
  	if(hmKRATasks == null) hmKRATasks = new LinkedHashMap<String, List<List<String>>>();
  	
    Map<String, List<List<String>>> hmEmpKra = (Map<String, List<List<String>>>) request.getAttribute("hmEmpKra");
    List<String> alCheckList = (List<String>) request.getAttribute("alCheckList");
    Map<String, String> hmOrientationViewAccess = (Map<String, String>) request.getAttribute("hmOrientationViewAccess");
	if(hmOrientationViewAccess == null) hmOrientationViewAccess = new HashMap<String,String>();
	Map<String, String> hmOrientationEditAccess = (Map<String, String>) request.getAttribute("hmOrientationEditAccess");
	if(hmOrientationEditAccess == null) hmOrientationEditAccess = new HashMap<String,String>();
	Map<String, String> hmSectionGivenQueCnt = (Map<String, String>) request.getAttribute("hmSectionGivenQueCnt");
	if(hmSectionGivenQueCnt == null) hmSectionGivenQueCnt = new HashMap<String, String>();
	Map<String, String> hmSectionQueCnt = (Map<String, String>) request.getAttribute("hmSectionQueCnt");
	if(hmSectionQueCnt == null) hmSectionQueCnt = new HashMap<String, String>();
	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
	if (hmEmpProfile == null) {
		hmEmpProfile = new HashMap<String, String>();
	}
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	/* Map<String,List<String>> hmGoal1 = (Map<String,List<String>>)request.getAttribute("goalList");
	if(hmGoal1 == null) hmGoal1 = new HashMap<String,List<String>>();
	 */
	%>

<section class="col-lg-4 connectedSortable" style="width: 100%;">
		<div class="box box-primary" style="border-top-color: #cda55f /*#E0E0E0;*/">
			<% if (hmProspectiveData != null && hmProspectiveData.size()>0) { %>
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size: 14px !important;"><b>Perspectives</b></h3>
				</div>
					<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 100%; background-color:#E6E6E6;">
			<%	
					Iterator<String> itPerspective = hmProspectiveData.keySet().iterator();
					while (itPerspective.hasNext()) {
						int count1=0;
						String perspective = itPerspective.next();
						List<String> perspectiveList = hmProspectiveData.get(perspective);
						String perspectiveName = perspectiveList.get(0);
						String perspectiveColor = perspectiveList.get(1);
						
						Map<String, Map<String, List<String>>> hmSuperIdwiseGoalDetails = hmPerspectivewiseGoalDetails.get(perspective);
						if(hmSuperIdwiseGoalDetails==null) hmSuperIdwiseGoalDetails = new HashMap<String, Map<String, List<String>>>();
						int height = 0;
					%>
							<div id="maindiv" style="float: left;">
							<div align="center" id="perspectId_<%=perspective %>" style="float: left; width:3%; padding-left: 3px; margin: 10px 10px 10px 0px; border-radius:5px; writing-mode:vertical-lr; transform: rotate(180deg); font-size:16px; font-weight:bold; background-color:<%=perspectiveColor %>">
               				 <%=perspectiveName %>
							</div>
							
							<% 
							//Map<String, List<String>> hmGoalDetails = (Map<String, List<String>>) request.getAttribute("hmGoalDetails");
							String currUserType = (String) request.getAttribute("currUserType");
	                     	String strBaseUserType=(String) session.getAttribute(IConstants.BASEUSERTYPE);
	                     	String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
	                        String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
							Map<String, Map<String, Map<String, List<List<String>>>>> hmGoalKraEmpwise = (Map<String, Map<String, Map<String, List<List<String>>>>>) request.getAttribute("hmGoalKraEmpwise");
                            if(hmGoalKraEmpwise == null) hmGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
                            //if(hmGoalDetails == null) hmGoalDetails = new HashMap<String, List<String>>();
                       		Map<String, List<String>> hmGoalKraPerspective = (Map<String, List<String>>) request.getAttribute("hmGoalKraPerspective");
                            if(hmGoalKraPerspective == null) hmGoalKraPerspective = new HashMap<String, List<String>>();
							
                            Map<String, String> hmKRATaskStatusAndRating = (Map<String, String>) request.getAttribute("hmKRATaskStatusAndRating");
                            if(hmKRATaskStatusAndRating == null) hmKRATaskStatusAndRating = new HashMap<String, String>();
                              
                            Map<String, String> hmTargetRatingAndComment = (Map<String, String>) request.getAttribute("hmTargetRatingAndComment");
                            if(hmTargetRatingAndComment == null) hmTargetRatingAndComment = new HashMap<String, String>();
                              
                            Map<String, String> hmEmpwiseKRARating = (Map<String, String>) request.getAttribute("hmEmpwiseKRARating");
                            if(hmEmpwiseKRARating == null) hmEmpwiseKRARating = new HashMap<String, String>();
                            
                            Map<String, String> hmEmpwiseGoalRating = (Map<String, String>) request.getAttribute("hmEmpwiseGoalRating");
                            if(hmEmpwiseGoalRating == null) hmEmpwiseGoalRating = new HashMap<String, String>();
                            
                            Map<String, String> hmEmpwiseGoalAndTargetRating = (Map<String, String>) request.getAttribute("hmEmpwiseGoalAndTargetRating");
                            if(hmEmpwiseGoalAndTargetRating == null) hmEmpwiseGoalAndTargetRating = new HashMap<String, String>();
                            
                            Map<String, String> hmEmpwiseKRACnt = (Map<String, String>) request.getAttribute("hmEmpwiseKRACnt");
                            if(hmEmpwiseKRACnt == null) hmEmpwiseKRACnt = new HashMap<String, String>();
                            
                            Map<String, String> hmTargetValue = (Map<String, String>)request.getAttribute("hmTargetValue");
                            Map<String, String> hmTargetID = (Map<String, String>)request.getAttribute("hmTargetID");
                            Map<String, String> hmTargetRemark = (Map<String, String>)request.getAttribute("hmTargetRemark");
                            Map<String, String> hmUpdateBy = (Map<String, String>)request.getAttribute("hmUpdateBy");
                            String strCurrency = (String) request.getAttribute("strCurrency");
                            List<String> empList=(List<String>)request.getAttribute("empList");
                            Map<String, String> hmHrIds = (Map<String, String>) request.getAttribute("hmHrIds");
                            if(hmHrIds == null) hmHrIds = new HashMap<String, String>();
                             String dataType = (String) request.getAttribute("dataType");
                            Map<String, String> hmEmpCodeName = (Map<String, String>)request.getAttribute("hmEmpName");
                            
                            Map<String, Map<String, Map<String, List<List<String>>>>> hmIndividualGoalKraEmpwise = (Map<String, Map<String, Map<String, List<List<String>>>>>) request.getAttribute("hmIndividualGoalKraEmpwise");
                            if(hmIndividualGoalKraEmpwise == null) hmIndividualGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
                            
                            Map<String, List<String>> hmIndividualGoalDetails = (Map<String, List<String>>) request.getAttribute("hmIndividualGoalDetails");
                            if(hmIndividualGoalDetails == null) hmIndividualGoalDetails = new HashMap<String, List<String>>();
                            
                            Map<String, String> hmKraAverage = (Map<String, String>)request.getAttribute("hmKraAverage");
                            if(hmKraAverage == null) hmKraAverage = new HashMap<String, String>();
                            
                            Map<String, String> hmCheckGTWithAllowance = (Map<String, String>)request.getAttribute("hmCheckGTWithAllowance");
                            if(hmCheckGTWithAllowance == null) hmCheckGTWithAllowance = new HashMap<String, String>();
                            
                            Map<String, String> hmCheckKWithAllowance = (Map<String, String>)request.getAttribute("hmCheckKWithAllowance");
                            if(hmCheckKWithAllowance == null) hmCheckKWithAllowance = new HashMap<String, String>();
                            Map<String, String> hmEmpSuperIds = (Map<String, String>)request.getAttribute("hmEmpSuperIds");
                            if(hmEmpSuperIds == null) hmEmpSuperIds = new HashMap<String, String>();
                            
                            Map<String, String> hmEmpwiseGoalAndTargetEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseGoalAndTargetEmpRating");
                            if(hmEmpwiseGoalAndTargetEmpRating == null) hmEmpwiseGoalAndTargetEmpRating = new HashMap<String, String>();
                            
                            Map<String, String> hmKRATaskEmpRating = (Map<String, String>)request.getAttribute("hmKRATaskEmpRating");
                            if(hmKRATaskEmpRating == null) hmKRATaskEmpRating = new HashMap<String, String>();
                            
                            Map<String, String> hmEmpwiseKRAEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseKRAEmpRating");
                            if(hmEmpwiseKRAEmpRating == null) hmEmpwiseKRAEmpRating = new HashMap<String, String>();
                            
                            Map<String, String> hmEmpwiseGoalEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseGoalEmpRating");
                            if(hmEmpwiseGoalEmpRating == null) hmEmpwiseGoalEmpRating = new HashMap<String, String>();
                              
                            Map<String, List<String>> hmActualAchievedGoal = (Map<String, List<String>>)request.getAttribute("hmActualAchievedGoal");
                  			if(hmActualAchievedGoal == null) hmActualAchievedGoal = new HashMap<String, List<String>>();
                              
                  		 	 List<String> membersAccessList = (List<String>)request.getAttribute("orientationMembersList");
                  	    	if(membersAccessList == null ) membersAccessList = new ArrayList<String>();
                  			List<String> algoal = new ArrayList<String>();
                  			Map<String,String> hmGoaldetailsData = ( Map<String,String>)request.getAttribute("hmGoaldetailsData");
							//Map<String, Map<String, List<List<String>>>> hmGoalKraSuperIdwise =(Map<String, Map<String, List<List<String>>>>)request.getAttribute("hmGoalKraSuperIdwise"); 
		             	    //String perspectiveId = hmGoaldetailsData.get(perspective);
                  			Map<String,String> hmGoal = ( Map<String,String>)request.getAttribute("hmGoal");
                  			Map<String, String> hmKraCompletedPercentage = (Map<String, String>)request.getAttribute("hmKraCompletedPercentage");
                            if(hmKraCompletedPercentage == null) hmKraCompletedPercentage = new HashMap<String, String>();
	                    	int goalCount = 0;
	                    	
	                    	Iterator<String> itSuperId = hmSuperIdwiseGoalDetails.keySet().iterator();
							while(itSuperId.hasNext()) {
								String superId = itSuperId.next();
								Map<String, List<String>> hmGoalDetails = hmSuperIdwiseGoalDetails.get(superId);
								
								Iterator<String> itGoal = hmGoalDetails.keySet().iterator();
								while(itGoal.hasNext()) {
									count1++;
									String goalAndFreqId = itGoal.next();
									List<String> gInnerList = hmGoalDetails.get(goalAndFreqId);
								
                  				String goalid = gInnerList.get(1);
                				String goalFreqId = gInnerList.get(32);
                				double avgGoalRating = 0.0d;
                				double dblWeightScore = 0.0d;
                				
                				if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                           			String goalRating = hmEmpwiseGoalRating.get(goalid+"_"+goalFreqId+"_RATING");
                           			String goalTaskCount = hmEmpwiseGoalRating.get(goalid+"_"+goalFreqId+"_COUNT");
                           			
                           			String goalEmpRating = hmEmpwiseGoalEmpRating.get(goalid+"_"+goalFreqId+"_RATING");
                           			String goalEmpTaskCount = hmEmpwiseGoalEmpRating.get(goalid+"_"+goalFreqId+"_COUNT");
                           			if(uF.parseToInt(goalTaskCount) > 0 || uF.parseToInt(goalEmpTaskCount) > 0) {
                           				avgGoalRating = (uF.parseToDouble(goalRating) + uF.parseToDouble(goalEmpRating)) / (uF.parseToInt(goalTaskCount) + uF.parseToInt(goalEmpTaskCount));
                           			}
                           			dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                           		} else {
                           			double avgEmpGoalRating = 0;
                           			avgGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetRating.get(goalid+"_"+goalFreqId+"_RATING"));
                           			if(uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(goalid+"_"+goalFreqId+"_COUNT")) > 0) {
                           				avgEmpGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetEmpRating.get(goalid+"_"+goalFreqId+"_RATING"))/ uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(goalid+"_"+goalFreqId+"_COUNT"));
                           				if(avgGoalRating > 0) {
                           					avgGoalRating = (avgGoalRating + avgEmpGoalRating) / 2; 
                           				} else {
                           					avgGoalRating = avgEmpGoalRating;
                           				}
                           			}
                           			dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                           		}
                				
							 boolean flag = true;
                              if(gInnerList.get(34)!= null && uF.parseToInt(gInnerList.get(34)) > 5) {
                              flag = false;
                              }	
                              String orientaionKey = gInnerList.get(34)+"_"+strUserTypeId;
                              if(currUserType != null && currUserType.equals(strBaseUserType)) {
                              orientaionKey = gInnerList.get(34)+"_"+strBaseUserTypeId;
                              }
                              String userId = gInnerList.get(36);
                          %>
							 <div style="float: left; width:50%; background-color:white; margin-top: 20px; padding: 0px 0px 10px 10px; border-radius: 5px;">
                                 <div style="float: left; width: 100%; margin-top:10px;">
                                   <div class="<%=gInnerList.get(10) %>" style="float: left; line-height: 18px; font-size: 14px;">
                                      <b>Objective </b>
                                         <br><%=gInnerList.get(3) %>
                                            <%=(gInnerList.get(33) != null && !gInnerList.get(33).equals("")) ? "["+gInnerList.get(33)+"]" : "" %></br>
                                    </div>
                                    <div style="float: left; width: 100%; border-right: 1 px solid #CCCCCC; margin-top:5px;">
                                       <span class="<%=gInnerList.get(10) %>" style="float: left; font-size: 13px; line-height: 18px;"><b>Goal
	                                     <br></b><%=uF.showData(gInnerList.get(13), "-") %></span>
                                     </div>
                                      <div style="float: left; width: 100%;">
                                         <span class="<%=gInnerList.get(10) %>" style="float: left;font-size: 13px; line-height: 18px;">-
                                          assigned by <%=gInnerList.get(4)%>, attribute <%=gInnerList.get(14) %>, effective date <%=gInnerList.get(15) %>, due date <%=gInnerList.get(5)%>
                                           </span>
                                      </div>
                                        <%
                                         if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                          	int kraTotalTaskCount = 0;
                                             double totalKraStatus =0;
                                            	double kraStatus = 0;
                                             totalKraStatus = uF.parseToDouble(hmKraCompletedPercentage.get(goalid+"_"+goalFreqId+"_PERCENTAGE"));
                                             kraTotalTaskCount= uF.parseToInt(hmKraCompletedPercentage.get(goalid+"_"+goalFreqId+"_TASKCOUNT")); 
                                              kraStatus = totalKraStatus / kraTotalTaskCount;
                                           %>
                                     <div id="KraPercentage_<%=perspective %>_<%=goalid %>" style="float: left; margin: 0px 25px 3px 25px; width:70%;">
                                         <div class="anaAttrib1">
                                              <span style="margin-left:<%=kraStatus > 95 ? kraStatus - 10 : kraStatus - 4%>%;"><%=uF.showData(""+kraStatus, "0")%>%</span>
                                           </div>
                                            <div id="outbox" style="width: 100% !important;">
                                                <% if (kraStatus < 33.33) { %>
                                                  <div id="redbox" style="width: <%=uF.showData(""+kraStatus, "0") %>%;"></div>
                                                 <% } else if (kraStatus >= 33.33 && kraStatus < 66.67) { %>
                                                   <div id="yellowbox" style="width: <%=uF.showData(""+kraStatus, "0")%>%;"></div>
                                                <% } else if (kraStatus >= 66.67) { %>
                                                     <div id="greenbox" style="width: <%=uF.showData(""+kraStatus, "0")%>%;"></div>
                                                 <% } %>
                                           </div>
                                             <div class="anaAttrib1" style="float: left; width: 100%;">
                                                 <span style="float: left; margin-left: -4%;">0%</span>
                                                 <span style="float: right; margin-right: -10%;">100%</span>
                                              </div>
                                     </div>
                                <% } %>
                             </div>
                              <div style="float: left; width: 100%;">
                               <script type="text/javascript">
					              $(function() {
					                $('#starPrimaryG<%=perspective %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>').raty({
					                readOnly: true,
					                start: <%=avgGoalRating %>,
					               half: true,
					               targetType: 'number'
					       	     });
					        	});
				          </script>
                            <div id="starPrimaryG<%=perspective %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>" style="float: left;margin-top:10px; width:100%;" ></div>
                               <div style="float: right; margin-top:10px;">
                                     <b>Rated Score:</b>
                                         <%=uF.formatIntoOneDecimalWithOutComma(dblWeightScore) %>/<%=uF.parseToDouble(gInnerList.get(16)) %>
                                         &nbsp;&nbsp;</div>
                               </div>
                                <%--updated by kalpana on 21/10/2016 - start --%>
                             <div style="float: left; width: 99%; margin-top: 5px;">
                                 <div style="float:left; width:100%;"><strong>Assigned To:</strong></div>
					              <%if(gInnerList.get(35)!=null){ 
										List<String> emplistID=Arrays.asList(gInnerList.get(35).split(","));
									%>
									<div style="float:left;width:100%;">
									 <% for(int j=0; emplistID!=null && j<emplistID.size();j++) {
										 if(emplistID.get(j)!=null && emplistID.get(j).length() > 0 ) {
											String empName = hmEmpName.get(emplistID.get(j).trim());
											String empimg = uF.showData(empImageMap.get(emplistID.get(j).trim()), "avatar_photo.png");
									%>
									<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(j).trim()%>', '<%=empName %>');"><span style="float:left; width:20px; height:20px; margin:2px;">
										<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(j).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
										</span>
									</a>
									<% } 
									 } %>
							</div>
							 <div style="float:left; width:100%;">
							 </div>
						<% }%>
                         </div>
                       </div>
                       						<%	boolean accessFlag = false;
                                               boolean managerAccessFlag = false;
                                               if(membersAccessList.contains(gInnerList.get(34)+"_5") || membersAccessList.contains(gInnerList.get(34)+"_13")) {
                                               accessFlag = true;
                                               } if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                              List<String> goalOuterList = hmGoalDetails.get(goalAndFreqId);
								 			%>
								 <div style="float: left; width: 40%; margin-top: 5px;">                                                  
                                              <% List<String> innerList = goalOuterList;
                                              List<List<String>> taskOuterList = hmKRATasks.get(innerList.get(11));
                                             	double avgKRARating = 0.0d;
                                             	double dblKRAWeightScore = 0.0d;
                                             	String kraRating = hmEmpwiseKRARating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                             	String kraTaskCount = hmEmpwiseKRARating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                             	if(uF.parseToInt(kraTaskCount) > 0) {
                                             		avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
                                             	}
                                             	String strUserCnt = "";
                                             	if(hmKRATaskEmpRating != null && !hmKRATaskEmpRating.isEmpty()) {
                                             		String strUserRating = hmEmpwiseKRAEmpRating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                             		strUserCnt = hmEmpwiseKRAEmpRating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                             		double avgUserRating = 0;
                                             		if(uF.parseToInt(strUserCnt)>0) {
                                             			avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
                                             		}
                                             		if(avgKRARating > 0 && avgUserRating > 0) {
                                             			avgKRARating = (avgKRARating + avgUserRating) / 2;
                                             		} else if(avgUserRating > 0) {
                                             			avgKRARating = avgUserRating;
                                             		}
                                             	}
                                             	dblKRAWeightScore = (avgKRARating * uF.parseToDouble(innerList.get(27))) / 5;
                                             %> <script type="text/javascript">
                                             $(function() {
                                             	$('#starPrimaryGK<%=perspective %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>').raty({
                                             		readOnly: true,
                                             		start: <%=avgKRARating %>,
                                             		half: true,
                                             		targetType: 'number'
                                             });
                                             });
                                         </script>
                                        
                                          <ul style="float: left; width:100%; margin-top:15px;">
                                         
                                         	 <li style="float: left; width: 100%; margin-left:-10px; padding-bottom:10px; background-color:white;border-radius:5px;">
                                            <div id = "the_div1">
                                             <div  style="float: left; width: 100%;">
                                                       <div style="float: left; margin: 10px 0px 0px 10px">
                                                         <span class="<%=innerList.get(10) %>" style="margin: 0px 0px 0px 0px; float: left;"><strong>Initiative:</strong>&nbsp;<%=uF.showData(innerList.get(2), "-") %>
                                                         <span style="float: left; width: 100%;"> <% if(uF.parseToBoolean(hmCheckKWithAllowance.get(innerList.get(11)))) { %>
                                                         <span style="font-style: italic;"><a href="javascript:void(0);" style="font-size: 10px; font-weight: normal;"
                                                             onclick="viewPerformanceAllowanceData('<%=innerList.get(11) %>', 'K');">This Initiative has Performance Incentives attached</a>
                                                         </span> <% } else { %> <span style="font-size: 10px; font-style: italic; color: gray;">This Initiative has no Performance Incentives attached</span> <% } %> </span> </span>
                                                     </div>
                                                     <div style="float: right;padding-left:10px; width: 100%; margin-top:5px; border-bottom: 1px solid #CCCCCC;">
                                                         <div   id="starPrimaryGK<%=perspective %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>" style="float: left;  width: 100%; margin-top:-5px;"></div>
                                                         <div style="float: right;padding-right:10px">
                                                             <b>Rated Score:</b>
                                                             <%=uF.formatIntoOneDecimalWithOutComma(dblKRAWeightScore) %>/<%=uF.parseToDouble(innerList.get(27)) %>
                                                         </div>
                                                     </div>
                                               </div>
                                              <% if(taskOuterList!=null && !taskOuterList.isEmpty()) { %>
                                                 <div style="float: left; width: 100%; ">
                                                     <div style="float: left; width: 100%; line-height: 12px; margin-top:10px;margin-left:-5px;" >
                                                         <span style="font-weight: bold; margin-left: 30px; color: gray;">Tasks:</span>
                                                     </div>
                                                     <% 
                                                         for(int a=0; a<taskOuterList.size(); a++) {
                                                          List<String> taskInnerList = taskOuterList.get(a);
                                                         String taskStatusPercent = hmKRATaskStatusAndRating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_STATUS");
                                                         String managerRating = hmKRATaskStatusAndRating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_RATING");
                                                         String hrRating = hmKRATaskStatusAndRating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_RATING");
                                                       	 String managerComment = hmKRATaskStatusAndRating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_COMMENT");
                                                         String hrComment = hmKRATaskStatusAndRating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_COMMENT");
                                                         String empComment = hmEmpwiseGoalAndTargetEmpRating.get(goalid+"_"+goalFreqId+"_COUNT");
											  String hrMngrReview = "block";
                                                         if(uF.parseToInt(taskStatusPercent) >= 100 || uF.parseToInt(taskInnerList.get(2)) == 1) {
                                                         	hrMngrReview = "block";
                                                         }
                                                         double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
                                                         if(managerRating == null) {
                                                         	avgRating = uF.parseToDouble(hrRating);
                                                         } else if(hrRating == null) {
                                                         	avgRating = uF.parseToDouble(managerRating);
                                                         }
                                                         
                                                         String strUserTaskCnt = "";
                                                         if(hmKRATaskEmpRating != null && !hmKRATaskEmpRating.isEmpty()) {
                                                         	String strUserRating = hmKRATaskEmpRating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_RATING");
                                                         	strUserTaskCnt = hmKRATaskEmpRating.get(goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_COUNT");
                                                         	System.out.println("strUserTaskCnt ===>> " + strUserTaskCnt);
                                                         	double avgUserRating = 0;
                                                         	if(uF.parseToInt(strUserCnt)>0) {
                                                         		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserTaskCnt);
                                                         	}
                                                         	if(avgRating > 0 && avgUserRating > 0) {
                                                         		avgRating = (avgRating + avgUserRating) / 2;
                                                         	} else if(avgUserRating > 0) {
                                                         		avgRating = avgUserRating;
                                                         	}
                                                         }
                                                         int commentCnt = 0;
                                                         if(managerComment != null && !managerComment.equals("null")) {
                                                         	commentCnt++;
                                                         }
                                                         if(hrComment != null && !hrComment.equals("null")) {
                                                         	commentCnt++;
                                                         }
                                                         System.out.println("commentCnt ===>> " + commentCnt);
                                                         %>
                                                         <div style="float: left; width: 100%;">
                                                         <script type="text/javascript">
                                                             $(function() {
                                                             	$('#starPrimaryGKT<%=perspective %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
                                                             		readOnly: true,
                                                             		start: <%=avgRating %>,
                                                             		half: true,
                                                             		targetType: 'number'
                                                             });
                                                             });
                                                          
                                                         </script>
                                                      <div style="float: left; margin-left:10px; line-height: 15px; width: 40%; margin-top:10px;"><%=uF.showData(taskInnerList.get(1), "-") %></div>
												 <div id="KTProBarDiv_<%=perspective %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="float: left; width: 30%;">                                                                   
													<div class="anaAttrib1">
                                                                 <span style="margin-left:<%=uF.parseToDouble(taskStatusPercent) > 95 ? uF.parseToDouble(taskStatusPercent) - 10 : uF.parseToDouble(taskStatusPercent) - 4%>%;"><%=uF.showData(taskStatusPercent, "0")%>%</span>
                                                             </div>
               												<div id="outbox" style="margin-left:10px">
                                                                 <% if (uF.parseToDouble(taskStatusPercent) < 33.33) { %>
                                                                 <div id="redbox" style="width: <%=uF.showData(taskStatusPercent, "0") %>%;"></div>
                                                                 <% } else if (uF.parseToDouble(taskStatusPercent) >= 33.33 && uF.parseToDouble(taskStatusPercent) < 66.67) { %>
                                                                 <div id="yellowbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
                                                                 <% } else if (uF.parseToDouble(taskStatusPercent) >= 66.67) { %>
                                                                 <div id="greenbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
                                                                 <% } %>
                                                             </div>
                                                            <div class="anaAttrib1" style="float: left; width: 100%; margin-left:0px;">
                                                                 <span style="float: left; margin-left: -4%;">0%</span>
                                                                 <span style="float: right; margin-left: -10%;">100%</span>
                                                             </div>
                                                         </div>
                                                         <%
                                                        
                                                             String addedBy = taskInnerList.get(4);
                                                             if(flag ||  (strUserType != null && (strUserType.equals(IConstants.ADMIN) || uF.parseToInt(addedBy)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { 
                                                          	   if(dataType == null || dataType.equals("L")) {
                                                             %>
                                                         <% } %>
                                                         <% } else { %>
                                                         <div style="float: left; margin: 20px 5px 3px 15px; width: 7%;">
                                                             <label style="width: 40px !important;"><%=uF.showData(taskStatusPercent, "") %></label>
                                                             <br />
													<span id="completedPercentStatusSpan_<%=perspective %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></span>                                                               
												</div>
                                                         <% } %>
												 		<div id="GivenTaskRatingDiv_<%=perspective %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="float: left; width:100%">
                                                               <div style="float: left; padding-left: 0px; margin-top:-5px; ">		
                                                               <% 
                                                               	String feedbackUserType = null;
                                                               	if(currUserType != null && currUserType.equals(strBaseUserType)) {
                                                                     	feedbackUserType = strBaseUserType;
                                                                     }
                                                              	    String taskInnerListdata =  taskInnerList.get(0);
                                                              	    String innerListdata = innerList.get(11);
                                                              	    
                                                             		%>
													 		</div>
                                                             <div id="starPrimaryGKT<%=perspective %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="float: left; margin: -5px 0px 0px 150px;"></div>
                                                             <% if(commentCnt > 0 || uF.parseToInt(strUserTaskCnt) > 0) { %>
                                                            
                                                             <div>
                                                                 <a href="javascript:void(0);" onclick="viewManagerAndHRComments(' ', '<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA')">Comments
                                                                 <%=(commentCnt + uF.parseToInt(strUserTaskCnt)) %></a>
                                                             </div>
                                                             <% } %>
                                                         </div>
                                                         <% 
                                                             String feedbackUserType1 = "";
                                                             if(currUserType != null && currUserType.equals(strBaseUserType)) {
                                                             	feedbackUserType1 = strBaseUserType;
                                                             }
                                                             	if((uF.parseToInt(hmHrIds.get(perspective)) == uF.parseToInt(strSessionEmpId) && strUserType != null && strUserType.equals(IConstants.HRMANAGER)) || (strUserType != null && !strUserType.equals(IConstants.HRMANAGER)) ) { %>
                                                             <% if(dataType == null || dataType.equals("L")) {%>	
                                                         <% } %>    
                                                         <% } %>
                                                     </div>
                                                     <% } %>
                                                 </div>
                                                 <% } %>
                                                 </div>
                                             </li>
                                         </ul>
                                       <% } %>
                                       </div>
							<% 	} } %>
								<script type="text/javascript">
									$(function() {
										var count = "<%=count1 %>";
										document.getElementById("perspectId_<%=perspective%>").style.height =((280 * count) +"px");
									});
								</script>
							<% } %>
                   		</div>
					<% } else { %>
						<div class="nodata msg">No data available.</div>
					<% } %>
		  </div>
		</div>
		
	</section>
	
<script type="text/javascript">

	function viewManagerAndHRComments(empId, goalId, goalFreqId, kraId, kraTaskId, goalType) { 
		
		var strID = '';
		//alert("strID == "+strID);
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Comments');
		$("#modalInfo").show();
		$.ajax({
			url : "ViewManagerAndHRComentsOfGoalKRATarget.action?empId=0&goalId="+goalId+"&goalFreqId="+goalFreqId
				+"&kraId="+kraId+"&kraTaskId="+kraTaskId+"&goalType="+goalType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	function getEmpProfile(val, empName){
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html(''+empName+'');
		$("#modalInfo").show();
	
		$.ajax({
			url : "AppraisalEmpProfile.action?empId=" + val ,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	/* $("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	}); */ 

    </script>

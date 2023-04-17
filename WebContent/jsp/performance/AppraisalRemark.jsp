<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<% String recommendationOrFinalization = (String)request.getParameter("recommendationOrFinalization");
	if(recommendationOrFinalization!=null && recommendationOrFinalization.equals("RECOMMENDATION")) {
		UtilityFunctions uF = new UtilityFunctions();
		String id = (String)request.getParameter("id");
		String appFreqId = (String)request.getParameter("appFreqId"); 
		String fromPage = (String)request.getParameter("fromPage");
	//===start parvez date: 22-03-2023===	
		Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
		if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	//===end parvez date: 22-03-223===		
%>
<script type="text/javascript">
     /*  jQuery(document).ready(function(){
          jQuery("#frmEmpActivity").validationEngine();
         
      }); */
      
      $(function(){
    		$("input[type='submit']").click(function(){
    			$('.validateRequired').filter(':hidden').prop('required',false);
    			$('.validateRequired').filter(':visible').prop('required',true);
    		});
    	});
      
	</script>

<%
	String managerRemark = (String) request.getAttribute("managerRemark");
	String areasOfStrength = (String) request.getAttribute("areasOfStrength");
	String areasOfDevelopment = (String) request.getAttribute("areasOfDevelopment");
	
	boolean flag = (Boolean) request.getAttribute("flag");
	String strRecommendBy = (String)request.getAttribute("strRecommendBy");
	String appraisal_freq = (String)request.getAttribute("appraisal_freq");
	
	Map<String, String> outerMp = (Map<String, String>) request.getAttribute("outerMp");
	List<String> memberList = (List<String>) request.getAttribute("memberList");
	Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");
	
	String appraiseeName = (String)request.getAttribute("appraiseeName");
	
%>

<div class="leftbox reportWidth" id="formBody">
	<s:form method="POST" action="AppraisalRemark" theme="simple" id="frmEmpActivity">
		<s:hidden name="id" />
		<s:hidden name="empid" />
		<s:hidden name="appFreqId" id="appFreqId" />
		<s:hidden name="thumbsFlag" />
		<s:hidden name="remarktype" />
		<s:hidden name="recommendationOrFinalization" />
		<s:hidden name="fromPage" />
		<input type="hidden" name="appraisal_freq" id= "appraisal_freq" value="<%=appraisal_freq %>" />

		<div style="float: left; width: 100%; margin-top: 10px;"><b><%=appraiseeName %></b>'s Performance Summary</div>
		<div style="float: left; width: 100%; margin-top: 20px;">  <!-- border: 1px solid #dbdbdb; -->
			<table class="table table-bordered" <% if(flag) { %> style="margin: 0px; border: 0px none !important;" <% } else { %> style="border: 0px none !important;"<% } %>>
			<% if (managerRemark == null || managerRemark.equals("")) { %>
				<tr>
					<td colspan="2">
						<%
						String thumbsFlag = (String) request.getAttribute("thumbsFlag");
						if(!uF.parseToBoolean(thumbsFlag)) { %> 
							<span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-up" style="color:#68ac3b;height: 16px; width: 16px;" aria-hidden="true"></i> </span>
						<% } else { %>
							<span style="float: left; margin-left: 10px; margin-top: 5px;"> <i class="fa fa-thumbs-down" style="color:#e22d2c;height: 16px; width: 16px;" aria-hidden="true"></i></span> 
						<% } %>
					</td>
				</tr>

				<tr>
					<td style="text-align: right;" class="txtlabel" valign="top"><b>Recommendation:<sup>*</sup></b>&nbsp;</td>
					<td><s:textarea name="remark" cssClass="validateRequired" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
				</tr>
				<%-- <tr>
					<td style="text-align: right;" class="txtlabel" valign="top"><b>Areas of Strength:</b>&nbsp;</td>
					<td><s:textarea name="areasOfStrength" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
				</tr>
				<tr>
					<td style="text-align: right;" class="txtlabel" valign="top"><b>Areas of Development:</b>&nbsp;</td>
					<td><s:textarea name="areasOfDevelopment" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
				</tr> --%>
				<tr>
					<td colspan="2">
						<%if(fromPage != null && fromPage.equals("AD")) { %> 
							<input type="submit" name="submit" value="Save" class="btn btn-primary" />
						<% } else { %>
							<s:submit name="submit" value="Save" cssClass="btn btn-primary" /> 
						<% } %>
					</td>
				</tr>
				<% } else { %>
				<tr>
					<td colspan="2" class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Recommendation:</b>
						<div style="margin-left: 30px;"><%=managerRemark != null ? managerRemark : "" %></div>
					</td>
				</tr>
				<%-- <tr>
					<td colspan="2" class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Areas of Strength:</b>
						<div style="margin-left: 30px;"><%=areasOfStrength != null ? areasOfStrength : ""%></div>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="txtlabel" valign="top" style="width:50%; padding-left: 10px !important;"><b>Areas of Development:</b>&nbsp;
						<div style="margin-left: 30px;"><%=areasOfDevelopment != null ? areasOfDevelopment : ""%></div>
					</td>
				</tr> --%>
				<% } %>
				
			</table>
		</div>
		
		
		<div style="float: left; width: 100%;">
			<table class="table table-bordered" style="width: 100%; margin: 0px; border: 0px none !important;">
				<% if(memberList != null && memberList.size()>0) { %>
				<tr>
					<th colspan="2" style="text-align: center; background-color: #E3E3E3;">
						Final Rating and Comments by Review Panel
					</th>
				</tr>
				<% 
				//System.out.println("memberList ===>> " + memberList);
					for (int i = 0; i < memberList.size(); i++) {
				%>
					<tr>
						<td valign="top" width="70%"><b><%=orientationMemberMp.get(memberList.get(i))%>:</b>&nbsp;</td>
					<!-- ===start parvez date: 01-03-2023=== -->	
						<td><div style="float: left;" id="starPrimaryFinal<%=memberList.get(i).trim()%>"></div> 
							<b>
								<%-- <%=uF.showData(outerMp.get(memberList.get(i).trim()), "N/A") %>% --%>
								<%if(uF.parseToBoolean(outerMp.get("ACTUAL_CAL_BASIS"))){ %>
									<%=outerMp.get(memberList.get(i).trim())!=null?uF.parseToDouble(outerMp.get(memberList.get(i).trim()))/20 : "NA" %>
								<%} else{ %>
									<%-- <%=uF.showData(outerMp.get(memberList.get(i).trim()), "N/A") %>% --%>
									<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
										<%=outerMp.get(memberList.get(i).trim())!=null?uF.parseToDouble(outerMp.get(memberList.get(i).trim()))/10 : "NA" %>
									<%} else{ %>
										<%=uF.showData(outerMp.get(memberList.get(i).trim()), "N/A") %>%
									<%} %>
								<%} %>
							</b>
						<%-- <script type="text/javascript">
	                         $('#starPrimaryFinal<%=memberList.get(i).trim()%>').raty({
	                         	readOnly: true,
	                         	start: <%=outerMp.get(memberList.get(i).trim()) != null ? uF.parseToDouble(outerMp.get(memberList.get(i).trim())) / 20 + "" : "0"%>,
	                         	half: true,
	                         	targetType: 'number'
	                         });
	                    </script> --%>
	                    <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
	                    <script type="text/javascript">
	                         $('#starPrimaryFinal<%=memberList.get(i).trim()%>').raty({
	                         	readOnly: true,
	                         	start: <%=outerMp.get(memberList.get(i).trim()) != null ? uF.parseToDouble(outerMp.get(memberList.get(i).trim())) / 10 + "" : "0"%>,
	                         	number: 10,
	                         	half: false,
	                         	targetType: 'number'
	                         });
	                    </script>
	                    <%} else{ %>
	                    	<script type="text/javascript">
		                         $('#starPrimaryFinal<%=memberList.get(i).trim()%>').raty({
		                         	readOnly: true,
		                         	start: <%=outerMp.get(memberList.get(i).trim()) != null ? uF.parseToDouble(outerMp.get(memberList.get(i).trim())) / 20 + "" : "0"%>,
		                         	half: true,
		                         	targetType: 'number'
		                         });
		                    </script>
	                    <%} %>
						</td>
				<!-- ===end parvez date: 01-03-2023=== -->		
					</tr>
					
					<% } %>
					<% //String aggregate = outerMp.get("AGGREGATE") != null ? uF.parseToDouble(outerMp.get("AGGREGATE")) / 20 + "" : "0";
					String aggregate = null;
					if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){
						aggregate = outerMp.get("AGGREGATE") != null ? uF.parseToDouble(outerMp.get("AGGREGATE")) / 10 + "" : "0";
					} else{
						aggregate = outerMp.get("AGGREGATE") != null ? uF.parseToDouble(outerMp.get("AGGREGATE")) / 20 + "" : "0";
					}
					%>
					<tr>
						<td valign="top" width="70%"><b>Final Score:</b>&nbsp;</td>
						<td><div style="float: left;" id="starPrimaryFinalScore"></div> 
					<!-- ===start parvez date: 01-03-2023=== -->		
							<%-- <b><%=uF.showData(outerMp.get("AGGREGATE"), "0.0") %>%</b> --%>
							
						<%if(uF.parseToBoolean(outerMp.get("ACTUAL_CAL_BASIS"))){ %>
							<b><%=aggregate!=null ? uF.getRoundOffValue(1,uF.parseToDouble(aggregate)) : "0.0" %></b>
						<%} else{ %>
							<%-- <b><%=uF.showData(outerMp.get("AGGREGATE"), "0.0") %>%</b> --%>
							<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
								<b><%=aggregate!=null ? uF.getRoundOffValue(1,uF.parseToDouble(aggregate)) : "0.0" %></b>
							<%} else{ %>
								<b><%=uF.showData(outerMp.get("AGGREGATE"), "0.0") %>%</b>
							<%} %>
						<%} %>
						
					<!-- ===end parvez date: 01-03-2023=== -->	
						<%-- <script type="text/javascript">
	                         $('#starPrimaryFinalScore').raty({
	                         	readOnly: true,
	                         	start: <%=aggregate %>,
	                         	half: true,
	                         	targetType: 'number'
	                         });
	                     </script> --%>
	                     <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
	                     <script type="text/javascript">
	                         $('#starPrimaryFinalScore').raty({
	                         	readOnly: true,
	                         	start: <%=aggregate %>,
	                         	number: 10,
	                         	half: false,
	                         	targetType: 'number'
	                         });
	                     </script>
	                     <%} else{ %>
		                     <script type="text/javascript">
		                         $('#starPrimaryFinalScore').raty({
		                         	readOnly: true,
		                         	start: <%=aggregate %>,
		                         	half: true,
		                         	targetType: 'number'
		                         });
		                     </script>
	                     <%} %>
						</td>
					</tr>
				<% } %>
			</table>
		</div>
		
		<div style="float: left; width: 100%;">
			<table class="table table_no_border">
			<% if (flag) { %>
				<tr><td colspan="2" style="padding-right: 20px;" align="right"><strong><i>Recommended by - <%=strRecommendBy%></i></strong></td></tr>
			<% } %>
			</table>
		</div>
		
	</s:form>
</div>


<script>


$("#frmEmpActivity").submit(function(e){
	e.preventDefault();
	var appId = '<%=id%>';
	var appFreqId = '<%=appFreqId %>';
	var fromPage = '<%=fromPage %>';
	//	alert("updateMyReview jsp ==appId==>"+appId+"==>appFreqId==>"+appFreqId);
	var form_data = $("#frmEmpActivity").serialize();
	$.ajax({ 
		type : 'POST',
	//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
		url: "AppraisalRemark.action",
		data: form_data+"&submit=Submit",
		cache: true,
		success: function(result){
			getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
   		},
		error: function(result){
			getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
		}
	});
});

</script>


<%

	} else {

	UtilityFunctions uF = new UtilityFunctions();
	String id = (String)request.getParameter("id");
	String appFreqId = (String)request.getParameter("appFreqId"); 
	String fromPage = (String)request.getParameter("fromPage");
	java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); 
	if(couterlist == null) couterlist = new java.util.ArrayList();
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
		
	List<List<String>> leaveTypeListWithBalance = (List<List<String>>) request.getAttribute("leaveTypeListWithBalance");
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);	//added by parvez date: 21-01-2023
%>

<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<script type="text/javascript">
     /*  jQuery(document).ready(function(){
          jQuery("#frmEmpActivity").validationEngine();
         
      }); */
      
      $(function(){
    		$("input[type='submit']").click(function(){
    			$("#frmEmpActivity").find('.validateRequired').filter(':hidden').prop('required',false);
    			$("#frmEmpActivity").find('.validateRequired').filter(':visible').prop('required',true);
    		});
    		$( "#idEffectiveDate").datepicker({format: 'dd/mm/yyyy'});
    		
    		$("#learningIds").multiselect().multiselectfilter();
    	});
      
      if(document.getElementById("strActivity")) {
     	 var activityId = document.getElementById("strActivity").value;
		 selectElements(activityId); 
      }
		
      function selectElements(activityId) {
    	  //alert("activityId ===>>>> " + activityId);
    	  disableAll();
    	  //alert("activityId 1 ===>>>> " + activityId);
    	  document.getElementById("strIncrementPercentage").value = "";
    	  
    	  if(activityId == <%=IConstants.ACTIVITY_OFFER_ID %>) { //  Offer 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    	  } else if(activityId == <%=IConstants.ACTIVITY_APPOINTMENT_ID %>) { // Appointment 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    	  } else if(activityId == <%=IConstants.ACTIVITY_PROBATION_ID %>) { // Probation 
    		  document.getElementById("tdEffectiveLbl").style.display = "none";
       		  document.getElementById("idEffectiveDate").style.display = "none";
    		  document.getElementById("probationTR").style.display = "table-row";
    	  } else if(activityId == <%=IConstants.ACTIVITY_EXTEND_PROBATION_ID %>) { // Extend Probation 
    		  document.getElementById("extendProbationTR").style.display = "table-row";
    		  document.getElementById("tdEffectiveLbl").style.display = "none";
    		  document.getElementById("idEffectiveDate").style.display = "none";
    	  } else if(activityId == <%=IConstants.ACTIVITY_CONFIRMATION_ID %> || activityId == <%=IConstants.ACTIVITY_LIFE_EVENT_ID %>) { // Confirmation 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    		  document.getElementById("incrementPercentTR").style.display = "table-row";
    		  var strEmpId = document.getElementById("strEmpId2").value;
  	  		//alert("strEmpId ===> " + strEmpId);
  	  		var action = 'EmpActivityEmpSalaryDetails.action?empId=' + strEmpId;
  	  		getContent('salaryDetailsDiv', action);
    	  } else if(activityId == <%=IConstants.ACTIVITY_TEMPORARY_ID %>) { // Temporary 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    		  /* document.getElementById("idLevelL").style.display = "table-cell";
    		  document.getElementById("idLevelV").style.display = "table-cell";
    		  document.getElementById("idDesigL").style.display = "table-cell";
    		  document.getElementById("idDesigV").style.display = "table-cell";
    		  document.getElementById("idGradeL").style.display = "table-cell";
    		  document.getElementById("gradeVTD").style.display = "table-cell"; */
    	  } else if(activityId == <%=IConstants.ACTIVITY_PERMANENT_ID %>) { // Permanent 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
        	  document.getElementById("promotionTR").style.display = "table-row";        		  
    	  } else if(activityId == <%=IConstants.ACTIVITY_TRANSFER_ID %>) { // Transfer 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    		  document.getElementById("tranferTypeTR").style.display = "table-row";
    	  } else if(activityId == <%=IConstants.ACTIVITY_PROMOTION_ID %>) { // Promotion 
    		  //levelLTD levelVTD desigLTD desigVTD gradeLTD gradeVTD incrementPercentTR 
    		  
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    		  
    		  document.getElementById("levelLTD").style.display = "table-cell";
    		  document.getElementById("levelVTD").style.display = "table-cell";
    		  
    		  document.getElementById("desigLTD").style.display = "table-cell";
    		  document.getElementById("desigVTD").style.display = "table-cell";
    		  
    		  document.getElementById("gradeLTD").style.display = "table-cell";
    		  document.getElementById("gradeVTD").style.display = "table-cell";
    		  
    		  //document.getElementById("incrementPercentTR").style.display = "table-row";
    		  
    		  
    	  } else if(activityId == <%=IConstants.ACTIVITY_INCREMENT_ID %>) { // Increment 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    		  document.getElementById("incrementTypeTR").style.display = "table-row";
    	  		var strEmpId = document.getElementById("strEmpId2").value;
    	  		//alert("strEmpId ===> " + strEmpId);
    	  		var action = 'EmpActivityEmpSalaryDetails.action?empId=' + strEmpId;
    	  		getContent('salaryDetailsDiv', action);
    	  		
    	  } else if(activityId == <%=IConstants.ACTIVITY_GRADE_CHANGE_ID %>) { // Grade change 
    		  document.getElementById("gradeChangeLTD").style.display = "table-cell";
    		  document.getElementById("gradeChangeVTD").style.display = "table-cell";
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    		  //getGradesByLevel();
    	  } else if(activityId == <%=IConstants.ACTIVITY_TERMINATE_ID %>) { // Terminate 
    		 // document.getElementById("noticeTR").style.display = "table-row";
    		  document.getElementById("noticeTR").style.display = "none";
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
    		  document.getElementById("idEffectiveDate").style.display = "table-cell";
    	  } else if(activityId == <%=IConstants.ACTIVITY_NOTICE_PERIOD_ID %>) { //  Notice Period 
    		  document.getElementById("noticeTR").style.display = "table-row";
    		  document.getElementById("tdEffectiveLbl").style.display = "none";
    		  document.getElementById("idEffectiveDate").style.display = "none";
    	  } else if(activityId == <%=IConstants.ACTIVITY_RESIGNATION_WITHDRWAL_ID %>) { // Withdrawn Resignation 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  	    	  document.getElementById("idEffectiveDate").style.display = "table-cell";
    	  } else if(activityId == <%=IConstants.ACTIVITY_FULL_FINAL_ID %>) { // Full & Final 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  	    	  document.getElementById("idEffectiveDate").style.display = "table-cell";
    	  } else if(activityId == <%=IConstants.ACTIVITY_NEW_JOINEE_PENDING_ID %>) { // New Joinee Pending 
    		  document.getElementById("tdEffectiveLbl").style.display = "table-cell";
  	    	  document.getElementById("idEffectiveDate").style.display = "table-cell";
    	  }
    	  
      }
      
      
      function selectElements1(transferType) {
    		 //
    		 	disableAll1();
    		 
    	    	  if(transferType == 'WL') { //  Location
    	    		  
    	    		  document.getElementById("locationLTD").style.display = "table-cell";
    	    		  document.getElementById("locationVTD").style.display = "table-cell";
    	    	  } else if(transferType == 'DEPT') { // Department
    	    		  
    	    		  document.getElementById("deptLTD").style.display = "table-cell";
    	    		  document.getElementById("deptVTD").style.display = "table-cell";
    	    	  } else if(transferType == 'LE') { // Legal Entity
    	    		  
    	    		  document.getElementById("legalEntityTR").style.display = "table-row";
    	    		 
    	    		  document.getElementById("locationLTD").style.display = "none";
    	    		  document.getElementById("locationVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("sbuLTD").style.display = "none";
    	    		  document.getElementById("sbuVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("deptLTD").style.display = "none";
    	    		  document.getElementById("deptVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("levelLTD").style.display = "none";
    	    		  document.getElementById("levelVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("desigLTD").style.display = "none";
    	    		  document.getElementById("desigVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("gradeLTD").style.display = "none";
    	    		  document.getElementById("gradeVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("gradeChangeLTD").style.display = "none";
    	    		  document.getElementById("gradeChangeVTD").style.display = "none";
    	    	  
    	    	  } else { // 
    	    		  
    	    		  document.getElementById('strOrganisation').selectedIndex = 0;
    	        	  document.getElementById('strWLocation').selectedIndex = 0;
    	        	  document.getElementById('strLevel').selectedIndex = 0;
    	        	  document.getElementById('strDepartment').selectedIndex = 0;
    	        	  //document.getElementById('strDesignation').selectedIndex = 0;
    	        	  if(document.getElementById('strDesignation')){
    	          	  	document.getElementById('strDesignation').selectedIndex = 0;
    	          	  }
    	        	  document.getElementById('empGrade').selectedIndex = 0;
    	        	  
    	        	  document.getElementById("legalEntityTR").style.display = "none";
    	    		  
    	    		  document.getElementById("locationLTD").style.display = "none";
    	    		  document.getElementById("locationVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("sbuLTD").style.display = "none";
    	    		  document.getElementById("sbuVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("deptLTD").style.display = "none";
    	    		  document.getElementById("deptVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("levelLTD").style.display = "none";
    	    		  document.getElementById("levelVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("desigLTD").style.display = "none";
    	    		  document.getElementById("desigVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("gradeLTD").style.display = "none";
    	    		  document.getElementById("gradeVTD").style.display = "none";
    	    		  
    	    		  document.getElementById("gradeChangeLTD").style.display = "none";
    	    		  document.getElementById("gradeChangeVTD").style.display = "none";
    	    	  }
    	    	  
    	      }
      
      
      function showIncrementPercent(value) {
    	  
    	  if(value == '1' || value == '2') {
    		  document.getElementById("incrementPercentTR").style.display = "table-row";
    		  document.getElementById("strIncrementPercentage").value = "";
    	  } else {
    		  document.getElementById("incrementPercentTR").style.display = "none";
    		  document.getElementById("strIncrementPercentage").value = "";
    	  }
    	  
    	  <%
			for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
				java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			%>
					var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	 
					document.getElementById(""+<%=(String)cinnerlist.get(1)%>).value = amt;
			<%} %>
			if(document.getElementById("1")) {
				changeLabelValuesE("1");
			}
      }      
      
      function disableAll() {
    	  
    	  //strTransferType strOrganisation strWLocation strLevel strDepartment strDesignation empGrade
  			if(document.getElementById('strIncrementType')) {
    		  document.getElementById('strIncrementType').selectedIndex = 0;
  			}
    	  
  			if(document.getElementById('strTransferType')) {
  				 document.getElementById('strTransferType').selectedIndex = 0;
    		}
      	  
  			if(document.getElementById('strOrganisation')) {
  				document.getElementById('strOrganisation').selectedIndex = 0;
    		}
      	  
  			if(document.getElementById('strWLocation')) {
  		  	  document.getElementById('strWLocation').selectedIndex = 0;
    		}
      	  
  			if(document.getElementById('strLevel')) {
  				document.getElementById('strLevel').selectedIndex = 0;
    		}
  			
  			if(document.getElementById('strDepartment')) {
  				document.getElementById('strDepartment').selectedIndex = 0;
    		}
      	 
    	  //document.getElementById('strDesignation').selectedIndex = 0;
    	  if(document.getElementById('strDesignation')){
      	  	document.getElementById('strDesignation').selectedIndex = 0;
      	  }
    	  
    	  if(document.getElementById('empGrade')){
    		  document.getElementById('empGrade').selectedIndex = 0;
    	  }

    	  if(document.getElementById('promotionTR')){
    		  document.getElementById("promotionTR").style.display = "none"; 
    	  }
    	   
    	  if(document.getElementById('extendProbationTR')){
    		  document.getElementById("extendProbationTR").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('tranferTypeTR')){
    		  document.getElementById("tranferTypeTR").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('incrementTypeTR')){
    		  document.getElementById("incrementTypeTR").style.display = "none"; 
    	  }
    	  
    	  if(document.getElementById('salaryDetailsDiv')){
    		  document.getElementById("salaryDetailsDiv").innerHTML = "";
    	  }
		  
    	  if(document.getElementById('incrementPercentTR')){
    		  document.getElementById("incrementPercentTR").style.display = "none";
    	  }
    	  if(document.getElementById('legalEntityTR')){
    		  document.getElementById("legalEntityTR").style.display = "none";
    	  }
    	  if(document.getElementById('locationLTD')){
    		  document.getElementById("locationLTD").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('locationVTD')){
    		  document.getElementById("locationVTD").style.display = "none";
    	  }
		  
    	  if(document.getElementById('sbuLTD')){
    		  document.getElementById("sbuLTD").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('sbuVTD')){
    		  document.getElementById("sbuVTD").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('levelLTD')){
    		  document.getElementById("levelLTD").style.display = "none";
    	  }
    	  
    	  if(document.getElementById('levelVTD')){
    		  document.getElementById("levelVTD").style.display = "none";
    	  }
		 
    	  if(document.getElementById('desigLTD')){
    		  document.getElementById("desigLTD").style.display = "none";
    	  }
		  
    	  if(document.getElementById('desigVTD')){
    		  document.getElementById("desigVTD").style.display = "none";
    	  }
		  
    	  if(document.getElementById('deptLTD')){
    		  document.getElementById("deptLTD").style.display = "none";
    	  }
		  
    	  if(document.getElementById('deptVTD')){
    		  document.getElementById("deptVTD").style.display = "none";
    	  }
		  
    	  if(document.getElementById('gradeLTD')){
    		  document.getElementById("gradeLTD").style.display = "none";
    	  }
		  
    	  if(document.getElementById('gradeVTD')){
    		  document.getElementById("gradeVTD").style.display = "none";
    	  }
    	  if(document.getElementById('gradeChangeLTD')){
    		  document.getElementById("gradeChangeLTD").style.display = "none";
    	  }
    	  if(document.getElementById('gradeChangeVTD')){
    		  document.getElementById("gradeChangeVTD").style.display = "none";
    	  }
    	  if(document.getElementById('noticeTR')){
    		  document.getElementById("noticeTR").style.display = "none";
    	  }
    	  if(document.getElementById('probationTR')){
    		  document.getElementById("probationTR").style.display = "none";
    	  }
		
      }
      
function disableAll1() {
    	  //strTransferType strOrganisation strWLocation strLevel strDepartment
    	  if(document.getElementById('strOrganisation')) {
				document.getElementById('strOrganisation').selectedIndex = 0;
    	  }
    	  
    	  if(document.getElementById('strWLocation')) {
    		  document.getElementById('strWLocation').selectedIndex = 0
    	  }
    	  
    	  if(document.getElementById('strLevel')) {
    		  document.getElementById('strLevel').selectedIndex = 0;
    	  }
    	  
    	  if(document.getElementById('strDepartment')) {
    		  document.getElementById('strDepartment').selectedIndex = 0;
    	  }

	  //document.getElementById('strDesignation').selectedIndex = 0;
	  if(document.getElementById('strDesignation')){
  	  	document.getElementById('strDesignation').selectedIndex = 0;
  	  }
	  
	  if(document.getElementById('empGrade')){
		  document.getElementById('empGrade').selectedIndex = 0;
	  }
	
	  if(document.getElementById('promotionTR')){
		  document.getElementById("promotionTR").style.display = "none"; 
	  }
	   
	  if(document.getElementById('legalEntityTR')){
		  document.getElementById("legalEntityTR").style.display = "none"; 
	  }
	 
	  if(document.getElementById('locationLTD')){
		  document.getElementById("locationLTD").style.display = "none";
	  }
	  
	  if(document.getElementById('locationVTD')){
		  document.getElementById("locationVTD").style.display = "none";
	  }
	  
	  if(document.getElementById('sbuLTD')){
		  document.getElementById("sbuLTD").style.display = "none";
	  }
	  
	  if(document.getElementById('sbuVTD')){
		  document.getElementById("sbuVTD").style.display = "none";
	  }
	  
	  if(document.getElementById('deptLTD')){
		  document.getElementById("deptLTD").style.display = "none";
	  }
	 
	  if(document.getElementById('deptVTD')){
		  document.getElementById("deptVTD").style.display = "none";
	  }
	 
	  if(document.getElementById('levelLTD')){
		  document.getElementById("levelLTD").style.display = "none";
	  }
	 
	  if(document.getElementById('levelVTD')){
		  document.getElementById("levelVTD").style.display = "none";
	  }
	  
	  if(document.getElementById('desigLTD')){
		  document.getElementById("desigLTD").style.display = "none";
	  }
	  
	  if(document.getElementById('desigVTD')){
		  document.getElementById("desigVTD").style.display = "none";
	  }
	 
	  if(document.getElementById('gradeLTD')){
		  document.getElementById("gradeLTD").style.display = "none";
	  }
	  if(document.getElementById('gradeVTD')){
		  document.getElementById("gradeVTD").style.display = "none";
	  }
	 
	  if(document.getElementById('gradeVTD')){
		  document.getElementById("gradeVTD").style.display = "none";
	  }
	  
	  if(document.getElementById('gradeChangeLTD')){
		  document.getElementById("gradeChangeLTD").style.display = "none";
	  }
	
	  if(document.getElementById('gradeChangeVTD')){
		  document.getElementById("gradeChangeVTD").style.display = "none";
	  }
	  
      }
      
      
      disableAll();
      
      disableAll1();
      
	function showDesignation(strLevel) {
		var strActivity = document.getElementById("strActivity").value;
		var appFreqId = "";
		if(document.getElementById("appFreqId")) {
			appFreqId = document.getElementById("appFreqId").value;
		}
		
		if(strActivity !='' && strActivity == <%=IConstants.ACTIVITY_PROMOTION_ID %>){
			var strEmpId = '<%=(String) request.getAttribute("empid")%>';
			var id = '<%=(String) request.getAttribute("id")%>';
			var empid = '<%=(String) request.getAttribute("empid")%>';
			var thumbsFlag = '<%=(String) request.getAttribute("thumbsFlag")%>';
			var remarktype = '<%=(String) request.getAttribute("remarktype")%>';
			var idEffectiveDate = document.getElementById("idEffectiveDate").value; appraisal_freq
			var appraisal_freq = document.getElementById("appraisal_freq").value;
			var action ='AppriasalEmpActivityLevelSalary.action?dataType=D&strEmpId='+strEmpId+'&id='+id+'&empid='+empid+'&thumbsFlag='+thumbsFlag;
			action +='&remarktype='+remarktype+'&strActivity='+strActivity+'&idEffectiveDate='+idEffectiveDate+'&strLevel='+strLevel
			+"&appFreqId="+appFreqId+"&fromPage=AD";
			//window.location = action;
			
			$("#formBody").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			
			$.ajax({ 
				type : 'GET',
				url: action,
				cache: true,
				success: function(result){
					$("#formBody").html(result);
		   		}
			});
				
		} else {	
			 var action = 'GetDesigList.action?strLevel=' + strLevel + "&type=EA";
			 getContent('desigListSpan', action);
		}
     }
      
      
      function getGrades(value) {
    	  
    	  var action = 'GetGradeList.action?strDesignation=' + value + "&type=EA";
  		getContent('gradeListSpan', action);
      }
	  
      function getWLocSbuDeptLevelByOrg(value) {
    	  //alert("value --->>>>" + value); 
    	  if(parseInt(value) > 0){
    		 
    		  document.getElementById("locationLTD").style.display = "table-cell";
    		  document.getElementById("locationVTD").style.display = "table-cell";
    		  
    		  document.getElementById("sbuLTD").style.display = "table-cell";
    		  document.getElementById("sbuVTD").style.display = "table-cell";
    		  
    		  document.getElementById("deptLTD").style.display = "table-cell";
    		  document.getElementById("deptVTD").style.display = "table-cell";
    		  
    		  document.getElementById("levelLTD").style.display = "table-cell";
    		  document.getElementById("levelVTD").style.display = "table-cell";
    		  
    		  document.getElementById("desigLTD").style.display = "table-cell";
    		  document.getElementById("desigVTD").style.display = "table-cell";
    		  
    		  document.getElementById("gradeLTD").style.display = "table-cell";
    		  document.getElementById("gradeVTD").style.display = "table-cell";
    	  
	    	var action = 'GetOrgWLocationList.action?OID=' + value + "&type=EA";
	 		getContent('locationListSpan', action);
	 	
	 		var action = 'GetOrgServiceList.action?OID=' + value + "&type=EA";
	   		getContent('sbuListSpan', action);
	   		
	   		var action = 'GetOrgDepartmentList.action?OID=' + value + "&type=EA";
	   		getContent('deptListSpan', action);
	   		
	   		var action = 'GetOrgLevelList.action?OID=' + value + "&type=EA";
	   		getContent('levelListSpan', action);
    	  } else {
    		  document.getElementById("locationLTD").style.display = "none";
    		  document.getElementById("locationVTD").style.display = "none";
    		  
    		  document.getElementById("sbuLTD").style.display = "none";
    		  document.getElementById("sbuVTD").style.display = "none";
    		  
    		  document.getElementById("deptLTD").style.display = "none";
    		  document.getElementById("deptVTD").style.display = "none";
    		  
    		  document.getElementById("levelLTD").style.display = "none";
    		  document.getElementById("levelVTD").style.display = "none";
    		  
    		  document.getElementById("desigLTD").style.display = "none";
    		  document.getElementById("desigVTD").style.display = "none";
    		  
    		  document.getElementById("gradeLTD").style.display = "none";
    		  document.getElementById("gradeVTD").style.display = "none";
    	  }
      }
      
	function getGradesByLevel() {
    	  var levelId = document.getElementById("hidelevelId").value;
    	  //alert("levelId ===>> " + levelId);
    	  var action = 'GetGradeList.action?levelId=' + levelId + "&type=EA";
  		  getContent('gradeListSpan', action);
      }
	
	function addActivity(appraisal_freq){
		//	alert("add freq==>"+appraisal_freq);
			if(appraisal_freq!=null && appraisal_freq == "Weekly"){
				document.getElementById("activity_div").style.display = "block";
				document.getElementById("final_div").style.display = "none";
				document.getElementById("link_div").style.display = "block";
				document.getElementById("add_activity").style.display = "none";
				document.getElementById("close_activity").style.display = "block";
			}
		}
		
		function closeActivity(appraisal_freq){
			//alert("close freq==>"+appraisal_freq);
			if(appraisal_freq!=null && appraisal_freq == "Weekly"){
				document.getElementById("activity_div").style.display = "none";
				document.getElementById("final_div").style.display = "block";
				document.getElementById("link_div").style.display = "none";
				document.getElementById("add_activity").style.display = "block";
				document.getElementById("close_activity").style.display = "none";
			}
		}
		
		
		function show_designation() {
			dojo.event.topic.publish("show_designation");
		}
		
		function show_grade() {
			dojo.event.topic.publish("show_grade");
		}
			
		function show_employees() {
			dojo.event.topic.publish("show_employees");
		}

		var roundOffCondition='<%=(String)request.getAttribute("roundOffCondition") %>';
	
	function incrementBasicAmount(id) {
		var percentAmount = document.getElementById(id).value;
		if(parseFloat(percentAmount) > parseFloat("0") ){
			var strIncrementType = document.getElementById("strIncrementType").value;
			if(strIncrementType == '2'){
				percentAmount = parseFloat(percentAmount) * 2;
			}
			
			
			if(document.getElementById("301")) {
				var txt_amount = document.getElementById("hide_301").value;
				
				var newBasic = (parseFloat(txt_amount) * parseFloat(percentAmount)) / 100;
				
				var finalBasic = parseFloat(txt_amount) + parseFloat(newBasic);
				document.getElementById("301").value = parseFloat(finalBasic).toFixed(2);
				
				changeLabelValuesE("301");
			} else if(document.getElementById("1")) {
				var txt_amount = document.getElementById("hide_1").value;
				
				var newBasic = (parseFloat(txt_amount) * parseFloat(percentAmount)) / 100;
				
				var finalBasic = parseFloat(txt_amount) + parseFloat(newBasic);
				document.getElementById("1").value = parseFloat(finalBasic).toFixed(2);
				
				changeLabelValuesE("1");
			} else{
				<%  
				for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
					java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
					
				%>
					var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	
					var newAmt = (parseFloat(amt) * parseFloat(percentAmount)) / 100;
					
					var finalAmt = parseFloat(amt) + parseFloat(newAmt);
					document.getElementById(""+<%=(String)cinnerlist.get(1)%>).value = finalAmt.toFixed(2);
				<%}%>
							
			} 
		} else {
			<%
			for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
				java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			%>
				var amt = document.getElementById("hide_"+<%=(String)cinnerlist.get(1)%>).value; 	 
				document.getElementById(""+<%=(String)cinnerlist.get(1)%>).value = amt;
			<%}%>
				changeLabelValuesE("1");
		}  
	}
	
	function checkSalaryHeadDisable(){
		changeLabelValuesE('1');
	}
	
	function changeLabelValuesE(id)  {
		
		var disableSalaryStructure = document.getElementById("disableSalaryStructure");		
		if(disableSalaryStructure.checked == false){
			var reimbursementCTC = document.getElementById("reimbursementCTC").value;	
			var reimbursementCTCOptional = document.getElementById("reimbursementCTCOptional").value;
			<%  
				for (int i=0; i<couterlist.size(); i++) {
					List<String> cinnerlist = (List<String>)couterlist.get(i); 
					if(cinnerlist.get(4)!=null && (cinnerlist.get(4)).trim().equals("P") 
							&& cinnerlist.get(14) != null && !cinnerlist.get(14).trim().equals("") 
							&& !cinnerlist.get(14).trim().equalsIgnoreCase("NULL") && cinnerlist.get(14).trim().length() > 0){
						List<String> al = Arrays.asList(cinnerlist.get(14).trim().split(","));
						int nAl = al != null ? al.size() : 0;
			%>	
					var formula = "";
					var cnt = 0;
					var isReimbursementCTC = new Boolean(false);
			<%		for(int j = 0; j < nAl; j++){
						String str = al.get(j);
						if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")){
							boolean isInteger = uF.isInteger(str.trim());
							if(isInteger){
								if(uF.parseToInt(str.trim()) == IConstants.REIMBURSEMENT_CTC){
			%>
									formula += ""+(parseFloat(getRoundOffValue(reimbursementCTC)) + parseFloat(getRoundOffValue(reimbursementCTCOptional)));
									cnt++;
									isReimbursementCTC = true;
			<%					} else {
			%>
									var sHeadDisplay = "isDisplay_"+<%=str.trim()%>;
									if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
										var sHeadId = ""+<%=str.trim()%>;
										var sAnnualHeadId = "annual_"+<%=str.trim()%>;
										if(document.getElementById(sAnnualHeadId)){
											formula += ""+ parseFloat(getRoundOffValue(document.getElementById(sAnnualHeadId).value));
											cnt++;
										} else if(document.getElementById(sHeadId)){
											formula += ""+parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));
											cnt++;	
										}
									} else {
										formula += ""+parseFloat(getRoundOffValue('0'));
										cnt++;
									}
			<%					}
							} else {
			%>			
								formula += '<%=str.trim() %>'; 	
			<%				}
						}
					}
				%>
					var total = 0;
					if(cnt > 0 && formula.trim() != ''){
						var formulaCal = eval(formula);
						var percentage = '<%=cinnerlist.get(7) %>';
						total = (parseFloat(percentage) * parseFloat(formulaCal))/100;
						total = isNaN(total) ? 0 : total;
						
						/* if(isReimbursementCTC){
							total += parseFloat(getRoundOffValue(reimbursementCTCOptional));
						} */
					}
					
					document.getElementById(""+<%=cinnerlist.get(1)%>).value = getRoundOffValue(total);
					document.getElementById("tempValue_"+<%=cinnerlist.get(1)%>).value = getRoundOffValue(total);
				<%}%>		
			<%}  %>
		}
		calculateTotalEarningandDeduction();
	}

function calculateTotalEarningandDeduction(){
	var total = 0;
	var totalD = 0;
    <%  
		for (int j=0; couterlist!=null && j<couterlist.size(); j++) {
			java.util.List innerlist = (java.util.List)couterlist.get(j); 
			if(uF.parseToInt(""+innerlist.get(1)) == IConstants.CTC){
				continue;
			}
	%>
			var sSalED = ""+'<%=innerlist.get(3)%>';
			if(sSalED == 'E'){
				var sHeadDisplay = "isDisplay_"+<%=innerlist.get(1)%>;
				if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
					var sHeadId = ""+<%=innerlist.get(1)%>;
					if(document.getElementById(sHeadId)){
						total =  parseFloat(total) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
					}
				}
			} else if(sSalED == 'D'){
				var sHeadDisplay = "isDisplay_"+<%=innerlist.get(1)%>;
				if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
					var sHeadId = ""+<%=innerlist.get(1)%>;
					if(document.getElementById(sHeadId)){
						totalD =  parseFloat(totalD) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
					}
				}
			}
	<%}%>
	document.getElementById("total_earning_value").innerHTML = getRoundOffValue(Math.round(parseFloat(total)));
	document.getElementById("total_deduction_value").innerHTML = getRoundOffValue(Math.round(parseFloat(totalD)));
}

function getRoundOffValue(val) {
	var roundOffVal = 0;
	if(parseInt(roundOffCondition) == 1){
		roundOffVal = parseFloat(val).toFixed(1);
	} else if(parseInt(roundOffCondition) == 2){
		roundOffVal = parseFloat(val).toFixed(2);
	} else {
		roundOffVal = Math.round(parseFloat(val));
	}
	
	return roundOffVal;
}
	
	
	<%  
	String CCID= (String) request.getAttribute("CCID");
	String ccName = (String) request.getAttribute("CCNAME");
	String EMPNAME = (String)session.getAttribute("EMPNAME_P");
	
	if(EMPNAME==null)
		EMPNAME = (String)request.getAttribute("EMPNAMEFORCC");
	
	%>	
	
	//var cnt;
	
	var oldValues = new Array();
	
	<%  for (int i=0;  couterlist!=null && i<couterlist.size(); i++) {
			java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
	%>
			oldValues[<%=cinnerlist.get(1)%>] = "<%=cinnerlist.get(8)%>"; 
			
	<%}%>
	
	function makeZeroOnUncheck(displayId) {
		var headId = displayId.substring(displayId.indexOf("_")+1, displayId.length);
		
		if(!document.getElementById(displayId).checked) {
			if(document.getElementById(headId)) {
				oldValues[headId] = document.getElementById(headId).value;
				changeLabelValuesE(headId);
			}
		}else {
			if(document.getElementById(headId)) {
				document.getElementById(headId).value = oldValues[headId];
				changeLabelValuesE(headId);
			}
		}
	}
	
	function checkNoOfDays(type) {
		//alert("type==>"+type);''
		var days = "";
		if(type == 'N' ) {
			days = document.getElementById("strNoticePeriod").value;
			if(parseInt(days) > 180) {
				document.getElementById("strNoticePeriod").value = "";
				alert("No. of days should be less than or equal to 180 days!");
			}
		} else if(type == 'P' ) {
			days = document.getElementById("strProbationPeriod").value;
			if(parseInt(days) > 180) {
				document.getElementById("strProbationPeriod").value = "";
				alert("No. of days should be less than or equal to 180 days!");
			}
		} else if(type == 'E' ) {
			days = document.getElementById("strExtendProbationDays").value;
			if(parseInt(days) > 180) {
				document.getElementById("strExtendProbationDays").value = "";
				alert("No. of days should be less than or equal to 180 days!");
			}
		}
	}

	
	function checkGapStatus(id) {
		if(document.getElementById("sendtoGapStatus").checked) {
			document.getElementById("learningPlanSpan").style.display = 'block';
		} else {
			document.getElementById("learningPlanSpan").style.display = 'none';
		}
	}
	
/* ===start parvez date: 21-03-2023=== */	
	function editFinalAppraisalRating(appId, appFreqId, empID, dataType){
		var appId = '<%=id%>';
		var appFreqId = '<%=appFreqId %>';
		var fromPage = '<%=fromPage %>';
		var anscomment;
		var gradewithrating;
		if(document.getElementById("anscomment")){
			anscomment = document.getElementById("anscomment").value;
		}
		
		if(document.getElementById("gradewithrating")){
			gradewithrating = document.getElementById("gradewithrating").value;
		}
		
		$.ajax({
			url : "AppraisalRemark.action?strQueOrSec=QUESTION&id="+appId+"&empid="+empID+"&appFreqId="+appFreqId+"&dataType="+dataType+"&anscomment="+anscomment+"&gradewithrating="+gradewithrating,
			cache: true,
			success: function(result){
				getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
	   		},
			error: function(result){
				getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
			}
		});
	}
/* ===end parvez date: 21-03-2023=== */	
	
	</script>

<%
	String hrremark = (String) request.getAttribute("hrremark");
	String areasOfStrength = (String) request.getAttribute("areasOfStrength");
	String areasOfDevelopment = (String) request.getAttribute("areasOfDevelopment");
	List<String> alLearningPlans = (List<String>) request.getAttribute("alLearningPlans");
	
	String if_approved = (String) request.getAttribute("if_approved");
	boolean flag = (Boolean) request.getAttribute("flag");
	String strApprovedBy = (String)request.getAttribute("strApprovedBy");
	String appraisal_freq = (String)request.getAttribute("appraisal_freq");
	Map<String, String> hmActivityMap = (Map<String, String>) request.getAttribute("hmActivityMap"); 
	if(hmActivityMap == null) hmActivityMap = new HashMap<String, String>(); 
	String strTitle = (String)request.getAttribute(IConstants.TITLE);
	
	Map<String, String> outerMp = (Map<String, String>) request.getAttribute("outerMp");
	List<String> memberList = (List<String>) request.getAttribute("memberList");
	Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");
	
	String appraiseeName = (String)request.getAttribute("appraiseeName");
	
	boolean markApprovedFlag = (Boolean) request.getAttribute("markApprovedFlag");
	
	List<String> alOneOnOneDiscussion = (List<String>) request.getAttribute("alOneOnOneDiscussion");
	if(alOneOnOneDiscussion==null) alOneOnOneDiscussion = new ArrayList<String>();
	
	if (!flag ) {
%>
<%-- 	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="<%=strTitle %>" name="title" />
	</jsp:include> --%>
<%} %>
<div class="leftbox reportWidth" id="formBody">
	<s:form method="POST" action="AppraisalRemark" theme="simple" id="frmEmpActivity">
		<s:hidden name="id" />
		<s:hidden name="empid" />
		<s:hidden name="appFreqId" id="appFreqId" />
		<s:hidden name="thumbsFlag" />
		<s:hidden name="remarktype" />
		<s:hidden name="fromPage" />
		<input type="hidden" name="appraisal_freq" id= "appraisal_freq" value="<%=appraisal_freq %>" />

		<div style="float: left; width: 100%; margin-top: 10px;"><b><%=appraiseeName %></b>'s Performance Summary</div>
		<div style="float: left; width: 100%; margin-top: 20px;">  <!-- border: 1px solid #dbdbdb; -->
			<table class="table table-bordered" <% if(flag) { %> style="margin: 0px; border: 0px none !important;" <% } else { %> style="border: 0px none !important;"<% } %>>
			<% if (!flag) { %>
				<tr>
					<td colspan="2">
						<%
						String thumbsFlag = (String) request.getAttribute("thumbsFlag");
						if(!uF.parseToBoolean(thumbsFlag)) { %> 
							<%-- <span style="float: left; margin-left: 10px; margin-top: 5px;"><img style="height: 16px; width: 16px;" src="images1/thumbs_up_green.png"> </span> --%>
							<span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-up" style="color:#68ac3b;height: 16px; width: 16px;" aria-hidden="true"></i> </span>
							
						<% } else { %>
							<%-- <span style="float: left; margin-left: 10px; margin-top: 5px;"> <img style="height: 16px; width: 16px;" src="images1/thumbs_down_red.png"></span>  --%>
							<span style="float: left; margin-left: 10px; margin-top: 5px;"> <i class="fa fa-thumbs-down" style="color:#e22d2c;height: 16px; width: 16px;" aria-hidden="true"></i></span> 
							
						<% } %>
						
						<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION))) { %>
							<span style="float: left; margin-left: 10px; margin-top: 5px;">Send to Learning Gap <s:checkbox name="sendtoGapStatus" id="sendtoGapStatus" onclick="checkGapStatus(this)"/> </span>
							<span id="learningPlanSpan" style="display: none; float: left; margin-left: 20px; margin-top: 5px;"> 
								<select name="learningIds" id="learningIds" multiple="multiple"><%=(String)request.getAttribute("sbOptions") %></select>
							</span>
						<% } %>	
					</td>
				</tr>

				<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ACTIVITY_REVIEW_FINALIZE))) { %>
					<tr>
						<td style="text-align: right;" class="txtlabel" valign="top"><b>Performance Summary:<sup>*</sup></b>&nbsp;</td>
						<td><s:textarea name="remark" cssClass="validateRequired" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
					</tr>
				<% } %>
				<tr>
					<td style="text-align: right;" class="txtlabel" valign="top"><b>Areas of Strength:</b>&nbsp;</td>
					<td><s:textarea name="areasOfStrength" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
				</tr>
				<tr>
					<td style="text-align: right;" class="txtlabel" valign="top"><b>Areas of Development:</b>&nbsp;</td>
					<td><s:textarea name="areasOfDevelopment" cssStyle="width: 500px !important; height: 70px;"></s:textarea></td>
				</tr>
				<% } else { %>
				<tr>
					<td colspan="2" class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Performance Summary:</b>
						<div style="margin-left: 30px;"><%=hrremark != null ? hrremark : ""%></div>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Areas of Strength:</b>
						<div style="margin-left: 30px;"><%=areasOfStrength != null ? areasOfStrength : ""%></div>
					</td>
				</tr>
				<tr>
				<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_DISABLE_LEARNING_GAP_IN_REVIEW_FINALIZATION))) { %>
					<td class="txtlabel" valign="top" style="width:50%; padding-left: 10px !important;"><b>Areas of Development:</b>&nbsp;
						<div style="margin-left: 30px;"><%=areasOfDevelopment != null ? areasOfDevelopment : ""%></div>
					</td>
					<td class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Assigned Trainings</b>&nbsp;
						<div>
						<% if(alLearningPlans != null) { 
							for(int i=0; i<alLearningPlans.size(); i++) {
						%>
							<%=i+1 %>. <%=alLearningPlans.get(i) %><br/>
						<% }
						} else { %>
							No trainings assigned. 
						<% } %>
						</div>
					</td>
				<% } else { %>
					<td colspan="2" class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Areas of Development:</b>&nbsp;
						<div style="margin-left: 30px;"><%=areasOfDevelopment != null ? areasOfDevelopment : ""%></div>
					</td>
				<% } %>	
				</tr>
				<% } %>
			</table>
		</div>
		
		
		<div style="float: left; width: 100%;">
			<table class="table table-bordered" style="width: 100%; margin: 0px; border: 0px none !important;">
				<% if(memberList != null && memberList.size()>0) { %>
				<tr>
					<th colspan="2" style="text-align: center; background-color: #E3E3E3;">
						Final Rating and Comments by Review Panel
					</th>
				</tr>
				<% 
				//System.out.println("memberList ===>> " + memberList);
					for (int i = 0; i < memberList.size(); i++) {
				%>
					<tr>
						<td valign="top" width="65%"><b><%=orientationMemberMp.get(memberList.get(i))%>:</b>&nbsp;</td>
					<!-- ===start parvez date: 01-03-2023=== -->	
						<td><div style="float: left;" id="starPrimaryFinal<%=memberList.get(i).trim()%>"></div> 
							<div style="padding-top: 3px;">
							<b>
								<%-- <%=uF.showData(outerMp.get(memberList.get(i).trim()), "N/A") %>% --%>
								
								<%if(uF.parseToBoolean(outerMp.get("ACTUAL_CAL_BASIS"))){ %>
									<%=outerMp.get(memberList.get(i).trim())!=null?uF.getRoundOffValue(1, (uF.parseToDouble(outerMp.get(memberList.get(i).trim()))/20)) : "NA" %>
								<%} else{ %>
									<%-- <%=uF.showData(outerMp.get(memberList.get(i).trim()), "N/A") %>% --%>
									<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
										<%=outerMp.get(memberList.get(i).trim())!=null?uF.getRoundOffValue(1, (uF.parseToDouble(outerMp.get(memberList.get(i).trim()))/10)) : "NA" %>
									<%} else{ %>
										<%=uF.showData(outerMp.get(memberList.get(i).trim()), "N/A") %>%
									<%} %>
								<%} %>
							</b>
							</div>
					<!-- ===end parvez date: 01-03-2023=== -->	
					<!-- ===start parvez date: 10-03-2023=== -->	
						<%-- <script type="text/javascript">
	                         $('#starPrimaryFinal<%=memberList.get(i).trim()%>').raty({
	                         	readOnly: true,
	                         	start: <%=outerMp.get(memberList.get(i).trim()) != null ? uF.parseToDouble(outerMp.get(memberList.get(i).trim())) / 20 + "" : "0"%>,
	                         	half: true,
	                         	targetType: 'number'
	                         });
	                    </script> --%>
	                    <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
		                    <script type="text/javascript">
		                         $('#starPrimaryFinal<%=memberList.get(i).trim()%>').raty({
		                         	readOnly: true,
		                         	start: <%=outerMp.get(memberList.get(i).trim()) != null ? uF.parseToDouble(outerMp.get(memberList.get(i).trim())) / 10 + "" : "0"%>,
		                         	number: 10,
		                         	half: false,
		                         	targetType: 'number'
		                         });
		                    </script>
	                    <%} else{ %>
		                    <script type="text/javascript">
		                         $('#starPrimaryFinal<%=memberList.get(i).trim()%>').raty({
		                         	readOnly: true,
		                         	start: <%=outerMp.get(memberList.get(i).trim()) != null ? uF.parseToDouble(outerMp.get(memberList.get(i).trim())) / 20 + "" : "0"%>,
		                         	half: true,
		                         	targetType: 'number'
		                         });
		                    </script>
	                    <%} %>
	                <!-- ===end parvez date: 01-03-2023=== -->    
						</td>
					</tr>
					<% } %>
				<!-- ===start parvez date: 01-03-2023=== -->	
					<% //String aggregate = outerMp.get("AGGREGATE") != null ? uF.parseToDouble(outerMp.get("AGGREGATE")) / 20 + "" : "0";
					String aggregate = null;
					if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){
						aggregate = outerMp.get("AGGREGATE") != null ? uF.parseToDouble(outerMp.get("AGGREGATE")) / 10 + "" : "0";
					} else{
						aggregate = outerMp.get("AGGREGATE") != null ? uF.parseToDouble(outerMp.get("AGGREGATE")) / 20 + "" : "0";
					}
					%>
				<!-- ===end parvez date: 01-03-2023=== -->	
					<tr>
						<td valign="top" width="65%"><b>Final Score:</b>&nbsp;</td>
					<!-- ===start parvez date: 01-03-2023=== -->	
						<td><div style="float: left;" id="starPrimaryFinalScore"></div> 
						<%-- <b><%=uF.showData(outerMp.get("AGGREGATE"), "0.0") %>%</b> --%>
						<div style="padding-top: 3px;">
							<b>
							<%if(uF.parseToBoolean(outerMp.get("ACTUAL_CAL_BASIS"))){ %>
								<%=aggregate!=null ? uF.getRoundOffValue(1,uF.parseToDouble(aggregate)) : "0.0" %>
							<%} else{ %>
								<%-- <%=uF.showData(outerMp.get("AGGREGATE"), "0.0") %>% --%>
								<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
									<%=aggregate!=null ? uF.getRoundOffValue(1,uF.parseToDouble(aggregate)) : "0.0" %>
								<%} else{ %>
									<%=uF.showData(outerMp.get("AGGREGATE"), "0.0") %>%
								<%} %>
							<%} %>
							</b>
						<!-- ===start parvez date: 20-01-2023=== -->	
							<%-- <%if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))){ %>
								<div style="float: right; padding-top: 3px;">
									<a href="javascript: void(0)" onclick="editFinalAppraisalRating('<%=id %>','<%=appFreqId %>','<%=request.getAttribute("empid") %>','UFR')" title="Edit Final Rating"><i class="fa fa-pencil-square-o"></i></a>
								</div>
							<%} %> --%>
						<!-- ===end parvez date: 20-01-2023=== -->	
						</div>
						
						
						
				<!-- ===end parvez date: 01-03-2023=== -->	
				<!-- ===start parvez date: 01-03-2023=== -->	
						<%-- <script type="text/javascript">
	                         $('#starPrimaryFinalScore').raty({
	                         	readOnly: true,
	                         	start: <%=aggregate %>,
	                         	half: true,
	                         	targetType: 'number'
	                         });
	                     </script> --%>
	                     <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
		                     <script type="text/javascript">
		                         $('#starPrimaryFinalScore').raty({
		                         	readOnly: true,
		                         	start: <%=aggregate %>,
		                         	number: 10,
		                         	half: false,
		                         	targetType: 'number'
		                         });
		                     </script>
	                     <%} else{ %>
		                     <script type="text/javascript">
		                         $('#starPrimaryFinalScore').raty({
		                         	readOnly: true,
		                         	start: <%=aggregate %>,
		                         	half: true,
		                         	targetType: 'number'
		                         });
		                     </script>
	                     <%} %>
	           <!-- ===end parvez date: 01-03-2023=== -->          
						</td>
					</tr>
				<% } %>
			</table>
		</div>
		
	<!-- ===start parvez date: 21-03-2023=== -->	
	<%-- <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT)) && strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))){ %> --%>
	<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT))){ %>
		<div style="float: left; width: 100%; margin-top: 20px;">
			<table class="table table-bordered" style="width: 100%; margin: 0px; border: 0px none !important;">
				<tr>
					<th style="text-align: center; background-color: #E3E3E3;"><!-- colspan="2" -->
						Approval for Final Rating and Comment by HR
					</th>
				</tr>
				<tr>
					<td>
						<% 
							String aggregate = null;
							if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){
								aggregate = outerMp.get("AGGREGATE") != null ? uF.parseToDouble(outerMp.get("AGGREGATE")) / 10 + "" : "0";
							} else{
								aggregate = outerMp.get("AGGREGATE") != null ? uF.parseToDouble(outerMp.get("AGGREGATE")) / 20 + "" : "0";
							}
						%>
						<div style="margin-left: 80px;">
							<div style="float: left;" id="starPrimaryHRFinalScore"></div>
							<input type="hidden" id="gradewithrating" value="<%=aggregate%>" name="gradewithrating" />
							<b><%=aggregate!=null ? uF.getRoundOffValue(1,uF.parseToDouble(aggregate)) : "0.0" %></b>
						</div>
						<br/>
						<div style="padding:10px">
							<b>Comment: </b>
							<% if (flag) { %>
								<%=uF.showData((String)request.getAttribute("anscomment"),"") %>
							<%} else{ %>
							<s:textarea name="anscomment" id="anscomment" cssClass="validateRequired" cssStyle="width: 500px !important; height: 70px;"></s:textarea>
							<%} %>
						</div>
						<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
		                     <script type="text/javascript">
		                         $('#starPrimaryHRFinalScore').raty({
		                         	readOnly: false,
		                         	start: <%=aggregate %>,
		                         	number: 10,
		                         	half: false,
		                         	targetType: 'number',
		                         	click: function(score, evt) {
						        		$('#gradewithrating').val(score);
									}
		                         });
		                     </script>
	                     <%} else{ %>
		                     <script type="text/javascript">
		                         $('#starPrimaryHRFinalScore').raty({
		                         	readOnly: false,
		                         	start: <%=aggregate %>,
		                         	half: true,
		                         	targetType: 'number',
		                         	click: function(score, evt) {
						        		$('#gradewithrating').val(score);
									}
		                         });
		                     </script>
	                     <%} %>
	                     
					</td>
				</tr>
				<% if (!flag) { %>
				<tr>
				<td style="text-align: center;"><input type="submit" name="submit" value="Save" class="btn btn-primary" onclick="editFinalAppraisalRating('<%=id %>','<%=appFreqId %>','<%=request.getAttribute("empid") %>','UFR')" /></td>
				</tr>
				<%} %>
				
			</table>
		</div>
		
	<%} %>
	<!-- ===end parvez date: 21-03-2023=== -->	
		
		<% 	String managerRemark = (String) request.getAttribute("managerRemark");
			String strRecommendBy = (String) request.getAttribute("strRecommendBy");
		%>
		<% if (managerRemark != null && !managerRemark.equals("")) { %>
			<div style="float: left; width: 100%; margin-top: 20px;">  <!-- border: 1px solid #dbdbdb; -->
				<table class="table table-bordered" style="margin: 0px; border: 0px none !important;" >
				<tr>
					<th colspan="2" style="text-align: center; background-color: #E3E3E3;">
						Manager Recommendation
					</th>
				</tr>
				
					<tr>
						<td colspan="2" class="txtlabel" valign="top" style="padding-left: 10px !important;"><!-- <b>Recommendation:</b> -->
							<div><%=managerRemark != null ? managerRemark : "" %></div>  <!-- style="margin-left: 30px;" -->
						</td>
					</tr>
					<%-- <tr>
						<td colspan="2" class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Areas of Strength:</b>
							<div style="margin-left: 30px;"><%=areasOfStrength != null ? areasOfStrength : ""%></div>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="txtlabel" valign="top" style="width:50%; padding-left: 10px !important;"><b>Areas of Development:</b>&nbsp;
							<div style="margin-left: 30px;"><%=areasOfDevelopment != null ? areasOfDevelopment : ""%></div>
						</td>
					</tr> --%>
					<tr><td colspan="2" style="padding-right: 20px;" align="right"><i>Recommended by - <%=strRecommendBy%></i></td></tr>
				</table>
			</div>
		<% } %>
		
	<!-- ===start parvez date: 01-03-2023=== -->	
		<%-- <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
			<%if(alOneOnOneDiscussion!=null && alOneOnOneDiscussion.size()>0){ %>
				<div style="float: left; width: 100%; margin-top: 20px;">
					<table class="table table-bordered" style="width: 100%; margin: 0px; border: 0px none !important;">
						<tr>
							<th style="text-align: center; background-color: #E3E3E3;"><!-- colspan="2" -->
								One on One Discussion
							</th>
						</tr>
						<tr>	
							<td class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Employee:</b>
								<div style="margin-left: 30px;"><%=alOneOnOneDiscussion!=null && alOneOnOneDiscussion.size()>0 ? alOneOnOneDiscussion.get(1) : ""%></div>
							</td>
						</tr>
						<tr>	
							<td class="txtlabel" valign="top" style="padding-left: 10px !important;"><b>Manager:</b>
								<div style="margin-left: 30px;"><%=alOneOnOneDiscussion!=null && alOneOnOneDiscussion.size()>0 ? alOneOnOneDiscussion.get(2) : ""%></div>
							</td>
						</tr>
					</table>
				</div>
			<%} %>
		<%} %> --%>
	<!-- ===end parvez date: 01-03-2023=== -->	
		
		<% if (flag && hmActivityMap!=null && !hmActivityMap.isEmpty()) { %>
		<div style="float: left; width: 100%;"> <!-- border: 1px solid #dbdbdb;  -->
			<!-- <h4>Activity:</h4> -->
			<table class="table table-bordered" cellpadding="0" cellspacing="0" style="border: 0px none !important;"><!-- style="width: 50%;" table-bordered -->
				<tr>
					<th style="text-align: right; width: 35%; border-top: 0px;" nowrap="nowrap">Activity:</th>
					<td style="border-top: 0px;" nowrap="nowrap"><%=uF.showData(hmActivityMap.get("ACTIVITY_NAME"), "") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Effective Date:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("EFFECTIVE_DATE"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Reason:</th>
					<td><%=uF.showData(hmActivityMap.get("REASON"),"") %></td>
				</tr>
				<%if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_EXTEND_PROBATION_ID) || uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_NOTICE_PERIOD_ID) || uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_PROBATION_ID)){ %>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">No. Of Days:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("NO_OF_DAYS"),"") %></td>
				</tr>
				<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_CONFIRMATION_ID)){ %>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Increment
						Percentage:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("INCREMENT_PERCENTAGE"),"") %></td>
				</tr>
				<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_GRADE_CHANGE_ID)){ %>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Grade:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("GRADE_NAME"),"") %></td>
				</tr>
				<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_INCREMENT_ID)){ %>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Increment Type:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("INCREMENT_TYPE"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Increment
						Percentage:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("INCREMENT_PERCENTAGE"),"") %></td>
				</tr>
				<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_PROMOTION_ID)){ %>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Level:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("LEVEL_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Designation:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("DESIG_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Grade:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("GRADE_NAME"),"") %></td>
				</tr>
				<%} else if(uF.parseToInt(hmActivityMap.get("ACTIVITY_ID")) == uF.parseToInt(IConstants.ACTIVITY_TRANSFER_ID)){ 
							if(hmActivityMap.get("TRANSFER_TYPE") != null && hmActivityMap.get("TRANSFER_TYPE").equals("WL")){
					%>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Transfer Type:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("TRANSFER_TYPE_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Work Location:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("WORK_LOCATION_NAME"),"") %></td>
				</tr>
				<%} else if(hmActivityMap.get("TRANSFER_TYPE") != null && hmActivityMap.get("TRANSFER_TYPE").equals("DEPT")){%>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Transfer Type:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("TRANSFER_TYPE_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Department:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("DEPARTMENT_NAME"),"") %></td>
				</tr>
				<%} else if(hmActivityMap.get("TRANSFER_TYPE") != null && hmActivityMap.get("TRANSFER_TYPE").equals("LE")){%>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Transfer Type:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("TRANSFER_TYPE_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Organisation:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("ORG_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Work Location:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("WORK_LOCATION_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">SBU:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("SERVICE_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Department:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("DEPARTMENT_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Level:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("LEVEL_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Designation:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("DESIG_NAME"),"") %></td>
				</tr>
				<tr>
					<th style="text-align: right;" nowrap="nowrap">Grade:</th>
					<td nowrap="nowrap"><%=uF.showData(hmActivityMap.get("GRADE_NAME"),"") %></td>
				</tr>
				<%} %>
				<%} %>
			</table>
		</div>
		<%} %>
		
		<div style="float: left; width: 100%;">
			<table class="table table_no_border">
			<% if (flag) { %>
				<tr><td colspan="2" style="padding-right: 20px;" align="right"><strong><i>Appraised by - <%=strApprovedBy%></i></strong></td></tr>
			<% } %>
			</table>
		</div>
		
		
		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ACTIVITY_REVIEW_FINALIZE))) { %>
			<%if (hmActivityMap == null || hmActivityMap.isEmpty()) {
				String strDisplay = "block";
				String strLabel = "";
				if(appraisal_freq!=null && appraisal_freq.equalsIgnoreCase("Weekly")) {
					strDisplay = "none";				
				} 
				
				if (!flag) {
			%>
			<%if(appraisal_freq!=null && appraisal_freq.equalsIgnoreCase("Weekly")){ %>
				<div id="btn_div" style="margin-bottom: 10px;">
					<div>
						<a href="#" onclick="addActivity('<%=appraisal_freq %>');event.preventDefault();"><i class="fa fa-plus-circle"></i>Add Activity</a>
					</div>
					<div>
						<s:submit name="submit1" cssClass="btn btn-primary" value="Finalise" />
					</div>
				</div>
			<% } %>
			<div id="link_div" class="box box-none nav-tabs-custom clr" style="display:<%=strDisplay%>; float: left; margin-top: 15px;">
				<%
					String dataType = (String) request.getAttribute("dataType");
					String urlA = "AppraisalRemark.action?dataType=A&strEmpId="+(String) request.getAttribute("strEmpId2")+"&id=" +(String) request.getAttribute("id")+"&fromPage="+fromPage
						+ "&empid=" +(String) request.getAttribute("empid") + "&thumbsFlag=" + (String) request.getAttribute("thumbsFlag")+"&appraisal_freq="+ appraisal_freq +"&remarktype=" + (String) request.getAttribute("remarktype")+"&appFreqId=" + (String) request.getAttribute("appFreqId");
					String urlD = "AppraisalRemark.action?dataType=D&strEmpId="+(String) request.getAttribute("strEmpId2")+"&id=" +(String) request.getAttribute("id")+"&fromPage="+fromPage
						+ "&empid=" +(String) request.getAttribute("empid") + "&thumbsFlag=" + (String) request.getAttribute("thumbsFlag") +"&appraisal_freq="+ appraisal_freq +"&remarktype=" + (String) request.getAttribute("remarktype")+"&appFreqId=" + (String) request.getAttribute("appFreqId");
				%>
				<%-- <div id ="link_div" style="float:left;display:<%=strDisplay%>"> --%>
				<% 
							if(dataType == null || dataType.equals("A")) { 
								strLabel ="Activity";
						%>
				<ul class="nav nav-tabs" style="display:<%=strDisplay%>">
					<li class="active"><a href="javascript:void(0)" onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
					</li>
					<li><a href="javascript:void(0)" onclick="getTabContent('activitywdoc')" data-toggle="tab">Activity W/Doc</a>
					</li>
				</ul>
				<% } else if(dataType != null && dataType.equals("D")) { 
								 strLabel ="Activity W/Doc";
							%>
				<ul class="nav nav-tabs" style="display:<%=strDisplay%>">
					<li><a href="javascript:void(0)" onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
					</li>
					<li class="active"><a href="javascript:void(0)" onclick="getTabContent('activitywdoc')" data-toggle="tab">Activity W/Doc</a>
					</li>
				</ul>
				<% }%>
				
				<script>
				function getTabContent(condition) {
					$(".modal-body").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
					if(condition === 'activity') {
						var action = '<%=urlA  %>';
					}else{ 
						var action = '<%=urlD  %>';
					}
					$.ajax({ 
						type : 'GET',
						url: action,
						cache: true,
						success: function(result) {
							$(".modal-body").html(result);
				   		}
					});
				}
				
				</script>
				<%  } %>
	
				<s:hidden name="strEmpId2" id="strEmpId2" />
				<s:hidden name="dataType"></s:hidden>
	
				<div id="activity_div" style="display: <%=strDisplay %>; width:100%;" class="tab-content">
	
					<table class="table table_no_border autoWidth">
						<tr>
							<td class="txtlabel" colspan="2"><strong>New <%=strLabel %> Information</strong></td>
						</tr>
						<tr>
							<td colspan=4><s:fielderror /></td>
						</tr>
						<tr>
							<td class="txtlabel alignRight">Activity:<sup>*</sup>
							</td>
							<td><s:select name="strActivity" id="strActivity" listKey="activityId" theme="simple" cssClass="validateRequired" listValue="activityName" headerKey=""
									headerValue="Select Activity" list="activityList" key="" onchange="selectElements(this.value)" />
								<span class="hint">Select an activity from the list.<span class="hint-pointer">&nbsp;</span></span>
							</td>
							<td class="txtlabel alignRight" id="tdEffectiveLbl">Effective Date:<sup>*</sup>
							</td>
							<td><s:textfield name="effectiveDate" id="idEffectiveDate" cssClass="validateRequired text-input" />
							<span class="hint">Add the effective date.<span class="hint-pointer">&nbsp;</span>
							</span>
							</td>
						</tr>
	
						<tr id="promotionTR">
							<td class="txtlabel alignRight" valign="top">Leave Balance:</td>
							<td colspan="3">
								<%if(leaveTypeListWithBalance != null && leaveTypeListWithBalance.size() >0 && !leaveTypeListWithBalance.isEmpty()) { %>
								<table>
								<% 
									for(int i=0; leaveTypeListWithBalance != null && !leaveTypeListWithBalance.isEmpty() && i<leaveTypeListWithBalance.size(); i++) {
									List<String> innerList = leaveTypeListWithBalance.get(i);
								%>
									<tr>
										<td class="txtlabel alignLeft"><input type="checkbox" name="leaveTypeId" id="leaveTypeId" value="<%=innerList.get(0)%>" /> <%=innerList.get(1) %>:</td>
										<td class="txtlabel alignLeft"><input type="text" name="leaveBal_<%=innerList.get(0) %>" id="leaveBal_<%=innerList.get(0) %>" value="<%=innerList.get(2) %>" style="width: 41px !important; text-align: right;" /></td>
									</tr>
									<%} %>
								</table>
								<%} else { %>
								  	N/A.
								<%} %>
							</td>	
						</tr>
	
						<tr id="extendProbationTR">
							<td class="txtlabel alignRight">No. of Days:<sup>*</sup>
							</td>
							<td><s:textfield name="strExtendProbationDays" id="strExtendProbationDays" cssClass="validateRequired" onkeypress="return isNumberKey(event)" onkeyup="checkNoOfDays('E')" /></td>
							<td class="txtlabel alignRight"></td>
							<td></td>
						</tr>
	
						<tr id="tranferTypeTR">
							<td class="txtlabel alignRight">Select Transfer Type:<sup>*</sup>
							</td>
							<td><s:select name="strTransferType" id="strTransferType" theme="simple" cssClass="validateRequired" headerKey="" headerValue="Select Transfer Type"
									list="#{'WL':'Work Location', 'DEPT':'Department', 'LE':'Legal Entity'}" onchange="selectElements1(this.value)" /></td>
							<td class="txtlabel alignRight"></td>
							<td></td>
						</tr>
	
						<tr id="incrementTypeTR">
							<td class="txtlabel alignRight">Select Increment Type:<sup>*</sup>
							</td>
							<td><s:select name="strIncrementType" id="strIncrementType" theme="simple" cssClass="validateRequired" headerKey=""
									headerValue="Select Increment Type" list="#{'1':'Single', '2':'Double'}" onchange="showIncrementPercent(this.value)" /></td>
							<td class="txtlabel alignRight"></td>
							<td></td>
						</tr>
	
						<tr id="legalEntityTR">
							<td class="txtlabel alignRight">Legal Entity:<sup>*</sup>
							</td>
							<td><s:select name="strOrganisation" id="strOrganisation" theme="simple" listKey="orgId" cssClass="validateRequired"
									listValue="orgName" headerKey="" headerValue="Select Legal Entity" list="organisationList1" key="" required="true"
									onchange="getWLocSbuDeptLevelByOrg(this.value);" /></td>
							<td class="txtlabel alignRight"></td>
							<td></td>
						</tr>
						<tr>
							<td class="txtlabel alignRight" id="locationLTD">Location:<sup>*</sup>
							</td>
							<td id="locationVTD"><span id="locationListSpan"> <s:select name="strWLocation" id="strWLocation" theme="simple" listKey="wLocationId" cssClass="validateRequired"
										listValue="wLocationName" headerKey="" headerValue="Select Work Location" list="wLocationList1" key="" required="true" /> </span> 
								<span class="hint">Add the work location name. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span>
							</span></td>
							<td class="txtlabel alignRight" id="sbuLTD">SBU:<sup>*</sup>
							</td>
							<td id="sbuVTD">
								<div id="sbuListSpan">
									<s:select name="strSBU" id="strSBU" theme="simple" listKey="serviceId" cssClass="validateRequired" listValue="serviceName" headerKey="" headerValue="Select SBU" list="serviceList1" key="" />
								</div></td>
						</tr>
	
						<tr>
							<td class="txtlabel alignRight" id="deptLTD">Department:<sup>*</sup>
							</td>
							<td id="deptVTD">
								<div id="deptListSpan">
									<s:select name="strDepartment" id="strDepartment" theme="simple" listKey="deptId" cssClass="validateRequired" listValue="deptName" headerKey=""
										headerValue="Select Department" list="departmentList1" key="" required="true" />
								</div> <span class="hint">Add the department name. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span>
							</span></td>
	
							<td class="txtlabel alignRight" id="levelLTD">Level:<sup>*</sup>
							</td>
							<td id="levelVTD"><span id="levelListSpan"> <s:select name="strLevel" id="strLevel" theme="simple" listKey="levelId"
										cssClass="validateRequired" listValue="levelCodeName" headerKey="" headerValue="Select Level" onchange="showDesignation(this.value);" list="levelList1" key="" /> </span> <!-- onchange="javascript:show_designation();return false;" -->
								<span class="hint">Add the level name. This will be displayed in time-sheets and clock entries.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
	
						<tr>
							<td class="txtlabel alignRight" id="desigLTD">Designation:<sup>*</sup>
								<%-- <s:hidden name="strDesignation"/> --%></td>
							<td id="desigVTD"><span id="desigListSpan"> <s:select theme="simple" name="strDesignation" id="strDesignation"
										listKey="desigId" cssClass="validateRequired" listValue="desigCodeName" headerKey=""
										headerValue="Select Designation" list="desigList" key="" required="true" onchange="getGrades(this.value);" /> </span></td>
	
							<td class="txtlabel alignRight" id="gradeLTD">Grade:<sup>*</sup>
								<s:hidden name="strGrade" /></td>
							<td id="gradeVTD"><span id="gradeListSpan"> <%-- <s:select theme="simple" name="empGrade" id="empGrade" cssClass="validateRequired" list="gradeList" 
							listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" /> --%>
									<s:select theme="simple" name="empGrade" id="empGrade" cssClass="validateRequired" list="gradeChangeList"
										listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" /> </span> <%-- <s:url id="gradeList_url" action="GetGradeList" />
					 		<sx:div href="%{gradeList_url}" listenTopics="show_grade" formId="frmEmpActivity" showLoadingText="true"></sx:div> --%>
							</td>
	
							<td class="txtlabel alignRight" id="gradeChangeLTD">Grade:<sup>*</sup>
								<s:hidden name="strGrade" /></td>
							<td id="gradeChangeVTD"><span id="gradeChangeListSpan">
									<s:select theme="simple" name="empChangeGrade" id="empChangeGrade" cssClass="validateRequired" list="gradeChangeList" listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" /> </span></td>
	
						</tr>
	
						<tr id="noticeTR" style="display: none">
							<td class="txtlabel alignRight">No. of Days:<sup>*</sup></td>
							<td><s:textfield name="strNoticePeriod" id="strNoticePeriod" cssClass="validateRequired" onkeypress="return isNumberKey(event)" onkeyup="checkNoOfDays('N')" /></td>
	
							<td class="txtlabel alignRight"></td>
							<td></td>
						</tr>
	
	
						<tr id="probationTR" style="display: none">
							<td class="txtlabel alignRight" id="idPeriodL">No. of Days:<sup>*</sup></td>
							<td id="idPeriodV"><s:textfield name="strProbationPeriod" id="strProbationPeriod" cssClass="validateRequired" onkeypress="return isNumberKey(event)" onkeyup="checkNoOfDays('P')" /></td>
	
							<td class="txtlabel alignRight"></td>
							<td></td>
						</tr>
	
						<tr id="incrementPercentTR" style="display: none">
							<td class="txtlabel alignRight" id="idPeriodL">Increment Percentage:<sup>*</sup></td>
							<td id="idPeriodV"><s:textfield name="strIncrementPercentage" id="strIncrementPercentage" cssClass="validateRequired" maxlength="5" onkeyup="incrementBasicAmount(this.id)" onkeypress="return isNumberKey(event)" cssStyle="width:50px;" /></td>
	
							<td class="txtlabel alignRight"></td>
							<td></td>
						</tr>
	
						<tr>
							<td class="txtlabel alignRight" valign="top" id="reasonLTD">Reason:<sup>*</sup></td>
							<td colspan="3" id="reasonVTD"><s:textarea name="strReason" cssClass="validateRequired" cssStyle="width: 500px !important; height: 70px;"></s:textarea>
							<span class="hint">Add the reason for this activity for reference.<span class="hint-pointer">&nbsp;</span>
							</span></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td colspan="3">
								<%if(fromPage != null && fromPage.equals("AD")) { %> 
									<input type="submit" name="submit" value="Finalise" class="btn btn-primary" />
								<% } else { %>
									<s:submit name="submit" value="Finalise" cssClass="btn btn-primary" /> 
								<% } %>
							</td>
							<!-- <td colspan="3">
								<div id = "cancel_div" style="margin-left:10px;display:none;">
									<input type="button" value="Cancel" style="float:left;" class="input_button" onclick="cancelActivity();"/> 
								</div>	
							</td> -->
						</tr>
					</table>
				</div>
			</div>
			
			<div id="salaryDetailsDiv" style="float: left; width: 99%;"></div>
			<% } %>
		<% } else { %>
			<%-- <% if (!flag) { %> --%>
			<% if (!flag) { %>
				<%if(hmFeatureStatus!=null && ((uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT)) && markApprovedFlag)
						|| !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT)))){ %>
					<div>
						<s:submit name="submit" cssClass="btn btn-primary" value="Finalise" />
					</div>
				<% } %>
			<% } %>	
		<% } %>
	</s:form>
</div>


<script>
disableAll();
disableAll1();


$("#frmEmpActivity").submit(function(e){
	e.preventDefault();
	var appId = '<%=id%>';
	var appFreqId = '<%=appFreqId %>';
	var fromPage = '<%=fromPage %>';
		/* alert("updateMyReview jsp ==appId==>"+appId+"==>appFreqId==>"+appFreqId); */
		
	var form_data = $("#frmEmpActivity").serialize();
	$.ajax({ 
		type : 'POST',
	//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
		url: "AppraisalRemark.action",
		data: form_data+"&submit=Finalise",
		cache: true,
		success: function(result){
			getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
   		},
		error: function(result){
			getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
		}
	});
});

</script>

<% } %>
<div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog modal-dialog1">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1"></h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
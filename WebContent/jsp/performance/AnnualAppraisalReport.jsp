<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants" %>
<%@page import="com.konnect.jpms.util.UtilityFunctions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/_rating/js/jquery.raty.js"> </script>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>
<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.9.2/html2pdf.bundle.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/0.4.1/html2canvas.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/1.0.272/jspdf.debug.js"></script>

<style>
	table-bordered>thead>tr>th, .table-bordered>tbody>tr>th,
		.table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td,
		.table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td {
		border: 1px solid #fff !important;
	}
	.table-striped>tbody>tr:nth-of-type(odd) {
		background-color: #f9f9f9;
	}
	.commentDiv{
		background-color: #257acd;
		color: white;
		font-weight: bold;
		text-align: center;
		font-size: 15px;
	}
	.report-custom-legend{
		display: inline-block;
	    border-left: 20px solid;
	    vertical-align: top;
	    margin: 3px 5px;
	    padding: 0px 5px;
	    line-height: 10px;
	}
	.not-meet-expectations{
		border-color: #d20010;
	}
	.needs-improvement{
		border-color: #efae26;
	}
	.needs-improvement1{
		border-color: #e87b00;
	}
	.meet-expectations{
		border-color: #f6d83c;
	}
	.meet-expectations-h{
		border-color: #ffc000;
	}
	.exceeds-expectations{
		border-color: #3fce4b;
	}
	.outstanding{
		border-color: #00602e;
	}
	.chartwrapper {
	  width: 70%;
	  position: relative;
	  padding-bottom: 50%;
	  box-sizing: border-box;
	 
	}
	
	.chartdiv {
	  position: absolute;
	  width: 100%;
	  height: 200px;
	 
	}
</style>
<script type="text/javascript">
	var kra360 = [];
	var kra90 = [];
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	Map hmOrganistaionMap = (Map)request.getAttribute("hmOrganistaionMap");
	
	List<String> memberList360 = (List<String>) request.getAttribute("memberList");
	List<String> memberList90 = (List<String>) request.getAttribute("memberList90");
	//System.out.println("memberList360 : "+memberList360);
	//System.out.println("memberList90 : "+memberList90);
	Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");
	//System.out.println("orientationMemberMp : "+orientationMemberMp);
	Map<String, List< List<String>>> hmAppraisalSections = (Map<String, List< List<String>>>) request.getAttribute("hmAppraisalSections");
	//System.out.println("hmAppraisalSections 11: "+hmAppraisalSections);
	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
	if (hmEmpProfile == null) {
		hmEmpProfile = new HashMap<String, String>();
	}
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	String docSaveLocation = (String) request.getAttribute("DOC_SAVE_LOCATION");
	
	String appId360 = (String) request.getAttribute("appId360");
	String appId90 = (String) request.getAttribute("appId90");
	Integer memberCount360 = (Integer) request.getAttribute("memberCount360");
	//System.out.println("innerCount3601 : "+memberCount360);
	
	Map<String, Map<String, String>> outerMp = (Map<String, Map<String, String>>) request.getAttribute("outerMp");
	
	Map<String, Map<String, String>> hmHeaderCount = (Map<String, Map<String, String>>) request.getAttribute("hmHeaderCount");
	//System.out.println("hmHeaderCount : "+hmHeaderCount);
	Map<String, String> hmfinalData = (Map<String, String>) request.getAttribute("hmfinalData");
	if(hmfinalData == null) hmfinalData = new HashMap<String, String>();
	//System.out.println("hmfinalData : "+hmfinalData);
	Map<String, Map<String, List<String>>> hmComment = (Map<String, Map<String, List<String>>>) request.getAttribute("hmComment");
	if(hmComment == null) hmComment = new HashMap<String, Map<String, List<String>>>();
	
	//Created By Dattatray Date : 19-07-21 Note : hmCommentKRA
	Map<String, Map<String, List<String>>> hmCommentKRA = (Map<String, Map<String, List<String>>>) request.getAttribute("hmCommentKRA");
	if(hmCommentKRA == null) hmCommentKRA = new HashMap<String, Map<String, List<String>>>();
	
	Map<String, List<String>> hmDetails = (Map<String, List<String>>) request.getAttribute("hmDetails");
	if(hmDetails == null) hmDetails = new HashMap<String, List<String>>();
	
	Map<String, String> hmKRA90W = (Map<String, String>) request.getAttribute("hmKRA90W");
	if(hmKRA90W == null) hmKRA90W = new HashMap<String, String>();
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus"); 
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	
	Map<String, String> hmSectionWeightage = (Map<String, String>) request.getAttribute("hmSectionWeightage");
	if(hmSectionWeightage == null) hmSectionWeightage = new HashMap<String, String>();
	//System.out.println("hmfinalData : "+hmfinalData);
%>

<div style="float: left; margin-left: 5px;"><a id="download" onclick="getAppraisalReporyPDF();" href="javascript:void(0)" ><i class="fa fa-file-pdf-o"></i></a></div>
<div id="divAppraisalReporyData">
	 <div class="col-lg-12 col-md-12 col-sm-12" style=" padding: 0px;   display: flex;">
	<!-- <div class="row" style="margin: 0px;"> -->
		<div style="border: 3px solid black;height: 100px;width: 100px;padding: 0px;" >
			<%
				if(hmOrganistaionMap != null && hmOrganistaionMap.size()>0) {
					Set setOrganisationMap = hmOrganistaionMap.keySet();
					Iterator itOrg = setOrganisationMap.iterator();
					
					while(itOrg.hasNext()){
						String strOrgId = (String)itOrg.next();
						List alOrg = (List)hmOrganistaionMap.get(strOrgId);
						if(alOrg==null)alOrg=new ArrayList();
			%>
				 <img src="<%=alOrg.get(3)%>" data-original="<%=alOrg.get(3)%>" width="100%" height="100%" />
				 <%} %>
			<%} %>
		</div>
		
		<div style="padding: 0px 0px 0px 20px;width: 100%;">
		<div style="text-align: center;color: white;height: 100px;background-color: #257acd;clip-path: polygon(0 0, 100% 0%, 100% 100%, 5% 100%);">
			 <div style="display: inline-block; text-align: right;position: relative;top: 30px;">
			 		<%
			      		List<String> app360Details = hmDetails.get(appId360);
			      		List<String> app90Details = hmDetails.get(appId360);
			      		String strAppFYr360 = "";
			      		String strAppFYr90 = "";
			      		//System.out.println("app90Details : "+app90Details);
			      	%>
			      	<% if((appId90 !=null && uF.parseToInt(appId90) >0 && appId360 !=null && uF.parseToInt(appId360) >0) ){
			      		strAppFYr360 =app360Details.get(1);
			      	%>
					<% } else if(appId360 !=null && uF.parseToInt(appId360) > 0 && appId90 ==null) { 
						strAppFYr360 =app360Details.get(1);
					%>
					<% } else if(appId90 !=null && uF.parseToInt(appId90) > 0 && appId360 ==null) { 
						strAppFYr90 = app90Details.get(1);
					%>
					<% } %>
			      	<span style="font-weight: bold;font-size: 23px;">Annual Appraisal Report, <%=strAppFYr360.length()>0 ? strAppFYr360 : strAppFYr90%></span><br>
					<span style="color: black;font-size: 15px;">Consolidated Appraisal Rating for Year <%=strAppFYr360.length()>0 ? strAppFYr360 : strAppFYr90%></span>
		    </div>
			</div>
		</div>
	</div>
	
	<div class="col-lg-12 col-md-12 col-sm-12" style="margin-top: 30px;padding: 0px;">
		<table  style="border-collapse:collapse;width: 100%;text-align: center;">
		    <thead>
		      <th width="100px">
		        <div class="widget-user-image">
					<%if(docRetriveLocation==null) { %>
						<img style="display:block;" id="profilecontainerimg" src="userImages/avatar_photo.png" width="100px" height="100px" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
					<%} else { %>
						<img style="display:block;" id="profilecontainerimg" src="<%=uF.isFileExist(docSaveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")) ? docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE") : "userImages/avatar_photo.png" %>" width="100%" height="100%" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
					<%} %>
				</div>
		      </th>
		      <th style="padding: 10px;background-color:#257acd;color: #fff;text-align: center;">
		        Employee Details
		      </th>
		      <th style="background-color: #eff0f1;">
		        <table class="table-bordered" style="border-collapse:collapse;width: 100%;text-align: center;height: 100px;">
		          <tbody style="background-color: #eff0f1;">
		            <tr style="border-bottom: 1px solid black;color: #333;">
		              <td style="border-right: 1px solid black;padding: 10px;">Name</td>
		              <td style="border-right: 1px solid black;padding: 10px;">Employee Code</td>
		              <td style="border-right: 1px solid black;padding: 10px;">Designation</td>
		              <td style="border-right: 1px solid black;padding: 10px;">Team</td>
		            </tr>
		            <tr style="font-weight: normal;color: #333;">
		              <td style="border-right: 1px solid black;padding: 10px;">
		              <input type="hidden" name="empName" id="empName" value="<%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%>">
		              <%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></td>
		              <td style="border-right: 1px solid black;padding: 10px;"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></td>
		              <td style="border-right: 1px solid black;padding: 10px;"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%></td>
		              <td style="border-right: 1px solid black;padding: 10px;"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%></td>
		            </tr>
		          </tbody>
		        </table>
		      </th>
		    </thead>
		    <tr align="center">
		    	<td colspan="3">
		    	<div style="display: inline-flex;">
		    	<label style="padding-top: 24px; padding-right: 8px;">Final Rating:</label>
		    	<%-- <% if((appId90 !=null && uF.parseToInt(appId90) >0 && appId360 !=null && uF.parseToInt(appId360) >0) ){
		    		double result360 = uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/20:0;
					double result90 = uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/20:0;
				%>
					<%=uF.getReportStatus( ((result360*40)/100) + ((result90*60)/100) ) %>
				<% } else if(appId360 !=null && uF.parseToInt(appId360) > 0 && appId90 ==null) { 
					double result360 = uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/20:0;
				%>
					<%=uF.getReportStatus(result360) %>
				<% } else if(appId90 !=null && uF.parseToInt(appId90) > 0 && appId360 ==null) { 
					double result90 = uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/20:0;
				%>
					<%=uF.getReportStatus(result90) %>
				<% } %> --%>
				<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
				
					<% if((appId90 !=null && uF.parseToInt(appId90) >0 && appId360 !=null && uF.parseToInt(appId360) >0) ){
			    		double result360 = uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/10:0;
						double result90 = uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/10:0;
						System.out.println("if----result90=="+hmfinalData.get(appId360)+"---result360=="+hmfinalData.get(appId90));
					%>
						<%=uF.getReportStatusForTenStarRating(((result360*40)/100) + ((result90*60)/100) ) %>
					<% } else if(appId360 !=null && uF.parseToInt(appId360) > 0 && appId90 ==null) { 
						double result360 = uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/10:0;
						System.out.println("else-if--1--result360=="+result360);
					%>
						<%=uF.getReportStatusForTenStarRating(result360) %>
					<% } else if(appId90 !=null && uF.parseToInt(appId90) > 0 && appId360 ==null) { 
						double result90 = uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/10:0;
						System.out.println("else-if--2--result90=="+result90);
					%>
						<%=uF.getReportStatusForTenStarRating(result90) %>
					<% } %>
				
				<%}else{ %>
					<% if((appId90 !=null && uF.parseToInt(appId90) >0 && appId360 !=null && uF.parseToInt(appId360) >0) ){
			    		double result360 = uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/20:0;
						double result90 = uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/20:0;
					%>
						<%=uF.getReportStatus( ((result360*40)/100) + ((result90*60)/100) ) %>
					<% } else if(appId360 !=null && uF.parseToInt(appId360) > 0 && appId90 ==null) { 
						double result360 = uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/20:0;
					%>
						<%=uF.getReportStatus(result360) %>
					<% } else if(appId90 !=null && uF.parseToInt(appId90) > 0 && appId360 ==null) { 
						double result90 = uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/20:0;
					%>
						<%=uF.getReportStatus(result90) %>
					<% } %>
				<%} %>
		    	</div>
		    	</td>
		    	
		    </tr>
  		</table>
		
	</div>
		<%if(hmFeatureStatus!=null && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
		<%
			if(appId360 !=null && uF.parseToInt(appId360) >0){
				List appDetails = hmDetails.get(appId360);
		%>
		<div class="col-lg-12 col-md-12 col-sm-12" style="color: black;text-align: center;margin-top: 15px;">
			<label style="font-size: 18px;"><%=appDetails.get(0) %>, <%=appDetails.get(1) %> – 360 degree</label>
		</div>
		
		 <div class="col-lg-12 col-md-12 col-sm-12" style="margin-bottom: 10px;">
		 
			<div class="col-lg-4 col-md-4 col-sm-4" style="padding: 0;text-align: center;"><!-- style="width: 30%;" -->
			
			<div style="margin-top: 45px;background-color: #eff0f1;text-align: center;padding: 5px 0px 11px 0px;">
				<h5 style="font-weight: bold;color: #257acd;">Review Performance Result</h5>
				
			<div class="d-flex" style="display: flex;justify-content: center;">
			<div id="starAllPrimary_<%=appId360 %>" style="width: 0px;height: 25px;">
			
			</div>
			<!-- ===start parvez date: 02-03-2023=== -->
				<div style="padding-left: 10px; margin-top: 3px;font-weight: bold;font-size: 15px;">
				<%-- <%=uF.showData(hmfinalData.get(appId360), "0")%>% --%>
				<%if(hmfinalData.get(appId360+"_ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(hmfinalData.get(appId360+"_ACTUAL_CAL_BASIS"))){ %>
						<%=hmfinalData.get(appId360)!=null ? uF.formatIntoOneDecimal(uF.parseToDouble(hmfinalData.get(appId360))) : "0"%>
					<%}else{ %>
						<%=uF.showData(hmfinalData.get(appId360), "0")%>%
					<%} %>
				</div>
			<!-- ===end parvez date: 02-03-2023=== -->	
			</div>
				<input type="hidden" id="starAllPrimary_<%=appId360 %>" value="<%=uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/20 + "":"0"%>" />
						<script type="text/javascript">
								$('#starAllPrimary_<%=appId360 %>').raty({
										 readOnly: true,
										 start: <%=uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/20 + "":"0"%>, 
										 half: true,
										 targetType: 'number',
										 targetText: 'sd',
										 click: function(score, evt) {
										        $('#starAllPrimary_<%=appId360 %>').val(score);
										 }
								});
					</script>
					<%-- <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
						<script type="text/javascript">
							$('#starAllPrimary_<%=appId360 %>').raty({
								readOnly: true,
								start: <%=uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/10 + "":"0"%>, 
								number: 10,
								half: false,
								targetType: 'number',
								targetText: 'sd',
								click: function(score, evt) {
									$('#starAllPrimary_<%=appId360 %>').val(score);
								}
							});
						</script>
					<%}else{ %>
						<script type="text/javascript">
							$('#starAllPrimary_<%=appId360 %>').raty({
								readOnly: true,
								start: <%=uF.parseToDouble(hmfinalData.get(appId360)) > 0 ? uF.parseToDouble(hmfinalData.get(appId360))/20 + "":"0"%>, 
								half: true,
								targetType: 'number',
								targetText: 'sd',
								click: function(score, evt) {
									$('#starAllPrimary_<%=appId360 %>').val(score);
								}
							});
						</script>
					<%} %> --%>

			</div>
			<%-- <%=uF.getReportStatus(uF.parseToDouble(hmfinalData.get(appId360))/20) %> --%><!-- Created By Dattatray Date : 19-07-21 -->
			
		</div>
		<div class="col-lg-8 col-md-8 col-sm-8" style="padding: 0;" ><!-- style="width: 70%;" -->
			<div id="chartdiv" style="width:75%; height:185px;position: relative;margin-bottom: 40px"></div>
			 <script>
			 <%
		 	 	for (int i=0; i<memberList360.size(); i++) { 
		 	 		Map<String,String> innerCount360 = hmHeaderCount.get(appId360);
	 	 			if(innerCount360 == null) innerCount360 = new HashMap<String,String>();
	 	 				Integer sum=0;
		 	 			for (int i1 = 0; i1 < memberList360.size() ; i1++) {
						    sum += uF.parseToInt(innerCount360.get(memberList360.get(i1)));
						}
		 	 			Map<String,String> color = uF.getAppraisalReportColor(uF.parseToInt(memberList360.get(i)));
		 	 %>
		 	kra360.push({"name": '<%=orientationMemberMp.get(memberList360.get(i)) %><%=uF.parseToInt(memberList360.get(i))==3 ? "":" ("+innerCount360.get(memberList360.get(i))+")" %>',"count": '<%=uF.formatIntoOneDecimal((uF.parseToDouble(innerCount360.get(memberList360.get(i).trim()))/sum)*100)%>',"color":'<%= color.get(memberList360.get(i))%>'});
				
			<% }%>
		 	</script> 
		 	<script>
		 	
		 	
								var chart = AmCharts.makeChart("chartdiv", {
									  "type": "pie",
									  "startDuration": 0,
									   "theme": "light",
									  "addClassNames": true,
									  "labelsEnabled": false,
									  "innerRadius": "40%",
									  "hideCredits":true,
		                              "legend":{
		                            	  	"position":"right",
 		                            	  	"marginTop":25,
				                            "autoMargins":false,
				                            "valueText":"[[value]]%"
				                      },
									  "defs": {
									    "filter": [{
									      "id": "shadow",
									      "width": "200%",
									      "height": "200%",
									      "feOffset": {
									        "result": "offOut",
									        "in": "SourceAlpha",
									        "dx": 0,
									        "dy": 0
									      },
									      "feGaussianBlur": {
									        "result": "blurOut",
									        "in": "offOut",
									        "stdDeviation": 5
									      },
									      "feBlend": {
									        "in": "SourceGraphic",
									        "in2": "blurOut",
									        "mode": "normal"
									      }
									    }]
									  },
									  "dataProvider": kra360,
									  "valueField": "count",
									  "titleField": "name",
									  "colorField": "color",
									  /* "labelColorField":"color", */
									  "export": {
									    "enabled": true
									  }
									});
								</script>
		</div>
		
	</div>
	
	<div style=" width:100%;">
		 <table class="table table-bordered table-striped" cellpadding="0" cellspacing="0" width="100%" style="border-top: 20px solid #257acd;">
		 <tr style="color: #333">
		 	 <th style="padding: 10px 0px !important;">Competency</th><!-- Created By Dattatray Date : 19-07-21 Note : Bucket to Competency-->
		 	 <%
		 		Map<String,String> innerCount360 = hmHeaderCount.get(appId360);
	 			if(innerCount360 == null) innerCount360 = new HashMap<String,String>();
		 		for (int i=0; memberList360 != null && !memberList360.isEmpty() && i < memberList360.size(); i++) { 
		 	 %>
		 	 	<!-- ===start parvez date: 29-03-2023=== -->	
		 	 		<%-- <th style="text-align: center;padding: 10px !important;"><%=orientationMemberMp.get(memberList360.get(i)) %> <%=uF.parseToInt(memberList360.get(i))==3 ? "":" ("+innerCount360.get(memberList360.get(i))+")" %></th> --%>
		 	 		<%if(hmFeatureStatus!=null && ((uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) && uF.parseToInt(memberList360.get(i)) == 2) || !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)))){ %>
		 	 			<th style="text-align: center;padding: 10px !important;"><%=orientationMemberMp.get(memberList360.get(i)) %> <%=uF.parseToInt(memberList360.get(i))==3 ? "":" ("+innerCount360.get(memberList360.get(i))+")" %></th>
		 	 		<%} %>
		 	 	<!-- ===end parvez date: 29-03-2023=== -->	
		 	 <% } %>
		 	 <th style="text-align: center;padding: 10px !important;">Overall Score</th>
		 </tr>
		 <%
				 	List<List<String>> sectionList = hmAppraisalSections.get(appId360);
				 	if(sectionList != null){
		 			for(int i = 0; i<sectionList.size();i++){
		 				List<String> innerList = sectionList.get(i);
		 				Map<String, String> value = outerMp.get(innerList.get(0).trim());
			       		if (value == null) value = new HashMap<String, String>();
		 				
		 %>
		 <tr style="color: #333">
		 	<td style="vertical-align: middle;font-weight: bold;"><%=innerList.get(1) %> </td>
		 	
		 	 <%
		 		double total =0.0f;
				int memCnt=0;	
				String actualBasis =null;
	            for (int j = 0; memberList360 != null && !memberList360.isEmpty() && j < memberList360.size(); j++) {
	            /* ===start parvez date: 29-03-2023=== */	
	            	if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) && uF.parseToInt(memberList360.get(j).trim())!=2){
	            		continue;
	            	}
	            /* ===end parvez date: 29-03-2023=== */
	            	if(uF.parseToInt(memberList360.get(j).trim())!=3 && uF.parseToDouble(value.get(memberList360.get(j).trim()))>0) {
	            		total += uF.parseToDouble(value.get(memberList360.get(j).trim()));
		            	memCnt++;
	            	} 
	         //===start parvez date: 02-03-2023===   	
	            actualBasis = value.get("ACTUAL_CAL_BASIS");	
	         //===end parvez date: 02-03-2023===
	         %>
		 	<td align="center">
				<span>
			<!-- ===start parvez date: 03-02-2023=== -->	
				 <%if(value.get(memberList360.get(j).trim())!=null){ %>
		              <%-- <%=uF.showData(value.get(memberList360.get(j).trim()), "0")%>% --%>
		              <%if(value.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(value.get("ACTUAL_CAL_BASIS"))){ %>
		              	<%=value.get(memberList360.get(j).trim())!=null ? uF.formatIntoOneDecimal(uF.parseToDouble(value.get(memberList360.get(j).trim()))/20) :"0"%>
		              <%}else{ %>
		              	<%=uF.showData(value.get(memberList360.get(j).trim()), "0")%>%
		              <%} %>
		         <% } else { %>
		              <!-- 0% -->
		              <%=value.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(value.get("ACTUAL_CAL_BASIS")) ? "" : "0%" %>
		         <% } %>
		  <!-- ===end parvez date: 03-02-2023=== -->       
				</span>
				<div id="starAllPrimary<%=memberList360.get(j).trim()+"_"+innerList.get(0).trim()%>" style="margin-bottom: 5px;"></div>
					<input type="hidden" id="gradeAllwithrating<%=memberList360.get(j).trim()+"_"+innerList.get(0).trim()%>" value="<%=value.get(memberList360.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList360.get(j).trim())) / 20 + "" : "0"%>" 
						name="gradeAllwithrating<%=innerList.get(0).trim()%>"  />
							<script type="text/javascript">
									$('#starAllPrimary<%=memberList360.get(j).trim()+"_"+innerList.get(0).trim()%>').raty({
										readOnly: true,
										start: <%=value.get(memberList360.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList360.get(j).trim())) / 20 + "" : "0"%>, 
										half: true,
										targetType: 'number',
										click: function(score, evt) {
											$('#gradeAllwithrating<%=memberList360.get(j).trim()+"_"+innerList.get(0).trim()%>').val(score);
										}
									});
							</script>
							
							<%-- <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
								<script type="text/javascript">
									$('#starAllPrimary<%=memberList360.get(j).trim()+"_"+innerList.get(0).trim()%>').raty({
										readOnly: true,
										start: <%=value.get(memberList360.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList360.get(j).trim())) / 10 + "" : "0"%>, 
										number: 10,
                                        half: false,
										targetType: 'number',
										click: function(score, evt) {
											$('#gradeAllwithrating<%=memberList360.get(j).trim()+"_"+innerList.get(0).trim()%>').val(score);
										}
									});
								</script>
							<%}else{ %>
								<script type="text/javascript">
									$('#starAllPrimary<%=memberList360.get(j).trim()+"_"+innerList.get(0).trim()%>').raty({
										readOnly: true,
										start: <%=value.get(memberList360.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList360.get(j).trim())) / 20 + "" : "0"%>, 
										half: true,
										targetType: 'number',
										click: function(score, evt) {
											$('#gradeAllwithrating<%=memberList360.get(j).trim()+"_"+innerList.get(0).trim()%>').val(score);
										}
									});
								</script>
							<%} %> --%>
							<!-- Created by Dattatray Date : 12-July-2021 Note : Checked NA -->
				<span style="color: black;"><%=uF.parseToInt(innerCount360.get(memberList360.get(j))) == 0 ? "NA": uF.getRatingStatus(uF.parseToDouble(value.get(memberList360.get(j).trim())) / 20) %></span>
			<%-- <span style="color: black;"><%=uF.getRatingStatus(uF.parseToDouble(value.get(memberList360.get(j).trim()))/ 20)%></span> --%>
		 	</td>
		 	<% } %>
		 	<td align="center">
				<span>
				<%
				
					double result = 0.0;
					if(memberList360.size() > 0) {
						result = total/memCnt;
				%>
		<!-- ===start parvez date: 02-03-2023=== -->		
				<%-- <%=uF.formatIntoOneDecimal(result) %>% --%>
				<%if(actualBasis !=null && uF.parseToBoolean(actualBasis)){ %>
					<%=uF.formatIntoOneDecimal(result/20) %>
				<%}else{ %>
					<%=uF.formatIntoOneDecimal(result) %>%
				<%} %>
					
				<% } else { %>
					<!-- 0% -->
					<%=actualBasis !=null && uF.parseToBoolean(actualBasis) ? "" : "0%" %>
				<% } %>
		<!-- ===end parvez date: 02-03-2023=== -->		
				</span>
				<div id="starAllPrimary360_<%=i %>" style="margin-bottom: 5px;"></div>
					<input type="hidden" id="gradeAllwithrating360_<%=i %>" value="<%=i %>" />
					<script type="text/javascript">
						$('#starAllPrimary360_<%=i %>').raty({
							readOnly: true,
							start:<%= result>0 ? result/20 :"0"%>, 
							half: true,
							targetType: 'number',
							click: function(score, evt) {
								$('#gradeAllwithrating360_<%=i %>').val(score);
							}
						});
					</script>
							<%-- <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
								<script type="text/javascript">
									$('#starAllPrimary360_<%=i %>').raty({
										readOnly: true,
										start:<%= result>0 ? result/10 :"0"%>, 
										number: 10,
		                                half: false,
										targetType: 'number',
										click: function(score, evt) {
											$('#gradeAllwithrating360_<%=i %>').val(score);
										}
									});
								</script>
							<%}else{ %>
								<script type="text/javascript">
									$('#starAllPrimary360_<%=i %>').raty({
										readOnly: true,
										start:<%= result>0 ? result/20 :"0"%>, 
										half: true,
										targetType: 'number',
										click: function(score, evt) {
											$('#gradeAllwithrating360_<%=i %>').val(score);
										}
									});
								</script>
							<%} %> --%>
				<span style="color: black;"><%=uF.getRatingStatus(result/ 20)%></span>
		 	</td>
		 </tr>
		  	<% } %>
		 <% } %>
		 
		 </table>
		 </div>
		<div style=" width:100%;">
				 <table class="table table-bordered" cellpadding="0" cellspacing="0" width="100%">
					 <thead>
					 <tr >
					 	 <th style="width: 33.3%;padding: 7px !important;border-right: 10px solid;" class="commentDiv">Areas of Strength</th>
					 	 <th style="width: 33.3%;padding: 7px !important;border-right: 10px solid;" class="commentDiv">Areas of Improvement</th>
					 	 <th style="width: 33.3%;padding: 7px !important;" class="commentDiv">Overall Comments</th>
					 </tr>
					 </thead>
					 <tbody>
					 <tr style="color: black">
					 	<%-- <%
						 	Map<String,List<String>> hmInner360 = hmComment.get(appId360);
						 	if(hmInner360 == null) hmInner360 = new HashMap<String,List<String>>();
					 	%> --%>
						<td style="border-right: 10px solid #fff !important;background-color: #eff0f1;"> 
						 	<div style="min-height: 100px;padding-top: 5px;padding-right: 5px;">
						 	<ul >
						 <%-- 	<%
						 		if(hmInner360 !=null && hmInner360.get("areasOfStrength") !=null && hmComment.get(appId360)!=null){
						 		List<String> alareaofstregnth360 = hmInner360.get("areasOfStrength");
						 		
						 		if(alareaofstregnth360 !=null && alareaofstregnth360.size() > 0){
						 			System.out.print("if alareaofstregnth360 size : "+alareaofstregnth360.size());
						 			for(int i=0;i<alareaofstregnth360.size();i++){
						 				
						 	%>
							    <li style="list-style-type: disc;"><%=alareaofstregnth360.get(i) %></li>
								<% } %>
								
							<% } else{ %>
								<li>-</li>
							<% } %>
							<% }else { %>
								<li>-</li>
							<% } %> --%>
								<li>-</li>
							  </ul>
						 	</div>
						 </td>
						 <td style="border-right: 10px solid #fff !important;background-color: #eff0f1;">
						 	<div style="min-height: 100px;padding-top: 5px;padding-right: 5px;">
						 	<ul>
							<%-- <%
							    if(hmInner360 !=null && hmInner360.get("areasOfImprovement") !=null && hmComment.get(appId360)!=null){
						 		List<String> alareaofimprovement360 = hmInner360.get("areasOfImprovement");
						 		if(alareaofimprovement360 !=null && alareaofimprovement360.size() > 0){	
						 			for(int i=0;i<alareaofimprovement360.size();i++){
						 	%>
							    <li style="list-style-type: disc;"><%=alareaofimprovement360.get(i) %></li>
								<% } %>
							<% } else{ %>
								<li>-</li>
							<% } %>
							<% }else { %>
								<li>-</li>
							<% } %>  --%>
								<li>-</li>
							  </ul>
						 	</div>
						 </td>
						 <td style="background-color: #eff0f1;">
						 	<div style="min-height: 100px;padding-top: 5px;padding-right: 5px;">
						 		<ul>
						 		<%-- <%
									if(hmInner360 !=null && hmInner360.get("overallComments") !=null && hmComment.get(appId360)!=null){
							 		List<String> alOverallComment360 = hmInner360.get("overallComments");
							 		if(alOverallComment360 !=null && alOverallComment360.size() > 0){
							 		 for(int i=0;i<alOverallComment360.size();i++){
						 		%>
								    <li style="list-style-type: disc;"><%=alOverallComment360.get(i) %></li>
									<% } %>
								<% } else { %>
									<li>-</li>
							 	<% } %>
							 	<% }else { %>
								<li>-</li>
								<% } %> --%>
								<li>-</li>
							 	</ul>
						 	</div>
						 </td>
					 </tr>
					 </tbody>
				 </table>
		</div>
		<% } %>
		<% } %>
		<%
			if(appId90 !=null && uF.parseToInt(appId90) >0){
				List appDetails = hmDetails.get(appId90);
		%>			
	<div class="col-lg-12 col-md-12 col-sm-12" style="color: black;text-align: center;">
		<label style="font-size: 18px;"><%=appDetails.get(0) %> - <%=appDetails.get(1) %></label>
	</div>
	
	<div class="col-lg-12 col-md-12 col-sm-12" style="margin-bottom: 10px;">
		<div class="col-lg-4 col-md-4 col-sm-4" style="padding: 0;text-align: center;">
			<div style="margin-top: 45px;background-color: #eff0f1;text-align: center;padding: 5px 0px 11px 0px;">
				<h5 style="font-weight: bold;color: #257acd;">Review Performance Result</h5>
				
			<div class="d-flex" style="display: flex;justify-content: center;">
			<div id="starAllPrimaryKRA_<%=appId90 %>" style="width: 0px;height: 25px;">
			
			</div>
			<!-- ===start parvez date: 02-03-2023=== -->
				<div style="padding-left: 10px; margin-top: 3px;font-weight: bold;font-size: 15px;">
					<%-- <%=uF.showData(hmfinalData.get(appId90), "0")%>% --%>
					<%if(hmfinalData.get(appId90+"_ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(hmfinalData.get(appId90+"_ACTUAL_CAL_BASIS"))){ %>
						<%=hmfinalData.get(appId90)!=null ? uF.formatIntoOneDecimal(uF.parseToDouble(hmfinalData.get(appId90))) : "0"%>
					<%}else{ %>
						<%-- <%=uF.showData(hmfinalData.get(appId90), "0")%>% --%>
						<% if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ 
							double finalScore = uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/10 : 0;
							//System.out.println("hmfinalData.get(appId90)=="+hmfinalData.get(appId90));
						%>
							<%if(finalScore > 0 && finalScore < 2){ %>
								<%="1" %>
							<%} else if(finalScore > 1 && finalScore < 4){ %>
								<%="2" %>
							<%} else if(finalScore > 3 && finalScore < 5){ %>
								<%="3L" %>
							<%} else if(finalScore > 4 && finalScore < 6){ %>
								<%="3H" %>
							<%} else if(finalScore > 5 && finalScore < 9){ %>
								<%="4" %>
							<%} else if(finalScore > 8){ %>
								<%="5" %>
							<%} else{ %>
								<%="0" %>
							<%} %>
						<%} else{ %>
							<%=uF.showData(hmfinalData.get(appId90), "0")%>%
						<%} %>
					<%} %>
				</div>
			<!-- ===end parvez date: 02-03-2023=== -->	
			</div>
				<input type="hidden" id="starAllPrimaryKRA_<%=appId90 %>" value="<%=uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/20 + "":"0"%>" />
						<script type="text/javascript">
								$('#starAllPrimaryKRA_<%=appId90 %>').raty({
										 readOnly: true,
										 start: <%=uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/20 + "":"0"%>, 
										 half: true,
										 targetType: 'number',
										 click: function(score, evt) {
										        $('#starAllPrimaryKRA_<%=appId90 %>').val(score);
										 }
								});
						</script>
						<%-- <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
							<script type="text/javascript">
								$('#starAllPrimaryKRA_<%=appId90 %>').raty({
									readOnly: true,
									start: <%=uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/10 + "":"0"%>, 
									number: 10,
					                half: false,
									targetType: 'number',
									click: function(score, evt) {
										$('#starAllPrimaryKRA_<%=appId90 %>').val(score);
									}
								});
							</script>
						<%}else{ %>
							<script type="text/javascript">
								$('#starAllPrimaryKRA_<%=appId90 %>').raty({
										 readOnly: true,
										 start: <%=uF.parseToDouble(hmfinalData.get(appId90)) > 0 ? uF.parseToDouble(hmfinalData.get(appId90))/20 + "":"0"%>, 
										 half: true,
										 targetType: 'number',
										 click: function(score, evt) {
										        $('#starAllPrimaryKRA_<%=appId90 %>').val(score);
										 }
								});
							</script>
						<%} %> --%>

			</div>
			<%-- <%=uF.getReportStatus(uF.parseToDouble(hmfinalData.get(appId90))/20) %> --%> <!-- Created By Dattatray Date : 19-07-21 -->
			
		</div>
		<div class="col-lg-8 col-md-8 col-sm-8" style="padding: 0;">
			<div id="chartdiv1" style="width:75%; height:190px;position: relative;margin-bottom: 40px"></div>
			 <script>
			 <%
				 Map<String,String> hmInnerCount90 = hmHeaderCount.get(appId90);
		 		 if(hmInnerCount90 == null) hmInnerCount90 = new HashMap<String,String>();
		 	 	for (int i=0; i<memberList90.size(); i++) { 
	 	 				Integer sum=0;
		 	 			for (int i1 = 0; i1 < memberList90.size() ; i1++) {
						    sum += uF.parseToInt(hmInnerCount90.get(memberList90.get(i1)));
						}
		 	 			//System.out.println("sum1 : "+sum);
		 	 			//System.out.println(uF.formatIntoOneDecimal((uF.parseToDouble(hmInnerCount90.get(memberList90.get(i).trim()))/sum)*100));
		 	 			Map<String,String> color = uF.getAppraisalReportColor(uF.parseToInt(memberList90.get(i)));
		 	 			//System.out.println("color : "+color.get(memberList90.get(i)));
		 	 %>
		 	kra90.push({"name": '<%=orientationMemberMp.get(memberList90.get(i)) %><%=uF.parseToInt(memberList90.get(i))==3 ? "":" ("+hmInnerCount90.get(memberList90.get(i))+")" %>',"count": '<%=uF.formatIntoOneDecimal((uF.parseToDouble(hmInnerCount90.get(memberList90.get(i).trim()))/sum)*100)%>',"color":'<%=color.get(memberList90.get(i))%>'});
				
			<% }%>
		 	</script> 
		 	<script>
								var chart = AmCharts.makeChart("chartdiv1", {
									  "type": "pie",
									  "startDuration": 0,
									   "theme": "light",
									  "addClassNames": true,
									  "labelsEnabled": false,
									  "innerRadius": "40%",
									  "hideCredits":true,
									  "legend":{
		                            	  	"position":"right",
		                            	  	"marginTop":25,
				                            "autoMargins":false,
				                            "valueText":"[[value]]%"
				                            },
									  "defs": {
									    "filter": [{
									      "id": "shadow",
									      "width": "200%",
									      "height": "200%",
									      "feOffset": {
									        "result": "offOut",
									        "in": "SourceAlpha",
									        "dx": 0,
									        "dy": 0
									      },
									      "feGaussianBlur": {
									        "result": "blurOut",
									        "in": "offOut",
									        "stdDeviation": 5
									      },
									      "feBlend": {
									        "in": "SourceGraphic",
									        "in2": "blurOut",
									        "mode": "normal"
									      }
									    }]
									  },
									  "dataProvider": kra90,
									  "valueField": "count",
									  "titleField": "name",
									  "colorField": "color",
									  "export": {
									    "enabled": true
									  }
									});
								</script>
		</div>
		
	</div>
	<div style=" width:100%;">
		<table class="table table-bordered table-striped" cellpadding="0" cellspacing="0" width="100%" style="border-top: 20px solid #257acd;">
		 <tr style="color: #333">
		 <% if(hmFeatureStatus!=null && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>	 
		 	 <th style="padding: 10px 0px !important;">Competency</th><!-- Created By Dattatray Date : 19-07-21 Note : Bucket to Competency-->
		 <%} %>	  
		 	  <%
		 	 	for (int i=0; i<memberList90.size(); i++) { 
		 	 		//System.out.print("ID : "+memberList90.get(i)+" Name : "+orientationMemberMp.get(memberList90.get(i)));
		 	 	/* ===start parvez date: 29-03-2023=== */	
		 	 		if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) && uF.parseToInt(memberList90.get(i))!=2){
		 	 			continue;
		 	 		}
		 	 	/* ===end parvez date: 29-03-2023=== */
		 	 %>
		 	 	<th style="text-align: center;padding: 10px !important;"><%=orientationMemberMp.get(memberList90.get(i)) %><%=uF.parseToInt(memberList90.get(i))==3 ? "":" ("+hmInnerCount90.get(memberList90.get(i))+")" %></th>
		 	 <% } %>
		 	 <%if(hmFeatureStatus!=null && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
		 	 	<th style="text-align: center;padding: 10px !important;">Overall Score</th>
		 	 <% } %>
		 </tr>
<!-- ===start parvez date: 29-03-2023=== -->
		<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
			<%
				List<List<String>> sectionKRAList = hmAppraisalSections.get(appId90);
			 	//System.out.println("sectionKRAList : "+sectionKRAList);
			 	if(sectionKRAList !=null){
			 		Map<String, String> value = new HashMap<String, String>();
			 	
			 	double dblUsertypeAvg = 0;
				for(int i = 0; i<sectionKRAList.size();i++){
					List<String> innerKRAList = sectionKRAList.get(i);
					Map<String, String> value1 = outerMp.get(innerKRAList.get(0).trim());
		       		if (value1 == null) value1 = new HashMap<String, String>();
		       		
		       		for (int k = 0; memberList90 != null && !memberList90.isEmpty() && k < memberList90.size(); k++) {
		       			
		       			double avgScore = uF.parseToDouble(value1.get(memberList90.get(k).trim()));
		       			double weightage = uF.parseToDouble(hmSectionWeightage.get(innerKRAList.get(0).trim()));
		       			double dblLevelAvg = (avgScore * weightage) / 100; 
		       			
		       			dblUsertypeAvg = uF.parseToDouble(value.get(memberList90.get(k).trim()))+dblLevelAvg;
		       			value.put(memberList90.get(k).trim(), dblUsertypeAvg+"");
		       		}
		       		
			 %>
			 <% } %>
		 <tr style="color: #333">
		 	
		 	 <%
		 		double total =0.0f;
		 		int memCnt=0;
		 		String actualBasis = null;
	            for (int j = 0; memberList90 != null && !memberList90.isEmpty() && j < memberList90.size(); j++) {
	            /* ===start parvez date: 29-03-2023=== */	
		 	 		if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) && uF.parseToInt(memberList90.get(j))!=2){
		 	 			continue;
		 	 		}
		 	 		
		 	 	/* ===end parvez date: 29-03-2023=== */
	            	if(uF.parseToInt(memberList90.get(j).trim())!=3 && uF.parseToDouble(value.get(memberList90.get(j).trim()))>0) {
	            		//total += uF.parseToDouble(value.get(memberList90.get(j).trim()));
	            		total += (uF.parseToDouble(value.get(memberList90.get(j).trim()))*uF.parseToInt(hmKRA90W.get(memberList90.get(j).trim())))/100;
		            	memCnt++;
		            //===start parvez date: 02-03-2023===   	
			            actualBasis = value.get("ACTUAL_CAL_BASIS");	
			        //===end parvez date: 02-03-2023===
	            	} 
	           //System.out.println("AAR.jsp/759---member_id=="+uF.parseToInt(memberList90.get(j).trim())+"---vd=="+uF.parseToDouble(value.get(memberList90.get(j).trim()))+"---kd=="+uF.parseToInt(hmKRA90W.get(memberList90.get(j).trim())));	
	         %>
		 	<td align="center">
				<span>
			<!-- ===start parvez date: 02-03-2023=== -->	
				<%if(value.get(memberList90.get(j).trim())!=null){ %>
		              <%-- <%=uF.showData(value.get(memberList90.get(j).trim()), "0")%>% --%>
		              <%if(value.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(value.get("ACTUAL_CAL_BASIS"))){ %>
		              		<%=value.get(memberList90.get(j).trim())!=null ? uF.formatIntoOneDecimal(uF.parseToDouble(value.get(memberList90.get(j).trim()))/20) :"0"%>
		              <% } else { %>
		             	<%-- <%=uF.showData(value.get(memberList90.get(j).trim()), "0")%>% --%>
		             	
		             	<% if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ 
							double ratingScore = value.get(memberList90.get(j).trim())!=null ? uF.parseToDouble(value.get(memberList90.get(j).trim()))/10 : 0;
							//System.out.println("hmfinalData.get(appId90)=="+hmfinalData.get(appId90));
						%>
							<%if(ratingScore > 0 && ratingScore < 2){ %>
								<%="1" %>
							<%} else if(ratingScore > 1 && ratingScore < 4){ %>
								<%="2" %>
							<%} else if(ratingScore > 3 && ratingScore < 5){ %>
								<%="3L" %>
							<%} else if(ratingScore > 4 && ratingScore < 6){ %>
								<%="3H" %>
							<%} else if(ratingScore > 5 && ratingScore < 9){ %>
								<%="4" %>
							<%} else if(ratingScore > 8){ %>
								<%="5" %>
							<%} else{ %>
								<%="0" %>
							<%} %>
						<%} else{ %>
							<%=uF.showData(value.get(memberList90.get(j).trim()), "0")%>%
						<%} %>
		             	
		              <%} %>
		         <% } else { %>
		              <!-- 0% -->
		              <%=value.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(value.get("ACTUAL_CAL_BASIS")) ? "" : "0%" %>
		         <% } %>
		    <!-- ===end parvez date: 02-03-2023=== -->     
				</span>
				
				<div id="starAllPrimaryKRA<%=memberList90.get(j).trim()%>" style="margin-bottom: 5px;"></div>
					<input type="hidden" id="gradeAllwithratingKRA<%=memberList90.get(j).trim()%>" value="<%=value.get(memberList90.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList90.get(j).trim())) / 20 + "" : "0"%>" 
						name="gradeAllwithratingKRA"  />
							<script type="text/javascript">
									$('#starAllPrimaryKRA<%=memberList90.get(j).trim()%>').raty({
										readOnly: true,
										start: <%=value.get(memberList90.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList90.get(j).trim())) / 20 + "" : "0"%>, 
										half: true,
										targetType: 'number',
										click: function(score, evt) {
											$('#gradeAllwithratingKRA<%=memberList90.get(j).trim()%>').val(score);
										}
									});
							</script>
							
				<span style="color: black;"><%=uF.parseToInt(hmInnerCount90.get(memberList90.get(j))) == 0 ? "NA": uF.getRatingStatus(uF.parseToDouble(value.get(memberList90.get(j).trim())) / 20) %></span>
		 	</td>
		 	<% } %>
		 	
		<!-- ===start parvez date: 01-04-2023=== -->	
		 <%if(hmFeatureStatus!=null && !uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>	
		 	<td align="center">
				
					<span>
				<%
					double result = 0.0;
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HR_GHR_APPROVAL_FOR_FINAL_RATING_AND_COMMENT))){
					result = uF.parseToDouble(hmfinalData.get(appId90));
				%> 
				<%if(actualBasis!=null && uF.parseToBoolean(actualBasis)){ %>
						<%=uF.formatIntoOneDecimal(result/20)%>
					<% } else { %>
						<%=uF.formatIntoOneDecimal(result)%>%
					<% } %>
				<%} else if(memCnt > 0) {
						result = total;	
				%>
					<%-- <%=uF.formatIntoOneDecimal(result)%>% --%>
					<%if(actualBasis!=null && uF.parseToBoolean(actualBasis)){ %>
						<%=uF.formatIntoOneDecimal(result/20)%>
					<% } else { %>
						<%=uF.formatIntoOneDecimal(result)%>%
					<% } %>
				<% } else { %>
					<!-- 0% -->
					<%=actualBasis!=null && uF.parseToBoolean(actualBasis) ? "" : "0%"%>
				<% } %>
					</span>
				
					<div id="starAllPrimaryKRA" style="margin-bottom: 5px;"></div>
						<input type="hidden" id="gradeAllwithratingKRA" />
								<script type="text/javascript">
										$('#starAllPrimaryKRA').raty({
												 readOnly: true,
												 start: <%= result>0 ? result/20 :"0"%>, 
												 half: true,
												 targetType: 'number',
												 click: function(score, evt) {
												        $('#gradeAllwithratingKRA').val(score);
												 }
										});
								</script>
								
					<span style="color: black;"><%=uF.getRatingStatus(result/ 20)%></span>
		 	</td>
		 	<%} %>
		 <!-- ===end parvez date: 01-04-2023=== -->		
		 </tr>
		 	
		 	<% } %>
		<%} else{ %>
		 <%
			List<List<String>> sectionKRAList = hmAppraisalSections.get(appId90);
		 	//System.out.println("sectionKRAList : "+sectionKRAList);
		 	if(sectionKRAList !=null){
			for(int i = 0; i<sectionKRAList.size();i++){
				List<String> innerKRAList = sectionKRAList.get(i);
				Map<String, String> value = outerMp.get(innerKRAList.get(0).trim());
	       		if (value == null) value = new HashMap<String, String>();
		 	
		 %>
		 <tr style="color: #333">
		 	<td style="vertical-align: middle;"><%=innerKRAList.get(1) %></td>
		 	 <%
		 		double total =0.0f;
		 		int memCnt=0;
		 		String actualBasis = null;
	            for (int j = 0; memberList90 != null && !memberList90.isEmpty() && j < memberList90.size(); j++) {
	            /* ===start parvez date: 29-03-2023=== */	
		 	 		if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) && uF.parseToInt(memberList90.get(j))!=2){
		 	 			continue;
		 	 		}
		 	 	/* ===end parvez date: 29-03-2023=== */
	            	if(uF.parseToInt(memberList90.get(j).trim())!=3 && uF.parseToDouble(value.get(memberList90.get(j).trim()))>0) {
	            		//total += uF.parseToDouble(value.get(memberList90.get(j).trim()));
	            		total += (uF.parseToDouble(value.get(memberList90.get(j).trim()))*uF.parseToInt(hmKRA90W.get(memberList90.get(j).trim())))/100;
		            	memCnt++;
		            //===start parvez date: 02-03-2023===   	
			            actualBasis = value.get("ACTUAL_CAL_BASIS");	
			        //===end parvez date: 02-03-2023===
	            	} 
	           // System.out.println("AAR.jsp/759---member_id=="+uF.parseToInt(memberList90.get(j).trim())+"---vd=="+uF.parseToDouble(value.get(memberList90.get(j).trim()))+"---kd=="+uF.parseToInt(hmKRA90W.get(memberList90.get(j).trim())));	
	         %>
		 	<td align="center">
				<span>
			<!-- ===start parvez date: 02-03-2023=== -->	
				<%if(value.get(memberList90.get(j).trim())!=null){ %>
		              <%-- <%=uF.showData(value.get(memberList90.get(j).trim()), "0")%>% --%>
		              <%if(value.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(value.get("ACTUAL_CAL_BASIS"))){ %>
		              		<%=value.get(memberList90.get(j).trim())!=null ? uF.formatIntoOneDecimal(uF.parseToDouble(value.get(memberList90.get(j).trim()))/20) :"0"%>
		              <% } else { %>
		             	<%=uF.showData(value.get(memberList90.get(j).trim()), "0")%>%
		              <%} %>
		         <% } else { %>
		              <!-- 0% -->
		              <%=value.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(value.get("ACTUAL_CAL_BASIS")) ? "" : "0%" %>
		         <% } %>
		    <!-- ===end parvez date: 02-03-2023=== -->     
				</span>
				
				<div id="starAllPrimaryKRA<%=memberList90.get(j).trim()+"_"+innerKRAList.get(0).trim()%>" style="margin-bottom: 5px;"></div>
					<input type="hidden" id="gradeAllwithratingKRA<%=memberList90.get(j).trim()+"_"+innerKRAList.get(0).trim()%>" value="<%=value.get(memberList90.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList90.get(j).trim())) / 20 + "" : "0"%>" 
						name="gradeAllwithratingKRA<%=innerKRAList.get(0).trim()%>"  />
							<script type="text/javascript">
									$('#starAllPrimaryKRA<%=memberList90.get(j).trim()+"_"+innerKRAList.get(0).trim()%>').raty({
											 readOnly: true,
											 start: <%=value.get(memberList90.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList90.get(j).trim())) / 20 + "" : "0"%>, 
											 half: true,
											 targetType: 'number',
											 click: function(score, evt) {
											        $('#gradeAllwithratingKRA<%=memberList90.get(j).trim()+"_"+innerKRAList.get(0).trim()%>').val(score);
											 }
									});
							</script>
							<%-- <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
								<script type="text/javascript">
									$('#starAllPrimaryKRA<%=memberList90.get(j).trim()+"_"+innerKRAList.get(0).trim()%>').raty({
										readOnly: true,
										start: <%=value.get(memberList90.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList90.get(j).trim())) / 10 + "" : "0"%>, 
										number: 10,
						                half: false,
										targetType: 'number',
										click: function(score, evt) {
											$('#gradeAllwithratingKRA<%=memberList90.get(j).trim()+"_"+innerKRAList.get(0).trim()%>').val(score);
										}
									});
								</script>
							<%}else{ %>
								<script type="text/javascript">
									$('#starAllPrimaryKRA<%=memberList90.get(j).trim()+"_"+innerKRAList.get(0).trim()%>').raty({
										readOnly: true,
										start: <%=value.get(memberList90.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList90.get(j).trim())) / 20 + "" : "0"%>, 
										half: true,
										targetType: 'number',
										click: function(score, evt) {
											$('#gradeAllwithratingKRA<%=memberList90.get(j).trim()+"_"+innerKRAList.get(0).trim()%>').val(score);
										}
									});
								</script>
							<%} %> --%>
				<%-- <span style="color: black;"><%=uF.getRatingStatus(uF.parseToDouble(value.get(memberList90.get(j).trim())) / 20) %></span> --%>
				<!-- Created by Dattatray Date : 12-July-2021 Note : Checked NA -->
				<span style="color: black;"><%=uF.parseToInt(hmInnerCount90.get(memberList90.get(j))) == 0 ? "NA": uF.getRatingStatus(uF.parseToDouble(value.get(memberList90.get(j).trim())) / 20) %></span>
		 	</td>
		 	<% } %>
		 	<td align="center">
			<!-- ===start parvez date: 02-03-2023=== -->	
					<span>
				<%
					double result = 0.0;
					if(memCnt > 0) {
						result = total;
				%>
					<%-- <%=uF.formatIntoOneDecimal(result)%>% --%>
					<%if(actualBasis!=null && uF.parseToBoolean(actualBasis)){ %>
						<%=uF.formatIntoOneDecimal(result/20)%>
					<% } else { %>
						<%=uF.formatIntoOneDecimal(result)%>%
					<% } %>
				<% } else { %>
					<!-- 0% -->
					<%=actualBasis!=null && uF.parseToBoolean(actualBasis) ? "" : "0%"%>
				<% } %>
					</span>
			<!-- ===end parvez date: 02-03-2023=== -->		
					<div id="starAllPrimaryKRA_<%=i %>" style="margin-bottom: 5px;"></div>
						<input type="hidden" id="gradeAllwithratingKRA_<%=i %>" value="<%=i %>" />
								<script type="text/javascript">
										$('#starAllPrimaryKRA_<%=i %>').raty({
												 readOnly: true,
												 start: <%= result>0 ? result/20 :"0"%>, 
												 half: true,
												 targetType: 'number',
												 click: function(score, evt) {
												        $('#gradeAllwithratingKRA_<%=i %>').val(score);
												 }
										});
								</script>
								<%-- <%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
									<script type="text/javascript">
										$('#starAllPrimaryKRA_<%=i %>').raty({
											readOnly: true,
											start: <%= result>0 ? result/10 :"0"%>, 
											number: 10,
									        half: false,
											targetType: 'number',
											click: function(score, evt) {
												$('#gradeAllwithratingKRA_<%=i %>').val(score);
											}
										});
									</script>
								<%} else{ %>
									<script type="text/javascript">
										$('#starAllPrimaryKRA_<%=i %>').raty({
											readOnly: true,
											start: <%= result>0 ? result/20 :"0"%>, 
											half: true,
											targetType: 'number',
											click: function(score, evt) {
												$('#gradeAllwithratingKRA_<%=i %>').val(score);
											}
										});
									</script>
								<%} %> --%>
					<span style="color: black;"><%=uF.getRatingStatus(result/ 20)%></span>
		 	</td>
		 	
		 </tr>
		 	<% } %>
		 	<% } %>
		 <% } %>
		 <!-- ===en parvez date: 29-03-2023=== -->
		 </table>
		 </div>
		 <div style=" width:100%;">
				 <table class="table table-bordered" cellpadding="0" cellspacing="0" width="100%">
					 <thead>
					 <tr >
					 	 <th style="width: 33.3%;padding: 7px !important;border-right: 10px solid;" class="commentDiv">Areas of Strength</th>
					 	 <th style="width: 33.3%;padding: 7px !important;border-right: 10px solid;" class="commentDiv">Areas of Improvement</th>
					 	 <th style="width: 33.3%;padding: 7px !important;" class="commentDiv">Overall Comments</th>
					 </tr>
					 </thead>
					 <tbody>
					 <tr style="color: black;">
						<%
						 	Map<String,List<String>> hmInner90 = hmComment.get(appId90);
						 	if(hmInner90 == null) hmInner90 = new HashMap<String,List<String>>();
						 	
						 // Created by Dattatray Date : 19-07-21
						 	Map<String,List<String>> hmInner90New = hmCommentKRA.get(appId90);
						 	if(hmInner90New == null) hmInner90New = new HashMap<String,List<String>>();
					 		
					 	%>
						<td style="border-right: 10px solid #fff !important;background-color: #eff0f1;"> 
						 	<div style="min-height: 100px;padding-top: 5px;padding-right: 5px;">
						 	<ul >
						 	<%
						 		if(hmInner90 !=null && hmInner90.get("areasOfStrength") !=null && hmComment.get(appId90) !=null){
						 		List<String> alareaofstregnth90 = hmInner90.get("areasOfStrength");
						 		if(alareaofstregnth90 !=null && alareaofstregnth90.size() > 0){
						 			for(int i=0;i<alareaofstregnth90.size();i++){
						 				
						 	%>
							    <li style="list-style-type: disc;"><%=alareaofstregnth90.get(i) %></li>
								<% } %>
							<% } else{ %>
								<li>-</li>
							<% } %>
							<% }else { %>
								<li>-</li>
							<% } %>
							  </ul>
						 	</div>
						 </td>
						 <td style="border-right: 10px solid #fff !important;background-color: #eff0f1;">
						 	<div style="min-height: 100px;padding-top: 5px;padding-right: 5px;">
						 	<ul>
							<%
								    if(hmInner90 !=null && hmInner90.get("areasOfImprovement") !=null && hmComment.get(appId90) !=null){
							 		List<String> alareaofimprovement90 = hmInner90.get("areasOfImprovement");
							 		if(alareaofimprovement90 !=null && alareaofimprovement90.size() >0){
							 			for(int i=0;i<alareaofimprovement90.size();i++){
							 				
						 	%>
							    <li style="list-style-type: disc;"><%=alareaofimprovement90.get(i) %></li>
								<% } %>
							<% } else{ %>
								<li>-</li>
							<% } %>
							<% }else { %>
								<li>-</li>
							<% } %>
							  </ul>
						 	</div>
						 </td>
						 <td style="background-color: #eff0f1;">
						 	<div style="min-height: 100px;padding-top: 5px;padding-right: 5px;">
						 	<ul>
							<%
							// Created by Dattatray Date : 19-07-21
							if(hmInner90New !=null && hmInner90New.get("overallComments") !=null && hmCommentKRA.get(appId90) !=null){
					 			List<String> alOverallComment90 = hmInner90New.get("overallComments");
						 		if(alOverallComment90 !=null && alOverallComment90.size() > 0){
						 			for(int i=0;i<alOverallComment90.size();i++){
						 				
						 	%>
							    <li style="list-style-type: disc;"><%=alOverallComment90.get(i) %></li>
								<% } %>
							<% } else{ %>
								<li>-</li>
							<% } %>
							<% }else { %>
								<li>-</li>
							<% } %>
							  </ul>
						 	</div>
						 </td>
					 </tr>
					 </tbody>
				 </table>
			 </div>
				<% } %>
				
				<div style="border: 0.5px solid gray;">
				</div>
				<div class="col-lg-12 col-md-12 col-sm-12" style="margin: 20px 0px;padding: 0px 0px 10px 0px;">
			<!-- ===start parvez date: 03-04-2023=== -->	
 					<%-- <strong style="color:black;padding: 4px;">Scale:</strong>
 					<div style="padding-top: 5px;">
						<div class="report-custom-legend not-meet-expectations">
							<div class="legend-info">0-1 Does not meet Expectations</div>
						</div>
						<div class="report-custom-legend needs-improvement">
							<div class="legend-info">1-2 Needs Improvement</div>
						</div>
						<div class="report-custom-legend meets-expectations">
							<div class="legend-info">2-3 Meets Expectations</div>
						</div>
						<div class="report-custom-legend exceeds-expectations">
							<div class="legend-info">3-4 Exceeds Expectations</div>
						</div>
						<div class="report-custom-legend outstanding">
							<div class="legend-info">4-5 Outstanding</div>
						</div>
					</div> --%>
					
					<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW))){ %>
						<strong style="color:black;padding: 4px;">Final Rating Scale:</strong>
	 					<div style="padding-top: 5px;">
							<div class="report-custom-legend outstanding">
								<div class="legend-info"><p style="display:inline">5 Always exceeds expectations across all specified criteria for the level above their current level, demonstrates role model behaviour</div>
							</div><br/>
							<div class="report-custom-legend exceeds-expectations">
								<div class="legend-info">4 Meets expectations and most of the times exceeds expectation across the majority of the specified criteria for the level above their current level</div>
							</div><br/>
							<div class="report-custom-legend meet-expectations-h">
								<div class="legend-info">3H Meets expectations for a subset of the level above</div>
							</div><br/>
							<div class="report-custom-legend meet-expectations">
								<div class="legend-info">3L Meets expectations for their current level</div>
							</div><br/>
							<div class="report-custom-legend needs-improvement1">
								<div class="legend-info">2 Somewhat meets or below expectations for their current level</div>
							</div><br/>
							<div class="report-custom-legend not-meet-expectations">
								<div class="legend-info">1 Below expectations for their current level</div>
							</div>
							
						</div>
					<%}else{ %>
						<strong style="color:black;padding: 4px;">Scale:</strong>
	 					<div style="padding-top: 5px;">
							<div class="report-custom-legend not-meet-expectations">
								<div class="legend-info">0-1 Does not meet Expectations</div>
							</div>
							<div class="report-custom-legend needs-improvement">
								<div class="legend-info">1-2 Needs Improvement</div>
							</div>
							<div class="report-custom-legend meets-expectations">
								<div class="legend-info">2-3 Meets Expectations</div>
							</div>
							<div class="report-custom-legend exceeds-expectations">
								<div class="legend-info">3-4 Exceeds Expectations</div>
							</div>
							<div class="report-custom-legend outstanding">
								<div class="legend-info">4-5 Outstanding</div>
							</div>
						</div>
					<%} %>
			<!-- ===start parvez date: 03-04-2023=== -->		
				</div>
		
	</div>
	<script type="text/javascript">
	
	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	
	function getAppraisalReporyPDF() {
		setTimeout(() => {
			var hrdata = this.document.getElementById("divAppraisalReporyData");
			var empName = this.document.getElementById("empName").value;
			var opt = {
	    		margin:0.2,
	        	filename: empName+' Appraisal Report.pdf',
	        	image: { type: 'jpeg', quality: 0.98 },
	        	html2canvas: { scale: 2},
	        	jsPDF: { unit: 'in', format: 'A4', orientation: 'portrait'}
	       };
	       html2pdf().from(hrdata).set(opt).save(); 
		}, 7000); 	    
	}
	</script>
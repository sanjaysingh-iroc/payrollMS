<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript">
		$(function() {
			$('#lt').dataTable({
				"order": [],
				"columnDefs": [ {
				      "targets"  : 'no-sort',
				      "orderable": false
				    }],
				'dom': 'lBfrtip',
		        'buttons': [
					'copy', 'csv', 'excel', 'pdf', 'print'
		        ]
		  	});
			$("#f_wlocation").multiselect().multiselectfilter();
			$("#strMinEducation").multiselect().multiselectfilter();
			$("#strSkills").multiselect().multiselectfilter();
			$("#strExperience").multiselect().multiselectfilter(); 
			$("#checkStatus_reportfilter").multiselect().multiselectfilter();
		}); 			
	
		
		function openCandidateProfilePopup(CandID,recruitId) {     
	    	var id=document.getElementById("panelDiv");
	    	if(id){
	    		id.parentNode.removeChild(id);
	    	}
	    	var dialogEdit = '#modal-body1';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$('.modal-title1').html('Candidate Information');
			$("#modalInfo1").show();
			if($(window).width() >= 900){
				$(".modal-dialog1").width(900);
			}
			$.ajax({
				//url : "ApplyLeavePopUp.action", 
				url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	     }
		
	/*  function openCandidateProfilePopup(CandID,recruitId) {
		 removeLoadingDiv('the_div');
		 
			var id=document.getElementById("panelDiv");
			if(id){
				id.parentNode.removeChild(id);
					}
			var dialogEdit = '#CandiProfilePopup';
			dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog({
				autoOpen : false,
				bgiframe : true,
				resizable : false, 
				height : 700,  
				width : '95%',
				modal : true,
				title : 'Candidate Information',
				open : function() {
					var xhr = $.ajax({
						//url : "ApplyLeavePopUp.action", 
						url :"CandidateMyProfilePopup.action?CandID="+CandID+"&recruitId="+recruitId,
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
					xhr = null;
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});
		
			$(dialogEdit).dialog('open');

		 } */
	
	
		function getCandiApplicationsDetailsPopup(candidateId, candiName, recruitId) { 
	    	var id=document.getElementById("panelDiv");
	    	if(id){
	    		id.parentNode.removeChild(id);
	    	}
	    	var dialogEdit = '#modal-body1';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$('.modal-title1').html(''+candiName +'\'s Tracker');
			$("#modalInfo1").show();
			if($(window).width() >= 900){
				$(".modal-dialog1").width(900);
			}
			
			$.ajax({
				//url : "ApplyLeavePopUp.action", 
				url :"getCandidateApplicationsDetails.action?candidateId="+candidateId+"&type=IFrame&recruitId="+recruitId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	     }
		 
		 
	/* function getCandiApplicationsDetailsPopup(candidateId, candiName, recruitId) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#CandiApplicationDetails';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 600,
			width : 800, 
			modal : true,
			title : ''+candiName +'\'s Tracker',
			open : function() {
				var xhr = $.ajax({
					url : "getCandidateApplicationsDetails.action?candidateId=" + candidateId + "&type=IFrame&recruitId=" + recruitId,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	} */
	
	
	function addCandidateApplicationsDetails(candidateId, recruitId) {
		if(confirm("Are you sure, you want to add this candidate in this job?")) {
			$.ajax({
				url  :'AddCandidateApplicationsDetails.action?candidateId='+candidateId+'&type=IFrame&fromPage=AC&jobCode='+recruitId+'&recruitId='+recruitId,
				cache:true/* ,
				success : function(result) {
					$("#subSubDivResult").html(result);
				} */
			});
			
			$.ajax({
				url: 'Applications.action?recruitId='+recruitId,
				cache: true,
				success: function(result){
					$("#subSubDivResult").html(result);
		   		}
			});
		}
  	}
	
	$("#submitFilter").click(function(){
		var form_data = $("form[name='frm_candidate_report_filter']").serialize();
		$("#showallcand").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			url :'ShowAllCandidate.action',
			data: form_data,
			type: 'POST',
			success : function(data) {
				$("#showallcand").html(data);
			} 
		});
	});
</script>
		
		<%
			//String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
			UtilityFunctions uF = new UtilityFunctions();
			Map<String, String> hmEducationDetails = (Map) request.getAttribute("hmEducationDetails");
			Map<String, String> hmExperienceDetails = (Map) request.getAttribute("hmExperienceDetails");
			Map<String, String> hmSkillDetails = (Map) request.getAttribute("hmSkillDetails");
			List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
			String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
		%>
	
<div id="printDiv" class="leftbox reportWidth">
		<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px;">Filter</h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
				<s:form name="frm_candidate_report_filter" action="ShowAllCandidate" theme="simple">
					<s:hidden name="recruitId"></s:hidden>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter" aria-hidden="true"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" listKey="orgId"
					                listValue="orgName" list="organisationList" key="" onchange="$('#submitFilter').click();"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_wlocation" id="f_wlocation" listKey="wLocationId"
					               listValue="wLocationName" list="workList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Education</p>
									<s:select  name="strMinEducation" id="strMinEducation" list="eduList" theme="simple" listKey="eduId" listValue="eduName" 
					 				 multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Skill</p>
									<s:select name="strSkills" id="strSkills" list="skillsList" theme="simple" listKey="skillsId" listValue="skillsName" 
					     			 multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Experience</p>
									<s:select theme="simple" name="strExperience" id="strExperience"
							      list="#{'1':'0 to 1 Year','2':'1 to 2 Years','3':'2 to 5 Years','4':'5 to 10 Years','5':'10+ Years'}" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status</p>
									<s:select theme="simple" name="checkStatus_reportfilter" id="checkStatus_reportfilter" list="#{'1':'Approved', '0':'Pending','-1':'Rejected' }"
									cssStyle="width:100px" multiple="true"/>
								</div>
							</div>
						</div>		
						<div class="row row_without_margin margintop10">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar visiblehide"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Current CTC</p>
									<s:textfield name="currCTC" id="currCTC" cssStyle="color: #f6931f; font-weight: bold;" readonly="true" onkeypress="return isNumberKey(event)"/><br/>
									<script>
									
									 $(function() {
										 $( "#currCTCSlider" ).slider({
										 range: true,
										 min: 0,
										 max: 10000000,
										 step : 50000,
										 values: [<%=uF.parseToDouble((String) request
											.getAttribute("minCurrCTC"))%>, <%=uF.parseToDouble((String) request
											.getAttribute("maxCurrCTC"))%>],
										 slide: function( event, ui ) {
										 $( "#currCTC" ).val(ui.values[ 0 ] + " - " + ui.values[ 1 ] );
										 }
										 });
										 $( "#currCTC" ).val($("#currCTCSlider").slider("values", 0 ) +
										 " - " + $( "#currCTCSlider").slider("values", 1 ) );
										 });
									</script>		
									<div id="currCTCSlider" style="margin-top: 7px;"></div>
									<div id="currCTCSliderMinMax">0 <span style="float:right;">1 Cr.</span></div>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Expected CTC</p>
									<s:textfield name="expectedCTC" id="expectedCTC" cssStyle="color: #f6931f; font-weight: bold;" readonly="true" onkeypress="return isNumberKey(event)"/><br/>
									<script>
									$(function() {
										 $( "#expectedCTCSlider" ).slider({
										 range: true,
										 min: 0,
										 max: 10000000,
										 step : 50000,
										 values: [<%=uF.parseToDouble((String) request
											.getAttribute("minExpectedCTC"))%>, <%=uF.parseToDouble((String) request
											.getAttribute("maxExpectedCTC"))%>],
										 slide: function( event, ui ) {
										 $( "#expectedCTC" ).val(ui.values[ 0 ] + " - " + ui.values[ 1 ] );
										 }
										 });
										 $( "#expectedCTC" ).val($("#expectedCTCSlider").slider("values", 0 ) +
										 " - " + $( "#expectedCTCSlider").slider("values", 1 ) );
										 });
									</script>		
									<div id="expectedCTCSlider" style="margin-top: 7px;"></div>
									<div id="expectedCTCSliderMinMax">0 <span style="float:right;">1 Cr.</span></div>	
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Notice Period</p>
									<s:textfield name="noticePeriod" id="noticePeriod" cssStyle="width: 144px; color: #f6931f; font-weight: bold;" readonly="true" onkeypress="return isNumberKey(event)"/><br/>
									<script>
									$(function() {
										 $( "#noticePeriodSlider" ).slider({
										 range: true,
										 min: 0,
										 max: 365,
										 step : 1,
										 values: [<%=uF.parseToInt((String) request
											.getAttribute("minNoticePeriod"))%>, <%=uF.parseToInt((String) request
											.getAttribute("maxNoticePeriod"))%>],
										 slide: function( event, ui ) {
										 $( "#noticePeriod" ).val(ui.values[ 0 ] + " - " + ui.values[ 1 ] );
										 }
										 });
										 $( "#noticePeriod" ).val($("#noticePeriodSlider").slider("values", 0 ) +
										 " - " + $( "#noticePeriodSlider").slider("values", 1 ) );
										 });
									</script>		
									<div id="noticePeriodSlider" style="margin-top: 7px;"></div>
									<div id="noticePeriodSliderMinMax">0 <span style="float:right;">365</span></div>
								</div>
							</div>
						</div>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar visiblehide"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select name="strMonth" cssStyle="margin-right: 9px; width: 110px;" headerKey="" headerValue="Select Month" listKey="monthId" listValue="monthName" list="monthList" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Year</p>
									<s:select name="strYear" cssStyle="width: 100px;" headerKey="" headerValue="Select Year" listKey="yearsID" listValue="yearsName" list="yearList" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" value="Submit" class="btn btn-primary" id="submitFilter">
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5 pull-right">
									<p style="padding-left: 5px;">&nbsp;</p>
									<span style="font-weight: bolder; font-family: digital; color: green; font-size: 26px;"> <%=reportList != null ? reportList.size() : "0"%></span>Candidates
								</div>
							</div>
						</div>	
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>
		<table id="lt" class="table table-bordered">
		<thead>
			<tr>
				<th style="text-align: left;">Candidate Name</th>
				<!-- <th style="text-align: left;">Age</th>
				<th style="text-align: left;">Existing Location</th> -->
				<th style="text-align: left;">Education</th>
				<th style="text-align: left;">Skills</th>
				<th style="text-align: left;">Experience</th>
				<th style="text-align: left;">Current CTC</th>
				<th style="text-align: left;">Expected CTC</th>
				<th style="text-align: left;">Notice Period</th>
				<th style="text-align: left;">Last Applied/Updated</th>
				<th style="text-align: left;">Last Job Name</th>
				
				<th style="text-align: left;">Profile</th>
				<th style="text-align: left; width: 7%;">Action</th>
			</tr>
		</thead>

		<tbody>

			<%
				for (int i = 0; i < reportList.size(); i++) {
					List<String> innerList = (List<String>) reportList.get(i);
			%>
			<tr>
				<td>
				<%
					if (docRetriveLocation == null) {
				%>
					<img height="20" width="20" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + innerList.get(3)%>" />
				<%
					} else {
				%>
                 	<img height="20" width="20" border="0" class="lazy img-circle" style="float:left;margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation + IConstants.I_CANDIDATE + "/"
							+ IConstants.I_IMAGE + "/" + innerList.get(2) + "/"
							+ IConstants.I_22x22 + "/" + innerList.get(3)%>" />
                <%
                	}
                %> 
				<%=innerList.get(4)%>
				<%
					if (uF.parseToBoolean(innerList.get(17))) {
				%>
					<img style="width: 14px;" src="images1/icons/hd_tick_20x20.png" title="Available">
				<%
					} else {
				%>
					<img style="width: 12px;" src="images1/icons/hd_cross_16x16.png" title="Not Available">
				<%
					}
				%>
				</td>
				<%-- <td><%=innerList.get(10)%></td>
				<td><%=innerList.get(11)%></td> --%>
				<td><%=uF.showData((String) hmEducationDetails
						.get((String) innerList.get(2)), "N/A")%></td>
				<td><%=uF.showData(
						(String) hmSkillDetails.get((String) innerList.get(2)),
						"N/A")%></td>
				<td><%=uF.showData((String) hmExperienceDetails
						.get((String) innerList.get(2)), "N/A")%></td>
				<td><%=innerList.get(14)%></td>
				<td><%=innerList.get(15)%></td>
				<td><%=innerList.get(16)%></td>
				
				<td><%=innerList.get(13)%></td>
				<td><%=innerList.get(12)%></td>
				
				<td><%=innerList.get(7)%></td>
				<td><%=innerList.get(9)%></td>
			</tr>
			<%
				}
			%>
		</tbody>

	</table>

</div>


<g:compress>	
<script type="text/javascript">
var config = {
  '.chosen-select'           : {},
  '.chosen-select-deselect'  : {allow_single_deselect:true},
  '.chosen-select-no-single' : {disable_search_threshold:10},
  '.chosen-select-no-results': {no_results_text:'Oops, nothing found!'},
  '.chosen-select-width'     : {width:"95%"}
}
for (var selector in config) {
  $(selector).chosen(config[selector]);
}
</script>	
</g:compress>
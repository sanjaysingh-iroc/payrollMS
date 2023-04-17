

<!DOCTYPE html>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>

<%
	String strPage = (String) request.getAttribute("PAGE");
	String strTitle = (String) request.getAttribute("TITLE");
	
	if (strPage == null) {
		strPage = "Login.jsp";
	}
	
	if (strTitle == null) {
		strTitle = "Workrig | Human Capital Management"; 
	}
	
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    
    if (CF == null){
		CF = new CommonFunctions();
		CF.setRequest(request);
	}
	String strLogo = (String) session.getAttribute("ORG_LOGO");
	//System.out.println("strLogo ===> " + strLogo);
	if (strLogo == null) {
		strLogo = CF.getOrgLogo(request, CF); 
	}
	String strLogoSmall = (String) session.getAttribute("ORG_LOGO_SMALL");
	
	if (strLogoSmall == null) {
		strLogoSmall = CF.getOrgLogoSmall(request, CF); 
	} 
	
	String strUITheme = CF.getStrUI_Theme();
	
    %>
<html>
<div id="divResult">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1"> 
         
        <title><%=strTitle%></title>
        <link rel="icon" href="images1/icons/icons/w_green.png">
        <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
        <% if(strUITheme != null && strUITheme.equals("2")) { %>
			<link rel="stylesheet" href="dist/css/AdminLTENew_two.min.css">
	        <link rel="stylesheet" href="dist/css/skins/_all-skinsNew_two.min.css">
	         <link rel="stylesheet" href="bootstrap/css/bootstrapNew_two.css">
	         <link rel="stylesheet" href="dist/css/skins/close-popupNew_two.css">
	         
		<% } else if(strUITheme != null && strUITheme.equals("1")) { %>
			<link rel="stylesheet" href="dist/css/AdminLTENew.min.css">
	        <link rel="stylesheet" href="dist/css/skins/_all-skinsNew.min.css">
	         <link rel="stylesheet" href="bootstrap/css/bootstrapNew.css">
	         <link rel="stylesheet" href="dist/css/skins/close-popupNew.css">
	         
		<% } else { %>
	        <link rel="stylesheet" href="dist/css/AdminLTE.min.css">
	        <link rel="stylesheet" href="dist/css/skins/_all-skins.min.css">
	         <link rel="stylesheet" href="bootstrap/css/bootstrap.css">
	        <link rel="stylesheet" href="dist/css/skins/close-popup.css"> 
	        
        <% } %>
        
        <link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
		<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
        <link href="js_bootstrap/timepicker/bootstrap-timepicker.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
        <link rel="stylesheet" type="text/css" href="js/datatables_new/buttons.dataTables.min.css" />
        <link rel="stylesheet" type="text/css" href="js/datatables_new/dataTables.bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="js/datatables_new/responsive.bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="css/jquery.poplight.css"/>
		<link rel="stylesheet" type="text/css" href="js_bootstrap/bootstrap-datetimepicker/bootstrap-datetimepicker.min.css"/>
		<link rel="stylesheet" href="css/font-awesome/font-awesome.min.css">
        <link rel="stylesheet" href="css/font-awesome/font-awesome-animation.min.css">
        
        <% if(strUITheme != null && strUITheme.equals("2")) { %>
       		<link rel="stylesheet" href="css/new_customNew_two.css">
       		<link rel="stylesheet" href="css/login-form-elementsNew_two.css">
       		<link rel="stylesheet" href="bootstrap/css/New_two.css">
       	<% } else if(strUITheme != null && strUITheme.equals("1")) { %>
    		<link rel="stylesheet" href="css/new_customNew.css">
    		<link rel="stylesheet" href="css/login-form-elementsNew.css">
    		<link rel="stylesheet" href="bootstrap/css/New.css">
    	<% } else { %>
       		<link rel="stylesheet" href="css/new_custom.css">
       		<link rel="stylesheet" href="css/login-form-elements.css">
       	<% } %>
       	
    </head>
    <body class="hold-transition skin-blue sidebar-mini sidebar-collapse">


<%@ page import="java.util.*,com.konnect.jpms.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script src="js/jquery.expander.js"></script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"> </script>
<script type="text/javascript" src="scripts/jquery.shorten.1.0.js"></script>
<link rel="preconnect" href="https://fonts.gstatic.com">
<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300&display=swap" rel="stylesheet">

<style>
	
	.jobbox {
		box-shadow: 2px 2px 5px #999898;
		margin-right: 5%;
		font-family: 'Roboto', sans-serif;
		font-size:16px;
		padding: 3%;
		float: left;
		width: 100%;
	}
	
	.jobbox:hover {
		box-shadow: 2px 2px 5px #5c5a5a;
		margin-right: 5%;
		font-family: 'Roboto', sans-serif;
		font-size:16px;
		padding: 3%;
		float: left;
	}
	
	.jobboxlist {
		box-shadow: 2px 2px 5px #999898;
		font-family: 'Roboto', sans-serif;
		margin-bottom: 4%;
		font-size:16px;
	}
	
	.jobboxlist:hover {
		box-shadow: 2px 2px 5px #5c5a5a;
		font-family: 'Roboto', sans-serif;
		margin-bottom: 4%;
		font-size:16px;
	}
	
	/* ===start parvez on 03-08-2021=== */
	#textlabel{
		white-space:pre-line;
	}
	/* ===end parvez on 03-08-2021=== */
	
	.joblink{
    display:none;
}

.skills{
  
  width: 50px; 
  overflow: hidden;
  text-overflow: ellipsis; 
  border: 1px solid #000000;
}
</style>

<script>

$(function(){
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	
	
	
});


$(document).ready(function() {
  $('div.expandDiv').expander({
    slicePoint: 20, //It is the number of characters at which the contents will be sliced into two parts.
    widow: 2,
    expandSpeed: 0, // It is the time in second to show and hide the content.
    userCollapseText: 'Read Less (-)' // Specify your desired word default is Less.
  });
  $('div.expandDiv').expander();
  
/*   $(".skills").shorten({
		"showChars" : 50,
		"moreText" : "See More",
		"lessText" : "Less"
	}); */
});


function addCandidateShortFormPopup(recruitID, desigName, jobCode, refEmpId) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Apply for the position of '+desigName); //+' ('+jobCode+')'
	$.ajax({
		url : 'AddCandidateInOneStepNew.action?recruitId='+recruitID+'&fromPage=JO&refEmpId='+refEmpId,
		cache : true,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
	

function addCandidateShortFormPopupWithOutJob(refEmpId) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('CV Dropbox');
	 $.ajax({
		url : 'AddCandidateInOneStepNew.action?applyType=withoutjob&fromPage=JO&refEmpId='+refEmpId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
 

function loadMore(proPage, minLimit) {
	document.frm_JobOpportunity.proPage.value = proPage;
	document.frm_JobOpportunity.minLimit.value = minLimit;
	document.frm_JobOpportunity.submit();
}
	
</script>

<%
		String strUserType = (String) session.getAttribute("USERTYPE");
		//String strTitle = (String) request.getAttribute(IConstants.TITLE);
		
		UtilityFunctions uF = new UtilityFunctions();
		List<String> recruitmentIDList = (List<String>) request.getAttribute("recruitmentIDList");
		Map<String, List<String>> hmJobReport = (Map<String, List<String>>) request.getAttribute("hmJobReport");
		Map<String, List<String>> hmSingleJobReport = (Map<String, List<String>>) request.getAttribute("hmSingleJobReport");
		
		String proCount = (String)request.getAttribute("proCount");
		String sbData = (String) request.getAttribute("sbData");
		String strSearchJob = (String) request.getAttribute("strSearchJob");
		
		String strRecruitId = (String) request.getAttribute("strRecruitId");
		String refEmpId = (String) request.getAttribute("refEmpId");
			
%>

	
<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
	<% if(uF.parseToInt(strRecruitId)>0) { %>
		<li><i></i><a href="JobOpportunities.action" style="color: #3c8dbc;"> Home</a></li>
	<% } %>
	<div style="float: left; margin-left: 10px; width: 98%; margin-bottom: 15px; padding: 5px; border-bottom: 1px solid lightgray;">
	<!-- <div style="float:left; margin-left: 10px;"><a href="javascript:void(0);" onclick="window.location='JobOpportunities.action'" style="font-weight: normal;">Reset to default</a></div> -->
		<% if(uF.parseToInt(strRecruitId) == 0) { %>
			<s:form name="frm_JobOpportunity" id="frm_JobOpportunity" action="JobOpportunities" theme="simple">
				<s:hidden name="proPage" id="proPage" />
				<s:hidden name="minLimit" id="minLimit" />
				<div class="col-lg-4 col-md-4 col-sm-12 no-padding">
					<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
					<!-- <input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');"> -->
					<input type="submit" name="btnSubmit" value="Search" class="btn btn-primary" />
					<input type="submit" name="btnReset" value="Reset" class="btn btn-warning" />
					
                
				</div>
				<div class="col-lg-5 col-md-5 col-sm-12 no-padding">
					Category:
                    <s:select theme="simple" name="strCategory" id="strCategory" cssClass="validateRequired" headerKey="0" onchange="submitForm();" list="#{'0':'All','1':'Technical','2':'Functional'}"/>
				</div>
				<div style="line-height: 22px; float: right; font-size: 12px; margin-right: 45px;">
		       		<a href="AddCandidateByCadiNew.action" target="_blank" ><input type="button" name="applyFresherJob" id="applyFresherJob" class="btn btn-primary" value="Job Application"/></a> <!-- onclick="window.location='AddCandidateByCadi.action'"  -->
		       		<input type="button" name="applyWithoutJob" id="applyWithoutJob" class="btn btn-primary" onclick="addCandidateShortFormPopupWithOutJob('<%=uF.parseToInt(refEmpId) %>')" value="CV Dropbox"/>
		       	</div>
		       
		    </s:form>
		<% } %>
		
		<script>
		$(function(){
	    	   $("#strSearchJob" ).autocomplete({
					source: [ <%=uF.showData(sbData,"") %> ]
				});
	       });
		</script>
	</div>	

	<%-- <% System.out.println("IConstants.MESSAGE ===>> " + (String)session.getAttribute(IConstants.MESSAGE)); %> --%>
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
	<% //session.setAttribute(IConstants.MESSAGE, ""); %>
	<% if(uF.parseToInt(strRecruitId) > 0) { %>
		<div class="col-lg-8 col-md-8 col-sm-8">
		<%
			List<String> alinner = (List<String>) hmSingleJobReport.get(strRecruitId);

			%>
			<div class="col-lg-12 col-md-12 col-sm-12 jobboxlist">
				<div style="float: left; margin-bottom: 7px; min-height: 250px; margin-top: 2%;width: 100%;"><!--  margin-left: 15px; -->
					<div style="float: left; width: 100%;">
						<div style="float: left; width: 100%; font-size: 18px;"><strong><%=alinner.get(8)%></strong></div> <%-- &nbsp;(<%=alinner.get(1)%>) --%> <%-- <%=alinner.get(4)%> --%>
						<!-- ===start parvez on 04-08-2021=== -->
							<div style="float: left; width: 100%;" id="textlabel"> <div class="expandDiv">Job Description:<br/><span style="color: gray;"><%=alinner.get(9)%></span></div></div>
							<!-- ===end parvez on 04-08-2021=== -->
						<div style="float: left; width: 100%; "><span style="float:left;">Technology:&nbsp;<span style="color: gray;"><%=alinner.get(15)%></span></span></div><!-- Created By Dattatray Date:24-08-21 -->
						<div style="float: left; width: 100%;">Essential Skills:&nbsp;<span style="color: gray;"><%=alinner.get(10)%></span></div>
						<div style="float: left; width: 100%;">
							<span style="float:left;">Experience:&nbsp;<span style="color: gray;"><%=uF.showData(alinner.get(11), "0")%>&nbsp;yrs - <%=alinner.get(12)%>&nbsp;yrs</span></span>
			                <%-- <span style="float:left;">Maximum Experience:&nbsp;<span style="color: gray;"><%=alinner.get(12)%>&nbsp;yrs</span></span> --%>
		                </div>
		                <div style="float: left; width: 100%;">
							<div style="float: left; width: 100%;">Location:&nbsp;<span style="color: gray;"><%=alinner.get(13)%></span></div>
			                <div style="float: left; width: 100%;">Organization:&nbsp;<span style="color: gray;"><%=alinner.get(14)%></span></div>
			                <div style="float: left; width: 100%; text-align: right; margin-top: 15px;">
			                	<input type="button" name="apply" id="apply" class="btn btn-primary" onclick="addCandidateShortFormPopup('<%=alinner.get(0) %>','<%=alinner.get(4) %>','<%=alinner.get(1) %>', '<%=uF.parseToInt(refEmpId) %>')" value="Apply Now"/>
			                </div>
		                </div>
					</div>
				</div>		
			</div>
		</div>
		
		
		<div class="col-lg-4 col-md-4 col-sm-4">
			<%
			String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+ "0123456789" + "abcdefghijklmnopqrstuvxyz";
			int n=25;
			for (int i = 0; recruitmentIDList != null && i < recruitmentIDList.size(); i++) {
				alinner = (List<String>) hmJobReport.get(recruitmentIDList.get(i));
				StringBuilder sb = new StringBuilder(n); 
		        for (int a=0; a<n; a++) {
		            int index = (int)(AlphaNumericString.length() * Math.random()); 
		            sb.append(AlphaNumericString.charAt(index)); 
		        } 
			%>
				<!-- <div style="float: left; width: 96%; margin-left: 25px;min-height: 65px; border-bottom: 1px lightgray solid; margin-bottom: 10px; padding: 5px;"> -->
				<div class="col-lg-12 col-md-12 col-sm-12 jobboxlist">
					<div style="float: left; margin-bottom: 7px; margin-left: 15px;margin-top: 2%;">
						<div style="float: left; width: 100%;">
							<%-- <div style="float: left; width: 100%;"><a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>"><strong><%=alinner.get(8)%></strong></a></div> &nbsp;(<%=alinner.get(1)%>) <%=alinner.get(4)%>
							<!-- ===start parvez on 04-08-2021=== -->
							<div style="float: left; width: 100%;" id="textlabel"> <div class="expandDiv">Job Description:<br/><span style="color: gray;"><%=alinner.get(9)%></span></div></div> --%>
							<!-- ===end parvez on 04-08-2021=== -->
							<%-- <div style="float: left; width: 100%;">Essential Skills:&nbsp;<span class="users-list-name" style="color: gray;"><%=alinner.get(10)%></span></div> --%>
							
							<!-- Start By Dattatray Date:24-08-21 -->
							<div style="float: left; width: 100%; ">
							<!-- Created by Dattatray date:26-08-21 Note: style:float: right;width:280px; added and title added and class="users-list-name" added-->
									<span style="float:left;"><b>Technology:</b></span>&nbsp;<span style="color: gray;float: left;width:55%;" title="<%=alinner.get(15)%>" class="users-list-name"><%=alinner.get(15)%></span>
				                </div>
								<div style="float: left; width: 100%; ">
								<!-- Created by Dattatray date:26-08-21 Note: style:float: right;width:280px; added and title added and class="users-list-name" added-->
									<span style="float:left;"><b>Position:</b></span>&nbsp;<span style="color: gray;float: left;width:65%;" class="users-list-name"><%-- <%=alinner.get(8)%> --%><a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>" title="<%=alinner.get(8)%>"><%=alinner.get(8)%></a></span>
				                </div>
				                
							<div style="float: left;width: 100%;">
									<span style="float: left;"><b>Key Skills:</b></span>&nbsp;
									<span class="users-list-name" id="skills<%=alinner.get(0) %>" style="color: gray;width: 50%;float: left;"> <%=alinner.get(10)%></span>
									<%
										if(alinner.get(10) !=null && !alinner.get(10).isEmpty() && alinner.get(10).length()>15){
									%>
										<span style="float: left;"><a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>">more</a></span>
									<%} %>
								</div>
							<!-- End By Dattatray Date:24-08-21 -->
							
							<!-- Created By Dattatray Date:24-08-21 Note: unusble code commited -->
							<%-- <div style="float: left; width: 100%; ">
								<span style="float:left;">Experience:&nbsp;<span style="color: gray;"><%=uF.showData(alinner.get(11), "0")%>&nbsp;yrs - <%=alinner.get(12)%>&nbsp;yrs</span></span>
				                <span style="float:left;">Maximum Experience:&nbsp;<span style="color: gray;"><%=alinner.get(12)%>&nbsp;yrs</span></span>
			                </div> --%>
			                <div style="float: left; width: 100%; ">
								<span style="float:left;"><b>Location:&nbsp;</b><span style="color: gray;"><%=alinner.get(13)%></span></span>
			                </div>
			                <!-- Created By Dattatray Date:24-08-21 Note: unusble code commited -->
			                <%-- <div style="float: left; width: 100%; ">
				                <span style="float:left;">Organization:&nbsp;<span style="color: gray;"><%=alinner.get(14)%></span></span>
				                <span style="float:right;">
				                	<input type="button" name="apply" id="apply" class="btn btn-primary" onclick="addCandidateShortFormPopup('<%=alinner.get(0) %>','<%=alinner.get(4) %>','<%=alinner.get(1) %>')" value="Apply Now"/>
				                </span>
			                </div> --%>
						</div>
					</div>		
				</div>
			<% } %>
		</div>
	<% } %>
	
	<% if(uF.parseToInt(strRecruitId) == 0) { %>
	
		
		<div class="attendance">
			<%
			String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+ "0123456789" + "abcdefghijklmnopqrstuvxyz";
			int n=25;
				for (int i = 0; recruitmentIDList != null && i < recruitmentIDList.size(); i++) {
					List<String> alinner = (List<String>) hmJobReport.get(recruitmentIDList.get(i));

					StringBuilder sb = new StringBuilder(n); 
			        for (int a=0; a<n; a++) {
			            int index = (int)(AlphaNumericString.length() * Math.random()); 
			            sb.append(AlphaNumericString.charAt(index)); 
			        } 
			         
			%>
			<script type="text/javascript">
				$("#jobDesc<%=alinner.get(0) %>").shorten({
					"showChars" : 30,
					"moreText" : "See More",
					"lessText" : "Less"
				});
		
				$("#skills<%=alinner.get(0) %>").shorten({
					"showChars" : 30,
					"moreText" : "See More",
					"lessText" : "Less"
				});
			</script>
			<!-- Start Dattatray Date:21-08-21 -->
				<%-- <% if(i==0 || (i%2)==0) { %> --%>
					<!-- <div class="col-lg-12 col-md-12 col-sm-12" style="margin-bottom: 2%;" > -->
				<%-- <% } %> --%>
				<div class="col-lg-3 col-md-3 col-sm-12" style="padding-left: 10px; padding-right: 10px; padding-bottom: 15px;">
					<div class="jobbox">
						<!-- <div style="float: left; margin-bottom: 7px; margin-left: 15px;margin-top: 2%;"> -->
							<div style="float: left; width: 100%;">
								<%-- <% if(uF.parseToInt(strRecruitId) == 0) { %>
									<div style="float: left; width: 100%;"><a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>"><strong><%=alinner.get(8)%></strong></a></div> &nbsp;(<%=alinner.get(1)%>) <%=alinner.get(4)%>
								<% } else { %>
									<div style="float: left; width: 100%;"><strong><%=alinner.get(8)%></strong></div> &nbsp;(<%=alinner.get(1)%>) <%=alinner.get(4)%>
								<% } %> --%>
								<div style="float: left; width: 100%; ">
								<!-- Created by Dattatray date:26-08-21 Note: style:float: right;width:200px; added and title added and class="users-list-name" added-->
									<span style="float:left;"><b>Technology:</b></span>&nbsp;<span style="color: gray; width:52%; float: left;" class="users-list-name" title="<%=alinner.get(15)%>"><%=alinner.get(15)%></span>
				                </div>
								<div style="float: left; width: 100%; ">
								<!-- Created by Dattatray date:26-08-21 Note: style:float: right;width:220px; added and title added and class="users-list-name" added-->
									<span style="float:left;"><b>Position:</b></span>&nbsp;<span style="color: gray; width:65%; float: left;" class="users-list-name"><%-- <%=alinner.get(8)%> --%><a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>" title="<%=alinner.get(8)%>"><%=alinner.get(8)%></a></span>
				                </div>
				                <%-- <a class="joblink" target="_blank" href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>"><%=alinner.get(8)%></a> --%>
								<!-- Start By Dattatray Date:24-08-21 -->
								<div style="float: left;width: 100%;">
									<span style="float: left;"><b>Key Skills:</b></span>&nbsp;
									<span class="users-list-name" id="skills<%=alinner.get(0) %>" style="color: gray; width:42%; float: left;"> <%=alinner.get(10)%></span>
									<%
										if(alinner.get(10) !=null && !alinner.get(10).isEmpty() && alinner.get(10).length()>15){
									%>
										<span style="float: left;"><a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alinner.get(0) %>"> more</a></span>
									<%} %>
								</div>
								<!-- End By Dattatray Date:24-08-21 -->
								<!-- Created By Dattatray Date:24-08-21 Note: unusble code commited -->
								<%-- <div style="float: left; width: 100%; ">
									<span style="float:left;">Experience:&nbsp;<span style="color: gray;"><%=uF.showData(alinner.get(11), "0")%>&nbsp;yrs - <%=alinner.get(12)%>&nbsp;yrs</span></span>
									<span style="float:left;margin-left: 32px;">Maximum Experience:&nbsp;<span style="color: gray;"><%=alinner.get(12)%>&nbsp;yrs</span></span>
				                </div> --%>
				                <div style="float: left; width: 100%; ">
									<span style="float:left;"><b>Location:</b>&nbsp;<span style="color: gray;"><%=alinner.get(13)%></span></span>
				                </div>
				                <div style="float: left; width: 100%; ">
					               <%--  <span style="float:left;"> Organization:&nbsp;<span style="color: gray;"><%=alinner.get(14)%></span></span> --%>
					                <span style="float:right;">
					                	<input type="button" name="apply" id="apply" class="btn btn-primary" onclick="addCandidateShortFormPopup('<%=alinner.get(0) %>','<%=alinner.get(4) %>','<%=alinner.get(1) %>')" value="Apply Now"/>
					                </span>
				                </div>
							</div>
						<!-- </div> -->
					</div>
				</div>
				<%-- <% if(i==1 || (i%2)==1) { %> --%>
					<!-- </div> -->
				<%-- <% } %> --%>
				<!-- End Dattatray -->
			<% } %>
			
			<% if(recruitmentIDList==null || recruitmentIDList.size()==0) { %>
				<div class="nodata msg" style="float: left;">Currently we don't have any open positions.</div>
			<% } %>
		</div>
	
		<%-- <%  if(recruitmentIDList!=null && recruitmentIDList.size()>0) { %>
			<div style="text-align: center; float: left; width: 100%; font-size: 18px;">
				<% int intproCnt = uF.parseToInt(proCount);
					int pageCnt = 0;
					int minLimit = 0;
					for(int i=1; i<=intproCnt; i++) { 
						minLimit = pageCnt * 10;
						pageCnt++;
				%>
				<% if(i ==1) {
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
					if(uF.parseToInt(strPgCnt) > 1) {
						 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
						 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
					}
					if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
				%>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
						<%="< Prev" %></a>
					<% } else { %>
						<b><%="< Prev" %></b>
					<% } %>
					</span>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					
					<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
						<b>...</b>
					<% } %>
				
				<% } %>
				
				<% if(i > 1 && i < intproCnt) { %>
				<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
				<% } %>
				<% } %>
				
				<% if(i == intproCnt && intproCnt > 1) {
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
					 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
					 if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
					%>
					<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
						<b>...</b>
					<% } %>
				
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
					<% } else { %>
						<b><%="Next >" %></b>
					<% } %>
					</span>
				<% } %>
				<%} %>
			</div>
		<%} %> --%>
		
	<%} %>
	
</div>
</div>

</section>
</div>
</section>

<div class="modal" id="modalInfo" role="dialog">
<!-- Created by Dattatray Date:24-08-21 Note:class width100  -->
    <div class="modal-dialog width100"><!-- Created by Dattatray Date:19-08-21 Note:height -->
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
             <!-- Created by Dattatray Date:19-08-21 Note:height -->
            <div class="modal-body height90" style="overflow-y:auto; padding-left: 25px;"><!-- Created by Dattatray Date:24-08-21 Note:class height90  -->
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

           	<%-- <footer class="main-footer" style="margin-left: 0px;">
            	<div class="pull-right hidden-xs">
                    <b>Version</b> 3.0.0
                </div>
                <strong>Copyright &copy; 2017-2018 <a href="http://www.workrig.com">Workrig</a>.</strong> All rights reserved.
            </footer> --%>
        </div>
        
        	
        	<script type="text/javascript" src="js_bootstrap/jQuery/jQuery-3.1.1.min.js"></script>
	        <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
	        <script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
	        <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
	        <script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
			<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
	        <script type="text/javascript" src="scripts/waypoints.js"></script>
	        <script type="text/javascript" src="js/datatables_new/jquery.dataTables.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/pdfmake.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/buttons.print.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/jszip.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/dataTables.bootstrap.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/dataTables.buttons.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/buttons.flash.min.js"></script>
	        <script type="text/javascript" src="js/datatables_new/vfs_fonts.js"></script>
	        <script type="text/javascript" src="js/datatables_new/buttons.html5.min.js"></script>
			<script type="text/javascript" src="js/datatables_new/dataTables.responsive.min.js"></script>
			<script type="text/javascript" src="js/datatables_new/buttons.bootstrap.min.js"></script>
			<script type="text/javascript" src="js/datatables_new/responsive.bootstrap.min.js"></script>
			<script type="text/javascript" src="scripts/jquery.lazyload.js"></script>
	        <script type="text/javascript" src="dist/js/app1.min.js"></script>
			<script type="text/javascript" src="scripts/_rating/js/jquery.raty.min.js"> </script>
	        <script type="text/javascript" src="scripts/organisational/jquery.jOrgChart.js"></script>
	        <script type="text/javascript" src="scripts/customAjax.js" ></script>
	        <script type="text/javascript" src="js/jquery.rateyo.min.js"></script>
	        <script type="text/javascript" src="js/moment.js"></script>
	        <script type="text/javascript" src="js_bootstrap/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js"></script>
	        <script type="text/javascript" src="js_bootstrap/Jcrop/jquery.Jcrop.min.js"></script>
	        <script type="text/javascript">
	        $(".selectedL").parent().addClass("active");
	        $(".selectedL").next('.treeview-menu').css('display','block');
	        $(".selectedL").next('.treeview-menu').addClass("menu-open");
	        

	        $(document).on('mouseover', 'input, select, textarea',function(){
	        	$(this).next('.hint').css("visibility", "visible");
	        });
	        $(document).on('mouseout', 'input, select, textarea',function(){
	        	$(this).next('.hint').css("visibility", "hidden");
	        });

	        $(document).on('click', '.products-list a',function(){
	        	$('a').removeClass("activelink");
	        	$(this).addClass("activelink");
	        });

	        $(window).on('load',function(){
	        	$("input[type='number']").prop('step','any');
	        	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	        });

	        $('body').on('onkeypress','input[type="number"]',function(evt){
	        	var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	               return false;
	            }
	            return true;
	        });


	        var nowTemp = new Date();
	        var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

	        jQuery.browser = {};
	        (function () {
	            jQuery.browser.msie = false;
	            jQuery.browser.version = 0;
	            if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
	                jQuery.browser.msie = true;
	                jQuery.browser.version = RegExp.$1;
	            }
	        })();

	        $(function(){
	        	
	        	loadLazyImages();
	        	
	        	$("body").on("click",".fc-button",function(){
	        		if($(this).hasClass("fc-month-button")){
	        			setTimeout(function(){ $(".fc-time").hide();}, 500);
		        	}else{
		        		setTimeout(function(){ $(".fc-time").show();}, 500);
		        	}
	        	});
	        	
	        	$("input[type='number']").keydown(function(e) {
	        		var n = (window.Event) ? e.which : e.keyCode;
	        		if(n==38 || n==40) return false;
	        	});
	        	
	        	$("input[type='number']").attr("onmousewheel", "return false;");

	        	if($(".fc-month-button").hasClass("fc-state-active")){
	        		$(".fc-time").hide();
	        	}
	        	
	        	$("body").on("click",".external-event input[type='checkbox']",function(){
	        		$(".fc-time").hide();
	        	});
	        	
	        	$("body").on('click','#closeButton',function(){
	        		$(".modal-dialog").removeAttr('style');
	        		$(".modal-bodyCP").height(400);
	        		$("#modalInfoCP").hide();
	        	});
	        	
	        	$("body").on('click','.close',function(){
	        		$(".modal-dialog").removeAttr('style');
	        		$(".modal-bodyCP").height(400);
	        		$("#modalInfoCP").hide();
	        	});
	        	
	        	var active = $(".active-workrig-user").html();
	        	$(".workrig-users-button>.btn-sm[for='"+active+"']").addClass("active");
	        	
	        	if($('body').hasClass('sidebar-collapse')){
	        		$(".treeview").removeClass("arrow_box");
	        		$(".treeview.active").addClass("arrow_box");
	        	}
	        	
	        	$('body').on('click','.sidebar-toggle',function(){
	        		if($('body').hasClass('sidebar-collapse')){
	        			$(".treeview").removeClass("arrow_box");	
	        			
	        		}else{
	        			$(".treeview").removeClass("arrow_box");
	        			$(".treeview.active").addClass("arrow_box");
	        			//$(".treeview.active").addClass("arrow_box");
	        		}
	        	});
				
	        	$('body').on('click','.sidebar-toggle,#open-dropdown',function(){
	        		loadLazyImages();
	        	});
	        		        	
	        	$(document).on("click",".nav-tabs li",function(e){
	        		//console.log(e);
	        		$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
	        			options.async = false;
	        		});
	        	});
	        	$(document).find(".box-tools").parent().attr("data-widget","collapse-full");
	        
	        	$( document ).on('ajaxComplete',function() {
	        		$(document).find(".box-tools").parent().attr("data-widget","collapse-full");
	        		setTimeout(function(){ $(".fc-time").hide();}, 500);
	        		$("input[type='number']").keydown(function(e) {
		        		var n = (window.Event) ? e.which : e.keyCode;
		        		if(n==38 || n==40) return false;
		        	});
		        	
		        	$("input[type='number']").attr("onmousewheel", "return false;");
		        	
		        	//lazy load
		        	loadLazyImages();
	        	});
	        		        	
	        	$('body').on('click','.box-header', function(e) { 
	        	  var e_target = e.target;
	        	  if (e_target !== this){
	        		  if($(e_target).hasClass('box-title') || $(e_target).parent().hasClass('box-title') || $(e_target).attr('data-widget') === "collapse" || 
	        				  $(e_target).hasClass('fa-minus') || $(e_target).hasClass('fa-plus')){
	        			  
	        		  }else{
	        			  e.stopPropagation();
	        		  }
	        	  }
	        	});
	        	$('body').on('keydown','.readonly',function(e) {
        		    e.preventDefault();
        		});
	        	
	        	$('body').on('keydown','.no-press-enter',function(e) {
	        	    if(e.which == 13) {
	        	       return false;
	        	    }
	        	});
	        });
	        
	        function loadLazyImages(){
	        	$("img.lazy").each(function(i,element) {
	        	    if($(element).attr("src") !== $(element).attr("data-original")){
	        	    	$(element).lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	        	    }
	        	});
	        }
	        
	        function fadeForm(form_id){
	        	if($('#'+form_id).find('.there').length === 0){
	        		$('#'+form_id).prepend('<div class="there"><div id="ajaxLoadImage"></div></div>');
	        	}
	        } 

	        function unfadeForm(form_id){
	        	$("#"+form_id).find('.there').remove();
	        }

	        function isNumberKey(evt){
	            var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
	               return false;
	            }
	            return true;
	         } 


	        function isOnlyNumberKey(evt) {
	            var charCode = (evt.which) ? evt.which : event.keyCode;
	            if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	         		return true;
	            }
	            return false;
	         }

	        function clearField(elementId){
	        	document.getElementById(elementId).value = '';
	        }
	        function submitForm(){
	        	var category = document.getElementById("strCategory").value;
	        	//alert("service ===>> " + service);
	        	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	        	$.ajax({
	        		type : 'POST',
	        		url: 'JobOpportunities.action?strCategory='+category,
	        		data: $("#"+this.id).serialize(),
	        		success: function(result){
	                	$("#divResult").html(result);
	           		}
	        	});
	        }
	        </script>
    </body>
    </div>
</html>
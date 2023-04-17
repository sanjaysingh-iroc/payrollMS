<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<!-- created by seema -->
 <script type="text/javascript" src="https://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
<!-- created by seema -->
 <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
 
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- created by seema -->
<head>
<style>
.goog-logo-link {
	display: none !important;
}

.goog-te-gadget {
	color: transparent !important;
}

.goog-te-banner-frame.skiptranslate {
    display: none !important;
    }
body {
    top: 0px !important; 
    }


#translateDiv{
/* margin-top:6%; */
}

</style>
</head>
<!-- created by seema -->
<%
UtilityFunctions uF = new UtilityFunctions();
String callFrom = (String) request.getAttribute("callFrom");
String pType = (String) request.getAttribute("pType");
String alertID = (String) request.getAttribute("alertID");

String strClass1 = "class=\"active\"";
String strClass2 = "";

if (callFrom != null && callFrom.equals("LPDash")) {
	strClass1 = "";
	strClass2 = "class=\"active\"";
}

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();

%> 
  
<section class="content">
	
	<div class="row jscroll">
       <section class="col-lg-12 connectedSortable col_no_padding">
          <div class="col-md-12">
          
				 <div class="nav-tabs-custom" >
				    <!-- created by seema -->
					<div id="translateDiv" class="box-body" style="float: right; padding: 0px 10px;">
						<div id="google_translate_element" style="float: right; height: 30px;"></div>
						<!-- <div style="float: left;"><input type="button" class="notranslate btn btn-info btn-lg" onclick="getPDF();" value="Download PDF" id="download"></div> -->
					</div>
					<!-- created by seema -->
					<ul class="nav nav-tabs">
				          
				      
				      	<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_HR_DISABLE_TAB))){ 
				        	  List<String> disableTabList = hmFeatureUserTypeId.get(IConstants.F_MY_HR_DISABLE_TAB);
				        	  	
				        %>
				        	<% if(disableTabList != null && disableTabList.contains("GOALS")){ %>
				        		<li <%=strClass1 %>><a href="javascript:void(0)" onclick="getMyHRData('KRATarget','L','<%=pType%>','<%=alertID%>');" data-toggle="tab">Goals</a></li>
				        	<% } %>
				        	
				        	<% if(disableTabList != null && disableTabList.contains("LEARNINGS")){ %>
				        		<li <%=strClass2 %>><a href="javascript:void(0)" onclick="getMyHRData('MyLearningPlan','L','<%=pType%>','<%=alertID%>');" data-toggle="tab">Learnings</a></li>
				        	<% } %>
				        
				      	<% } else{ %>
				      		<%-- <li <%=strClass1 %>><a href="javascript:void(0)" onclick="getMyHRData('KRATarget','L','<%=pType%>','<%=alertID%>');" data-toggle="tab">Goals, KRAs, Targets</a></li> --%>
				          	<li <%=strClass1 %>><a href="javascript:void(0)" onclick="getMyHRData('KRATarget','L','<%=pType%>','<%=alertID%>');" data-toggle="tab">Goals</a></li>
				    
				          	<%-- <li <%=strClass2 %>><a href="javascript:void(0)" onclick="getMyHRData('MyLearningPlan','','<%=pType%>','<%=alertID%>');" data-toggle="tab">Learnings</a></li> --%>
				          	<li <%=strClass2 %>><a href="javascript:void(0)" onclick="getMyHRData('MyLearningPlan','L','<%=pType%>','<%=alertID%>');" data-toggle="tab">Learnings</a></li>
				      	<% } %>
				     </ul>
				       
				      <div class="tab-content" >
				         <!-- ===start parvez date: 24-02-2023=== -->  
				           <div class="active tab-pane" id="divMyHRData" style="min-height: 600px; max-height: 500px !important; overflow-y: hidden;">
				     		</div>
				     	<!-- ===end parvez date: 24-02-2023=== -->	
				      </div>
				  </div>
			    </div>
	   </section>
    </div>
</section> 

<script type="text/javascript" charset="utf-8">
var actionType="";
	$(document).ready(function() {
		<% if(callFrom != null && callFrom.equals("LPDash")) { %>
		actionType='Learning';
			<%-- getMyHRData('MyLearningPlan','','<%=pType %>','<%=alertID %>'); --%>
			getMyHRData('MyLearningPlan','L','<%=pType %>','<%=alertID %>');
		<%} else { %>
			
			<%-- actionType='Goals';
			getMyHRData('KRATarget','L','<%=pType %>','<%=alertID %>'); --%>
			<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_HR_DISABLE_TAB))){ 
		      	 List<String> disableTabList = hmFeatureUserTypeId.get(IConstants.F_MY_HR_DISABLE_TAB);
		      	  	
		    %>
			    <% if(disableTabList != null && disableTabList.contains("GOALS")){ %>
				    actionType='Goals';
					getMyHRData('KRATarget','L','<%=pType %>','<%=alertID %>');
			    <%-- <% } else{ %> --%>
			    <% } else if(disableTabList != null && disableTabList.contains("LEARNINGS")){ %>
			    
			    	actionType='Learning';
			    	getMyHRData('MyLearningPlan','L','<%=pType %>','<%=alertID %>');
			    <% } %>
			    
      		<% } else { %>
	      		actionType='Goals';
				getMyHRData('KRATarget','L','<%=pType %>','<%=alertID %>');
      		<% } %>
			
		<% } %>
	});
		function getMyHRData(strAction,dataType,pType,alertID){
	 // alert("getMyHRData jsp action==>" + strAction+"==>dataType==>"+dataType);
	  strAction = strAction+".action?fromPage=MyHR&pType="+pType+"&alertID="+alertID;
	  if(dataType != "") {
		  actionType="Goals";
		  strAction+= "&dataType="+dataType;
	  }
	  else{
		  actionType='Learning';
		  strAction+= "&dataType="+dataType;	/* added by parvez date: 27-09-2021 */
	  }
     
	  $("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	  $.ajax({ 
		type : 'POST',
		url: strAction,
		cache: true,
		success: function(result){
			//alert("result2==>"+result);
			$("#divMyHRData").html(result);
   		}
	  });
	}
		
		/*  created by seema */ 
	function googleTranslateElementInit() {
		new google.translate.TranslateElement({
			pageLanguage : 'en',
			includedLanguages : 'en,kn,mr,ta,te,hi'
		}, 'google_translate_element');
	}
	
	
	function getPDF(){
		if(actionType=='Goals'){
			getMyHRDataPDF('KRATarget','L','<%=pType%>','<%=alertID%>','downloadPdf');
		}
		else{
		/* ===start parvez date: 27-09-2021=== */	
			<%-- getMyHRDataPDF('MyLearningPlan','','<%=pType%>','<%=alertID%>','downloadPdf'); --%>
			getMyHRDataPDF('MyLearningPlan','L','<%=pType%>','<%=alertID%>','downloadPdf');
		/* ===end parvez date: 27-09-2021=== */
		}
	}
	
	function getMyHRDataPDF(strAction,dataType,pType,alertID,downloadPdf){
			 // alert("getMyHRData jsp action==>" + strAction+"==>dataType==>"+dataType);
			  strAction = strAction+".action?fromPage=MyHR&pType="+pType+"&alertID="+alertID;
			  if(dataType != "") {
				  actionType="Goals";
				  strAction+= "&dataType="+dataType+"&download="+downloadPdf;
			  }
			  else{
				  actionType='Learning';
			/* ===start parvez date: 27-09-2021=== */	  
				  /* strAction+= "&download="+downloadPdf; */
				  strAction+= "&dataType="+dataType+"&download="+downloadPdf;
			/* ===end parvez date: 27-09-2021=== */
			  }
		     
			  $("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			  $.ajax({ 
				type : 'POST',
				url: strAction,
				cache: true,
				success: function(result){
					//alert("result2==>"+result);
					$("#divMyHRData").html(result);
		   		}
			  });
	}
	 /* created by seema  */
	 
	 /* ===start parvez date: 24-02-2023=== */
$(window).bind('mousewheel DOMMouseScroll', function(event){
    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
        // scroll up
        if($(window).scrollTop() == 0 && $("#divMyHRData").scrollTop() != 0) {
        	$("#divMyHRData").scrollTop($("#divMyHRData").scrollTop() - 30);
        }
    } else {
        // scroll down
        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#divMyHRData").scrollTop($("#divMyHRData").scrollTop() + 30);
   		}
    }
});

$(window).keydown(function(event){
	if(event.which == 40 || event.which == 34){
		if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
			$("#divMyHRData").scrollTop($("#divMyHRData").scrollTop() + 50);
   		}
	} else if(event.which == 38 || event.which == 33){
		if($(window).scrollTop() == 0 && $("#divMyHRData").scrollTop() != 0) {
	    	$("#divMyHRData").scrollTop($("#divMyHRData").scrollTop() - 50);
	    }
	}
});
/* ===end parvez date: 24-02-2023=== */
	
</script>          
    
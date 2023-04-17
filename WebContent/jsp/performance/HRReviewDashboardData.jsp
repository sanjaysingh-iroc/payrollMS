<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" src="https://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>

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

</style>
</head>   

 <%	
 	UtilityFunctions uF = new UtilityFunctions();
	String dataType = (String) request.getAttribute("dataType"); 
	String currUserType = (String) request.getAttribute("currUserType");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
   	String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
    String divClass1 = "col-lg-12 connectedSortable";
    String divClass2 = "col-md-12";
    if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) {
    	 divClass1 = "col-lg-12 connectedSortable col_no_padding";
         divClass2 = "col-md-12 col_no_padding";
    }
%> 

 <div class="nav-tabs-custom">
 
 		<div id="translateDiv" class="box-body" style="float: right; padding: 0px 10px;">
			<div id="google_translate_element" style="float: right; height: 30px;"></div>
			<!-- <div style="float: left;"><input type="button" class="notranslate btn btn-info btn-lg" onclick="getPDF();" value="Download PDF" id="download"></div> -->
		</div>
      	<ul class="nav nav-tabs">
	        <li class="active"><a href="javascript:void(0)" style="padding: 5px 10px;" onclick="getAppraisalData('Appraisal','L','<%=currUserType %>');" data-toggle="tab">To Review</a></li>
	        <li><a href="javascript:void(0)" style="padding: 5px 10px;" onclick="getAppraisalData('Appraisal','C','<%=currUserType %>');" data-toggle="tab">Reviewed</a></li>
      	</ul>
      <div class="tab-content" >
           <div class="active tab-pane" id="appraisalResult" style="min-height: 600px;">
     		</div>
      </div>
  </div>
			    

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		//alert("inside getGoalDashboardData");
		getAppraisalData('Appraisal','<%=dataType%>','<%=currUserType%>');
	});
	
	function getAppraisalData(strAction,dataType,currUserType){
	//	alert("getGoalDashboardData jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		var paramValues = 'dataType='+dataType+'&currUserType='+currUserType;
		$("#appraisalResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?'+paramValues,
			cache: true,
			success: function(result){
				//console.log("result2==>"+result);
				$("#appraisalResult").html(result);
	   		}
		});
	}
	
	function googleTranslateElementInit() {
		new google.translate.TranslateElement({
			pageLanguage : 'en',
			includedLanguages : 'en,kn,mr,ta,te,hi'
		}, 'google_translate_element');
	}
	
</script>          
    
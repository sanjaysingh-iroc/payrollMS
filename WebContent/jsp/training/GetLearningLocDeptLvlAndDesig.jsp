
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<g:compress>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/displaystyle.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/stylesheet.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/tooltip.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/style_IE_nav.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/demo_table_jui.css" />
<link rel="stylesheet" type="text/css" media="screen" href="<%= request.getContextPath()%>/css/style1.css">
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/css/tabs.css' />
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.css' />
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.print.css' media='print' />
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/jquery.modaldialog.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/TableTools.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/TableTools_JUI.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/pro_dropline_ie.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/pro_dropline.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/style_IE_N.css" />
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/newsticker/ticker-style.css" />
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/highslide/highslide.css" />
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/validationEngine.jquery.css" />
</g:compress> 

<link rel="shortcut icon" href="images1/icons/icons/w_green.png" type="image/x-icon">
<link rel="icon" href="images1/icons/icons/w_green.png" type="image/x-icon">
 <%-- 
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery-ui-1.8.6.custom.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.min.js"> </script> --%>

<g:compress>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.ui.datepicker.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.ui.widget.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.ui.core.js"> </script>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/tooltip.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/custom.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/main.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery.PrintArea.js_4.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/complete.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery-1.4.4.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery.dataTables.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery.jeditable.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery-ui.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery.validate.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/datatable/jquery.dataTables.editable.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/charts/highstock.js"> </script> 

<script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery.tools.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/fullcalendar/timepicker.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.modaldialog.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/customAjax.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/TableTools.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/TableTools.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/highslide/highslide-with-html.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.lazyload.js"> </script>
</g:compress>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/charts/highcharts1.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/charts/highcharts-more.js"> </script>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/newsticker/jquery.ticker.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.validationEngine-en.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.validationEngine.js"> </script> 
 
<script type="text/javascript">

function getWLocIds() {
	var wlocLen = document.getElementById("f_strWLocation").length;
	 var txt = "";
	 for (var i = 0; i < wlocLen; i++) {
         var isSelected = document.getElementById("f_strWLocation").options[i].selected;
        if(isSelected = (isSelected)) {
        	if(txt == "") {
        		txt += "," + document.getElementById("f_strWLocation").options[i].value + ",";
        	} else {
        		txt += document.getElementById("f_strWLocation").options[i].value + ",";
        	}
        }
     }
	 parent.document.getElementById("wLocids").value = txt;
	 parent.document.getElementById("orgids").value = document.getElementById("f_org").value;
	 //alert("txt ===> " +txt);
}

function getDeptIds() {
	var departLen = document.getElementById("f_department").length;
	 var txt = "";
	 for (var i = 0; i < departLen; i++) {
         var isSelected = document.getElementById("f_department").options[i].selected;
        if(isSelected = (isSelected)) {
        	if(txt == "") {
        		txt += "," + document.getElementById("f_department").options[i].value + ",";
        	} else {
        		txt += document.getElementById("f_department").options[i].value + ",";
        	}
        }
     }
	 parent.document.getElementById("deptartids").value = txt;
	 //alert("txt ===> " +txt);
}

function getLevelIds() {
	var levelLen = document.getElementById("f_level").length;
	 var txt = "";
	 for (var i = 0; i < levelLen; i++) {
         var isSelected = document.getElementById("f_level").options[i].selected;
        if(isSelected = (isSelected)) {
        	if(txt == "") {
        		txt += "," + document.getElementById("f_level").options[i].value + ",";
        	} else {
        		txt += document.getElementById("f_level").options[i].value + ",";
        	}
        }
     }
	 parent.document.getElementById("levelids").value = txt; 
	 //alert("txt ===> " +txt);
}

function getDesigIds() {
	var desigLen = document.getElementById("f_desig").length;
	 var txt = "";
	 for (var i = 0; i < desigLen; i++) {
         var isSelected = document.getElementById("f_desig").options[i].selected;
        if(isSelected = (isSelected)) {
        	if(txt == "") {
        		txt += "," + document.getElementById("f_desig").options[i].value + ",";
        	} else {
        		txt += document.getElementById("f_desig").options[i].value + ",";
        	}
        }
     }
	 parent.document.getElementById("desigids").value = txt; 
	 //alert("txt ===> " +txt);
}

</script>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
	$("#f_desig").multiselect();
	$("#f_grade").multiselect();
});    
</script>

	<s:form name="frmGetLearningLocDeptLvlAndDesig" id="frmGetLearningLocDeptLvlAndDesig" action="GetLearningLocDeptLvlAndDesig" theme="simple">
		<%-- <input type="hidden" name="lPlanID" id="lPlanID" value="<%=planId %>" /> --%>
		
		<div id="multiFilterDiv" class="filter_div">
		<div class="filter_caption">Filter</div>
			<div style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Organisation</p>
				<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;" listValue="orgName" headerKey="" 
				headerValue="All Organisations" value="orgID" onchange="document.frmGetLearningLocDeptLvlAndDesig.submit();" list="organisationList" key="" />
			</div>
			<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Location</p>
				<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
					listValue="wLocationName" multiple="true" list="wLocationList" key="" value="locID" onchange="getWLocIds();"/>
			</div> 

			<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Department</p>
				<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId"
					cssStyle="float:left;margin-right: 10px;" listValue="deptName" multiple="true" value="departID" onchange="getDeptIds();"/>
			</div>

			<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Level</p>
				<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" cssStyle="float:left;margin-right: 10px;width:100px;" 
				listValue="levelCodeName" multiple="true" list="levelList" key="" value="levelID" onchange="getLevelIds();"/>
			</div>

			<div style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Designation</p>
				<s:select name="f_desig" list="desigList" listKey="desigId" id="f_desig" listValue="desigCodeName" 
					cssStyle="float:left;margin-right: 10px;width:100px;" multiple="true" value="desigID" onchange="getDesigIds();"/>
			</div>
			
			</div>
	</s:form>
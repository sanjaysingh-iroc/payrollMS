<%@page import="com.konnect.jpms.recruitment.FillEducational"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@page import="com.konnect.jpms.select.FillMaritalStatus"%>

<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.select.FillYears"%>
<%@page import="com.konnect.jpms.select.FillDegreeDuration"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ taglib prefix="s" uri="/struts-tags" %> 
 

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
 
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery-ui-1.8.6.custom.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery.min.js"> </script>
 

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
 

<style>

<g:compress>
.row_language {
	float: left;
	width: 100%;
	margin: 5px 0px 5px 0px;
	padding: 5px 0px 5px;
	border-bottom: solid 1px #efefef;
}

.row_hobby{
	float: left;
	width: 100%;
	margin: 5px 0px 5px 0px;
	padding: 5px 0px 5px;
	border-bottom: solid 1px #efefef;
}
.row_education{
	float: left;
	width: 100%;
	margin: 5px 0px 5px 0px;
	padding: 5px 0px 5px;
	border-bottom: solid 1px #efefef;
}

#div_language {
	height: 300px;
	width: 46%;
	float: left;
	border: solid 2px #ccc;
	overflow: auto;
	margin: 10px 10px 10px 10px;
	padding: 10px;
}

#div_education {
	height: 300px;
	width: 46%;
	float: left;
	border: solid 2px #ccc;
	overflow: auto;
	margin: 10px 10px 10px 10px;
	padding: 10px;
}

</g:compress>

</style>

<% 	
	
	String struserType = (String)session.getAttribute(IConstants.USERTYPE);
	ArrayList educationalList = (ArrayList) request.getAttribute("educationalList");
	ArrayList alSkills = (ArrayList) request.getAttribute("alSkills"); 
	ArrayList alHobbies = (ArrayList) request.getAttribute("alHobbies");
	ArrayList alLanguages = (ArrayList) request.getAttribute("alLanguages");
	List<List<String>> alEducation = (List<List<String>>) request.getAttribute("alEducation");
	if (alEducation == null)alEducation = new ArrayList<List<String>>();
	
	ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
	ArrayList alSiblings = (ArrayList) request.getAttribute("alSiblings");
	ArrayList alchilds = (ArrayList) request.getAttribute("alchilds");

	ArrayList alPrevEmployment = (ArrayList) request.getAttribute("alPrevEmployment");
	List<FillDegreeDuration> degreeDurationList = (List<FillDegreeDuration>) request.getAttribute("degreeDurationList");
	if (degreeDurationList == null)	degreeDurationList = new ArrayList<FillDegreeDuration>();
	
	List<FillYears> yearsList = (List<FillYears>) request.getAttribute("yearsList");
	if (yearsList == null)yearsList = new ArrayList<FillYears>();
	
	List empGenderList = (List) request.getAttribute("empGenderList");
	List maritalStatusList = (List) request.getAttribute("maritalStatusList");
	
	List skillsList = (List) request.getAttribute("skillsList");
	String strImage = (String) request.getAttribute("strImage");
	
	HashMap empServicesMap = (HashMap) request.getAttribute("empServicesMap");
	
	UtilityFunctions uF = new UtilityFunctions();
	String currentYear = (String)request.getAttribute("currentYear");
	
	
	
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	int nEmpAlphaCodeLength = 2;
	if(CF!=null && CF.getStrOEmpCodeAlpha()!=null){
		nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();
	}
	
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	
%>

<script>  


function validateMandatory(value){
	if(value=='AT' || value=='CO'){
		document.getElementById("desigId").style.display = 'none';
		document.getElementById("gradeId").style.display = 'none';
		
		document.getElementById("desigIdV").className = '';
		document.getElementById("gradeIdV").className = '';
	}else{
		document.getElementById("desigId").style.display = 'inline';
		document.getElementById("gradeId").style.display = 'inline';
		
		document.getElementById("desigIdV").className = 'validateRequired';
		document.getElementById("gradeIdV").className = 'validateRequired';
	}
}

function showState() {	
	
}


function callDatePicker() {

	$("input[name=memberDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
	$("input[name=childDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
	$("input[name=prevCompanyFromDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
   <%--  $("input[name=prevCompanyToDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true}); --%>
    
}

$(function() {
	
    $( "#empStartDate" ).datepicker({dateFormat: 'dd/mm/yy', yearRange: '<%=(String)request.getAttribute("dobYear")%>:<%=uF.parseToInt(currentYear)+1%>', changeYear: true});
    $( "#empDateOfBirth" ).datepicker({dateFormat: 'dd/mm/yy',yearRange: '<%=uF.parseToInt(currentYear)-80%>:<%=uF.parseToInt(currentYear)-14%>', changeYear: true});
    $( "#empDateOfMarriage" ).datepicker({dateFormat: 'dd/mm/yy',yearRange: '<%=uF.parseToInt(currentYear)-60%>:<%=currentYear%>',changeYear: true});
    $("input[name=prevCompanyFromDate]").datepicker({dateFormat: 'dd/mm/yy',yearRange: '<%=uF.parseToInt(currentYear)-80%>:<%=currentYear%>',  changeYear: true});
    <%-- $("input[name=prevCompanyToDate]").datepicker({dateFormat: 'dd/mm/yy',yearRange: '<%=uF.parseToInt(currentYear)-80%>:<%=currentYear%>', changeYear: true}); --%>
    $("input[name=fatherDob]").datepicker({dateFormat: 'dd/mm/yy',yearRange: '<%=uF.parseToInt((String)request.getAttribute("dobYear"))-50%>:<%=(String)request.getAttribute("dobYear")%>', changeYear: true});
    $("input[name=motherDob]").datepicker({dateFormat: 'dd/mm/yy',yearRange: '<%=uF.parseToInt((String)request.getAttribute("dobYear"))-50%>:<%=(String)request.getAttribute("dobYear")%>', changeYear: true});
    $("input[name=spouseDob]").datepicker({dateFormat: 'dd/mm/yy',yearRange: '<%=uF.parseToInt(currentYear)-80%>:<%=currentYear%>', changeYear: true});
    $("input[name=memberDob]").datepicker({dateFormat: 'dd/mm/yy',yearRange: '<%=uF.parseToInt(currentYear)-80%>:<%=currentYear%>', changeYear: true});
    $("input[name=childDob]").datepicker({dateFormat: 'dd/mm/yy',yearRange: '<%=(String)request.getAttribute("dobYear")%>:<%=currentYear%>', changeYear: true});
    $("input[name=empPassportExpiryDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '<%=currentYear%>:<%=uF.parseToInt(currentYear)+20%>', changeYear: true});
    
});

var prevcnt =0;
<% if (alPrevEmployment!=null) {%>

	prevcnt=<%=alPrevEmployment.size()%>;
<%}%>

function addPrevEmployment() {

	prevcnt++;
	
	var divTag = document.createElement("div");
	divTag.id = "col_prev_employer"+prevcnt;
	divTag.setAttribute("style", "float:left");
	divTag.innerHTML = "<%=request.getAttribute("sbPrevEmployment")%>" + 
					"<a href=\"javascript:void(0)\"  onclick=\"removePrevEmployment(this.id)\" id=\""+prevcnt+"\" class=\"remove\" ></td>";
					"</tr>" + 
				     "</table>";
	document.getElementById("div_prev_employment").appendChild(divTag);
	callDatePicker();
}

function removePrevEmployment(removeId) {

	var remove_elem = "col_prev_employer" + removeId;
	var row_document = document.getElementById(remove_elem); 
	document.getElementById("div_prev_employment").removeChild(row_document);

}
      
function setcompanyTodd(val){
	
	var val1=val.split("/");
    $("input[name=prevCompanyToDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: val1[2]+':<%=currentYear%>', changeYear: true});
}

function showMarriageDate(){
	if(document.frmPersonalInfo.empMaritalStatus.options[document.frmPersonalInfo.empMaritalStatus.options.selectedIndex].value=='M'){
		document.getElementById("trMarriageDate").style.display = 'table-row';
	}else{
		document.getElementById("trMarriageDate").style.display = 'none';
	}
}


var skillcnt =0;
<% if (alSkills!=null) {%>
	 skillcnt=<%=alSkills.size()%>;
<%}%>

function addSkills() {
	
	skillcnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_skill"+skillcnt;
    divTag.setAttribute("class", "row_skill");
	divTag.innerHTML = 	"<%=request.getAttribute("sbSkills")%>" +
    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addSkills()\" class=\"add\">Add</a></td>" +
    			    	"<td><a href=\"javascript:void(0)\" onclick=\"removeSkills(this.id)\" id=\""+skillcnt+"\" class=\"remove\">Remove</a></td>"; 
    document.getElementById("div_skills").appendChild(divTag);
    
}

function removeSkills(removeId) {
	
	var remove_elem = "row_skill"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_skills").removeChild(row_skill);
	
}

var hobbiescnt=0;
<% if (alHobbies!=null) {%>
 hobbiescnt=<%=alHobbies.size()%>;

<%}%>

function addHobbies() {
	
	hobbiescnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_hobby"+hobbiescnt;
    divTag.setAttribute("class", "row_hobby");
	divTag.innerHTML = 	"<table>"+
	                    "<tr><td><input type=\"text\" style=\"height: 25px; width: 180px; \" name=\"hobbyName\"></input></td>" +   			    	
    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addHobbies()\" class=\"add\">Add</a></td>" +
 						"<td><a href=\"javascript:void(0)\" onclick=\"removeHobbies(this.id)\" id=\""+hobbiescnt+"\" class=\"remove\">Remove</a></td></tr>" +
	                    "</table>";
	                    
    document.getElementById("div_hobbies").appendChild(divTag);
    
}

function removeHobbies(removeId) {
	
	var remove_elem = "row_hobby"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_hobbies").removeChild(row_skill);
	
}

var languagecnt=0;
<% if (alLanguages!=null) {%>

 languagecnt=<%=alLanguages.size()%>;



<%}%>


function addLanguages() {
	languagecnt = parseInt(languagecnt)+1;
	var trTag = document.createElement("tr");
    trTag.id = "row_language_"+languagecnt;
    trTag.setAttribute("class", "row_language");
	trTag.innerHTML = 	"<td><input type=\"text\" name=\"languageName\" ></input></td>" + 
		"<td align=\"center\"><input type=\"checkbox\" name=\"isReadcheckbox\" value=\"1\"  id=\"isRead_"+languagecnt+"\" onchange=\"showHideHiddenField(this.id)\" />" +
		"<input type=\"hidden\" value=\"0\" name=\"isRead\" id=\"hidden_isRead_"+languagecnt+"\" /></td>" +
		"<td  align=\"center\"><input type=\"checkbox\" name=\"isWritecheckbox\" value=\"1\" id=\"isWrite_"+languagecnt+"\" onchange=\"showHideHiddenField(this.id)\" />"+
		"<input type=\"hidden\" value=\"0\" name=\"isWrite\" id=\"hidden_isWrite_"+languagecnt+"\" /></td>" +
		"<td  align=\"center\"><input type=\"checkbox\" name=\"isSpeakcheckbox\" value=\"1\" id=\"isSpeak_"+languagecnt+"\" onchange=\"showHideHiddenField(this.id)\" />"+
		"<input type=\"hidden\" value=\"0\" name=\"isSpeak\" id=\"hidden_isSpeak_"+languagecnt+"\" /></td>" +
		"<td  align=\"center\"><input type=\"radio\" name=\"isMotherToungeRadio\" value=\"1\" id=\"isMotherTounge_\" onchange=\"showHideHiddenField1(this.id, '"+languagecnt+"')\" />"+
		"<input type=\"hidden\" value=\"0\" name=\"isMotherTounge\" id=\"hidden_isMotherTounge_"+languagecnt+"\" /></td>" +
		"<td  align=\"center\"><a href=\"javascript:void(0)\" onclick=\"addLanguages()\" class=\"add-font\"></a><a href=\"javascript:void(0)\" onclick=\"removeLanguages(this.id)\" id=\""+languagecnt+"\" class=\"remove-font\" ></a></td>"; 

	document.getElementById("table-language").appendChild(trTag);
    document.getElementById("hideLanguageRowCount").value = parseInt(languagecnt)+1;
}

function removeLanguages(removeId) {
	
	var remove_elem = "row_language_"+removeId;
	var row_language = document.getElementById(remove_elem); 
	document.getElementById("table-language").removeChild(row_language);
	
}

 var educationcnt =0;
<% if (alEducation!=null && !alEducation.isEmpty()) {%>
	educationcnt=<%=alEducation.size()%>;
<%}

String sbdegreeDuration=(String)request.getAttribute("sbdegreeDuration");
%>

<%-- function addEducation() {
	
	educationcnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_education"+educationcnt;
    divTag.setAttribute("class", "row_education");
	divTag.innerHTML = "<table>"+"<%=uF.showData(sbdegreeDuration,"") %> " +
			"<a href=javascript:void(0) onclick=removeEducation(this.id) id="+educationcnt+" class=remove >Remove</a></td></tr>" +
    		"</table>"; 
    document.getElementById("div_education").appendChild(divTag);
    
} --%>

function addEducation() {
	
	educationcnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_education"+educationcnt;
    divTag.setAttribute("class", "row_education");
    divTag.innerHTML = "<table><tr><td><select name=\"degreeName\" style=\"width:110px;\" onchange=\"checkEducation(this.value,"+educationcnt+")\"> "+
					 "<%=request.getAttribute("sbdegreeDuration")%>" +
			"<td><a href=\"javascript:void(0)\" onclick=\"removeEducation(this.id)\" id=\""+educationcnt+"\" class=\"remove\" >Remove</a></td></tr>" +
 			" <tr id=\"degreeNameOtherTR"+educationcnt+"\"  style=\"display:none;\"><td style=\"text-align: right;\">Enter Education :</td><td colspan=\"3\"> " +
			"<input type=\"text\" name=\"degreeNameOther\" style=\"height: 25px;\"></td></tr>"+ 
			"</table>"; 
    document.getElementById("div_education").appendChild(divTag);
}

function checkEducation(value,count){
	if(value=="other"){
		document.getElementById("degreeNameOtherTR"+count).style.display="table-row";
	}
}

function removeEducation(removeId) {
	
	var remove_elem = "row_education"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_education").removeChild(row_skill);
	
} 

  var siblingcnt = 0;

function addSibling() {
	
	siblingcnt++;
	
	var divTag = document.createElement("div");

	divTag.id = "col_family_siblings"+siblingcnt;
	divTag.setAttribute("style", "float: left;border:solid 0px #f00;width:100%");
    
    divTag.innerHTML = 	"<%=request.getAttribute("sbSibling")%>" +
 			"<td><a href=\"javascript:void(0)\" onclick=\"removeSibling(this.id)\" id=\""+siblingcnt+"\" class=\"remove\" >Remove</a></td></tr>" +
            "</table>"; 

	document.getElementById("div_id_family").appendChild(divTag);
	callDatePicker();
	
}

function removeSibling(removeId) {
	
	var remove_elem = "col_family_siblings"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_id_family").removeChild(row_skill);
	
}  


 var childcnt = 0;
function addChildren() {
	
	childcnt++;
	
	var divTag = document.createElement("div");

	divTag.id = "col_family_child"+childcnt;
	divTag.setAttribute("style", "float: left;border:solid 0px #f00;width:100%");
    
    divTag.innerHTML = 	"<%=request.getAttribute("sbChildren")%>" +
 			"<td><a href=\"javascript:void(0)\" onclick=\"removeChildren(this.id)\" id=\""+childcnt+"\" class=\"remove\" >Remove</a></td></tr>" +
            "</table>"; 

	document.getElementById("div_id_child").appendChild(divTag);
	callDatePicker();
	
}

function removeChildren(removeId) {
	
	var remove_elem = "col_family_child"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_id_child").removeChild(row_skill);
	
} 


var documentcnt = 0;

<% if (alDocuments!=null) {%>

documentcnt=<%=alDocuments.size()%>;



<%}%>

function addDocuments() {
	documentcnt++;
        var table = document.getElementById('row_document_table');

        var rowCount = table.rows.length;
       
        var row = table.insertRow(rowCount);
        row.id = "row_document"+rowCount;
        var cell1 = row.insertCell(0);
        cell1.setAttribute("class", "txtlabel alignRight");
        cell1.setAttribute("style", "text-align: -moz-center");

       cell1.innerHTML = "<input type=\"hidden\" name=\"idDocType\" value=\"<%=IConstants.DOCUMENT_OTHER%>\"></input><input type=\"text\" class=\"validateRequired text-input\" style=\"width: 180px; \" name=\"idDocName\"></input>";
       
       var cell2 = row.insertCell(1);
       cell2.setAttribute("class", "txtlabel alignRight");
       cell2.setAttribute("style", "text-align: -moz-center");
       cell2.innerHTML = "<input type=\"file\" name=\"idDoc\" onchange=\"fillFileStatus('idDoc"+documentcnt+"Status')\" /><input type=\"hidden\" name=\"idDocStatus\" id=\"idDoc"+documentcnt+"Status\" value=\"0\"></input>";
       
       var cell3 = row.insertCell(2);
       cell3.setAttribute("class", "txtlabel alignRight");
       cell3.setAttribute("style", "text-align: -moz-center");
       cell3.innerHTML = "<a href=\"javascript:void(0)\" onclick=\"addDocuments()\" class=\"add\">Add</a><a href=\"javascript:void(0)\" onclick=\"removeDocuments('row_document"+rowCount+"')\" id=\""+documentcnt+"\" class=\"remove\">Remove</a>";
     
}


function removeDocuments(rowid)  
{   
    var table =  document.getElementById('row_document_table');
    var row = document.getElementById(rowid);
    table.deleteRow(row.rowIndex);
    
} 
 
function deleteDocuments(removeId) {
	getContent('removeDivDocument_'+removeId,'DeleteDocuments.action?documentId='+removeId);
} 


function showHideHiddenField(fieldId) {
	
	if(document.getElementById(fieldId).checked) {
		document.getElementById( "hidden_"+fieldId).value="1";
	}else {
		document.getElementById( "hidden_"+fieldId).value="0";
	}
} 


function showHideHiddenField1(fieldId, fieldCnt) {
	var langCount = document.getElementById("hideLanguageRowCount").value;
	 //alert("langCount ===>> " + langCount + " -- fieldCnt ===>> " + fieldCnt+" --- fieldId ===>> " + fieldId);
	for(var i=0; i<langCount; i++) {
		 //alert("i ===>> " + i);
		if(i==fieldCnt) {
			 //alert("in if ===>> " + i);
			document.getElementById("hidden_"+fieldId+i).value = "1";
		} else {
			 //alert("else ===>> " + i);
			if(document.getElementById("hidden_"+fieldId+i)) {
				 //alert("else in if ===>> " + i);
				document.getElementById("hidden_"+fieldId+i).value = "0";
			}
		}
	}
}
 
 function checkRadio(obj,val){
	document.getElementById(val).disabled=true;
	if(obj.value=='false'){
		document.getElementById(val).disabled=true;
	document.getElementById(val+'File').disabled=true;

	}else{
		document.getElementById(val).disabled=false;
		document.getElementById(val+'File').disabled=false;

	}
		
} 

 function fillFileStatus(ids){
	
	document.getElementById(ids).value=1;
} 

 function copyAddress(obj){    
	if(obj.checked){
		
		var sel=document.getElementById("countryTmp");
		for(var i = 0, j = sel.options.length; i < j; ++i) {
	        if(sel.options[i].innerHTML === document.getElementById("country").options[document.getElementById("country").selectedIndex].text) {
	           sel.selectedIndex = i;
	           break;
	        }
	    }
		
		sel=document.getElementById("stateTmp");
		for(var i = 0, j = sel.options.length; i < j; ++i) {
	        if(sel.options[i].innerHTML === document.getElementById("state").options[document.getElementById("state").selectedIndex].text) {
	           sel.selectedIndex = i;
	           break;
	        }
	    }
		
		document.getElementById("frmPersonalInfo_empAddress1Tmp").value = document.getElementById("frmPersonalInfo_empAddress1").value;
		document.getElementById("frmPersonalInfo_cityTmp").value = document.getElementById("frmPersonalInfo_city").value;
		document.getElementById("frmPersonalInfo_empPincodeTmp").value = document.getElementById("frmPersonalInfo_empPincode").value;
		
		
		
	}else{
		document.getElementById("frmPersonalInfo_empAddress1Tmp").value = '';
		document.getElementById("frmPersonalInfo_cityTmp").value = '';
		document.getElementById("frmPersonalInfo_empPincodeTmp").value = '';
		
		var sel=document.getElementById("countryTmp");
		for(var i = 0, j = sel.options.length; i < j; ++i) {
	        if(sel.options[i].innerHTML === document.getElementById("country").options[0].text) {
	           sel.selectedIndex = i;
	           break;
	        }
	    }
		
		sel=document.getElementById("stateTmp");
		for(var i = 0, j = sel.options.length; i < j; ++i) {
	        if(sel.options[i].innerHTML === document.getElementById("state").options[0].text) {
	           sel.selectedIndex = i;
	           break;
	        }
	    }
	}
	
}  

//addLoadEvent(prepareInputsForHints);
</script>



<script>

// perform JavaScript after the document is scriptable.
$(function() {
	// setup ul.tabs to work as tabs for each div directly under div.panes 
	$("ul.tabs").tabs("div.panes > div");
});
</script>

<script>
      jQuery(document).ready(function(){
          // binds form submission and fields to the validation engine
          jQuery("#frmPersonalInfo").validationEngine();
          jQuery("#frmReferences").validationEngine();
          jQuery("#frmOfficialInfo").validationEngine();
          jQuery("#frmBackgroundInfo").validationEngine();
          jQuery("#frmFamilyInfo").validationEngine();
          jQuery("#frmPrevEmployment").validationEngine();
          jQuery("#frmMedicalInfo").validationEngine();
          jQuery("#frmDocumentation").validationEngine();
          
      });
      
      var empCode='<%=(String)request.getAttribute("EMP_CODE")%>';
      function checkCodeValidation(){
    	  
    	  
    	 var empCodeAlphabet='';
    	 if(document.getElementById('empCodeAlphabet')){
    		 empCodeAlphabet =document.getElementById('empCodeAlphabet').value;
    	 }
    	 var empCodeNumber= document.getElementById('empCodeNumber').value;

    	 if(empCode==(empCodeAlphabet+empCodeNumber)){
    		 document.getElementById('empCodeMessege').innerHTML='';
      		return;
      	}
    	 
    	 var xmlhttp = GetXmlHttpObject();
   		if (xmlhttp == null) {
   			alert("Browser does not support HTTP Request");
   			return;
   		} else {

   			var xhr = $.ajax({
   				url : "EmailValidation.action?empCodeAlphabet=" +empCodeAlphabet+empCodeNumber,
   				cache : false,
   				success : function(data) {
   					document.getElementById('empCodeMessege').innerHTML=data;
   					if(data.length>1){
   						
   						document.getElementById('empCodeNumber').value='';
   					}
   					
   				}
   			});

   		}
   		
    	//  getContent('empCodeMessege','EmailValidation.action?empCodeAlphabet='+empCodeAlphabet+empCodeNumber);
      }
      
      var val='<%=(String)request.getAttribute("EMPLOYEE_EMAIL")%>';
      function emailValidation(id,id1,val1,action){
    	  
    	
     	if(val==val1){
     		document.getElementById(id).innerHTML='';
     		return;
     	}

     	 var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {

    			var xhr = $.ajax({
    				url : action,
    				cache : false,
    				success : function(data) {
    					document.getElementById(id).innerHTML=data;
    					if(data.length>1){
    						
    						
    						document.getElementById(id1).value='';
    					}
    					
    				}
    			});

    		}
    		
       }
      var val2='<%=(String)request.getAttribute("EMPLOYEE_EMAIL2")%>';
function emailValidation1(id,id1,val1,action){
    	  
    	  
     	if(val2==val1){
     		document.getElementById(id).innerHTML='';
     		return;
     	}

     	 var xmlhttp = GetXmlHttpObject();
    		if (xmlhttp == null) {
    			alert("Browser does not support HTTP Request");
    			return;
    		} else {

    			var xhr = $.ajax({
    				url : action,
    				cache : false,
    				success : function(data) {
    					document.getElementById(id).innerHTML=data;
    					if(data.length>1){
    						
    						
    						document.getElementById(id1).value='';
    					}
    					
    				}
    			});

    		}
    		
       }
      
      
     
  		 function GetXmlHttpObject() {
  				if (window.XMLHttpRequest) {
  					// code for IE7+, Firefox, Chrome, Opera, Safari
  					return new XMLHttpRequest();
  				}
  				if (window.ActiveXObject) {
  					// code for IE6, IE5
  					return new ActiveXObject("Microsoft.XMLHTTP");
  				}
  				return null;
  			}
</script>




<div class="pagetitle">
<%if(session.getAttribute(IConstants.USERID)!=null){ %>
      <span>Edit Employee Detail</span>
<%}else{ %>
	  <span>Edit your Details</span>
<%}%>
</div>

<div class="leftbox reportWidth" >

<%
	String strEmpType = (String) session.getAttribute("USERTYPE");
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>


<%if(!"U".equalsIgnoreCase(request.getParameter("operation"))) { %>

<div class="steps">
<s:if test="step==1">
  <span class="current"> Personal Information :</span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=1&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Background Information :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=2&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=3&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=4&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=5&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=6&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Documentation :</a></span>
  <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
  <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=7&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Official Information :</a></span>
  <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=8&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Salary Information :</a></span>
  <%}else{%>
  <span class="next1"> Availablity :</span>
  <%}%>
</s:if>
<s:if test="step==2">
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=0&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
  <span class="current">Background Information :</span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=2&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=3&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=4&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=5&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=6&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Documentation :</a></span>
  <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
  <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=7&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Official Information :</a></span>
  <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=8&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Salary Information :</a></span>
  <%}else{%>
  <span class="next1"> Availablity :</span>
  <%}%>
</s:if>
<s:if test="step==3">
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=0&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=1&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
  <span class="current">Family Information :</span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=3&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment :</a></span>
   <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=4&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
   <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=5&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
   <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=6&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Documentation :</a></span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
   <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=7&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Official Information :</a></span>
   <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=8&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Salary Information :</a></span>
   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<s:if test="step==4">
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=0&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=1&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=2&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
  <span class="current">Previous Employment :</span>
   <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=4&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
   <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=5&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
   <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=6&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Documentation :</a></span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
   <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=7&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Official Information :</a></span>
   <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=8&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Salary Information :</a></span>
   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<s:if test="step==5">
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=0&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=1&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=2&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=3&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment:</a></span>
  <span class="current">References :</span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=5&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=6&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Documentation :</a></span>
  <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
  <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=7&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Official Information :</a></span>
  <span class="next1"> <a href="AddOnboardingFromCandidate.action?mode=onboard&step=8&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Salary Information :</a></span>
  <%}else{%>
  <span class="next1"> Availablity :</span>
  <%}%>
</s:if>
<s:if test="step==6">
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=0&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=1&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=2&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=3&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment:</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=4&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
  <span class="current">Medical Information :</span>
  <span class="next"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=6&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Documentation :</a></span>
  <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
  <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=7&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Official Information :</a></span>
  <span class="next1"> <a href="AddOnboardingFromCandidate.action?mode=onboard&step=8&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Salary Information :</a></span>
  <%}else{%>
  <span class="next1"> Availablity :</span>
  <%}%>
</s:if>
<s:if test="step==7">
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=0&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=1&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=2&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=3&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment:</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=4&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=5&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
   <span class="current">Documentation :</span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
   <span class="next1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=7&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Official Information :</a></span>
  <span class="next1"> <a href="AddOnboardingFromCandidate.action?mode=onboard&step=8&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Salary Information :</a></span>
   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<s:if test="step==8">
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=0&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=1&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=2&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=3&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment:</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=4&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=5&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=6&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Documentation :</a></span>
  <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
  <span class="current">Official Information :</span>
  <span class="next1"> <a href="AddOnboardingFromCandidate.action?mode=onboard&step=8&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Salary Information :</a></span>
  <%}else{%>
  <span class="next1"> Availablity :</span>
  <%}%>
</s:if>

<s:if test="step==9">
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=0&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=1&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=2&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=3&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment:</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=4&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=5&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=6&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Documentation :</a></span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
   	<span class="prev1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=7&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Official Information :</a></span>
   	<span class="current">Salary Information :</span>
   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>


<s:if test="step==11">
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=0&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Personal Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=1&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Background Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=2&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Family Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=3&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Previous Employment:</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=4&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">References :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=5&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Medical Information :</a></span>
  <span class="prev"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=6&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;">Documentation :</a></span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
   	<span class="prev1"><a href="AddOnboardingFromCandidate.action?mode=onboard&step=7&empId=<%=request.getAttribute("empId")%>" style="color: #5A87B4; font-weight: normal; font-size: 11px;"> Official Information :</a></span>
   	<span class="current">Salary Information :</span>
   <%}else{%>
   <span class="next1"> Availablity :</span>
   <% } %>
</s:if>
</div>

<% } %>

<p class="message"><%=strMessage%></p>

<!-- the tabs -->
<ul class="tabs">

	<s:if test="step==1 || mode=='report'">
		<li><a href="#tab1">Personal Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==2 || mode=='report'">
		<li><a href="#tab4">Background Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==3 || mode=='report'">
		<li><a href="#tab5">Family Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==4 || mode=='report'">
		<li><a href="#tab6">Previous Employment of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==5 || mode=='report'">
		<li><a href="#tab2">References of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
	</s:if>

	<s:if test="step==6 || mode=='report'">
		<li><a href="#tab7">Medical Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
	</s:if>
		
	<s:if test="step==7 || mode=='report'">
		<li><a href="#tab8">Documentation of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==8 || mode=='report'">
		<li><a href="#tab3">Official Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==9 || mode=='report'">
		
		<%-- <li><a href="#">Salary Information for <s:property value="serviceName" /> </a></li> --%>
		<li><a href="#">Salary Structure Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
		
	</s:if>
	
	<s:if test="step==11">
		
		<%-- <li><a href="#">Salary Information for <s:property value="serviceName" /> </a></li> --%>
		<li><a href="#">Availability</a></li>
		
	</s:if>
</ul>

<!-- tab "panes" -->
	
<div class="panes">
	<s:if test="step==1 || mode=='report'">
		<div>

		<s:form theme="simple" action="AddOnboardingFromCandidate" name="frmPersonalInfo" id="frmPersonalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
				
			<div style="float: left;" >
			
			<table border="0" class="formcss">
			
			<tr><td><s:hidden name="empId" /></td></tr>
			<tr><td><s:hidden name="step" /></td></tr>
			<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px; padding:5px;">
            Step 1 : </span> Enter Employee Personal Information</td></tr>
			<tr><td height="10px">&nbsp;</td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empFname</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Salutation<sup>*</sup>:</td>
			
			<td>
			<% if(session.getAttribute("isApproved")==null) { %>
				<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation"
				listKey="salutationId" listValue="salutationName" cssClass="validateRequired"></s:select>
			
			<% } else { %>
				<s:textfield name="salutation" required="true" disabled="true"/>
				<s:hidden name="salutation" />
			<% } %>
			
			<span class="hint">Employee's salutation.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td class="txtlabel alignRight">First Name<sup>*</sup>:</td>
			
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<s:textfield name="empFname" cssClass="validateRequired text-input" cssStyle="height: 25px; width: 206px;" required="true"/>
			<%}else{%>
				<s:textfield name="empFname" cssStyle="height: 25px; width: 206px;" required="true" disabled="true"/>
				<s:hidden name="empFname" />
			<%}%>
			
			<span class="hint">Employee's first name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td class="txtlabel alignRight">Middle Name:</td>
			
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<s:textfield name="empMname" cssStyle="height: 25px; width: 206px;" required="true"/>
			<%}else{%>
				<s:textfield name="empMname" cssStyle="height: 25px; width: 206px;" required="true" disabled="true"/>
				<s:hidden name="empMname" />
			<%}%>
			
			<span class="hint">Employee's Middle name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empLname</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Last Name<sup>*</sup>:</td>
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<s:textfield name="empLname" cssClass="validateRequired text-input" cssStyle="height: 25px; width: 206px;" required="true"/>
			<%}else{%>
				<s:textfield name="empLname" cssStyle="height: 25px; width: 206px;" required="true" disabled="true"/>
				<s:hidden name="empLname" />
			<%}%>
			
			<span class="hint">Employee's last name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empEmail</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Personal Email Id<sup>*</sup>:</td>
			<td>
			
			<%if(session.getAttribute("isApproved")==null) {
			String email=(String)request.getAttribute("EMPLOYEE_EMAIL");
			if(email==null){
				email="";
			}
			%>
			
				<s:textfield name="empEmail" id="empEmail" cssClass="validate[required,custom[email]]" cssStyle="height: 25px; width: 206px;" required="true"  onchange="emailValidation('emailValidatorMessege','empEmail',this.value, 'EmailValidation.action?email='+this.value);"/>
				<div id="emailValidatorMessege"></div>
			<%}else{%>
				<s:textfield name="empEmail" cssStyle="height: 25px; width: 206px;" required="true" disabled="true"/>
				<s:hidden name="empEmail" />
			<%}%>
			
			<span class="hint">Email id is required as the user will received all information on this id.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2>Permanent Address:
			<hr style="background-color:#346897;height:1px">&nbsp;<s:fielderror ><s:param>empAddress1</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight" valign="top">Address<sup>*</sup>:</td><td><s:textarea name="empAddress1" cssClass="validateRequired text-input" cssStyle="width: 206px; height: 108px;" required="true"></s:textarea><span class="hint">Employee current address.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<%-- <tr><td class="txtlabel alignRight">Address2:</td><td><s:textfield name="empAddress2" /><span class="hint">Employee current address. (optional)<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
			<tr><td colspan=2><s:fielderror ><s:param>city</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Suburb<sup>*</sup>:</td><td><s:textfield name="city" cssClass="validateRequired text-input" cssStyle="height: 25px; width: 206px;" required="true"/><span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td colspan=2><s:fielderror ><s:param>country</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Select Country<sup>*</sup>:</td><td><s:select id="country" cssClass="validateRequired"
						name="country" listKey="countryId" 	listValue="countryName" headerKey="" headerValue="Select Country"
						onchange="getContentAcs('stateTD','GetStates.action?country='+this.value);"
					list="countryList" key="" required="true" /><span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span></td></tr>
					
			<tr><td colspan=2><s:fielderror ><s:param>state</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Select State<sup>*</sup>:</td><td id="stateTD"><s:select theme="simple" title="state" cssClass="validateRequired"
					id="state" name="state" listKey="stateId" listValue="stateName" headerKey="" headerValue="Select State"		
					list="stateList" key="" required="true" /><span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
			</td></tr>
	<!-- ===start parvez date: 30-07-2022=== -->		
			<%-- <tr><td class="txtlabel alignRight">Postcode:</td><td><s:textfield name="empPincode" cssStyle="height: 25px; width: 206px;" label="Employee Pincode" /><span class="hint">Employee's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
			<tr><td class="txtlabel alignRight">Pincode:</td><td><s:textfield name="empPincode" cssStyle="height: 25px; width: 206px;" label="Employee Pincode" /><span class="hint">Employee's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td></tr>
	<!-- ===end parvez date: 30-07-2022=== -->




			<tr><td colspan=2 style="border-bottom:1px solid #346897">Temporary Address:
			<div style="float:right;">
			<input type="checkbox" onclick="copyAddress(this);" />Same as above</div>
			</td></tr>
			
			<tr><td class="txtlabel alignRight" valign="top"> Address<sup>*</sup>:</td><td><s:textarea name="empAddress1Tmp" cssClass="validateRequired text-input" cssStyle="width: 206px; height: 108px;" required="true"></s:textarea><span class="hint">Employee current address.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<%-- <tr><td class="txtlabel alignRight">Address2:</td><td><s:textfield name="empAddress2Tmp" /><span class="hint">Employee current address. (optional)<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
			<tr><td class="txtlabel alignRight">Suburb<sup>*</sup>:</td><td><s:textfield name="cityTmp" cssClass="validateRequired text-input" cssStyle="height: 25px; width: 206px;" required="true"/><span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Select Country<sup>*</sup>:</td><td><s:select id="countryTmp" cssClass="validateRequired"
						name="countryTmp" listKey="countryId" 	listValue="countryName" headerKey="" headerValue="Select Country"
						onchange="getContentAcs('stateTD1','GetStates.action?country='+this.value);"
					list="countryList" key="" required="true" /><span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Select State<sup>*</sup>:</td><td id="stateTD1"><s:select theme="simple" title="state" cssClass="validateRequired"
					id="stateTmp" name="stateTmp" listKey="stateId" listValue="stateName" headerKey="" headerValue="Select State"		
					list="stateList" key="" required="true" /><span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
			</td></tr>
		<!-- ===start parvez date: 30-07-2022=== -->	
			<%-- <tr><td class="txtlabel alignRight">Postcode:</td><td><s:textfield name="empPincodeTmp" cssStyle="height: 25px; width: 206px;" label="Employee Pincode" /><span class="hint">Employee's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
			<tr><td class="txtlabel alignRight">Pincode:</td><td><s:textfield name="empPincodeTmp" cssStyle="height: 25px; width: 206px;" label="Employee Pincode" /><span class="hint">Employee's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<!-- ===end parvez date: 30-07-2022=== -->	


			<tr><td colspan=2 style="border-bottom:1px solid #346897">Personal Information:</td></tr>

			<tr><td class="txtlabel alignRight">Landline Number<sup>*</sup>:</td><td><s:textfield name="empContactno" cssClass="validateRequired text-input" cssStyle="height: 25px; width: 206px;" /><span class="hint">Employee's contact no. (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Mobile Number<sup>*</sup>:</td><td><s:textfield name="empMobileNo" cssClass="validateRequired text-input" cssStyle="height: 25px; width: 206px;" /><span class="hint">Employee's Mobile No<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td class="txtlabel alignRight">Emergency Contact Name<sup>*</sup>:</td><td><s:textfield cssClass="validateRequired text-input" name="empEmergencyContactName" cssStyle="height: 25px; width: 206px;" />
			<tr><td colspan=2><s:fielderror ><s:param>empEmergencyContactNo</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Emergency Contact Number<sup>*</sup>:</td><td><s:textfield name="empEmergencyContactNo" cssClass="validateRequired text-input" cssStyle="height: 25px; width: 206px;"/></td></tr>
			
			<tr><td class="txtlabel alignRight">Doctor's Name:</td><td><s:textfield name="empDoctorName" />
			<tr><td colspan=2><s:fielderror ><s:param>empDoctorNo</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Doctor's Contact Number:</td><td><s:textfield name="empDoctorNo" /></td></tr>
			
			<tr><td class="txtlabel alignRight">UAN No:</td><td><s:textfield name="empUANNo"/><span class="hint">Employee's UAN (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">UID No:</td><td><s:textfield name="empUIDNo"/><span class="hint">Employee's UID (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">PAN No:</td><td><s:textfield name="empPanNo" cssStyle="height: 25px; width: 206px;"/><span class="hint">Employee's PAN (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Provident Fund No :</td><td><s:textfield name="empPFNo" cssStyle="height: 25px; width: 206px;"/><span class="hint">Employee's Provident Number (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">GPF Acc No :</td><td><s:textfield name="empGPFNo" cssStyle="height: 25px; width: 206px;"/><span class="hint">Employee's GPF Number <span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			
			<tr><td class="txtlabel alignRight">Passport Number:</td><td><s:textfield name="empPassportNo" cssStyle="height: 25px; width: 206px;"/>
			<tr><td class="txtlabel alignRight">Passport Expiry Date:</td><td><s:textfield name="empPassportExpiryDate" cssStyle="height: 25px; width: 206px;" />
			<tr><td class="txtlabel alignRight">Blood Group:</td><td><s:select theme="simple" name="empBloodGroup" listKey="bloodGroupId"
					listValue="bloodGroupName" headerKey="0" headerValue="Select Blood Group"		
					list="bloodGroupList" key="" required="true" /></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empDateOfBirth</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Date Of Birth<sup>*</sup>:</td><td>
			<s:textfield name="empDateOfBirth" id="empDateOfBirth" cssClass="validateRequired text-input" cssStyle="height: 25px; width: 206px;" required="true"></s:textfield>
			<span class="hint">Employee's Date Of Birth.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empGender</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Gender<sup>*</sup>:</td><td><s:select theme="simple" cssClass="validateRequired" label="Select Gender" name="empGender" listKey="genderId"
					listValue="genderName" headerKey="" headerValue="Select Gender"		
					list="empGenderList" key="" required="true" /><span class="hint">Select Gender.<span class="hint-pointer">&nbsp;</span></span>
			</td></tr>
			
			<tr><td class="txtlabel alignRight">Marital Status:</td><td><s:select theme="simple" name="empMaritalStatus" listKey="maritalStatusId"
					listValue="maritalStatusName" headerKey="0" headerValue="Select Marital Status"		
					list="maritalStatusList" key="" required="true" onchange="showMarriageDate();"/></td></tr>
			
			
			<tr id="trMarriageDate"><td class="txtlabel alignRight">Date Of Marriage<sup></sup>:</td><td>
			<s:textfield name="empDateOfMarriage" id="empDateOfMarriage" cssStyle="height: 25px; width: 206px;"></s:textfield>
			<span class="hint">Employee's Date Of Marriage.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			
			</table>
			</div>
            
			<div style="float:left; padding:10px; border:solid 5px #CCCCCC;  margin: 80px 100px; padding: 10px 30px;" >
				<table>
				<tr><td><strong>Update employee image</strong></td></tr>
				<tr><td><img height="100" width="100" id="profilecontainerimg" src="userImages/<%=strImage!=null? strImage:"avatar_photo.png"%>" /></td> </tr>
			     <tr><td><s:file name="empImage" ></s:file></td></tr>
				</table>
			
			</div>
			
			<div class="clr"></div>
			
			<div style="float:right;">
				<table>
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:hidden name="mode" />
						<s:submit cssClass="input_button" value="Update Information" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
			
		</s:form>
		</div>
	</s:if>
	
	<s:if test="step==5 || mode=='report'">
	<div>
	<s:form theme="simple" action="AddOnboardingFromCandidate" id="frmReferences" method="POST" cssClass="formcss" enctype="multipart/form-data">
		<table border="0" class="formcss">
			
			<tr><td>
			</td></tr>
			<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 5 : </span>Enter Employee References 1:</td></tr>
			
			<s:hidden name="empId" />
			<s:hidden name="step" />
			
			<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield name="ref1Name" cssStyle="height: 25px; width: 206px;"/></td></tr>
			<tr><td class="txtlabel alignRight">Company:</td><td><s:textfield name="ref1Company" cssStyle="height: 25px; width: 206px;"/></td></tr>
			<tr><td class="txtlabel alignRight">Designation:</td><td><s:textfield name="ref1Designation" cssStyle="height: 25px; width: 206px;"/></td></tr>
			<tr><td class="txtlabel alignRight">Contact No:</td><td><s:textfield name="ref1ContactNo" cssStyle="height: 25px; width: 206px;"/></td></tr>
			<tr><td class="txtlabel alignRight">Email Id:</td><td><s:textfield name="ref1Email" cssStyle="height: 25px; width: 206px;"/></td></tr>
			
		</table>
		
		<table border="0" class="formcss">
			
			<tr><td>
			</td></tr>
			<tr><td class="tdLabelheadingBg alignCenter" colspan="2">Enter Employee References 2:</td></tr>
			
			<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield name="ref2Name" cssStyle="height: 25px; width: 206px;"/></td></tr>
			<tr><td class="txtlabel alignRight">Company:</td><td><s:textfield name="ref2Company" cssStyle="height: 25px; width: 206px;"/></td></tr>
			<tr><td class="txtlabel alignRight">Designation:</td><td><s:textfield name="ref2Designation" cssStyle="height: 25px; width: 206px;"/></td></tr>
			<tr><td class="txtlabel alignRight">Contact No:</td><td><s:textfield name="ref2ContactNo" cssStyle="height: 25px; width: 206px;"/></td></tr>
			<tr><td class="txtlabel alignRight">Email Id:</td><td><s:textfield name="ref2Email" cssStyle="height: 25px; width: 206px;"/></td></tr>
			
		</table>
		
		<div class="clr"></div>
		
		<div style="float:right;">
			<table>
			
			<s:if test="mode==null">
			
				<tr><td colspan="2" align="center">
					<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
				</td></tr>
				
			</s:if>
			
			<s:else>
				
				<tr><td colspan="2" align="center">
					<s:hidden name="mode" />
					<s:submit cssClass="input_button" value="Update Information" align="center" />
				</td></tr>
				
			</s:else>
				
			</table>
		</div>
		
	</s:form>
	</div>
	</s:if>
	
	
<g:compress>	
<script>
$( "#idEffectiveDate" ).datepicker({dateFormat: 'dd/mm/yy'});
var kracnt=0;
function addKRA() {
	
	kracnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_kra"+kracnt;
	divTag.innerHTML = 	"<div style=\"float:left\">"+
						"<textarea rows=\"1\" cols=\"100\" style=\"width:80%\" name=\"empKra\"></textarea>"+
    			    	"<a href=\"javascript:void(0)\" onclick=\"addKRA()\" class=\"add\" style=\"float:right\">Add</a>" +
    			    	"<a href=\"javascript:void(0)\" onclick=\"removeKRA(this.id)\" id=\""+kracnt+"\" class=\"remove\">Remove</a>"+
    			    	"</div>"+
    			    	"<div class=\"clr\"/>"; 
    document.getElementById("div_kras").appendChild(divTag);
    
}

function removeKRA(removeId) {
	
	var remove_elem = "row_kra"+removeId;
	var row_kra = document.getElementById(remove_elem); 
	document.getElementById("div_kras").removeChild(row_kra);
	
}


function showHideBankName(){
	var obj = document.getElementById("idEmpPaymentMode");
	if(obj.options[obj.selectedIndex].value==1){
		document.getElementById("idBankName").style.display="table-row";
		document.getElementById("idEmpAccount").style.display="table-row";
	}else{
		document.getElementById("idBankName").style.display="none";
		document.getElementById("idEmpAccount").style.display="none";
	}
	
}

function getDataFromAjax(val){	
	getWlocation(val);
}

function getWlocation(val){
 	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetOrgWLocationList.action?OID='+val,
				cache : false,
				success : function(data) {
					document.getElementById('idOrgId').innerHTML=data;
					getdepartment(val);
				}
			});
		}
   }
function getdepartment(val){
	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetOrgDepartmentList.action?OID='+val,
				cache : false,
				success : function(data) {
					document.getElementById('idDepartment').innerHTML=data;
					getLevel(val);
				}
			});
		}
  }
function getLevel(val){
	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetOrgLevelList.action?OID='+val,
				cache : false,
				success : function(data) {
					document.getElementById('idLevel').innerHTML=data;
					getService(val);
					
				}
			});
		}
 }
 
function getDesig(val){
	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetDesigList.action?strLevel='+val,
				cache : false,
				success : function(data) {
					document.getElementById('myDesig').innerHTML=data;
					getGrades(val);
				}
			});
		}
}

function getGrades(val){
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var xhr = $.ajax({
			url : 'GetGradeList.action?DId='+val,
			cache : false,
			success : function(data) {
				document.getElementById('myGrade').innerHTML=data;					
			}
		});
	}
}

function getService(val){
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var xhr = $.ajax({
			url : 'GetOrgServiceList.action?OID='+val,
			cache : false,
			success : function(data) {
				document.getElementById('idService').innerHTML=data;
				getleaveType(val);
			}
		});
	}
}
function getDesigAndLeave(val) {
	var strOrg = document.getElementById("strOrg").value; 
	getContentAcs('myDesig','GetDesigList.action?strLevel='+val)
	window.setTimeout(function() {
		getleaveTypeByLevel(val);
	}, 400);
}	
function getleaveType(val){
	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetLeaveType.action?type=O&orgId='+val,
				cache : false,
				success : function(data) {
					document.getElementById('leaveProbationListID').innerHTML=data;
					getHRList(val);
				}
			});
		}
}
function getleaveTypeByLevel(val){
	var levelId = document.getElementById("levelIdV").value;
	var wLocationId = document.getElementById("wLocation").value;
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var xhr = $.ajax({
			url : 'GetLeaveType.action?orgId='+val+'&wLocationId='+wLocationId+'&levelId='+levelId,
			cache : false,
			success : function(data) {
				document.getElementById('leaveProbationListID').innerHTML=data;
				getHRList(val);
			}
		});
	}
}
function getHRList(val){
	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetEmployeeList.action?type=HR&f_org='+val,
				cache : false,
				success : function(data) {
					document.getElementById('hrListID').innerHTML=data;
					
				}
			});
		}
}
</script>
</g:compress>

	<%if(strEmpType!=null && !strEmpType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
	
<s:if test="step==8 || mode=='report'">

	<div>
	
	<s:form theme="simple" name="frmOfficial" action="AddOnboardingFromCandidate" id="frmOfficialInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
	
		<table border="0" class="formcss" style="float:left">
		
		<tr><td>
		
		</td></tr>
		<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 8 : </span>Enter Employee Official Information</td></tr>
		
		<s:hidden name="empId" />
		<s:hidden name="step" />
		<tr><td colspan=2><s:fielderror ><s:param>empCodeAlphabet</s:param></s:fielderror></td></tr>
		<tr><td colspan=2><s:fielderror ><s:param>empCodeNumber</s:param></s:fielderror></td></tr>
		
		<tr><td class="txtlabel alignRight">Employee Code<sup>*</sup>:</td><td>
		
		<s:hidden name="autoGenerate"/>
		
		<s:if test="autoGenerate==true">
			 <s:textfield name="empCodeAlphabet"  required="true" cssStyle="height: 25px; width: 90px;" disabled="true"/>
				<s:hidden name="empCodeAlphabet"/>
			<s:textfield name="empCodeNumber" required="true" cssStyle="height: 25px; width: 90px;" disabled="true" />
				<s:hidden name="empCodeNumber"/>
		</s:if>
		<s:else>
		
 			<s:textfield name="empCodeAlphabet" id="empCodeAlphabet" onchange="checkCodeValidation()" cssStyle="height: 25px; width: 90px;" cssClass="text-input" readonly="true"/>
 		<s:textfield name="empCodeNumber" id="empCodeNumber" onchange="checkCodeValidation()" cssStyle="height: 25px; width: 90px;" cssClass="validateRequired text-input" required="true" />
			<div id="empCodeMessege"></div>
		</s:else>
		
		<span class="hint">Employee Code represents the employee in a company. Code can be in any format e.g. KT001, E01, etc<span class="hint-pointer">&nbsp;</span></span>
		
		<tr><td colspan=2><s:fielderror ><s:param>empStartDate</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Joining Date<sup>*</sup>:</td><td><s:textfield name="empStartDate" id="empStartDate" cssClass="validateRequired text-input"  required="true"></s:textfield><span class="hint">Employee's date of joining.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<%-- </td></tr>
		<tr><td colspan=2><s:fielderror ><s:param>userName</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">User Name<sup>*</sup>:</td><td><s:textfield name="userName" required="true" onkeyup="javascript:show_userValidation();return false;" id="usrname"/><span class="hint">Username is required for an employee to login into the system.<span class="hint-pointer">&nbsp;</span></span>
			
			</td></tr>
		<tr><td colspan=2><s:fielderror ><s:param>empPassword</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Password<sup>*</sup>:</td><td><s:password showPassword="true" name="empPassword" cssClass="validateRequired text-input" required="true" /><span class="hint">Password is used to login securely.<span class="hint-pointer">&nbsp;</span></span><s:hidden name="empUserTypeId"/></td></tr> --%>
		
			<tr><td colspan=2><s:fielderror ><s:param>empType</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Select Employment Type<sup>*</sup>:</td><td>
				<s:select label="Select Employment Type" name="empType" cssClass="validateRequired" 
				listKey="empTypeId" listValue="empTypeName" headerKey="" headerValue="Select Emp Type" list="empTypeList" key=""
				required="true" onchange="validateMandatory(this.options[this.options.selectedIndex].value)"/><span class="hint">Employment type as part time or full time. It will be used while calculating payroll.<span class="hint-pointer">&nbsp;</span></span></td></tr>
				
		<tr><td colspan=2><s:fielderror ><s:param>orgId</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Select Organisation<sup>*</sup>:</td><td>
				<s:select label="Select Organisation" name="orgId" id="strOrg" cssClass="validateRequired"
				listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation" list="orgList" key=""
				 onchange="getDataFromAjax(this.value)"/>
				</td></tr>		
				
		<tr><td colspan=2><s:fielderror ><s:param>wLocation</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Select Work Location<sup>*</sup>:</td><td id="idOrgId">
				<s:select label="Select Work Location" name="wLocation" cssClass="validateRequired"
				listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="Select Location" list="wLocationList" key=""
				onchange="javascript:show_department();return false;" required="true" />
				<span class="hint">Employee's work location.<span class="hint-pointer">&nbsp;</span></span></td></tr>
				
		<tr><td colspan=2><s:fielderror ><s:param>department</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Select Department<sup>*</sup>:</td>
		<%-- <td><s:url id="department_url" action="GetDepartment" /> <sx:div href="%{department_url}" listenTopics="show_department" formId="frm_emp" showLoadingText=""></sx:div></td></tr> --%>
		<td id="idDepartment"><s:select theme="simple" label="Select Department" name="department" listKey="deptId" cssClass="validateRequired"
				listValue="deptName" headerKey="" headerValue="Select Department"
				list="deptList" key="" required="true" /></td>
				
		<tr><td colspan=2></td></tr>		
			<tr><td class="txtlabel alignRight">Level:<sup>*</sup></td><td id="idLevel">
			
			<s:select name="strLevel" list="levelList" listKey="levelId" id="levelIdV" listValue="levelCodeName" headerKey=""
				headerValue="Select Level" required="true" onchange="getDesigAndLeave(this.value);"></s:select></td>		
				
		<tr><td colspan=2></td></tr>		
			<tr><td class="txtlabel alignRight">Designation:<sup id="desigId">*</sup></td><td>
			<div id="myDesig">
			<s:select name="strDesignation" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName" headerKey=""
				headerValue="Select Designation" required="true" onchange="getGrades(this.options[this.selectedIndex].value)"></s:select></div></td>		
				
		<tr><td colspan=2><s:fielderror ><s:param>empGrade</s:param></s:fielderror></td></tr>		
			<tr><td class="txtlabel alignRight">Grade:<sup id="gradeId">*</sup></td><td>
			<div id="myGrade">
			<s:select name="empGrade" cssClass="validateRequired" list="gradeList" 
				listKey="gradeId" listValue="gradeCode" headerKey=""  id="gradeIdV"
				headerValue="Select Grade" required="true"></s:select></div></td>
				
		<%-- <tr><td colspan=2><s:fielderror ><s:param>supervisor</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Supervisor<sup>*</sup>:</td><td><s:select label="Supervisor" name="supervisor" listKey="employeeId"
				listValue="employeeCode" headerKey="" cssClass="validateRequired" headerValue="Select Supervisor"
				list="supervisorList" key="" required="true" /><span class="hint">Employee's manager/superior as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
		<tr><td colspan=2><s:fielderror ><s:param>Manager</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Manager<sup>*</sup>:</td><td><s:select label="Supervisor" name="supervisor" listKey="employeeId"
				listValue="employeeCode" headerKey="" cssClass="validateRequired" headerValue="Select Manager"
				list="supervisorList" key="" required="true" /><span class="hint">Employee's manager/superior as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		
		<tr><td class="txtlabel alignRight">H.O.D.<sup>*</sup>:</td><td><s:select label="Supervisor" name="hod" listKey="employeeId"
				listValue="employeeCode" headerKey="" cssClass="validateRequired" headerValue="Select H.O.D."
				list="HodList" key="" required="true" /><span class="hint">Employee's H.O.D. as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		
		 <tr><td colspan=2><s:fielderror ><s:param>HR</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">HR<sup>*</sup>:</td><td id="hrListID"><s:select label="Supervisor" name="HR" listKey="employeeId"
				listValue="employeeCode" headerKey="" cssClass="validateRequired" headerValue="Select HR"
				list="HRList" key="" required="true" /><span class="hint">Employee's HR as he/she will also get updates about the team.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		 
		
		<tr><td colspan=2><s:fielderror ><s:param>service</s:param></s:fielderror></td></tr>		 
		<tr><td class="txtlabel alignRight" valign="top">SBU<sup>*</sup>:</td><td class="" id="idService">
				<s:select label="Cost Centre" name="service" listKey="serviceId" cssClass="validateRequired"
							headerKey="" headerValue="Select SBU"
				listValue="serviceName" multiple="true" size="3" list="serviceList" key="" required="true" />
				<span class="hint">The SBU where the employee is supposed to work. This field will be used while calculating roster.<span class="hint-pointer">&nbsp;</span></span>
				<br/>Press ctrl for multiple selections</td></tr>
		
		<tr><td colspan=2><s:fielderror ><s:param>rosterDependency</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Time validation required?<sup>*</sup>:</td><td>
				<s:select label="Roster Dependency" name="rosterDependency" cssClass="validateRequired"
				listKey="approvalId" listValue="approvalName" headerKey=""
				headerValue="Select Dependency" list="rosterDependencyList" key=""
				required="true" /><span class="hint">Do you want this employee be dependent on roster entries?<span class="hint-pointer">&nbsp;</span></span></td></tr>
				
		<tr><td class="txtlabel alignRight">Is Payroll dependent on Attendance?<sup>*</sup>:</td><td>
				<s:select name="attendanceDependency" cssClass="validateRequired"
				listKey="approvalId" listValue="approvalName" headerKey=""
				headerValue="Select Dependency" list="rosterDependencyList" key=""
				required="true" /><span class="hint">Do you want this employee be dependent on roster entries?<span class="hint-pointer">&nbsp;</span></span></td></tr>
		
		<%-- <tr><td class="txtlabel alignRight">Select Paycycle Duration<sup>*</sup>:</td><td>
				<s:select theme="simple" name="strPaycycleDuration" listKey="paycycleDurationId" 
	             listValue="paycycleDurationName"  
	             list="paycycleDurationList" key="" /><span class="hint">Choose the paycycle duration. e.g., Weekly, Fortnightly, Monthly, etc. <span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
		
		
		<%-- <tr><td colspan=2><s:fielderror ><s:param>isFirstAidAllowance</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">First Aid Allowance:</td><td><s:checkbox cssStyle="width:20px" cssClass="tdLabel" 
		name="isFirstAidAllowance" label="First Aid Allowance" />
		<span class="hint">This field is used to calculate the deduction amount. Income from is the lower slab.<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
		
		<tr><td colspan=2><s:fielderror ><s:param>probationDuration</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight">Probation Period<sup>*</sup>:</td><td><s:select name="probationDuration"
				listKey="probationDurationID" listValue="probationDurationName" headerKey="" cssClass="validateRequired"
				headerValue="Select Probation Period" list="probationDurationList" key=""
				required="true" /> <span class="hint">This field is used for the Probation Period of Employee.<span class="hint-pointer">&nbsp;</span></span></td></tr>
				
		<tr><td colspan=2><s:fielderror ><s:param>probationLeaves</s:param></s:fielderror></td></tr>
		<tr><td class="txtlabel alignRight" >Leaves During Probation:</td><td id="leaveProbationListID">
				<s:select name="probationLeaves" multiple="true" size="3" headerKey="" 
				headerValue="Select Leave Types" listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key=""
				required="true" /> <span class="hint">This field specifies Leaves Allowed During Probation Period of Employee.<span class="hint-pointer">&nbsp;</span></span></td></tr>
	
		<tr><td class="txtlabel alignRight">Notice Period<sup>*</sup>:</td><td>
				<s:select name="noticeDuration"
				listKey="noticeDurationID" listValue="noticeDurationName" headerKey="" cssClass="validateRequired"
				headerValue="Select Probation Period" list="noticeDurationList" key=""
				required="true" /> <span class="hint">This field is used for the Probation Period of Employee.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			
			<tr><td class="txtlabel alignRight">Corporate Email Id:</td><td><s:textfield name="empEmailSec"  id="empEmailSec" cssStyle="height: 25px; width: 206px;" label="Employee Secondary Email Id"  onchange="emailValidation1('corporateemailValidatorMessege','empEmailSec',this.value,'EmailValidation.action?cemail='+this.value);" /><span class="hint">Employee's Secondary Email Id<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			<tr><td></td><td><div id="corporateemailValidatorMessege"></div></td></tr>
			<tr><td class="txtlabel alignRight">Skype Id:</td><td><s:textfield name="skypeId" cssStyle="height: 25px; width: 206px;" label="Employee Skype Id" /><span class="hint">Employee's Skype Id<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<%-- <tr><td class="txtlabel alignRight">Bank Name:</td><td><s:textfield name="empBankName" label="Employee Bank Name" /><span class="hint">Employee's Bank Name<span class="hint-pointer">&nbsp;</span></span></td></tr> --%>
	
			<tr><td class="txtlabel alignRight">Payment Mode:<sup>*</sup></td><td>
			<s:select id="idEmpPaymentMode" name="empPaymentMode" listKey="payModeId" listValue="payModeName" cssClass="validateRequired" headerKey="" headerValue="Select Payment Mode" list="paymentModeList" onchange="showHideBankName();"/>
			</td></tr>
	
			
			<tr id="idBankName"><td class="txtlabel alignRight">Bank Name:</td><td>
			<s:select name="empBankName" listKey="bankId" listValue="bankName" headerValue="Select Bank" list="bankList"/>
			<span class="hint">Employee's Bank Name<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr id="idEmpAccount"><td class="txtlabel alignRight">Employee Bank Account Number:</td><td><s:textfield name="empBankAcctNbr" cssStyle="height: 25px; width: 206px;" label="Employee Bank Account Number" /><span class="hint">Employee's Bank Account no.<span class="hint-pointer">&nbsp;</span></span></td></tr>
				
		</table>
		
		
		<%if(request.getParameter("operation")==null){ %>
		<table style="float: right;width:40%;margin-top:20px;">
		    <tbody>
		    <tr><td class="pagetitle">Add KRAs</td></tr>
			<tr><td class="pagetitle">&nbsp;</td></tr>
		    <tr>
		        <td>  
		        <div id="div_kras" style="float:left">
			        <div id="row_kra0" style="float:left">
						<textarea id="row_kra0" rows="1" cols="100" style="width:80%" name="empKra"></textarea>
						<a href="javascript:void(0)" onclick="addKRA()" class="add" style="float:right">Add</a>
					</div>
				</div>
		        </td>
		    </tr>
		</tbody></table>

		<%} %>

		<div class="clr"></div>
		<div style="float:right;">
		<table>
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:hidden name="mode" />
						<s:submit cssClass="input_button" value="Update Information" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
	
	</s:form>
	
	</div>

</s:if>

<script>
showHideBankName();
</script>


<%}%>


<s:if test="step==2 || mode=='report'">
<div>
	<s:form theme="simple" action="AddOnboardingFromCandidate" id="frmBackgroundInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
	
	<s:hidden name="empId" />
	<s:hidden name="step" />
    
    <div><span style="color:#68AC3B; font-size:18px">Step 2 : </span><span class="tdLabelheadingBg">Enter employees background information</span> </div>
    
	<div  id="div_skills">
    
        <h3 style="padding:0px;margin:5px 0px 10px 0px">Enter employee skills and their values</h3>
            
           <div id="row_skill" >  
			<div style="float:left; width:200px;margin-left:50px"><label>Skill Name</label></div>
			<div style="float:left; width:200px;"><label>Skill Rating</label></div>
		   </div>	
			<% 	
				if(alSkills!=null && alSkills.size()!=0){
					String empId = (String)((ArrayList)alSkills.get(0)).get(3);
				
			%>
				<%-- <input type="hidden" name="empId" value="<%=empId%>" /> --%>
			<% 
			 	for(int i=0; alSkills != null && i<alSkills.size(); i++) {
			%>
				
				<div id="row_skill<%=i%>" class="row_skill">
					
                    <table>
					<tr>
						<td>
						<%if(i==0){ %>
							[PRI]
						<%}else{ %>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<%} %>
							<select name="skillName">
			                	<%for(int k=0; skillsList!= null && k< skillsList.size(); k++) { 
			                		if( (((FillSkills)skillsList.get(k)).getSkillsId()+"").equals( (String)((ArrayList)alSkills.get(i)).get(0) )) {
			                	%>
			                		<option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>" selected="selected">
			                		<%=((FillSkills)skillsList.get(k)).getSkillsName() %>
			                		</option>
			                	<%}else { %>
			                		<option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>">
			                			<%=((FillSkills)skillsList.get(k)).getSkillsName() %>
			                		</option>
			                	<% }
			                	}%>
		                	</select>
						<%-- <input type="text" style="width: 180px;"name="skillName" value="<%=((ArrayList)alSkills.get(i)).get(1)%>" ></input> --%>
						</td>
						<td>
							<select name="skillValue">
			                	<%for(int k=1; k< 11; k++) { 
			                		if( (k+"").equals(((String)((ArrayList)alSkills.get(i)).get(2)))) {
			                	%>
			                		<option value="<%=k%>" selected="selected">
			                			<%=k%>
			                		</option>
			                	<%}else {%>
			                		<option value="<%=k%>">
			                			<%=k%>
			                		</option>
			                	<% }
			                	}%>
		                	</select>
			    	    <%-- <input type="text" style="width: 180px;"name="skillValue" value="<%=((ArrayList)alSkills.get(i)).get(2)%>"></input> --%>
			    	    </td>
			    	    <td><a href="javascript:void(0)" onclick="addSkills()" class="add">Add</a></td>
			            <%if(i>0) { %>
			            <td><a href="javascript:void(0)" onclick="removeSkills(this.id)" id=<%=i%> class="remove" >Remove</a></td>
			            <% } %>
                     </tr>   
			    	</table>
			    </div>
			 <%}
			 }else {
			 %>
			 	<div id="row_skill" class="row_skill">
                    <table>
                        <tr>
                        	<td>
                        	[Pri]
	                        	<select name="skillName">
	                        		<option value="">Select skill Name</option>
				                	<%for(int k=0; skillsList != null && k< skillsList.size(); k++) { %> 
				                		
				                		<option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>">
				                			<%=((FillSkills)skillsList.get(k)).getSkillsName() %>
				                		</option>
				                	<%}%>
			                	</select>
                        	</td>
                        	<td>
	                        	<select name="skillValue">
	                        		<option value="">Skill Rating</option>
				                	<%for(int k=1; k< 11; k++) {%>
				                		<option value="<%=k%>">
				                			<%=k%>
				                		</option>
			                		<%}%>
			                	</select>
                        	</td>
                            <!-- <td><input type="text" style="width: 180px;"name="skillName" ></input></td>
                            <td><input type="text" style="width: 180px;"name="skillValue"></input></td> -->
                            <td><a href="javascript:void(0)" onclick="addSkills()" class="add">Add</a></td>
                        </tr>   
                    </table>    
				</div>
			    	
			 <%}%>
			 
	</div>
	
	<div id="div_education">
	
       <h3 style="padding:0px;margin:5px 0px 10px 0px">Enter employee educational details</h3>
        
		<div id="row_education" class="row_education">  
                <table style="font-size: 12px">
				<tr><td width="120px"><label>Degree Name</label></td>
				    <td width="120px"><label>Duration</label></td>
				    <td width="120px"><label>Completion Year</label></td>
				    <td width="120px"><label>Grade</label></td>
                </tr> 
                </table>   
		</div>
		<% 	
			if(alEducation!=null && alEducation.size()!=0){
		 	for(int i=0; i<alEducation.size(); i++) {
		 		List<String> innerList=alEducation.get(i);
		%>
		
				<div id="row_education<%=i%>" class="row_education">
				
                    <table>    
					<tr>
					<td>
					<%-- <input type="text" style="width: 110px;" name="degreeName" value="<%=innerList.get(1)%>" ></input> --%>
					<select name="degreeName" style="width: 120px;">
	                	<%for(int k=0; k< educationalList.size(); k++) { 
	                		if( (((FillEducational)educationalList.get(k)).getEduId()+"").equals( (String)((ArrayList)alEducation.get(i)).get(0) )) {
	                	%>
	                		<option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" selected="selected">
	                		<%=((FillEducational)educationalList.get(k)).getEduName() %>
	                		</option>
	                	<%}else { %>
	                		<option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" >
	                		<%=((FillEducational)educationalList.get(k)).getEduName() %>
	                		</option>
	                	<% }
	                		}%>
	                	</select>
					</td>
	                <td>
	                	<select name="degreeDuration" style="width: 110px;">
	                	<option value=0>Duration</option>
	                	<%for(int k=0; k< degreeDurationList.size(); k++) { 
	                		FillDegreeDuration fillDegreeDuration=degreeDurationList.get(k);
	                		if( (fillDegreeDuration.getDegreeDurationID()+"").equals( innerList.get(2) )) {
	                	%>
	                		<option value="<%=fillDegreeDuration.getDegreeDurationID() %>" selected="selected">
	                		<%=fillDegreeDuration.getDegreeDurationName() %>
	                		</option>
	                	<%}else { %>
	                		<option value="<%=fillDegreeDuration.getDegreeDurationID() %>">
	                		<%=fillDegreeDuration.getDegreeDurationName() %>
	                		</option>
	                	<% }
	                	}%>
	                	</select>
	                </td>
	                
	                <td>
	                	<select name="completionYear" style="width: 110px;">
	                	<option value="">Completion Year</option>
	                	<%for(int k=0; k< yearsList.size(); k++) { 
	                		FillYears fillYears=yearsList.get(k);
	                		if( (fillYears.getYearsID()+"").equals( innerList.get(3) )) {
	                	%>
	                		<option value="<%=fillYears.getYearsID() %>" selected="selected">
	                		<%=fillYears.getYearsName() %>
	                		</option>
	                	<%}else { %>
	                		<option value="<%=fillYears.getYearsID() %>">
	                		<%=fillYears.getYearsName() %>
	                		</option>
	                	<% }
	                	}%>
	                	</select>
	                </td>
	                
	                <td><input type="text" style="width: 110px;" name="grade" value="<%=innerList.get(4)%>" ></input></td>
					<td><a href="javascript:void(0)" onclick="addEducation()" class="add">Add</a></td>
					<%if(i>0){ %>
					<td><a href="javascript:void(0)" onclick="removeEducation(this.id)" id=<%=i%> class="remove" >Remove</a></td>
					<% } %>
                    </tr>
                    </table>
				</div>
		<%}
		 	
		}else { %>
		
			<div id="row_education" class="row_education">
                <table>
				<tr>
				<td>
				<!-- <input type="text" style="width: 110px;" name="degreeName"></input> -->
				<select name="degreeName"	style="width:110px;"  onchange="checkEducation(this.value,0);" >
					<option value="">Degree</option>
						<%for(int k=0; k< educationalList.size(); k++) {%> 
					<option value="<%=((FillEducational)educationalList.get(k)).getEduId() %>" >
	                		<%=((FillEducational)educationalList.get(k)).getEduName() %>
	                		</option>
	                	<%} %>
	                	<option value="other">Other</option> 
	                	</select>
				</td>
					<td>
					<s:select name="degreeDuration"	cssStyle="width: 110px;" listKey="degreeDurationID" listValue="degreeDurationName" headerKey="-1"
						headerValue="Duration" list="degreeDurationList" key=""
						required="true" />
					</td>
                    
                    <td>
					<s:select name="completionYear"	cssStyle="width: 110px;" listKey="yearsID" listValue="yearsName" headerKey="-1"
						headerValue="Completion Year" list="yearsList" key=""
						required="true" />
					</td>
                    
                    <td><input type="text" style="width: 110px;" name="grade"></input></td>
				    <td><a href="javascript:void(0)" onclick="addEducation()" class="add">Add</a></td>
                </tr> 
                <tr id="degreeNameOtherTR0" style="display:none;">
                <td style="text-align:right;">Enter Education :</td>
				<td colspan="3"> 
                  <input type="text" name="degreeNameOther" style="height: 25px;">
                </td>
                </tr>   
				</table>
			</div>
		
		
		<%} %>
</div>
	
	<div id="div_language">
	
	<h3 style="padding:0px;margin:5px 0px 10px 0px">Enter employee languages</h3>
            
           <div id="row_language" class="row_language">  
                <table class="table table-head-highlight"> 
	                <tbody id="table-language">         
					  <tr>
						  <th width="190px"><label>Language Name</label></th>
						  <th width="50px" align="center"><label>Read</label></th>
						  <th width="50px" align="center"><label>Write</label></th>
						  <th width="50px" align="center"><label>Speak</label></th>
						  <th style="width: 60px;">Mother Tongue</th>
					  </tr>
						<% 	
							if(alLanguages!=null && alLanguages.size()!=0){
						 	for(int i=0; i<alLanguages.size(); i++) {
						%>
						<tr>
							<td><input type="text" style="height: 25px; width: 180px;" name="languageName" value="<%=((ArrayList)alLanguages.get(i)).get(1)%>" ></input></td>
		                    <td width="50px" align="center" >
		                    <%  if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(2)) ) { %>
		                    	<input type="checkbox" name="isReadcheckbox" value="1" id="isRead_<%=i%>" checked="checked" onchange="showHideHiddenField(this.id)" />
		                    	<input type="hidden" name="isRead" value="1" id="hidden_isRead_<%=i%>" />
		                    <% } else { %>
		                    	<input type="checkbox" name="isReadcheckbox" value="0" id="isRead_<%=i%>" onchange="showHideHiddenField(this.id)" />
		                    	<input type="hidden" name="isRead" value="0" id="hidden_isRead_<%=i%>" />
		                    <% } %>
		                    </td>
		                    
		                     <td width="50px" align="center" id="td_isWrite_<%=i%>">
		                    <% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(3)) ) { %>
		                    	<input type="checkbox" name="isWritecheckbox" value="1" checked="checked" id="isWrite_<%=i%>" onchange="showHideHiddenField(this.id)"	/>
		                    	<input type="hidden" name="isWrite" value="1" id="hidden_isWrite_<%=i%>" />
		                    <% } else { %>
		                    	<input type="checkbox" name="isWritecheckbox" value="0" id="isWrite_<%=i%>" onchange="showHideHiddenField(this.id)"	/>
		                    	<input type="hidden" name="isWrite" value="0" id="hidden_isWrite_<%=i%>" />
		                    <%} %>
		                    </td>
		                    
		                     <td width="50px" align="center" id="td_isSpeak_<%=i%>">
		                    <% if(uF.parseToBoolean((String)((ArrayList)alLanguages.get(i)).get(4)) ) { %>
		                    	<input type="checkbox" name="isSpeakcheckbox" value="1" checked="checked" id="isSpeak_<%=i%>" onchange="showHideHiddenField(this.id)"	/>
		                    	<input type="hidden" name="isSpeak" value="1" id="hidden_isSpeak_<%=i%>" />
		                    <% } else { %>
		                    	<input type="checkbox" name="isSpeakcheckbox" value="0" id="isSpeak_<%=i%>" onchange="showHideHiddenField(this.id)"	/>
		                    	<input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_<%=i%>" />
		                    <% } %>
		                    </td>
		                    
							<td align="center" id="td_isMotherTounge_<%=i%>">
		                    <% if(uF.parseToBoolean((String)((ArrayList)alLanguages.get(i)).get(5)) ) { %>
		                    	<input type="radio" name="isMotherToungeRadio" value="1" checked="checked" id="isMotherTounge_" onchange="showHideHiddenField1(this.id, '<%=i%>')"	/>
		                    	<input type="hidden" name="isMotherTounge" value="1" id="hidden_isMotherTounge_<%=i%>" />
		                    <%} else { %>
		                    	<input type="radio" name="isMotherToungeRadio" value="0" id="isMotherTounge_" onchange="showHideHiddenField1(this.id, '<%=i%>')"	/>
		                    	<input type="hidden" name="isMotherTounge" value="0" id="hidden_isMotherTounge_<%=i%>" /> 	
		                    <%} %>
		                    </td>
		                    
					    	<td>
						    	<%if(i==0) { %>
						    		<input type="hidden" name="hideLanguageRowCount" id="hideLanguageRowCount" value="<%=alLanguages.size() %>" />
						    	<% } %>
						    	<a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a>
						    	<%if(i>0) { %>
						        	<a href="javascript:void(0)" onclick="removeLanguages(this.id)" id="<%=i%>" class="remove-font" ></a>
						        <% } %>
					        </td>
				        </tr>
						 <% }
						 } else {
						 %>
					 	<tr>
						 	<td><input type="hidden" name="hideLanguageRowCount" id="hideLanguageRowCount" value="1" /><input type="text" style="height: 25px; width: 180px;" name="languageName" ></input></td>
						 	<td width="50px" align="center"><input type="checkbox" id="isRead_0" name="isReadcheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
						 		<input type="hidden" name="isRead" value="0" id="hidden_isRead_0" /></td>
							<td width="50px" align="center"><input type="checkbox" id="isWrite_0" name="isWritecheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
								<input type="hidden" name="isWrite" value="0" id="hidden_isWrite_0" /></td>
				            <td width="50px" align="center"><input type="checkbox" id="isSpeak_0" name="isSpeakcheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
				            	<input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_0" /></td>
				            <td width="50px" align="center"><input type="radio" id="isMotherTounge_" name="isMotherToungeRadio" value="1" onclick="showHideHiddenField1(this.id, '0')"/>
								<input type="hidden" name="isMotherTounge" value="0" id="hidden_isMotherTounge_0" /></td>	
					    	<td><a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a></td>
					    </tr>
			 			<% } %>
				 	</tbody>
				 </table>
			</div>
		</div>
		
	
	<div id="div_hobbies">
	
	<h3 style="padding:0px;margin:5px 0px 10px 0px">Enter employee hobbies</h3>
            
           <div id="row_hobby" class="row_hobby">  
			<div style="float:left; width:200px;"><label>Hobby Name</label></div>
		   </div>	
			<% 	
				if(alHobbies!=null && alHobbies.size()!=0) {
					String empId = (String)((ArrayList)alHobbies.get(0)).get(2);
			%>
			<% for(int i=0; i<alHobbies.size(); i++) { %>
				<div id="row_hobby<%=((ArrayList)alHobbies.get(i)).get(0)%>" class="row_hobby">
					<table>
                        <tr><td><input type="text" style="height: 25px; width: 180px;" name="hobbyName" value="<%=((ArrayList)alHobbies.get(i)).get(1)%>" ></input></td> 
                            <td><a href="javascript:void(0)" onclick="addHobbies()" class="add">Add</a></td>
                            <%if(i>0) { %>
                            <td><a href="javascript:void(0)" onclick="removeHobbies(this.id)" id=<%=((ArrayList)alHobbies.get(i)).get(0)%> class="remove" >Remove</a></td>
                            <% } %>
                        </tr>    
			    	</table>
			    </div>
			    
			 <%}
			 } else {
			 %>
				<div id="row_hobby" class="row_hobby">
                    <table>
						<tr><td><input type="text" style="height: 25px; width: 180px;"name="hobbyName" ></input></td>
							<td><a href="javascript:void(0)" onclick="addHobbies()" class="add">Add</a></td>
						</tr>    
					</table>  
				</div>
			 <% } %>
			</div>
	
	
	<div class="clr"></div>
	<div style="float:right;">
				<table>
				<s:if test="mode==null">
					<tr><td colspan="2" align="center">
						<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
				</s:if>
				
				<s:else>
					<tr><td colspan="2" align="center">
						<s:hidden name="mode" />
						<s:submit cssClass="input_button" value="Update Information" align="center" />
					</td></tr>
				</s:else>
				</table>
			</div>
	</s:form>
</div>
</s:if>


<s:if test="step==3 || mode=='report'">

<div>	<!-- Family Information -->
<s:form theme="simple" action="AddOnboardingFromCandidate" id="frmFamilyInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
	
	<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" >
	
	<s:hidden name="empId" />
	<s:hidden name="step" />
	<table border="0" class="formcss">
	<tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 3 : </span>
    <span class="tdLabelheadingBg">Enter Employees Family Information </span></td></tr>
	</table>
    
    <table>
      <tr><td>
		<table>	
		       <tr><td  style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Father's Information </td></tr>
			 	<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield cssStyle="height: 25px; width: 207px;" name="fatherName" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Date of birth:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="fatherDob" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Education:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="fatherEducation" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Occupation:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="fatherOccupation" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Contact Number:</td><td><s:textfield cssStyle="height: 25px; width: 207px;" name="fatherContactNumber" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Email Id:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="fatherEmailId" ></s:textfield></td></tr>
				<tr><td colspan="2">&nbsp;</td></tr>
	   </table>     
		</td>
       
        <td>
       <table>	 	 
		   <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Mother's Information </td></tr>
		 	<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield cssStyle="height: 25px; width: 207px;" name="motherName" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Date of birth:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="motherDob" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Education:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="motherEducation" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Occupation:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="motherOccupation" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Contact Number:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="motherContactNumber" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Email Id:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="motherEmailId" ></s:textfield></td></tr>
			<tr><td colspan="2">&nbsp;</td></tr>
		</table>     
		</td>
        <td>&nbsp;</td>
        <td>
       <table>	 	 
		   	<tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Spouse's Information </td></tr>
		 	<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield cssStyle="height: 25px; width: 207px;" name="spouseName" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Date of birth:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="spouseDob" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Education:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="spouseEducation" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Occupation:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="spouseOccupation" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Contact Number:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="spouseContactNumber" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Email Id:</td><td> <s:textfield cssStyle="height: 25px; width: 207px;" name="spouseEmailId" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Gender:</td>
			<td><s:select theme="simple" label="Select Gender" name="spouseGender" listKey="genderId"
					listValue="genderName" headerKey="0" headerValue="Select Gender"		
					list="empGenderList" key="" required="true" />
			</td>
		</table>
		</td></tr>	 
    </table>    
             </div>
             <div id="div_id_family">
             
			<%	if(alSiblings!=null && alSiblings.size()!=0) {
					for(int i=0; i<alSiblings.size(); i++) {%>
			
			<div id="col_family_siblings<%=i%>" style="float:left; border:solid 0px #ccc;width:100%" >
			                      
               <table> 
                  <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td></tr>    
                
			 	<tr><td class="txtlabel alignRight">Name:</td><td><input type="text" style="height: 25px; width: 207px;" name="memberName" value="<%=((ArrayList)alSiblings.get(i)).get(1)%>" ></input></td></tr>    
				<tr><td class="txtlabel alignRight">Date of birth:</td><td> <input type="text" style="height: 25px; width: 207px;" name="memberDob" value="<%=((ArrayList)alSiblings.get(i)).get(2)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Education: </td><td><input type="text" style="height: 25px; width: 207px;" name="memberEducation" value="<%=((ArrayList)alSiblings.get(i)).get(3)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Occupation:</td><td> <input type="text" style="height: 25px; width: 207px;" name="memberOccupation" value="<%=((ArrayList)alSiblings.get(i)).get(4)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Contact Number:</td><td> <input type="text" style="height: 25px; width: 207px;" name="memberContactNumber" value="<%=((ArrayList)alSiblings.get(i)).get(5)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Email Id:</td><td> <input type="text" style="height: 25px; width: 207px;" name="memberEmailId" value="<%=((ArrayList)alSiblings.get(i)).get(6)%>"></input></td></tr>    
				
				<tr><td class="txtlabel alignRight">Gender:</td>
				
				<%-- <input type="text" style="width: 180px;" name="memberGender" value="<%=((ArrayList)alSiblings.get(i)).get(7)%>"></input></td> --%>
					<td>
	                	<select name="memberGender" >
		                	<%for(int k=0; k< empGenderList.size(); k++) { 
		                		if( (((FillGender)empGenderList.get(k)).getGenderId()+"").equals( (String)((ArrayList)alSiblings.get(i)).get(7) )) {
		                	%>
		                		<option value="<%=((FillGender)empGenderList.get(k)).getGenderId() %>" selected="selected">
		                		<%=((FillGender)empGenderList.get(k)).getGenderName() %>
		                		</option>
		                	<%}else { %>
		                		<option value="<%=((FillGender)empGenderList.get(k)).getGenderId() %>">
		                		<%=((FillGender)empGenderList.get(k)).getGenderName() %>
		                		</option>
		                	<% }
		                	}%>
	                	</select>
		            </td>
		        </tr>
		        <tr><td class="txtlabel alignRight">Marital Status:</td>
				  <td>
				  <select name="siblingMaritalStatus" >
		                	<%for(int k=0; k< maritalStatusList.size(); k++) { 
		                		if( (((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusId()+"").equals( (String)((ArrayList)alSiblings.get(i)).get(7) )) {
		                	%>
		                		<option value="<%=((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusId() %>" selected="selected">
		                		<%=((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusName() %>
		                		</option>
		                	<%}else { %>
		                		<option value="<%=((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusId() %>">
		                		<%=((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusName() %>
		                		</option>
		                	<% }
		                	}%>
	                	</select>
				  
				 </td></tr>
				  
		    	<!-- <tr><td class="txtlabel alignRight"><a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add">&nbsp;</a></td> -->   
		    	<tr><td class="txtlabel alignRight" colspan="2">
		    	<a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add">&nbsp;</a>
		    	<a href="javascript:void(0)" onclick="removeSibling(this.id)" id=<%=i%> class="remove" >Remove</a>
		    	</td></tr>    
                    
               </table>     
			</div>
			
			<%}
			}else {%>
			
			<div id="col_family_siblings" style="float: left;border:solid 0px #f00;width:100%" >
			   
               <table> 
                <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td></tr>        
			 	<tr><td class="txtlabel alignRight">Name:</td><td><input type="text" style="height: 25px; width: 207px;" name="memberName" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Date of birth:</td><td> <input type="text" style="height: 25px; width: 207px;" name="memberDob" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Education:</td><td> <input type="text" style="height: 25px; width: 207px;" name="memberEducation" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Occupation:</td><td><input type="text" style="height: 25px; width: 207px;" name="memberOccupation" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Contact Number:</td><td> <input type="text" style="height: 25px; width: 207px;" name="memberContactNumber" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Email Id:</td><td> <input type="text" style="height: 25px; width: 207px;" name="memberEmailId" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Gender:</td><td>
					<s:select theme="simple" label="Select Gender" name="memberGender" listKey="genderId"
						listValue="genderName" headerKey="0" headerValue="Select Gender"		
						list="empGenderList" key="" required="true" />
					</td>
				</tr>
				 <tr><td class="txtlabel alignRight">Marital Status:</td>
				  <td>
				   <s:select theme="simple" name="siblingMaritalStatus" listKey="maritalStatusId"
					listValue="maritalStatusName" headerKey="0" headerValue="Select Marital Status"		
					list="maritalStatusList" key="" required="true" /></td></tr>
		    	<tr><td class="txtlabel alignRight" colspan="2"><a href="javascript:void(0)" onclick="addSibling()" class="add" style="float:right">&nbsp;</a></td></tr>
		       </table>
            	
			</div>
			
			<%}%>
			
			<!-- <div class="clr"></div> -->
			</div>
			<div class="clr"></div>
			<div id="div_id_child">
             
			<%	if(alchilds!=null && alchilds.size()!=0) {
					for(int i=0; i<alchilds.size(); i++) {%>
			
			<div id="col_family_child<%=i%>" style="float:left; border:solid 0px #ccc;width:100%" >
			                      
               <table> 
                  <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Children's Information </td></tr>    
                
			 	<tr><td class="txtlabel alignRight">Name:</td><td><input type="text" style="height: 25px; width: 207px;" name="childName" value="<%=((ArrayList)alchilds.get(i)).get(1)%>" ></input></td></tr>    
				<tr><td class="txtlabel alignRight">Date of birth:</td><td> <input type="text" style="height: 25px; width: 207px;" name="childDob" value="<%=((ArrayList)alchilds.get(i)).get(2)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Education: </td><td><input type="text" style="height: 25px; width: 207px;" name="childEducation" value="<%=((ArrayList)alchilds.get(i)).get(3)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Occupation:</td><td> <input type="text" style="height: 25px; width: 207px;" name="childOccupation" value="<%=((ArrayList)alchilds.get(i)).get(4)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Contact Number:</td><td> <input type="text" style="height: 25px; width: 207px;" name="childContactNumber" value="<%=((ArrayList)alchilds.get(i)).get(5)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Email Id:</td><td> <input type="text" style="height: 25px; width: 207px;" name="childEmailId" value="<%=((ArrayList)alchilds.get(i)).get(6)%>"></input></td></tr>    
				
				<tr><td class="txtlabel alignRight">Gender:</td>
				
				<%-- <input type="text" style="width: 180px;" name="memberGender" value="<%=((ArrayList)alSiblings.get(i)).get(7)%>"></input></td> --%>
					<td>
	                	<select name="childGender" >
		                	<%for(int k=0; k< empGenderList.size(); k++) { 
		                		if( (((FillGender)empGenderList.get(k)).getGenderId()+"").equals( (String)((ArrayList)alchilds.get(i)).get(7) )) {
		                	%>
		                		<option value="<%=((FillGender)empGenderList.get(k)).getGenderId() %>" selected="selected">
		                		<%=((FillGender)empGenderList.get(k)).getGenderName() %>
		                		</option>
		                	<%}else { %>
		                		<option value="<%=((FillGender)empGenderList.get(k)).getGenderId() %>">
		                		<%=((FillGender)empGenderList.get(k)).getGenderName() %>
		                		</option>
		                	<% }
		                	}%>
	                	</select>
		            </td>
		        </tr>
				  <tr><td class="txtlabel alignRight">Marital Status:</td>
				  <td>
				  <select name="childMaritalStatus" >
		                	<%for(int k=0; k< maritalStatusList.size(); k++) { 
		                		if( (((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusId()+"").equals( (String)((ArrayList)alSiblings.get(i)).get(7) )) {
		                	%>
		                		<option value="<%=((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusId() %>" selected="selected">
		                		<%=((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusName() %>
		                		</option>
		                	<%}else { %>
		                		<option value="<%=((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusId() %>">
		                		<%=((FillMaritalStatus)maritalStatusList.get(k)).getMaritalStatusName() %>
		                		</option>
		                	<% }
		                	}%>
	                	</select>
				  
				  
				 </td></tr>
					
		    	<!-- <tr><td class="txtlabel alignRight"><a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add">&nbsp;</a></td> -->   
		    	<tr><td class="txtlabel alignRight" colspan="2">
		    	<a href="javascript:void(0)" onclick="addChildren()"  style="float:right" class="add">&nbsp;</a>
		    	<a href="javascript:void(0)" onclick="removeChildren(this.id)" id=<%=i%> class="remove" >Remove</a>
		    	</td></tr>    
                    
               </table>     
			</div>
			
			<%}
			}else {%>
			
			<div id="col_family_child" style="float: left;border:solid 0px #f00;width:100%" >
			   
               <table> 
                <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Child's Information </td></tr>        
			 	<tr><td class="txtlabel alignRight">Name:</td><td><input type="text" style="height: 25px; width: 207px;" name="childName" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Date of birth:</td><td> <input type="text" style="height: 25px; width: 207px;" name="childDob" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Education:</td><td> <input type="text" style="height: 25px; width: 207px;" name="childEducation" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Occupation:</td><td><input type="text" style="height: 25px; width: 207px;" name="childOccupation" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Contact Number:</td><td> <input type="text" style="height: 25px; width: 207px;" name="childContactNumber" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Email Id:</td><td> <input type="text" style="height: 25px; width: 207px;" name="childEmailId" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Gender:</td><td>
					<s:select theme="simple" label="Select Gender" name="childGender" listKey="genderId"
						listValue="genderName" headerKey="0" headerValue="Select Gender"		
						list="empGenderList" key="" required="true" />
					</td>
				</tr>
				 <tr><td class="txtlabel alignRight">Marital Status:</td>
				  <td>
				   <s:select theme="simple" name="childMaritalStatus" listKey="maritalStatusId"
					listValue="maritalStatusName" headerKey="0" headerValue="Select Marital Status"		
					list="maritalStatusList" key="" required="true" /></td></tr>
		    	<tr><td class="txtlabel alignRight" colspan="2"><a href="javascript:void(0)" onclick="addChildren()" class="add" style="float:right">&nbsp;</a></td></tr>
		       </table>
            	
			</div>
			
			<%}%>
			
			<!-- <div class="clr"></div> -->
			</div>
			<div>
			<div style="float:right;">
				<table>
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:hidden name="mode" />
						<s:submit cssClass="input_button" value="Update Information" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
</div> 
</s:form> 

</div>
</s:if>

<s:if test="step==4 || mode=='report'">
<div>	<!-- Previous Employment -->

<s:form theme="simple" action="AddOnboardingFromCandidate" id="frmPrevEmployment" method="POST" cssClass="formcss" enctype="multipart/form-data">
<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
	
	<s:hidden name="empId" />
	<s:hidden name="step" />
	<table border="0" class="formcss">

<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 4 : </span>Enter Employees Previous Employment </td></tr>
	</table>			
		
		 <%	if(alPrevEmployment!=null && alPrevEmployment.size()!=0) {
			 
				for(int i=0; i<alPrevEmployment.size(); i++) {%>
					
			<div id="col_prev_employer<%=i%>" style="float: left;">
			
             <table>
			 	<tr><td class="txtlabel alignRight">Company Name:</td><td><input type="text" name="prevCompanyName" style="height: 25px; width: 207px;" name="prevCompanyLocation" value="<%=((ArrayList)alPrevEmployment.get(i)).get(1)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Location:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyLocation" value="<%=((ArrayList)alPrevEmployment.get(i)).get(2)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">City:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyCity" value="<%=((ArrayList)alPrevEmployment.get(i)).get(3)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">State:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyState" value="<%=((ArrayList)alPrevEmployment.get(i)).get(4)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Country:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyCountry" value="<%=((ArrayList)alPrevEmployment.get(i)).get(5)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Contact Number:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyContactNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(6)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Reporting To:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyReportingTo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(7)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Reporting Manager Phone Number:</td><td> <input type="text" style="height:25px; width: 207px;" name="prevCompanyReportManagerPhNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(8)%>"></input></td></tr>
				<tr><td class="txtlabel alignRight">HR Manager:</td><td> <input type="text" style="height:25px; width: 207px;" name="prevCompanyHRManager" value="<%=((ArrayList)alPrevEmployment.get(i)).get(9)%>"></input></td></tr>
				<tr><td class="txtlabel alignRight">HR Manager Phone Number:</td><td> <input type="text" style="height:25px; width: 207px;" name="prevCompanyHRManagerPhNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(10)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">From: </td><td><input type="text" style="height: 25px; width: 207px;" name="prevCompanyFromDate" value="<%=((ArrayList)alPrevEmployment.get(i)).get(11)%>" onchange="setcompanyTodd(this.value)"></input></td></tr> 
				<tr><td class="txtlabel alignRight">To:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyToDate" value="<%=((ArrayList)alPrevEmployment.get(i)).get(12)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Designation:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyDesination" value="<%=((ArrayList)alPrevEmployment.get(i)).get(13)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Responsibility:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyResponsibilities" value="<%=((ArrayList)alPrevEmployment.get(i)).get(14)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Skills:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanySkills" value="<%=((ArrayList)alPrevEmployment.get(i)).get(15)%>"></input></td></tr>
		<!-- ===start parvez date: 08-08-2022=== -->
				<tr><td class="txtlabel alignRight">UAN No.:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyUANNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(16)%>"></input></td></tr>
				<tr><td class="txtlabel alignRight">ESIC No.:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyESICNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(17)%>"></input></td></tr>
				
		<!-- ===end parvez date: 08-08-2022=== -->		 
				
				<tr>
				<td style="margin:0px 5px 0px 0px">
				<a href="javascript:void(0)" onclick="addPrevEmployment()" class="add">&nbsp;</a>
				<a href="javascript:void(0)" onclick="removePrevEmployment(this.id)" id=<%=i%> >&nbsp;</a>
				</td></tr>
				
              </table> 
                
			</div>
			 
			 
		<%}
				
		 }else { %>
			
			<div id="col_prev_employer" style="float: left;" >
			 
              <table>
			 	<tr><td class="txtlabel alignRight"> Company Name:</td><td><input type="text" name="prevCompanyName" style="height: 25px; width: 207px;" name="prevCompanyLocation" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Location:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyLocation" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> City: </td><td><input type="text" style="height: 25px; width: 207px;" name="prevCompanyCity" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> State:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyState" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Country:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyCountry" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Contact Number:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyContactNo" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Reporting To:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyReportingTo" ></input></td></tr> 
				<tr><td class="txtlabel alignRight">Reporting Manager Phone Number:</td><td> <input type="text" style="height:25px; width: 207px;" name="prevCompanyReportManagerPhNo"></input></td></tr>
				<tr><td class="txtlabel alignRight">HR Manager:</td><td> <input type="text" style="height:25px; width: 207px;" name="prevCompanyHRManager"></input></td></tr>
				<tr><td class="txtlabel alignRight">HR Manager Phone Number:</td><td> <input type="text" style="height:25px; width: 207px;" name="prevCompanyHRManagerPhNo"></input></td></tr> 
				<tr><td class="txtlabel alignRight"> From:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyFromDate" onchange="setcompanyTodd(this.value)" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> To:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyToDate" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Designation:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyDesination" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Responsibility:</td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyResponsibilities" ></input>  </td></tr> 
				<tr><td class="txtlabel alignRight"> Skills: </td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanySkills" ></input></td></tr> 
			<!-- ===start parvez date: 08-08-2022=== -->
				<tr><td class="txtlabel alignRight"> UAN No.: </td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyUANNo" ></input></td></tr>
				<tr><td class="txtlabel alignRight"> ESIC No.: </td><td> <input type="text" style="height: 25px; width: 207px;" name="prevCompanyESICNo" ></input></td></tr>
			<!-- ===end parvez date: 08-08-2022=== -->
				
				<tr><td class="txtlabel alignRight" style="text-align:right" colspan="2"> <a href="javascript:void(0)" onclick="addPrevEmployment()" class="add">&nbsp;</a></td></tr> 
				
              </table>   
			</div>
			 
			
			<%}%>
			
</div>
<div class="clr"></div>
				<div style="float:right;">
				<table>
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:hidden name="mode" />
						<s:submit cssClass="input_button" value="Update Information" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
</s:form>  
</div>
</s:if>

<s:if test="step==6 || mode=='report'">
<div> <!-- Medical Information -->

<s:form theme="simple" action="AddOnboardingFromCandidate" id="frmMedicalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">

<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
	<s:hidden name="empId" />
	<s:hidden name="step" />
	
	<table border="0" class="formcss">
	    <tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 6 : </span>Medical Information :-</td></tr>
	</table>
	    
	<table border="0" class="formcss">
	
		<tr><td class="tdLabelheadingBg alignCenter" colspan="2">Enter Employee's Medical Information </td></tr>
		<tr>
			<td>
	            <table style="font-size: 12px">
				 	<tr><td class="txtlabel alignRight">Are you now receiving medical attention:</td>
				 		<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue1" onclick="checkRadio(this,'text1');"></s:radio>
				 			<s:hidden name="empMedicalId1" />
				 			<s:hidden name="que1Id" value="1"></s:hidden>
				 			<s:hidden name="que1IdFileStatus" id="que1IdFileStatus" value="0"></s:hidden>
				 		</td>
				 		
				 		<s:if test="checkQue1==true">
				 		<td><s:textarea  rows="7" cols="63" id="text1" name="que1Desc"  ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text1File" onchange="fillFileStatus('que1IdFileStatus')" /></td> 
				 		</s:if>
				 		<s:else>
				 		<td><s:textarea  rows="7" cols="63" id="text1" name="que1Desc" disabled="true" ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text1File" disabled="true" onchange="fillFileStatus('que1IdFileStatus')"/></td> 
				 		</s:else>
				 		
				 		 
				 	</tr> 
					<tr><td class="txtlabel alignRight">Have you had any form of serious illness or operation:</td>
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue2" onclick="checkRadio(this,'text2');"></s:radio>
							<s:hidden name="empMedicalId2" />
							<s:hidden name="que2Id" value="2"></s:hidden>
							<s:hidden name="que1IdFileStatus" id="que2IdFileStatus" value="0"></s:hidden>
						</td>
						
						<s:if test="checkQue2==true">
				 		<td><s:textarea  rows="7" cols="63" id="text2" name="que2Desc"  ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text2File" onchange="fillFileStatus('que2IdFileStatus')"/></td> 
				 		</s:if>
				 		<s:else>
				 		<td><s:textarea  rows="7" cols="63" id="text2" name="que2Desc" disabled="true" ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text2File" disabled="true" onchange="fillFileStatus('que2IdFileStatus')"/></td> 
				 		</s:else> 
					</tr> 
					<tr><td class="txtlabel alignRight">Have you had any illness in the last two years? YES/NO If YES, 
							please give the details about the same and any absences from work: </td>
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue3" onclick="checkRadio(this,'text3');"></s:radio>
							<s:hidden name="empMedicalId3" />
							<s:hidden name="que3Id" value="3"></s:hidden>
							<s:hidden name="que1IdFileStatus" id="que3IdFileStatus" value="0"></s:hidden>
						</td>
						
						<s:if test="checkQue3==true">
				 		<td><s:textarea  rows="7" cols="63" id="text3" name="que3Desc"  ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text3File" onchange="fillFileStatus('que3IdFileStatus')"/></td> 
				 		</s:if>
				 		<s:else>
				 		<td><s:textarea  rows="7" cols="63" id="text3" name="que3Desc" disabled="true" ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text3File" disabled="true"
														onchange="fillFileStatus('que3IdFileStatus')" />
												</td> 
				 		</s:else>
					</tr>
					
					<%-- <tr><td class="txtlabel alignRight">Has any previous post been terminated on medical grounds?</td>
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue4" onclick="checkRadio(this,'text4');"></s:radio>
							<s:hidden name="empMedicalId4" />
							<s:hidden name="que4Id" value="4"></s:hidden>
							<s:hidden name="que1IdFileStatus" id="que4IdFileStatus" value="0"></s:hidden>
						</td>
						<s:if test="checkQue4==true">
				 		<td><s:textarea  rows="7" cols="63" id="text4" name="que4Desc"  ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text4File" onchange="fillFileStatus('que4IdFileStatus')"/></td> 
				 		</s:if>
				 		<s:else>
				 		<td><s:textarea  rows="7" cols="63" id="text4" name="que4Desc" disabled="true" ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text4File" disabled="true" onchange="fillFileStatus('que4IdFileStatus')" /></td> 
				 		</s:else>
					</tr> --%>
					
					<%-- <tr><td class="txtlabel alignRight">Do you have an allergies?</td>
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue4" onclick="checkRadio(this,'text4');"></s:radio>
							<s:hidden name="empMedicalId4" />
							<s:hidden name="que4Id" value="4"></s:hidden>
							<s:hidden name="que1IdFileStatus" id="que4IdFileStatus" value="0"></s:hidden>
						</td>
						<s:if test="checkQue5==true">
				 		<td><s:textarea  rows="7" cols="63" id="text4" name="que4Desc"  ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text4File" onchange="fillFileStatus('que4IdFileStatus')"/></td> 
				 		</s:if>
				 		<s:else>
				 		<td><s:textarea  rows="7" cols="63" id="text4" name="que4Desc" disabled="true" ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text4File" disabled="true" onchange="fillFileStatus('que4IdFileStatus')"/></td> 
				 		</s:else>
					</tr> 
					
					<tr><td class="txtlabel alignRight">Do you have an allergies?</td>
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue5" onclick="checkRadio(this,'text5');"></s:radio>
							<s:hidden name="empMedicalId5" />
							<s:hidden name="que5Id" value="5"></s:hidden>
							<s:hidden name="que1IdFileStatus" id="que5IdFileStatus" value="0"></s:hidden>
						</td>
						<s:if test="checkQue5==true">
				 		<td><s:textarea  rows="7" cols="63" id="text5" name="que5Desc"  ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text5File" onchange="fillFileStatus('que5IdFileStatus')"/></td> 
				 		</s:if>
				 		<s:else>
				 		<td><s:textarea  rows="7" cols="63" id="text5" name="que5Desc" disabled="true" ></s:textarea></td>
				 		<td><s:file name="que1DescFile" id="text5File" disabled="true" onchange="fillFileStatus('que5IdFileStatus')"/></td> 
				 		</s:else>
					</tr>  --%>
				</table>
			</td> 
		</tr>
	
	</table>
	<div class="clr"></div>
	<div style="float:right;">
				<table>
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:hidden name="mode" />
						<s:submit cssClass="input_button" value="Update Information" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
		 
</div>
</s:form>	     

</div>
</s:if>

<s:if test="step==7 || mode=='report'">
<div>	

<form action="AddOnboardingFromCandidate.action" id="frmDocumentation" method="POST" class="formcss" enctype="multipart/form-data">
<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" >
	<s:hidden name="empId" />
	<s:hidden name="step" />
	
		<table border="0" class="formcss">
	    <tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 7 : </span>Attach Documents :-</td></tr>
	    </table>
				
                
				
				<% 	
					if(alDocuments!=null && alDocuments.size()!=0) {
						
						String empId = (String)((ArrayList)alDocuments.get(0)).get(3);
				%>
				<!-- <table>
                      
                </table> -->
					<%-- <input type="hidden" name="empId" value="<%=empId%>" /> --%>
					
					 <table style="width:70%" id="row_document_table">
                        <tr><!-- <td class="txtlabel alignRight"><label><b>Document Type</b></label></td> -->
                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Document Name</b></label></td>
                <td class="txtlabel alignRight" style="text-align: -moz-center" ><label><b>Attached Document</b></label></td>
                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Added By</b></label></td>
                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Entry Date</b></label></td>
                <!--<td><a href="javascript:void(0)" onclick="addDocuments()" class="add"><b>Add New Document..</b></a></td>-->
                </tr>  
               
				<% 
				 	for(int i=0; i<alDocuments.size(); i++) {
				%>
					<%-- <div id="row_document<%=((ArrayList)alDocuments.get(i)).get(0)%>" >
						
                       <table style="width:70%"> --%>
						  <tr>
						  		<%-- <td class="txtlabel alignRight"><%=((ArrayList)alDocuments.get(i)).get(1)%>:
						  			
						  		</td> --%>
						  		
						  		<td class="txtlabel alignRight" style="text-align: -moz-center"><input type="hidden" name="idDocType" value="<%=((ArrayList)alDocuments.get(i)).get(2)%>"></input>
						  			<input type="hidden" name="docId" value="<%=((ArrayList)alDocuments.get(i)).get(0)%>"></input>
						  			<%=((ArrayList)alDocuments.get(i)).get(1)%>
						  		</td>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center">
						  			<a href="<%=request.getContextPath()+"/userDocuments/"+((ArrayList)alDocuments.get(i)).get(4)%>" ><i class="fa fa-file-o" aria-hidden="true" title="click to download"></i></a>
						  		</td>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(6)%></td>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(5)%></td>
						  		
						  		<td class="txtlabel alignRight" style="text-align: -moz-center;width:125px;">
						  		<%if (struserType != null && (struserType.equalsIgnoreCase(IConstants.HRMANAGER))){ %>
						  		<div id="removeDivDocument_<%=((ArrayList)alDocuments.get(i)).get(0)%>"><a href="javascript:void(0)" onclick="deleteDocuments('<%=((ArrayList)alDocuments.get(i)).get(0)%>')" class="remove"></a></div>
						  		<%} %>
						  		<%if(i==alDocuments.size()-1){ %>
						  		<a href="javascript:void(0)" onclick="addDocuments()" class="add"></a>
						  		<%} %>
						  		</td>
                                <%-- <td class="txtlabel alignRight"><input disabled="disabled" type="text" style="width: 160px;" name="idDocName" class="validateRequired text-input" value="<%=((ArrayList)alDocuments.get(i)).get(1)%>" ></input></td> 
                                <td class="txtlabel alignRight"><input type="file" name="idDoc" value="<%=((ArrayList)alDocuments.get(i)).get(4)%>" disabled="disabled"/></td>
                                <td class="txtlabel alignRight"><!-- <a href="javascript:void(0)" onclick="addDocuments()" class="add"></a> --></td>
                                <td class="txtlabel alignRight"><a href="javascript:void(0)" onclick="removeDocuments(this.id)" id=<%=((ArrayList)alDocuments.get(i)).get(0)%> >Remove</a></td>
                                <td><a href="<%=request.getContextPath()+"/userDocuments/"+((ArrayList)alDocuments.get(i)).get(4)%>" >Download</a></td> --%>
                                
                          </tr>
                       <!--  </table>
                       
				    </div> -->
				    
				    
				 <%}%>
				 </table>
				 <%}else {
				 %>
				 
				 <div id="row_document">
                    <table>
                    <tr>
                <td class="txtlabel alignRight"><label><b>Document Name</b></label></td>
                <td class="txtlabel alignRight"><label><b>Attached Document</b></label></td>
                <!--<td><a href="javascript:void(0)" onclick="addDocuments()" class="add"><b>Add New Document..</b></a></td>-->
                </tr>  
				 		<tr>
				 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_RESUME%><sup>*</sup>:
				 		<input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_RESUME%>"></input>
				 		<input type="hidden" style="width: 180px;" value="<%=IConstants.DOCUMENT_RESUME%>" name="idDocName" ></input>
				 		<input type="hidden" name="idDocStatus" id="idDoc1Status" value="0"></input>
				 		
				 		</td>
				 		
						<td class="txtlabel alignRight"><input type="file" name="idDoc" onchange="fillFileStatus('idDoc1Status')"/></td>
                    
				 		<tr>
				 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ID_PROOF%><sup>*</sup>:
				 		<input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_ID_PROOF%>"></input>
				 		<input type="hidden" value="<%=IConstants.DOCUMENT_ID_PROOF%>" style="width: 180px;" name="idDocName" ></input>
				 		<input type="hidden" name="idDocStatus" id="idDoc2Status" value="0"></input>
				 		</td>
						<td class="txtlabel alignRight"><input type="file" name="idDoc" onchange="fillFileStatus('idDoc2Status')"/></td>
                   
				 		<tr>
				 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ADDRESS_PROOF%><sup>*</sup>:
				 		<input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>"></input>
				 		<input type="hidden" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>" style="width: 180px;" name="idDocName" ></input>
				 		<input type="hidden" name="idDocStatus" id="idDoc3Status" value="0"></input>
				 		</td>
						<td class="txtlabel alignRight"><input type="file" name="idDoc" onchange="fillFileStatus('idDoc3Status')"/></td>
			    		</tr>
			    	<!-- ===start parvez date: 28-10-2022=== -->	
			    		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_COMPANY_PROFILE_DOCUMENT))){ %>
			    			<tr>
						 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_COMPANY_PROFILE%><sup>*</sup>:
						 		<input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_COMPANY_PROFILE%>"></input>
						 		<input type="hidden" value="<%=IConstants.DOCUMENT_COMPANY_PROFILE%>" style="width: 180px;" name="idDocName" ></input>
						 		<input type="hidden" name="idDocStatus" id="idDoc4Status" value="0"></input>
						 		</td>
								<td class="txtlabel alignRight"><input type="file" name="idDoc" onchange="fillFileStatus('idDoc4Status')"/></td>
				    		</tr>
			    		<% } %>
			    	<!-- ===end parvez date: 28-10-2022=== -->	
			    		</table>
			    		</div>
			    		<div id="div_id_docs">
			    		<table>
			    		
			    		
                 <%--  <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2"><%=IConstants.DOCUMENT_OTHER%> </td></tr> 
                   <tr><td><%=IConstants.DOCUMENT_OTHER%></td></tr>
                    <tr>
                   
				 	<td class="txtlabel alignRight"><input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_OTHER%>"></input><input type="text" style="width: 180px;" name="idDocName" ></input></td>
					<td class="txtlabel alignRight"><input type="file" name="idDoc"/></td>
			    	<td><a href="javascript:void(0)" onclick="addDocuments()" class="add">Add</a></td></tr>  --%>
                    </table>
				</div>
				 
				 <%} %>
				 <div class="clr"></div>
				 <div style="float:right;">
				<table>
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:hidden name="mode" />
						<s:submit cssClass="input_button" value="Update Information" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
</div>				 
</form>


</div>
</s:if>

<s:if test="step==9 || mode=='report'">

	<div>
	
    <div><span style="color:#68AC3B; font-size:18px">Step 9 : </span><strong>Salary Information</strong></div>
	<s:if test="mode=='report' || mode=='profile' || mode=='onboard'">
		<s:action name="EmployeeSalaryDetails" executeResult="true">
			<s:param name="empId"><s:property value="empId"/> </s:param>
			<s:param name="CCID"><s:property value="serviceId"/></s:param>
			<s:param name="step"><s:property value="step"/></s:param>
			<s:param name="mode">E</s:param>
		</s:action>
	</s:if>
	
	<s:else>
		<s:action name="EmployeeSalaryDetails" executeResult="true">
			<s:param name="empId"><s:property value="empId"/> </s:param>
			<s:param name="CCID"><s:property value="serviceId"/></s:param>
			<s:param name="step"><s:property value="step"/></s:param>
			<s:param name="mode">A</s:param>
		</s:action>
	</s:else>
	
	</div>

</s:if>




<s:if test="step==11">

<script>
$(function() {
	$("input[name=strDate]").datepicker({dateFormat: 'dd/mm/yy'});
	$( "input[name=strTime]" ).timepicker({});
});
</script>

<form action="AddOnboardingFromCandidate.action" id="frmAvailibility" method="POST" class="formcss">
	<s:hidden name="empId" />
	<s:hidden name="step" />
	<s:hidden name="mode" />
	
	<table border="0" class="formcss" style="font-size: 12px">
	    <tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 8 : </span> Your Availability for interview:-</td></tr>
	    <tr><td colspan="3">Please enter your availability details. You will be scheduled for an interview based on any of the available time specified by you. </td></tr>
	    <tr><td colspan="3">Please ignore this step if you have alreay joined the organisation. </td></tr>
	</table>
	
<table style="width:500px">
	<tr>
		<th>&nbsp;</th>
		<th class="txtlabel alignRight">Date</th>
		<th class="txtlabel" align="left">Time</th>
	</tr>
	
	<tr>
		<td class="txtlabel alignRight">Option 1</td>
		<td align="right"><input type="text" name="strDate" style="height: 25px; width: 100px;"></td>
		<td><input type="text" name="strTime" style="height: 25px; width: 100px;"></td>
	</tr>
	
	<tr>
		<td class="txtlabel alignRight">Option 2</td>
		<td align="right"><input type="text" name="strDate" style="height: 25px; width: 100px;"></td>
		<td><input type="text" name="strTime" style="height: 25px; width: 100px;"></td>
	</tr>
	
	<tr>
		<td class="txtlabel alignRight">Option 3</td>
		<td align="right"><input type="text" name="strDate" style="height: 25px; width: 100px;"></td>
		<td><input type="text" name="strTime" style="height: 25px; width: 100px;"></td>
	</tr>
</table>

		<div style="float: right;">
				<table>
					<tr><td align="center" colspan="2">
					</td></tr><tr>
    				<td colspan="2"><div align="center"><input type="submit" style="width: 200px; float: right;" class="input_button" value="Submit &amp; Finish" id="">
					</div></td>
				</tr>
				</table>
		</div>

</form>

</s:if>

<p>All fields marked with <sup>*</sup> are mandatory.</p>

</div>




<script>
showMarriageDate();
validateMandatory(document.frmOfficial.empType.options[document.frmOfficial.empType.options.selectedIndex].value);
</script>

</div>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillGender"%>
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.select.FillYears"%>
<%@page import="com.konnect.jpms.select.FillDegreeDuration"%>
<%@page import="java.io.File"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.*" %>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

  

<!--Step1 => Personal
	Step2 => Background 
	Step3 => Family 
	Step4 => Prev Employment 
	Step5 => References 
	Step6 => Medical 
	Step7 => Documentaion 
	Step8 => Official  -->



<!-- tab pane styling -->
<style>
	
	/* tab pane styling */
	/* .panes div {
		display:none;		
		padding:15px 10px;
		border:1px solid #999;
		border-top:0;
		height:auto;
		font-size:14px;
		background-color:#fff;
	} */
	
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
	

	ArrayList alSkills = (ArrayList) request.getAttribute("alSkills"); 
	ArrayList alHobbies = (ArrayList) request.getAttribute("alHobbies");
	ArrayList alLanguages = (ArrayList) request.getAttribute("alLanguages");
	ArrayList alEducation = (ArrayList) request.getAttribute("alEducation");
	ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
	ArrayList alSiblings = (ArrayList) request.getAttribute("alSiblings");
	ArrayList alPrevEmployment = (ArrayList) request.getAttribute("alPrevEmployment");
	List degreeDurationList = (List) request.getAttribute("degreeDurationList");
	List yearsList = (List) request.getAttribute("yearsList");
	List empGenderList = (List) request.getAttribute("empGenderList");
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
	
%>

<g:compress>
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
		
		document.getElementById("desigIdV").className = 'validate[required]';
		document.getElementById("gradeIdV").className = 'validate[required]';
	}
}

function showState() {	
	dojo.event.topic.publish("showState");
}


function callDatePicker() {

<%-- 	$("input[name=memberDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
	$("input[name=prevCompanyFromDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=prevCompanyToDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true}); --%>
    
}

$(function() {
	
    $( "#empStartDate" ).datepicker({dateFormat: 'dd/mm/yy', yearRange: '1980:<%=currentYear%>', changeYear: true});
    $( "#empDateOfBirth" ).datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:2000', changeYear: true});
    $( "#empDateOfMarriage" ).datepicker({dateFormat: 'dd/mm/yy', yearRange: '1970:<%=currentYear%>', changeYear: true});
    $("input[name=prevCompanyFromDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=prevCompanyToDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=fatherDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=motherDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=spouseDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=memberDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=empPassportExpiryDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '<%=currentYear%>:2020', changeYear: true});
    
});

function showMarriageDate(){
	if(document.frmPersonalInfo.empMaritalStatus.options[document.frmPersonalInfo.empMaritalStatus.options.selectedIndex].value=='M'){
		document.getElementById("trMarriageDate").style.display = 'table-row';
	}else{
		document.getElementById("trMarriageDate").style.display = 'none';
	}
}

<% if (alSkills!=null) {%>
	var cnt=<%=alSkills.size()%>;
<%}else{%>
	var cnt =0;
<%}%>

function addSkills() {
	
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_skill"+cnt;
    divTag.setAttribute("class", "row_skill");
	divTag.innerHTML = 	"<%=request.getAttribute("sbSkills")%>" +
    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addSkills()\" class=\"add\">Add</a></td>" +
    			    	"<td><a href=\"javascript:void(0)\" onclick=\"removeSkills(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a></td>"; 
    document.getElementById("div_skills").appendChild(divTag);
    
}

function removeSkills(removeId) {
	
	var remove_elem = "row_skill"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_skills").removeChild(row_skill);
	
}

<% if (alHobbies!=null) {%>
var cnt=<%=alHobbies.size()%>;
<%}else{%>
var cnt =0;
<%}%>

function addHobbies() {
	
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_hobby"+cnt;
    divTag.setAttribute("class", "row_hobby");
	divTag.innerHTML = 	"<table>"+
	                    "<tr><td><input type=\"text\" style=\"width: 180px; \" name=\"hobbyName\"></input></td>" +   			    	
    			    	"<td><a href=\"javascript:void(0)\" onclick=\"addHobbies()\" class=\"add\">Add</a></td>" +
 						"<td><a href=\"javascript:void(0)\" onclick=\"removeHobbies(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a></td></tr>" +
	                    "</table>";
	                    
    document.getElementById("div_hobbies").appendChild(divTag);
    
}

function removeHobbies(removeId) {
	
	var remove_elem = "row_hobby"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_hobbies").removeChild(row_skill);
	
}


<% if (alLanguages!=null) {%>

var cnt=<%=alLanguages.size()%>;

<%}else{%>

var cnt =0;

<%}%>

function addLanguages() {
	
	cnt++;
	var divTag = document.createElement("div");
	
    divTag.id = "row_language"+cnt;
    divTag.setAttribute("class", "row_language");
	divTag.innerHTML = 	"<table>" +
	                    "<tr><td><input type=\"text\" style=\"width: 180px;\" name=\"languageName\" ></input></td>" + 
 						"<td width=\"50px\" align=\"center\"><input type=\"checkbox\" name=\"isRead\" value=\"1\" /></td>" +
						"<input type=\"hidden\" value=\"0\" name=\"isDisplay\">" +
						"<td width=\"50px\" align=\"center\"><input type=\"checkbox\" name=\"isWrite\" value=\"1\" /></td>"+
						"<input type=\"hidden\" value=\"0\" name=\"isWrite\">" +
						"<td width=\"50px\" align=\"center\"><input type=\"checkbox\" name=\"isSpeak\" value=\"1\" /></td>"+
						"<input type=\"hidden\" value=\"0\" name=\"isSpeak\">" +
						"<td width=\"50px\" align=\"center\"><a href=\"javascript:void(0)\" onclick=\"addLanguages()\" class=\"add\">Add</a></td>" +
						"<td><a href=\"javascript:void(0)\" onclick=\"removeLanguages(this.id)\" id=\""+cnt+"\" class=\"remove\" >Remove</a></td>" +
						"</table>"; 

    document.getElementById("div_language").appendChild(divTag);
    
}

function removeLanguages(removeId) {
	
	var remove_elem = "row_language"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_language").removeChild(row_skill);
	
}

<% if (alEducation!=null) {%>
	var cnt=<%=alEducation.size()%>;
<%}else{%>
	var cnt =0;
<%}%>

function addEducation() {
	
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_education"+cnt;
    divTag.setAttribute("class", "row_education");
	divTag.innerHTML = "<%=request.getAttribute("sbdegreeDuration")%>" +
			"<td><a href=\"javascript:void(0)\" onclick=\"removeEducation(this.id)\" id=\""+cnt+"\" class=\"remove\" >Remove</a></td></tr>" +
    		"</table>"; 
    document.getElementById("div_education").appendChild(divTag);
    
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


<% if (alDocuments!=null) {%>

var cnt=<%=alDocuments.size()%>;

<%}else{%>

var cnt = 0;

<%}%>

function addDocuments() {

cnt++;

var divTag = document.createElement("div");

divTag.id = "row_document"+cnt;

divTag.innerHTML = 	"<table>" +
					"<tr><td class=\"txtlabel alignRight\"><%=IConstants.DOCUMENT_OTHER%>" +
					"<input type=\"hidden\" name=\"idDocType\" value=\"<%=IConstants.DOCUMENT_OTHER%>\"></input></td>" +
                   	"<td class=\"txtlabel alignRight\"><input type=\"text\" class=\"validate[required] text-input\" style=\"width: 180px; \" name=\"idDocName\"></input></td>" +   			    	
			    	"<td class=\"txtlabel alignRight\"><input type=\"file\" name=\"idDoc\"/></td>"+
			    	"<td><a href=\"javascript:void(0)\" onclick=\"addDocuments()\" class=\"add\">Add</a></td>" +
			    	"<td><a href=\"javascript:void(0)\" onclick=\"removeDocuments(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a></td></tr>" +
			    	"</table>"; 

			    	
document.getElementById("div_id_docs").appendChild(divTag);

}

function removeDocuments(removeId) {

	var remove_elem = "row_document"+removeId;
	var row_document = document.getElementById(remove_elem); 
	document.getElementById("div_id_docs").removeChild(row_document);

}


<% if (alPrevEmployment!=null) {%>

	var cnt=<%=alPrevEmployment.size()%>;

<%}else{%>

	var cnt =0;

<%}%>

function addPrevEmployment() {

	cnt++;
	
	var divTag = document.createElement("div");
	divTag.id = "col_prev_employer"+cnt;
	divTag.innerHTML = "<%=request.getAttribute("sbPrevEmployment")%>" + 
					"<td class=\"txtlabel alignRight\"><a href=\"javascript:void(0)\" onclick=\"removePrevEmployment(this.id)\" id=\""+cnt+"\" class=\"remove\" >Remove..</td>";
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

function showHideHiddenField(fieldId, fieldName) {
	
	if(document.getElementById(fieldId).checked) {
		if(document.getElementById("hidden_"+fieldId)) {
			(document.getElementById(fieldId)).parentNode.removeChild(document.getElementById("hidden_"+fieldId));
		}
	
	}else {
		
		var inputTag = document.createElement("input");
		
		inputTag.id = "hidden_"+fieldId;
		inputTag.name = fieldName;
		inputTag.type = "hidden";
		inputTag.value = "0";
		(document.getElementById(fieldId)).parentNode.appendChild(inputTag);
	}
	
}

function checkRadio(obj,val){
	document.getElementById(val).disabled=true;
	if(obj.value=='false')
		document.getElementById(val).disabled=true;
	else
		document.getElementById(val).disabled=false;
		
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
		document.getElementById("frmPersonalInfo_empAddress2Tmp").value = document.getElementById("frmPersonalInfo_empAddress2").value;
		document.getElementById("frmPersonalInfo_cityTmp").value = document.getElementById("frmPersonalInfo_city").value;
		document.getElementById("frmPersonalInfo_empPincodeTmp").value = document.getElementById("frmPersonalInfo_empPincode").value;
		
		
		
	}else{
		document.getElementById("frmPersonalInfo_empAddress1Tmp").value = '';
		document.getElementById("frmPersonalInfo_empAddress2Tmp").value = '';
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

</g:compress>

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
</script>




<div class="pagetitle">
<%if(session.getAttribute(IConstants.USERID)!=null){ %>
      <span><%=(request.getParameter("operation")!=null)?"Edit":"Enter" %> Candidate Detail</span>
<%}else{ %>
	  <span>Enter your Details</span>
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


<%if(!"U".equalsIgnoreCase(request.getParameter("operation"))){ %>


<div class="steps">
<s:if test="step==1">
  <span class="current"> Personal Information :</span>
  <span class="next"> Background Information :</span>
  <span class="next"> Family Information :</span>
  <span class="next"> Previous Employment :</span>
   <span class="next"> References :</span>
   <span class="next"> Medical Information :</span>
   <span class="next"> Documentation :</span>
   <% if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>

   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<s:if test="step==2">
  <span class="prev">Personal Information : </span>
  <span class="current">Background Information : </span>
  <span class="next">Family Information : </span>
  <span class="next">Previous Employment: </span>
   <span class="next">References : </span>
   <span class="next">Medical Information : </span>
   <span class="next">Documentation : </span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>

   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<s:if test="step==3">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="current">Family Information : </span>
  <span class="next">Previous Employment: </span>
   <span class="next">References : </span>
   <span class="next">Medical Information : </span>
   <span class="next">Documentation : </span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>

   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<s:if test="step==4">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="current">Previous Employment: </span>
   <span class="next">References : </span>
   <span class="next">Medical Information : </span>
   <span class="next">Documentation : </span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>

   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<s:if test="step==5">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="current">References : </span>
   <span class="next">Medical Information : </span>
   <span class="next">Documentation : </span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>

   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<s:if test="step==6">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="prev">References : </span>
   <span class="current">Medical Information : </span>
   <span class="next">Documentation : </span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
 
   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<s:if test="step==7">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="prev">References : </span>
   <span class="prev">Medical Information : </span>
   <span class="current">Documentation : </span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>

   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
<%-- <s:if test="step==8">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="prev">References : </span>
   <span class="prev">Medical Information : </span>
   <span class="prev">Documentation : </span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
   <span class="current"> Official Information :</span>
   <span class="next1"> Salary Information :</span>
   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if> --%>
<%-- 
<s:if test="step==9">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="prev">References : </span>
   <span class="prev">Medical Information : </span>
   <span class="prev">Documentation : </span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
   	<span class="prev1"> Official Information :</span>
   	<span class="current"> Salary Information :</span>
   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>
 --%>


<s:if test="step==11">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="prev">References : </span>
   <span class="prev">Medical Information : </span>
   <span class="prev">Documentation : </span>
   <%if(strEmpType!=null && !strEmpType.equals(IConstants.EMPLOYEE)) {%>
  
   <%}else{%>
   <span class="next1"> Availablity :</span>
   <%}%>
</s:if>

</div>

<%} %>

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
	
<%-- 	<s:if test="step==8 || mode=='report'">
		<li><a href="#tab3">Official Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
	</s:if> --%>
	
	<%-- <s:if test="step==9 || mode=='report'">
		
		<li><a href="#">Salary Information for <s:property value="serviceName" /> </a></li>
		<li><a href="#">Salary Structure Information of <%=uF.showData((String)request.getAttribute("strEmpName"), "") %></a></li>
		
	</s:if>
 --%>	
	<%-- <s:if test="step==11">
		
		<li><a href="#">Salary Information for <s:property value="serviceName" /> </a></li>
		<li><a href="#">Availability</a></li>
		
	</s:if> --%>
</ul>

<!-- tab "panes" -->
	
<div class="panes">
	<s:if test="step==1 || mode=='report'">
		<div>

		<s:form theme="simple" action="AddCandidate" name="frmPersonalInfo" id="frmPersonalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
			
			<div style="float: left;" >
			
			<table border="0" class="formcss">
			
			<tr><td><s:hidden name="empId" /></td></tr>
			<tr><td><s:hidden name="step" /></td></tr>
			<s:hidden name="jobcode" />
			<s:hidden name="recruitId" />
			<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px; padding:5px;">
            Step 1 : </span> Enter Candidate Personal Information</td></tr>
			<tr><td height="10px">&nbsp;</td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empFname</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">First Name<sup>*</sup>:</td>
			
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<s:textfield name="empFname" cssClass="validate[required] text-input" required="true"/>
			<%}else{%>
				<s:textfield name="empFname" required="true" disabled="true"/>
				<s:hidden name="empFname" />
			<%}%>
			
			<span class="hint">Candidate's first name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empLname</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Last Name<sup>*</sup>:</td>
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<s:textfield name="empLname" cssClass="validate[required] text-input" required="true"/>
			<%}else{%>
				<s:textfield name="empLname" required="true" disabled="true"/>
				<s:hidden name="empLname" />
			<%}%>
			
			<span class="hint">Candidate's last name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empEmail</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Personal Email Id<sup>*</sup>:</td>
			<td>
			
			<%if(session.getAttribute("isApproved")==null) {%>
				<s:textfield name="empEmail" cssClass="validate[required,custom[email]]" required="true"/>
			<%}else{%>
				<s:textfield name="empEmail" required="true" disabled="true"/>
				<s:hidden name="empEmail" />
			<%}%>
			
			<span class="hint">Email id is required as the user will received all information on this id.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2>Permanent Address:
			<hr style="background-color:#346897;height:1px">&nbsp;<s:fielderror ><s:param>empAddress1</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Address1<sup>*</sup>:</td><td><s:textfield name="empAddress1" cssClass="validate[required] text-input" required="true"/><span class="hint">Candidate current address.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Address2:</td><td><s:textfield name="empAddress2" /><span class="hint">Candidate current address. (optional)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td colspan=2><s:fielderror ><s:param>city</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Suburb<sup>*</sup>:</td><td><s:textfield name="city" cssClass="validate[required] text-input" required="true"/><span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td colspan=2><s:fielderror ><s:param>country</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Select Country<sup>*</sup>:</td><td><s:select id="country" cssClass="validate[required]"
						name="country" listKey="countryId" 	listValue="countryName" headerKey="" headerValue="Select Country"
						onchange="javascript:showState();return false;"
					list="countryList" key="" required="true" /><span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span></td></tr>
					
			<tr><td colspan=2><s:fielderror ><s:param>state</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Select State<sup>*</sup>:</td><td><s:select theme="simple" title="state" cssClass="validate[required]"
					id="state" name="state" listKey="stateId" listValue="stateName" headerKey="" headerValue="Select State"		
					list="stateList" key="" required="true" /><span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
			</td></tr>
			<tr><td class="txtlabel alignRight">Postcode:</td><td><s:textfield name="empPincode" label="Candidate Pincode" /><span class="hint">Candidate's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td></tr>





			<tr><td colspan=2 style="border-bottom:1px solid #346897">Temporary Address:
			<div style="float:right;">
			<input type="checkbox" onclick="copyAddress(this);" />Same as above</div>
			</td></tr>
			
			<tr><td class="txtlabel alignRight">Address1<sup>*</sup>:</td><td><s:textfield name="empAddress1Tmp" cssClass="validate[required] text-input" required="true"/><span class="hint">Candidate current address.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Address2:</td><td><s:textfield name="empAddress2Tmp" /><span class="hint">Candidate current address. (optional)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Suburb<sup>*</sup>:</td><td><s:textfield name="cityTmp" cssClass="validate[required] text-input" required="true"/><span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Select Country<sup>*</sup>:</td><td><s:select id="countryTmp" cssClass="validate[required]"
						name="countryTmp" listKey="countryId" 	listValue="countryName" headerKey="" headerValue="Select Country"
						onchange="javascript:showState();return false;"
					list="countryList" key="" required="true" /><span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Select State<sup>*</sup>:</td><td><s:select theme="simple" title="state" cssClass="validate[required]"
					id="stateTmp" name="stateTmp" listKey="stateId" listValue="stateName" headerKey="" headerValue="Select State"		
					list="stateList" key="" required="true" /><span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
			</td></tr>
			<tr><td class="txtlabel alignRight">Postcode:</td><td><s:textfield name="empPincodeTmp" label="Candidate Pincode" /><span class="hint">Candidate's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td></tr>




			<tr><td class="txtlabel alignRight">Landline Number<sup>*</sup>:</td><td><s:textfield name="empContactno" cssClass="validate[required] text-input" /><span class="hint">Candidate's contact no. (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Mobile Number<sup>*</sup>:</td><td><s:textfield name="empMobileNo" cssClass="validate[required] text-input" /><span class="hint">Candidate's Mobile No<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<%-- 	
			<tr><td class="txtlabel alignRight">Emergency Contact Name<sup>*</sup>:</td><td><s:textfield cssClass="validate[required] text-input" name="empEmergencyContactName" />
			<tr><td colspan=2><s:fielderror ><s:param>empEmergencyContactNo</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Emergency Contact Number<sup>*</sup>:</td><td><s:textfield name="empEmergencyContactNo" cssClass="validate[required] text-input"/></td></tr>
			
			<tr><td class="txtlabel alignRight">PAN<sup>*</sup>:</td><td><s:textfield name="empPanNo" cssClass="validate[required] text-input" /><span class="hint">Candidate's PAN (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Provident Fund No :</td><td><s:textfield name="empPFNo" /><span class="hint">Candidate's Provident Number (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">GPF Acc No :</td><td><s:textfield name="empGPFNo" /><span class="hint">Candidate's GPF Number <span class="hint-pointer">&nbsp;</span></span></td></tr>
		 --%>	
			
			<tr><td class="txtlabel alignRight">Passport Number:</td><td><s:textfield name="empPassportNo" />
			<tr><td class="txtlabel alignRight">Passport Expiry Date:</td><td><s:textfield name="empPassportExpiryDate"  />
			<tr><td class="txtlabel alignRight">Blood Group:</td><td><s:select theme="simple" name="empBloodGroup" listKey="bloodGroupId"
					listValue="bloodGroupName" headerKey="0" headerValue="Select Blood Group"		
					list="bloodGroupList" key="" required="true" /></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empDateOfBirth</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Date Of Birth<sup>*</sup>:</td><td>
			<s:textfield name="empDateOfBirth" id="empDateOfBirth" cssClass="validate[required] text-input" required="true"></s:textfield>
			<span class="hint">Candidate's Date Of Birth.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empGender</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Gender<sup>*</sup>:</td><td><s:select theme="simple" cssClass="validate[required]" label="Select Gender" name="empGender" listKey="genderId"
					listValue="genderName" headerKey="" headerValue="Select Gender"		
					list="empGenderList" key="" required="true" /><span class="hint">Select Gender.<span class="hint-pointer">&nbsp;</span></span>
			</td></tr>
			
			<tr><td class="txtlabel alignRight">Marital Status:</td><td><s:select theme="simple" name="empMaritalStatus" listKey="maritalStatusId"
					listValue="maritalStatusName" headerKey="0" headerValue="Select Marital Status"		
					list="maritalStatusList" key="" required="true" onchange="showMarriageDate();"/></td></tr>
			
			
			<tr id="trMarriageDate"><td class="txtlabel alignRight">Date Of Marriage<sup></sup>:</td><td>
			<s:textfield name="empDateOfMarriage" id="empDateOfMarriage"></s:textfield>
			<span class="hint">Candidate's Date Of Marriage.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			
			</table>
			</div>
            
			<div style="float:left; padding:10px; border:solid 5px #CCCCCC;  margin: 80px 100px; padding: 10px 30px;" >
				<table>
				<tr><td><strong>Update Candidate image</strong></td></tr>
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
						<s:submit cssClass="input_button" value="Update Candidate" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
			
		</s:form>
		</div>
	</s:if>
	
	<s:if test="step==5 || mode=='report'">
	<div>
	<s:form theme="simple" action="AddCandidate" id="frmReferences" method="POST" cssClass="formcss" enctype="multipart/form-data">
		<table border="0" class="formcss">
			
			<tr><td>
			</td></tr>
			<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 5 : </span>Enter Candidate References 1:</td></tr>
			
			<s:hidden name="empId" />
			<s:hidden name="step" />
			
			<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield name="ref1Name" /></td></tr>
			<tr><td class="txtlabel alignRight">Company:</td><td><s:textfield name="ref1Company" /></td></tr>
			<tr><td class="txtlabel alignRight">Designation:</td><td><s:textfield name="ref1Designation" /></td></tr>
			<tr><td class="txtlabel alignRight">Contact No:</td><td><s:textfield name="ref1ContactNo" /></td></tr>
			<tr><td class="txtlabel alignRight">Email Id:</td><td><s:textfield name="ref1Email" /></td></tr>
			
		</table>
		
		<table border="0" class="formcss">
			
			<tr><td>
			</td></tr>
			<tr><td class="tdLabelheadingBg alignCenter" colspan="2">Enter Candidate References 2:</td></tr>
			
			<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield name="ref2Name" /></td></tr>
			<tr><td class="txtlabel alignRight">Company:</td><td><s:textfield name="ref2Company" /></td></tr>
			<tr><td class="txtlabel alignRight">Designation:</td><td><s:textfield name="ref2Designation" /></td></tr>
			<tr><td class="txtlabel alignRight">Contact No:</td><td><s:textfield name="ref2ContactNo" /></td></tr>
			<tr><td class="txtlabel alignRight">Email Id:</td><td><s:textfield name="ref2Email" /></td></tr>
			
		</table>
		
		<div class="clr"></div>
		
		<div style="float:right;">
			<table>
			
			<s:if test="mode==null">
			
				<tr><td colspan="2" align="center">
					<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value="Submit" align="center" />
				</td></tr>
				
			</s:if>
			
			<s:else>
				
				<tr><td colspan="2" align="center">
					<s:hidden name="mode" />
					<s:submit cssClass="input_button" value="Update Candidate" align="center" />
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
var cnt=0;
function addKRA() {
	
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_kra"+cnt;
	divTag.innerHTML = 	"<div style=\"float:left\">"+
						"<textarea rows=\"1\" cols=\"100\" style=\"width:80%\" name=\"empKra\"></textarea>"+
    			    	"<a href=\"javascript:void(0)\" onclick=\"addKRA()\" class=\"add\" style=\"float:right\">Add</a>" +
    			    	"<a href=\"javascript:void(0)\" onclick=\"removeKRA(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a>"+
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

</script>
</g:compress>

	<%if(strEmpType!=null && !strEmpType.equalsIgnoreCase(IConstants.EMPLOYEE)){%>
	
<%-- <s:if test="step==8 || mode=='report'">

	<div>
 will be removed soon
	</div>

</s:if>
 --%>
<script>
showHideBankName();
</script>


<%}%>


<s:if test="step==2 || mode=='report'">
<div>
	<s:form theme="simple" action="AddCandidate" id="frmBackgroundInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
	
	<s:hidden name="empId" />
	<s:hidden name="step" />
    
    <div><span style="color:#68AC3B; font-size:18px">Step 2 : </span><span class="tdLabelheadingBg">Enter Candidates background information</span> </div>
    
	<div  id="div_skills">
    
        <h3 style="padding:0px;margin:5px 0px 10px 0px">Enter Candidate skills and their values</h3>
            
           <div id="row_skill" >  
			<div style="float:left; width:200px;margin-left:50px"><label>Skill Name</label></div>
			<div style="float:left; width:200px;"><label>Skill Rating</label></div>
		   </div>	
			<% 	
				if(alSkills!=null && alSkills.size()!=0){
					String empId = (String)((ArrayList)alSkills.get(0)).get(3);
				
			%>
				<input type="hidden" name="empId" value="<%=empId%>" />
			<% 
			 	for(int i=0; i<alSkills.size(); i++) {
			%>
				
				<div id="row_skill<%=((ArrayList)alSkills.get(i)).get(0)%>" class="row_skill">
					
                    <table>
					<tr>
						<td>
						<%if(i==0){ %>
							[PRI]
						<%}else{ %>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<%} %>
							<select name="skillName">
			                	<%for(int k=0; k< skillsList.size(); k++) { 
			                		if( (((FillSkills)skillsList.get(k)).getSkillsId()+"").equals( (String)((ArrayList)alSkills.get(i)).get(1) )) {
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
			            <td><a href="javascript:void(0)" onclick="removeSkills(this.id)" id=<%=((ArrayList)alSkills.get(i)).get(0)%> class="remove" >Remove</a></td>
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
				                	<%for(int k=0; k< skillsList.size(); k++) {%> 
				                		
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
	
       <h3 style="padding:0px;margin:5px 0px 10px 0px">Enter Candidate educational details</h3>
        
		<div id="row_education" class="row_education">  
                <table>
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
		%>
		
				<div id="row_education<%=((ArrayList)alEducation.get(i)).get(0)%>" class="row_education">
				
                    <table>    
					<tr><td><input type="text" style="width: 110px;" name="degreeName" value="<%=((ArrayList)alEducation.get(i)).get(1)%>" ></input></td>
	                <td>
	                	<select name="degreeDuration" style="width: 110px;">
	                	<%for(int k=0; k< degreeDurationList.size(); k++) { 
	                		if( (((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationID()+"").equals( (String)((ArrayList)alEducation.get(i)).get(2) )) {
	                	%>
	                		<option value="<%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationID() %>" selected="selected">
	                		<%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationName() %>
	                		</option>
	                	<%}else { %>
	                		<option value="<%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationID() %>">
	                		<%=((FillDegreeDuration)degreeDurationList.get(k)).getDegreeDurationName() %>
	                		</option>
	                	<% }
	                	}%>
	                	</select>
	                </td>
	                
	                <td>
	                	<select name="completionYear" style="width: 110px;">
	                	<%for(int k=0; k< yearsList.size(); k++) { 
	                		if( (((FillYears)yearsList.get(k)).getYearsID()+"").equals( (String)((ArrayList)alEducation.get(i)).get(3) )) {
	                	%>
	                		<option value="<%=((FillYears)yearsList.get(k)).getYearsID() %>" selected="selected">
	                		<%=((FillYears)yearsList.get(k)).getYearsName() %>
	                		</option>
	                	<%}else { %>
	                		<option value="<%=((FillYears)yearsList.get(k)).getYearsID() %>">
	                		<%=((FillYears)yearsList.get(k)).getYearsName() %>
	                		</option>
	                	<% }
	                	}%>
	                	</select>
	                </td>
	                
	                <td><input type="text" style="width: 110px;" name="grade" value="<%=((ArrayList)alEducation.get(i)).get(4)%>" ></input></td>
					<td><a href="javascript:void(0)" onclick="addEducation()" class="add">Add</a></td>
					<td><a href="javascript:void(0)" onclick="removeEducation(this.id)" id=<%=((ArrayList)alEducation.get(i)).get(0)%> class="remove" >Remove</a></td>
                    </tr>
                    </table>
				</div>
		<%}
		 	
		}else { %>
		
			<div id="row_education" class="row_education">
                <table>
				<tr><td><input type="text" style="width: 110px;" name="degreeName"></input></td>
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
				</table>
			</div>
		
		
		<%} %>
</div>
	
	<div id="div_language">
	
	<h3 style="padding:0px;margin:5px 0px 10px 0px">Enter Candidate languages</h3>
            
           <div id="row_language" class="row_language">  
                <table>              
				  <tr><td width="190px"><label>Language Name</label></td>
				  <td width="50px" align="center"><label>Read</label></td>
				  <td width="50px" align="center"><label>Write</label></td>
				  <td width="50px" align="center"><label>Speak</label></td></tr>
                </table>
		   </div>	
			<% 	
				if(alLanguages!=null && alLanguages.size()!=0){
			 	for(int i=0; i<alLanguages.size(); i++) {
			%>
				
				<div id="row_language<%=((ArrayList)alLanguages.get(i)).get(0)%>" class="row_language">
					<table>
						<tr><td><input type="text" style="width: 180px;" name="languageName" value="<%=((ArrayList)alLanguages.get(i)).get(1)%>" ></input></td>
	                    <td width="50px" align="center" >
	                    <% 
	                    	if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(2)) ) { 
	                    %>
	                    	<input type="checkbox" name="isRead" value="1" id="isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                    		checked="checked" onclick="showHideHiddenField(this.id, this.name)" />
	                    	
	                    <%}else { %>
	                    	<input type="checkbox" name="isRead" value="0" id="isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                    			onclick="showHideHiddenField(this.id, this.name)" />
	                    	<input type="hidden" id = "hidden_isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" name="isRead" value="0" />
	                    <%} %>
	                    </td>
	                    
	                     <td width="50px" align="center" id="td_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>">
	                    <% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(3)) ) { %>
	                    	<input type="checkbox" name="isWrite" value="1" checked="checked" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>"  
	                    			onclick="showHideHiddenField(this.id, this.name)"/>
	                    	
	                    <%}else { %>
	                    	<input type="checkbox" name="isWrite" value="0" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                    			onclick="showHideHiddenField(this.id, this.name)"/>
	                    	<input type="hidden" name="isWrite" value="0" id="_hidden_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>"/>
	                    <%} %>
	                    </td>
	                    
	                     <td width="50px" align="center" id="td_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>">
	                    <% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(4)) ) { %>
	                    	<input type="checkbox" name="isSpeak" value="1" checked="checked" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                    		onclick="showHideHiddenField(this.id, this.name)"/>
	                    	
	                    <%}else { %>
	                    	<input type="checkbox" name="isSpeak" value="0" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>"
	                    			onclick="showHideHiddenField(this.id, this.name)"/>
	                    	<input type="hidden" name="isSpeak" id="hidden_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" value="0" />
	                    <%} %>
	                    </td>
							
						
				    	<td><a href="javascript:void(0)" onclick="addLanguages()" class="add">Add</a></td>
				        <td><a href="javascript:void(0)" onclick="removeLanguages(this.id)" id=<%=i%> class="remove" >Remove</a></td></tr>
			    	</table>
			    </div>
			    
			 <%}
			 
			 }else {
			 %>
			 	<div id="row_language" class="row_language" >
			 	    <table>
				 	<tr><td><input type="text" style="width: 180px;" name="languageName" ></input></td>
				 	<td width="50px" align="center"><input type="checkbox" id="isRead_0" name="isRead" value="1" onclick="showHideHiddenField(this.id, this.name)"/>
				 		
					<td width="50px" align="center"><input type="checkbox" id="isWrite_0" name="isWrite" value="1" onclick="showHideHiddenField(this.id, this.name)"/>
						
		            <td width="50px" align="center"><input type="checkbox" id="isSpeak_0" name="isSpeak" value="1" onclick="showHideHiddenField(this.id, this.name)"/>
		            	
			    	<td><a href="javascript:void(0)" onclick="addLanguages()" class="add">Add</a></td></tr>
			    	</table>
				</div>
			    	
			 <%}%>
	</div>
	
	<div id="div_hobbies">
	
	<h3 style="padding:0px;margin:5px 0px 10px 0px">Enter Candidate hobbies</h3>
            
           <div id="row_hobby" class="row_hobby">  
			<div style="float:left; width:200px;"><label>Hobby Name</label></div>
		   </div>	
			<% 	
				if(alHobbies!=null && alHobbies.size()!=0){
					String empId = (String)((ArrayList)alHobbies.get(0)).get(2);
				
			%>
				<input type="hidden" name="empId" value="<%=empId%>" />
			<% 
			 	for(int i=0; i<alHobbies.size(); i++) {
			%>
				
				<div id="row_hobby<%=((ArrayList)alHobbies.get(i)).get(0)%>" class="row_hobby">
					<table>
                        <tr><td><input type="text" style="width: 180px;" name="hobbyName" value="<%=((ArrayList)alHobbies.get(i)).get(1)%>" ></input></td> 
                            <td><a href="javascript:void(0)" onclick="addHobbies()" class="add">Add</a></td>
                            <td><a href="javascript:void(0)" onclick="removeHobbies(this.id)" id=<%=((ArrayList)alHobbies.get(i)).get(0)%> class="remove" >Remove</a></td>
                        </tr>    
			    	</table>
			    </div>
			    
			 <%}
			 
			 }else {
			 %>
			 	<div id="row_hobby" class="row_hobby">
                    <table>
                          <tr><td><input type="text" style="width: 180px;"name="hobbyName" ></input></td>
                              <td><a href="javascript:void(0)" onclick="addHobbies()" class="add">Add</a></td>
                          </tr>    
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
						<s:submit cssClass="input_button" value="Update Candidate" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
	
	</s:form>
</div>
</s:if>

<s:if test="step==3 || mode=='report'">

<div>	<!-- Family Information -->


<s:form theme="simple" action="AddCandidate" id="frmFamilyInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
	
	<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_id_family">
	
	<s:hidden name="empId" />
	<s:hidden name="step" />
	<table border="0" class="formcss">
	<tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 3 : </span>
    <span class="tdLabelheadingBg">Enter Candidates Family Information </span></td></tr>
	</table>
    
    <table>
      <tr><td>
		<table>	
		       <tr><td  style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Father's Information </td></tr>
			 	<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield cssStyle="width: 180px;" name="fatherName" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Date of birth:</td><td> <s:textfield cssStyle="width: 180px;" name="fatherDob" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Education:</td><td> <s:textfield cssStyle="width: 180px;" name="fatherEducation" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Occupation:</td><td> <s:textfield cssStyle="width: 180px;" name="fatherOccupation" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Contact Number:</td><td><s:textfield cssStyle="width: 180px;" name="fatherContactNumber" ></s:textfield></td></tr>
				<tr><td class="txtlabel alignRight">Email Id:</td><td> <s:textfield cssStyle="width: 180px;" name="fatherEmailId" ></s:textfield></td></tr>
				
	   </table>     
		</td>
        <td>&nbsp;</td>
        <td>
       <table>	 	 
		   <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Mother's Information </td></tr>
		 	<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield cssStyle="width: 180px;" name="motherName" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Date of birth:</td><td> <s:textfield cssStyle="width: 180px;" name="motherDob" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Education:</td><td> <s:textfield cssStyle="width: 180px;" name="motherEducation" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Occupation:</td><td> <s:textfield cssStyle="width: 180px;" name="motherOccupation" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Contact Number:</td><td> <s:textfield cssStyle="width: 180px;" name="motherContactNumber" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Email Id:</td><td> <s:textfield cssStyle="width: 180px;" name="motherEmailId" ></s:textfield></td></tr>
		</table>     
		</td>
        <td>&nbsp;</td>
        <td>
       <table>	 	 
		   	<tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Spouse's Information </td></tr>
		 	<tr><td class="txtlabel alignRight">Name:</td><td><s:textfield cssStyle="width: 180px;" name="spouseName" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Date of birth:</td><td> <s:textfield cssStyle="width: 180px;" name="spouseDob" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Education:</td><td> <s:textfield cssStyle="width: 180px;" name="spouseEducation" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Occupation:</td><td> <s:textfield cssStyle="width: 180px;" name="spouseOccupation" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Contact Number:</td><td> <s:textfield cssStyle="width: 180px;" name="spouseContactNumber" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Email Id:</td><td> <s:textfield cssStyle="width: 180px;" name="spouseEmailId" ></s:textfield></td></tr>
			<tr><td class="txtlabel alignRight">Gender:</td>
			<td><s:select theme="simple" label="Select Gender" name="spouseGender" listKey="genderId"
					listValue="genderName" headerKey="0" headerValue="Select Gender"		
					list="empGenderList" key="" required="true" />
			</td>
		</table>
		</td></tr>	 
    </table>    
             
			<%	if(alSiblings!=null && alSiblings.size()!=0) {
					for(int i=0; i<alSiblings.size(); i++) {%>
			
			<div id="col_family_siblings<%=i%>" style="float:left; border:solid 0px #ccc" >
			                      
               <table> 
                  <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td></tr>    
                
			 	<tr><td class="txtlabel alignRight">Name:</td><td><input type="text" style="width: 180px;" name="memberName" value="<%=((ArrayList)alSiblings.get(i)).get(1)%>" ></input></td></tr>    
				<tr><td class="txtlabel alignRight">Date of birth:</td><td> <input type="text" style="width: 180px;" name="memberDob" value="<%=((ArrayList)alSiblings.get(i)).get(2)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Education: </td><td><input type="text" style="width: 180px;" name="memberEducation" value="<%=((ArrayList)alSiblings.get(i)).get(3)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Occupation:</td><td> <input type="text" style="width: 180px;" name="memberOccupation" value="<%=((ArrayList)alSiblings.get(i)).get(4)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Contact Number:</td><td> <input type="text" style="width: 180px;" name="memberContactNumber" value="<%=((ArrayList)alSiblings.get(i)).get(5)%>"></input></td></tr>    
				<tr><td class="txtlabel alignRight">Email Id:</td><td> <input type="text" style="width: 180px;" name="memberEmailId" value="<%=((ArrayList)alSiblings.get(i)).get(6)%>"></input></td></tr>    
				
				<tr><td class="txtlabel alignRight">Gender:</td><td> 
				
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
				  
		    	<!-- <tr><td class="txtlabel alignRight"><a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add">&nbsp;</a></td> -->   
		    	<tr><td class="txtlabel alignRight" colspan="2">
		    	<a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add">&nbsp;</a>
		    	<a href="javascript:void(0)" onclick="removeSibling(this.id)" id=<%=i%> class="remove" >Remove</a>
		    	</td></tr>    
                    
               </table>     
			</div>
			
			<%}
			}else {%>
			
			<div id="col_family_siblings" style="float: left;border:solid 0px #f00" >
			   
               <table> 
                <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td></tr>        
			 	<tr><td class="txtlabel alignRight">Name:</td><td><input type="text" style="width: 180px;" name="memberName" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Date of birth:</td><td> <input type="text" style="width: 180px;" name="memberDob" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Education:</td><td> <input type="text" style="width: 180px;" name="memberEducation" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Occupation:</td><td><input type="text" style="width: 180px;" name="memberOccupation" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Contact Number:</td><td> <input type="text" style="width: 180px;" name="memberContactNumber" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Email Id:</td><td> <input type="text" style="width: 180px;" name="memberEmailId" ></input></td></tr>
				<tr><td class="txtlabel alignRight">Gender:</td><td>
					<s:select theme="simple" label="Select Gender" name="memberGender" listKey="genderId"
						listValue="genderName" headerKey="0" headerValue="Select Gender"		
						list="empGenderList" key="" required="true" />
					</td>
				</tr>
		    	<tr><td class="txtlabel alignRight" colspan="2"><a href="javascript:void(0)" onclick="addSibling()" class="add" style="float:right">&nbsp;</a></td></tr>
		       </table>
            	
			</div>
			
			<%}%>
			
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
						<s:submit cssClass="input_button" value="Update Candidate" align="center" />
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

<s:form theme="simple" action="AddCandidate" id="frmPrevEmployment" method="POST" cssClass="formcss" enctype="multipart/form-data">
<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
	
	<s:hidden name="empId" />
	<s:hidden name="step" />
	<table border="0" class="formcss">

<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 4 : </span>Enter Candidates Previous Employment </td></tr>
	</table>			
		
		 <%	if(alPrevEmployment!=null && alPrevEmployment.size()!=0) {
			 
				for(int i=0; i<alPrevEmployment.size(); i++) {%>
					
			<div id="col_prev_employer<%=i%>" style="float: left;">
			
             <table>
			 	<tr><td class="txtlabel alignRight">Company Name:</td><td><input type="text" name="prevCompanyName" style="width: 180px;" name="prevCompanyLocation" value="<%=((ArrayList)alPrevEmployment.get(i)).get(1)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Location:</td><td> <input type="text" style="width: 180px;" name="prevCompanyLocation" value="<%=((ArrayList)alPrevEmployment.get(i)).get(2)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">City:</td><td> <input type="text" style="width: 180px;" name="prevCompanyCity" value="<%=((ArrayList)alPrevEmployment.get(i)).get(3)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">State:</td><td> <input type="text" style="width: 180px;" name="prevCompanyState" value="<%=((ArrayList)alPrevEmployment.get(i)).get(4)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Country:</td><td> <input type="text" style="width: 180px;" name="prevCompanyCountry" value="<%=((ArrayList)alPrevEmployment.get(i)).get(5)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Contact Number:</td><td> <input type="text" style="width: 180px;" name="prevCompanyContactNo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(6)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Reporting To:</td><td> <input type="text" style="width: 180px;" name="prevCompanyReportingTo" value="<%=((ArrayList)alPrevEmployment.get(i)).get(7)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">From: </td><td><input type="text" style="width: 180px;" name="prevCompanyFromDate" value="<%=((ArrayList)alPrevEmployment.get(i)).get(8)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">To:</td><td> <input type="text" style="width: 180px;" name="prevCompanyToDate" value="<%=((ArrayList)alPrevEmployment.get(i)).get(9)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Designation:</td><td> <input type="text" style="width: 180px;" name="prevCompanyDesination" value="<%=((ArrayList)alPrevEmployment.get(i)).get(10)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Responsibility:</td><td> <input type="text" style="width: 180px;" name="prevCompanyResponsibilities" value="<%=((ArrayList)alPrevEmployment.get(i)).get(11)%>"></input></td></tr> 
				<tr><td class="txtlabel alignRight">Skills:</td><td> <input type="text" style="width: 180px;" name="prevCompanySkills" value="<%=((ArrayList)alPrevEmployment.get(i)).get(12)%>"></input></td></tr> 
				
				<tr>
				<td style="margin:0px 5px 0px 0px">
				<a href="javascript:void(0)" onclick="addPrevEmployment()" class="add">&nbsp;</a>
				<a href="javascript:void(0)" onclick="removePrevEmployment(this.id)" id=<%=i%> >&nbsp;</a>
				</td></tr>
				
              </table> 
                
			</div>
			 
			 
		<%}
				
		 }else { %>
			
			<div id="col_prev_employer" style="float: left;">
			 
              <table>
			 	<tr><td class="txtlabel alignRight"> Company Name:</td><td><input type="text" name="prevCompanyName" style="width: 180px;" name="prevCompanyLocation" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Location:</td><td> <input type="text" style="width: 180px;" name="prevCompanyLocation" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> City: </td><td><input type="text" style="width: 180px;" name="prevCompanyCity" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> State:</td><td> <input type="text" style="width: 180px;" name="prevCompanyState" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Country:</td><td> <input type="text" style="width: 180px;" name="prevCompanyCountry" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Contact Number:</td><td> <input type="text" style="width: 180px;" name="prevCompanyContactNo" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Reporting To:</td><td> <input type="text" style="width: 180px;" name="prevCompanyReportingTo" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> From:</td><td> <input type="text" style="width: 180px;" name="prevCompanyFromDate" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> To:</td><td> <input type="text" style="width: 180px;" name="prevCompanyToDate" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Designation:</td><td> <input type="text" style="width: 180px;" name="prevCompanyDesination" ></input></td></tr> 
				<tr><td class="txtlabel alignRight"> Responsibility:</td><td> <input type="text" style="width: 180px;" name="prevCompanyResponsibilities" ></input>  </td></tr> 
				<tr><td class="txtlabel alignRight"> Skills: </td><td> <input type="text" style="width: 180px;" name="prevCompanySkills" ></input></td></tr> 

				
				<tr><td class="txtlabel alignRight" colspan="2"> <a href="javascript:void(0)" onclick="addPrevEmployment()" class="add">&nbsp;</a></td></tr> 
				
              </table>   
			</div>
			 
			
			<%}%>
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
						<s:submit cssClass="input_button" value="Update Candidate" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
</div>
</s:form>  
</div>
</s:if>

<s:if test="step==6 || mode=='report'">
<div> <!-- Medical Information -->

<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
<s:form theme="simple" action="AddCandidate" id="frmMedicalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="empId" />
	<s:hidden name="step" />
	
	<table border="0" class="formcss">
	    <tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 6 : </span>Medical Information :-</td></tr>
	</table>
	    
	<table border="0" class="formcss">
	
		<tr><td class="tdLabelheadingBg alignCenter" colspan="2">Enter Candidate's Medical Information </td></tr>
		<tr>
			<td>
	            <table>
				 	<tr><td class="txtlabel alignRight">Are you now receiving medical attention:</td>
				 		<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue1" onclick="checkRadio(this,'text1');"></s:radio>
				 			<s:hidden name="empMedicalId1" />
				 			<s:hidden name="que1Id" value="1"></s:hidden>
				 		</td>
				 		<td><s:textarea cssStyle="width:800%" id="text1" name="que1Desc" disabled="true" ></s:textarea></td>
				 	</tr> 
					<tr><td class="txtlabel alignRight">Have you had any form of serious illness or operation:</td>
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue2" onclick="checkRadio(this,'text2');"></s:radio>
							<s:hidden name="empMedicalId2" />
							<s:hidden name="que2Id" value="2"></s:hidden>
						</td>
						
						<td><s:textarea cssStyle="width:800%" id="text2" name="que2Desc" disabled="true"></s:textarea></td>
					</tr> 
					<tr><td class="txtlabel alignRight">Have you had any illness in the last two years? YES/NO If YES, 
							please give the details about the same and any absences from work: </td>
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue3" onclick="checkRadio(this,'text3');"></s:radio>
							<s:hidden name="empMedicalId3" />
							<s:hidden name="que3Id" value="3"></s:hidden>
						</td>
						
						<td><s:textarea cssStyle="width:800%" name="que3Desc" id="text3" disabled="true"></s:textarea></td>
					</tr>
					<tr><td class="txtlabel alignRight">Has any previous post been terminated on medical grounds?</td>
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue4" onclick="checkRadio(this,'text4');"></s:radio>
							<s:hidden name="empMedicalId4" />
							<s:hidden name="que4Id" value="4"></s:hidden>
						</td>
						<td><s:textarea cssStyle="width:800%" name="que4Desc" id="text4" disabled="true"></s:textarea></td>
					</tr>
					<tr><td class="txtlabel alignRight">Do you have an allergies?</td>
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue5" onclick="checkRadio(this,'text5');"></s:radio>
							<s:hidden name="empMedicalId5" />
							<s:hidden name="que5Id" value="5"></s:hidden>
						</td>
						<td><s:textarea cssStyle="width:800%" name="que5Desc" id="text5" disabled="true"></s:textarea></td>
					</tr> 
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
						<s:submit cssClass="input_button" value="Update Candidate" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
		 
</s:form>	     
</div>
</div>
</s:if>

<s:if test="step==7 || mode=='report'">
<div>	

<form action="AddCandidate.action" id="frmDocumentation" method="POST" class="formcss" enctype="multipart/form-data">
<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_id_docs">
	<s:hidden name="empId" />
	<s:hidden name="step" />
	
		<table border="0" class="formcss">
	    <tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 7 : </span>Attach Documents :-</td></tr>
	    </table>
				
                <table>
                <tr><td class="txtlabel alignRight"><label><b><!-- Document Type --></b></label></td>
                <td class="txtlabel alignRight"><label><b>Document Name</b></label></td>
                <td class="txtlabel alignRight"><label><b>Attached Document</b></label></td>
                <td><a href="javascript:void(0)" onclick="addDocuments()" class="add"><b>Add New Document..</b></a></td>
                </tr>        
                </table>
				
				<% 	
					if(alDocuments!=null && alDocuments.size()!=0) {
						
						String empId = (String)((ArrayList)alDocuments.get(0)).get(3);
				%>
					<input type="hidden" name="empId" value="<%=empId%>" />
				<% 
				 	for(int i=0; i<alDocuments.size(); i++) {
				%>
					<div id="row_document<%=((ArrayList)alDocuments.get(i)).get(0)%>" >
						
                        <table>
						  <tr>
						  		<td class="txtlabel alignRight"><%-- <%=((ArrayList)alDocuments.get(i)).get(1)%>: --%>
						  			<input type="hidden" name="idDocType" value="<%=((ArrayList)alDocuments.get(i)).get(2)%>"></input>
						  			<input type="hidden" name="docId" value="<%=((ArrayList)alDocuments.get(i)).get(0)%>"></input>
						  		</td>
						  		
						  		<td class="txtlabel" style="padding-left:115px;width:190px;">
						  			<%=((ArrayList)alDocuments.get(i)).get(1)%>
						  		</td>
						  		<td>
						  			<a href="<%=request.getContextPath()+"/userDocuments/"+((ArrayList)alDocuments.get(i)).get(4)%>" ><i class="fa fa-file-o" aria-hidden="true" title="click to download"></i></a>
						  		</td>
						  		
						  		<%if(i==alDocuments.size()-1){ %>
						  		<td class="txtlabel alignRight"><a href="javascript:void(0)" onclick="addDocuments()" class="add"></a></td>
						  		<%} %>
                                <%-- <td class="txtlabel alignRight"><input disabled="disabled" type="text" style="width: 160px;" name="idDocName" class="validate[required] text-input" value="<%=((ArrayList)alDocuments.get(i)).get(1)%>" ></input></td> 
                                <td class="txtlabel alignRight"><input type="file" name="idDoc" value="<%=((ArrayList)alDocuments.get(i)).get(4)%>" disabled="disabled"/></td>
                                <td class="txtlabel alignRight"><!-- <a href="javascript:void(0)" onclick="addDocuments()" class="add"></a> --></td>
                                <td class="txtlabel alignRight"><a href="javascript:void(0)" onclick="removeDocuments(this.id)" id=<%=((ArrayList)alDocuments.get(i)).get(0)%> >Remove</a></td>
                                <td><a href="<%=request.getContextPath()+"/userDocuments/"+((ArrayList)alDocuments.get(i)).get(4)%>" >Download</a></td> --%>
                                
                          </tr>
                        </table>
                        
				    </div>
				    <div class="clr"></div>
				    
				 <%}
				 
				 }else {
				 %>
				 
				 <div id="row_document">
                    <table>
				 		<tr>
				 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_RESUME%><sup>*</sup>:
				 		<input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_RESUME%>"></input></td>
				 		<td class="txtlabel alignRight"><input type="text" style="width: 180px;" class="validate[required]" name="idDocName" ></input></td>
						<td class="txtlabel alignRight"><input type="file" name="idDoc"/></td>
                    </table>
				</div>
				
				<div id="row_document">
                    <table>
				 		<tr>
				 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ID_PROOF%><sup>*</sup>:
				 		<input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_ID_PROOF%>"></input></td>
				 		<td class="txtlabel alignRight"><input type="text" class="validate[required]" style="width: 180px;" name="idDocName" ></input></td>
						<td class="txtlabel alignRight"><input type="file" name="idDoc"/></td>
                    </table>
				</div>
				
				<div id="row_document">
                    <table>
				 		<tr>
				 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ADDRESS_PROOF%><sup>*</sup>:
				 		<input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>"></input></td>
				 		<td class="txtlabel alignRight"><input type="text" class="validate[required]" style="width: 180px;" name="idDocName" ></input></td>
						<td class="txtlabel alignRight"><input type="file" name="idDoc"/></td>
			    		
                    </table>
				</div>
				 
				<div id="row_document">
                    <table>
                    <tr>
                    <td class="txtlabel alignRight"><%=IConstants.DOCUMENT_OTHER%>
                    <input type="hidden" name="idDocType" value="<%=IConstants.DOCUMENT_OTHER%>"></input></td>
				 	<td class="txtlabel alignRight"><input type="text" style="width: 180px;" name="idDocName" ></input></td>
					<td class="txtlabel alignRight"><input type="file" name="idDoc"/></td>
			    	<td><a href="javascript:void(0)" onclick="addDocuments()" class="add">Add</a></td></tr> 
                    </table>
				</div>
				 
				 <%} %>
				 <div class="clr"></div>
				 <div style="float:right;">
				<table>
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="input_button" cssStyle="width:200px; float:right;" value=" Submit " align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:hidden name="mode" />
						<s:submit cssClass="input_button" value="Update Candidate" align="center" />
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
	
    <div><span style="color:#68AC3B; font-size:30px"> </span><strong>Candidate Added Successfully....</strong></div>
	<%-- <s:if test="mode=='report' || mode=='profile'">
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
	 --%>
	</div>
	<a href="OpenJobReport.action"><strong>Click to Continue......</strong></a>	
	
</s:if>





<%-- 
<s:if test="step==11">

<script>
$(function() {
	$("input[name=strDate]").datepicker({dateFormat: 'dd/mm/yy'});
	$( "input[name=strTime]" ).timepicker({});
});
</script>

<form action="AddCandidate.action" id="frmAvailibility" method="POST" class="formcss">
	<s:hidden name="empId" />
	<s:hidden name="step" />
	
	<table border="0" class="formcss">
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
		<td align="right"><input type="text" name="strDate" style="width: 100px;"></td>
		<td><input type="text" name="strTime" style="width: 100px;"></td>
	</tr>
	
	<tr>
		<td class="txtlabel alignRight">Option 2</td>
		<td align="right"><input type="text" name="strDate" style="width: 100px;"></td>
		<td><input type="text" name="strTime" style="width: 100px;"></td>
	</tr>
	
	<tr>
		<td class="txtlabel alignRight">Option 3</td>
		<td align="right"><input type="text" name="strDate" style="width: 100px;"></td>
		<td><input type="text" name="strTime" style="width: 100px;"></td>
	</tr>
</table>

		<div style="float: right;">
				<table>
					<tr><td align="center" colspan="2">
					</td></tr><tr>
    				<td colspan="2"><div align="center"><input type="submit" style="width: 200px; float: right;" class="input_button" value="Submit &amp; Proceed" id="">
					</div></td>
				</tr>
				</table>
		</div>

</form>


</s:if> --%>





</div>







<script>
showMarriageDate();
validateMandatory(document.frmOfficial.empType.options[document.frmOfficial.empType.options.selectedIndex].value);
</script>

</div>

<%-- <div id="popup_document" class="popup_block">
	<h2 class="textcolorWhite">Attach Documents</h2>
	<s:form id="frmdeductionDetails" action="AddDocument" method="post" enctype="multipart/form-data">
		<div id="row_document">
		 	<div style="float:left; width:300px;">
		 		<label>Document Name</label><div class="clr"></div>
		 		<input type="text" style="width: 160px;"name="idDocName" ></input>
		 	</div> 
		    <div style="float:left; width:300px;"><input type="file" name="idDoc"/></div>
		    <div style="float:left; width:100px;"><a href="javascript:void(0)" onclick="addDocuments()" class="add">Add New</a></div>
		</div>
	</s:form>
</div> --%>





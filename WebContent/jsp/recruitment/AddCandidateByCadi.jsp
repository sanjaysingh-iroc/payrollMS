<%@page import="com.konnect.jpms.select.FillYears"%>
<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.select.FillDegreeDuration"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.recruitment.FillEducational"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillGender"%>

<%@page import="com.konnect.jpms.util.*" %>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%-- <jsp:include page="/jsp/common/Links.jsp" flush="true" /> --%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
 
 <!-- ====start parvez on 01-07-2021==== -->
 	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
 <!-- ====start parvez on 01-07-2021===== -->   
       
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
	
	.textfield_height{
		height: 25px;
	}
	
	/* ====start parvez 0n 02-07-2021===== */
	
	#div_language {
        height: 300px;
        border: solid 2px #F5F5F5;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding-left: 5px;
		padding-right: 5px;
        }
	
	#div_education {
        height: 300px;
        border: solid 2px #F5F5F5;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding-left: 5px;
		padding-right: 5px;
        }
	
	
	#div_skills {
        height: 300px;
        border: solid 2px #F5F5F5;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding-left: 5px;
		padding-right: 5px;
        }
	
	#div_hobbies {
        height: 300px;
        border: solid 2px #F5F5F5;
        overflow: auto;
        margin: 10px 10px 10px 10px;
        padding-left: 5px;
		padding-right: 5px;
        }
	/* ====end parvez 0n 02-07-2021===== */
	</g:compress>
	
	</style>

	<%
	String struserType = (String)session.getAttribute(IConstants.USERTYPE);
	ArrayList educationalList = (ArrayList) request.getAttribute("educationalList"); 
	ArrayList alSkills = (ArrayList) request.getAttribute("alSkills"); 
	ArrayList alHobbies = (ArrayList) request.getAttribute("alHobbies");
	ArrayList alLanguages = (ArrayList) request.getAttribute("alLanguages");
	ArrayList alEducation = (ArrayList) request.getAttribute("alEducation");
	ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
	if(alDocuments== null) alDocuments = new ArrayList();
	
	ArrayList alSiblings = (ArrayList) request.getAttribute("alSiblings");
	ArrayList alPrevEmployment = (ArrayList) request.getAttribute("alPrevEmployment");
	List degreeDurationList = (List) request.getAttribute("degreeDurationList");
	List yearsList = (List) request.getAttribute("yearsList");
	List empGenderList = (List) request.getAttribute("empGenderList");
	List skillsList = (List) request.getAttribute("skillsList");
	String strImage = (String) request.getAttribute("strImage");
	
	//ArrayList alOtherDocuments = (ArrayList) request.getAttribute("alOtherDocuments"); 
	//int documentcnt  = alDocuments.size();
	
	HashMap empServicesMap = (HashMap) request.getAttribute("empServicesMap");
	
	UtilityFunctions uF = new UtilityFunctions();
	String currentYear = (String)request.getAttribute("currentYear");
	
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	int nEmpAlphaCodeLength = 2;
	if(CF!=null && CF.getStrOEmpCodeAlpha()!=null) {
		nEmpAlphaCodeLength = CF.getStrOEmpCodeAlpha().length();
	}
	
	String strUserType=(String)session.getAttribute(IConstants.USERTYPE);
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	int docCount = 0;
	if(alDocuments != null && alDocuments.size()>0 ) {
		docCount = alDocuments.size();
	}
	
	
	Map<String, List<String>> hmEducationDocs = (Map<String, List<String>>)request.getAttribute("hmEducationDocs");
	//===start parvez date: 06-08-2022===
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	//===end parvez date: 06-08-2022===
	
	//System.out.println("AddByCandi");
	%>

<script>
    $(function(){
    	$("input[name='stepSubmit']").click(function(){
    		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
    		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
    		$("#"+ this.form.id ).find('.validateEmail').filter(':visible').attr('type','email');
    	});
    	
        $("#empEmail").prop('type','email');
        $("#fatherEmailId").prop('type','email'); 
        $("#motherEmailId").prop('type','email');
        $("#spouseEmailId").prop('type','email');
        $("#memberEmailId").prop('type','email');
        $("#ref1Email").prop('type','email');
        $("#ref2Email").prop('type','email');
    });
</script>



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
		
		document.getElementById("desigIdV").className = 'validateRequired';
		document.getElementById("gradeIdV").className = 'validateRequired';
	}
}


function documentCount()
{
	
	var a = $(".otherdocument").length;
	document.getElementById("otherDocumentCnt").value = $(".otherdocument").length;
	
}
function showState() {	
	dojo.event.topic.publish("showState");
}

//====start parvez on 02-07-2021=====

function callDatePicker() {
	//alert("callDatePicker ");
    $( "#empDateOfBirth" ).datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:2000', changeYear: true});
    $( "#empDateOfMarriage" ).datepicker({dateFormat: 'dd/mm/yy', yearRange: '1970:<%=currentYear%>', changeYear: true});
    $("input[name=prevCompanyFromDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=prevCompanyToDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=fatherDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=motherDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=spouseDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=memberDob]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=empPassportExpiryDate]").datepicker({dateFormat: 'dd/mm/yy', yearRange: '<%=currentYear%>:2020', changeYear: true});
    /* start parvez Date: 18-09-21 */	    
    $("input[name=strStartDate]").datepicker({format: 'dd/mm/yyyy'});
    $("input[name=strCompletionDate]").datepicker({format: 'dd/mm/yyyy'});
    /* end parvez Date: 18-09-21 */
}
//====end parvez on 02-07-2021=====

$(function() {
	//===start parvez on 01-07-2021==== 
	$("input[name=strDate]").datepicker({format: 'dd/mm/yy'});
	$( "input[name=strTime]" ).datetimepicker({format: 'HH:mm'});
	//====end parvez on 01-07-2021===== 
    $("#empDateOfBirth").datepicker({format: 'dd/mm/yy', yearRange: '1950:2000', changeYear: true});
    $( "#empDateOfMarriage" ).datepicker({format: 'dd/mm/yy', yearRange: '1970:<%=currentYear%>', changeYear: true});
    $("input[name=prevCompanyFromDate]").datepicker({format: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=prevCompanyToDate]").datepicker({format: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=fatherDob]").datepicker({format: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=motherDob]").datepicker({format: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=spouseDob]").datepicker({format: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=memberDob]").datepicker({format: 'dd/mm/yy', yearRange: '1950:<%=currentYear%>', changeYear: true});
    $("input[name=empPassportExpiryDate]").datepicker({format: 'dd/mm/yy', yearRange: '<%=currentYear%>:2020', changeYear: true});
    
    /* start parvez Date: 08-09-21 */	    
    $("input[name=strStartDate]").datepicker({format: 'dd/mm/yyyy'});
    $("input[name=strCompletionDate]").datepicker({format: 'dd/mm/yyyy'});
    /* end parvez Date: 08-09-21 */
});

function fillFileStatus(ids){
	document.getElementById(ids).value=1;
	// Start Dattatray Date:30-June-2021
	 var allowedFileExtensions = /(\.doc|\.docs|\.docx|\.pdf)$/i;
	 var allowedIdProofExtensions = /(\.jpeg|\.jpg|\.png|\.svg)$/i;
	 var fileErrorMsg = 'Please upload pdf,docx,docs or doc only';
	 var idProofErrorMsg = 'Please upload jpeg,jpg,png or svg only';
	 
	if(ids == 'idDoc1Status'){
		fileValidation('idDoc0',allowedFileExtensions,fileErrorMsg);
	}else if(ids == 'idDoc2Status'){
		fileValidation('idDoc1',allowedIdProofExtensions,idProofErrorMsg);
	}else if(ids == 'idDoc3Status'){
		fileValidation('idDoc2',allowedIdProofExtensions,idProofErrorMsg);
	} // End Dattatray
}

function fillFileStatus1(ids,fileIds){
	//alert("ids/712=="+fileIds);
	document.getElementById(ids).value=1;
	 
	 var allowedDocExtensions = /(\.jpeg|\.jpg|\.png|\.tif|\.svg|\.svgz|\.doc|\.docs|\.docx|\.pdf)$/i
	 var docErrorMsg = 'Please upload jpeg,jpg,png,tif,svg,svgz,pdf,docx,docs or doc only';
	
	fileValidation(fileIds,allowedDocExtensions,docErrorMsg);
	
}

//Start Dattatray Date:30-June-2021
function fileValidation(documentID,allowedFileExtensions,errorMsg) {
    var fileInput = document.getElementById(documentID);
    var filePath = fileInput.value;
    var allowedExtensions = allowedFileExtensions;
      
    if (!allowedExtensions.exec(filePath)) {
        alert(errorMsg);
        fileInput.value = '';
        return false;
    } 
}// End Dattatray

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

//====start parvez on 02-07-2021===== 
function addSkills() {
	
	cnt++;
	var trTag = document.createElement("tr");
    trTag.id = "row_skill"+cnt;
    trTag.setAttribute("class", "row_skill");
	trTag.innerHTML = 	"<%=request.getAttribute("sbSkills")%>" +
    			    	"<td style='padding-left: 17px;'><a href=\"javascript:void(0)\" onclick=\"addSkills()\" class=\"add-font\"></a>" +
    			    	"<a href=\"javascript:void(0)\" onclick=\"removeSkills(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a></td>"; 
    document.getElementById("table-skills").appendChild(trTag);
    
}

function removeSkills(removeId) {
	
	var remove_elem = "row_skill"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("table-skills").removeChild(row_skill);
	
}
//====end parvez on 02-07-2021===== 

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
	divTag.innerHTML = 	"<table class=\"table table_no_border\">"+
			"<tr><td><input type=\"text\" style=\"height: 25px; width: 180px; \" name=\"hobbyName\"></input></td>" +   			    	
			"<td><a href=\"javascript:void(0)\" onclick=\"addHobbies()\" class=\"add-font\"></a></td>" +
			"<td><a href=\"javascript:void(0)\" onclick=\"removeHobbies(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a></td></tr>" +
		"</table>";
    document.getElementById("div_hobbies").appendChild(divTag);
}

function removeHobbies(removeId) {
	var remove_elem = "row_hobby"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_hobbies").removeChild(row_skill);
	
}

function getState(country){
	var action= 'GetStateDetails.action?type=candidate&country_id=' + country;
	getContent('statetdid', action); 
}

function getState1(country){
	var action= 'GetStateDetails.action?type=candidate1&country_id=' + country;
	getContent('statetdid1', action); 
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
	divTag.innerHTML = 	"<table class=\"table table_no_border\">" +
	                    "<tr><td><input type=\"text\" style=\"height: 25px; width: 180px;\" name=\"languageName\" ></input></td>" + 
 						"<td width=\"50px\" align=\"center\"><input type=\"checkbox\" name=\"isReadcheckbox\" value=\"1\"  id=\"isRead_"+cnt+"\" onchange=\"showHideHiddenField(this.id)\" /></td>" +
						"<input type=\"hidden\" value=\"0\" name=\"isRead\" id=\"hidden_isRead_"+cnt+"\" />" +
						"<td width=\"50px\" align=\"center\"><input type=\"checkbox\" name=\"isWritecheckbox\" value=\"1\" id=\"isWrite_"+cnt+"\" onchange=\"showHideHiddenField(this.id)\" /></td>"+
						"<input type=\"hidden\" value=\"0\" name=\"isWrite\" id=\"hidden_isWrite_"+cnt+"\" />" +
						"<td width=\"50px\" align=\"center\"><input type=\"checkbox\" name=\"isSpeakcheckbox\" value=\"1\" id=\"isSpeak_"+cnt+"\" onchange=\"showHideHiddenField(this.id)\" /></td>"+
						"<input type=\"hidden\" value=\"0\" name=\"isSpeak\" id=\"hidden_isSpeak_"+cnt+"\" />" +
						"<td width=\"50px\" align=\"center\"><a href=\"javascript:void(0)\" onclick=\"addLanguages()\" class=\"add-font\"></a>" +
						"<a href=\"javascript:void(0)\" onclick=\"removeLanguages(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a></td>" +
						"</table>"; 

    document.getElementById("div_language").appendChild(divTag);
    
}

function removeLanguages(removeId) {
	
	var remove_elem = "row_language"+removeId;
	var row_skill = document.getElementById(remove_elem); 
	document.getElementById("div_language").removeChild(row_skill);
	
}


<%-- <% if (alEducation!=null) {%>
/* ===start parvez date: 17-09-2021=== */
	var cnt=<%=alEducation.size()%>;
	var cnt=<%=alEducation.size()-1%>;
/* ===end parvez date: 17-09-2021=== */	
<%}else{%>
	var cnt =0;
<%}%> --%>

function addEducation() {
	
	/* cnt++; */
	
	var cnt = document.getElementById("educationCnt").value;
       	cnt = parseInt(cnt)+1;
           
	var divTag = document.createElement("div");
    divTag.id = "row_education"+cnt;
    divTag.setAttribute("class", "row_education");
  //====start parvez on 02-07-2021===== 
   <%--  divTag.innerHTML = "<table class=\"table table_no_border\"><tr><td><select name=\"degreeName\" style=\"width:110px!important;\" onchange=\"checkEducation(this.value,"+cnt+")\"> "+
					 "<%=request.getAttribute("sbdegreeDuration")%>" +
			"<a href=\"javascript:void(0)\" onclick=\"removeEducation(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a></td></tr>" +
 			" <tr id=\"degreeNameOtherTR"+cnt+"\"  style=\"display:none;\"><td style=\"text-align: right;\">Enter Education :</td><td colspan=\"3\"> " +
			"<input type=\"text\" name=\"degreeNameOther\" style=\"height: 25px;\"></td></tr>"+ 
			"</table>";  --%>
	//====end parvez on 02-07-2021===== 
		
	 divTag.innerHTML = "<table class=\"table table_no_border\"><tr><td><select name=\"degreeName\" style=\"width:110px!important;\" onchange=\"checkEducation(this.value,"+cnt+")\"> "
			+"<%=request.getAttribute("sbdegreeDuration")%>"
			+"<td><input type=\"hidden\" name=\"degreeCertiStatus\" id=\"degreeCertiStatus"+cnt+"\" value=\"0\">"
			+"<div id=\"degreeCertiDiv"+cnt+"\"><input type=\"hidden\" name=\"degreeCertiSubDivCnt"+cnt+"\" id=\"degreeCertiSubDivCnt"+cnt+"\" value=\"0\" />"
			+"<div id=\"degreeCertiSubDiv"+cnt+"_0\">"
			+"<input type=\"file\" accept=\".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf\" name=\"degreeCertificate"+cnt+"\" id=\"degreeCertificate"+cnt+"\" onchange=\"fillFileStatus1('degreeCertiStatus"+cnt+"','degreeCertificate"+cnt+"');\" />"
			+"<a href=\"javascript:void(0)\" onclick=\"addEducationCerti('"+cnt+"')\" class=\"add-font\"></a></div></div></td>"
			+"<td><a href=\"javascript:void(0)\" onclick=\"addEducation()\" class=\"add-font\" ></a>"
			+"<a href=\"javascript:void(0)\" onclick=\"removeEducation(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a></td></tr>" 
 			+" <tr id=\"degreeNameOtherTR"+cnt+"\"  style=\"display:none;\"><td style=\"text-align: right;\">Enter Education :</td><td colspan=\"3\"> " 
			+"<input type=\"text\" name=\"degreeNameOther\" style=\"height: 25px;\"></td></tr>"+ 
			"</table>"; 
		
    document.getElementById("div_education").appendChild(divTag);
    callDatePicker();
}


	function checkEducation(value,count){
		if(value=="other"){
			document.getElementById("degreeNameOtherTR"+count).style.display="table-row";
		} else {
			document.getElementById("degreeNameOtherTR"+count).style.display="none";
		}
		//===start parvez date: 14-09-2021=== 
		if(value.length !== 0 && value !== ""){
			document.getElementById("degreeCertificate"+count).className = 'validateRequired';
		}else{
			$(".validateRequired").prop('required',false);
			document.getElementById("degreeCertificate"+count).classList.remove("validateRequired");
		}
    	 
    	//===end parvez date: 14-09-2021=== 
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
	divTag.setAttribute("style","float: left; width: 100%;");
	divTag.id = "col_family_siblings"+siblingcnt;
    
	//====start parvez on 02-07-2021===== 
    divTag.innerHTML = 	"<%=request.getAttribute("sbSibling")%>" +
    		"<tr><td colspan=\"2\" ><span style=\"float: right;\">"
    		+"<a href=\"javascript:void(0)\" onclick=\"removeSibling(this.id)\" id=\""+siblingcnt+"\" class=\"remove-font\" ></a>"
    		+"<a href=javascript:void(0) onclick=addSibling() class=add-font></a></span></td></tr>" +
            "</table>"; 
    //====end parvez on 02-07-2021===== 

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

divTag.innerHTML = 	"<table class=\"table table_no_border\">" +
					"<tr><td class=\"txtlabel alignRight\"><%=IConstants.DOCUMENT_OTHER%>" +
					"<input type=\"hidden\" name=\"idDocType\" value=\"<%=IConstants.DOCUMENT_OTHER%>\"></input></td>" +
                   	"<td class=\"txtlabel alignRight\"><input type=\"text\" class=\"validateRequired text-input\" style=\"width: 180px; \" name=\"idDocName\"></input></td>" +   			    	
			    	"<td class=\"txtlabel alignRight\"><input type=\"file\" name=\"idDoc\"/></td>"+
			    	"<td><a href=\"javascript:void(0)\" onclick=\"addDocuments()\" class=\"add-font\"></a></td>" +
			    	"<td><a href=\"javascript:void(0)\" onclick=\"removeDocuments(this.id)\" id=\""+cnt+"\" class=\"remove-font\"></a></td></tr>" +
			    	"</table>"; 

			    	
document.getElementById("div_id_docs").appendChild(divTag);

}

function removeDocuments(removeId) {

	var remove_elem = "row_document"+removeId;
	var row_document = document.getElementById(remove_elem); 
	document.getElementById("div_id_docs").removeChild(row_document);

}


<% if (alPrevEmployment!=null) {%>
	
	/* ===start parvez date: 17-09-2021=== */
	<%-- var cnt=<%=alPrevEmployment.size()%>; --%>
	var cnt=<%=alPrevEmployment.size()-1%>;
	/* ===end parvez date: 17-09-2021=== */
	
<%}else{%>

	var cnt =0;

<%}%>

function addPrevEmployment() {
	cnt++;
	var divTag = document.createElement("div");
	divTag.setAttribute("style","float: left;");
	divTag.id = "col_prev_employer"+cnt;
	//====start parvez on 02-07-2021===== 
	<%-- divTag.innerHTML = "<%=request.getAttribute("sbPrevEmployment")%>" + 
			"<tr><td colspan=\"2\" ><span style=\"float: right;\">"
    		+"<a href=\"javascript:void(0)\" onclick=\"removePrevEmployment(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a>"
    		+"<a href=javascript:void(0) onclick=addPrevEmployment() class=add-font></a></span></td></tr>" +
			"</table>";	 --%>
	//====end parvez on 02-07-2021===== 
	
	/* ===start parvez date: 20-09-2021=== */
	divTag.innerHTML = "<table style=\"width:auto\" class=\"table table_no_border form-table\">" 
			+ "<tr><td class=\"txtlabel\" style=\"text-align:right\"> Company Name:</td>"
			+"<td><input type=\"text\" name=\"prevCompanyName\" style=\"height:25px;width:180px;\" name=\"prevCompanyLocation\" onchange=\"prevCompanyExpFile("+cnt+",this.value);\" ></input></td></tr>"
			+"<%=request.getAttribute("sbPrevEmployment")%>"
			+"<tr><td class=\"txtlabel alignRight\"> Experience Letter:</td>"
			+"<td><input type=\"hidden\" name=\"expLetterFileStatus\" id=\"expLetterFileStatus"+cnt+"\" value=\"0\">"
			+"<input type=\"file\" accept=\".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf\" name=\"prevCompanyExpLetter"+cnt+"\" id=\"prevCompanyExpLetter"+cnt+"\" onchange=\"fillFileStatus1('expLetterFileStatus"+cnt+"','prevCompanyExpLetter"+cnt+"');\" />"
			+"</td></tr>"
			+"<tr><td colspan=\"2\" ><span style=\"float: right;\">"
    		+"<a href=\"javascript:void(0)\" onclick=\"removePrevEmployment(this.id)\" id=\""+cnt+"\" class=\"remove-font\" ></a>"
    		+"<a href=javascript:void(0) onclick=addPrevEmployment() class=add-font></a></span></td></tr>" +
			"</table>";	
	/* ===end parvez date: 20-09-2021=== */		
	document.getElementById("div_prev_employment").appendChild(divTag);
	callDatePicker();
}

function removePrevEmployment(removeId) {

	var remove_elem = "col_prev_employer" + removeId;
	var row_document = document.getElementById(remove_elem); 
	document.getElementById("div_prev_employment").removeChild(row_document);

}


function showHideHiddenField(fieldId) {
	
	if(document.getElementById(fieldId).checked) {
		document.getElementById( "hidden_"+fieldId).value="1";
	}else {
		document.getElementById( "hidden_"+fieldId).value="0";
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
		
		document.getElementById("empAddress1Tmp").value = document.getElementById("empAddress1").value;
		document.getElementById("empAddress2Tmp").value = document.getElementById("empAddress2").value;
		document.getElementById("cityTmp").value = document.getElementById("city").value;
		document.getElementById("empPincodeTmp").value = document.getElementById("empPincode").value;
		
		
	}else{
		document.getElementById("empAddress1Tmp").value = '';
		document.getElementById("empAddress2Tmp").value = '';
		document.getElementById("cityTmp").value = '';
		document.getElementById("empPincodeTmp").value = '';
		
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

/* ===start parvez on 05-08-2021=== */
function submitForm(){
	
	document.getElementById('frmAvailibility').submit();
	
	window.setTimeout(function() {  
		parent.window.location="Applications.action";
		}, 500); 
}
/* ===end parvez on 05-08-2021=== */

function checkMailID(value) {
	//alert("value ===> "+value);
     xmlhttp = GetXmlHttpObject();
     if (xmlhttp == null) {
             alert("Browser does not support HTTP Request");
             return;
     } else {
             var xhr = $.ajax({
               url : "EmailValidation.action?candiEmail=" + value,
               cache : false,
               success : function(data) {
               	//alert("data.length ===> "+data.length + "  data ===> "+data);
               	if(data.length > 1){
               		document.getElementById("empEmail").value = "";
                    document.getElementById("emailValidatorMessege").innerHTML = data;
               	}else{
               		document.getElementById("emailValidatorMessege").innerHTML = data;
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

function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;

	   return true;
	}

function showSourceData(value) {
	
	if(value == 2) {
		document.getElementById("tr_ref_details").style.display = 'table-row';
		document.getElementById("tr_other_details").style.display = 'none';
	} else if(value == 8) {
		document.getElementById("tr_ref_details").style.display = 'none';
		document.getElementById("tr_other_details").style.display = 'table-row';
	} else {
		document.getElementById("tr_ref_details").style.display = 'none';
		document.getElementById("tr_other_details").style.display = 'none';
	}
	
}

function checkEmployeeCode(value) {
	//alert("value ===> "+value);
	if(value.trim() == '') {
		document.getElementById("isEmpCode").value = '';
    	document.getElementById("refEmpId").value = '';
 	document.getElementById("empIdMsgSpan").innerHTML = '';
	} else {
	     xmlhttp = GetXmlHttpObject();
	     if (xmlhttp == null) {
	             alert("Browser does not support HTTP Request");
	             return;
	     } else {
	             var xhr = $.ajax({
	               url : "EmailValidation.action?empCode="+value,
	               cache : false,
	               success : function(data) {
	               //	alert("data.length ===> "+data.length + "  data ===> "+data);
	               	var allData = data.split("::::");
	               	document.getElementById("isEmpCode").value = allData[0];
	               	document.getElementById("refEmpId").value = allData[1];
	            	document.getElementById("empIdMsgSpan").innerHTML = allData[2];
	               }
	             });
	     	}
		}
	}
	
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
      
      function checkImageSize(){
 		 if (window.File && window.FileReader && window.FileList && window.Blob){
 	       var fsize = $('#empImage')[0].files[0].size;
 	       var ftype = $('#empImage')[0].files[0].type;
 	       var fname = $('#empImage')[0].files[0].name;
 	       var flag = true;
 	       switch(ftype){
 	           case 'image/png':
 	           case 'image/gif':
 	           case 'image/jpeg':
 	           case 'image/pjpeg':
 	               if(fsize>500000){ //do something if file size more than 1 mb (1048576)
 	                   alert("You are trying to upload a larger file than 500kb.");
 	                   flag = false;
 	               }else{
 	                   //alert(fsize +" bites\nYou are good to go!");
 	                   flag = true;
 	               }
 	               break;
 	           default:
 	               alert('Unsupported File!');
 	           	flag = false;
 	       }
 	       if(flag){
 	       	return true;
 	       } else {
 	       	return false;
 	       }
 	       
 	   }else{
 	       alert("Please upgrade your browser, because your current browser lacks some new features we need!");
 	       return false;
 	   }
 	}
      
      
	function addBackgroundVerificationDocuments(documentcnt, tableCount) {
		documentcnt++;
		var table;
		    if(tableCount == 1) {
		        table = document.getElementById("row_document_table"+tableCount);
		    } else {
		     	table = document.getElementById("row_document_table");
		    }
		    var rowCount = table.rows.length;
            var rowcount1 = rowCount-1; 
            var row = table.insertRow(rowCount);
            row.id = "row_document"+rowCount;
            row.setAttribute("class","otherdocument");
            var cell1 = row.insertCell(0);
            cell1.setAttribute("class", "txtlabel alignRight");
            cell1.setAttribute("style", "text-align: -moz-center");
            cell1.innerHTML ="<td class='txtlabel alignRight'><input type='hidden' name = 'idocTypeOther' value='Other'></input><input type = 'hidden' name='docIdother' value='other' /><input type = 'text' name='otherDocName' id = 'otherDocName_'"+documentcnt+ " required='true' /><span class='hint'>Enter Other Document name.<span class='hint-pointer'>&nbsp;</span></span></td>";
            var cell2 = row.insertCell(1);
            cell2.setAttribute("class", "txtlabel alignRight");
            cell2.setAttribute("style", "text-align: -moz-center");
          		 //cell2.innerHTML = "<td style =\"float:left;margin-left:50px;\"class=\"txtlabel alignRight\"><input type=\"file\" id = \"idOtherDoc"+documentcnt +"\" name=\"idOtherDoc"+documentcnt +"\"onchange= \"fillFileStatus('idOtherDoc"+documentcnt+"Status')"+'"' +"/> <input type=\"hidden\" name=\"idOtherDocStatus"+documentcnt+ "\" id=\"idOtherDoc"+documentcnt+"\" Status\"  value=\"0\"></input></td>";
			cell2.innerHTML = "<td style = 'float:left;margin-left:50px;' class='txtlabel alignRight'> <input type='file' id = 'idDocOther' name='idDocOther' onchange= 'fillFileStatus( \'idDocOtherStatus\') ></input> <input type='hidden' name='idDocStatusOther' id='idDocStatusOther'  value='0'></input></td>";
          	var cell3 = row.insertCell(2);
            cell3.setAttribute("class", "txtlabel alignRight");
            cell3.setAttribute("style", "text-align: -moz-center");
            cell3.innerHTML ="<td class=\"txtlabel alignRight\"></td>";
            var cell4 = row.insertCell(3);
            cell4.setAttribute("class", "txtlabel alignRight");
            cell4.setAttribute("style", "text-align: -moz-center");
            cell4.innerHTML ="<td class=\"txtlabel alignRight\"></td>";
            var cell5 = row.insertCell(4);
       }
	
	/* added by Parvez date: 07-09-2021
	    *	start
	    */
	    function addEducationCerti(count) {
			var cnt = document.getElementById("degreeCertiSubDivCnt"+count).value;
			cnt = parseInt(cnt)+1;
			var divTag = document.createElement("div");
			divTag.id = "degreeCertiSubDiv"+count+"_"+cnt;
			//divTag.setAttribute("style", "float: left;");
			divTag.innerHTML = "<input type=\"file\" accept=\".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf\" name=\"degreeCertificate"+count+"\" id=\"degreeCertificate"+count+"\" onchange=\"fillFileStatus1('degreeCertiStatus"+count+"','degreeCertificate"+count+"');\" required='true' />" +
				"<a href=\"javascript:void(0);\" onclick=\"addEducationCerti('"+count+"');\" class=\"add-font\"></a>"+
				"<a href=\"javascript:void(0);\" onclick=\"removeEducationCerti('"+count+"', 'degreeCertiSubDiv"+count+"_"+cnt+"');\" class=\"remove-font\"></a>";
			document.getElementById("degreeCertiDiv"+count).appendChild(divTag);
			
			document.getElementById("degreeCertiSubDivCnt"+count).value = cnt;
		}
	    
	    function removeEducationCerti(count, removeId) {
			var removeSubDiv = document.getElementById(removeId); 
			document.getElementById("degreeCertiDiv"+count).removeChild(removeSubDiv);
		} 
	    
	  //===start parvez date: 14-09-2021===
	    function validateDegreeCerti(count1,value){
	    	if(value.length !== 0 && value !== ""){
	    		document.getElementById("degreeCertificate"+count1).className = 'validateRequired';
	    	}else{
	    		$(".validateRequired").prop('required',false);
				document.getElementById("degreeCertificate"+count1).classList.remove("validateRequired");
	    	}
	    	
	    }
	    
	    
	    
		function prevCompanyExpFile(fileCnt, inputValue){
			
			if(inputValue.length !== 0 && inputValue !== ""){
				document.getElementById("prevCompanyExpLetter"+fileCnt).className = 'validateRequired';
			}else{
				$(".validateRequired").prop('required',false);
				document.getElementById("prevCompanyExpLetter"+fileCnt).classList.remove("validateRequired");
			}
			
		}
		//===end parvez date: 14-09-2021===
		
	    /* parvez end */

</script>


<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
				<div class="box-header with-border">
                    <h4 class="box-title">
						<%if(session.getAttribute(IConstants.USERID)!=null) { 
						//System.out.println("Operation===> "+request.getParameter("operation"));
						%>
							<%=(request.getParameter("operation")!=null && !request.getParameter("operation").equals(""))?"Edit":"Enter" %> Candidate Detail
						<% } else { %>
							Enter your Details
						<%}%>
					</h4>
				</div>

<%
//System.out.println("AddCandidateByCadi");
	String strEmpType = (String) session.getAttribute("USERTYPE");
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
	
	Map<String, List<String>> hmValidationFields = (Map<String, List<String>> ) request.getAttribute("hmValidationFields");
	String validReqOpt = "";
	String validAsterix = "";
%>


<%if(!"U".equalsIgnoreCase(request.getParameter("operation"))) { %>

<!-- ====start parvez on 01-07-2021 -->
<div class="steps" style="padding-left: 15px; padding-right: 15px;">
<!-- ====end parvez on 01-07-2021 -->
<s:if test="step==1">
  <span class="current"> Personal Information :</span>
  <span class="next"> Background Information :</span>
  <span class="next"> Family Information :</span>
  <span class="next"> Previous Employment :</span>
   <span class="next"> References :</span>
   <span class="next"> Medical Information :</span>
   <span class="next"> Documentation :</span>
   <span class="next"> Availablity :</span>

</s:if>
<s:if test="step==2">
  <span class="prev">Personal Information : </span>
  <span class="current">Background Information : </span>
  <span class="next">Family Information : </span>
  <span class="next">Previous Employment: </span>
   <span class="next">References : </span>
   <span class="next">Medical Information : </span>
   <span class="next">Documentation : </span>
   <span class="next"> Availablity :</span>

</s:if>
<s:if test="step==3">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="current">Family Information : </span>
  <span class="next">Previous Employment: </span>
   <span class="next">References : </span>
   <span class="next">Medical Information : </span>
   <span class="next">Documentation : </span>
   <span class="next"> Availablity :</span>

</s:if>
<s:if test="step==4">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="current">Previous Employment: </span>
   <span class="next">References : </span>
   <span class="next">Medical Information : </span>
   <span class="next">Documentation : </span>
   <span class="next"> Availablity :</span>

</s:if>
<s:if test="step==5">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="current">References : </span>
   <span class="next">Medical Information : </span>
   <span class="next">Documentation : </span>
   <span class="next"> Availablity :</span>

</s:if>
<s:if test="step==6">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="prev">References : </span>
   <span class="current">Medical Information : </span>
   <span class="next">Documentation : </span>
   <span class="next"> Availablity :</span>

</s:if>
<s:if test="step==7">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="prev">References : </span>
   <span class="prev">Medical Information : </span>
   <span class="current">Documentation : </span>
   <span class="next"> Availablity :</span>

</s:if>

<s:if test="step==8">
  <span class="prev">Personal Information : </span>
  <span class="prev">Background Information : </span>
  <span class="prev">Family Information : </span>
  <span class="prev">Previous Employment: </span>
   <span class="prev">References : </span>
   <span class="prev">Medical Information : </span>
   <span class="prev">Documentation : </span>
      <span class="current"> Availablity :</span>

</s:if>

</div>

<%} %>

<p class="message"><%=strMessage%></p>

<!-- the tabs -->
<ul class="tabs">

	<s:if test="step==1 || mode=='report'">
		<li><a href="#tab1">Personal Information of <%=uF.showData((String)request.getAttribute("CandidateName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==2 || mode=='report'">
		<li><a href="#tab4">Background Information of <%=uF.showData((String)request.getAttribute("CandidateName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==3 || mode=='report'">
		<li><a href="#tab5">Family Information of <%=uF.showData((String)request.getAttribute("CandidateName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==4 || mode=='report'">
		<li><a href="#tab6">Previous Employment of <%=uF.showData((String)request.getAttribute("CandidateName"), "") %></a></li>
	</s:if>
	
	<s:if test="step==5 || mode=='report'">
		<li><a href="#tab2">References of <%=uF.showData((String)request.getAttribute("CandidateName"), "") %></a></li>
	</s:if>

	<s:if test="step==6 || mode=='report'">
		<li><a href="#tab7">Medical Information of <%=uF.showData((String)request.getAttribute("CandidateName"), "") %></a></li>
	</s:if>
		
	<s:if test="step==7 || mode=='report'">
		<li><a href="#tab8">Documentation of <%=uF.showData((String)request.getAttribute("CandidateName"), "") %></a></li>
	</s:if>
	
 	<s:if test="step==8 || mode=='report'">
		<li><a href="#">Availability for Interview of <%=uF.showData((String)request.getAttribute("CandidateName"), "") %></a></li>
	</s:if> 
	
</ul>

<!-- tab "panes" -->
	<!-- ====start parvez on 01-07-2021 -->
<div class="panes" style="padding-left: 15px; padding-right: 15px;">
<!-- ====end parvez on 01-07-2021 -->
	<s:if test="step==1 || mode=='report'">
		<div >

		<s:form theme="simple" action="AddCandidateByCadi" name="frmPersonalInfo" id="frmPersonalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkImageSize();">
			<s:hidden name="show"></s:hidden>
			
			<s:hidden name="operation" />
			<s:hidden name="recruitId" />
			<s:hidden name="CandidateId" />
			<s:hidden name="mode" />	
			<s:hidden name="step" />
		    <s:hidden name="show"></s:hidden>
			<s:hidden name="jobcode" />
			
			<div style="float: center;" >
			<table border="0" class="table table_no_border form-table">
			
			<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px; padding:5px;">Step 1: </span> Enter Candidate Personal Information</td></tr>
			<tr>
			<% 	List<String> candiSalutationValidList = hmValidationFields.get("CANDI_SALUTATION"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSalutationValidList != null && uF.parseToBoolean(candiSalutationValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Salutation:<%=validAsterix %></td>
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<% if(candiSalutationValidList != null && uF.parseToBoolean(candiSalutationValidList.get(0))) { %>
				<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation" listKey="salutationId" listValue="salutationName" cssClass="validateRequired"></s:select>
			<% } else { %>
				<s:select list="salutationList" name="salutation" headerKey="" headerValue="Select Salutation" listKey="salutationId" listValue="salutationName"></s:select>
			<% } %>
				
			<%}else{%>
				<s:textfield name="salutation" required="true" disabled="true"/>
				<s:hidden name="salutation" />
			<%}%>
			<span class="hint">Employee's salutation.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empFname</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiFNameValidList = hmValidationFields.get("CANDI_FIRST_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiFNameValidList != null && uF.parseToBoolean(candiFNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">First Name:<%=validAsterix %></td>
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<input type="text" name="empFname" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empFname"), "") %>" style="height: 25px; width: 206px;" />
			<%}else{%>
				<s:textfield name="empFname" required="true" disabled="true"/>
				<s:hidden name="empFname" />
			<%}%>
			<span class="hint">Candidate's first name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr>
			<% 	List<String> candiMNameValidList = hmValidationFields.get("CANDI_MIDDLE_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMNameValidList != null && uF.parseToBoolean(candiMNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Middle Name:<%=validAsterix %></td>
			<td>
			<% if(session.getAttribute("isApproved")==null) { %>
				<input type="text" name="empMname" class="<%=validReqOpt %>" style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empMname"), "") %>" />
			<% } else { %>
				<s:textfield name="empMname" cssStyle="height: 25px; width: 206px;" required="true" disabled="true"/>
				<s:hidden name="empMname" />
			<% } %>
			<span class="hint">Candidate's Middle name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empLname</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiLNameValidList = hmValidationFields.get("CANDI_LAST_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiLNameValidList != null && uF.parseToBoolean(candiLNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Last Name:<%=validAsterix %></td>
			<td>
			<%if(session.getAttribute("isApproved")==null) {%>
				<input type="text" name="empLname"  class="<%=validReqOpt %>" style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empLname"), "") %>" />
			<%}else{%>
				<s:textfield name="empLname" required="true" disabled="true"/>
				<s:hidden name="empLname" />
			<%}%>
			<span class="hint">Candidate's last name.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empEmail</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiPersonalEmailIdValidList = hmValidationFields.get("CANDI_PERSONAL_EMAIL_ID"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPersonalEmailIdValidList != null && uF.parseToBoolean(candiPersonalEmailIdValidList.get(0))) {
					validReqOpt = "validate[required,custom[email]]";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight" >Personal Email Id:<%=validAsterix %></td>
			<td>
			
			<%if(session.getAttribute("isApproved")==null) {%>
				<input type="text" name="empEmail" id="empEmail" class="<%=validReqOpt %>" style="height: 25px; width: 206px;"  value="<%=uF.showData((String)request.getAttribute("empEmail"), "") %>" onchange="checkMailID(this.value);"/>  <!-- getContent('emailValidatorMessege','EmailValidation.action?candiEmail='+this.value) -->
			<%}else{%>
				<s:textfield name="empEmail" id="empEmail" required="true" disabled="true"/>
				<s:hidden name="empEmail" id="empEmail"/>
			<%}%>
			
			<span class="hint">Email id is required as the user will received all information on this id.<span class="hint-pointer">&nbsp;</span></span></td></tr>
			
			<tr><td>&nbsp;</td> <td><div id="emailValidatorMessege" style="font-size: 12px;"></div></td></tr>
			
			<tr><td colspan=2 style="font-size: 14px;">Permanent Address:
			<hr style="background-color:#346897;height:1px">&nbsp;<s:fielderror ><s:param>empAddress1</s:param></s:fielderror></td></tr>
			
			<tr>
			<% 	List<String> candiPAddress1ValidList = hmValidationFields.get("CANDI_PERMANENT_ADDRESS_1"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPAddress1ValidList != null && uF.parseToBoolean(candiPAddress1ValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Address1:<%=validAsterix %></td>
			<td><input type="text" name="empAddress1" id="empAddress1" class="<%=validReqOpt %>" style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empAddress1"), "") %>" /><span class="hint">Candidate current address.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
			<% 	List<String> candiPAddress2ValidList = hmValidationFields.get("CANDI_PERMANENT_ADDRESS_2"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPAddress2ValidList != null && uF.parseToBoolean(candiPAddress2ValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Address2:<%=validAsterix %></td>
			<td><input type="text" name="empAddress2" id="empAddress2" class="<%=validReqOpt %>" style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empAddress2"), "") %>"/><span class="hint">Candidate current address. (optional)<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>city</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiPSuburbValidList = hmValidationFields.get("CANDI_PERMANENT_SUBURB"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPSuburbValidList != null && uF.parseToBoolean(candiPSuburbValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Suburb:<%=validAsterix %></td>
			<td><input type="text" name="city" id="city"  class="<%=validReqOpt %>"  style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("city"), "") %>"/><span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>country</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiPCountryValidList = hmValidationFields.get("CANDI_PERMANENT_COUNTRY"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPCountryValidList != null && uF.parseToBoolean(candiPCountryValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Select Country:<%=validAsterix %></td>
			<td>
			<% if(candiPCountryValidList != null && uF.parseToBoolean(candiPCountryValidList.get(0))) { %>
				<s:select id="country" cssClass="validateRequired" name="country" listKey="countryId" listValue="countryName" headerKey=""
				 headerValue="Select Country" 	onchange="getContentAcs('statetdid','GetStates.action?country='+this.value+'&type=PADD&validReq=1');" list="countryList" key="" required="true" />
			<% } else { %>
				<s:select id="country" name="country" listKey="countryId" listValue="countryName" headerKey="" headerValue="Select Country" 
				onchange="getContentAcs('statetdid','GetStates.action?country='+this.value+'&type=PADD');" list="countryList" key="" required="true" />
			<% } %>		 
			<span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span>
			</td>
			</tr>
					
			<tr><td colspan=2><s:fielderror ><s:param>state</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiPStateValidList = hmValidationFields.get("CANDI_PERMANENT_STATE"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPStateValidList != null && uF.parseToBoolean(candiPStateValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Select State:<%=validAsterix %></td>
			<td id="statetdid">
			<% if(candiPStateValidList != null && uF.parseToBoolean(candiPStateValidList.get(0))) { %>
					<s:select theme="simple" title="state" cssClass="validateRequired" id="state" name="state" listKey="stateId" 
					listValue="stateName" headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
			<% } else { %>
					<s:select theme="simple" title="state" id="state" name="state" listKey="stateId" listValue="stateName" headerKey="" 
					headerValue="Select State" list="stateList" key="" required="true" />
			<% } %>
			<span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
			</td>
			</tr>
			
			<tr>
			<% 	List<String> candiPPostcodeValidList = hmValidationFields.get("CANDI_PERMANENT_POSTCODE"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPPostcodeValidList != null && uF.parseToBoolean(candiPPostcodeValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
		<!-- ===start parvez date: 30-07-2022=== -->	
			<%-- <td class="txtlabel alignRight">Postcode:<%=validAsterix %></td> --%>
			<td class="txtlabel alignRight">Pincode:<%=validAsterix %></td>
		<!-- ===end parvez date: 30-07-2022=== -->	
			<td><input type="text" name="empPincode" id="empPincode" label="Candidate Pincode" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empPincode"), "") %>" style="height: 25px; width: 206px;" /><span class="hint">Candidate's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>

			<tr><td style="border-bottom:1px solid #346897; font-size: 14px;">Temporary Address:</td>
			<td style="border-bottom:1px solid #346897; font-size: 14px;">
			<div style="float:left;">
			<input type="checkbox" onclick="copyAddress(this);" />Same as above</div>
			</td></tr>
			
			<tr>
			<% 	List<String> candiTAddress1ValidList = hmValidationFields.get("CANDI_TEMPORARY_ADDRESS_1"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiTAddress1ValidList != null && uF.parseToBoolean(candiTAddress1ValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Address1:<%=validAsterix %></td>
			<td><input type="text" name="empAddress1Tmp" id="empAddress1Tmp" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empAddress1Tmp"), "") %>" style="height: 25px; width: 206px;"/><span class="hint">Candidate current address.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
			<% 	List<String> candiTAddress2ValidList = hmValidationFields.get("CANDI_TEMPORARY_ADDRESS_2"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiTAddress2ValidList != null && uF.parseToBoolean(candiTAddress2ValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Address2:<%=validAsterix %></td>
			<td><input type="text" name="empAddress2Tmp" id="empAddress2Tmp" class="<%=validReqOpt %>" style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empAddress2Tmp"), "") %>"/><span class="hint">Candidate current address. (optional)<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
			<% 	List<String> candiTSuburbValidList = hmValidationFields.get("CANDI_TEMPORARY_SUBURB"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiTSuburbValidList != null && uF.parseToBoolean(candiTSuburbValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Suburb:<%=validAsterix %></td>
			<td><input type="text" name="cityTmp" id="cityTmp" class="<%=validReqOpt %>"  value="<%=uF.showData((String)request.getAttribute("cityTmp"), "") %>" style="height: 25px; width: 206px;" /><span class="hint">Add suburb.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
			<% 	List<String> candiTCountryValidList = hmValidationFields.get("CANDI_TEMPORARY_COUNTRY"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiTCountryValidList != null && uF.parseToBoolean(candiTCountryValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Select Country:<%=validAsterix %></td>
			<td>
				<% if(candiTCountryValidList != null && uF.parseToBoolean(candiTCountryValidList.get(0))) { %>
					<s:select id="countryTmp" cssClass="validateRequired" name="countryTmp" listKey="countryId" listValue="countryName"
				 	headerKey="" headerValue="Select Country" onchange="getContentAcs('statetdid1','GetStates.action?country='+this.value+'&type=TADD&validReq=1');" list="countryList" key="" required="true" />
			    <% } else { %>
					<s:select id="countryTmp" name="countryTmp" listKey="countryId" listValue="countryName" headerKey="" headerValue="Select Country" 
					onchange="getContentAcs('statetdid1','GetStates.action?country='+this.value+'&type=TADD');" list="countryList" key="" required="true" />
				<% } %>	
				 <span class="hint">Select country.<span class="hint-pointer">&nbsp;</span></span>
			 </td>
			 </tr>
			 
			<tr>
			<% 	List<String> candiTStateValidList = hmValidationFields.get("CANDI_TEMPORARY_STATE"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiTStateValidList != null && uF.parseToBoolean(candiTStateValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Select State:<%=validAsterix %></td>
			<td id="statetdid1">
			<% if(candiTStateValidList != null && uF.parseToBoolean(candiTStateValidList.get(0))) { %>
				<s:select theme="simple" title="state" cssClass="validateRequired" id="stateTmp" name="stateTmp" listKey="stateId" 
				listValue="stateName" headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
			<% } else { %>
				<s:select theme="simple" title="state" id="stateTmp" name="stateTmp" listKey="stateId" listValue="stateName" headerKey="" 
				headerValue="Select State" list="stateList" key="" required="true" />
			<% } %>	
				<span class="hint">Select state.<span class="hint-pointer">&nbsp;</span></span>
			</td>
			</tr>
			<% 	List<String> candiTPostcodeValidList = hmValidationFields.get("CANDI_TEMPORARY_POSTCODE"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiTPostcodeValidList != null && uF.parseToBoolean(candiTPostcodeValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<tr>
			<!-- ===start parvez date: 30-07-2022=== -->
				<%-- <td class="txtlabel alignRight">Postcode:<%=validAsterix %></td> --%>
				<td class="txtlabel alignRight">Pincode:<%=validAsterix %></td>
			<!-- ===end parvez date: 30-07-2022=== -->	
				<td><input type="text" name="empPincodeTmp" id="empPincodeTmp" label="Candidate Pincode"  class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empPincodeTmp"), "") %>" style="height: 25px; width: 206px;"/><span class="hint">Candidate's residential pincode/zipcode.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>

			<tr>
			<% 	List<String> candiLandlineNoValidList = hmValidationFields.get("CANDI_LANDLINE_NO"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiLandlineNoValidList != null && uF.parseToBoolean(candiLandlineNoValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Landline Number:<%=validAsterix %></td>
			<td><input type="text" name="empContactno" class="<%=validReqOpt %>"  style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empContactno"), "") %>"/><span class="hint">Candidate's contact no. (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
			<% 	List<String> candiMobileNoValidList = hmValidationFields.get("CANDI_MOBILE_NO"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMobileNoValidList != null && uF.parseToBoolean(candiMobileNoValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Mobile Number:<%=validAsterix %></td>
			<td><input type="text" name="empMobileNo" class="<%=validReqOpt %>" value="<%=uF.showData((String)request.getAttribute("empMobileNo"), "") %>"  style="height: 25px; width: 206px;"/><span class="hint">Candidate's Mobile No<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
		<%-- 	
			<tr><td class="txtlabel alignRight">Emergency Contact Name<sup>*</sup>:</td><td><s:textfield cssClass="validateRequired text-input" name="empEmergencyContactName" />
			<tr><td colspan=2><s:fielderror ><s:param>empEmergencyContactNo</s:param></s:fielderror></td></tr>
			<tr><td class="txtlabel alignRight">Emergency Contact Number<sup>*</sup>:</td><td><s:textfield name="empEmergencyContactNo" cssClass="validateRequired text-input"/></td></tr>
			
			<tr><td class="txtlabel alignRight">PAN<sup>*</sup>:</td><td><s:textfield name="empPanNo" cssClass="validateRequired text-input" /><span class="hint">Candidate's PAN (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">Provident Fund No :</td><td><s:textfield name="empPFNo" /><span class="hint">Candidate's Provident Number (optional but recommended)<span class="hint-pointer">&nbsp;</span></span></td></tr>
			<tr><td class="txtlabel alignRight">GPF Acc No :</td><td><s:textfield name="empGPFNo" /><span class="hint">Candidate's GPF Number <span class="hint-pointer">&nbsp;</span></span></td></tr>
		 --%>	
			
			<tr>
			<% 	List<String> candiPassportNoValidList = hmValidationFields.get("CANDI_PASSPORT_NO"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPassportNoValidList != null && uF.parseToBoolean(candiPassportNoValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Passport Number:<%=validAsterix %></td>
			<td><input type="text" name="empPassportNo"  class="<%=validReqOpt %>" style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empPassportNo"), "") %>"/></td>
			</tr>
					
			<tr>
			<% 	List<String> candiPassportExpiryDateValidList = hmValidationFields.get("CANDI_PASSPORT_EXPIRY_DATE"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiPassportExpiryDateValidList != null && uF.parseToBoolean(candiPassportExpiryDateValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Passport Expiry Date:<%=validAsterix %></td>
			<td><input type="text" name="empPassportExpiryDate" class="<%=validReqOpt %>" style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empPassportExpiryDate"), "") %>" /></td>
			</tr>
			
			<tr>
			<% 	List<String> candiBloodGroupValidList = hmValidationFields.get("CANDI_BLOOD_GROUP"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiBloodGroupValidList != null && uF.parseToBoolean(candiBloodGroupValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Blood Group:<%=validAsterix %></td>
			<td>
			<% if(candiBloodGroupValidList != null && uF.parseToBoolean(candiBloodGroupValidList.get(0))) { %>
				<s:select theme="simple" name="empBloodGroup" cssClass="validateRequired" listKey="bloodGroupId" listValue="bloodGroupName" headerKey="0"
					 headerValue="Select Blood Group"  list="bloodGroupList" key="" required="true" />
			<% } else { %>
					<s:select theme="simple" name="empBloodGroup" listKey="bloodGroupId" listValue="bloodGroupName" headerKey="0" 
						headerValue="Select Blood Group" list="bloodGroupList" key="" required="true" />
			<% } %>	
			</td>
			</tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empDateOfBirth</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiDateOfBirthValidList = hmValidationFields.get("CANDI_DATE_OF_BIRTH"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiDateOfBirthValidList != null && uF.parseToBoolean(candiDateOfBirthValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Date Of Birth:<%=validAsterix %></td>
			<td>
			<input type="text" name="empDateOfBirth" id="empDateOfBirth" class="<%=validReqOpt %>"  style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empDateOfBirth"), "") %>">
			<span class="hint">Candidate's Date Of Birth.<span class="hint-pointer">&nbsp;</span></span>
			</td>
			</tr>
			
			<tr><td colspan=2><s:fielderror ><s:param>empGender</s:param></s:fielderror></td></tr>
			<tr>
			<% 	List<String> candiGenderValidList = hmValidationFields.get("CANDI_GENDER"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiGenderValidList != null && uF.parseToBoolean(candiGenderValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Gender:<%=validAsterix %></td>
			<td>
			<% if(candiGenderValidList != null && uF.parseToBoolean(candiGenderValidList.get(0))) { %>
				<s:select theme="simple" cssClass="validateRequired" label="Select Gender" name="empGender" listKey="genderId"
				listValue="genderName" headerKey="" headerValue="Select Gender"	list="empGenderList" key="" required="true" />
		    <% } else { %>
				<s:select theme="simple" label="Select Gender" name="empGender" listKey="genderId" listValue="genderName" headerKey="" 
				headerValue="Select Gender" list="empGenderList" key="" required="true" />
			<% } %>
			<span class="hint">Select Gender.<span class="hint-pointer">&nbsp;</span></span>
			</td></tr>
			
			<tr>
			<% 	List<String> candiMaritalStatusValidList = hmValidationFields.get("CANDI_MARITAL_STATUS"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMaritalStatusValidList != null && uF.parseToBoolean(candiMaritalStatusValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Marital Status:<%=validAsterix %></td>
			<td>
			<% if(candiMaritalStatusValidList != null && uF.parseToBoolean(candiMaritalStatusValidList.get(0))) { %>
				<s:select theme="simple" name="empMaritalStatus" cssClass="validateRequired" listKey="maritalStatusId" listValue="maritalStatusName" headerKey="0" 
				headerValue="Select Marital Status" list="maritalStatusList" key="" required="true" onchange="showMarriageDate();"/>
			<% } else { %>
				<s:select theme="simple" name="empMaritalStatus" listKey="maritalStatusId" listValue="maritalStatusName" headerKey="0" 
				headerValue="Select Marital Status" list="maritalStatusList" key="" required="true" onchange="showMarriageDate();"/>
			<% } %>
			</td>
			</tr>
			
			
			<tr id="trMarriageDate">
			<% 	List<String> candiDateOfMarriageValidList = hmValidationFields.get("EMP_DATE_OF_MARRIAGE"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiDateOfMarriageValidList != null && uF.parseToBoolean(candiDateOfMarriageValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Date Of Marriage:<%=validAsterix %></td>
			<td>
			<input type="text" name="empDateOfMarriage" id="empDateOfMarriage" class="<%=validReqOpt %>" style="height: 25px; width: 206px;" value="<%=uF.showData((String)request.getAttribute("empDateOfMarriage"), "") %>">
			<span class="hint">Candidate's Date Of Marriage.<span class="hint-pointer">&nbsp;</span></span>
			</td>
			</tr>
			
			<tr><td colspan=2 style="border-bottom:1px solid #346897; font-size: 14px;">Employement Status & Expectations: </td></tr>
			<%-- <tr><td class="txtlabel alignRight" valign="top">Availability:</td>
				<td> <s:radio name="availability" id="availability" list="#{'1':'Yes','0':'No'}" value="{strAvailability}" /></td>
			</tr> --%>
			
		 	<tr><td class="txtlabel alignRight" valign="top">Source:<sup>*</sup></td>
				<td> <s:hidden name="availability" id="availability" value="1"></s:hidden>
					<s:select theme="simple" name="candiSource" cssClass="validateRequired" listKey="sourceId" listValue="sourceName" headerKey="" 
						headerValue="Select Source" list="sourceList" key="" required="true" onchange="showSourceData(this.value);" />
				</td>
			</tr>
			<%	
				String displayRef = "none";
				String displayOther = "none";
			    String refEmp = (String)request.getAttribute("refEmpCode");
			    String otherSrc = (String)request.getAttribute("otherRefSrc");
				if(refEmp != null && !refEmp.equals("")) {
					displayRef = "table-row";
					displayOther ="none";
				}
				
				if(otherSrc != null && !otherSrc.equals("")) {
					displayOther = "table-row";
					displayRef ="none";
				}
			%>
			<tr id = "tr_ref_details" style="<%=displayRef%>;">
				<td class="txtlabel alignRight">Ref. Employee Code:<sup>*</sup></td>
				<td> 
					<span style="float: left;"><s:hidden name="isEmpCode" id="isEmpCode"/><s:hidden name="refEmpId" id="refEmpId"/>
						<input type="text" name="refEmpCode" id="refEmpCode" class="validateRequired" style="height: 25px; width: 206px;" onchange="checkEmployeeCode(this.value);"/>
					</span>
					<span id="empIdMsgSpan" style="width: 30px; float: left;"></span>
				</td>
			</tr>
			
			<tr id = "tr_other_details" style="<%=displayOther%>">
				<td class="txtlabel alignRight"><sup>*</sup></td>
				<td> <input type="text" name="otherRefSrc" id="otherRefSrc" class="validateRequired" style="height: 25px; width: 206px;"/></td>
			</tr>
			 
			<tr>
			<% 	List<String> candiCurrentCTCValidList = hmValidationFields.get("CANDI_CURRENT_CTC"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiCurrentCTCValidList != null && uF.parseToBoolean(candiCurrentCTCValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Current CTC:<%=validAsterix %></td>
			<td><input type="text" name="candiCurrCTC" class="<%=validReqOpt %>" style="height: 25px;" onkeypress="return isNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("candiCurrCTC"), "") %>"/> LPA</td>
			</tr>
			<%-- <tr>
			<% 	List<String> candiExpectedCTCValidList = hmValidationFields.get("CANDI_EXPECTED_CTC"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiExpectedCTCValidList != null && uF.parseToBoolean(candiExpectedCTCValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Expected CTC:<%=validAsterix %></td>
			<td><input type="text" name="candiExpectedCTC" class="<%=validReqOpt %>" style="height: 25px;" onkeypress="return isNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("candiExpectedCTC"), "") %>"/></td>
			</tr> --%>
			<tr>
			<% 	List<String> candiNoticePeriodValidList = hmValidationFields.get("CANDI_NOTICE_PERIOD"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiNoticePeriodValidList != null && uF.parseToBoolean(candiNoticePeriodValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Notice Period:<%=validAsterix %></td>
			<td><input type="text" name="candiNoticePeriod" class="<%=validReqOpt %>" style="height: 25px;" onkeypress="return isNumberKey(event)" value="<%=uF.showData((String)request.getAttribute("candiNoticePeriod"), "") %>"/> days</td>
			</tr>
			
			<!-- ====start parvez on 01-07-2021 -->
			<tr>
				<td colspan=2 style="border-bottom:1px solid #346897; font-size: 14px;"><strong>Update Candidate image</strong></td>
			</tr>
				<tr><td></td><td><img height="100" width="100" id="profilecontainerimg" style="border:1px solid #CCCCCC;" src="userImages/<%=strImage!=null? strImage:"avatar_photo.png"%>" /></td> </tr>
			    <tr>
			    	<% List<String> candiProfilePhotoValidList = hmValidationFields.get("CANDI_PROFILE_PHOTO"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiProfilePhotoValidList != null && uF.parseToBoolean(candiProfilePhotoValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
				%>
				<td></td>
			    	<td>
			    	 <% if(candiProfilePhotoValidList != null && uF.parseToBoolean(candiProfilePhotoValidList.get(0))) { %>
			    		<s:file name="empImage" id="empImage" cssClass="validateRequired"></s:file>
			    	<% } else { %>
				     	<s:file name="empImage" id="empImage"/>
				     <% } %>
			    		<span style="color:#efefef">Image size must be smaller than or equal to 500kb.</span>
			    	</td>
			  </tr>
			<!-- ====end parvez on 01-07-2021 -->
		</table>
	</div>
            
			<%-- <div style="float:left; padding:10px;  margin: 80px 100px; padding: 10px 30px;" >
				<table class="table table_no_border">
				<tr><td style="font-size: 14px;"><strong>Update Candidate image</strong></td></tr>
				<tr><td><img height="100" width="100" id="profilecontainerimg" style="border:1px solid #CCCCCC;" src="userImages/<%=strImage!=null? strImage:"avatar_photo.png"%>" /></td> </tr>
			    <tr>
			    	<% List<String> candiProfilePhotoValidList = hmValidationFields.get("CANDI_PROFILE_PHOTO"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiProfilePhotoValidList != null && uF.parseToBoolean(candiProfilePhotoValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
				%>
			    	<td>
			    	 <% if(candiProfilePhotoValidList != null && uF.parseToBoolean(candiProfilePhotoValidList.get(0))) { %>
			    		<s:file name="empImage" id="empImage" cssClass="validateRequired"></s:file>
			    	<% } else { %>
				     	<s:file name="empImage" id="empImage"/>
				     <% } %>
			    		<span style="color:#efefef">Image size must be smaller than or equal to 500kb.</span>
			    	</td>
			    </tr>
				</table>
			
			</div> --%>
			
			
			
			<div style="float:right;">
				<table class="table table_no_border">
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
			<!-- ====start parvez on 01-07-2021 -->
		<div class="clr"></div>	
		<!-- ====end parvez on 01-07-2021 -->
		</s:form>
		</div>
	</s:if>
	
	

<s:if test="step==2 || mode=='report'">
<div>
	<s:form theme="simple" action="AddCandidateByCadi" id="frmBackgroundInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
	
	<s:hidden name="operation" /><s:hidden name="recruitId" /><s:hidden name="CandidateId" />
	<s:hidden name="mode" />	<s:hidden name="step" />
    <s:hidden name="show"></s:hidden>
    <div><span style="color:#68AC3B; font-size:18px">Step 2: </span><span class="tdLabelheadingBg" style="font-weight: 600;font-size: 16px;">Enter Candidates background information</span> </div>
    
    <!-- ====start parvez on 02-07-2021=====  -->
    <div class="row row_without_margin">
                    <div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
	<div  id="div_skills">
    
        <h3 style="padding:0px;margin:5px 0px 10px 0px;background: aliceblue;">Enter Candidate skills and their values</h3>
          <% List<String> candiSkillNameValidList = hmValidationFields.get("CANDI_SKILL_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSkillNameValidList != null && uF.parseToBoolean(candiSkillNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			
			<% 
				String validReqOpt1 = "";
				String validAsterix1 = "";
				List<String> candiSkillRatingValidList = hmValidationFields.get("CANDI_SKILL_RATING"); 
				if(candiSkillRatingValidList != null && uF.parseToBoolean(candiSkillRatingValidList.get(0))) {
					validReqOpt1 = "validateRequired";
					validAsterix1 = "<sup>*</sup>";
				}
			%>  
           <%-- <div id="row_skill" >  
			<div style="float:left; width:45%;margin-left:10%"><label>Skill Name<%=validAsterix %></label></div>
			<div style="float:left; width:40%;"><label>Skill Rating<%=validAsterix1 %></label></div>
		   </div>	
			<% 	
				if(alSkills!=null && alSkills.size()!=0){
					String empId = (String)((ArrayList)alSkills.get(0)).get(3);
				
			%>
			
			<% 
			 	for(int i=0; i<alSkills.size(); i++) {
			%>
				
				<div id="row_skill<%=((ArrayList)alSkills.get(i)).get(0)%>" class="row_skill">
					
                    <table class="table table_no_border" style="font-size: 12px;">
					<tr>
						<td>
						<%if(i==0){ %>
							[PRI]
						<%}else{ %>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<%} %>
							<select name="skillName" class="<%=validReqOpt %>">
			                	<%
			                	//System.out.println("skillsList ===> "+skillsList);
			                	//System.out.println("alSkills ===> "+alSkills);
			                	for(int k=0; k< skillsList.size(); k++) { 
			                		if( (((FillSkills)skillsList.get(k)).getSkillsId()+"").equals((String)((ArrayList)alSkills.get(i)).get(1) )) {
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
						<input type="text" style="width: 180px;"name="skillName" value="<%=((ArrayList)alSkills.get(i)).get(1)%>" ></input>
						</td>
						<td>
							<select name="skillValue" class="<%=validReqOpt1 %>" style="width: 105px;">
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
			    	    <input type="text" style="width: 180px;"name="skillValue" value="<%=((ArrayList)alSkills.get(i)).get(2)%>"></input>
			    	    </td>
			    	    <td><a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a></td>
			            <%if(i>0){ %>
			            <td><a href="javascript:void(0)" onclick="removeSkills(this.id)" id=<%=((ArrayList)alSkills.get(i)).get(0)%> class="remove-font" ></a></td>
			            <% } %>
                     </tr>   
			    	</table>
			    </div>
			 <%}
			 }else {
			 %>
			 	<div id="row_skill" class="row_skill">
                    <table class="table table_no_border" style="font-size: 12px;">
                        <tr>
                        	<td>
                        	[Pri]
	                        	<select name="skillName" class="<%=validReqOpt %>">
	                        		<option value="">Select Skill Name</option>
				                	<%for(int k=0; k< skillsList.size(); k++) {%> 
				                		
				                		<option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>">
				                			<%=((FillSkills)skillsList.get(k)).getSkillsName() %>
				                		</option>
				                	<%}%>
			                	</select>
                        	</td>
                        	<td>
	                        	<select name="skillValue" class="<%=validReqOpt1 %>"style="width: 105px;">
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
                            <td><a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a></td>
                        </tr>   
                    </table>
				</div>
			    	
			 <%}%> --%>
			 
			 
			 <div>
				 <table class="table table-head-highlight"> 
				 	<tbody id="table-skills">
				
						<tr id="row_skill">
							<th></th>
							<th>Skill Name<%=validAsterix %></th>
							<th>Skill Rating<%=validAsterix1 %></th>
							<th style="width:10%"></th>
						</tr>
			  
				<% 	
					if(alSkills!=null && alSkills.size()!=0){
						String empId = (String)((ArrayList)alSkills.get(0)).get(3);
					
				%>
				
				<% 
				 	for(int i=0; i<alSkills.size(); i++) {
				%>
					
				
					<tr id="row_skill<%=((ArrayList)alSkills.get(i)).get(0)%>" class="row_skill">
						
							<td>
							<%if(i==0){ %>
								[PRI]
							<%}else{ %>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<%} %>
							</td>
							<td>
								<select name="skillName" class="<%=validReqOpt %>">
				                	<%
				                	//System.out.println("skillsList ===> "+skillsList);
				                	//System.out.println("alSkills ===> "+alSkills);
				                	for(int k=0; k< skillsList.size(); k++) { 
				                		if( (((FillSkills)skillsList.get(k)).getSkillsId()+"").equals((String)((ArrayList)alSkills.get(i)).get(1) )) {
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
							<input type="text" style="width: 180px;"name="skillName" value="<%=((ArrayList)alSkills.get(i)).get(1)%>" ></input>
							</td>
							<td>
								<select name="skillValue" class="<%=validReqOpt1 %>" style="width: 105px ;">
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
				    	    <input type="text" style="width: 180px;"name="skillValue" value="<%=((ArrayList)alSkills.get(i)).get(2)%>"></input>
				    	    </td>
				    	    <td><a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a><!-- </td> -->
				            <%if(i>0){ %>
				            <!-- <td> --><a href="javascript:void(0)" onclick="removeSkills(this.id)" id=<%=((ArrayList)alSkills.get(i)).get(0)%> class="remove-font" ></a>
				            </td>
				            <% } %>
	                     <!-- </tr>   
				    	</table> -->
				    </tr>
				    <!-- </div> -->
				 <%}
				 }else {
				 %>
				 
				 	<tr id="row_skill" class="row_skill">
	                    <!-- <table class="table table_no_border" style="font-size: 12px;">
	                        <tr> -->
	                        	<td>
	                        	[Pri]
	                        	</td>
	                        	<td>
		                        	<select name="skillName" class="<%=validReqOpt %>">
		                        		<option value="">Select Skill Name</option>
					                	<%for(int k=0; k< skillsList.size(); k++) {%> 
					                		
					                		<option value="<%=((FillSkills)skillsList.get(k)).getSkillsId() %>">
					                			<%=((FillSkills)skillsList.get(k)).getSkillsName() %>
					                		</option>
					                	<%}%>
				                	</select>
	                        	</td>
	                        	<td>
		                        	<select name="skillValue" class="<%=validReqOpt1 %>" style="width: 105px;">
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
	                            <td><a href="javascript:void(0)" onclick="addSkills()" class="add-font"></a></td>
	                        
				    </tr>
				 <%}%>
				 </tbody>
			 	</table>
			 </div>
			 
			 </div>
	</div>
	<div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
	<div id="div_education">
	
       <h3 style="padding:5px;margin:5px 0px 10px 0px;background: aliceblue;">Enter Candidate educational details</h3>
        <% List<String> candiDegreeNameValidList = hmValidationFields.get("CANDI_DEGREE_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiDegreeNameValidList != null && uF.parseToBoolean(candiDegreeNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			
			<% 
				String validReqOptDD = "";
				String validAsterixDD = "";
				List<String> candiDegreeDurationValidList = hmValidationFields.get("CANDI_DEGREE_DURATION"); 
				if(candiDegreeDurationValidList != null && uF.parseToBoolean(candiDegreeDurationValidList.get(0))) {
					validReqOptDD = "validateRequired";
					validAsterixDD = "<sup>*</sup>";
				}
			%>
			
			<% 
				String validReqOptDCY = "";
				String validAsterixDCY = "";
				List<String> candiDegreeCompYearValidList = hmValidationFields.get("CANDI_DEGREE_COMPLETION_YEAR"); 
				if(candiDegreeCompYearValidList != null && uF.parseToBoolean(candiDegreeCompYearValidList.get(0))) {
					validReqOptDCY = "validateRequired";
					validAsterixDCY = "<sup>*</sup>";
				}
			%>
			
			<% 
				String validReqOptDG = "";
				String validAsterixDG = "";
				List<String> candiDegreeGradeValidList = hmValidationFields.get("CANDI_DEGREE_GRADE"); 
				if(candiDegreeGradeValidList != null && uF.parseToBoolean(candiDegreeGradeValidList.get(0))) {
					validReqOptDG = "validateRequired";
					validAsterixDG = "<sup>*</sup>";
				}
			%>
			<!-- start parvez date: 07-09-2021 -->
                            <% 
                                String validReqOptNOI = "";
                                String validAsterixDNOI = "";
                                
                                List<String> candiInstituteValidList = hmValidationFields.get("CANDI_NAME_OF_INSTITUTE"); 
                        		
                        		if(candiInstituteValidList != null && uF.parseToBoolean(candiInstituteValidList.get(0))) {
                        			validReqOptNOI = "validateRequired";
                        			validAsterixDNOI = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptB = "";
                                String validAsterixB = "";
                                
                                List<String> candiBoardValidList = hmValidationFields.get("CANDI_DEGREE_BOARD"); 
                        		
                        		if(candiBoardValidList != null && uF.parseToBoolean(candiBoardValidList.get(0))) {
                        			validReqOptB = "validateRequired";
                        			validAsterixB = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptSubject = "";
                                String validAsterixSubject = "";
                                
                                List<String> candiSubjectValidList = hmValidationFields.get("CANDI_DEGREE_SUBJECT"); 
                        		
                        		if(candiSubjectValidList != null && uF.parseToBoolean(candiSubjectValidList.get(0))) {
                        			validReqOptSubject = "validateRequired";
                        			validAsterixSubject = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptDSDate = "";
                                String validAsterixDSDate = "";
                                
                                List<String> candiSDateValidList = hmValidationFields.get("CANDI_DEGREE_START_DATE"); 
                        		
                        		if(candiSDateValidList != null && uF.parseToBoolean(candiSDateValidList.get(0))) {
                        			validReqOptDSDate = "validateRequired";
                        			validAsterixDSDate = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptDCDate = "";
                                String validAsterixDCDate = "";
                                
                                List<String> candiCDateValidList = hmValidationFields.get("CANDI_DEGREE_COMPLETION_DATE"); 
                        		
                        		if(candiCDateValidList != null && uF.parseToBoolean(candiCDateValidList.get(0))) {
                        			validReqOptDCDate = "validateRequired";
                        			validAsterixDCDate = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptDM = "";
                                String validAsterixDM = "";
                                
                                List<String> candiDegreeMarksValidList = hmValidationFields.get("CANDI_DEGREE_MARKS"); 
                        		
                        		if(candiDegreeMarksValidList != null && uF.parseToBoolean(candiDegreeMarksValidList.get(0))) {
                        			validReqOptDM = "validateRequired";
                        			validAsterixDM = "<sup>*</sup>";
                        		}
                                %>
                                
                            <% 
                                String validReqOptCity = "";
                                String validAsterixCity = "";
                                
                                List<String> candiCityValidList = hmValidationFields.get("CANDI_DEGREE_CITY"); 
                        		
                        		if(candiCityValidList != null && uF.parseToBoolean(candiCityValidList.get(0))) {
                        			validReqOptCity = "validateRequired";
                        			validAsterixCity = "<sup>*</sup>";
                        		}
                                %>
                            <% 
                                String validReqOptDCertificate = "";
                                String validAsterixDCertificate = "";
                                
                                List<String> candiDegreeCertificateValidList = hmValidationFields.get("CANDI_EDUCATION_CERTIFICATE"); 
                        		
                        		if(candiDegreeCertificateValidList != null && uF.parseToBoolean(candiDegreeCertificateValidList.get(0))) {
                        			validReqOptDCertificate = "validateRequired";
                        			validAsterixDCertificate = "<sup>*</sup>";
                        		}
                                %>
                          <!-- end parvez date: 07-09-2021 -->
        
		<div id="row_education" class="row_education">  
                <table class="table table_no_border" style="font-size: 12px;">
				<!-- start parvez date: 06-09-2021 -->
				<tr><td width="120px" style="text-align:center" ><label style="width:95px">Degree Name<%=validAsterix %></label></td>
				    <td width="120px" style="text-align:center"><label style="width:95px">Duration<%=validAsterixDD %></label></td>
				    <td width="120px" style="text-align:center"><label style="width:95px">Completion Year<%=validAsterixDCY %></label></td>
			<!-- ===start parvez date: 30-07-2022=== -->	    
				    <td width="120px" style="text-align:center"><label style="width:95px">Grade / Percentage<%=validAsterixDG %></label></td>
			<!-- ===end parvez date: 30-07-2022=== -->	    
					<td width="120px" style="text-align:center"><label style="width:95px">Name of Institute<%=validAsterixDNOI %></label></td>
					<td width="120px" style="text-align:center"><label style="width:95px">Board<%=validAsterixB %></label></td>
					<td width="120px" style="text-align:center"><label style="width:95px">Subject<%=validReqOptSubject %></label></td>
					<td width="120px" style="text-align:center"><label style="width:95px">Start Date<%=validAsterixDSDate %></label></td>
					<td width="120px" style="text-align:center"><label style="width:95px">Completion Date<%=validAsterixDCDate %></label></td>
					<td width="120px" style="text-align:center"><label style="width:95px">Marks / CGPA<%=validAsterixDM %></label></td>
					<td width="120px" style="text-align:center"><label style="width:95px">City<%=validAsterixCity %></label></td>
					<td width="120px" style="text-align:center"><label style="width:95px">Certificate<%=validAsterixDCertificate %></label></td>
					<td></td>
                </tr> 
                <!-- end parvez date: 07-09-2021 -->
                </table>   
		</div>
		<% 	
			if(alEducation!=null && alEducation.size()!=0){
		 	for(int i=0; i<alEducation.size(); i++) {
		%>
				<div id="row_education<%=((ArrayList)alEducation.get(i)).get(0)%>" class="row_education">
                    <table class="table table_no_border">
                      
						
						<tr>
							<td>
						<!-- ===start parvez date: 14-09-2021=== -->
							<input type="hidden" name="degreeId" value="<%=((ArrayList)alEducation.get(i)).get(12) %>"/>
							<select name="degreeName" class="<%=validReqOpt %>"  style="width: 120px !important;" onchange="validateDegreeCerti(<%=i%>,'this.value');">
			            <!-- ===end parvez date: 14-09-2021=== -->
			                	<%for(int k=0; k< educationalList.size(); k++) { 
			                		if( (((FillEducational)educationalList.get(k)).getEduName()+"").equals( (String)((ArrayList)alEducation.get(i)).get(1) )) {
			                	%>
			                		<option value="<%=((FillEducational)educationalList.get(k)).getEduName() %>" selected="selected">
			                		<%=((FillEducational)educationalList.get(k)).getEduName() %>
			                		</option>
			                	<%}else { %>
			                		<option value="<%=((FillEducational)educationalList.get(k)).getEduName() %>" >
			                		<%=((FillEducational)educationalList.get(k)).getEduName() %>
			                		</option>
			                	<% }
			                		}%>
			                	</select>
							
							</td>
			                <td>
			                	<select name="degreeDuration" class="<%=validReqOptDD %>" style="width: 90px !important;">
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
			                	<select name="completionYear" class="<%=validReqOptDCY %>" style="width: 110px !important;">
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
			                
			                <td><input type="text" class="<%=validReqOptDG %>" style="height: 25px; width: 90px !important;" name="grade" value="<%=((ArrayList)alEducation.get(i)).get(4)%>" ></input></td>
			                
			                <!-- start parvez date: 07-09-2021 -->
		                                		
		                                		<td>
		                                			<input type="text"   style="height: 25px; width: 90px !important;" name="instituteName"  id="instituteName" value="<%=((ArrayList)alEducation.get(i)).get(6)%>" class="<%=validReqOptNOI %>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text"    style="height: 25px; width: 90px !important;" name="universityName"  id="universityName" value="<%=((ArrayList)alEducation.get(i)).get(7)%>" class="<%=validReqOptB %>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text"  style="height: 25px; width: 90px !important;" name="subject"  id="subject" value="<%=((ArrayList)alEducation.get(i)).get(5)%>" class="<%=validReqOptSubject %>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" style="height: 25px; width: 90px !important;" name="strStartDate"  id="strStartDate" value="<%=((ArrayList)alEducation.get(i)).get(8)%>" class="<%=validReqOptDSDate%>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" style="height: 25px; width: 90px !important;" name="strCompletionDate" id="strCompletionDate" value="<%=((ArrayList)alEducation.get(i)).get(9)%>" class="<%=validReqOptDCDate%>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text"  style="height: 25px; width: 90px !important;" name="marks" id="marks" value="<%=((ArrayList)alEducation.get(i)).get(10)%>" onkeypress="return isNumberKey(event)" class="<%=validReqOptDM%>" ></input>
		                                		</td>
		                                		<td>
		                                			<input type="text" style="height: 25px; width: 90px !important;" name="city1" id="city1"   value="<%=((ArrayList)alEducation.get(i)).get(11)%>" class="<%=validReqOptCity%>" ></input>
		                                		</td>
		                                		
		                                		<td><input type="hidden" name="degreeCertiStatus" id="degreeCertiStatus<%=i %>" value="0">
													<div id="degreeCertiDiv<%=i %>"><input type="hidden" name="degreeCertiSubDivCnt<%=i %>" id="degreeCertiSubDivCnt<%=i %>" value="0" />
														<div id="degreeCertiSubDiv<%=i %>_0">
															<input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="degreeCertificate<%=i %>" 
																id="degreeCertificate<%=i %>"  onchange="fillFileStatus1('degreeCertiStatus<%=i %>','degreeCertificate<%=i %>');" class="<%=validReqOptDCertificate%>" />
															<a href="javascript:void(0)" onclick="addEducationCerti('<%=i %>')" class="add-font"></a>
														</div>
													</div>
													<!-- ===start parvez date: 18-09-2021=== -->
							                                        
							                         <% if(hmEducationDocs != null && hmEducationDocs.size()>0) {
						                                   List<String> innrList = hmEducationDocs.get(((List) alEducation.get(i)).get(12));
						                                   for(int l=0; innrList != null && l<innrList.size(); l++) {
						                             %>
																
																	<%if(docRetriveLocation == null) { %>
																		<a
																			href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + innrList.get(l)  %>"
																				title="Education Document"><i class="fa fa-file-o"
																				aria-hidden="true"></i>
																		</a>
																	<% } else { %>
																	
																			<a
																				href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_EDUCATION_DOC+"/"+(String)request.getAttribute("CandidateId")+"/"+ innrList.get(l) %>"
																				title="Education Document"><i class="fa fa-file-o"
																				aria-hidden="true"></i>
																			</a>
																			<% } %>
																 <% } %> <% }  %>
							                                       
							                        <!-- ===end parvez date: 18-09-2021=== -->
													
												</td>
		                                		<td>
		                                			<a href="javascript:void(0)" onclick="addEducation()" class="add-font"></a>
			                                        <%if(i>0){ %>
			                                        <a href="javascript:void(0)" onclick="removeEducation(this.id)" id=<%=i%> class="remove-font" ></a>
			                                        <% } %>
		                                		</td>
		                                
							<%-- <td><a href="javascript:void(0)" onclick="addEducation()" class="add-font"></a></td>
							<%if(i>0){ %>
							<td><a href="javascript:void(0)" onclick="removeEducation(this.id)" id=<%=((ArrayList)alEducation.get(i)).get(0)%> class="remove-font" ></a></td>
							<% } %> --%>
						
						<!-- end parvez date: 07-09-2021 -->
							
	                    </tr>
                    </table>
				</div>
		<%}%>
		 	<div><input type="hidden" name="educationCnt" id="educationCnt" value="<%=alEducation.size()-1 %>" /></div>
		<% }else { %>
		
			<div id="row_education" class="row_education">
                <table class="table table_no_border">
					<tr>
					
						<td>
						<input type="hidden" name="educationCnt" id="educationCnt" value="0"/>
						<select name="degreeName" class="<%=validReqOpt %>" style="width:110px !important;" onchange="checkEducation(this.value,0);" >
						<option value="">Degree</option>
							<%for(int k=0; k< educationalList.size(); k++) {%> 
						<option value="<%=((FillEducational)educationalList.get(k)).getEduName() %>" >
		                		<%=((FillEducational)educationalList.get(k)).getEduName() %>
		                		</option>
		                	<%} %>
		                	<option value="other">Other</option> 
		                	</select>
		                	
						</td>
						<td>
						<% if(candiDegreeDurationValidList != null && uF.parseToBoolean(candiDegreeDurationValidList.get(0))) { %>
						<s:select name="degreeDuration"	cssClass="validateRequired" cssStyle="width: 90px !important;" listKey="degreeDurationID" listValue="degreeDurationName" 
							headerKey="" headerValue="Duration" list="degreeDurationList" key="" required="true" />
						<% } else { %>
							<s:select name="degreeDuration"	cssStyle="width: 90px !important;" listKey="degreeDurationID" listValue="degreeDurationName" 
							 headerKey="" headerValue="Duration" list="degreeDurationList" key="" required="true" />
						<% } %>
						</td>
	                    
	                    <td>
	                     <% if(candiDegreeCompYearValidList != null && uF.parseToBoolean(candiDegreeCompYearValidList.get(0))) { %>
							<s:select name="completionYear"	cssClass="validateRequired" cssStyle="width: 110px !important;" listKey="yearsID" listValue="yearsName" 
							headerKey="" headerValue="Completion Year" list="yearsList" key="" required="true" />
						<% } else { %>
							<s:select name="completionYear"	cssStyle="width: 110px !important;" listKey="yearsID" listValue="yearsName" headerKey="" 
								headerValue="Completion Year" list="yearsList" key="" required="true" />
						<% } %>
						</td>
	                    
	                    <td><input type="text" class="<%=validReqOptDG %>" style="height: 25px; width: 90px !important;" name="grade"></input></td>
					    
				 <!-- Start Parvez Date:07-09-2021 -->
		                <td>
		                   <input type="text" class="<%=validReqOptNOI %>"  style="height: 25px; width: 90px !important;" name="instituteName"  id="instituteName" ></input>
		                </td>
		                <td>
		                   <input type="text" class="<%=validReqOptB %>" style="height: 25px; width: 90px !important;" name="universityName"  id="universityName" ></input>
		                </td>
		                <td>
		                   <input type="text" class="<%=validReqOptSubject %>" style="height: 25px; width: 90px !important;" name="subject"  id="subject" ></input>
		                </td>
		                <td>
		                    <input type="text" class="<%=validReqOptDSDate%>" style="height: 25px; width: 90px !important;" name="strStartDate"  id="strStartDate" ></input>
		                </td>
		                <td>
		                    <input type="text" class="<%=validReqOptDCDate%>" style="height: 25px; width: 90px !important;" name="strCompletionDate" id="strCompletionDate" ></input>
		                </td>
		                <td>
		                    <input type="text" class="<%=validReqOptDM%>" style="height: 25px; width: 90px !important;" name="marks" id="marks" onkeypress="return isNumberKey(event)"></input>
		                </td>
		                <td>
		                   <input type="text" class="<%=validReqOptCity%>" style="height: 25px; width: 90px !important;" name="city1" id="city1" ></input>
		                </td>
		                <td><input type="hidden" name="degreeCertiStatus" id="degreeCertiStatus0" value="0">
							<div id="degreeCertiDiv0"><input type="hidden" name="degreeCertiSubDivCnt0" id="degreeCertiSubDivCnt0" value="0" />
								<div id="degreeCertiSubDiv0_0">
									<input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="degreeCertificate0" id="degreeCertificate0" onchange="fillFileStatus1('degreeCertiStatus0','degreeCertificate0');" class="<%=validReqOptDCertificate%>" />
									<a href="javascript:void(0)" onclick="addEducationCerti('0')" class="add-font"></a>
								</div>
							</div>
						</td>
						<td>
		                    <a href="javascript:void(0)" onclick="addEducation()" class="add-font"></a>
		                </td>
		         
					    <!-- <td><a href="javascript:void(0)" onclick="addEducation()" class="add-font"></a></td> -->
	                <!-- End Parvez Date:07-09-2021 -->
	                </tr>  
	                <tr id="degreeNameOtherTR0" style="display:none;">
		                <td style="text-align:right;">Enter Education :</td>
						<td colspan="3"> 
		                  <input type="text" name="degreeNameOther" class="<%=validReqOptDG %>" style="height: 25px;">
		                </td>
	                </tr> 
				</table>
			</div>
		<%} %>
		
	</div>
</div>
</div>
	
	 <div class="row row_without_margin">
                    <div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
	<div id="div_language">
	
	<h3 style="padding:0px;margin:5px 0px 10px 0px;background: aliceblue;">Enter employee languages</h3>
            <% List<String> candiLanguageNameValidList = hmValidationFields.get("CANDI_LANGUAGE_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiLanguageNameValidList != null && uF.parseToBoolean(candiLanguageNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
           <div id="row_language" class="row_language">  
                <table class="table table_no_border" style="font-size: 12px">              
				  <tr><td width="150px"><label>Language Name<%=validAsterix %></label></td>
				  <td width="50px" align="center"><label>Read</label></td>
				  <td width="50px" align="center"><label>Write</label></td>
				  <td width="50px" align="center"><label>Speak</label></td></tr>
                </table>
		   </div>	
			<% 	
				if(alLanguages!=null && alLanguages.size()!=0){
			 	for(int i=0; i<alLanguages.size(); i++) {
			%>
				
				<div id="row_language<%=i%>" class="row_language">
					<table class="table table_no_border">
						<tr><td width="150px"><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 150px;" name="languageName" value="<%=((ArrayList)alLanguages.get(i)).get(1)%>" ></input></td>
	                    <td style="width:50px; text-align:center;" >
	                    <% 
	                    	if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(2)) ) { 
	                    %>
	                    	<input type="checkbox" name="isReadcheckbox" value="1" id="isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                    		checked="checked" onchange="showHideHiddenField(this.id)" />
	                    	<input type="hidden" name="isRead" value="1" id="hidden_isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                    <%}else { %>
	                    	<input type="checkbox" name="isReadcheckbox" value="0" id="isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                    			onchange="showHideHiddenField(this.id)" />
	                    	<input type="hidden" name="isRead" value="0" id="hidden_isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                    <%} %>
	                    </td>
	                    
	                     <td width="50px" align="center" id="td_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>">
	                    <% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(3)) ) { %>
	                    	<input type="checkbox" name="isWritecheckbox" value="1" checked="checked" id="isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>"  
	                    		onchange="showHideHiddenField(this.id)"	/>
	                    	<input type="hidden" name="isWrite" value="1" id="hidden_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                    <%}else { %>
	                    	<input type="checkbox" name="isWritecheckbox" value="0" id="isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                    		onchange="showHideHiddenField(this.id)"	/>
	                    	<input type="hidden" name="isWrite" value="0" id="hidden_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                    <%} %>
	                    </td>
	                    
	                     <td width="50px" align="center" id="td_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>">
	                    <% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(4)) ) { %>
	                    	<input type="checkbox" name="isSpeakcheckbox" value="1" checked="checked" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                    	onchange="showHideHiddenField(this.id)"	/>
	                    	<input type="hidden" name="isSpeak" value="1" id="hidden_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                    <%}else { %>
	                    	<input type="checkbox" name="isSpeakcheckbox" value="0" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>"
	                    		onchange="showHideHiddenField(this.id)"	/>
	                    	<input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>" />
	                    <%} %>
	                    </td>
							
						
				    	<td><a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a></td>
				    	<%if(i>0){ %>
				        <td><a href="javascript:void(0)" onclick="removeLanguages(this.id)" id=<%=i%> class="remove-font" ></a></td></tr>
				        <%} %>
			    	</table>
			    </div>
			 <% }
			 } else {
			 %>
			 	<div id="row_language" class="row_language" >
			 	    <table class="table table_no_border">
				 	<tr><td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="languageName" ></input></td>
				 	<td width="50px" align="center"><input type="checkbox" id="isRead_0" name="isReadcheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
				 		<input type="hidden" name="isRead" value="0" id="hidden_isRead_0" />
					<td width="50px" align="center"><input type="checkbox" id="isWrite_0" name="isWritecheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
						<input type="hidden" name="isWrite" value="0" id="hidden_isWrite_0" />
		            <td width="50px" align="center"><input type="checkbox" id="isSpeak_0" name="isSpeakcheckbox" value="1" onclick="showHideHiddenField(this.id)"/>
		            	<input type="hidden" name="isSpeak" value="0" id="hidden_isSpeak_0" />
			    	<td ><a href="javascript:void(0)" onclick="addLanguages()" class="add-font"></a></td></tr>
			    	</table>
				</div>
			 <%}%>
	</div>
	
	</div>
	<%-- <div id="div_language">
	
	<h3 style="padding:0px;margin:5px 0px 10px 0px">Enter Candidate languages</h3>
            
           <div id="row_language" class="row_language">  
                <table >              
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
	                    	<input type="hidden" id = "hidden_isRead_<%=((ArrayList)alLanguages.get(i)).get(0)%>" name="hidden_isRead" value="0" />
	                    <%} %>
	                    </td>
	                    
	                     <td width="50px" align="center" id="td_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>">
	                    <% if(uF.parseToBoolean( (String)((ArrayList)alLanguages.get(i)).get(3)) ) { %>
	                    	<input type="checkbox" name="isWrite" value="1" checked="checked" id="isSpeak_<%=((ArrayList)alLanguages.get(i)).get(0)%>"  
	                    			onclick="showHideHiddenField(this.id, this.name)"/>
	                    	
	                    <%}else { %>
	                    	<input type="checkbox" name="isWrite" value="0" id="isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>" 
	                    			onclick="showHideHiddenField(this.id, this.name)"/>
	                    	<input type="hidden" name="isWrite" value="0" id="_hidden_isWrite_<%=((ArrayList)alLanguages.get(i)).get(0)%>"/>
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
				        <td><a href="javascript:void(0)" onclick="removeLanguages(this.id)" id="<%=((ArrayList)alLanguages.get(i)).get(0)%>" class="remove" >Remove</a></td></tr>
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
	 --%>
<% if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EMPLOYEE_HOBBIES_DISABLE))){ %>
	 <div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
		<div id="div_hobbies">
		
			<h3 style="padding:0px;margin:5px 0px 10px 0px;background: aliceblue;">Enter Candidate hobbies</h3>
	            <% List<String> candiHobbyNameValidList = hmValidationFields.get("CANDI_HOBBY_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiHobbyNameValidList != null && uF.parseToBoolean(candiHobbyNameValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
	           <div id="row_hobby" class="row_hobby">  
				<div style="float:left; width:200px;"><label>Hobby Name<%=validAsterix %></label></div>
			   </div>	
				<% 	
					if(alHobbies!=null && alHobbies.size()!=0){
						String empId = (String)((ArrayList)alHobbies.get(0)).get(2);
					
				%>
			<%-- 		<input type="hidden" name="empId" value="<%=empId%>" /> --%>
				<% 
				 	for(int i=0; i<alHobbies.size(); i++) {
				%>
					
					<div id="row_hobby<%=((ArrayList)alHobbies.get(i)).get(0)%>" class="row_hobby">
						<table class="table table_no_border">
	                        <tr><td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="hobbyName" value="<%=((ArrayList)alHobbies.get(i)).get(1)%>" ></input></td> 
	                            <td><a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a></td>
	                            <%if(i>0){ %>
	                            <td><a href="javascript:void(0)" onclick="removeHobbies(this.id)" id=<%=((ArrayList)alHobbies.get(i)).get(0)%> class="remove-font" ></a></td>
	                            <% } %>
	                        </tr>    
				    	</table>
				    </div>
				    
				 <%}
				 
				 }else {
				 %>
				 	<div id="row_hobby" class="row_hobby">
	                    <table class="table table_no_border">
	                          <tr><td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;"name="hobbyName" ></input></td>
	                              <td><a href="javascript:void(0)" onclick="addHobbies()" class="add-font"></a></td>
	                          </tr>    
	                    </table>  
					</div>
				    	
				 <%}%>
		
		</div>
	</div>
	<% } %>
	</div>
	<!-- ====end parvez on 02-07-2021=====  -->
	
	<div style="float:right;">
				<table class="table table_no_border">
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
			<!-- ====start parvez on 01-07-2021 -->
	<div class="clr"></div>
	<!-- ====end parvez on 01-07-2021 -->
	</s:form>
</div>
</s:if>

<s:if test="step==3 || mode=='report'">

<div>	<!-- Family Information -->


<s:form theme="simple" action="AddCandidateByCadi" id="frmFamilyInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
	
	<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_id_family">
	<s:hidden name="show"></s:hidden>
	<s:hidden name="operation" /><s:hidden name="recruitId" /><s:hidden name="CandidateId" />
	<s:hidden name="mode" />	<s:hidden name="step" />
	<table border="0" class="table table_no_border">
	<tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 3: </span>
    <span class="tdLabelheadingBg">Enter Candidates Family Information </span></td></tr>
	</table>
    
    <table class="table table_no_border">
      <tr><td>
		<table class="table table_no_border">	
		       <tr><td  style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Father's Information </td></tr>
			 	<tr>
			 	<% List<String> candiFatherNameValidList = hmValidationFields.get("CANDI_FATHER_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiFatherNameValidList != null && uF.parseToBoolean(candiFatherNameValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
			 	<td class="txtlabel alignRight">Name:<%=validAsterix %></td>
			 	<td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="fatherName" value="<%=uF.showData((String)request.getAttribute("fatherName"), "") %>" ></td>
			 	</tr>
			 	
				<tr>
				<% List<String> candiFatherDOBValidList = hmValidationFields.get("CANDI_FATHER_DATE_OF_BIRTH"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiFatherDOBValidList != null && uF.parseToBoolean(candiFatherDOBValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>"style="height: 25px; width: 180px;" name="fatherDob" value="<%=uF.showData((String)request.getAttribute("fatherDob"), "") %>"/></td>
				</tr>
				
				<tr>
				<% List<String> candiFatherEducationValidList = hmValidationFields.get("CANDI_FATHER_EDUCATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiFatherEducationValidList != null && uF.parseToBoolean(candiFatherEducationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Education:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="fatherEducation" value="<%=uF.showData((String)request.getAttribute("fatherEducation"), "") %>" ></td>
				</tr>
				
				<tr>
				<% List<String> candiFatherOccupationValidList = hmValidationFields.get("CANDI_FATHER_OCCUPATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiFatherOccupationValidList != null && uF.parseToBoolean(candiFatherOccupationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="fatherOccupation" value="<%=uF.showData((String)request.getAttribute("fatherOccupation"), "") %>"></td>
				</tr>
				
				<tr>
				<% List<String> candiFatherContactNoValidList = hmValidationFields.get("CANDI_FATHER_CONTACT_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiFatherContactNoValidList != null && uF.parseToBoolean(candiFatherContactNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
				<td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="fatherContactNumber" value="<%=uF.showData((String)request.getAttribute("fatherContactNumber"), "") %>" ></td>
				</tr>
				
				<tr>
				<% List<String> candiFatherMailIdValidList = hmValidationFields.get("CANDI_FATHER_EMAIL_ID"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiFatherMailIdValidList != null && uF.parseToBoolean(candiFatherMailIdValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="fatherEmailId" value="<%=uF.showData((String)request.getAttribute("fatherEmailId"), "") %>" ></td>
				</tr>
				
				<tr><td colspan="2">&nbsp;</td></tr>		
	   </table>     
		</td>
       
        <td>
       <table class="table table_no_border">	 	 
		   <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Mother's Information </td></tr>
		 	<tr>
		 	<% List<String> candiMotherNameValidList = hmValidationFields.get("CANDI_MOTHER_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMotherNameValidList != null && uF.parseToBoolean(candiMotherNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
		 	<td class="txtlabel alignRight">Name:<%=validAsterix %></td>
		 	<td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="motherName" value="<%=uF.showData((String)request.getAttribute("motherName"), "") %>"></td>
		 	</tr>
		 	
			<tr>
			<% List<String> candiMotherDOBValidList = hmValidationFields.get("CANDI_MOTHER_DATE_OF_BIRTH"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMotherDOBValidList != null && uF.parseToBoolean(candiMotherDOBValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
			<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="motherDob" value="<%=uF.showData((String)request.getAttribute("motherDob"), "") %>" ></td>
			</tr>
			
			<tr>
			<% List<String> candiMotherEducationValidList = hmValidationFields.get("CANDI_MOTHER_EDUCATION"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMotherEducationValidList != null && uF.parseToBoolean(candiMotherEducationValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Education:<%=validAsterix %></td>
			<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="motherEducation" value="<%=uF.showData((String)request.getAttribute("motherEducation"), "") %>" ></td>
			</tr>
			
			<tr>
			<% List<String> candiMotherOccupationValidList = hmValidationFields.get("CANDI_MOTHER_OCCUPATION"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMotherOccupationValidList != null && uF.parseToBoolean(candiMotherOccupationValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
			<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="motherOccupation" value="<%=uF.showData((String)request.getAttribute("motherOccupation"), "") %>"></td>
			</tr>
			
			<tr>
			<% List<String> candiMotherContactNoValidList = hmValidationFields.get("CANDI_MOTHER_CONTACT_NO"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMotherContactNoValidList != null && uF.parseToBoolean(candiMotherContactNoValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
			<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="motherContactNumber" value="<%=uF.showData((String)request.getAttribute("motherContactNumber"), "") %>" ></td>
			</tr>
			
			<tr>
			<% List<String> candiMotherMailIdValidList = hmValidationFields.get("CANDI_MOTHER_EMAIL_ID"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiMotherMailIdValidList != null && uF.parseToBoolean(candiMotherMailIdValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
			<td> <input type="text" style="height: 25px; width: 180px;"  class="<%=validReqOpt %>"name="motherEmailId" value="<%=uF.showData((String)request.getAttribute("motherEmailId"), "") %>" ></td>
			</tr>
			
			<tr><td colspan="2">&nbsp;</td></tr>
		</table>     
		</td>
        
        <td>
       <table class="table table_no_border">	 	 
		   	<tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Spouse's Information </td></tr>
		 	<tr>
		 	<% List<String> candiSpouseNameValidList = hmValidationFields.get("CANDI_SPOUSE_NAME"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSpouseNameValidList != null && uF.parseToBoolean(candiSpouseNameValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
		 	<td class="txtlabel alignRight">Name:<%=validAsterix %></td>
		 	<td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="spouseName" value="<%=uF.showData((String)request.getAttribute("spouseName"), "") %>"></td>
		 	</tr>
		 	
			<tr>
			<% List<String> candiSpouseDOBValidList = hmValidationFields.get("CANDI_SPOUSE_DATE_OF_BIRTH"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSpouseDOBValidList != null && uF.parseToBoolean(candiSpouseDOBValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
			<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="spouseDob" value="<%=uF.showData((String)request.getAttribute("spouseDob"), "") %>"></td>
			</tr>
			
			<tr>
			<% List<String> candiSpouseEducationValidList = hmValidationFields.get("CANDI_SPOUSE_EDUCATION"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSpouseEducationValidList != null && uF.parseToBoolean(candiSpouseEducationValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Education:<%=validAsterix %></td>
			<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="spouseEducation" value="<%=uF.showData((String)request.getAttribute("spouseEducation"), "") %>"></td>
			</tr>
			
			<tr>
			<% List<String> candiSpouseOccupationValidList = hmValidationFields.get("CANDI_SPOUSE_OCCUPATION"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSpouseOccupationValidList != null && uF.parseToBoolean(candiSpouseOccupationValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
			<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="spouseOccupation" value="<%=uF.showData((String)request.getAttribute("spouseOccupation"), "") %>"></td>
			</tr>
			
			<tr>
			<% List<String> candiSpouseContactNoValidList = hmValidationFields.get("CANDI_SPOUSE_CONTACT_NO"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSpouseContactNoValidList != null && uF.parseToBoolean(candiSpouseContactNoValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
			<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="spouseContactNumber" value="<%=uF.showData((String)request.getAttribute("spouseContactNumber"), "") %>" ></td>
			</tr>
			
			<tr>
			<% List<String> candiSpouseMailIdValidList = hmValidationFields.get("CANDI_SPOUSE_EMAIL_ID"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSpouseMailIdValidList != null && uF.parseToBoolean(candiSpouseMailIdValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
			<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="spouseEmailId" value="<%=uF.showData((String)request.getAttribute("spouseEmailId"), "") %>" ></td>
			</tr>
			
			<tr>
			<% List<String> candiSpouseGenderValidList = hmValidationFields.get("CANDI_SPOUSE_GENDER"); 
				validReqOpt = "";
				validAsterix = "";
				if(candiSpouseGenderValidList != null && uF.parseToBoolean(candiSpouseGenderValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				}
			%>
			<td class="txtlabel alignRight">Gender:<%=validAsterix %></td>
			<td>
			<% if(candiSpouseGenderValidList != null && uF.parseToBoolean(candiSpouseGenderValidList.get(0))) { %>
				<s:select theme="simple" label="Select Gender" name="spouseGender" cssClass="validateRequired"  listKey="genderId" cssStyle="width: 180px;"
					listValue="genderName" headerKey="0" headerValue="Select Gender" list="empGenderList" key="" required="true" />
			<% } else { %>
				<s:select theme="simple" label="Select Gender" name="spouseGender" listKey="genderId" cssStyle="width: 180px;"
					listValue="genderName" headerKey="0" headerValue="Select Gender" list="empGenderList" key="" required="true" />
			<% } %>
			</td>
		</table>
		</td></tr>	 
    </table>    
             
			<%	if(alSiblings!=null && alSiblings.size()!=0) {
					for(int i=0; i<alSiblings.size(); i++) { %>
			
			<div id="col_family_siblings<%=i%>" style="float:left; width: 100; border:solid 0px #ccc" >
			                      
               <table class="table table_no_border"> 
                  <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td></tr>    
                
			 	<tr>
			 	<% List<String> candiSiblingNameValidList = hmValidationFields.get("CANDI_SIBLING_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingNameValidList != null && uF.parseToBoolean(candiSiblingNameValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
			 	<td class="txtlabel alignRight">Name:<%=validAsterix %></td>
			 	<td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberName" value="<%=((ArrayList)alSiblings.get(i)).get(1)%>" ></input></td>
			 	</tr> 
			 	   
				<tr>
				<% List<String> candiSiblingDOBValidList = hmValidationFields.get("CANDI_SIBLING_DATE_OF_BIRTH"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingDOBValidList != null && uF.parseToBoolean(candiSiblingDOBValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberDob" value="<%=((ArrayList)alSiblings.get(i)).get(2)%>"></input></td>
				</tr>
				    
				<tr>
				<% List<String> candiSiblingEducationValidList = hmValidationFields.get("CANDI_SIBLING_EDUCATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingEducationValidList != null && uF.parseToBoolean(candiSiblingEducationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Education:<%=validAsterix %> </td>
				<td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberEducation" value="<%=((ArrayList)alSiblings.get(i)).get(3)%>"></input></td>
				</tr>
				    
				<tr>
				<% List<String> candiSiblingOccupationValidList = hmValidationFields.get("CANDI_SIBLING_OCCUPATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingOccupationValidList != null && uF.parseToBoolean(candiSiblingOccupationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberOccupation" value="<%=((ArrayList)alSiblings.get(i)).get(4)%>"></input></td>
				</tr>
				    
				<tr>
				<% List<String> candiSiblingContactNoValidList = hmValidationFields.get("CANDI_SIBLING_CONTACT_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingContactNoValidList != null && uF.parseToBoolean(candiSiblingContactNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px;width: 180px;" name="memberContactNumber" value="<%=((ArrayList)alSiblings.get(i)).get(5)%>"></input></td>
				</tr>
				    
				<tr>
				<% List<String> candiSiblingMailIdValidList = hmValidationFields.get("CANDI_SIBLING_EMAIL_ID"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingMailIdValidList != null && uF.parseToBoolean(candiSiblingMailIdValidList.get(0))) {
						validReqOpt = "validate[required,custom[email]]";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberEmailId" value="<%=((ArrayList)alSiblings.get(i)).get(6)%>"></input></td>
				</tr>
				    
				
				<tr>
				<% List<String> candiSiblingGenderValidList = hmValidationFields.get("CANDI_SIBLING_GENDER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingGenderValidList != null && uF.parseToBoolean(candiSiblingGenderValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Gender:<%=validAsterix %></td> 
				
				<%-- <td><input type="text" style="width: 180px;" name="memberGender" value="<%=((ArrayList)alSiblings.get(i)).get(7)%>"></input></td> --%>
					<td>
	                	<select name="memberGender" class="<%=validReqOpt %>"  style="width: 180px;">
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
		    	<a href="javascript:void(0)" onclick="addSibling()"  style="float:right" class="add-font"></a>
		    	<% if(i>0){ %>
		    	<a href="javascript:void(0)" onclick="removeSibling(this.id)" id=<%=i%> class="remove-font" ></a>
		    	<%} %>
		    	</td></tr>    
                    
               </table>     
			</div>
			
			<%}
			}else {%>
			
			<div id="col_family_siblings" style="float: left;border:solid 0px #f00" >
			   
               <table class="table table_no_border"> 
                <tr><td style="text-align:center" class="tdLabelheadingBg alignRight" colspan="2">Sibling's Information </td></tr>        
			 	
			 	<tr>
			 	<% List<String> candiSiblingNameValidList = hmValidationFields.get("CANDI_SIBLING_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingNameValidList != null && uF.parseToBoolean(candiSiblingNameValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
			 	<td class="txtlabel alignRight">Name:<%=validAsterix %></td>
			 	<td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberName" ></input></td>
			 	</tr>
			 	
				<tr>
				<% List<String> candiSiblingDOBValidList = hmValidationFields.get("CANDI_SIBLING_DATE_OF_BIRTH"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingDOBValidList != null && uF.parseToBoolean(candiSiblingDOBValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Date of birth:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberDob" ></input></td>
				</tr>
				
				<tr>
				<% List<String> candiSiblingEducationValidList = hmValidationFields.get("CANDI_SIBLING_EDUCATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingEducationValidList != null && uF.parseToBoolean(candiSiblingEducationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Education:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberEducation" ></input></td>
				</tr>
				
				<tr>
				<% List<String> candiSiblingOccupationValidList = hmValidationFields.get("CANDI_SIBLING_OCCUPATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingOccupationValidList != null && uF.parseToBoolean(candiSiblingOccupationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Occupation:<%=validAsterix %></td>
				<td><input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberOccupation" ></input></td>
				</tr>
				
				<tr>
				<% List<String> candiSiblingContactNoValidList = hmValidationFields.get("CANDI_SIBLING_CONTACT_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingContactNoValidList != null && uF.parseToBoolean(candiSiblingContactNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberContactNumber" ></input></td>
				</tr>
				
				<tr>
				<% List<String> candiSiblingMailIdValidList = hmValidationFields.get("CANDI_SIBLING_EMAIL_ID"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingMailIdValidList != null && uF.parseToBoolean(candiSiblingMailIdValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Email Id:<%=validAsterix %></td>
				<td> <input type="text" class="<%=validReqOpt %>" style="height: 25px; width: 180px;" name="memberEmailId" ></input></td>
				</tr>
				
				<tr>
				<% List<String> candiSiblingGenderValidList = hmValidationFields.get("CANDI_SIBLING_GENDER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiSiblingGenderValidList != null && uF.parseToBoolean(candiSiblingGenderValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Gender:<%=validAsterix %></td><td>
				<% if(candiSiblingGenderValidList != null && uF.parseToBoolean(candiSiblingGenderValidList.get(0))) { %>
					<s:select theme="simple" label="Select Gender" name="memberGender" listKey="genderId" cssClass="validateRequired" cssStyle="width: 180px;"
						listValue="genderName" headerKey="0" headerValue="Select Gender" list="empGenderList" key="" required="true" />
				<% } else { %>
					<s:select theme="simple" label="Select Gender" name="memberGender" listKey="genderId" cssStyle="width: 180px;"
						listValue="genderName" headerKey="0" headerValue="Select Gender" list="empGenderList" key="" required="true" />
				<% } %>
					</td>
				</tr>
		    	<tr><td class="txtlabel alignRight" colspan="2"><a href="javascript:void(0)" onclick="addSibling()" class="add-font" style="float:right"></a></td></tr>
		       </table>
            	
			</div>
			
			<%}%>
			
</div> 

		
			<div style="float:right;">
				<table class="table table_no_border">
				<s:if test="mode==null">
					<tr><td colspan="2" align="center">
						<s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
					</td></tr>
				</s:if>
				<s:else>
					<tr><td colspan="2" align="center">
						<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
					</td></tr>
				</s:else>
				</table>
			</div>
			<!-- ====start parvez on 01-07-2021 -->
		<div class="clr"></div>	
		<!-- ====end parvez on 01-07-2021 -->
</s:form> 

</div>
</s:if>

<s:if test="step==4 || mode=='report'">
<div>	<!-- Previous Employment -->

<s:form theme="simple" action="AddCandidateByCadi" id="frmPrevEmployment" method="POST" cssClass="formcss" enctype="multipart/form-data">
<div style="height: auto; width:100%; float: left; border: solid 0px black; overflow: auto;" id="div_prev_employment">
	<s:hidden name="show"></s:hidden>
	<s:hidden name="operation" />
	<s:hidden name="recruitId" />
	<s:hidden name="CandidateId" />
	<s:hidden name="mode" />
	<s:hidden name="step" />
	<table border="0" class="table table_no_border">

<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 4: </span>Enter Candidates Previous Employment </td></tr>
	</table>			
		
		 <%	if(alPrevEmployment!=null && alPrevEmployment.size()!=0) {
			 
				for(int i=0; i<alPrevEmployment.size(); i++) {%>
					
			<div id="col_prev_employer<%=i%>" style="float: left;">
			
             <table class="table table_no_border">
			 	<tr>
			 	<% List<String> candiPrevCompanyNameValidList = hmValidationFields.get("CANDI_PREV_COMPANY_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyNameValidList != null && uF.parseToBoolean(candiPrevCompanyNameValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
			 	<td class="txtlabel alignRight">Company Name:<%=validAsterix %></td>
			 	<!-- ===start parvez date: 14-09-2021=== -->
			 	<td><input type="text" name="prevCompanyName" style="height:25px; width: 180px;"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(1)%>" onchange="prevCompanyExpFile('<%=i%>',this.value);"></input></td>
			 	<!-- ===end parvez date: 14-09-2021=== -->
			 	</tr>
			 	 
				<tr>
				<% List<String> candiPrevCompanyLocationValidList = hmValidationFields.get("CANDI_PREV_COMPANY_LOCATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyLocationValidList != null && uF.parseToBoolean(candiPrevCompanyLocationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Location:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyLocation"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(2)%>"></input></td></tr> 
				
				<tr>
				<% List<String> candiPrevCompanyCityValidList = hmValidationFields.get("CANDI_PREV_COMPANY_CITY"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyCityValidList != null && uF.parseToBoolean(candiPrevCompanyCityValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">City:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyCity"  class="<%=validReqOpt %>"  value="<%=((ArrayList)alPrevEmployment.get(i)).get(3)%>"></input></td>
				</tr> 
			
				<tr>
				<% List<String> candiPrevCompanyStateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_STATE"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyStateValidList != null && uF.parseToBoolean(candiPrevCompanyStateValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">State:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyState"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(4)%>"></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyCountryValidList = hmValidationFields.get("CANDI_PREV_COMPANY_COUNRTY"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyCountryValidList != null && uF.parseToBoolean(candiPrevCompanyCountryValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Country:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyCountry"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(5)%>"></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyContactNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_CONTACT_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyContactNoValidList != null && uF.parseToBoolean(candiPrevCompanyContactNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Contact Number:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyContactNo"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(6)%>"></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyReportingToValidList = hmValidationFields.get("CANDI_PREV_COMPANY_REPORTING_TO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyReportingToValidList != null && uF.parseToBoolean(candiPrevCompanyReportingToValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Reporting To:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyReportingTo"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(7)%>"></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyReportingToPhoneNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_REPORTING_TO_PHONE_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyReportingToPhoneNoValidList != null && uF.parseToBoolean(candiPrevCompanyReportingToPhoneNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Reporting Manager Phone Number:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyReportManagerPhNo"  class="<%=validReqOpt %>"  value="<%=((ArrayList)alPrevEmployment.get(i)).get(8)%>"></input></td>
				</tr>
				
				<tr>
				<% List<String> candiPrevCompanyHrManagerValidList = hmValidationFields.get("CANDI_PREV_COMPANY_HR_MANAGER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyHrManagerValidList != null && uF.parseToBoolean(candiPrevCompanyHrManagerValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">HR Manager:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyHRManager" class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(9)%>"></input></td>
				</tr>
				
				<tr>
				<% List<String> candiPrevCompanyHrManagerPhoneNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_HR_MANAGER_PHONE_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyHrManagerPhoneNoValidList != null && uF.parseToBoolean(candiPrevCompanyHrManagerPhoneNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">HR Manager Phone Number:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyHRManagerPhNo" class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(10)%>"></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyFromDateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_FROM_DATE"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyFromDateValidList != null && uF.parseToBoolean(candiPrevCompanyFromDateValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">From:<%=validAsterix %> </td>
				<td><input type="text" style="height:25px; width: 180px;" name="prevCompanyFromDate" class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(11)%>"></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyToDateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_TO_DATE"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyToDateValidList != null && uF.parseToBoolean(candiPrevCompanyToDateValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">To:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyToDate" class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(12)%>"></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyDesignationValidList = hmValidationFields.get("CANDI_PREV_COMPANY_DESIGNATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyDesignationValidList != null && uF.parseToBoolean(candiPrevCompanyDesignationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Designation:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyDesination"  class="<%=validReqOpt %>"  value="<%=((ArrayList)alPrevEmployment.get(i)).get(13)%>"></input></td>
				</tr> 
			
				<tr>
				<% List<String> candiPrevCompanyResponsibilityValidList = hmValidationFields.get("CANDI_PREV_COMPANY_RESPONSIBILITY"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyResponsibilityValidList != null && uF.parseToBoolean(candiPrevCompanyResponsibilityValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Responsibility:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyResponsibilities"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(14)%>"></input></td>
				</tr> 
			
				<tr>
				<% List<String> candiPrevCompanySkillsValidList = hmValidationFields.get("CANDI_PREV_COMPANY_SKILLS"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanySkillsValidList != null && uF.parseToBoolean(candiPrevCompanySkillsValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Skills:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanySkills"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(15)%>"></input></td>
				</tr> 
			<!-- ===start parvez date: 08-08-2022=== -->
				<tr>
					<% List<String> candiPrevCompanyESICNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_ESIC_NO"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiPrevCompanyESICNoValidList != null && uF.parseToBoolean(candiPrevCompanyESICNoValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">ESIC No.:<%=validAsterix %></td>
					<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyESICNo"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(17)%>"></input></td>
				</tr>
				
				<tr>
					<% List<String> candiPrevCompanyUANNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_UAN_NO"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiPrevCompanyUANNoValidList != null && uF.parseToBoolean(candiPrevCompanyUANNoValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight">UAN No.:<%=validAsterix %></td>
					<td> <input type="text" style="height:25px; width: 180px;" name="prevCompanyUANNo"  class="<%=validReqOpt %>" value="<%=((ArrayList)alPrevEmployment.get(i)).get(18)%>"></input></td>
				</tr>
			<!-- ===end parvez date: 08-08-2022=== -->
				<tr>
				<% List<String> candiPrevCompanyExpLetterValidList = hmValidationFields.get("CANDI_PREV_COMPANY_EXP_LETTER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyExpLetterValidList != null && uF.parseToBoolean(candiPrevCompanyExpLetterValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Experience Letter:<%=validAsterix %> </td>
				<td><input type="hidden" name="expLetterFileStatus" id="expLetterFileStatus<%=i%>" value="0"/>
				<input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="prevCompanyExpLetter<%=i%>" id="prevCompanyExpLetter<%=i%>" class="<%=validReqOpt %>" onchange="fillFileStatus1('expLetterFileStatus<%=i%>','prevCompanyExpLetter<%=i%>')"/></td>
				<!-- ===start parvez date: 20-09-2021=== -->
				<td class="textblue">
		            <% if(((ArrayList)alPrevEmployment.get(i)).get(16) != null) {%>
						<div>
						<%if(docRetriveLocation == null) { %>
							<a
								href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alPrevEmployment.get(i)).get(16)  %>"
								title="Experience Latter"><i class="fa fa-file-o" aria-hidden="true"></i>
							</a>
						<% } else { %>
							<a
								href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_PREVIOUS_EMPLOYMENT_DOC+"/"+(String)request.getAttribute("CandidateId")+"/"+ ((ArrayList)alPrevEmployment.get(i)).get(16) %>"
									title="Experience Latter"><i class="fa fa-file-o" aria-hidden="true"></i>
							</a>
						<% } %>
						</div> <% } %>
												
		        </td>
				
				</tr> 
				<!-- ===end parvez date: 07-09-2021=== -->
				
				<%-- <tr>
				<% List<String> candiPrevCompanyExpLetterValidList = hmValidationFields.get("CANDI_PREV_COMPANY_EXP_LETTER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyExpLetterValidList != null && uF.parseToBoolean(candiPrevCompanyExpLetterValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}/
				%>
				<td class="txtlabel alignRight"> Experience Letter:<%=validAsterix %> </td>
				<td><input type="hidden" name="expLetterFileStatus" id="expLetterFileStatus" value="0"/>
				<input type="file" name="prevCompanyExpLetter" id="prevCompanyExpLetter" class="<%=validReqOpt %>" onchange="fillFileStatus('expLetterFileStatus')"/></td>
				</tr> 

				<tr>
				<% List<String> candiPrevCompanyRelievingLetterValidList = hmValidationFields.get("CANDI_PREV_COMPANY_RELIEVING_LETTER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyRelievingLetterValidList != null && uF.parseToBoolean(candiPrevCompanyRelievingLetterValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Relieving Letter:<%=validAsterix %> </td>
				<td><input type="hidden" name="relievingLetterFileStatus" id="relievingLetterFileStatus" value="0"/>
				<input type="file" name="prevCompanyRelievingLetter" id="prevCompanyRelievingLetter" class="<%=validReqOpt %>" onchange="fillFileStatus('relievingLetterFileStatus')"/></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyFnFDocValidList = hmValidationFields.get("CANDI_PREV_COMPANY_FNF_DOC"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyFnFDocValidList != null && uF.parseToBoolean(candiPrevCompanyFnFDocValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> FnF Document:<%=validAsterix %> </td>
				<td><input type="hidden" name="fnfDocFileStatus" id="fnfDocFileStatus" value="0"/>
				<input type="file" name="prevCompanyFnFDoc" id="prevCompanyFnFDoc" class="<%=validReqOpt %>" onchange="fillFileStatus('fnfDocFileStatus')"/></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyBankStatementValidList = hmValidationFields.get("CANDI_PREV_COMPANY_BANK_STATEMENT"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyBankStatementValidList != null && uF.parseToBoolean(candiPrevCompanyBankStatementValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Bank Statement:<%=validAsterix %> </td>
				<td><input type="hidden" name="bankStatementFileStatus" id="bankStatementFileStatus" value="0"/>
				<input type="file" name="prevCompanyBankStatement" id="prevCompanyBankStatement" class="<%=validReqOpt %>" onchange="fillFileStatus('bankStatementFileStatus')"/></td>
				</tr>  --%>
				
				<tr>
				<td colspan="2" style="margin:0px 5px 0px 0px"><span style="float: right;"> 
				<%if(i>0){ %>
				<a href="javascript:void(0)" onclick="removePrevEmployment(this.id)" id=<%=i%> class="remove" >Remove</a>
				<%} %>
    			<a href=javascript:void(0) onclick="addPrevEmployment()" class=add>Add</a></span>
				</td></tr>
				
              </table> 
                
			</div>
			 
			 
		<%}
				
		 } else { %>
			
			<div id="col_prev_employer" style="float: left;">
			 
              <table class="table table_no_border">
			 	<tr>
			 	<% List<String> candiPrevCompanyNameValidList = hmValidationFields.get("CANDI_PREV_COMPANY_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyNameValidList != null && uF.parseToBoolean(candiPrevCompanyNameValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
			 	<td class="txtlabel alignRight"> Company Name:<%=validAsterix %></td>
			 	<td><input type="text" name="prevCompanyName" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyName" onchange="prevCompanyExpFile('0',this.value);"></input></td></tr> 
				
				<tr>
				<% List<String> candiPrevCompanyLocationValidList = hmValidationFields.get("CANDI_PREV_COMPANY_LOCATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyLocationValidList != null && uF.parseToBoolean(candiPrevCompanyLocationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Location:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyLocation" ></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyCityValidList = hmValidationFields.get("CANDI_PREV_COMPANY_CITY"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyCityValidList != null && uF.parseToBoolean(candiPrevCompanyCityValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> City:<%=validAsterix %> </td>
				<td><input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyCity" ></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyStateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_STATE"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyStateValidList != null && uF.parseToBoolean(candiPrevCompanyStateValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> State:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyState" ></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyCountryValidList = hmValidationFields.get("CANDI_PREV_COMPANY_COUNRTY"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyCountryValidList != null && uF.parseToBoolean(candiPrevCompanyCountryValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Country:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyCountry" ></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyContactNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_CONTACT_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyContactNoValidList != null && uF.parseToBoolean(candiPrevCompanyContactNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Contact Number:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyContactNo" ></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyReportingToValidList = hmValidationFields.get("CANDI_PREV_COMPANY_REPORTING_TO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyReportingToValidList != null && uF.parseToBoolean(candiPrevCompanyReportingToValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Reporting To:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyReportingTo" ></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyReportingToPhoneNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_REPORTING_TO_PHONE_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyReportingToPhoneNoValidList != null && uF.parseToBoolean(candiPrevCompanyReportingToPhoneNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">Reporting Manager Phone Number:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyReportManagerPhNo"></input></td>
				</tr>
				
				<tr>
				<% List<String> candiPrevCompanyHrManagerValidList = hmValidationFields.get("CANDI_PREV_COMPANY_HR_MANAGER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyHrManagerValidList != null && uF.parseToBoolean(candiPrevCompanyHrManagerValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">HR Manager:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyHRManager"></input></td>
				</tr>
				
				<tr>
				<% List<String> candiPrevCompanyHrManagerPhoneNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_HR_MANAGER_PHONE_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyHrManagerPhoneNoValidList != null && uF.parseToBoolean(candiPrevCompanyHrManagerPhoneNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight">HR Manager Phone Number:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyHRManagerPhNo"></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyFromDateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_FROM_DATE"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyFromDateValidList != null && uF.parseToBoolean(candiPrevCompanyFromDateValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> From:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyFromDate" ></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyToDateValidList = hmValidationFields.get("CANDI_PREV_COMPANY_TO_DATE"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyToDateValidList != null && uF.parseToBoolean(candiPrevCompanyToDateValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> To:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyToDate" ></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyDesignationValidList = hmValidationFields.get("CANDI_PREV_COMPANY_DESIGNATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyDesignationValidList != null && uF.parseToBoolean(candiPrevCompanyDesignationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Designation:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyDesination" ></input></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyResponsibilityValidList = hmValidationFields.get("CANDI_PREV_COMPANY_RESPONSIBILITY"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyResponsibilityValidList != null && uF.parseToBoolean(candiPrevCompanyResponsibilityValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Responsibility:<%=validAsterix %></td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>" name="prevCompanyResponsibilities" ></input>  </td></tr> 
				
				<tr>
				<% List<String> candiPrevCompanySkillsValidList = hmValidationFields.get("CANDI_PREV_COMPANY_SKILLS"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanySkillsValidList != null && uF.parseToBoolean(candiPrevCompanySkillsValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Skills:<%=validAsterix %> </td>
				<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>"  name="prevCompanySkills" ></input></td>
				</tr>
						
		<!-- ===start parvez date: 08-08-2022=== -->
				<tr>
					<% List<String> candiPrevCompanyESICNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_ESIC_NO"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiPrevCompanyESICNoValidList != null && uF.parseToBoolean(candiPrevCompanyESICNoValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					<td class="txtlabel alignRight"> ESIC No.:<%=validAsterix %> </td>
					<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>"  name="prevCompanyESICNo" ></input></td>
					
				</tr>
				
				<tr>
					<% List<String> candiPrevCompanyUANNoValidList = hmValidationFields.get("CANDI_PREV_COMPANY_UAN_NO"); 
						validReqOpt = "";
						validAsterix = "";
						if(candiPrevCompanyUANNoValidList != null && uF.parseToBoolean(candiPrevCompanyUANNoValidList.get(0))) {
							validReqOpt = "validateRequired";
							validAsterix = "<sup>*</sup>";
						}
					%>
					
					<td class="txtlabel alignRight"> UAN No.:<%=validAsterix %> </td>
					<td> <input type="text" style="height:25px; width: 180px;" class="<%=validReqOpt %>"  name="prevCompanyUANNo" ></input></td>
					
				</tr>
			<!-- ===end parvez date: 08-08-2022=== -->
				<tr>
				<% List<String> candiPrevCompanyExpLetterValidList = hmValidationFields.get("CANDI_PREV_COMPANY_EXP_LETTER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyExpLetterValidList != null && uF.parseToBoolean(candiPrevCompanyExpLetterValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Experience Letter:<%=validAsterix %> </td>
				<td><input type="hidden" name="expLetterFileStatus" id="expLetterFileStatus0" value="0"/>
				<input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="prevCompanyExpLetter0" id="prevCompanyExpLetter0" class="<%=validReqOpt %>" onchange="fillFileStatus1('expLetterFileStatus0','prevCompanyExpLetter0')"/></td>
				</tr>
				
				<%-- <tr>
				<% List<String> candiPrevCompanyExpLetterValidList = hmValidationFields.get("CANDI_PREV_COMPANY_EXP_LETTER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyExpLetterValidList != null && uF.parseToBoolean(candiPrevCompanyExpLetterValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}/
				%>
				<td class="txtlabel alignRight"> Experience Letter:<%=validAsterix %> </td>
				<td><input type="hidden" name="expLetterFileStatus" id="expLetterFileStatus" value="0"/>
				<input type="file" name="prevCompanyExpLetter" id="prevCompanyExpLetter" class="<%=validReqOpt %>" onchange="fillFileStatus('expLetterFileStatus')"/></td>
				</tr> 

				<tr>
				<% List<String> candiPrevCompanyRelievingLetterValidList = hmValidationFields.get("CANDI_PREV_COMPANY_RELIEVING_LETTER"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyRelievingLetterValidList != null && uF.parseToBoolean(candiPrevCompanyRelievingLetterValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Relieving Letter:<%=validAsterix %> </td>
				<td><input type="hidden" name="relievingLetterFileStatus" id="relievingLetterFileStatus" value="0"/>
				<input type="file" name="prevCompanyRelievingLetter" id="prevCompanyRelievingLetter" class="<%=validReqOpt %>" onchange="fillFileStatus('relievingLetterFileStatus')"/></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyFnFDocValidList = hmValidationFields.get("CANDI_PREV_COMPANY_FNF_DOC"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyFnFDocValidList != null && uF.parseToBoolean(candiPrevCompanyFnFDocValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> FnF Document:<%=validAsterix %> </td>
				<td><input type="hidden" name="fnfDocFileStatus" id="fnfDocFileStatus" value="0"/>
				<input type="file" name="prevCompanyFnFDoc" id="prevCompanyFnFDoc" class="<%=validReqOpt %>" onchange="fillFileStatus('fnfDocFileStatus')"/></td>
				</tr> 
				
				<tr>
				<% List<String> candiPrevCompanyBankStatementValidList = hmValidationFields.get("CANDI_PREV_COMPANY_BANK_STATEMENT"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiPrevCompanyBankStatementValidList != null && uF.parseToBoolean(candiPrevCompanyBankStatementValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
				%>
				<td class="txtlabel alignRight"> Bank Statement:<%=validAsterix %> </td>
				<td><input type="hidden" name="bankStatementFileStatus" id="bankStatementFileStatus" value="0"/>
				<input type="file" name="prevCompanyBankStatement" id="prevCompanyBankStatement" class="<%=validReqOpt %>" onchange="fillFileStatus('bankStatementFileStatus')"/></td>
				</tr> --%> 
				
				<tr><td colspan="2"><span style="float:right;"><a href="javascript:void(0)" onclick="addPrevEmployment()" class="add-font"></a></span></td></tr> 
				
              </table>   
			</div>
			
		<%}%>
</div>

	
		<div style="float:right;">
		<table class="table table_no_border">
		<s:if test="mode==null">
			<tr><td colspan="2" align="center">
				 <s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
			</td></tr>
		</s:if>
		<s:else>
			<tr><td colspan="2" align="center">
				 <s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
			</td></tr>
		</s:else>
		</table>
	</div>
	<!-- ====start parvez on 01-07-2021 -->
	<div class="clr"></div>
	<!-- ====end parvez on 01-07-2021 -->
</s:form> 

</div>
</s:if>


<s:if test="step==5 || mode=='report'">
	<div>
	<s:form theme="simple" action="AddCandidateByCadi" id="frmReferences" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="show"></s:hidden>
		<table border="0" class="table table_no_border">
			
			<tr><td>
			</td></tr>
			<tr><td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 5: </span>Enter Candidate References 1:</td></tr>
			
			<s:hidden name="operation" /><s:hidden name="recruitId" /><s:hidden name="CandidateId" />
			<s:hidden name="mode" />	<s:hidden name="step" />
			
			<tr>
			<% List<String> candiReferencesNameValidList = hmValidationFields.get("CANDI_REFERENCES_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferencesNameValidList != null && uF.parseToBoolean(candiReferencesNameValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Name:<%=validAsterix%></td>
			<td><input type="text" name="ref1Name" class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref1Name"), "") %>"/></td>
			</tr>
			
			<tr>
			<% List<String> candiReferencesCompanyValidList = hmValidationFields.get("CANDI_REFERENCES_COMPANY"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferencesCompanyValidList != null && uF.parseToBoolean(candiReferencesCompanyValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Company:<%=validAsterix%></td>
			<td><input type="text" name="ref1Company" class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref1Company"), "") %>"/></td>
			</tr>
			
			<tr>
			<% List<String> candiReferencesDesignationValidList = hmValidationFields.get("CANDI_REFERENCES_DESIGNATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferencesDesignationValidList != null && uF.parseToBoolean(candiReferencesDesignationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Designation:<%=validAsterix%></td>
			<td><input type="text" name="ref1Designation" class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref1Designation"), "") %>" /></td>
			</tr>
			
			<tr>
			<% List<String> candiReferencesContactNoValidList = hmValidationFields.get("CANDI_REFERENCES_CONTACT_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferencesContactNoValidList != null && uF.parseToBoolean(candiReferencesContactNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Contact No:<%=validAsterix%></td>
			<td><input type="text" name="ref1ContactNo" class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref1ContactNo"), "") %>"/></td>
			</tr>
			
			<tr>
			<% List<String> candiReferencesEmailIdValidList = hmValidationFields.get("CANDI_REFERENCES_EMAIL_ID"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferencesEmailIdValidList != null && uF.parseToBoolean(candiReferencesEmailIdValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Email Id:<%=validAsterix%></td>
			<td><input type="text" name="ref1Email" class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref1Email"), "") %>"/></td>
			</tr>
			
		</table>
		
		<table border="0" class="table table_no_border">
			
			<tr><td>
			</td></tr>
			<tr><td class="tdLabelheadingBg alignCenter" colspan="2">Enter Candidate References 2:</td></tr>
			
			<tr>
			<% List<String> candiReferencesName2ValidList = hmValidationFields.get("CANDI_REFERENCES_NAME"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferencesName2ValidList != null && uF.parseToBoolean(candiReferencesName2ValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Name:<%=validAsterix%></td>
			<td><input type="text" name="ref2Name" class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref2Name"), "") %>"/></td>
			</tr>
			
			<tr>
			<% List<String> candiReferencesCompany2ValidList = hmValidationFields.get("CANDI_REFERENCES_COMPANY"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferencesCompany2ValidList != null && uF.parseToBoolean(candiReferencesCompany2ValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Company:<%=validAsterix%></td>
			<td><input type="text" name="ref2Company"  class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref2Company"), "") %>"/></td>
			</tr>
			
			<tr>
			<% List<String> candiReferences2DesignationValidList = hmValidationFields.get("CANDI_REFERENCES_DESIGNATION"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferences2DesignationValidList != null && uF.parseToBoolean(candiReferences2DesignationValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Designation:<%=validAsterix%></td>
			<td><input type="text" name="ref2Designation"  class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref2Designation"), "") %>"/></td>
			</tr>
		
			<tr>
			<% List<String> candiReferences2ContactNoValidList = hmValidationFields.get("CANDI_REFERENCES_CONTACT_NO"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferences2ContactNoValidList != null && uF.parseToBoolean(candiReferences2ContactNoValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Contact No:<%=validAsterix%></td>
			<td><input type="text" name="ref2ContactNo"  class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref2ContactNo"), "") %>"/></td>
			</tr>
			
			<tr>
			<% List<String> candiReferences2EmailIdValidList = hmValidationFields.get("CANDI_REFERENCES_EMAIL_ID"); 
					validReqOpt = "";
					validAsterix = "";
					if(candiReferences2EmailIdValidList != null && uF.parseToBoolean(candiReferences2EmailIdValidList.get(0))) {
						validReqOpt = "validateRequired";
						validAsterix = "<sup>*</sup>";
					}
			%>
			<td class="txtlabel alignRight">Email Id:<%=validAsterix%></td>
			<td><input type="text" name="ref2Email"  class=<%=validReqOpt%>" style="height: 25px;" value="<%=uF.showData((String)request.getAttribute("ref2Email"), "") %>"/></td>
			</tr>
			
		</table>
		
		
		
		<div style="float:right;">
			<table class="table table_no_border">
			
			<s:if test="mode==null">
			
				<tr><td colspan="2" align="center">
					<s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit" align="center" />
				</td></tr>
				
			</s:if>
			
			<s:else>
				
				<tr><td colspan="2" align="center">
					<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" />
				</td></tr>
				
			</s:else>
				
			</table>
		</div>
		
		<!-- ====start parvez on 01-07-2021 -->
		<div class="clr"></div>
		<!-- ====end parvez on 01-07-2021 -->
	</s:form>
	</div>
	</s:if>
	

<s:if test="step==6 || mode=='report'">
<div> <!-- Medical Information -->
<!-- ====start parvez on 01-07-2021 -->
<div style="height: auto; width:100%; border: solid 0px black; overflow: auto;" id="div_prev_employment">
<!-- ====end parvez on 01-07-2021 -->
<s:form theme="simple" action="AddCandidateByCadi" id="frmMedicalInfo" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="operation" /><s:hidden name="recruitId" /><s:hidden name="CandidateId" />
	<s:hidden name="mode" />	<s:hidden name="step" />
	<s:hidden name="show"></s:hidden>
	<table border="0" class="table table_no_border">
	    <tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 6: </span>Medical Information :-</td></tr>
	</table>
	    
	<table border="0" class="table table_no_border">
	
		<tr><td class="tdLabelheadingBg alignCenter" colspan="2">Enter Candidate's Medical Information </td></tr>
		<tr>
			<td>
			
			<table class="table table_no_border" style="font-size: 12px">
			<!-- ====start parvez on 02-07-2021===== -->
				 	<tr><td class="txtlabel alignRight" style="width:600px">Are you now receiving medical attention:</td>
			<!-- ====end parvez on 02-07-2021===== -->
				 		<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue1" onclick="checkRadio(this,'text1');"></s:radio>
				 			<s:hidden name="empMedicalId1" />
				 			<s:hidden name="que1Id" value="1"></s:hidden>
				 			<s:hidden name="que1IdFileStatus" id="que1IdFileStatus" value="0"></s:hidden>
				 		</td>
				 		<% List<String> candiMedical_1ValidList = hmValidationFields.get("CANDI_MEDICAL_1"); 
							validReqOpt = "";
							validAsterix = "";
							if(candiMedical_1ValidList != null && uF.parseToBoolean(candiMedical_1ValidList.get(0))) {
								validReqOpt = "validateRequired";
								validAsterix = "<sup>*</sup>";
							}
						%>
				 		<s:if test="checkQue1==true">
				 		<td><textarea  rows="7" cols="63" id="text1" name="que1Desc" class="<%=validReqOpt%>" > <%=uF.showData((String)request.getAttribute("que1Desc"), "") %></textarea></td>
				 		<td>
				 		<% if(candiMedical_1ValidList != null && uF.parseToBoolean(candiMedical_1ValidList.get(0))) { %>
				 			<s:file name="que1DescFile" id="text1File" cssClass="validateRequired" onchange="fillFileStatus('que1IdFileStatus')" />
				 		<% } else { %>
				 			<s:file name="que1DescFile" id="text1File" onchange="fillFileStatus('que1IdFileStatus')"/>
				 		<% } %> 	
				 		</td> 
				 		</s:if>
				 		<s:else>
				 		<td><textarea  rows="7" cols="63" id="text1" name="que1Desc" disabled="true" class="<%=validReqOpt%>" ><%=uF.showData((String)request.getAttribute("que1Desc"), "") %></textarea></td>
				 		<!-- ====start parvez on 01-07-2021 -->
				 		<td>
				 		<!-- ====end parvez on 01-07-2021 -->
				 		<% if(candiMedical_1ValidList != null && uF.parseToBoolean(candiMedical_1ValidList.get(0))) { %>
				 			<s:file name="que1DescFile" id="text1File" cssClass="validateRequired" disabled="true" onchange="fillFileStatus('que1IdFileStatus')"/>
				 		<% } else { %>
				 			<s:file name="que1DescFile" id="text1File" disabled="true" onchange="fillFileStatus('que1IdFileStatus')"/>
				 		<% } %> 
				 		</td>
				 		</s:else>
				 	</tr> 
				 	
				 	<!-- ====start parvez on 02-07-2021===== -->
					<tr><td class="txtlabel alignRight" style="width:600px">Have you had any form of serious illness or operation:</td>
					<!-- ====end parvez on 02-07-2021===== -->
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue2" onclick="checkRadio(this,'text2');"></s:radio>
							<s:hidden name="empMedicalId2" />
							<s:hidden name="que2Id" value="2"></s:hidden>
							<s:hidden name="que1IdFileStatus" id="que2IdFileStatus" value="0"></s:hidden>
						</td>
						<% List<String> candiMedical_2ValidList = hmValidationFields.get("CANDI_MEDICAL_2"); 
								validReqOpt = "";
								validAsterix = "";
								if(candiMedical_2ValidList != null && uF.parseToBoolean(candiMedical_2ValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
								}
						%>
						<s:if test="checkQue2==true">
				 		<td><textarea  rows="7" cols="63" id="text2" name="que2Desc" class="<%=validReqOpt%>" ><%=uF.showData((String)request.getAttribute("que2Desc"), "") %></textarea></td>
				 		<td>
				 		<%if(candiMedical_2ValidList != null && uF.parseToBoolean(candiMedical_2ValidList.get(0))) {%>
				 			<s:file name="que1DescFile" id="text2File" cssClass="validateRequired" onchange="fillFileStatus('que2IdFileStatus')"/>
				 		<%}else{%> 
				 			<s:file name="que1DescFile" id="text2File" onchange="fillFileStatus('que2IdFileStatus')"/>
				 		<% } %>
				 		</td> 
				 		</s:if>
				 		<s:else>
				 		<td><textarea  rows="7" cols="63" id="text2" name="que2Desc" disabled="true" ><%=uF.showData((String)request.getAttribute("que2Desc"), "") %></textarea></td>
				 		<td>
				 		<%if(candiMedical_2ValidList != null && uF.parseToBoolean(candiMedical_2ValidList.get(0))) {%>
				 			<s:file name="que1DescFile" id="text2File" cssClass="validateRequired" disabled="true" onchange="fillFileStatus('que2IdFileStatus')"/>
				 		<%}else{%> 
				 			<s:file name="que1DescFile" id="text2File" disabled="true" onchange="fillFileStatus('que2IdFileStatus')"/>
				 		<% } %>
				 		</td> 
				 		</s:else> 
					</tr> 
					
					<!-- ====start parvez on 02-07-2021===== -->
					<tr><td class="txtlabel alignRight" style="width:600px">Have you had any illness in the last two years? YES/NO If YES, 
							please give the details about the same and any absences from work: </td>
					<!-- ====end parvez on 02-07-2021===== -->
						<td><s:radio list="#{'true':'Yes','false':'No'}" name="checkQue3" onclick="checkRadio(this,'text3');"></s:radio>
							<s:hidden name="empMedicalId3" />
							<s:hidden name="que3Id" value="3"></s:hidden>
							<s:hidden name="que1IdFileStatus" id="que3IdFileStatus" value="0"></s:hidden>
						</td>
						<% List<String> candiMedical_3ValidList = hmValidationFields.get("CANDI_MEDICAL_3"); 
								validReqOpt = "";
								validAsterix = "";
								if(candiMedical_3ValidList != null && uF.parseToBoolean(candiMedical_3ValidList.get(0))) {
									validReqOpt = "validateRequired";
									validAsterix = "<sup>*</sup>";
							}
						%>
						<s:if test="checkQue3==true">
				 		<td><textarea  rows="7" cols="63" id="text3" name="que3Desc" class="<%=validReqOpt%>" ><%=uF.showData((String)request.getAttribute("que3Desc"), "") %></textarea></td>
				 		<td>
				 		<%if(candiMedical_3ValidList != null && uF.parseToBoolean(candiMedical_3ValidList.get(0))) { %>
				 			<s:file name="que1DescFile" id="text3File" cssClass="validateRequired" onchange="fillFileStatus('que3IdFileStatus')"/>
				 		<% }else{ %> 
				 			<s:file name="que1DescFile" id="text3File" onchange="fillFileStatus('que3IdFileStatus')" />
				 		<%} %>
				 		</td> 
				 		</s:if>
				 		<s:else>
				 		<td><textarea  rows="7" cols="63" id="text3" name="que3Desc" class="<%=validReqOpt%>" disabled="true" ><%=uF.showData((String)request.getAttribute("que3Desc"), "") %></textarea></td>
				 		<td>
				 		<%if(candiMedical_3ValidList != null && uF.parseToBoolean(candiMedical_3ValidList.get(0))) { %>
				 			<s:file name="que1DescFile" id="text3File" disabled="true" cssClass="validateRequired" onchange="fillFileStatus('que3IdFileStatus')" />
				 		<% }else{ %> 
				 			<s:file name="que1DescFile" id="text3File" disabled="true" onchange="fillFileStatus('que3IdFileStatus')" />
				 		<%} %>
				 		</td> 
				 		</s:else>
					</tr>
				</table>
			</td> 
		</tr>
	</table>
	<div class="clr"></div>
	<div style="float:right;">
				<table class="table table_no_border">
					<s:if test="mode==null">
						<tr><td colspan="2" align="center">
							<s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit & Proceed" align="center" />
						</td></tr>
					</s:if>
					
					<s:else>
						<tr><td colspan="2" align="center">
							<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" align="center" /> 
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

<form action="AddCandidateByCadi.action" id="frmDocumentation" method="POST" class="formcss" enctype="multipart/form-data">
<!-- ====start parvez on 01-07-2021 -->
<div style="height: auto; width:100%; border: solid 0px black; overflow: auto;" id="div_id_docs">
<!-- ====end parvez on 01-07-2021 -->
	<s:hidden name="operation" /><s:hidden name="recruitId" /><s:hidden name="CandidateId" />
	<s:hidden name="mode" />	<s:hidden name="step" />
	<s:hidden name="show"></s:hidden>
		<table border="0" class="table table_no_border">
	    <tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 7: </span>Attach Documents:-</td></tr>
	    </table>
				<% 	
					if(alDocuments!=null && alDocuments.size()!=0) {
						String empId = (String)((ArrayList)alDocuments.get(0)).get(3);
				%>
					<input type="hidden" name="empId" value="<%=empId%>" />
					<input type="hidden" name="otherDocumentCnt" id = "otherDocumentCnt" value="" />
					 <table class="table table_no_border" id="row_document_table">
                        <tr>
			                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Document Name</b></label></td>
			                <td class="txtlabel alignRight" style="text-align: -moz-center" ><label><b>Attached Document</b></label></td>
			                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Added By</b></label></td>
			                <td class="txtlabel alignRight" style="text-align: -moz-center"><label><b>Entry Date</b></label></td>
		                </tr>  
               
						<% for(int i=0; i<alDocuments.size(); i++) { 
						%>
						  <tr>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center"><input type="hidden" name="idDocType<%=i %>" value="<%=((ArrayList)alDocuments.get(i)).get(2)%>"></input>
						  			<input type="hidden" name="docId<%=i %>" value="<%=((ArrayList)alDocuments.get(i)).get(0)%>"/> <%=((ArrayList)alDocuments.get(i)).get(1)%>
						  		</td>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center">
						  		     <%String onChangeFunct = "fillFileStatus('idDoc1Status')";
						  		        if(i ==1) {
						  		        	onChangeFunct = "fillFileStatus('idDoc2Status')";
						  		        } else if(i == 2) {
						  		        	onChangeFunct = "fillFileStatus('idDoc3Status')";
						  		        }
						  		     %>
						  		 <!-- ===start parvez date: 18-10-2021=== -->
						  		     <input type="file" accept=".jpeg,.jpg,.png,.tif,.svg,.svgz,.doc,.docs,.docx,.pdf" name="idDoc<%=i%>" id="idDoc<%=i%>" class="<%=validReqOpt %>" onchange="<%=onChangeFunct%>"/>
						  		 <!-- ===end parvez date: 18-10-2021=== -->
						  		     <input type="hidden" name="idDocStatus<%=i%>" id="idDoc<%=i+1 %>Status" value="0"></input>
						  			<%-- <a href="<%=request.getContextPath()+"/userDocuments/"+((ArrayList)alDocuments.get(i)).get(4)%>" ><img src="images1/payslip.png" title="click to download"/></a> --%>
						  			<% if(((ArrayList)alDocuments.get(i)).get(4) != null && !((ArrayList)alDocuments.get(i)).get(4).toString().equalsIgnoreCase("null") && !((ArrayList)alDocuments.get(i)).get(4).toString().equals("")) { %>
							  			<%if(docRetriveLocation == null) { %>
											<a href="<%=request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alDocuments.get(i)).get(4) %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
										<%} else { %>
											<a href="<%=docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+(String)request.getAttribute("CandidateId")+"/"+ ((ArrayList)alDocuments.get(i)).get(4) %>" title="Reference Document" ><i class="fa fa-file-o" aria-hidden="true"></i></a>
										<%} %>
									<%} %>
						  		</td>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(6)%></td>
						  		<td class="txtlabel alignRight" style="text-align: -moz-center"><%=((ArrayList)alDocuments.get(i)).get(5)%></td>
		                 		
		                 </tr>
				 	<%}%>
				 	
				 	<tr>
				 	<td>
				 	</td>
				 	<td>
				 	</td>
				 	<td>
				 	</td>
				 	<td>
				 	</td>
				 	
				 	</tr>
				 	
				  </table>
				  <div style="float:right;margin-right:200px;">
				  
				 	 <a href="javascript:void(0)" onclick="addBackgroundVerificationDocuments(0);"><i class="fa fa-plus-circle"></i>Add More Documents...</a>&nbsp;&nbsp;
				
				 
				</div>	
				 <% } else { %>
					 
					 <div id="row_document">
					 <!-- ====start parvez on 01-07-2021 -->
	                    <table class="table table_no_border autoWidth" id='row_document_table1'>
	                    <!-- ====end parvez on 01-07-2021 -->
		                    <tr>
				                <td class="txtlabel alignRight"><label><b>Document Name</b></label></td>
				                <td class="txtlabel alignRight"><label><b>Attached Document</b></label></td>
			                </tr>
		                  
			                  <tr>
						 		<% 	List<String> candiDocResumeValidList = hmValidationFields.get("CANDI_DOC_RESUME"); 
									validReqOpt = "";
									validAsterix = "";
									if(candiDocResumeValidList != null && uF.parseToBoolean(candiDocResumeValidList.get(0))) {
										validReqOpt = "validateRequired";
										validAsterix = "<sup>*</sup>";
									}
								%>	
						 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_RESUME%>:<%=validAsterix %>
						 		<input type="hidden" name="idDocType0" value="<%=IConstants.DOCUMENT_RESUME%>"></input>
						 		<input type="hidden" style="" value="<%=IConstants.DOCUMENT_RESUME%>" name="idDocName0" ></input>
						 		<input type="hidden" name="idDocStatus0" id="idDoc1Status" value="0"></input>
						 		</td>
								<td class="txtlabel alignRight"><input type="file" name="idDoc0" id="idDoc0" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc1Status')"/></td>
		                    </tr>
						 	<tr>
						 		<% 	List<String> candiDocIdProofValidList = hmValidationFields.get("CANDI_DOC_IDENTITY_PROOF"); 
									validReqOpt = "";
									validAsterix = "";
									if(candiDocIdProofValidList != null && uF.parseToBoolean(candiDocIdProofValidList.get(0))) {
										validReqOpt = "validateRequired";
										validAsterix = "<sup>*</sup>";
									}
								%>
						 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ID_PROOF%>:<%=validAsterix %>
						 		<input type="hidden" name="idDocType1" value="<%=IConstants.DOCUMENT_ID_PROOF%>"></input>
						 		<input type="hidden" value="<%=IConstants.DOCUMENT_ID_PROOF%>" style="" name="idDocName1" ></input>
						 		<input type="hidden" name="idDocStatus1" id="idDoc2Status" value="0"></input>
						 		</td>
						 		<!-- Created By Dattatray Date : 30-June-2021 Note : changed  accept=".pdf,.docx,.doc,.docs"-->
								<td class="txtlabel alignRight"><input type="file" accept=".jpeg,.jpg,.png,.svg" name="idDoc1" id="idDoc1" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc2Status')"/></td>
		                   	</tr>
						 	<tr>
						 		<% 	List<String> candiDocAddressProofValidList = hmValidationFields.get("CANDI_DOC_ADDRESS_PROOF"); 
									validReqOpt = "";
									validAsterix = "";
									if(candiDocAddressProofValidList != null && uF.parseToBoolean(candiDocAddressProofValidList.get(0))) {
										validReqOpt = "validateRequired";
										validAsterix = "<sup>*</sup>";
									}
								%>
						 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_ADDRESS_PROOF%>:<%=validAsterix %>
						 		<input type="hidden" name="idDocType2" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>"></input>
						 		<input type="hidden" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>" style="" name="idDocName2" ></input>
						 		<input type="hidden" name="idDocStatus2" id="idDoc3Status" value="0"></input>
						 		</td>
						 		<!-- Created By Dattatray Date : 30-June-2021 Note : changed  accept=".jpeg,.jpg,.png,.svg"-->
								<td class="txtlabel alignRight"><input type="file" accept=".jpeg,.jpg,.png,.svg" name="idDoc2" id="idDoc2" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc3Status')"/></td>
					    	</tr>
					   <!-- ===start parvez date: 28-10-2022=== -->
					   		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_COMPANY_PROFILE_DOCUMENT))){ %>		
						   		<tr>
							 		<% 	List<String> candiDocCompanyProfileValidList = hmValidationFields.get("CANDI_DOC_COMPANY_PROFILE"); 
										validReqOpt = "";
										validAsterix = "";
										if(candiDocCompanyProfileValidList != null && uF.parseToBoolean(candiDocCompanyProfileValidList.get(0))) {
											validReqOpt = "validateRequired";
											validAsterix = "<sup>*</sup>";
										}
									%>
							 		<td class="txtlabel alignRight"><%=IConstants.DOCUMENT_COMPANY_PROFILE%>:<%=validAsterix %>
							 		<input type="hidden" name="idDocType3" value="<%=IConstants.DOCUMENT_COMPANY_PROFILE%>"></input>
							 		<input type="hidden" value="<%=IConstants.DOCUMENT_COMPANY_PROFILE%>" style="" name="idDocName3" ></input>
							 		<input type="hidden" name="idDocStatus3" id="idDoc4Status" value="0"></input>
							 		</td>
							 		<td class="txtlabel alignRight"><input type="file" accept=".jpeg,.jpg,.png,.svg,.pdf,.docx,.doc,.docs" name="idDoc3" id="idDoc3" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc4Status')"/></td>
						    	</tr>
					    	<% } %>	
				   <!-- ===end parvez date: 28-10-2022=== -->
					    	
					    	<!--  <tr><td>
					    		<a href="javascript:void(0)" onclick="addBackgroundVerificationDocuments(0);"><i class="fa fa-plus-circle"></i>Add More Documents...</a>&nbsp;&nbsp;
					    	</td>
					    	</tr>-->
				    	</table>
				    	 <div style="float:right;margin-right:200px;">
				    	 		<a href="javascript:void(0)" onclick="addBackgroundVerificationDocuments(3, 1);"><i class="fa fa-plus-circle"></i>Add More Documents...</a>&nbsp;&nbsp;
				    	 
						</div>	
				    </div>
				 <%} %>
				 <div class="clr"></div>
				 <div style="float:right;">
				<table class="table table_no_border">
				
				<s:if test="mode==null">
				
					<tr><td colspan="2" align="center">
						<s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="width:200px; float:right;" value="Submit" align="center" />
					</td></tr>
					
				</s:if>
				
				<s:else>
					
					<tr><td colspan="2" align="center">
						<s:submit cssClass="btn btn-primary" name="stepSubmit" value="Submit & Proceed" onclick = "documentCount()" align="center" />
					</td></tr>
					
				</s:else>
					
				</table>
			</div>
</div>				 
</form>

</div>
</s:if>

<s:if test="step==8 || mode=='report'">

<div>
<%-- <script>
$(function() {
	$("input[name=strDate]").datepicker({format: 'dd/mm/yy'});
	$( "input[name=strTime]" ).timepicker({});
});
</script> --%>

<form action="AddCandidateByCadi.action" id="frmAvailibility" method="POST" class="formcss">
	<s:hidden name="operation"/><s:hidden name="recruitId"/>
	<s:hidden name="CandidateId"/>
	<s:hidden name="mode"/>
	<s:hidden name="step"/>
	<s:hidden name="candibymail"/>
	<s:hidden name="show"></s:hidden>
	<%if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.HRMANAGER)){ %>
	<table border="0" class="table table_no_border" style="font-size: 12px">
	    <tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px"></span>  <%=uF.showData((String)request.getAttribute("CandidateName"), "") %> Availability for interview:-</td></tr>
	    <tr><td colspan="3">Please enter  <%=uF.showData((String)request.getAttribute("CandidateName"), "") %> availability details. Interview would be sheduled based on any of the available time specified by you. </td></tr>
	    <tr><td colspan="3">Please ignore this step if <%=uF.showData((String)request.getAttribute("CandidateName"), "") %> have already given Interview. </td></tr>
	</table>
	<%}else{ %>
	<table border="0" class="table table_no_border" style="font-size: 12px">
	    <tr><td  class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px">Step 8: </span> Your Availability for interview:-</td></tr>
	    <tr><td colspan="3">Please enter your availability details. You will be scheduled for an interview based on any of the available time specified by you. </td></tr>
	    <tr><td colspan="3">Please ignore this step if you have alreay joined the organisation. </td></tr>
	</table>
	<%} %>

	<div>
<table class="table table_no_border" style="width:500px">
	<tr>
		<th>&nbsp;</th>
		<th class="txtlabel alignRight">Date</th>
		<th class="txtlabel" align="left">Time</th>
	</tr>
	
	<%
     Map<String,String> hmDatesSelected	= (Map<String,String>)request.getAttribute("hmDatesSelected");
     Map<String,String> hmDatesRejected	= (Map<String,String>)request.getAttribute("hmDatesRejected");
 	 List<String> alDates = (List<String>)request.getAttribute("alDates");
 	 Map<String,String> hmDates = (Map<String,String>)request.getAttribute("hmDates");
 	 	//System.out.println("alDates ===> "+alDates);
 		//System.out.println("hmDates ===> "+hmDates);
    %>
	<tr>
		<% List<String> candiInterviewAvailable_1ValidList = hmValidationFields.get("CANDI_INTERVIEW_AVAILABLE_1"); 
			validReqOpt = "";
			validAsterix = "";
			if(candiInterviewAvailable_1ValidList != null && uF.parseToBoolean(candiInterviewAvailable_1ValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
			}
		%>
		<td class="txtlabel alignRight">Option 1:<%=validAsterix %></td>
		
		<td align="right"><input type="text" name="strDate" class="<%=validReqOpt %>" style="height: 25px; width: 75px;"
		<%if(alDates.size()>0 &&  alDates.get(0)!=null) {%>
		value="<%=alDates.get(0)%>"
		<%} %>
		></td>
		<td><input type="text" name="strTime" class="<%=validReqOpt %>" style="height: 25px; width: 75px;"
		<%if(alDates.size()>0  && hmDates.get(alDates.get(0))!=null) {%>
		value="<%=hmDates.get(alDates.get(0)) %>"
		<%} %>
		>
		</td>
		
	</tr>
	
	<tr>
		<% List<String> candiInterviewAvailable_2ValidList = hmValidationFields.get("CANDI_INTERVIEW_AVAILABLE_2"); 
			validReqOpt = "";
			validAsterix = "";
			if(candiInterviewAvailable_2ValidList != null && uF.parseToBoolean(candiInterviewAvailable_2ValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
			}
		%>
		<td class="txtlabel alignRight">Option 2:<%=validAsterix %></td>

		
		<td align="right"><input type="text" name="strDate" class="<%=validReqOpt %>" style="height: 25px; width: 75px;"
		<%if(alDates.size()>0 &&  alDates.get(0)!=null) {%>
		value="<%=alDates.get(0)%>"
		<%} %>
		></td>
		<td><input type="text" name="strTime" class="<%=validReqOpt %>" style="height: 25px; width: 75px;"
		<%if(alDates.size()>0  && hmDates.get(alDates.get(0))!=null) {%>
		value="<%=hmDates.get(alDates.get(0)) %>"
		<%} %>
		>
	</tr>
	
	<tr>
		<% List<String> candiInterviewAvailable_3ValidList = hmValidationFields.get("CANDI_INTERVIEW_AVAILABLE_3"); 
			validReqOpt = "";
			validAsterix = "";
			if(candiInterviewAvailable_3ValidList != null && uF.parseToBoolean(candiInterviewAvailable_3ValidList.get(0))) {
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
			}
		%>
		<td class="txtlabel alignRight">Option 3:<%=validAsterix %></td>

		
		<td align="right"><input type="text" name="strDate" class="<%=validReqOpt %>" style="height: 25px; width: 75px;"
		<%if(alDates.size()>0 &&  alDates.get(0)!=null) {%>
		value="<%=alDates.get(0)%>"
		<%} %>
		></td>
		<td><input type="text" name="strTime" class="<%=validReqOpt %>" style="height: 25px; width: 75px;"
		<%if(alDates.size()>0  && hmDates.get(alDates.get(0))!=null) {%>
		value="<%=hmDates.get(alDates.get(0)) %>"
		<%} %>
		>
		</td>
		
		</tr>
	</table>
	</div>

		
<!-- 			<div style="float: left; margin: 50px; width: 40%;" id="selectedDates"> -->

      

 		<% 	
        if(hmDatesSelected!=null && hmDatesSelected.size()>0){%>
<!-- 		<div style="border: 2px solid #ccc;"> -->
  		<div style="float:left;border: 2px solid #ccc;margin-top:45px;">
        <b>Selected Dates</b>
        <table class="table table_no_border" border="0" width="100%">
        <thead>
        <tr>
        <th>
        S.No
        </th>
         <th>
        Date
        </th>
        <th>
        Interview Person
        </th>
        </tr>
        </thead>
        <%
           Iterator<String> it= hmDatesSelected.keySet().iterator();
        int i=0;
        while(it.hasNext()){
        	String key=it.next();
        %>
            <tr>
                <td nowrap="nowrap" style="font-weight: bold;"><%=i+1 %></td>
                <td align="left"><%=key %></td>
                <td align="left"><%=hmDatesSelected.get(key) %></td>
            </tr>
            <%
        i++;    
        }
            %>
        </table>
   		 </div> 
 		<%} %>

  <% 	
        if(hmDatesRejected!=null && hmDatesRejected.size()>0){%>
  		<div style="float:left;border: 2px solid #ccc;margin-left:15px;margin-top:45px;">
        <b>Rejected Dates</b>
        <table class="table table_no_border" border="0" width="100%">
        <thead>
        <tr>
        <th>
        S.No
        </th>
         <th>
        Date
        </th>
        <th>
        Interview Person
        </th>
        </tr>
        </thead>
        
        <%
           Iterator<String> it= hmDatesRejected.keySet().iterator();
        int i=0;
        while(it.hasNext()){
        	String key=it.next();
        %>
            <tr>
                <td nowrap="nowrap" style="font-weight: bold;"><%=i+1 %></td>
                <td align="left"><%=key %></td>
                <td align="left"><%=hmDatesRejected.get(key) %></td>
            </tr>
            <%
        i++;    
        }
            %>
        </table>
   		 </div>
     		<%} %>
 		
	<div style="float: right; margin-top: 200px;">
				<table class="table table_no_border">
					<tr><td align="center" colspan="2">
					</td></tr><tr>
    				<td colspan="2"><div align="center">
    				<input type="button" name="save" style="width: 200px; float: right;" class="btn btn-primary" value="Submit &amp; Proceed" onclick="submitForm();">
    				<!-- <input type="submit" style="width: 200px; float: right;" class="input_button" value="Submit &amp; Proceed" id=""> -->
					</div></td>
				</tr>
				</table>
		</div>
		<!-- ====start parvez on 01-07-2021 -->
		<div class="clr"></div>
		<!-- ====start parvez on 01-07-2021 -->
</form>
	</div>
</s:if>

	<script>
	showMarriageDate();
	validateMandatory(document.frmOfficial.empType.options[document.frmOfficial.empType.options.selectedIndex].value);
	</script>

</div>
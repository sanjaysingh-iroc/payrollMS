 

<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
$(document).ready( function () {
	$("#addCertificateSubmit").click(function(){
		$(".validateRequired").prop('required',true);
	});
});
</script>

<script type="text/javascript">
$(document).ready(function(){
	
	var borderType = document.getElementById("certificateBorder").value;
	changeBorderType(borderType);
	
	var fontsz = document.getElementById("fontSizeId").value;
	changeFontSize(fontsz);
	
	var logoAlign = document.getElementById("certiLogoAlign").value;
	changeLogoAlign(logoAlign);
	/* changeFirstLine('');
	changeSecondLine('');
	changeThirdLine(''); */
});
	

 function changeBorderType(value) { 
	 if(value == 1){
		$('#addNewCertificateDiv').removeClass('certi_border2');
		$('#addNewCertificateDiv').removeClass('certi_border3');
		$('#addNewCertificateDiv').addClass('certi_border1');
	 }else if(value == 2){
		$('#addNewCertificateDiv').removeClass('certi_border1');
		$('#addNewCertificateDiv').removeClass('certi_border3');
		$('#addNewCertificateDiv').addClass('certi_border2');
	 }else if(value == 3){
		$('#addNewCertificateDiv').removeClass('certi_border2');
		$('#addNewCertificateDiv').removeClass('certi_border1');
		$('#addNewCertificateDiv').addClass('certi_border3');
	 }
 }
 
 function changeFirstLine(value) {
	 
/* ===start parvez date: 11-03-2022=== */	 
	 var flinelbl = $( "#firstLineId option:selected" ).text();
	 $("p#firstLineP").text(flinelbl);
/* ===end parvez date: 11-03-2022=== */	 
	
	/* var flinelbl = document.getElementById('firstLine')[document.getElementById('firstLine').selectedIndex].innerHTML;
	 document.getElementById("firstLineP").innerHTML = flinelbl; */

 }
 
 function changeSecondLine(value) {
	 /* var slinelbl = document.getElementById('secondLine')[document.getElementById('secondLine').selectedIndex].innerHTML;
	// alert("slinelbl ===> " +slinelbl);
	 document.getElementById("secondLineP").innerHTML = slinelbl; */
	 
/* ===start parvez date: 11-03-2022=== */	 
	 var slinelbl = $( "#secondLineId option:selected" ).text();
	 $("p#secondLineP").text(slinelbl);
/* ===end parvez date: 11-03-2022=== */	 
	 
 }

 function changeThirdLine(value) {
	 /* var tlinelbl = document.getElementById('thirdLine')[document.getElementById('thirdLine').selectedIndex].innerHTML;
	 //alert("tlinelbl ===> " +tlinelbl);
	 document.getElementById("thirdLineP").innerHTML = tlinelbl; */
	 
/* ===start parvez date: 11-03-2022=== */	 
	 var tlinelbl = $( "#thirdLineId option:selected" ).text();
	 $("p#thirdLineP").text(tlinelbl);
/* ===end parvez date: 11-03-2022=== */	 
	 
 }
 
 function changeFontSize(value) {
	 /* var fontsz = document.getElementById('fontSize')[document.getElementById('fontSize').selectedIndex].innerHTML; */
	 //alert("fontsz ===> " + fontsz);
	 
/* ===start parvez date: 11-03-2022=== */	 
	 var fontsz = $( "#fontSizeId option:selected" ).text();
/* ===end parvez date: 11-03-2022=== */	 
	 document.getElementById("firstLineP").style.fontSize = fontsz;
	 document.getElementById("secondLineP").style.fontSize = fontsz;
	 document.getElementById("thirdLineP").style.fontSize = fontsz;
 }
 
 function addCertificateTitle() {
	 var certiTitle = document.getElementById("certificateTitle").value;
	 document.getElementById("certiTitleP").innerHTML = certiTitle; 
 }
 
 function changeLogoAlign(value) {
	 if(value == 1) {
		 document.getElementById("certiLogoP").style.textAlign = "left";
	 } else if(value == 2) {
		 document.getElementById("certiLogoP").style.textAlign = "right";
	 } else if(value == 3) {
		 document.getElementById("certiLogoP").style.textAlign = "center";
	 }
	 
 }
 
</script>

 <%
String certificateTitle = (String)request.getAttribute("certificateTitle");
String certiLogoAlign = (String)request.getAttribute("certiLogoAlign");
String certificateBorder = (String)request.getAttribute("certificateBorder");
String strFirstLine = (String)request.getAttribute("strFirstLine");
String strSecondLine = (String)request.getAttribute("strSecondLine");
String strThirdLine = (String)request.getAttribute("strThirdLine");
String fontSize = (String)request.getAttribute("fontSize");
String signOne = (String)request.getAttribute("signOne");
String signTwo = (String)request.getAttribute("signTwo");
String signThree = (String)request.getAttribute("signThree");
String operation = (String)request.getAttribute("operation");
String certiId = (String)request.getAttribute("certiId");
%>
 
<div id="idAddCertificate">
<s:form theme="simple" action="AddCertificate" name="frmAddCertificate" id="frmAddCertificate" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="operation" id="operation"></s:hidden>
	<s:hidden name="certiId"></s:hidden>
	<s:hidden name="type" id="typeid"></s:hidden>
 	<div class="row">
 		<div class="col-lg-6 col-md-6 col-sm-12">
 			<table class="table table_no_border">
				<tr>
					<td class="txtlabel alignRight">Certificate Name:<sup>*</sup></td>
					<td><s:textfield name="certificateName" id="certificateName" cssStyle="width: 250px;" cssClass="validateRequired form-control" required="true" />
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Certificate Title:<sup>*</sup></td>
					<td><s:textfield name="certificateTitle" id="certificateTitle" cssStyle="width: 250px;" cssClass="validateRequired form-control" required="true" onkeyup="addCertificateTitle();"/>
					</td>
				</tr>
		
				<tr>
					<td class="txtlabel alignRight">Certificate Logo Align:<sup>*</sup></td>
					<td>
					<s:select theme="simple" cssClass="validateRequired form-control" name="certiLogoAlign" id="certiLogoAlign" listKey="id" 
										listValue="name" headerKey=""  headerValue="Select" list="certiLogoAlignList" onchange="changeLogoAlign(this.value)"/>					
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Certificate Border:<sup>*</sup></td>
					<td>
					<%-- <s:select name="skills" id="skills" headerKey="" headerValue="Select Skills" theme="simple" listKey="skillsId" 
										listValue="skillsName" list="skillslist" size="4" multiple="true" cssClass="validateRequired" value="skillsID" /> --%>
					<s:select theme="simple" cssClass="validateRequired form-control " name="certificateBorder" id="certificateBorder" listKey="id" 
										listValue="name" headerKey=""  headerValue="Select" list="borderList" onchange="changeBorderType(this.value)"/>					
					<%-- <s:select theme="simple" cssClass="validateRequired" name="certificateBorder" id="certificateBorder" headerKey="" headerValue="Select" 
					list="#{'1':'Border 1', '2':'Border 2', '3':'Border 3'}" onchange="checngeBorderType(this.value)"/> --%>
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">First Line:<sup>*</sup></td>
					<td>
			<!-- ===start parvez date: 11-03-2022=== Note: changed id="firstLine" -->		
					<s:select theme="simple" cssClass="validateRequired form-control " name="firstLine" id="firstLineId" listKey="id" headerKey="" headerValue="Select" 
										listValue="name" list="firstLineList" onchange="changeFirstLine(this.value)"/>
			<!-- ===end parvez date: 11-03-2022=== -->							
										
					<%-- <s:select theme="simple" cssClass="validateRequired" name="firstLine" id="firstLine" headerKey="" headerValue="Select" 
					list="#{'First Line 1':'First Line 1', 'First Line 2':'First Line 2', 'First Line 3':'First Line 3'}" /> --%>
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Second Line:<sup>*</sup></td>
					<td>
			<!-- ===start parvez date: 11-03-2022=== Note: changed id="secondLine" -->		
					<s:select theme="simple" cssClass="validateRequired form-control " name="secondLine" id="secondLineId" listKey="id" headerKey="" headerValue="Select" 
										listValue="name" list="secondLineList" onchange="changeSecondLine(this.value)"/>
			<!-- ===end parvez date: 11-03-2022=== -->	
				
					<%-- <s:select theme="simple" cssClass="validateRequired" name="secondLine" id="secondLine" headerKey="" headerValue="Select" 
					list="#{'Second Line 1':'Second Line 1', 'Second Line 2':'Second Line 2', 'Second Line 3':'Second Line 3'}" /> --%>
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Third Line:<sup>*</sup></td>
					<td>
			<!-- ===start parvez date: 11-03-2022=== Note: changed id="thirdLine" -->		
					<s:select theme="simple" cssClass="validateRequired form-control " name="thirdLine" id="thirdLineId" listKey="id" headerKey="" headerValue="Select" 
										listValue="name" list="thirdLineList" onchange="changeThirdLine(this.value)"/>
			<!-- ===end parvez date: 11-03-2022=== -->	
				
					<%-- <s:select theme="simple" cssClass="validateRequired" name="thirdLine" id="thirdLine" headerKey="" headerValue="Select" 
					list="#{'Third Line 1':'Third Line 1', 'Third Line 2':'Third Line 2', 'Third Line 3':'Third Line 3'}" /> --%>
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Font Size:<sup>*</sup></td>
					<td>
					
			<!-- ===start parvez date: 11-03-2022=== Note: changed id="fontSize" -->		
					<s:select theme="simple" cssClass="validateRequired form-control " name="fontSize" id="fontSizeId" listKey="id" headerKey="" headerValue="Select" 
										listValue="name" list="firstFontList" onchange="changeFontSize(this.value)"/>
			<!-- ===end parvez date: 11-03-2022=== -->		
					
					<%-- <s:select theme="simple" cssClass="validateRequired" name="fontSize" id="fontSize" headerKey="" headerValue="Select" 
					list="#{'1':'Font Size 1', '2':'Font Size 2', '3':'Font Size 3'}" /> --%>
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Sign 1:<sup>*</sup></td>
					<td><s:select theme="simple" cssClass="validateRequired form-control " name="signOne" id="signOne" headerKey="" headerValue="Select" 
					list="#{'1':'Sign Left 1', '2':'Sign Left 2', '3':'Sign Left 3'}" />
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Sign 2:<sup>*</sup></td>
					<td><s:select theme="simple" cssClass="validateRequired form-control " name="signTwo" id="signTwo" headerKey="" headerValue="Select" 
					list="#{'1':'Sign Center 1', '2':'Sign Center 2', '3':'Sign Center 3'}" />
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">Sign 3:<sup>*</sup></td>
					<td><s:select theme="simple" cssClass="validateRequired form-control " name="signThree" id="signThree" headerKey="" headerValue="Select" 
					list="#{'1':'Sign Right 1', '2':'Sign Right 2', '3':'Sign Right 3'}" />
					</td>
				</tr>
		
				<tr>
					<td></td>
					<td style="height: 20px;" colspan="2">
					<% if(operation != null && operation.equals("U")) { %>
						<s:submit cssClass="btn btn-primary" cssStyle="width:125px;" value="Save Certificate" name="addCertificateSubmit" id="addCertificateSubmit" />
					<% } else { %>
						<s:submit cssClass="btn btn-primary" cssStyle="width:125px;" value="Add Certificate" name="addCertificateSubmit" id="addCertificateSubmit" />
					<% } %>	
					</td>
				</tr>
		
			</table>
 		</div>
 		<div class="col-lg-6 col-md-6 col-sm-12">
 			<div id="addNewCertificateDiv" class="certi_border1">
				<p id="certiLogoP" style="text-align: center;"><img style="width: 25px; height: 25px;" src="images1/certificate_img.png"/> </p>
				<p id="certiTitleP" style="text-align: center; font-weight: bold; font-size: 22px; font-family: Tahoma; margin: 15px 0px;">
					<%=certificateTitle != null ? certificateTitle : "Add Title" %>
				</p>
				<p id="firstLineP" style="text-align: center; font-style: italic; margin: 2px 0px;">
					<%=strFirstLine != null ? strFirstLine : "First line here ... " %></p>
				<p id="secondLineP" style="text-align: center; font-style: italic; margin: 2px 0px;">
					<%=strSecondLine != null ? strSecondLine : "Second line here ... " %></p>
				<p id="thirdLineP" style="text-align: center; font-style: italic; margin: 2px 0px;">
					<%=strThirdLine != null ? strThirdLine : "Third line here ..." %></p>
				<br/><br/>
				<br/><br/>
				<p id="bottomSignP" style="font-style: italic; margin: 2px 0px;">
					<span id="leftSignSpan" style="float: left; width: 33%; text-align: center;">Left sign</span>
					<span id="centerSignSpan" style="float: left; width: 33%; text-align: center;">Middle sign</span>
					<span id="rightSignSpan" style="float: left; width: 33%; text-align: center;">Right sign</span>
				</p>
			</div>
 		</div>
 	</div>
</s:form>
</div>

<script type="text/javascript">

   $('#frmAddCertificate').submit(function(event) {
	   event.preventDefault();
	   var certiId = '<%=certiId%>';
	  // alert("certiId==>"+certiId);
	   var saveBtn = document.getElementById("addCertificateSubmit").value;
	 //  alert("saveBtn==>"+saveBtn);
	   var form_data = $('#frmAddCertificate').serialize();
	   $.ajax({
		   type:'POST',
		   url:'AddCertificate.action?addCertificateSubmit='+saveBtn,
		   data:form_data /* ,
		   success:function(result){
			   getLearningDashboardData('CertificateInfo','LD','5');
		   }  */
	   });
	   
	 /* ===start parvez date: 10-03-2022 */  
	   getLearningDashboardData('CertificateInfo');
	 /* ===end parvez date: 10-03-2022=== */
   });
   
   
</script>

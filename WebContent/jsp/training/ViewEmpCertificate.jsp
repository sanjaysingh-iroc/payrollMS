

<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<style>
#certificateDiv{
	
	border: 25px solid transparent;
	padding: 10px 20px;
	width: 90% !important;
	border-image: url(images1/icons/icons/certi_border1.png) 30 30 round;
}
</style>
<script type="text/javascript">

$(document).ready(function(){
	
	var borderType = document.getElementById("certificateBorder").value;
	//alert("borderType --- " + borderType);
	changeBorderType(borderType);
	
	/* var fontsz = document.getElementById("fontSize").value;
	alert("fontsz --- " + fontsz);
	changeFontSize(fontsz); */
	
	var logoAlign = document.getElementById("certiLogoAlign").value;
	//alert("logoAlign --- " + logoAlign);
	changeLogoAlign(logoAlign);
});
	

 function changeBorderType(value) { 
	 if(value == 1){
		$('#certificateDiv').removeClass('certi_border2');
		$('#certificateDiv').removeClass('certi_border3');
		$('#certificateDiv').addClass('certi_border1');
	 }else if(value == 2){
		$('#certificateDiv').removeClass('certi_border1');
		$('#certificateDiv').removeClass('certi_border3');
		$('#certificateDiv').addClass('certi_border2');
	 }else if(value == 3){
		$('#certificateDiv').removeClass('certi_border2');
		$('#certificateDiv').removeClass('certi_border1');
		$('#certificateDiv').addClass('certi_border3');
	 }
 }
 
 /* function changeFontSize(value) {
	 var fontsz = document.getElementById('fontSize')[document.getElementById('fontSize').selectedIndex].innerHTML;
	 //alert("fontsz ===> " + fontsz);
	 document.getElementById("firstLineP").style.fontSize = fontsz;
	 document.getElementById("secondLineP").style.fontSize = fontsz;
	 document.getElementById("thirdLineP").style.fontSize = fontsz;
 } */
 
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

<%-- <%List<String> alCertificateDetails=(List<String>) request.getAttribute("alCertificateDetails");
String image_size="583px 840px";
if(alCertificateDetails.get(2)!=null && alCertificateDetails.get(2).equals("1")){
	image_size="840px 583px";
}
%> --%>

<%
String certificateTitle = (String)request.getAttribute("certificateTitle");
String certiLogoAlign = (String)request.getAttribute("certiLogoAlign");
String certificateBorder = (String)request.getAttribute("certificateBorder");
String strFirstLine = (String)request.getAttribute("strFirstLine");
String strSecondLine = (String)request.getAttribute("strSecondLine");
String strThirdLine = (String)request.getAttribute("strThirdLine");
String strfontSize = (String)request.getAttribute("strfontSize");
String signOne = (String)request.getAttribute("signOne");
String signTwo = (String)request.getAttribute("signTwo");
String signThree = (String)request.getAttribute("signThree");
%>
	<div id="certificateDiv" class="certi_border1" style="float:left; min-height: 300px; width: 78%; margin-left: 20px; margin-top: 30px;">
	
	<s:hidden name="certificateName" id="certificateName"></s:hidden>
	<s:hidden name="certificateTitle" id="certificateTitle"></s:hidden>
	<s:hidden name="certiLogoAlign" id="certiLogoAlign"></s:hidden>
	<s:hidden name="certificateBorder" id="certificateBorder"></s:hidden>
	<s:hidden name="firstLine" id="firstLine"></s:hidden>
	<s:hidden name="secondLine" id="secondLine"></s:hidden>
	<s:hidden name="thirdLine" id="thirdLine"></s:hidden>
	<s:hidden name="fontSize" id="fontSize"></s:hidden>
	<s:hidden name="signOne" id="signOne"></s:hidden>
	<s:hidden name="signTwo" id="signTwo"></s:hidden>
	<s:hidden name="signThree" id="signThree"></s:hidden>
	
		<p id="certiLogoP" style="text-align: center;"><img style="min-width: 25px; height: 25px;" src="images1/certificate_img.png"/> </p>
		<p id="certiTitleP" style="text-align: center; font-weight: bold; font-size: 22px; font-family: Tahoma; margin: 15px 0px;">
			<%=certificateTitle != null ? certificateTitle : "Add Title" %>
		</p>
		<p id="firstLineP" style="text-align: center; font-style: italic; margin: 2px 0px; <% if(strfontSize != null && !strfontSize.equals("")) {%>font-size: <%=strfontSize %> <% } %>">
			<%=strFirstLine != null ? strFirstLine : "First line here ... " %></p>
		<p id="secondLineP" style="text-align: center; font-style: italic; margin: 2px 0px; <% if(strfontSize != null && !strfontSize.equals("")) {%>font-size: <%=strfontSize %> <% } %>">
			<%=strSecondLine != null ? strSecondLine : "Second line here ... " %></p>
		<p id="thirdLineP" style="text-align: center; font-style: italic; margin: 2px 0px; <% if(strfontSize != null && !strfontSize.equals("")) {%>font-size: <%=strfontSize %> <% } %>">
			<%=strThirdLine != null ? strThirdLine : "Third line here ..." %></p>
		<br/><br/>
		<br/><br/>
		<p id="bottomSignP" style="font-style: italic; margin: 2px 0px;">
			<span id="leftSignSpan" style="float: left; width: 33%; text-align: center;">Left sign</span>
			<span id="centerSignSpan" style="float: left; width: 33%; text-align: center;">Middle sign</span>
			<span id="rightSignSpan" style="float: left; width: 33%; text-align: center;">Right sign</span>
		</p>
	</div>
	
<%-- <div  style="background-image: url('<%=alCertificateDetails.get(0) %>') ;
	height: 100%;background-size:<%=image_size %>;background-repeat: no-repeat;" >

<%=alCertificateDetails.get(1) %>
</div>  --%>
 
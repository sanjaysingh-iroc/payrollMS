
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">

$(document).ready(function(){
	
	var borderType = document.getElementById("certificateBorder").value;
	changeBorderType(borderType);
	
	var logoAlign = document.getElementById("certiLogoAlign").value;
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
 
 function changeLogoAlign(value) {
	 if(value == 1) {
		 document.getElementById("certiLogoP").style.textAlign = "left";
	 } else if(value == 2) {
		 document.getElementById("certiLogoP").style.textAlign = "right";
	 } else if(value == 3) {
		 document.getElementById("certiLogoP").style.textAlign = "center";
	 }
	 
 }
 
 function deleteCertificate(operation,certiId) {
	   if(confirm('Are you sure, you want to delete this Certificate?')) {
		  var strAction = 'AddCertificate.action?operation='+operation+'&certiId='+certiId;
		  $('#divResult').html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
		  $.ajax({
			   type:'POST',
			   url:strAction,
			   success:function(result){
				   $('#divResult').html(result);
			   }
		   });
	   }
 }
 
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	List<String> certiDetails = (ArrayList<String>)request.getAttribute("certiDetails");
	if(certiDetails == null) certiDetails = new ArrayList<String>();
	
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
	String flag = (String)request.getAttribute("flag");
	
	String fromPage = (String)request.getAttribute("fromPage");
	
%>
	<div class = "box-body">
		<div style="width:100%;font-size:14px;">
			<%if(fromPage == null || fromPage.equals("")){ %>
				<% if(!uF.parseToBoolean(certiDetails.get(7))) { %>
					 <div style="float:left; padding-right: 5px;" id="myDivStatus"><i class="fa fa-circle" title="Waiting for live" aria-hidden="true" style="color:#ea9900"></i><!-- <img src="images1/icons/pullout.png" title="Waiting for live" /> --></div>
				<% } else { %>
					<div style="float:left; padding-right: 5px;" id="myDivStatus"><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i><!-- <img src="images1/icons/approved.png" title="Live" /> --></div>
				<% }%>
			<%} %>
			
			<span style="font-weight:bold;"><%=certiDetails.get(1)%></span>  last updated by <%=certiDetails.get(3)%> on <%=certiDetails.get(4)%>.
			<%if(fromPage != null && fromPage.equals("LD")){ %>
				<%if(!uF.parseToBoolean(flag)) { %>
					<a style="float: right; margin-left: 5px;" onclick="deleteCertificate('D','<%=certiDetails.get(0)%>')" href="javascript:void(0);" style="float: left"><i class="fa fa-trash" aria-hidden="true"></i></a>
					<% if(!uF.parseToBoolean(certiDetails.get(7))) { %>
						<a style="float: right;" onclick="addCertificate('E', '<%=certiDetails.get(0)%>', '')" href="javascript:void(0)"  style="text-indent: -99999px; float: left; margin-right:10px; padding: 0"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					<% } else { %>
						<a style="float: right;" onclick="createNewVersionOfCerti('<%=certiDetails.get(0)%>');" href="javascript:void(0)" style="text-indent: -99999px; float: left; margin-right:10px; padding: 0"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					<% } %>
				<%} %>
			<%} %>
		</div>
		<div id="certificateDiv" class="certi_border1" style=" min-height: 300px; margin-top:10px;">
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
  </div>
 
<%@page import="com.konnect.jpms.util.IConstants"%>
<div class="footer_content">
  <p>Powered By <strong>Workrig </strong>. All Rights Reserved</p>
</div>  <!-- Taskrig Solutions Pvt. Ltd. -->
<%session.setAttribute(IConstants.MESSAGE,"");%>

<style>
.showNotification{
height:100%;width:100%;position:fixed;z-index:10000
}
</style>

<%if(session.getAttribute("clock_inout_reminder")!=null){ %>

<script>
 $(document).ready(function(){ 
    timeOut();
 }); 

function timeOut() {
	 setTimeout("showPopup();", 15000);
}

function showPopup() {
  	if(document.getElementById('the_div1')) {
		document.getElementById('the_div1').className="showNotification";
  	}
  	if(document.getElementById('ajax')) {
   		document.getElementById("ajax").style.visibility="visible";
  	}
}

function hidePopup() {
	if(document.getElementById('the_div1')) {
    	document.getElementById('the_div1').className="nothere";
	}
	if(document.getElementById('ajax')) {
		document.getElementById("ajax").style.visibility="hidden";
	}
}

function redirectLocation() {
	window.location="MyDashboard.action";
}


</script>

<div id="the_div1" class="nothere">
   <div id="ajax" style="background-color: gray;border: 1px solid black;height: 100px;left: 35%;padding: 5px;position: absolute;top: 35%;visibility: hidden; width: 30%;">
   		<div style="background-color: gray;float:left;width:100%;height:100%;text-align:center;color:white;font-weight:bold">
   			<%=session.getAttribute("clock_inout_reminder") %>
   			<input type="button" onclick="redirectLocation()" value="OK" class="input_button">
   			<input type="button" onclick="hidePopup();" value="Cancel" class="input_button">	
   		</div>
   </div>
</div>

<%} %>

<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*, ChartDirector.*"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

 

<script type="text/javascript">
jQuery(document).ready(function() {
	jQuery("#frmClockEntries").validationEngine();
});
</script>





<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
Map hmDetails = (Map)request.getAttribute("hmDetails");
if(hmDetails==null)hmDetails=new HashMap();

String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
%>
 
    <div class="leftbox reportWidth">


		<div id="center" style="margin:0px; width:100%;">
			<div id="involmentcontainer" style="margin:0px; width:97%;min-height:500px">

			<div style="float:left;width:100%">
			<%=uF.showData((String)request.getAttribute(IConstants.MESSAGE), "") %>			
			</div>
			
			
			
			<p style="font-family:Digital;font-size:20px;padding:50px 0 20px 0;">
			Welcome To Time & Attendance Management System
			</p>
			
			<s:form id="frmClockEntries" name="frmClockEntries" theme="simple" action="CommonClock" cssStyle="float:left;width:100%">
			
			<div style="float:left;margin-right:5%;width:30%;">&nbsp;
			 <%if((String)hmDetails.get("EMP_NAME")!=null){ %>
				<div class="holder" style="width:100%;text-align: left;border:2px solid #CCCCCC">
                    <div class="profileborder">
                    	<%-- <img height="82" width="84" id="profilecontainerimg" src="userImages/<%=(String)hmDetails.get("EMP_CODE") %>"/> --%>
                    	<%if(docRetriveLocation==null) { %>
								<img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + ((String)hmDetails.get("EMP_IMAGE"))%>" />
						<%} else { %>
                            	<img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+((String)hmDetails.get("EMP_ID"))+"/"+IConstants.I_100x100+"/"+((String)hmDetails.get("EMP_IMAGE"))%>" />
                         <%} %> 
                    </div>
                    <div>
                    <p class="welcome" style="color:#fff;text-align: left;">Hi, <strong><%=uF.showData((String)hmDetails.get("EMP_NAME"),"")%> [<%=uF.showData((String)hmDetails.get("EMP_NAME"), "")%>]</strong></p>
                    <p class="monday" style="color:#fff;text-align: left;"> <%=uF.showData((String)hmDetails.get("EMP_NAME"),"")%></p>
                    <p class="monday" style="color:#fff;text-align: left;"><%=uF.showData((String)hmDetails.get("EMP_LOCATION"), "")%></p>
                    <%if((String)hmDetails.get("EMP_MANAGER")!=null){%>
                    <p class="monday" style="color:#fff;text-align: left;">You report to <strong><%=uF.showData((String)hmDetails.get("EMP_MANAGER"), "")%></strong> </p>
                    <%} %>
                    <div id="fixed" class="monday"></div> 
                    <div id="starPrimary" class="monday"></div>
                    <p class="reportingMessage" style="color:#fff;text-align: left;"><%=uF.showData((String)hmDetails.get("EMP_EMAIL"), "")%></p>
                    </div>
			</div>
			<%} %>
			</div> 
			
			
			<table align="center" style="float:left;">
				<tr>
				
				<%if(CF.getStrCommonAttendanceFormat()!=null && CF.getStrCommonAttendanceFormat().equalsIgnoreCase(IConstants.USER_NAME)){ %>
					<td>Enter Username</td>
				<%}else{ %>
					<td>Enter Employee ID</td>
				<%}%>
					
					<td><s:textfield name="strEmpCode" cssClass="validateRequired"></s:textfield></td>
				</tr>
			
			
			
			
			
			<%if((String)hmDetails.get("EMP_ID")!=null){ %>
			
				<tr>
					<td>Enter Password</td>
					<td>
						<s:hidden name="strEmpId"></s:hidden>
						<s:password name="strEmpPassword" cssClass="validateRequired"></s:password>
					</td>
				</tr>
				
				
				
				<%if((String)request.getAttribute("strMessage")!=null){ %>
				
				<tr>
					<td colspan="2">
					<%=(String)request.getAttribute("strMessage") %>
					</td>
				</tr>
				
				<tr>
					<td valign="top">Please Enter Reason</td>
					<td>
						<s:textarea name="strEmpReason" rows="5" cols="50" cssStyle="width:200px;" cssClass="validateRequired"></s:textarea>
					</td>
				</tr>
				
				
					
				<%} %>
				
			<%} %>
			
			
				<tr>
					<td colspan="2">
					
					<%if("0".equalsIgnoreCase((String)request.getAttribute("CLOCK"))){ %>
						<s:submit value ="Submit" name="smtBtn" cssClass="input_button"></s:submit>
					<%}else if("1".equalsIgnoreCase((String)request.getAttribute("CLOCK"))){ %>
						<s:submit value ="Clock On" name="smtBtn" cssClass="input_button"></s:submit>
					<%} else if("2".equalsIgnoreCase((String)request.getAttribute("CLOCK"))){ %>
						<s:submit value ="Clock Off" name="smtBtn" cssClass="input_button"></s:submit>
					<%}else{ %>
						<s:submit value ="Enter" name="smtBtn" cssClass="input_button"></s:submit>
					<%} %>
						
					</td>
				</tr>
				
			</table>
			
		<%-- 	</s:form>
			
			
			
			
			
			<s:form id="frmClockEntries" name="frmClockEntries" theme="simple" action="ClockOnOffEntry">
		 --%>		
				
			    <%-- <h2>
			     <s:url id="clockLabelUrl" action="GetClockLabel" /><sx:div onclick="return confirmMsg(this.innerHTML);" href="%{clockLabelUrl}"  listenTopics="showClockLabel" formId="frmClockEntries" showLoadingText=""></sx:div>
			    </h2> --%>
			
			<s:hidden name="strClock"></s:hidden>
			
			
			
			
			
			<div class="clr"></div>
			<div class="clockon_content" style="text-align: center; float: left; position: absolute; width: 96%;">
			       
			       
			      
			
			       
			       
			       
			            <div class="clockon_content" style="text-align: center;width:100%;position:absolute;">It is <%= request.getAttribute("CURRENT_DATE") %></div>
			           	 <br/>
			           	<%if((request.getAttribute("ROSTER_START_TIME")!=null)){ %>
			           	<div class="clockon_content" style="text-align: center;width:100%;position:absolute;">and your roster start time is <%= request.getAttribute("ROSTER_START_TIME") %></div>
			           	<%} %>
			           	
			           	<%if((request.getAttribute("ROSTER_END_TIME")!=null)){ %>
			           	<div class="clockon_content" style="text-align: center;width:100%;position:absolute;">and your roster start time is <%= request.getAttribute("ROSTER_END_TIME") %></div>
			           	<%} %>
			           	<br/><br/><br/>
			            <div class="clockon_content_time" style="text-align: center;width:100%;position:absolute;"><%-- <%= request.getAttribute("CURRENT_TIME") %> --%><div id="myTime" style="text-align:center;"></div> </div>
			            
				        <div class="clockon_content"><%= (request.getAttribute("ROSTER_TIME")!=null)?request.getAttribute("ROSTER_TIME"):"" %></div>
						<%-- <s:url id="clockMessageUrl" action="GetClockEntryMessage"  /> <sx:div href="%{clockMessageUrl}" listenTopics="showClockMessage" formId="frmClockEntries" showLoadingText=""></sx:div> --%>
			
			
			
			
						         
				        </div>
				   
			</s:form>
			
			</div>
            <div class="clr"></div>
		</div>

	 </div>  
		
	
<script>
function getTime(){
	getContent('myTime','GetServerTime.action');	
}

//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  

setInterval ( "getTime()", 1000 );

</script>
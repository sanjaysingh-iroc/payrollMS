<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags"	prefix="s" %>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>





<script type="text/javascript">
hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';

function show_employees() { 
	dojo.event.topic.publish("show_employees");
}
  
$(function() {
    $( "#selectDate" ).datepicker({dateFormat: 'dd/mm/yy'});
});

</script> 

<%
CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
%>

 
 

	   
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Exceptions" name="title"/>
</jsp:include>		
	
<div class="leftbox reportWidth">


	<div class="pagetitle" style="margin:0px;"></div>



		<s:form theme="simple" id="selectLevel" name="frm_roster_actual1" action="Exceptions"
			cssClass="formcss" enctype="multipart/form-data">

<div class="filter_div">
<div class="filter_caption">Filter</div>	   
	   
	   <input type="hidden" name="strMul" value="Y" />
	   
       
              
              <%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
              
	              <div style="float:left;margin-right:5px">        
	                <s:select theme="simple" label="Select Pay Cycle" name="paycycle" listKey="paycycleId"
	                            listValue="paycycleName" headerKey="0" 		
	                            list="payCycleList" key="" required="true" onchange="javascript:show_employees();return false;" />
	              </div>
	              
	              <div style="float:left;margin-right:5px">
	                <s:select theme="simple" name="level" listKey="levelId" headerValue="All Levels"
	                            listValue="levelCodeName" headerKey="0" onchange="javascript:show_employees();return false;"
	                            list="levelList" key="" required="true" />
	              </div>	
	              
               <%}else {%>
               
               		<div style="float:left;margin-right:5px">       
		                <s:select theme="simple" label="Select Pay Cycle" name="paycycle" listKey="paycycleId"
		                            listValue="paycycleName" headerKey="0" 		
		                            list="payCycleList" key="" required="true" onchange="document.frm_roster_actual1.submit();" />
	              	</div>
               
               <%}%>
	          
     	<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
			<s:url id="employees_url" action="GetEmployeeList" /> 
	        <div style="float:left;margin-right:5px">     
				<sx:div href="%{employees_url}" listenTopics="show_employees" formId="selectLevel" showLoadingText="true"></sx:div>
			</div>
			
			<div style="float:left">
				<s:submit theme="simple" value="Submit" name="submit" cssClass="input_button" cssStyle="margin:0px"></s:submit>
			</div>
			
        <%}%>
        
</div>       
       
	</s:form>




</div>
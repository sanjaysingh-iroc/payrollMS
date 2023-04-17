<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>



<%
List alDates = (List)request.getAttribute("alDates");
UtilityFunctions uF = new UtilityFunctions();

%>


<!-- Custom form for adding new records -->

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Extra Hours Worked Report" name="title"/>
</jsp:include>
   


    <div class="leftbox reportWidth">
    
    
    
    <s:form name="frm_ExtraWork" action="ExtraWork" theme="simple">
    
	<div class="filter_div">
    <div class="filter_caption">Filter</div>			
				
			<s:select theme="simple" name="strWLocation" listKey="wLocationId"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         onchange="document.frm_ExtraWork.submit();" 		
                         list="wLocationList" key=""  />
                    
             <s:select name="department" list="departmentList" listKey="deptId" 
             			listValue="deptName" headerKey="0" headerValue="All Departments" 
             			onchange="document.frm_ExtraWork.submit()"></s:select>
             			
			<s:select name="service" list="serviceList" listKey="serviceId" 
						listValue="serviceName" headerKey="0" headerValue="All Services"
						 onchange="document.frm_ExtraWork.submit()"></s:select>     
                      
            <s:select theme="simple" name="strMonth" listKey="monthId" cssStyle="width:101px;"
                         listValue="monthName" headerKey="0"
                         onchange="document.frm_ExtraWork.submit();" 		
                         list="monthList" key=""  />
                            	
			<s:select theme="simple" name="strYear" listKey="yearsID" cssStyle="width:65px;"
                         listValue="yearsName" headerKey="0"
                         onchange="document.frm_ExtraWork.submit();" 		
                         list="yearList" key=""  />
                         
	</div>	                      
	                            
	</s:form>
	
		
<display:table name="alReport" cellspacing="1" class="itis" export="true" style="width:100%"
pagesize="15" id="lt" requestURI="ExtraWork.action">
	
	<display:setProperty name="export.excel.filename" value="ExtraWorkReport.xls" />
	<display:setProperty name="export.xml.filename" value="ExtraWorkReport.xml" />
	<display:setProperty name="export.csv.filename" value="ExtraWorkReport.csv" />
	
	<display:column style="align:left" nowrap="nowrap" title="Employee Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
		<%
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 1+ii;
				String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
				%>
				<display:column style="text-align:right;padding-right:10px" title="<%=strDate %>" > <%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
				<%
			}  
			%>
		
</display:table>
	
    </div>
   
   
 




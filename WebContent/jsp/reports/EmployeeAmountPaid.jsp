<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script type="text/javascript" src="scripts/chart/jquery.min.js"></script>
<script type="text/javascript" src="scripts/chart/highcharts.js"></script>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 

	UtilityFunctions uF = new UtilityFunctions();
	Map hmAmountPaid = (Map) request.getAttribute("hmAmountPaid");
	Map hmAmountPaidC = (Map) request.getAttribute("hmAmountPaidC");
	Map hmEmpCodeName = (Map) request.getAttribute("hmEmpCodeName"); 
	
	  
	//out.println("<br/>hmAmountPaidC==>"+hmAmountPaidC);
	//out.println("<br/>hmAmountPaid==>"+hmAmountPaid);
	

	List alId = (List) request.getAttribute("alId");
 
	List alPayCycles = (List) request.getAttribute("alPayCycles");
	
	String strP = (String)request.getParameter("param");
	String strSubTitle = "";
	if(strP!=null && strP.equalsIgnoreCase("APWL")){
		strSubTitle="By Work Locations [all figures displayed in INR]";
	}else if(strP!=null && strP.equalsIgnoreCase("APD")){
		strSubTitle="By Department [all figures displayed in INR]";
	}else if(strP!=null && strP.equalsIgnoreCase("APUT")){
		strSubTitle="By Usertype [all figures displayed in INR]";
	}else if(strP!=null && strP.equalsIgnoreCase("APS")){
		strSubTitle="By Service [all figures displayed in INR]";
	}else if(strP!=null && strP.equalsIgnoreCase("APE")){
		strSubTitle="By Employee [all figures displayed in INR]";
	}
%>

 <script type="text/javascript">

var chart;

$(document).ready(function() {
	
	chart = new Highcharts.Chart({
   		
      chart: {
         renderTo: 'container_compensation',
        	defaultSeriesType: 'column'
      },
      title: {
         text: 'Amount Paid'
      },
      xAxis: {
         categories: [<%=(String)request.getAttribute("sbPC")%>],
         labels: {
             rotation: -45,
             align: 'right',
             style: {
                 font: 'normal 10px Verdana, sans-serif'
             }
          },
         title: {
	            text: 'Pay Cycles'
	         }
      },
      credits: {
       	enabled: false
   	  },
      yAxis: {
         min: 0,
         title: {
            text: 'Amount'
         }
      },
      plotOptions: {
         column: {
            pointPadding: 0.2,
            borderWidth: 0
         }
      },
     series: [<%=request.getAttribute("sbHours")%>]
   });
	
	
});
</script> 

 <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Amount Paid" name="title"/>
</jsp:include>



<div id="printDiv" class="leftbox reportWidth">

<span style="font-weight:bold">[<%=strSubTitle%>]</span>


<s:form name="frmAmountPaidEmployee" action="AmountPaidEmployee" theme="simple" cssStyle="float:left;width:100%">

<div class="filter_div">
<div class="filter_caption">Filter</div>

    <s:select theme="simple" name="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         list="wLocationList" key=""  />
                    
    <s:select name="f_department" list="departmentList" listKey="deptId"  cssStyle="float:left;margin-right: 10px;"
    			listValue="deptName" headerKey="0" headerValue="All Departments" 
    			></s:select>
    			
    <s:select theme="simple" name="f_level" listKey="levelId" headerValue="All Levels"  cssStyle="float:left;margin-right: 10px;"
	                            listValue="levelCodeName" headerKey="0" 
	                            list="levelList" key="" required="true" />
     
       
    
    <s:submit value="Submit" cssClass="input_button"  cssStyle="margin:0px"/>
    
    
    
    <div style="padding-top:10px">
	    <s:radio name="param" list="#{'APWL':'By Location','APS':'By Service','APD':'By Department','APUT':'By UserType','APE':'By Employee'}" />
    </div>

    </div>
</s:form>

<div class="scroll" style="float:left;">
<%-- <table cellpadding="0" cellspacing="1">
	<tr>

		<td nowrap="nowrap">&nbsp;</td>

		<%
			for (int i = alPayCycles.size()-1; i >=0 ; i--) {
		%>

		<td class="reportHeading" nowrap="nowrap"><%=(String) alPayCycles.get(i)%></td>

		<% 
			}
		%>

	</tr>

	<%
		for (int j = 0; j < alId.size(); j++) {
			String strCol = ((j%2==0)?"dark":"light");
			Employee objEmp = (Employee) alId.get(j);
			String strEmpId = (String) objEmp.getStrEmpId();
			String strEmpName = (String) objEmp.getStrName();
			
			//Map hmAmountPaidInner = (Map) hmAmountPaid.get(strEmpId);
			
			
	%>
	<tr class="<%=strCol%>" title="<%=strEmpName%>">


		<td nowrap="nowrap" class="reportHeading alignLeft"><%=strEmpName%></td>

		<%
				for (int i = alPayCycles.size()-1; i >=0 ; i--) {
				//uF.showData((String) hmAmountPaidInner.get(i + "L"), IConstants.CURRENCY_SHORT+" "+0)
		%>

		<td class="alignRight" nowrap="nowrap"><%=uF.showData((String) hmAmountPaidC.get(strEmpId+"PC"+i), ""+0)%></td>

		<%
			}
		%>

		<%
			}
		%>

	</tr>

</table>

 --%>

		
<display:table name="alReport" cellspacing="1" class="itis" export="true" 
	pagesize="50" id="lt1" requestURI="AmountPaidEmployee.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="AmountPaid.xls" />
	<display:setProperty name="export.xml.filename" value="AmountPaid.xml" />
	<display:setProperty name="export.csv.filename" value="AmountPaid.csv" />
	
	<display:column style="text-align:left" nowrap="nowrap" title="Employee Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
		<%
			for (int ii=alPayCycles.size()-1; ii>=0; ii--){
				int count = 1+ii;
				%>
				<display:column  style="width:120px;text-align:center" nowrap="nowrap" title="<%=(String)alPayCycles.get(ii)%>" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(count)%></display:column>
				<%
			}  
			%>

</display:table>


</div>

	<%if(strP!=null && (strP.equalsIgnoreCase("APWL") || strP.equalsIgnoreCase("APUT") || strP.equalsIgnoreCase("APS") || strP.equalsIgnoreCase("APD"))){ %>
	
		<div class="chartholder">
		<div style="float: right; text-decoration: underline;">Displaying only last 6 paycycles</div>
		<div id="container_compensation" style="height: 300px; width:95%; margin-top:20px; float:left"></div>
		</div>
	<%} %>
	

</div>




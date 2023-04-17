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
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
<%-- <jsp:include page="/jsp/common/Links.jsp" flush="true" /> --%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %> 
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<%

String validReqOpt = "";
String validAsterix = "";
%>
<script type="text/javascript">

function addDocuments(documentcnt) {
		documentcnt++;
        var table = document.getElementById('row_document_table');
		var rowCount = table.rows.length;
       	var row = table.insertRow(rowCount);
        row.id = "row_document"+rowCount;
        var cell1 = row.insertCell(0);
        cell1.setAttribute("class", "txtlabel alignRight");
        cell1.setAttribute("style", "text-align: -moz-center");
		cell1.innerHTML = "<input type=\"file\" style=\"float:right;margin-right:-275px;\" name=\"idDoc\" onchange=\"fillFileStatus('idDoc"+documentcnt+"Status')\" /><input type=\"hidden\" name=\"idDocStatus\" id=\"idDoc"+documentcnt+"Status\"  value=\"0\"></input>";
      	var cell2 = row.insertCell(1);
}
</script>

<% String sbAllData = (String)request.getAttribute("sbAllData");%>
<%if(sbAllData != null){%>
<%System.out.println("sbAllData==>"+sbAllData); %>
<%=sbAllData %>
<%} %>

<s:form theme="simple" action="AddCandidateBackgroundDetails" name="AddCandidateBackgroundDetails" id="frmAddCandidateBackgroundDetails" method="POST" cssClass="formcss" enctype="multipart/form-data">
			<s:hidden name="fromPage" id="fromPage"/>
			<s:hidden name="recruitId" id="recruitId" />
			<s:hidden name="CandidateId" />
			<s:hidden name="applyType" />
  <div>
      <table border="0" class="table table_no_border form-table" id = "row_document_table">
          <tr>
             <td class="tdLabelheadingBg alignCenter" colspan="2"><span style="color:#68AC3B; font-size:18px; padding:5px;font-weight: 600;">
                   </span><span style="font-weight: 600;font-size: 16px;"> Enter Candidate Background Verification Details</span>
              </td>
          </tr>
          <tr>
			<% 
					validReqOpt = "validateRequired";
					validAsterix = "<sup>*</sup>";
				
			%>	
			<td class="txtlabel alignRight" style= "float:left;"><%=IConstants.DOCUMENT_RESUME %>:<%=validAsterix %>
				<input type="hidden" name="idDocType0" value="<%=IConstants.DOCUMENT_RESUME %>"></input>
				 <input type="hidden" style="" value="<%=IConstants.DOCUMENT_RESUME %>" name="idDocName0" ></input>
				 <input type="hidden" name="idDocStatus0" id="idDoc1Status" value="0"></input>
			</td>
			<td class="txtlabel alignRight">
				<input type="file" name="idDoc0" id="idDoc0" accept=".jpg,.png,.svg,.svgz,.doc,.docs,.docx,.pdf" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc1Status')"/>
			</td>
         </tr>
         <tr>
			<% validReqOpt = "validateRequired";
				validAsterix = "<sup>*</sup>";
					
			%>
			<td class="txtlabel alignRight" style="float:left;"><%=IConstants.DOCUMENT_ADDRESS_PROOF%>:<%=validAsterix %>
			<input type="hidden" name="idDocType2" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>"></input>
			<input type="hidden" value="<%=IConstants.DOCUMENT_ADDRESS_PROOF%>" style="" name="idDocName2" ></input>
			<input type="hidden" name="idDocStatus2" id="idDoc3Status" value="0"></input>
			</td>
			<td class="txtlabel alignRight"><input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="idDoc2" name="idDoc2" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc3Status')"/></td>
		</tr>
		
		<tr>
			<%
				validReqOpt = "validateRequired";
				validAsterix = "<sup>*</sup>";
				
			%>
			<td class="txtlabel alignRight" style ="float:left";><%=IConstants.DOCUMENT_ID_PROOF%>:<%=validAsterix %>
			  	<input type="hidden" name="idDocType1" value="<%=IConstants.DOCUMENT_ID_PROOF%>"></input>
				<input type="hidden" value="<%=IConstants.DOCUMENT_ID_PROOF%>" style="" name="idDocName1" ></input>
				<input type="hidden" name="idDocStatus1" id="idDoc2Status" value="0"></input>
			</td>
			<td class="txtlabel alignRight"><input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="idDoc1" id="idDoc1" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc2Status')"/></td>
	    </tr>
	    <tr>
			<%
				validReqOpt = "validateRequired";
				validAsterix = "<sup>*</sup>";
				
			%>
			<td class="txtlabel alignRight" style ="float:left";>Educational Document:<%=validAsterix %>
			  	<input type="hidden" name="idDocType1" value="Educational Document:"></input>
				<input type="hidden" value="Educational Document:" style="" name="idDocName4" ></input>
				<input type="hidden" name="idDocStatus4" id="idDoc4Status" value="0"></input>
			</td>
			<td class="txtlabel alignRight"><input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="idDoc1" id="idDoc1" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc2Status')"/></td>
	    </tr>
	     <tr>
			<%
				validReqOpt = "validateRequired";
				validAsterix = "<sup>*</sup>";
			%>
			<td class="txtlabel alignRight" style ="float:left";>Passport<%=validAsterix %>
			  	<input type="hidden" name="idDocType1" value="Passport:"></input>
				<input type="hidden" value="Passport Document:" style="" name="idDocName5" ></input>
				<input type="hidden" name="idDocStatus5" id="idDoc5Status" value="0"></input>
			</td>
			<td class="txtlabel alignRight"><input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="idDoc1" id="idDoc1" class="<%=validReqOpt %>" onchange="fillFileStatus('idDoc2Status')"/></td>
	    </tr>
	   
		 </table>
		 <div style="float:right;margin-right:200px;">
			<a href="javascript:void(0)" onclick="addDocuments(3);"><i class="fa fa-plus-circle"></i>Add More Documents...</a>&nbsp;&nbsp;
		</div>		
		<s:submit cssClass="btn btn-primary" name="stepSubmit" cssStyle="float: right;" value="Submit"/>
   			
  </div>
</s:form>

</html>
<%@page import="com.konnect.jpms.training.FillCertificate"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript">
$('#CreateCertificate').dialog('close');
</script>
<%List<FillCertificate> certificateList=(List<FillCertificate>) request.getAttribute("certificateList"); %>
<select id="strCertificateId" name="strCertificateId" onchange="createNewCertificate();">
	<option value="-1">Select Certificate</option>
	<%for(int k=0; certificateList!=null && k< certificateList.size(); k++) { %>
		  <option value="<%=((FillCertificate)certificateList.get(k)).getId() %>">
				<%=((FillCertificate)certificateList.get(k)).getName() %>
		 </option>
		<%}%>
		<option value="0">Create New Certificate</option>
		</select>
		 <span><a href="javascript:void(0)" onclick="previewcertificate();">Preview</a> </span>
		 
		 

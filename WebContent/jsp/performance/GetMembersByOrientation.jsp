
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<% 
	UtilityFunctions uF = new UtilityFunctions();
	String operation = (String) request.getParameter("operation");
	String appraisalId = (String) request.getParameter("appraisalId");
	String orientationId = (String) request.getParameter("orientation_id");
	List<String> appraisalData = (List<String>)request.getAttribute("appraisalData");
	if(appraisalData == null) appraisalData = new ArrayList<String>(); 
	
	String members = (String)request.getAttribute("members");
	String[] memberArray=members.split(",");
	String displayMgr = "none";
	String displayHr = "none";
	String displayPeer = "none";
	String displayCeo = "none";
	String displayHod = "none";
	String displayOthers = "none";
	
	for(int i=0;i<memberArray.length;i++){
		if(memberArray[i].equalsIgnoreCase("HR")) {
			displayHr = "block";
		} 
		
		if(memberArray[i].equalsIgnoreCase("Manager")) {
			displayMgr = "block";
		} 
		
		if(memberArray[i].equalsIgnoreCase("Peer")) {
			displayPeer = "block";
		}
		
		if(memberArray[i].equalsIgnoreCase("CEO")) {
			displayCeo = "block";
		}
		
		if(memberArray[i].equalsIgnoreCase("HOD")) {
			displayHod = "block";
		}
		
		if(memberArray[i].equalsIgnoreCase("Anyone")) {
			displayOthers = "block";
		}
	}
	
%>
	<%if(operation != null && !operation.equals("") && operation.equals("E"))  { 
		if(appraisalData != null && appraisalData.size() >0 && !appraisalData.isEmpty()) {
	%>
			  <input type="hidden" name="operation" id="operation" value ="<%=operation%>"/>
			   <input type="hidden" name="appraisalId" id="appraisalId" value ="<%=appraisalId%>"/>
			  <input type="hidden" name="hidehrIdEdit" id="hidehrIdEdit" value="<%=appraisalData.get(8) %>"/>
			 <input type="hidden" name="hidemanagerIdEdit" id="hidemanagerIdEdit" value="<%=appraisalData.get(9) %>"/>
			 <input type="hidden" name="hidepeerIdEdit" id="hidepeerIdEdit" value="<%=appraisalData.get(10) %>"/>
			 <input type="hidden" name="hideotherIdEdit" id="hideotherIdEdit" value="<%=appraisalData.get(11) %>"/>
			 <input type="hidden" name="hideCeoIdEdit" id="hideCeoIdEdit" value="<%=appraisalData.get(14) %>"/>
			 <input type="hidden" name="hideHodIdEdit" id="hideHodIdEdit" value="<%=appraisalData.get(15) %>"/>
			 <div id="hrDivEdit" style="display: <%=displayHr %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hidehrIdEdit','lblHridEdit','HR','<%=appraisalId%>');">Choose HR</a>:&nbsp;<label id="lblHridEdit"><%=uF.showData(appraisalData.get(4), "Not Choosen") %></label></div>
			 <div id="managerDivEdit" style="display: <%=displayMgr %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hidemanagerIdEdit','lblManageridEdit','Manager','<%=appraisalId%>');">Choose Manager</a>:&nbsp;<label id="lblManageridEdit"><%=uF.showData(appraisalData.get(3), "Not Choosen") %></label></div>
			 <div id="peerDivEdit" style="display: <%=displayPeer %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hidepeerIdEdit','lblPeeridEdit','Peer','<%=appraisalId%>');">Choose Peer</a>:&nbsp;<label id="lblPeeridEdit"><%=uF.showData(appraisalData.get(5), "Not Choosen") %></label></div>
			 <div id="otherDivEdit" style="display:<%=displayOthers %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hideotherIdEdit','lblOtheridEdit','Other','<%=appraisalId%>');">Choose Anyone</a>:&nbsp;<label id="lblOtheridEdit"><%=uF.showData(appraisalData.get(6), "Not Choosen") %></label>
				 	 &nbsp;&nbsp;&nbsp;&nbsp;<label id="lblSelfFillEmpIds">&nbsp;</label> <input type="hidden" name="hideSelfFillEmpIds" id="hideSelfFillEmpIds" value="<%=appraisalData.get(16) %>" />
			 </div>
			 
			 <div id="ceoDivEdit" style="display: <%=displayCeo %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hideCeoIdEdit','lblCeoIdEdit','CEO','<%=appraisalId%>');">Choose CEO</a>:&nbsp;<label id="lblCeoIdEdit"><%=uF.showData(appraisalData.get(12), "Not Choosen") %></label></div>
			 <div id="hodDivEdit" style="display: <%=displayHod %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hideCeoIdEdit','lblHodIdEdit','HOD','<%=appraisalId%>');">Choose HOD</a>:&nbsp;<label id="lblHodIdEdit"><%=uF.showData(appraisalData.get(13), "Not Choosen") %></label></div>
		 <%} %>
	<% } else { %>
		 <input type="hidden" name="hidehrId" id="hidehrId"/>
		 <input type="hidden" name="hidemanagerId" id="hidemanagerId"/>
		 <input type="hidden" name="hidepeerId" id="hidepeerId"/>
		 <input type="hidden" name="hideotherId" id="hideotherId"/>
		 <input type="hidden" name="hideCeoId" id="hideCeoId"/>
		 <input type="hidden" name="hideHodId" id="hideHodId"/>
		 <div id="hrDiv" style="display: <%=displayHr %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopup('hidehrId','lblHrid','HR');">Choose HR</a>:&nbsp;<label id="lblHrid">Not Choosen</label></div>
		 <div id="managerDiv" style="display: <%=displayMgr %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopup('hidemanagerId','lblManagerid','Manager');">Choose Manager</a>:&nbsp;<label id="lblManagerid">Not Choosen</label></div>
		 <div id="peerDiv" style="display: <%=displayPeer %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopup('hidepeerId','lblPeerid','Peer');">Choose Peer</a>:&nbsp;<label id="lblPeerid">Not Choosen</label></div>
		 <div id="otherDiv" style="display:<%=displayOthers %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopup('hideotherId','lblOtherid','Other');">Choose Anyone</a>:&nbsp;<label id="lblOtherid">Not Choosen</label>
			 	 &nbsp;&nbsp;&nbsp;&nbsp;<label id="lblSelfFillEmpIds">&nbsp;</label> <input type="hidden" name="hideSelfFillEmpIds" id="hideSelfFillEmpIds" />
		 </div>
		 
		 <div id="ceoDiv" style="display: <%=displayCeo %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopup('hideCeoId','lblCeoId','CEO');">Choose CEO</a>:&nbsp;<label id="lblCeoId">Not Choosen</label></div>
		 <div id="hodDiv" style="display: <%=displayHod %>; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopup('hideHodId','lblHodId','HOD');">Choose HOD</a>:&nbsp;<label id="lblHodId">Not Choosen</label></div>
			  
	  <% } %>
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
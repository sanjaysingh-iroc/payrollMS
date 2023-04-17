<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">

	function editPolicy(levelID, type) {
		if(type == 'U') {
			document.getElementById("policyName_"+levelID).style.display='none';
			document.getElementById("policyCombo_"+levelID).style.display='inline';
		} else {
			document.getElementById("policyName_"+levelID).style.display='inline';
			document.getElementById("policyCombo_"+levelID).style.display='none';
		}
	}
	
	function updatePolicy(levelID) {
		var policyId = document.getElementById("policy_"+levelID).value;
		var strLocation = document.getElementById("strLocation").value;
		document.getElementById("policyName_"+levelID).style.display='inline';
		document.getElementById("policyCombo_"+levelID).style.display='none';
		getContent('policyName_'+levelID, 'UpdateTimesheetWorkflowPolicy.action?levelId='+levelID+'&policyId='+policyId+'&strLocation='+strLocation);
	}
	
	function submitForm(type){
		if(type == '1'){
			document.getElementById("strLocation").selectedIndex = "0";
		}
		document.frm.submit();
	}

</script>


<% 
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	
	Map<String, String> hmOrgName = (Map<String, String>)request.getAttribute("hmOrgName");
	
	Map<String, List<List<String>>> hmOrgLevels = (Map<String, List<List<String>>>) request.getAttribute("hmOrgLevels");
	if(hmOrgLevels == null) hmOrgLevels = new HashMap<String, List<List<String>>>();
%>

 
	<div class="box-body">

		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frm" action="MyDashboard" theme="simple">
					<s:hidden name="userscreen" />
					<s:hidden name="navigationId" />
					<s:hidden name="toPage" />
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
								<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm('1')"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Location</p>
								<s:select theme="simple" name="strLocation" id="strLocation" headerKey="" headerValue="Select Location" listKey="wLocationId" listValue="wLocationName" list="workList" onchange="submitForm('2')"/>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		

	<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
	<%session.setAttribute("MESSAGE", ""); %>

	<!-- <div style="float: left; width: 100%; margin-left: 15px; margin-top: 15px; color: #346897; font-size: 16px; font-weight: bolder; text-shadow: 0 1px 2px #FFFFFF;">Timesheet Setting</div> -->
<%if(uF.parseToInt((String)request.getParameter("strLocation"))>0){ %>           
	<div style=";float:left; width:100%">
         <ul class="level_list">
		<% 
			Set<String> set = hmOrgLevels.keySet();
			Iterator<String> it = set.iterator();
			
			while(it.hasNext()) {
				String strOrgId = (String)it.next();
				String StrOrgName = hmOrgName.get(strOrgId);
			%>
			<li> 
			<strong><%=StrOrgName %> </strong>
			<ul style="width: 100%; float: left;">
			<%		
				List<List<String>> levelList = (List<List<String>>)hmOrgLevels.get(strOrgId);
					if(levelList!= null && !levelList.isEmpty()) {
						for(int i=0; i<levelList.size(); i++) {
							List<String> innerList = levelList.get(i);
					%>
					
					<li style="float: left; border-bottom: 1px solid #CCCCCC; width: 95%;">
					<div style="width: 100%; float: left;"> 
					<a href="javascript:void(0);" class="fa fa-edit" style="float: left;" onclick="editPolicy('<%=innerList.get(0)%>', 'U')">&nbsp;</a>
						<div style="float: left; width: 30%;"> Level: <strong><%=innerList.get(2)%> [<%=innerList.get(1)%>]</strong></div> 
						<div style="float: left;">Workflow Policy: 
							<span id="policyName_<%=innerList.get(0)%>" style="font-weight: bold;"><%=innerList.get(4)%></span>
							<span style="display: none;" id="policyCombo_<%=innerList.get(0)%>" >
								<select name="policy_<%=innerList.get(0)%>" id="policy_<%=innerList.get(0)%>" style="margin-bottom: 5px;">
									<%=innerList.get(5)%> 
								</select>
								&nbsp;<input type="button" class="btn btn-primary" name="update_<%=innerList.get(0)%>" id="update_<%=innerList.get(0)%>" value="Save" onclick="updatePolicy('<%=innerList.get(0)%>');">
								&nbsp;<input type="button" class="btn btn-danger" name="cancel_<%=innerList.get(0)%>" id="cancel_<%=innerList.get(0)%>" value="Cancel" onclick="editPolicy('<%=innerList.get(0)%>', 'C');">
							</span>
						</div>
					</div>
					</li>
			<% }
					}
			%>
		</ul>
		
		</li>	
			<% } %>
		</ul>
	</div>
   <% } else { %>
		<div class="filter">
			<div class="msg nodata"><span>No Data available</span></div>
		</div>
	<% } %>
          
</div>


<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<style>
.level_list li{
padding-top: 5px;
padding-bottom: 5px;
border-bottom: 1px solid #ECECEC !important;
}
</style>
<script type="text/javascript">

function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
       return false;
    }
    return true;
 }

	function editPolicy(levelID,pType, type) {
		if(type == 'U') {
			document.getElementById("policyName_"+levelID+"_"+pType).style.display='none';
			document.getElementById("policyCombo_"+levelID+"_"+pType).style.display='inline';
		} else {
			document.getElementById("policyName_"+levelID+"_"+pType).style.display='inline';
			document.getElementById("policyCombo_"+levelID+"_"+pType).style.display='none';
		}
	}
	
	function updatePolicy(levelID,pType) {
		var policyId = document.getElementById("policy_"+levelID+"_"+pType).value;
		var strLocation = document.getElementById("strLocation").value;
		document.getElementById("policyName_"+levelID+"_"+pType).style.display='inline';
		document.getElementById("policyCombo_"+levelID+"_"+pType).style.display='none'; 
		getContent('policyName_'+levelID+'_'+pType, 'UpdateAssignWorkflow.action?levelId='+levelID+'&policyId='+policyId+'&pType='+pType+'&strLocation='+strLocation);
	}
	function submitForm(type){
		if(type == '1'){
			document.getElementById("strLocation").selectedIndex = "0";
		}
		document.frm.submit();
	}
	
	function editLeavePolicy(levelID,pType, type) {
		var period = document.getElementById("strPeriod_"+levelID+"_"+pType);
		if(type == 'U') {
			document.getElementById("leavePolicyName_"+levelID+"_"+pType).style.display='none';
			document.getElementById("leavePolicyCombo_"+levelID+"_"+pType).style.display='inline';
			if(period.checked == true){
				document.getElementById("leavePeriodCombo_"+levelID+"_"+pType).style.display='block';
			} else {
				document.getElementById("leavePeriodCombo_"+levelID+"_"+pType).style.display='none';
			}
			document.getElementById("leaveIsPeriodCombo_"+levelID+"_"+pType).style.display='none';
		} else {
			document.getElementById("leavePolicyName_"+levelID+"_"+pType).style.display='inline';
			document.getElementById("leavePolicyCombo_"+levelID+"_"+pType).style.display='none';
			document.getElementById("leavePeriodCombo_"+levelID+"_"+pType).style.display='none';
			var period1 = document.getElementById("isPeriod_"+levelID+"_"+pType).value;
			if(period1 == 'Yes'){
				document.getElementById("leaveIsPeriodCombo_"+levelID+"_"+pType).style.display='block';
			} else {
				document.getElementById("leaveIsPeriodCombo_"+levelID+"_"+pType).style.display='none';
			}
		}
	}
	
	function checkPeriod(levelID,pType){
		var period = document.getElementById("strPeriod_"+levelID+"_"+pType);
		if(period.checked == true){
			document.getElementById("leavePeriodCombo_"+levelID+"_"+pType).style.display='block';
		} else {
			document.getElementById("leavePeriodCombo_"+levelID+"_"+pType).style.display='none';
		}
	}
	
function updateLeavePolicy(levelID,pType){
		//leavePolicy_ strPeriod_ strMin1_ strMax1_ leavePeriodPolicy1_ 
		var strOrg=document.getElementById("strOrg").value;
		var strLocation = document.getElementById("strLocation").value;
		var leavePolicy=document.getElementById("leavePolicy_"+levelID+"_"+pType).options[document.getElementById("leavePolicy_"+levelID+"_"+pType).selectedIndex].value;
		var period = document.getElementById("strPeriod_"+levelID+"_"+pType);
		var strPeriod = 'false';
		var strMin1 = '';
		var strMax1 = '';
		var leavePeriodPolicy1='';
		var strMin2 = '';
		var strMax2 = '';
		var leavePeriodPolicy2='';
		var strMin3 = '';
		var strMax3 = '';
		var leavePeriodPolicy3='';
		var strMin4 = '';
		var strMax4 = '';
		var leavePeriodPolicy4='';
		
		if(period.checked == true){
			strPeriod = 'true';
		
			strMin1 = document.getElementById("strMin1_"+levelID+"_"+pType).value;
			strMax1 = document.getElementById("strMax1_"+levelID+"_"+pType).value;
			leavePeriodPolicy1=document.getElementById("leavePeriodPolicy1_"+levelID+"_"+pType).options[document.getElementById("leavePeriodPolicy1_"+levelID+"_"+pType).selectedIndex].value;
			strMin2 = document.getElementById("strMin2_"+levelID+"_"+pType).value;
			strMax2 = document.getElementById("strMax2_"+levelID+"_"+pType).value;
			leavePeriodPolicy2=document.getElementById("leavePeriodPolicy2_"+levelID+"_"+pType).options[document.getElementById("leavePeriodPolicy2_"+levelID+"_"+pType).selectedIndex].value;
			strMin3 = document.getElementById("strMin3_"+levelID+"_"+pType).value;
			strMax3 = document.getElementById("strMax3_"+levelID+"_"+pType).value;
			leavePeriodPolicy3=document.getElementById("leavePeriodPolicy3_"+levelID+"_"+pType).options[document.getElementById("leavePeriodPolicy3_"+levelID+"_"+pType).selectedIndex].value;
			strMin4 = document.getElementById("strMin4_"+levelID+"_"+pType).value;
			strMax4 = document.getElementById("strMax4_"+levelID+"_"+pType).value;
			leavePeriodPolicy4=document.getElementById("leavePeriodPolicy4_"+levelID+"_"+pType).options[document.getElementById("leavePeriodPolicy4_"+levelID+"_"+pType).selectedIndex].value;
		}
		var action = 'UpdateAssignWorkflow.action?type=L&strOrg='+strOrg+'&strLocation='+strLocation+'&levelId='+levelID+'&leaveTypeId='+pType;
		action += '&leavePolicy='+leavePolicy+'&strPeriod='+strPeriod;
		action += '&strMin1='+strMin1+'&strMax1='+strMax1+'&leavePeriodPolicy1='+leavePeriodPolicy1;
		action += '&strMin2='+strMin2+'&strMax2='+strMax2+'&leavePeriodPolicy2='+leavePeriodPolicy2;
		action += '&strMin3='+strMin3+'&strMax3='+strMax3+'&leavePeriodPolicy3='+leavePeriodPolicy3;
		action += '&strMin4='+strMin4+'&strMax4='+strMax4+'&leavePeriodPolicy4='+leavePeriodPolicy4;
		
		//alert(action);
		
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : action,
				cache : false,
				success : function(data) {
					if(data == "") {
                 		
                 	} else {
                 		var allData = data.split("::::");
                        document.getElementById("leavePolicyName_"+levelID+"_"+pType).innerHTML = allData[0];
                        document.getElementById("leavePolicyName_"+levelID+"_"+pType).style.display='inline';
            			document.getElementById("leavePolicyCombo_"+levelID+"_"+pType).style.display='none';
            			document.getElementById("leavePeriodCombo_"+levelID+"_"+pType).style.display='none';
            			if(period.checked == true){
                        	document.getElementById("leaveIsPeriodCombo_"+levelID+"_"+pType).style.display='block';
                        	document.getElementById("leaveIsPeriodCombo_"+levelID+"_"+pType).innerHTML = allData[1];
                        } else {
                        	document.getElementById("leaveIsPeriodCombo_"+levelID+"_"+pType).style.display='none';
                        }
                        
                 	} //leavePolicyName_ leaveIsPeriodCombo_
				}
			});
		}
	}
	 function GetXmlHttpObject() {
			if (window.XMLHttpRequest) {
				// code for IE7+, Firefox, Chrome, Opera, Safari
				return new XMLHttpRequest();
			}
			if (window.ActiveXObject) {
				// code for IE6, IE5
				return new ActiveXObject("Microsoft.XMLHTTP");
			}
			return null;
		}

</script>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	
	Map<String, String> hmOrgName = (Map<String, String>)request.getAttribute("hmOrgName");
	
	Map<String, List<List<String>>> hmOrgLevels = (Map<String, List<List<String>>>) request.getAttribute("hmOrgLevels");
	if(hmOrgLevels == null) hmOrgLevels = new HashMap<String, List<List<String>>>();
	
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	List<String> alType = (List<String>)request.getAttribute("alType");
	if(alType == null) alType = new ArrayList<String>();
	
	Map<String, List<List<String>>> hmLevelPolicy = (Map<String, List<List<String>>>) request.getAttribute("hmLevelPolicy");
	if(hmLevelPolicy == null) hmLevelPolicy = new HashMap<String, List<List<String>>>();
	
	Map<String, String> hmType = (Map<String, String>)request.getAttribute("hmType");
	if(hmType == null) hmType = new HashMap<String, String>();
	Map<String, List<Map<String,String>>> hmLeaveLevels = (Map<String, List<Map<String,String>>>)request.getAttribute("hmLeaveLevels");
	if(hmLeaveLevels == null) hmLeaveLevels = new HashMap<String, List<Map<String,String>>>();
	
	Map<String, List<Map<String,String>>> hmLeavePeriodLevels = (Map<String, List<Map<String,String>>>)request.getAttribute("hmLeavePeriodLevels");
	if(hmLeavePeriodLevels == null) hmLeavePeriodLevels = new HashMap<String, List<Map<String,String>>>();
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
%>

<div class="box-body">

<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
<% session.setAttribute(IConstants.MESSAGE, ""); %>

	<div class="box box-default collapsed-box">
		<div class="box-header with-border">
		    <h3 class="box-title"> <%=(String)request.getAttribute("selectedFilter") %></h3>
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
				<div style="float: left; width: 99%; margin-left: 10px;">
					<div style="float: left; margin-right: 5px;">
						<i class="fa fa-filter"></i>
					</div>
					<div style="float: left; width: 75%;">
						<div style="float: left; margin-left: 10px;">
							<p style="padding-left: 5px;">Organisation</p>
							<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm('1')"></s:select>
						</div>
						<div style="float: left; margin-left: 10px;">
							<p style="padding-left: 5px;">Location</p>
							<s:select theme="simple" name="strLocation" id="strLocation" listKey="wLocationId" listValue="wLocationName" list="workList" headerKey="" headerValue="Select Location" onchange="submitForm('2')"/>
						</div>
					</div>
				</div>
			</s:form>
		</div>
	</div>
	
<%-- <div class="filter_div">
<div class="filter_caption">Select</div>
<s:form name="frm" action="AssignWorkFlow" theme="simple">
	<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm('1')"></s:select>
	<s:select theme="simple" name="strLocation" id="strLocation" headerKey="" headerValue="Select Location" listKey="wLocationId" listValue="wLocationName" list="workList" onchange="submitForm('2')"/>
</s:form>
</div> --%>	


<div style="float:right; margin:10px 0px 0px 0px; text-align: left; width: 100%;"><a href="MyDashboard.action?strOrg=<%=request.getAttribute("strOrg") %>&strLocation=<%=request.getAttribute("strLocation") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>&toTab=WFP">Workflow Policy</a></div>
<%if(uF.parseToInt((String)request.getAttribute("strLocation"))>0){ %>
	<div style="float:left; width:100%;">
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
						Level: <strong><%=innerList.get(2)%> [<%=innerList.get(1)%>]</strong>
						<ul>
							<li style="float: left; border-bottom: 1px solid #CCCCCC; width: 95%;">
								<div style="width: 100%; float: left;"> 
									<div style="float: left; width: 30%;"><strong>Leave:</strong></div> 
									<div style="float: left;">&nbsp;</div>
								</div>
							</li>
							<%
							List<Map<String,String>> alLevels = hmLeaveLevels.get(innerList.get(0));
							if(alLevels == null) alLevels = new ArrayList<Map<String,String>>();
							for(int j=0; j<alLevels.size(); j++) {
								Map<String,String> hmInner = alLevels.get(j);
							%>
								<li style="float: left; border-bottom: 1px solid #CCCCCC; width: 95%;">
									<div style="width: 100%; float: left; margin-left: 31px;"> 
										<a href="javascript:void(0);" class="fa fa-edit" onclick="editLeavePolicy('<%=hmInner.get("LEVEL_ID")%>','<%=hmInner.get("LEAVE_TYPE_ID")%>', 'U')" style="float: left;"></a>
										<div style="float: left; width: 30%;"><strong><%=uF.showData(hmInner.get("LEAVE_TYPE_NAME"),"") %></strong></div> 
										<div style="float: left;">Workflow Policy:
											<%
												String strIsPeriod="No";
												String strIsPeriodDisplay = "none";
												if(uF.parseToBoolean(hmInner.get("IS_PERIOD"))){
													strIsPeriod = "Yes"; 
													strIsPeriodDisplay = "block";
												} 
											%>
											<span id="leavePolicyName_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>">
												<strong><%=uF.showData(hmInner.get("POLICY_NAME"),"N/A") %></strong>&nbsp;
												Is Period: <strong><%=strIsPeriod %></strong>
												<input type="hidden" name="isPeriod_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="isPeriod_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" value="<%=strIsPeriod %>"/>
											</span> 
											<span style="display: <%=strIsPeriodDisplay %>;" id="leaveIsPeriodCombo_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>">
												<%
												List<Map<String,String>> alLevelPeriods = hmLeavePeriodLevels.get(hmInner.get("LEVEL_ID")+"_"+hmInner.get("LEAVE_TYPE_ID"));
												if(alLevelPeriods == null) alLevelPeriods = new ArrayList<Map<String,String>>();
												for(int k = 0; k < alLevelPeriods.size();k++){
													Map<String,String> hmPeriod = alLevelPeriods.get(k); 
												%>
													<span>
														<strong><%=(k+1) %>.</strong> Min: <strong><%=hmPeriod.get("MIN_VALUE")%></strong>&nbsp;
														Max: <strong><%=hmPeriod.get("MAX_VALUE")%></strong>&nbsp;
														Workflow Policy: <strong><%=uF.showData(hmPeriod.get("POLICY_NAME"),"N/A")%></strong>
													</span><br/>
												<%} %>
												
											</span>
											<span style="display: none;" id="leavePolicyCombo_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>">
												<select name="leavePolicy_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="leavePolicy_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="margin-bottom: 5px;">
													<option value="">Select Workflow</option>
													<%=uF.showData(hmInner.get("POLICY_LIST"),"")%>
												</select>
												&nbsp;<input type="checkbox" name="strPeriod_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="strPeriod_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" onclick="checkPeriod('<%=hmInner.get("LEVEL_ID")%>','<%=hmInner.get("LEAVE_TYPE_ID")%>');"/> Is Period
												<span style="display: none;" id="leavePeriodCombo_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>">
												<span>
													1. Min:<input type="text" name="strMin1_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="strMin1_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="width: 31px; text-align: right;" onkeypress="return isNumberKey(event)"/>&nbsp;
													Max:<input type="text" name="strMax1_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="strMax1_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="width: 31px; text-align: right;" onkeypress="return isNumberKey(event)"/>&nbsp;
													Workflow Policy: <select name="leavePeriodPolicy1_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="leavePeriodPolicy1_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="margin-bottom: 5px;">
														<option value="">Select Workflow</option>
														<%=uF.showData(hmInner.get("POLICY_LIST"),"")%>
													</select>
												</span><br/>
												<span>
													2. Min:<input type="text" name="strMin2_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="strMin2_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="width: 31px; text-align: right;" onkeypress="return isNumberKey(event)"/>&nbsp;
													Max:<input type="text" name="strMax2_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="strMax2_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="width: 31px; text-align: right;" onkeypress="return isNumberKey(event)"/>&nbsp;
													Workflow Policy: <select name="leavePeriodPolicy2_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="leavePeriodPolicy2_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="margin-bottom: 5px;">
														<option value="">Select Workflow</option>
														<%=uF.showData(hmInner.get("POLICY_LIST"),"")%>
													</select>
												</span><br/>
												<span>
													3. Min:<input type="text" name="strMin3_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="strMin3_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="width: 31px; text-align: right;" onkeypress="return isNumberKey(event)"/>&nbsp;
													Max:<input type="text" name="strMax3_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="strMax3_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="width: 31px; text-align: right;" onkeypress="return isNumberKey(event)"/>&nbsp;
													Workflow Policy: <select name="leavePeriodPolicy3_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="leavePeriodPolicy3_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="margin-bottom: 5px;">
														<option value="">Select Workflow</option>
														<%=uF.showData(hmInner.get("POLICY_LIST"),"")%>
													</select>
												</span><br/>
												<span>
													4. Min:<input type="text" name="strMin4_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="strMin4_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="width: 31px; text-align: right;" onkeypress="return isNumberKey(event)"/>&nbsp;
													Max:<input type="text" name="strMax4_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="strMax4_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="width: 31px; text-align: right;" onkeypress="return isNumberKey(event)"/>&nbsp;
													Workflow Policy: <select name="leavePeriodPolicy4_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="leavePeriodPolicy4_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" style="margin-bottom: 5px;">
														<option value="">Select Workflow</option>
														<%=uF.showData(hmInner.get("POLICY_LIST"),"")%>
													</select>
												</span>
											</span>
												<br/><input type="button" class="btn btn-primary" name="leaveupdate_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="leaveupdate_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" value="Save" onclick="updateLeavePolicy('<%=hmInner.get("LEVEL_ID")%>','<%=hmInner.get("LEAVE_TYPE_ID")%>');">
												&nbsp;<input type="button" class="btn btn-danger" name="leavecancel_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" id="leavecancel_<%=hmInner.get("LEVEL_ID")%>_<%=hmInner.get("LEAVE_TYPE_ID")%>" value="Cancel" onclick="editLeavePolicy('<%=hmInner.get("LEVEL_ID")%>','<%=hmInner.get("LEAVE_TYPE_ID")%>', 'C');">
											</span>
										</div>
									</div>
								</li>
							<%} %>
							<%
								for(int j=0; j<alType.size(); j++){
									List<List<String>> alOuter = hmLevelPolicy.get(innerList.get(0)+"_"+alType.get(j));
									if(alOuter == null) alOuter = new ArrayList<List<String>>();
									
									for(int k=0; k<alOuter.size(); k++){
										List<String> al = alOuter.get(k);
							%>
										<li style="float: left; border-bottom: 1px solid #CCCCCC; width: 95%;">
											<div style="width: 100%; float: left;"> 
												<a href="javascript:void(0);" class="fa fa-edit" onclick="editPolicy('<%=innerList.get(0)%>','<%=alType.get(j)%>', 'U')" style="float: left"></a>
												<div style="float: left; width: 30%;"><strong><%=uF.showData(hmType.get(alType.get(j)),"") %></strong></div> 
												<div style="float: left;">Workflow Policy: 
													<span id="policyName_<%=innerList.get(0)%>_<%=alType.get(j)%>" style="font-weight: bold;"><%=al.get(0)%></span>
													<span style="display: none;" id="policyCombo_<%=innerList.get(0)%>_<%=alType.get(j)%>">
														<select name="policy_<%=innerList.get(0)%>_<%=alType.get(j)%>" id="policy_<%=innerList.get(0)%>_<%=alType.get(j)%>" style="margin-bottom: 5px;">
															<option value="">Select Workflow</option>
															<%=al.get(1)%>
														</select>
														&nbsp;<input type="button" class="btn btn-primary" name="update_<%=innerList.get(0)%>_<%=alType.get(j)%>" id="update_<%=innerList.get(0)%>_<%=alType.get(j)%>" value="Save" onclick="updatePolicy('<%=innerList.get(0)%>','<%=alType.get(j)%>');">
														&nbsp;<input type="button" class="btn btn-danger" name="cancel_<%=innerList.get(0)%>_<%=alType.get(j)%>" id="cancel_<%=innerList.get(0)%>_<%=alType.get(j)%>" value="Cancel" onclick="editPolicy('<%=innerList.get(0)%>','<%=alType.get(j)%>', 'C');">
													</span>
												</div>
											</div>
										</li>
									<%} %>
							<%} %>
						</ul>
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
			<div class="msg nodata"><span>Please select the location.</span></div>
		</div>
	<% } %>          
</div>
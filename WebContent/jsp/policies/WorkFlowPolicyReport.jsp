<%@page import="com.konnect.jpms.policies.WorkFlowPolicy"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(function() {
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
});

function getWorkFlowPolicy(operation,pcount,org,location,group_id,strGroupame, userscreen, navigationId, toPage){
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Workflow Policy to '+strGroupame); 
	$.ajax({
		url : 'WorkFlowPolicy.action?operation='+operation+'&pcount='+pcount+'&organization='+org+'&location='+location+'&group_id='+group_id
			+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
	  
}

function getWorkFlowMember(operation, groupid, org, location, chechGroup, policyCnt, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Workflow Group');
	$.ajax({
		url : 'AddWorkFlowMember.action?operation='+operation+'&group_id='+groupid+'&organization='+org+'&location='+location+'&type='+chechGroup
				+'&policyCnt='+policyCnt+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function submitForm(type){
	if(type == '1'){
		document.getElementById("strLocation").selectedIndex = "0";
	}
	document.frm.submit();
}

</script>
 
<% 
	UtilityFunctions uF=new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	
	Map<String, List<List<String>>> hmReportList =(Map<String, List<List<String>>>)request.getAttribute("hmReportList");
	Map<String, String> hmEmpName =(Map<String, String>) request.getAttribute("hmEmpName");
	
	Map<String,String> hmRegularPolicy =(Map<String,String>)request.getAttribute("hmRegularPolicy");
	Map<String,String> hmContengencyPolicy =(Map<String,String>)request.getAttribute("hmContengencyPolicy");
	
	Map<String,String> hmMemberGroup =(Map<String,String>)request.getAttribute("hmMemberGroup"); 
	Map<String,String> hmGroupID =(Map<String,String>)request.getAttribute("hmGroupID");

	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
%> 
 

<div class="box-body">
	<div class="msg nodata">
		<span>Please change the workflow according to your requirement. Default policy is set, which allows all Approvals to be done by the <%=IConstants.ADMIN %>.</span>		
	</div>
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
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

	<%if(uF.parseToInt((String)request.getAttribute("strLocation"))>0) { %>
	
	<div style="margin:0px 0px 10px 0px">
		<a href="javascript:void(0)" onclick="getWorkFlowMember('A', '', '<%=request.getAttribute("strOrg") %>','<%=request.getAttribute("strLocation") %>','0', '0', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Workflow Group</a>
		&nbsp;|&nbsp;<a href="MyDashboard.action?strOrg=<%=request.getAttribute("strOrg") %>&strLocation=<%=request.getAttribute("strLocation") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>&toTab=AWF">Assign Workflow</a>
	</div>
	<div class="clr"></div>
	<div>
         <ul class="level_list">

		<% 
			Iterator<String> it = hmMemberGroup.keySet().iterator();
			int ii=0;
			while(it.hasNext()) {
				ii++;
				String strGroup_id = (String)it.next();
				String strGroupame = hmMemberGroup.get(strGroup_id);
				int checkGroup = 0;
				if(hmGroupID.get(strGroup_id.trim()) == null) {
					checkGroup = 0;
				} else {
					checkGroup = 1;
				}
				
				List<List<String>> reportList = hmReportList.get(strGroup_id.trim());				
		   		if(reportList == null) reportList = new ArrayList<List<String>>();
					
		%>
				<li>
			      	<% if(reportList == null || reportList.size() == 0) {%>
			      	   <a onclick="return confirm('Are you sure you wish to delete this group?')" href="AddWorkFlowMember.action?operation=D&group_id=<%=strGroup_id%>&organization=<%=request.getAttribute("strOrg") %>&location=<%=request.getAttribute("strLocation") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" style="color:red;"> <i class="fa fa-trash" aria-hidden="true"></i></a>
			    		<a href="javascript:void(0)" onclick="getWorkFlowMember('E','<%=strGroup_id%>','<%=request.getAttribute("strOrg") %>','<%=request.getAttribute("strLocation") %>','<%=checkGroup %>', '<%=reportList.size() %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
			    	<%} %>
				    
					<strong><%=strGroupame%> </strong>
					<ul>		
						<li class="addnew desgn" style="width: 550px;">
					   		<a href="javascript:void(0)" onclick="getWorkFlowPolicy('A','','<%=request.getAttribute("strOrg") %>','<%=request.getAttribute("strLocation") %>','<%=strGroup_id%>','<%=strGroupame%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Workflow Policy to <%=strGroupame%></a>
						</li>
					
				 <% 				
						int count=0;
						for(int i=0;reportList!=null && i<reportList.size();i++){
							List<String> workFlowPolicy = (List<String>)reportList.get(i);
							if(workFlowPolicy==null)workFlowPolicy=new ArrayList<String>();
							count++;							
					%>  					
						<li>
							<a onclick="return confirm('Are you sure you wish to delete this policy?')" href="WorkFlowPolicy.action?operation=D&pcount=<%=workFlowPolicy.get(7)%>&organization=<%=request.getAttribute("strOrg") %>&location=<%=request.getAttribute("strLocation") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" style="color:red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
							<a href="javascript:void(0)" onclick="getWorkFlowPolicy('E','<%=workFlowPolicy.get(7)%>','<%=request.getAttribute("strOrg") %>','<%=request.getAttribute("strLocation") %>','<%=strGroup_id%>','<%=strGroupame%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
							<strong><%=workFlowPolicy.get(8) %></strong>
							<p style="font-size: 12px; padding-left: 66px; font-style: italic;">Last updated by <%=workFlowPolicy.get(5)!=null ? hmEmpName.get(workFlowPolicy.get(5).trim()) : "-" %> on <%=uF.showData(workFlowPolicy.get(6), "") %></p>
							<p style="font-size: 12px; padding-left: 66px;">Workflow: <strong><%=uF.showData(hmRegularPolicy.get(workFlowPolicy.get(7).trim()),"N/A") %></strong></p>
						</li> 
					<%}%>
                 </ul>
            </li> 
		<%}%>
		 </ul>
         
     </div>	
	<% } else { %>
		<div class="filter">
			<div class="msg nodata"><span>Please select the location.</span></div>
		</div>
	<% } %>
		
</div>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div id="workflowdivid"></div>
<div id="workflowmemdivid"></div>
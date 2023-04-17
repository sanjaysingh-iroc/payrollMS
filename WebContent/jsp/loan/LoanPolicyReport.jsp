<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

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

function addLoan(strOrg, strLevelId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Loan');
	$.ajax({
		url : 'AddLoan.action?orgId='+strOrg+'&strLevel='+strLevelId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
}

function editLoan(strOrg, strLevelId, strLoanId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Loan');
	$.ajax({
		url : 'AddLoan.action?operation=E&orgId='+strOrg+'&strLevel='+strLevelId+'&ID='+strLoanId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
</script>


<%
	UtilityFunctions uF = new UtilityFunctions();

	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);

	Map<String, Map<String, String>> hmLevelMap = (Map<String, Map<String, String>>)request.getAttribute("hmLevelMap"); 
	Map<String, List<List<String>>> hmLoanReport = (Map<String, List<List<String>>>)request.getAttribute("hmLoanReport"); 

	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
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
					<s:hidden name="userscreen" id="userscreen" />
					<s:hidden name="navigationId" id="navigationId" />
					<s:hidden name="toPage" id="toPage" />

					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
								<% } %>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
		

	<div class="col-md-12">
         <ul class="level_list">
			<% 
			Iterator<String> it = hmLevelMap.keySet().iterator();
			
			while(it.hasNext()) {
				String strLevelId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmLevelMap.get(strLevelId);
				if(hmInner==null) hmInner = new HashMap<String, String>();
			%>
					
				<li> <span>Level : <strong><%=hmInner.get("LEVEL_NAME")+" ["+hmInner.get("LEVEL_CODE")+"]" %></strong></span>
					<ul>	
						<li class="addnew desgn">
							<a href="javascript:void(0)"  onclick="addLoan('<%=(String)request.getAttribute("strOrg") %>','<%=strLevelId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New Loan slab"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Loan Slab</a>
						</li>
						
						<%
							List<List<String>> outerList = hmLoanReport.get(strLevelId);
							if(outerList==null)outerList=new ArrayList<List<String>>();
							
							for(int i=0; outerList!=null && i<outerList.size(); i++){
								List<String> alLoanPolicyInner = (List<String>)outerList.get(i);
								if(alLoanPolicyInner==null)alLoanPolicyInner=new ArrayList<String>();
								String strLoanId = (String)alLoanPolicyInner.get(0);
						%>
							<li>
								<a href="AddLoan.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=alLoanPolicyInner.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this loan policy?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
								<a href="javascript:void(0)" onclick="editLoan('<%=(String)request.getAttribute("strOrg") %>','<%=strLevelId %>','<%=strLoanId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Loan"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
								Loan Code: <strong><%=alLoanPolicyInner.get(1)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Loan Desc: <strong><%=alLoanPolicyInner.get(2)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Loan Min. Service Yrs: <strong><%=alLoanPolicyInner.get(3)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								ROI: <strong><%=alLoanPolicyInner.get(4)%>%</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Fine Amount: <strong><%=alLoanPolicyInner.get(5)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Times Salary: <strong><%=alLoanPolicyInner.get(6)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Is Check Previous Loan: <strong><%=alLoanPolicyInner.get(9)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								  
								<p style="font-size: 12px; padding-left: 42px; font-style: italic;">Last updated by <%=uF.showData((String)alLoanPolicyInner.get(7), "N/A")%> on <%=uF.showData((String)alLoanPolicyInner.get(8),"")%></p> 
									  
							</li> 								
						<% } %>	
					</ul>
				</li> 
			<% } %>
		 </ul>
         
     </div>
     	
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

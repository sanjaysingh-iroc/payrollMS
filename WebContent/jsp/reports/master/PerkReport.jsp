<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

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


function getData(type) {
	var org='';
	var location='';
	var financialYear = document.getElementById("financialYear").value;
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	if(type=='2'){
		org=document.getElementById("strOrg").value;
	} else {
		org=document.getElementById("strOrg").value;		
	}
	
	window.location='MyDashboard.action?strOrg='+org+"&strLocation="+location+"&strCFYear="+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
}


function addPerk(strOrg, strLevelId, financialYear, userscreen, navigationId, toPage) {  
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Perk');
	$.ajax({
		url : 'AddPerk.action?orgId='+strOrg+'&perklevel='+strLevelId+'&financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
}

function editPerk(strOrg, strLevelId, strPerkId, financialYear, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Perk');
	$.ajax({
		url : 'AddPerk.action?operation=E&orgId='+strOrg+'&perklevel='+strLevelId+'&ID='+strPerkId+'&financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addPerkInSalary(salaryHeadName,salaryHeadId, strLevelId, strOrg, financialYear, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Perk for Salary Structure \''+salaryHeadName+'\'');
	$.ajax({
		url : 'AddPerkSalary.action?salaryHeadId='+salaryHeadId+'&orgId='+strOrg+'&levelId='+strLevelId+'&financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editPerkInSalary(strPerkSalaryId,salaryHeadName,salaryHeadId, strLevelId, strOrg, financialYear, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit New Perk for Salary Structure \''+salaryHeadName+'\'');
	$.ajax({
		url : 'AddPerkSalary.action?operation=E&ID='+strPerkSalaryId+'&salaryHeadId='+salaryHeadId+'&orgId='+strOrg+'&levelId='+strLevelId+'&financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
</script>

<%
	String strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	
	Map<String, Map<String, String>> hmLevelMap = (Map<String, Map<String, String>>)request.getAttribute("hmLevelMap"); 
	Map<String, List<List<String>>> hmPerkReport = (Map<String, List<List<String>>>)request.getAttribute("hmPerkReport");
	Map<String, List<Map<String, String>>> hmPerkAlign = (Map<String, List<Map<String, String>>>) request.getAttribute("hmPerkAlign");
	if(hmPerkAlign == null) hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
	Map<String, List<Map<String, String>>> hmPerkAlignSalary = (Map<String, List<Map<String, String>>>)request.getAttribute("hmPerkAlignSalary");
	if(hmPerkAlignSalary == null) hmPerkAlignSalary = new HashMap<String, List<Map<String, String>>>();
	
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
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" onchange="getData('0');" list="financialYearList" key="" />
							</div>
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="getData('1');"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="getData('1');"></s:select>
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
			
			while(it.hasNext()){
				String strLevelId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmLevelMap.get(strLevelId);
				if(hmInner==null) hmInner = new HashMap<String, String>();
			%>
				<li><span>Level: <strong><%=hmInner.get("LEVEL_NAME")+" ["+hmInner.get("LEVEL_CODE")+"]" %></strong></span>
					<ul>
						
					<%
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(strLevelId);
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlign = alPerkAlign.size();
						boolean isPerkInSalary = false;
						for(int i = 0; i < nPerkAlign; i++){
							Map<String, String> hmAlign = (Map<String, String>) alPerkAlign.get(i);
							if(hmAlign == null) hmAlign = new HashMap<String, String>();
							isPerkInSalary = true;
						%>
							<li class="addnew desgn" style="width: 98%;">
								Perk in salary structure '<strong><%=uF.showData(hmAlign.get("SALARY_HEAD_NAME"),"") %></strong>'
								<ul class="level_list">
									<li class="addnew desgn" style="width: 98%;">
										<a href="javascript:void(0)"  onclick="addPerkInSalary('<%=uF.showData(hmAlign.get("SALARY_HEAD_NAME"),"") %>','<%=hmAlign.get("SALARY_HEAD_ID") %>','<%=strLevelId %>','<%=(String)request.getAttribute("strOrg") %>','<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New Perk in salary"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Perk in salary</a>
									</li>
									<%
									List<Map<String, String>> outerList = hmPerkAlignSalary.get(strLevelId+"_"+hmAlign.get("SALARY_HEAD_ID"));
									if (outerList == null) outerList = new ArrayList<Map<String, String>>();
									int nOuterList = outerList.size();
									for(int j=0; j < nOuterList; j++){
										Map<String, String> hmPerkSalary = outerList.get(j);
										String strPerkSalaryId = hmPerkSalary.get("PERK_SALARY_ID");
									%>
										<li> 
					                    	<a href="AddPerkSalary.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=strPerkSalaryId%>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>" onclick="return confirm('Are you sure you wish to delete this perk?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
					                    	<a href="javascript:void(0)" class="edit_lvl" onclick="editPerkInSalary('<%=strPerkSalaryId %>','<%=uF.showData(hmAlign.get("SALARY_HEAD_NAME"),"") %>','<%=hmAlign.get("SALARY_HEAD_ID") %>','<%=strLevelId %>','<%=(String)request.getAttribute("strOrg") %>','<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Perk"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						                    Perk Code: <strong><%=uF.showData(hmPerkSalary.get("PERK_CODE"),"") %></strong>&nbsp;&nbsp;&nbsp;
						                    Perk Name: <strong><%=uF.showData(hmPerkSalary.get("PERK_NAME"),"") %></strong>&nbsp;&nbsp;&nbsp;
						                    Amount: <strong><%=uF.showData(hmPerkSalary.get("PERK_AMOUNT"),"") %></strong>&nbsp;&nbsp;&nbsp;
						                    Financial Year: <strong><%=uF.showData(hmPerkSalary.get("FINANCIAL_YEAR"),"") %></strong>&nbsp;&nbsp;&nbsp;
						                    Need to apply with documents or receipts: <strong><%=uF.showData(hmPerkSalary.get("PERK_ATTACHMENT"),"") %></strong>&nbsp;&nbsp;&nbsp;
						                    Is Optimal: <strong><%=uF.showData(hmPerkSalary.get("PERK_IS_OPTIMAL"),"") %></strong>&nbsp;&nbsp;&nbsp;
						                    Description: <strong><%=uF.showData(hmPerkSalary.get("PERK_DESCRIPTION"),"") %></strong>
						                    <p style="font-size: 10px; padding-left: 42px; font-style: italic;">	Last updated by <%=uF.showData(hmPerkSalary.get("PERK_USER"),"") %> on <%=uF.showData(hmPerkSalary.get("ENTRY_DATE"),"") %></p>
					                    </li>
									<%} %>
								</ul>
							</li>
						<%} %>
						<%if(isPerkInSalary){ %>
							<li class="addnew desgn" style=" width:97%;border-top: 1px solid #cccccc;"><strong>Default Perk Policy</strong></li>
						<%} %>
						
						<li class="addnew desgn">
							<a href="javascript:void(0)"  onclick="addPerk('<%=(String)request.getAttribute("strOrg") %>', '<%=strLevelId %>', '<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New Perk slab"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Perk Policy</a>
						</li>
						
						<%
						List<List<String>> outerList = hmPerkReport.get(strLevelId);
						if(outerList==null)outerList=new ArrayList<List<String>>();
						
						for(int d=0; d<outerList.size(); d++){
							List<String> alPerk = (List<String>)outerList.get(d);						
							String strPerkId = (String)alPerk.get(0);
						%>
						
						<li>
		                    <a href="AddPerk.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=strPerkId%>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>" onclick="return confirm('Are you sure you wish to delete this perk?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
		                    <a href="javascript:void(0)" onclick="editPerk('<%=(String)request.getAttribute("strOrg") %>', '<%=strLevelId %>', '<%=strPerkId%>', '<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Perk"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
		
		                    Perk Code: <strong><%=(String)alPerk.get(1) %></strong>&nbsp;&nbsp;&nbsp;
		                    Perk Name: <strong><%=(String)alPerk.get(2) %></strong>&nbsp;&nbsp;&nbsp;
		                    Description: <strong><%= (String)alPerk.get(3)%></strong>&nbsp;&nbsp;&nbsp; 
		                    <%-- Perk Type: <strong><%= (String)alPerk.get(4)%></strong>&nbsp;&nbsp;&nbsp; --%> 
		                    Payment Cycle: <strong><%=(String)alPerk.get(5) %></strong>&nbsp;&nbsp;&nbsp;
		                    Max Amount : <strong><%=(String)alPerk.get(6) %></strong>&nbsp;&nbsp;&nbsp;
		                    Financial Year : <strong><%=(String)alPerk.get(10) %></strong>&nbsp;&nbsp;&nbsp;
		                      
							<p style="font-size: 10px; padding-left: 42px; font-style: italic;">	Last updated by <%=uF.showData((String)alPerk.get(8), "N/A")%> on <%=uF.showData((String)alPerk.get(9),"")%></p>
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
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

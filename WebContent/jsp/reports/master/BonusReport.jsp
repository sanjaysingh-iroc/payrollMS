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
	org=document.getElementById("strOrg").value;		
  
	window.location='MyDashboard.action?strOrg='+org+"&strLocation="+location+"&strCFYear="+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
}


function addBonus(orgId, strLevelId, strFinancialYearStart, strFinancialYearEnd, userscreen, navigationId, toPage) {
	var financialYear = document.getElementById("financialYear").value;
	var action = 'AddBonus.action?orgId='+orgId+'&param='+strLevelId+'&FYS='+strFinancialYearStart+'&FYE='+strFinancialYearEnd+"&financialYear="+financialYear
			+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Bonus');
	$.ajax({
		url : action,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
}


function editBonus(orgId, strLevelId, strBonusId, userscreen, navigationId, toPage) {
	var financialYear = document.getElementById("financialYear").value;
	var action = 'AddBonus.action?param='+strLevelId+'&orgId='+orgId+'&operation=E&ID='+strBonusId+"&financialYear="+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html( 'Edit Bonus');
	$.ajax({
		url : action,
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

	String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
	String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
	String  financialYear = (String)request.getAttribute("financialYear");
	Map hmLevelMap = (Map)request.getAttribute("hmLevelMap"); 
	Map hmBonusReport = (Map)request.getAttribute("hmBonusReport");
	
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
			Set setLevelMap = hmLevelMap.keySet();
			Iterator it = setLevelMap.iterator();
			
			while(it.hasNext()){
				String strLevelId = (String)it.next();
				List alLevel = (List)hmLevelMap.get(strLevelId);
				if(alLevel==null)alLevel=new ArrayList();
				
					 
					List alBonus = (List)hmBonusReport.get(strLevelId);
					if(alBonus==null)alBonus=new ArrayList();
					%>
					
					<li> <span>Level Code: <strong><%=alLevel.get(0) %></strong>&nbsp;&nbsp;&nbsp; Level Name: <strong><%=alLevel.get(1) %></strong></span>
						<ul>		
							<li class="addnew desgn">
								<a href="javascript:void(0)" onclick="addBonus('<%=(String)request.getAttribute("strOrg") %>','<%=strLevelId %>','<%=strFinancialYearStart %>','<%=strFinancialYearEnd %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New Bunus slab"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Bonus Slab</a>
							</li>
							<%
								for(int d=0; d<alBonus.size(); d+=9){
								String strBonusId = (String)alBonus.get(d);
							%>  
							<li> 
		                    <a href="AddBonus.action?orgId=<%=request.getAttribute("strOrg") %>&financialYear=<%=financialYear %>&operation=D&ID=<%=strBonusId%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this bonus policy?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
		                    <a href="javascript:void(0)" onclick="editBonus('<%=(String)request.getAttribute("strOrg") %>','<%=strLevelId %>','<%=strBonusId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Bonus"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
		                    <%
		                    	String strBonusType = (String)alBonus.get(d+3);
		                     	if (strBonusType!=null && strBonusType.equals("Percent")){ 
		                    %>
		                     	Max. Bonus: <strong><%=(String)alBonus.get(d+2) %></strong>&nbsp;&nbsp;&nbsp;
		                    <%} %> 
		                    Bonus Type: <strong><%= (String)alBonus.get(d+3)%></strong>&nbsp;&nbsp;&nbsp;
		                    Bonus Amount: <strong><%=(String)alBonus.get(d+4) %></strong>&nbsp;&nbsp;&nbsp;
		                    Days: <strong><%= (String)alBonus.get(d+5)%></strong>&nbsp;&nbsp;&nbsp;
		                    Payable Period: <strong><%= (String)alBonus.get(d+6)%></strong>&nbsp;&nbsp;&nbsp;  
		                      
		                    <p style="font-size: 12px; padding-left: 42px; font-style: italic;">Last updated by <%=uF.showData((String)alBonus.get(d+7), "N/A")%> on <%=uF.showData((String)alBonus.get(d+8),"")%></p>
		                      
		                    </li>
								
						<% } %>		
					</ul>
				</li> 
		<% } if(hmLevelMap.size()==0) { %>
			<li><div class="msg nodata"><span>Company Structure not configured yet for the selected organisation</span></div></li>
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
	
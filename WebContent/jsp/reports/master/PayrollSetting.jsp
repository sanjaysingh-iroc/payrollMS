<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
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
	
	$('#lt').DataTable();
	
});


function editPayrollSetting(strOrgId, userscreen, navigationId, toPage) { 

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Payroll Settings');
	$.ajax({
		url : 'AddPayrollSetting.action?operation=E&ID='+strOrgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function editPaySlipSetting(strOrgId, formatId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit PaySlip Settings');
	$.ajax({
		url : 'AddPaySlipSetting.action?operation=E&ID='+strOrgId+'&formatId='+formatId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addVDARate(userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New VDA Rate');
	var strOrgId = document.getElementById("strOrg").value;
	$.ajax({
		url : 'AddUpdateDeleteVDARate.action?strOrgId='+strOrgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

	
function editVDARate(strVdaRateId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Update VDA Rate');
	var strOrgId = document.getElementById("strOrg").value;
	$.ajax({
		url : 'AddUpdateDeleteVDARate.action?operation=E&strOrgId='+strOrgId+'&userscreen='+userscreen+'&navigationId='+navigationId
			+'&toPage='+toPage+'&strVdaRateId='+strVdaRateId, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function deleteVDARate(strVdaRateId, userscreen, navigationId, toPage) {
	if(confirm("Are you sure, you want to delete this VDA Rate?")) {
		var url='AddUpdateDeleteVDARate.action?operation=D&strVdaRateId='+strVdaRateId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
		window.location = url;
	}
}


function viewVDAAmount(strVdaRateId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('View VDA Amount');
	var strOrgId = document.getElementById("strOrg").value;
	$.ajax({
		url : 'UpdateVDAIndex.action?operation=VIEW&strOrgId='+strOrgId+'&userscreen='+userscreen+'&navigationId='+navigationId
			+'&toPage='+toPage+'&strVdaRateId='+strVdaRateId, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editVDAIndex(userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Update VDA Index');
	var strOrgId = document.getElementById("strOrg").value;
	$.ajax({
		url : 'UpdateVDAIndex.action?operation=E&strOrgId='+strOrgId+'&userscreen='+userscreen+'&navigationId='+navigationId
			+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function generatePdfNew(paySlipFormatId) {
   // var paySlipFormatId = document.getElementById("strSalaryPaySlip").value;
		var url="ReviewPaySlipFormat.action?paySlipFormatId="+paySlipFormatId;
		window.location = url;
	}

</script>

 
<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	Map<String, Map<String, String>> hmOrg = (Map<String, Map<String, String>>) request.getAttribute("hmOrg");
	if(hmOrg == null) hmOrg = new HashMap<String, Map<String, String>>();

	Map<String, Map<String, String>> hmVDAData = (Map<String, Map<String, String>>) request.getAttribute("hmVDAData");
	if(hmVDAData == null) hmVDAData = new HashMap<String, Map<String, String>>();
	
	Map<String, Map<String, String>> hmVDAIndexData = (Map<String, Map<String, String>>) request.getAttribute("hmVDAIndexData");
	if(hmVDAIndexData == null) hmVDAIndexData = new HashMap<String, Map<String, String>>();
	
	
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
         <p style="text-align:left;"><b>PayCycle Setting</b></p>      
   
		<% 
			Iterator<String> it = hmOrg.keySet().iterator();
			while(it.hasNext()) {
				String strOrgId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmOrg.get(strOrgId);
			%>
				<li> 
					<strong>
						<a href="javascript:void(0)" class="edit_lvl" onclick="editPayrollSetting('<%=hmInner.get("ORG_ID") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Payroll Setting"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						<%=uF.showData(hmInner.get("ORG_NAME"),"") %> [<%=uF.showData(hmInner.get("ORG_CODE"),"") %>] 
					</strong>
					<ul>
						<li>
		                    <p>
								Start Paycycle: <strong><%=uF.showData(hmInner.get("ORG_START_PAYCYCLE"),"") %> </strong>&nbsp;&nbsp;&nbsp;
								Display Paycycle: <strong><%=uF.showData(hmInner.get("ORG_DISPLAY_PAYCYCLE"),"") %> </strong>&nbsp;&nbsp;&nbsp;
								Duration of Paycycle: <strong><%=uF.showData(hmInner.get("ORG_DURATION_PAYCYCLE"),"") %> </strong>&nbsp;&nbsp;&nbsp;
								Salary Calculation Basis: <strong><%=uF.showData(hmInner.get("ORG_SALARY_CAL_BASIS"),"") %> </strong>&nbsp;&nbsp;&nbsp;
								<%if(hmInner.get("ORG_SALARY_CAL_BASIS")!=null && hmInner.get("ORG_SALARY_CAL_BASIS").equals("Fixed Days")){ %>
								Fix Days: <strong><%=uF.showData(hmInner.get("ORG_SALARY_FIX_DAYS"),"") %> </strong>&nbsp;&nbsp;&nbsp;
								<%} %>
		                    </p> 
		                   
						</li>
					</ul>
				</li>	
			 <%} %>  
			 
		<p style="text-align:left;"><b>PaySlip Setting</b></p>  
		<% 
			Iterator<String> it2 = hmOrg.keySet().iterator(); 
			while(it2.hasNext()) {
				String strOrgId = (String)it2.next();
				Map<String, String> hmInner = (Map<String, String>)hmOrg.get(strOrgId);
			%>
				<li> 
					<strong>
						<a href="javascript:void(0)" class="edit_lvl" onclick="editPaySlipSetting('<%=hmInner.get("ORG_ID") %>', '<%=hmInner.get("ORG_PAYSLIP_FORMAT_ID") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit PaySlip Setting"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						<%=uF.showData(hmInner.get("ORG_NAME"),"") %> [<%=uF.showData(hmInner.get("ORG_CODE"),"") %>] 
					</strong>
					<ul>
						<li>
		                    <p>
								Payslip Format: <strong>
								<%=uF.showData(hmInner.get("ORG_PAYSLIP_FORMAT"),"") %> </strong>&nbsp;&nbsp;&nbsp;
								<a href="javascript:void(0)" onclick="generatePdfNew('<%=uF.showData(hmInner.get("ORG_PAYSLIP_FORMAT_ID"),"") %>');"><i class="fa fa-file-o" aria-hidden="true" style="margin: 0px 0px -6px -11px;"></i></a>
		                    </p> 
		                    
						</li>
					</ul>
				</li>	
			 <%} %>
			 
			 
		<p style="text-align:left;"><b>VDA Setting</b></p>
		
		<p style="text-align:left;"><b>VDA Rate</b></p>
			<div>
			<a href="javascript:void(0)" onclick="addVDARate('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New VDA Rate"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New VDA Rate</a></div>
			<% 
			Iterator<String> itVDA = hmVDAData.keySet().iterator(); 
			while(itVDA.hasNext()) {
				String strVdaRateId = (String)itVDA.next();
				Map<String, String> hmInner = (Map<String, String>)hmVDAData.get(strVdaRateId);
			%>
				<li> 
					<ul>
						<li>
		                    <p>
		                    <a href="javascript:void(0)" style="color: rgb(233, 0, 0);" onclick="deleteVDARate('<%=strVdaRateId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')">  <i class="fa fa-trash" aria-hidden="true"></i>  </a>
		                    <a href="javascript:void(0)" class="edit_lvl" onclick="editVDARate('<%=strVdaRateId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit VDA Rate"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
		                    <a href="javascript:void(0)" class="edit_lvl" onclick="viewVDAAmount('<%=strVdaRateId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="View VDA Amount"><i class="fa fa-file-text-o" aria-hidden="true"></i></a>
								Paycycle: <strong>
								<%=uF.showData(hmInner.get("PAYCYCLE"), "") %> </strong>&nbsp;&nbsp;&nbsp;
								VDA Rate: <strong>
								<%=uF.showData(hmInner.get("VDA_RATE"), "") %> </strong>
		                    </p> 
		                    
						</li>
					</ul>
				</li>	
			 <%} %>
			 
		<p style="text-align:left;"><b>VDA Index</b></p>
			<div>
			
				<li> 
					<ul>
						<li>
		                    <table class="table table-bordered" id="lt">
		                    <tr>
			                    <th>Designation Name</th>
			                    <th>Probation</th>
			                    <th>Permanent</th>
			                    <th>Temporary</th>
			                    <th>Action</th>
		                    </tr>
		                    <% 
							Iterator<String> itVDAIndex = hmVDAIndexData.keySet().iterator(); 
							while(itVDAIndex.hasNext()) {
								String strDesigId = (String)itVDAIndex.next();
								Map<String, String> hmInner = (Map<String, String>)hmVDAIndexData.get(strDesigId);
							%>
			                    <tr>
			                    <td><%=hmInner.get("DESIG_NAME") %></td>
			                    <td><%=hmInner.get("VDA_INDEX_PROBATION") %></td>
			                    <td><%=hmInner.get("VDA_INDEX_PERMANENT") %></td>
			                    <td><%=hmInner.get("VDA_INDEX_TEMPORARY") %></td>
			                    <td><a href="javascript:void(0)" class="edit_lvl" onclick="editVDAIndex('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit VDA Index"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a></td>
			                    </tr>
		                     <%} %>
		                    </table>
						</li>
					</ul>
				</li>	
			
			 
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

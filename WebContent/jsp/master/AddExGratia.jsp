<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
	if(hmOrg==null) hmOrg = new HashMap<String, String>();
%>


<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script> 
$(document).ready( function () {
	$("input[name='gratiaUpdate']").click(function(){
		$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');	 
	});
	$('#ltp').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});
});	

function submitForm(type) {
	var org='';
	var financialYear='';
	var paycycle='';
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	var toTab = document.getElementById("toTab").value;
	if(type=='3') {
		org = document.getElementById("strOrg").value;
		financialYear = document.getElementById("financialYear").value;
		paycycle = document.getElementById("paycycle").value;
	} else if(type=='2') {
		org=document.getElementById("strOrg").value;
		financialYear = document.getElementById("financialYear").value;
	} else {
		org=document.getElementById("strOrg").value;		
	}
	
	window.location='MyDashboard.action?strOrg='+org+'&strCFYear='+financialYear+'&paycycle='+paycycle+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&toTab='+toTab;
	
}

</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Add Ex Gratia" name="title" />
</jsp:include> --%>

<section class="content" style="padding-top:0px;">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable col_no_padding">
        <%
			String userscreen = (String)request.getAttribute("userscreen");
			String navigationId = (String)request.getAttribute("navigationId");
			String toPage = (String)request.getAttribute("toPage");
			String toTab = (String)request.getAttribute("toTab");
			
		%>			
        	<div class="box box-none nav-tabs-custom">
        		<ul class="nav nav-tabs">
				<% if(toTab == null || toTab.equals("EGS")) { %>
					<li class="active"><a href="javascript:void(0)" onclick="window.location='MyDashboard.action?toTab=EGS&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>'" data-toggle="tab">Ex Gratia Slab</a></li>
					<li><a href="javascript:void(0)" onclick="window.location='MyDashboard.action?toTab=EGP&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>'" data-toggle="tab">Ex Gratia Policy</a></li>
				<% } else if(toTab != null && toTab.equals("EGP")) { %>
					<li><a href="javascript:void(0)" onclick="window.location='MyDashboard.action?toTab=EGS&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>'" data-toggle="tab">Ex Gratia Slab</a></li>
					<li class="active"><a href="javascript:void(0)" onclick="window.location='MyDashboard.action?toTab=EGP&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>'" data-toggle="tab">Ex Gratia Policy</a></li>
				<% } %>	
				</ul>
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
					<%=uF.showData((String) session.getAttribute("MESSAGE"), "") %>
					<% session.setAttribute("MESSAGE", ""); %>
					<s:form name="frm_ExGratia" id="idFrmExGratia" theme="simple" action="AddExGratia">
						<s:hidden name="userscreen" id="userscreen"/>
						<s:hidden name="navigationId" id="navigationId"/>
						<s:hidden name="toPage" id="toPage"/>
						<s:hidden name="toTab" id="toTab"/>
						<table class="table">
							<tr><td>Organisation:</td>
								<td valign="top" style="padding-left: 10px">
									<s:select theme="simple" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" 
										onchange="submitForm('1');" list="orgList" key="" />
								</td>
							</tr>
							<tr>
								<td>Financial Year:</td>
								<td valign="top" style="padding-left: 10px">
									<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" 
										name="financialYear" id="financialYear" onchange="submitForm('2');" />
								</td>
							</tr>
							<tr>
								<td>PayCycle:</td>
								<td valign="top" style="padding-left: 10px">
									<s:select name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="payCycleList"
										onchange="submitForm('3');"/>
								</td>
							</tr>
							
							<tr>
								<td>Net Profit:</td>
								<td valign="top" style="padding-left: 10px">
									<s:textfield name="netProfit" id="netProfit" cssClass="required validateNumber" onkeypress="return isNumberKey(event)" />
								</td>
							</tr>
				
							<tr>
								<td colspan="2">
									<input type="submit" class="btn btn-primary" name="gratiaUpdate" value="Update"
										onclick="return confirm('Are you sure you want to update these Ex-Gratia?')" />
								</td>
							</tr>
						</table>
					</s:form>
				
					<div class="pagetitle" style="margin: 10px 0px 10px 0px; font-size: 16px; font-weight: 600; color: #346897;">Ex Gratia</div>
					<table class="table table-bordered" id="ltp">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Organisation</th>
							<th>Net Profit</th>
							<th>Paycycle</th>
							<th>Added By</th>
							<th>Entry Date</th>
							<th>Action</th>
						</tr>
						</thead>
						<tbody>
						<%List<Map<String, String>> gratiaList = (List<Map<String, String>>)request.getAttribute("gratiaList"); 
					      if(gratiaList==null) gratiaList = new ArrayList<Map<String,String>>();
					      for(int i=0;gratiaList!=null && i<gratiaList.size();i++){
					    	  Map<String,String> hmInner = (Map<String,String>) gratiaList.get(i);
					    	  String payCycle = "PayCycle "+uF.showData(hmInner.get("PAYCYCLE"),"")+", "+uF.showData(hmInner.get("PAYCYCLE_FROM"),"")+" - "+uF.showData(hmInner.get("PAYCYCLE_TO"),"");
						%>
						<tr>
							<td class="alignCenter"><%=(i+1) %></td>
							<td class="alignCenter"><%=uF.showData(hmOrg.get(hmInner.get("ORG_ID")),"") %></td>
							<td class="alignRight"><%=uF.showData(hmInner.get("NET_PROFIT"),"") %></td>
							<td class="alignCenter"><%=payCycle %></td>
							<td class="alignCenter"><%=uF.showData(hmInner.get("ADDED_BY"),"") %></td>
							<td class="alignCenter"><%=uF.showData(hmInner.get("ENTRY_DATE"),"") %></td>
							<td><a href="AddExGratia.action?operation=D&ID=<%=hmInner.get("EX_GRATIA_ID")%>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>&toTab=<%=toTab%>&financialYear=<%=hmInner.get("FINANCIAL_YEAR")%>" title="Delete Gratia"  onclick="return confirm('Are you sure you wish to delete this Ex-Gratia?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a></td>
						</tr>
						<%} %>
						</tbody>
					</table>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>

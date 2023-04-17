<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strMonth = document.getElementById("strMonth").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&financialYear='+financialYear+'&strMonth='+strMonth;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form6ESIC.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

function generateForm6ESIC() {
	var financialYear=document.frm_from6.financialYear.value;
	var strMonth=document.frm_from6.strMonth.value;
	var f_org=document.frm_from6.f_org.value;
	//var strSelectedEmpId = document.frm_from6.strSelectedEmpId.value;
	
	var url='Form6ESIC.action?formType=pdf&financialYear='+financialYear;
	url+='&strMonth='+strMonth+'&f_org='+f_org; // +'&strSelectedEmpId=' + strSelectedEmpId; 
	window.location = url;
	}
</script> 

<style type="text/css">
body
{
 margin:0 auto;
}
.fill:after
{
 content:"_______________________________________________________________________________________________________"	
}
.tdBorder{
	 border: 1px solid black;
}
 
</style>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	
	Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
	if(hmOrg == null) hmOrg = new HashMap<String, String>();
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
	if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
	Map<String, String> hmEmpDepartment = (Map<String, String>) request.getAttribute("hmEmpDepartment");
	if(hmEmpDepartment == null) hmEmpDepartment = new HashMap<String, String>();
	Map<String, String> hmDept = (Map<String, String>) request.getAttribute("hmDept");
	if(hmDept == null) hmDept = new HashMap<String, String>();
	Map<String, String> hmMonthChallanDate = (Map<String, String>) request.getAttribute("hmMonthChallanDate");
	if(hmMonthChallanDate == null) hmMonthChallanDate = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	if(hmEmpName == null) hmEmpName = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpContribution = (Map<String, String>) request.getAttribute("hmEmpContribution");
	if(hmEmpContribution == null) hmEmpContribution = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpWages = (Map<String, String>) request.getAttribute("hmEmpWages");
	if(hmEmpWages == null) hmEmpWages = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpPaidDays = (Map<String, String>) request.getAttribute("hmEmpPaidDays");
	if(hmEmpPaidDays == null) hmEmpPaidDays = new LinkedHashMap<String, String>();
	
	Map<String, String> hmEmployerContribution = (Map<String, String>) request.getAttribute("hmEmployerContribution");
	if(hmEmployerContribution == null) hmEmployerContribution = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpJoinLeftDate = (Map<String, String>) request.getAttribute("hmEmpJoinLeftDate");
	if(hmEmpJoinLeftDate == null) hmEmpJoinLeftDate = new HashMap<String, String>();
	
	Map<String, String> hmEmpInsuranceNo = (Map<String, String>) request.getAttribute("hmEmpInsuranceNo");
	if(hmEmpInsuranceNo == null) hmEmpInsuranceNo = new LinkedHashMap<String, String>();
	
	List<String> alEmpList = (List<String>) request.getAttribute("alEmpList");
	if(alEmpList == null) alEmpList = new ArrayList<String>();
	
	String strHalfYear = (String) request.getAttribute("strHalfYear");
	String yearType = (String)request.getAttribute("yearType");
%>
                
		<div class="box-header with-border">
		    <h3 class="box-title">Form 6 ESIC</h3>
		</div>
		<!-- /.box-header -->
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
					<div class="box-tools pull-right"><button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
					<s:form name="frm_from6" action="Form6ESIC" theme="simple">
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" onchange="submitForm('2');" list="monthList" key=""/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
		
		<!-- 	<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<a onclick="generateForm6ESIC();" href="javascript:void(0)" class="fa fa-file-pdf-o" >&nbsp;&nbsp;</a>
			</div>  -->
			<div class="col-md-2 pull-right">
					
<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>

</div>
		
			<div style="margin:0 auto; width:100%; text-align:center; overflow:hidden;">
				<h3 style="font-size:20px;"><strong>FORM 6</strong></h3>
				<p style="text-transform:uppercase; font-size:16px;">REGISTER OF EMPLOYEES</p>
				<p style="text-transform:uppercase; font-size:16px;">EMPLOYEES' STATE INSURANCE CORPORATION<br/><span style="font-size:12px; text-transform:capitalize;">(Regulation 32)</span></p>
				
				<div style="clear:both;">
					<p style="text-align:left;padding-left:40px;">Contribution Period : From <%=uF.showData(strHalfYear, "") %></p>
					<div style="overflow:hidden; margin:0 auto; width:94%;">
						<%
							int j = 10;
					        if(yearType!=null && yearType.equals("1")){
					        	j = 4;
					        }
						%>
						<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
							<tr>
								<td rowspan="2" class="tdBorder">Sl.No.</td>
								<td rowspan="2" class="tdBorder">Insurance No.</td>
								<td rowspan="2" class="tdBorder">Name of Insured Person</td>
								<td rowspan="2" class="tdBorder">*Name of dispensary to which attached</td>
								<td rowspan="2" class="tdBorder">Occupation</td>
								<td rowspan="2" class="tdBorder">Deptt. and shift, if any</td>
								<td rowspan="2" class="tdBorder">If appointed or left service during the contribution period, date of appointment/leaving service</td>
								<td colspan="3" class="tdBorder">Month <strong><%=uF.getMonth(j) %></strong></td>
							</tr>
							<tr>
								<td class="tdBorder">No. of days for which wages paid/payable</td>
								<td class="tdBorder">Total amount of wages paid/payable</td>
								<td class="tdBorder">Employees' share of contribution</td>
							</tr>
							<tr>
								<td class="tdBorder">1</td>
								<td class="tdBorder">2</td>
								<td class="tdBorder">3</td>
								<td class="tdBorder">3(A)</td>
								<td class="tdBorder">4</td>
								<td class="tdBorder">5</td>
								<td class="tdBorder">6</td>
								<td class="tdBorder">7</td>
								<td class="tdBorder">8</td>
								<td class="tdBorder">9</td>
							</tr>
							<%
								double dblTotalWages = 0.0d;
						        double dblTotalContribution = 0.0d;
						        Map<String,String> hmEmpTotalPaidDays = new HashMap<String, String>();
						        Map<String,String> hmEmpTotalWages = new HashMap<String, String>();
						        Map<String,String> hmEmpTotalContribution = new HashMap<String, String>();
						        for (int i = 0; i < alEmpList.size(); i++){
						        	String strEmpId = alEmpList.get(i);
						        	
						        	hmEmpTotalPaidDays.put(strEmpId, ""+uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j)));
						        	
						        	dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
							        hmEmpTotalWages.put(strEmpId, ""+uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j)));
							        
							        dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
							        hmEmpTotalContribution.put(strEmpId, ""+uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j)));
							%>
								<tr>
									<td class="tdBorder" style="text-align: center;"><%=(i+1) %></td>
									<td class="tdBorder" style="text-align: center;"><%=uF.showData(hmEmpInsuranceNo.get(strEmpId), "") %></td>
									<td class="tdBorder" style="text-align: left;"><%=uF.showData(hmEmpName.get(strEmpId), "") %></td>
									<td class="tdBorder" style="text-align: left;"></td>
									<td class="tdBorder" style="text-align: left;"><%=uF.showData(hmEmpCodeDesig.get(strEmpId), "") %></td>
									<td class="tdBorder" style="text-align: left;"><%=uF.showData(hmDept.get(hmEmpDepartment.get(strEmpId)), "") %></td>
									<td class="tdBorder" style="text-align: center;"><%=uF.showData(hmEmpJoinLeftDate.get(strEmpId), "") %></td>
									<td class="tdBorder" style="text-align: right;"><%=uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), "") %></td>
									<td class="tdBorder" style="text-align: right;"><%=uF.showData(hmEmpWages.get(strEmpId+"_"+j), "") %></td>
									<td class="tdBorder" style="text-align: right;"><%=uF.showData(hmEmpContribution.get(strEmpId+"_"+j), "") %></td>
								</tr>
								
							<%} %>
								
								<tr>
									<td class="tdBorder" style="text-align: right;" colspan="8">Total</td>
									<td class="tdBorder" style="text-align: right;"><%=dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "" %></td>
									<td class="tdBorder" style="text-align: right;"><%=dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "" %></td>
								</tr>
								<tr>
									<td class="tdBorder" style="text-align: right;" colspan="9">Employers' Share</td>
									<td class="tdBorder" style="text-align: right;"><%=uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "" %></td>
								</tr>
								<%double dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j)); %>
								<tr>
									<td class="tdBorder" style="text-align: right;" colspan="9">Grand Total</td>
									<td class="tdBorder" style="text-align: right;"><%=dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "" %></td>
								</tr>
								<tr>
									<td class="tdBorder" style="text-align: right;" colspan="9">Paid on</td>
									<td class="tdBorder" style="text-align: right;"><%=uF.showData(hmMonthChallanDate.get(""+j), "") %></td>
								</tr>
						</table>
					</div>
					
					<div style="overflow:hidden; margin:0 auto; width:94%;">
						<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
							<tr>
								<td>
									<%
										j++;
									%>
									<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
										<tr>
											<td class="tdBorder" colspan="3">Month <strong><%=uF.getMonth(j) %></strong></td>
										</tr>
										<tr>
											<td class="tdBorder">No. of days for which wages paid/payable</td>
											<td class="tdBorder">Total amount of wages paid/payable</td>
											<td class="tdBorder">Employees' share of contribution</td>
										</tr>
										<tr>
											<td class="tdBorder">10</td>
											<td class="tdBorder">11</td>
											<td class="tdBorder">12</td>
										</tr>
										<%
											dblTotalWages = 0.0d;
									        dblTotalContribution = 0.0d;
									        for (int i = 0; i < alEmpList.size(); i++){
									        	String strEmpId = alEmpList.get(i);
										%>
											<tr>
												<%
													double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
										        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
										        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        
											        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
											        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpWages.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
											        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpContribution.get(strEmpId+"_"+j), "") %></td>
											</tr>
											
										<%} %>
											
											<tr>
												<td class="tdBorder" style="text-align: right;">Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "" %></td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Employers' Share</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "" %></td>
											</tr>
											<%
												dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
											 %>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Grand Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Paid on</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.showData(hmMonthChallanDate.get(""+j), "") %></td>
											</tr>
									</table>
								</td>
								<td>
									<%
										j++;
									%>
									<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
										<tr>
											<td class="tdBorder" colspan="3">Month <strong><%=uF.getMonth(j) %></strong></td>
										</tr>
										<tr>
											<td class="tdBorder">No. of days for which wages paid/payable</td>
											<td class="tdBorder">Total amount of wages paid/payable</td>
											<td class="tdBorder">Employees' share of contribution</td>
										</tr>
										<tr>
											<td class="tdBorder">13</td>
											<td class="tdBorder">14</td>
											<td class="tdBorder">15</td>
										</tr>
										<%
											dblTotalWages = 0.0d;
									        dblTotalContribution = 0.0d;
									        for (int i = 0; i < alEmpList.size(); i++){
									        	String strEmpId = alEmpList.get(i);
										%>
											<tr>
												<%
													double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
										        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
										        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        
											        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
											        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpWages.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
											        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpContribution.get(strEmpId+"_"+j), "") %></td>
											</tr>
											
										<%} %>
											
											<tr>
												<td class="tdBorder" style="text-align: right;">Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "" %></td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Employers' Share</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "" %></td>
											</tr>
											<%
												dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
											 %>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Grand Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Paid on</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.showData(hmMonthChallanDate.get(""+j), "") %></td>
											</tr>
									</table>
								</td>
								<td>
									<%
										j++;
										if(j==13){
											j=1;
										}
									%>
									<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
										<tr>
											<td class="tdBorder" colspan="3">Month <strong><%=uF.getMonth(j) %></strong></td>
										</tr>
										<tr>
											<td class="tdBorder">No. of days for which wages paid/payable</td>
											<td class="tdBorder">Total amount of wages paid/payable</td>
											<td class="tdBorder">Employees' share of contribution</td>
										</tr>
										<tr>
											<td class="tdBorder">16</td>
											<td class="tdBorder">17</td>
											<td class="tdBorder">18</td>
										</tr>
										<%
											dblTotalWages = 0.0d;
									        dblTotalContribution = 0.0d;
									        for (int i = 0; i < alEmpList.size(); i++){
									        	String strEmpId = alEmpList.get(i);
										%>
											<tr>
												<%
													double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
										        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
										        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        
											        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
											        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpWages.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
											        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpContribution.get(strEmpId+"_"+j), "") %></td>
											</tr>
											
										<%} %>
											
											<tr>
												<td class="tdBorder" style="text-align: right;">Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "" %></td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Employers' Share</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "" %></td>
											</tr>
											<%
												dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
											 %>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Grand Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Paid on</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.showData(hmMonthChallanDate.get(""+j), "") %></td>
											</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
					
					<div style="overflow:hidden; margin:0 auto; width:94%;">
						<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
							<tr>
								<td>
									<%
										j++;
										if(j==14){
											j=2;
										}
									%>
									<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
										<tr>
											<td class="tdBorder" colspan="3">Month <strong><%=uF.getMonth(j) %></strong></td>
										</tr>
										<tr>
											<td class="tdBorder" style="height: 115px;">No. of days for which wages paid/payable</td>
											<td class="tdBorder" style="height: 115px;">Total amount of wages paid/payable</td>
											<td class="tdBorder" style="height: 115px;">Employees' share of contribution</td>
										</tr>
										<tr>
											<td class="tdBorder">19</td>
											<td class="tdBorder">20</td>
											<td class="tdBorder">21</td>
										</tr>
										<%
											dblTotalWages = 0.0d;
									        dblTotalContribution = 0.0d;
									        for (int i = 0; i < alEmpList.size(); i++){
									        	String strEmpId = alEmpList.get(i);
										%>
											<tr>
												<%
													double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
										        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
										        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        
											        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
											        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpWages.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
											        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpContribution.get(strEmpId+"_"+j), "") %></td>
											</tr>
											
										<%} %>
											
											<tr>
												<td class="tdBorder" style="text-align: right;">Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "" %></td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Employers' Share</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "" %></td>
											</tr>
											<%
												dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
											 %>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Grand Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Paid on</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.showData(hmMonthChallanDate.get(""+j), "") %></td>
											</tr>
									</table>
								</td>
								<td>
									<%
										j++;
										if(j==15){
											j=3;
										}
									%>
									<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
										<tr>
											<td class="tdBorder" colspan="3">Month <strong><%=uF.getMonth(j) %></strong></td>
										</tr>
										<tr>
											<td class="tdBorder" style="height: 115px;">No. of days for which wages paid/payable</td>
											<td class="tdBorder" style="height: 115px;">Total amount of wages paid/payable</td>
											<td class="tdBorder" style="height: 115px;">Employees' share of contribution</td>
										</tr>
										<tr>
											<td class="tdBorder">22</td>
											<td class="tdBorder">23</td>
											<td class="tdBorder">24</td>
										</tr>
										<%
											dblTotalWages = 0.0d;
									        dblTotalContribution = 0.0d;
									        for (int i = 0; i < alEmpList.size(); i++){
									        	String strEmpId = alEmpList.get(i);
										%>
											<tr>
												<%
													double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
										        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
										        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        
											        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
											        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
											        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpWages.get(strEmpId+"_"+j), "") %></td>
												<%
													dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
											        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
											        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.showData(hmEmpContribution.get(strEmpId+"_"+j), "") %></td>
											</tr>
											
										<%} %>
											
											<tr>
												<td class="tdBorder" style="text-align: right;">Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "" %></td>
												<td class="tdBorder" style="text-align: right;"><%=dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Employers' Share</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "" %></td>
											</tr>
											<%
												dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
											 %>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Grand Total</td>
												<td class="tdBorder" style="text-align: right;"><%=dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "" %></td>
											</tr>
											<tr>
												<td class="tdBorder" style="text-align: right;" colspan="2">Paid on</td>
												<td class="tdBorder" style="text-align: right;"><%=uF.showData(hmMonthChallanDate.get(""+j), "") %></td>
											</tr>
									</table>
								</td>
								<td>
									
									<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
										<tr>
											<td class="tdBorder" colspan="4">Summary</strong></td>
										</tr>
										<tr>
											<td class="tdBorder" style="height: 115px;">Total No. of days for which wages paid/payable in Contribution period</td>
											<td class="tdBorder" style="height: 115px;">Total amount of wages paid/payable in Contribution period (Rs.)</td>
											<td class="tdBorder" style="height: 115px;">Total Employee's share of Contribution in Contribution period (Rs.)</td>
											<td class="tdBorder" style="height: 115px;">Daily wage<br/>(26/25)<br/>(Rs.)</td>
										</tr>
										<tr>
											<td class="tdBorder">25</td>
											<td class="tdBorder">26</td>
											<td class="tdBorder">27</td>
											<td class="tdBorder">28</td>
										</tr>
										<%
										for (int i = 0; i < alEmpList.size(); i++){
								        	String strEmpId = alEmpList.get(i);
										%>
											<tr>
												<%
													double dblTotalPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
										        	dblTotalPaidDays = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalPaidDays));
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=dblTotalPaidDays %></td>
												<%
													double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
											        dblEmpTotalWages = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEmpTotalWages));
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=dblEmpTotalWages %></td>
												<%
													double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
											        dblEmpTotalContribution = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEmpTotalContribution));
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=dblEmpTotalContribution %></td>
												<%
													double dblDailyAvg = dblEmpTotalWages / dblTotalPaidDays;
												%>
												<td class="tdBorder" style="text-align: right; height: 20.5px;"><%=uF.formatIntoTwoDecimalWithOutComma(dblDailyAvg) %></td>
											</tr>
											
										<%} %>
											
											<tr> <td class="tdBorder" colspan="4">&nbsp;</td> </tr> 
											<tr> <td class="tdBorder" colspan="4">&nbsp;</td> </tr> 
											<tr> <td class="tdBorder" colspan="4">&nbsp;</td> </tr> 
											<tr> <td class="tdBorder" colspan="4">&nbsp;</td> </tr> 
									</table>
								</td>
							</tr>
						</table>
					</div>
					
					<p style="text-align:left;padding-left:40px;">Note: The figures in Columns 7 to 24 shall be in respect of wage periods ending in a particular calendar month.</p>
				</div>
				
			</div>
		
		</div>
		<!-- /.box-body -->
	</div>
 
   
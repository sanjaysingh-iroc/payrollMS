<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript">
jQuery(document).ready(function() {

	jQuery(".content1").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading_dash").click(function() {
		jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("filter_close");
	});
});

function submitForm(type){
	if(type == '1'){
		document.getElementById("paycycle").selectedIndex = "0";
	}
	document.frm_SalarySummary.exportType.value='';
	document.frm_SalarySummary.submit();
}
</script> 

<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

Map<String,String> hmhead = (Map<String,String>)request.getAttribute("hmhead"); 
if(hmhead == null) hmhead = new HashMap<String, String>();
Map<String,String> hmlevel =(Map<String,String>)request.getAttribute("hmlevel");
if(hmlevel == null) hmlevel = new HashMap<String, String>();
Map<String,String> hmAmount =(Map<String,String>) request.getAttribute("hmAmount");
if(hmAmount == null) hmAmount = new HashMap<String, String>();
List<String> alEarning =(List<String>) request.getAttribute("alEarning");
if(alEarning == null) alEarning = new ArrayList<String>();
List<String> alDeduction =(List<String>) request.getAttribute("alDeduction");
if(alDeduction == null) alDeduction = new ArrayList<String>();
List<String> alLevel =(List<String>) request.getAttribute("alLevel");
if(alLevel == null) alLevel = new ArrayList<String>();

double totalEarning=0.0d;
double totalDeduction=0.0d;
%>
<div class="leftbox reportWidth">
		<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
			<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
				<%=(String)request.getAttribute("selectedFilter") %>
			</p>
			<div class="content1" style="height: 170px;">
				<s:form name="frm_SalarySummary" action="SalarySummary" theme="simple">
					<s:hidden name="exportType"></s:hidden>
					<div style="float: left; width: 100%;">
						<div style="float: left; margin-top: 10px;">
							<img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/filter_icon.png">
						</div>
						
						<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Organization</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="orgList" key="" onchange="submitForm('1')" />
			            </div>
					</div>
					<div style="float: left; width: 100%;">
						<div style="float: left; margin-top: 10px;">
							<img style="padding: 2px 1px 0 1px; width: 24px;" border="0" src="<%=request.getContextPath()%>/images1/icons/cal_period_icon.png">
						</div>
						
			      		<div id="financialYearDIV" style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Paycycle</p>
							<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" 
								headerKey="" headerValue="Select Paycycle" key="" onchange="submitForm('2')"/>
			      		</div>
			      		
			      		<!-- <div id="monthDIV" style="float: left;margin-top: 10px; margin-left: 10px; width: 325px;">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" name="Submit" value="Submit" class="input_button" style="margin:0px" onclick="submitForm('2');"/>
			      		</div> -->
					</div>
				</s:form>
			</div>	
		</div>

		<div style="overflow:scroll; margin:0 auto; width:94%;">
			 <table cellpadding="3" cellspacing="0" width="100%">
				<tr>			
					<td style="text-align:left; border-bottom:solid 1px #000;border-top:solid 1px #000;"><strong>Level</strong></td>
					<%
					int nLevel = alLevel.size();
					for(int i=0; i<alLevel.size(); i++){
						String strLevelId = alLevel.get(i);
					%>
						<td align="right" style="border-bottom:solid 1px #000;border-top:solid 1px #000" nowrap="nowrap"><strong><%=uF.showData(hmlevel.get(strLevelId),"") %></strong></td>
					<%} %>
				</tr>
				
				<tr>			
					<td colspan="<%=(nLevel+1) %>" style="text-align:left; border-bottom:solid 1px #000;"><strong>Earning</strong></td>
				</tr>	
		
				<%
				Map<String,String> hmEarningTotal = new HashMap<String,String>(); 
				for(int i=0; i<alEarning.size(); i++){
					String strSalaryHeadId = alEarning.get(i);
				%>
					<tr>
						<td style="text-align:left; border-bottom:dashed 1px #ccc;" nowrap="nowrap"><%=uF.showData(hmhead.get(strSalaryHeadId),"")%></td>
						<%
						for(int j=0; j<alLevel.size(); j++){
							String strLevelId = alLevel.get(j);
							double dblAmount = uF.parseToDouble(hmAmount.get(strSalaryHeadId+"_"+strLevelId));
							double dblEarning = uF.parseToDouble(hmEarningTotal.get(strLevelId));
							dblEarning += dblAmount;
							hmEarningTotal.put(strLevelId,""+dblEarning);
						%>
							<td align="right" class="paddingRight20" style="border-bottom:dashed 1px #ccc"><%=dblAmount %></td>
							<%		
						}
						%>
					</tr>
				<%} %>
				
				<tr>			
					<td style="text-align:left; border-bottom:solid 1px #000;"><strong>Earning Total</strong></td>
					<%
						for(int j=0; j<alLevel.size(); j++){
							String strLevelId = alLevel.get(j);
							double dblAmountTotal = uF.parseToDouble(hmEarningTotal.get(strLevelId));
							
						%>
							<td align="right" class="paddingRight20" style=" border-bottom:solid 1px #000;"><%=dblAmountTotal %></td>
							<%		
						}
						%>
				</tr>
	
				<tr>			
					<td colspan="<%=(nLevel+1) %>">&nbsp;</td>
				</tr>	
				
				<tr>			
					<td colspan="<%=(nLevel+1) %>" style="text-align:left; border-bottom:solid 1px #000;"><strong>Deduction</strong></td>
				</tr>	
				<%
				Map<String,String> hmDeductionTotal = new HashMap<String,String>(); 
				for(int i=0; i<alDeduction.size(); i++){
					String strSalaryHeadId = alDeduction.get(i);
				%>
					<tr>
						<td style="text-align:left;border-bottom:dashed 1px #ccc;" nowrap="nowrap"><%=uF.showData(hmhead.get(strSalaryHeadId),"")%></td>
						<%
						for(int j=0; j<alLevel.size(); j++){
							String strLevelId = alLevel.get(j);
							double dblAmount = uF.parseToDouble(hmAmount.get(strSalaryHeadId+"_"+strLevelId));
							double dblDeduction = uF.parseToDouble(hmDeductionTotal.get(strLevelId));
							dblDeduction += dblAmount;
							hmDeductionTotal.put(strLevelId,""+dblDeduction);
						%>
							<td align="right" class="paddingRight20" style="border-bottom:dashed 1px #ccc"><%=dblAmount %></td>
							<%		
						}
						%>
					</tr>
				<%} %>
				<tr>			
					<td style="text-align:left; border-bottom:solid 1px #000;"><strong>Deduction Total</strong></td>
					<%
						for(int j=0; j<alLevel.size(); j++){
							String strLevelId = alLevel.get(j);
							double dblAmountTotal = uF.parseToDouble(hmDeductionTotal.get(strLevelId));
							
						%>
							<td align="right" class="paddingRight20" style="border-bottom:solid 1px #000;"><%=dblAmountTotal %></td>
							<%		
						}
						%>
				</tr>
			</table>
		</div>				
</div>

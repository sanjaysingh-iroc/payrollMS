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
	document.frm_professionaltax.exportType.value='';
	document.frm_professionaltax.submit();
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

table {
    font-family: arial, sans-serif;
    border-collapse: collapse;
    width: 100%;
}

td, th {
    border: 1px solid #dddddd;
    padding: 8px;
}
</style>

<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

Map<String, Map<String, String>> hmPTSlab = (Map<String, Map<String, String>>) request.getAttribute("hmPTSlab"); 
if(hmPTSlab == null) hmPTSlab = new LinkedHashMap<String, Map<String, String>>();
Map<String, Map<String, String>> hmPTDetails =  (Map<String, Map<String, String>>) request.getAttribute("hmPTDetails"); 
if(hmPTDetails == null) hmPTDetails = new LinkedHashMap<String, Map<String, String>>();


double totalEmp=0.0d;
double totaltaxpayed=0.0d;
double dblTaxAmt = 0.0d;

%>
    <div class="leftbox reportWidth">
		<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
			<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
				<%=(String)request.getAttribute("selectedFilter") %>
			</p>
			<div class="content1" style="height: 170px;">
				<s:form name="frm_professionaltax" action="ProfessionalTaxReport" theme="simple">
					<s:hidden name="exportType"></s:hidden>
					<div style="float: left; width: 100%;">
						<div style="float: left; margin-top: 10px;">
							<img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/filter_icon.png">
						</div>
						
						<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Organization</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="orgList" key="" onchange="submitForm('1')" />
			            </div>
			            <div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">State</p>
							<s:select name="f_state" listKey="stateId" listValue="stateName" onchange="submitForm('2')" list="stateList" key=""  headerKey="" headerValue="Select State"/>
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
		
		<div style="margin:0 auto; width:100%; text-align:center; overflow:hidden;">
			<div style="clear:both;">
				<div style="overflow:hidden; margin:0 auto; width:94%;">
					 <table border="0" style="width:100%;" cellpadding="5" align="center" class="lt">
						<thead>
							<tr>
								<th>Slab</th>
								<th>Professional Tax Rate</th>
								<th>No. of Employees</th>
								<!-- <th>PT Tax Payable</th> -->
								<th>Amount of Tax deducted</th>
							</tr>
						</thead>
						<%
						 Iterator<String> it = hmPTSlab.keySet().iterator();
				        int i = 0;
				       
				        while(it.hasNext()){
				        	String strAmount = it.next();
				        	Map<String, String> hmPTSlabDetails = hmPTSlab.get(strAmount);
				        	Map<String, String> hmSalPT = hmPTDetails.get(strAmount);
				        	if (hmSalPT == null) hmSalPT = new HashMap<String, String>();
				        	
				        	String strMsg = "";
				        	String strRate = "";
				        	String strTotalAmt = uF.showData(hmSalPT.get("TOTAL_AMOUNT"), "");
				        	dblTaxAmt += uF.parseToDouble(hmSalPT.get("TOTAL_AMOUNT"));
				        	totalEmp+=uF.parseToDouble(hmSalPT.get("EMP_COUNT"));
				        	if(i==0){
				        		strMsg = "Up to "+ hmPTSlabDetails.get("INCOME_TO");
				        		strRate = ""+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_PAYCYCLE")));
				        		strTotalAmt = "0.00";
				        	} else if(i == hmPTSlab.size()-1){
				        		strMsg = hmPTSlabDetails.get("INCOME_FROM")+" and above";
				        		strRate = ""+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_PAYCYCLE")));
				        	} else {
				        		strMsg = hmPTSlabDetails.get("INCOME_FROM")+" to "+hmPTSlabDetails.get("INCOME_TO");
				        		strRate = ""+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_PAYCYCLE")));
				        	}
						%>
						<tr>
							<td><%=strMsg %></td>
							<td><%=strRate %></td>
							<td><%=uF.showData(hmSalPT.get("EMP_COUNT"), "0.00") %></td>
							<%-- <td ><%=uF.showData(hmSalPT.get("EMP_COUNT"), "0.00") %></td> --%>
							<td><%=uF.showData(hmSalPT.get("TOTAL_AMOUNT"), "0.00") %></td>
						</tr>
						<%
							i++;
				        } 
				        %>
				        <tr>
							<td >Total</td>
							<td > </td>
							<td ><%=totalEmp%></td>
							<%-- <td ><%= totaltaxpayed%></td> --%>
							<td ><%=dblTaxAmt %></td>
						</tr>
					</table> 
				</div>
				
			</div>
			
		</div>
		</div>
	</div>
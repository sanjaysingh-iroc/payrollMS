<div id="salDiv">
	<%@page import="java.util.ArrayList"%>
	<%@page import="java.util.Arrays"%>
	<%@page import="java.util.List"%>
	<%@page import="com.konnect.jpms.util.IConstants"%>
	<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
	<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 
	
	
	<%
		UtilityFunctions uF = new UtilityFunctions();
		java.util.List couterlist = (java.util.List)request.getAttribute("reportList");
		if(couterlist == null) couterlist = new java.util.ArrayList();
		
		String nEarningCnt = (String) request.getAttribute("nEarningCnt");
      	String displayFlag = (String) request.getAttribute("displayFlag");
      	
	%>
	<style>
	.crdb_details
	{
	  width:97%;
	  border:#a4a4a4 solid 1px;
	  height:auto;
	  float:left;
	  margin:10px;
	  padding:10px 10px 10px 10px;
	  -moz-border-radius:5px;
	  -webkit-border-radius:5px;
	   border-radius:5px;
	}
	
	.credit
	{
	  width:49%;
	  height:auto;
	  float:left;
	  border-right:#489BE9 solid 1px;
	  margin:0px 5px 0px 0px;
	  padding:5px 10px 5px 5px;
	 
	}
	
	.deduction
	{
	width:47%;
	  height:auto;
	  float:left;
	 /* border:#FF0000 solid 1px;*/
	  padding:5px;
	  
	}
	
	
	.details_lables .row
	{
	  width:auto;
	  /*border:#009966 solid 1px;*/
	  float:left;
	  margin:0px 0px 10px 0px;
	}
	/* .heading h3
	{
		margin: 0 0 10px;
		padding:0px 0px 10px 0px;
	    border-bottom: 2px solid #EDEBEF;
	    color: #000000;
	    font-family: verdana,arial,sans-serif;
	    font-size: 18px;
		text-align:center;
	} */
	
	.heading1 {
	    background-color: #EFEFEF;
	    background-position: 10px 6px;
	    background-repeat: no-repeat;
	    cursor: pointer;
	    text-shadow: 0 1px 0 #FFFFFF;
	    padding-top: 5px; 
	}
	
	.heading1 h3
	{
		margin: 0 0 10px;
		padding:0px 0px 10px 0px;
	    border-bottom: 2px solid #EDEBEF;
	    color: #000000;
	    font-family: verdana,arial,sans-serif;
	    font-size: 18px;
		text-align:center;
	}
	
	.details_lables
	{
	 width:auto;
	 height:auto;
	/* border:#0000CC solid 1px;*/
	 float:left;
	}
	
	
	
	.col1
	{
	   width:150px; 
	 /* border:#00CC33 solid 1px;*/
	  float:left;
	  text-align:right;
	}
	
	.col2
	{
	float:left;
	/*text-align:left;*/
	width:auto;
	margin:0px 0px 0px 10px;
	
	}
	
	.buttons
	{
	  float:left;
	  width:310px;
	  text-align:center;
	 /* border:#FF9900 solid 1px;*/
	  margin:10px 0px 10px 0px;
	}
	
	h4
	{
	 margin:0px;
	/*  color:#666666; */
	}
	
	.netvalue
	{
	  float:right;
	  margin:10px 5px 10px 5px;
	  font-size:22px;
	}
	
	.tdDashLabel {
	    color: #477199;
	    font-size: 14px;
	}
	
	.tdDashLabel_net
	{
	   color: #298CE9;
	    font-family: verdana,arial,helvetica,sans-serif;
	    font-size: 18px;
	    padding: 3px;
	}
	
	#popup_deduction label {
	    color: #FFFFFF;
	    text-align: left;
	}
	
	</style>
	
	<script type="text/javascript">
	$(function() {
		//====start parvez on 03-07-2021===== 
		$("#idEffectiveDate" ).datepicker({format: 'dd/mm/yyyy', changeYear: true});
		//====end parvez on 03-07-2021=====
			//alert("loading .....");
		changeLabelValuesE('1', '');
	});

	function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
	
	var roundOffCondition='<%=(String)request.getAttribute("roundOffCondition") %>';

	function changeLabelValuesE(id, type) {
		//alert('id ===>> ' + id);
		if(id == '301' && (type=='' || type!='MZ')) {
			var candidateID = document.getElementById("candidateID").value;
			//alert("candidateID ===>> " + candidateID);
			var recruitId = document.getElementById("recruitId").value;
			var ctcAmt = document.getElementById("301").value;
			//alert("ctcAmt ===>> " + ctcAmt);
			getContent('salDiv','SalaryRenegotiatePopup.action?candidateID='+candidateID+'&recruitId='+recruitId+'&ctcAmt='+ctcAmt);
		} else {
			var reimbursementCTC = '';
			var reimbursementCTCOptional = '';
			if(document.getElementById("reimbursementCTC")) {
				reimbursementCTC = document.getElementById("reimbursementCTC").value;
			}
			if(document.getElementById("reimbursementCTCOptional")) {
				reimbursementCTCOptional = document.getElementById("reimbursementCTCOptional").value;
			}
			var salHeadsAndAmt = ",";
			//alert('salHeadsAndAmt ===>> ' + salHeadsAndAmt);
			<%  
				List<String> alContributeHeads = new ArrayList<String>();
				StringBuilder sbContributeHeads = new StringBuilder();
				for (int i=0; i<couterlist.size(); i++) {
					List<String> cinnerlist = (List<String>)couterlist.get(i);
					if(cinnerlist.get(4)!=null && (cinnerlist.get(4)).trim().equals("P") && cinnerlist.get(14) != null 
						&& !cinnerlist.get(14).trim().equals("") && !cinnerlist.get(14).trim().equalsIgnoreCase("NULL") && cinnerlist.get(14).trim().length() > 0) {
						List<String> al = Arrays.asList(cinnerlist.get(14).trim().split(","));
						int nAl = al != null ? al.size() : 0;
			%>	
					var formula = "";
					var cnt = 0;
					var isReimbursementCTC = new Boolean(false);
					//alert('isReimbursementCTC ===>> ' + isReimbursementCTC);
			<%		for(int j = 0; j < nAl; j++) {
						String str = al.get(j);
						if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")) {
							boolean isInteger = uF.isInteger(str.trim());
							if(isInteger) {
								if((!alContributeHeads.contains(IConstants.EMPLOYER_EPF+"") && uF.parseToInt(str) == IConstants.EMPLOYER_EPF) 
									|| (!alContributeHeads.contains(IConstants.EMPLOYER_ESI+"") && uF.parseToInt(str) == IConstants.EMPLOYER_ESI) 
									|| (!alContributeHeads.contains(IConstants.EMPLOYER_LWF+"") && uF.parseToInt(str) == IConstants.EMPLOYER_LWF)) {
									alContributeHeads.add(str);
									sbContributeHeads.append(str+",");
								}
								
								if(uF.parseToInt(str.trim()) == IConstants.REIMBURSEMENT_CTC) {
			%>
									formula += ""+(parseFloat(getRoundOffValue(reimbursementCTC)) + parseFloat(getRoundOffValue(reimbursementCTCOptional)));
									cnt++;
									isReimbursementCTC = true;
			<%					} else {
			%>
									var sHeadDisplay = "isDisplay_"+<%=str.trim()%>;
									if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
										var sHeadId = ""+<%=str.trim()%>;
										var sAnnualHeadId = "annual_"+<%=str.trim()%>;
										if(document.getElementById(sAnnualHeadId)){
											var frmlSalHeadAmt = document.getElementById(sAnnualHeadId).value;
											var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
											if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
												frmlSalHeadAmt = maxCapAmt;
											}
											formula += ""+parseFloat(getRoundOffValue(frmlSalHeadAmt));
											//formula += ""+ parseFloat(getRoundOffValue(document.getElementById(sAnnualHeadId).value));
											cnt++;
										} else if(document.getElementById(sHeadId)){
											var frmlSalHeadAmt = document.getElementById(sHeadId).value;
											var maxCapAmt = document.getElementById(sHeadId+'_max_cap_amount').value;
											if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
												frmlSalHeadAmt = maxCapAmt;
											}
											formula += ""+parseFloat(getRoundOffValue(frmlSalHeadAmt));
											//formula += ""+parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));
											cnt++;	
										}
									} else {
										formula += ""+parseFloat(getRoundOffValue('0'));
										cnt++;
									}
			<%					}
							} else {
			%>			
								formula += '<%=str.trim() %>'; 	
			<%				}
						}
					}
				%>
				//alert('formula ===>> ' + formula);
					var total = 0;
					if(cnt > 0 && formula.trim() != '') {
						var formulaCal = eval(formula);
						var percentage = '<%=cinnerlist.get(7) %>';
						var maxCapAmt = '<%=cinnerlist.get(16) %>';
						total = (parseFloat(percentage) * parseFloat(formulaCal))/100;
						total = isNaN(total) ? 0 : total;
						if(parseFloat(total) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
							total = maxCapAmt;
						}
						if(parseFloat(total) <0) {
							total=0;
						}
					}
					//alert('total ===>> ' + total);
					document.getElementById(""+<%=cinnerlist.get(1)%>).value = getRoundOffValue(total);
					document.getElementById("tempValue_"+<%=cinnerlist.get(1)%>).value = getRoundOffValue(total);
					
				<% } %>
			<% } %>

		calculateTotalEarningandDeduction();
		}
	}
	
	
	function calculateTotalEarningandDeduction(){
		var total = 0;
		var totalD = 0;
	    <%  
			for (int j=0; j<couterlist.size(); j++) {
				java.util.List innerlist = (java.util.List)couterlist.get(j); 
				if(uF.parseToInt(""+innerlist.get(1)) == IConstants.CTC){
					continue;
				}
		%>
				var sSalED = ""+'<%=innerlist.get(3)%>';
				if(sSalED == 'E'){
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(1)%>+"_"+<%=innerlist.get(5)%>;
					var sHeadDisplay1 = "isDisplay_"+<%=innerlist.get(1)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true) 
							|| (document.getElementById(sHeadDisplay1) && document.getElementById(sHeadDisplay1).checked==true)) {
						var sHeadId = "lblValue_"+<%=innerlist.get(1)%>+"_"+<%=innerlist.get(5)%>;
						var sHeadId1 = ""+<%=innerlist.get(1)%>;
						if(document.getElementById(sHeadId)){
							var frmlSalHeadAmt = document.getElementById(sHeadId).value;
							var maxCapAmt = document.getElementById(sHeadId1+'_max_cap_amount').value;
							if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								frmlSalHeadAmt = maxCapAmt;
							}
							total =  parseFloat(total) + parseFloat(getRoundOffValue(frmlSalHeadAmt));
							//total =  parseFloat(total) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
						} else if(document.getElementById(sHeadId1)){
							var frmlSalHeadAmt = document.getElementById(sHeadId1).value;
							var maxCapAmt = document.getElementById(sHeadId1+'_max_cap_amount').value;
							if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								frmlSalHeadAmt = maxCapAmt;
							}
							total =  parseFloat(total) + parseFloat(getRoundOffValue(frmlSalHeadAmt));
							//total =  parseFloat(total) + parseFloat(getRoundOffValue(document.getElementById(sHeadId1).value));
						}
					}
				} else if(sSalED == 'D'){
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(1)%>+"_"+<%=innerlist.get(5)%>;
					var sHeadDisplay1 = "isDisplay_"+<%=innerlist.get(1)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true) 
							|| (document.getElementById(sHeadDisplay1) && document.getElementById(sHeadDisplay1).checked==true)) {
						var sHeadId = "lblValue_"+<%=innerlist.get(1)%>+"_"+<%=innerlist.get(5)%>;
						var sHeadId1 = ""+<%=innerlist.get(1)%>;
						if(document.getElementById(sHeadId)){
							var frmlSalHeadAmt = document.getElementById(sHeadId).value;
							var maxCapAmt = document.getElementById(sHeadId1+'_max_cap_amount').value;
							if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								frmlSalHeadAmt = maxCapAmt;
							}
							totalD =  parseFloat(totalD) + parseFloat(getRoundOffValue(frmlSalHeadAmt));
							//totalD =  parseFloat(totalD) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
						} else if(document.getElementById(sHeadId1)){
							var frmlSalHeadAmt = document.getElementById(sHeadId1).value;
							var maxCapAmt = document.getElementById(sHeadId1+'_max_cap_amount').value;
							if(parseFloat(frmlSalHeadAmt) > parseFloat(maxCapAmt) && parseFloat(maxCapAmt)>0) {
								frmlSalHeadAmt = maxCapAmt;
							}
							totalD =  parseFloat(totalD) + parseFloat(getRoundOffValue(frmlSalHeadAmt));
							//totalD =  parseFloat(totalD) + parseFloat(getRoundOffValue(document.getElementById(sHeadId1).value));
						}
					}
				}
		<%}%>
		document.getElementById("total_earning_value").innerHTML = getRoundOffValue(Math.round(parseFloat(total)));
		//document.getElementById("hide_total_earning_value").value = getRoundOffValue(Math.round(parseFloat(total)));
		document.getElementById("total_deduction_value").innerHTML = getRoundOffValue(Math.round(parseFloat(totalD)));
	}
		
		function removeField(id1) {
			
			if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
				window.location="EmployeeSalaryDetails.action?id=" +id1; 
	  			return true;
	  		}
	  		else {
	  			return false;
	  		}
		}
		
		
		<%  
		String CCID= (String) request.getAttribute("CCID");
		String ccName = (String) request.getAttribute("CCNAME");
		String EMPNAME = (String)session.getAttribute("EMPNAME_P");
		
		if(EMPNAME==null)
			EMPNAME = (String)request.getAttribute("EMPNAMEFORCC");
		
		%>	
		
	var cnt;
	var oldValues = new Array();
	
	<%  for (int i=0; i<couterlist.size(); i++) {
			java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
	%>
			oldValues[<%=cinnerlist.get(1)%>] = "<%=cinnerlist.get(8)%>"; 
			
	<%}%>
	
	function makeZeroOnUncheck(displayId) {
		
		var headId = displayId.substring(displayId.indexOf("_")+1, displayId.length);
		
		if(!document.getElementById(displayId).checked) {
			
			if(document.getElementById("lblValue_"+headId)) {
				oldValues[headId.split("_")[0]] = document.getElementById("lblValue_"+headId).value;
				//document.getElementById("lblValue_"+headId).value = 0;
				changeLabelValuesE("lblValue_"+headId, 'MZ');
			} else if(document.getElementById(headId)) {
				oldValues[headId] = document.getElementById(headId).value;
				//document.getElementById(headId).value = 0;
				changeLabelValuesE(headId, 'MZ');
			}
		
		} else {
			
			if(document.getElementById("lblValue_"+headId)) {
				document.getElementById("lblValue_"+headId).value = oldValues[headId.split("_")[0]];
				changeLabelValuesE("lblValue_"+headId, 'MZ');
			} else if(document.getElementById(headId)) {
				document.getElementById(headId).value = oldValues[headId];
				changeLabelValuesE(headId, 'MZ');
			}
			
		}
	}
	
	function getRoundOffValue(val) {
		var roundOffVal = 0;
		if(parseInt(roundOffCondition) == 1){
			roundOffVal = parseFloat(val).toFixed(1);
		} else if(parseInt(roundOffCondition) == 2){
			roundOffVal = parseFloat(val).toFixed(2);
		} else {
			roundOffVal = Math.round(parseFloat(val));
		}
		
		return roundOffVal;
	}
		
	$("#frm_candisalrenegotiate").submit(function(event){
  		event.preventDefault();
  		
  		var recruitId = document.getElementById("recruitId").value;
  		
  		var form_data = $("#frm_candisalrenegotiate").serialize();
		$.ajax({
			type :'POST',
			url  :'SalaryRenegotiatePopup.action',
			data :form_data,
			cache:true/* ,
			success : function(result) {
				$("#subSubDivResult").html(result);
			} */
		});
		
		$.ajax({
			url: 'Offers.action?recruitId='+recruitId,
			cache: true,
			success: function(result){
				$("#subSubDivResult").html(result);
	   		}
		});
		
  	});
		
		</script>
	
	     <%--  <div class="pagetitle">
	        <span>Salary Details <%= (EMPNAME!=null) ?" of "+EMPNAME: "" %><%= (ccName!=null) ? " for "+ ccName:""%></span>
	      </div> --%>
	    
	      <!-- <div class="leftbox reportWidth"> -->
	      
				<div class="crdb_details">
				
					<s:hidden name="curr_short" id="curr_short"></s:hidden>
				
					<form id="frm_candisalrenegotiate" name="frm_candisalrenegotiate" action="SalaryRenegotiatePopup.action" method="post" enctype="multipart/form-data">
						<div class="row row_without_margin">
							<div class="col-lg-4 col-md-4 col-sm-4 autoWidth">
								<span style="vertical-align: top;">Remark :&nbsp;</span><textarea id="renegotiateRemark" name="renegotiateRemark" rows="2" cols="45" required></textarea>
							</div>
							<div class="col-lg-4 col-md-4 col-sm-4 autoWidth">
								<span style="vertical-align: top;">Effective From :&nbsp;</span><s:textfield id="idEffectiveDate" name="effectiveDate"></s:textfield>
							</div>
							<div id="div_update" class="col-lg-4 col-md-4 col-sm-4 autoWidth">
								<input type="submit" class="btn btn-primary" name="update" value="Update"/>
							</div>
						</div>
						<s:hidden name="candidateID" id="candidateID" />
						<s:hidden name="CCID" />
						<s:hidden name="recruitId"  id="recruitId" />
						<s:hidden name="tableMode" />
						<s:hidden name="salryUpdate" value="Update"/>
						
						<div class="clr"></div>
						
						<div class="credit" id="div_earning">
			
						    <div class="heading1">
				      			<h3>EARNING DETAILS</h3>
						    </div>
					    	
					    	<div class="details_lables" >
					    	
								<% 	boolean isBenefit = false;
									boolean isGRoss = false;
									//couterlist = (java.util.List)request.getAttribute("reportList"); 
									//System.out.println("couterlist ===>> " + couterlist);
								%>
				 				<%
				 				for (int i=0; i<couterlist.size(); i++) { 
					 				%>
					 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i);
					 				if(uF.parseToInt((String)cinnerlist.get(1))==IConstants.GROSS){
					 					isGRoss = true;
					 				}
					 				%>
									
									<% if(cinnerlist.get(3).equals("E")) { %>	
										
											<div class="row">
											
												<input type="hidden" name="emp_salary_id" value='<%= cinnerlist.get(0) %>'></input>
												<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(1) %>'></input>
												
											 	<div class="col1 tdDashLabel" <%=((uF.parseToInt((String)cinnerlist.get(1))==IConstants.GROSS)?"color: #298CE9;font-size: 18px;":"")%>>
									      	 		<label id="lbl"><%= cinnerlist.get(2) %>:</label>	
						      	 				</div>
						      	 				
						      	 				<div class="col2" id="col2">
						      	 					<input type="hidden" id="<%=cinnerlist.get(1)%>_max_cap_amount" value="<%=cinnerlist.get(16) %>">
						      	 					<input type="text" id="<%=cinnerlist.get(1)%>" name="salary_head_value" value="<%=cinnerlist.get(8) %>" 
						       							style="width:60px !important;text-align:right" onchange="changeLabelValuesE(this.id, '')" maxlength="15" onkeypress="return isNumberKey(event)" />
						       						<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(1) %>" value="<%=cinnerlist.get(8) %>">
							       					
							       					<% if(uF.parseToBoolean((String)cinnerlist.get(12))) { %>
								       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" 
								       							onclick="makeZeroOnUncheck(this.id)" value="true" checked="checked">	
								       				<%} else if(uF.parseToBoolean(displayFlag)) {%>
								       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" 
								       							onclick="makeZeroOnUncheck(this.id)" value="true" checked="checked">
								       				<% } else { %>
								       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=(String)cinnerlist.get(1)%>" 
								       							onclick="makeZeroOnUncheck(this.id)" value="true">
								       				<% } %>
								       				<% if(cinnerlist.get(4)!=null && ((String)cinnerlist.get(4)).trim().equals("P")) { %>
								       					<label id="lbl_amount_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(7) %></label>
										      	 	 	% of <label id="lblMulCal_<%=(String)cinnerlist.get(1) %>">[<%=uF.showData(""+cinnerlist.get(15),"") %>]</label>
								       				<%} %>
							       					
							       				</div>
						       				</div>
						       				<div class="clr"></div>
					       					<% } %>
					       				
				   					<% } %>
			   					 
									<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9; <%=((!isGRoss)?"":"display:none;")%>">
										<label name="total_gross" id="lbl_total_gross">Total Gross Salary: </label>
										<label name="total_earning_value" id= "total_earning_value" style="color: green;">0</label>
										<!-- <input type="hidden" name="hide_total_earning_value" id="hide_total_earning_value" /> -->
									</div>
							</div>
							
						    <div class="clr"></div>
			  
			  			</div> 
			 
						<div class="deduction" id="div_deduction">
			  
							<div class="heading1">
								<h3>DEDUCTION DETAILS</h3>
							</div>
			    
				         	<div class="details_lables" >
				 				<%  int count = 0;
				 					for (int i=0; i<couterlist.size(); i++) { %>
					 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
									
									<%  
										if(cinnerlist.get(3).equals("D")) { %>	
										
											<div class="row">
											
											<%if(isGRoss && count==0){count++; %>
											<div class="row" style="width:100%; float: left; font-size: 18px; color: #298CE9;">
												<label >Total Deduction:</label>
												<label id= "total_deduction_value" style="color: green;">0</label>
											</div>
											<%} %>
										
												<input type="hidden" name="emp_salary_id" value='<%= cinnerlist.get(0) %>'></input>
												<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(1) %>'></input>
											 	<div class="col1 tdDashLabel">
									      	 		<label id="lbl"><%= cinnerlist.get(2) %>:</label>	
						      	 				</div>
						      	 				
						      	 				<div class="col2" id="col2">
										      	 	<input type="hidden" id="<%=cinnerlist.get(1)%>_max_cap_amount" value="<%=cinnerlist.get(16) %>">
						       						<input type="text" id="<%=cinnerlist.get(1)%>" name="salary_head_value" value="<%= cinnerlist.get(8) %>"
						       							style="width:60px !important;text-align:right" onchange="changeLabelValuesE(this.id, '')" maxlength="15" onkeypress="return isNumberKey(event)" />
							       					<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(1) %>" value="<%=cinnerlist.get(8) %>">
							       					
							       					<% if(uF.parseToBoolean((String)cinnerlist.get(12))) { %>
								       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>"
								       							value="true" checked="checked" onclick="makeZeroOnUncheck(this.id);">	
								       				<%} else if(uF.parseToBoolean(displayFlag)) {%>
								       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>"
								       							value="true" checked="checked" onclick="makeZeroOnUncheck(this.id);">
								       				<% } else { %>
										       			<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(1)%>" id="isDisplay_<%=cinnerlist.get(1)%>" 
										       					onclick="makeZeroOnUncheck(this.id);" value="true">
								       				<%}%>
							       					<% if(cinnerlist.get(4)!=null && ((String)cinnerlist.get(4)).trim().equals("P")) { %>
								       					<label id="lbl_amount_<%=(String)cinnerlist.get(1)+"_"+(String)cinnerlist.get(5)%>"><%= cinnerlist.get(7) %></label>
										      	 	 	% of <label id="lblMulCal_<%=(String)cinnerlist.get(1) %>">[<%=uF.showData(""+cinnerlist.get(15),"") %>]</label>
								       				<%} %>
							       				</div>
						       				</div>
						       				<div class="clr"></div>
					       					<%}%>
					       				
				   					<%} %>
			   					
									<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;<%=((!isGRoss)?"":"display:none;")%>">
										<label >Total Deduction:</label>
										<label id= "total_deduction_value" style="color: green;">0</label>
									</div>
								
							    <div class="clr"></div>
			   				</div>
						</div>
			       	</form>
			       	
			  <div class="clr"></div>
			  <div class="netamount" style="display:none;">
			     
			      <div class="netvalue"> 
			      		<label class="tdDashLabel_net" id="lblNetAmountRs">Net Amount Rs:</label> 
			      		<s:label id="lbl_net_amount" name="lbl_net_amount" cssStyle="color: green;"></s:label>
			      </div>
			       
			  </div>
			
			</div>
			
	<!-- </div> -->
	
	<script>
	
	</script>
</div>
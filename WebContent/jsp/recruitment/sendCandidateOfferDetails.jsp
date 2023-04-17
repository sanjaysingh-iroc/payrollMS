
<div id="divResult">
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 
<%@page import="com.konnect.jpms.util.UtilityFunctions,java.util.*"%>

<!-- <link rel="stylesheet" href="scripts/timeline/css/reset.css"> -->
<!-- CSS reset -->
<!-- <link rel="stylesheet" href="scripts/timeline/css/style.css"> -->

<% 
	UtilityFunctions uF = new UtilityFunctions();
	String candidateID = (String) request.getAttribute("CandID");
	String recruitId = (String) request.getAttribute("recruitId");

	List<List<String>> couterlist = (List<List<String>>)request.getAttribute("reportList");
	if(couterlist == null) couterlist = new ArrayList<List<String>>();
%>

<style>
.crd b_details 
{
  width:95%;
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
  width:47%;
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

#salayDiv .details_lables
{
 width:auto;
 height:auto;
/* border:#0000CC solid 1px;*/
 float:left;
}



#salayDiv .row
{
  width:auto;
  /*border:#009966 solid 1px;*/
  float:left;
  margin:0px 0px 10px 0px;
}

.col1
{
   width:60px; 
 /* border:#00CC33 solid 1px;*/ 
  float:left;
  text-align:right;
}

.col2
{
/* float:left; */
/*text-align:left;*/
width:auto;
margin:0px 0px 0px 82px;

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
 /* color:#666666; */
}

.netvalue
{
  float:right;
  margin:10px 5px 10px 5px;
  font-size:22px;
}

.tdDashLabel {
    color: #298CE9;
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 12px;
    padding: 3px;
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


$("#hrsubmit").click(function(){
	$("#frmIntCommentHR").find('.validateRequired').filter(':hidden').prop('required',false);
	$("#frmIntCommentHR").find('.validateRequired').filter(':visible').prop('required',true);
});

$(function() {
    $("#joiningdate").datepicker({format: 'dd/mm/yyyy', yearRange: '1980:2020', changeYear: true});
});



function previewOfferLetter(candidateId) {

	var form_data = $("#frmIntCommentHR").serialize();
	var recruitId = document.getElementById("strJD").value;
	//alert("form_data ===>> " + form_data);
	$.ajax({
		type :'POST',
		url  :'CandidateMyProfilePopup.action',
		data :form_data+"&preveiwOffer=Submit",
		cache:true
	});
	window.location = "CandidateMyProfilePopup.action?operation=PREVIEW&CandID="+candidateId+"&recruitId="+recruitId;

}

function changedsalaryDetails(recruitId) {
	
	//var strJDValue = document.getElementById("strJD").value;
	var candidateId = '<%=candidateID%>';
	//alert("recruitId ===>> " + recruitId +" -- candidateId ===>> " + candidateId);
	var divResult = 'divResult';
	$('#divResult').html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
			url: "sendCandidateOffer.action?candidateId="+candidateId+"&strJD="+recruitId,
  			cache : false,
		    success : function(data) {
		    	$('#divResult').html(data);
		    }
	    });
	
}

</script>

    <%--  <div class="pagetitle">
        <span>Salary Details <%= (EMPNAME!=null) ?"of "+EMPNAME: "" %><%= (ccName!=null) ? " for "+ ccName:""%></span>
      </div> --%>
    
      <!-- <div class="leftbox reportWidth"> -->
     
   <s:form name="frmIntCommentHR" id="frmIntCommentHR" theme="simple" action="sendCandidateOffer" method="post">
	<s:hidden name="candidateId" id="candidateId"></s:hidden>
	<s:hidden name="recruitId" id="recruitId"></s:hidden>
	<s:hidden name="CandID" id="CandID"></s:hidden>
	<s:hidden name="form" id="form" />
   <table class="table">
	<tbody>

	<tr id="jobTR" style="display: table-row;">
	 <td>
	 	Job Code:<sup>*</sup> 
		<s:select name="strJD" list="JDList" listKey="strJDId" id="strJD" listValue="strJDName" headerKey="" required="true" onchange="changedsalaryDetails(this.value);"/>
	</td>
	</tr>
      <tr id="joiningTR" style="display: table-row;">
			<td>Joining Date:<sup>*</sup> 
				<input name="joiningdate" value="" id="joiningdate" class="validateRequired text-input" style="width:105px" type="text">
			</td>
		</tr>
		
      <tr id="ctcDisplayTR" style="display: table-row;">
		<td>
			<s:action name="CandidateSalaryDetails" executeResult="true">
				<s:param name="CandID" value="#CandID"/>
				<s:param name="recruitId" value="#recruitId"/>
			</s:action>
			
			<%-- <div class="crdb_details" id="salayDiv">
				<s:hidden name="curr_short" id="curr_short"></s:hidden>
				
					<div class="clr"></div>
					<div style="float: left; width: 100%">
					
					<input type="checkbox" name="disableSalaryStructure" id="disableSalaryStructure" onclick="checkSalaryHeadDisable();"/>
						Calculation from level/grade based structure is disabled
					</div>
					
					<div class="credit" id="div_earning">
		
					    <div class="heading1">
			      			<h3>EARNING DETAILS</h3>
					    </div>
				    	
				    	<div>
				    	
							<% for (int i=0; i<couterlist.size(); i++) { 
			 					java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			 					if(cinnerlist.get(2).equals("E")) {
			 				%>
							
								<div class="row">
									<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
								 	<div class="col1 tdDashLabel"><label id="lbl"><%= cinnerlist.get(1) %>:</label>	
			      	 				</div>
			      	 				
			      	 				<div class="col2" id="col2">
			      	 				
			       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>" style="width:90px !important; text-align:right" onchange="changeLabelValuesE(this.id)" maxlength="15" onkeypress="return isNumberKey(event)" />
				       					<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>">
				       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)%>" value="true" checked="checked" onclick="makeZeroOnUncheck(this.id)">
			       						
			       						<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equals("P")) { %>
					       					<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label> % of <label id="lblMulCal_<%=(String)cinnerlist.get(0) %>">(<%=uF.showData(""+cinnerlist.get(8),"") %>)</label>
					       				<%} %>	
				       				</div>
			       				</div>
			       				<div class="clr"></div>
			       				
		       					<%}%>
		       				
		  					<%}%>
		  					
							<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
								<label id="lbl_total_gross">Total Gross Salary:</label>
								<label id= "total_earning_value" style="color: green;">0</label>
								<input type="hidden" name="hide_total_earning_value" id="hide_total_earning_value" />
							</div>
						</div>
						
					    <div class="clr"></div>
		  
		  			</div>
		  			
					<div class="deduction" id="div_deduction">
		  
						<div class="heading1">
							<h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DEDUCTION DETAILS</h3>
						</div>
		    
			         	<div>
			 				<% for (int i=0; i<couterlist.size(); i++) {
								java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
								
								if(cinnerlist.get(2).equals("D")) {
							%>	
									<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
									
									<div class="row">
									
									 	<div class="col1 tdDashLabel">
							      	 		<label id="lbl"><%= cinnerlist.get(1) %>:</label>	
				      	 				</div>
				      	 				
				      	 				<div class="col2" id="col2">
				       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>" style="width:90px !important; text-align:right" onchange="changeLabelValuesE(this.id)" maxlength="15" onkeypress="return isNumberKey(event)" />
				       						<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>">
				       						<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=cinnerlist.get(0)%>" value="true" checked="checked" onclick="makeZeroOnUncheck(this.id)">
				       						<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equals("P")) { %>
						       					<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label> % of <label id="lblMulCal_<%=(String)cinnerlist.get(0) %>">(<%=uF.showData(""+cinnerlist.get(8),"") %>)</label>
						       				<%} %>	
				       					</div>
				       				</div>
				       				<div class="clr"></div>
				       				 
			       				<%} %>
			  				<%}%>
			  					
							<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
								<label >Total Deduction:</label>
								<label id= "total_deduction_value" style="color: green;">0</label>
							</div>
					
						    <div class="clr"></div>
		   				</div>
					</div>
		       	<!-- </form> -->
		       	
		  	<div class="clr"></div>
		  
		</div> --%>
		
		</td>
		</tr>
		<tr>
			<td style="float: right">
				<input style="margin: 0px; display: inline;" class="btn btn-primary" value="Preview Offer" name="preveiwOffer" id="preveiwOffer" onclick="previewOfferLetter('<%=candidateID %>');" type="button">										
				<!-- <input type="submit" style="margin: 0px" class="btn btn-primary" value="Save Offer" name="saveOffer" id="saveOffer" /> -->
				<input style="margin: 0px;" class="btn btn-primary" value="Save &amp; Send Offer" name="hrsubmit" id="hrsubmit" type="submit">
			</td>
		</tr>
		
	</tbody>
  </table>
  </s:form>
 
		
<!-- </div> -->
</div>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 



<style>

.clr 
{
 clear:both;
}

.input {
width : 100px;
}
 
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
  width:50%;
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

.heading h3
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

.row
{
  width:auto;
  /*border:#009966 solid 1px;*/
  float:left;
  margin:0px 0px 10px 0px;
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
 color:#666666;
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

var checkSalaryHead = '<%=request.getAttribute("checkSalaryHead")%>';

    $(document).ready(function() {
    	
    	//deductProfTax();
    	//changeLabelValue();
    	//changeDeductionLabelValue();
 
    	<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); 
			for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
					
	 		java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
	 		
	 		show_sub_salary_head("newSel_"+<%=cinnerlist.get(0)%>);
	 		
	 		show_sub_salary_head("newSelE");
	 		show_sub_salary_head("newSelD");
	 		
	 	<%}%>
			 		
    	
        $('a.poplight[href^=#]').click(function() {
            var popID = $(this).attr('rel'); //Get Popup Name
            var popURL = $(this).attr('href'); //Get Popup href to define size

            //Pull Query & Variables from href URL
            var query= popURL.split('?');
            var dim= query[1].split('&');
            var popWidth = dim[0].split('=')[1]; //Gets the first query string value

            //Fade in the Popup and add close button
            $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

            //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
            var popMargTop = ($('#' + popID).height() + 80) / 2;
            var popMargLeft = ($('#' + popID).width() + 80) / 2;

            //Apply Margin to Popup
            $('#' + popID).css({
                'margin-top' : -popMargTop,
                'margin-left' : -popMargLeft
            });
            
            //Fade in Background
            $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
            $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies

            return false;
        });

        //Close Popups and Fade Layer
        $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
            $('#fade , .popup_block').fadeOut(function() {
                $('#fade, a.close').remove();  //fade them both out
            });
            return false;
        });
    });
    
    function showCreditDetails() {	
		dojo.event.topic.publish("showCreditDetails");
	} 
    
    
    function show_sub_salary_head(selId) {
    	
    	if(document.getElementById(selId).value == 'A') {
			if(document.getElementById("id_salaryHead_"+selId.split("newSel_")[1])) {
        		document.getElementById("id_salaryHead_"+selId.split("newSel_")[1]).style.display = "none";
			}
	//		if(document.getElementById("id_headAmount_"+selId.split("newSel_")[1])) {
      //  		document.getElementById("id_headAmount_"+selId.split("newSel_")[1]).style.display = "none";
	//		}
        	if(document.getElementById("id_salaryHeadE")) {
        		document.getElementById("id_salaryHeadE").style.display = "none";
        	}
		//	if(document.getElementById("id_headAmountE")) {
		//		document.getElementById("id_headAmountE").style.display = "none";
        //	}
			if(document.getElementById("id_salaryHeadD")) {
        		document.getElementById("id_salaryHeadD").style.display = "none";
        	}
		//	if(document.getElementById("id_headAmountD")) {
		//		document.getElementById("id_headAmountD").style.display = "none";
        //	}
        }
    	
       	if(document.getElementById(selId).value == 'P') {
       		
       		if(document.getElementById("id_salaryHead_"+selId.split("newSel_")[1])) {
           		document.getElementById("id_salaryHead_"+selId.split("newSel_")[1]).style.display = "table-row";
       		}
       		if(document.getElementById("id_headAmount_"+selId.split("newSel_")[1])) {
           		document.getElementById("id_headAmount_"+selId.split("newSel_")[1]).style.display = "table-row";
       		}
           	if(document.getElementById("id_salaryHeadE")) {
        		document.getElementById("id_salaryHeadE").style.display = "table-row";
        	}
			if(document.getElementById("id_headAmountE")) {
				document.getElementById("id_headAmountE").style.display = "table-row";
        	}
			if(document.getElementById("id_salaryHeadD")) {
        		document.getElementById("id_salaryHeadD").style.display = "table-row";
        	}
			if(document.getElementById("id_headAmountD")) {
				document.getElementById("id_headAmountD").style.display = "table-row";
        	}
       	}
    	
    }
    
</script>

<script type="text/javascript">
	
	var gross_lbl_values = new Array();
	var deduction_lbl_values = new Array();
	var expected_deduction = 0;
	
	function changeAllLabelValues() {
		changeLabelValue();
		changeDeductionLabelValue();
	}
	
	function isNumberKey(evt)
	{
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}

	function deductProfTax() {
		
		var sample_salary = document.getElementById("sample_salary").value;
		var prof_tax_value = document.getElementById("txt_Professional_Tax").value;
		
		document.getElementById("lbl_Professionla_Tax").innerHTML = parseInt(sample_salary) * parseInt(prof_tax_value) / 100 ;
		
	}
	
	function changeLabelValue()  {
		
			if(document.getElementById("sample_salary").value.length != 0)
				var sample_value = document.getElementById("sample_salary").value;
			else 
				var sample_value = 0;
			
			var elem = document.getElementById("div_gross").getElementsByTagName("select");
			var total = 0;
			gross_lbl_values = new Array();
			var counter = 0;
			
			//Change label value accorrding to P or A value from select list
			
			for(var i = 0; i < elem.length; i++)
	        {
				if(elem[i].id != "Others_Earning") {
					
		           	if(elem[i].value == 'A') {
		           		
		           		 if(document.getElementById('txt_'+elem[i].id).value.length == 0) {		
		           			gross_lbl_values[counter] = 0;
		           			document.getElementById('lbl_'+elem[i].id).innerHTML =   0;
		           		}else { 
		           			gross_lbl_values[counter] = document.getElementById('txt_'+elem[i].id).value;
		           		 	document.getElementById('lbl_'+elem[i].id).innerHTML =  document.getElementById('txt_'+elem[i].id).value;	
		           		}
		           		 
		           	} else if(elem[i].value == 'P') {
		           		
		           		gross_lbl_values[counter] = ( document.getElementById('txt_'+elem[i].id).value * parseInt(sample_value) ) / 100 ;
		           		
		           		document.getElementById('lbl_'+elem[i].id).innerHTML = 
		           			( document.getElementById('txt_'+elem[i].id).value * parseInt(sample_value) ) / 100 ; 
		           	}
		           	counter++;
				}
	        }
			
			//update total value excluding others value 
			for(var i = 0; i < gross_lbl_values.length; i++)
	        {
	            	total =  parseInt(total) + parseInt(gross_lbl_values[i]);
	        }
			
			//calculate others label value 
			
			if(parseInt(total) <= parseInt(sample_value)) {
			
				document.getElementById("lbl_Others_Earning").innerHTML = parseInt(sample_value) - parseInt(total);
				expected_deduction = 0;
			
			}else {
				
				expected_deduction = - (parseInt(sample_value) - parseInt(total));
				document.getElementById("lbl_Others_Earning").innerHTML = "0";
				
			}
			
			document.getElementById("total_earning_value").innerHTML = parseInt(total) + 
				parseInt(document.getElementById("lbl_Others_Earning").innerHTML);
			
			//Change Net Ammount 
			
			var netAmmount = parseInt(document.getElementById("total_earning_value").innerHTML) - 
			parseInt(document.getElementById("total_deduction_value").innerHTML);
			
			if (netAmmount < 0 ) {
			
				document.getElementById("lbl_net_amount").innerHTML = 0;
			}
			
			else {
				
				document.getElementById("lbl_net_amount").innerHTML = netAmmount;
			}
			 
			
   	}
    
	function changeDeductionLabelValue()  {
		
		var total_earning = document.getElementById("total_earning_value").value;
		var sample_salary = document.getElementById("sample_salary").value;
		var total_deduction_sum;
			
		var elem = document.getElementById("frm_deduction").getElementsByTagName("select");
		var total = 0;
		var elem2 = document.getElementById("frm_deduction");
		var lbls = elem2.getElementsByTagName("label");
		deduction_lbl_values = new Array();
		var counter = 0;
		
 		for(var i = 0; i < elem.length; i++)
        {
 			if(elem[i].id != "Others_Deduction") {
 				
	 			if(elem[i].id != 'sel') {			//to exclude pop-up window selects included in the div
	 				
		           	if(elem[i].value == 'A') {
		           		if(document.getElementById('txt_'+elem[i].id).value.length == 0) {		
		           			deduction_lbl_values[counter] = 0;
		           			document.getElementById('lbl_'+elem[i].id).innerHTML =  0;
		           		}else {
		           		deduction_lbl_values[counter] = document.getElementById('txt_'+elem[i].id).value; 
		           		document.getElementById('lbl_'+elem[i].id).innerHTML =  
		           			document.getElementById('txt_'+elem[i].id).value;
		           		}
		           	}else if(elem[i].value == 'P') {
		           		deduction_lbl_values[counter] = ( document.getElementById('txt_'+elem[i].id).value * parseInt(expected_deduction) ) / 100 ;
		           		document.getElementById('lbl_'+elem[i].id).innerHTML = 
		           			( document.getElementById('txt_'+elem[i].id).value * parseInt(expected_deduction) ) / 100 ;
		           	}
	 			}
	 			counter++;
 			}
        }
		
		for(var i = 0; i < deduction_lbl_values.length; i++)
        {
           	total =  parseInt(total) + parseInt(deduction_lbl_values[i]);
        }

		//calculate others label value 
			
		if(parseInt(total) <= parseInt(expected_deduction)) {
		
			document.getElementById("lbl_Others_Deduction").innerHTML = parseInt(expected_deduction) - parseInt(total);
		
		}else {
			
			// Dont let user save this format.  
			document.getElementById("lbl_Others_Deduction").innerHTML = "0";
		}
		
		document.getElementById("total_deduction_value").innerHTML = parseInt(total) + 
				parseInt(document.getElementById("lbl_Others_Deduction").innerHTML);
		
		//Change Net Ammount 
		
		var netAmmount = parseInt(document.getElementById("total_earning_value").innerHTML) - 
			parseInt(document.getElementById("total_deduction_value").innerHTML);
			
			if (netAmmount < 0 ) {
			
				document.getElementById("lbl_net_amount").innerHTML = 0;
			}
			
			else {
				
				document.getElementById("lbl_net_amount").innerHTML = netAmmount;
			}
		
    }
	
	function saveAll() {
		
		var sample_salary_value = document.getElementById("sample_salary").value;
		var total_earning = document.getElementById("total_earning_value").innerHTML;
		var total_deduction = document.getElementById("total_deduction_value").innerHTML;
		var testValue = 0;
		testValue = parseInt(total_earning) - parseInt(total_deduction);
		
		if(testValue == sample_salary_value) {
			alert('Saving..');
			return true;
		
		}else {
			alert('Net Amount is not equal to sample Ammount Entered..');
			return false;
		}
		
	}
	
	function removeField(removeId) {
		
		if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
			window.location="SalaryDetails.action?removeId=" +removeId+"&strOrg=<%=request.getAttribute("strOrg")%>&level=<%=request.getAttribute("level")%>"; 
  			return true;
  		}
  		else {
  			return false;
  		}
		
	}
	
</script>

<% String empId = (String)request.getParameter("empId");
	
	UtilityFunctions uF = new UtilityFunctions();
%> 
                        
		<jsp:include page="../common/SubHeader.jsp">
			<jsp:param value="Salary Structure" name="title"/>
		</jsp:include>

    
      <div class="leftbox reportWidth">
      
      
      
      <div class="filter_div">
<div class="filter_caption">Select Organisation</div>
<s:form name="frm" action="SalaryDetails" theme="simple">
	<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
	
	<s:select theme="simple" name="level" listKey="levelId" cssStyle="margin-left:10px"
	             listValue="levelCodeName" headerKey="-1" headerValue="Choose Level" onchange="this.form.submit();"
	             list="levelList" key="" required="true" />
	             
</s:form>
</div>
      
      <%-- <s:form theme="simple" name="selectLevel" action="SalaryDetails" >
      </s:form> --%>
                    
                    
                    
                    
       <%if(uF.parseToInt((String)request.getParameter("level"))>0){ %>             
                            
			<div class="crdb_details">
			
			<!-- <form action="generateHourlySalarySlip.action"> 
   				<div id="div_genHourly" style="float:right; margin:5px; width:170px">
    				<input type="submit" class="input_button" name="HourlySalarySlip" id = "HourlySalarySlip" value="Generate Hourly Salary Slip" onclick="return generateSalarySlip()" />
   				</div>
  			</form> -->
			
				<s:hidden name="curr_short" id="curr_short"></s:hidden>
			
				<!-- <form id="frm_global" name="frm_global" action="GetCreditDetails.action"  > -->
					
					<!-- <div id="div_gen" style="float:right; margin:5px; width:170px">
						<input type="submit" class="input_button" name="salarySlip" id = "salarySlip" value="Generate Salary Slip" onclick="return generateSalarySlip()" />
					</div> -->
					
					<!-- <div id="div_save" style="float:right; margin:5px;">
						<input type="submit" class="input_button" name="save" value="Save" onclick="return saveAll()"/>
					</div> -->
					
					<div class="clr"></div>
					
					<div class="credit" id="div_gross">
		
					    <div class="heading">
			      			<h3>EARNING DETAILS</h3>
					    </div>
				    	
				    	<div class="details_lables" >
				    	
			 				<%boolean isBenefit = false;
			 				for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
			 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
							
							<% 
							if(cinnerlist.get(2).equals("E")) {%>	
								
								<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
								<input type="hidden" name="salary_head_name" value='<%= cinnerlist.get(1) %>'></input>
								<input type="hidden" name="salary_head_byte" value='<%= cinnerlist.get(2) %>'></input>
								
								<%if(uF.parseToInt((String)cinnerlist.get(9))>990 && !isBenefit){ isBenefit=true;%>
								
								Benefits
								<hr/>
								<%} %>
									<div class="row">
									
									 	<div class="col1 tdDashLabel">
							      	 		<label id="lbl"><%= cinnerlist.get(1) %>:</label>	
				      	 				</div>
				      	 				<div class="col2">
				      	 				
				      	 				<%if( cinnerlist.get(3).equals("P")) {%>
				      	 				
				      	 					<label id="lbl_head_value"><%= cinnerlist.get(5) %></label>
				      	 					
				      	 				<%}%>
				      	 				
						      	 			<%-- <select style="width:50px" onchange="changeAllLabelValues()" id="<%= cinnerlist.get(1) %>" name="salary_head_amount_type" >
							      	 			<% if( cinnerlist.get(3).equals("P")) { %>
							      	 				<option value="P" selected="selected">%</option>
							      	 				<option value="A">A</option>
						      	 				<% } else { %>
							      	 				<option value="P">%</option>
							      	 				<option value="A" selected="selected">A</option>
						      	 				<% } %>
								      	 	</select> --%>
								      	 	
								      	 	<% if( cinnerlist.get(3).equals("P")) { %>
								      	 	
								      	 	 % of <label id="lbl_sub_head"><%= cinnerlist.get(4).equals("0") ? "": cinnerlist.get(4) %></label>
								      	 	
								      	 	<%}else{ %>
									      	 	
									      	 	<!-- Amount -->
									      	 	<%=cinnerlist.get(5) %>
									      	 	
									      	<%}
								      	 	
								      	 	if(uF.parseToBoolean((String)cinnerlist.get(6))) {
									      		
									      	%>
								      	 	
						       					<a style="color:red" href="javascript:void(0)" onclick="removeField(this.id)" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
						       				
						       				<%}
									      	
								      	 	if(uF.parseToBoolean((String)cinnerlist.get(7))) {
									      	
						       				%>
						       				
							       				<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %>
							       				<a href="#?w=400" rel="popup_name_edit_<%=cinnerlist.get(8)%>" class="poplight">Edit..</a>
							       				<%} %>
						       				<%}%>
						       				
					       				</div>
				       				</div>
				       				<div class="clr"></div>
			       					<%}%>
			       				
		   					<%}%>
		   					
						</div>
						
						
						<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>
					    <div class="buttons">
						    	<a href="#?w=400" rel="popup_name" class="poplight" ">Add New Field..</a>
						   		<!-- <input type="submit" name="save" value="save..." style="width:100px" /> -->
					    </div>
						<%} %>
					    <div class="clr"></div>
		  
		  			</div> 
		 
					<div class="deduction" id="frm_deduction">
		  
						<div class="heading">
							<h3>DEDUCTION DETAILS</h3>
						</div>
		    
		    			<!-- <form id="frm_deduction" name="frm_deduction" action="SalaryDetails.action.action" onsubmit="return saveDeductionAll()" > -->
				         	<div class="details_lables" >
			 				<% for (int i=0; couterlist!=null && i<couterlist.size(); i++) {%>
			 				
				 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
								
								<% if(cinnerlist.get(2).equals("D")) { %>	
								
									<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
									<input type="hidden" name="salary_head_name" value='<%= cinnerlist.get(1) %>'></input>
									<input type="hidden" name="salary_head_byte" value='<%= cinnerlist.get(2) %>'></input>
									
				       					<div class="row">
											
											<div class="col1 tdDashLabel">
								      	 		<label id="lbl" ><%= cinnerlist.get(1) %>:</label>
							      	 		</div>
							      	 		
					      	 				<div class="col2">
					      	 				
					      	 					<%if( cinnerlist.get(3).equals("P")) {%>
				      	 							<label id="lbl_head_value"><%= cinnerlist.get(5) %></label>
				      	 						<%}%>
				      	 						
							      	 			<%-- <select disabled="disabled" style="width:50px" onchange="changeAllLabelValues()" id="<%= cinnerlist.get(1) %>" name="salary_head_amount_type" >
							      	 			<%if( cinnerlist.get(3).equals("P")) { %>
							      	 				<option value="P" selected="selected">%</option>
							      	 				<option value="A">A</option>
						      	 				<% } else {%>
							      	 				<option value="P">%</option>
							      	 				<option value="A" selected="selected">A</option>
						      	 				<% } %>
									      	 	</select> --%>
									      	 	
									      	 	<% if( cinnerlist.get(3).equals("P")) {%>
								      	 	
										      	 	% of <label id="lbl_sub_head"><%= cinnerlist.get(4).equals("0") ? "": cinnerlist.get(4) %></label>
									      	 	
									      	 	<%}else{ %>
									      	 	
									      	 	<!-- Amount -->
									      	 	<%=cinnerlist.get(5) %>
									      	 	
									      	 	<%}%>
									      	 	
									      	 	<% if(empId!=null) {%>
									      	 	
						       						<input type="text" id="txt_<%= cinnerlist.get(1) %>" name="salary_head_value" value="<%= cinnerlist.get(4) %>" 
						       							style="width:60px" onkeyup="changeAllLabelValues()" maxlength="15" onkeypress="return isNumberKey(event)" />
						       							
							       					<label style="color:green" id="lbl_<%= cinnerlist.get(1) %>">0.00</label><br></br>
							       					
							       				<%}
									      	 	
							       				if(uF.parseToBoolean((String)cinnerlist.get(6))) {
									      		
										      		%>
									      	 	
							       						<a style="color:red" href="javascript:void(0)" onclick="removeField(this.id)" id="remove_<%= cinnerlist.get(8) %>">Remove</a>
							       				
								       				<%}
										      	
							       				if(uF.parseToBoolean((String)cinnerlist.get(7))) {
										      	
								       				%>
								       				<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %>
								       					<a href="#?w=400" rel="popup_name_edit_<%=cinnerlist.get(8)%>" class="poplight" ">Edit..</a>
							       					<%} %>
							       				<%}%>
							       				
						       				</div>
						       				
					       				</div>
				       				  <div class="clr"></div>
				       				<%}%>
		   						<%}%>
		   						
								<% if(empId!=null) { %>
									<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
										<label name="total_deduction" id="lbl">Total Deduction: </label>
										<label name="total_deduction_value" id= "total_deduction_value" style="color: green;"></label>
									</div>
								<%}%>
							</div>
					
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>
						    <div class="buttons">
							    	<a href="#?w=400" rel="popup_deduction" class="poplight" ">Add New Field..</a>
							   		<!-- <input type="submit" name="save" value="save..." style="width:100px" /> -->
						    </div>
					<%} %>
						    <div class="clr"></div>
		   				</div>
						
		       	<!-- </form> -->
		       	
		  <div class="clr"></div>
	  	<% if(empId!=null) { %>
		  <div class="netamount">
		     
		      <div class="netvalue"> 
		      		<label class="tdDashLabel_net" id="lblNetAmountRs">Net Amount Rs:</label> 
		      		<s:label id="lbl_net_amount" name="lbl_net_amount"></s:label>
		      </div>
		       
		  </div>
		<%}%>
		
		</div>
		
		<%}else{ %>
		
		<div class="filter">
			<div class="msg nodata"><span>Please choose the level</span></div>
		</div>
		<%} %>
		
		
		<!-- div for pop - up windows -->
		
		<script>
			function showhideOthers(id){
				if(parseInt(id)==-1){
					document.getElementById('idOtherE').style.display = "table-row";
					document.getElementById('idOtherD').style.display = "table-row";
				}else{
					document.getElementById('idOtherE').style.display = 'none';
					document.getElementById('idOtherD').style.display = 'none';
				}
				
				var sHeadArray=checkSalaryHead.split(",");
				if(parseInt(id)==0){
					document.getElementById('id_isvariableE').style.display = "none";
					document.getElementById('id_isvariableD').style.display = "none";
				}else if(!contains(sHeadArray, id)){
					document.getElementById('id_isvariableE').style.display = "table-row";
					document.getElementById('id_isvariableD').style.display = "table-row";
				}else{
					document.getElementById('id_isvariableE').style.display = "none";
					document.getElementById('id_isvariableD').style.display = "none";
				}
				
				
			}
			
			function contains(arr, value) {
			    var i = arr.length;
			    while (i--) {
			        if (arr[i] === value) return true;
			    }
			    return false;
			}
		
		</script>
		
		
		<div id="popup_name" class="popup_block">
			<h2 class="textcolorWhite">Add New Earning Head</h2>
			<s:form id="frmCreditDetails" action="SalaryDetails" method="post" theme="simple">
				<table>
					<s:hidden name="operation" value="A"></s:hidden>
					<s:hidden name="earningOrDeduction" id="headByte" value="E"></s:hidden>
					<s:hidden name="level"></s:hidden>
					<s:hidden name="strOrg"></s:hidden>
					
					<tr>
						
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Head Name</td>
						
						<td>
						
						<%-- <s:textfield name="headName" label="Head Name"></s:textfield> --%>
						
						
						<s:select theme="simple" label="Select Salary Head" name="headName" listKey="salaryHeadId"
									listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
									list="salaryHeadListEarning" key="" required="true" onchange="showhideOthers(this.value)"/>
									
									
						</td>
					</tr>
					
					<tr id="idOtherE" style="display:none"><td>&nbsp;</td><td><s:textfield name="headNameOther"></s:textfield></td></tr>
					
					<tr>
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Earning/Deduction</td>
						<td>
							<select id="newSelE" name="headAmountType" style="width:50px" onchange="javascript:show_sub_salary_head(this.id);return false;" >
				   	 				<option value="P">%</option>
				   	 				<option value="A">A</option>
				      	 	</select>
						</td>
					</tr>
					
					<tr id="id_salaryHeadE">
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">% Of</td>
						<td>
							<s:select theme="simple" label="Select Salary Head" name="salarySubHead" listKey="salaryHeadId"
									listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
									list="salaryHeadList" key="" required="true"/>
						</td>
					</tr>
					
					<tr id="id_headAmountE">
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Amount</td>
						<td><input type="text" name="headAmount" /></td>
					</tr>
					
					<tr id="id_isvariableE" style="display:none;">
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is variable</td>
						<td><input type="checkbox" name="isVariable" id="isVariable"/></td>
					</tr>		
						<tr >
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is CTC Variable</td>
						<td><input type="checkbox" name="isCTCVariable" /></td>
					</tr>			
					<tr>
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Salary Type</td>
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">
							<input type="radio" name="salary_type" value="M" checked/> Monthly
							<input type="radio" name="salary_type" value="D"/> Daily
							<input type="radio" name="salary_type" value="F"/> Fixed
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center"><s:submit cssClass="input_button" value="Add Earning Head" align="center"></s:submit></td>
					</tr>
				</table>
			</s:form>
		</div>
		
		<div id="popup_deduction" class="popup_block">
			<h2 class="textcolorWhite" style="text-align: center">Add New Deduction Head</h2>
			<s:form id="frmdeductionDetails"  action="SalaryDetails" method="post" theme="simple">
				<table>
				
					<s:hidden name="operation" value="A"></s:hidden>
					<s:hidden name="earningOrDeduction" id="headByte" value="D"></s:hidden>
					<s:hidden name="level"></s:hidden>
					<s:hidden name="strOrg"></s:hidden>
					
					<%-- <tr>
						<td> <s:textfield name="headName" label="Head Name"></s:textfield> </td>
					</tr> --%>
					
					<tr>
						
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Head Name</td>
						
						<td>
						
						<%-- <s:textfield name="headName" label="Head Name"></s:textfield> --%>
						
						
						<s:select theme="simple" label="Select Salary Head" name="headName" listKey="salaryHeadId"
									listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" 
									list="salaryHeadListDeduction" key="" required="true" onchange="showhideOthers(this.value)"/>
									
									
						</td>
					</tr>
					
					<tr id="idOtherD" style="display:none"><td>&nbsp;</td><td><s:textfield name="headNameOther"></s:textfield></td></tr>
					
					
					<tr>
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Type of Head</td>
						<td>
							<select id="newSelD" name="headAmountType" style="width:50px" onchange="javascript:show_sub_salary_head(this.id);return false;" >
				   	 				<option value="P">%</option>
				   	 				<option value="A">A</option>
				      	 	</select>
						</td>
					</tr>
					
					<tr id="id_salaryHeadD">
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">% Of</td>
						<td>
							<s:select theme="simple" label="Select Salary Head" name="salarySubHead" id="id_salaryHead" listKey="salaryHeadId"
									listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head"
									list="salaryHeadList" key="" required="true"/>
						</td>
							
						<%-- <s:url id="department_url" action="GetSalaryHeads" />
						<sx:div href="%{department_url}" listenTopics="show_sub_salary_head" formId="frmCreditDetails" showLoadingText="true"></sx:div> --%>
						
					</tr>
					<tr id="id_headAmountD">
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Amount</td>
						<td><input type="text" name="headAmount" /></td>
					</tr>
					<tr id="id_isvariableD" style="display:none;">
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is variable</td>
						<td><input type="checkbox" name="isVariable" id="isVariable"/></td>
					</tr>
					
						<tr >
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Is CTC Variable</td>
						<td><input type="checkbox" name="isCTCVariable" /></td>
					</tr>
					<tr>
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Salary Type</td>
						<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">
							<input type="radio" name="salary_type" value="M" checked/> Monthly
							<input type="radio" name="salary_type" value="D"/> Daily
							<input type="radio" name="salary_type" value="F"/> Fixed
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center"><s:submit cssClass="input_button" value="Add Deduction Head" align="center"></s:submit></td>
					</tr>
				</table>
			</s:form>
		</div>
 
		<%if(request.getAttribute("sb")!=null) { 
		
			out.println(request.getAttribute("sb"));
			
		%>
		
		<%}%>
		
		</div>

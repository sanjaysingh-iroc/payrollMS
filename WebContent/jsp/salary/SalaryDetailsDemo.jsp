<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Credit deduction Details</title>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<style>

.crdb_details
{
  width:800px;
  border:#666666 solid 1px;
  height:auto;
  float:left;
  margin:20px 15px 0px 65px;
  padding:10px 10px 10px 10px;
}

.credit
{
  width:400px;
  height:auto;
  float:left;
  border-right:#489BE9 solid 1px;
  margin:0px 5px 0px 0px;
  padding:5px 10px 5px 5px;
 
}

.deduction
{
  width:365px;
  height:auto;
  float:left;
 /* border:#FF0000 solid 1px;*/
  padding:5px;
  
}

.heading h3
{
	margin: 0 0 10px;
    border-bottom: 2px solid #EDEBEF;
    color: #000000;
    font-family: verdana,arial,sans-serif;
    font-size: 18px;
	text-align:center;
}

.details_lables
{
 width:305px;
 height:auto;
/* border:#0000CC solid 1px;*/
 float:left;
}

.row
{
  width:400px;
  /*border:#009966 solid 1px;*/
  float:left;
  margin:0px 0px 10px 0px;
}

.col1
{
   width:125px; 
 /* border:#00CC33 solid 1px;*/
  float:left;
  text-align:right;
}

.col2
{
float:left;
/*text-align:left;*/
width:250px;
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
    $(document).ready(function() {
    	
    	changeLabelValue();
    	changeDeductionLabelValue();

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
    
</script>

<script type="text/javascript">

	function callme() {
		
		var head_name = document.getElementById("headName").value;
		//var head_type = document.getElementById("headType").value;
		//alert(head_type);
		var head_value = document.getElementById("headValue").value;
		
		var divTag = document.createElement("div");

        divTag.id = "divDyn";

        divTag.className = "row";

        divTag.innerHTML = "<div class=\"col1 tdDashLabel\"><label id=\"lbl\" >"+head_name+"</label></div><div class=\"col2\"><select id=\""+head_name+"\" style=\"width:50px\" onchange=\"changeAllLabelValues()\"><option value=\"P\">%</option><option value=\"A\">A</option></select><input type=\"text\" id=\"txt_"+head_name+"\" name=\"txt_basic\" style=\"width:60px\" value=\""+head_value+"\" onkeyup=\"changeAllLabelValues()\"/><label style=\"color:green\" id=\"lbl_"+head_name+"\">0.0</label></div>";

        //document.body.appendChild(divTag);
		
        document.getElementById("Earnings_Holder").appendChild(divTag);
        
        changeAllLabelValues();
        
		//event.preventDefault(); // disable normal form submit behavior
        return false; // prevent further bubbling of event
	}

	function ded_callme() {
		
		var ded_head_name = document.getElementById("ded_headName").value;
		//var head_type = document.getElementById("headType").value;
		//alert(head_type);
		var ded_head_value = document.getElementById("ded_headValue").value;
		
		var divTag = document.createElement("div");

        divTag.id = "ded_divDyn";

        divTag.className = "row";

        divTag.innerHTML = "<div class=\"col1 tdDashLabel\"><label id=\"lbl\" >"+ded_head_name+"</label></div><div class=\"col2\"><select id=\""+ded_head_name+"\" style=\"width:50px\" onchange=\"changeAllLabelValues()\"><option value=\"P\">%</option><option value=\"A\">A</option></select><input type=\"text\" id=\"txt_"+ded_head_name+"\" name=\"txt_basic\" style=\"width:60px\" value=\""+ded_head_value+"\" onkeyup=\"changeAllLabelValues()\"/><label style=\"color:green\" id=\"lbl_"+ded_head_name+"\">0.0</label></div>";

        document.getElementById("Deduction_Holder").appendChild(divTag);
		
        changeAllLabelValues();
        
		//event.preventDefault(); // disable normal form submit behavior
        return false; // prevent further bubbling of event
	}
	
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
			
			document.getElementById("lbl_net_amount").innerHTML = 
				(
			parseInt(document.getElementById("total_earning_value").innerHTML) - 
			parseInt(document.getElementById("total_deduction_value").innerHTML) 
			); 
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
		
		document.getElementById("lbl_net_amount").innerHTML = 
			(
		parseInt(document.getElementById("total_earning_value").innerHTML) - 
		parseInt(document.getElementById("total_deduction_value").innerHTML) 
		); 
		
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
	
</script>

</head>

<body>

	<div id="shadow">

	<div class="pagetitle">
        <span>Salary Details</span>
    </div>
      
    <div class="leftbox reportWidth">
    
	<div class="crdb_details">
	
	<form id="frm_global" name="frm_global" action="GetCreditDetails.action">
	
		<div class="credit" id="div_gross">

		    <div class="heading">
      			<h3>GROSS SALERY DETAILS</h3>
		    </div>
		    
		    <div class="clr"></div>
		    
    		<div class="details_lables" style="">
      		
		       <div class="row">
     	 	
		      	 	<div class="col1 tdDashLabel"> 
		      	 		<label id="lbl">Basic :</label>
		      	 	</div>
     	 			
     	 			<div class="col2">
	      	 			
	      	 			<select id="sel_basic" style="width:50px" onchange="changeAllLabelValues()">
	      	 				<option value="P">%</option>
	      	 				<option value="A">A</option>
			      	 	</select>
			      	 	
	   					<input type="text" id="txt_sel_basic" name="txt_sel_basic" style="width:80px" onkeyup="changeAllLabelValues()"/>
	       		
	       				<label style="color:green" id="lbl_sel_basic">0.00</label>
	       				
      				</div>
      				
   				</div>
      			
      			<div id="Earnings_Holder" class="row">
      				
      			</div>
      		
      		
	      		<div class="row">
					 	<div class="col1 tdDashLabel">
			      	 		<label id="lbl">Others_Earning:</label>	
	     	 				</div>
	     	 				
	     	 				<div class="col2">
		      	 			<select style="width:50px; visibility: hidden;" onchange="changeAllLabelValues()" id="Others_Earning" name="salary_head_amount_type" >
		      	 			
		      	 				<option value="A" selected="selected">A</option>
	      	 				
				      	 	</select>
				      	 	
	       					<input type="text" id="txt_Others_Earning" name="salary_head_value" value="0" 
	       							style="width:60px; visibility: hidden;" onkeyup="changeAllLabelValues()" style="visibility: hidden;" />
		       		
		       				<label style="color:green" id="lbl_Others_Earning"></label><br></br>
	       				</div>
     			</div>
			       				
	      		<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
						<label id="lbl">Total Gross Salary:</label>
						<label id= "total_earning_value" style="color: green;"></label>
				</div>
				
			</div>
	      
		    <div class="buttons">
			    	<a href="#?w=400" rel="popup_name" class="poplight" ">Add..</a>
		    </div>
	
		    <div class="clr"></div>
    
    	</div>
    
     	<div class="deduction" id="frm_deduction">
    
      		<div class="heading">
      			<h3>DEDUCTION DETAILS</h3>
     		</div>
   
		    <div class="details_lables">
       
		       <div class="row">
		       
			       <div class="col1 tdDashLabel">
	      	 			<label id="lbl" >PF :</label>
	   	 			</div>
	   	 			
		  			<div class="col2">
		  	 			
	    	 			<select style="width:50px" onchange="changeAllLabelValues()" id="PF" name="salary_head_amount_type" >
	    	 				<option value="P" selected="selected">%</option>
	    	 				<option value="A">A</option>
	    		  	 	</select>
	      	 	
	    				<input type="text" id="txt_PF" name="salary_head_value" value="0" 
	    						style="width:60px" onkeyup="changeAllLabelValues()" maxlength="15" onkeypress="return isNumberKey(event)" />
	     		
	     				<label style="color:green" id="lbl_PF">0.00</label><br></br>
		     				
		   			</div>
	  	 			
      	 		</div>
		       
		       	<div id="Deduction_Holder" class="row">
      				
      			</div>
		       
		       	<div class="row">
											
						<div class="col1 tdDashLabel">
			      	 		<label id="lbl" >Others_Deduction:</label>
		      	 		</div>
		      	 		
	     	 				<div class="col2">
		      	 			<select style="width:50px; visibility: hidden;" id="Others_Deduction" name="salary_head_amount_type" >
		      	 				<option value="A" selected="selected">A</option>
				      	 	</select>
				      	 	
	       					<input type="text" id="txt_Others_Deduction" name="salary_head_value" value="0" 
	       							style="width:60px; visibility: hidden;" />
		       		
		       				<label style="color:green" id="lbl_Others_Deduction">0.00</label><br></br>
	       				</div>
						       				
   				</div>
		       
	        		<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
							<label id="lbl">Total Deduction: </label>
							<label id= "total_deduction_value" style="color: green;"></label>
					</div>
					
				</div>
		       
			    <div class="buttons">
			      	<a href="#?w=400" rel="popup_deduction" class="poplight" ">Add..</a>
			    </div>
			    
		    	<div class="clr"></div>
		    
		    </div>
		    
    	</form>
    	
	     <div class="clr"></div>
	  
		  <div class="netamount">
		     
		      <div class="netvalue"> 
		      		<label class="tdDashLabel_net" id="lblNetAmountRs">Net Amount Rs:</label> 
		      		<label id="lbl_net_amount"></label>
		      </div>
		       
		  </div>
		
			<div class="sampleAmount">
			     
			      <div>
			      		<label id="lblNetAmountRs" style="font-size: 14px;">Sample Salary Amount:</label> 
						<input type="text" onkeyup="changeAllLabelValues()" maxlength="15" value="0" style="width: 80px;" name="sample_salary" id="sample_salary"/>
			      </div>
			       
	 		</div>
  
		</div>
	
<!-- div for pop - up windows -->

<div id="popup_name" class="popup_block">
	<h2 class="textcolorWhite">Gross Salary Details</h2>
	<form id="frmCreditDetails" >
		<table>
			<tr>
				<td>
					<label>Head Name</label>
					<input type="text" name="headName" id="headName" />
				</td>
				<!-- <s:textfield name="headName" label="Head Name" id="headName"></s:textfield></td> -->
			</tr>
			<tr>
				<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Type of Head</td>
				<td>
					<select id="sel" name="headType" style="width:50px" onchange="" id="headType">
		   	 				<option value="P">%</option>
		   	 				<option value="A">A</option>
		      	 	</select>
				</td>
			</tr>
			<tr>
				<td>
					<label>Head Value</label>
					<input type="text" name="headValue" id="headValue" />
					<!-- <s:textfield name="headValue" label="Head Value" onkeyup="" id="headValue"></s:textfield> -->
				</td>
			</tr>
			<tr>
				<td>
					<input type="submit" class="input_button" value="Add Credit Details" onclick="return callme()" />
				</td>
				<!-- <s:submit cssClass="input_button" value="Add Credit Details" align="center" onclick="return callme()"></s:submit> -->
			</tr>
		</table>
	</form>
</div>

<div id="popup_deduction" class="popup_block">
	<h2 class="textcolorWhite">deduction Details</h2>
	<form id="frmdeductionDetails" >
		<table>
			<tr>
				<td>
					<label>Head Name</label>
					<input type="text" name="ded_headName" id="ded_headName" />
				</td>
				<!-- <td><s:textfield name="ded_headName" label="Head Name" id="ded_headName"></s:textfield></td> -->
			</tr>
			<tr>
				<td style="color: rgb(255, 255, 255); font-style: italic; font-size: 12px;">Type of Head</td>
				<td>
					<select id="sel" name="ded_headType" style="width:50px" id="ded_headType">
		   	 				<option value="P">%</option>
		   	 				<option value="A">A</option>
		      	 	</select>
				</td>
			</tr>
			<tr>
				<td>
					<label>Head Value</label>
					<input type="text" name="ded_headValue" id="ded_headValue" />
				<!-- <s:textfield name="ded_headValue" label="Head Value" onkeyup="" id="ded_headValue"></s:textfield> -->
				</td>
			</tr>
			<tr>
				<td>
					<input type="submit" class="input_button" value="Add Deduction Details" onclick="return ded_callme()" />
				</td>
				<!-- <s:submit cssClass="input_button" value="Add Deduction Head" align="center" onclick="return ded_callme()"></s:submit> -->
			</tr>
		</table>
	</form>
</div>

	</div>
		
	</div>	<!-- shadow -->
</body>
</html>
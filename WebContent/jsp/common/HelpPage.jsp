<%@ taglib prefix="s" uri="/struts-tags"%>


<script type="text/javascript">

$(document).ready(function(){
	$("body").on('click','#closeButton',function(){
		$(".modal-dialogHelp").removeAttr('style');
		$("#modalInfoHelp").hide();
    });
	$("body").on('click','.close',function(){
		$(".modal-dialogHelp").removeAttr('style');
		$("#modalInfoHelp").hide();
	});
	
});


function getData(callName) {
	
	if(callName == 'ApplyLeave') {
		$('#modalTitleHelp').html('How can I apply Leave?');
		document.getElementById("divHomePage").style.display = "none";
		document.getElementById("divLeave").style.display = "block";
		document.getElementById("divReimbursement").style.display = "none";
		
		document.getElementById("liAboutThisPage").className = "";
		document.getElementById("liLeave").className = "active";
		document.getElementById("liReimbursement").className = "";
	} else if(callName == 'ApplyReimbursement') {
		$('#modalTitleHelp').html('How do I apply Reimbursement?');
		
		document.getElementById("divHomePage").style.display = "none";
		document.getElementById("divLeave").style.display = "none";
		document.getElementById("divReimbursement").style.display = "block";
		
		document.getElementById("liAboutThisPage").className = "";
		document.getElementById("liLeave").className = "";
		document.getElementById("liReimbursement").className = "active";
	} else {
		$('#modalTitleHelp').html('Home Page');
		
		document.getElementById("divHomePage").style.display = "block";
		document.getElementById("divLeave").style.display = "none";
		document.getElementById("divReimbursement").style.display = "none";
		
		document.getElementById("liAboutThisPage").className = "active";
		document.getElementById("liLeave").className = "";
		document.getElementById("liReimbursement").className = "";
	}
}

</script>

<script type="text/javascript">
	setTimeout(function() {
		if (location.hash) {
			window.scrollTo(0, 0);
		}
	}, 1);
</script>

<style>

/* .modal-body {
	background-color:rgba(255, 255, 255, 0.65); 
	-webkit-background-clip: padding-box;
	background-clip: padding-box;
	border: 1px solid #999;
	border: 1px solid rgba(0, 0, 0, .2);
	border-radius: 6px;
	outline: 0;
	-webkit-box-shadow: 0 3px 9px rgba(0, 0, 0, .5);
	box-shadow: 0 3px 9px rgba(0, 0, 0, .5);
	height:auto;
} */

#modalheader {
	margin: 10px 30px;
	padding: 0px !important;
	color: white;
	font-size: 24px !important;
}

.modal-dialogHelp {
	max-width: 100% !important;
	max-height: 100% !important;
	position: relative;
	margin: 50px 0px 0px 50px;
  /*background-color: rgba(0, 0, 0, 0.19);
  background-color:rgba(100, 149, 192, 0.31)*/
	background-color:rgba(0, 0, 0, 0.7);
	/* -webkit-background-clip: padding-box;
	background-clip: padding-box; */
	/* border: 1px solid #999; */
	border: 1px solid rgba(0, 0, 0, .2);
	border-radius: 6px;
	/* outline: 0; */
	/* -webkit-box-shadow: 0 3px 9px rgba(0, 0, 0, .5); */
	box-shadow: 0 3px 9px rgba(0, 0, 0, .5);
}

</style>

 
<%-- <s:if test="callFrom == null || callForm == ''"> --%>

<% String callPage = (String) request.getAttribute("callPage"); %>
	<div class="container1">
		<div class="row1">
		     
		     <div id="divHomePage" style="display: <%=(callPage != null && callPage.equalsIgnoreCase("MyHome")) ? "block" : "" %>">
			     <div class="col-sm-12 col-md-8 col-lg-8">
				     <div style="color: white; font-size: 18px; padding: 50px 50px 20px;">
				     <!-- fa-hand-o-right -->
				     	<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">Right hand corner- 'Quick Links', based on your profile and product selection helps you reach 
				     	where you wish to go in the software.</span></div>
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">Bottom right corner- If you were an HR, this section appears for you to navigate to various 
						parts of the software, as an HR.</span></div>
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">Top Center column- counters, for all relevant daily activities that are important for you.</span></div>
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">'To Do'- section which tells you what to do.</span></div>
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">'My Updates' new updates for you.</span></div>
				     </div>
				</div>
			</div>
			
			<div id="divLeave" style="display: none;">
			     <div class="col-sm-12 col-md-8 col-lg-8">
				     <div style="color: white; font-size: 18px; padding: 50px 50px 20px;">
				     <!-- fa-hand-o-right -->
				     	<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">Section dedicated for Leave and Travel applications. Employees travel out of office and out of town. 
				     		Each such Travel can be planned and applied for confirmation. Based on this travel plan one can apply Reimbursements against it.</span></div>
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">Every organization has multiple leave types, which are driven by various kinds of policies. 
							In the section the employee can check their leave status against each leave type.</span></div>
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">All Applied Leaves and Travel transactions are visible with their respective status below the Leave Card.</span></div>
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">Extra working can be applied using ‘Apply for Extra Working’. On Application of Extra Working, 
							one can avail compensation for the extra work undertaken.</span></div>
				     </div>
				</div>
			</div>
			
			
			<div id="divReimbursement" style="display: none;">
			     <div class="col-sm-12 col-md-8 col-lg-8">
				     <div style="color: white; font-size: 18px; padding: 50px 50px 20px;">
				     <!-- fa-hand-o-right -->
				     	<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">Workrig allows you to apply for reimbursement in two ways:</span></div>
				     		<div style="float: left; width: 100%; margin-left: 40px;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 10px; padding-top: 6px;"></i> <span style="float: left; font-size: 14px; width: 95%;">Single Reimbursement</span></div>
				     		<div style="float: left; width: 100%; margin-left: 40px;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 10px; padding-top: 6px;"></i> <span style="float: left; font-size: 14px; width: 95%;">Bulk Reimbursement</span></div>
				     	
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">The Single Reimbursement form allows the users to apply reimbursement one at a time.</span></div>
						
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">The Bulk Reimbursement form allows multiple entries and is suitable for users who wish to upload multiple transactions.</span></div>
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">The reimbursement heads, visibility and restrictions are based on the Policies governing employees level and designation.</span></div>
						<div style="float: left; width: 100%;"><i class="fa fa-circle" aria-hidden="true" style="float: left; font-size: 12px; padding-top: 6px;"></i> <span style="float: left; width: 95%;">Every transaction or application can be tracked in the section, with every detail.</span></div>
				     </div>
				</div>
			</div>
			
<!-- Single Reimbursement
Bulk Reimbursement -->
			
			<div class="col-sm-12 col-md-4 col-lg-4" style="border-left: 1px solid white; min-height: 400px;">
		      	<ul class="nav nav-pills nav-stacked bullets" data-spy="affix" data-offset-top="205" style="list-style-type:circle; font-size: 16px;">
			        <h4 style="margin-left: 18px; color: white; font-weight: bold;">More Help:</h4>
			        <li id="liAboutThisPage" class="active" style="background-color: transparent;"><a href="javascript:void(0);" style="color: white;" onclick="getData('MyHome');"><i class="fa fa-caret-right" aria-hidden="true"></i> About this page.</a></li>
			        <li id="liLeave" style="background-color: transparent;"><a href="javascript:void(0);" style="color: white;" onclick="getData('ApplyLeave');"><i class="fa fa-caret-right" aria-hidden="true"></i> How can I apply Leave?</a></li>
			        <li id="liReimbursement" style="background-color: transparent;"><a href="javascript:void(0);" style="color: white;" onclick="getData('ApplyReimbursement');"><i class="fa fa-caret-right" aria-hidden="true"></i> How do I apply Reimbursement?</a></li>
		      	</ul>
		    </div>
		</div> 
	</div>



	<!-- <div id="divLeave" style="display: none;">
		<div class="container1">
			<div class="row1">
			    <nav class="col-sm-3" id="myScrollspy">
			      <ul class="nav nav-pills nav-stacked bullets affix-top" data-spy="affix" data-offset-top="205" style="list-style-type:circle">
			        <h4 style="margin-left: 18px;text-decoration: underline;color:white;font-weight: bold;">More Help:</h4>
			        <li><a href="javascript:void(0)" onclick="getData('MyHome');"> <i class="fa fa-caret-right" aria-hidden="true"></i> About this page.</a></li>
			       <li class="active"><a href="javascript:void(0)"  onclick="getData('Leave');"><i class="fa fa-caret-right" aria-hidden="true"></i> How can I apply Leave?</a></li>
	        <li><a href="javascript:void(0)" onclick="getData('Reimbursement');"><i class="fa fa-caret-right" aria-hidden="true"></i> How do I apply Reimbursement?</a></li>
			      </ul>
			    </nav>
				<div class="col-sm-9" id="scrolldata">
			
			<div id="section1">    
				        <h4>Figure 1: Navigating to Leave Page</h4>
				        <img src="images1/apply-leave.png" style="width:350px; height:250px;margin-left: 180px;border: 1px solid #504e4e;box-shadow: 13px 9px 20px grey;">
						<br><br><p style=" ">To navigate to the Apply Leave Page, click the My Pay Actions menu on the Left Side Bar and select Leave & Travel Apply from the top line Headers. Below the Top line we have sub-headers to the extreme right hand side corner. We find the second sub-header to be the <Apply Leave> Click.</p>
					</div>
			  		<div id="section2"> 
				        <h4>Figure 2: Submitting Leave Application</h4>
				        <img src="images1/submit-leave.png" style="width:350px; height:250px;margin-left: 180px;border: 1px solid #504e4e;box-shadow: 13px 9px 20px grey;">
						<br><br><p style="">Leave applications should be submitted before going on leave. For an unplanned leave or sick leave, one needs to apply for leave immediately after returning back to work.</p>
						<br><h4>Steps:</h4>
						<ol id="listdata" style=" ">
						<li>Click Apply Leave under Leave & Travel tab on the My Pay page.</li>
						<li>In the Half Day Field Select the checkbox if Half Day of Leave intended to apply for a respective date or simply keep the checkbox deselected in case of full day leave.	</li>
						<li>In the Leave From Date field, select the intended date for leave to apply from the calendar that comes as a drop down once the cursor is placed and then select the relevant date.	</li>
						<li>In the To Date field, select the intended date for leave to apply from the calendar that comes as a drop down once the cursor is placed and then select the relevant date.	</li>
						<li>In the Leave Type field, select leave type which has been assigned to you. Once you select a leave type, the right hand side corner shall display the Leave Balance to the credit.	</li>
						<li>In the Leave Reason Field, one may detail the reasoning for the leave.	</li>
						<li>Click Apply Leave Button at the bottom to submit the leave application.	</li>
						<li>The system shall send an email notification to the approver and the relevant workflow designed for the concern.	</li>
						
						</ol>
						
						
					</div>
				</div>
	  		</div>
		</div>
	
	</div> -->



<!-- <div id="divReimbursement" style="display: none;">

		<div class="container1">
			<div class="row1">
			    <nav class="col-sm-3" id="myScrollspy">
			      <ul class="nav nav-pills nav-stacked bullets affix-top" data-spy="affix" data-offset-top="205" style="list-style-type:circle">
			        <h4 style="margin-left: 18px;text-decoration: underline;color:white;font-weight: bold;">More Help:</h4>
			        <li><a href="javascript:void(0)" onclick="getData('MyHome');"><i class="fa fa-caret-right" aria-hidden="true"></i> About this page.</a></li>
			       <li><a href="javascript:void(0)"  onclick="getData('Leave');"> <i class="fa fa-caret-right" aria-hidden="true"></i> How can I apply Leave1?</a></li>
	        <li class="active"><a href="javascript:void(0)" onclick="getData('Reimbursement');"><i class="fa fa-caret-right" aria-hidden="true"></i> How can I apply Reimbursement?</a></li>
			 
			     
			      </ul>
			    </nav>
				<div class="col-sm-9" id="scrolldata">
			
				<div id="section1">    
				        <p style="">One can claim reimbursement for expenses incurred for performing the official duties. These reimbursement claims do not form the part of salary and are in addition to what employees are entitled. </p>
				        
				        <h4>Figure 1: Navigating to Reimbursement Page</h4>
				        <img src="images1/1.png" style="width:350px; height:250px;margin-left: 180px;border: 1px solid #504e4e;box-shadow: 13px 9px 20px grey;">
						<br><br><p style=" ">To navigate to the Reimbursement page, click the My Pay menu on the Left Side Bar and select Reimbursements from the top line Headers. Just below the top line border, we have sub-headers as <Reimbursement> and <Reimbursement Part of CTC>.<br>By default the <Reimbursement> tab displays options to Apply Reimbursements in both Single and Bulk subject to the cap put the concerned level of employees by the HR/Admin.</p>
					</div>
			  		<div id="section2"> 
				        <h4>Apply New Reimbursement (Single)</h4>
				        <img src="images1/2.png" style="width:350px; height:250px;margin-left: 180px;border: 1px solid #504e4e;box-shadow: 13px 9px 20px grey;">
						<br><br><p style=" ">Multiple expense types such as travel, local, mobile bill & various project based expenses can be claimed. One needs to apply for reimbursement based on the pay Cycle against which such official expenses made are called for reimbursement.</p>
						<h4>General Steps:</h4>
						<ol id="listdata" style="">
						<li>Click on Apply New Reimbursement under Reimbursement Tab on the My Pay page.</li>
						<li>All the mandatory fields have Red Asterix to the top right corner of each field names.</li>
						<li>Select the Pay Cycle against which the expense needs to be reimbursed.</li>
						<li>Select the Expense Headers from Travel Plan, Local, Mobile and Project.</li>
						<li>Fill the related information in the fields below which may vary with respect to the Expense Headers selected.</li>
						<li>Type the Receipt number if applicable.</li>
						<li>Fill the Amount of Reimbursement.</li>
						<li>Type the details of the Claim Amount in the text box provided against the field named Purpose.</li>
						<li>Click Choose File to attach document associated with the reimbursements.</li>
						<li>Click Submit for approval based on the workflow set by the HR/Admin.</li>
						</ol>
						
						
					</div>
					
					
					<div id="section2"> 
				       
				        
				        <h4>Travel Plan Specific:</h4>
				         <p style="">The Travel expenses reimbursement has a dependency on the approval of Travel applied.</p>
				         <img src="images1/3.png" style="width:350px; height:250px;margin-left: 180px;border: 1px solid #504e4e;box-shadow: 13px 9px 20px grey;">
						<br><br><p style="">Herein one would share the Plan name to which the business trip is associated. The complete journey details including the advance requirement and requirement of Concierge Service from the company etc. are reported to the concerns based on the workflow set for the claimant. Once this is approved then the reimbursement for such travel can be claimed.</p>
						<ol id="listdata" style=" ">
						<li>All the mandatory fields have Red Asterix to the top right corner of each field names.</li>
						<li>Select the Pay Cycle against which the expense needs to be reimbursed.</li>
						<li>Select Travel Plan from the Expense Headers.</li>
						<li>Travel Plan as mentioned above should have been approved so it to appear under the dropdown list of Travel Plan within the Reimbursement section.</li>
						<li>Mention the number of persons for which the expense incurred cumulatively.</li>
						<li>Select the Transportation Type namely Car, Bus, Train and Flight.</li>
						<li>Based on the selection of Transportation Type the associated list of dropdown shall appear with opting selction for Cab/Self Owned, AC/Non AC and Business/Economy Class. These all have a CAP set based on the business Policies of the Organization.</li>
						<li>Fill the Amount of Reimbursement.</li>
						<li>Select the type and amount of expenses for Lodging.</li>
						<li>Select the Local Conveyance Mode, Total KMs and Rate per KM authorized for the level of Concern. This shall give the amount of local conveyance to further add reimbursement.</li>
						<li>Subsequently mention the expenses incurred against Food & Beverage, Laundry and Sundry Miscellaneous.</li>
						<li>Mention the name of Vendor if any supplies or kind purchased.</li>
						<li>Type the Receipt number if applicable.</li>
						<li>The Amount Field shall club and auto-calculate the expenses as mentioned in the respective fields.</li>
						<li>Type the details of the Claim Amount in the text box provided against the field named Purpose.</li>
						<li>Click Choose File to attach document associated with the reimbursements.</li>
						<li>Based on the workflow, select the Reporting Manager from the dropdown of My Team, Global HR, Reporting HR, CEO, and the Accountant from the subsequent dropdown lists.</li>
						<li>Click Submit for approval based on the workflow set by the HR/Admin.</li>
						</ol>
						
						
					</div>
					
					
					<div id="section2"> 
				       <h4>Local Specific:</h4>
				          <img src="images1/submit-leave.png" style="width:350px; height:250px;margin-left: 60px;">
						<br><br>
						<p style="">All the expenses considered as incurred within city are covered here. Some of the expenses to name are local conveyance to client site, food expenses, purchase of Supplies, Repair & Maintenance, Internet Charges, Relocation expenses, Courier charges, Laundry Charges, Printing & Stationery and such Other related charges that could be claimed off.</p>
						 <br><br><h4>General Steps:</h4>
						<ol id="listdata" style="">
						<li>All the mandatory fields have Red Asterix to the top right corner of each field names. </li>
						<li>Select the Pay Cycle against which the expense needs to be reimbursed.</li>
						<li>Select Local from the Expense Headers.</li>
						<li>Select the nature of expenses in the dropdown field named Type. </li>
						<li>Mention the number of persons for which the expense incurred cumulatively.</li>
						<li>Mention the name of Vendor if any supplies or kind purchased.</li>
						<li>Type the Receipt number if applicable.</li>
						<li>Fill the Amount of Reimbursement.</li>
						<li>Type the details of the Claim Amount in the text box provided against the field named Purpose.</li>
						<li>Click Choose File to attach document associated with the reimbursements.</li>
						<li>Based on the workflow, select the Reporting Manager from the dropdown of My Team, Global HR, Reporting HR, CEO, and the Accountant from the subsequent dropdown lists.</li>
						<li>Click Submit for approval based on the workflow set by the HR/Admin.</li>
						
						</ol>
						
						
					</div>
					
					<div id="section2"> 
				       <h4>Mobile Bill Specific:</h4>
				      <ol id="listdata" style="">
							<li>All the mandatory fields have Red Asterix to the top right corner of each field names. </li>
							<li>Select the Pay Cycle against which the expense needs to be reimbursed.</li>
							<li>Select Mobile Bill from the Expense Headers.</li>
							<li>Mention the number of persons for which the expense incurred cumulatively.</li>
							<li>Mention the name of Vendor if any supplies or kind purchased.</li>
							<li>Type the Receipt number if applicable.</li>
							<li>Fill the Amount of Reimbursement.</li>
							<li>Type the details of the Claim Amount in the text box provided against the field named Purpose.</li>
							<li>Click Choose File to attach document associated with the reimbursements.</li>
							<li>Based on the workflow, select the Reporting Manager from the dropdown of My Team, Global HR, Reporting HR, CEO, and the Accountant from the subsequent dropdown lists.</li>
							<li>Click Submit for approval based on the workflow set by the HR/Admin.</li>
						
						</ol>
						
						
					</div>
					
					
					
					<div id="section2"> 
				       <h4>Project Specific:</h4>
				       <p style="">This is majorly the Client/Project/Task specific expenses which have a correlation with the Project Management module under Taskrig. Some of the expenses to name are local conveyance to client site, food expenses, purchase of Supplies, Repair & Maintenance, Internet Charges, Relocation expenses, Courier charges, Laundry Charges, Printing & Stationery and such Other related charges that could be claimed off.</p>
				      <ol id="listdata" style="">
							<li>All the mandatory fields have Red Asterix to the top right corner of each field names. </li>
							<li>Select the Pay Cycle against which the expense needs to be reimbursed.</li>
							<li>Select Project from the Expense Headers.</li>
							<li>Select the name of Client in the dropdown field which comes from the Taskrig.</li>
							<li>Select the name of Project in the dropdown field which comes from the Taskrig. </li>
							<li>Select the nature of expenses in the dropdown field named Type. </li>
							<li>Mention the number of persons for which the expense incurred cumulatively.</li>
							<li>Mention the name of Vendor if any supplies or kind purchased.</li>
							<li>Type the Receipt number if applicable.</li>
							<li>Fill the Amount of Reimbursement.</li>
							<li>Type the details of the Claim Amount in the text box provided against the field named Purpose.</li>
							<li>Select the Checkbox gainst the field Chargeable to Client if the expenses are to be recovered back from the concerned client against the project specified and leave it unchecked if it is an in-house expense.</li>
							<li>Click Choose File to attach document associated with the reimbursements.</li>
							<li> Based on the workflow, select the Reporting Manager from the dropdown of My Team, Global HR, Reporting HR, CEO, and the Accountant from the subsequent dropdown lists.</li>
							<li>Click Submit for approval based on the workflow set by the HR/Admin.</li>
						</ol>
						
						
					</div>
					
					<div id="section2"> 
				       <h4>Apply Bulk Reimbursement</h4>
				          <img src="images1/4.png" style="width:350px; height:250px;margin-left: 180px;border: 1px solid #504e4e;box-shadow: 13px 9px 20px grey;">
						<br><br>
						<p style="">On clicking to the Apply Bulk Reimbursement the Software leads us to Bulk Reimbursement Page.</p>
						<img src="images1/5.png" style="width:350px; height:250px;margin-left: 180px;border: 1px solid #504e4e;box-shadow: 13px 9px 20px grey;">
						<br><br><p style="">The concept here is same as of Apply New Reimbursement but it has an extra Button of Add which allows the user to bundle a couple of expenses and apply for bulk at one click to Submit Button. The rules here are the same. The only difference is of the format being horizontal here whereas the fields appearing while applying New reimbursement has Vertical Show down.</p>
						<br><br>
						
						
					</div>
					
					
					
					
					
				</div>
	  		</div>
		</div>
	
</div> -->

<%-- </s:if> --%>
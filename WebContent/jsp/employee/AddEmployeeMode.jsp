<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
response.setHeader("Content-Disposition", "attachment;filename=ImportEmployees.xlsx");
response.setHeader("Content-Type", "application/octet-stream;");
String mode = (String) request.getAttribute("mode");
///System.out.println("mode===at start"+mode);
String fromPage = (String) request.getAttribute("fromPage");
String sbmsg=(String)session.getAttribute("sbMessage");
//System.out.println("sbmsg===at start"+sbmsg);
String selectedFilter=(String)request.getAttribute("selectedFilter");
//System.out.println("selectedFilter=====>"+selectedFilter);

%>
 
<%-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script> --%> 
 
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	if(document.getElementById("f_strWLocation")) {
		$("#f_strWLocation").multiselect().multiselectfilter();
	}
});
</script>

<script>

function  getLocationOrg(strOrg) {
	var action='GetLocationOrg.action?strOrg='+strOrg+'&fromPage=AddEmp';
	//alert("action"+action);
	getContent('locationdivid', action);
} 

function generateEmployeeReportingStructure() {
	//alert("in generateEmployeeReportingStructure");
	
	var org = "";
	var location = "";
	 org = document.getElementById("f_org").value;
	// alert("org=="+org);
	 location = getSelectedValue("f_strWLocation");
	
	/* if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	alert("org"+org);
	if(document.getElementById("f_strWLocation")) {
		location = getSelectedValue("f_strWLocation");
	} */
	
	//alert("location"+location);
	var paramValues = '&f_strWLocation='+location+'&f_org='+org;
	//alert("paramValues=="+paramValues);
	window.location='ImportEmpReportingStructure.action?exceldownload=true'+paramValues;
}

/* ===start parvez date: 23-02-2023=== */
 /* function generateEmployeeBankDetails() {
	window.location='ImportEmpBankDetails.action?exceldownload=true';
}
/* ===end parvez date: 23-02-2023=== */


function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	
	//alert("choice=="+choice);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		if (choice.options[i].selected == true) {
			if (j == 0) {
				exportchoice = choice.options[i].value;
				j++;
			} else {
				exportchoice += "," + choice.options[i].value;
				j++;
			}
		}
	}
	//alert("exportchoice" +exportchoice);
	return exportchoice;
}



</script>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto;">
                  <div class="leftbox reportWidth">
                    	<div class="row row_without_margin">
                    	  <%if((mode == null || mode.equals("") || mode.equalsIgnoreCase("null")) || (mode != null && mode.equals("4"))) { %>
                    		
                    		<div class="col-lg-5 col-md-5 col-sm-12">
                    		 <%if(mode == null || mode.equals("") || mode.equalsIgnoreCase("null")) { %>
                    			<div style="width:100%;float:left;border: solid 1px #cccccc; ">
									<p class="past">To add new employee by yourself click on the link below </p> 
									<div style="text-align:center; padding:10px;">
										<s:form theme="simple" action="AddEmployee" method="post" cssClass="formcss">
											<s:submit cssClass="btn btn-primary" name="myself" value="Add Employee By Myself"></s:submit>
										</s:form>
									</div>
								</div>
								<%} %>
								<%if((mode == null || mode.equals("") || mode.equalsIgnoreCase("null")) || (mode != null && mode.equals("4"))) { %>
									<div style="width:100%;float:left;border: solid 1px #cccccc;margin-top: 40px">
										<p class="past">Quick bulk upload your employees</p> 
										<div style="text-align:center; padding:10px;">
											<s:form theme="simple" action="ImportEmployees" method="POST" enctype="multipart/form-data">
												<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage %>"/>
												<input type="hidden" name="mode" id="mode" value="<%=mode %>"/>
												<table style="width:100%" class="table">
													<tr>
														<td class="txtlabel alignRight">Select File to Import</td>
														<td><s:file name="fileUpload" label="Select a File to upload" size="20" accept=".xls,.xlsx" /></td>
													</tr>  
													<tr>
														<td colspan="2" align="center"><s:submit value="Import File" name="importSubmit" id="importSubmit" align="center" cssClass="btn btn-primary" /></td>
													</tr>
													<tr>
														<td colspan="2" align="right"> 
															<a target="_blank" href="<%=request.getContextPath() %>/import/ImportEmployees.xlsx" title="Download Sample File" ><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
														</td>
													</tr> 
												</table> 
											</s:form>
										</div>
									</div>
								<% } %>
                    		</div>
                    		<% } %>
                    		
                    		
                    		 <%if(mode == null || mode.equals("") || mode.equalsIgnoreCase("null")) { %>
                    		<div class="col-lg-2 col-md-2 hidden-sm hidden-xs autoWidth">
                    			<img src="images1/or_bg.png" width="25px" height="100px"/>
                    			<div style="background:#fff;text-align:center;font-weight: 600;font-size: 20px;color: #999999;">OR</div>
                    			<img src="images1/or_bg.png" width="25px" height="100px"/>
                    		</div>
                    		
                    		<div class="visible-sm-block visible-xs-block hidden-md hidden-lg autoWidth">
                    			<img src="images1/or_horizontal.png" width="45%" height="25px"/>
                    			<div style="background:#fff;display: inline;text-align:center;font-weight: 600;font-size: 20px;color: #999999;">OR</div>
                    			<img src="images1/or_horizontal.png" width="45%" height="25px"/>
                    		</div>
                    		<%} %>
                    		<%if((mode == null || mode.equals("") || mode.equalsIgnoreCase("null")) || (mode != null && mode.equals("3"))) { %>
                    		 <%if(mode != null &&  mode.equals("3")) { %>
                    		 <div class = "col-lg-12 col-md-12 col-sm-12  ">
                    		 <%} else { %>
                    			  <div class="col-lg-5 col-md-5 col-sm-12">
                    		<%} %>
                    			<div style="border: solid 0px #cccccc;text-align: center; margin: 0px 0px;">
								<p style="color: green"><s:property value="message"/></p>
								</div>
								
								<div style="border: solid 1px #cccccc; ">
								<p class="past">Let Employee fill the information for you</p>
							        <div style=" padding:10px;">
							            <s:form theme="simple" method="post" id="signUpForm" name="signUpForm" cssClass="formcss" enctype="multipart/form-data">
							                <s:token name="token"></s:token>
							                <input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>"/>
							                <s:hidden name="mode" id="mode"/>
							                <table class="table">
							                <tr><td>Employee First Name<sup>*</sup>:</td><td><s:textfield name="fname" cssClass="validateRequired form-control autoWidth"></s:textfield></td></tr>
							                <tr><td>Employee Last Name<sup>*</sup>:</td><td><s:textfield name="lname" cssClass="validateRequired form-control autoWidth"></s:textfield></td></tr>
							                <tr><td>Employee Email Id<sup>*</sup>:</td><td><s:textfield name="email" cssClass="validateRequired form-control autoWidth"></s:textfield></td></tr>
							                <s:hidden name="notification" value="signup" />
							                <tr><td>&nbsp;</td><td><s:submit cssClass="btn btn-primary" name="emailEmployee" value="Let Employee Enter the Info"></s:submit></td></tr>
							                </table>
							            </s:form>
							        </div>
							        <script type="text/javascript">
							        
							         <%if(fromPage != null && fromPage.equals("P") && mode != null && mode.equals("3")) { %>
							           $("#signUpForm").submit(function(event) {
							        	   event.preventDefault();
							        	   var form_data = $("#signUpForm").serialize();
									         $.ajax({ 
									     		type : 'POST',
									     		url: 'AddEmployeeMode.action',
									     		data: form_data,
									     		cache: true,
									     		success: function(result){
									     			$("#divResult").html(result);
									        	},
									        	error: function(result){
									        		$.ajax({
									 					url: 'EmployeeReport.action',
									 					cache: true,
									 					success: function(result){
									 						$("#divResult").html(result);
									 			   		}
									 				});
									        	}
									     	});
							           });
							        <% } %>
							        </script>
								</div>
                    		</div>
                    	</div>
 						<%} %>
 						
 						
 						<div class="col-lg-6 col-md-6 col-sm-12">
							<%if((mode == null || mode.equals("") || mode.equalsIgnoreCase("null")) || (mode != null && mode.equals("4"))) { %>
							<!--code for import employee reporting structure  -->	
								<div style="width:100%;float:right;border: solid 1px #cccccc;margin-top: 40px">
									<p class="past">Quick bulk upload for employee Reporting Structure</p> 
									<div style="text-align:center; padding:10px;">
										<s:form theme="simple" action="ImportEmpReportingStructure" method="POST" enctype="multipart/form-data">
										<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage %>"/>
										<input type="hidden" name="mode" id="mode" value="<%=mode %>"/>
										<table style="width:100%" class="table">
											 <tr>
												 <div class="box-body" style="padding: 5px; overflow-y: auto;">
													<div class="row row_without_margin">
														<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
															<i class="fa fa-filter" aria-hidden="true"></i>
														</div>
														<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
															<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
																<p style="padding-left: 5px;">Organisation</p>
																<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="getLocationOrg(this.value);" list="organisationList" key="" /><!-- headerKey="" headerValue="All Organisations"  -->
															</div>
							
															<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
																<p style="padding-left: 5px;">Location</p>
																<div id="locationdivid">
																<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId"
																	listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
																</div>
															</div>
														</div>
													</div>
												</div>
											</tr> 
											<tr>
												<td class="txtlabel alignRight">Select File to Import</td>
												<td ><s:file name="fileUpload" label="Select a File to upload" size="20" accept=".xls" /></td>
											</tr>  
											<tr>
												<td colspan="2" align="center"><s:submit value="Import File" name="importSubmit" id="importSubmit" align="center" cssClass="btn btn-primary" /></td>
											</tr>
											<tr>
												<td colspan="2" align="right"> 
													<a onclick="generateEmployeeReportingStructure()"  href="javascript:void(0)" ><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
												</td>
											</tr> 
										</table> 
										</s:form>
									</div>
								</div>
							<!--End of div  -->	
							<% } %>
                   		</div>
                   		
                   		<!-- ===start parvez date: 23-02-2023=== -->
                   		<%-- <div class="col-lg-5 col-md-5 col-sm-12">
							<%if((mode == null || mode.equals("") || mode.equalsIgnoreCase("null")) || (mode != null && mode.equals("4"))) { %>
								<div style="width:100%;float:left;border: solid 1px #cccccc;margin-top: 40px">
									<p class="past">Quick bulk upload for employee Bank details</p>
									<div style="text-align:center; padding:10px;">
										<s:form theme="simple" action="ImportEmpBankDetails" method="POST" enctype="multipart/form-data">
											<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage %>"/>
											<input type="hidden" name="mode" id="mode" value="<%=mode %>"/>
											<table style="width:100%" class="table">
												<tr>
													<td class="txtlabel alignRight">Select File to Import</td>
													<td><s:file name="fileUpload" label="Select a File to upload" size="20" accept=".xls,.xlsx" /></td>
												</tr>  
												<tr>
													<td colspan="2" align="center"><s:submit value="Import File" name="importSubmit" id="importSubmit" align="center" cssClass="btn btn-primary" /></td>
												</tr>
												<tr>
													<td colspan="2" align="right"> 
														<a onclick="generateEmployeeBankDetails()"  href="javascript:void(0)" ><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
													</td>
												</tr> 
											</table> 
										</s:form>
									</div>
								</div>
							<!--End of div  -->	
							<% } %>
                   		</div> --%>
                    	<!-- ===end parvez date: 23-02-2023=== -->	
 						
						<div style="float: left;width:100%;margin-top: 10px;">
						<% System.out.println("sbmsg in jsp "+session.getAttribute("sbMessage"));
							if(request.getAttribute("sbMessage")!=null) {%>
							<%=(String)request.getAttribute("sbMessage")%>
							<% request.setAttribute("sbMessage","");%>	
							<%} else if(session.getAttribute("sbMessage")!=null) {%>
							<%=(String)session.getAttribute("sbMessage") %>	
								<% session.setAttribute("sbMessage",""); %>	
						<%}%>
						</div>
					</div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")){ %>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%} %>
<script>
$(document).ready(function(){
	$("#signUpForm_emailEmployee").click(function(){
		$(".validateRequired").prop('required',true);
		$("#signUpForm_email").prop('type','email');
	});
});
</script>
 


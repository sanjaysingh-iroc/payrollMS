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
 
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script> 
 
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$("#f_strWLocation").multiselect().multiselectfilter();
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
                    	<div class="row row_without_margin">
                    	<!-- Created by Dattatray Date : 07-July-21 Note : added col-lg-5 col-md-5 to  col-lg-12 col-md-12 -->
                    		<div class="col-lg-12 col-md-12 col-sm-12">
								<div style="width:100%;float:left;border: solid 1px #cccccc;margin-top: 40px">
									<p class="past">Quick bulk upload your Candidates</p> 
									<div style="text-align:center; padding:10px;">
						<!-- ===start parvez date: 01-11-2021 Note: theme="simple" is written two time=== -->
										<%-- <s:form theme="simple" action="ImportCandidate" method="POST" theme="simple" enctype="multipart/form-data"> --%>
										<s:form theme="simple" action="ImportCandidate" method="POST" enctype="multipart/form-data">
											<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage %>"/>
											<table style="width:100%" class="table">
												<tr>
													<td class="txtlabel alignRight">Select File to Import:<sup>*</sup></td>
													<!-- Created by Dattatray Date : 07-July-21 Note : added fileUpload and cssClass and onchange properties -->
													<td><s:file name="fileUpload" id="fileUpload"  label="Select a File to upload" size="20" accept=".xlsx" cssClass="validateRequired" onchange="readFileURL(this, 'file');"/></td>
												</tr>  
												<tr>
													<td colspan="2" align="center"><s:submit value="Import" name="importSubmit" id="importSubmit" align="center" cssClass="btn btn-primary" /></td>
												</tr>
												<tr>
													<td colspan="2" align="right"> 
														<a target="_blank" href="<%=request.getContextPath() %>/import/Import8StepCandidate.xlsx" title="Download Sample File" ><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
													</td>
												</tr> 
											</table> 
										</s:form>
									</div>
								</div>
                    		</div>
                    		
							<div style="float: left;width:100%;margin-top: 10px;">
							<% //System.out.println("sbmsg in jsp "+session.getAttribute("sbMessage"));
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

<script type="text/javascript">

$(document).ready(function(){
	$("#signUpForm_emailEmployee").click(function(){
		$(".validateRequired").prop('required',true);
		$("#signUpForm_email").prop('type','email');
	});
	//Start Dattatray Date:07-July-2021
	$("#importSubmit").click(function(){
		$(".validateRequired").prop('required', true);
	});
	//End Dattatray Date:07-July-2021
});

//Start Dattatray Date:07-July-2021
function readFileURL(input, targetDiv) {
     	fileValidation();
         if (input.files && input.files[0]) {
             var reader = new FileReader();
             reader.onload = function (e) {
                 $('#'+targetDiv).attr('path', e.target.result);
             };
             reader.readAsDataURL(input.files[0]);
         }
     }

function fileValidation() {
    var fileInput = document.getElementById('fileUpload');
    var filePath = fileInput.value;
    var allowedExtensions = /(\.xlsx)$/i;
      
    if (!allowedExtensions.exec(filePath)) {
        alert('Please select .xlsx format');
        fileInput.value = '';
        return false;
    } 
}
// End Dattatray Date:07-July-2021

</script>
 
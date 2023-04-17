<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%UtilityFunctions uF = new UtilityFunctions(); %>	   
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Import Attendance" name="title"/>
    </jsp:include>  --%>
    
<script>
    function getLocationList(displayID, id) {
    	
    	getContent(displayID, 'GetOrgWLocationList.action?OID='+id);
    }
    
</script>
	
<script>

   function generateAtt_Format1(){
	   
	    var paycycle = document.getElementById("paycycle").value;
	    var f_org = document.getElementById("f_org").value;
	    var strLocation = document.getElementById("strLocation").value;
	    var strDepartment = document.getElementById("strDepartment").value;
	    var strSbu = document.getElementById("strSbu").value;
	    var strLevel = document.getElementById("strLevel").value;
	    var strGrade = document.getElementById("strGrade").value;
	    var strEmployeType = document.getElementById("strEmployeType").value;
				 
		 window.location='ApproveAttendance.action?exceldownload=f1'+'&f_org='+f_org+'&strLocation='+strLocation+'&strDepartment='+strDepartment+'&strSbu='+strSbu+'&strLevel='+strLevel
		+'&paycycle='+paycycle+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType; 
		
 	}

	  function generateAtt_Format2(){
		 // alert("generateAtt_Format2");
		  
		 	var paycycle = document.getElementById("paycycle").value;
		    var f_org = document.getElementById("f_org").value;
		    var strLocation = document.getElementById("strLocation").value;
		    var strDepartment = document.getElementById("strDepartment").value;
		    var strSbu = document.getElementById("strSbu").value;
		    var strLevel = document.getElementById("strLevel").value;
		    var strGrade = document.getElementById("strGrade").value;
		    var strEmployeType = document.getElementById("strEmployeType").value;
		    
		    window.location='ApproveAttendance.action?exceldownload=f2'+'&f_org='+f_org+'&strLocation='+strLocation+'&strDepartment='+strDepartment+'&strSbu='+strSbu+'&strLevel='+strLevel
			+'&paycycle='+paycycle+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType; 
	} 
	
	function generateAtt_Format3(){
		
		var paycycle = document.getElementById("paycycle").value;
	    var f_org = document.getElementById("f_org").value;
	    var strLocation = document.getElementById("strLocation").value;
	    var strDepartment = document.getElementById("strDepartment").value;
	    var strSbu = document.getElementById("strSbu").value;
	    var strLevel = document.getElementById("strLevel").value;
	    var strGrade = document.getElementById("strGrade").value;
	    var strEmployeType = document.getElementById("strEmployeType").value;
	    
	    window.location='ApproveAttendance.action?exceldownload=f3'+'&f_org='+f_org+'&strLocation='+strLocation+'&strDepartment='+strDepartment+'&strSbu='+strSbu+'&strLevel='+strLevel
		+'&paycycle='+paycycle+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType; 
	}
	
	function generateAtt_Format4(){
		var paycycle = document.getElementById("paycycle").value;
	    var f_org = document.getElementById("f_org").value;
	    var strLocation = document.getElementById("strLocation").value;
	    var strDepartment = document.getElementById("strDepartment").value;
	    var strSbu = document.getElementById("strSbu").value;
	    var strLevel = document.getElementById("strLevel").value;
	    var strGrade = document.getElementById("strGrade").value;
	    var strEmployeType = document.getElementById("strEmployeType").value;
	    
	    window.location='ApproveAttendance.action?exceldownload=f4'+'&f_org='+f_org+'&strLocation='+strLocation+'&strDepartment='+strDepartment+'&strSbu='+strSbu+'&strLevel='+strLevel
		+'&paycycle='+paycycle+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
	}
	function generateAtt_Leave_Form(){
		var paycycle = document.getElementById("paycycle").value;
	    var f_org = document.getElementById("f_org").value;
	    var strLocation = document.getElementById("strLocation").value;
	    var strDepartment = document.getElementById("strDepartment").value;
	    var strSbu = document.getElementById("strSbu").value;
	    var strLevel = document.getElementById("strLevel").value;
	    var strGrade = document.getElementById("strGrade").value;
	    var strEmployeType = document.getElementById("strEmployeType").value;
	    
	    window.location='ApproveAttendance.action?exceldownload=Leave_Form'+'&f_org='+f_org+'&strLocation='+strLocation+'&strDepartment='+strDepartment+'&strSbu='+strSbu+'&strLevel='+strLevel
		+'&paycycle='+paycycle+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
		
	} 
	
	function getSelectedValue(selectId) {
		var choice = document.getElementById(selectId);
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
		return exportchoice;
	}

</script>

	<section class="content">
	    <div class="row jscroll">
	        <section class="col-lg-12 connectedSortable">
            
                    <div class="leftbox reportWidth">
                        <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
                        <%session.removeAttribute(IConstants.MESSAGE);%>
                        <div style="width: 95%; float: left; margin-top: 40px;">
                            <h4>Attendance Import</h4>
                        </div>
                        <div class="row row_without_margin">
         
					
					<!--**************************************FROM-2*****************************************  --> 	
           				<s:hidden name="pageFrom" id="pageFrom"/>
						<s:hidden name="paycycle" id="paycycle"/>
						<s:hidden name="f_org" id="f_org"/>
							
						<s:hidden name="strDepartment" id="strDepartment" />
						<s:hidden name="strLocation" id="strLocation"/>
						<s:hidden name="strSbu" id="strSbu"/>
							
						<s:hidden name="strLevel" id="strLevel"/>
						<s:hidden name="strGrade" id="strGrade"/>
						<s:hidden name="strEmployeType" id="strEmployeType"/>
               
						<div class="col-lg-6 col-md-6 col-sm-12">
                        		<!-- =====================================Format 1(Common Import DailyHrz)=================================== -->
		                        <div style="border: 1px solid rgb(204, 204, 204);">
		                            <p class="past">Bulk Attendance Format 1: Att_Form_0001</p>
		                            <div style="text-align: center; padding: 10px;">
		                                <s:form enctype="multipart/form-data" method="POST" action="ImportAttendance"  name="ImportEmployees" id="ImportEmployees" theme="simple">
		                                    <table style="width: 100%;" class="table">
		                                        <tbody>
		                                            <tr>
		                                                <td class="txtlabel alignRight">Select File to Import</td>
		                                                <td>
		                                                    <%-- <s:file name="fileUpload" label="Select a File to upload" size="20" /> --%>
		                                                    <input name="fileUpload" size="20" value="" id="ImportEmployees_fileUpload" required="" type="file" accept=".xlsx,.xls">
		                                                </td>
		                                            </tr>
		                                            <tr>
		                                                <td align="center" colspan="2"><input type="submit" class="btn btn-primary" value="Import File" id="ImportEmployees_0"></td>
		                                            </tr>
		                                            <tr>
		                                                <td align="right" colspan="2">
		                                                
		                                               	 <a title="Download Sample File" href="import/Att_Form_0001.xls" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
		                                               	<!-- <a onclick="generateAtt_Format1()" href="javascript:void(0)">Download Sample File</a> -->
		                                               
		                                                </td>
		                                            </tr>
		                                        </tbody>
		                                    </table>
		                                </s:form>
		                            </div>
		                        </div>
                        	</div>

				
					<!--**************************************FROM-2*****************************************  --> 	
                        	
                        	 <div class="col-lg-6 col-md-6 col-sm-12">
                        		<!-- =====================================Format 2(Common Import DailyHrz)=================================== -->
		                        <div style="border: 1px solid rgb(204, 204, 204);">
		                            <p class="past">Bulk Attendance Format 2: Att_Form_0002</p>
		                            <div style="text-align: center; padding: 10px;">
		                                <s:form enctype="multipart/form-data" method="POST" action="ImportAttendance1"  name="ImportEmployees" id="ImportEmployees" theme="simple">
											<s:hidden name="pageFrom" id="pageFrom"/>
		                                    <table style="width: 100%;" class="table">
		                                        <tbody>
		                                            <tr>
		                                                <td class="txtlabel alignRight">Select File to Import</td>
		                                                <td>
		                                                    <%-- <s:file name="fileUpload1" label="Select a File to upload" size="20" /> --%>
		                                                   <input name="fileUpload1" size="20" value="" id="ImportEmployees_fileUpload" required="" type="file" accept=".xls,.xlsx">
		                                                </td>
		                                            </tr>
		                                            <tr>
		                                                <td align="center" colspan="2"><input type="submit" class="btn btn-primary" value="Import File" id="ImportEmployees_0"></td>
		                                            </tr>
		                                            <tr>
		                                                <td align="right" colspan="2">
															<a title="Download Sample File" href="import/Att_Form_0002.xls" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a> 		                                             
															 <!-- <a onclick="generateAtt_Format2()"  href="javascript:void(0)">Download Sample File</a>  -->
  														</td>
		                                            </tr>
		                                        </tbody>
		                                    </table>
		                                </s:form>
		                            </div>
		                        </div>
                        	</div> 
                        	
                        	
					<!--**************************************FROM-3*****************************************  --> 	
                        	
							<div class="col-lg-6 col-md-6 col-sm-12">
                        	<!-- =====================================Format 3(Common Import DailyHrz)=================================== -->
                        	 	<div style="border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
									<p class="past">Bulk Attendance Format 3: Actual Days</p> 
									<div style="text-align: center; padding: 10px;">
										<s:form enctype="multipart/form-data" method="POST" action="ImportAttendance"  name="ImportEmployees" id="ImportEmployees" theme="simple">
											<s:hidden name="pageFrom" id="pageFrom"/>
											<table style="width: 100%;"  class="table">
												<tbody>
													<tr>
														<td class="txtlabel alignRight">Select File to Import</td>
														<td>
															<%-- <s:file name="fileUploadNew3" label="Select a File to upload" size="20" />--%>
															<input name="fileUploadNew3" size="20" value="" id="ImportEmployees_fileUploadNew3" required="" type="file" accept=".xls,.xlsx">
														</td> 
													</tr>  
													<tr>
														<td align="center" colspan="2"><input type="submit" class="btn btn-primary" value="Import File" id="ImportEmployees_0"></td>
													</tr>
													<tr>
														<td align="right" colspan="2">
															<!-- <a title="Download Sample File" href="import/Att_Form_0003.xlsx" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a> -->
															<a onclick="generateAtt_Format3()"  href="javascript:void(0)">Download Sample File</a>
														</td>
													</tr>
												</tbody>
											</table>
										</s:form>
									</div>
								</div>
							</div>
							
				
					<!--**************************************FROM-4*****************************************  --> 	
							
						<div class="col-lg-6 col-md-6 col-sm-12">
							<!-- =====================================Format 4(Common Import)=================================== -->
                        		<div style="border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
									<p class="past">Bulk Attendance Format 4</p> 
									<div style="text-align: center; padding: 10px;">
										<s:form enctype="multipart/form-data" method="POST" action="ImportAttendance"  name="ImportEmployees" id="ImportEmployees" theme="simple">
											<s:hidden name="pageFrom" id="pageFrom"/>
											<table style="width: 100%;"  class="table">
												<tbody>
													<tr>
														<td class="txtlabel alignRight">Select File to Import</td>
														<td>
															<%-- <s:file name="fileUploadNew4" label="Select a File to upload" size="20" /> --%>
															<input name="fileUploadNew4" size="20" value="" id="ImportEmployees_fileUploadNew4" required="" type="file" accept=".xls,.xlsx">
														</td>
													</tr>  
													<tr>
														<td align="center" colspan="2"><input type="submit" class="btn btn-primary" value="Import File" id="ImportEmployees_0"></td>
													</tr>
													<tr>
													<td align="right" colspan="2"><a title="Download Sample File" href="import/Att_Form_0004.xlsx" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a></td>
													<!-- <td align="right" colspan="2"><a onclick="generateAtt_Format4()"  href="javascript:void(0)">Download Sample File</a></td> -->
													</tr>
												</tbody>
											</table>
										</s:form>
									</div>
								</div>
							</div>
						
				
						<!--**************************************Zicon Format*****************************************  --> 	
							
							<div class="col-lg-6 col-md-6 col-sm-12">
							<!-- =====================================Format 4(Common Import)=================================== -->
                        		<div style="border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
									<p class="past">Bulk Attendance Biometric Zicon Format</p> 
									<div style="text-align: center; padding: 10px;">
										<s:form enctype="multipart/form-data" method="POST" action="ImportAttendance"  name="ImportEmployees" id="ImportEmployees" theme="simple">
											<s:hidden name="pageFrom" id="pageFrom"/>
											<table style="width: 100%;"  class="table">
												<tbody>
													<tr>
														<td class="txtlabel alignRight">Select File to Import</td>
														<td>
															<%-- <s:file name="fileUploadNew4" label="Select a File to upload" size="20" /> --%>
															<input name="fileUploadZicon" size="20" value="" id="ImportEmployees_fileUploadZicon" required="" type="file" accept=".xls,.xlsx">
														</td>
													</tr>  
													<tr>
														<td align="center" colspan="2"><input type="submit" class="btn btn-primary" value="Import File" id="ImportEmployees_0"></td>
													</tr>
													<!-- <tr>
														<td align="right" colspan="2"><a title="Download Sample File" href="import/Att_Form_0004.xlsx" target="_blank">Download Sample File</a></td>
													</tr> -->
												</tbody>
											</table>
										</s:form>
									</div>
								</div>
							</div>
							
                        </div>
                        
 <!--**************************************Leave Format*****************************************  --> 	
                        
                        <div style="width: 95%; float: left; margin-top: 40px;">
                            <h4>Leave Entry Import</h4>
                        </div>
                       
                         <div class="row row_without_margin" style="margin-bottom: 30px;">
                        	<div class="col-lg-12 col-md-12 col-sm-12">
                        		<!-- =====================================Format 1(Common Leave Import)=================================== -->
		                        <div style="border: 1px solid rgb(204, 204, 204);">
		                            <p class="past">Bulk Leave Entry Format 1: Leave_Form_0001</p>
		                            <div style="text-align: center; padding: 10px;">
		                                <s:form enctype="multipart/form-data" method="POST" action="ImportLeave"  name="frm_ImportLeave" id="frm_ImportLeave" theme="simple">
											<s:hidden name="pageFrom" id="pageFrom"/>
		                                    <table style="width: 100%;" class="table">
		                                        <tbody>
		                                            <tr>
		                                                <td class="txtlabel alignRight">Select File to Import</td>
		                                                <td>
		                                                   <%--  <s:file name="fileUpload" label="Select a File to upload" size="20" /> --%>
		                                                   <input name="fileUpload" size="20" value="" id="frm_ImportLeave_fileUpload" required="" type="file" accept=".xls,.xlsx">
		                                                </td>
		                                            </tr>
		                                            <tr>
		                                                <td align="center" colspan="2"><input type="submit" class="btn btn-primary" value="Import File" id="ImportLeaves_0"></td>
		                                            </tr>
		                                            <tr>
		                                                <td align="right" colspan="2">
		                                               		<a title="Download Sample File" href="import/Leave_Form_0001.xlsx" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
		                                               		<!-- <a onclick="generateAtt_Leave_Form()"  href="javascript:void(0)">Download Sample File</a> -->
		                                                </td>
		                                            </tr>
		                                        </tbody>
		                                    </table>
		                                </s:form>
		                            </div>
		                        </div>
                        	</div>
                        </div>
                        
                        <%
                            java.util.List couterlist = (java.util.List)request.getAttribute("alReport");
                            if(couterlist!=null){
                            %>
                        <div style="width: 95%; float: left; margin-top: 40px;">
                            <table style='width:100%;' class="table">
                                <tbody style='background-color:#EFEFEF;'>
                                    <% for (int i=0; i<couterlist.size(); i++) {%>
                                    <tr>
                                        <td align="left"><%= couterlist.get(i) %></td>
                                    </tr>
                                    <%} %>
                                </tbody>
                            </table>
                        </div>
                        <%} %>
                    </div>
        </section>
    </div>
</section>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>


<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);

List alResignationEntry = (List)request.getAttribute("alResignationEntry");
if(alResignationEntry==null)alResignationEntry=new ArrayList();
String strDF = (String)request.getAttribute("DISPLAY_FORM");

Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
String policy_id = (String) request.getAttribute("policy_id");


%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	
	$("#empResignationDate").datepicker({format: 'dd/mm/yyyy'});
	
	$("input[type='submit'").click(function(){
		$("#formAddNewRow").find('.validatedRequired').filter(':hidden').prop('required',false);
		$("#formAddNewRow").find('.validatedRequired').filter(':visible').prop('required',true);
	});
	
});


function getApprovalStatus(id, empname) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Work flow of '+empname);
	$.ajax({
		url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=10",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>



<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div class="leftbox reportWidth"> 
						<%=uF.showData((String)request.getAttribute(IConstants.MESSAGE), "") %>
						<s:form theme="simple" name="formAddNewRow" id="formAddNewRow" action="ResignationEntry1" method="POST" cssClass="formcss">
							<s:hidden name="strResignationId" />
							<s:hidden name="emp_id" />
							<s:hidden name="from" />
							<%if(alResignationEntry.size()==0 || strDF!=null){ %>
								<input type="hidden" name="policy_id" id="policy_id" value="<%=(String)request.getAttribute("policy_id") %>"/>
								<table border="0" class="table table_no_border form-table autoWidth">
									<tr>
										<td valign="top">Resignation Date:<sup>*</sup></td>
										<td><input type="text" name="empResignationDate" id="empResignationDate" class="validateRequired" value="<%=uF.showData((String)request.getAttribute("empResignationDate"), "") %>" /><span class="hint">Employee's date of Resignation.<span class="hint-pointer">&nbsp;</span></span></td>
									</tr>
									<tr>
										<td valign="top">Enter Reason for Resignation:</td>
										<td>
											<s:textarea name="empResignationReason"  id="empResignationReason" cssClass="validateRequired"  rows="7" cols="45" required="" ></s:textarea>
										</td>
									</tr>
									
									<%-- <tr>
										<td align="center">
										<s:submit cssClass="input_button" onclick="return confirm('Are you sure you wish to resign?')" value="Submit Reason" />
										</td>
									</tr> --%>
									
									<%
									if(uF.parseToBoolean(CF.getIsWorkFlow())){		
								    	if(hmMemberOption!=null && !hmMemberOption.isEmpty() ){
											Iterator<String> it1=hmMemberOption.keySet().iterator();
											while(it1.hasNext()){
												String memPosition=it1.next();
												String optiontr=hmMemberOption.get(memPosition);					
												out.println(optiontr); 
											}
								%>
											<tr><td>&nbsp;</td>
											<td><input  type="submit" name="submit" id="submitButton" value="Submit Reason" class="btn btn-primary" onclick="return confirm('Are you sure you wish to resign?')"/></td>
											</tr>
										<% }else{%>
											 <tr><td colspan="2">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>
										 <%}%>
									<%}else{%>
										<tr><td>&nbsp;</td>
											<td><input  type="submit" name="submit" id="submitButton" value="Submit Reason" class="btn btn-primary"/></td>
										</tr>
									<%}
									%> 
									
								</table>
							<%} else { %>
							
							<table border="0" class="table">
								<%
								for(int i=0; i<alResignationEntry.size(); i++){
									List alInner = (List)alResignationEntry.get(i);
									if(alInner==null)alInner=new ArrayList();
									
								%>
								<tr>
									<td>
									<%= (String)alInner.get(1)%>
									</td>
								</tr>
								<% }
								}%>
							</table>
							
						
						</s:form>
						
						</div>
                </div>
            </div>
        </section>
    </div>
</section>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


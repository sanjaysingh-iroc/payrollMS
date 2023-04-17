<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">
<script type="text/javascript">
	$(document).ready(function() {
		$('#lt1').DataTable({
			dom: 'lBfrtip',
	        buttons: [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ]
		});
		$("#f_strWLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_service").multiselect().multiselectfilter();
    	$("#f_level").multiselect().multiselectfilter(); 
	});
    function submitForm(type){
    	document.frm_LeaveCard.exportType.value='';
    	var org = document.getElementById("f_org").value;
    	var paycycle = document.getElementById("paycycle").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	var level = getSelectedValue("f_level");
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
    		+'&strLevel='+level+'&paycycle='+paycycle;
    	}
    	//alert("service ===>> " + service);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'LeaveCard1.action?f_org='+org+paramValues, 
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	//console.log(result);
            	$("#divResult").html(result);
       		}
    	});
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

<%
    UtilityFunctions uF = new UtilityFunctions();
    List reportListPrint = (List) request.getAttribute("reportListPrint");
%>
    
<!-- Custom form for adding new records -->
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=IMessages.TLeaveCard %>" name="title" />     
    </jsp:include> --%>

                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                        <div class="desgn" style="margin-bottom: 5px;color:#232323;">
                            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                                <div class="box-header with-border">
                                    <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
                                    <div class="box-tools pull-right">
                                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                                    </div>
                                </div>
                                <!-- /.box-header -->
                                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                                    <div class="content1">
                                        <s:form name="frm_LeaveCard" id="frm_LeaveCard" action="LeaveCard1" theme="simple">
                                            <s:hidden name="exportType"></s:hidden>
                                            <div class="row row_without_margin">
												<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
													<i class="fa fa-filter"></i>
												</div>
												<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
                                                    	<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">Location</p>
                                                    	<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">Department</p>
                                                    	<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
													</div>
					
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">SBU</p>
                                                    	<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">Level</p>
                                                    	<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
													</div>
												</div>
											</div><br>
											<div class="row row_without_margin">
												<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
													<i class="fa fa-calendar"></i>
												</div>
												<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">Paycycle</p>
														<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key="" onchange="submitForm('2');"/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">&nbsp;</p>
														<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
													</div>
												</div>
											</div>
                                        </s:form>
                                    </div>
                                </div>
                                <!-- /.box-body -->
                            </div>
                        </div>
                        <%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
                        <%session.setAttribute(IConstants.MESSAGE, ""); %>
                        <!-- <div style="float: right; margin-bottom: 10px">
                            <a title="Download File" onclick="addRegularizeLeaveBalance();" href="javascript:void(0)">Regularize Leave Balance</a>
                            </div> --> 
                        <display:table name="reportListPrint" cellspacing="1" class="table table-bordered" id="lt1">
                            <display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
                            <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
                            <%-- <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Leave Type"><div style="padding:0 5px;background-color: <%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%>"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></div></display:column> --%>
                            <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Leave Type"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
                            <display:column style="text-align:right;" valign="top" title="Opening Balance"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
                            <display:column style="text-align:right;" valign="top" title="Accrued"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
                            <display:column style="text-align:right;" valign="top" title="Added"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
                            <display:column style="text-align:right;" valign="top" title="Closing Balance"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
                            <display:column style="text-align:right;" valign="top" title="Taken Paid"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
                            <display:column style="text-align:right;" valign="top" title="Taken Unpaid"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>
                            <display:column style="text-align:right;" valign="top" title="Previous Paid"><%=((java.util.List) pageContext.getAttribute("lt1")).get(10)%></display:column>
                            <display:column style="text-align:right;" valign="top" title="Future Paid"><%=((java.util.List) pageContext.getAttribute("lt1")).get(11)%></display:column>
                        </display:table>
                </div>
                <!-- /.box-body -->
</div>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	 $("select[multiple='multiple']").multiselect().multiselectfilter();
});
	
function getSalaryStructure(emp_id, grade_from,grade_to,fitmentMonth,fitmentYear,empName) {
 	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Salary Structure of '+empName);
	$.ajax({
		url : "ApproveBasicFitmentStructure.action?operation=A&emp_id="+emp_id+"&grade_from="+grade_from+"&grade_to="+grade_to+"&fitmentMonth="+fitmentMonth+"&fitmentYear="+fitmentYear,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
	
function getDefer(emp_id, grade_from,grade_to,fitmentMonth,fitmentYear,empName) {
    var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Defer of '+empName);
	$.ajax({
		url : "DeferBasicFitment.action?emp_id="+emp_id+"&grade_from="+grade_from+"&grade_to="+grade_to+"&fitmentMonth="+fitmentMonth+"&fitmentYear="+fitmentYear,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});			
}
	
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
%>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-none nav-tabs-custom">
            	<ul class="nav nav-tabs">
					<li><a href="javascript:void(0)" onclick="window.location='EmpSalaryApproval.action'" data-toggle="tab">Salary Approval</a></li>
					<li class="active"><a href="javascript:void(0)" onclick="window.location='ApproveBasicFitment.action'" data-toggle="tab">Basic Fitment Approval</a></li>
				</ul>
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                        <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
                        <%session.setAttribute(IConstants.MESSAGE,""); %>
						<s:form name="frmBasicFit" action="ApproveBasicFitment" theme="simple">
							<div class="box box-default collapsed-box">
								<div class="box-header with-border">
								    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
								    <div class="box-tools pull-right">
								        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
								        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
								    </div>
								</div>
								<div class="box-body" style="padding: 5px; overflow-y: auto;">
									<div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Organisation</p>
												<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" 
													headerKey="" headerValue="All Organisations" onchange="document.frmBasicFit.submit();" list="organisationList" key="" />
											</div>
											
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Location</p>
												<s:select theme="simple" name="strWLocation" id="strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" 
													list="wLocationList" key="" />
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Department</p>
												<s:select name="department" id="department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">&nbsp;</p>
												<input type="Submit" name="Submit" value="Submit" class="btn btn-primary"/>
											</div>
										</div>
									</div>									
								</div>
							</div>
					
						</s:form>
					
						<div>
							<ul style="float: left; width: 99%; margin-bottom: 50px;">
								 <% 
								 String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
								 Map<String, Map<String, String>> hmFitmentMap = (Map<String, Map<String, String>>)request.getAttribute("hmFitmentMap");
								 if(hmFitmentMap==null) hmFitmentMap = new LinkedHashMap<String, Map<String, String>>();
								 Iterator<String> it = hmFitmentMap.keySet().iterator();
								 while(it.hasNext()){
									String strEmpId = it.next();	 
									Map<String, String> hmBasicFitment = hmFitmentMap.get(strEmpId);
								 %>
								 		<li class="list"><%=hmBasicFitment.get("EMP_DATA") %></li>
								 <%
									}if(hmFitmentMap.size()==0){
								%>
										<li class="nodata msg"> No Data Found. </li>
								<%
									}
								%>
							 </ul>
						</div>
					</div>
                </div>
                <!-- /.box-body -->
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
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
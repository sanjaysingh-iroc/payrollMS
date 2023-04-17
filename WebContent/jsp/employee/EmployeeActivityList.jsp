<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<style>
.viewmore {clear: both;}

ul.issuereasons>li {
	padding: 2px;
	margin: 3px;
}
</style>

	<div style="float: left; width: 99%;">
		<div id="details">
		<% List<List<String>> newJoineeEmpList = (List<List<String>>) request.getAttribute("newJoineeEmpList"); %>
			<div class="box box-default" style="margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">New Joinees &nbsp;<span class="label label-primary"><%=newJoineeEmpList != null ? newJoineeEmpList.size() : "0" %> new joinees</span>
	                    <span class="viewmore" style="float: right;"><a href="EmployeeActivity.action?empType=I">
		       				<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Activity.."></i>
		       			</a></span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 300px;">
                    <ul class="issuereasons">
					<% for(int i=0; newJoineeEmpList!=null && i<newJoineeEmpList.size(); i++) { %>
						<li style="float:left;width:93%"><%=newJoineeEmpList.get(i) %></li>
					<% } if(newJoineeEmpList==null || newJoineeEmpList.isEmpty() || (newJoineeEmpList!=null && newJoineeEmpList.size()==0)) { %>
						<li style="float:left;width:93%" class="tdDashLabel"> No new joinee for a week. </li>
					<% } %>
					</ul>
                </div>
                <!-- /.box-body -->
            </div>
		</div>
		
		<div id="details">
		<% List<List<String>> confirmationEmpList = (List<List<String>>) request.getAttribute("confirmationEmpList"); %>
			<div class="box box-default collapsed-box" style="margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Confirmations &nbsp;<span class="label label-primary"><%=confirmationEmpList != null ? confirmationEmpList.size() : "0" %></span>
	                    <span class="viewmore" style="float: right;"><a href="EmployeeActivity.action?empType=C">
		       				<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Activity.."></i>
		       			</a></span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;max-height: 300px;">
                    <ul class="issuereasons">
					<% for(int i=0; confirmationEmpList!=null && i<confirmationEmpList.size(); i++) { %>
						<li style="float:left;width:93%"><%=confirmationEmpList.get(i) %></li>
					<% } if(confirmationEmpList==null || confirmationEmpList.isEmpty() || (confirmationEmpList!=null && confirmationEmpList.size()==0)) { %>
						<li style="float:left;width:93%" class="tdDashLabel"> No Confirmations. </li>
					<% } %>
					</ul>
                </div>
                <!-- /.box-body -->
            </div>
		</div>
		
		<div id="details">
		<% List<List<String>> retirementEmpList = (List<List<String>>) request.getAttribute("retirementEmpList"); %>
			<div class="box box-default collapsed-box" style="margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Retirements &nbsp;<span class="label label-primary"><%=retirementEmpList != null ? retirementEmpList.size() : "0" %></span>
	                    <span class="viewmore" style="float: right;"><a href="EmployeeActivity.action?empType=RETIRE">
		       				<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Activity.."></i>
		       			</a></span>
	       			</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;max-height: 300px;">
                    <ul class="issuereasons">
					<% for(int i=0; retirementEmpList!=null && i<retirementEmpList.size(); i++) { %>
						<li style="float:left;width:93%"><%=retirementEmpList.get(i) %></li>
					<% } if(retirementEmpList==null || retirementEmpList.isEmpty() || (retirementEmpList!=null && retirementEmpList.size()==0)) { %>
						<li style="float:left;width:93%" class="tdDashLabel"> No Retirements. </li>
					<% } %>
					</ul>
                </div>
                <!-- /.box-body -->
            </div>
		</div>
		
		<div id="details">
		<% List<List<String>> resignationEmpList = (List<List<String>>) request.getAttribute("resignationEmpList"); %>
			<div class="box box-default collapsed-box" style="margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Resignations&nbsp;<span class="label label-primary"><%=resignationEmpList != null ? resignationEmpList.size() : "0" %></span>
	                    <span class="viewmore" style="float: right;"><a href="EmployeeActivity.action?empType=R">
		       				<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Activity.."></i>
		       			</a></span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;max-height: 300px;">
                    <ul class="issuereasons">
					<% for(int i=0; resignationEmpList!=null && i<resignationEmpList.size(); i++) { %>
						<li style="float:left;width:93%"><%=resignationEmpList.get(i) %></li>
					<% } if(resignationEmpList==null || resignationEmpList.isEmpty() || (resignationEmpList!=null && resignationEmpList.size()==0)) { %>
						<li style="float:left;width:93%" class="tdDashLabel"> No Resignations. </li>
					<% } %>
					</ul>
					
                </div>
                <!-- /.box-body -->
            </div>
		</div>
		
		<div id="details">
		<% List<List<String>> finalDayEmpList = (List<List<String>>) request.getAttribute("finalDayEmpList"); %>
			<div class="box box-default collapsed-box" style="margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">Final Day Employees&nbsp;<span class="label label-primary"><%=finalDayEmpList != null ? finalDayEmpList.size() : "0" %></span>
	                    <span class="viewmore" style="float: right;"><a href="EmployeeActivity.action?empType=FD">
		       				<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Activity.."></i>
		       			</a></span>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;max-height: 300px;">
                    <ul class="issuereasons">
					<% for(int i=0; finalDayEmpList!=null && i<finalDayEmpList.size(); i++) { %>
						<li style="float:left;width:93%"><%=finalDayEmpList.get(i) %></li>
					<% } if(finalDayEmpList==null || finalDayEmpList.isEmpty() || (finalDayEmpList!=null && finalDayEmpList.size()==0)) { %>
						<li style="float:left;width:93%" class="tdDashLabel"> No final day Employees. </li>
					<% } %>
					</ul>
					
                </div>
                <!-- /.box-body -->
            </div>
		</div>
	</div>


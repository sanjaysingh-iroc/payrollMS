<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">


$(function() {
    $("#strStartDate").datepicker({format: 'dd/mm/yyyy'});
    $("#strEndDate").datepicker({format: 'dd/mm/yyyy'});
    
    $("#strStartDate1").datepicker({format: 'dd/mm/yyyy'});
    $("#strEndDate1").datepicker({format: 'dd/mm/yyyy'});
});


</script>

<script type="text/javascript">
$(function() {
	$("#f_service").multiselect().multiselectfilter();
	$("#f_client").multiselect().multiselectfilter();
});    
</script>

	<%
		UtilityFunctions uF = new UtilityFunctions();
	%>

<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
    
	<div style="float: left; margin: 20px;">
		<div style="float: left; width: 100%; margin-bottom: 10px; font-size: 14px; font-weight: bold;">SBU Wise: </div>
	    <s:form name="frmARManagementReportXml" action="ARManagementReportXml" theme="simple">
	    	<input type="hidden" name="fromPage" value="sbu" />
	    		<div class="desgn" style="float: left; margin-bottom: 5px;background:#f5f5f5; color:#232323;">
					<div style="float: left; width: 100%; margin-top: -5px; margin-bottom: 15px;">
						<div style="float: left; margin-top: 10px;">
							<i class="fa fa-filter"></i>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">SBU</p>
							<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"/>
						</div>
						
						<div id="fromToDIV" style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="text" name="strStartDate" id="strStartDate" placeholder="From Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strStartDate"), "") %>"/>
							<input type="text" name="strEndDate" id="strEndDate" placeholder="To Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strEndDate"), "") %>"/>
			      		</div>
				</div>
			</div>	
			
			<div style="float: left; margin-top: 15px; margin-left: 50px;"> <s:submit cssClass="btn btn-primary" name="generate" value="Generate XML" /></div>	
		</s:form>
	</div>
	
	<div style="float: left; border-top: 1px solid #f4f4f4; width: 100%;"></div>

    		<input type="hidden" name="fromPage" value="client" />
	<div style="float: left; margin: 20px;">
		<div style="float: left; width: 100%; margin-bottom: 10px; font-size: 14px; font-weight: bold;">Client Wise: </div>
	    <s:form name="frmARManagementReportXml" action="ARManagementReportXml" theme="simple">
    		<div class="desgn" style="float: left; margin-bottom: 5px;background:#f5f5f5; color:#232323;">
				<div style="float: left; width: 100%; margin-top: -5px; margin-bottom: 15px;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-filter"></i>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Client</p>
						<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" />
             		</div>
					
					<div id="fromToDIV" style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<input type="text" name="strStartDate" id="strStartDate1" placeholder="From Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strStartDate"), "") %>"/>
						<input type="text" name="strEndDate" id="strEndDate1" placeholder="To Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strEndDate"), "") %>"/>
		      		</div>
				</div>
			</div>	
		
		<div style="float: left; margin-top: 15px; margin-left: 50px;"> <s:submit cssClass="btn btn-primary" name="generate" value="Generate XML" /></div>	
		</s:form>
	</div>
	
</div>	


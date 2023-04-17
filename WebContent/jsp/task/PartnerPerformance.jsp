<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<div id="divResult">

<%	String btnSubmit = (String)request.getAttribute("btnSubmit");
//	System.out.println("btnSubmit ===>> " + btnSubmit);
	if(btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals("")) {
%>
	<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"> </script> --%>
	
<% } %>

 
<script> 

    $(function() {
        $( "#f_start" ).datepicker({format: 'dd/mm/yyyy'});
        $( "#f_end" ).datepicker({format: 'dd/mm/yyyy'});
        
        var value = document.getElementById("selectOne").value;
        checkSelectType(value);
    });
    
    function checkSelectType(value) {
    	
    	//fromToDIV financialYearDIV monthDIV paycycleDIV
    	if(value == '4') {
    		document.getElementById("fromToDIV").style.display = 'block';
    	} else {
    		document.getElementById("fromToDIV").style.display = 'none';
    	}
    }
    
    
    function submitForm(type) {
    	//strProType f_org
    	var data =  $("#frmPartnerPerformance").serialize();
    	//alert(data);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'PartnerPerformance.action?btnSubmit=Submit',
    		data: data,
    		success: function(result){
            	$("#divResult").html(result);
       		}
    	});
    }
</script>
 

<section class="content">
		<!-- title row -->
	<div class="row">
		<section class="content">
			<div class="col-lg-12 col-md-12 col-sm-12 box box-body">

				<s:form name="frmPartnerPerformance" id="frmPartnerPerformance" action="PartnerPerformance" theme="simple">
					<div class="box box-default collapsed-box">
						<div class="box-header with-border">
						    <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
						    <div class="box-tools pull-right">
						        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						    </div>
						</div>
						<!-- /.box-header -->
						<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Select Period</p>
										<s:select theme="simple" name="selectOne" id="selectOne" cssStyle="float:left; margin-right: 10px;" headerKey="1" 
											headerValue="Since last 3 months" list="#{'2':'Since last 6 months', '3':'Since last 1 year', '4':'From-To'}" onchange="checkSelectType(this.value);"/>
									</div>
									
									<div id="fromToDIV"  class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<%
										String f_start = (String)request.getAttribute("f_start");
										String f_end = (String)request.getAttribute("f_end");
										%>
										<input type="text" name="f_start" id="f_start" placeholder="From Date" style="width:85px !important;" value="<%=f_start %>"/>
										<input type="text" name="f_end" id="f_end" placeholder="To Date" style="width:85px !important;" value="<%=f_end %>"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<!-- <input type="button" name="btnSubmit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/> -->
										<s:submit value="Submit" cssClass="btn btn-primary" cssStyle="margin: 0px;" />
									</div>
								</div>
							</div>
							
						</div>
					</div>
				</s:form>
	
	<%
		UtilityFunctions uF = new UtilityFunctions();
	
		Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
		if (hmEmpName == null) hmEmpName = new HashMap<String, String>();
		Map<String, String> hmEmpDesigMap = (Map<String, String>) request.getAttribute("hmEmpDesigMap");
		if (hmEmpDesigMap == null) hmEmpDesigMap = new HashMap<String, String>();
	
		List<String> alProOwner = (List<String>) request.getAttribute("alProOwner");
		if (alProOwner == null) alProOwner = new ArrayList<String>();
		Map<String, String> hmPOActBillAmt = (Map<String, String>) request.getAttribute("hmPOActBillAmt");
		if (hmPOActBillAmt == null) hmPOActBillAmt = new HashMap<String, String>();
		Map<String, String> hmPOActIdealTime = (Map<String, String>) request.getAttribute("hmPOActIdealTime");
		if (hmPOActIdealTime == null) hmPOActIdealTime = new HashMap<String, String>();
		Map<String, String> hmPOActIdealTimeHRS = (Map<String, String>) request.getAttribute("hmPOActIdealTimeHRS");
		if (hmPOActIdealTimeHRS == null) hmPOActIdealTimeHRS = new HashMap<String, String>();
		int count = 0;
		int cnt = 0;
		for(int i = 0; i < alProOwner.size(); i++) {
			String strEmpId = alProOwner.get(i);
			
			if(i == 0 || cnt%4 == 0) {
				count = 0;
			}
	%>
	
	<% if(count == 0) { %>
	<div class="col-lg-12 col-md-12 col-sm-12" style="margin: 5px 0px;">
	<% } %>
		<div class="col-lg-3 col-md-6 col-sm-12" style="margin: 5px 0px;">
                 <div class="kpi_view1">
                     <div id="guageM_<%=strEmpId %>" class="gauge"></div>
                     <div id="guageT_<%=strEmpId %>" class="gauge"></div>
                     
                     <script>
					    document.addEventListener("DOMContentLoaded", function(event) {
					        var g1 = new JustGage({
					            id: "guageM_<%=strEmpId %>",
					            title: "",
					            label: "Money",
					            value: <%=uF.parseToDouble(hmPOActBillAmt.get(strEmpId+"ACT_AMT"))%>,
					            min: 0,
					            max: <%=uF.parseToDouble(hmPOActBillAmt.get(strEmpId+"BILL_AMT"))%>,
					            decimals: 0,
					            gaugeWidthScale: 0.6,
					            levelColors: [
		                          "#008000",
		                          "#FFFF00",
		                          "#FF0000"
		                        ]
					        });
					        
					        var g2 = new JustGage({
					            id: "guageT_<%=strEmpId %>",
					            title: "",
					            label: "Time",
					            value: <%=uF.parseToDouble(hmPOActIdealTimeHRS.get(strEmpId+"ACT_TIME_HRS"))%>,
					            min: 0,
					            max: <%=uF.parseToDouble(hmPOActIdealTimeHRS.get(strEmpId+"IDEAL_TIME_HRS"))%>,
					            decimals: 0,
					            gaugeWidthScale: 0.6,
					            levelColors: [
		                          "#008000",
		                          "#FFFF00",
		                          "#FF0000"
		                        ]
					        });
					    });
				    </script>
                  </div>
                  
                  <div class="emp_info">
                    <%-- <div class="info_row"><span>Name:</span><strong> <a target="_blank" href="ProjectPerformanceCP.action?empId=<%=strEmpId%>"><%= (String)hmEmpName.get(strEmpId)%></a></strong></div> --%>
                    <div class="info_row"><span>Name:</span><strong><%=(String)hmEmpName.get(strEmpId)%></strong></div>
                    <div class="info_row"><span>Designation:</span> <strong><%= uF.showData((String)hmEmpDesigMap.get(strEmpId), "-")%></strong></div> 
                  </div>
			</div>
		<% if(count == 3) { %>
		</div>
		<% } %>
			<% cnt++; count++; }
			if(cnt == 0) {
			%>
				<div>No projects have been added.</div>
			<% } %>

</div>
</section>

</div>
</section>

	<script src="scripts/charts/justgage/raphael-2.1.4.min.js" type="text/javascript"></script>
	<script src="scripts/charts/justgage/justgage.js" type="text/javascript"></script>
	
</div>

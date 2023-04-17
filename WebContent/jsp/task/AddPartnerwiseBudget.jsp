<div id="divResult">
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>


<script type="text/javascript">

function GetXmlHttpObject() {
    if (window.XMLHttpRequest) {
        // code for IE7+, Firefox, Chrome, Opera, Safari
        return new XMLHttpRequest();
    }
    if (window.ActiveXObject) {
    	// code for IE6, IE5
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
    return null;
}


	function calAmt(){
		
		var strTotalAmt = 0;
		/* alert(strTotalAmt); */
		
		var strAprAmt = $('#strAprilAmt').val();
		var strMayAmt = $('#strMayAmt').val();
		var strJunAmt = $('#strJuneAmt').val();
		var strJulAmt = $('#strJulyAmt').val();
		var strAugAmt = $('#strAugustAmt').val();
		var strSepAmt = $('#strSeptemberAmt').val();
		var strOctAmt = $('#strOctoberAmt').val();
		var strNovAmt = $('#strNovemberAmt').val();
		var strDecAmt = $('#strDecemberAmt').val();
		var strJanAmt = $('#strJanuaryAmt').val();
		var strFebAmt = $('#strFebruaryAmt').val();
		var strMarAmt = $('#strMarchAmt').val();
		
		if((isNaN(parseFloat(strAprAmt)))) {
			strAprAmt = 0;
		}
		if((isNaN(parseFloat(strMayAmt)))) {
			strMayAmt = 0;
		}
		if((isNaN(parseFloat(strJunAmt)))) {
			strJunAmt = 0;
		}
		if((isNaN(parseFloat(strJulAmt)))) {
			strJulAmt = 0;
		}
		if((isNaN(parseFloat(strAugAmt)))) {
			strAugAmt = 0;
		}
		if((isNaN(parseFloat(strSepAmt)))) {
			strSepAmt = 0;
		}
		if((isNaN(parseFloat(strOctAmt)))) {
			strOctAmt = 0;
		}
		if((isNaN(parseFloat(strNovAmt)))) {
			strNovAmt = 0;
		}
		if((isNaN(parseFloat(strDecAmt)))) {
			strDecAmt = 0;
		}
		if((isNaN(parseFloat(strJanAmt)))) {
			strJanAmt = 0;
		}
		if((isNaN(parseFloat(strFebAmt)))) {
			strFebAmt = 0;
		}
		if((isNaN(parseFloat(strMarAmt)))) {
			strMarAmt = 0;
		}
		
		strTotalAmt = parseFloat(strAprAmt)+parseFloat(strMayAmt)+parseFloat(strJunAmt)+parseFloat(strJulAmt)+parseFloat(strAugAmt)+parseFloat(strSepAmt)+parseFloat(strOctAmt)+parseFloat(strNovAmt)+parseFloat(strDecAmt)+parseFloat(strJanAmt)+parseFloat(strFebAmt)+parseFloat(strMarAmt);
		
		document.getElementById("strTotalAmt").value = strTotalAmt;
	}
	
	function submitForm() {
		
		var financialYear = document.getElementById("financialYear").value;
		var partnerId = document.getElementById("partnerId").value;
		
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'AddPartnerwiseBudget.action?financialYear='+financialYear+'&partnerId='+partnerId,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	//console.log(result);
	        	$("#divResult").html(result);
	   		}
		});
	}
	
	function addUpdateBudget(operation) {
		
		/* var partnerId = document.getElementById("partnerId").value; */
		var partnerId = $('#partnerId').val();
		var financialYear = document.getElementById("financialYear").value;
		var form_data = $("#frm_AddPartnerwiseBudget").serialize();
		$("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
	     	url: "AddPartnerwiseBudget.action",
	     	type: 'POST',
	     	data: form_data+'&operation='+operation+'&partnerId='+partnerId+'&financialYear='+financialYear,
	     	success: function(result){
	     		$("#divResult").html(result);
	     		/* $.ajax({
   	     			url:'AddPartnerwiseBudget.action',    
   	     			cache:false,
   	     			success:function(result){
	   	     			$("#divResult").html(result);
   	     			}
   	     		}); */
	     	}
	    });
	}
	
	/* $(function(){
		$("form").bind('submit',function(event) {
			
			var form_data = $("#"+this.id).serialize();
			$("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
   	     		url: "AddPartnerwiseBudget.action",
   	     		type: 'POST',
   	     		data: form_data,
   	     	    success: function(result){
   	     	    	$("#divResult").html(result);
   	     	    }
   	       });
		});
	}); */
	
	
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	List alPartnersBudget = (List) request.getAttribute("alPartnersBudget");
	
	List monthYearsList = (List) request.getAttribute("monthYearsList");
	
	Map<String, String> hmEmpProDetails = (Map<String, String>) request.getAttribute("hmEmpProDetails");
	if(hmEmpProDetails == null) hmEmpProDetails = new HashMap<String, String>();
	
	String BASEUSERTYPE = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	
%>
<section class="content">
	<div class="row jscroll">
		<div class="col-md-12">
			<div class="box box-primary">
			<%-- <s:form name="frm_AddPartnerwiseBudget" id="frm_AddPartnerwiseBudget" action="AddPartnerwiseBudget" theme="simple" method="post"> --%>	
				<div class="desgn" style="margin-bottom: 5px; color:#232323;">
					<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
						<div class="box-header with-border">
							<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div>
						<!-- /.box-header -->
						<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
							<s:form name="frm_AddPartnerwiseBudget1" id="frm_AddPartnerwiseBudget1" action="AddPartnerwiseBudget" theme="simple" method="post">
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
										<i class="fa fa-calendar"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
										<div id="financialYearDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Financial Year</p>
											<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerValue="Select Financial Year" list="financialYearList" />
							      		</div>
							      		
							      		<% 
							      			if(BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.ADMIN)){
							      		%>
							      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Partner</p>
											<s:select name="partnerId" id="partnerId" listKey="employeeId" listValue="employeeName" headerValue="Select Partner" list="partnerList" />
							      		</div>
							      		<% } else{ %>
							      			<s:hidden name="partnerId" id="partnerId"></s:hidden>
							      		<% } %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">&nbsp;</p>
											<input type="button" name="strSubmit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm();"/>
										</div>
									</div>
								</div>
							</s:form>
						</div>
			          <!-- /.box-body -->
					</div>
				</div>
				
				<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
				
					<div style="width: 99%; float: left; height:98%; padding:0px 20px;">
						<div style="font-weight: bold;">Partner Name: <%=(String)request.getAttribute("partnerName") %> &nbsp;&nbsp;&nbsp;&nbsp; 
						Financial Year: <%=(String)request.getAttribute("financialYear") %> </div>
						<%=uF.showData((String)request.getAttribute(IConstants.MESSAGE),"") %>
						<s:form name="frm_AddPartnerwiseBudget" id="frm_AddPartnerwiseBudget" action="AddPartnerwiseBudget" theme="simple" method="post">
						<s:hidden name="empId"></s:hidden>
							<table class="table " style="margin-top: 10px;width:93%;">
								<tr>
									<!-- <th class="alignCenter" style="vertical-align: top;">Financial Year</th> -->
									<th></th>
									<% for(int i=0; i<monthYearsList.size(); i++){ %>
										<th class="alignCenter" style="vertical-align: top;"><%=uF.getDateFormat(monthYearsList.get(i)+"","MM/yyyy","MMMM-yy") %></th>
									<% } %>
									
									<th class="alignCenter" style="vertical-align: top;">TOTAL</th>
								</tr>
								
							<% if(alPartnersBudget != null && !alPartnersBudget.isEmpty()){ %>	
								<tr>
									<td></td>
									<td>
										<input type="text" name="strAprilAmt" id="strAprilAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(0) %>" />
									</td>
									<td>
										<input type="text" name="strMayAmt" id="strMayAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(1) %>" />
									</td>
									<td>
										<input type="text" name="strJuneAmt" id="strJuneAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(2) %>" />
									</td>
									<td>
										<input type="text" name="strJulyAmt" id="strJulyAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(3) %>" />
									</td>
									<td>
										<input type="text" name="strAugustAmt" id="strAugustAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(4) %>" />
									</td>
									<td>
										<input type="text" name="strSeptemberAmt" id="strSeptemberAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(5) %>" />
									</td>
									<td>
										<input type="text" name="strOctoberAmt" id="strOctoberAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(6) %>" />
									</td>
									<td>
										<input type="text" name="strNovemberAmt" id="strNovemberAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(7) %>" />
									</td>
									<td>
										<input type="text" name="strDecemberAmt" id="strDecemberAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(8) %>" />
									</td>
									<td>
										<input type="text" name="strJanuaryAmt" id="strJanuaryAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(9) %>" />
									</td>
									<td>
										<input type="text" name="strFebruaryAmt" id="strFebruaryAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(10) %>" />
									</td>
									<td>
										<input type="text" name="strMarchAmt" id="strMarchAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" value="<%=alPartnersBudget.get(11) %>" />
									</td>
									<td>
										<input type="text" name="strTotalAmt" id="strTotalAmt" style="width: 100px !important; text-align:right;" readonly="readonly" value="<%=alPartnersBudget.get(12) %>" />
									</td>
								</tr>
								<%-- <tr ></tr>
								<tr>
									<td colspan="14">
										<div class="pull-right" style="margin-top: 10px" >
											<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="submit" value="Updated" id="btnSubmit" />
										</div>
									</td>
								</tr> --%>
								<% } else{ %>
								
									<tr>
									<td></td>
										<td>
											<input type="text" name="strAprilAmt" id="strAprilAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strMayAmt" id="strMayAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strJuneAmt" id="strJuneAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strJulyAmt" id="strJulyAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strAugustAmt" id="strAugustAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strSeptemberAmt" id="strSeptemberAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strOctoberAmt" id="strOctoberAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strNovemberAmt" id="strNovemberAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strDecemberAmt" id="strDecemberAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strJanuaryAmt" id="strJanuaryAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strFebruaryAmt" id="strFebruaryAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strMarchAmt" id="strMarchAmt" style="width: 100px !important; text-align:right;" onchange="calAmt();" onkeypress="return isNumberKey(event)" />
										</td>
										<td>
											<input type="text" name="strTotalAmt" id="strTotalAmt" style="width: 100px !important; text-align:right;" readonly="readonly" />
										</td>
									</tr>
								
								<%-- <tr ></tr>
								<tr>
									<td colspan="14">
										<div class="pull-right" style="margin-top: 10px" >
											<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="submit" value="Add" id="btnSubmit" />
										</div>
									</td>
								</tr> --%>
							<% } %>	
							
							<tr>
								<td class="alignLeft">Commitment Given</td>
								<%  for(int i=0; i<monthYearsList.size(); i++){ %>
									<td class="alignRight"><%=uF.showData(hmEmpProDetails.get(monthYearsList.get(i)+"_COMMITMENT"),"0") %></td>
								<% } %>
								<td class="alignRight"><%=uF.showData(hmEmpProDetails.get("TOTAL_COMMITMENT"), "0") %></td>
							</tr>
							<tr>
								<td class="alignLeft">Actual Billing</td>
								<% for(int i=0; i<monthYearsList.size(); i++) { %>
									<td class="alignRight"><%=uF.showData(hmEmpProDetails.get(monthYearsList.get(i)+"_INVOICE"),"0") %></td>
								<% } %>
								<td class="alignRight"><%=uF.showData(hmEmpProDetails.get("TOTAL_INVOICE"), "0") %></td>
							</tr>
							<tr>
								<td class="alignLeft">Actual Receipt</td>
								<% for(int i=0; i<monthYearsList.size(); i++){ %>
									<td class="alignRight"><%=uF.showData(hmEmpProDetails.get(monthYearsList.get(i)+"_RECEIVED"),"0") %></td>
								<% } %>
								<td class="alignRight"><%=uF.showData(hmEmpProDetails.get("TOTAL_RECEIVED"), "0") %></td>
							</tr>
							
							<tr ></tr>
								<tr>
									<td colspan="14" class="alignCenter">
										<div style="margin-top: 10px" >
											<%-- <s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="submit" value="submit" id="btnSubmit" /> --%>
											<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="addUpdateBudget('A');"/>
										</div>
									</td>
								</tr>
							</table>
							
						</s:form>
	                </div>
	                
	            </div>
	            <%-- </s:form> --%>
            </div>
        </div>
    </div>
</section>
</</div>
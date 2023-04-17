<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);

	Map<String,List<String>> hmEmpData = (Map<String,List<String>>)request.getAttribute("hmEmpData");
	Map<String,Map<String,String>> empSalaryMap=(Map<String,Map<String,String>>)request.getAttribute("empSalaryMap");

	List alDates = (List)request.getAttribute("alDates"); 
	List alLegends = (List)request.getAttribute("alLegends");
	Map<String,List<String>> hmEmpAttendanceData=(Map<String,List<String>>)request.getAttribute("hmEmpAttendanceData");
	Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");
	Map<String,List<String>> hmEmpSallaryDetail=(Map<String,List<String>>)request.getAttribute("hmEmpSallaryDetail");
	List<ComparatorWeight> alEarnings = (List<ComparatorWeight>) request.getAttribute("alEarnings");
	if (alEarnings == null) alEarnings = new ArrayList<ComparatorWeight>();
	List<ComparatorWeight> alDeductions = (List<ComparatorWeight>) request.getAttribute("alDeductions");
	if (alDeductions == null) alDeductions = new ArrayList<ComparatorWeight>();
	
	List alReportList = (List) request.getAttribute("reportList");
	if (alReportList == null) alReportList = new ArrayList();
	
	List<String> alEarningss= (ArrayList<String>)request.getAttribute("alEarningss");
	if(alEarningss==null)alEarningss=new ArrayList<String>();
	
	List alDeductionss=(List)request.getAttribute("alDeductionss");
	if(alDeductionss == null)alDeductionss=new ArrayList<String>();
	
%>
<script>
$(function(){
	$('#ltm').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});
	
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_emptype").multiselect().multiselectfilter();
});
 
 function generateMusterRollWagesReportExcel() {
		var org = document.getElementById("f_org").value;
	 	var location = getSelectedValue("f_strWLocation");
	 	var department = getSelectedValue("f_department");
	 	var service = getSelectedValue("f_service");
	 	var level = getSelectedValue("f_level");
	 	var strEmpType = getSelectedValue("f_emptype");
	 	var paycycle = document.getElementById("paycycle").value;
	 	var paramValues = "";
	    paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
					+'&strLevel='+level+'&paycycle='+paycycle+'&strEmpType='+strEmpType;
	
		window.location='MusterRollCumWagesRegisterReport.action?exceldownload=true&f_org='+org+paramValues;
 } 
 
 function submitForm(type){
 	var org = document.getElementById("f_org").value;
 	var location = getSelectedValue("f_strWLocation");
 	var department = getSelectedValue("f_department");
 	var service = getSelectedValue("f_service");
 	var level = getSelectedValue("f_level");
 	var strEmpType = getSelectedValue("f_emptype");
 	var paycycle = document.getElementById("paycycle").value;
 	var paramValues = "";
 	if(type == '2') {
 		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
				+'&strLevel='+level+'&paycycle='+paycycle+'&strEmpType='+strEmpType;
 	}
 
 	var action = 'MusterRollCumWagesRegisterReport.action?f_org='+org+paramValues;
 	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
 	$.ajax({
 		type : 'POST',
 		url: action, 
 		data: $("#"+this.id).serialize(),
 		success: function(result) {
         	console.log(result);
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

<div id="divResult">
 <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
       <div class="desgn" style="background:#f5f5f5; color:#232323;">
   			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-bottom: 10px;">
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
					<s:form name="frm_MusterRollCumWagesRegisterReport" id="frm_MusterRollCumWagesRegisterReport" action="MusterRollCumWagesRegisterReport" theme="simple">
						<s:hidden name="exportType"></s:hidden>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<% if(strUserType != null && !strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && !strBaseUserType.equals(IConstants.HOD)) { %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
									</div>
									<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
									</div>
									<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
									</div>
		
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">SBU</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level"listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
									</div>
								<%} %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_emptype" id="f_emptype" listKey="empTypeId" cssStyle="float:left;margin-right: 10px;width:200px;" 
										listValue="empTypeName" multiple="true" list="empTypeList" key="" />
								</div>
							</div>
						</div>
						
						 <%-- <div class="row row_without_margin">
							
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0" style="margin-left: 10px;">
								 <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">paycycle</p>
									<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<input type="radio" name="paycycleDate" id="paycycleDate" value="2" <%=check2%> />
									<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;"/>
									<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div> 
							</div>
						</div> --%>
						
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							    	<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key=""/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
									</div>
								</div>
							</div>
						
					</s:form>
				</div>
			</div>
          <!-- /.box-body -->
			</div>
				<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
					<a onclick="generateMusterRollWagesReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
				</div>
			</div>
                        
                        
                <div class="clr margintop20"></div>
                   <div class="scroll">
                     <table class="table table-bordered" id="ltm">
                     <thead>
                     	<tr>
                     		<th class="alignCenter" nowrap rowspan="3" >Sr.No.</th>
                     		<th class="alignCenter" nowrap rowspan="3" >Emp.ID</th>
                  
                       		<th class="alignCenter" nowrap rowspan="3" >Employee Name</th>
                     		<th class="alignCenter" nowrap rowspan="3" >Father/Husband Name</th>
                     		<th class="alignCenter" nowrap rowspan="3" >Male/Female</th>
                     		<th class="alignCenter" nowrap rowspan="3" >Designation/Department</th>
                     		<th class="alignCenter" nowrap rowspan="3" >Date Of Joinning</th>
                     		<th class="alignCenter" nowrap rowspan="3" >ESI No.</th>
                     		<th class="alignCenter" nowrap rowspan="3" >PF No.</th>
                     		<th class="alignCenter" nowrap rowspan="3" >Wages Fixed <br>Including VDA</th>
                     		
                     		<%if(alDates!=null && alDates.size()>0){ %>
                     		<th class="alignCenter" colspan="<%=alDates.size()%>">Attendence</th>
                     		<%}%>
                     		<th class="alignCenter" nowrap rowspan="3">No. of<br> Payable Days</th>
                     		<th class="alignCenter" nowrap rowspan="3">Date of<br> Suspension if any</th>
                     		
                     		<%if(alDates!=null && alDates.size()>0){
    							String strMonth = uF.getDateFormat((String)alDates.get(1), IConstants.DATE_FORMAT, "MM");
								String currentMonth=uF.getMonth(uF.parseToInt(strMonth));
							    if(alEarnings!=null && alEarnings.size()>0 && alDeductions!=null && alDeductions.size()>0 ){
							    int colspanSize=alEarnings.size()+alDeductions.size();%>
								
                     		<th class="alignCenter" colspan="<%=colspanSize+1%>"><%=currentMonth %></th>
                     		
                     		<%}else{%>
                     			<th class="alignCenter" colspan="3"><%=currentMonth %></th>
                     		<%}}%>
                     		
                     		<th class="alignCenter" nowrap rowspan="3">Total Deduction</th>
                     		<th class="alignCenter" nowrap rowspan="3">Net Payable</th>
                     		<th class="alignCenter" nowrap rowspan="3">Mode of Payement cash/<br>check No.</th>
                     		<th class="alignCenter" nowrap rowspan="3">Employee Signature/<br>Thumb Impression</th>
                     	</tr>
                     	
                     	<tr>
                    	<%if(alDates!=null && alDates.size()>0){
                    		for (int ii=0; ii<alDates.size(); ii++){
							String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");%>
							<th class="alignCenter" nowrap rowspan="2"><%=uF.showData(strDate,"-")%></th>
							<%}
							if(alEarnings!=null && alEarnings.size()>0 && alDeductions!=null && alDeductions.size()>0 ){%>
							
                     		<th class="alignCenter"  colspan="<%=alEarnings.size()+1%>">Earned Wage and other Allowances</th>
                     		<th class="alignCenter"  colspan="<%=alDeductions.size()%>">Deductions</th>
                     		<%}else{ %>
                     		<th class="alignCenter" colspan="2">Earned Wage and other Allowances</th>
                     		<th class="alignCenter" >Deductions</th>
                     		<%}} %>
                     	</tr>
                     	
                     	<tr>
                     		<%if(alEarnings!=null && alEarnings.size()>0 && alDeductions!=null && alDeductions.size()>0 ){
                     		for (int ii = 0; alEarnings != null && ii < alEarnings.size(); ii++){
                     		%>
							<th nowrap="nowrap" class="alignCenter"><%=uF.showData((String) hmSalaryDetails.get(((ComparatorWeight) alEarnings.get(ii)).getStrName()) + "(+)","-")%></th>
							<%}%>
							<th class="alignCenter">Gross Salary</th>
							
							<% for (int ii = 0; alDeductions != null && ii < alDeductions.size(); ii++){%>
							<th nowrap="nowrap" class="alignCenter"><%=uF.showData((String) hmSalaryDetails.get(((ComparatorWeight) alDeductions.get(ii)).getStrName())+ "(-)","-")%></th>
							<%}}else{%>
							
									<th nowrap="nowrap" class="alignCenter"></th>
									<th class="alignCenter">Gross Salary</th>
									<th nowrap="nowrap" class="alignCenter"></th>
							<%}%>
                     	</tr>
                     	
                     	<% if(hmEmpData!=null && hmEmpData.size()>0 && empSalaryMap!=null && empSalaryMap.size()>0 && hmEmpAttendanceData!=null && hmEmpAttendanceData.size()>0 ){
                     		Iterator<String> it= hmEmpData.keySet().iterator();
                     		int count=0;
                     		int count1=0;
                     		int l=0;
                     		
                     		while(it.hasNext()) {
                     			count++;
                     			String empid = it.next();
                     			List<String> innerList = hmEmpData.get(empid);
                     			if(innerList!=null && innerList.size()>0){%>	
                     			
                     			<tr>
                     			<td><%=uF.showData(""+count,"")%></td>
                         	 	<td><%=uF.showData(innerList.get(0),"-")%></td>
                         	 	<td><%=uF.showData(innerList.get(1),"-")%></td>
                         	 	<td><%=uF.showData(innerList.get(2),"-")%></td>
                         	 	<td><%=uF.showData(innerList.get(3),"-")%></td>
                         	 	<td><%=uF.showData(innerList.get(4),"-")%>/<%=uF.showData(innerList.get(5),"-")%></td>
                         	 	<td><%=uF.showData(innerList.get(6),"-")%></td>
                         	 	<td><%=uF.showData(innerList.get(7),"-")%></td>
                         	 	<td><%=uF.showData(innerList.get(8),"-")%></td>
                         	 	<td></td>
                         	 	
                         	 	<%List<String>alInner=hmEmpAttendanceData.get(empid);
                         	 	  if(alInner!=null && alInner.size()>0){
                         	 		for(int i=0; alInner != null && i<alInner.size(); i++){%>
                         	 		<td><%=uF.showData(alInner.get(i),"-")%></td>
                         	 	<%}}else{%>
                         	 	     <td></td>
                         	 	<%}%>
                         	 	
                         	 	<td><%=uF.showData(innerList.get(9),"-")%></td>
                         
								<%Map<String,String> salaryMap=empSalaryMap.get(empid);
								if(salaryMap!=null && salaryMap.size()>0){
									if(alEarningss!=null && alEarningss.size()>0){
										for(int i=0;i<alEarningss.size();i++){%>
											<td><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get(alEarningss.get(i)))) %></td>	
								<%}}%>
								
								<td><%= uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get("GROSS"))) %></td>
								
								<%if(alDeductionss!=null && alDeductionss.size()>0){
									for(int i=0;i<alDeductionss.size();i++){ %>
										<td><%= uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get(alDeductionss.get(i))))%></td>	
								<%}}%>
								
								<td><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get("DEDUCTION"))) %></td>
								<td><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get("NET")))%></td>
								
								<%}else{%>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
								<%}%>
							
								<td><%=uF.showData(innerList.get(10),"-") %></td>	
                         	  <%}}%><!--end of firstwhile loop  -->
                         	 <%}%>
                         	 </tr>
                     	</thead>
                     </table>  
                 <div class="custom-legends">
					<%for(int i=0; i<alLegends.size(); i++){%>
						<%=alLegends.get(i) %>
					<%}%>
				</div>
		</div>
     </div>
            <!-- /.box-body -->
</div>
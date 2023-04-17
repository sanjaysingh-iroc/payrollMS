<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page import="java.text.DateFormatSymbols" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%

 UtilityFunctions uF = new UtilityFunctions();
 CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
 String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
 String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);

 Map<String,List<String>>hmEmpJobList=(Map<String,List<String>>)request.getAttribute("hmEmpJobList");
 if(hmEmpJobList==null)hmEmpJobList=new LinkedHashMap<String,List<String>>();
 System.out.println("hmEmpJobList in jsp"+hmEmpJobList.size());

 List<String>alDates=(List<String>)request.getAttribute("alDates");
 if(alDates==null)alDates=new ArrayList<String>();
 System.out.println("alDates in jsp"+alDates.size());
// System.out.println("month in jsp"+uF.getDateFormat((String)alDates.get(1), IConstants.DATE_FORMAT, "MM"));

 Map<String,List<String>>hmEmpAttendanceData=(Map<String,List<String>>)request.getAttribute("hmEmpAttendanceData");
 if(hmEmpAttendanceData==null)hmEmpAttendanceData=new LinkedHashMap<String,List<String>>();
 System.out.println("hmEmpAttendanceData size in jsp"+hmEmpAttendanceData.size());
 
 Map<String,List<List<String>>>hmEmpLeaveMap=(Map<String,List<List<String>>>)request.getAttribute("hmEmpLeaveMap");
 if(hmEmpLeaveMap==null)hmEmpLeaveMap=new HashMap<String,List<List<String>>>();

 System.out.println("hmEmpLeaveMap in jsp"+hmEmpLeaveMap.size());
 
 Map<String,String>hmMainBalance=(Map<String,String>)request.getAttribute("hmMainBalance");
 if(hmMainBalance==null)hmMainBalance=new HashMap<String,String>();
 
 Map<String,String>hmTakenPaid=(Map<String,String>)request.getAttribute("hmTakenPaid");
 if(hmTakenPaid==null)hmTakenPaid=new HashMap<String,String>();
 System.out.println("hmTakenPaid in jsp"+hmTakenPaid.size());
 
 Map<String,List<String>>hmRosterBreakTime=(Map<String,List<String>>)request.getAttribute("hmRosterBreakTime");
 if(hmRosterBreakTime==null)hmRosterBreakTime=new LinkedHashMap<String,List<String>>();
 System.out.println("hmRosterBreakTime in jsp"+hmRosterBreakTime.size());
 
 Map<String,List<String>>hmTotalWorkedDays=(Map<String,List<String>>)request.getAttribute("hmTotalWorkedDays");
 if(hmTotalWorkedDays==null)hmTotalWorkedDays=new LinkedHashMap<String,List<String>>();
%>
<script>
$(function(){
	$('#ltm1').DataTable({
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

function generateMusterRollJobReportExcel(){
	
		var org = document.getElementById("f_org").value;
	 	var location = getSelectedValue("f_strWLocation");
	 	var department = getSelectedValue("f_department");
	 	var service = getSelectedValue("f_service");
	 	var level = getSelectedValue("f_level");
	 	var strEmpType = getSelectedValue("f_emptype");
	 	var paycycle = document.getElementById("paycycle").value;
	 	var paramValues = "";
	 	
	 	encodeURIComponent
	 	
	    paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
					+'&strLevel='+level+'&paycycle='+encodeURIComponent(paycycle)+'&strEmpType='+strEmpType;
	 
	    window.location='MusterRollJobReport.action?exceldownload=True&f_org='+org+paramValues;
}

function submitForm(type){
	
	var org = document.getElementById("f_org").value;
 	var location = getSelectedValue("f_strWLocation");
 	var department = getSelectedValue("f_department");
 	var service = getSelectedValue("f_service");
 	var level = getSelectedValue("f_level")
 	var strEmpType = getSelectedValue("f_emptype");
 	var paycycle = document.getElementById("paycycle").value;
 	
 	var paramValues = "";
 	if(type == '2') {
 		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
				+'&strLevel='+level+'&paycycle='+encodeURIComponent(paycycle)+'&strEmpType='+strEmpType;
 	}
 	
 	var action = 'MusterRollJobReport.action?f_org='+org+paramValues;
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
					<s:form name="frm_MusterRollJobReport" id="frm_MusterRollJobReport" action="MusterRollJobReport" theme="simple">
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
					<a onclick="generateMusterRollJobReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
				</div>
    </div>
               <div class="clr margintop20"></div>
                   <div class="scroll">
                  	<table class="table table-bordered" id="ltm1">
                  		<thead>
                  			<tr>
                  				<th class="alignCenter" nowrap rowspan="2">SR.NO.</th>
                  				<th class="alignCenter" nowrap rowspan="2">FULL NAME</th>
                  				<th class="alignCenter" nowrap rowspan="2">GENDER</th>
                  				<th class="alignCenter" nowrap rowspan="2">AGE</th>
                  				<th class="alignCenter" nowrap colspan="2">WORKING HOURS</th>
                  		    	<th class="alignCenter" nowrap colspan="2">LEAVE WITH WAGES</th>
                  				<th class="alignCenter" nowrap rowspan="2">DOE</th>
                  				<th class="alignCenter" colspan="2" nowrap>INTERVAL FOR REST</th>
                  				<th class="alignCenter" rowspan="2" nowrap>DESIGNATION</th>
                  				<%if(alDates!=null && alDates.size()>0){ %>
                  				<th class="alignCenter" colspan="<%=alDates.size()%>" nowrap>ATTENDANCE</th>
                  				<%}else{ %>
                  				<th class="alignCenter" nowrap>ATTENDANCE</th>
                  				<%}%>
                  				
                  				<th class="alignCenter" rowspan="2" nowrap>TOTAL DAYS</th>
                  				<th class="alignCenter" rowspan="2" nowrap>TOTAL WORKED DAYS</th>
                  			</tr>
                  			
                  			<tr>
                  			 	<th class="alignCenter" nowrap>FROM</th>
                  			 	<th class="alignCenter" nowrap>To</th>
                  			 	<th class="alignCenter" nowrap>BALANCED</th>
                  			 	<th class="alignCenter" nowrap>ENJOYED</th>
                  			 	<th class="alignCenter" nowrap>FROM</th>
                  			 	<th class="alignCenter" nowrap>To</th>
                  			 	<%if(alDates!=null && alDates.size()>0){
                  			 	 for(int i=0;i<alDates.size();i++){
                  			 	 String strDate = uF.getDateFormat((String)alDates.get(i), IConstants.DATE_FORMAT, "dd");
                  			 	%>
                  			 	<th class="alignCenter"><%=strDate%></th>
                  			 	<%}}else{%>
                  			 	 <th class="alignCenter"></th>
                  			 	<%}%>
                  			</tr>
                  			
                  		</thead>
                  		<tbody>
                  			<% if(hmEmpJobList!=null && hmEmpJobList.size()>0 && hmEmpLeaveMap!=null && hmEmpLeaveMap.size()>0
                  					&& hmRosterBreakTime!=null && hmRosterBreakTime.size()>0 && hmEmpAttendanceData!=null && hmEmpAttendanceData.size()>0
                  					&& hmTotalWorkedDays!=null && hmTotalWorkedDays.size()>0){ 
                  				
                  					double balanceAmount=0.0;
                  				 	double paidAmount=0.0;
                  				 	
                  					Iterator<String>it=hmEmpJobList.keySet().iterator();
                  					int srNo=0;
                  					while(it.hasNext())
                  					{
                  						srNo++;
                  						String empid=it.next();
                  						List<String>alEmpJobList=hmEmpJobList.get(empid);
                  						if(alEmpJobList!=null && alEmpJobList.size()>0){%>
                  						
                  					<tr>
                  				   		<td><%=srNo%></td>
                  						<td><%=uF.showData(alEmpJobList.get(0),"-") %></td>
                  						<td><%=uF.showData(alEmpJobList.get(1),"-") %></td>
                  						<td><%=uF.showData(alEmpJobList.get(2),"-") %></td>
                  						<td><%=uF.showData(alEmpJobList.get(3),"-") %></td>
                  						<td><%=uF.showData(alEmpJobList.get(4),"-") %></td>
                  					
                  						 <%List<List<String>>outerList= hmEmpLeaveMap.get(empid);
                  						
                  						 	double dblOpeningBalance=0.0;
                  							double dblTakenPaid=0.0;
                  							if(outerList!=null && outerList.size()>0){
                  							for(int i=0;i<outerList.size();i++){
                  								List<String>innerList=outerList.get(i);
                  								for(int j=0;j<innerList.size();j++){
                  									 String leaveTypeId = innerList.get(j);
                  									 dblOpeningBalance = dblOpeningBalance+uF.parseToDouble(hmMainBalance.get(empid+"_"+leaveTypeId));
                  									 dblTakenPaid = uF.parseToDouble(hmTakenPaid.get(empid+"_"+leaveTypeId));
             									 }
                  							 }
                  								balanceAmount=dblOpeningBalance;
                   								dblOpeningBalance=0.0;
                   								paidAmount=dblTakenPaid;
                   								dblTakenPaid=0.0;%> 
                  						
                  				    		<td><%=balanceAmount%></td>
                  							<td><%=paidAmount%></td>
                  						<%}else{ %>
                  					 		<td><%=uF.showData("","-") %></td>
                  					 		<td><%=uF.showData("","-") %></td> 
                  						<%}%>
                  			
                  					 		<td><%=alEmpJobList.get(5)%></td>
                  					
                  						<%if(hmRosterBreakTime!=null && hmRosterBreakTime.size()>0){
                  						
                  						List<String>alBreakTime=hmRosterBreakTime.get(empid);
                  						if(alBreakTime==null)alBreakTime=new ArrayList<String>();
                  						if(alBreakTime!=null && alBreakTime.size()>0){
                  						for(int i1=0;i1<alBreakTime.size();i1++){%>
                  							<td><%=uF.showData(alBreakTime.get(i1),"-")%></td>
                  						<%}}else{%><td><%=uF.showData("","-") %></td>
                  							<td><%=uF.showData("","-")%></td>
                  						<%}} %>
                  					
                  						<td><%=alEmpJobList.get(6)%></td>
                  					
                  						<%List<String>alAttendance= hmEmpAttendanceData.get(empid);
                  						if(alAttendance!=null && alAttendance.size()>0){
                  						for(int i=0;i<alAttendance.size();i++){%>
                  							<td><%=uF.showData(alAttendance.get(i),"") %></td>
                  						<%}}%>
                  					
                  						<%if(hmTotalWorkedDays!=null){
                  						List<String>alTotalWorkedDays=hmTotalWorkedDays.get(empid);
                  						if(alTotalWorkedDays!=null && alTotalWorkedDays.size()>0){
                  						for(int i=0;i<alTotalWorkedDays.size();i++){%>
                  							<td><%=uF.showData(alTotalWorkedDays.get(i),"0") %></td>
                  						<%}}else{%>
                  							<td><%=uF.showData("","0")%></td>
                  						<%}}%>
                  					
                  				<%}}} %>
                  		 	</tr>
                  		</tbody>
                  	</table>	
				</div>
     		</div>
            <!-- /.box-body -->
</div>			
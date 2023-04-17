<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<script type="text/javascript" src="scripts/customAjax.js"></script>


<script type="text/javascript">
    function checkUncheckValue() { 
    	var allOt=document.getElementById("allOt");		
    	var strOt = document.getElementsByName('ot');
    
    	if(allOt.checked==true){
    		 for(var i=0;i<strOt.length;i++){
    			 strOt[i].checked = true;			  
    		 }
    	}else{		
    		 for(var i=0;i<strOt.length;i++){
    			 strOt[i].checked = false;			 
    		 }		 
    	}	 
    }
    
</script>

<%
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    
    String  strYear = (String)request.getAttribute("strYear");
    String  strMonth = (String)request.getAttribute("strMonth");
    
    List<String> alDates = (List<String>)request.getAttribute("alDates"); 
    List<String> alEmployees = (List<String>)request.getAttribute("alEmployees");
    Map hmEmpAttendance = (Map)request.getAttribute("hmEmpAttendance");
    Map hmEmpWlocation = (Map)request.getAttribute("hmEmpWlocation");
    Map hmEmpName = (Map)request.getAttribute("hmEmpName");
    Map hmServiceMap = (Map)request.getAttribute("hmServiceMap");
    Map hmEmpCodeMap =  (Map)request.getAttribute("hmEmpCodeMap");
    
    Map<String, String> hmApproveOT=(Map<String, String>)request.getAttribute("hmApproveOT");
    
    Map<String, String> hmCheckPayroll =(Map<String, String>)request.getAttribute("hmCheckPayroll");
    
    Map<String, String> hmOTHours =(Map<String, String>)request.getAttribute("hmOTHours");
    if(hmOTHours == null) hmOTHours = new HashMap<String, String>();
    
    String sbData = (String) request.getAttribute("sbData");
    String strSearch = (String) request.getAttribute("strSearch");
    System.out.println("OTH.jsp/52---");
    %>
    
<script type="text/javascript" charset="utf-8">
    function loadMore(pageNumber, minLimit) {
    	document.frm_OverTimeHours.pageNumber.value = pageNumber;
    	document.frm_OverTimeHours.minLimit.value = minLimit;
    	//document.frm_OverTimeHours.submit();
    	
    	var org = document.getElementById("f_org").value;
    	var paycycle = document.getElementById("paycycle").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	
    	var strSearch = document.getElementById("strSearch").value;
    	
    	var paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&paycycle='+paycycle+'&strSearch='+strSearch;
    	//alert("service ===>> " + service);
    	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'OverTimeHours.action?pageNumber='+pageNumber+'&minLimit='+minLimit+'&f_org='+org+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	$("#subDivResult").html(result);
       		}
    	});
    }
    
    
    function selectall(x,strEmpId){
    	var  status=x.checked; 
    	var  arr= document.getElementsByName(strEmpId);
    	for(i=0;i<arr.length;i++){ 
      		arr[i].checked=status;
     	}
    	if(x.checked == true){
    		document.getElementById("unSendSpan").style.display = 'none';
    		document.getElementById("sendSpan").style.display = 'block';
    	} else {
    		document.getElementById("unSendSpan").style.display = 'block';
    		document.getElementById("sendSpan").style.display = 'none';
    	}
    }
    
    function checkAll(){
    	var sendAll = document.getElementById("sendAll");		
    	var strEmpIds = document.getElementsByName('strEmpIds');
    	var cnt = 0;
    	var chkCnt = 0;
    	for(var i=0;i<strEmpIds.length;i++) {
    		cnt++;
    		 if(strEmpIds[i].checked) {
    			 chkCnt++;
    		 }
    	 }
    	if(parseFloat(chkCnt) > 0) {
    		document.getElementById("unSendSpan").style.display = 'none';
    		document.getElementById("sendSpan").style.display = 'block';
    	} else {
    		document.getElementById("unSendSpan").style.display = 'block';
    		document.getElementById("sendSpan").style.display = 'none';
    	}
    	
    	if(cnt == chkCnt) {
    		sendAll.checked = true;
    	} else {
    		sendAll.checked = false;
    	}
    }
    
    
    function submitForm(type) {
    	var org = document.getElementById("f_org").value;
    	var paycycle = document.getElementById("paycycle").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	
    	var strSearch = document.getElementById("strSearch").value;
    	
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&paycycle='+paycycle;
    	} else if(type == '3') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&paycycle='+paycycle
    		+'&strSearch='+strSearch;
    	}
    	//alert("service ===>> " + service);
    	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'OverTimeHours.action?f_org='+org+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	$("#subDivResult").html(result);
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
    
    
    function approveOvertimeHours(event){
    	event.preventDefault();
    	if(confirm('Are you sure, you want to approve overtime hours of selected employee?')){
    		var data = $("#frm_OverTimeHours").serialize();
    		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$.ajax({
    			type : 'POST',
    			url: 'OverTimeHours.action?approveSubmit=Approve',
    			data: data,
    			success: function(result){
    	        	$("#subDivResult").html(result); 
    	   		}
    		});
    		
    	}		
    }
    
</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Overtime Hours" name="title"/>
    </jsp:include>  --%>

	<div class="box-body" style="padding: 5px; min-height: 600px;">
		<s:form name="frm_OverTimeHours" id="frm_OverTimeHours" action="OverTimeHours" theme="simple" method="post">
			<s:hidden name="pageNumber" id="pageNumber" />
			<s:hidden name="minLimit" id="minLimit" />
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
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true" />
								</div>
							</div>
						</div>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 cOverTimeHoursol-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Paycycle</p>
									<s:select name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
								</div>
							</div>	
						</div>
					</div>
					<!-- /.box-body -->
				</div>
				
				<div class="col-md-12" style="margin: 0px 0px 10px 0px; text-align: right;">
                      <div style="float: left;line-height: 22px; width: 514px; margin-left: 350px;">
                          <span style="float: left; display: block; width: 78px;">Search:</span>
                          <div style="margin: 0px 0px 0px 16px; float: left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
                              <div style="float: left">
                                  <input type="text" name="strSearch" id="strSearch" class="form-control" style="margin-left: 0px; width: 250px; box-shadow: 0px 0px 0px #ccc" 
                                  	value="<%=uF.showData(strSearch,"") %>" />
                              </div>
                              <div style="float: right">
                                  <input type="button" value="Search" class="btn btn-primary" name="submit" onclick="submitForm('3');" style="margin-left: 10px;"/>
                              </div>
                          </div>
                      </div>
                      <script>
                          $( "#strSearch" ).autocomplete({
                          	source: [ <%=uF.showData(sbData,"") %> ]
                          });
                      </script>
                </div>
                
               <%-- <div style="float:left; font-size:12px; line-height:22px; width:100%; margin-left: 350px;margin-bottom: 10px;">
                   <span style="float:left; margin-right:7px;">Search:</span>
                   <div style="border:solid 1px #68AC3B; float:left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
                       <div style="float:left">
                           <input type="text" id="strSearch" name="strSearch" style="margin-left: 0px; border:0px solid #ccc; width:282px; box-shadow:0px 0px 0px #ccc" value="<%=uF.showData(strSearch,"") %>"/> 
                       </div>
                       <div style="float:right">
                           <input type="submit" value="Search" class="btn btn-primary"/>
                       </div>
                   </div>
               </div> 
               <script type="text/javascript">
                   $( "#strSearch" ).autocomplete({
                   	source: [ <%=uF.showData(sbData,"") %> ]
                   });
               </script> --%>
               <div style="float: left; margin-bottom: 24px;">
                   <span id="unSendSpan" style="display: none;">
                   <input type="button" name="unSend" class="btn btn-primary disabled" value="Approve Overtime Hours" />
                   </span>
                   <span id="sendSpan">
                   <input type="submit" value="Approve Overtime Hours" name="approveSubmit" class="btn btn-primary" onclick="approveOvertimeHours(event);"/>
                   </span>
               </div>
               <!-- <div style="float:right; margin:10px 0px 0px 0px; text-align: right;"><a href="OvertimeForm.action">Overtime Form</a></div> -->
               <div style="width:100%;float:left; overflow: auto;">
                   <table cellpadding="5" cellspacing="2" class="table table-bordered" width="100%">  <!-- id="lt" -->
                       <thead>
                           <tr>
                               <th align="left" class="no-sort"><input type="checkbox" name="sendAll" id="sendAll" onclick="selectall(this,'strEmpIds')" checked="checked"/></th>
                               <th class="alignCenter" nowrap="nowrap">Employee Code</th>
                               <th class="alignCenter" nowrap="nowrap">Employee Name</th>
                               <%for(int j=0; j<alDates.size(); j++){ 
                                   //String strDate = uF.getDateFormat((String)alDates.get(j), IConstants.DATE_FORMAT, "dd");
                                   String strDate =(String)alDates.get(j);
                                   %>
                               <th class="alignCenter" nowrap="nowrap"><%=strDate %></th>
                               <%} %>
                           </tr>
                       <thead>
                       <tbody>
                           <% for(int i=0; alEmployees!=null && i<alEmployees.size(); i++) {
                        	   /* if(uF.parseToInt((String)alEmployees.get(i)) !=269) {
                        		   continue;
                        	   } */
                        	   %>
                           <tr>
                               <td><input type="checkbox" name="strEmpIds" id="strEmpIds" value="<%=(String)alEmployees.get(i)%>" onclick="checkAll();" checked/></td>
                               <td class="" nowrap="nowrap"><%=(String)hmEmpCodeMap.get((String)alEmployees.get(i)) %></td>
                               <td class="" nowrap="nowrap"><%=(String)hmEmpName.get((String)alEmployees.get(i)) %></td>
                               <%for(int j=0; j<alDates.size(); j++) {
                                   String strOvertime = "0.00";
                                   String actOt=(String)hmEmpAttendance.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i));
                                   if(hmApproveOT.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i))!=null && !hmApproveOT.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i)).equals("0.00")){
                                   	strOvertime=(String)hmApproveOT.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i));
                                   } else if(actOt!=null && !actOt.equals("0.00")) {
                                   	strOvertime=(String)hmEmpAttendance.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i));
                                   } else if(uF.parseToDouble(hmOTHours.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i))) > 0.0d) {
                                   	strOvertime = hmOTHours.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i));
                                   }
                                   
                                   %>
                               <td nowrap="nowrap">
                                   <%if(hmCheckPayroll!=null && hmCheckPayroll.containsKey((String)alEmployees.get(i))){ %>
                                   <%=uF.showData(strOvertime,"0.00") %>
                                   <%}else{ %>
                                   <div style="float:left">
                                       <input type="hidden" name="strEmpId_strMonth" value="<%=(String)alEmployees.get(i)%>_<%=(String)alDates.get(j)%>"/>
                                       <input id="amt_<%=(String)alEmployees.get(i)+"_"+(String)alDates.get(j)%>" type="text" name="amt_<%=(String)alEmployees.get(i)+"_"+(String)alDates.get(j)%>" value="<%=uF.showData(strOvertime," ")%>" style="width:75px !important;"/>
                                       <a href="javascript:void()" onclick="getContent('id_<%=alDates.get(j).replace("/","")%>_<%=alEmployees.get(i)%>', 'OverTimeHours.action?action=U&paycycle=<%=(String) request.getAttribute("paycycle") %>&strDate=<%=(String)alDates.get(j)%>&empId=<%=(String)alEmployees.get(i)%>&strAmount='+document.getElementById('amt_<%=(String)alEmployees.get(i)+"_"+(String)alDates.get(j)%>').value)"><i class="fa fa-pencil-square-o" title="click to update"></i></a>
                                       <a href="javascript:void()" onclick="(confirm('Are you sure you want to delete this entry?')?getContent('id_<%=alDates.get(j).replace("/","")%>_<%=alEmployees.get(i)%>','OverTimeHours.action?action=D&paycycle=<%=(String) request.getAttribute("paycycle") %>&strDate=<%=(String)alDates.get(j)%>&empId=<%=(String)alEmployees.get(i)%>'):'')"><i class="fa fa-trash" title="click to remove"></i></a>
                                   </div>
                                   <div  style="float:left;margin-top: 5px;" id="id_<%=alDates.get(j).replace("/","")%>_<%=alEmployees.get(i)%>" class="badge bg-yellow"></div>
                                   <%} %>	
                               </td>
                               <%} %>
                           </tr>
                           <% } %>
                       </tbody>
                   </table>
               </div>
               
               <div style="text-align: center; float: left; width: 100%;">
                   <% 
                       String pageCount = (String)request.getAttribute("pageCount");
                       int intproCnt = uF.parseToInt(pageCount);
                       	int pageCnt = 0;
                       	int minLimit = 0;
                       	
                       	for(int i=1; i<=intproCnt; i++) {
                  			minLimit = pageCnt * 10;
                  			pageCnt++;
                       %>
                   <% if(i ==1) {
                       String strPgCnt = (String)request.getAttribute("pageNumber");
                       String strMinLimit = (String)request.getAttribute("minLimit");
                       if(uF.parseToInt(strPgCnt) > 1) {
                       	 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
                       	 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
                       }
                       if(strMinLimit == null) {
                       	strMinLimit = "0";
                       }
                       if(strPgCnt == null) {
                       	strPgCnt = "1";
                       }
                       %>
                   <span style="color: lightgray;">
                   <% if(uF.parseToInt((String)request.getAttribute("pageNumber")) > 1) { %>
                   <a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
                   <%="< Prev" %></a>
                   <% } else { %>
                   <b><%="< Prev" %></b>
                   <% } %>
                   </span>
                   <span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
                       <% if(((String)request.getAttribute("pageNumber") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("pageNumber")) == pageCnt) { %>
                       style="color: black;"
                       <% } %>
                       ><%=pageCnt %></a></span>
                   <% if((uF.parseToInt((String)request.getAttribute("pageNumber"))-3) > 1) { %>
                   <b>...</b>
                   <% } %>
                   <% } %>
                   <% if(i > 1 && i < intproCnt) { %>
                   <% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("pageNumber"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("pageNumber"))+2)) { %>
                   <span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
                       <% if(((String)request.getAttribute("pageNumber") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("pageNumber")) == pageCnt) { %>
                       style="color: black;"
                       <% } %>
                       ><%=pageCnt %></a></span>
                   <% } %>
                   <% } %>
                   <% if(i == intproCnt && intproCnt > 1) {
                       String strPgCnt = (String)request.getAttribute("pageNumber");
                       String strMinLimit = (String)request.getAttribute("minLimit");
                        strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
                        strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
                        if(strMinLimit == null) {
                       	strMinLimit = "0";
                       }
                       if(strPgCnt == null) {
                       	strPgCnt = "1";
                       }
                       %>
                   <% if((uF.parseToInt((String)request.getAttribute("pageNumber"))+3) < intproCnt) { %>
                   <b>...</b>
                   <% } %>
                   <span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
                       <% if(uF.parseToInt((String)request.getAttribute("pageNumber")) == pageCnt) { %>
                       style="color: black;"
                       <% } %>
                       ><%=pageCnt %></a></span>
                   <span style="color: lightgray;">
                   <% if(uF.parseToInt((String)request.getAttribute("pageNumber")) < pageCnt) { %>
                   <a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
                   <% } else { %>
                   <b><%="Next >" %></b>
                   <% } %>
                   </span>
                   <% } %>
                   <%} %>
              </div>
          </s:form>
	</div>
	<!-- /.box-body -->


<script type="text/javascript">
    $(function(){
    	$("#f_strWLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_service").multiselect().multiselectfilter(); 
    	$("#lt").DataTable({
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
    });    
</script>

<%@page import="java.io.File"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript" src="scripts/customAjax.js"></script>

<script type="text/javascript">
    $(function(){
    	$("#f_strWLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_service").multiselect().multiselectfilter();
    	$("#f_level").multiselect().multiselectfilter();
    	$("#wLocation").multiselect().multiselectfilter();
    	$("#f_employeType").multiselect().multiselectfilter();
    	$("#f_grade").multiselect().multiselectfilter();
    	
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
    }); 
    
    $(document).ready( function () {
    	var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
    	var sbUserTypeList = '<%= ((String)request.getAttribute("sbUserTypeList")) %>';
     	var sbEmpCodeList = '<%= ((String)request.getAttribute("sbEmpCodeList")) %>';
     	var sbUserStatusList = '<%= ((String)request.getAttribute("sbUserStatusList")) %>';
    	 if (usertype == '<%=IConstants.ADMIN%>'  
     		|| usertype == '<%=IConstants.CEO%>' || usertype == '<%=IConstants.CFO%>'
     		|| usertype == '<%=IConstants.ACCOUNTANT%>' || usertype == '<%=IConstants.HRMANAGER%>'
     			|| usertype == '<%=IConstants.MANAGER%>') {
	     	$('#lt').DataTable({
	     		aLengthMenu: [
   		 			[25, 50, 100, 200, -1],
   		 			[25, 50, 100, 200, "All"]
   		 		],
   		 		"order": [],
	    		"columnDefs": [ {
	    		      "targets"  : 'no-sort',
	    		      "orderable": false
	    		    }],
	    		'dom': 'lBfrtip',
	            'buttons': [
	    			'copy', 'csv', 'excel', 'pdf', 'print'
	            ],
	            drawCallback: function(){
	                $("img.lazy").lazyload();
	           }
	      	});
	     }else {
	    			/* $('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers" }) */
	    $('#lt').DataTable({
	    	aLengthMenu: [
   	  			[25, 50, 100, 200, -1],
   	  			[25, 50, 100, 200, "All"]
   	  		],
			"order": [],
			"columnDefs": [ {
			      "targets"  : 'no-sort',
			      "orderable": false
			    }],
			'dom': 'lBfrtip',
	        'buttons': [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ],
	        drawCallback: function(){
	            $("img.lazy").lazyload();
	       }
	  	});
	     } 
    });
    			
    			
   	function changeUserName(empid,userid,empname) { 
   		
   		var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 $(".modal-title").html('Change Username for '+empname);	
   		 $.ajax({  
			url : 'ChangeUserName.action?empid='+empid+'&userid='+userid,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data); 
			}
		});
   	}
    	
    	
   	function changeUserType(empid,userid,empname) { 
   		
   		var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 $(".modal-title").html('Change User type for '+empname);				
   		 $.ajax({  
			url : 'ChangeUserType.action?empid='+empid+'&userid='+userid,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data); 
			}
		});
   	}
    
    	
   	function getWlocation() {
   		var org = $('#strOrg').val();
   	 	var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
  			var xhr = $.ajax({
			url : 'GetOrgWLocationList.action?strOrgId='+org+"&type=UTChange",
			cache : false,
			success : function(data) {
				document.getElementById('idWlocId').innerHTML=data;
				//getdepartment(val);
			}
			});
		}
   	 }
    	
   	function selectall(x,strEmpId){
   		var  status=x.checked; 
   		var  arr= document.getElementsByName(strEmpId);
   		for(i=0;i<arr.length;i++){ 
   	  		arr[i].checked=status;
   	 	}
   		if(x.checked == true){
   			document.getElementById("unSendSpan").style.display = 'none';
   			document.getElementById("sendSpan").style.display = 'inline';
   		} else {
   			document.getElementById("unSendSpan").style.display = 'inline';
   			document.getElementById("sendSpan").style.display = 'none';
   		}
   	}
    	
   	function checkAll(){
   		var sendAll = document.getElementById("sendAll");		
   		var strSendLogin = document.getElementsByName('strSendLogin');
   		var cnt = 0;
   		var chkCnt = 0;
   		for(var i=0;i<strSendLogin.length;i++) {
   			cnt++;
   			 if(strSendLogin[i].checked) {
   				 chkCnt++;
   			 }
   		 }
   		if(parseFloat(chkCnt) > 0) {
   			document.getElementById("unSendSpan").style.display = 'none';
   			document.getElementById("sendSpan").style.display = 'inline';
   		} else {
   			document.getElementById("unSendSpan").style.display = 'inline';
   			document.getElementById("sendSpan").style.display = 'none';
   		}
   		
   		if(cnt == chkCnt) {
   			sendAll.checked = true;
   		} else {
   			sendAll.checked = false;
   		}
   	}
   	
   	function callClockOnOffAccessControl() { 
   		window.location='ClockOnOffAccessControl.action';
   	}
    
   	function callGeofenceAccessControl() { 
   		window.location='GeofenceAccessControl.action';
   	}
   	
   	function submitForm(type, strAction) {
		var org = document.getElementById("f_org").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var strGrade = getSelectedValue("f_grade");
		var strEmployeType = getSelectedValue("f_employeType");
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
		}
		//alert("paramValues ===>> " + paramValues);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: strAction+'.action?f_org='+org+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
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
    	
	
	function checkAction(strAction) {
		
		$("#frmUsers").submit(function(e){
			e.preventDefault();
			var strMessage = 'Are you sure, you want to assign mobile access of selected employee?';
			var strButton = 'assignMbAccess=AssignMobileAccess';
			if(strAction == 'SendLoginDetails') {
				strMessage = 'Are you sure, you want to send login details of selected employee?';
				strButton = 'loginSubmit=SendLogin';
			}
			
			if(confirm(strMessage)) {
				var form_data = $("form[name='frmUsers']").serialize();
		     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		     	$.ajax({
		     		type : 'POST',
		 			url : "UserReport.action?"+strButton,
		 			data: form_data,  
		 			cache : false,
		 			success : function(res) {
		 				$("#divResult").html(res);
		 			}
		 		});
			}
		});
	}
	
	
	
	function getLevelwiseGrade() {
		
		var orgId = document.getElementById("f_org").value;
		var levelIds = getSelectedValue('f_level');
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var xhr = $.ajax({
				url : 'GetGradeList.action?fromPage=filter&orgId='+orgId+'&levelIds='+levelIds,
				cache : false,
				success : function(data) {
					
					document.getElementById('myGrade').innerHTML = data;
					$("#f_grade").multiselect().multiselectfilter();
				}
			});
		}
	}


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
	
	function selectMbAccessall(x,strEmpId) {
		var  status=x.checked; 
		var  arr= document.getElementsByName(strEmpId);
		var empIds = "";
		var j = 0;
		for(i=0; i<arr.length; i++) { 
	  		arr[i].checked = status;
	  		if(!arr[i].checked) {
	  			if (j == 0) {
	  				empIds = arr[i].value;
					j++;
				} else {
					empIds += "," + arr[i].value;
					j++;
				}
			}
	 	}
		document.getElementById("mbAccessDisableIds").value = empIds;
		
		/* if(x.checked == true){
			document.getElementById("unApproveSpan").style.display = 'none'; 
			document.getElementById("approveSpan").style.display = 'inline';
		} else {
			document.getElementById("unApproveSpan").style.display = 'inline';
			document.getElementById("approveSpan").style.display = 'none';
		} */
	}
	
	
	function checkmbAccessAll() {
		var sendMbAccessAll = document.getElementById("sendMbAccessAll");		
		var strSendMbAccess = document.getElementsByName('strSendMbAccess');
		var cnt = 0;
		var chkCnt = 0;
		var empIds = "";
		var j = 0;
		for(var i=0;i<strSendMbAccess.length;i++) {
			cnt++;
			if(strSendMbAccess[i].checked) {
				chkCnt++;
			}
			if(!strSendMbAccess[i].checked) {
	  			if (j == 0) {
	  				empIds = strSendMbAccess[i].value;
					j++;
				} else {
					empIds += "," + strSendMbAccess[i].value;
					j++;
				}
			}
		 }
		document.getElementById("mbAccessDisableIds").value = empIds;
		
		/* if(parseFloat(chkCnt) > 0) {
			document.getElementById("unApproveSpan").style.display = 'none';
			document.getElementById("approveSpan").style.display = 'inline';
		} else {
			document.getElementById("unApproveSpan").style.display = 'inline';
			document.getElementById("approveSpan").style.display = 'none';
		} */
		
		if(cnt == chkCnt) {
			sendMbAccessAll.checked = true;
		} else {
			sendMbAccessAll.checked = false;
		}
	}
	
	
	function showAccessControls(value) {
    	//alert("value == >"+value);
    	if(value == 1) {
    		callClockOnOffAccessControl();
    	} else if(value == 2) {
    		callGeofenceAccessControl();
    	} else if(value == 3) {
    		window.location='IncomeTaxSlabAccessControl.action';
    	}
    }
	
	
</script>
<%
    String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
    UtilityFunctions uF = new UtilityFunctions();
    Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
    if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
    
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO)
    	|| strUserType.equalsIgnoreCase(IConstants.CFO) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))) {
    %>
<%} %>
    
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<%=uF.showData((String)request.getAttribute(IConstants.MESSAGE),"") %>
		<s:form name="frmUsers" id="frmUsers" action="UserReport" theme="simple">
		<input type="hidden" name="mbAccessDisableIds" id="mbAccessDisableIds" />
			<div class="box box-default"> <!--  collapsed-box -->
    			<%-- <div class="box-header with-border">
         			<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
    				<div class="box-tools pull-right">
        				<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
        				<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
    				</div>
				</div> --%>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto;"> <!--  display:none; -->
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1', 'UserReport');" list="organisationList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Service</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
							</div>
							<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true" />
							</div> --%>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""  onchange="getLevelwiseGrade();" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                                <p style="padding-left: 5px;">Grade</p>
                               <div id="myGrade">
                                	<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"  />
                               </div>
                        	</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2', 'UserReport');"/>
							</div>
						</div>
					</div>
				</div>
				<!-- /.box-body -->
			</div>
			
			<div class="row row_without_margin" style="margin-bottom: 10px;">
				<div class="col-lg-6 col-md-6 col-sm-6 autoWidth">
					<select name="addEmpModes" id="addEmpModes" class="validateRequired" onchange="showAccessControls(this.value);">
						<option value="">Select Access Control</option>
						<option value="1">Clock On Off Access Control</option>
						<option value="2">Geo-fence Access Control</option>
						<option value="3">Income Tax Slab Access Control</option>
					</select>
				</div>
				<div class="col-lg-6 col-md-6 col-sm-6 autoWidth" style="float: right; padding-right: 0px;">
					<span id="unSendSpan" style="display: none;">
						<input type="button" name="unSend" class="btn btn-default" value="Send Login Details" />
					</span>
					<span id="sendSpan">
						<input type="submit" style="margin-top: 0px;" value="Send Login Details" name="loginSubmit" class="btn btn-primary" onclick="checkAction('SendLoginDetails');"/>
					</span>
					<span id="unApproveSpan" style="display: none;">
						<input type="button" name="unSend" class="btn btn-default" value="Assign Mobile Access" />
					</span>
					<span id="approveSpan">
						<input type="submit" style="margin-top: 0px;" value="Assign Mobile Access" name="assignMbAccess" class="btn btn-primary" onclick="checkAction('AssignMobileAccess');"/>
					</span>
				</div>
			</div>
				
			<table class="table table-bordered" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Username</th>
						<th style="text-align: left;">Password</th>
						<th style="text-align: left;">Created On</th>
						<th style="text-align: left;">User Type</th>
						<th style="text-align: left;">Status</th>
						<th style="text-align: left;">User Status</th>
						<th style="text-align: left;" class="no-sort">Reset</th>
						<th style="text-align: left;">Last Reset On</th>
						<th style="text-align: left;" class="no-sort">Action</th>
						<th align="left" class="no-sort">Login Details<br/><input type="checkbox" name="sendAll" id="sendAll" onclick="selectall(this, 'strSendLogin')" checked="checked"/></th>
						<th style="text-align: left;" class="no-sort">Mobile Access<br/><input type="checkbox" name="sendMbAccessAll" id="sendMbAccessAll" onclick="selectMbAccessall(this, 'strSendMbAccess')"/></th>
					</tr>
				</thead>
				<tbody>
				<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
				<% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %> 
					<tr id = <%=cinnerlist.get(0) %> >
						<td>
							<%if(docRetriveLocation==null) { %>
								<%-- <img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(13) %>" > --%>
								<%
								File file = new File(IConstants.DOCUMENT_LOCATION + cinnerlist.get(13));
								boolean existFile = false;
								if(file.exists()){
									existFile = true;
								}
								%>
								<%if(cinnerlist.get(13)!=null && !cinnerlist.get(13).equals("") && !cinnerlist.get(13).toString().contains("avatar_photo.png")){ %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right:5px; border:1px solid #CCCCCC;" src="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(13) %>" data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(13) %>" >
								<% } else{ %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(13) %>" >
								<% } %>
							<% } else { %>
							<% File file = new File(docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(11)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13));
							//System.out.println("empId==="+cinnerlist.get(11)+"---cinnerlist.get(13)=="+cinnerlist.get(13));
							//System.out.println("empId==="+docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(11)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13));
							boolean existFile = false;
							if(file.exists()){
								existFile = true;
							}
							%>
								<%-- <img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(11)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13) %>"> --%>
								<%-- <%if(cinnerlist.get(13)!=null && !cinnerlist.get(13).equals("") && !cinnerlist.get(13).toString().contains("avatar_photo.png")){ %> --%>
								<%if(existFile){ %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right:5px; border:1px solid #CCCCCC;" src="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(11)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13) %>" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(11)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13) %>">
								<% } else{ %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right:5px; border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(11)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13) %>">
								<% } %>
							<% } %>
							<%=cinnerlist.get(2) %>
						</td>
						<td><%=cinnerlist.get(1) %></td>
						<td><%=cinnerlist.get(3) %></td>
						<td><%=cinnerlist.get(4) %></td>
						<td><%=cinnerlist.get(5) %></td>
						<td><%=cinnerlist.get(6) %></td>
						<td><%=cinnerlist.get(7) %></td>
						<td><%=cinnerlist.get(12) %></td>
						<td><%=cinnerlist.get(8) %></td>
						<td><%=cinnerlist.get(9) %></td>
						<td><%=cinnerlist.get(10) %></td>
						<td>
						<% if(cinnerlist.get(12) != null && cinnerlist.get(12).toString().equalsIgnoreCase("Active")) { %> 
							<input type="checkbox" name="strSendLogin" id="strSendLogin" value="<%=cinnerlist.get(11)%>" onclick="checkAll();" checked/>
						<% } %>
						</td>
						<td>
							<% if(cinnerlist.get(12) != null && cinnerlist.get(12).toString().equalsIgnoreCase("Active")) { %> 
								<input type="hidden" name="hideEmpIds" value="<%=cinnerlist.get(11) %>" />
								<%
								String isMobileAuthorizedChecked = "";
								if(uF.parseToBoolean((String)cinnerlist.get(14))){
									isMobileAuthorizedChecked = "checked=\"checked\"";
								}
								%>
								<input type="checkbox" name="strSendMbAccess" id="strSendMbAccess" value="<%=cinnerlist.get(11)%>" onclick="checkmbAccessAll();" <%=isMobileAuthorizedChecked %>/>
							<% } %>
						</td>	
					</tr>
				<% } %>
    			</tbody>
			</table>
		</s:form>
	</div>

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
	
	

<script>
	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
</script>

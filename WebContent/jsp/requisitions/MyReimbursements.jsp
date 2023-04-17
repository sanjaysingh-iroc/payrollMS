<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>

<%String strUserType = (String) session.getAttribute(IConstants.USERTYPE); %>
<%String strE = (String)request.getParameter("E"); %>

<% if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) { %>
	<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<% } %>
<% CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF =  new UtilityFunctions(); %>

<script>

<% String approveAmount= (String)request.getAttribute("approveAmount");
   String pendingAmount= (String)request.getAttribute("pendingAmount");
   
   if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) {
%>
   var myExpense = [];
   myExpense.push({"name": "Approved","count": '<%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(approveAmount))%>',"color":"#54aa0d"});
   myExpense.push({"name": "Pending","count": '<%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(pendingAmount))%>',"color":" #FF5733"});

   
 $(function() {
	
	 /* var chart; */
	 /* reimbursementsChart = function () { */
	 var chart = AmCharts.makeChart("containerForReimbursements1", {
	  "type": "pie",
	  "startDuration": 0,
	   "theme": "",
	  "addClassNames": true,
	  "legend":{
	   	"position":"right",
	    "marginRight":10,
	    "autoMargins":false
	  },
	  "labelsEnabled": false,
	  "innerRadius": "",
	  "defs": {
	    "filter": [{
	      "id": "shadow",
	      "width": "250%",
	      "height": "250%",
	      "feOffset": {
	        "result": "offOut",
	        "in": "SourceAlpha",
	        "dx": 0,
	        "dy": 0
	      },
	      "feGaussianBlur": {
	        "result": "blurOut",
	        "in": "offOut",
	        "stdDeviation": 5
	      },
	      "feBlend": {
	        "in": "SourceGraphic",
	        "in2": "blurOut",
	        "mode": "normal"
	      }
	    }]
	  },
	  "dataProvider": myExpense,
	  "valueField": "count",
	  "titleField": "name",
	  "colorField": "color",
	  "export": {
	    "enabled": true
	  }
	});
	 /* } */
	 
	/* if (AmCharts.isReady) {
    	debugger;
    	reimbursementsChart();
      } else {
    	  debugger;
        AmCharts.ready(reimbursementsChart);
      } */
	
   });
 
 <% } %>
 </script>


<script type="text/javascript" charset="utf-8">
    //addLoadEvent(prepareInputsForHints);
    $(function() {
	
		$("body").on('click','#closeButton',function() {
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
	    });
		$("body").on('click','.close',function() {
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
		});
		
        $("#strStartDate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strEndDate').datepicker('setStartDate', minDate);
        });
        
        $("#strEndDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#strStartDate').datepicker('setEndDate', minDate);
        });

        $('#lt').DataTable({
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
    	$("#f_grade").multiselect().multiselectfilter();
    }); 
    
    
    function selectAll(x) {
    	var status=x.checked; 
    	var arr= document.getElementsByName("strReimId");
    	for(i=0; i<arr.length; i++) {
      		arr[i].checked=status;
     	}
    }
    
    
    function checkAll() {
   		var strAllReimId = document.getElementById("strAllReimId");		
   		var strReimId = document.getElementsByName('strReimId');
   		var cnt = 0;
   		var chkCnt = 0;
   		for(var i=0; i<strReimId.length; i++) {
   			cnt++;
   			 if(strReimId[i].checked) {
   				 chkCnt++;
   			 }
   		 }
   		if(cnt == chkCnt) {
   			strAllReimId.checked = true;
   		} else {
   			strAllReimId.checked = false;
   		}
   	}

    
    function bulkApproveDeny(status, strUserType1) {
    	var arr= document.getElementsByName("strReimId");
    	if(arr.length > 0) {
    		var strReim="";
    		var x=0;
    		
    		for(i=0;i<arr.length;i++) {
    			if(arr[i].checked == true) {
    		  		if(x==0) {
    		  			strReim = arr[i].value;
    		  		} else {
    		  			strReim +=","+ arr[i].value;
    		  		}
    		  		x++;
    			}
    	 	}
    		if(x > 0) {
    			var divResult = 'divResult';
    	    	var strBaseUserType = document.getElementById("strBaseUserType").value;
    	    	var strCEO = '<%=IConstants.CEO %>';
    	    	var strHOD = '<%=IConstants.HOD %>';
    	    	var strManager = '<%=IConstants.MANAGER %>';
    	    	var strUserType = '<%=strUserType %>';
    	    	if(strBaseUserType == strCEO || strBaseUserType == strHOD || strUserType != strManager) {
    	    		divResult = 'subDivResult';
    	    	}
    	    	//alert("strUserType1 ===>> " + strUserType1);
    			if(status=='1') {
    				if(confirm('Are you sure, you want to approve the selected expenses?')) {
    					var reason = window.prompt("Please enter your approval reason.");
    					if (reason != null) {
    						$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    						$.ajax({
    							type : 'POST',
    							url: 'UpdateReimbursements.action?T=bulk&M=AA&S=1&RID='+strReim+'&mReason='+reason+'&userType='+strUserType1,
    							//url: 'UpdateReimbursements.action?currUserType='+currUserType+'&S='+apStatus+'&RID='+reimbursementId+'&T=RIM&M=AA&mReason='+reason+'&userType='+userType+'&paycycle='+paycycle,
								success: function(result) {
    					        	$("#"+divResult).html(result); 
    					   		},
    							error: function(result) {
    								$.ajax({
    				    				url: 'Reimbursements.action',
    				    				cache: true,
    				    				success: function(result) {
    				    					//alert("result ============>> " + result);
    				    					$("#"+divResult).html(result);
    				    		   		}
    				    			}); 
    					   		}
    						});
    						//window.location='UpdateReimbursements.action?T=bulk&M=AA&S=1&RID=' + strReim+'&mReason='+reason;
    					}
    				}
    			} else if(status=='-1') {
    				if(confirm('Are you sure, you want to deny the selected expenses?')) {
    					var reason = window.prompt("Please enter your denial reason.");
    					if (reason != null) {
    						$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    						$.ajax({
    							type : 'POST',
    							url: 'UpdateReimbursements.action?T=bulk&M=AA&S=-1&RID=' + strReim +'&mReason='+reason+'&userType='+strUserType1,
    							success: function(result) {
    					        	$("#"+divResult).html(result); 
    					   		},
    							error: function(result) {
    								$.ajax({
    				    				url: 'Reimbursements.action',
    				    				cache: true,
    				    				success: function(result) {
    				    					//alert("result ============>> " + result);
    				    					$("#"+divResult).html(result);
    				    		   		}
    				    			}); 
    					   		}
    						});
    						//window.location='UpdateReimbursements.action?T=bulk&M=AA&S=-1&RID=' + strReim +'&mReason='+reason;
    					}
    				}
    			}
    		} else {
    			alert('Please select the expense.');
    		}
    	} else {
    		alert('Please select the expense.');
    	}
    }
    
    
    function getApprovalStatus(id,empname) {
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('Work flow of '+empname);
	   	$.ajax({
			url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=5",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    
    function approveDeny(apStatus,reimbursementId,userType) {
    	var divResult = 'divResult';
    	var strBaseUserType = document.getElementById("strBaseUserType").value;
    	var strCEO = '<%=IConstants.CEO %>';
    	var strHOD = '<%=IConstants.HOD %>';
    	var strManager = '<%=IConstants.MANAGER %>';
    	var strUserType = '<%=strUserType %>';
    	if(strBaseUserType == strCEO || strBaseUserType == strHOD || strUserType != strManager) {
    		divResult = 'subDivResult';
    	}
    	var status = '';
    	if(apStatus == '1') {
    		status='approval';
    	} else if(apStatus == '-1') {
    		status='denial';
    	}
    	var paycycle = document.getElementById("paycycle").value;
    	var currUserType = document.getElementById("currUserType").value;
    	if(confirm('Are you sure, do you want to '+status+' this request?')) {
    		var reason = window.prompt("Please enter your "+status+" reason.");
    		if (reason != null) {
    			$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					type : 'POST',
					url: 'UpdateReimbursements.action?currUserType='+currUserType+'&S='+apStatus+'&RID='+reimbursementId+'&T=RIM&M=AA&mReason='+reason+'&userType='+userType+'&paycycle='+paycycle,
					success: function(result) {
			        	$("#"+divResult).html(result); 
			   		},
			   		error: function(result) {
			   			$.ajax({
			   				url: 'Reimbursements.action?currUserType='+currUserType+'&paycycle='+paycycle,
			   				cache: true,
			   				success: function(result) {
			   					$("#"+divResult).html(result);
			   		   		}
			   			}); 
			   		} 
				});
    			/* var action = 'UpdateReimbursements.action?S='+apStatus+'&RID='+reimbursementId+'&T=RIM&M=AA&mReason='+reason+'&userType='+userType;
    			window.location = action; */
    		}
    	}
    } 

    
    function viewReimbursmentDetails(empId,reimbursementId) {
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('View Expense Details');
	   	 $.ajax({
			url : "ViewReimbursementDetails.action?strEmpId="+empId+"&reimbursementId="+reimbursementId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});	
    }
    
    
    function viewBulkExpenseDetails(empId,parentId) {
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('View Expense Details');
	   	 var height = $(window).height()* 0.95;
		 var width = $(window).width()* 0.95;
		 $(".modal-dialog").css("height", height);
		 $(".modal-dialog").css("width", width);
	   
	  	 $.ajax({
			url : "ViewBulkExpenseDetails.action?strEmpId="+empId+"&parentId="+parentId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});	
    }
    

    
    function importReimbursment() {
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('Quick bulk upload your Expenses');
	   	 $.ajax({
			url : "ImportReimbursements.action",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});	
    }
    
    function editReimbursement(id) {
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('Edit Expense');
	   	 $.ajax({
			url : "AddReimbursements.action?E="+id,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    /* function submitSelectEmpForm() {
    	document.frm_MyReimbursements.changeEmpType.value='emp';
    	document.frm_MyReimbursements.submit();
    } */
    
    
    function applyNewReimbursement() {
    	var paycycle = document.getElementById("paycycle").value;
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$(".modal-title").html('Apply New Expense');
    	$.ajax({
    		url : 'AddReimbursements.action?paycycle='+paycycle,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    
    function editNewReimbursement(rId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$(".modal-title").html('Edit Expense');
    	$.ajax({
    		url : 'AddReimbursements.action?E='+rId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    
    function submitForm(type) {
    	
    	var divResult = 'divResult';
    	var strBaseUserType = document.getElementById("strBaseUserType").value;
    	var strCEO = '<%=IConstants.CEO %>';
    	var strHOD = '<%=IConstants.HOD %>';
    	var strManager = '<%=IConstants.MANAGER %>';
    	var strUserType = '<%=strUserType %>';
    	if(strBaseUserType == strCEO || strBaseUserType == strHOD || strUserType != strManager) {
    		divResult = 'subDivResult';
    	}
    	var org = "";
    	var f_strWLocation = "";
    	
    	var department = "";
    	var service = "";
    	var level = "";
    	var strGrade = "";
    	
    	var strSelectedEmpId = "";
    	if(document.getElementById("f_org")) {
    		org = document.getElementById("f_org").value;
    	}
    	/* if(document.getElementById("f_strWLocation")) {
    		f_strWLocation = document.getElementById("f_strWLocation").value;
    	} */
    	
    	if(document.getElementById("f_strWLocation")) {
    		f_strWLocation = getSelectedValue("f_strWLocation");
    	}
    	
    	if(document.getElementById("f_department")) {
    		department = getSelectedValue("f_department");
    	}
    	
    	if(document.getElementById("f_service")) {
    		service = getSelectedValue("f_service");
    	}
    	
    	if(document.getElementById("f_level")) {
    		level = getSelectedValue("f_level");
    	}
    	
    	if(document.getElementById("f_grade")) {
    		strGrade = getSelectedValue("f_grade");
    	}
    	
    	if(document.getElementById("strSelectedEmpId")) {
    		strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
    	}
    	var currUserType = document.getElementById("currUserType").value;
    	
    	var paycycleDate = getCheckedValue("paycycleDate");
    	var paycycle = document.getElementById("paycycle").value;
    	var strStartDate = document.getElementById("strStartDate").value;
    	var strEndDate = document.getElementById("strEndDate").value;
    	var approveStatus = document.getElementById("approveStatus").value;
    	var paramValues = "f_org="+org+"&strf_WLocation="+f_strWLocation+"&currUserType="+currUserType;
    
    	//alert('paycycleDate ===>> ' + paycycleDate);
    	
    	if(type == '2') {
    		paramValues =paramValues+ '&strSelectedEmpId='+strSelectedEmpId+'&paycycleDate='+paycycleDate+'&paycycle='+paycycle
    		+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate+'&approveStatus='+approveStatus+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&strGrade='+strGrade;
    	}
    	//alert("paramValues ===>> " + paramValues);
    	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'Reimbursements.action?'+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result) {
            	$("#"+divResult).html(result);
       		}
    	});
    }
    
    
    function getCheckedValue(checkId) {
        var radioObj = document.getElementsByName(checkId);
        var radioLength = radioObj.length;
    	for(var i = 0; i < radioLength; i++) {
    		if(radioObj[i].checked) {
    			return radioObj[i].value;
    		}
    	}
    }
    
    
    function generateReportExcel() {
    	window.location = "ExportExcelReport.action?excelType=STANDARD";
    }
    
    
    function viewCancelReason(id, empname) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Cancelation reason of '+empname);
    	$("#modalInfo").show();
    	$.ajax({
    		url : "UpdateRequest.action?T=RIM&M=VIEW&RID="+id,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    
    function cancelReimbursement(reimbursementId, myDiv) {
    	if(confirm('Are you sure, do you want to cancel this request?')) {
    		var reason = window.prompt("Please enter your cancelation reason.");
    		if (reason != null) {
    			$.ajax({
    				type : 'GET',
    				url: 'UpdateRequest.action?S=-2&T=RIM&M=D&RID='+reimbursementId+'&strReason='+reason,
    				success: function(result) {
    		        	$("#"+myDiv).html(result); 
    		   		}
    			});
    		}
    	}
    }
    
    
    function cancelMyReimbursement(reimbursementId, myDiv) {
    	if(confirm('Are you sure, do you want to pullout this request?')) {
    		var reason = window.prompt("Please enter your pullout reason.");
    		if (reason != null) {
    			$.ajax({
    				type : 'GET',
    				url: 'UpdateRequest.action?S=2&T=RIM&M=D&RID='+reimbursementId+'&strReason='+reason,
    				success: function(result) {
    		        	$("#"+myDiv).html(result); 
    		   		}
    			});
    		}
    	}
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
   
    
    Map<String, String> hmUserTypeIdMap = (Map<String, String>) request.getAttribute("hmUserTypeIdMap");
    if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();
    
    String strApproveStatus = (String) request.getAttribute("approveStatus");
	String currUserType = (String) request.getAttribute("currUserType");
	
	String strUserTypeId = (String)session.getAttribute(IConstants.USERTYPEID);
	if(strUserType!=null && strUserType.equals(IConstants.MANAGER)) {
		if(currUserType != null && currUserType.equals("MYTEAM")) {
			strUserTypeId = hmUserTypeIdMap.get(IConstants.MANAGER);
		} else {
			strUserTypeId = hmUserTypeIdMap.get(currUserType);
		}
	}
	
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	boolean flagBulkExpenses = CF.getFeatureManagementStatus(request, uF, IConstants.F_SHOW_BULK_EXPENSES_LINK);
	%>
	
	<%-- <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>	
   		<div style="width: 100%;">
			<ul class="nav nav-pills">
				<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="Reimbursements.action?currUserType=MYTEAM" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;">My Team</a>
				</li>
				<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"  style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="Reimbursements.action?currUserType=<%=strBaseUserType %>" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
				</li>
			</ul>
		</div> 
	<% } %> --%>
		
	<%-- <% if(strUserTYpe!=null && (strUserTYpe.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserTYpe.equalsIgnoreCase(IConstants.ARTICLE) || strUserTYpe.equalsIgnoreCase(IConstants.CONSULTANT)) || strUserTYpe.equalsIgnoreCase(IConstants.HRMANAGER)) {%> --%>
	<div class="box-body" style="padding:5px; overflow-y:auto; min-height:600px;">
		<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
		<%session.setAttribute(IConstants.MESSAGE, ""); %>
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
				<h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frm" action="Reimbursements" theme="simple">
				<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
				<s:hidden name="currUserType" id="currUserType"/>
	    		<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
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
							<p style="padding-left: 5px;">Work Location</p>
							<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" list="wLocationList" key="" multiple="true" /><!-- onchange="submitForm('2');" -->	
						</div>
						
			<!-- ===Ajinkya -->		
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Department</p>
							<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Service</p>
							<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
						</div>
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
			<!-- ===end -->
						
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Employee</p>
							<s:select theme="simple" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="All Employees" list="empNamesList" key=""/>													
						</div>
					</div>
				</div>
				<%} %>
				<div class="row row_without_margin">
				<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
					<i class="fa fa-calendar"></i>
				</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<%
						String paycycleDate=(String)request.getAttribute("paycycleDate");
						String check1="";
						String check2="";
						if(paycycleDate!=null && paycycleDate.equals("2")) {
							check1="";
							check2="checked=\"checked\"";	
						}else{
							check1="checked=\"checked\"";
							check2="";
						}
						%>
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="radio" name="paycycleDate" id="paycycleDate" value="1" <%=check1 %>/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Paycycle</p>
							<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleListFull" key=""/>
						</div>
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="radio" name="paycycleDate" id="paycycleDate" value="2" <%=check2 %>/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">From Date</p>
							<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;"></s:textfield>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">To Date</p>
							<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;"></s:textfield>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Status</p>
							<s:select theme="simple" name="approveStatus" id="approveStatus" cssStyle="width: 110px !important;" list="#{'0':'All','1':'Approved', '2':'Pending','3':'Denied','4':'Canceled'}" />
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
						</div>
					</div>
				</div>
			</s:form>						    
		</div>
		</div>
        
        <div class="col-md-12" style="margin: 0px 0px 10px;">   		
		<% if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
			<a href="javascript:void(0)" onclick="applyNewReimbursement();" title="Apply New Expense"><i class="fa fa-plus-circle" ></i>Apply New Expense</a>
			<!-- <a href="BulkReimbursements.action"><i class="fa fa-plus-circle" ></i>Apply Bulk Reimbursements</a> -->
			<a href="BulkExpenses.action"><i class="fa fa-plus-circle" ></i>Apply Bulk Expenses</a>
			
			<%-- <% if(flagBulkExpenses) { %>
				<a href="BulkExpenses.action"><i class="fa fa-plus-circle" ></i>Apply Bulk Expenses</a>
			<% } %> --%>
			
			<!-- | <a href="javascript:void(0);" style="font-style: italic;" onclick="importReimbursment();">Import Reimbursements</a> -->
		<% } else if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.HRMANAGER)) { %>
			<!-- <p style="font-size: 12px;"><a href="javascript:void(0);" style="font-style: italic;" onclick="importReimbursment();">Import Reimbursements</a></p> -->
		<% } %>
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
		</div>
		<% List cOuterList = (List)request.getAttribute("alReport");
			if(cOuterList == null) cOuterList = new ArrayList();
			int pendingReimbCnt = (Integer) request.getAttribute("pendingReimbCnt");
		%> 
		<% if(pendingReimbCnt > 0) { %>
		<div class="col-md-12" style="margin: 0px 0px 10px;">
			<input type="button" name="btnBulkApprove" value="Bulk Approve" class="btn btn-primary" onclick="bulkApproveDeny('1', '<%=strUserTypeId %>');"/>
			<input type="button" name="btnBulkDeny" value="Bulk Deny" class="btn btn-danger" onclick="bulkApproveDeny('-1', '<%=strUserTypeId %>');"/>
		</div>
		<% } %>
		 <br> 
	<% if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) { %>
	 	<div class="col-sm-12 col-lg-12 col-md-12">
		 	<section class="col-sm-12 col-lg-6 col-md-6 connectedSortable paddingright5">
				<div class="box box-info">
		            <div class="box-header with-border">
		                <h3 class="box-title">Expenses Amount</h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
		            <!-- /.box-header -->
		            <div class="box-body" style="padding: 5px; max-height: 230px; min-height: 230px; overflow-y: auto;">
		                <div class="content1">
							<div id="containerForReimbursements1" style="height: 250px; width:100%;">&nbsp;</div>
						</div>
		            </div>
		            <!-- /.box-body -->
		        </div>
			</section>
			<section class="col-sm-12 col-lg-6 col-md-6 connectedSortable paddingright5">
				<div class="box box-info">
	                <% //if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) {
	                    String travelAmt = (String) request.getAttribute("travelAmt");
	                    String localAmt = (String) request.getAttribute("localAmt");
	                    String mobileBillAmt = (String) request.getAttribute("mobileBillAmt");
	                    String projectAmt = (String) request.getAttribute("projectAmt");
	                    //String quoteCount = (String) request.getAttribute("quoteCount");
					%> 
	                <div class="box-header with-border">
	                    <h3 class="box-title">Expenses Typewise Amount</h3>
	                    <div class="box-tools pull-right">
	                        <%-- <span data-toggle="tooltip" title="" class="badge bg-blue"><%=totalHubCount %></span> --%>
	                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                    </div>
	                </div>
	                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                    <div class="rosterweek" style="margin-top: 10px;">
	                        <div class="content1">
	                            <div class="holder">
	                                <ul class="site-stats">
	                                    <!-- <a href="Hub.action?type=F"> -->
	                                        <li class="bg_lh"><i class="fa fa-plane"></i>	 <strong><%=travelAmt %></strong> <small>Travel</small></li>
	                                    <!-- </a>
	                                    <a href="Hub.action?type=A"> -->
	                                        <li class="bg_lh"><i class="fa fa-car"></i> <strong><%=localAmt %></strong> <small>Local</small></li>
	                                    <!-- </a>
	                                    <a href="Hub.action?type=E"> -->
	                                        <li class="bg_lh"><i class="fa fa-mobile"></i> <strong><%=mobileBillAmt %></strong> <small>Mobile</small></li>
	                                    <!-- </a>
	                                    <a href="Hub.action?type=Q"> -->
	                                        <li class="bg_lh"><i class="fa fa-list-alt"></i> <strong><%=projectAmt %></strong> <small>Project</small></li>
	                                    <!-- </a> -->
	                                </ul>
	                            </div>
	                        </div>
	                    </div>
	                </div>
	                <!-- /.box-body -->
	            </div>
			</section>
		</div>
		 <br>   
	<% } %>   
		
		     
			<table class="table table-bordered" id="lt">
				<thead>
					<tr>
					<% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && pendingReimbCnt > 0) { %>
						<th style="text-align: left; width: 4%;"><input type="checkbox" name="strAllReimId" id="strAllReimId" onclick="selectAll(this)" checked></th>
					<% } %>
						<th style="text-align: left; width: 80%;">Reimbursements</th>
						<th style="text-align: left;">Workflow</th>
					</tr>
				</thead>
				<tbody>
				<% if(cOuterList.size() != 0) {
					for (int i=0; cOuterList!=null && i<cOuterList.size(); i++) {
				 	java.util.List cInnerList = (java.util.List)cOuterList.get(i); 
				 %>
					<tr>
						<% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && pendingReimbCnt > 0) { %>
							<td><%=cInnerList.get(1) %></td>
						<% } %>
						<td><%=cInnerList.get(0) %></td>
						<td><%=cInnerList.get(2) %> <%=cInnerList.get(3) %></td>
					</tr>
					<% } } %>
				</tbody>
			</table>
			<% if(cOuterList.size() != 0) { %>
			<div class="custom-legends">
			  <div class="custom-legend pullout">
			    <div class="legend-info">Pull Out</div>
			  </div>
			  <div class="custom-legend pending">
			    <div class="legend-info">Waiting for approval</div>
			  </div>
			  <div class="custom-legend approved">
			    <div class="legend-info">Approved</div>
			  </div>
			  <div class="custom-legend denied">
			    <div class="legend-info">Denied/ Canceled</div>
			  </div>
			  <div class="custom-legend re_submit">
			    <div class="legend-info">Waiting for workflow</div>
			  </div>
			  <% if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
				  <br/>
				  <div class="custom-legend no-borderleft-for-legend">
				    <div class="legend-info"><i class="fa fa-check-circle checknew" aria-hidden="true"></i>Approve Expense</div>
				  </div>
				  <div class="custom-legend no-borderleft-for-legend">
				    <div class="legend-info"> <i class="fa fa-times-circle cross" aria-hidden="true"></i>Deny Expense</div>
				  </div>
			  <% } %>
			</div>
			<% } %>
	</div>


	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">&nbsp;</h4>
	            </div>
	            <div class="modal-body" style="height:350px; overflow-y:auto; padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>

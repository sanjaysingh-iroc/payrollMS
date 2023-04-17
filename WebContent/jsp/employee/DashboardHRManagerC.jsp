<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*, ChartDirector.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>


<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
 <script src="scripts/ckeditor_cust/ckeditor.js"></script> 
 
<%  	
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		Map<String,Integer> hmChart1=(Map)request.getAttribute("hmchart1");
		if(hmChart1==null)hmChart1 = new HashMap();
		Map<String,Integer> hmChart2=(Map)request.getAttribute("hmchart2");
		if(hmChart2==null) hmChart2 = new HashMap(); 
		String []arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
		List alAchievements = (List)request.getAttribute("alAchievements");
		List alProbationEndDate = (List)request.getAttribute("alProbationEndDate");
		 
		List alAttendance = (List)request.getAttribute("alAttendance");
		List alRecruitment = (List)request.getAttribute("alRecruitment");
		UtilityFunctions uF = new UtilityFunctions();
		
		String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
		
%>

<style>
 
#greenbox {
height: 11px;
background-color:#00FF00; /* the critical component */
}
#redbox {
height: 11px;
background-color:#FF0000; /* the critical component */
}
#yellowbox {
height: 11px;
background-color:#FFFF00; /* the critical component */
}
#outbox {

height: 11px;
width: 100%;
background-color:#D8D8D8; /* the critical component */

}

.anaAttrib1 {
font-size: 14px;
font-family: digital;
color: #3F82BF;
font-weight: bold;
}

</style>


<!-- <link href="scripts/ckeditor/samples/sample.css" rel="stylesheet"> -->
	<script>
		 
		// The instanceReady event is fired, when an instance of CKEditor has finished
		// its initialization. 
		CKEDITOR.on( 'instanceReady', function( ev ) { 
			// Show the editor name and description in the browser status bar.
			document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
		
			// Show this sample buttons.
			document.getElementById( 'eButtons' ).style.display = 'block';
		}); 
		
		function InsertHTML() {
			// Get the editor instance that we want to interact with.
			var editor = CKEDITOR.instances.editor1;
			var value = document.getElementById( 'htmlArea' ).value;
		
			// Check the active editing mode.
			if ( editor.mode == 'wysiwyg' )
			{
				// Insert HTML code.
				// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertHtml
				editor.insertHtml( value );
			}
			else
				alert( 'You must be in WYSIWYG mode!' );
		}
		
		function InsertText() {
			// Get the editor instance that we want to interact with.
			var editor = CKEDITOR.instances.editor1;
			var value = document.getElementById( 'txtArea' ).value;
		
			// Check the active editing mode.
			if ( editor.mode == 'wysiwyg' )
			{
				// Insert as plain text.
				// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertText
				editor.insertText( value );
			}
			else
				alert( 'You must be in WYSIWYG mode!' );
		}
		
		function SetContents() {
			// Get the editor instance that we want to interact with.
			var editor = CKEDITOR.instances.editor1;
			var value = document.getElementById( 'htmlArea' ).value;
		
			// Set editor contents (replace current contents).
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-setData
			editor.setData( value );
		}
		
		function GetContents() {
			// Get the editor instance that you want to interact with.
			var editor = CKEDITOR.instances.editor1;
		
			// Get editor contents
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-getData
			alert( editor.getData() );
		}
		
		function ExecuteCommand( commandName ) {
			// Get the editor instance that we want to interact with.
			var editor = CKEDITOR.instances.editor1;
		
			// Check the active editing mode.
			if ( editor.mode == 'wysiwyg' )
			{
				// Execute the command.
				// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-execCommand
				editor.execCommand( commandName );
			}
			else
				alert( 'You must be in WYSIWYG mode!' );
		}
		
		function CheckDirty() {
			// Get the editor instance that we want to interact with.
			var editor = CKEDITOR.instances.editor1;
			// Checks whether the current editor contents present changes when compared
			// to the contents loaded into the editor at startup
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-checkDirty
			alert( editor.checkDirty() );
		}
		
		function ResetDirty() {
			// Get the editor instance that we want to interact with.
			var editor = CKEDITOR.instances.editor1;
			// Resets the "dirty state" of the editor (see CheckDirty())
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-resetDirty
			editor.resetDirty();
			alert( 'The "IsDirty" status has been reset' );
		}
		
		function Focus() {
			CKEDITOR.instances.editor1.focus();
		}
		
		function onFocus() {
			document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
		}
		
		function onBlur() {
			document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
		}
	</script>




<script type="text/javascript">
jQuery(document).ready(function() { 
  jQuery(".content1").show();
  //toggle the componenet with class msg_body
  jQuery(".heading").click(function()
  {
    jQuery(this).next(".content1").slideToggle(500);
    $(this).toggleClass("close_div");
  });
});

</script>
 
  <script type="text/javascript">
 function approveLeave(E,LID) {
		removeLoadingDiv('the_div');
		
		var dialogEdit = '#approveLeaveDiv';
		var data1 = "<div id=\"the_div\"><div id=\"ajaxLoadImage\"></div></div>";
		dialogEdit = $(data1).appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 650,
					width : 800,
					modal : true,
					title : 'Approve Leave',
					open : function() {
						var xhr = $.ajax({
							url : "ManagerLeaveApproval.action?type=type&E="+E+"&LID="+LID,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;

					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});

		$(dialogEdit).dialog('open');
	}  
 
 
 function reportJobProfilePopUp(recruitID) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#reportJobProfilePopup';
		
			$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false, 
			height : 650,  
			width : 800,
			modal : true,
			title : 'Job Profile',
			open : function() {
				var xhr = $.ajax({
					//url : "ApplyLeavePopUp.action", 
					url : "ReportJobProfilePopUp.action?view=openjobreport&recruitID="+recruitID ,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	}
 
 
 function denyRequest(ncount, RID) {
		removeLoadingDiv('the_div');
		
		var dialogEdit = '#denyRequest';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 250,
					width : 400,
					modal : true,
					title : 'Deny Reason',
					open : function() { 
						var xhr = $.ajax({
							url : "DenyRequestPopUp.action?ST=-1&RID=" + RID+"&requestDeny=popup&frmPage=HRDash",
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
		$(dialogEdit).dialog('open');
	}
 
 
 function denyProfile(ncount, RID) {
		removeLoadingDiv('the_div');
		
		/*alert("1");
		//var orgID = document.getElementById("f_org").value; 
		var orgID = document.getElementById("orgID").value;
		alert("2");
		var wlocID = document.getElementById("wlocID").value;
		alert("3");
		var desigID = document.getElementById("desigID").value;
		alert("4");
		var checkStatus = document.getElementById("checkStatus").value;
		alert("5");
		var fdate = document.getElementById("fdate").value;
		alert("6");
		var tdate = document.getElementById("tdate").value; 
		alert("7");
		var frmPage = document.getElementById("frmPage").value; 
		alert("8");
		
		var action = "JobDenyPopUp.action?ST=-1&RID="+RID+"&requestDeny=popup"+"&orgID="+orgID+"&wlocID="+wlocID
				+"&desigID="+desigID+"&checkStatus="+checkStatus+"&fdate="+fdate+"&tdate="+tdate+"&frmPage="+frmPage;
		alert("action====>"+action);
		
		var dialogEdit = '#denyProf';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 250,
					width : 400,
					modal : true,
					title : 'Job Deny Reason',
					open : function() {
						var xhr = $.ajax({
							url : action,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;

					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});

		$(dialogEdit).dialog('open');*/
		
		var frmPage = document.getElementById("frmPage").value;
		var dialogEdit = '#JobdenyPopup';
		$(dialogEdit).dialog(
		{
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 250,
			width : 400,
			modal : true,
			title : 'Job Deny Reason',
			open : function() {
				var xhr = $.ajax({
					url : "JobDenyPopUp.action?ST=-1&RID=" + RID+"&requestDeny=popup&frmPage="+frmPage,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
		
}

	
	
	
	function getDesignationDetails(desigId) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#designationDetails'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					width : 750,
					modal : true,
					title : 'Designation',
					open : function() {
						var xhr = $.ajax({
							url : "DesignationDetails.action?desig_id="+desigId,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
		$(dialogEdit).dialog('open');
	}
	
	function addDesignation(recruitID) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#UpdateDesignation'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 350,
					width : 400,
					modal : true,
					title : 'Update Designation',
					open : function() {
						var xhr = $.ajax({
							url : "AddCustomDesignation.action?recruitmentID="+recruitID,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
		$(dialogEdit).dialog('open');
	}
	
	
	function editRequest(ncount, RID) {

		removeLoadingDiv('the_div');
		var dialogEdit = '#UpdateJobPorfile'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
				.appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 650,
			width : '75%',
			modal : true,
			title : 'Edit Request',
			open : function() {
				var xhr = $.ajax({
					url : "RequirementRequest.action?recruitmentID="+RID+"&frmPage=HRDash",
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open');
	}
	
	
function closePopUP(){
	var recruitID=document.getElementById('recruitID').value;
	
	 $('#ViewJobPorfile').dialog('close');
	 
	 editProfile(recruitID);
	 
}	
	
 function editProfile(recruitId) {
	 removeLoadingDiv('the_div');
		//alert("editProfile ");
		var dialogEdit = '#editProfile'; 
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
		.appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 650,
			width : '80%',
			modal : true,
			title : 'Job Profile',
			open : function() {
				var xhr = $.ajax({					
					url : "UpdateJobProfilePopUp.action?recruitID="+recruitId+'&frmPage=HRDash',
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;

			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open');
	} 
	 
	
	function viewProfile(recruitid) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#ViewJobPorfile';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog(
						{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 650,
					width : 800,
					modal : true,
					title : 'Job Profile',
					open : function() {
						var xhr = $.ajax({
									url : "ViewJobProfilesApprovalPopUp.action?recruitID="
											+recruitid +"&view=view&frmPage=HRDash",
									cache : false,
									success : function(data) {
										$(dialogEdit).html(data);
									}
								});
						xhr = null;
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
		$(dialogEdit).dialog('open');
	}
	
	
	function denyProfile(ncount, RID) {
		removeLoadingDiv('the_div');
		
		var dialogEdit = '#JobdenyPopup';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog(
		{
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 250,
			width : 400,
			modal : true,
			title : 'Job Deny Reason',
			open : function() {
				var xhr = $.ajax({
					url : "JobDenyPopUp.action?ST=-1&RID=" + RID+"&requestDeny=popup&frmPage=HRDash",
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	}
	
	
	function approveRequest(nCount,RID){
		//alert(nCount +" "+ RID);
		if(confirm('Are you sure, you want to approve this request?')){
			getContent('myDivStatusR'+ nCount, "UpdateADRRequest.action?S=1&RID="+RID);
			document.getElementById("myDivR"+nCount).innerHTML="<a href=\"javascript:void(0)\" onclick=\"viewProfile('" + RID + "')\">View Job Profile</a>";
		}
	}
 
	function approveJob(nCount,RID){
		//alert(nCount +" "+ RID);
		if(confirm('Are you sure, you want to approve this profile?')){
			getContent('myDivStatusJP'+ nCount, "JobADRequest.action?S=1&RID="+RID);
			document.getElementById("myDivJP"+nCount).innerHTML="<span style=\"color: #68AC3B;\"> Approved </span>";
		}
	}
	
	
 function staffReviewPoup(id, empID, userType, currentLevel, role) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#comment';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : true, 
			height : 720,
			width : '92%',
			modal : true,
			title : 'Review Form',
			open : function() {
				var xhr = $.ajax({
					//url : "AppraisalDetail.action?id="+id+"&empId="+empId, 
					url : "IFrameStaffAppraisal.action?id=" + id + "&empID=" + empID + "&userType=" + userType + "&currentLevel=" + currentLevel
							+ "&role=" + role,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				}); 
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	}
	
	
	/* function staffReviewSummaryPoup(id, empID, userType, currentLevel, role) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#comment';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : true, 
			height : 720,
			width : '92%',
			modal : true,
			title : 'Review Summary Form',
			open : function() {
				var xhr = $.ajax({
					//url : "AppraisalDetail.action?id="+id+"&empId="+empId, 
					url : "EmpAppraisalSummary.action?id=" + id + "&empID=" + empID + "&userType=" + userType + "&currentLevel=" + currentLevel
							+ "&role=" + role,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				}); 
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	} */
	

	function seeUserTypeList(id, empId, sectionId, memberIds) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#userTypeList';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : true,
					height : 250,
					width : 550,
					modal : true,
					title : 'Workflow Preview',
					open : function() {
						var xhr = $.ajax({
							url : "UserTypeListPopUp.action?id=" + id
									+ "&empId=" + empId+ "&sectionId=" + sectionId+ "&memberIds=" + memberIds,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
		$(dialogEdit).dialog('open');
	}

function viewCertificate(strEmpId,planId) { 
		
		//window.open('ViewCertificate.action?ID='+id,'_newtab');
		removeLoadingDiv("the_div");
		
		 var dialogEdit = '#viewCertificateDiv';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 600,
			width : 800,
			modal : true,
			title : 'Certificate',
			open : function() {
				var xhr = $.ajax({
					url : "ViewEmpCertificate.action?strEmpId="+strEmpId+"&planId="+planId,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open'); 
	}
	
function approveDenyLeave(apStatus,leaveId,levelId,compensatory){
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var action = 'ManagerLeaveApproval.action?type=type&apType=auto&apStatus='+apStatus+'&E='+leaveId+'&LID='+levelId+'&strCompensatory='+compensatory+'&mReason='+reason;
			//alert(action); 
			window.location = action;
		}
	} 
}

function approveDenyRembursement(apStatus,reimbId){
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var action = 'UpdateReimbursements.action?type=type&S='+apStatus+'&RID='+ reimbId +'&T=RIM&M=AA&mReason='+reason; 
			//alert(action); 
			window.location = action;
		}
	}
}

</script>
 
 
<%

AngularMeter semiWorkedAbsent = (AngularMeter)request.getAttribute("KPI_BEST");
String semiBestKPI1URL = (String)semiWorkedAbsent.makeSession(request, "KPI_BEST");
Map hmEmployeeMap = (Map)request.getAttribute("hmEmployeeMap");
Map hmEmpDesigMap = (Map)request.getAttribute("hmEmpDesigMap");
Map hmEmpProfileImageMap = (Map)request.getAttribute("hmEmpProfileImageMap");
Map hmDepartmentMap = (Map)request.getAttribute("hmDepartmentMap");
Map hmWorkLocationMap = (Map)request.getAttribute("hmWorkLocationMap");

List alBirthDays = (List)request.getAttribute("alBirthDays");
if(alBirthDays==null)alBirthDays=new ArrayList();

List alEvents = (List)request.getAttribute("alEvents");
if(alEvents==null)alEvents=new ArrayList();

Map hmTopEmployees = (Map)request.getAttribute("hmTopEmployees");
if(hmTopEmployees==null)hmTopEmployees=new HashMap<String, Map<String, String>>();

Map hmDepartmentEmployeeCount = (Map)request.getAttribute("hmDepartmentEmployeeCount");
if(hmDepartmentEmployeeCount==null)hmDepartmentEmployeeCount=new HashMap<String, Map<String, String>>();

Map hmWLocationEmployeeCount = (Map)request.getAttribute("hmWLocationEmployeeCount");
if(hmWLocationEmployeeCount==null)hmWLocationEmployeeCount=new HashMap<String, Map<String, String>>();
List<List<String>> skillwiseEmpCountGraphList = (List<List<String>>) request.getAttribute("skillwiseEmpCountGraphList");
/* List<String> alSkillName =(List<String>)request.getAttribute("alSkillName"); 
Map<String,Integer> hmSkillCount=(Map<String,Integer>)request.getAttribute("hmSkillCount");
if(hmSkillCount==null)hmWLocationEmployeeCount=new HashMap<String,Integer>(); */

List<List<String>> leaveSummaryList = (List<List<String>>) request.getAttribute("leaveSummaryList");
if(leaveSummaryList == null) leaveSummaryList = new ArrayList<List<String>>();

List<List<String>> compensationSummaryList = (List<List<String>>) request.getAttribute("compensationSummaryList");
if(compensationSummaryList == null) compensationSummaryList = new ArrayList<List<String>>();

String compensationDate = (String) request.getAttribute("compensationDate");
compensationDate = uF.showData(compensationDate,"");

%>

<script type="text/javascript" src="scripts/chart/jquery.min.js"></script>
<script type="text/javascript" src="scripts/chart/highcharts.js"></script>

<style>
.counterLabel{
	font-family:Verdana;
	font-size:20px; 
	text-align:right; 
	padding-right:10px;
}

.counterText{
	font-family:Digital;
	font-size:20px; 
	text-align:right; 
	padding-right:10px;
}
</style>
 
<script type="text/javascript" >
var chartAttendance;
var chartAttendance1;
var chartAttendance2;
var chart1;
//var chartSkill;

Highcharts.setOptions({
    colors: ['#00FF00','#FF6633']
});

$(document).ready(function() {
	
	chartAttendance = new Highcharts.Chart({
   		
      chart: {
         renderTo: 'container_Attendance',
        	type: 'column'
      },
      title: {
         text: 'In (Hours)'
      },
      xAxis: {
         categories: [<%=request.getAttribute("sbDatesAttendanceDate")%>],
         labels: {
             rotation: -45,
             align: 'right',
             style: {
                 font: 'normal 10px Verdana, sans-serif'
             }
          },
         title: {
	            text: 'Date'
	         }
      },
      credits: {
       	enabled: false
   	  },
      yAxis: {
         min: 0,
         title: {
            text: 'Attendance'
         }
      },
      plotOptions: {
         column: {
            pointPadding: 0.2,
            borderWidth: 0
         }
      },
     series: [<%=request.getAttribute("sbDatesAttendance")%>]
   });
	
	<%-- 
	chartAttendance1 = new Highcharts.Chart({
   		
	      chart: {
	         renderTo: 'container_Attendance1',
	        	type: 'pie'
	      },
	      title: {
	         text: null
	      },
	      tooltip: {
	          formatter: function() {
	             return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
	          }
	       },
	       legend: {
		        enabled: true
   		},
   		plotOptions: {
   	         pie: {
   	            allowPointSelect: true,
   	            cursor: 'pointer',
   	            dataLabels: {
   	               enabled: false
   	            },
   	            showInLegend: true
   	         }
   	      },
	      series: [{
	          type: 'pie',
	          name: 'Attendance',
	          data: [<%=request.getAttribute("CHART_WORKED_ABSENT_C")%> ]
	       }]
	     
	   });
	 --%>
	
});



$(document).ready(function() {
	
	var chartSkill;

	chartSkill = new Highcharts.Chart({
	      chart: {
	          renderTo: 'containerForSkillCharts',
	          defaultSeriesType: 'column',
	        	 plotBorderWidth: 1
	       },
	       credits: {
	           enabled: false
	       }, 
	       title: {
	          text: ' '
	       },
	       
	       xAxis: {
	          categories: ['Skills']
	       },      
	       
	       yAxis: {
	     	  
	     	  lineWidth: 2,	//y axis itself
	           title: {
	              text: ' '
	 	        }
	       },
	       credits: {
	        	enabled: false
	 	   },
	 	   title: {
	 	 	  		text : '',
	 	     		floating: true
	 	   },
	       plotOptions: {
	     	  bar: {
	           pointPadding: 0.2,
	           borderWidth: 0
	        }
	       },           
	       
	       series: [
	       <%for(int i=0; skillwiseEmpCountGraphList!=null && i<skillwiseEmpCountGraphList.size(); i++){
	    	   List<String> innerList = skillwiseEmpCountGraphList.get(i);
	    	   if(innerList !=null && innerList.size() > 1) {
	    	   if(i==0){ 
           	%>
           	{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToInt(uF.showData(innerList.get(1), "0"))%>]
           	}
           	<%}else{%>
           	,{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToInt(uF.showData(innerList.get(1), "0"))%>]
           	}
           	<%}%>   
           	<%}%>
           <%}%>]
   });
});


$(document).ready(function() {
	
	var chartSkill;

	chartSkill = new Highcharts.Chart({
	      chart: {
	          renderTo: 'containerForLeaveSumaryCharts',
	          defaultSeriesType: 'column',
	        	 plotBorderWidth: 1
	       },
	       credits: {
	           enabled: false
	       }, 
	       title: {
	          text: ' '
	       },
	       
	       xAxis: {
	          categories: ['Leave Summary']
	       },      
	       
	       yAxis: {
	     	  
	     	  lineWidth: 2,	//y axis itself
	           title: {
	              text: ' '
	 	        }
	       },
	       credits: {
	        	enabled: false
	 	   },
	 	   title: {
	 	 	  		text : '',
	 	     		floating: true
	 	   },
	       plotOptions: {
	     	  bar: {
	           pointPadding: 0.2,
	           borderWidth: 0
	        }
	       },           
	       
	       series: [
	       <%for(int i=0; leaveSummaryList!=null && i<leaveSummaryList.size(); i++){
	    	   List<String> innerList = leaveSummaryList.get(i);
	    	   if(innerList !=null && innerList.size() > 1) {
	    	   if(i==0){ 
           	%>
           	{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToInt(uF.showData(innerList.get(1), "0"))%>]
           	}
           	<%}else{%>
           	,{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToInt(uF.showData(innerList.get(1), "0"))%>]
           	}
           	<%}%>   
           	<%}%>
           <%}%>]
   });
});

$(document).ready(function() {
	
	var chartSkill;

	chartSkill = new Highcharts.Chart({
	      chart: {
	          renderTo: 'containerForCompensationSumaryCharts',
	          defaultSeriesType: 'column',
	        	 plotBorderWidth: 1
	       },
	       credits: {
	           enabled: false
	       }, 
	       title: {
	          text: ' '
	       },
	       
	       xAxis: {
	          categories: ['<%=compensationDate %>']
	       },      
	       
	       yAxis: {
	     	  
	     	  lineWidth: 2,	//y axis itself
	           title: {
	              text: ' '
	 	        }
	       },
	       credits: {
	        	enabled: false
	 	   },
	 	   title: {
	 	 	  		text : '',
	 	     		floating: true
	 	   },
	       plotOptions: {
	     	  bar: {
	           pointPadding: 0.2,
	           borderWidth: 0
	        }
	       },           
	       
	       series: [
	       <%for(int i=0; compensationSummaryList!=null && i<compensationSummaryList.size(); i++){
	    	   List<String> innerList = compensationSummaryList.get(i);
	    	   if(i==0){ 
           	%>
           	{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToDouble(innerList.get(1))%>]
           	}
           	<%}else{%>
           	,{
           		name: '<%=innerList.get(0)%>',
	           	data: [<%=uF.parseToDouble(innerList.get(1))%>]
           	}
           	<%}%>   
           <%}%>]
   });
});

</script>

<script>
	function showClockMessage() {
		dojo.event.topic.publish("showClockMessage");
	}
	function showClockLabel() {
		dojo.event.topic.publish("showClockLabel");
	}
</script>

<%
	XYChart xyApprovals = (XYChart)request.getAttribute("CHART_APPROVALS");
	String xyApprovals1URL = (String)xyApprovals.makeSession(request, "chart2");
	String xyApprovalsURLMap1 = xyApprovals.getHTMLImageMap("", "", "title='{xLabel}: {value}'");

	Map<String,List<String>> hmThoughts = (Map<String, List<String>>) request.getAttribute("hmthoughts");
	Map hmMyAttendence = (HashMap) request.getAttribute("hmMyAttendence");
	List alReasons = (List)request.getAttribute("alReasons");
	List alLeaves = (List)request.getAttribute("alLeaves");
	List alLeaveRequest = (List)request.getAttribute("alLeaveRequest");
	List alRequisitionRequest = (List)request.getAttribute("alRequisitionRequest");
	List alReimbursementRequest = (List)request.getAttribute("alReimbursementRequest");
	
	//List<List<String>> trainingDetails = (List<List<String>>)request.getAttribute("trainingDetails");
	List<List<String>> learningDetails = (List<List<String>>)request.getAttribute("learningDetails");
	
	if (hmMyAttendence == null) {
		hmMyAttendence = new HashMap();
	}
	if(alReasons==null){
		alReasons = new ArrayList();
	}
	if(alLeaves==null){
		alLeaves = new ArrayList();
	}
	if(alLeaveRequest==null){
		alLeaveRequest = new ArrayList();
	}
	if(alRequisitionRequest==null){
		alRequisitionRequest = new ArrayList();
	}
	if(alReimbursementRequest==null){
		alReimbursementRequest = new ArrayList();
	}
%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="HR Manager's Dashboard" name="title"/>
</jsp:include>


<div class="leftbox reportWidth">
	<div id="left">
		
		<div id="clockcontainer">
			<p class="past heading">Upcoming Events and Birthdays</p>
				<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<%if(request.getAttribute("DAY_THOUGHT_TEXT")!=null){ %>
                <p class="thought">
                  <span> <%=request.getAttribute("DAY_THOUGHT_TEXT") %></span> 
                  <br/>
                   <span style="float:right;font-style:italic">- <strong><%=request.getAttribute("DAY_THOUGHT_BY") %></strong></span>
                </p>
                <%}
                			
					Map<String,List<String>> hmEventUpdates = (Map<String,List<String>>) request.getAttribute("eventUpdates");
					Map<String,List<String>> hmQuoteUpdates = (Map<String,List<String>>) request.getAttribute("quoteUpdates");
					Map<String,List<String>> hmNoticeUpdates = (Map<String,List<String>>) request.getAttribute("noticeUpdates");
					List<String> holidayList = (List<String>) request.getAttribute("holidays");
					
					
					if(holidayList == null ) holidayList = new ArrayList<String>();
					if(hmEventUpdates == null){
						hmEventUpdates = new LinkedHashMap<String,List<String>>();
					}
					
					if(hmQuoteUpdates == null){
						hmQuoteUpdates = new LinkedHashMap<String,List<String>>();
					}
					if(hmNoticeUpdates == null){
						hmNoticeUpdates = new LinkedHashMap<String,List<String>>();
					}
					
					if(hmQuoteUpdates != null && hmQuoteUpdates.size()>0){
						Set<String> quoteSet = hmQuoteUpdates.keySet();
						Iterator<String> qit = quoteSet.iterator();
						while(qit.hasNext()){
							String quoteId = qit.next();
							List<String> quoteList  = hmQuoteUpdates.get(quoteId);  
							if(quoteList == null ) quoteList = new ArrayList<String>();
							if(quoteList != null && quoteList.size()>0){
								
					%>
							<p class="empthought">
				          		<span> <%=quoteList.get(1) %></span> 
				          		<br/>
				          		<span style="float:right;font-style:italic">- <strong><%=quoteList.get(2) %></strong></span>
				        	</p>
					<%
							}
						}
					}
					%>
					
				<%for(int i=0; alBirthDays!=null && i<alBirthDays.size(); i++){ %>
                	<div class="repeat_row" style="width:90%;">
						<%=(String)alBirthDays.get(i) %>
					</div>
				<%}	
				
					if(hmNoticeUpdates != null && hmNoticeUpdates.size()>0){	
						Set<String> noticeSet = hmNoticeUpdates.keySet();
						Iterator<String> nit = noticeSet.iterator();
						while(nit.hasNext()){
							String noticeId = nit.next();
							List<String> noticeList  = hmNoticeUpdates.get(noticeId);  
							if(noticeList == null ) noticeList = new ArrayList<String>();
							if(noticeList != null && noticeList.size()>0){
					%>
							<div style="float: left; width: 100%; line-height: 16px; margin: 5px 0px 0px 7px;">
								<div style="float:left;font-style:bold;font-size:12px;"><%=noticeList.get(2) %></div>
								<a href="<%=noticeList.get(0) %>" style="float: right; margin-top: 3px;"><img title="Go to Announcements.." src="images1/icons/icons/forward_icon.png"></a> 
							</div>
					<%
							}
						}
					}
					%>
					<%
					
					if(hmEventUpdates != null && hmEventUpdates.size()>0){	
						Set<String> eventSet = hmEventUpdates.keySet();
						Iterator<String> eit = eventSet.iterator();
						while(eit.hasNext()){
							String eventId = eit.next();
							List<String> eventList  = hmEventUpdates.get(eventId);  
							if(eventList == null ) eventList = new ArrayList<String>();
							if(eventList != null && eventList.size()>0){
					%>
							<div style="float: left; width: 100%; line-height: 16px; margin: 5px 0px 0px 7px;">
							   <div style="float:left;font-size:12px;"><%=eventList.get(2) %></div> 
							 	<div style="float:left;margin-left:3px;font-size:12px;"> organised at <%=eventList.get(6) %></div> 
							 	<div style="margin-left:3px;float:left;font-size:12px;">from <%=eventList.get(4)%></div> 
							 	<div style="margin-left:3px;float:left;font-size:12px;">to <%=eventList.get(5)%> </div> 
							 	<a href="<%=eventList.get(0) %>" style="float: right; margin-top: 3px;"><img title="Go to Events.." src="images1/icons/icons/forward_icon.png"></a> 
							</div>
					<%
							}
						}
					}
						if(holidayList != null && holidayList.size()>0){
							Iterator hit  = holidayList.iterator();
							while(hit.hasNext()){
								String holidayData = (String) hit.next();	
							
					%>			
							<div style="float: left; width: 100%; line-height: 16px; margin: 5px 0px 0px 7px;"><%=holidayData%> </div>
					<%
							}
						}
					%>	
				</div>
			</div>
			
			<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0){ %>
			<div id="clockcontainer">
				<p class="past heading">Attendance Summary</p>
				<div class="content1">
					<div id="container_Attendance" style="height: 300px; width:95%; margin:10px 0 0 0px;"></div>
				</div>
			</div>
			<%} %>
			
			
			
			
			<%-- <%if(alUserModulesList!=null && alUserModulesList.contains(IConstants.MODULE_ONBOARDING)){ %>
			
			<div id="clockcontainer">
				<p class="past heading">Joinings </p>
				  <div class="content1">
					<div style="padding: 15px;">
						<b>	Accepted</b> : <%= hmChart2.get("acceptedCand")!=null && !hmChart2.get("acceptedCand").equals("") ? hmChart2.get("acceptedCand") : "0"%>
						<br>				
						<b>	Rejected</b> : <%= hmChart2.get("rejectedCand")!=null && !hmChart2.get("rejectedCand").equals("") ? hmChart2.get("rejectedCand") : "0"%>
						<br>
						<b>	Underprocess</b> : <%=hmChart2.get("underprocessCand")!=null && !hmChart2.get("underprocessCand").equals("") ? hmChart2.get("underprocessCand") : "0" %>
					</div>
				  <div class="holder">
		  			<div id="offerFinalStats" style="height: 200px; width:100%"></div>
	              </div>
                </div>  
			</div>
		
		
		
		
			<div id="clockcontainer">
				<p class="past heading">Applications </p>
				  <div class="content1">
					<div style="padding: 15px;">
					
					
						<b>  Accepted </b> : <%= hmChart1.get("acceptedAppl")!=null && !hmChart1.get("acceptedAppl").equals("") ? hmChart1.get("acceptedAppl") : "0"  %>
					  	<br>
						<b>  Rejected </b> : <%= hmChart1.get("rejectedAppl")!=null && !hmChart1.get("rejectedAppl").equals("") ? hmChart1.get("rejectedAppl") : "0"  %>
					  	<br>
						<b>  Underprocess </b> : <%= hmChart1.get("underprocessAppl")!=null && !hmChart1.get("underprocessAppl").equals("") ? hmChart1.get("underprocessAppl") : "0"  %>
					</div>  
					  <div class="holder">	
				  		<div id="applicationFinalstats" style="height: 200px; width:100%"></div>
	                   </div>
                   </div>  
			</div>

			<%} %>
 --%>
		
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
			Map<String, List<String>> hmCorporate = (Map<String, List<String>>) request.getAttribute("hmCorporate");
			Map<String, Map<String, String>> hmManagerGoalCalDetailsCorporate = (Map<String, Map<String, String>>)request.getAttribute("hmManagerGoalCalDetailsCorporate");
			%>
			<div id="details">
					<%
						String corpGoalCount = (String) request.getAttribute("corpGoalCount");
					%>
				<!-- <p class="past heading">Manager Goals & Team Goals & Targets </p> -->
				<p class="past heading">Corporate Goals [<%=corpGoalCount %>]</p>
				<div class="content1" style="max-height: 300px; overflow-y: auto;">
					<ul class="issuereasons">
					<%int iA=0;
						Iterator<String> it = hmCorporate.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next();
							List<String> cinnerList = hmCorporate.get(key);
							String pClass = cinnerList.get(32);
							
							String alltwoDeciTotProgressAvgCorporate = "0";
							String alltotal100Corporate = "100";
							String strtwoDeciTotCorporate = "0";
							if(hmManagerGoalCalDetailsCorporate != null && !hmManagerGoalCalDetailsCorporate.isEmpty()){
			 					Map<String, String> hmManagerGoalCalDetailsParentCorporate = hmManagerGoalCalDetailsCorporate.get(cinnerList.get(0));
								if(hmManagerGoalCalDetailsParentCorporate != null && !hmManagerGoalCalDetailsParentCorporate.isEmpty()){
									alltwoDeciTotProgressAvgCorporate = hmManagerGoalCalDetailsParentCorporate.get(cinnerList.get(0)+"_PERCENT");
									alltotal100Corporate = hmManagerGoalCalDetailsParentCorporate.get(cinnerList.get(0)+"_TOTAL");
									strtwoDeciTotCorporate = hmManagerGoalCalDetailsParentCorporate.get(cinnerList.get(0)+"_STR_PERCENT");	
								}
							}
					%>
						<li style="float:left;width:93%">
							<div style="float:left;width:45%; margin-right:5px;"><strong><%=cinnerList.get(3)%></strong></div>
							<div style="float:left;width:50%; margin-right:5px; text-align: right; color:#00FF00; font-weight: bold;">
								<div style="float: right; min-height: 40px; padding-right: 10px; width: 85%;">
									<div class="anaAttrib1" style="text-align: left; margin-left: <%=uF.parseToInt(alltwoDeciTotProgressAvgCorporate) > 94 ? uF.parseToInt(alltwoDeciTotProgressAvgCorporate)-9 : uF.parseToInt(alltwoDeciTotProgressAvgCorporate)-5.5 %>%;"><%=strtwoDeciTotCorporate%>%</span></div>
									<div id="outbox">
									<%if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) < 33.33){ %> 
									<div id="redbox" style="width: <%=alltwoDeciTotProgressAvgCorporate %>%;"></div>
									<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) < 66.67){ %>
									<div id="yellowbox" style="width: <%=alltwoDeciTotProgressAvgCorporate %>%;"></div>
									<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) >= 66.67){ %>
									<div id="greenbox" style="width: <%=alltwoDeciTotProgressAvgCorporate %>%;"></div>
									<%} %>
									</div>
									<div class="anaAttrib1" style="float: left; width: 100%;"><span style="float: left; margin-left:-3%;">0%</span>
									<span style="float: right;  margin-right:-8%;"><%=alltotal100Corporate %>%</span></div>
									<span style="color: #808080;float: left; font-size: 9px;">Slow</span>
									<span style="color: #808080; float: left; font-size: 9px; margin-left: 30px;">Steady</span>
									<span style="float: right; color: #808080; font-size: 9px; margin-right: -10px;">Momentum</span>
								</div>
							</div>
						</li>
						<%} if(hmCorporate==null){ %>
						<li style="float:left;width:93%" class="tdDashLabel">No Goals found.</li>
						<%} %>
					</ul>
				
					<div class="viewmore"><a href="GoalKRATargets.action?callFrom=GDash">
           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Corporate Goals.."/> --%>
           				
           				<i class="fa fa-forward" aria-hidden="true" title="Go to Corporate Goals.."></i>
           				
           				
           			</a></div>	
				</div>
			</div>
		<%} %>	
		
		
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0){ %>
			<div id="details">
				<p class="past heading">Recruitment [<%=alRecruitment != null ? alRecruitment.size() : "0" %>]<span style="float:right;margin-right:10px"><%-- <a href="OpenJobReport.action">[<%=((alRecruitment!=null)?alRecruitment.size():0) %>]</a> --%></span></p>
				<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				<%
				int iR = 0;
				for(iR=0; alRecruitment!=null && iR<alRecruitment.size(); iR++) {
					//if(iR>5)break;// to avoid long list on dahsboard
					List alInner = (List)alRecruitment.get(iR);
				%>
					<li style="float:left;width:93%">
						<div style="float:left;width:45%;margin-right:5px;"><%=alInner.get(0) %>:</div>
						<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#00FF00;font-weight: bold;"><%=alInner.get(1) %></div>
						<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#FF6633;font-weight: bold;"><%=alInner.get(2) %></div>
						<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#000099;font-weight: bold;"><%=alInner.get(3) %></div>
					</li>
				<%}if(iR==0){ %>
				<li style="float:left;width:93%" class="tdDashLabel">No ongoing recruitments.</li>
				<%} %>
				</ul>
				<%if(iR>0){ %>
				<div style="padding-left:10px">
					<%-- <span style="font-size:10px;color:#00FF00">Required</span> <span style="font-size:10px;">|</span>
					<span style="font-size:10px;color:#FF6633">Existing</span> <span style="font-size:10px;">|</span>
					<span style="font-size:10px;color:#000099">Planned</span> <span style="font-size:10px;">|</span> --%>
					<span style="font-size:10px;color:#00FF00">Required</span> <span style="font-size:10px;">|</span>
					<span style="font-size:10px;color:#FF6633">Completed</span> <span style="font-size:10px;">|</span>
					<span style="font-size:10px;color:#000099">Remaining</span> <span style="font-size:10px;">|</span>
				<div class="viewmore"><a href="JobList.action">
     				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Recruitment.."/> --%>
     				<i class="fa fa-forward" aria-hidden="true" title="Go to Recruitment.."></i>
     			</a></div>
				</div>
				<%} else { %>
				
				<div class="viewmore"><a href="JobList.action">
     				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Recruitment.."/> --%>
     				<i class="fa fa-forward" aria-hidden="true" title="Go to Recruitment.."></i>
     			</a></div>
     			<% } %>
				</div>
			</div>
		<%} %>
   
		
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){ 
			List<List<String>> liveAppraisalDetails = (List<List<String>>)request.getAttribute("liveAppraisalDetails");
		%>
			<div id="details">
				<p class="past heading">Performance [<%=liveAppraisalDetails != null ? liveAppraisalDetails.size() : "0" %>]</p>
				<div class="content1" style="max-height: 300px; overflow-y: auto;">
					<ul class="issuereasons">
					<%int iA=0;
					for(iA=0; liveAppraisalDetails!= null && !liveAppraisalDetails.isEmpty() && iA<liveAppraisalDetails.size(); iA++){ 
						List<String> appInner = liveAppraisalDetails.get(iA);
					%>
						<li style="float:left;width:93%">
							<div style="float:left;width:45%;margin-right:5px;"><%=appInner.get(0)%> : <p style="font-size:10px;color:#666;">(<%=appInner.get(1)%>)</p></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#00FF00;font-weight: bold;"><%=appInner.get(2)%></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#FF6633;font-weight: bold;"><%=appInner.get(3)%></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#000099;font-weight: bold;"><%=appInner.get(4)%></div>
						</li>
						<%} if(iA==0){ %>
						<li style="float:left;width:93%" class="tdDashLabel">No appraisals available for you.</li>
						<%} %>
					</ul>
					<%if(iA>0){ %>
					<div style="padding-left:10px">
						<span style="font-size:10px;color:#00FF00">Action Required</span> <span style="font-size:10px;">|</span>
						<span style="font-size:10px;color:#FF6633">Completed</span> <span style="font-size:10px;">|</span>
						<span style="font-size:10px;color:#000099">Remaining</span> <span style="font-size:10px;">|</span>
					<div class="viewmore"><a href="Reviews.action?callFrom=Dash">
	     				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Performance.."/> --%>
	     				
	     				<i class="fa fa-forward" aria-hidden="true" title="Go to Performance.."></i>
	     			</a></div>
					</div>
					<%} else { %>
					<div class="viewmore"><a href="Reviews.action?callFrom=Dash">
	     				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Performance.."/> --%>
	     				<i class="fa fa-forward" aria-hidden="true" title="Go to Performance.."></i>
	     				
	     				
	     			</a></div>
     			<% } %>
				</div>
			</div>
		<%} %>	
		
		
		
		<% 
		if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0){ %>
			<div id="details">
				<p class="past heading">Career Development [<%=learningDetails != null ? learningDetails.size() : "0" %>]</p>
				<div class="content1" style="max-height: 300px; overflow-y: auto;">
					<ul class="issuereasons">
					
						<%int iC=0;
						for(iC=0; learningDetails!= null && !learningDetails.isEmpty() && iC<learningDetails.size(); iC++){ 
						List<String> learnInner = learningDetails.get(iC);
					%>
						<li style="float:left;width:93%">
							<div style="float:left;width:45%;margin-right:5px;"><%=learnInner.get(0)%>:</div> <%-- (<%=trainInner.get(1)%>) --%>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#00FF00;font-weight: bold;"><%=learnInner.get(1)%></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#FF6633;font-weight: bold;"><%=learnInner.get(2)%></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#000099;font-weight: bold;"><%=learnInner.get(3)%></div>
						</li>
						<%} if(iC==0){ %>
						<li style="float:left;width:93%" class="tdDashLabel">No learning created.</li>
						<%} %>
					</ul>
					<%if(iC> 0){ %>
					<div style="padding-left:10px">
						<%-- <span style="font-size:10px;color:#00FF00">Invited Participants</span> <span style="font-size:10px;">|</span>
						<span style="font-size:10px;color:#FF6633">Participating</span> <span style="font-size:10px;">|</span> --%>
						<span style="font-size:10px;color:#00FF00">Learners</span> <span style="font-size:10px;">|</span>
						<span style="font-size:10px;color:#FF6633">Ongoing</span> <span style="font-size:10px;">|</span>
						<span style="font-size:10px;color:#000099">Pending</span> <span style="font-size:10px;">|</span>
					<div class="viewmore"><a href="Learnings.action?callFrom=Dash">
	     				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Learning.."/> --%>
	     				<i class="fa fa-forward" aria-hidden="true" title="Go to Learning.."></i>
	     			</a></div>
					</div>
					<%} else {%>
					<div class="viewmore"><a href="Learnings.action?callFrom=Dash">
	     				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Learning.."/> --%>
	     				
	     				<i class="fa fa-forward" aria-hidden="true" title="Go to Learning.."></i>
	     				
	     				
	     			</a></div>
					<% } %>
				</div>
			</div>
		<%} %>
		
		<%-- <% 
		if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0){
			List<Map<String,String>> learnGapList = (List<Map<String,String>>) request.getAttribute("trainingGapList");
		%>
			<div id="details">
				<p class="past heading">Learning Gaps: [<%=uF.parseToInt((String)request.getAttribute("schedule")) %>]</p>
				<div class="content1">
					<ul class="issuereasons">
					
						<%int iC=0;
						for(iC=0; learnGapList!= null && !learnGapList.isEmpty() && iC<learnGapList.size(); iC++){ 
							Map<String, String> hmLearnGap= learnGapList.get(iC);
					%>
						<li style="float:left;width:93%">
							<div style="float:left;width:60%;margin-right:5px;"><%=(iC+1) %>. <%=hmLearnGap.get("TRAINING_TITLE")%></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#00FF00;font-weight: bold;"><%=hmLearnGap.get("NO_OF_PARTICIPANT")%></div>
						</li>
						<%} if(iC==0){ %>
						<li style="float:left;width:93%" class="tdDashLabel">No Upcoming Learning.</li>
						<%} %>
					</ul>
					
				</div>
			</div>
		<%} %> --%>
		
	</div>






	<div id="center">
	
		<%-- <div id="details">
			<p class="past heading">Pending Counters</p>
			<div class="content1">
				<div class="skill_div">
                    <p class="sk_value"><%=uF.parseToInt((String)session.getAttribute("PENDING_EXCEPTION_COUNT"))%></p>             
                    <p class="sk_name">Attendance</p>                
               </div>  
               <div class="skill_div">
                    <p class="sk_value"><%=uF.parseToInt((String)session.getAttribute("LEAVE_REQUEST_COUNT"))%></p>             
                    <p class="sk_name">Leave</p>                
               </div>
               <div class="skill_div">
                    <p class="sk_value"><%=uF.parseToInt((String)session.getAttribute("PENDING_REIMBURSEMENT_COUNT"))%></p>             
                    <p class="sk_name">Reimbursements</p>                
               </div>
               <div class="skill_div">
                    <p class="sk_value"><%=uF.parseToInt((String)session.getAttribute("PENDING_REQUISITION_COUNT"))%></p>             
                    <p class="sk_name">Others</p>                
               </div>
               
			</div>
		</div> --%>
	
		<div id="details">
			<p class="past heading" style="width:100%">Attendance [<%=alAttendance != null ? alAttendance.size() : "0" %>]<span style="float:right;margin-right:10px"><%-- <a href="UpdateClockEntries.action">[<%=((alAttendance!=null)?alAttendance.size():0) %>]</a> --%></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
					<%
					int iA = 0;
					for(iA=0; alAttendance!=null && iA<alAttendance.size(); iA++){
						if(iA==10)break;
						List alInner = (List)alAttendance.get(iA);
					%>
					<li style="float:left;width:93%;margin:0px;">
					Exception pending from <%=alInner.get(0)%> (<%=alInner.get(1)%>) for <%=alInner.get(2)%>
					<p style="font-size: 10px;color: #666;"><%=((alInner.get(3)!=null)?alInner.get(3):"")%></p> 
					</li>
					<%}if(iA==0){%>
					<li style="float:left;width:93%" class="tdDashLabel">No pending exception</li>
					<%}else if (iA==10){ %>
					<li style="float:left;width:93%;margin:0px;text-align:right"><a href="UpdateClockEntries.action">Click here to see all</a></li>
					<%}%>
				</ul>
			</div>
		</div>
	  
	  
	  <%-- <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0){ %>
	
		<div id="details">
			<p class="past heading" style="width:100%">Leave [<%=alLeaveRequest != null ? alLeaveRequest.size() : "0" %>]<span style="float:right;margin-right:10px"><a href="ManagerLeaveApprovalReport.action">[<%=alLeaveRequest.size() %>]</a></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				<%
				int i=0;
				for(i=0; alLeaveRequest != null && i< alLeaveRequest.size(); i++){	
				%>
				<li style="float:left;width:93%"><%= (String)alLeaveRequest.get(i)%></li>
				<%
				}if(i==0){ %>
				<li style="float:left;width:93%" class="tdDashLabel">No one has applied leave for next one month. </li>
				<%} %>
				</ul>
			</div>
		</div>
		
		<% List alTravelRequest = (List)request.getAttribute("alTravelRequest"); %>
		<div id="details">
			<p class="past heading" style="width:100%">Travel [<%=alTravelRequest != null ? alTravelRequest.size() : "0" %>]</p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				<%
				i=0;
				for(i=0; alTravelRequest != null && i< alTravelRequest.size(); i++){	
				%>
				<li style="float:left;width:93%"><%= (String)alTravelRequest.get(i)%></li>
				<%
				}if(i==0){ %>
				<li style="float:left;width:93%" class="tdDashLabel">No one has applied travel for next one month. </li>
				<%} %>
				</ul>
			</div>
		</div>
	<%} %>	 --%> 
		
		
	<%-- <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0){ %>	
		<div id="details">
			<p class="past heading" style="width:100%">Reimbursements [<%=alReimbursementRequest != null ? alReimbursementRequest.size() : "0" %>]<span style="float:right;margin-right:10px"><a href="Reimbursements.action">[<%=alReimbursementRequest.size() %>]</a></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
					<%
					int i=0;
					for(i=0; alReimbursementRequest!=null && i< alReimbursementRequest.size(); i++){
					%>
					<li style="float:left;width:93%"><%= (String)alReimbursementRequest.get(i)%></li>
					<%
					}if(i==0){ %>
					<li style="float:left;width:93%" class="tdDashLabel">No one has applied for any reimbursement.</li>
					<%} %>
				</ul>
			</div>
		</div>
	<%} %>	 --%> 
	
		<%-- <div id="details">
			<div><p class="past heading" style="width:100%">Other Requests <span style="float:right;margin-right:10px"><a href="Requisitions.action">[0]</a></span></p></div>
			<div class="content1">
				<ul class="issuereasons">
					<li style="float:left;width:93%" class="tdDashLabel">There is no other request pending</li>
				</ul>
			</div>
		</div> --%>
		
		<div id="details">
			<p class="past heading" style="width:100%">Other Requests [<%=alProbationEndDate != null ? alProbationEndDate.size() : "0" %>]<%-- <span style="float:right;margin-right:10px"><a href="Requisitions.action">[0]</a></span> --%></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
					<% 
					int ii=0;
					for(ii=0; alProbationEndDate!=null && ii< alProbationEndDate.size(); ii++){
					%>
					<li style="float:left;width:93%"><%= (String)alProbationEndDate.get(ii)%></li>
					<%
					}if(ii==0){ %>
					<li style="float:left;width:93%" class="tdDashLabel">No one's probation is ending in the next 30 days.</li>
					<%} %>
				</ul>
				<div class="viewmore"><a href="Requisitions.action">
       				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Other Requests.."/> --%>
       				<i class="fa fa-forward" aria-hidden="true" title="Go to Other Requests.."></i>
       				
       			</a></div>
			</div>
		</div>
		
		
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0){ 
			List<List<String>> recentAwardedEmpList = (List<List<String>>) request.getAttribute("recentAwardedEmpList");
		%>	
		<div id="details">
			<p class="past heading" style="width:100%">Awarded Employees [<%=recentAwardedEmpList != null ? recentAwardedEmpList.size() : "0" %>]<span style="float:right;margin-right:10px"><%-- <a href="Reimbursements.action">[<%=alReimbursementRequest.size() %>]</a> --%></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				
				<%
						for(int i=0; recentAwardedEmpList!=null && i<recentAwardedEmpList.size(); i++) {
					%>
						<li style="float:left;width:93%"><%=recentAwardedEmpList.get(i) %></li>
					<%
						} if(recentAwardedEmpList==null || recentAwardedEmpList.isEmpty() || (recentAwardedEmpList!=null && recentAwardedEmpList.size()==0)){
					%>
						<li style="float:left;width:93%" class="tdDashLabel"> No awarded employee. </li>
					<%
						}
					%>
				</ul>
			</div>
		</div>
	<%} %>
	
		
		<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PEOPLE_MANAGEMENT+"")>=0) { 
		
		%>	
		<div id="details">
		<% List<List<String>> newJoineeEmpList = (List<List<String>>) request.getAttribute("newJoineeEmpList"); %>
			<p class="past heading" style="width:100%">New Joinees [<%=newJoineeEmpList != null ? newJoineeEmpList.size() : "0" %>]<span style="float:right;margin-right:10px"><%-- <a href="Reimbursements.action">[<%=alReimbursementRequest.size() %>]</a> --%></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				
				<% 
						for(int i=0; newJoineeEmpList!=null && i<newJoineeEmpList.size(); i++) {
					%>
						<li style="float:left;width:93%"><%=newJoineeEmpList.get(i) %></li>
					<%
						} if(newJoineeEmpList==null || newJoineeEmpList.isEmpty() || (newJoineeEmpList!=null && newJoineeEmpList.size()==0)){
					%>
						<li style="float:left;width:93%" class="tdDashLabel"> No new joinee for a week. </li>
					<%
						}
					%>
				</ul>
				
				<div class="viewmore"><a href="EmployeeReport.action">
       				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Employee Dashboard.."/> --%>
       				<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Dashboard.."></i>
       				
       			</a></div>
			</div>
		</div>
		
		<div id="details">
		<% List<List<String>> confirmationEmpList = (List<List<String>>) request.getAttribute("confirmationEmpList"); %>
			<p class="past heading" style="width:100%">Confirmations [<%=confirmationEmpList != null ? confirmationEmpList.size() : "0" %>]<span style="float:right;margin-right:10px"><%-- <a href="Reimbursements.action">[<%=alReimbursementRequest.size() %>]</a> --%></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				
				<% 
						for(int i=0; confirmationEmpList!=null && i<confirmationEmpList.size(); i++) {
					%>
						<li style="float:left;width:93%"><%=confirmationEmpList.get(i) %></li>
					<%
						} if(confirmationEmpList==null || confirmationEmpList.isEmpty() || (confirmationEmpList!=null && confirmationEmpList.size()==0)){
					%>
						<li style="float:left;width:93%" class="tdDashLabel"> No Confirmations. </li>
					<%
						}
					%>
				</ul>
				
				<div class="viewmore"><a href="EmployeeActivity.action?empType=C">
       				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Employee Dashboard.."/> --%>
       				<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Dashboard.."></i>
       				
       				
       				
       			</a></div>
			</div>
		</div>
		
		<div id="details">
		<% List<List<String>> resignationEmpList = (List<List<String>>) request.getAttribute("resignationEmpList"); %>
			<p class="past heading" style="width:100%">Resignations [<%=resignationEmpList != null ? resignationEmpList.size() : "0" %>]<span style="float:right;margin-right:10px"><%-- <a href="Reimbursements.action">[<%=alReimbursementRequest.size() %>]</a> --%></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				
				<% 
						for(int i=0; resignationEmpList!=null && i<resignationEmpList.size(); i++) {
					%>
						<li style="float:left;width:93%"><%=resignationEmpList.get(i) %></li>
					<%
						} if(resignationEmpList==null || resignationEmpList.isEmpty() || (resignationEmpList!=null && resignationEmpList.size()==0)){
					%>
						<li style="float:left;width:93%" class="tdDashLabel"> No Resignations. </li>
					<%
						}
					%>
				</ul>
				
				<div class="viewmore"><a href="EmployeeActivity.action?empType=R">
       				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Employee Dashboard.."/> --%>
       				<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Dashboard.."></i>
       				
       				
       				
       			</a></div>
			</div>
		</div>
		
		<div id="details">
		<% List<List<String>> finalDayEmpList = (List<List<String>>) request.getAttribute("finalDayEmpList"); %>
			<p class="past heading" style="width:100%">Final Day Employees [<%=finalDayEmpList != null ? finalDayEmpList.size() : "0" %>]<span style="float:right;margin-right:10px"><%-- <a href="Reimbursements.action">[<%=alReimbursementRequest.size() %>]</a> --%></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				
				<% 
						for(int i=0; finalDayEmpList!=null && i<finalDayEmpList.size(); i++) {
					%>
						<li style="float:left;width:93%"><%=finalDayEmpList.get(i) %></li>
					<%
						} if(finalDayEmpList==null || finalDayEmpList.isEmpty() || (finalDayEmpList!=null && finalDayEmpList.size()==0)){
					%>
						<li style="float:left;width:93%" class="tdDashLabel"> No final day Employees. </li>
					<%
						}
					%>
				</ul>
				
				<div class="viewmore"><a href="EmployeeActivity.action?empType=FD">
       				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Employee Dashboard.."/> --%>
       				<i class="fa fa-forward" aria-hidden="true" title="Go to Employee Dashboard.."></i>
       				
       				
       				
       			</a></div>
			</div>
		</div>
	<%} %>
	
	
		<%-- <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0){ 
			List<String> requestList = (List<String>) request.getAttribute("requestList");
		%>	
		<div id="details">
			<p class="past heading" style="width:100%">Requirements [<%=requestList != null ? requestList.size() : "0" %>]<span style="float:right;margin-right:10px"><a href="Reimbursements.action">[<%=alReimbursementRequest.size() %>]</a></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				
				<%
						for(int i=0; requestList!=null && i<requestList.size(); i++){
					%>
						<li style="float:left;width:93%"><%=requestList.get(i) %></li>
					<%
						} if(requestList==null || requestList.isEmpty() || (requestList!=null && requestList.size()==0)){
					%>
						<li style="float:left;width:93%" class="tdDashLabel"> No requirement application made yet. </li>
					<%
						}
					%>
				</ul>
				
				<div class="viewmore"><a href="RequirementApproval.action">
       				<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Requirement Request.."/>
       			</a></div>
			</div>
		</div>
	<%} %> --%>
	
	
	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0){ 
		List<String> jobProfileList = (List<String>) request.getAttribute("jobProfileList");
		%>	
		<div id="details">
			<p class="past heading" style="width:100%">Job Profiles [<%=jobProfileList != null ? jobProfileList.size() : "0" %>]<span style="float:right;margin-right:10px"><%-- <a href="Reimbursements.action">[<%=alReimbursementRequest.size() %>]</a> --%></span></p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul class="issuereasons">
				
				<%
						for(int i=0; jobProfileList!=null && i<jobProfileList.size(); i++){
					%>
						<li style="float:left;width:93%"><%=jobProfileList.get(i) %></li>
					<%
						}if(jobProfileList==null || jobProfileList.isEmpty() || (jobProfileList!=null && jobProfileList.size()==0)){
					%>
						<li style="float:left;width:93%" class="tdDashLabel"> No job profile is available for approval. </li>
					<%
						}
					%>
				</ul>
				<div class="viewmore"><a href="JobProfilesApproval.action">
       				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Job Profiles.."/> --%>
       				<i class="fa fa-forward" aria-hidden="true" title="Go to Job Profiles.."></i>
       				
       			</a></div>
			</div>
		</div>
	<%} %>
	
		
		<% 
		if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
		%>
		
		<%
			Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");	
			Map<String, String> hmEmpCodeDesig = (Map<String, String>)request.getAttribute("hmEmpCodeDesig");
			if(hmEmpCodeDesig==null) hmEmpCodeDesig=new HashMap<String, String>();
		 	Map<String,String> orientationMemberMp=(Map<String,String> )request.getAttribute("orientationMemberMp");
			if(orientationMemberMp==null)orientationMemberMp=new HashMap<String,String>();
			
			Map<String,Map<String, String>> appraisalDetails=(Map<String,Map<String, String>> )request.getAttribute("appraisalDetails");
			List<String> appraisalIdList=(List<String> )request.getAttribute("appraisalIdList");
			Map<String,Map<String,List<String>>> empMpDetails=(Map<String,Map<String,List<String>>> )request.getAttribute("empMpDetails");
			Map<String,Map<String,Map<String,String>>> appraisalStatusMp=(Map<String,Map<String,Map<String,String>>> )request.getAttribute("appraisalStatusMp");
			Map<String, List<List<String>>> hmAppraisalSectins = (Map<String, List<List<String>>>)request.getAttribute("hmAppraisalSectins");
			Map<String, Map<String, List<String>>> hmRemainOrientDetailsAppWise = (Map<String, Map<String, List<String>>>)request.getAttribute("hmRemainOrientDetailsAppWise");
			Map<String, Map<String, List<String>>> hmRemainOrientDetailsForSelfAppWise = (Map<String, Map<String, List<String>>>)request.getAttribute("hmRemainOrientDetailsForSelfAppWise");
			Map<String, Map<String, List<String>>> hmRemainOrientDetailsForPeerAppWise = (Map<String, Map<String, List<String>>>)request.getAttribute("hmRemainOrientDetailsForPeerAppWise");
			Map<String,List<String>> hmExistUsersAQA = (Map<String,List<String>>)request.getAttribute("hmExistUsersAQA");
			if(hmExistUsersAQA==null)hmExistUsersAQA=new HashMap<String,List<String>>();
			Map<String,List<String>> hmOrientTypewiseID = (Map<String,List<String>>)request.getAttribute("hmOrientTypewiseID");
			if(hmOrientTypewiseID==null)hmOrientTypewiseID=new HashMap<String,List<String>>();
			Map<String, List<String>> hmExistSectionID = (Map<String, List<String>>)request.getAttribute("hmExistSectionID");
			if(hmExistSectionID==null)hmExistSectionID=new HashMap<String,List<String>>();
			Map<String, Map<String,List<String>>> hmExistOrientTypeAQAAppWise = (Map<String, Map<String,List<String>>>)request.getAttribute("hmExistOrientTypeAQAAppWise");
			Map<String,String> hmSectionwiseWorkflow = (Map<String,String>) request.getAttribute("hmSectionwiseWorkflow");
			Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
			
			String reviewEmpCount = (String) request.getAttribute("reviewEmpCount");
		%>
			<div id="details">
				<p class="past heading">Reviews [<%=reviewEmpCount %>]</p>
				<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<ul style="float: left; width:94%;">
				<%
				String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
				for(int j=0; appraisalIdList!=null && j<appraisalIdList.size(); j++) {
					Map<String,Map<String,String>> userTypeMp=appraisalStatusMp.get(appraisalIdList.get(j));
					if(userTypeMp==null)userTypeMp=new HashMap<String,Map<String,String>>();
					Map<String, String> hmAppraisalMp =appraisalDetails.get(appraisalIdList.get(j));
					Map<String,List<String>> empMp=empMpDetails.get(appraisalIdList.get(j));
					List<List<String>> listAppSections = hmAppraisalSectins.get(appraisalIdList.get(j));
					Set<String> keys=empMp.keySet();
					Iterator<String> it=keys.iterator();
					while(it.hasNext()){
						String key=it.next();
						Map<String,String> empstatusMp=userTypeMp.get(key);
						if(empstatusMp==null)empstatusMp=new HashMap<String,String>();
						List<String> employeeList =empMp.get(key);
					for(int i=0; employeeList!=null && i<employeeList.size(); i++){
						List<String> sectionIDList = hmExistSectionID.get(employeeList.get(i)+"_"+appraisalIdList.get(j));
						Map<String, List<String>> hmRemainOrientDetails = hmRemainOrientDetailsAppWise.get(appraisalIdList.get(j));
						if(hmRemainOrientDetails==null)hmRemainOrientDetails=new HashMap<String, List<String>>();
						
						Map<String, List<String>> hmRemainOrientDetailsForSelf = hmRemainOrientDetailsForSelfAppWise.get(appraisalIdList.get(j));
						if(hmRemainOrientDetailsForSelf==null)hmRemainOrientDetailsForSelf=new HashMap<String, List<String>>();
						
						Map<String, List<String>> hmRemainOrientDetailsForPeer = hmRemainOrientDetailsForPeerAppWise.get(appraisalIdList.get(j));
						if(hmRemainOrientDetailsForPeer==null)hmRemainOrientDetailsForPeer=new HashMap<String, List<String>>();
						
						Map<String, List<String>> hmExistOrientTypeAQA = hmExistOrientTypeAQAAppWise.get(appraisalIdList.get(j));
						if(hmExistOrientTypeAQA==null)hmExistOrientTypeAQA=new HashMap<String, List<String>>();
						String role = orientationMemberMp.get(key);
					%>
					<li class="list">
					<div style="float: left; width: 77%;">
						<span style="float: left;"><b><%=hmAppraisalMp.get("APPRAISAL")%></b> for 
						<%
							if (strSessionEmpId.equals(employeeList.get(i))) {
						%>
							You
						<%
							} else {
						%>
							<%=hmEmpName.get(employeeList.get(i))%>
						<%
							}
						%>
						 [Role-<%=role%>] <br/>
						<i><%=hmAppraisalMp.get("REVIEW_TYPE")%>, <%=hmAppraisalMp.get("ORIENT")%>&deg;, <%=hmAppraisalMp.get("FREQUENCY")%>, <%=hmAppraisalMp.get("TO")%></i>
						</span>
					</div>
				
						<div style="float: left; margin-top: 5px;">
						<%
						for(int k=0; listAppSections!= null && !listAppSections.isEmpty() && k<listAppSections.size(); k++){
							List<String> innerList = listAppSections.get(k);
							List<String> listRemainOrientName = hmRemainOrientDetails.get(innerList.get(0)+"NAME");
							List<String> listRemainOrientID = hmRemainOrientDetails.get(innerList.get(0)+"ID");
							
							List<String> listRemainOrientNameForSelf = hmRemainOrientDetailsForSelf.get(innerList.get(0)+"NAME");
							List<String> listRemainOrientIDForSelf = hmRemainOrientDetailsForSelf.get(innerList.get(0)+"ID");
							
							List<String> listRemainOrientNameForPeer = hmRemainOrientDetailsForPeer.get(innerList.get(0)+"NAME");
							List<String> listRemainOrientIDForPeer = hmRemainOrientDetailsForPeer.get(innerList.get(0)+"ID");
							List<String> listExistOrientTypeInAQA = hmExistOrientTypeAQA.get(innerList.get(0)+"_"+employeeList.get(i));
							
							List<String> listRemainOrientType = new ArrayList<String>();
							List<String> listRemainOrientTypeForSelf = new ArrayList<String>();
							List<String> listRemainOrientTypeForPeer = new ArrayList<String>();
							StringBuilder sbRemainOrientTypeID = new StringBuilder();
							StringBuilder sbRemainOrientTypeIDForSelf = new StringBuilder();
							StringBuilder sbRemainOrientTypeIDForPeer = new StringBuilder();
							for(int b = 0; listRemainOrientID != null && b<listRemainOrientID.size();b++){
								if(listExistOrientTypeInAQA != null){
								if(!listExistOrientTypeInAQA.contains(listRemainOrientID.get(b))){
									listRemainOrientType.add(listRemainOrientName.get(b));
									sbRemainOrientTypeID.append(listRemainOrientID.get(b)+",");
								} else if(!listRemainOrientID.get(b).equals("3")){
									List<String> listExistUserInAQA = hmExistUsersAQA.get(employeeList.get(i)+"_"+innerList.get(0)+"_"+listRemainOrientID.get(b));
									List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j)+"_"+listRemainOrientID.get(b));
									boolean flag = false;
									for(int a = 0; listIds != null && a<listIds.size();a++){
										if(listExistUserInAQA != null){
										if(!listExistUserInAQA.contains(listIds.get(a))){
											flag = true;
										}
										}
									}
										if(flag == true){
											listRemainOrientType.add(listRemainOrientName.get(b));
											sbRemainOrientTypeID.append(listRemainOrientID.get(b)+",");
										}
								} 
								}else{
									listRemainOrientType.add(listRemainOrientName.get(b));
									sbRemainOrientTypeID.append(listRemainOrientID.get(b)+",");
								}
							}
							
							for(int b = 0; listRemainOrientIDForSelf != null && b<listRemainOrientIDForSelf.size();b++){
								if(listExistOrientTypeInAQA != null){
								if(!listExistOrientTypeInAQA.contains(listRemainOrientIDForSelf.get(b))){
									listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
									sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b)+",");
								}else{
									List<String> listExistUserInAQA = hmExistUsersAQA.get(employeeList.get(i)+"_"+innerList.get(0)+"_"+listRemainOrientIDForSelf.get(b));
									List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j)+"_"+listRemainOrientIDForSelf.get(b));
									boolean flag = false;
									for(int a = 0; listIds != null && a<listIds.size();a++){
										if(listExistUserInAQA != null){
										if(!listExistUserInAQA.contains(listIds.get(a))){
											flag = true;
										}
										}
									}
										if(flag == true){
											listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
											sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b)+",");
										} 
								} 
								}else{
									listRemainOrientTypeForSelf.add(listRemainOrientNameForSelf.get(b));
									sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b)+",");
								}
							}
							
							for(int b = 0; listRemainOrientIDForPeer != null && b<listRemainOrientIDForPeer.size();b++){
								if(listExistOrientTypeInAQA != null){
								if(!listExistOrientTypeInAQA.contains(listRemainOrientIDForPeer.get(b))){
									listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
									sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b)+",");
								} else if(!listRemainOrientIDForPeer.get(b).equals("3")){
									List<String> listExistUserInAQA = hmExistUsersAQA.get(employeeList.get(i)+"_"+innerList.get(0)+"_"+listRemainOrientIDForPeer.get(b));
									List<String> listIds = hmOrientTypewiseID.get(appraisalIdList.get(j)+"_"+listRemainOrientIDForPeer.get(b));
									boolean flag = false;
									for(int a = 0; listIds != null && a<listIds.size();a++){
										if(listExistUserInAQA != null){
										if(!listExistUserInAQA.contains(listIds.get(a))){
											flag = true;
										}
										}
									}
										if(flag == true){
											listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
											sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b)+",");
										}
								} 
								}else{
									listRemainOrientTypeForPeer.add(listRemainOrientNameForPeer.get(b));
									sbRemainOrientTypeIDForPeer.append(listRemainOrientIDForPeer.get(b)+",");
								}
							}
						%>
						<%
						if(empstatusMp.get(employeeList.get(i))!=null && sectionIDList != null && sectionIDList.contains(innerList.get(0))){ %>
							<!-- <img src="images1/icons/re_submit.png" title="Waiting for Approval"> -->
							<i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d" title="Waiting for Approval"></i>
							
						<%}else if(listRemainOrientType !=null && !listRemainOrientType.isEmpty() && !role.equalsIgnoreCase("Self") && !role.equalsIgnoreCase("Peer")){ %>
							<%-- <img src="images1/icons/pullout.png" title="Waiting for <%=listRemainOrientType%> Approval"> --%>
							<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
							
						<%}else if(listRemainOrientTypeForSelf !=null && !listRemainOrientTypeForSelf.isEmpty() && role.equalsIgnoreCase("Self")){ %>
							<%-- <img src="images1/icons/pullout.png" title="Waiting for <%=listRemainOrientTypeForSelf%> Approval"> --%>
							<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
							
						<%}else if(listRemainOrientTypeForPeer !=null && !listRemainOrientTypeForPeer.isEmpty() && role.equalsIgnoreCase("Peer")){ %>
							<%-- <img src="images1/icons/pullout.png" title="Waiting for <%=listRemainOrientTypeForPeer%> Approval"> --%>
							<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for Approval"></i>
							
						<%} else { %>
							<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting"></i>
							<!-- <img src="images1/icons/pending.png" title="Waiting"> -->
							
						<%} %>
						
						<% if(empstatusMp.get(employeeList.get(i))!=null && sectionIDList != null && sectionIDList.contains(innerList.get(0))){ %>
							
							<a href="javascript: void(0);" onclick="staffReviewSummaryPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','<%=innerList.get(0) %>','<%=role %>')">Section(<%=k+1 %>)</a>
							
						<%}else if(listRemainOrientType !=null && !listRemainOrientType.isEmpty() && !role.equalsIgnoreCase("Self") && !role.equalsIgnoreCase("Peer")){ %>
							<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0) %>','<%=sbRemainOrientTypeID.toString() %>');">Section(<%=k+1 %>)</a>
						<%}else if(listRemainOrientTypeForSelf !=null && !listRemainOrientTypeForSelf.isEmpty() && role.equalsIgnoreCase("Self")){ %>
							<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0) %>','<%=sbRemainOrientTypeIDForSelf.toString() %>');">Section(<%=k+1 %>)</a>
						<%}else if(listRemainOrientTypeForPeer !=null && !listRemainOrientTypeForPeer.isEmpty() && role.equalsIgnoreCase("Peer")){ %>
							<a href="javascript: void(0)" onclick="seeUserTypeList('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=innerList.get(0) %>','<%=sbRemainOrientTypeIDForPeer.toString() %>');">Section(<%=k+1 %>)</a>
						<%} else { %>
						
						<a href="javascript: void(0);" onclick="staffReviewPoup('<%=hmAppraisalMp.get("ID")%>','<%=employeeList.get(i)%>','<%=key %>','<%=innerList.get(0) %>','<%=role %>')">
								Section(<%=k+1 %>)
							</a>
						<%} %>
							<br/>
					<%} %>
					</div>
				</li>
				<%} } %> 

				<%} %>
				<%if(appraisalIdList == null || appraisalIdList.isEmpty() || appraisalIdList.size() == 0) { %>
					<li style="float:left;width:93%" class="tdDashLabel">No reviews assigned.</li>
				<% } %>
			</ul>
			<div class="viewmore"><a href="Reviews.action?callFrom=Dash">
   				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Reviews.."/> --%>
   				<i class="fa fa-forward" aria-hidden="true" title="Go to Reviews.."></i>
   				
   				
   			</a></div>
			</div>
				
			</div>
		<%} %>
		
		
		<% 
		if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0){
			List<List<String>> learningGapList = (List<List<String>>) request.getAttribute("learningGapList");
		%>
			<div id="details">
				<p class="past heading">Learning Gaps [<%=learningGapList != null ? learningGapList.size() : "0" %>]</p>
				<div class="content1" style="max-height: 300px; overflow-y: auto;">
					<ul class="issuereasons">
					
						<%int iC=0;
						for(iC=0; learningGapList!= null && !learningGapList.isEmpty() && iC<learningGapList.size(); iC++){ 
							List<String> innerList = learningGapList.get(iC);
					%>
						<li style="float:left;width:93%">
							<div style="float:left;width:60%;margin-right:5px;"><%=innerList.get(0)%> <p style="font-size:10px;color:#666;">(<%=innerList.get(1)%>)</p></div>
							<div style="float:left;width:15%;margin-right:5px;text-align: right;color:#FF0000;font-weight: bold;"><%=innerList.get(2)%>%</div>
						</li> <!-- #00FF00; -->
						<%} if(iC==0){ %>
						<li style="float:left;width:93%" class="tdDashLabel">No learning gaps identified.</li>
						<%} %>
					</ul>
					<div class="viewmore"><a href="Learnings.action?callFrom=LDash">
		   				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Learning Gaps.."/> --%>
		   				<i class="fa fa-forward" aria-hidden="true" title="Go to Learning Gaps.."></i>
		   				
		   				
		   				
		   			</a></div>
				</div>
			</div>
		<%} %>
			
	</div>








	<div id="right">
	
	
		<div id="details">
		<%String wLocationCount = (String) request.getAttribute("wLocationCount"); %>
		<p class="past heading">Geographical Spread [<%=wLocationCount %>]</p>
			<div class="content1" style="max-height: 370px; overflow-y: auto;">         
               <% 
               Map<String, String> hmWLocOrgName = (Map<String, String>) request.getAttribute("hmWLocOrgName");
				Set setWLocationEmployeeCount = hmWLocationEmployeeCount.keySet();
				Iterator itWLocationEmployeeCount = setWLocationEmployeeCount.iterator();
				while(itWLocationEmployeeCount.hasNext()){
					String strWLocationId = (String)itWLocationEmployeeCount.next();
					
					Map hmWLocation = (Map)hmWorkLocationMap.get(strWLocationId);
					if(hmWLocation==null)hmWLocation=new HashMap();
					if(hmWLocation != null && !hmWLocation.isEmpty()) {
			%>
               <div class="skill_div">
                    <span class="sk_value" style="float: right;"><%=(String)hmWLocationEmployeeCount.get(strWLocationId) %></span>             
                    <span class="sk_name" style="float: left; width: 79%;">
                    <span style="float: left;"><%=(String)hmWLocation.get("WL_CITY")+", "+(String)hmWLocation.get("WL_COUNTRY") %></span>
                    <br/>
                    <span style="float: left; font-style: italic; font-size: 11px; margin-top: -4px;"><%=(String)hmWLocOrgName.get(strWLocationId) %></span>
                    </span>                
               </div>
			<%   }
				}
			%> 
			</div>
		</div>
		
		<div id="details">
		<% String departCount = (String) request.getAttribute("departCount"); %>
		<p class="past heading">Departmental Spread [<%=departCount %>]</p>
			<div class="content1" style="max-height: 370px; overflow-y: auto;">
               <%
               Map<String, String> hmDepartOrgName = (Map<String, String>) request.getAttribute("hmDepartOrgName");
				Set setDepartmentEmployeeCount = hmDepartmentEmployeeCount.keySet();
				Iterator itDepartmentEmployeeCount = setDepartmentEmployeeCount.iterator();
				while(itDepartmentEmployeeCount.hasNext()){
					String strDepartmentId = (String)itDepartmentEmployeeCount.next();
					if(hmDepartmentMap.get(strDepartmentId) != null) {
				%>
               <div class="skill_div">
                    <span class="sk_value" style="float: right;"><%=(String)hmDepartmentEmployeeCount.get(strDepartmentId) %></span>             
                    <span class="sk_name" style="float: left; width: 79%;">
                    <span style="float: left;"><%=(String)hmDepartmentMap.get(strDepartmentId) %></span>
                    <br/>
                    <span style="float: left; font-style: italic; font-size: 11px; margin-top: -4px;"><%=(String)hmDepartOrgName.get(strDepartmentId) %></span>
                    </span>                
               </div>  
				<% }
					}   
				%>
			</div>
		</div>
		
		<div id="details">
		<p class="past heading">Skills Graph</p>
			<div class="content1">
			<%if(skillwiseEmpCountGraphList == null || skillwiseEmpCountGraphList.isEmpty()){ %>
			<div style="float:left;margin-left:15px" class="tdDashLabel">No skills added yet </div>
			<% } else { %>
				<div id="containerForSkillCharts" style="height: 250px; width:95%; margin:10px 0 0 0px;">12</div>
			<% } %>	
			</div>
		</div> 
		
		
		
		<%-- <div id="details">
			<div><p class="past">Awarded Employees</p></div>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
				<%	iA = 0;
					for(iA=0; alAchievements!=null && iA<alAchievements.size(); iA++){
				%>
				<div style="float: left; width:100%;border-bottom: 1px solid #eee;">
					<div style="float:left;margin-right:5px;margin-top:5px"><img src="images1/trophy.png"></div><div style="float:left; width:90%"><%=alAchievements.get(iA) %></div>
				</div>
				<%}if(iA==0){ %>
				<div style="float:left;margin-right:5px" class="tdDashLabel">No employee awarded yet.</div>
				<%}%>
			</div>
		</div> --%>
		
		<div id="details">
		<p class="past heading">Performing Employees</p>
			<div class="content1" style="max-height: 300px; overflow-y: auto;">
			
               <%
               	Set setTopEmployees = hmTopEmployees.keySet();
				Iterator itTopEmployees = setTopEmployees.iterator();
				int nCount = 0;
				while(itTopEmployees.hasNext()){
					nCount++;
					String strEmpId = (String)itTopEmployees.next();
					Map hmInner = (Map)hmTopEmployees.get(strEmpId);
			%>
               <div class="skill_div" style="width:92%">
                    <div style="float:left; margin:0px 5px; width:27%;">
                      <%-- <img class="lazy" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation + (String)hmEmpProfileImageMap.get(strEmpId) %>" alt="Profile pic" width="52px" height="50px"/> --%>
                      <%if(docRetriveLocation == null) { %>
							<img height="50px" width="52px" class="lazy img-circle" id="profilecontainerimg" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + ((String)hmEmpProfileImageMap.get(strEmpId)) %>" />
					  <%} else { %>
                            <img height="50px" width="52px" class="lazy img-circle" id="profilecontainerimg" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strEmpId+"/"+IConstants.I_60x60+"/"+((String)hmEmpProfileImageMap.get(strEmpId))%>" />
                      <%} %>                
                 </div>
                      <div style="float: left; width: 67%;">
                        <p class="sk_value"><%=uF.showData((String)hmInner.get("WORKED_HRS"), "0")%></p>             
                        <p class="sk_name"><%=uF.showData((String)hmEmployeeMap.get(strEmpId), "")%></p>  
                        <p class="sk_info"><%=uF.showData((String)hmEmpDesigMap.get(strEmpId), "")%></p>               
                      </div>                  
               </div>  
			<%
				} if(nCount == 0) {
			%> 
			<div style="float:left;margin-left:15px" class="tdDashLabel">No ratings updated yet. </div> 
			<% } %>
			</div>
		</div>
		
		<div id="details">
			<p class="past heading">Leave Summary</p>
			<div class="content1">
				<div id="containerForLeaveSumaryCharts" style="height: 250px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
			</div>
		</div> 
		
		<div id="details">
			<p class="past heading">Compensation Summary</p>
			<div class="content1">
				<div id="containerForCompensationSumaryCharts" style="height: 250px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
			</div>
		</div> 
		
		
	
	</div>
</div>



<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
}); 
</script>

<div id="reportJobProfilePopup"></div>
<div id="denyRequest"></div>
<div id="designationDetails"></div>
<div id="UpdateDesignation"></div>
<div id="UpdateJobPorfile"></div>
<div id="editProfile"></div>
<div id="ViewJobPorfile"></div>
<div id="comment"></div>
<div id="userTypeList"></div>
<div id="JobdenyPopup"></div>
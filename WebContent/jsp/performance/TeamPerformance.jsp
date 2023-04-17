
<%@page import="com.konnect.jpms.performance.FillAttribute"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillDepartment"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@page import="com.konnect.jpms.select.FillOrganisation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<style>
.printli {
	list-style: none;
	float: left;
	padding-left: 90px; 
	font-size: 0.8em;
}

.testa {
	text-decoration: none;
	font-size: 0.8em;
}

.ImageDiv {
	padding: 2px 2px 0px 2px;
	margin: 1px 1px;
	float: left;
}

#MainDiv {
	width: 90%;

}

.process {
	position: absolute;
	top: 280px;
	left: -57px;
}

.verticalLine {
	border-left: 2px solid #888888;
	border-bottom: 2px solid #888888;
}

.CL {
	clear: both
}

.fl {
	float: left
}

.fr {
	float: right
}

.OR {
	font: 14px;
	color: #FF9900
}

.BL {
	font: 14px;
	/* color: #33CCCC */
}

.PT {
	padding-top: 20px
}

.PT10 {
	padding-top: 10px
}

.PT40 {
	padding-top: 40px
}

.PT50 {
	padding-top: 50px
}

.PR {
	padding-right: 200px
}

.PR10 {
	padding-right: 10px
}

.PR20 {
	padding-right: 20px
}

.PR45 {
	padding-right: 45px
}

.PL {
	padding-left: 150px
}

.PLL {
	padding-left: 200px
}

.PL130 { /* padding-left: 130px */
	padding-left: 185px
}

.PL40 {
	padding-left: 40px
}

.PB40 {
	padding-bottom: 40px
}

.PL20 {
	padding-left: 20px
}

.PL10 {
	padding-left: 10px
}

.ML20 {
	margin-left: 20px
}

.ro-Div {
	width: 40px;
	margin-top: 60px;
	font-size: 0.8em;
}

#matrixBox {
	padding: 0px;
	float: right;
	position: relative;
left: 50px;
}

.squared {
	border: 2x solid black;
}

.sidebox1 {
	padding: 0px;
	margin: 0 auto;
}

.sidebox {
	padding: 2px 2px 2px 2px;
}

.Box {
	margin: 0px 5px 5px 5px;
	padding: 0px 5px 5px 5px;
}

.imageholder {
	padding: 2px 2px 2px 2px;
	border: 1px #999999 solid;
	width: 7%;
}

.imagebox {
	padding: 1px 1px 1px 1px;
	width: 90%;
	height: 95%
}

.rotate { /* Safari */
	-webkit-transform: rotate(-90deg);
	/* Firefox */
	-moz-transform: rotate(-90deg);
	/* IE */
	-ms-transform: rotate(-90deg);
	/* Opera */
	-o-transform: rotate(-90deg);
	/* Internet Explorer */
	filter: progid :     DXImageTransform.Microsoft.BasicImage (    
		rotation = 
		   3 );
}
/*  SECTIONS  */
.section {
	clear: both;
	padding: 0px;
	margin: 0px;

}

/*  COLUMN SETUP  */
.col {
	display: block;
	float: left;
	margin: 0 auto;
	border: 1px #999999 solid;
}

.col:first-child {
	margin-left: 0;
}

/*  GRID OF THREE  */
.span_3_of_3 {
	width: 100%;
}

.span_2_of_3 {
	width: 66.1%;
}

.span_1_of_3 {
	width: 32.2%;
}

/*  GRID OF FOUR  */
.span_4_of_4 {
	width: 100%;
}

.span_3_of_4 {
	width: 74.6%;
}

.span_2_of_4 {
	width: 66.1%;
}

.span_1_of_4 {
	width: 29.0%;
}

/*  GROUPING  */
.group:before,.group:after {
	content: "";
	display: table;
}

.group:after {
	clear: both;
}
</style>


<style>
#greenbox {
	height: 18px;
	background-color: #00FF00; /* the critical component */
}

#redbox {
	height: 18px;
	background-color: #FF0000; /* the critical component */
}

#yellowbox {
	height: 18px;
	background-color: #FFFF00; /* the critical component */
}

#outbox {
	height: 18px;
	width: 100%;
	background-color: #D8D8D8; /* the critical component */
}

.emps {
	text-align: center;
	font-size: 26px;
	color: #3F82BF; /* none repeat scroll 0 0 #3F82BF */
	font-family: digital;
	font-weight: bold;
}

.anaAttrib1 {
	font-size: 14px;
	font-family: digital;
	color: #3F82BF;
	font-weight: bold;
}

.ORDig {
	color: #FF9900;
	font-family: digital;
	font-size: 14px;
}
</style>

<script type="text/javascript" src="scripts/customAjax.js"></script>

<script type="text/javascript">
$(document).ready(function(){
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
});

 
	function getEmpProfile(val) {

		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Employee Profile');
		 $.ajax({
				//url : "AppraisalDetail.action?id="+id+"&empId="+empId, 
				url : "AppraisalEmpProfile.action?empId=" + val,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	function seeEmpList(empId, blockName) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('List- '+blockName);
		 $.ajax({
				url : "EmpListPopup.action?empID=" + empId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});

}
	
	
	
	 <%-- $(document).ready(function() {
		 
	        $('a.poplight[href^=#]').click(function() {
	            var popID = $(this).attr('rel'); //Get Popup Name
	            var popURL = $(this).attr('href'); //Get Popup href to define size

	            //Pull Query & Variables from href URL
	            var query= popURL.split('?');
	            var dim= query[1].split('&');
	            var popWidth = dim[0].split('=')[1]; //Gets the first query string value

	            //Fade in the Popup and add close button
	            $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>
	/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

											//Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
											var popMargTop = ($('#' + popID)
													.height() + 80) / 2;
											var popMargLeft = ($('#' + popID)
													.width() + 80) / 2;

											//Apply Margin to Popup
											$('#' + popID).css({
												'margin-top' : -popMargTop,
												'margin-left' : -popMargLeft
											});

											//Fade in Background
											$('body').append(
													'<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
											$('#fade').css({
												'filter' : 'alpha(opacity=80)'
											}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies

											return false;
										});

						//Close Popups and Fade Layer
						$('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
							$('#fade , .popup_block').fadeOut(function() {
								$('#fade, a.close').remove(); //fade them both out
							});
							return false;
						});

						//jQuery(".content1").hide();
						jQuery(".content1").show();
						//toggle the componenet with class msg_body
						jQuery(".heading").click(function() {
							jQuery(this).next(".content1").slideToggle(500);
							$(this).toggleClass("close_div");
						});

						/* showPerformanceReport();
						showAnalysisSummary(); */
						window.setTimeout(function() {
							showAnalysisSummary();
						}, 500);

						window.setTimeout(function() {
							showPerformanceReport();
						}, 900);

					}); --%>
	 
					
	$(document).ready(function(){
		showAnalysisSummary();
		showPerformanceReport();
	});
	
	
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

	function getLocationByOrg() {
		var checkedOrg = new Array();
		$("input:checkbox[name=checkOrg]:checked").each(function() {
			checkedOrg.push($(this).val());
		});
		//alert("selected checkOrg ::::::"+ checkedOrg);
		var action = 'getLocatoinList.action?strOrg=' + checkedOrg;
		getContent('idlocation', action);

		getDepartmentByOrg();
	}

	function getDepartmentByOrg() {
		var checkedOrg = new Array();
		$("input:checkbox[name=checkOrg]:checked").each(function() {
			checkedOrg.push($(this).val());
		});
		//alert("selected checkOrg ::::::"+ checkedOrg);
		var action = 'getDepartmentList.action?strOrg=' + checkedOrg
				+ '&type=org';
		getContent('iddepart', action);

		getLevelByOrg();
	}

	function getLevelByOrg() {
		var checkedOrg = new Array();
		$("input:checkbox[name=checkOrg]:checked").each(function() {
			checkedOrg.push($(this).val());
		});
		//alert("selected checkOrg ::::::"+ checkedOrg);
		var action = 'getLevelList.action?strOrg=' + checkedOrg + '&type=org';
		getContent('idlevels', action);

		getParameterByOrg();

	}

	function getParameterByOrg() {
		var checkedOrg = new Array();
		$("input:checkbox[name=checkOrg]:checked").each(function() {
			checkedOrg.push($(this).val());
		});
		//alert("getParameterByOrg checkOrg ::::::"+ checkedOrg);
		var action = 'getParameterList.action?strOrg=' + checkedOrg
				+ '&type=org';
		getContent('idparameters', action);

		/* window.setTimeout(function() {
			showPerformanceReport();
			showAnalysisSummary();
		}, 200);  */
	}

	function getParameterByWorkLocation() {

		var checkedWork = new Array();
		$("input:checkbox[name=checkLocation]:checked").each(function() {
			checkedWork.push($(this).val());
		});
		//alert("selected checkLocation ::::::"+ checkedWork);
		var action = 'getParameterList.action?strWork=' + checkedWork
				+ '&type=wlocation';
		getContent('idparameters', action);

		/* window.setTimeout(function() {
			showPerformanceReport();
			showAnalysisSummary();
		}, 200); */

	}

	function getParameterByDepartment() {
		var checkedDepart = new Array();
		$("input:checkbox[name=checkDepart]:checked").each(function() {
			checkedDepart.push($(this).val());
		});
		//alert("selected checkedDepart ::::::"+ checkedDepart);
		var action = 'getParameterList.action?strDepart=' + checkedDepart
				+ '&type=department';
		getContent('idparameters', action);

		/* window.setTimeout(function() {
			showPerformanceReport();
			showAnalysisSummary();
		}, 200);  */
	}

	function getParameterByLevel() {
		var checkedLevel = new Array();
		$("input:checkbox[name=checkLevel]:checked").each(function() {
			checkedLevel.push($(this).val());
		});
		//alert("selected checkLocation ::::::"+ selected);
		var action = 'getParameterList.action?strLevel=' + checkedLevel
				+ '&type=level';
		getContent('idparameters', action);

		/* window.setTimeout(function() {
			showPerformanceReport();
			showAnalysisSummary();
			
		}, 200);  */
	}

	function showPerformanceReport() {
		var wLocParam = new Array();
		var deptParam = new Array();
		var levelParam = new Array();
		var checkedParam = new Array();
		var checkedReview = new Array();
		var checkedGoal = new Array();
		var checkedKRA = new Array();
		var checkedTarget = new Array();
		//alert("checkedParam ===>> " +checkedParam);
		
		var datedParam = new Array();
		if(document.getElementById("checkLocation")) {
			$("input:checkbox[name=checkLocation]:checked").each(function() {
				wLocParam.push($(this).val());
			});
		}
		$("input:checkbox[name=checkDepart]:checked").each(function() {
			deptParam.push($(this).val());
		});
		$("input:checkbox[name=checkLevel]:checked").each(function() {
			levelParam.push($(this).val());
		});
		$("input:checkbox[name=checkParam]:checked").each(function() {
			checkedParam.push($(this).val());
		});
		//alert("checkedParam 00 ===>> " +checkedParam);
		$("input:radio[name=dateParam]:checked").each(function() {
			datedParam.push($(this).val());
		});
		$("input:checkbox[name=checkReviews]:checked").each(function() {
				checkedReview.push($(this).val());
		});
		$("input:checkbox[name=checkGoals]:checked").each(function() {
				checkedGoal.push($(this).val());
		});
		$("input:checkbox[name=checkKRAs]:checked").each(function() {
				checkedKRA.push($(this).val());
		});
		$("input:checkbox[name=checkTargets]:checked").each(function() {
				checkedTarget.push($(this).val());
		});
		//alert("------ checkedReview ===> " + checkedReview+"==> checkedGoal ===> " + checkedGoal+"==> checkedKRA ===> " + checkedKRA+"==> checkedTarget ===> " + checkedTarget);
		//var dateParam = document.getElementById("dateParam").value;
		var period = document.getElementById("period").value;
		var strStartDate = document.getElementById("strStartDate").value;
		var strEndDate = document.getElementById("strEndDate").value;
		var dataType = document.getElementById("dataType").value;
		// alert(" period ===> " +period + " strStartDate ===> " +strStartDate+ " strEndDate ===> " +strEndDate);
		$("#matrixBox").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		var action = 'TeamPerformanceReport.action?checkParam=' + checkedParam + '&dateParam=' + datedParam + '&wLocParam=' 
			+ wLocParam + '&deptParam=' + deptParam + '&levelParam=' + levelParam + '&checkedReview='+checkedReview
			+'&checkedGoal='+checkedGoal+'&checkedKRA='+checkedKRA +'&checkedTarget='+checkedTarget+'&period=' + period + '&strStartDate='
			+ strStartDate + '&strEndDate=' + strEndDate + '&dataType=' + dataType;
		//alert("action ===> " + action);
		getContent('matrixBox', action);
	}

	function showAnalysisSummary() {
		var wLocParam = new Array();
		var deptParam = new Array();
		var levelParam = new Array();
		var checkedParam = new Array();
		
		var checkedReview = new Array();
		var checkedGoal = new Array();
		var checkedKRA = new Array();
		var checkedTarget = new Array();
		
		var datedParam = new Array();
		if(document.getElementById("checkLocation")) {
			$("input:checkbox[name=checkLocation]:checked").each(function() {
				wLocParam.push($(this).val());
			});
		}
		$("input:checkbox[name=checkDepart]:checked").each(function() {
			deptParam.push($(this).val());
		});
		$("input:checkbox[name=checkLevel]:checked").each(function() {
			levelParam.push($(this).val());
		});
		$("input:checkbox[name=checkParam]:checked").each(function() {
			checkedParam.push($(this).val());
		});
		$("input:radio[name=dateParam]:checked").each(function() {
			datedParam.push($(this).val());
		});
		$("input:checkbox[name=checkReviews]:checked").each(function() {
			checkedReview.push($(this).val());
		});
		$("input:checkbox[name=checkGoals]:checked").each(function() {
				checkedGoal.push($(this).val());
		});
		$("input:checkbox[name=checkKRAs]:checked").each(function() {
				checkedKRA.push($(this).val());
		});
		$("input:checkbox[name=checkTargets]:checked").each(function() {
				checkedTarget.push($(this).val());
		});
		/* $("input:checkbox[name=checkMeasures]:checked").each(function() {
			var dt = $(this).val();
			if(dt == 'REVIEW') {
				checkedReview = 'REVIEW';
			} else if(dt == 'GOAL') {
				checkedGoal = 'GOAL';
			} else if(dt == 'KRA') {
				checkedKRA = 'KRA';
			} else if(dt == 'TARGET') {
				checkedTarget = 'TARGET';
			}
		}); */
		
		//alert("checkedReview ===> " + checkedReview+"==> checkedGoal ===> " + checkedGoal+"==> checkedKRA ===> " + checkedKRA+"==> checkedTarget ===> " + checkedTarget);
		var period = document.getElementById("period").value;
		var strStartDate = document.getElementById("strStartDate").value;
		var strEndDate = document.getElementById("strEndDate").value;
		var dataType = document.getElementById("dataType").value;
		//alert(" period ===> " +period + " strStartDate ===> " +strStartDate+ " strEndDate ===> " +strEndDate);
		$("#perAnlysDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		var action = 'TeamAnalysisSummaryReport.action?checkParam=' + checkedParam + '&dateParam=' + datedParam + '&wLocParam=' 
				+ wLocParam + '&deptParam=' + deptParam + '&levelParam=' + levelParam + '&checkedReview='+checkedReview
				+'&checkedGoal='+checkedGoal+'&checkedKRA='+checkedKRA+'&checkedTarget='+checkedTarget+'&period=' + period + '&strStartDate='
				+ strStartDate + '&strEndDate=' + strEndDate + '&dataType=' + dataType;
		//alert("action ===> " + action);
		getContent('perAnlysDiv', action);
	}

	/* function showPerformanceReport() {
	 
	 alert("showPerformanceReport 1");
		dojo.event.topic.publish("showPerformanceReport");
	 alert("showPerformanceReport 2");	
	} */

	/*  function showAnalysisSummary() {
		 alert("showAnalysisSummary 1");
			dojo.event.topic.publish("showAnalysisSummary");
		alert("showAnalysisSummary 2");
		} */

	function callTwoFunctionsWLOc() {
		getParameterByWorkLocation();
		//alert("Click Here");

		window.setTimeout(function() {
			showAnalysisSummary();
		}, 500);

		window.setTimeout(function() {
			showPerformanceReport();
		}, 900);

		/* showAnalysisSummary();
		showPerformanceReport(); */
	}

	function callTwoFunctionsDept() {
		getParameterByDepartment();
		//alert("Click Here");
		/* showPerformanceReport();
		showAnalysisSummary(); */
		window.setTimeout(function() {
			showAnalysisSummary();
		}, 500);

		window.setTimeout(function() {
			showPerformanceReport();
		}, 900);
	}

	function callTwoFunctionsLevel() {
		getParameterByLevel();
		//alert("Click Here");
		/* showPerformanceReport();
		showAnalysisSummary(); */
		window.setTimeout(function() {
			showAnalysisSummary();
		}, 500);

		window.setTimeout(function() {
			showPerformanceReport();
		}, 900);
	}

	function callTwoFunctionsMeasures() {	
		//getParameterByLevel();
		window.setTimeout(function() {
			showAnalysisSummary();
		}, 500);
		
		window.setTimeout(function() {
			showPerformanceReport();
		}, 900);
	}
	
	
	
	function getTeamPerformance(dataType) {
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url:'TeamPerformance.action?dataType='+dataType,
			data: $("#"+this.id).serialize(),
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	}
	
	function submitForm() {
		
		var datedParam = new Array();
		$("input:radio[name=dateParam]:checked").each(function() {
			datedParam.push($(this).val());
		});
		
		var strOrg = "";
		if(document.getElementById("strOrg")) {
			strOrg = document.getElementById("strOrg").value;
		}
		// alert("checkedParam ===> " + checkedParam+" datedParam ===> " + datedParam);
		
		var period = "";
		if(document.getElementById("period")) {
			period = document.getElementById("period").value;
		}
		
		var strStartDate = "";
		if(document.getElementById("strStartDate")) {
			strStartDate = document.getElementById("strStartDate").value;
		}
		
		var strEndDate = "";
		if(document.getElementById("strEndDate")) {
			strEndDate = document.getElementById("strEndDate").value;
		}
		
		var dataType = document.getElementById("dataType").value;
		//alert(" period ===> " +period + " strStartDate ===> " +strStartDate+ " strEndDate ===> " +strEndDate);
		
		var action = 'TeamPerformance.action?strOrg='+strOrg+'&dateParam=' + datedParam +'&period=' + period + '&strStartDate='
				+ strStartDate + '&strEndDate=' + strEndDate + '&dataType=' + dataType;
		//alert("action ===> " + action);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url:action,
			data: $("#"+this.id).serialize(),
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	}
</script>

<%
	//double dbltotAverage = 0;
	UtilityFunctions uF = new UtilityFunctions();
	List<FillOrganisation> orgList = (List<FillOrganisation>) request.getAttribute("orgList");
	List<FillWLocation> workList = (List<FillWLocation>) request.getAttribute("workList");
	List<FillDepartment> departmentList = (List<FillDepartment>) request.getAttribute("departmentList");
	List<FillLevel> levelList = (List<FillLevel>) request.getAttribute("levelList");
	List<FillAttribute> attributeList = (List<FillAttribute>) request.getAttribute("attributeList");
	Map<String, List<List<String>>> hmElementAttribute = (Map<String, List<List<String>>>) request.getAttribute("hmElementAttribute");
	Map<String, String> hmAnalysisSummaryMap = (Map<String, String>) request.getAttribute("hmAnalysisSummaryMap");
	List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
%>

	<%
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		String dataType = (String) request.getAttribute("dataType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>
	
<%-- <section class="content">	
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable"> --%>
        	<% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
				<div class="box box-none nav-tabs-custom">
					<ul class="nav nav-tabs">
						<li class="<%=(dataType == null || dataType.equals("MYTEAM")) ? "active" : "" %>"><a href="javascript:void(0)" onclick="getTeamPerformance('MYTEAM')" data-toggle="tab">My Team</a></li>
						<li class="<%=(dataType == null || dataType.equals(strBaseUserType)) ? "active" : "" %>"><a href="javascript:void(0)" onclick="getTeamPerformance('<%=strBaseUserType %>')" data-toggle="tab"><%=strBaseUserType %></a></li>
					</ul>
			<% }else{ %>
				<div class="box box-none">
			<% } %>
                <div class="box-body" style="padding: 5px; overflow-y: auto;">
						<s:form theme="simple" action="TeamPerformance" method="POST" id="frmPerformance" name="frmPerformance">
							<s:hidden name="dataType" id="dataType" />
							<div class="box box-default collapsed-box">
								<div class="box-header with-border">
								    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
								    <div class="box-tools pull-right">
								        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
								        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
								    </div>
								</div>
								<div class="box-body" style="padding: 5px; overflow-y: auto;">
									<div class="content1">
										<div class="row row_without_margin">
											<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
												<i class="fa fa-filter"></i>
											</div>
											<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
												<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER)) || (dataType != null && dataType.equals(strBaseUserType))) { %>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Organization</p>
													<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" headerKey=""/>
												</div>
												<% } %>
												<div class="col-lg-8 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px; margin-bottom: 5px;">Period</p>
													<s:radio name="dateParam" id="dateParam" list="#{'1':''}"/>
													<s:select theme="simple" label="Select Pay Cycle" name="period" id="period" listKey="periodId"
														listValue="periodName" headerKey="0" list="periodList" key="" required="true"/>
													<s:radio name="dateParam" id="dateParam" list="#{'2':''}"/>
													<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;"/>
													<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;"/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">&nbsp;</p>
													<input type="button" class="btn btn-primary" value="Search" name="search" onclick="submitForm()" />
												</div>
											</div>
										</div><br>
									</div>
								</div>
							</div>
						
					
							<div id="MainDiv" class="row" style="margin-left: 0px;margin-right: 0px;margin-top: 30px;"> 
								<%-- <s:form theme="simple" action="TeamPerformance" method="POST" id="frmPerformance" name="frmPerformance"> --%>
								<div class="fl BL sidebox col-lg-3 col-md-3 col-sm-12" style="overflow-y: hidden;max-height: 500px;" id="sectionTeamPerformace"><!--@uthor Dattatray Note set id and change style:overflow-y value   -->
									<div id="perAnlysDiv" style="margin-top: 10px; margin-left: 4px; border: 2px solid #F3F0F0;padding-right: 10px;padding-left: 10px;padding-bottom: 0px;">
									</div>
									<div style="margin-top: 10px; margin-left: 4px; border: 2px solid #F3F0F0;padding-top: 10px;padding-bottom: 10px;padding-left: 5px;padding-right: 5px;">
					
										<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER)) || (dataType != null && dataType.equals(strBaseUserType))) { %>
										<div class="box box-primary" style="border-top-color:#cda55f;/* #EEEEEE;*/">
                
							                <div class="box-header with-border">
							                    <h3 class="box-title">Work Location</h3>
							                    <div class="box-tools pull-right">
							                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
							                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							                    </div>
							                </div>
							                <!-- /.box-header -->
							                <div class="box-body" style="padding: 5px; overflow-y: auto;">
							                    <div class="content1" id="idlocation">
													<table>
														<% for (int i = 0; workList != null && !workList.isEmpty() && i < workList.size(); i++) { %>
														<tr>
															<td class="textblue"><input type="checkbox" value="<%=(String) ((FillWLocation) workList.get(i)).getwLocationId()%>" name="checkLocation" id="checkLocation" checked="checked" onclick="callTwoFunctionsWLOc();" /></td>
															<td><%=(String) ((FillWLocation) workList.get(i)).getwLocationName()%></td>
														</tr>
														<% } %>
													</table>
												</div>
							                </div>
							                <!-- /.box-body -->
							            </div>
										<% } %>
										<div class="box box-primary" style="border-top-color:	#cda55f;/* #EEEEEE;*/">
                
							                <div class="box-header with-border">
							                    <h3 class="box-title">Department</h3>
							                    <div class="box-tools pull-right">
							                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
							                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							                    </div>
							                </div>
							                <!-- /.box-header -->
							                <div class="box-body" style="padding: 5px; overflow-y: auto;">
							                    <div class="content1" id="iddepart">
													<table>
														<% for (int i = 0; departmentList != null && !departmentList.isEmpty() && i < departmentList.size(); i++) { %>
														<tr>
															<td class="textblue"><input type="checkbox" value="<%=(String) ((FillDepartment) departmentList.get(i)).getDeptId()%>" name="checkDepart" checked="checked" onclick="callTwoFunctionsDept();" /></td>
															<td><%=(String) ((FillDepartment) departmentList.get(i)).getDeptName()%></td>
														</tr>
														<% } %>
													</table>
												</div>
							                </div>
							                <!-- /.box-body -->
							            </div>
										<div class="box box-primary" style="border-top-color: #cda55f;/* #EEEEEE;*/">
                
							                <div class="box-header with-border">
							                    <h3 class="box-title">Levels</h3>
							                    <div class="box-tools pull-right">
							                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
							                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							                    </div>
							                </div>
							                <!-- /.box-header -->
							                <div class="box-body" style="padding: 5px; overflow-y: auto;">
							                    <div class="content1" id="idlevels">
													<table>
														<% for (int i = 0; levelList != null && !levelList.isEmpty() && i < levelList.size(); i++) { %>
														<tr>
															<td class="textblue"><input type="checkbox" value="<%=(String) ((FillLevel) levelList.get(i)).getLevelId()%>" name="checkLevel" checked="checked" onclick="callTwoFunctionsLevel();" />
															</td>
															<td><%=(String) ((FillLevel) levelList.get(i)).getLevelCodeName()%></td>
														</tr>
														<% } %>
													</table>
												</div>
							                </div>
							                <!-- /.box-body -->
							            </div>
							            <div class="box box-primary" style="border-top-color:#cda55f;/* #EEEEEE;*/">
                
							                <div class="box-header with-border">
							                    <h3 class="box-title">Measure</h3>
							                    <div class="box-tools pull-right">
							                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
							                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							                    </div>
							                </div>
							                <!-- /.box-header -->
							                <div class="box-body" style="padding: 5px; overflow-y: auto;">
							                    <div class="content1" id="idMeasures">
													<table>
									                  	<tr>
									                  	<td class="textblue"><input type="checkbox" value="REVIEW" name="checkReviews" checked="checked" onclick="callTwoFunctionsMeasures();"/></td>
									                  	<td>Review</td> 
									                  	</tr>
									                  	<tr>
									                  	<td class="textblue"><input type="checkbox" value="GOAL" name="checkGoals" checked="checked" onclick="callTwoFunctionsMeasures();"/></td>
									                  	<td>Goal</td> 
									                  	</tr>
									                  	<tr>
									                  	<td class="textblue"><input type="checkbox" value="KRA" name="checkKRAs" checked="checked" onclick="callTwoFunctionsMeasures();"/></td>
									                  	<td>KRA</td> 
									                  	</tr>
									                  	<tr>
									                  	<td class="textblue"><input type="checkbox" value="TARGET" name="checkTargets" checked="checked" onclick="callTwoFunctionsMeasures();"/></td>
									                  	<td>Target</td> 
									                  	</tr>
													</table>
												</div>
							                </div>
							                <!-- /.box-body -->
							            </div>
										<div class="box box-primary" style="border-top-color: #cda55f;/* #EEEEEE;*/">
                
							                <div class="box-header with-border">
							                    <h3 class="box-title">Parameters</h3>
							                    <div class="box-tools pull-right">
							                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
							                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							                    </div>
							                </div>
							                <!-- /.box-header -->
							                <div class="box-body" style="padding: 5px; overflow-y: auto;">
							                    <div class="content1" id="idparameters">
							
													<ul style="padding-left: 0px;">
														<%
															//List<List<String>>  attributeouterList=(List<List<String>>  )request.getAttribute("attributeouterList");
															for (int i = 0; elementouterList != null && !elementouterList.isEmpty() && i < elementouterList.size(); i++) {
																List<String> innerList = elementouterList.get(i);
														%>
														<li><strong><%=innerList.get(1)%></strong>
															<ul>
																<%
																	int count = 0;
																		List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
																		for (int j = 0; attributeouterList1 != null && !attributeouterList1.isEmpty() && j < attributeouterList1.size(); j++) {
																			List<String> attributeList1 = attributeouterList1.get(j);
																%>
																<li><input type="checkbox" value="<%=attributeList1.get(0)%>" name="checkParam" checked="checked" onclick="callTwoFunctionsMeasures();" /> <%=attributeList1.get(1) %></li>
							
																<% } %>
															</ul></li>
														<% } %>
													</ul>
												</div>
							                </div>
							                <!-- /.box-body -->
							            </div>

									</div>
					
								</div>
								
					
					
								<div id="matrixBox" class="col-lg-9 col-md-9 col-sm-12">
									
								</div>
							</div>
						</s:form>
                </div>
            </div>
       <%--  </section>
    </div>
</section> --%>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
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
/* @uthor : Dattatray */
$(window).bind('mousewheel DOMMouseScroll', function(event){
    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
        // scroll up
        if($(window).scrollTop() == 0 && $("#sectionTeamPerformace").scrollTop() != 0) {
        	$("#sectionTeamPerformace").scrollTop($("#sectionTeamPerformace").scrollTop() - 30);
        }
    } else {
        // scroll down
        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#sectionTeamPerformace").scrollTop($("#sectionTeamPerformace").scrollTop() + 30);
   		}
    }
});

/* @uthor : Dattatray */
$(window).keydown(function(event){
	if(event.which == 40 || event.which == 34){
		if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
			$("#sectionTeamPerformace").scrollTop($("#sectionTeamPerformace").scrollTop() + 50);
   		}
	} else if(event.which == 38 || event.which == 33){
		if($(window).scrollTop() == 0 && $("#sectionTeamPerformace").scrollTop() != 0) {
	    	$("#sectionTeamPerformace").scrollTop($("#sectionTeamPerformace").scrollTop() - 50);
	    }
	}
}); 
</script>


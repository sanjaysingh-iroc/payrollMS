<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<style>
    .balls{
    display:inline; 
    padding-left: 5px;
    padding-right: 5px;
    }
    .balls img{
    padding-right: 2px !important;
    padding-top: 0px !important;
    }
    .table-bordered>thead>tr>th, .table-bordered>tbody>tr>th, .table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td, .table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td {
    border: 1px solid #DBDBDB !important;
    }
    .table-bordered {
    border: 2px solid #DBDBDB !important;
    }
    
    .closePop {
	    float: right;
	    font-size: 21px;
	    font-weight: 700;
	    line-height: 1;
	    color: #000;
	    text-shadow: 0 1px 0 #fff;
	    filter: alpha(opacity=20);
	    opacity: .2;
	}

</style>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
<script type="text/javascript">
    $(document).ready(function(){
    	$("body").on('click','#closeButtonPopup',function(){
    		$("#modal-dialogPopup").removeAttr('style');
    		$("#modal-bodyPopup").height(500);
    		$("#modalInfoPopup").hide();
    	});
    	$("body").on('click','#closePop',function(){
    		$("#modal-dialogPopup").removeAttr('style');
    		$("#modal-bodyPopup").height(500);
    		$("#modalInfoPopup").hide();
    	});
    });
    
    function getData(empId, id,appFreqId) {
    	var dialogEdit = '#modal-bodyPopup';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfoPopup").show();
    	$('#modal-titlePopup').html('See Score');
    	$.ajax({
    		url : "AppraisalScoreStatus.action?id=" + id+ "&empid=" + empId + "&type=popup&appFreqId="+appFreqId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    
    }
        
    function getBalancedScoreData(id, appFreqId,empId,fromPage,empName) {
        var title = 'Balanced Score for '+ empName;
    	var dialogEdit = '#modal-bodyPopup';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfoPopup").show();
    	$('#modal-titlePopup').html(title);
    	if($(window).width() >= 900) {
    		$('#modal-dialogPopup').width(1100);
    	}
    	$.ajax({
    		url : "AppraisalSummary.action?id="+id+"&appFreqId="+appFreqId+"&empId="+empId+"&fromPage="+fromPage,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }  
    
    function getMemberData(memberId, empId, id, empName, role, appFreqId) {
    
    	var dialogEdit = '#modal-bodyPopup';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfoPopup").show();
    	$('#modal-titlePopup').html('See Score');
    	if($(window).width() >= 900) {
    		$('#modal-dialogPopup').width(900);
    	}
    	$.ajax({
    		url : "AppraisalScoreStatus.action?id="+id+"&empid="+empId+"&type=popup&memberId="+memberId+"&appFreqId="+appFreqId+"&role="+role
    				+"&fromPage=ReviewerStatus",
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    } 
    
    function getAppraisalDetail(empId, id,appFreqId) {
    	var dialogEdit = '#modal-bodyPopup';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfoPopup").show();
    	$('#modal-titlePopup').html('See Score');
    	$.ajax({
    		url : "FullAppraisalDetails.action?id=" + id+ "&empid=" + empId+"&appFreqId="+appFreqId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    
    }
    
    function getCustomerFactSheet(empId) {
    	var dialogEdit = '#modal-bodyPopup';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfoPopup").show();
    	$('#modal-titlePopup').html('See Score');
    	$.ajax({
    		url : "MyProfile.action?empId=" + empId + "&popup=popup",
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    
    }
    
    function giveRemark(id, empId, thumbsFlag,appraisal_freq,remarktype,appFreqId,fromPage) {
    	/* if(remarktype == 1){
    		window.location = "AppraisalRemark.action?id=" + id + "&empid=" + empId + "&thumbsFlag=" + thumbsFlag + "&appraisal_freq="+ appraisal_freq +"&remarktype=" + remarktype+"&appFreqId="+appFreqId;
    	} else {*/
    		var dialogEdit = '#modal-bodyPopup';
    		$(dialogEdit).empty();
    		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$("#modalInfoPopup").show();
    		$('#modal-titlePopup').html('Review Summary');
    		if($(window).width() >= 900) {
        		$('#modal-dialogPopup').width(900);
        	}
    		$.ajax({
    			url : "AppraisalRemark.action?id=" + id + "&empid=" + empId + "&thumbsFlag=" + thumbsFlag + "&appraisal_freq="+ appraisal_freq +"&remarktype=" + remarktype
    					+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    	//} 
    }
    
    
    function seeEmpList(empId,aid,appFreqId) {
    	var dialogEdit = '#modal-bodyPopup';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfoPopup").show();
    	$('#modal-titlePopup').html('Employee List');
    	$.ajax({
    		url : "AppraisalApproveMembers.action?empID="+empId+"&id="+aid+"&appFreqId="+appFreqId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    
    //var dialogEdit = '#ShowQueDiv';
    function showAllQuestion(appid, empId, usertypeId, readstatus, appFreqId, fromPage) {
    	var dialogEdit = '#modal-bodyPopup';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfoPopup").show();
    	$('#modal-titlePopup').html('Reviews');
    	$.ajax({
    		url : "ShowAllSingleOpenWithoutMarkQue.action?appid="+appid+"&empId="+empId+"&usertypeId="+usertypeId+"&readstatus="+readstatus
    				+"&appFreqId="+appFreqId+"&fromPage="+fromPage,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    function closePopup(){
    	$("#modalInfoPopup").hide();
    }	
    
    
    function openEmployeeProfilePopup(empId) {
    	var dialogEdit = '#modal-bodyPopup';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfoPopup").show();
    	$('#modal-titlePopup').html('Employee Information');
    	if($(window).width() >= 900) {
    		$('#modal-dialogPopup').css('width', 900);
    	}
    	$.ajax({
    		url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
     }
    
    
    function getEmpProfile(val, empName){    
    var dialogEdit = '#modal-bodyPopup';
    $(dialogEdit).empty();
    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $("#modalInfoPopup").show();
    $('#modal-titlePopup').html(''+empName+'');
    $.ajax({
    	//url : "AppraisalDetail.action?id="+id+"&empId="+empId, 
    	url : "AppraisalEmpProfile.action?empId="+val ,
    	cache : false,
    	success : function(data) {
    		$(dialogEdit).html(data);
    	}
    });
    }
    
    
    function approveAverageFeedbackOfReviewer(id, empID, userType, role, appFreqId) {
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			if(confirm('Are you sure, you want to approve this review?')) {
				var xhr = $.ajax({
					url : "ReviewerViewStatus.action?id="+id+"&empID="+empID+"&userType="+userType+"&appFreqId="+appFreqId
							+"&operation=ApproveFeedbak",
					cache : false,
					success : function(data) {
						document.getElementById(id+'_'+empID+'_'+userType).innerHTML = data;
					}
				});
			}
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
	
    function generateBalanceScorecardExcel(){
    	window.location = "ExportExcelReportReview.action";
    }
</script>


<script type="text/javascript">
    function selectall(x,strEmpId){
    	var  status=x.checked; 
    	var  arr= document.getElementsByName(strEmpId);
    	for(i=0;i<arr.length;i++){ 
      		arr[i].checked=status;
     	}
    }
</script>

	<%
		List<String> empList = (List<String>) request.getAttribute("empList");
		String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
		
		String oriented_type = (String) request.getAttribute("oriented_type");
		UtilityFunctions uF = new UtilityFunctions();
		
		Map<String, String> appraisalMp = (Map<String, String>) request.getAttribute("appraisalMp");
		
		Map<String, String> hmEmpCode = (Map<String, String>) request.getAttribute("hmEmpCode");
		Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig"); 
		Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
		
		List<String> memberList = (List<String>) request.getAttribute("memberList");
		Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");
		Map<String, String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
		Map<String, String> locationMp = (Map<String, String>) request.getAttribute("locationMp");
		
		String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
		Map<String,String> hmEmpCount = (Map<String,String>)request.getAttribute("hmEmpCount");
		int memberCount = (Integer)request.getAttribute("memberCount");
		
		Map<String,String> hmRemark = (Map<String,String>)request.getAttribute("hmRemark");
		Map<String,String> hmEmpSuperVisor = (Map<String,String>)request.getAttribute("hmEmpSuperVisor");
		
		String apid = (String)request.getAttribute("id");
		String appFreqId = (String)request.getAttribute("appFreqId");
		String empID = (String)request.getAttribute("empID");
		String dataType = (String)request.getAttribute("dataType");
		boolean flag = (Boolean) request.getAttribute("flag");
		
		String strBaseUserTypeId = (String) session.getAttribute(IConstants.BASEUSERTYPEID);
		
		Map<String,String> hmMemberMP=(Map<String,String>)request.getAttribute("hmMemberMP");
		Map<String, String> hmReadUnreadCount = (Map<String, String>)request.getAttribute("hmReadUnreadCount");
		
		String strMessage = (String)request.getAttribute("strMessage");
		if(strMessage == null) {
			strMessage = "";
		}
		strMessage = URLDecoder.decode(strMessage);
		
		//EncryptionUtils EU = new EncryptionUtils();// Created By Dattatray Date:21-July-2021
	%>
	
	<% if(appraisalMp != null && !appraisalMp.isEmpty()  && appraisalMp.size()>0) { %>
                <div class="box-header with-border">
                    <h4 class="box-title" style="width: 100%;">
                        <%=strMessage %>
                        <div style="float: left; width: 100%; font-size: 18px; font-weight: bold; margin-bottom: 9px;">
                            <div style="float: left;"><%=appraisalMp.get("APPRAISAL") %></div> 
                            <div style="float: right;">
                            	<a onclick="generateBalanceScorecardExcel();" href="javascript:void(0)"><i class="fa fa-file-excel-o"></i></a>
                            </div>
                        </div>
                    </h4>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 500px;">
                    <div class="leftbox reportWidth">
                        <table class="table table-striped" cellpadding="0" cellspacing="0" width="100%">
                            <tr>
                                <th width="15%" align="right">Review Type:</th>
                                <td><%=appraisalMp.get("APPRAISALTYPE") %></td>
                            </tr>
                            <tr>
                                <th valign="top" align="right">Description:</th>
                                <td><%=appraisalMp.get("DESCRIPTION") %></td>
                            </tr>
                            <tr>
                                <th valign="top" align="right">Instructions:</th>
                                <td><%=appraisalMp.get("INSTRUCTION") %></td>
                            </tr>
                            <tr>
                                <th align="right">Frequency:</th>
                                <td><%=appraisalMp.get("FREQUENCY") %></td>
                            </tr>
                            <tr>
                                <th align="right">Effective Date:</th>
                                <td><%=appraisalMp.get("APP_FREQ_FROM") %></td>
                            </tr>
                            <tr>
                                <th align="right">Due Date:</th>
                                <td><%=appraisalMp.get("APP_FREQ_TO") %></td>
                            </tr>
							<tr>
								<th align="right">Orientation:</th>
								<td><%=appraisalMp.get("ORIENT") %></td>
							</tr>
							<tr>
								<th align="right">Appraiser:</th>
								<td><%=appraisalMp.get("APPRAISER")%></td>
							</tr>
							<tr>
								<th align="right">Reviewer:</th>
								<td><%=appraisalMp.get("REVIEWER")%></td>
							</tr>
                        </table>
                        
                        <!-- Legends -->
                    
                        <%-- <s:form action="AppraisalBulkFinalization" id="formID" method="POST" theme="simple">
                            <s:hidden name="id"></s:hidden>
                             <input type="hidden" name="fromPage" id ="fromPage" value="<%=fromPage%>"/>
                            <input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId%>"/>
	                        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
	                            <h4 style="float: left; font-weight: bold; width: 100%;">Score Cards
	                              <% if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {%>
		                            	<input type="submit" style="float: right;" value="Bulk Finalization" class="btn btn-primary" />
			                      <%} %>
			                      </h4>
	                        </div> --%>
                            <div style=" width:100%;">
                                <table class="table table-bordered" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <th>Employee Name</th>
                                        <%for (int i = 0; i < memberList.size(); i++) { %>
                                      	  <th>
                                      	  <% 
	                                      	  String strLabel = "Appraiser";
	                                      	  if(uF.parseToInt(memberList.get(i)) == 3) { 
	                                      		strLabel = "Appraisee";
	                                      	  }
                                      	  %>
                                      	  <%=strLabel %> <span style="font-weight: normal;"> (<%=orientationMemberMp.get(memberList.get(i))%>)</span>
                                      	  </th>
                                        <% } %>
                                        <th>Reviewer</th>
                                        <!-- <th width="10%">Balanced Score</th>
                                        <th>Finalize</th> -->
                                    </tr>
                                    <%
                                        Map<String, Map<String, String>> outerMp = (Map<String, Map<String, String>>) request.getAttribute("outerMp");
                                    	Map<String, Map<String, String>> reviewerOutMp = (Map<String, Map<String, String>>) request.getAttribute("reviewerOutMp");
                                       	StringBuilder sbGapEmp = null;
                                       	for (int i = 0; empList != null && i < empList.size(); i++) {
                                       		Map<String, String> value = outerMp.get(empList.get(i).trim());
                                       		if (value == null)
                                       			value = new HashMap<String, String>();
                                       		Map<String, String> valueReviewer = reviewerOutMp.get(empList.get(i).trim());
                                       		//System.out.println(empList.get(i).trim() +" --- valueReviewer ===> "+valueReviewer);
                                       		if (valueReviewer == null)
                                       			valueReviewer = new HashMap<String, String>();
                                       		double total = 0.0;
                                       		String remark = hmRemark.get(apid+"_"+empList.get(i).trim());
                                        %>
                                    <tr>
                                        <td>
                                            <div style="float: left; width: 100%;">
                                                <div style="float: left; width: 21px; height: 21px; margin-right:10px;">
                                                    <%if(uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))==0) { %>
                                                    	<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Not filled yet"></i>
                                                    <% } else if(memberCount == uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) { 
                                                        if(remark==null) { %>
                                                   		 <i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>
                                                      <% } else { %>
                                                   		 <i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
                                                    <%}
                                                    } else if(memberCount>uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) { %>
                                                   		<i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d;padding: 5px 5px 0 5px;" title="Waiting for completion"></i>
                                                  <% } %>
                                                    	&nbsp;&nbsp;
                                                </div>
                                                <div style="float: left; width: 21px; height: 21px;">
                                                    <img height="21" width="21" class="lazy img-circle" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+empImageMap.get(empList.get(i).trim())%>" />
                                                </div>
                                                <div style="margin-left: 60px;">
                                                <!-- Created By Dattatray Date:21-July-2021 Note: empId encrypt -->
                                                    <a href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empList.get(i) %>');"><%=hmEmpName.get(empList.get(i).trim())%></a>
                                                    <%=uF.showData(hmEmpCodeDesig.get(empList.get(i).trim()),"")%>
                                                    working at
                                                    <%=uF.showData(locationMp.get(empList.get(i).trim()),"")%>
                                                </div>
                                            </div>
                                            <div style="float: left; margin-top: 3px; padding-left: 60px;">
                                                <%=hmMemberMP!=null && hmMemberMP.get(empList.get(i).trim())!=null ? hmMemberMP.get(empList.get(i).trim()) : "" %>
                                            </div>
                                        </td>
                                        <%
                                            for (int j = 0; memberList != null && !memberList.isEmpty() && j < memberList.size(); j++) {
                                            	total += uF.parseToDouble(value.get(memberList.get(j).trim()));
                                            %>
                                        <td align="right">
                                            <div>
	                                            <%if(!uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_0"),"0").equals("0") || !uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_1"),"0").equals("0")) { %>
		                                            <span style="float: left; margin-left: 10px;">
		                                            	<a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=memberList.get(j).trim() %>','RUR','<%=appFreqId %>','<%="" %>')" title="Unread Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_0"),"0") %>]</a>
		                                            	<a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=memberList.get(j).trim() %>','R','<%=appFreqId %>','<%="" %>')" title="Read Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_1"),"0") %>]</a>
		                                            </span>
	                                            <% } %>
	                                            <%if(value.get(memberList.get(j).trim())!=null) { %>
	                                        <!-- ===start parvez date: 02-03-2023=== -->	
	                                            	<%-- <a href="javascript:void(0)" onclick="getMemberData('<%=memberList.get(j)%>', '<%=empList.get(i).trim()%>', '<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%=orientationMemberMp.get(memberList.get(j).trim())%>', '<%=appFreqId %>')">
	                                            	<%=uF.showData(value.get(memberList.get(j).trim()), "0")%>%</a> --%>
	                                            	<%if(value.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(value.get("ACTUAL_CAL_BASIS"))){ %>
		                                            	<a href="javascript:void(0)" onclick="getMemberData('<%=memberList.get(j)%>', '<%=empList.get(i).trim()%>', '<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%=orientationMemberMp.get(memberList.get(j).trim())%>', '<%=appFreqId %>')">
		                                            		<%=value.get(memberList.get(j).trim()) != null ? uF.formatIntoOneDecimal(uF.parseToDouble(value.get(memberList.get(j).trim())) / 20) : "0"%></a>
		                                            		
	                                            	<%}else{ %>
	                                            		<a href="javascript:void(0)" onclick="getMemberData('<%=memberList.get(j)%>', '<%=empList.get(i).trim()%>', '<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%=orientationMemberMp.get(memberList.get(j).trim())%>', '<%=appFreqId %>')">
	                                            			<%=uF.showData(value.get(memberList.get(j).trim()), "0")%>%</a>
	                                            	<%} %>
	                                            	
	                                            <% } else { %>
	                                            	<!-- 0% -->
	                                            	<%=value.get("CalculationBasisOn")!=null && uF.parseToBoolean(value.get("CalculationBasisOn"))?"" : "0%" %>
	                                            <% } %>
	                                        <!-- ===end parvez date: 02-03-2023=== -->    
	                                            <div id="starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>"></div>
	                                            <input type="hidden" name="gradewithrating<%=empList.get(i).trim()%>" id="gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>"
	                                                value="<%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 20 + "" : "0"%>" /> 
		                                            <script type="text/javascript">
		                                                $('#starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>').raty({
		                                                	readOnly: true,
		                                                	start: <%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 20 + "" : "0"%>,
		                                                	half: true,
		                                                	targetType: 'number',
		                                                	click: function(score, evt) {
		                                                		$('#gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()%>').val(score);
		                                                	}
		                                                });
		                                            </script>
	                                            </div>
	                                            <%if(uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER"))>0) { %>
		                                          	<div>
		                                            <%-- <%if(!uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_0"),"0").equals("0") || !uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_1"),"0").equals("0")) { %>
			                                            <span style="float: left; margin-left: 10px;">
			                                            	<a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=memberList.get(j).trim() %>','RUR','<%=appFreqId %>','<%="" %>')" title="Unread Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_0"),"0") %>]</a>
			                                            	<a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=memberList.get(j).trim() %>','R','<%=appFreqId %>','<%="" %>')" title="Read Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_1"),"0") %>]</a>
			                                            </span>
		                                            <% } %> --%>
		                                            <%if(value.get(memberList.get(j).trim()+"_REVIEWER") != null) { %>
		                                            	<%-- <a href="javascript:void(0)" onclick="getMemberData('<%=memberList.get(j)%>', '<%=empList.get(i).trim()%>', '<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%=orientationMemberMp.get(memberList.get(j).trim())%>', '<%=appFreqId %>')"> --%>
		                                            	Reviewer: <%=uF.showData(value.get(memberList.get(j).trim()+"_REVIEWER"), "0")%>%
		                                            	<!-- </a> -->
		                                            <% } else { %>
		                                            	0%
		                                            <% } %>
		                                            <div id="starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER" %>"></div>
		                                            <input type="hidden" name="gradewithrating<%=empList.get(i).trim()%>" id="gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>"
		                                                value="<%=value.get(memberList.get(j).trim()+"_REVIEWER") != null ? uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER")) / 20 + "" : "0"%>" /> 
			                                            <script type="text/javascript">
			                                                $('#starPrimary<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>').raty({
			                                                	readOnly: true,
			                                                	start: <%=value.get(memberList.get(j).trim()+"_REVIEWER") != null ? uF.parseToDouble(value.get(memberList.get(j).trim()+"_REVIEWER")) / 20 + "" : "0"%>,
			                                                	half: true,
			                                                	targetType: 'number',
			                                                	click: function(score, evt) {
			                                                		$('#gradewithrating<%=memberList.get(j).trim()+"_"+empList.get(i).trim()+"_REVIEWER"%>').val(score);
			                                                	}
			                                                });
			                                            </script>
		                                            </div>
	                                            <% } %>
                                        	</td>
                                        <% } %>
                                        
                                        <td align="right">
                                           <%if(!uF.showData(hmReadUnreadCount.get("reviewer_"+empList.get(i).trim()+"_0"),"0").equals("0") || !uF.showData(hmReadUnreadCount.get("reviewer_"+empList.get(i).trim()+"_1"),"0").equals("0")) { %>
                                            <span style="float: left; margin-left: 10px;">
                                            	<a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=valueReviewer.get("REVIEWER_USERTYPE") %>','RUR','<%=appFreqId %>','<%="" %>')" title="Unread Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(valueReviewer.get("REVIEWER_USERTYPE")+"_"+empList.get(i).trim()+"_0"),"0") %>]</a>
                                            	<a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=valueReviewer.get("REVIEWER_USERTYPE") %>','R','<%=appFreqId %>','<%="" %>')" title="Read Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(valueReviewer.get("REVIEWER_USERTYPE")+"_"+empList.get(i).trim()+"_1"),"0") %>]</a>
                                            </span>
                                           <% } %>
                                           <%if(valueReviewer.get("REVIEWER")!=null) { %>
                                           	<a href="javascript:void(0)" onclick="getMemberData('<%=valueReviewer.get("REVIEWER_USERTYPE") %>', '<%=empList.get(i).trim()%>', '<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%="Reviewer" %>', '<%=appFreqId %>')">
                                           	<%-- <%=uF.showData(valueReviewer.get("REVIEWER"), "0")%>% --%>
                                           	<%if(valueReviewer.get("ACTUAL_CAL_BASIS")!=null && uF.parseToBoolean(valueReviewer.get("ACTUAL_CAL_BASIS"))){ %>
                                           		<%=valueReviewer.get("REVIEWER") != null ? uF.formatIntoOneDecimal(uF.parseToDouble(valueReviewer.get("REVIEWER")) / 20) : "0"%>
                                           	<% } else { %>
                                           		<%=uF.showData(valueReviewer.get("REVIEWER"), "0")%>%
                                           <% } %>
                                           	</a>
                                           <% } else { %>
                                           	0%
                                           <% } %>
                                           <div id="starPrimary<%="reviewer_"+empList.get(i).trim()%>"></div>
                                           <input type="hidden" name="gradewithrating<%=empList.get(i).trim()%>" id="gradewithrating<%="reviewer_"+empList.get(i).trim()%>"
                                               value="<%=valueReviewer.get("REVIEWER") != null ? uF.parseToDouble(valueReviewer.get("REVIEWER")) / 20 + "" : "0"%>" /> 
                                            <script type="text/javascript">
                                                $('#starPrimary<%="reviewer_"+empList.get(i).trim()%>').raty({
                                                	readOnly: true,
                                                	start: <%=valueReviewer.get("REVIEWER") != null ? uF.parseToDouble(valueReviewer.get("REVIEWER")) / 20 + "" : "0"%>,
                                                	half: true,
                                                	targetType: 'number',
                                                	click: function(score, evt) {
                                                		$('#gradewithrating<%="reviewer_"+empList.get(i).trim()%>').val(score);
                                                	}
                                                });
                                            </script>
                                       	</td>
                                        <%-- <td align="right">
                                            <%
                                                Map<String, String> hmAttributeThreshhold = (Map<String, String>) request.getAttribute("hmAttributeThreshhold");
                                                List<String> attribIdList = (List<String>) request.getAttribute("attribIdList");
                                                Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
                                                boolean flag = false;
                                                StringBuilder attribIds = new StringBuilder();
                                                int attribCnt = 0;
                                                int aggregateCnt = 0;
                                                for(int a=0; attribIdList != null && !attribIdList.isEmpty() && a<attribIdList.size(); a++) {
                                                	double aggregate = uF.parseToDouble(hmScoreAggregateMap.get(empList.get(i).trim()+"_"+attribIdList.get(a)));
                                                	//System.out.println("aggregate ===>> " + aggregate + " uF.parseToDouble(hmAttributeThreshhold.get(attribIdList.get(a))) ===>> " + uF.parseToDouble(hmAttributeThreshhold.get(attribIdList.get(a))));
                                                	if(aggregate < uF.parseToDouble(hmAttributeThreshhold.get(attribIdList.get(a)))) {
                                                		//System.out.println("aggregate ===>> " + aggregate);
                                                		attribIds.append(attribIdList.get(a)+"::");
                                                		aggregateCnt++;
                                                	}
                                                	attribCnt++;
                                                }
                                                //System.out.println("attribCnt ===>> " + attribCnt +" aggregateCnt ===>> " + aggregateCnt);
                                                if(attribCnt == aggregateCnt) {
                                                	flag = true;
                                                }
                                                %>
                                            <div style="float: left; width: 100%;">
                                                <%
                                                    String aggregate="0.0";
                                                    if(memberCount==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) {
                                                    	aggregate=value.get("AGGREGATE") != null ? uF.parseToDouble(value.get("AGGREGATE")) / 20 + "" : "0";
                                                    %>
                                                <% if(!flag) { %>
                                                 <span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-up" style="color:#68ac3b;height: 16px; width: 16px;" aria-hidden="true"></i></span>
                                                 
                                                <% } else {
                                                    if(sbGapEmp == null){
                                                    	sbGapEmp = new StringBuilder();
                                                    	sbGapEmp.append(","+empList.get(i).trim()+",");
                                                    } else {
                                                    	sbGapEmp.append(empList.get(i).trim()+",");
                                                    }
                                                    %>
                                                <span style="float: left; margin-left: 10px; margin-top: 5px;"><img style="height: 16px; width: 16px;" src="images1/thumbs_down_red.png"></span>
                                                <% } %>
                                                <span style="float: right;">
                                                <a onclick="getBalancedScoreData('<%=apid%>','<%=appFreqId%>','<%=empList.get(i).trim()%>','AD','<%=hmEmpName.get(empList.get(i).trim())%>')" href="javascript:void(0);"><%=uF.showData(uF.getRoundOffValue(2,uF.parseToDouble(value.get("AGGREGATE"))), "NA")%>%</a>
                                                </span>
                                                <% } else { %>
                                                <span style="float: right;">NA</span>
                                                <% } %>
                                            </div>
                                            <div id="starPrimary_BS<%=empList.get(i).trim()%>"></div>
                                            <input type="hidden" id="gradewithrating_BS<%=empList.get(i).trim()%>" value="<%=aggregate%>" name="gradewithrating<%=empList.get(i).trim()%>" /> 
                                            <script type="text/javascript">
                                                $('#starPrimary_BS<%=empList.get(i).trim()%>').raty({
                                                	readOnly: true,
                                                	start: <%=aggregate%>,
                                                	half: true,
                                                	targetType: 'number',
                                                	click: function(score, evt) {
                                                		$('#gradewithrating_BS<%=empList.get(i).trim()%>').val(score);
                                                	}
                                                });
                                            </script>
                                        </td> --%>
                                        
                                        <%-- <td align="center">
                                            <% if(!uF.parseToBoolean(appraisalMp.get("APP_FREQ_CLOSE"))) { %>
                                            <%if(memberCount == uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))) {
                                                if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {
                                                	if(remark ==null) {
                                                %> 
                                           				 <a href="javascript:void(0)" onclick="giveRemark('<s:property value="id" />','<%=empList.get(i).trim()%>','<%=flag %>','<%=appraisalMp.get("FREQUENCY")%>',1,'<%=appFreqId %>','AD')" >Finalize</a> 
                                            	<% } else { %>
                                           				 <a href="javascript:void(0)" onclick="giveRemark('<s:property value="id" />','<%=empList.get(i).trim()%>','<%=flag %>','<%=appraisalMp.get("FREQUENCY")%>',2,'<%=appFreqId %>','AD')" ><%="Finalized by "+remark %></a>
                                            	<% }
                                                } else {
                                                	if(remark !=null) {
                                                %>
                                           			 <a href="javascript:void(0)" onclick="giveRemark('<s:property value="id" />','<%=empList.get(i).trim()%>','<%=flag %>','<%=appraisalMp.get("FREQUENCY")%>',2,'<%=appFreqId %>','AD')" ><%="Finalized by "+remark %></a>
                                          	  <% }
                                                }
                                                } else {
                                                %>
                                            -
                                            <% } %>
                                            <% } else { %>
                                            -
                                            <% } %>
                                        </td> --%>
                                    </tr>
                                    <% }
                                        if(sbGapEmp==null) { 
                                        	sbGapEmp = new StringBuilder();
                                        }
                                        %>
                                </table>
                                <input type="hidden" name="strGapEmp" id="strGapEmp" value="<%=sbGapEmp.toString() %>" />
                            </div>
                        <%-- </s:form> --%>
                    </div>
                    <% if(dataType != null && dataType.equals("L")) { 
                    	if(!flag) {
                    %>
	                    <div class="col-lg-12 col-sm-12 col-md-12" style="text-align: center;">
	                    	<div id="<%=apid %>_<%=empID %>_<%=strBaseUserTypeId %>">
		                    	<input type="button" value="Approve" class="btn btn-primary" onclick="approveAverageFeedbackOfReviewer('<%=apid %>', '<%=empID %>', '<%=strBaseUserTypeId %>', 'Reviewer', '<%=appFreqId %>');" /> <!-- id, empID, userType, currentLevel, role, appFreqId -->
		                    	<input type="button" value="Review" class="btn btn-primary" onclick="staffReviewPoup('<%=apid %>', '<%=empID %>', '<%=strBaseUserTypeId %>', '', 'Reviewer', '<%=appFreqId %>');" />
	                    	</div>
	                    </div>
                    <% } 
                    } else { %>
                    	<div class="col-lg-12 col-sm-12 col-md-12" style="text-align: center;"> 
	                    	<input type="button" value="View Review" class="btn btn-primary" onclick="staffReviewSummaryPoup('<%=apid %>', '<%=empID %>', '<%=strBaseUserTypeId %>', '', 'Reviewer', '<%=appFreqId %>');" />
	                    </div>
                    <% } %>
                    
                    <div class="custom-legends">
                    	<div class="custom-legend pullout"><div class="legend-info">Completed</div></div>
						<div class="custom-legend pending"><div class="legend-info">Not filled yet</div></div>
						<div class="custom-legend approved"><div class="legend-info">Finalized</div></div>
						<div class="custom-legend re_submit"><div class="legend-info">Waiting for completion</div></div>
					</div>
					
                </div>
                <!-- /.box-body -->
		<% } else { %>
			<div class="nodata msg">No Review Status.</div>
		<% } %>


	<!-- <div class="modal" id="modalInfoPopup" role="dialog">
	    <div class="modal-dialog" id="modal-dialogPopup">
	        <div>
	            <div id="modalheaderPopup">
	                <button type="button" class="close" id="closePopup" data-dismiss="modal">&times;</button>
	                <div class="modal-title" id="modal-titlePopup" style="font-weight:bold">-</div>
	            </div>
	            <div id="modal-bodyPopup">
	            </div>
	            <div class="modal-footer" id="modal-footerPopup">
	                <button type="button" id="closeButtonPopup" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div> -->
	
	
	<div class="modal" id="modalInfoPopup" role="dialog">
	    <div class="modal-dialog" id="modal-dialogPopup">
	        <!-- Modal content -->
	        <div class="modal-content">
	            <div class="modal-header" id="modal-headerPopup">
	                <button type="button" class="closePop" id="closePop" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title" id="modal-titlePopup">-</h4>
	            </div>
	            <div class="modal-body" id="modal-bodyPopup" style="height:500px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer" id="modal-footerPopup">
	                <button type="button" id="closeButtonPopup" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>


<script>
 /* $("#formID").submit(function(event){
	event.preventDefault();
	var form_data = $("#formID").serialize();
	var title = 'Appraisal Bulk Finalization';
  	var dialogEdit = '.modal-body';
  	$(dialogEdit).empty();
  	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
  	$("#modalInfo").show();
  	$('.modal-title').html(title);
  	if($(window).width() >= 900) {
  		$('.modal-dialog').width(900);
  	}
  	$.ajax({
  		type: 'POST',
		 url:"AppraisalBulkFinalization.action",
	     data:form_data,
	     success:function(result){
	    	 $(dialogEdit).html(result);
	     }
  	});
	
 }); */

</script>
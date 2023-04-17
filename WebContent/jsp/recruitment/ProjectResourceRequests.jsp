<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>

<style>
    .list_req>li{
    padding-bottom: 5px;
    padding-top: 5px; 
    border-bottom: 1px solid #F0F0F0; 
    }
    .list_req>li>span{
    top:0px;
    }
</style>

	<%-- <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
	<script type="text/javascript" src="scripts/_rating/js/jquery.raty.js"> </script>
	<script type="text/javascript" src="scripts/_rating/js/jquery.raty.js"> </script>
	<script type="text/javascript" src="js/datatables_new/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="js/datatables_new/dataTables.bootstrap.min.js"></script>
	<script type="text/javascript" src="js/datatables_new/dataTables.buttons.min.js"></script>
	<script type="text/javascript" src="scripts/jquery.lazyload.js"></script> --%>
	
<%-- <script type="text/javascript" src="scripts/charts/highcharts.js"></script>
<script type="text/javascript" src="scripts/charts/exporting.js"></script> --%>

<g:compress>
    <script>

    $(function(){
    	$("#strSkills").multiselect().multiselectfilter();
    	$("#strProjects").multiselect().multiselectfilter();
    	
    });
    
    function GetXmlHttpObject() {
        if (window.XMLHttpRequest) {
                return new XMLHttpRequest();
        }
        if (window.ActiveXObject) {
                return new ActiveXObject("Microsoft.XMLHTTP");
        }
        return null;
    }
        
   	function approveDenyProResReq(nCount, adStatus, proResReqId) {
   		//alert(nCount +" "+ RID);
   		var adTitle = 'approve';
   		if(adStatus == -1) {
   			adTitle = 'deny';
   		} 
   		var strDesig = document.getElementById("strDesig"+proResReqId).value;
   		var f_strFinancialYear = document.getElementById("f_strFinancialYear").value;
   		if(strDesig=='' && adStatus == 1) {
   			alert("Please select designation");
   		} else {
    		if(confirm('Are you sure, you want to '+adTitle+' this resource request?')) {
    			getContent('', 'ProjectResourceRequests.action?type=ApproveDeny&adStatus='+adStatus+'&proResReqId='+proResReqId
    				+'&strDesig='+strDesig+'&f_strFinancialYear='+f_strFinancialYear);
    			document.getElementById("myDivM"+nCount).innerHTML="<span style=\"color: #68AC3B;\"> Approved </span>";
    		}
   		}
   	}
    	
   	function selectall(x, proResReqId) {
   		var  status=x.checked; 
   		var  arr= document.getElementsByName(proResReqId);
   		for(i=0;i<arr.length;i++){ 
   	  		arr[i].checked=status;
   	 	}
   	}
       	
   	function checkAll() {
   		var sendAll = document.getElementById("sendAll");		
   		var proResReqId = document.getElementsByName('proResReqId');
   		var cnt = 0;
   		var chkCnt = 0;
   		for(var i=0;i<proResReqId.length;i++) {
   			cnt++;
   			 if(proResReqId[i].checked) {
   				 chkCnt++;
   			 }
   		 }
   		if(cnt == chkCnt) {
   			sendAll.checked = true;
   		} else {
   			sendAll.checked = false;
   		}
   	}
      	
  	function checkAction(adStatus) {
		var adTitle = 'approve';
   		if(adStatus == -1) {
   			adTitle = 'deny';
   		} 
   		var strAllDesig = document.getElementById("strAllDesig").value;
   		if(strAllDesig=='' && adStatus == 1) {
   			alert("Please select designation");
   		} else {
    		if(confirm('Are you sure, you want to '+adTitle+' selected resource request?')) {
    			var form_data = $("form[name='frmProjectResourceRequests']").serialize();
   		     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		     	$.ajax({
   		     		type : 'POST',
   		 			url : 'ProjectResourceRequests.action?type=BulkApproveDeny&adStatus='+adStatus,
   		 			data: form_data,  
   		 			cache : false,
   		 			success : function(res) {
   		 				$("#divResult").html(res);
   		 			}
   		 		});
    		}
   		}
	}
       	
    </script>
</g:compress>

<%
    UtilityFunctions uF = new UtilityFunctions();
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String fromPage = (String) request.getAttribute("fromPage");
    %>
<%-- <section class="content">

    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 400px;"> --%>
                      <s:form name="frmProjectResourceRequests" id="frmProjectResourceRequests" action="ProjectResourceRequests" theme="simple">
                         <input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>"/>
                         <div class="box box-default collapsed-box">
							<div class="box-header with-border">
							    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
							    <div class="box-tools pull-right">
							        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							    </div>
							</div>
							<div class="box-body" style="padding: 5px; overflow-y: auto;">
								<% if(strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) { %>
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
										<i class="fa fa-filter"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Financial Year</p>
											<s:select theme="simple" name="f_strFinancialYear" id="f_strFinancialYear" listKey="financialYearId"
												listValue="financialYearName" list="financialYearList" key="" onchange="submitForm();" />
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Organisation</p>
											<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="orgList" key=""  onchange="submitForm();"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Skills</p>
											<s:select theme="simple" name="strSkills" id="strSkills" listKey="skillsId" listValue="skillsName" list="skillsList" key="" multiple="true"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Projects</p>
											<s:select theme="simple" name="strProjects" id="strProjects" listKey="projectID" listValue="projectName" list="projectList" key="" multiple="true"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">&nbsp;</p>
											<input type="button" value="Search" class="btn btn-primary" onclick="submitForm();"/>
										</div>
									</div>
								</div><br>
								<% } %>
							</div>
						</div> 
                            
					
					<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
					<% session.setAttribute(IConstants.MESSAGE, ""); %>
					
                       <ul style="clear: both; width: 99%; margin-bottom: 50px" class="list_req">
                       		<% 	java.util.List requestList = (java.util.List) request.getAttribute("requestList"); %>
                       		<% if (requestList != null && requestList.size()>0) { %>
                       		<li class="list" style="float: left; width: 100%;">
                       			<div><span style="float: left;"><input type="checkbox" name="sendAll" id="sendAll" onclick="selectall(this, 'proResReqId')" /></span>  <!-- checked="checked" -->
		                       		<div style="float:left; margin-left: 15px;">
		                       			<select name="strAllDesig" id="strAllDesig" style="width:150px !important;">
		                       				<option value="">Select Designation</option>
		                       				<%=(String)request.getAttribute("sbDesigList") %>
		                       			</select>
		                       			<a href="javascript:void(0)" onclick="checkAction('1');"><i class="fa fa-check-circle checknew" style="padding-top: 0px !important;" aria-hidden="true" title="Approve"></i></a>
		                       			<a href="javascript:void(0)" onclick="checkAction('-1');"><i class="fa fa-times-circle cross" style="padding-top: 0px !important;" aria-hidden="true" title="Deny"></i></a>
		                       		</div>
	                       		</div>
	                       	</li>
                       		<% } %>
                            <%	for (int i = 0; requestList != null && i < requestList.size(); i++) { %>
                            <li class="list" style="float: left; width: 100%;"><%=requestList.get(i)%></li>
                            <%	}
                                if (requestList == null || (requestList != null && requestList.size() == 0)) {
                            %>
                            <li class="nodata msg"><div class="alert" style="background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No project resource request data available.</div></li>
                            <% } %>
                        </ul> 
                        
                        <div class="custom-legends">
						  <!-- <div class="custom-legend pullout">
						    <div class="legend-info">Waiting for workflow</div>
						  </div>
						  <div class="custom-legend pending">
						    <div class="legend-info">Waiting for approval</div>
						  </div>
						  <div class="custom-legend approved">
						    <div class="legend-info">Approved</div>
						  </div>
						  <div class="custom-legend denied">
						    <div class="legend-info">Denied</div>
						  </div> -->
						  <%
                              if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.MANAGER)
                              	|| strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
                          %>
						  <!-- <br/> -->
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info">
						    	<i class="fa fa-check-circle checknew" aria-hidden="true"></i>Approve 
						    </div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info">
						    	<i class="fa fa-times-circle cross" aria-hidden="true"></i>Deny
						    </div>
						  </div>
						  <% } %>
						  <!-- <br/> -->
						</div>
				</s:form>
						
               <%--  </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section> --%>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" charset="utf-8">
    $(function(){
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
    
	function submitForm() {
		var finansyr = document.getElementById("f_strFinancialYear").value;
		if(document.getElementById("f_org")) {
			var org = document.getElementById("f_org").value;
		}
		var strSkills = getSelectedValue("strSkills");
		var strProjects = getSelectedValue("strProjects");
		var divResult = 'divResult';
    	var action = 'ProjectResourceRequests.action?f_strFinancialYear='+finansyr+'&f_org='+org+'&strSkill='+strSkills+'&strProject='+strProjects;
    	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
            	$("#"+divResult).html(result);
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
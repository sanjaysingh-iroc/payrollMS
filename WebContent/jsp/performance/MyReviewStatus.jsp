<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

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
/* .table-bordered {
border: 2px solid #DBDBDB !important;
} */
</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">

	function getEmpProfile(empId){
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('See Score');
		 $.ajax({
				url : "AppraisalEmpProfile.action?empId="+empId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	function getData(empId, id,appFreqId) {

		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('See Score');
		 $.ajax({
				url : "AppraisalScoreStatus.action?id=" + id+ "&empid=" + empId + "&type=popuMyReviewScoreStatusp&appFreqId="+appFreqId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);	
				}
			});

	}

	function getMemberData(memberId, empId, id, empName, role, appFreqId) {
 //alert("memberId "+memberId+" empId "+empId+" id "+id+" empName "+empName+" role "+role);
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 var title= 'Score Summary of '+empName+((role=='')?'[Aggregate]':' [Role: '+role+']');
		 $(".modal-title").html(title);
		 if($(window).width() >= 900){
			 $(".modal-dialog").width(900);
		 } 
		 $.ajax({
				url : "MyReviewScoreStatus.action?id=" + id + "&empid=" + empId + "&type=popup&memberId=" + memberId+"&appFreqId="+appFreqId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});

	} 

	function getAppraisalDetail(empId, id,appFreqId) {

		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('See Score');
		 $.ajax({
				url : "FullAppraisalDetails.action?id=" + id + "&empid=" + empId+"&appFreqId="+appFreqId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});

	}

	function getCustomerFactSheet(empId) {

		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('See Score');
		 $.ajax({
				url : "MyProfile.action?empId=" + empId + "&popup=popup",
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});

	}
	
	function giveRemark(id, empId,appFreqId) {

		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Finalisation');
		 $.ajax({
				url : "AppraisalRemark.action?id=" + id
						+ "&empid=" + empId+"&appFreqId="+appFreqId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});

	}
	
	function seeEmpList(empId,aid,appFreqId) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Employee List');
		 $.ajax({
				url : "AppraisalApproveMembers.action?empID="+empId+"&id="+aid+"&appFreqId="+appFreqId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});

}
	
	var dialogEdit = '#ShowQueDiv';
	function showAllQuestion(appid,empId,usertypeId,readstatus,appFreqId) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Reviews');
		 $.ajax({
				url : "ShowAllSingleOpenWithoutMarkQue.action?appid="+appid+"&empId="+empId+"&usertypeId="+usertypeId+"&readstatus="+readstatus+"&appFreqId="+appFreqId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	
	function closePopup(){
		$(dialogEdit).dialog('close');
	}	
	
	
function openEmployeeProfilePopup(empId) {
	
	 
		var id=document.getElementById("panelDiv");
		if(id){
			id.parentNode.removeChild(id);
				}
	
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Employee Information');
		 if($(window).width() >= 900){
			 $(".modal-dialog").width(900);
		 }
		 $.ajax({
				//url : "ApplyLeavePopUp.action",  
				url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});

	 }
	 
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
		$("body").on('click','#closeButton1',function(){
			$(".modal-dialog1").removeAttr('style');
			$("#modal-body1").height(400);
			$("#modalInfo1").hide();
	    });
	 });
	
	/* function showCommentbox(id){
		//alert("IN status .....")
		var cmtboxstatus = document.getElementById("commentboxstatus"+id).value;
		if(cmtboxstatus == 'O'){
			document.getElementById("commentboxstatus"+id).value = 'C';
			document.getElementById("quecommentdiv"+id).style.display='block';
		}else{
			document.getElementById("quecommentdiv"+id).style.display='none';
			document.getElementById("commentboxstatus"+id).value = 'O';
		}
	} */	
	
</script>
<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Review Status" name="title" />
</jsp:include> --%>

<%
	List<String> empList = (List<String>) request.getAttribute("empList");
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	String appFreqId = (String) request.getParameter("appFreqId");
	String oriented_type = (String) request.getAttribute("oriented_type");
	UtilityFunctions uF = new UtilityFunctions();
 
	Map<String, String> empMap = (Map<String, String>) request.getAttribute("appraisalMp");
	Map<String, String> hmEmpCode = (Map<String, String>) request.getAttribute("hmEmpCode");
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	List<String> memberList = (List<String>) request.getAttribute("memberList");
	Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");
	Map<String, String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
	Map<String, String> locationMp = (Map<String, String>) request.getAttribute("locationMp");

	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
	Map<String,String> hmEmpCount=(Map<String,String>)request.getAttribute("hmEmpCount");
	int memberCount=(Integer)request.getAttribute("memberCount");
	
	Map<String,String> hmRemark=(Map<String,String>)request.getAttribute("hmRemark");
	Map<String,String> hmEmpSuperVisor=(Map<String,String>)request.getAttribute("hmEmpSuperVisor");
	
	String apid=request.getParameter("id");
	
	Map<String,String> hmMemberMP=(Map<String,String>)request.getAttribute("hmMemberMP");
	Map<String, String> hmReadUnreadCount = (Map<String, String>)request.getAttribute("hmReadUnreadCount");
	//EncryptionUtils EU = new EncryptionUtils();// Created By Dattatray Date:21-July-2021
%>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
                
                <div class="box-header with-border">
                    <h3 class="box-title">Review Status</h3>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                    <div class="leftbox reportWidth">
						<s:form action="StaffAppraisal" id="formID" method="POST"
							theme="simple">
							<h4>&nbsp;&nbsp; 
							<%=empMap.get("APPRAISAL")%>
							</h4>
							<table class="table table-striped">
								<%-- <tr>
									<th width="15%" align="right">Appraisal Name</th>
									<td><%=empMap.get("APPRAISAL")%></td>
								</tr> --%>
								<tr>
									<th width="15%" align="right">Review Type:</th>
									<td><%=empMap.get("APPRAISALTYPE")%></td>
								</tr>
								<tr>
									<th valign="top" align="right">Description:</th>
									<td><%=empMap.get("DESCRIPTION")%></td>
								</tr>
								<tr>
									<th valign="top" align="right">Instructions:</th>
									<td><%=empMap.get("INSTRUCTION")%></td>
								</tr>
								<tr>
									<th align="right">Frequency:</th>
									<td><%=empMap.get("FREQUENCY")%></td>
								</tr>
								<tr>
									<th align="right">Effective Date:</th>
									<td><%=empMap.get("FREQ_START_DATE")%></td>
								</tr>
								<tr>
									<th align="right">Due Date:</th>
									<td><%=empMap.get("FREQ_END_DATE")%></td>
								</tr>
								<%-- <tr>
									<th align="right">Reviewee</th>
									<td><%=empMap.get("APPRAISEE")%></td>
								</tr> --%>
								<tr>
									<th align="right">Orientation:</th>
									<td><%=empMap.get("ORIENT")%></td>
								</tr>
								<tr>
									<th align="right">Appraiser:</th>
									<td><%=empMap.get("APPRAISER")%></td>
								</tr>
								<tr>
									<th align="right">Reviewer:</th>
									<td><%=empMap.get("REVIEWER")%></td>
								</tr>
							</table>
					
					<br/>
					<div class="row row_without_margin">
						<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
							<div style="float: left;margin-bottom:10px">
								<h4 style="margin-top: 0px;">Score Cards</h4>
							</div>
						</div>
					</div>
					<br/>
					
					<table class="table table-bordered" cellpadding="0" cellspacing="0" width="100%">
								<tr>
									<th>Employee Name</th>
								<%for (int i = 0; i < memberList.size(); i++) {  %>
									<th class="alignRight"><%=orientationMemberMp.get(memberList.get(i))%></th>
								<%} %>
					
									<th width="10%" class="alignRight">Balanced Score</th>
									<!-- <th>Finalize</th> -->
								</tr>
								<%
									Map<String, Map<String, String>> outerMp = (Map<String, Map<String, String>>) request.getAttribute("outerMp");
					
										for (int i = 0; empList != null && i < empList.size(); i++) {
											Map<String, String> value = outerMp.get(empList.get(i).trim());
											if (value == null)
												value = new HashMap<String, String>();
											double total = 0.0;
											String remark=hmRemark.get(apid+empList.get(i).trim());
											
								%>
								<tr>
									<td>
									<div style="float: left; width: 21px; height: 21px; margin-right:10px;">
									<%if(uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))==0){ %>
										<%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png" title="Not filled yet"/> --%>
										<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Not filled yet"></i>
										<%}else if(memberCount==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ 
											if(remark==null){
										%>
										<%-- <img border="0" style="padding: 5px 5px 0pt;" src="<%=request.getContextPath()%>/images1/icons/pullout.png"> --%>
										<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>
										<%}else{
											%>
											<%-- <img border="0" style="padding: 5px 5px 0pt;" src="<%=request.getContextPath()%>/images1/icons/approved.png"> --%> 
											<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
											<%
										}
										}else if(memberCount>uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ %>
										 <%-- <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/re_submit.png" title="Waiting for completion"/> --%>
										 
										 <i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"  title="Waiting for completion"></i>
										<%} %>
										&nbsp;&nbsp;
									</div>
									<div style="float: left; width: 21px; height: 21px;">
											<img height="21" width="21" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+empImageMap.get(empList.get(i).trim())%>" />
											<%-- <img src="userImages/<%=uF.showData(empImageMap.get(empList.get(i).trim()), "avatar_photo.png")%>" border="0" height="21px" /> --%>
										</div>
										<div style="float: left; margin-left: 5px; width:80%;">
										
											<%-- <a href="javascript:void(0)" onclick="getCustomerFactSheet('<%=empList.get(i)%>')"><%=hmEmpName.get(empList.get(i))%></a> --%>
											
											<!-- Created By Dattatray Date:21-July-2021 Note : empId encrypt -->
											<a href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empList.get(i) %>');"><%=hmEmpName.get(empList.get(i).trim())%></a>
											<%=hmEmpCodeDesig.get(empList.get(i).trim())%>
											working at
											<%=locationMp.get(empList.get(i).trim())%>
										</div>
										
										<div style="margin-top: 30px; padding-left: 60px;">
										<%=hmMemberMP!=null && hmMemberMP.get(empList.get(i).trim())!=null ? hmMemberMP.get(empList.get(i).trim()) : "" %>
										
										</div>
										
								</td>
									<%
										for (int j = 0; j < memberList.size(); j++) {
													total += uF.parseToDouble(value.get(memberList.get(j).trim()));
													//System.out.println("Created Id :::::::::: "+memberList+" _ "+empList);
													//System.out.println("Created Id :::::::::: "+memberList.get(j)+"_"+empList.get(i)+"_0");
									%>
					
									<td align="right">
									<%if(!uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_0"),"0").equals("0") || !uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_1"),"0").equals("0")){ %>
										<span style="float: left; margin-left: 10px;"> <a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=memberList.get(j).trim() %>','RUR','<%=appFreqId%>')" title="Unread Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_0"),"0") %>]</a>
										<a href="javascript: void(0)" onclick="showAllQuestion('<%=apid%>','<%=empList.get(i).trim()%>','<%=memberList.get(j).trim() %>','R','<%=appFreqId%>')" title="Read Single Open without marks question">[<%=uF.showData(hmReadUnreadCount.get(memberList.get(j).trim()+"_"+empList.get(i).trim()+"_1"),"0") %>]</a> </span>
									<%} %>
									<%if(value.get(memberList.get(j).trim())!=null){ %>
									<a href="javascript:void(0)" onclick="getMemberData('<%=memberList.get(j)%>','<%=empList.get(i).trim()%>','<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '<%=orientationMemberMp.get(memberList.get(j).trim())%>','<%=appFreqId%>')">
											<%=uF.showData(value.get(memberList.get(j).trim()), "0")%>%</a>
											<%}else{ %>
											0%
											<%} %>
											<div id="starPrimary<%=memberList.get(j).trim()+""+empList.get(i).trim()%>"></div> <input
												type="hidden" id="gradewithrating<%=memberList.get(j).trim()+""+empList.get(i).trim()%>"
												value="<%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 20 + "" : "0"%>"
												name="gradewithrating<%=empList.get(i).trim()%>" /> <script
													type="text/javascript">
										        $(function() {
										        	$('#starPrimary<%=memberList.get(j).trim()+""+empList.get(i).trim()%>').raty({
										        		readOnly: true,
										        		start: <%=value.get(memberList.get(j).trim()) != null ? uF.parseToDouble(value.get(memberList.get(j).trim())) / 20 + "" : "0"%>,
										        		half: true,
										        		targetType: 'number',
										        		click: function(score, evt) {
										        			$('#gradewithrating<%=memberList.get(j).trim()+""+empList.get(i).trim()%>').val(score);
										        			}
										        	});
										        	});
										        </script>
											</td>
									<%
										}
									%>
									<td align="right">
									<%-- <a href="javascript:void(0)"
										onclick="getData('<%=empList.get(i)%>','<s:property value="id" />')"><%=total%>%</a>
										<br /> <a href="javascript:void(0)"
										onclick="getAppraisalDetail('<%=empList.get(i)%>','<s:property value="id" />')"><%=total%>%</a> --%>
										
										<%-- <a href="AppraisalSummary.action?id=<s:property value="id" />&empId=<%=empList.get(i)%>"><%=uF.showData(value.get("AGGREGATE"), "NA")%></a> --%>
										
										<%
										String aggregate="0.0";
										if(memberCount==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ 
											aggregate=value.get("AGGREGATE") != null ? uF.parseToDouble(value.get("AGGREGATE")) / 20 + "" : "0";
										%>
										<%-- <a href="javascript:void(0)"
										onclick="getMemberData('','<%=empList.get(i).trim()%>','<s:property value="id" />', '<%=hmEmpName.get(empList.get(i).trim())%>', '')">
											<%=uF.showData(value.get("AGGREGATE"), "NA")%>%						
											</a> --%>
											<a href="MyReviewSummary.action?id=<s:property value="id" />&appFreqId=<%=appFreqId%>&empId=<%=empList.get(i).trim()%>"><%=uF.showData(value.get("AGGREGATE"), "NA")%>%</a>
											<%}else{ %>
											NA
											<%} %>
										
										<div id="starPrimary<%=empList.get(i).trim()%>"></div> <input
												type="hidden" id="gradewithrating<%=empList.get(i).trim()%>"
												value="<%=aggregate%>"
												name="gradewithrating<%=empList.get(i).trim()%>" /> <script
													type="text/javascript">
																        $(function() {
																        	$('#starPrimary<%=empList.get(i).trim()%>').raty({
																        		readOnly: true,
																        		start: <%=aggregate%>,
																        		half: true,
																        		targetType: 'number',
																        		click: function(score, evt) {
																        			$('#gradewithrating<%=empList.get(i).trim()%>').val(score);
																        			}
																        	});
																        	});
																        </script>
										
									</td>
									<%-- <td align="center">
									<%if(memberCount==uF.parseToInt(hmEmpCount.get(empList.get(i).trim()))){ 
										
										if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER)) {
											if(remark ==null){
									%> <a href="javascript:void(0)" onclick="giveRemark('<s:property value="id" />','<%=empList.get(i).trim()%>')" >Finalize</a> 
									<%}else{
										%>
										<a href="javascript:void(0)" onclick="giveRemark('<s:property value="id" />','<%=empList.get(i).trim()%>')" ><%="Finalized by "+remark %></a>
										<%
									}
					 					}else{
					 						if(remark !=null){
					 							%>
					 							<a href="javascript:void(0)" onclick="giveRemark('<s:property value="id" />','<%=empList.get(i).trim()%>')" ><%="Finalized by "+remark %></a>
					 							<%
					 						}
					 					}
									}else{
					 				%>
					 				-
					 				<%} %>
								</td> --%>
								</tr>
								<%
									}
								%>
							</table>
						</s:form>
					</div>
					<div class="custom-legends">
					  <div class="custom-legend pullout">
					    <div class="legend-info">Completed</div>
					  </div>
					  <div class="custom-legend pending">
					    <div class="legend-info">Not filled yet</div>
					  </div>
					  <div class="custom-legend approved">
					    <div class="legend-info">Finalized</div>
					  </div>
					  <div class="custom-legend re_submit">
					    <div class="legend-info">Waiting for completion</div>
					  </div>
					</div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>

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
<div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog modal-dialog1">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1">Employee Information</h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
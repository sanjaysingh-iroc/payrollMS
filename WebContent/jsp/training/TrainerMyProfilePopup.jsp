<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jquery-ui.css" /> --%> 
<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/scripts/fullcalendar/fullcalendar.css' />
<link rel='stylesheet' type='text/css' href='<%=request.getContextPath()%>/scripts/fullcalendar/fullcalendar.print.css' media='print' />
<%-- <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.ui.core.js"> </script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/datatable/jquery-1.4.4.min.js"> </script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tools.min.js"> </script>  --%>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/fullcalendar/fullcalendar.min.js"> </script>

<% List alEvents = (List)request.getAttribute("alTrainerCalender"); %>

<script type="text/javascript">
        
        
        $(function() {
    		$("body").on('click','#closeButton',function(){
    			$(".modal-body").height(400);
    			$(".modal-dialog").removeAttr('style');
    			$("#modalInfo").hide();
    	    });
    		$("body").on('click','.close',function(){
    			$(".modal-body").height(400);
    			$(".modal-dialog").removeAttr('style');
    			$("#modalInfo").hide();
    		});
    		
    		$('#calendarBox').fullCalendar({
    	        header: {
    	    	left: 'title', 
    	    	center: 'today prev,next',
    	    	right: 'month,basicWeek,basicDay'
    		    },
    		    editable: false,
    		    events: <%=alEvents %> 
    	    });

    	});
   		
                    		
</script>
<style type='text/css'>
    body {
    font-size: 14px;
    }
    #calendar {
    width: 450px;
    margin: 0 auto;
    }
</style>
<%
    UtilityFunctions uF = new UtilityFunctions();
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String strEmpID = (String) request.getAttribute("empId");
   // String strEmpID1 = (String) request.getAttribute("empId");
   
    String strProID = (String) request.getParameter("PROFILEID");
    String strSessionEmpID = (String) session.getAttribute("EMPID");
    String fromPage = (String) request.getAttribute("fromPage");
    /* System.out.println("fromPage==>"+fromPage);
    System.out.println("strEmpID==>"+strEmpID+"==>strEmpID1==>"+strEmpID1); */
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    ArrayList alDocuments = (ArrayList) request.getAttribute("alDocuments");
    List alSkills = (List) request.getAttribute("alSkills");
    List alHobbies = (List) request.getAttribute("alHobbies");
    List alPrevEmployment = (List) request.getAttribute("alPrevEmployment");
    List alLanguages = (List) request.getAttribute("alLanguages");
    List alEducation = (List) request.getAttribute("alEducation");
    List alFamilyMembers = (List) request.getAttribute("alFamilyMembers");
    
    boolean isFilledStatus = uF.parseToBoolean((String) request.getAttribute("isFilledStatus"));
    
    if (strEmpID != null) {
    	strProID = strEmpID;
    } else if (strProID != null) {
    } else if (strSessionEmpID != null) {
    	strProID = strSessionEmpID;
    }
    
    Map hm = (HashMap) request.getAttribute("myProfile");
    if (hm == null) {
    	hm = new HashMap();
    }
    String strImage = (String) hm.get("IMAGE");
    
    String strTitle = "";
    if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
    	strTitle = (String) hm.get("NAME") + "'s Profile";
    } else {
    	strTitle = "My Profile";
    
    }
    String docRetriveLocation = CF.getStrDocRetriveLocation();
    %>
    
<%
String rowStyle= "";
if(fromPage != null && fromPage.equalsIgnoreCase("LD")) { 
	rowStyle= "style=margin-top:10px";
%>
	
<%} %>
<div id="printDiv" class="leftbox reportWidth" <%=rowStyle %>">
	<div class="row">
		<div class="col-md-3">
			<div class="box box-primary">
                <div class="box-body box-profile">
                    <%if(docRetriveLocation==null) { %>
	                    <div class="profile-photo">
	                        <img class="profile-user-img img-responsive img-circle lazy"  id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + strImage%>" >
	                    </div>
                    <% }else{ %>
	                    <div class="profile-photo">
	                        <img class="profile-user-img img-responsive img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_TRAINER+"/"+IConstants.I_IMAGE+"/"+(String) request.getAttribute("empId")+"/"+IConstants.I_100x100+"/"+strImage%>">
	                    </div>
                    <% } %>
                    <h4 class="profile-username text-center"><%=uF.showData((String) hm.get("NAME"), "-")%></h4>
                    <p class="text-muted text-center text-no-margin">
                    <% if(alSkills!=null && alSkills.size()!=0) {
                        for(int i=0; i<alSkills.size(); i++) { 
                        if(i%5 == 0){%>
                        	<span class="label label-danger">
                        <% }else if(i%5 == 1){ %> 
                       		 <span class="label label-success">
                        <% }else if(i%5 == 2){ %> 
                        	 <span class="label label-info">
                        <% }else if(i%5 == 3){ %> 
                             <span class="label label-warning">
                        <% }else{ %>
                             <span class="label label-primary">
                        <% } %>
                            <strong><%=(i<alSkills.size()-1) ? uF.showData((String)((List)alSkills.get(i)).get(1),"") + ", " : uF.showData((String)((List)alSkills.get(i)).get(1),"")%></strong></span>
                        <%}
                    }%>
                    </p>
                    <p class="text-muted text-center text-no-margin"><span id="skillPrimary"></span></p>
					<%if(hm.get("CONTACT_MOB")!= null && !hm.get("CONTACT_MOB").equals("")){ %>
                        <p class="text-muted text-center text-no-margin"><i class="fa fa-phone" aria-hidden="true"></i> <%=uF.showData((String) hm.get("CONTACT_MOB"), "-")%></p>
                    <%} %>
                    
                    <%if(hm.get("EMAIL")!= null && !hm.get("EMAIL").equals("")){ %>
                        <p class="text-muted text-center text-no-margin"><i class="fa fa-envelope" aria-hidden="true"></i> <%=uF.showData((String) hm.get("EMAIL"), "-")%></p>
                    <%} %>
                </div>
                <!-- /.box-body -->
            </div>
		</div>
		<div class="col-md-9">
			<div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li class="active"><a href="#about" data-toggle="tab">About</a></li>
                    <li><a href="#timeline" data-toggle="tab">Timeline</a></li>
                    <li ><a href="#calender" data-toggle="tab" class="calenderLink">Schedules</a></li>
                    <div style="width:100%;" >
						<a href="javascript:void(0);" onclick="deleteTrainer('<%=strEmpID %>');" style="color: rgb(204, 0, 0);float:right;" class="del" ><i class="fa fa-trash"></i></a>
						<a href="javascript:void(0);" onclick="editTrainer('<%=strEmpID %>');" style="float:right;"><i class="fa fa-pencil-square-o"></i></a>
						<%-- <a href="javascript:void(0);" onclick= "editTrainer('<%=strEmpID %>');" style="float:right;"><i class="fa fa-pencil-square-o"></i></a> --%>
					</div> 
                </ul>
                <div class="tab-content">
                    <div class="active tab-pane" id="about">
						<div class="about-item">
	                      <h3 class="about-header">Skill Set</h3>
	                      <div class="about-body">
	                      	  <table style="width: 98%" class="table table_no_border autoWidth">
                                   <%
                                       if (alSkills != null && alSkills.size() != 0) {
                                       	for (int i = 0; i < alSkills.size(); i++) {
                                       		List alInner = (List) alSkills.get(i);
                                       %>
		                                    <tr>
		                                        <td><strong><%=alInner.get(1)%>:</strong></td>
		                                        <td><div id="star<%=i%>"></div></td>
		                                    </tr>
                                   <%
                                      		}
                                       } else {
                                       %>
	                                    <tr><td class="nodata msg"><span>No skill sets added</span></td></tr>
                                   <%  }%>
                              </table>
	                      </div>
	                    </div>
	                    <br/>
	                    <div class="about-item">
	                      <h3 class="about-header">Languages</h3>
	                      <div class="about-body">
	                      	  <table style="width: 98%"  class="table table-bordered autoWidth">
                                    <%
                                        if (alLanguages != null && alLanguages.size() != 0) {
                                        %>
                                    <tr class="center">
                                        <td width="150px"><strong>Language</strong></td>
                                        <td width="150px"><strong>Read</strong></td>
                                        <td width="150px"><strong>Write</strong></td>
                                        <td width="150px"><strong>Speak</strong></td>
                                    </tr>
                                    <%
                                        for (int i = 0; i < alLanguages.size(); i++) {
                                        		List alInner = (List) alLanguages.get(i);
                                        %>
                                    <tr>
                                        <td class="textblue"><strong><%=alInner.get(1)%></strong></td>
                                        <%
                                            if (((String) alInner.get(2)).equals("1")) {
                                            %>
                                        <td class="textblue yes"></td>
                                        <%
                                            } else {
                                            %>
                                        <td class="textblue no"></td>
                                        <%
                                            }
                                            %>
                                        <%
                                            if (((String) alInner.get(3)).equals("1")) {
                                            %>
                                        <td class="textblue yes"></td>
                                        <%
                                            } else {
                                            %>
                                        <td class="textblue no"></td>
                                        <%
                                            }
                                            %>
                                        <%
                                            if (((String) alInner.get(4)).equals("1")) {
                                            %>
                                        <td class="textblue yes"></td>
                                        <%
                                            } else {
                                            %>
                                        <td class="textblue no"></td>
                                        <%
                                            }
                                            %>
                                    </tr>
                                    <%
                                        }
                                        } else {
                                        %>
                                    <tr>
                                        <td class="nodata msg"><span>No languages added</span></td>
                                    </tr>
                                    <%
                                        }
                                        %>
                                </table>
	                      </div>
	                    </div>
	                    <br/>
						<div class="about-item">
	                      <h3 class="about-header">Hobbies</h3>
	                      <div class="about-body">
	                      	  <table style="width: 98%" class="table table_no_border autoWidth">
                                    <tr>
                                        <%
                                            if (alHobbies != null && alHobbies.size() != 0) {
                                            %>
                                        <%
                                            for (int i = 0; i < alHobbies.size(); i++) {
                                            		List alInner = (List) alHobbies.get(i);
                                            %>
                                        <td class="textblue"><strong><%=i < alHobbies.size() - 1 ? (String) alInner
                                            .get(1) + " ," : (String) alInner.get(1)%></strong></td>
                                        <%
                                            }
                                            %>
                                        <%
                                            } else {
                                            %>
                                    </tr>
                                    <tr>
                                        <td class="nodata msg"><span>No hobbies added</span></td>
                                    </tr>
                                    <%
                                        }
                                        %>
                                </table>
	                      </div>
	                    </div>
                    </div>
                    <div class="tab-pane" id="timeline">
                    	<ul class="timeline timeline-inverse">
                    		<%
                            if (alPrevEmployment.size() != 0) {
                            for (int i = 0; i < alPrevEmployment.size(); i++) {
                                	List<String> alInner = (List<String>) alPrevEmployment.get(i);
                            %>
                            	<li>
	                                <i class="fa fa-envelope bg-blue"></i>
	                                <div class="timeline-item">
	                                    <h3 class="timeline-header"><%=uF.showData(alInner.get(3),"")%></h3>
	                                    <div class="timeline-body">
		                                        <strong>Objective : </strong>
		                                        <%=uF.showData(alInner.get(4),"")%>
		                                        From
		                                        <%=uF.showData(alInner.get(0),"")%>
		                                        To
		                                        <%=uF.showData(alInner.get(1),"")%>
		                                        At
		                                        <%=uF.showData(alInner.get(2),"")%>
	                                    </div>
	                                </div>
	                            </li>
                             <%}
                              }else{%>
                             	 <li>
	                                <i class="fa fa-envelope bg-blue"></i>
	                                <div class="timeline-item">
	                                    <h3 class="timeline-header">No Previous Trainings </h3>
	                                    <div class="timeline-body">
		                                        No Previous Trainings 
	                                    </div>
	                                </div>
	                            </li>
                              	
                              <% } %>
                         </ul>
                    </div>
                    <div class="tab-pane" id="calender">
                    	<div id='calendarBox' style="width: 100%;"></div>
                    	
                    </div>
                </div>
             </div>
		</div>
	</div>
</div>
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
    function deleteTrainer(empId) {
    	//alert("deleteTrainer empId==>"+empId);
    	$("#divTrainerResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	if(confirm('Are you sure, you want to delete this Trainer?')) {
			$.ajax({
				url:'AddTrainer.action?operation=D&empId='+empId+'&trainerType=External',
				cache :false,
				success:function(result){
					$("#divTrainerResult").html(result);
				}		
			});
    	}
	}
   
    function editTrainer(empId) {
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({ 
    		type : 'POST',
    		url :'AddTrainer.action?operation=U&empId='+empId+'&mode=profile&type=type',
    		cache: true,
    		success: function(result){
    			//alert("addTrainer result1==>"+result);
    			$("#divResult").html(result);
       		}
    	});
    	 /* var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Edit Trainer');
		 if($(window).width() >= 900){
    		 $(".modal-dialog").width(900);
    	 }
		 $.ajax({
				type : 'POST',
	    		url :'AddTrainer.action?operation=U&empId='+empId+'&mode=profile&type=type',
	    		cache: true,
				success : function(data) {
					$(dialogEdit).html(data);
				}
		 });  */
	} 

</script>

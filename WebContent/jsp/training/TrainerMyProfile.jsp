<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<link rel='stylesheet' type='text/css'
    href='<%=request.getContextPath()%>/scripts/fullcalendar/fullcalendar.css' />
<link rel='stylesheet' type='text/css'
    href='<%=request.getContextPath()%>/scripts/fullcalendar/fullcalendar.print.css'
    media='print' />
<script type="text/javascript"
    src="<%=request.getContextPath()%>/scripts/fullcalendar/fullcalendar.min.js"> </script>
<% List alEvents = (List)request.getAttribute("alTrainerCalender"); %>
<script type="text/javascript">
    $(document).ready(function() {
        $('#calendar').fullCalendar({
	        header: {
	    	left: 'title',
	    	center: 'today prev,next',
	    	right: 'month,basicWeek,basicDay'
		    },
		    editable: false,
		    events: <%=alEvents %> 
        })
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
    String strUserType = (String) session
    		.getAttribute(IConstants.USERTYPE);
    String strEmpID = (String) request.getAttribute("EMPID");
    String strProID = (String) request.getParameter("PROFILEID");
    String strSessionEmpID = (String) session.getAttribute("EMPID");
    CommonFunctions CF = (CommonFunctions) session
    		.getAttribute(IConstants.CommonFunctions);
    ArrayList alDocuments = (ArrayList) request
    		.getAttribute("alDocuments");
    List alSkills = (List) request.getAttribute("alSkills");
    List alHobbies = (List) request.getAttribute("alHobbies");
    List alPrevEmployment = (List) request
    		.getAttribute("alPrevEmployment");
    List alLanguages = (List) request.getAttribute("alLanguages");
    List alEducation = (List) request.getAttribute("alEducation");
    List alFamilyMembers = (List) request
    		.getAttribute("alFamilyMembers");
    
    boolean isFilledStatus = uF.parseToBoolean((String) request
    		.getAttribute("isFilledStatus"));
    
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
    if (strUserType != null
    		&& !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
    	strTitle = (String) hm.get("NAME") + "'s Profile";
    } else {
    	strTitle = "My Profile";
    
    }
    %>
<jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=strTitle %>" name="title" />
</jsp:include>
<div id="printDiv" class="leftbox reportWidth">
    <div class="leftholder" style="width: 40%; border: solid 0px #ccc">
        <div class="tableblock"
            style="background: #efefef; padding: 5px; border: solid 1px #d4d4d4">
            <div class="trow"
                style="background: #fff; margin: 0px; border-right: solid 1px #ccc; width: 93%">
                <div style="float: left; padding: 5px; width: 100px">
                    <div
                        style="height: 82px; width: 84px; border: 1px solid #CCCCCC; float: left; margin: 2px 10px 0px 0px">
                        <img height="100" width="100" id="profilecontainerimg"
                            src="userImages/<%=strImage%>" />
                    </div>
                    <div style="text-align: center; float: left; width: 85px">
                        <a href="#?w=550" rel="popup_name" class="poplight">Edit Photo</a>
                    </div>
                </div>
                <div style="float: left; border: 0px #ccc solid">
                    <div>
                        <table>
                            <tr>
                                <td class="textblue" style="font-size: 12px; font-weight: bold"><%=uF.showData((String) hm.get("NAME"), "-")%></td>
                            </tr>
                            <%-- <tr>
                                <td>Trainer code: <%=uF.showData((String) hm.get("EMPCODE"), "-")%></td>
                                </tr> --%>
                            <tr>
                                <td>Primary Email: <%=uF.showData((String) hm.get("EMAIL"), "-")%></td>
                            </tr>
                        </table>
                    </div>
                    <div class="clr"></div>
                    <div class="">
                        <%
                            if (alSkills != null && alSkills.size() != 0) {
                            	for (int i = 0; i < alSkills.size(); i++) {
                            %>
                        <strong><%=(i < alSkills.size() - 1) ? ((List) alSkills
                            .get(i)).get(1) + ", " : ((List) alSkills.get(i))
                            .get(1)%></strong>
                        <%
                            }
                            }
                            %>
                    </div>
                    <div class="clr"></div>
                    <div style="border: solid 0px #ccc; float: left; width: 30%">
                        <div id="skillPrimary"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="clr"></div>
        <div class="trow">
            <div>
                <div class="fieldset">
                    <fieldset>
                        <legend>Skill Set</legend>
                        <%
                            if (strUserType != null
                            		&& !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
                            %>
                        <div class="edit_profile">
                            <a rel="popup_name" class="edit poplight"
                                href="AddTrainer.action?operation=U&mode=profile&empId=<s:property value="empId"/>&step=2&type=edit">Edit</a>
                            <!-- <a rel="popup_name" class="edit poplight" href="">Edit</a> -->
                        </div>
                        <%
                            }
                            %>
                        <div class="clr"></div>
                        <table style="width: 98%">
                            <%
                                if (alSkills != null && alSkills.size() != 0) {
                                	for (int i = 0; i < alSkills.size(); i++) {
                                		List alInner = (List) alSkills.get(i);
                                %>
                            <tr>
                                <td><strong><%=alInner.get(1)%>:</strong></td>
                                <%-- <td class="textblue"><strong><%=alInner.get(2)%>/10</strong></td> --%>
                                <td>
                                    <div id="star<%=i%>"></div>
                                </td>
                            </tr>
                            <%
                                }
                                } else {
                                %>
                            <tr>
                                <td class="nodata msg"><span>No skill sets added</span></td>
                            </tr>
                            <%
                                }
                                %>
                        </table>
                    </fieldset>
                </div>
            </div>
        </div>
        <div class="clr"></div>
        <div class="trow">
            <div>
                <div class="fieldset">
                    <fieldset>
                        <legend>Languages</legend>
                        <%
                            if (strUserType != null
                            		&& !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
                            %>
                        <div class="edit_profile">
                            <a rel="popup_name" class="edit poplight"
                                href="AddTrainer.action?operation=U&mode=profile&empId=<s:property value="empId"/>&step=2&type=edit">Edit</a>
                        </div>
                        <div class="clr"></div>
                        <%
                            }
                            %>
                        <table style="width: 98%">
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
                    </fieldset>
                </div>
            </div>
        </div>
        <div class="trow">
            <div>
                <div class="fieldset">
                    <fieldset>
                        <legend>Hobbies</legend>
                        <%
                            if (strUserType != null
                            		&& !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
                            %>
                        <div class="edit_profile">
                            <a rel="popup_name" class="edit poplight"
                                href="AddTrainer.action?operation=U&mode=profile&empId=<s:property value="empId"/>&step=2&type=edit">Edit</a>
                        </div>
                        <div class="clr"></div>
                        <%
                            }
                            %>
                        <table style="width: 98%">
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
                    </fieldset>
                </div>
            </div>
        </div>
    </div>
    <div class="rightholder" style="width: 59%; border: solid 0px #ccc">
        <div id='calendar' style="font-size: 8"></div>
        <div class="clr"></div>
        <div class="tmln_holder">
            <!-- Previous Employment -->
            <div
                style="float: left; border: solid 0px #ccc; width: 93%; margin: 0px 0px 0px 10px">
                <%
                    if (alPrevEmployment.size() != 0) {
                    %>
                <div class="lholder prev_empl">
                    <%
                        for (int i = 0; i < alPrevEmployment.size(); i++) {
                        
                        		if (i % 2 == 0) {
                        %>
                    <div class="lblock" style="min-width: 100%;">
                        <div class="lp"></div>
                        <div class="tm_container">
                            <p class="act_title">
                                <strong><%=((List) alPrevEmployment.get(i)).get(3)%></strong>
                            </p>
                            <p>
                                <strong>Objective : </strong>
                                <%=((List) alPrevEmployment.get(i)).get(4)%>
                            </p>
                            <p>
                                From
                                <%=((List) alPrevEmployment.get(i)).get(0)%>
                                To
                                <%=((List) alPrevEmployment.get(i)).get(1)%>
                            </p>
                            <p>
                                At
                                <%=((List) alPrevEmployment.get(i)).get(2)%>
                            </p>
                        </div>
                    </div>
                    <div class="clr"></div>
                    <%
                        }
                        	}
                        %>
                </div>
                <div class="rholder">
                    <%
                        for (int i = 0; i < alPrevEmployment.size(); i++) {
                        
                        		if (i % 2 == 1) {
                        %>
                    <div class="rblock" style="min-width: 100%;">
                        <div class="lp_r"></div>
                        <div class="tm_container_r">
                            <p class="act_title">
                                <strong><%=((List) alPrevEmployment.get(i)).get(3)%></strong>
                            </p>
                            <p>
                                <strong>Objective : </strong>
                                <%=((List) alPrevEmployment.get(i)).get(4)%>
                            </p>
                            <p>
                            <p>
                                From
                                <%=((List) alPrevEmployment.get(i)).get(0)%>
                                To
                                <%=((List) alPrevEmployment.get(i)).get(1)%>
                            </p>
                            <p>
                                At
                                <%=((List) alPrevEmployment.get(i)).get(2)%>
                            </p>
                        </div>
                    </div>
                    <div class="clr"></div>
                    <%
                        }
                        	}
                        %>
                </div>
                <%
                    } else {
                    %>
                <div class="lholder prev_empl">
                    <div class="lblock" style="min-width: 100%;">
                        <div class="lp"></div>
                        <div class="tm_container">
                            <p class="act_title">
                                <strong>No Previous Trainings </strong>
                            </p>
                        </div>
                    </div>
                    <div class="clr"></div>
                    <%
                        }
                        %>
                </div>
            </div>
        </div>
        <div class="clr"></div>
    </div>
    <div id="popup_name" class="popup_block">
        <s:form name="uploadImage" action="UploadImage.action"
            enctype="multipart/form-data" method="post">
            <s:hidden name="imageType" value="EMPLOYEE_IMAGE"></s:hidden>
            <%-- <input type="hidden" name="empId" value="<%=strProID%>" /> --%>
            <s:hidden name="empId"></s:hidden>
            <s:file name="empImage"></s:file>
            <s:submit value="Upload" cssClass="input_button"></s:submit>
        </s:form>
    </div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%
  UtilityFunctions uF = new UtilityFunctions();
  List<String> learningPlanList = (List<String>) request.getAttribute("learningPlanList");
  List<List<String>> stageList = (List<List<String>>) request.getAttribute("stageList");
  Map<String, String> hmWeekdays = (Map<String, String>) request.getAttribute("hmWeekdays");
  List<List<String>> feedbackQueList = (List<List<String>>) request.getAttribute("feedbackQueList");
  Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request.getAttribute("answertypeSub");
  
  %>
<style>
  .addgoaltoreview {
  background-color: #ECF3F8;
  border-bottom: 1px solid #DDDDDD;
  box-shadow: 0 17px 21px -6px #CCCCCC;
  display: inline-block;
  padding: 10px 0;
  position: relative;
  text-align: center;
  width: 99.8%;
  margin-top:10px;
  }
  .addgoaltoreview-arrow {
  border-color: #ecf3f8 transparent transparent transparent;
  border-style: solid;
  border-width: 10px;
  height: 0;
  width: 0;
  position: absolute;
  bottom: -19px;
  left: 50%;
  }
  .addgoaltoreview h3 {
  float: left;
  padding: 10px 20px;
  width: 100%;
  text-align: left;
  }
  .addgoaltoreview input {
  font-weight: 700;
  float: right;
  }
  #calendar {
  width: 450px;
  margin: 0 auto;
  }
  .fc-left>h2{
  font-size: 25px;
  }
  .fc-toolbar {
  padding: 0px;
  }
  .fc-left h2{
  font-size: 14px;
  font-weight: 600;
  }
  .fc-day-grid-container{
  height: auto !important;
  }
  .fc-day-number{
  font-size: 13px;
  }
  .fc-basic-view .fc-body .fc-row {
  min-height: 26px !important; 
  }
</style>
<!-- <SCRIPT type="text/javascript">
  // window.onload=function(){window.history.forward(); };
  //window.onload=function(){history.go(); };
  
  </SCRIPT> -->
<% 
  Map<String, List<String>> hmlPlanStageDetails = (Map<String, List<String>>) request.getAttribute("hmlPlanStageDetails");
  Map<String, List<String>> hmlPlanStageMonths = (Map<String, List<String>>) request.getAttribute("hmlPlanStageMonths");
  Map<String, List<String>> hmlTrainingDetails = (Map<String, List<String>>) request.getAttribute("hmlTrainingDetails");
  Map<String, List<String>> hmlTrainingMonths = (Map<String, List<String>>) request.getAttribute("hmlTrainingMonths");
  List alEmp = (List)request.getAttribute("reportListEmp"); 
  //List<String> monthList = (List<String>) request.getAttribute("monthList");	
  //System.out.println("monthList -----> " + monthList);
  //System.out.println("alEmp -----> " + alEmp);
  %>
<%-- <jsp:include page="../common/SubHeader.jsp">
  <jsp:param value="Review Summary" name="title" />
  </jsp:include> --%>
  <script type="text/javascript" src="js/moment.js"></script>
<link href='scripts/calender/fullcalendar.min.css' rel='stylesheet' />
<link href='scripts/calender/fullcalendar.print.css' rel='stylesheet' media='print' />
<script src='scripts/calender/fullcalendar.min.js'></script> 
<div>
  <h4>
    <%=learningPlanList.get(1)%>
  </h4>
  <br/>
  <table class="table-striped" width="100%">
    <tr>
      <th width="18%" align="right">Objective</th>
      <td><%=learningPlanList.get(2)%></td>
    </tr>
    <tr>
      <th valign="top" align="right">Aligned with</th>
      <td colspan="1"><%=learningPlanList.get(3)%></td>
    </tr>
    <tr>
      <th valign="top" align="right">Certificate</th>
      <td><%=learningPlanList.get(6)%></td>
    </tr>
    <tr>
      <th align="right">Effective Date</th>
      <td><%=learningPlanList.get(8)%></td>
    </tr>
    <tr>
      <th align="right">Due Date</th>
      <td><%=learningPlanList.get(9)%></td>
    </tr>
    <tr>
      <th align="right">Attribute</th>
      <td><%=learningPlanList.get(5)%></td>
    </tr>
    <tr>
      <th align="right">Skills</th>
      <td><%=learningPlanList.get(7)%></td>
    </tr>
    <tr>
      <th align="right">Assignee</th>
      <td><%=learningPlanList.get(4)%></td>
    </tr>
    <tr>
      <th align="right">Type</th>
      <td><%=learningPlanList.get(10)%></td>
    </tr>
  </table>
  <br/><br/>
  <s:form action="learningPlan" id="formID" method="POST" theme="simple">
    <%
      int sectionCnt=0;
      //System.out.println("stageList ===> " + stageList);
      for(int i =0; stageList!= null && !stageList.isEmpty() && i < stageList.size(); i++) {
      	List<String> innerList = stageList.get(i);
      	%>
    <script type="text/javascript">
      $(function() {
      	
      	var date = new Date();
      	var d = date.getDate();
      	var m = date.getMonth();
      	var y = date.getFullYear();
      <% 
        //System.out.println("hmlPlanStageDetails -----> " + hmlPlanStageDetails);
        List<String> stageDetailsList = new ArrayList<String>();
        List<String> monthList = new ArrayList<String>();
        
        if(innerList.get(3) != null && innerList.get(3).equals("Training")) {
        	stageDetailsList = hmlTrainingDetails.get(innerList.get(1));
        	monthList = hmlTrainingMonths.get(innerList.get(1));
        }else {
        	stageDetailsList = hmlPlanStageDetails.get(innerList.get(0));
        	monthList = hmlPlanStageMonths.get(innerList.get(0));
        }
        
        //System.out.println("innerList.get(1) ---> "+ innerList.get(1) +"monthList -----> " + monthList);
        for(int a=0; monthList != null && a < monthList.size(); a++) { 
        
        %>
      	$('#calendar<%=innerList.get(0)%>_<%=a %>').fullCalendar({
      		/* header: {
      			left: 'prev,next today',
      			center: 'title',
      			right: 'month,basicWeek,basicDay'
      		}, */editable: false,
      		events: <%=stageDetailsList %>,
      		month: <%=monthList.get(a) %>
      	    /* theme: true */
      	});
      	
      <% } %>
      
      }); 
    </script>
    <% if(innerList.get(3) != null && !innerList.get(3).equals("Training")) { %>
    <div class="addgoaltoreview">
      <div style="float: left;padding-left: 20px;float: left;">
        <h4><%=i+1 %>)&nbsp;<%=uF.showData(innerList.get(2), "")%> (<%=innerList.get(3) %>)</h4>
        <%-- <div style="padding: 0px 20px; font-size: 10px; text-align: justify; line-height: 12px;">
          <%=uF.showData((String)hmLevelDetails.get(str+"_SDESC"), "")%>				
          </div>
          <div style="padding: 0px 20px; font-size: 10px; text-align: justify; line-height: 12px;">
          <%=uF.showData((String)hmLevelDetails.get(str+"_LDESC"), "")%> 				
          </div> --%>
      </div>
    </div>
    <div class="row" style="width: 80%; margin: 10px 50px 10px 50px; width:96%;">
      <div class="col-lg-6 col-md-6 col-sm-12">
        <table border="0" class="table table-bordered" style="width: 100%;">
          <%-- <tr>
            <td class="txtlabel" align="right">Learning Plan Type:</td>
            <td colspan="3"><%=innerList.get(3) %>
            </tr> --%>
          <tr>
            <td class="txtlabel" align="right">Start Date:</td>
            <td><%=innerList.get(4) %></td>
            <td class="txtlabel" align="right">End Date:</td>
            <td><%=innerList.get(5) %></td>
          </tr>
          <tr>
            <%-- <td class="txtlabel" align="right">
              <input type="checkbox" name="everyday" id="everyday<%=innerList.get(3) %>_<%=innerList.get(1) %>" onclick="checkAllDays('<%=innerList.get(3) %>_<%=innerList.get(1) %>');" value="everyday" disabled="disabled">Everyday &nbsp;
              </td> --%>
            <td class="txtlabel" colspan="4">&nbsp;&nbsp;
              <input type="checkbox" name="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" id="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" onclick="uncheckAllDays('<%=innerList.get(3) %>_<%=innerList.get(1) %>');" value="Mon" <%=hmWeekdays.get(innerList.get(0)+"_MON") != null ? hmWeekdays.get(innerList.get(0)+"_MON") : "" %> disabled="disabled">Mon
              <input type="checkbox" name="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" id="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" onclick="uncheckAllDays('<%=innerList.get(3) %>_<%=innerList.get(1) %>');" value="Tue" <%=hmWeekdays.get(innerList.get(0)+"_TUE") != null ? hmWeekdays.get(innerList.get(0)+"_TUE") : "" %> disabled="disabled">Tue
              <input type="checkbox" name="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" id="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" onclick="uncheckAllDays('<%=innerList.get(3) %>_<%=innerList.get(1) %>');" value="Wed" <%=hmWeekdays.get(innerList.get(0)+"_WED") != null ? hmWeekdays.get(innerList.get(0)+"_WED") : "" %> disabled="disabled">Wed
              <input type="checkbox" name="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" id="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" onclick="uncheckAllDays('<%=innerList.get(3) %>_<%=innerList.get(1) %>');" value="Thu" <%=hmWeekdays.get(innerList.get(0)+"_THU") != null ? hmWeekdays.get(innerList.get(0)+"_THU") : "" %> disabled="disabled">Thu
              <input type="checkbox" name="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" id="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" onclick="uncheckAllDays('<%=innerList.get(3) %>_<%=innerList.get(1) %>');" value="Fri" <%=hmWeekdays.get(innerList.get(0)+"_FRI") != null ? hmWeekdays.get(innerList.get(0)+"_FRI") : "" %> disabled="disabled">Fri
              <input type="checkbox" name="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" id="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" onclick="uncheckAllDays('<%=innerList.get(3) %>_<%=innerList.get(1) %>');" value="Sat" <%=hmWeekdays.get(innerList.get(0)+"_SAT") != null ? hmWeekdays.get(innerList.get(0)+"_SAT") : "" %> disabled="disabled">Sat
              <input type="checkbox" name="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" id="weekdays<%=innerList.get(3) %>_<%=innerList.get(1) %>" onclick="uncheckAllDays('<%=innerList.get(3) %>_<%=innerList.get(1) %>');" value="Sun" <%=hmWeekdays.get(innerList.get(0)+"_SUN") != null ? hmWeekdays.get(innerList.get(0)+"_SUN") : "" %> disabled="disabled">Sun
            </td>
          </tr>
          <tr>
            <td class="txtlabel" align="right">Start Time:</td>
            <td><%=innerList.get(6) %></td>
            <td class="txtlabel" align="right">End Time:</td>
            <td><%=innerList.get(7) %></td>
          </tr>
        </table>
      </div>
      <div class="col-lg-6 col-md-6 col-sm-12">
        <%  
          int calCnt = 0;
          //System.out.println("innerList.get(0) 11---> "+ innerList.get(0) +"monthList 11-----> " + monthList);
          for(int a=0; monthList != null && a < monthList.size(); a++) {
          
          %>
        <%if(calCnt%2 == 0 || calCnt == 0) { %>
        <div class="row">
          <% } %>
          <div style=" <%if(calCnt%2 != 0 || calCnt == 1) { %> <% } %> " class="col-lg-6 col-md-6 col-sm-12">
            <div id="calendar<%=innerList.get(0)%>_<%=a %>" style=" width: 100%; font-size: 11px;"></div>
          </div>
          <%if(calCnt%2 != 0 || calCnt == 1) { %>
        </div>
        <br/>
        <% } %>
        <%if(calCnt%2 == 0 && calCnt > 0) { %>
        <% } %>
        <% 
          calCnt++;
          } %>
      </div>
      <%-- <% for(int a=0; monthList != null && a < monthList.size(); a++) {%>
        <div style="width: 50%; float: left;">
        <div id='calendar<%=a %>' style="font-size: 8"></div>
        </div>
        <% } %> --%>
    </div>
    <% } else { %>
    <%
      Map<String, List<String>> hmTrainingList = (Map<String, List<String>>) request.getAttribute("hmTrainingList");
      Map<String, List<String>> hmTrainingDataList = (Map<String, List<String>>) request.getAttribute("hmTrainingDataList");
      //String scheduleType = (String) request.getAttribute("scheduleTypeValue");
      //String trainingSchedulePeriod = (String) request.getAttribute("trainingSchedulePeriod");
      //String planId = (String) request.getAttribute("planId");
      Map<String, List<List<String>>> hmSessionData = (Map<String, List<List<String>>>) request.getAttribute("hmSessionData");
      //System.out.println("allList.get(1) ---> "+allList.get(1));
      
      List<String> trainingList1 = hmTrainingList.get(innerList.get(1));
      List<String> trainingDataList = hmTrainingDataList.get(innerList.get(1));
      List<List<String>> alSessionData = hmSessionData.get(innerList.get(1));
      %>
    <div class="addgoaltoreview">
      <div style="float: left;padding-left: 20px; float: left;">
        <h4><%=i+1 %>)&nbsp;<%=uF.showData(innerList.get(2), "")%> (<%=innerList.get(3) != null && innerList.get(3).equals("Training") ? "Classroom Training" : "" %>)</h4>
      </div>
    </div>
    <div class="row" style=" margin: 10px 50px 10px 50px;">
      <div class="col-lg-6 col-md-6 col-sm-12">
        <table border="0" class="table table-bordered" style="width: 100%;">
          <tr>
            <td class="txtlabel" style="vertical-align: top; text-align: right; width: 70px;">Periodic:</td>
            <td nowrap="nowrap">
              <div style="position:reletive;">
                <span style="float: left; margin-right: 20px">
                <%if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("1")) { %>
                One Time
                <% } else  if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("2")) { %>
                Weekly
                <% } else  if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("3")) { %>
                Monthly
                <% } %>
                </span>                           
              </div>
            </td>
            <td colspan="2">
              <div style="position:reletive;">
                <span id="weekly" style="<%if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("2")) { %>display: block; <% } else { %> display: none; <% } %> float: left;">Day: <%=(String)request.getAttribute("weekdayValue") %>
                <%-- <s:select theme="simple" name="weekday" id="weekday" headerKey="" cssStyle="width:115px;" headerValue="Select Day" value="weekdayValue"
                  list="#{'Monday':'Monday','Tuesday':'Tuesday', 'Wednesday':'Wednesday','Thursday':'Thursday','Friday':'Friday','Saturday':'Saturday','Sunday':'Sunday'}" /> --%>
                </span>
                <span id="monthly" style="<%if(trainingDataList.get(5) != null && trainingDataList.get(5).equals("3")) { %>display: block; <% } else { %> display: none; <% } %> float: left;"> <%=(String)request.getAttribute("weekdayValue") %>
                <%-- <s:select theme="simple" name="day" id="day" headerKey="" cssStyle="width:75px;" headerValue="Day" value="dayValue"
                  list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
                  '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
                  '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" /> --%>
                </span>
              </div>
            </td>
          </tr>
          <tr>
            <td class="txtlabel" style="text-align: right; width: 70px;">Start Date:</td>
            <td><%=trainingDataList.get(1) %> </td>
            <td class="txtlabel" style="text-align: right; width: 70px;">End Date:</td>
            <td><%=trainingDataList.get(2) %> </td>
          </tr>
          <tr>
            <td style="text-align: right; width: 75px;" class="txtlabel">Day Schedule</td>
            <td id="dayScheduleTD" style="display: table-cell;" colspan="3">
              <% if(trainingDataList.get(7) != null && trainingDataList.get(7).equals("1")) { %>
              Daily
              <% } else if(trainingDataList.get(7) != null && trainingDataList.get(7).equals("2")) { %>
              Occasionally
              <% } %>
            </td>
          </tr>
          <%
            if (alSessionData != null && alSessionData.size() != 0) {
            	Map<String, String> hmWeekdays1 = (Map<String, String>) request.getAttribute("hmWeekdays1");	
            %>
          <tr id="weekDaysTR" <%if (trainingDataList.get(7) != null && trainingDataList.get(7).equals("1") && trainingDataList.get(5) != null && trainingDataList.get(5).equals("1")) { %>
            style="display: table-row;" <%} else { %> style="display: none;"
            <%}%>>
            <%-- <td style="text-align: right;" class="txtlabel">
              <input type="hidden" name="hideScheduleType" id="hideScheduleType" value="<%=trainingDataList.get(7) %>"/>
              &nbsp;</td> --%>
            <td class="txtlabel" align="center" colspan="4">
              <input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" value="Mon" <%=hmWeekdays1.get(trainingList1.get(0)+"_MON") != null ? hmWeekdays1.get(trainingList1.get(0)+"_MON") : "" %> disabled="disabled">Mon
              <input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" value="Tue" <%=hmWeekdays1.get(trainingList1.get(0)+"_TUE") != null ? hmWeekdays1.get(trainingList1.get(0)+"_TUE") : "" %> disabled="disabled">Tue
              <input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" value="Wed" <%=hmWeekdays1.get(trainingList1.get(0)+"_WED") != null ? hmWeekdays1.get(trainingList1.get(0)+"_WED") : "" %> disabled="disabled">Wed
              <input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" value="Thu" <%=hmWeekdays1.get(trainingList1.get(0)+"_THU") != null ? hmWeekdays1.get(trainingList1.get(0)+"_THU") : "" %> disabled="disabled">Thu
              <input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" value="Fri" <%=hmWeekdays1.get(trainingList1.get(0)+"_FRI") != null ? hmWeekdays1.get(trainingList1.get(0)+"_FRI") : "" %> disabled="disabled">Fri
              <input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" value="Sat" <%=hmWeekdays1.get(trainingList1.get(0)+"_SAT") != null ? hmWeekdays1.get(trainingList1.get(0)+"_SAT") : "" %> disabled="disabled">Sat
              <input type="checkbox" name="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" id="weekdays<%=trainingList1.get(2) %>_<%=trainingList1.get(0) %>" value="Sun" <%=hmWeekdays1.get(trainingList1.get(0)+"_SUN") != null ? hmWeekdays1.get(trainingList1.get(0)+"_SUN") : "" %> disabled="disabled">Sun
            </td>
          </tr>
          <%				for (int j = 0; alSessionData != null && j < alSessionData.size(); j++) {
            List<String> alInner = (List<String>) alSessionData.get(j);
            //System.out.println("alInner.get(4) ===> "+alInner.get(4));
            %>
          <tr id="trainingSchedulePeriodTR<%=i%>"
            <% 
              //System.out.println("alInner.get(0) ---> "+alInner.get(0) + "   alInner.get(4) ---> "+alInner.get(4));
              if (alInner.get(0) != null && alInner.get(0).equals("1") && alInner.get(4) != null && alInner.get(4).equals("2")) { 
              %>
            style="display: table-row;" <%} else { %> style="display: none;"
            <%}%>>
            <td class="txtlabel" style="text-align: right; width: 75px;"> Day <%=j+1 %>:</td>
            <td colspan="3"> <%=alInner.get(1)%> </td>
          </tr>
          <tr id="startTimeTR<%=j%>">
            <td class="txtlabel" style="text-align: right;width: 70px;">Start Time:</td>
            <td><%=alInner.get(2)%></td>
            <td class="txtlabel" style="text-align: right; width: 70px;">End Time:</td>
            <td><%=alInner.get(3)%></td>
          </tr>
          <% } } %>
        </table>
      </div>
      <div class="col-lg-6 col-md-6 col-sm-12">
        <%  
          int calCnt1 = 0;
          for(int a=0; monthList != null && a < monthList.size(); a++) {
          
          %>
        <%if(calCnt1%2 == 0 || calCnt1 == 0) { %>
        <div class="row">
          <% } %>
          <div class="col-lg-6" style=" <%if(calCnt1%2 != 0 || calCnt1 == 1) { %> <% } %>">
            <div id="calendar<%=innerList.get(0)%>_<%=a %>" style=" width: 100%; font-size: 11px;"></div>
          </div>
          <%if(calCnt1%2 != 0 || calCnt1 == 1) { %>
        </div>
        <br/>
        <% } %>
        <%if(calCnt1%2 == 0 && calCnt1 > 0) { %>
        <% } %>
        <% 
          calCnt1++;
          } %>
      </div>
    </div>
    <% } %>
    <hr>
    <%
      } 
      //}	
      %>
    <div class="addgoaltoreview">
      <div style="float: left;padding-left: 20px; float: left;">
        <h4>Feedback </h4>
      </div>
    </div>
    <% for(int ii=0; feedbackQueList != null && !feedbackQueList.isEmpty() && ii< feedbackQueList.size();  ii++) {
      //List<String> innerlist = (List<String>) alQuestion.get(i);
      List<String> questioninnerList = feedbackQueList.get(ii);
      //System.out.println("innerlist ===> "+ innerlist);
      //System.out.println("questioninnerList ===> "+ questioninnerList);
      %>
    <div style="margin: 10px 50px 10px 50px; border-bottom: 1px solid #000;">
      <ul>
        <li><b><%=(ii + 1)%>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> </b></li>
        <li>
          <ul style="margin: 10px 10px 10px 30px">
            <li>
              <%
                if (uF.parseToInt(questioninnerList.get(8)) == 1) {
                %>
              <div>
                a) <input type="checkbox" disabled="disabled" value="a" name="correct"/><%=questioninnerList.get(2)%><br/> 
                b) <input type="checkbox" disabled="disabled" name="correct" value="b"/><%=questioninnerList.get(3)%><br/>
                c) <input type="checkbox" disabled="disabled" value="c" name="correct"/><%=questioninnerList.get(4)%><br/> 
                d) <input type="checkbox" disabled="disabled" name="correct" value="d" /><%=questioninnerList.get(5)%><br/>
                <textarea rows="5" cols="50" readonly="readonly" style="width:100%" name="<%=questioninnerList.get(0)%>"></textarea>
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
                			//answerTypeList.add("2");
                %>
              <div>
                a) <input type="checkbox" disabled="disabled" value="a" name="correct"/><%=questioninnerList.get(2)%><br />
                b) <input type="checkbox" disabled="disabled" name="correct"value="b"/><%=questioninnerList.get(3)%><br />
                c) <input type="checkbox" disabled="disabled" value="c" name="correct"/><%=questioninnerList.get(4)%><br />
                d) <input type="checkbox" disabled="disabled" name="correct" value="d" /><%=questioninnerList.get(5)%>
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
                			//answerTypeList.add("3");
                %>
              <div>
                <input type="hidden" name="marks<%=questioninnerList.get(0)%>" id="marks<%=ii%>" style="width: 31px;"/>
               
                <br/>
                <div id="slidemarksscore<%=ii%>" style="width:25%; text-align:center;"></div>
                <div id="sliderscore<%=questioninnerList.get(8)+"_"+ii%>" style="width:25%; float: left;"></div>
                <div id="marksscore<%=questioninnerList.get(8)+"_"+ii%>" style="width:25%;">0 <span style="float:right;">100</span></div>
              	 <script>
              		$("#sliderscore<%=questioninnerList.get(8)+"_"+ii%>").slider({
                      	value : 50,
                      	min : 0,
                      	max : 100,
                      	step : 2,
                      	disabled:true,
                      	slide : function(event, ui) {
                      		$("#marks"+<%=ii%>).val(ui.value);
                      		$("#slidemarksscore"+<%=ii%>).html(ui.value);
                      	}
                      });
                      $("#marks<%=ii%>").val($("#sliderscore<%=questioninnerList.get(8)+"_"+ii%>").slider("value"));
                      $("#slidemarksscore<%=ii%>").html($("#sliderscore<%=questioninnerList.get(8)+"_"+ii%>").slider("value"));
                </script>
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
                			//answerTypeList.add("4");
                			List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
                %>
              <div>
                <%
                  for (int j = 0; j < outer.size(); j++) {
                  				List<String> inner = outer.get(j);
                  %>
                <input type="radio" disabled="disabled" name="<%=questioninnerList.get(0)%>" value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
                <%
                  }
                  %>
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
                			//answerTypeList.add("5");
                			List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
                %>
              <div>
                <%
                  for (int j = 0; j < outer.size(); j++) {
                  				List<String> inner = outer.get(j);
                  %>
                <input type="radio" disabled="disabled" name="<%=questioninnerList.get(0)%>" value="<%=inner.get(0)%>" /><%=inner.get(1)%><br/>
                <%
                  }
                  %>
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
                			//answerTypeList.add("6");
                			List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
                %>
              <div>
                <%
                  for (int j = 0; j < outer.size(); j++) {
                  				List<String> inner = outer.get(j);
                  %>
                <input type="radio" disabled="disabled" name="<%=questioninnerList.get(0)%>" value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
                <%
                  }
                  %>
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
                			//answerTypeList.add("7");
                %>
              <div>
                <strong>Ans:</strong>
                <input type="hidden" name="outofmarks<%=questioninnerList.get(0)%>" id="outofmarks<%=ii%>" value="100" />
                <input type="hidden" name="marks<%=questioninnerList.get(0)%>" id="marks<%=ii%>" style="width: 31px;"/>
               
                <br/>
                <div id="slidemarkssingleopen<%=ii%>" style="width:25%; text-align:center;"></div>
                <div id="slidersingleopen<%=questioninnerList.get(8)+"_"+ii%>" style="width:25%; float: left;"></div>
                <div id="markssingleopen<%=questioninnerList.get(8)+"_"+ii%>" style="width:25%;">0 <span style="float:right;">100</span></div>
            	 <script>
            		 $("#slidersingleopen<%=questioninnerList.get(8)+"_"+ii%>").slider({
                       	value : 0,
                       	min : 0,
                       	max : 100,
                       	step : 2,
                       	disabled:true,
                       	slide : function(event, ui) {
                       		$("#marks"+<%=ii%>).val(ui.value);
                       		$("#slidemarkssingleopen"+<%=ii%>).html(ui.value);
                       	}
                       });
            		 $("#marks<%=ii%>").val($("#slidersingleopen<%=questioninnerList.get(8)+"_"+ii%>").slider("value"));
                     $("#slidemarkssingleopen<%=ii%>").html($("#slidersingleopen<%=questioninnerList.get(8)+"_"+ii%>").slider("value"));
                </script>
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
                			//answerTypeList.add("8");
                %>
              <div>
                a) <input type="radio" value="a" disabled="disabled" name="correct<%=questioninnerList.get(0)%>"/><%=questioninnerList.get(2)%><br />
                b) <input type="radio" disabled="disabled" name="correct<%=questioninnerList.get(0)%>" value="b"/><%=questioninnerList.get(3)%><br />
                c) <input type="radio" disabled="disabled" value="c" name="correct<%=questioninnerList.get(0)%>"/><%=questioninnerList.get(4)%><br />
                d) <input type="radio" disabled="disabled" name="correct<%=questioninnerList.get(0)%>" value="d" /><%=questioninnerList.get(5)%><br />
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
                			//answerTypeList.add("9");
                %>
              <div>
                a) <input type="checkbox" disabled="disabled" value="a" name="correct<%=questioninnerList.get(0)%>"/><%=questioninnerList.get(2)%><br />
                b) <input type="checkbox" disabled="disabled" name="correct<%=questioninnerList.get(0)%>" value="b"/><%=questioninnerList.get(3)%><br />
                c) <input type="checkbox" disabled="disabled" value="c" name="correct<%=questioninnerList.get(0)%>"/><%=questioninnerList.get(4)%><br />
                d) <input type="checkbox" disabled="disabled" name="correct<%=questioninnerList.get(0)%>" value="d" /><%=questioninnerList.get(5)%><br />
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
                			//answerTypeList.add("10");
                %> 
              <div>
                <div style="float: left; margin: 30px 10px 0px 0px;">a)</div>
                <div>
                  <textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="a<%=questioninnerList.get(0)%>"></textarea>
                  <br />
                </div>
                <div style="float: left; margin: 30px 10px 0px 0px;">b)</div>
                <div>
                  <textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="b<%=questioninnerList.get(0)%>"></textarea>
                  <br />
                </div>
                <div style="float: left; margin: 30px 10px 0px 0px;">c)</div>
                <div>
                  <textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="c<%=questioninnerList.get(0)%>"></textarea>
                  <br />
                </div>
                <div style="float: left; margin: 30px 10px 0px 0px;">d)</div>
                <div>
                  <textarea readonly="readonly" rows="5" cols="50" style="width:100%" name="d<%=questioninnerList.get(0)%>"></textarea>
                  <br />
                </div>
                <input type="hidden" name="outofmarks<%=questioninnerList.get(0)%>" id="outofmarks<%=ii%>" value="100" />
                <input type="hidden" name="marks<%=questioninnerList.get(0)%>" id="marks<%=ii%>" style="width: 31px;"/>
                
                <br/>
                <div id="slidemarksmultipleopen<%=ii%>" style="width:25%; text-align:center;"></div>
                <div id="slidermultipleopen<%=questioninnerList.get(8)+"_"+ii%>" style="width:25%; float: left;"></div>
                <div id="marksmultipleopen<%=questioninnerList.get(8)+"_"+ii%>" style="width:25%;">0 <span style="float:right;">100</span></div>
              	<script>
              		$("#slidermultipleopen<%=questioninnerList.get(8)+"_"+ii%>").slider({
                      	value : 0,
                      	min : 0,
                      	max : 100,
                      	step : 2,
                      	disabled:true,
                      	slide : function(event, ui) {
                      		$("#marks"+<%=ii%>).val(ui.value);
                      		$("#slidemarksmultipleopen"+<%=ii%>).html(ui.value);
                      	}
                     });
              		$("#marks<%=ii%>").val($("#slidermultipleopen<%=questioninnerList.get(8)+"_"+ii%>").slider("value"));
                    $("#slidemarksmultipleopen<%=ii%>").html($("#slidermultipleopen<%=questioninnerList.get(8)+"_"+ii%>").slider("value"));
                </script>
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
                			//answerTypeList.add("11");
                %>
              <div id="starPrimary<%=questioninnerList.get(0)%>"></div>
              <input
                type="hidden" id="gradewithrating<%=questioninnerList.get(0)%>"
                value="3"
                name="gradewithrating<%=questioninnerList.get(0)%>"/> <script
                type="text/javascript">
               
                	$('#starPrimary<%=questioninnerList.get(0)%>').raty({
                		readOnly: true,
                		start: 3,
                		half: true,
                		targetType: 'number',
                		click: function(score, evt) {
                			$('#gradewithrating<%=questioninnerList.get(0)%>').val(score);
                			}
                	});
                	
              </script>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%
                } else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
                //answerTypeList.add("12");
                 %>
              <div>
                <strong>Ans:</strong>
                <textarea rows="5" cols="50" readonly="readonly" style="width:100%" name="anscomment<%=questioninnerList.get(0)%>"></textarea>
              </div>
              <div><br/><b>Comment:</b><br/><textarea rows="3" cols="50" readonly="readonly" style="width:70%" name="anscomment<%=questioninnerList.get(0)%>"></textarea></div>
              <%}%>
            </li>
          </ul>
        </li>
      </ul>
    </div>
    <hr>
    <%
      } 
      //}	
      %>
  </s:form>
</div>
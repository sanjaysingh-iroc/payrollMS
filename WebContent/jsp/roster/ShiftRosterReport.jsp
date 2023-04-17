<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@page import=" com.konnect.jpms.roster.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
   
<script type="text/javascript">
   $(document123).ready123(function() {
  
         $('a.poplight[href^=#]').click(function() {
            var popID = $(this).attr('rel'); //Get Popup Name
            var popURL = $(this).attr('href'); //Get Popup href to define size

            //Pull Query & Variables from href URL
            var query= popURL.split('?'); 
            var dim= query[1].split('&');
            var popWidth = dim[0].split('=')[1]; //Gets the first query string value
  
            //Fade in the Popup and add close button
            $('#' + popID).fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="<%=request.getContextPath()%>/images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

            //Define margin for center alignment (vertical   horizontal) - we add 80px to the height/width to accomodate for the padding  and border width defined in the css
            var popMargTop = ($('#' + popID).height() + 80) / 2;
            var popMargLeft = ($('#' + popID).width() + 80) / 2;

            //Apply Margin to Popup
            $('#' + popID).css({
                'margin-top' : -popMargTop,
                'margin-left' : -popMargLeft
            });

            //Fade in Background
            $('body').append('<div id="fade"></div>'); //Add the fade layer to bottom of the body tag.
            $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn(); //Fade in the fade layer - .css({'filter' : 'alpha(opacity=80)'}) is used to fix the IE Bug on fading transparencies

            return false;
        });

        //Close Popups and Fade Layer
        $('a.close, #fade').live('click', function() { //When clicking on the close or fade layer...
            $('#fade , .popup_block').fadeOut(function() {
                $('#fade, a.close').remove();  //fade them both out
            });
            return false;
        });

    });


</script>

<script src="jquery-1.4.2.js"></script>
  
<script>
        function divScroll()
        {
            var pvt = document.getElementById('pivot');
            var sdates = document.getElementById('scrolldates');
			var semp = document.getElementById('scrollemp');
            semp.scrollTop = pvt.scrollTop;
			sdates.scrollLeft = pvt.scrollLeft;
        }

      
 function displayBlock(id){
	 document.getElementById(id).style.display= 'block';
 }
 
 function hideBlock(id){
	 document.getElementById(id).style.display= 'none';
 }

</script>

<style>
.reportHeading {
    background-color: #d8d8d8;
	
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 10px;
    font-weight: bold;
}

img
{
  border:none;
}

.prevlink a
{
  float:right;
}


.day
{
  text-align:center;
  width:35px;
  _width:35px;
  float:left;
  border:#fff solid 1px;
}

.date
{
   text-align:center;
   width:35px;
   _width:34px;
   float:left;
   border:#fff solid 1px;
}

.inout
{
  width:35px; 
  float:left;
  border:#fff solid 1px;
  _height:21px;
  
}

.empname
{
 border:#fff solid 1px;
 background:#d8d8d8;
 float:left;
 overflow:hidden;
 width:180px;
 padding:0px 3px 0px 3px;
 line-height:19px;
 height:auto;
 
}



.block { 
float:left;
position:absolute; 
background-color:#abc; 
/*left:180px;*/
top:0px;
width:1459px; 
height:auto;
margin:0px; 

z-index:10;
overflow:hidden;
}

.block2 { 
float:left;

background-color:#fff; 
left:0px;
top:0px;
width:180px; 
height:auto;

margin:0px; 

z-index:100;
border-top:#fff solid 1px; 
overflow:hidden;
}

.block_dates { 
float:left;
position:absolute; 
background-color:#fff; 
left:0px;
top:0px;
width:1460px; 
height:auto;
margin:0px; 

z-index:10;
border:solid 0px #f00;
}

.next { width:32px; float:right; border:#666666 solid 1px;display:block;}

.posfix { left:20%; top:35% ; width:630px; }

.posfix h2 { color:#fff;margin:5px 0px 10px 0px}

#mask { width:100%; border:#fff solid 1px; height:600px; overflow:hidden; position:relative; float:left;}


  
  #pivot {width:745px;width:81%;  border:solid 0px #ff0; position:absolute; height:595px;overflow:scroll;float:left;margin:0px 0px 0px 180px;left:0px; }
  
.weekly_width
{
   width: 730px;
}

.biweekly_width
{
  width: 1563px;
}

.monthly_width
{
/*   width: 3225px; */
width:1155px;
}

.fortnightly_width
{
  width: 1560px;
}
</style>


<%

	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	
	List alDay = (List) request.getAttribute("alDay");
	List alDate = (List) request.getAttribute("alDate");
	List alDayF = (List) request.getAttribute("alDayF");
	List alDateF = (List) request.getAttribute("alDateF");
	
	List alEmpId = (List) request.getAttribute("alEmpId");
	Map hmList = (Map) request.getAttribute("hmList");
	String paycycleDuration = (String)request.getAttribute("paycycleDuration");
	
	Map hmServicesWorkrdFor = (Map) request.getAttribute("hmServicesWorkrdFor");  
	Map hmServices = (Map) request.getAttribute("hmServices");
	
	Map hmHolidayDates = (Map) request.getAttribute("hmHolidayDates");
	Map hmHolidays = (Map) request.getAttribute("hmHolidays");
	Map hmWLocation = (Map) request.getAttribute("hmWLocation");
	Map hmWeekEnds = (Map) request.getAttribute("hmWeekEnds");
	Map hmLeavesMap = (Map) request.getAttribute("hmLeavesMap");
	Map hmLeavesColour = (Map) request.getAttribute("hmLeavesColour");
	
	Map<String, String> hmEmpOrgId = (Map<String, String>) request.getAttribute("hmEmpOrgId");
	if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
	
	List<String> shiftDetails = (List<String>)request.getAttribute("shiftDetails"); 
//	out.println("<br/>hmServices="+hmServices);
//	out.println("<br/>hmServicesWorkrdFor="+hmServicesWorkrdFor);
//  out.println("<br/>hmLeavesMap="+hmLeavesMap);
	




	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");	
	List _alHolidays = (List) request.getAttribute("_alHolidays");
	
	String strReqAlphaValue = (String)request.getParameter("alphaValue");
	if(strReqAlphaValue==null){
		strReqAlphaValue="";
	}
	
	
	String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
	if(strAction!=null){
		strAction = strAction.replace(request.getContextPath()+"/","");
	}
	
%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Staff Roster" name="title"/>
</jsp:include>

<%StringBuilder sb = new StringBuilder(); %>

<div class="leftbox reportWidth" >
 
 
 
 
 
 
  <s:form theme="simple" method="post" name="frm_roster_actual">
 
 
 
 
 
<div class="filter_div">
<div class="filter_caption">Filter</div>
<%if(strUserType!=null && !strUserType.equals(IConstants.MANAGER)){ %>
	<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName"
			onchange="document.frm_roster_actual.submit();" list="organisationList" key="" cssStyle="float:left; margin-right: 10px; width:150px;"/>
			
    <s:select theme="simple" name="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         onchange="document.frm_roster_actual.submit();"
                         list="wLocationList" key=""  />
                    
    <s:select name="f_department" list="departmentList" listKey="deptId"  cssStyle="float:left;margin-right: 10px;"
    			listValue="deptName" headerKey="0" headerValue="All Departments"
    			onchange="document.frm_roster_actual.submit();" 
    			></s:select>
    			
    <s:select name="f_service" list="serviceList" listKey="serviceId"  cssStyle="float:left;margin-right: 10px;"
    			listValue="serviceName" headerKey="0" headerValue="All Services"
    			onchange="document.frm_roster_actual.submit();" 
    			></s:select>
    			
    <s:select theme="simple" name="f_level" listKey="levelId" headerValue="All Levels"  cssStyle="float:left;margin-right: 10px;"
	                            listValue="levelCodeName" headerKey="0" 
	                            onchange="document.frm_roster_actual.submit();"
	                            list="levelList" key="" required="true" />
     <%} %>
    
    <s:select theme="simple" name="strMonth" listKey="monthId" cssStyle="width:101px;"
                         listValue="monthName" headerKey="0"
                         onchange="document.frm_roster_actual.submit();" 		
                         list="monthList" key=""  />
	<s:select theme="simple" name="strYear" listKey="yearsID" cssStyle="width:65px;" listValue="yearsName" headerKey="0"
         onchange="document.frm_roster_actual.submit();" list="yearList" key=""  />   
    
    </div>

 
 
 
  <%-- 
      <label style="font-weight:bold">Select Paycycle: </label>
      <s:select label="Select PayCycle" name="paycycle" listKey="paycycleId" listValue="paycycleName"	onchange="document.frm_roster_actual.submit();"
		list="paycycleList" key="" />
	 --%>	
		
		
	<%-- 	<s:select theme="simple" name="strWLocation" listKey="wLocationId"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         onchange="document.frm_roster_actual.submit();" 		
                         list="wLocationList" key=""  />
	 --%>				
			
		
		
    </s:form>



<div >

<%if(strAction!=null && strAction.equalsIgnoreCase("ShiftRosterReport.action")) {%>
	<a href="RosterReport.action"><img src="images1/ckt_rep.png" /></a>
	<img src="images1/shft_rep_dis.png" />
<%} %>

</div>



 <%
if(hmList!=null && hmList.size()!=0){
%>


<div class="clr"></div>

<div class="roster_holder" style="border:solid 0px #ccc; margin:0px auto; width:100%">

    <div style="width:925px;width:100%;  float:left; border:#ff0 solid 0px; height:40px">
    
         <div class="prev" style="width:172px; border:solid #fff 0px; height:55px;float:left;background: none">
                <div class="prevlink" style="float:right">  </div>      
         </div>
         
             <div class="mask_dates" id="scrolldates" style="width:730px;width:81%; border:#00f solid 0px;position:relative; float:left; overflow:hidden; height:65px">
            
            		<% if(paycycleDuration.equalsIgnoreCase("W")) {%>
					<div class="block_dates weekly_width" style="height:45px;">
				
					<% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
						<div class="block_dates biweekly_width" style="height:45px;">
					
					<% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
						<div class="block_dates fortnightly_width" style="height:45px;">
					
					<% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
						<div class="block_dates monthly_width" style="height:45px;">
					
					<%} %>
                                        
			           <div>
			                <div class="row_day">
			                      
			                <% for (int i = 0; alDay!=null && i < alDay.size(); i++) { %>
					          <div class="day reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) alDayF.get(i) == null) ? "" : (String) alDayF.get(i))%>
					         </div>
					        <%}%>
			                
			                </div>
			                                
			                <div style="clear:both"></div>
			                
			                <div class="row_date">
			                               
			                <%for (int i = 0; alDate!=null && i < alDate.size(); i++) {%>
					          <div class="date reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) alDateF.get(i) == null) ? "" : (String) alDateF.get(i))%>
					         </div>
					        <%	}%>
					        </div>
			                
			                <div style="clear:both"></div>
			                
			 				<%-- <div class="row_day">
			                      
			                <%for (int i = 0; i < alDay.size(); i++) {%>
					           <div class="day reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>>Shift
					           </div>
					        <%	} %>
			                                                
			                </div> --%>
			                
			                  <div style="clear:both"></div>
			
			         </div>

                </div>
              </div>
          </div>
         
    </div>
      
      <div class="clr"></div>
       
      <div style="border:#fff solid 0px; width:925px;width:100%; float:left ; height:auto">
   
        <div id="mask" >
       
          <div id="scrollemp" style="border:0px solid #f0f;width:180px;height:580px; overflow:hidden;float:left;"  >
       
            <div class="block2" >
          
             <!-- Employee names -->      
         
                <%	for (int i = 0; alEmpId!=null && i < alEmpId.size(); i++) {
					// String strCol = ((i % 2 == 0) ? "1" : "");
					String strCol = ((i % 2 == 0) ? "#f9f9f9" : "#efefef");
					List alServices = (List) hmList.get((String) alEmpId.get(i));
					Map hm2 = (Map) hmServicesWorkrdFor.get((String) alEmpId.get(i));
				
					
		     	%>

	      	<div class="empname" >
		                
             
		   <% for(int ii=0; alServices!=null && ii<alServices.size(); ii++){
			String strServiceId = (String)alServices.get(ii);
			Map hm = (Map) hmList.get((String) alEmpId.get(i)+"_"+strServiceId);
			if(hm==null){
				hm = new HashMap();
			}
			
			if(ii==0){
				if(hm!=null&& (String) hm.get("EMPNAME")!=null){%>
				
				<div class="alignLeft" style="float:left;height: 20px;overflow: hidden;width: 100px;"><%=(String) hm.get("EMPNAME")%></div>
				<div style="float:right; width:50px;overflow: hidden; height:auto">
				<%
				   }}%>
		
                <div style="height:21px; overflow:hidden;"><%=(String)hmServices.get(strServiceId)%></div>
                         
                 <% if(ii<alServices.size() -1 ){%>  <div style="border:1px solid #fff;"></div> <%} %>
                          
               <% if(ii==alServices.size() -1 ){%>
                </div>
               <%}
				  }
               %>
				
        </div>
             
        <%}%>   
                
      
      </div>

        
         </div>
       
       
         <div id="pivot"  onscroll="divScroll();"> 
     
         <% if(paycycleDuration.equalsIgnoreCase("W")) {%>
	         <div class="block weekly_width" id="sos">
		
			<% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
			<div class="block biweekly_width" id="sos">
			
			<% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
			<div class="block fortnightly_width" id="sos">
			
			<% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
			<div class="block monthly_width" id="sos">
					
		<%} %>
         
       <div>

<!-- hrizontal colck entries row thas is to be repeated -->


      <%for (int i = 0; alEmpId!=null && i < alEmpId.size(); i++) {
			// String strCol = ((i % 2 == 0) ? "1" : "");
			String strCol = ((i % 2 == 0) ? "#f9f9f9" : "#efefef");
			List alServices = (List) hmList.get((String) alEmpId.get(i));
			Map hm2 = (Map) hmServicesWorkrdFor.get((String) alEmpId.get(i));
			String strLocationId  = (String)hmWLocation.get((String)alEmpId.get(i));
			
			
			Map hmLeaves = (Map)hmLeavesMap.get((String) alEmpId.get(i));
			if(hmLeaves==null)hmLeaves=new HashMap();
		%>

		<%
		
		for(int ii=0; alServices!=null && ii<alServices.size(); ii++){
			String strServiceId = (String)alServices.get(ii);
			Map hm = (Map) hmList.get((String) alEmpId.get(i)+"_"+strServiceId);
			
		//	out.println("<br>hm"+hm);
			
			if(hm==null){
				hm = new HashMap();
			}

			%>
			
			<% if(paycycleDuration.equalsIgnoreCase("W")) {%>
					<div style="height:21px;float:left;border:solid 1px #fff;" class="weekly_width" >
				
			<% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
			<div style="height:21px;float:left;border:solid 1px #fff;" class="biweekly_width" >
			
			<% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
			<div style="height:21px;float:left;border:solid 1px #fff;" class="fortnightly_width" >
			
			<% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
			<div style="height:21px;float:left;border:solid 1px #fff;" class="monthly_width" >
			
			<%} %>
					
			<!-- <div style="height:21px;width:1460px;float:left;border:solid 1px #fff;"> -->
			
			
			<%
			
			
			//out.println("hm===>"+hm);
			//out.println((String) alEmpId.get(i)+" strServiceId===>"+strServiceId);
			//out.println("hmList===>"+hmList);
			
			
			
			
			
			for (int k = 0; alDate!=null && k < alDate.size(); k++) {
				
				String strColour = (String)hmHolidayDates.get((String)alDate.get(k)+"_"+strLocationId);
				
				//out.println(strColour);
				
				if(strColour==null){
					//strColour = (String)hmWeekEnds.get(uF.getDateFormat((String)alDate.get(k), CF.getStrReportDateFormat(), "EEEE").toUpperCase()+"_"+strLocationId);
					strColour = (String)hmWeekEnds.get(uF.getDateFormat((String)alDate.get(k), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT).toUpperCase()+"_"+strLocationId);
				}
				
				
				
				String strLeave = (String)hmLeaves.get(uF.getDateFormat((String)alDate.get(k), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
				if(strLeave!=null){
					strColour = (String)hmLeavesColour.get(strLeave);
				}
				
				String strDate = (String) alDate.get(k);
				String strCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", IConstants.DBDATE, CF.getStrReportDateFormat());

		%>
	
	
	
		<%if(strLeave!=null){ %>
			<div class="inout"	style="<%= (strDate!=null && strDate.equalsIgnoreCase(strCurrentDate)?"border-right: 1px solid blue;border-left: 1px solid blue;":"border: 1px solid rgb(255, 255, 255)")%>;cursor:pointer; <%=((strColour!=null) ? "background-color:" + strColour + ";line-height:21px;" : "background:"+strCol+";line-height:21px")%>" align="center">
				<%=strLeave %>
			</div>
		<%}else{ %>
			<div class="inout"	style="<%= (strDate!=null && strDate.equalsIgnoreCase(strCurrentDate)?"border-right: 1px solid blue;border-left: 1px solid blue;":"border: 1px solid rgb(255, 255, 255)")%>;cursor:pointer; <%=((strColour!=null) ? "background-color:" + strColour + ";line-height:21px;" : "background:"+strCol+";line-height:21px")%>" align="center">
				<%=((hm.containsKey((String) alDate.get(k) + "FROM")) ? "<a  title=\""+ (String) hm.get((String) alDate.get(k) + "TO") +" \"  href=\"javascript:void(0)\" onclick=\"displayBlock('popup_name"+(String) hm.get((String) alDate.get(k)+"_"+strServiceId+"ROSTER_ID")+"');\"  class=\"poplight\">"+ uF.showData( (String) hm.get((String) alDate.get(k) + "FROM"),"-")+"</a>" :  "<a href=\"javascript:void(0)\" onclick=\"displayBlock('popup_name"+(String) alEmpId.get(i)+uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"');\" rel=\"popup_name"+(String) alEmpId.get(i)+uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"\" class=\"poplight\"> - </a>") %>
			</div>
		<%} %>
		
		
		<%
		int nOrgId = uF.parseToInt(hmEmpOrgId.get((String) alEmpId.get(i)));
		List<FillShift> shiftList = new FillShift(request).fillShiftByOrg(nOrgId);
		
		sb.append("<div id=\"popup_name"+(String) alEmpId.get(i)+ uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"\" class=\"popup_block posfix\">"+
				"<a href=\"javascript:void(0)\" onclick=\"hideBlock('popup_name"+(String) alEmpId.get(i)+ uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"');\" class=\"close\"><img src=\""+request.getContextPath()+"/images/close_pop.png\" class=\"btn_close\" title=\"Close Window\" alt=\"Close\" /></a>"+
		        "<h2>You have chosen to add roster for "+(String) hm.get("EMPNAME")+" on "+  (String) alDate.get(k) +"</h2>"+
				"<form name=\"frmAddRoster\" action=\"AddShiftRosterReport.action\">"+
				"<input type=\"hidden\" name=\"rosterDate\" value=\""+ (String) alDate.get(k) +"\">"+
				"<input type=\"hidden\" name=\"empId\" value=\""+(String) alEmpId.get(i) +"\">"+
				"<table align=\"center\">"+
				"<tr>"+
					/* "<td class=\"reportLabel\" nowrap=\"nowrap\">Select cost centre</td>"+ */
				"<td>"+
					/* "<select name=\"service\">"+
						request.getAttribute("CC")+
					"</select>"+ */
				"<input type=\"hidden\" name=\"service\" value=\""+strServiceId+"\" /> </td>"+
				"</tr>"+
				
				"<tr>"+
					"<td class=\"reportLabel\">Select Shift</td>"+
					"<td><select name=\"shift_id\" style=\"width:100px\">");
		
				for (int j=0; shiftList!=null && j<shiftList.size(); j++) {
			
					sb.append("<option value="+((FillShift)shiftList.get(j)).getShiftId()+"> "+((FillShift)shiftList.get(j)).getShiftCode()+"</option>"); 
					
				}
				sb.append("</select> </td>"+
			     "</tr>"+
				 "<tr>"+
					"<td colspan=\"2\" align=\"center\"><input type=\"submit\" class=\"input_button\" value=\"Add Roster\" /></td>"+
				"</tr>"+
				"</table>"+
				"</form>"+
				
		  "</div>");
			
			}
     %>
</div>
<div class="clr" style="clear:both"></div>
<%	} %>
   
<%	} %>

         </div> 
       
       </div>
       
     </div>  
         
 <!-- </div> -->    
  
 
     
<!-- </div> -->

<%
if (request.getAttribute("empRosterDetails") != null)
{
	out.println(request.getAttribute("empRosterDetails"));
}
out.print(sb.toString());
}
else
{
%>
 <div class="msg nodata">
<span>
No employees found for the current selection.
</span>
</div>

<%
}
%>

  
      </div>

<div class="clr"></div>
				<%
							
					for (int z = 0; shiftDetails!=null && z < shiftDetails.size()-1;) {
						
					String strColour = shiftDetails.get(z++);
				%>
				<div style="width:100%;float:left;margin:5px">
     				<div style="border:1px solid <%=strColour%>; float:left;">
						<div style="background-color:<%=strColour%>;width:100%;height:15px;text-align:center"><%=shiftDetails.get(z++)%></div>
						<p style="padding-left:5px;padding-right:5px"><span style="font-weight:bold">Shift Start</span> <%=shiftDetails.get(z++)%>  	<span style="font-weight:bold">End</span> <%=shiftDetails.get(z++)%></p>
						<p style="padding-left:5px;padding-right:5px"><span style="font-weight:bold">Break Start</span> <%=shiftDetails.get(z++)%>  	<span style="font-weight:bold">End</span> <%=shiftDetails.get(z++)%></p>
					</div>
				</div>
     			<%
					}
				%> 

</div> 

  




   
      


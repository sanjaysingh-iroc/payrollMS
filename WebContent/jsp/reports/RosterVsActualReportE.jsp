<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<style>
.block {
	float: left;
	position: absolute;
	background-color: #abc;
	left: 255px;
	top: 0px;
	width: 2180px;
	height: auto;
	margin: 0px;
	border: #666666 solid 1px;
	z-index: 10; 
}

.block2 { /* float:left; */
	position: absolute;
	background-color: #ccc;
	left: 0px;
	top: 0px;
	width: 253px;
	height: auto;
	margin: 0px;
	border: #666666 solid 1px;
	z-index: 100;
}

.block_dates {
	float: left;
	position: absolute;
	background-color: #ccc;
	left: 0px;
	top: 0px;
	width: 2180px;
	height: auto;
	margin: 0px;
	border: #00ff00 solid 0px;
	z-index: 10;
}

.weekly_width
{
   width: 1085px;
}

.biweekly_width
{
  width: 2170px;
}

.monthly_width
{
  width: 4805px;
}

.fortnightly_width
{
  width: 2170px;
}

.inout
{
  float:left;
  border:#fff solid 1px;
  text-align: center;
  width: 153px;
  _height:21px;
  
}

.empname {
	border: #fff solid 1px;
	background: #99CCFF;
	float: left;
	overflow: hidden;
	width: 245px;
	padding: 0px 3px 0px 3px;
	line-height: 19px;
	height: auto;
}

.next {
	width: 32px;
	float: right; /* border:#666666 solid 1px; */
	display: block;
}

img {
	border: 0px solid #fff;
	outline: none;
}
</style>

<script src="jquery-1.4.2.js"></script>

<script>
	var h;
	var w;
	var remain;
	var r1;
	var h_remain;
	var h_r1;

	function resetall() {
		$(".block").animate({"left" : "250px"}, 10);
		$(".block").animate({"top" : "0px"}, 10);
		$(".block2").animate({"left" : "0px"}, 10);
		$(".block2").animate({"top" : "0px"}, 10);
	};

	function shiftright() {
		
		var leftval = $(".block").css("left");
		$("#right1").show();
		if (leftval == "255px") {
			$("#left1").hide();
		}

		else {
			if (remain == 0) {
				$(".block").animate({"left" : "+=" + r1 + ""}, 10);
				$(".block_dates").animate({"left" : "+=" + r1 + ""}, 10);
				remain = r1;

			}
			else {
				$(".block").animate({"left" : "+=620px"}, 10);
				$(".block_dates").animate({"left" : "+=620px"}, 10);
				remain = remain + 620;
			}
		}
	};

	function shiftleft() {

		var leftval = $(".block").css("left");

		if (remain >= 620) {
			$(".block").animate({"left" : "-=620px"}, 10);
			$(".block_dates").animate({"left" : "-=620px"}, 10);
			$("#left1").show();

			remain = remain - 620;

		}

		else {
			$(".block").animate({"left" : "-=" + remain + ""}, 10);
			$(".block_dates").animate({"left" : "-=" + remain + ""}, 10);
			$("#right1").hide();

			r1 = remain;

			remain = 0;

		}

	};

	function shiftdown() {

		$("#down").show();

		var topval = $(".block").css("top");

		var hght = $(".block").height();

		if (topval == "0px") {

		}

		else {

			if (h_remain == 0) {
				$(".block").animate({"top" : "+=" + h_r1 + ""}, 10);
				$(".block2").animate({"top" : "+=" + h_r1 + ""}, 10);
				h_remain = h_r1;
			}

			else {
				$(".block").animate({"top" : "+=570"}, 10);
				$(".block2").animate({"top" : "+=570"}, 10);
				h_remain = h_remain + 570;

			}
		}
	};

	function shifttop() {
		var hght = $(".block").height();
		var hght2 = $(".block2").height();
		if (hght <= "570") {
			$("#down").hide();

		} else {
			$("#down").show();
			if (h_remain >= 570) {

				$(".block").animate({"top" : "-=570px"}, 10);
				$(".block2").animate({"top" : "-=570px"}, 10);
				h_remain = h_remain - 570;
			}

			else {
				
				h_remain = h_remain + 20;
				$(".block").animate({"top" : "-=" + h_remain + ""}, 10);
				$(".block2").animate({"top" : "-=" + h_remain + ""}, 10);
				h_r1 = h_remain;
				h_remain = 0;

				$("#down").hide();
			}
		}
	};

	$(document).ready(function()

	{
		var hght = $(".block").height();
		var hght2 = $(".block2").height();
		var divh = document.getElementById('sos').offsetHeight;
		var divw = document.getElementById('sos').offsetWidth;
		h = divh;
		w = divw;
		h_remain = h - 570;
		remain = w - 882 + 255;

		if (w < 620) {

			$("#right1").hide();
			$("#left1").hide();
		}

		else {
			$("#right1").show();
			$("#left1").show();
		}

		if (h < 570) {

			$("#down").hide();
			$("#top1").hide();
		}

		else {
			$("#down").show();
			$("#top1").show();
		}

	});
</script>


<%

	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	
	List _allDates = (List) request.getAttribute("_allDates");
	List _alHolidays = (List) request.getAttribute("_alHolidays");
	List empId = (List) request.getAttribute("empId");
	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
	Map hmRosterHours = (Map) request.getAttribute("hmRosterHours");	
	Map hmActualHours = (Map) request.getAttribute("hmActualHours");
	Map hmEmpCodeName = (Map) request.getAttribute("hmEmpCodeName");
	String paycycleDuration = (String)request.getAttribute("paycycleDuration");
	String strFrom = (String)request.getAttribute("FROM");
	String strTo = (String)request.getAttribute("TO");
	
	if(_allDates==null){
		_allDates = new ArrayList();
	}
	if(_alHolidays==null){
		_alHolidays = new ArrayList();
	}
	
	if(_hmHolidaysColour==null){
		_hmHolidaysColour = new HashMap();
	}
	if(hmRosterHours==null){
		hmRosterHours = new HashMap();
	}
	if(hmActualHours==null){
		hmActualHours = new HashMap();
	}
	if(hmEmpCodeName==null){
		hmEmpCodeName = new HashMap();
	}
	
%>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Rostered hours vs actual hours" name="title"/>
</jsp:include>




<s:form cssStyle="margin-left:735px; margin-bottom:10px" theme="simple" method="post" name="frm_roster_actual">
	<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId"
		listValue="paycycleName" 
		onchange="document.frm_roster_actual.submit();"
		list="paycycleList" key="" />
</s:form>

<%-- <div class="aboveform scroll">

<table cellpadding="2" cellspacing="1">

	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<%
			for (int i = 0; i<_allDates.size(); i++) {
		%>
		<td class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) _allDates.get(i) == null) ? "" : (String) _allDates.get(i))%>
		</td>
		<%
			}
		%>
	</tr>

	
	<%
	int j=0;
for(j=0; j<empId.size(); j++){
	
	String strCol = ((j%2==0)?"dark":"light");
	Map hmRoster = (Map)hmRosterHours.get((String)empId.get(j));
	Map hmActual = (Map)hmActualHours.get((String)empId.get(j));
	if(hmRoster==null){
		hmRoster = new HashMap();
	}
	if(hmActual==null){
		hmActual = new HashMap();
	}
	
	%>
	
	
	
	<tr class="<%=strCol%>" title="<%=(String)hmEmpCodeName.get((String)empId.get(j)) %>- Roster Hours">
		<td class="reportHeading alignLeft" nowrap="nowrap" rowspan="2"><%=(String)hmEmpCodeName.get((String)empId.get(j)) %></td>
		<td class="reportHeading alignCenter">Rostered</td>

		<%
			for (int i = 0; i<_allDates.size(); i++) {
			
		%>
		<td class="alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmRoster.get((String)_allDates.get(i)),"0") %></td>
		<%
			}
		%>



	</tr>


	<tr class="<%=strCol%>" title="<%=(String)hmEmpCodeName.get((String)empId.get(j)) %>- Actual Hours">
		<td class="reportHeading alignCenter">Actual</td>

		<%
			for (int i = 0; i<_allDates.size(); i++) {
		%>
		<td class="alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmActual.get((String)_allDates.get(i)),"0") %></td>
		<%
			}
		%>



	</tr>
	
	<%} 
	
if(j==0){
	%>
	
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td class="reportLabel alignCenter" colspan="14">No record found</td>
	</tr>
	<%
}
	%>
	

</table>
 --%>
 <div id="printDiv" class="leftbox reportWidth">
 
<span style="color: #346897; font-size: 14px; font-weight: 300;">
	By Employee (per employee)- by and between [<%=uF.getDateFormat(strFrom, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) %> to <%=uF.getDateFormat(strTo, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) %>]
</span>
		
<%
if(hmActualHours.size()!=0 || hmRosterHours.size()!=0 ) //either actual hours or rosterhours has data then goes inside
{ %>
 
 <%

%>

	<div style="width: 920px; float: left; border: #ff0000 solid 0px; height: 35px;">
		
			<div class="prev" style="width: 258px; border: solid #00ff00 0px; height: 35px; float: left">
					
					<div class="prevlink" style="float: right">
						<a href="javascript:void(0)" id="left1" onclick="shiftright()"><img	src="<%=request.getContextPath()+"/images1/prev.png"%>"/></a>
					</div>
					
			</div>
			
			<div class="mask_dates"	style="width: 623px; border: #99CC00 solid 0px; position: relative; float: left; overflow: hidden; height: 100px">
				
				<% if(paycycleDuration.equalsIgnoreCase("W")) {%>
				
				<div class="block_dates weekly_width" style="height: 100px; background: #999999;">
				
				<% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
				
				<div class="block_dates biweekly_width" style="height: 100px; background: #999999;">
				
				<% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
				
				<div class="block_dates fortnightly_width" style="height: 100px; background: #999999;">
				
				<% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
				
				<div class="block_dates monthly_width" style="height: 100px; background: #999999;">
				
				<%} %>
				
					<%
						for (int i = 0; i<_allDates.size(); i++) {
							System.out.println("value of date "+_allDates.get(i));
					%>
					
				<%-- <td class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) _allDates.get(i) == null) ? "" : (String) _allDates.get(i))%> --%>
				
				<div style="text-align: center; width: 153px; float: left; 
					height:35px; border: 1px solid #fff; background-color: #CEE3F6;">
					<%=(String) _allDates.get(i)%>
				</div>

				<%
					}
				%>

				</div>
				
			</div>
	
			<div class="next">

				<a href="javascript:void(0)" id="right1" onclick="shiftleft()"><img	src="<%=request.getContextPath()+"/images1/next.png"%>"/></a>

			</div>
			
		</div>
		
		<div class="clr" style="clear: both"></div>
		
		<div style="border: #9933CC solid 0px; width: 920px; float: left; height: auto">

			<div class="mask" style="width: 882px; border: #ccc solid 1px; height: 570px; overflow: hidden; position: absolute; float: left">

				<div class="block2">
				
					<div style="border: 0px solid #fff; width: 180px">
			
							<%
							int j=0;
							for(j=0; j<empId.size(); j++){
							
								String strCol = ((j%2==0)?"dark":"light");
								Map hmRoster = (Map)hmRosterHours.get((String)empId.get(j));
								Map hmActual = (Map)hmActualHours.get((String)empId.get(j));
								if(hmRoster==null){
									hmRoster = new HashMap();
								}
								if(hmActual==null){
									hmActual = new HashMap();
								}
	
							%>
							
							<div class="empname">

								
								<div style="text-align: center; width: 185px; float: left; overflow: hidden; height:21px;">
									<%=(String)hmEmpCodeName.get((String)empId.get(j)) %>
								</div>
							
								<div style="float: right; width: 60px; overflow: hidden; height: auto">
			
									<div style="text-align: center; width: 60px; float: left; overflow: hidden; height:21px;">
										Rostered
									</div>
									
									<div style="border: 1px solid #fff; float: left; width: 80px;"></div>
									
									<div style="text-align: center; width: 60px; float: left; overflow: hidden; height:21px;">
										Actual
									</div>
								
								</div>
							
							</div>
							
							<%
								}
							%>
					</div>	
					
				</div>		
				
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
						
						<%
							for(j=0; j<empId.size(); j++){
							
								String strCol = ((j%2==0)?"dark":"light");
								Map hmRoster = (Map)hmRosterHours.get((String)empId.get(j));
								Map hmActual = (Map)hmActualHours.get((String)empId.get(j));
								if(hmRoster==null){
									hmRoster = new HashMap();
								}
								if(hmActual==null){
									hmActual = new HashMap();
							}
	
						%>
						
							<%
								if (j % 2 == 0) { 
							
								%>
									<% if(paycycleDuration.equalsIgnoreCase("W")) {%>
									<div style="float:left; background-color: #EFF5FB;" class="weekly_width">
									
									<% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
									<div style="float:left; background-color: #EFF5FB;" class="biweekly_width">
									
									<% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
									<div style="float:left; background-color: #EFF5FB;" class="fortnightly_width">
									
									<% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
									<div style="float:left; background-color: #EFF5FB;" class="monthly_width">
									
									<%} %>
								
								<%} else {%>
									
									<% if(paycycleDuration.equalsIgnoreCase("W")) {%>
									<div style="float:left; background-color: #CEE3F6;" class="weekly_width">
									
									<% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
									<div style="float:left; background-color: #CEE3F6;" class="biweekly_width">
									
									<% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
									<div style="float:left; background-color: #CEE3F6;" class="fortnightly_width">
									
									<% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
									<div style="float:left; background-color: #CEE3F6;" class="monthly_width">
									
									<%} %>
						
							<%} %>
									
								<%
									for (int i = 0; i<_allDates.size(); i++) {
								%>	
									<div class="inout" style="height: 21px;">
									
										<%= uF.showData((String)hmRoster.get((String)_allDates.get(i)),"0") %>
									
									</div>
										
									<%
										}
								%>
							</div>
								
								
							<%
								if (j % 2 == 0) { 
							
								%>
									<% if(paycycleDuration.equalsIgnoreCase("W")) {%>
									<div style="float:left; background-color: #EFF5FB;" class="weekly_width">
									<% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
									<div style="float:left; background-color: #EFF5FB;" class="biweekly_width">
									<% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
									<div style="float:left; background-color: #EFF5FB;" class="fortnightly_width">
									<% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
									<div style="float:left; background-color: #EFF5FB;" class="monthly_width">
									<%} %>
								
								<%} else {%>
									<% if(paycycleDuration.equalsIgnoreCase("W")) {%>
									<div style="float:left; background-color: #CEE3F6;" class="weekly_width">
									<% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
									<div style="float:left; background-color: #CEE3F6;" class="biweekly_width">
									<% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
									<div style="float:left; background-color: #CEE3F6;" class="fortnightly_width">
									<% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
									<div style="float:left; background-color: #CEE3F6;" class="monthly_width">
									<%} %>
						
							<%} %>
									
								<%
									for (int i = 0; i<_allDates.size(); i++) {
								%>	
									<div class="inout" style="height: 21px;">
									
										<%= uF.showData((String)hmActual.get((String)_allDates.get(i)),"0") %>
									
									</div>
										
									<%
										}
								%>
								
							</div>
								
								<%
									}
								%>
						</div>
					</div>
				</div>
			
			<div class="scrollupdown" style="height: auto; height: 570px; float: right; z-index: 30; background: #fff; border: #003399 solid 0px;">
				<div style="vertical-align: top">
					<a href="javascript:void(0)" id="top1" onclick="shiftdown()"><img src="<%=request.getContextPath()+"/images1/up.png"%>"/></a>
				</div>

				<div style="vertical-align: bottom; margin-top: 500px">
					<a href="javascript:void(0)" id="down" onclick="shifttop()"><img
						src="<%=request.getContextPath()+"/images1/down.png"%>"/>
					</a>
				</div>
			</div>
			
	</div>
	</div>	
</div>
		<%
					}
else
{	%>

	The data is not available!
	<%} %>
 

 </div>  



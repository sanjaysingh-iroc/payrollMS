<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>


<style>
.block {
	float: left; 
	position: absolute;
	background-color: #abc;
	left: 255px;
	top: 0px;
	width: 4044px;
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
	width: 255px;
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
	width: 4044px;
	height: auto;
	margin: 0px;
	border: #00ff00 solid 0px;
	z-index: 10;
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
				$(".block").animate({"left" : "+=621px"}, 10);	
				$(".block_dates").animate({"left" : "+=621px"}, 10);
				remain = remain + 621;
			}
		}

	};

	function shiftleft() {

		var leftval = $(".block").css("left");

		if (remain >= 621) {
			$(".block").animate({"left" : "-=621px"}, 10);
			$(".block_dates").animate({"left" : "-=621px"}, 10);
			$("#left1").show();

			remain = remain - 621;

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
			$("#top1").hide();
		}

		else {
			$("#top1").show();
			if (h_remain == 0) {
				$(".block").animate({"top" : "+=" + h_r1 + ""}, 10);
				$(".block2").animate({"top" : "+=" + h_r1 + ""}, 10);		
				h_remain = h_r1;
			}

			else {
				$(".block").animate({"top" : "+=564"}, 10);
				$(".block2").animate({"top" : "+=564"}, 10);
				h_remain = h_remain + 564;
			}

		}

	};

	function shifttop() {
		var hght = $(".block").height();

		if (hght <= "564") {
			$("#down").hide();

		} else {
			$("#down").show();

			if (h_remain >= 564) {
				$("#top1").show();
				$(".block").animate({"top" : "-=564px"}, 10);
				$(".block2").animate({"top" : "-=564px"}, 10);
				h_remain = h_remain - 564;
			}

			else {
				//h_remain = h_remain + 20;
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

		var divh = document.getElementById('sos').offsetHeight;
		var divw = document.getElementById('sos').offsetWidth;
		h = divh;
		w = divw;
		h_remain = h - 564;
		remain = w - 882 + 255;

		if (w < 621) {

		 	$("#right1").hide();
			$("#left1").hide();
		}

		else {
			$("#right1").show();
			$("#left1").show();
		}

		if (h < 564) {

			$("#down").hide();
			$("#top1").hide();
		}

		else {
			$("#down").show();
			$("#top1").show();
		}

	});
</script>

<%!String showData(String strData, String strVal) {
		if (strData == null)
			return strVal;
		else
			return strData;
	}%>

<%
	Map hm = (Map) request.getAttribute("hmList");
	List alPayCycles = (List) request.getAttribute("alPayCycles");
	List alServiceId = (List) request.getAttribute("alServiceId");
	Map hmServiceName = (Map) request.getAttribute("alServiceName");
	System.out.println(hm);
	
%>

<div class="pagetitle">
      <span>Employee vs Service Hours</span>
</div>



<%-- <div class="aboveform ">

	<table cellpadding="0" cellspacing="1">
	<tr>

		<td nowrap="nowrap">&nbsp;</td>
		<td nowrap="nowrap" class="reportHeading">Services</td>

		<%
			for (int i = 0; i < alPayCycles.size(); i++) {
				
		%>

		<td class="reportHeading" nowrap="nowrap"><%=(String) alPayCycles.get(i)%></td>

		<%
				
			}
		%>

	</tr>

	<%
		Set set = hm.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			
			String strEmpId = (String) it.next();
			Map hmInner = (Map) hm.get(strEmpId);

			for (int k = 0; k < alServiceId.size(); k++) {
				String strCol = ((k%2==0)?"dark":"light");
				String strServiceId = (String)alServiceId.get(k);

			
	%>
	
<tr class="<%=strCol%>" title="<%=showData((String) hmInner.get("EMP_NAME"),"-")%> - <%=showData((String)hmServiceName.get(strServiceId),"")%>">

<%if(k==0){ %>
<td nowrap="nowrap" rowspan="<%=alServiceId.size() %>" class="reportHeading alignLeft"><%=showData((String) hmInner.get("EMP_NAME"),"-")%></td>
<%} %>

<td nowrap="nowrap" class="reportHeading alignLeft"><%=showData((String)hmServiceName.get(strServiceId),"")%></td>

<%
	
	for(int i=0; i<alPayCycles.size(); i++){ 
	
%>

<td class="alignRight" nowrap="nowrap"><%=showData((String) hmInner.get(i+strServiceId+"L"),0+"")%></td>

<%}}%>

</tr>

	<%
		}
	%>

</table>

</div> --%>


	<!-- --------------------------   New Code For Sliding Table Begins -------------------------------->
	
<div class="leftbox reportWidth">
<%
if(hm.size()!=0) //go inside only if the list has data registered in attendence details table.
{

%>
	<div style="width: 920px; float: left; border: #ff0000 solid 0px; height: 35px;">

		<div class="prev" style="width: 258px; border: solid #00ff00 0px; height: 35px; float: left">

			<div class="prevlink" style="float: right">
				<a href="javascript:void(0)" id="left1" onclick="shiftright()"><img
					src="<%=request.getContextPath()+"/images1/prev.png"%>"/>
				</a>
			</div>
		</div>

		<div class="mask_dates"	style="width: 623px; border: #99CC00 solid 0px; position: relative; float: left; overflow: hidden; height: 100px">

			<div class="block_dates" style="height: 100px; background: #999999;">

				<%
					for (int i = 0; i < alPayCycles.size(); i++) {
				%>

				<div style="text-align: center; width: 153px; float: left; border: 1px solid #fff; background-color: #CEE3F6;">
					<%=(String) alPayCycles.get(i)%>
				</div>

				<%
					}
				%>

			</div>

		</div>

		<div class="next">

			<a href="javascript:void(0)" id="right1" onclick="shiftleft()"><img
				src="<%=request.getContextPath()+"/images1/next.png"%>"/>
			</a>

		</div>
		<!-- <a  href="#" id="reset" onclick="resetall()">reset</a>-->

	</div>

	<div class="clr" style="clear: both"></div>

	<div style="border: #9933CC solid 0px; width: 920px; float: left; height: auto">

		<div class="mask" style="width: 882px; border: #ccc solid 1px; height: 567px; overflow: hidden; position: absolute; float: left">

			<div class="block2">
			
				<div style="border: 0px solid #fff; width: 180px">
					
					<%
						Set set1 = hm.keySet();
						Iterator it1 = set1.iterator();
						while (it1.hasNext()) {

							String strEmpId = (String) it1.next();
							Map hmInner = (Map) hm.get(strEmpId);
					%>

					<div class="empname">

						<%
							for (int k = 0; k < alServiceId.size(); k++) {

								String strServiceId = (String) alServiceId.get(k);
	
								if (k == 0) {
									%>
						
									<div style="text-align: center; width: 145px; float: left; overflow: hidden; height:21px;">
										<%=showData((String) hmInner.get("EMP_NAME"),"-")%>
									</div>
						
									<div style="float: right; width: 100px; overflow: hidden; height: auto">
						
								<%}%>

								<div style="text-align: center; width: 94px; float: left; overflow: hidden; height:21px;">
									<%=showData((String) hmServiceName.get(strServiceId), "")%>
								</div>

						<%if (k < alServiceId.size() - 1) {%>
							<div style="border: 1px solid #fff; float: left; width: 94px;"></div>
						<%}%>


						<%if (k == alServiceId.size() - 1) {%>
						</div>
						
						<%}
							}
						%>
						
					</div>


					<%
						}
					%>

				</div>

			</div>

			<div class="block" id="sos">
				<div>
				
				<%
					Set set11 = hm.keySet();
					Iterator it11 = set11.iterator();
					int cnt =0;
					
					while (it11.hasNext()) {
						
						String strEmpId = (String) it11.next();
						Map hmInner = (Map) hm.get(strEmpId);
						

						for (int k = 0; k < alServiceId.size(); k++) {
							String strServiceId = (String) alServiceId.get(k);
					
						if (k % 2 == 0) { 
							%>

							<div style="width:4200px;float:left; background-color: #EFF5FB;">

						<% }else{%>

							<div style="width:4200px;float:left;background-color: #CEE3F6;">
						
							<%} %>

								<%
								for (int i = 0; i < alPayCycles.size(); i++) {
						
						 		%>
							 
								<div class="inout" style="height:21px;">
							
									<%=showData((String) hmInner.get(i + strServiceId + "L"),0 + "")%>
	
								</div>

						<%}%>

					</div>

				<%}
					}
				%>

			</div>

		</div>
		
		</div>
		
		<div class="scrollupdown"
			style="height: auto; height: 570px; float: right; z-index: 30; background: #fff; border: #003399 solid 0px;">
			<div style="vertical-align: top">
				<a href="javascript:void(0)" id="top1" onclick="shiftdown()"><img
					src="<%=request.getContextPath()+"/images1/up.png"%>"/>
				</a>
			</div>

			<div style="vertical-align: bottom; margin-top: 505px">
				<a href="javascript:void(0)" id="down" onclick="shifttop()"><img
					src="<%=request.getContextPath()+"/images1/down.png"%>"/>
				</a>
			</div>
		</div>

	</div>

</div>
<%
}
else
{
%>
The data is not available!
<%
}
%>
</div>


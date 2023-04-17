<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.HashMap"%>

<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">
	$(function() {
		$("#cgoalDueDate0").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("#mgoalDueDate0").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("#tgoalDueDate0").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("#igoalDueDate0").datepicker({
			format : 'dd/mm/yyyy'
		});

	});

	var pcount = 0;
	function addKRA(ch, count) {

		var KRACount = document.getElementById(ch + "KRACount" + count).value;
		var KRACountID = ch + "KRACount" + count;
		pcount++;
		var pid = ch + "KRAtd" + count + pcount;
		var ptag = document.createElement('p');
		ptag.setAttribute("class", "pclass");
		ptag.id = pid;

		var data = "<input type=\"text\" name=\""+ch+"KRA\" id=\""+ch+"KRA\" /><a href=\"javascript:void(0)\" class=\"add_lvl\" "
				+ "onclick=\"addKRA('"
				+ ch
				+ "',"
				+ count
				+ ");\">Add KRA</a><a href=\"javascript:void(0)\" class=\"add_lvl\" "
				+ "onclick=\"removeKRAID('"
				+ pid
				+ "','"
				+ KRACountID
				+ "');\">Remove KRA</a>";

		ptag.innerHTML = data;
		document.getElementById(ch + "KRAtdID" + count).appendChild(ptag);
		KRACount++;
		document.getElementById(ch + "KRACount" + count).value = KRACount;

	}
	function removeKRAID(id, KRACountID) {
		var row_skill = document.getElementById(id);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);

			var a = document.getElementById(KRACountID).value;
			document.getElementById(KRACountID).value = parseInt(a) - 1;

		}
	}

	function getMeasureWith(value, ch, count) {
		//$ Effort cdollarAmtid0 cmeasureEffortsid0  
		//cmeasureEffortsHrs cmeasureEffortsDays cmeasureDollar
		/*  document.getElementById("cmeasureEffortsHrs").value="";
		 document.getElementById("cmeasureEffortsDays").value="";
		 document.getElementById("cmeasureDollar").value=""; 
		 Amount Percentage
		 */
		if (value == 'Value' || value == 'Amount' || value == 'Percentage') {
			document.getElementById(ch + "dollarAmtid" + count).style.display = "table-row";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
			//document.getElementById("measureSpanId").innerHTML=value;
		} else if (value == 'Effort') {
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "measureEffortsid" + count).style.display = "table-row";
			//document.getElementById("measureSpanId").innerHTML=value;
		} else {
			document.getElementById(ch + "measureEffortsid" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
			//document.getElementById("measureSpanId").innerHTML="";
		}
	}

	function getMeasureKRA(value, ch, count) {
		//cMKRAID0 Yes No 
		if (value == 'Yes') {
			document.getElementById(ch + "MKRAID" + count).style.display = "table-row";
		} else {
			document.getElementById(ch + "MKRAID" + count).style.display = "none";
			document.getElementById(ch + "kraID" + count).style.display = "none";
			document.getElementById(ch + "measureID" + count).style.display = "none";
		}
	}

	function addFeedback(value, ch, count) {
		//Yes No iOrientation0 
		if (value == 'Yes') {
			document.getElementById(ch + "Orientation" + count).style.display = "table-row";
		} else {
			document.getElementById(ch + "Orientation" + count).style.display = "none";
		}
	}

	function addMeasureKRA(value, ch, count) {

		//KRA Measure ckraID0 cmeasureID0  dollarAmtid 
		if (value == 'KRA') {
			document.getElementById(ch + "kraID" + count).style.display = "table-row";
			document.getElementById(ch + "measureID" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
		} else if (value == 'Measure') {
			document.getElementById(ch + "measureID" + count).style.display = "table-row";
			document.getElementById(ch + "kraID" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "table-row";
		} else {
			document.getElementById(ch + "kraID" + count).style.display = "none";
			document.getElementById(ch + "measureID" + count).style.display = "none";
			document.getElementById(ch + "dollarAmtid" + count).style.display = "none";
		}
	}
	function addMeasureWith(value, ch, count) {
		//$ Effort cmkdollarAmtid0 cmkEffortsid0 
		if (value == 'Amount') {
			document.getElementById(ch + "mkdollarAmtid" + count).style.display = "table-row";
			document.getElementById(ch + "mkEffortsid" + count).style.display = "none";
		} else if (value == 'Effort') {
			document.getElementById(ch + "mkdollarAmtid" + count).style.display = "none";
			document.getElementById(ch + "mkEffortsid" + count).style.display = "table-row";
		} else {
			document.getElementById(ch + "mkEffortsid" + count).style.display = "none";
			document.getElementById(ch + "mkdollarAmtid" + count).style.display = "none";
		}
	}

	
		
	function checkFrequency(value) {		
		
		if (value == '3') {
			document.getElementById("weekly").style.display = "none";
			document.getElementById("annualy").style.display = "none";
			document.getElementById("monthly").style.display = "block";
			document.getElementById("dayMonth").style.display = "none";
		} else if (value == '2') {
			
			document.getElementById("weekly").style.display = "block";
			document.getElementById("annualy").style.display = "none";
			document.getElementById("monthly").style.display = "none";
			document.getElementById("dayMonth").style.display = "none";
						
		}else if (value == '6') {
			document.getElementById("weekly").style.display = "none";
			document.getElementById("annualy").style.display = "block";
			document.getElementById("monthly").style.display = "none";
			document.getElementById("dayMonth").style.display = "none";
		}else if (value == '4' || value == '5') {	
			document.getElementById("weekly").style.display = "none";
			document.getElementById("annualy").style.display = "none";
			document.getElementById("monthly").style.display = "none";
			document.getElementById("dayMonth").style.display = "block";
		} else {
			document.getElementById("weekly").style.display = "none";
			document.getElementById("annualy").style.display = "none";
			document.getElementById("monthly").style.display = "none";
			document.getElementById("dayMonth").style.display = "none";
		}
	}
	
</script>
<%
	Map<String, String> hmGoalType = (Map<String, String>) request.getAttribute("hmGoalType");
	String supervisorId = (String) request.getAttribute("supervisorId");
	String goaltype = request.getParameter("goaltype");
	System.out.println("goaltype in new " + goaltype);
	
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmCheckEmpList = (Map<String, String>) request.getAttribute("hmCheckEmpList");
	if(hmCheckEmpList==null) hmCheckEmpList=new HashMap<String, String>();
%>
<div class="leftbox reportWidth">
	<s:form id="formID" name="frmEditGoal" theme="simple" action="MyPersonalTarget"
		method="POST" cssClass="formcss">
		<s:hidden name="operation"></s:hidden>
		
		<input type="hidden" name="goaltype" id="goaltype" value="<s:property value="goaltype"/>"/> 
		<input type="hidden" name="goal_parent_id" value="<s:property value="goalid"/>"/> 
		<input type="hidden" name="supervisorId" id="supervisorId" value="<%=supervisorId%>"/>
		<s:hidden name="type"></s:hidden>
		<table class="tb_style" style="width: 100%">
			<!-- <tr>
				<th width="20%" align="right">Goal Type</th>
				<td>Personal Goal 
				</td>
			</tr> -->
			<tr>
				<th nowrap align="right">Target</th>
				<td><input type="text" name="corporateGoal"
					style="width: 600px;" />
				</td>
			</tr>
			<tr>
				<th nowrap align="right">Objective</th>
				<td><input type="text" name="cgoalObjective"
					style="width: 600px;" />
				</td>
			</tr>
			<tr>
				<th align="right" valign="top">Description</th>
				<td><textarea rows="3" cols="72" name="cgoalDescription"></textarea>
					<!-- <input type="text" name="cgoalDescription"
					style="width: 600px;" /> -->
				</td>
			</tr>
			
			<tr>
				<th nowrap align="right">Priority</th>
				<td><s:select theme="simple" name="priority" headerKey=""
						headerValue="Select" list="#{'1':'High', '2':'Medium', '3':'Low'}"/> 
				</td>
			</tr>
			
			
			<tr>
				<th align="right">Align an Attribute</th>
				<td><s:select name="cgoalAlignAttribute" list="attributeList"
						listKey="id" listValue="name" headerKey="0" headerValue="Select"></s:select>

				</td>
			</tr>
			<%-- <tr>
				<th align="right">Measure with</th>
				<td>
					<s:select theme="simple" name="cmeasurewith" headerKey="Amount"
						headerValue="Amount"
						list="#{'Value':'Value', 'Effort':'Effort','Percentage':'Percentage'}"
						onchange="getMeasureWith(this.value,'c',0);" />
						</td>
			</tr>
			<tr id="cdollarAmtid0" style="display: none;">
				<th align="right"><span id="measureSpanId"></span></th>
				<td><input type="text" name="cmeasureDollar"
					id="cmeasureDollar" />
				</td>
			</tr>
			<tr id="cmeasureEffortsid0" style="display: none;">
				<th align="right">Efforts</th>
				<td>Days&nbsp;<input type="text" name="cmeasureEffortsDays"
					id="cmeasureEffortsDays" style="width: 40px;" />&nbsp; Hrs&nbsp;<input
					type="text" name="cmeasureEffortsHrs" style="width: 40px;"
					id="cmeasureEffortsHrs" />
				</td>
			</tr> --%>
			<%--
				if (goaltype.equals("4") || goaltype.equals("5")) {
			--%>
			<%-- <tr>
				<th nowrap align="right">Does it have a Measure/KRA</th>
				<td><s:select theme="simple" name="cmeasureKra" headerKey=""
						headerValue="Select" list="#{'Yes':'Yes', 'No':'No'}"
						onchange="getMeasureKRA(this.value,'c',0);"
						value="measureKravalue" /> 
				</td>
			</tr>
			<tr id="cMKRAID0" style="display: none;">
				<th align="right">Add measure/KRA</th>
				<td><s:select theme="simple" name="cAddMKra" headerKey=""
						headerValue="Select" list="#{'KRA':'KRA', 'Measure':'Measure'}"
						onchange="addMeasureKRA(this.value,'c',0);" value="addMKravalue" />
					
			</tr>

			<tr id="ckraID0" style="display: none;">
				<th valign="top" align="right">KRA</th>
				<td id="cKRAtdID0"><input type="hidden" name="cKRACount"
					id="cKRACount0" value="1" />
					<p>
						<input type="text" name="cKRA" id="cKRA" /><a
							href="javascript:void(0)" class="add_lvl"
							onclick="addKRA('c',0);">Add KRA</a>
					</p></td>
			</tr> --%>

			<!-- <tr id="cmeasureID0" style="display: none;">
			<th valign="top" align="right">Measure</th>
				<td><input type="hidden" name="cMeasureCount" id="cMeasureCount0" value="1" />
					<p>
						<input type="text" name="cMeasureDesc" id="cMeasureDesc" />&nbsp;&nbsp;&nbsp;
						<input type="text" name="cMeasureVal" id="cMeasureVal" style="width: 100px"/>
					</p></td> -->
				<%-- <th align="right">Measure with</th>
				<td><s:select theme="simple" name="cmkwith" headerKey=""
						headerValue="Select" list="#{'Amount':'Amount', 'Effort':'Effort'}"
						onchange="addMeasureWith(this.value,'c',0);" value="mkwithvalue" />

				</td> --%>
			<!-- </tr> -->
			
			
			<tr id="cmeasureID0" style="display: table-row;">
				<th align="right">Measure with</th>
				<td>
					<s:select theme="simple" name="cmeasurewith" headerKey="Amount"
						headerValue="Amount"
						list="#{'Value':'Value', 'Effort':'Effort','Percentage':'Percentage'}"
						onchange="getMeasureWith(this.value,'c',0);" />
						</td>
			</tr>
			<tr id="cdollarAmtid0" style="display: table-row;">
				<th align="right"><span id="measureSpanId">&nbsp;</span></th>
				<td>
				<!-- <input type="text" name="cMeasureDesc" id="cMeasureDesc" />&nbsp;&nbsp;&nbsp; -->
				&nbsp;<input type="text" name="cmeasureDollar" id="cmeasureDollar" style="width: 64px;"/>
				</td>
			</tr>
			<tr id="cmeasureEffortsid0" style="display: none;">
				<th align="right">&nbsp;</th>
				<td>
				<!-- <input type="text" name="cMeasureDesc" id="cMeasureDesc"/>&nbsp;&nbsp;&nbsp; -->
				Days&nbsp;<input type="text" name="cmeasureEffortsDays"
					id="cmeasureEffortsDays" style="width: 40px;" />&nbsp; Hrs&nbsp;<input
					type="text" name="cmeasureEffortsHrs" style="width: 40px;"
					id="cmeasureEffortsHrs" />
				</td>
			
			
			
			
			
			<!-- <tr id="cmkdollarAmtid0" style="display: none;">
				<th align="right">Amount</th>
				<td><input type="text" name="cmeasurekraDollar" /></td>
			</tr>
			<tr id="cmkEffortsid0" style="display: none;">
				<th align="right">Efforts</th>
				<td>Days&nbsp;<input type="text" name="cmeasurekraEffortsDays"
					style="width: 40px;" />&nbsp; Hrs&nbsp;<input type="text"
					name="cmeasurekraEffortsHrs" style="width: 40px;" /></td>
			</tr> -->
			<%--
				}
			--%>
			<tr>
				<th align="right">Due Date</th>
				<td><input type="text" name="cgoalDueDate" id="cgoalDueDate0"
					class="duedatepick" style="width: 75px;" /></td>
			</tr>
			
			<%-- <tr>
				<th align="right">Feedback</th>
				<td><s:select theme="simple" name="cgoalFeedback" headerKey=""
						headerValue="Select" list="#{'Yes':'Yes', 'No':'No'}"
						onchange="addFeedback(this.value,'c',0);" /></td>
			</tr>
			
			<tr>
				<th style="text-align:right" valign="top">Select Frequency for Review</th>
                <td colspan="5">
                  <div style="position:reletive; height:70px">
                  <s:select theme="simple" name="frequency"  list="frequencyList"
                              listKey="id" listValue="name" onchange="checkFrequency(this.value)" />                            
                 
                  
                  <span id="weekly"
                      style="display: none; float: right; margin-right: 5cm;">
                          <s:select theme="simple" name="weekday" headerKey=""
                              headerValue="Select Day"
                              list="#{'Monday':'Monday','Tuesday':'Tuesday', 'Wednesday':'Wednesday','Thursday':'Thursday','Friday':'Friday','Saturday':'Saturday','Sunday':'Sunday'}" />
                  </span>  
                  <span id="annualy"
                      style="display: none; float: right; margin-right: 7cm;"> 
                      <s:select theme="simple" name="annualDay" cssStyle="width:50px;" headerKey=""
                              headerValue="Day"
                              list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
                              '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
                              '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
                      
                          <s:select theme="simple" name="annualMonth" cssStyle="width:110px;" headerKey=""
                              headerValue="Select Month"
                              list="#{'January':'January','February':'February', 'March':'March','April':'April','May':'May',
                              'June':'June','July':'July','August':'August','September':'September', 'October':'October','November':'November','December':'December'}" />
                              
                  </span> 
                  <span id="monthly"
                      style="display: none; float: right; margin-right: 5cm;"> 
                      <s:select theme="simple" name="day" headerKey=""
                              headerValue="Day"
                              list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
                              '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
                              '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
                      
                  </span>
                  <span id="dayMonth"
                      style="display: none; float: right; margin-right: 10cm;">
                      <s:select theme="simple" name="monthday" cssStyle="width:50px; position:absolute;" headerKey=""
                              headerValue="Day"
                              list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
                              '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
                              '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
                      
                          <s:select theme="simple" name="month" cssStyle="width:110px; position:absolute; margin-left:50px" headerKey=""
                              headerValue="Select Month" multiple="true"size="4"
                              list="#{'January':'January','February':'February', 'March':'March','April':'April','May':'May',
                              'June':'June','July':'July','August':'August','September':'September', 'October':'October','November':'November','December':'December'}" />
                              
                  </span> 
                  </div>
                  </td>
              </tr>  
                        
            <tr id="cOrientation0" style="display: none;">
				<th align="right">Orientation</th>
				<td><select name="corientation">
						<%=request.getAttribute("orientation")%>
				</select></td>
			</tr> --%>
			
			<!-- <tr>
				<th align="right">Weightage (%)</th>
				<td><input type="text" name="cgoalWeightage"
					style="width: 40px;" value="100" /></td>
			</tr>
			 -->

		</table>
		

		<div>
			<s:submit value="Save" cssClass="input_button" name="submit"></s:submit>
		</div>
	</s:form>
</div>





<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="com.konnect.jpms.util.*"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script src="<%= request.getContextPath()%>/scripts/color.js" type="text/javascript"></script>
<script type="text/JavaScript">
	var cp = new ColorPicker('window'); 
	var cp2 = new ColorPicker('window');
	
	
</script>

  
<script type="text/javascript">

	
	 function compStartDate()
     {
		 var pro_deadline='<%=(String) request.getAttribute("pro_deadline")%>'; 
		 var pro_startDate='<%=(String) request.getAttribute("pro_startDate")%>'; 
         var startDate= document.getElementById('startDate').value;
             
            /*  if(startDate < pro_startDate )
             {
             document.getElementById("err_strt_date").innerHTML="Please select start date greater than  start date of project.";
             document.getElementById('startDate').focus();
             return false;
             }else if(startDate > pro_deadline)
             {
                 document.getElementById("err_strt_date").innerHTML="Please select start date less than  deadline of project.";
                 document.getElementById('startDate').focus();
                 return false;
                 }
             else
             {

             document.getElementById("err_strt_date").innerHTML="";
             } */
     return false;
     }
	 function compEndDate()
     {
		 var pro_deadline='<%=(String) request.getAttribute("pro_deadline")%>'; 
		 var pro_startDate='<%=(String) request.getAttribute("pro_startDate")%>'; 
             var deadline= document.getElementById('deadline2').value;
             var startDate= document.getElementById('startDate').value;
            /*  if(deadline < pro_startDate )
             {
             document.getElementById("err_end_date").innerHTML="Please select deadline of task greater than  start date of project.";
             document.getElementById('deadline2').focus();
             return false;
             }else if(deadline > pro_deadline)
             {
                 document.getElementById("err_end_date").innerHTML="Please select deadline of task less than  deadline of project.";
                 document.getElementById('deadline2').focus();
                 return false;
                 }else if(deadline > startDate)
             {
                 document.getElementById("err_end_date").innerHTML="Please select deadline of task greater than  start date of task.";
                 document.getElementById('deadline2').focus();
                 return false;
                 }
             else
             {

             document.getElementById("err_end_date").innerHTML="";
             } */
     return false;
     }
	
var cnt=0;
function addExtraDoc() {
    cnt++;
    var divTag = document.createElement("div");
    divTag.id = "p_type"+cnt;
    divTag.setAttribute("style","float:left;");
        divTag.innerHTML = "<table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" style=\"width: 150px;\" name=\"doc_name1\"/></td><td><input type=\"file\" name=\"document1\" /></td>"+
        "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
        +"<a href=\"javascript:void(0)\"  onclick=\"removeExtraDoc(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a></td></tr></table>";
        
    document.getElementById("tblDiv").appendChild(divTag);
}



function removeExtraDoc(removeId) {
    var remove_elem = "p_type"+removeId;
    var row_skill = document.getElementById(remove_elem);
    document.getElementById("tblDiv").removeChild(row_skill);
    
}

	$(function() {
		/* $("#deadline2").datepicker({
			dateFormat : 'dd/mm/yy'
		});

		$("#startDate").datepicker({
			dateFormat : 'dd/mm/yy'
		}); */
		
		$("#deadline2").datepicker({
			dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("pro_startDate")%>", maxDate: "<%=request.getAttribute("pro_deadline")%>", 
			onClose: function(selectedDate){
				$("#startDate").datepicker("option", "maxDate", selectedDate);
			}
		});
		
		$("#startDate").datepicker({
			dateFormat : 'dd/mm/yy', minDate:"<%=request.getAttribute("pro_startDate")%>", maxDate: "<%=request.getAttribute("pro_deadline")%>", 
			onClose: function(selectedDate){
				$("#deadline1").datepicker("option", "minDate", selectedDate);
			}
		});
		$("#monthlyStartDate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
	});
	

	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		jQuery("#frmAddNewActivity").validationEngine();
	});

	//addLoadEvent(prepareInputsForHints);
	function getSkillwiseEmployee(skillId) {
		
		var proId = document.getElementById("hide_pro_id").value;
		getContent('empSpan', 'GetSkillwiseEmployee.action?proId='+proId+'&skillId='+skillId+'&type=EditTask');
	}
	
	
	function checkTimeFilledEmp() {
		
		var timeFilledEmps = document.getElementById("timeFilledEmps").value;
		var timeFilledEmp = timeFilledEmps.split(",");
		var choice = document.getElementById("emp_id");
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
		var selectedEmp = exportchoice.split(",");
		//alert("selectedEmp ===>> " + selectedEmp);
		var empCnt = 0;
		var filledEmpCnt = 0;
		for(var a=0; a<timeFilledEmp.length; a++) {
			if(timeFilledEmp[a] != '' && timeFilledEmp[a] != ' ') {
				for(var b=0; b<selectedEmp.length; b++) {
					if(timeFilledEmp[a] == selectedEmp[b]) {
						empCnt++;
						//alert("timeFilledEmp[a] ==>>> " + timeFilledEmp[a]);
					}
				}
				filledEmpCnt++;
			}
		}
		if(filledEmpCnt == empCnt) {
			//alert("ok ........"); 
			document.frmAddNewActivity.submit();
			return true;
		} else {
			alert("Resource already fill timesheet against this task, so can't remove resource from this task.");
			return false;
		}
	}
	
	
</script>

<div class="leftbox reportWidth" id="div_id_docs" style="font-size: 12px;">

	<!-- <br>
	<div class="pagetitle" align="center" style="margin: 0px;">Add
		New Task</div> -->
		
	<% 	List<GetPriorityList> priorityList = (List<GetPriorityList>)request.getAttribute("priorityList");
		List<FillSkills> empSkillList = (List<FillSkills>)request.getAttribute("empSkillList");
		List<String> alTaskData = (List<String>) request.getAttribute("alTaskData");
		String operation = (String) request.getAttribute("operation");
		String type = (String) request.getAttribute("type");
		String fromPage = (String) request.getAttribute("fromPage");
		String strTorSTLbl = "Task";
		if(type != null && type.equals("SubTask")) {
			strTorSTLbl = "Sub Task";
		}
	%>	
	<s:form name="frmAddNewActivity" id="frmAddNewActivity" action="AddNewActivity" method="post" cssClass="formcss" enctype="multipart/form-data" onsubmit="return checkTimeFilledEmp()" theme="simple">

		<s:hidden name="pro_id" id="pro_id" ></s:hidden>
		<s:hidden name="task_id" />
		<s:hidden name="sub_task_id" />
		<s:hidden name="operation"/>
		<s:hidden name="type"/>
		<s:hidden name="fromPage"/>
		<input type="hidden" name="hide_pro_id" id="hide_pro_id" value="<%=(String)request.getAttribute("pro_id") %>"/>
		<% if(operation != null && operation.equals("E")) { %>
			<div style="float: left" id="tblDiv"> 
				<table class="formcss">
					<tr>
						<td class="txtlabel alignRight"><%=strTorSTLbl %> Name:<sup>*</sup></td>
						<td>
						<input type="hidden" name="taskID" id="taskID" value="<%=alTaskData.get(0) %>"/>
						<input type="hidden" name="timeFilledEmps" id="timeFilledEmps" value="<%=alTaskData.get(13) %>"/>
						<input type="text" id="activity_name" class="validateRequired" name="activity_name" value="<%=alTaskData.get(3) %>"/>
						</td>
					</tr>
					<tr>
						<td class="txtlabel alignRight">Select Dependency:</td>
						<td><select name="dependency" id="dependency">
								<option value="">Select Dependency</option>
								<%=alTaskData.get(4)%>
							</select>
						</td>
					</tr>
					<tr>
						<td class="txtlabel alignRight">Select Dependency Type:</td>
						<td><select name="dependencyType" id="dependencyType" class="validateRequired">
								<option value="">Select Dependency Type</option>
								<option value="0"
									<%if(alTaskData.get(5) != null && alTaskData.get(5).equals("0")) { %>
									selected <% } %>>Start-Start</option>
								<option value="1"
									<%if(alTaskData.get(5) != null && alTaskData.get(5).equals("1")) { %>
									selected <% } %>>Finish-Start</option>
							</select>
						</td>
					</tr>
					<tr>
						<td class="txtlabel alignRight">Select Priority:<sup>*</sup></td>
						<td><select name="priority" id="priority" class="validateRequired">
								<% for(GetPriorityList getPriorityList: priorityList) { %>
								<option value="<%=getPriorityList.getPriId() %>"
									<%if(alTaskData.get(6) != null && getPriorityList.getPriId().equals(alTaskData.get(6))) { %>
									selected <%} %>>
									<%=getPriorityList.getProName() %></option>
								<% } %>
							</select>
						</td>
					</tr>
	
					<tr>
						<td class="txtlabel alignRight">Select Skills:</td>
						<td><select name="empSkills" id="empSkills" onchange="getSkillwiseEmployee(this.value);"
						<% if(fromPage != null && fromPage.equals("MP")) { %> disabled="disabled" <% } %>>
								<option value="">Select Skill</option>
								<% for(FillSkills fillEmpSkillList: empSkillList) { %>
								<option value="<%=fillEmpSkillList.getSkillsId() %>"
									<%if(alTaskData.get(7) != null && fillEmpSkillList.getSkillsId().equals(alTaskData.get(7))) { %>
									selected <%} %>>
									<%=fillEmpSkillList.getSkillsName() %></option>
								<% } %>
							</select>
						</td>
					</tr>
					
					<tr>
						<td class="txtlabel alignRight">Select Employee:<sup>*</sup></td>
						<td><span id="empSpan">
								<% if(fromPage != null && fromPage.equals("MP")) { %>
									<select name="emp_id" id="emp_id" class="validateRequired">
										<%=alTaskData.get(8)%>
									</select>
								<% } else { %>
								<select name="emp_id" id="emp_id" class="validateRequired" multiple size="3">
									<option value="">Select Employee</option>
									<%=alTaskData.get(8)%>
								</select>	
								<% } %>
							 </span>
						</td>
					</tr>
					<%-- <tr>
						<td class="txtlabel alignRight" valign="top">Description<sup>*</sup></td>
						<td colspan="2"> <textarea name="comment" class="validate[required]" rows="5" cols="50"></textarea> </td>
					</tr> --%>
			<% 
			String strStartDate = (String) request.getAttribute("pro_startDate");
			String strEndDate = (String) request.getAttribute("pro_deadline");
			String proBillingType = (String) request.getAttribute("proBillingType");
			String strITime = "Hrs";
			if(proBillingType != null && proBillingType.equals("D")) {
				strITime = "Days";
			} else if(proBillingType != null && proBillingType.equals("M")) {
				strITime = "Months";
			}
			%>
					<tr>
						<td class="txtlabel alignRight">Start Date:<sup>*</sup></td>
						<td><input type="text" class="validateRequired" id="startDate" name="startDate" value="<%=alTaskData.get(9) %>" onchange="compStartDate();" /></td>
						<td><div style="color:red;" id="err_strt_date"></div></td>
					</tr>
	
					<tr>
						<td class="txtlabel alignRight">Deadline:<sup>*</sup></td>
						<td><input type="text" class="validateRequired" id="deadline2" name="deadline" value="<%=alTaskData.get(10) %>" onchange="compEndDate();" /></td>
						<td><div style="color:red;" id="err_end_date"></div></td>
					</tr>
					<tr>
						<td class="txtlabel alignRight">Ideal time(<%=strITime %>):<sup>*</sup></td>
						<td><input type="text" class="validateRequired" id="idealtime" name="idealtime" value="<%=alTaskData.get(11) %>"/></td>
					</tr>
					<tr>
				<td class="txtlabel alignRight">Choose Color:<sup>*</sup></td>
				<td><input type="text" name="colourCode" id="colourCode" value="<%=alTaskData.get(12) %>" class="validateRequired" style="background-color: <%=alTaskData.get(12) %>" readonly="readonly"/>
				<img align="left" style="cursor: pointer;position:absolute; padding:5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick= "cp2.select(document.getElementById('frmAddNewActivity').colourCode,'pick1'); return false;"/>
				<span class="hint ml_25">Choose a colour for this holiday. This colour will be marked in timesheets and clock entries.
					<span class="hint-pointer">&nbsp;</span>
				</span> </td>
			</tr>
			<!-- <tr>
				<td colspan="2" align="center">
					<input type="hidden" value="Submit" name="start">
					<input type="button" class="input_button" value="Submit" onclick="checkTimeFilledEmp();">
				</td>
			</tr> -->	
			</table>
			</div>
		<% } else { %>
			<div style="float: left" id="tblDiv"> 
				<table class="formcss">
					<tr>
						<td class="txtlabel alignRight"><%=strTorSTLbl %> Name:<sup>*</sup></td>
						<td>
						<input type="hidden" name="taskID" id="taskID"/>
						<input type="hidden" name="timeFilledEmps" id="timeFilledEmps"/>
						<input type="text" id="activity_name" class="validateRequired" name="activity_name"/>
						</td>
					</tr>

					<tr>
						<td class="txtlabel alignRight">Select Dependency:</td>
						<td>
						<s:select name="dependency" id="dependency" listKey="dependencyId" headerKey="" headerValue="Select Dependency" 
							listValue="dependencyName" list="dependencyList" key="" />
						</td>
					</tr>

					<tr>
						<td class="txtlabel alignRight">Select Dependency Type:</td>
						<td>
						<s:select name="dependencyType" id="dependencyType" listKey="dependancyTypeId" headerKey="" headerValue="Select Dependency Type"
								listValue="dependancyTypeName" list="dependancyTypeList" key="" />
						</td>
					</tr>

					<tr>
						<td class="txtlabel alignRight">Select Priority:<sup>*</sup></td>
						<td><s:select label="Select Priority" name="priority" id="priority0" cssClass="validateRequired" listKey="priId"
								listValue="proName" list="priorityList" key="" />
						</td>
					</tr>

					<tr>
						<td class="txtlabel alignRight">Select Skills:</td>
						<td>
						<% if(fromPage != null && fromPage.equals("MP")) { %>
							<s:select label="Select Skill" name="empSkills" id="empSkills" listKey="skillsId" listValue="skillsName" headerKey="" 
								headerValue="Select Skill" list="empSkillList" onchange="getSkillwiseEmployee(this.value);" disabled="true"/>
						<% } else { %>
							<s:select label="Select Skill" name="empSkills" id="empSkills" listKey="skillsId" listValue="skillsName" headerKey="" 
								headerValue="Select Skill" list="empSkillList" onchange="getSkillwiseEmployee(this.value);" />
						<% } %>	
							
						</td>
					</tr>
					
					<tr>
						<td class="txtlabel alignRight">Select Employee:<sup>*</sup></td>
						<td><span id="empSpan">
						<s:select label="Select Employee" name="emp_id" id="emp_id" cssClass="validateRequired" listKey="TaskEmployeeId"
							listValue="TaskEmployeeName" headerKey="" headerValue="Select Employee" list="TaskEmpNamesList" multiple="true" size="3" />
							</span>
						</td>
					</tr>
					<%-- <tr>
						<td class="txtlabel alignRight" valign="top">Description<sup>*</sup></td>
						<td colspan="2"> <textarea name="comment" class="validate[required]" rows="5" cols="50"></textarea> </td>
					</tr> --%>
			<% 
			String strStartDate = (String) request.getAttribute("pro_startDate");
			String strEndDate = (String) request.getAttribute("pro_deadline");
			String proBillingType = (String) request.getAttribute("proBillingType");
			String strITime = "Hrs";
			if(proBillingType != null && proBillingType.equals("D")) {
				strITime = "Days";
			} else if(proBillingType != null && proBillingType.equals("M")) {
				strITime = "Months";
			}
			%>
					<tr>
						<td class="txtlabel alignRight">Start Date:<sup>*</sup></td>
						<td><input type="text" class="validateRequired" id="startDate" name="startDate" onchange="compStartDate();" /></td>
						<td><div style="color:red;" id="err_strt_date"></div></td>
					</tr>
	
					<tr>
						<td class="txtlabel alignRight">Deadline:<sup>*</sup></td>
						<td><input type="text" class="validateRequired" id="deadline2" name="deadline" onchange="compEndDate();" /></td>
						<td><div style="color:red;" id="err_end_date"></div></td>
					</tr>
					<tr>
						<td class="txtlabel alignRight">Ideal time(<%=strITime %>):<sup>*</sup></td>
						<td><input type="text" class="validateRequired" id="idealtime" name="idealtime" /></td>
					</tr>
					<tr>
				<td class="txtlabel alignRight">Choose Color:<sup>*</sup></td>
				<td><input type="text" name="colourCode" id="colourCode" class="validateRequired" readonly="readonly"/>
					<img align="left" style="cursor: pointer; position: absolute; padding: 5px 0 0 5px;" src="images1/color_palate.png" id="pick1" onclick= "cp2.select(document.getElementById('frmAddNewActivity').colourCode,'pick1'); return false;"/>
					<span class="hint ml_25">Choose a colour for this holiday. This colour will be marked in timesheets and clock entries.
					<span class="hint-pointer">&nbsp;</span>
				</span></td>
			</tr>
			<!-- <tr>
				<td colspan="2" align="center">
					<input type="button" class="input_button" value="Submit" onclick="checkTimeFilledEmp();">
				</td>
			</tr> -->
			</table>
			</div>
		<% } %>
		<div class="clr"></div>
		<div style="margin: 0px 0px 0px 210px">
			<table class="formcss">
				<tr>
					<td class="txtlabel alignLeft">
					<input type="hidden" value="Submit" name="start">
					<s:submit value="Submit" cssClass="input_button" name="submit"></s:submit>
					<!-- <input type="button" class="input_button" value="Submit" onclick="checkTimeFilledEmp();"> -->
					</td>
					<td></td>
				</tr>
			</table>
		</div>
	</s:form>

</div>



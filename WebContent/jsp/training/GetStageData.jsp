<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
	
	
	<% 
	List<String> trainingList = (List<String>)request.getAttribute("trainingList");
	String count = (String)request.getAttribute("count");
	//System.out.println("trainingList ===> "+ trainingList);
	%>
	<table border="0" class="formcss" style="width: 95%;">
		<tr>
			<td class="txtlabel" align="right">Learning Stage Type:</td>
			<td colspan="3">
			<input type="hidden" name="lstagetypeid" value="<%=trainingList.get(0) %>" />
			<%-- <s:hidden name="lstagetypeid" ></s:hidden> --%>
			<%-- <s:textfield name="lstagetype" id="lstagetype" cssStyle="width: 320px;" readonly="true"/></td> --%>
			<input type="text" name="lstagetype" id="lstagetype<%=count %>" value="<%=trainingList.get(2) %>" style="width: 220px;" readonly="readonly"/>
		</tr>
		<tr>
			<td class="txtlabel" align="right">Learning Stage Name:</td>
			<td colspan="3">
			<input type="text" name="lstagename" id="lstagename<%=count %>" value="<%=trainingList.get(1) %>" style="width: 220px;" readonly="readonly"/>
			<%-- <s:textfield name="lstagename" id="lstagename" cssStyle="width: 320px;" readonly="true"/> --%>
			</td>
		</tr>
		<tr>
			<td class="txtlabel" align="right">Start Date:<sup>*</sup></td>
			<td>
			<input type="text" name="startdate" id="startdate<%=count %>" class="validateRequired text-input" style="width: 100px;"/>
			<%-- <s:textfield name="startdate" id="startdate" cssClass="validateRequired text-input" cssStyle="width: 110px;"/> --%>
			</td>
			<td class="txtlabel" align="right">End Date:<sup>*</sup></td>
			<td>
			<input type="text" name="enddate" id="enddate<%=count %>" class="validateRequired text-input" style="width: 100px;"/>
			<%-- <s:textfield name="enddate" id="enddate" cssClass="validateRequired text-input" cssStyle="width: 110px;"/> --%>
			</td>
		</tr>
		<tr>
			<td class="txtlabel" align="right">
			<input type="checkbox" name="everyday" id="everyday<%=count %>" onclick="checkAllDays('<%=count %>');" value="everyday">Everyday &nbsp;
			<td class="txtlabel" colspan="3">
			<input type="checkbox" name="weekdays" id="weekdays<%=count %>" onclick="uncheckAllDays('<%=count %>');" value="Mon">Mon
			<input type="checkbox" name="weekdays" id="weekdays<%=count %>" onclick="uncheckAllDays('<%=count %>');" value="Tue">Tue
			<input type="checkbox" name="weekdays" id="weekdays<%=count %>" onclick="uncheckAllDays('<%=count %>');" value="Wed">Wed
			<input type="checkbox" name="weekdays" id="weekdays<%=count %>" onclick="uncheckAllDays('<%=count %>');" value="Thu">Thu
			<input type="checkbox" name="weekdays" id="weekdays<%=count %>" onclick="uncheckAllDays('<%=count %>');" value="Fri">Fri
			<input type="checkbox" name="weekdays" id="weekdays<%=count %>" onclick="uncheckAllDays('<%=count %>');" value="Sat">Sat
			<input type="checkbox" name="weekdays" id="weekdays<%=count %>" onclick="uncheckAllDays('<%=count %>');" value="Sun">Sun
				<%-- <s:checkbox name="everyday" id="everyday" value="Everyday" onclick="checkAllDays();">Everyday</s:checkbox>&nbsp;
				<s:checkboxlist name="weekDays" id="weekDays" list="#{'Mon':'Mon','Tue':'Tue','Wed':'Wed','Thu':'Thu','Fri':'Fri','Sat':'Sat','Sun':'Sun'}" value="weekdaysValue"/> --%>
			</td>
		</tr>
		<tr>
			<td class="txtlabel" align="right">Start Time:<sup>*</sup></td>
			<td>
			<input type="text" name="starttime" id="starttime<%=count %>" class="validateRequired text-input" style="width: 100px;"/>
			<%-- <s:textfield name="starttime" id="starttime" cssClass="validateRequired text-input" cssStyle="width: 60px;"/> --%>
			</td>
			<td class="txtlabel" align="right">End Time:<sup>*</sup></td>
			<td>
			<input type="text" name="endtime" id="endtime<%=count %>" class="validateRequired text-input" style="width: 100px;"/>
			<%-- <s:textfield name="endtime" id="endtime" cssClass="validateRequired text-input" cssStyle="width: 60px;"/> --%>
			</td>
		</tr>
	</table>
	
	
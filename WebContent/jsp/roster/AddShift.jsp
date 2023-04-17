<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillDesig"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<% UtilityFunctions uF = new UtilityFunctions(); %>

<script>
$(function(){
	var date_yest = new Date();
    var date_tom = new Date();
    date_yest.setHours(0,0,0);
    date_tom.setHours(23,59,59); 
    var shiftStartTimeMoment = date_yest;
    var shiftEndTimeMoment = date_tom;
    //console.log(shiftStartTimeMoment);
    //console.log(shiftEndTimeMoment);
    $('#shiftStartTime').datetimepicker({
    	format: 'HH:mm',
    	defaultDate: date_yest
    }).on('dp.change', function(e){ 
    	shiftStartTimeMoment = e.date._d;
    	if(new Date(shiftStartTimeMoment).getTime() > new Date(shiftEndTimeMoment).getTime()){
    		shiftEndTimeMoment.setDate(new Date(shiftEndTimeMoment).getDate()+1);
    	}
    	$('#breakEndTime').data("DateTimePicker").clear();
	    $('#breakStartTime').data("DateTimePicker").clear();
	    $('#breakStartTime').data("DateTimePicker").defaultDate(shiftStartTimeMoment);
	    $('#breakEndTime').data("DateTimePicker").defaultDate(shiftStartTimeMoment);		
    });
    
    $('#shiftEndTime').datetimepicker({
    	format: 'HH:mm',
    	defaultDate: date_tom
    }).on('dp.change', function(e){ 
    	shiftEndTimeMoment = e.date._d;
    	if(new Date(shiftStartTimeMoment).getTime() > new Date(shiftEndTimeMoment).getTime()){
    		shiftEndTimeMoment.setDate(new Date(shiftEndTimeMoment).getDate()+1);
    	}
	    $('#breakEndTime').data("DateTimePicker").clear();
	    $('#breakStartTime').data("DateTimePicker").clear();
	    $('#breakStartTime').data("DateTimePicker").defaultDate(shiftStartTimeMoment);
	    $('#breakEndTime').data("DateTimePicker").defaultDate(shiftStartTimeMoment);
    });
	
	$('#breakStartTime').datetimepicker({
		format: 'HH:mm'
    }).on('dp.change', function(e){ 
    	if(e.date._d === undefined){
			var nd = shiftEndTimeMoment;
			$('#breakEndTime').data("DateTimePicker").maxDate(false);
		}else{
			var nd = e.date;
			$('#breakEndTime').data("DateTimePicker").maxDate(false);
		}
    	$('#breakEndTime').data("DateTimePicker").minDate(nd);
    	$('#breakEndTime').data("DateTimePicker").maxDate(shiftEndTimeMoment);
    });
	
	$('#breakEndTime').datetimepicker({
		format: 'HH:mm'
	}).on('dp.change', function(e){ 
		if(e.date._d === undefined){
			var md = shiftEndTimeMoment;
			$('#breakStartTime').data("DateTimePicker").maxDate(false);
		}else{
			var md = e.date;
			$('#breakStartTime').data("DateTimePicker").maxDate(false);
		}
    	$('#breakStartTime').data("DateTimePicker").minDate(shiftStartTimeMoment);
    	$('#breakStartTime').data("DateTimePicker").maxDate(md);
    });
});


function setBlankData() {
	document.getElementById("colourCode").value = '';
}


$("#btnAddNewRowOk").click(function(){
	$(".validateRequired").prop('required',true);
});

</script> 
<script src="<%= request.getContextPath()%>/scripts/color.js" type="text/javascript"></script>
<script type="text/JavaScript">
	var cp = new ColorPicker('window'); 
	var cp2 = new ColorPicker('window');
</script> 

	<s:form theme="simple" id="formAddNewRow" action="AddShiftRoster" method="POST" cssClass="formcss">
		<s:hidden name="shiftId"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table border="0" class="table table_no_border">
			<tr>
				<th class="txtlabel alignRight">Shift Code:<sup>*</sup></th>
				<td>
					<s:textfield name="shiftCode" id="shiftCode" cssClass="validateRequired"/> 
				</td>
			</tr>
		    
		    <tr>
				<th class="txtlabel alignRight">Shift Name:<sup>*</sup></th>
				<td>
					<s:textfield name="shiftName" id="shiftName" cssClass="validateRequired"/> 
				</td>
			</tr>
			<%-- <tr>
				<th class="txtlabel alignRight">Shift Type:<sup>*</sup></th>
				<td>
					<%
						String shiftType = (String) request.getAttribute("shiftType");
						String selectReqular = "";
						String selectCustom = "";
						if(shiftType!=null && shiftType.trim().equals("Regular")){
							selectReqular = "selected";
							selectCustom = "";
						} else if(shiftType!=null && shiftType.trim().equals("Custom")){
							selectReqular = "";
							selectCustom = "selected";
						}
					%>
					<select name="shiftType" id="shiftType" class="validateRequired">
						<option value="Regular" <%=selectReqular %>>Regular</option>
						<option value="Custom" <%=selectCustom %>>Custom</option>
					</select>
				</td>
			</tr> --%> 
			<tr>
				<th class="txtlabel alignRight">Shift start time:<sup>*</sup></th>
				<td>
					<s:textfield name="shiftStartTime" id="shiftStartTime" cssClass="validateRequired readonly"/> 
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Shift end time:<sup>*</sup></th>
				<td>
					<s:textfield name="shiftEndTime" id="shiftEndTime" cssClass="validateRequired readonly"/> 
				</td>
			</tr>
			<tr> 
				<th class="txtlabel alignRight">Break start time:<sup>*</sup></th>
				<td>
					<s:textfield name="breakStartTime" id="breakStartTime" cssClass="validateRequired readonly"/> 
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Break end time:<sup>*</sup></th>
				<td>
					<s:textfield name="breakEndTime" id="breakEndTime" cssClass="validateRequired readonly"/> 
				</td>
			</tr>
	
			<tr>
				<th class="txtlabel alignRight">Choose Colour:<sup>*</sup></th>
				<td>
					<%
						String colourCode = (String) request.getAttribute("colourCode");
						String strColorStyle = "";
						if(colourCode!=null && !colourCode.trim().equals("")){
							strColorStyle = "style=\"background-color: "+colourCode+";\"";
						}
					%>
					<input type="text" name="colourCode" id="colourCode" onkeydown="e.preventDefault();" class="validateRequired" value="<%=uF.showData(colourCode,"")%>" <%=strColorStyle %> onkeyup="setBlankData();"/>
					<img align="left" style="cursor: pointer;position:absolute; padding:5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick= "cp2.select(document.getElementById('formAddNewRow').colourCode,'pick1'); return false;"/>
				</td>
			</tr>	
	
			<tr>
				<td class="txtlabel" style="width: 25%;">&nbsp;</td>
				<td>
					<s:submit  cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
				</td>
			</tr>
		</table>	
	</s:form>


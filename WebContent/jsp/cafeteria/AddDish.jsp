<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="com.konnect.jpms.select.FillUserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="scripts/customAjax.js" ></script>
<script>
function readImageURL(input, targetDiv) {
	//alert("notice targetDiv==>"+targetDiv);
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('#'+targetDiv)
                .attr('src', e.target.result)
                .width(60)
                .height(60);
        };
        reader.readAsDataURL(input.files[0]);
    }
}
 
function readImageURL(input, targetDiv) {
	//alert("notice targetDiv==>"+targetDiv);
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('#'+targetDiv)
                .attr('src', e.target.result)
                .width(60)
                .height(60);
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function getLocationOrg(orgid){
	var action='GetLocationOrg.action?strOrg='+orgid ;
	getContent('locationdivid', action);
}
</script>
<%

String dishImgPath = (String) request.getAttribute("dishImgPath");
String dImage = (String) request.getAttribute("dImage");
String dishImage = (String) request.getAttribute("dishImage");
String operation = (String)request.getAttribute("operation");
String dishId = (String)request.getAttribute("dishId");

%>

	<div id="printDiv" class="leftbox reportWidth">
		<s:form id="frmAddDish" name ="frmAddDish" action="AddDish" method="POST" theme="simple" cssClass="formcss" enctype="multipart/form-data">
			<s:hidden name="dishId" />
			<s:hidden name="operation" />
				<table class="table table_no_border form-table">
				    <tr>
						<td class="txtlabel alignRight">Select Organization:<sup>*</sup></td>
						<td>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" 
								onchange="getLocationOrg(this.value);" cssClass="validateRequired" list="orgList" />
						</td>
					</tr>	
					<tr>
						<td class="txtlabel alignRight">Select Location:<sup>*</sup></td>
						<td>
							<div id="locationdivid">
							<s:select cssClass="validateRequired" name="location" id="locationid" theme="simple" listKey="wLocationId" 
								listValue="wLocationName" headerKey="" headerValue="Select Location" list="wLocationList" />
							</div>
						</td>
					</tr>	
				    <tr>
						<td class="txtlabel alignRight">Dish Name:<sup>*</sup></td>
						<td><s:textfield  name="strDishName" id="strDishName" cssClass="validateRequired" /></td>
					</tr>	
					<tr>
						<td class="txtlabel alignRight">Dish Type:<sup>*</sup></td>
						<td>
							<s:select name="mealType" listKey="mealTypeId" listValue="mealTypeName" headerKey="" 
						 		headerValue="Select Meal Type" list="mealTypeList" key="" cssClass="validateRequired" />
						</td>
					</tr>	
					<tr>
						<td class="txtlabel alignRight">From Date:<sup>*</sup></td>
						<td><s:textfield name="strFromDate" id="strFromDate" cssClass="validateRequired"/></td>
					</tr>	
					
					<tr>
						<td class="txtlabel alignRight">To Date:<sup>*</sup></td>
						<td><s:textfield name="strToDate" id="strToDate" cssClass="validateRequired"/></td>
					</tr>	
					 
					<tr>
						<td class="txtlabel alignRight">Between(Time):<sup>*</sup></td>
						<td>From: &nbsp;<s:textfield name="strFromTime" id="strFromTime" cssStyle="width: 55px !important;" cssClass="validateRequired"/>
							&nbsp;&nbsp;&nbsp;&nbsp;
							To: &nbsp;<s:textfield name="strToTime" id="strToTime" cssStyle="width: 55px !important;" cssClass="validateRequired"/>
						</td>
					</tr>
					<tr>
						<td class="txtlabel alignRight">Cost to Company:</td>
						<td>
							<s:textfield name="strDishPrice" id="strDishPrice" onkeypress="return isNumberKey(event)"></s:textfield>
						</td>
					</tr>	
					
					 <tr>
						<td class="txtlabel alignRight">Short Description:</td>
						<td><s:textarea rows="3" name="strDishComment" id="strDishComment" cssStyle="width: 205px;" /></td>
					 </tr>	
					
			         <tr>
						<td class="txtlabel alignRight">Take a picture:</td>
						<% if(dishImgPath!=null && !dishImgPath.equals("")) { %>
							<td>
								<div id="tblDiv" style="float: left;"><%=dishImage%></div>
								<div style="float: left; margin-left: 10px; width: 66%;"><span style="float: left; width: 100%;">
									<input type="file" accept = ".gif,.jpg,.png,.tif,.svg, .svgz" id="strDishImage" name="strDishImage" size="5" style="height: 22px; margin-top: 10px; vertical-align: top; font-size: 11px;" onchange="readImageURL(this, 'dishImage');"></span>
									<span style="float: left; font-size: 11px;">Best size of picture is 300px X 300px</span>
								</div>
							</td>
						<% } else { %>
							<td>
								<img height="62" width="70" class="lazy" id="dishImage" style="float: left; border: 1px solid #CCCCCC;" src="userImages/dishe_avatar_photo.png" />
								<div style="float: left; margin-left: 10px;"><span style="float: left; width: 100%;">
									<input type="file"  accept = ".gif,.jpg,.png,.tif,.svg, .svgz" id="strDishImage" name="strDishImage" size="5" style="height: 22px; margin-top: 10px; vertical-align: top; font-size: 11px;" onchange="readImageURL(this, 'dishImage');"></span>
									<span style="float: left; font-size: 11px;">Best size of picture is 300px X 300px</span>
								</div>
							</td>
						<% } %>
					</tr>	
					
					<tr>
						<td></td>
					 	<td>
						<%if(operation !=null && operation.equals("E")) { %> 	
						 	<s:submit name="strUpdate" cssClass="btn btn-primary" value="Update" align="center"/>
						 	<!-- <input type="button" class="btn btn-danger" name="strCancel" onclick="closeEditDishPopup();" value="Cancel"> -->
						<% } else { %>
							<s:submit name="strSubmit" cssClass="btn btn-primary" value="Submit" align="center"/>
							<!-- <input type="button" class="btn btn-danger" name="strCancel" onclick="closeAddDishPopup();" value="Cancel"> -->
						<% } %>	 	
					 	</td>
					</tr>
					 
				</table>
		</s:form>
	</div>
	
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
 --%>


<script>
$(function() {

	$("#strFromDate").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {	
        var minDate = new Date(selected.date.valueOf());
        $('#strToDate').datepicker('setStartDate', minDate);
    });
    
    $("#strToDate").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#strFromDate').datepicker('setEndDate', minDate);
    });
	
    var date_yest = new Date();
    var date_tom = new Date();
    date_yest.setHours(0,0,0);
    date_tom.setHours(23,59,59); 
   
	$('#strFromTime').datetimepicker({ 
		format: 'HH:mm',
		minDate: date_yest,
		defaultDate: date_yest
    }).on('dp.change', function(e){ 
    	$('#strToTime').data("DateTimePicker").minDate(e.date);
    });
	
	$('#strToTime').datetimepicker({
		format: 'HH:mm',
		maxDate: date_tom,
		defaultDate: date_tom
    }).on('dp.change', function(e){ 
    	$('#strFromTime').data("DateTimePicker").maxDate(e.date);
    });
});	
	
	//$("#strDishPrice").prop('required',true);

	$("#frmAddDish_strSubmit").click(function(){
		$(".validateRequired").prop('required',true);
	}); 
	$(".btn-danger").click(function(){$("#modalInfo").hide();});
	
</script>

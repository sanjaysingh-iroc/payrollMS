<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script src="scripts/customAjax.js"></script>
<script>
    $(function() {
        $( "#effectiveDate" ).datepicker({format: 'dd/mm/yyyy'});
        $("#frmArear_0").click(function(){
        	$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
			$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
        });
        
        
        $("body").on("click", "#arrearSubmit", function(){ 
        	$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
			$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
	    });
	    
	    $("body").on("click", "#submitButton", function(){
	    	$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
			$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
	    });
	    
    });
    
    function amountCheck(id) {
    	var strAmt = document.getElementById(id).value;
    	if(parseFloat(strAmt) > 0) {
    		strAmt = parseFloat(strAmt).toFixed(0);
    	} else {
    		strAmt = 0;
    	}
    	document.getElementById(id).value = strAmt;
    }
    
    
    $("#frmAddArear").submit(function(e){
		e.preventDefault();
		var form_data = $("form[name='frmAddArear']").serialize();
		//alert("form_data ===>> " + form_data);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
			type : 'POST',
			url : "GetArrearEmployees.action",
			data: form_data,
			cache : false,
			success : function(res) {
				$("#divResult").html(res);
			},
			error : function(res) {
				$.ajax({
					url: 'PayArrears.action',
					cache: true,
					success: function(result){
						$("#divResult").html(result);
			   		}
				});
			}
			
		});
    	
    	
    	
	});
    
    function getEmployeeList(action){
    	$.ajax({
			url : action,
			type: 'GET',
			success : function(res) {
				$("#myDiv").html(res);
				$("#strArrearEmpId").multiselect().multiselectfilter();
			}
		});
    }
    
    function changeType() {
    	//trBasicAmt trArrearAmt trArrearDays trArrearEffectiveDate trArrearPaycycle
    	var arrearType = $('input[name=arrearType]:checked').val();
    	//var arrearType = $('input[name=arrearType]').val();
    	if (arrearType == '1') {
    		document.getElementById("trBasicAmt").style.display = "none";
    		document.getElementById("trArrearAmt").style.display = "none";
    		document.getElementById("trArrearEffectiveDate").style.display = "none";
    		document.getElementById("trArrearDays").style.display = "table-row";
    		document.getElementById("trArrearPaycycle").style.display = "table-row";
    	} else {
    		document.getElementById("trBasicAmt").style.display = "table-row";
    		document.getElementById("trArrearAmt").style.display = "table-row";
    		document.getElementById("trArrearEffectiveDate").style.display = "table-row";
    		document.getElementById("trArrearDays").style.display = "none";
    		document.getElementById("trArrearPaycycle").style.display = "none";
    	}
    }
</script>

<div id="printDiv" class="leftbox reportWidth">
    <%if(request.getAttribute("strArrearId")==null || (request.getAttribute("strArrearId")!=null && ((String)request.getAttribute("strArrearId")).length()<=0)){ %>
    <s:form name="frm_AddArear" action="" theme="simple" method="post">
    	<div class="box box-default collapsed-box" style="margin-top: 10px;">
            <div class="box-header with-border">
                <h3 class="box-title" style="font-size: 14px;padding-right: 10px;">FIlter</h3>
                <div class="box-tools pull-right">
                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                </div>
            </div>
            <!-- /.box-header -->
            <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                <div class="row row_without_margin">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-filter"></i>
					</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5 allmargin5">
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
		                    listValue="orgName"
		                    onchange="getOrgLocationDepartLevelDesigGrade(this.value);" list="organisationList"
		                    key=""/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5 allmargin5" id="wlocDiv">
							<s:select theme="simple" name="f_WLocation" id="f_WLocation" listKey="wLocationId"
		                    listValue="wLocationName" headerKey="-1" headerValue="All Locations"
		                    list="wLocationList" key="" onchange="getEmployeeList('GetArrearEmployees.action?f_org='+document.frm_AddArear.f_org.options[document.frm_AddArear.f_org.selectedIndex].value+'&wlocationId='+document.frm_AddArear.f_WLocation.options[document.frm_AddArear.f_WLocation.selectedIndex].value+'&departmentId='+document.frm_AddArear.f_department.options[document.frm_AddArear.f_department.selectedIndex].value+'&serviceId='+document.frm_AddArear.f_service.options[document.frm_AddArear.f_service.selectedIndex].value+'&levelId='+document.frm_AddArear.f_level.options[document.frm_AddArear.f_level.selectedIndex].value)"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5 allmargin5" id="deptDiv">
							<s:select theme="simple" name="f_department" id="f_department" listKey="deptId"
		                    listValue="deptName" headerKey="-1" headerValue="All Departments"
		                    list="departmentList" key="" onchange="getEmployeeList('GetArrearEmployees.action?f_org='+document.frm_AddArear.f_org.options[document.frm_AddArear.f_org.selectedIndex].value+'&wlocationId='+document.frm_AddArear.f_WLocation.options[document.frm_AddArear.f_WLocation.selectedIndex].value+'&departmentId='+document.frm_AddArear.f_department.options[document.frm_AddArear.f_department.selectedIndex].value+'&serviceId='+document.frm_AddArear.f_service.options[document.frm_AddArear.f_service.selectedIndex].value+'&levelId='+document.frm_AddArear.f_level.options[document.frm_AddArear.f_level.selectedIndex].value)"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5 allmargin5" id="serviceDiv">
							<s:select theme="simple" name="f_service" id="f_service" listKey="serviceId"
		                    listValue="serviceName" headerKey="-1" headerValue="All Services"
		                    list="serviceList" key="" onchange="getEmployeeList('GetArrearEmployees.action?f_org='+document.frm_AddArear.f_org.options[document.frm_AddArear.f_org.selectedIndex].value+'&wlocationId='+document.frm_AddArear.f_WLocation.options[document.frm_AddArear.f_WLocation.selectedIndex].value+'&departmentId='+document.frm_AddArear.f_department.options[document.frm_AddArear.f_department.selectedIndex].value+'&serviceId='+document.frm_AddArear.f_service.options[document.frm_AddArear.f_service.selectedIndex].value+'&levelId='+document.frm_AddArear.f_level.options[document.frm_AddArear.f_level.selectedIndex].value)"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5 allmargin5" id="levelDiv">
							<s:select theme="simple" name="f_level" id="f_level" listKey="levelId"
		                    listValue="levelCodeName" headerKey="-1" headerValue="All Levels"
		                    list="levelList" key="" onchange="getEmployeeList('GetArrearEmployees.action?f_org='+document.frm_AddArear.f_org.options[document.frm_AddArear.f_org.selectedIndex].value+'&wlocationId='+document.frm_AddArear.f_WLocation.options[document.frm_AddArear.f_WLocation.selectedIndex].value+'&departmentId='+document.frm_AddArear.f_department.options[document.frm_AddArear.f_department.selectedIndex].value+'&serviceId='+document.frm_AddArear.f_service.options[document.frm_AddArear.f_service.selectedIndex].value+'&levelId='+document.frm_AddArear.f_level.options[document.frm_AddArear.f_level.selectedIndex].value)"/>
						</div>
					</div>
				</div>
            </div>
            <!-- /.box-body -->
        </div>
    </s:form>
    <%} %>
    <div class="row row_without_margin"> 
    	<s:form name="frmAddArear" action="GetArrearEmployees" theme="simple" method="post" id="frmAddArear">
	    	<div class="col-lg-12">
		        <s:hidden name="strArrearId" ></s:hidden>
		        <table class="table table_no_border form-table">
		        	<%if(request.getAttribute("strArrearId")==null || (request.getAttribute("strArrearId")!=null && ((String)request.getAttribute("strArrearId")).length()<=0)){ %>
			            <tr>
			            	<td>Select Employee/s:<sup>*</sup></td>
			            	<td>
			            		<div id="myDiv"><div id="ajaxLoadImage"></div></div>
			            	</td>
			            </tr>
		            <% } %>
		            <tr>
		                <td>Arrear Code:</td>
		                <td><s:textfield name="strArearCode"></s:textfield></td>
		            </tr>
		            <tr>
		                <td>Arrear Name:</td>
		                <td><s:textfield name="strArearName"></s:textfield></td>
		            </tr>
		            <tr>
		                <td>Arrear Description:</td>
		                <td><s:textarea name="strArearDesc" rows="2" cols="22"></s:textarea></td>
		            </tr>
		            <tr>
						<td>Arrear Type:<sup>*</sup></td> 
						<td><s:radio name="arrearType" id="arrearType" list="#{'0':'Amount','1':'Days'}" value="defaultArrearType" cssClass="validateRequired" onclick="changeType();"/></td>
					</tr>
		            <tr id="trBasicAmt">
		                <td>Basic Amount(PF Only):<sup>*</sup></td>
		                <td><s:textfield name="strBasic" id="strBasic" cssClass="validateRequired" onkeyup="amountCheck(this.id)" onkeypress="return isNumberKey(event)"></s:textfield></td>
		            </tr>
		            <tr id="trArrearAmt">
		                <td>Arrear Amount:<sup>*</sup></td>
		                <td><s:textfield name="strArearAmount" cssClass="validateRequired" onkeyup="amountCheck(this.id)" onkeypress="return isNumberKey(event)"></s:textfield></td>
		            </tr> 
		            <tr id="trArrearEffectiveDate">
		                <td>Effective Date:<sup>*</sup></td>
		                <td><s:textfield name="strArearEffectiveDate" id="effectiveDate" cssClass="validateRequired"></s:textfield></td>
		            </tr>
		            
		            <tr id="trArrearDays">
		                <td>Arrear Days:<sup>*</sup></td>
		                <td><s:textfield name="strArrearDays" id="strArrearDays" cssClass="validateRequired" onkeypress="return isNumberKey(event)"></s:textfield></td>
		            </tr> 
		            
		            <tr id="trArrearPaycycle">
		                <td>Arrear Paycycle:<sup>*</sup></td>
		                <td><s:select name="arrearPaycycle" id="arrearPaycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key="" cssClass="validateRequired"/></td>
		            </tr>		            
		            
		            <tr>
		            	<td>&nbsp;</td>
		                <td><input class="btn btn-primary" id="arrearSubmit" type="submit" value="Save"/></td>
		            </tr>
		        </table>
	    	</div>
    	</s:form>
    </div>
    
    <%if(request.getAttribute("strArrearId")==null || (request.getAttribute("strArrearId")!=null && ((String)request.getAttribute("strArrearId")).length()<=0)){ %>
    <script type="text/javascript">
		getEmployeeList('GetArrearEmployees.action?wlocationId='+document.frm_AddArear.f_WLocation.options[document.frm_AddArear.f_WLocation.selectedIndex].value+'&departmentId='+document.frm_AddArear.f_department.options[document.frm_AddArear.f_department.selectedIndex].value+'&serviceId='+document.frm_AddArear.f_service.options[document.frm_AddArear.f_service.selectedIndex].value+'&levelId='+document.frm_AddArear.f_level.options[document.frm_AddArear.f_level.selectedIndex].value);
    </script>
    <%} %>
</div>
<script type="text/javascript">
	
	changeType();

    function getOrgLocationDepartLevelDesigGrade(value) {
    	//alert("value ===>> " + value);
        	getEmployeebyOrg(value);
    	xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
        } else {
           var xhr = $.ajax({
             url : "GetOrgwiseData.action?strOrgId=" + value,
             cache : false,
             success : function(data) {
             	if(data == ""){
             	} else {
             		// wlocDiv	deptDiv serviceDiv 	levelDiv   
             		var allData = data.split("::::");
                    document.getElementById("wlocDiv").innerHTML = allData[0];
                    document.getElementById("deptDiv").innerHTML = allData[1];
                    document.getElementById("serviceDiv").innerHTML = allData[2];
                    document.getElementById("levelDiv").innerHTML = allData[3];
             	}
             }
           });
        }
        $(dialogEdit).dialog('close');
    }
    
    function GetXmlHttpObject() {
        if (window.XMLHttpRequest) {
           return new XMLHttpRequest();
        }
        if (window.ActiveXObject) {
           return new ActiveXObject("Microsoft.XMLHTTP");
        }
        return null;
    }	
    
    
    function getEmployeebyOrg(val){
    	var action = 'GetArrearEmployees.action?f_org='+val;
    	document.getElementById("f_WLocation").selectedIndex = 0;
    	document.getElementById("f_department").selectedIndex = 0;
    	document.getElementById("f_service").selectedIndex = 0;
    	document.getElementById("f_level").selectedIndex = 0;
    	document.getElementById("myDiv").innerHTML = '';
    	$("#myDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	getEmployeeList(action);
    }
</script>
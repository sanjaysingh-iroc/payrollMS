<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<style>
	.ul_class li {
		margin: 10px 0px 10px 100px;
	} 
</style>  
<script type="text/javascript">
$(function() {
    $("#month").multiselect({
		noneSelectedText: 'Select Month'
	}).multiselectfilter();
    $("#strOrg").multiselect({
		noneSelectedText: 'Select Organisation'
	}).multiselectfilter();
    $("#wlocation").multiselect({
		noneSelectedText: 'Select Location'
	}).multiselectfilter();
    $("#depart").multiselect({
		noneSelectedText: 'Select Department'
	}).multiselectfilter();
    $("#strLevel").multiselect({
		noneSelectedText: 'Select Level'
	}).multiselectfilter();
    $("#desigIdV").multiselect({
		noneSelectedText: 'Select Designation'
	}).multiselectfilter();
    $("#gradeIdV").multiselect({
		noneSelectedText: 'Select Grade'
	}).multiselectfilter();
    $("#employee").multiselect({
		noneSelectedText: 'Select Employee'
	}).multiselectfilter();
    
    $("#reviewerId").multiselect().multiselectfilter();
    
    $("#fromEdit").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#toEdit').datepicker('setStartDate', minDate);
    });
    
    $("#toEdit").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#fromEdit').datepicker('setEndDate', minDate);
    });
    
    checkFrequencyOnload();
    //createRevieweePanelForReview('employee');
});

function setDatepickerDefault(id1,id2){
	
    $( '#'+id1 ).datepicker({format: 'dd/mm/yyyy'});
    $( '#'+id2 ).datepicker({format: 'dd/mm/yyyy'});
}

function checkFrequencyOnload() {
	var value = document.getElementById("frequency").value;
	if (value == '3') {
		//alert("dfgsdf"); halfYearly quarterly
		document.getElementById("weekly").style.display = "none";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "block";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
		
	} else if (value == '2') {
		document.getElementById("weekly").style.display = "block";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "none";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
		
								
	}else if (value == '6') {
		document.getElementById("weekly").style.display = "none";
	//	document.getElementById("annualy").style.display = "block";
		document.getElementById("monthly").style.display = "none";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
		
	}else if (value == '4' || value == '5') {
		document.getElementById("weekly").style.display = "none";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "none";
				
	} else {
		document.getElementById("weekly").style.display = "none";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "none";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
		
	}
}

function checkFrequency(value) {
	// dayMonth  monthly annualy weekly
	//alert("value ===>> " + value);
	if (value == '3') {
		//alert("dfgsdf"); halfYearly quarterly
		document.getElementById("weekly").style.display = "none";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "block";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
		
		document.getElementById("weekday").selectedIndex = 0;
		document.getElementById("annualDay").selectedIndex = 0;
		document.getElementById("annualMonth").selectedIndex = 0;
		document.getElementById("day").selectedIndex = 0;
		document.getElementById("monthday").selectedIndex = 0;
		document.getElementById("month").selectedIndex = 0;
		
	} else if (value == '2') {
		document.getElementById("weekly").style.display = "block";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "none";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
		
		document.getElementById("weekday").selectedIndex = 0;
		document.getElementById("annualDay").selectedIndex = 0;
		document.getElementById("annualMonth").selectedIndex = 0;
		document.getElementById("day").selectedIndex = 0;
		document.getElementById("monthday").selectedIndex = 0;
		document.getElementById("month").selectedIndex = 0;
					
	}else if (value == '6') {
		document.getElementById("weekly").style.display = "none";
	//	document.getElementById("annualy").style.display = "block";
		document.getElementById("monthly").style.display = "none";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
		
		document.getElementById("weekday").selectedIndex = 0;
		document.getElementById("annualDay").selectedIndex = 0;
		document.getElementById("annualMonth").selectedIndex = 0;
		document.getElementById("day").selectedIndex = 0;
		document.getElementById("monthday").selectedIndex = 0;
		document.getElementById("month").selectedIndex = 0;
		
	}else if (value == '4' || value == '5') {
		document.getElementById("weekly").style.display = "none";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "none";
				
		document.getElementById("weekday").selectedIndex = 0;
		document.getElementById("annualDay").selectedIndex = 0;
		document.getElementById("annualMonth").selectedIndex = 0;
		document.getElementById("day").selectedIndex = 0;
		document.getElementById("monthday").selectedIndex = 0;
		document.getElementById("month").selectedIndex = 0;
		
	} else {
		document.getElementById("weekly").style.display = "none";
		document.getElementById("annualy").style.display = "none";
		document.getElementById("monthly").style.display = "none";
		document.getElementById("dayMonth").style.display = "none";
		document.getElementById("quarterly").style.display = "none";
		document.getElementById("halfYearly").style.display = "none";
		
		document.getElementById("weekday").selectedIndex = 0;
		document.getElementById("annualDay").selectedIndex = 0;
		document.getElementById("annualMonth").selectedIndex = 0;
		document.getElementById("day").selectedIndex = 0;
		document.getElementById("monthday").selectedIndex = 0;
		document.getElementById("month").selectedIndex = 0;
	}
}


function getOrgLocationDepartLevelDesigGrade() {
	//alert("value ===>> " + value);
	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
    } else {
    	var strOrg = getSelectedValue("strOrg");
        var xhr = $.ajax({
             url : "GetOrgwiseLocationDepartLevelDesigGrade.action?strOrgId=" + strOrg,
             cache : false,
             success : function(data) {
             	if(data == "") {
             		
             	} else {
             		//alert("data --------->> " + data);     
             		var allData = data.split("::::");
                    document.getElementById("wlocDiv").innerHTML = allData[0];
                    document.getElementById("departDiv").innerHTML = allData[1];
                   	document.getElementById("levelDiv").innerHTML = allData[2];
                   	document.getElementById("myDesig").innerHTML = allData[3];
                   	document.getElementById("myGrade").innerHTML = allData[4];
                	$("select[name='strOrg']").multiselect().multiselectfilter(); 
               		$("select[name='strWlocation']").multiselect().multiselectfilter(); 
               		$("select[name='strDepart']").multiselect().multiselectfilter(); 
               		$("select[name='strLevel']").multiselect().multiselectfilter(); 
               		$("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); 
               		$("select[name='empGrade']").multiselect().multiselectfilter();
                   	getEmployeebyOrg();
             	}
             }
        });
    }
}


function getLevelwiseDesigGrade(value) {
	//alert("value ===>> " + value);
	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
       alert("Browser does not support HTTP Request");
       return;
    } else {
       var xhr = $.ajax({
           url : "GetLevelwiseDesigAndGrade.action?strLevelId=" + value,
           cache : false,
           success : function(data) {
           	if(data == "") {
           		
           	} else {
           		//alert("data --------->> " + data);     
           		var allData = data.split("::::");
                  	document.getElementById("myDesig").innerHTML = allData[0];
                  	document.getElementById("myGrade").innerHTML = allData[1];
                    $("select[name='empGrade']").multiselect().multiselectfilter(); 
            	    $("select[name='strDesignationUpdate']").multiselect().multiselectfilter();
           	}
           }
       });
    }
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


function getEmployeebyOrg(){
	var strOrg = getSelectedValue("strOrg");
	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
       alert("Browser does not support HTTP Request");
       return;
    } else {
    	var action = 'getEmployeeList.action?strOrg='+strOrg; // +'&page=SOrient' + "&type="+type
       	var xhr = $.ajax({
           	url : action,
           	cache : false,
           	success : function(data) {
	           	if(data == "") {
	           	} else {
	           		//alert("data ===>> " + data);
                  	document.getElementById("myEmployee").innerHTML = data;
                  	$("select[name='employee']").multiselect().multiselectfilter();
	           	}
           	}
       	});
    }
	/* var action = 'getEmployeeList.action?strOrg=' + strOrg;
	getContent('myEmployee', action);
	setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500); */
}


function getEmployeebyLocation() {
	var location = getSelectedValue("wlocation");
	var strOrg = getSelectedValue("strOrg");
	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
       alert("Browser does not support HTTP Request");
       return;
    } else {
    	var action = 'getEmployeeList.action?strOrg='+ strOrg+'&location='+ location;
       	var xhr = $.ajax({
           	url : action,
           	cache : false,
           	success : function(data) {
	           	if(data == "") {
	           	} else {
                  	document.getElementById("myEmployee").innerHTML = data;
    		   		$("select[name='employee']").multiselect().multiselectfilter();   
	           	}
           	}
       	});
    }
	
    /* var action = 'getEmployeeList.action?strOrg='+ strOrg+'&location='+ location;
	getContent('myEmployee', action);
	setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
	setTimeout(function(){ $("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); }, 500); */
}


function getEmployeebyDepart() {
	var strOrg = getSelectedValue("strOrg");
	var location = getSelectedValue("wlocation");
	var depart = getSelectedValue("depart");
	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
       alert("Browser does not support HTTP Request");
       return;
    } else {
    	var action = 'getEmployeeList.action?depart=' + depart+ '&strOrg='+ strOrg;
    	if (location == '') {
		} else {
			if (location != '') {
				action += '&location=' + location;
			}
		}
       	var xhr = $.ajax({
           	url : action,
           	cache : false,
           	success : function(data) {
	           	if(data == "") {
	           	} else {
                  	document.getElementById("myEmployee").innerHTML = data;
    		   		$("select[name='employee']").multiselect().multiselectfilter();   
	           	}
           	}
       	});
    }
    
	/* var action = 'getEmployeeList.action?depart=' + depart + '&strOrg='+ strOrg+'&location='+ location;
	getContent('myEmployee', action);
	setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500); */
}


function getEmployeebyLevel() {
	var strOrg = getSelectedValue("strOrg");
	var location = getSelectedValue("wlocation");
	var depart = getSelectedValue("depart");
	var Level = getSelectedValue("strLevel");

	getLevelwiseDesigGrade(Level);
	
	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
       alert("Browser does not support HTTP Request");
       return;
    } else {
    	var action = 'getEmployeeList.action?level=' + Level;
		if (location == '' && depart == '') {
		} else {
			if (location != '') {
				action += '&location=' + location;
			}
			if (depart != '') {
				action += '&depart=' + depart;
			}
		}
       	var xhr = $.ajax({
           	url : action,
           	cache : false,
           	success : function(data) {
	           	if(data == "") {
	           	} else {
                  	document.getElementById("myEmployee").innerHTML = data;
    		   		$("select[name='employee']").multiselect().multiselectfilter();  
	           	}
           	}
       	});
    }
    
	/* var action = 'getEmployeeList.action?level=' + Level +'&depart=' + depart + '&strOrg='+ strOrg+'&location='+ location;
	getContent('myEmployee', action);
	setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500); */
}


function getEmployeebyDesig() {
	var strOrg = getSelectedValue("strOrg");
	var location = getSelectedValue("wlocation");
	var depart = getSelectedValue("depart");
	var Level = getSelectedValue("strLevel");
	var design = getSelectedValue("desigIdV");

	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
       alert("Browser does not support HTTP Request");
       return;
    } else {
    	var action = 'getEmployeeList.action?design=' + design;
		if (location == '' && depart == '' && Level == '') {
		} else {
			if (location != '') {
				action += '&location=' + location;
			}
			if (depart != '') {
				action += '&depart=' + depart;
			}
			if (Level != '') {
				action += '&level=' + Level;
			}
		}
       	var xhr = $.ajax({
           	url : action,
           	cache : false,
           	success : function(data) {
	           	if(data == "") {
	           	} else {
                  	document.getElementById("myEmployee").innerHTML = data;
    		   		$("select[name='employee']").multiselect().multiselectfilter();  
	           	}
           	}
       	});
    }
	
    
    xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
       alert("Browser does not support HTTP Request");
       return;
    } else {
    	var action = 'getGrade.action?strDesignation=' + design;
       	var xhr = $.ajax({
           	url : action,
           	cache : false,
           	success : function(data) {
	           	if(data == "") {
	           	} else {
                  	document.getElementById("myGrade").innerHTML = data;
                  	$("select[name='empGrade']").multiselect().multiselectfilter();  
	           	}
           	}
       	});
    }

	/* var action = 'getEmployeeList.action?design=' + design+'&level=' + Level +'&depart=' + depart + '&strOrg='+ strOrg+'&location='+ location;
	getContent('myEmployee', action);
	setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500);
	getContent('myGrade', 'getGrade.action?strDesignation=' + design);
	setTimeout(function(){ $("select[name='empGrade']").multiselect().multiselectfilter(); }, 500); */
}


function getEmployeebyGrade() {
	var strOrg = getSelectedValue("strOrg");
	var location = getSelectedValue("wlocation");
	var depart = getSelectedValue("depart");
	var Level = getSelectedValue("strLevel");
	var design = getSelectedValue("desigIdV");
	var grade = getSelectedValue("gradeIdV");

	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
       alert("Browser does not support HTTP Request");
       return;
    } else {
    	var action = 'getEmployeeList.action?grade=' + grade;
		if (location != '')  {
			action += '&location=' + location;
		}if (depart != '')  {
			action += '&depart=' + depart;
		}if (Level != '')  {
			action += '&level=' + Level;
		}if (design != '')  {
			action += '&design=' + design;
		}
       	var xhr = $.ajax({
           	url : action,
           	cache : false,
           	success : function(data) {
	           	if(data == "") {
	           	} else {
                  	document.getElementById("myEmployee").innerHTML = data;
                  	$("select[name='employee']").multiselect().multiselectfilter();  
	           	}
           	}
       	});
    }
    
	/* var action = 'getEmployeeList.action?grade=' + grade +'&design=' + design+'&level=' + Level +'&depart=' + depart + '&strOrg='+ strOrg+'&location='+ location;
	document.getElementById("employee").selectedIndex = 0;
	getContent('myEmployee', action);
	setTimeout(function(){ $("select[name='employee']").multiselect().multiselectfilter(); }, 500); */
}


function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	//alert("choice --->> " + choice + " choice.options.length --->> " + choice.options.length);
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
	return exportchoice;
}

var dialogEdit = '#showChoosePopupDiv';
function showChoosePopupEdit(hideID,lblID, type, appID) {
	var hideIdValue = document.getElementById(hideID).value;
	var dialogEdit = '#modal-body1';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo1").show();
	$('.modal-title1').html('Choose '+type);
	$.ajax({
		url : "ShowOrientationWiseEmpChoosePopup.action?hideID=" + hideID + "&lblID="+lblID + "&appID="+appID 
				+ "&type="+type+"&hideIdValue="+hideIdValue+"&from=EA",
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function showOrientData(value){
	if(value=="1"){
		//hrDiv managerDiv peerDiv
		document.getElementById("hrDivEdit").style.display = "block";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "none";
		//document.getElementById("managerIdDiv").style.display = "none";
		document.getElementById("peerDivEdit").style.display = "none";
		//document.getElementById("peerIdDiv").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "none";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	} else if(value=="2"){
		document.getElementById("hrDivEdit").style.display = "block";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		//document.getElementById("managerIdDiv").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "none";
		//document.getElementById("peerIdDiv").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "none";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	} else if(value=="3"){
		document.getElementById("hrDivEdit").style.display = "block";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		//document.getElementById("managerIdDiv").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "block";
		//document.getElementById("peerIdDiv").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "none";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	} else if(value=="4"){
		document.getElementById("hrDivEdit").style.display = "block";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		//document.getElementById("managerIdDiv").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "block";
		//document.getElementById("peerIdDiv").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "none";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	} else if(value=="5"){
		document.getElementById("hrDivEdit").style.display = "none";
		//document.getElementById("hrIdDiv").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "none";
		//document.getElementById("managerIdDiv").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "none";
		//document.getElementById("peerIdDiv").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "block";
		document.getElementById("hidehrIdEdit").value = "";
		document.getElementById("hidemanagerIdEdit").value = "";
		document.getElementById("hidepeerIdEdit").value = "";
		document.getElementById("hideotherIdEdit").value = "";
		document.getElementById("lblHridEdit").innerHTML = "";
		document.getElementById("lblManageridEdit").innerHTML = "";
		document.getElementById("lblPeeridEdit").innerHTML = "";
		document.getElementById("lblOtheridEdit").innerHTML = "";
	}
}

function showOrientDataonload(){
	var value = document.getElementById("hideorientvaledit").value;
	if(value=="1"){
		document.getElementById("hrDivEdit").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "none";
		document.getElementById("peerDivEdit").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "none";
	} else if(value=="2"){
		document.getElementById("hrDivEdit").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "none";
	} else if(value=="3"){
		document.getElementById("hrDivEdit").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "none";
	} else if(value=="4"){
		document.getElementById("hrDivEdit").style.display = "block";
		document.getElementById("managerDivEdit").style.display = "block";
		document.getElementById("peerDivEdit").style.display = "block";
		document.getElementById("otherDivEdit").style.display = "none";
	} else if(value=="5"){
		document.getElementById("hrDivEdit").style.display = "none";
		document.getElementById("managerDivEdit").style.display = "none";
		document.getElementById("peerDivEdit").style.display = "none";
		document.getElementById("otherDivEdit").style.display = "block";
	}
}

function addRevieweeForSelfReview(employee) {
	//alert("employee ===>> ");
	if(document.getElementById("hideSelfFillEmpIds")) {
		//alert(" ---->>> ");
		var employeeIds = getSelectedValue(employee);
		//  hideSelfFillEmpIds
		//alert("employeeIds ===>> "+employeeIds);
        xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
        } else {
            var xhr = $.ajax({
                url : "SetSelfFillEmployee.action?empids=" + employeeIds,
                cache : false,
                success : function(data) {
                	//alert("data ===>> " + data);
                	if(data == "") {
                	} else {
                		var allData = data.split("::::");
                		document.getElementById("hideSelfFillEmpIds").value = allData[0];
                		document.getElementById("lblSelfFillEmpIds").innerHTML = allData[1];
                        //document.getElementById("statetitle").style.display = 'block';
                	}
                }
            });
    	}
	}
}

function removeRevieweeForSelfReview(removeEmpid) {
	if(document.getElementById("hideSelfFillEmpIds")) {
		//alert(" ---->>> ");
		var employeeIds = document.getElementById("hideSelfFillEmpIds").value;
		//  hideSelfFillEmpIds
		//alert("employeeIds ===>> "+employeeIds);
        xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");	
            return;
        } else {
            var xhr = $.ajax({
                url : "SetSelfFillEmployee.action?empids=" + employeeIds + "&removeEmpid=" + removeEmpid,
                cache : false,
                success : function(data) {
                	//alert("data ===>> " + data);
                	if(data == "") {
                	} else {
                		var allData = data.split("::::");
                		document.getElementById("hideSelfFillEmpIds").value = allData[0];
                		document.getElementById("lblSelfFillEmpIds").innerHTML = allData[1];
                        //document.getElementById("statetitle").style.display = 'block';
                	}
                }
            });
    	}
	}
}

function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
      return false;
   return true;
}

function showMembersSelectEdit(orientation) {
	
	var appId  = document.getElementById("appraisalId").value;
	//alert("orientation==>"+orientation+"==>appId==>"+appId);
    xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
         alert("Browser does not support HTTP Request");
         return;
    } else {
         var xhr = $.ajax({
             url : "GetMembersByOrientation.action?orientation_id="+orientation+"&operation=E&appraisalId="+appId,
             cache : false,
             success : function(data) {
             	//alert("data==>"+data);
             	document.getElementById("td_choose_members_edit").innerHTML = data;
             }
         });
     }
 }
 
 
function createRevieweePanelForReview(employee) {
	//alert("employee");
	if(document.getElementById("divAppraiser")) {
		var employeeIds = getSelectedValue(employee);
		//alert("employeeIds ===>> " + employeeIds);
		var orientationType = document.getElementById("oreinted").value;
		//alert("orientationType ===>> " + orientationType);
		xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
        } else {
            var xhr = $.ajax({
                url : "AutoGeneratedRevieweePanel.action?empids="+employeeIds+"&orientationType="+orientationType,
                cache : false,
                success : function(data) {
                	if(data == "") {
                	} else {
                		document.getElementById("divAppraiser").innerHTML = data;
                	}
                }
            });
    	}
	}
}


function removeRevieweePanelMember(spanName) {
	//alert("spanName ===>> " + spanName);
	document.getElementById(spanName).innerHTML = "";
}

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String id = (String) request.getAttribute("id");
	String appFreqId = (String) request.getAttribute("appFreqId");
	String fromPage = (String) request.getAttribute("fromPage");
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	List<String> appraisalList = (List<String>) request.getAttribute("appraList");
	Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
	Map<String, String> questMp = (Map<String, String>) request.getAttribute("questMp");
	Map<String, Map<String,String>> memberMp =(Map<String, Map<String,String>>) request.getAttribute("memberMp");
	List<String> memberList = (List<String>) request.getAttribute("memberList");
	Map<String, String> hmOrientMemberID = (Map<String, String>) request.getAttribute("hmOrientMemberID");
	
	if(hmOrientMemberID == null) {
		hmOrientMemberID = new HashMap<String,String>();
	}
	
	if(appraisalList == null) appraisalList = new ArrayList<String>();
	
	if(memberList == null) memberList = new ArrayList<String>();
	
	if(hmScoreDetailsMap == null) {
		hmScoreDetailsMap = new HashMap<String,String>();
	}
	
	if(questMp == null) {
		questMp = new HashMap<String,String>();
	}
	
	if(memberMp == null) {
		memberMp = new HashMap<String, Map<String,String>>();
	}
%>
<div class="reportWidth">
	<s:form action="EditAppraisal" id="formID1" name="formID1" method="POST" theme="simple">
	  <%String appsystem = request.getParameter("appsystem"); 
		if (appsystem != null && (appsystem.trim().equals("appraisal") || appsystem.trim().equals("editexistapp"))) {	
			List<String> appraisalData = (List<String>) request.getAttribute("appraisalData");
			if(appraisalData == null) appraisalData = new ArrayList<String>();
			if(appraisalData != null && appraisalData.size() >0 && !appraisalData.isEmpty()) {
		%>
				<s:hidden name="id"></s:hidden>
				<s:hidden name="appsystem" id="appsystem"></s:hidden>
				<input type="hidden" name="hideorientval" id="hideorientvaledit" value="<%=request.getAttribute("orientedValue") %>">
				<input type="hidden" name="appraisalId" id="appraisalId" value="<%=appraisalData.get(0) %>"> 
				<input type="hidden" name="appFreqId" id="appFreqId" value="<%=appFreqId %>"> 
				<table class="table table_no_border">
	               <tr>
	                   <th width="15%" style="text-align:right" >Review Name:<sup>*</sup></th>
	                   <td colspan="6"><input type="text" name="appraiselName" id="appraiselName" class="validateRequired" value="<%=appraisalData.get(1) %>" style="width: 50%;"/></td>
	               </tr>
	               <tr>
	                   <th style="text-align:right">Review Type:<sup>*</sup></th>
	                   <td colspan="6">
		                   <%if(appraisalData.get(45)!= null && uF.parseToInt(appraisalData.get(45)) == 1) { %>
		                   		<s:select theme="simple" name="appraisalType"  cssClass="validateRequired"
		                               list="#{'Self Review':'Self Review'}" value="appraisal_typeValue"/>
		                   <% } else { %>
		                           <s:select theme="simple" name="appraisalType" headerKey="Annual Review" cssClass="validateRequired"
		                               headerValue="Annual Review"
		                               list="#{'Monthly Review':'Monthly Review',
		                               'Mid Term Review':'Mid Term Review', 'Ad hoc Review':'Ad hoc Review',
		                               'Feedback':'Feedback', 'Review':'Review'}" value="appraisal_typeValue"/>
		                   <% } %>
	                   </td>
	               </tr>
	                <tr>
	                   <th style="text-align: right;" valign="top">Description:</th>
	                   <td colspan="6">
	                      <textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="appraisal_description"><%=appraisalData.get(25) %></textarea>  <!-- id="editor22" -->
	                  </td>
	               </tr>
	               <tr>
	                   <th style="text-align:right" valign="top">Instructions:</th>
	                   <td colspan="6">
	                       <textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="appraisal_instruction"><%=appraisalData.get(27) %></textarea>  <!-- id="editor11" -->                               
	                   </td>
	               </tr>
	               
	               <tr>
	                   <th style="text-align:right" valign="top">Select Frequency:</th>
	                   <td colspan="6">
		                   <div style="position:reletive;">
			                   <span style="float: left; margin-right: 20px"> 
				                  <% if(appraisalData.get(45)!= null && uF.parseToInt(appraisalData.get(45)) == 1) {  %>
				                  		<s:select theme="simple" name="frequency" id="frequency" list="#{'1 ':'One Time'}" value="1"/>
				                  <% } else { %>
				                       <s:select theme="simple" name="frequency" id="frequency" list="frequencyList"
				                       listKey="id" listValue="name" onchange="checkFrequency(this.value)" value="frequencyValue"/>    
				                   <% } %>
			                   </span>
			                   
			                   <span id="weekly" style="display: none; float: left;">
		                           <s:select theme="simple" name="weekday" id="weekday" cssStyle="width:100px;" headerKey="" headerValue="Select Day" 
		                           value="weekdayValue" cssClass="validateRequired"
		                               list="#{'Monday':'Monday','Tuesday':'Tuesday', 'Wednesday':'Wednesday','Thursday':'Thursday','Friday':'Friday','Saturday':'Saturday','Sunday':'Sunday'}" />
			                   </span>
			                 <span id="annualy" style="display: none; float: left;"> 
			                       <s:select theme="simple" name="annualDay" id="annualDay" cssStyle="width:55px;" headerKey="" headerValue="Day" 
			                       	value="annualDayValue" cssClass="validateRequired "
			                           list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10','11':'11','12':'12',
			                           '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20', '21':'21','22':'22','23':'23',
			                           '24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
			                       &nbsp;
			                       <s:select theme="simple" name="annualMonth" id="annualMonth" cssStyle="width:120px;" headerKey="" 
			                       	headerValue="Select Month" value="annualMonthValue" cssClass="validateRequired"
			                           list="#{'January':'January','February':'February', 'March':'March','April':'April','May':'May','June':'June',
			                           'July':'July','August':'August','September':'September', 'October':'October','November':'November','December':'December'}" />
			                   </span> 
			                   <span id="monthly" style="display: none; float: left;"> 
			                       <s:select theme="simple" name="day" id="day" headerKey="" headerValue="Date" value="dayValue" 
			                       	cssClass="validateRequired" cssStyle="width:85px;"
			                               list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
			                               '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
			                               '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
			                      
			                   </span> 
			                    <span id="dayMonth" style="display: none; float: left; height: 70px;">
			                       <s:select theme="simple" name="monthday" id="monthday" cssStyle="width:55px; position:absolute;" headerKey="" 
			                       headerValue="Day" value="monthdayValue" cssClass="validateRequired"
			                               list="#{'1':'1','2':'2', '3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9', '10':'10',
			                               '11':'11','12':'12', '13':'13','14':'14','15':'15','16':'16','17':'17','18':'18','19':'19', '20':'20',
			                               '21':'21','22':'22', '23':'23','24':'24','25':'25','26':'26','27':'27','28':'28','29':'29', '30':'30', '31':'31'}" />
			                       &nbsp;
			                           <s:select theme="simple" name="month" id="month" cssStyle="width:135px; position:absolute; margin-left:65px" headerKey=""
			                               multiple="true" size="4" value="monthValue" cssClass="validateRequired "
			                               list="#{'January':'January','February':'February', 'March':'March','April':'April','May':'May',
			                               'June':'June','July':'July','August':'August','September':'September', 'October':'October','November':'November','December':'December'}" />
			                   </span> 
			                    <span id="quarterly" style="display: none; float: left; margin-left: 210px;"><i>
			                   	Eg. 'Jan' as first month, Qrt1: Jan, Feb, Mar <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                   	'Feb' as first month, Qrt1: Feb, Mar, Apr</i>
			                   </span>
			                   <span id="halfYearly" style="display: none; float: left; margin-left: 210px;"><i>
			                   	Eg. 'Jan' as first month, Half1: Jan, Feb, Mar, Apr, May, Jun <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                   	'Feb' as first month, Half1: Feb, Mar, Apr, May, Jun, Jul</i>
			                   </span> 
		                   </div>
	                   </td>
	               </tr>
	               
	               <tr>
	                   <th style="text-align:right">Start Date:<sup>*</sup></th>
	                   <td><input type="text" name="from" id="fromEdit" class="validateRequired" style="width: 70px;" value="<%=appraisalData.get(17) %>"/></td>
	                   <th style="text-align:right">End Date:<sup>*</sup></th>
	                   <td colspan="4"><input type="text" name="to" id="toEdit" class="validateRequired" style="width: 70px;"  value="<%=appraisalData.get(18) %>"/></td>
	               </tr>
	               	<tr>
						<th style="text-align: right">Eligibility min days before start date:<sup>*</sup></th>
						<td><input type="text" name="eligibilityMinDaysBeforeStartDate" id="eligibilityMinDaysBeforeStartDate" class="validateRequired" style="width: 85px !important;" value="<%=appraisalData.get(47) %>"/></td>
						<th style="text-align: right">OR Eligibility min days <br/> before end date:</th>
						<td colspan="4"><input type="text" name="eligibilityMinDaysBeforeEndDate" id="eligibilityMinDaysBeforeEndDate" style="width: 85px !important;" value="<%=appraisalData.get(48) %>"/></td>
					</tr>
					<tr>
						<th style="text-align: right">Anonymous Review:</th>
						<td colspan="3"><input type="checkbox" name="anonymousReview" <% if(uF.parseToBoolean(appraisalData.get(49))) { %> checked="checked" <% } %>/></td>
					</tr>
	               <tr>
	                   <th style="text-align:right">Orientation Type:</th>
	                   <td colspan="6">
	                  	 <input type="hidden" name="oldOrientVal" id="oldOrientVal" value="<%=request.getAttribute("orientedValue") %>"/>
	                     <s:select theme="simple" name="oreinted" id="oreinted" list="orientationList" listKey="id" listValue="name" value="orientedValue" onchange="createRevieweePanelForReview('employee');"/>  <!-- onchange="showMembersSelectEdit(this.value);" -->
	                 	 <span style="float: right;" id="spanOreint"></span>
	                   </td>
	               </tr>
	               
		         <% if(strUserType != null && !strUserType.equals(IConstants.MANAGER)) { %>
		               <tr>
		                  <th style="text-align:right" valign="top">Group:</th>
		                  <td>                                   
	                           <s:select theme="simple" name="strOrg" list="organisationList" id="strOrg" listKey="orgId" listValue="orgName" 
	                             required="true" onchange="getOrgLocationDepartLevelDesigGrade();" multiple="true" size="4"></s:select>
		                   </td>
		                   <td>
			                   	<div id="wlocDiv">
		                           <s:select name="strWlocation" list="workList" id="wlocation" listKey="wLocationId" listValue="wLocationName" 
		                                required="true" value="wlocationvalue" onchange="getEmployeebyLocation();" multiple="true" size="4" />
			                    </div>
		                   </td>
		                   <td>
			                   	<div id="departDiv">
		                           <s:select name="strDepart" list="departmentList" id="depart" listKey="deptId" listValue="deptName" 
		                                required="true" onchange="getEmployeebyDepart();" multiple="true" size="4" value="departmentvalue" />
		                  		</div>
		                   </td>
		               </tr>
		               
		               <tr>
		               		<td></td>
		               		<td>
			                   	<div id="levelDiv">
		                           <s:select name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName"
		                                required="true" onchange="getEmployeebyLevel()" multiple="true" size="4" value="levelvalue" />
			                   </div>     
		                   </td>
		               		<td>
		                       <div id="myDesig">
		                           <s:select name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdV" listValue="desigCodeName"
		                                onchange="getEmployeebyDesig();" multiple="true" size="4" value="desigvalue" />
		                        </div>
		                   </td>
		                   <td>
		                       <div id="myGrade">
		                           <s:select name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode" id="gradeIdV"
		                               onchange="getEmployeebyGrade();" multiple="true" size="4" value="gradevalue" />
		                      </div>
		                   </td>
		               </tr>
		          <% } %>
	               <tr>
	                   <th style="text-align:right" valign="top">Reviewee:</th>
	                   <td id="myEmployee" colspan="6">
	                   <% //System.out.println("empvalue ===>> " + (String)request.getAttribute("empvalue")); %>
	                       <s:select name="employee" list="empList" theme="simple" cssClass="validateRequired form-control" listKey="employeeId" id="employee" listValue="employeeCode" 
	                       	 required="true" multiple="true" size="4" value="empvalue" onchange="createRevieweePanelForReview('employee');"/>  <!-- onclick="addRevieweeForSelfReview('employee');"  onclick="createRevieweePanelForReview('employee');" -->
	                   </td>
	               </tr>
	               
	               <tr>
						<th style="text-align: right">Appraiser:</th>
						<td colspan="6">
							<div id="divAppraiser"> 
							<% String sbAllRevieweePanalist = (String) request.getAttribute("sbAllRevieweePanalist");
								if(sbAllRevieweePanalist!=null && sbAllRevieweePanalist.length()>0) {
							%>
								<%=sbAllRevieweePanalist %>
							<% } else { %>
								Please select reviewee.
							<% } %>
							</div>
						</td>
					</tr>
							
	               <%-- <tr>
	                   <th style="text-align:right">Appraiser:</th>
	                   <td colspan="6">
							<div id="td_choose_members_edit">
		                       <%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR"))) { %>
		                       		<input type="hidden" name="hidehrIdEdit" id="hidehrIdEdit" value="<%=appraisalData.get(33) %>"/>
		                       		<div id="hrDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hidehrIdEdit','lblHridEdit','HR','<%=appraisalData.get(0) %>');">Choose HR</a>:&nbsp;<label id="lblHridEdit"><%=uF.showData(appraisalData.get(29), "Not Choosen") %></label></div>
		                       <% } %>
		                		
		                		<%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("Manager") != null && memberList.contains(hmOrientMemberID.get("Manager"))) { %>
		                        	<input type="hidden" name="hidemanagerIdEdit" id="hidemanagerIdEdit" value="<%=appraisalData.get(34) %>"/>
		                       	    <div id="managerDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hidemanagerIdEdit','lblManageridEdit','Manager','<%=appraisalData.get(0) %>');">Choose Manager</a>:&nbsp;<label id="lblManageridEdit"><%=uF.showData(appraisalData.get(28), "Not Choosen") %></label></div>
		                       <% } %>
		                       
		                       <%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("Peer") != null &&  memberList.contains(hmOrientMemberID.get("Peer"))) { %>
		                        	<input type="hidden" name="hidepeerIdEdit" id="hidepeerIdEdit" value="<%=appraisalData.get(35) %>"/>
		                       	    <div id="peerDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hidepeerIdEdit','lblPeeridEdit','Peer','<%=appraisalData.get(0) %>');">Choose Peer</a>:&nbsp;<label id="lblPeeridEdit"><%=uF.showData(appraisalData.get(30), "Not Choosen") %></label></div>
		                       <% } %>
		                       
		                      <%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("Anyone") != null &&  memberList.contains(hmOrientMemberID.get("Anyone"))) { %>
		                       	   	<input type="hidden" name="hideotherIdEdit" id="hideotherIdEdit" value="<%=appraisalData.get(36) %>"/>
		                       	   	<div id="otherDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hideotherIdEdit','lblOtheridEdit','Other','<%=appraisalData.get(0) %>');">Choose Anyone</a>:&nbsp;<label id="lblOtheridEdit"><%=uF.showData(appraisalData.get(31), "Not Choosen") %></label>
		                       		&nbsp;&nbsp;&nbsp;&nbsp;<label id="lblSelfFillEmpIds"><%=uF.showData(appraisalData.get(32), "") %></label>
		                     			<input type="hidden" name="hideSelfFillEmpIds" id="hideSelfFillEmpIds" value="<%=(String)request.getAttribute("hideSelfFillEmpIds") %>" />
		                       	   	</div>
		                       <% } %>
		                       
		                        <%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("CEO") != null &&  memberList.contains(hmOrientMemberID.get("CEO"))) { %>
		                       	    <input type="hidden" name="hideCeoIdEdit" id="hideCeoIdEdit" value="<%=appraisalData.get(39) %>"/>
		                       	    <div id="ceoDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hideCeoIdEdit','lblCeoIdEdit','CEO','<%=appraisalData.get(0) %>');">Choose CEO</a>:&nbsp;<label id="lblCeoIdEdit"><%=uF.showData(appraisalData.get(37), "Not Choosen") %></label></div>
		                       <% } %>
		                       
		                       <%if(memberList != null && hmOrientMemberID != null && hmOrientMemberID.get("HOD") != null &&  memberList.contains(hmOrientMemberID.get("HOD"))) { %>
		                       	    <input type="hidden" name="hideHodIdEdit" id="hideHodIdEdit" value="<%=appraisalData.get(40) %>"/>
		                       	    <div id="hodDivEdit" style="display: block; float: left; width: 100%;"><a href="javascript: void(0);" onclick="showChoosePopupEdit('hideHodIdEdit','lblHodIdEdit','HOD','<%=appraisalData.get(0) %>');">Choose HOD</a>:&nbsp;<label id="lblHodIdEdit"><%=uF.showData(appraisalData.get(38), "Not Choosen") %></label></div>
		                       <% } %>
	                       </div>
	                   </td>
	               </tr> --%>
	               
	               <tr>
	                   <th style="text-align:right">Reviewer:</th>
	                   <td colspan="6">
	                   		<div id="div_Reviewer_edit"> 
								<s:select name="reviewerId" list="reviewerList" theme="simple" listKey="employeeId" 
									id="reviewerId" listValue="employeeCode" multiple="true" size="4" value="reviewervalue" />
							</div>
	                   </td>
	               </tr>
	               <tr>
	               		<td></td>
	               		<td>
	               			<%if(fromPage != null && fromPage.equals("AD")) { %>
			            		<input type="submit" value="Save" class="btn btn-primary" name="submit"/>
			            	<% } else { %>
								<s:submit value="Save" cssClass="btn btn-primary" name="submit" ></s:submit>
							<%} %>
	               		</td>
	               </tr>
	            </table>
		   <% } %>
	   <%}%>
	</s:form>
</div>

<script>
	// Replace the <textarea id="editor1"> with an CKEditor instance.
	if(document.getElementById("editor11")) {
		CKEDITOR.replace( 'editor11', {
			on: {
				focus: onFocus,
				blur: onBlur,

				// Check for availability of corresponding plugins.
				pluginsLoaded: function( evt ) {
					var doc = CKEDITOR.document, ed = evt.editor;
					if ( !ed.getCommand( 'bold' ) )
						doc.getById( 'exec-bold' ).hide();
					if ( !ed.getCommand( 'link' ) )
						doc.getById( 'exec-link' ).hide();
				}
			}
		});
	}
	
	if(document.getElementById("editor22")) {
		CKEDITOR.replace( 'editor22', {
			on: {
				focus: onFocus,
				blur: onBlur,

				// Check for availability of corresponding plugins.
				pluginsLoaded: function( evt ) {
					var doc = CKEDITOR.document, ed = evt.editor;
					if ( !ed.getCommand( 'bold' ) )
						doc.getById( 'exec-bold' ).hide();
					if ( !ed.getCommand( 'link' ) )
						doc.getById( 'exec-link' ).hide();
				}
			}
		});
	}
	
	$("#formID1").submit(function(event){
		event.preventDefault();
		var appsystem = document.getElementById("appsystem").value;
		var form_data = $("#formID1").serialize();
		$.ajax({
			type : 'POST',
			url: "EditAppraisal.action",
			data: form_data+"&submit=Save",
			success: function(result) {
				if(appsystem != null && appsystem === 'editexistapp') {
					getReviewsData('AppraisalDashboard','','','','');
				} else {
					getReviewSummary('AppraisalSummary','<%=id%>','<%=appFreqId %>','<%=fromPage %>');
				}
			},
			error: function(result){
				if(appsystem != null && appsystem === 'editexistapp') {
					getReviewsData('AppraisalDashboard','','','','');
				} else {
					getReviewSummary('AppraisalSummary','<%=id%>','<%=appFreqId %>','<%=fromPage %>');
				}
			}
		});
		
	});
	
	
	$("input[name='submit']").click(function(){
		for ( instance in CKEDITOR.instances ) {
            CKEDITOR.instances[instance].updateElement();
        }

		$("#formID1").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formID1").find('.validateRequired').filter(':visible').prop('required',true);
	});
	
	
</script>

<div id="showChoosePopupDiv"></div>


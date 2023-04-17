<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script type="text/javascript">

function checkValidationStep1() {
	/* var x = document.getElementsByName("fname").length;
    var val="";
    for(var i=0; i<x; i++) {
    	val = val + " - " + document.getElementsByName("fname")[i].value;
    }
    document.getElementById("demo").innerHTML = val; */
	
    var strBillingKind = "";
    var billingType = "";
    if(document.getElementById("strBillingKind")) {
    	var strBillingKind = document.getElementById("strBillingKind").value;
    }
    if(document.getElementById("billingType")) {
    	billingType = document.getElementById("billingType").value;
    }
	if(document.getElementById("prjectname") && document.getElementById("prjectname").value == '') {
		return false;
	} else if(document.getElementById("strClient") && document.getElementById("strClient").value == '') {
		return false;
	} else if(document.getElementById("clientPoc") && document.getElementById("clientPoc").value == '') {
		return false;
	} else if(document.getElementById("strProjectOwner") && document.getElementById("strProjectOwner").value == '') {
		return false;
	} else if(document.getElementById("service") && document.getElementById("service").value == '') {
		return false; 
	} else if(document.getElementById("organisation") && document.getElementById("organisation").value == '') {
		return false;
	} else if(document.getElementById("location") && document.getElementById("location").value == '') {
		return false;
	} else if(document.getElementById("strSBU") && document.getElementById("strSBU").value == '') {
		return false;
	} else if(document.getElementById("strDepartment") && document.getElementById("strDepartment").value == '') {
		return false;
	} else if(document.getElementById("priority") && document.getElementById("priority").value == '') {
		return false;
	} else if(document.getElementById("startDate") && document.getElementById("startDate").value == '') {
		return false;
	} else if(document.getElementById("deadline1") && document.getElementById("deadline1").value == '') {
		return false;
	} else if(document.getElementById("strCurrency") && document.getElementById("strCurrency").value == '') {
		return false;
	} else if(document.getElementById("strBillingCurrency") && document.getElementById("strBillingCurrency").value == '') {
		return false;
	} else if(document.getElementById("billingType") && document.getElementById("billingType").value == '') {
		return false;
	} else if(billingType == 'F' && document.getElementById("billingAmountF") && document.getElementById("billingAmountF").value == '') {
		return false;
	} else if(document.getElementById("strBillingKind") && document.getElementById("strBillingKind").value == '') {
		return false;
	} else if(strBillingKind == 'W' && document.getElementById("weekdayCycle") && document.getElementById("weekdayCycle").value == '') {
		return false;
	} else if((strBillingKind == 'B' || strBillingKind == 'M') && document.getElementById("dayCycle") && document.getElementById("dayCycle").value == '') {
		return false;
	} else {
		//alert("true");
		return true;
	}
}


function checkValidationStep3() {
	var x = document.getElementsByName("taskname").length;
    var val="";
    for(var i=0; i<x; i++) {
    	val = document.getElementsByName("taskname")[i].value;
    	if(val == '') {
    		return false;
    	}
    	val = document.getElementsByName("startDate")[i].value;
    	if(val == '') {
    		return false;
    	}
    	val = document.getElementsByName("deadline1")[i].value;
    	if(val == '') {
    		return false;
    	}
    	val = document.getElementsByName("idealTime")[i].value;
    	if(val == '') {
    		return false;
    	}
    	var taskcount = document.getElementById("taskcount").value;
    	for(var a=0; a<parseInt(taskcount); a++) {
    		if(document.getElementById("emp_id"+a)) {
    			val = getSelectedValue("emp_id"+a);
    			if(val == '') {
    	    		return false;
    	    	}
    		}
			if(document.getElementById("sub_emp_id"+a)) {
				val = getSelectedValue("sub_emp_id"+a);
    			if(val == '') {
    	    		return false;
    	    	}
    		}
    	}
    	/* val = document.getElementsByName("colourCode")[i].value;
    	if(val == '') {
    		return false;
    	} */
    }
		//alert("true");
	return true;
}


function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		var value = choice.options[i].value;
		if(choice.options[i].selected == true && value != "") {
			if (j == 0) {
				exportchoice = "," + choice.options[i].value + ",";
				j++;
			} else {
				exportchoice += choice.options[i].value + ",";
				j++;
			}
		} else if(choice.options[i].selected == true && value == "") {
			exportchoice = "";
			break;
		}
	}
	//alert("exportchoice==>"+exportchoice);
	return exportchoice;
}




</script>
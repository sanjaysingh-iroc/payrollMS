<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script>
function showSystem(value, divcount, callFrom) {
		//alert("in showSystem value callfrom==>"+callFrom+"\tdivcount==>"+divcount+"\tvalue==>"+value);
		
		if (value == '1') {
			document.getElementById("mainDiv").style.display='block';
			document.getElementById("otherDiv").style.display='none';
			document.getElementById("goalDiv").style.display='none';
			
			document.getElementById("assessOfSubsectionDiv").style.display='none';
			document.getElementById("otherDiv").innerHTML='';
			document.getElementById("goalDiv").innerHTML='';
			document.getElementById("scoreCardID" + divcount).style.display = 'table-row';
			document.getElementById("otherqueTypeTr").style.display = 'none';
			document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
			/*document.getElementById("weightageTr" + divcount).style.display = 'table-row';*/
		} else if (value == '2') {
			// alert("value 2 other")
			questionCnt=0;
			document.getElementById("otherqueTypeTr").style.display = 'table-row';
			document.getElementById("mainDiv").style.display='none';
			document.getElementById("otherDiv").style.display='block';
			if(document.getElementById("goalDiv")) {
				document.getElementById("goalDiv").style.display='none';
				document.getElementById("goalDiv").innerHTML='';
			}
			
			document.getElementById("assessOfSubsectionDiv").style.display='block';
			document.getElementById("otherDiv").innerHTML=otherDivData(callFrom);
			document.getElementById("mainDiv").innerHTML='';
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
		//	document.getElementById("weightageTr" + divcount).style.display = 'table-row';
		} else if (value == '3' || value == '4' || value == '5') {
			questionCnt=0; 
			document.getElementById("otherqueTypeTr").style.display = 'none';
			document.getElementById("mainDiv").style.display='none';
			document.getElementById("otherDiv").style.display='none';
			document.getElementById("goalDiv").style.display='block';
			document.getElementById("assessOfSubsectionDiv").style.display='none';
			document.getElementById("otherDiv").innerHTML='';
			document.getElementById("mainDiv").innerHTML='';
			if(value == '3'){
				document.getElementById("goalDiv").innerHTML=goalDivData(callFrom, 'goal', 'Goals');
			} else if(value == '4'){
				document.getElementById("goalDiv").innerHTML=goalDivData(callFrom, 'KRA', 'KRAs');
			} else if(value == '5'){
				document.getElementById("goalDiv").innerHTML=goalDivData(callFrom, 'target', 'Targets');
			}
	//		document.getElementById("goalDiv").innerHTML=goalDivData(callFrom, 'goal', 'Goals');
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
			//document.getElementById("weightageTr" + divcount).style.display = 'table-row';
		}
		else if (value == '4') {
			questionCnt=0;
			document.getElementById("otherqueTypeTr").style.display = 'none';
			document.getElementById("mainDiv").style.display='none';
			document.getElementById("otherDiv").style.display='none';
			document.getElementById("goalDiv").style.display='block';
			document.getElementById("assessOfSubsectionDiv").style.display='none';
			document.getElementById("otherDiv").innerHTML='';
			document.getElementById("mainDiv").innerHTML='';
			document.getElementById("goalDiv").innerHTML=goalDivData(callFrom, 'KRA', 'KRAs');
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
		//	document.getElementById("weightageTr" + divcount).style.display = 'table-row';
		} else if (value == '5') {
			questionCnt=0;
			document.getElementById("otherqueTypeTr").style.display = 'none';
			document.getElementById("mainDiv").style.display='none';
			document.getElementById("otherDiv").style.display='none';
			document.getElementById("goalDiv").style.display='block';
			document.getElementById("assessOfSubsectionDiv").style.display='none';
			document.getElementById("otherDiv").innerHTML='';
			document.getElementById("mainDiv").innerHTML='';
			document.getElementById("goalDiv").innerHTML=goalDivData(callFrom, 'target', 'Targets');
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
			//document.getElementById("weightageTr" + divcount).style.display = 'table-row';
//===start parvez date: 21-12-2021===	
		} else if (value == '6') {
			questionCnt=0;
			document.getElementById("otherqueTypeTr").style.display = 'table-row';
			document.getElementById("mainDiv").style.display='none';
			document.getElementById("goalDiv").style.display='none';
			document.getElementById("otherDiv").style.display='none';
			
			document.getElementById("otherQuestionTypeTd").style.display='none';
			document.getElementById("pgQuestionTd").style.display='block';
			
			document.getElementById("assessOfSubsectionDiv").style.display='none';
			document.getElementById("mainDiv").innerHTML='';
			document.getElementById("goalDiv").innerHTML='';
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("anstypeTr" + divcount).style.display = 'none';
			
//===end parvez date: 21-12-2021===
		} else {
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("otherqueTypeTr").style.display = 'none';
			document.getElementById("otherDiv").style.display='none';
			document.getElementById("assessOfSubsectionDiv").style.display='none';
			document.getElementById("mainDiv").innerHTML='';
			document.getElementById("otherQuestionLi").innerHTML='';
			document.getElementById("anstypeTr" + divcount).style.display = 'none';
			//document.getElementById("weightageTr" + divcount).style.display = 'none';
		}
	}
	

	function deleteBlock(childId, parentIds,type){
		
		//var a=document.getElementById(type+parentIds).value;
		//document.getElementById(type+parentIds).value=parseInt(a)-1;
			//alert("type ca1 ===>> " + type);
			var row_skill = document.getElementById(childId); 
			document.getElementById(parentIds).removeChild(row_skill);
	}

	 function deleteBlock1(childId, parentIds){
			var row_skill = document.getElementById(childId); 
			document.getElementById(parentIds).removeChild(row_skill);
	}
	
	function deleteMeasure(id, ids) {

		var row_skill = document.getElementById(ids);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
		}
	}

	function deleteQuestion(ids) {
		var row_skill = document.getElementById(ids);
		if (row_skill && row_skill.parentNode
				&& row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
		}
	}

	function isOnlyNumberKey(evt) {
		var charCode = (evt.which) ? evt.which : event.keyCode;
		if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
			return true; 
		}
		return false;
	}
	
function OpenCGMnewquediv(value, type, totWeightage,uid,newQueCnt){
	//alert("OpenCGMnewquediv value==>"+value+"==>type==>"+type);
	var remainWeightage = 100 - parseFloat(totWeightage);
	if(parseInt(remainWeightage) <= 0){
		alert("Unable to add questions because of no weightage available ");
	}else{   
		if(type == "score"){
			document.getElementById("type40"+value).value = type;
			document.getElementById("UID40"+value).value = uid;
			document.getElementById("CGMScoreCntS"+value).innerHTML = newQueCnt+")";
			document.getElementById("CGMGoalCntS"+value).innerHTML = newQueCnt+".1)";
			document.getElementById("CGMMeasureCntS"+value).innerHTML = newQueCnt+".1.1)";
			document.getElementById("CGMQueCntS"+value).innerHTML = newQueCnt+".1.1.1)";
			document.getElementById("scoreCardWeightage"+value+"s").value = remainWeightage;
			document.getElementById("hidescoreCardWeightage"+value+"s").value = remainWeightage;
			document.getElementById("CGMsavebtndivOfS"+value).style.display="block";
			document.getElementById("CGMsavebtndivOfG"+value).style.display="none";
			document.getElementById("CGMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CGMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CGMscorenewquediv"+value).style.display="block";
			document.getElementById("CGMgoalnewquediv"+value).style.display="none";
			document.getElementById("CGMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CGMquenewquediv"+value).style.display="none";
		}else if(type == "goal"){
			document.getElementById("type41"+value).value = type;
			document.getElementById("UID41"+value).value = uid;
			document.getElementById("CGMGoalCntG"+value).innerHTML = newQueCnt+")";
			document.getElementById("CGMMeasureCntG"+value).innerHTML = newQueCnt+".1)";
			document.getElementById("CGMQueCntG"+value).innerHTML = newQueCnt+".1.1)";
			document.getElementById("goalWeightage"+value+"g").value = remainWeightage;
			document.getElementById("hidegoalWeightage"+value+"g").value = remainWeightage;
			document.getElementById("CGMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CGMsavebtndivOfG"+value).style.display="block";
			document.getElementById("CGMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CGMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CGMscorenewquediv"+value).style.display="none";
			document.getElementById("CGMgoalnewquediv"+value).style.display="block";
			document.getElementById("CGMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CGMquenewquediv"+value).style.display="none";
		}else if(type == "measure"){
			document.getElementById("type42"+value).value = type;
			document.getElementById("UID42"+value).value = uid;
			document.getElementById("CGMMeasureCntM"+value).innerHTML = newQueCnt+")";
			document.getElementById("CGMQueCntM"+value).innerHTML = newQueCnt+".1)";
			document.getElementById("measureWeightage"+value+"m").value = remainWeightage;
			document.getElementById("hidemeasureWeightage"+value+"m").value = remainWeightage;
			document.getElementById("CGMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CGMsavebtndivOfG"+value).style.display="none";
			document.getElementById("CGMsavebtndivOfM"+value).style.display="block";
			document.getElementById("CGMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CGMscorenewquediv"+value).style.display="none";
			document.getElementById("CGMgoalnewquediv"+value).style.display="none";
			document.getElementById("CGMmeasurenewquediv"+value).style.display="block";
			document.getElementById("CGMquenewquediv"+value).style.display="none";
		}else if(type == "quest"){
			document.getElementById("type43"+value).value = type;
			document.getElementById("UID43"+value).value = uid;
			document.getElementById("CGMQueCntQ"+value).innerHTML = newQueCnt+")";
			document.getElementById("weightage"+value+"q").value = remainWeightage;
			document.getElementById("hideweightage"+value+"q").value = remainWeightage;
			document.getElementById("CGMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CGMsavebtndivOfG"+value).style.display="none";
			document.getElementById("CGMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CGMsavebtndivOfQ"+value).style.display="block";
			document.getElementById("CGMscorenewquediv"+value).style.display="none";
			document.getElementById("CGMgoalnewquediv"+value).style.display="none";
			document.getElementById("CGMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CGMquenewquediv"+value).style.display="block";
		}
	}	
}



function OpenCGOMnewquediv(value, type, totWeightage,uid,newQueCnt){ 
	//alert("in 2st OpenCGOMnewquediv==>value==>"+ value+"==>type==>"+type);
	var remainWeightage = 100 - parseFloat(totWeightage);
	//alert("remainWeightage==>"+remainWeightage);
	if(parseInt(remainWeightage) <= 0){
		alert("Unable to add questions because of no weightage available ");
	}else {
		if(type == "score"){
			document.getElementById("type20"+value).value = type;
			document.getElementById("UID20"+value).value = uid;
			document.getElementById("CGOMScoreCntS"+value).innerHTML = newQueCnt+")";
			document.getElementById("CGOMGoalCntS"+value).innerHTML = newQueCnt+".1)";
			document.getElementById("CGOMObjCntS"+value).innerHTML = newQueCnt+".1.1)";
			document.getElementById("CGOMMeasureCntS"+value).innerHTML = newQueCnt+".1.1.1)";
			document.getElementById("CGOMQueCntS"+value).innerHTML = newQueCnt+".1.1.1.1)";
			document.getElementById("scoreCardWeightage"+value+"s").value = remainWeightage;
			document.getElementById("hidescoreCardWeightage"+value+"s").value = remainWeightage;
			document.getElementById("CGOMsavebtndivOfS"+value).style.display="block";
			document.getElementById("CGOMsavebtndivOfG"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfO"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CGOMscorenewquediv"+value).style.display="block";
			document.getElementById("CGOMgoalnewquediv"+value).style.display="none";
			document.getElementById("CGOMobjectivenewquediv"+value).style.display="none";
			document.getElementById("CGOMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CGOMquenewquediv"+value).style.display="none";
		} else if(type == "goal"){
			document.getElementById("type21"+value).value = type;
			document.getElementById("UID21"+value).value = uid;
			document.getElementById("CGOMGoalCntG"+value).innerHTML = newQueCnt+")";
			document.getElementById("CGOMObjCntG"+value).innerHTML = newQueCnt+".1)";
			document.getElementById("CGOMMeasureCntG"+value).innerHTML = newQueCnt+".1.1)";
			document.getElementById("CGOMQueCntG"+value).innerHTML = newQueCnt+".1.1.1)";
			document.getElementById("goalWeightage"+value+"g").value = remainWeightage;
			document.getElementById("hidegoalWeightage"+value+"g").value = remainWeightage;
			document.getElementById("CGOMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfG"+value).style.display="block";
			document.getElementById("CGOMsavebtndivOfO"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CGOMscorenewquediv"+value).style.display="none";
			document.getElementById("CGOMgoalnewquediv"+value).style.display="block";
			document.getElementById("CGOMobjectivenewquediv"+value).style.display="none";
			document.getElementById("CGOMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CGOMquenewquediv"+value).style.display="none";
		} else if(type == "objective"){
			document.getElementById("type22"+value).value = type;
			document.getElementById("UID22"+value).value = uid;
			document.getElementById("CGOMObjCntO"+value).innerHTML = newQueCnt+")";
			document.getElementById("CGOMMeasureCntO"+value).innerHTML = newQueCnt+".1)";
			document.getElementById("CGOMQueCntO"+value).innerHTML = newQueCnt+".1.1)";
			document.getElementById("objectiveWeightage"+value+"o").value = remainWeightage;
			document.getElementById("hideobjectiveWeightage"+value+"o").value = remainWeightage;
			document.getElementById("CGOMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfG"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfO"+value).style.display="block";
			document.getElementById("CGOMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CGOMscorenewquediv"+value).style.display="none";
			document.getElementById("CGOMgoalnewquediv"+value).style.display="none";
			document.getElementById("CGOMobjectivenewquediv"+value).style.display="block";
			document.getElementById("CGOMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CGOMquenewquediv"+value).style.display="none";
		}else if(type == "measure"){
			document.getElementById("type23"+value).value = type;
			document.getElementById("UID23"+value).value = uid;
			document.getElementById("CGOMMeasureCntM"+value).innerHTML = newQueCnt+")";
			document.getElementById("CGOMQueCntM"+value).innerHTML = newQueCnt+".1)";
			document.getElementById("measureWeightage"+value+"m").value = remainWeightage;
			document.getElementById("hidemeasureWeightage"+value+"m").value = remainWeightage;
			document.getElementById("CGOMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfG"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfO"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfM"+value).style.display="block";
			document.getElementById("CGOMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CGOMscorenewquediv"+value).style.display="none";
			document.getElementById("CGOMgoalnewquediv"+value).style.display="none";
			document.getElementById("CGOMobjectivenewquediv"+value).style.display="none";
			document.getElementById("CGOMmeasurenewquediv"+value).style.display="block";
			document.getElementById("CGOMquenewquediv"+value).style.display="none";
		}else if(type == "quest"){
			document.getElementById("type24"+value).value = type;
			document.getElementById("UID24"+value).value = uid;
			document.getElementById("CGOMQueCntQ"+value).innerHTML = newQueCnt+")";
			document.getElementById("weightage"+value+"q").value = remainWeightage;
			document.getElementById("hideweightage"+value+"q").value = remainWeightage;
			document.getElementById("CGOMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfG"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfO"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CGOMsavebtndivOfQ"+value).style.display="block";
			document.getElementById("CGOMscorenewquediv"+value).style.display="none";
			document.getElementById("CGOMgoalnewquediv"+value).style.display="none";
			document.getElementById("CGOMobjectivenewquediv"+value).style.display="none";
			document.getElementById("CGOMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CGOMquenewquediv"+value).style.display="block";
		}
	}
}


function OpenCMnewquediv(value, type, totWeightage,uid,newQueCnt){
	//alert("type ===> "+type+"newQueCnt ===> "+newQueCnt);
	var remainWeightage = 100 - parseFloat(totWeightage);
	if(parseInt(remainWeightage) <= 0){
		alert("Unable to add questions because of no weightage available ");
	}else{
		if(type == "score"){
			document.getElementById("type30"+value).value = type;
			document.getElementById("UID30"+value).value = uid;
			document.getElementById("CMScoreCntS"+value).innerHTML = newQueCnt+")";
			document.getElementById("CMMeasureCntS"+value).innerHTML = newQueCnt+".1)";
			document.getElementById("CMQueCntS"+value).innerHTML = newQueCnt+".1.1)";
			document.getElementById("scoreCardWeightage"+value+"s").value = remainWeightage;
			document.getElementById("hidescoreCardWeightage"+value+"s").value = remainWeightage;
			document.getElementById("CMsavebtndivOfS"+value).style.display="block";
			document.getElementById("CMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CMscorenewquediv"+value).style.display="block";
			document.getElementById("CMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CMquenewquediv"+value).style.display="none";
		}else if(type == "measure"){
			document.getElementById("type31"+value).value = type;
			//alert("UID ===> "+ uid);
			document.getElementById("UID31"+value).value = uid;
			document.getElementById("CMMeasureCntM"+value).innerHTML = newQueCnt+")";
			document.getElementById("CMQueCntM"+value).innerHTML = newQueCnt+".1)";
			document.getElementById("measureWeightage"+value+"m").value = remainWeightage;
			document.getElementById("hidemeasureWeightage"+value+"m").value = remainWeightage;
			document.getElementById("CMsavebtndivOfM"+value).style.display="block";
			document.getElementById("CMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CMscorenewquediv"+value).style.display="none";
			document.getElementById("CMmeasurenewquediv"+value).style.display="block";
			document.getElementById("CMquenewquediv"+value).style.display="none";
		}else if(type == "quest"){
			//alert("type ==>  "+type);
			//alert("uid ==>  "+uid);
			document.getElementById("type32"+value).value = type;
			document.getElementById("UID32"+value).value = uid;
			document.getElementById("CMQueCntQ"+value).innerHTML = newQueCnt+")";
			document.getElementById("weightage"+value+"q").value = remainWeightage;
			document.getElementById("hideweightage"+value+"q").value = remainWeightage;
			document.getElementById("CMsavebtndivOfQ"+value).style.display="block";
			document.getElementById("CMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CMscorenewquediv"+value).style.display="none";
			document.getElementById("CMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CMquenewquediv"+value).style.display="block";
			//alert("type last ==>  "+type);
		}
	}	
}


function changeNewQuestionType(val, id, id1, id2, cnt) {
	//alert("val : "+ val +" id : "+ id + " id1 : "+ id1 + " cnt : " + cnt);
	if (val == 1 || val == 2 || val == 8) {
		addQuestionType1(id, cnt);
		document.getElementById(id).style.display = 'table-row';

		addQuestionType2(id1, cnt);
		document.getElementById(id1).style.display = 'table-row';
		document.getElementById(id2).innerHTML = "";
		document.getElementById(id2).style.display = 'none';
	} else if (val == 9) {
		addQuestionType3(id, cnt);
		document.getElementById(id).style.display = 'table-row';

		addQuestionType4(id1, cnt);
		document.getElementById(id1).style.display = 'table-row';
		document.getElementById(id2).innerHTML = "";
		document.getElementById(id2).style.display = 'none';

	} else if (val == 6) {
		addTrueFalseType(id, cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).innerHTML = "";
		document.getElementById(id1).style.display = 'none';
		document.getElementById(id2).innerHTML = "";
		document.getElementById(id2).style.display = 'none';

	} else if (val == 5) {
		addYesNoType(id, cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).innerHTML = "";
		document.getElementById(id1).style.display = 'none';
		document.getElementById(id2).innerHTML = "";
		document.getElementById(id2).style.display = 'none';

	} else if (val == 13) {
		addQuestionType1_1(id, cnt);
		addQuestionType2_1(id1, cnt);
		addQuestionType5_1(id2, cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).style.display = 'table-row';
		document.getElementById(id2).style.display = 'table-row';
	} else {
		addQuestionType1(id, cnt);
		addQuestionType2(id1, cnt);
		document.getElementById(id).style.display = 'none';
		document.getElementById(id1).style.display = 'none';
		document.getElementById(id2).innerHTML = "";
		document.getElementById(id2).style.display = 'none';
	}
}

function addTrueFalseType(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
			+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
}
function addYesNoType(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
			+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
}
function addQuestionType1(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType2(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}
function addQuestionType3(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType4(id1, cnt) {
	document.getElementById(id1).innerHTML = "<th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}

function addQuestionType1_1(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiona\" id=\"rateoptiona"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
	+"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionb\" id=\"rateoptionb"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
}
function addQuestionType2_1(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionc\" id=\"rateoptionc"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
	+"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiond\" id=\"rateoptiond"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
}
function addQuestionType5_1(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>e)&nbsp;<input type=\"text\" name=\"optione\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptione\" id=\"rateoptione"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td><td colspan=\"2\">&nbsp;</td>";
}


function changeNewQuestionTypeOther(val, id, id1, id2, cnt) {
	//alert("val : "+ val +" id : "+ id + " id1 : "+ id1 + " id2 : "+ id2 + " cnt : " + cnt);
	if (val == 1 || val == 2 || val == 8) {
		addQuestionType1Other(id, cnt);
		document.getElementById(id).style.display = 'table-row';
	
		addQuestionType2Other(id1, cnt);
		document.getElementById(id1).style.display = 'table-row';
		document.getElementById(id2).innerHTML = "";
		document.getElementById(id2).style.display = 'none';
	} else if (val == 9) {
		addQuestionType3Other(id, cnt);
		document.getElementById(id).style.display = 'table-row';
	
		addQuestionType4Other(id1, cnt);
		document.getElementById(id1).style.display = 'table-row';
		document.getElementById(id2).innerHTML = "";
		document.getElementById(id2).style.display = 'none';
	
	} else if (val == 6) {
		addTrueFalseTypeOther(id, cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).innerHTML = "";
		document.getElementById(id1).style.display = 'none';
		document.getElementById(id2).innerHTML = "";
		document.getElementById(id2).style.display = 'none';
	
	} else if (val == 5) {
		addYesNoTypeOther(id, cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).innerHTML = "";
		document.getElementById(id1).style.display = 'none';
		document.getElementById(id2).innerHTML = "";
		document.getElementById(id2).style.display = 'none';
	
	} else if (val == 13) {
		addQuestionType101Other(id, cnt);
		addQuestionType201Other(id1, cnt);
		addQuestionType501Other(id2, cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).style.display = 'table-row';
		document.getElementById(id2).style.display = 'table-row';
	} else {
		addQuestionType1Other(id, cnt);
		addQuestionType2Other(id1, cnt);
		document.getElementById(id).style.display = 'none';
		document.getElementById(id1).style.display = 'none';
		document.getElementById(id2).innerHTML ="";
		document.getElementById(id2).style.display = 'none';
	}
}

function addTrueFalseTypeOther(id, cnt) {
document.getElementById(id).innerHTML = "<th></th><th></th><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
}

function addYesNoTypeOther(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td><input type=\"hidden\" name=\"optiona\" /><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
}
function addQuestionType1Other(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\" /><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType2Other(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}
function addQuestionType3Other(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType4Other(id1, cnt) {
	document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}

function addQuestionType101Other(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiona\" id=\"rateoptiona"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
	+"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionb\" id=\"rateoptionb"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
}
function addQuestionType201Other(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionc\" id=\"rateoptionc"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
	+"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiond\" id=\"rateoptiond"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
}
function addQuestionType501Other(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>e)&nbsp;<input type=\"text\" name=\"optione\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptione\" id=\"rateoptione"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td><td colspan=\"2\">&nbsp;</td>";
}


function removeID(id) {
	var row_skill = document.getElementById(id);
	if (row_skill && row_skill.parentNode
			&& row_skill.parentNode.removeChild) {
		row_skill.parentNode.removeChild(row_skill);
	}
}


function changeStatus(id) {
	if (document.getElementById('addFlag' + id).checked == true) {
		document.getElementById('status' + id).value = '1';
	} else {
		document.getElementById('status' + id).value = '0';
	}
}


function getcheckWeightage(value){
	 
    if(value=='1'){
    	for(var i=0;i<=questionCnt;i++){
    		if(document.getElementById("weightageTr"+i)!=null){
            document.getElementById("weightageTr"+i).style.display = 'table-row';
    		}
    	}
    }else if(value=='0'){
for(var i=0;i<=questionCnt;i++){
    		if(document.getElementById("weightageTr"+i)!=null){
            document.getElementById("weightageTr"+i).style.display = 'none';
    		}
    	}
    }
	document.getElementById("hidecheckWeightage").value=value;
}

function otherDivData(callFrom){
  var cnt = questionCnt; 
  var a="<ul class=\"level_list ul_class\"><li><a href=\"javascript:void(0)\" onclick=\"getOtherquestion('"+cnt+"','"+callFrom+"');\"><i class=\"fa fa-plus-circle\"></i>Assess info / points</a></li>"
	+"<li id=\"otherQuestionLi\"></li></ul>";
  return a;
} 

function goalDivData(callFrom, systemType, showText){
	var a="<ul class=\"level_list ul_class\"><li><a href=\"javascript:void(0)\" onclick=\"getGoalsForReview('"+callFrom+"','"+systemType+"');\"><i class=\"fa fa-plus-circle\"></i>"+showText+"</a></li>"
		//+"<div class=\"sectionfont\" id=\"goalDataDiv\" style=\"float:left; width:100%;\"></div></ul>";
		+"<li id=\"goalDataDiv\"></li></ul>";
		return a;
	} 


function getGoalsForReview(callFrom, systemType){
	//alert(callFrom + "  " +systemType);
	var id = null;
	if(document.getElementById("id")){
		id = document.getElementById("id").value; 
    }
	var answerType = null;
	if(document.getElementById("ansTypeAddSAndSubS")){
		answerType = document.getElementById("ansTypeAddSAndSubS").value; 
    }
	var action = 'GetGoalKRATargetForReview.action?id='+id+'&callFrom='+callFrom+'&systemType='+systemType+'&ansType='+answerType;
	getContent('goalDataDiv', action);
	
}

function openAddGoalsTarget(id, subSectionId, sysType, answerType, attributeId, callFrom, divCount,appFreqId){
	//alert("subSectionId ===> "+subSectionId);
	document.getElementById("goalsTargetDiv"+divCount).style.display = 'block';
	var action = 'GetGoalTargetKRA.action?id='+id+'&callFrom='+callFrom+'&sysType='+sysType+'&ansType='+answerType
	+"&subSectionId="+subSectionId+"&attributeId="+attributeId+"&divCount="+divCount+"&appFreqId="+appFreqId;
	getContent('goalsTargetDiv'+divCount, action);
	
}

function closeGoalTargetDiv(divCount){
//	alert("answerType ===> "+answerType);
	document.getElementById("goalsTargetDiv"+divCount).style.display = 'none';
	document.getElementById("goalsTargetDiv"+divCount).innerHTML = '';
}


function goalChart(corpGoalID, managerGoalID, teamGoalID, goalID) {
	//alert("corpGoalID ===> "+corpGoalID+" managerGoalID ===> "+managerGoalID+" teamGoalID ===> "+teamGoalID+" goalID ===> "+goalID);
	removeLoadingDiv('the_div');
	
	var dialogEdit = '#chartGoal';
	var data1 = "<div id=\"the_div\"><div id=\"ajaxLoadImage\"></div></div>";
	dialogEdit = $(data1).appendTo('body'); 
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 600,
				width : 900,
				modal : true,
				title : 'Goal Chart',
				open : function() {
					var xhr = $.ajax({
//						url : "GoalChartIndividualGoal.action?corpGoalID=" + corpGoalID + "&managerGoalID=" + managerGoalID +"&teamGoalID=" + teamGoalID+"&goalID=" + goalID,
						url : 'GoalChartIndividualGoal.action?corpGoalID=' + corpGoalID + '&managerGoalID=' + managerGoalID +'&teamGoalID=' + teamGoalID+'&goalID=' + goalID,
						cache : false,
						success : function(data) {
							$(dialogEdit).html(data);
						}
					});
					xhr = null;

				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				}
			});

	$(dialogEdit).dialog('open');
}

function changeNewQuestionType1(val, id, id1, id2, cnt) {
	 if (val == 1 || val == 2 || val == 8) {
		addQuestionType11(id, cnt);
		document.getElementById(id).style.display = 'table-row';
		addQuestionType21(id1, cnt);
		document.getElementById(id1).style.display = 'table-row';
		document.getElementById(id2).innerHTML ="";
		document.getElementById(id2).style.display = 'none';
	} else if (val == 9) {
		addQuestionType31(id, cnt);
		document.getElementById(id).style.display = 'table-row';
		addQuestionType41(id1, cnt);
		document.getElementById(id1).style.display = 'table-row';
		document.getElementById(id2).innerHTML ="";
		document.getElementById(id2).style.display = 'none';
	 }else if (val == 6) {
		addTrueFalseType1(id, cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).innerHTML ="";
		document.getElementById(id1).style.display = 'none';
		document.getElementById(id2).innerHTML ="";
		document.getElementById(id2).style.display = 'none';
	}else if (val == 5) {
		addYesNoType1(id, cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).innerHTML ="";
		document.getElementById(id1).style.display = 'none';
		document.getElementById(id2).innerHTML ="";
		document.getElementById(id2).style.display = 'none';
	} else if (val == 13) {
		addQuestionType111(id, cnt);
		addQuestionType211(id1, cnt);
		addQuestionType511(id2, cnt);
		document.getElementById(id).style.display = 'table-row';
		document.getElementById(id1).style.display = 'table-row';
		document.getElementById(id2).style.display = 'table-row';
	} else {
		addQuestionType11(id, cnt);
		addQuestionType21(id1, cnt);
		document.getElementById(id).style.display = 'none';
		document.getElementById(id1).style.display = 'none';
		document.getElementById(id2).innerHTML ="";
		document.getElementById(id2).style.display = 'none';
	}
}

function addTrueFalseType1(id,cnt){
	document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"3\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
	+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
}
function addYesNoType1(id,cnt){
	document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"3\"><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
	+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
} 
function addQuestionType11(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType21(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}
function addQuestionType31(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType41(id1,cnt) {
	document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"/> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}

function addQuestionType111(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiona\" id=\"rateoptiona"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
	+"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionb\" id=\"rateoptionb"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
}
function addQuestionType211(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionc\" id=\"rateoptionc"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>"
	+"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiond\" id=\"rateoptiond"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td>";
}
function addQuestionType511(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>e)&nbsp;<input type=\"text\" name=\"optione\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptione\" id=\"rateoptione"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control\" onkeypress=\"return isOnlyNumberKey(event)\"/></td><td colspan=\"2\">&nbsp;</td>";
}

</script>
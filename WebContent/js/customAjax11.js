
	function showSystem(value, divcount, callFrom) {
		alert("in showSystem value "+ value); 
		if (value == '1') {
			document.getElementById("mainDiv").style.display='block';
			document.getElementById("otherDiv").style.display='none';
			document.getElementById("assessOfSubsectionDiv").style.display='none';
			document.getElementById("otherDiv").innerHTML='';
			document.getElementById("scoreCardID" + divcount).style.display = 'table-row';
			document.getElementById("otherqueTypeTr").style.display = 'none';
			document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
			document.getElementById("weightageTr" + divcount).style.display = 'table-row';
		} else if (value == '2') {
//			alert("in other")
			questionCnt=0;
			document.getElementById("otherqueTypeTr").style.display = 'table-row';
			document.getElementById("mainDiv").style.display='none';
			document.getElementById("otherDiv").style.display='block';
			document.getElementById("assessOfSubsectionDiv").style.display='block';
			document.getElementById("otherDiv").innerHTML=otherDivData(callFrom);
			document.getElementById("mainDiv").innerHTML='';
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("anstypeTr" + divcount).style.display = 'table-row';
			document.getElementById("weightageTr" + divcount).style.display = 'table-row';
		} else {
			document.getElementById("scoreCardID" + divcount).style.display = 'none';
			document.getElementById("otherqueTypeTr").style.display = 'none';
			document.getElementById("otherDiv").style.display='none';
			document.getElementById("assessOfSubsectionDiv").style.display='none';
			document.getElementById("mainDiv").innerHTML='';
			document.getElementById("otherQuestionLi").innerHTML='';
			document.getElementById("anstypeTr" + divcount).style.display = 'none';
			document.getElementById("weightageTr" + divcount).style.display = 'none';
		}
	}
	

	function deleteBlock(childId, parentIds,type){
		
		//var a=document.getElementById(type+parentIds).value;
		//document.getElementById(type+parentIds).value=parseInt(a)-1;
		//alert("type ca11===>> " + type);
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

	
function OpenCGMnewquediv(value, type, totWeightage,uid,newQueCnt){
	var remainWeightage = 100 - parseFloat(totWeightage);
	if(parseInt(remainWeightage) <= 0){
		alert("Unable to add questions because of no weightage available ");
	}else{   
		if(type == "score"){
			document.getElementById("type40").value = type;
			document.getElementById("UID40").value = uid;
			document.getElementById("CGMScoreCntS").innerHTML = newQueCnt+")";
			document.getElementById("CGMGoalCntS").innerHTML = newQueCnt+".1)";
			document.getElementById("CGMMeasureCntS").innerHTML = newQueCnt+".1.1)";
			document.getElementById("CGMQueCntS").innerHTML = newQueCnt+".1.1.1)";
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
			document.getElementById("type41").value = type;
			document.getElementById("UID41").value = uid;
			document.getElementById("CGMGoalCntG").innerHTML = newQueCnt+")";
			document.getElementById("CGMMeasureCntG").innerHTML = newQueCnt+".1)";
			document.getElementById("CGMQueCntG").innerHTML = newQueCnt+".1.1)";
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
			document.getElementById("type42").value = type;
			document.getElementById("UID42").value = uid;
			document.getElementById("CGMMeasureCntM").innerHTML = newQueCnt+")";
			document.getElementById("CGMQueCntM").innerHTML = newQueCnt+".1)";
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
			document.getElementById("type43").value = type;
			document.getElementById("UID43").value = uid;
			document.getElementById("CGMQueCntQ").innerHTML = newQueCnt+")";
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
	//alert("totWeightage :: " + totWeightage);
	var remainWeightage = 100 - parseFloat(totWeightage);
	if(parseInt(remainWeightage) <= 0){
		alert("Unable to add questions because of no weightage available ");
	}else{
		if(type == "score"){
			document.getElementById("type20").value = type;
			document.getElementById("UID20").value = uid;
			document.getElementById("CGOMScoreCntS").innerHTML = newQueCnt+")";
			document.getElementById("CGOMGoalCntS").innerHTML = newQueCnt+".1)";
			document.getElementById("CGOMObjCntS").innerHTML = newQueCnt+".1.1)";
			document.getElementById("CGOMMeasureCntS").innerHTML = newQueCnt+".1.1.1)";
			document.getElementById("CGOMQueCntS").innerHTML = newQueCnt+".1.1.1.1)";
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
		}else if(type == "goal"){
			document.getElementById("type21").value = type;
			document.getElementById("UID21").value = uid;
			document.getElementById("CGOMGoalCntG").innerHTML = newQueCnt+")";
			document.getElementById("CGOMObjCntG").innerHTML = newQueCnt+".1)";
			document.getElementById("CGOMMeasureCntG").innerHTML = newQueCnt+".1.1)";
			document.getElementById("CGOMQueCntG").innerHTML = newQueCnt+".1.1.1)";
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
		}else if(type == "objective"){
			document.getElementById("type22").value = type;
			document.getElementById("UID22").value = uid;
			document.getElementById("CGOMObjCntO").innerHTML = newQueCnt+")";
			document.getElementById("CGOMMeasureCntO").innerHTML = newQueCnt+".1)";
			document.getElementById("CGOMQueCntO").innerHTML = newQueCnt+".1.1)";
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
			document.getElementById("type23").value = type;
			document.getElementById("UID23").value = uid;
			document.getElementById("CGOMMeasureCntM").innerHTML = newQueCnt+")";
			document.getElementById("CGOMQueCntM").innerHTML = newQueCnt+".1)";
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
			document.getElementById("type24").value = type;
			document.getElementById("UID24").value = uid;
			document.getElementById("CGOMQueCntQ").innerHTML = newQueCnt+")";
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
	var remainWeightage = 100 - parseFloat(totWeightage);
	if(parseInt(remainWeightage) <= 0){
		alert("Unable to add questions because of no weightage available ");
	}else{
		if(type == "score"){
			document.getElementById("type30").value = type;
			document.getElementById("UID30").value = uid;
			document.getElementById("CMScoreCntS").innerHTML = newQueCnt+")";
			document.getElementById("CMMeasureCntS").innerHTML = newQueCnt+".1)";
			document.getElementById("CMQueCntS").innerHTML = newQueCnt+".1.1)";
			document.getElementById("scoreCardWeightage"+value+"s").value = remainWeightage;
			document.getElementById("hidescoreCardWeightage"+value+"s").value = remainWeightage;
			document.getElementById("CMsavebtndivOfS"+value).style.display="block";
			document.getElementById("CMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CMscorenewquediv"+value).style.display="block";
			document.getElementById("CMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CMquenewquediv"+value).style.display="none";
		}else if(type == "measure"){
			document.getElementById("type31").value = type;
			//alert("UID ===> "+ uid);
			document.getElementById("UID31").value = uid;
			document.getElementById("CMMeasureCntM").innerHTML = newQueCnt+")";
			document.getElementById("CMQueCntM").innerHTML = newQueCnt+".1)";
			document.getElementById("measureWeightage"+value+"m").value = remainWeightage;
			document.getElementById("hidemeasureWeightage"+value+"m").value = remainWeightage;
			document.getElementById("CMsavebtndivOfM"+value).style.display="block";
			document.getElementById("CMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CMsavebtndivOfQ"+value).style.display="none";
			document.getElementById("CMscorenewquediv"+value).style.display="none";
			document.getElementById("CMmeasurenewquediv"+value).style.display="block";
			document.getElementById("CMquenewquediv"+value).style.display="none";
		}else if(type == "quest"){
			document.getElementById("type32").value = type;
			document.getElementById("UID32").value = uid;
			document.getElementById("CMQueCntQ").innerHTML = newQueCnt+")";
			document.getElementById("weightage"+value+"q").value = remainWeightage;
			document.getElementById("hideweightage"+value+"q").value = remainWeightage;
			document.getElementById("CMsavebtndivOfQ"+value).style.display="block";
			document.getElementById("CMsavebtndivOfS"+value).style.display="none";
			document.getElementById("CMsavebtndivOfM"+value).style.display="none";
			document.getElementById("CMscorenewquediv"+value).style.display="none";
			document.getElementById("CMmeasurenewquediv"+value).style.display="none";
			document.getElementById("CMquenewquediv"+value).style.display="block";
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
	document.getElementById(id).innerHTML = "<th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" /><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType2(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\"  /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}
function addQuestionType3(id, cnt) {
	document.getElementById(id).innerHTML = "<th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType4(id1, cnt) {
	document.getElementById(id1).innerHTML = "<th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}

function addQuestionType1_1(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\" class=\"validateRequired form-control\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiona\" id=\"rateoptiona"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control \"/></td>"
	+"<td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" class=\"validateRequired form-control\"/><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionb\" id=\"rateoptionb"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control \"/></td>";
}
function addQuestionType2_1(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptionc\" id=\"rateoptionc"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control \"/></td>"
	+"<td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" class=\"validateRequired form-control\"class=\"validateRequired form-control\"/> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptiond\" id=\"rateoptiond"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control \"/></td>";
}
function addQuestionType5_1(id,cnt) {
	document.getElementById(id).innerHTML = "<th></th><th></th><td>e)&nbsp;<input type=\"text\" name=\"optione\" class=\"validateRequired form-control\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"e\" />&nbsp;Rate&nbsp;<input type=\"text\" name=\"rateoptione\" id=\"rateoptione"+cnt+"\" style=\"width: 20px !important;\" class=\"validateRequired form-control \"/></td><td colspan=\"2\">&nbsp;</td>";
}


function changeNewQuestionTypeOther(val, id, id1, cnt) {
//alert("val : "+ val +" id : "+ id + " id1 : "+ id1 + " cnt : " + cnt);
if (val == 1 || val == 2 || val == 8) {
	addQuestionType1Other(id, cnt);
	document.getElementById(id).style.display = 'table-row';

	addQuestionType2Other(id1, cnt);
	document.getElementById(id1).style.display = 'table-row';
} else if (val == 9) {
	addQuestionType3Other(id, cnt);
	document.getElementById(id).style.display = 'table-row';

	addQuestionType4Other(id1, cnt);
	document.getElementById(id1).style.display = 'table-row';

} else if (val == 6) {
	addTrueFalseTypeOther(id, cnt);
	document.getElementById(id).style.display = 'table-row';
	document.getElementById(id1).innerHTML = "";
	document.getElementById(id1).style.display = 'none';

} else if (val == 5) {
	addYesNoTypeOther(id, cnt);
	document.getElementById(id).style.display = 'table-row';
	document.getElementById(id1).innerHTML = "";
	document.getElementById(id1).style.display = 'none';

} else {
	addQuestionType1Other(id, cnt);
	addQuestionType2Other(id1, cnt);
	document.getElementById(id).style.display = 'none';
	document.getElementById(id1).style.display = 'none';
}
}

function addTrueFalseTypeOther(id, cnt) {
document.getElementById(id).innerHTML = "<th></th><th></th><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">False</td>";
}

function addYesNoTypeOther(id, cnt) {
document.getElementById(id).innerHTML = "<th></th><th></th><td><input type=\"hidden\" name=\"optiona\"/><input type=\"hidden\" name=\"optionb\"/><input type=\"hidden\" name=\"optionc\"/><input type=\"hidden\" name=\"optiond\"/><input type=\"radio\" name=\"correct"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
		+ "<input type=\"radio\" name=\"correct"+ cnt+ "\" value=\"0\">No</td>";
}
function addQuestionType1Other(id, cnt) {
document.getElementByIdOther(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\"/> <input type=\"radio\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\" /><input type=\"radio\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType2Other(id, cnt) {
document.getElementById(id).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\"  /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" /> <input type=\"radio\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
}
function addQuestionType3Other(id, cnt) {
document.getElementById(id).innerHTML = "<th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona\"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"b\" /></td>";
}
function addQuestionType4Other(id1, cnt) {
document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc\"  /> <input type=\"checkbox\" name=\"correct"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond\" /> <input type=\"checkbox\" name=\"correct"+ cnt+"\" value=\"d\" /></td>";
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
var a="<ul class=\"level_list ul_class\"><li><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"getOtherquestion('"+cnt+"','"+callFrom+"');\">Assess Info/Points</a></li>"
	+"<li id=\"otherQuestionLi\"></li></ul>";
	return a;
} 



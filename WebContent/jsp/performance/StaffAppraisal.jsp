<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<!-- Start Dattatray Date:29-June-21  -->
<script type="text/javascript" src="js/jquery.sparkline.js"></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>	
<!-- End Dattatray Date:29-June-21  -->

<!-- ===start parvez date: 22-03-2022=== -->
<style>

	#textlabel{
		white-space:pre-line;
	}

</style>
 <!-- ===end parvez date: 22-03-2022=== -->
<%
	UtilityFunctions uF = new UtilityFunctions();
	List alSkills = (List) request.getAttribute("alSkills");
	String strUserType = (String) session
			.getAttribute(IConstants.USERTYPE);
	//System.out.println("SAp/19--strUserType="+strUserType);
	String empID = (String) request.getAttribute("empID");
	String dataType = (String) session.getAttribute("dataType");
	String[] arrEnabledModules = (String[]) request
			.getAttribute("arrEnabledModules");
	String docRetriveLocation = (String) request
			.getAttribute("DOC_RETRIVE_LOCATION");

	Map<String, String> hmEmpProfile = (Map<String, String>) request
			.getAttribute("hmEmpProfile");
	if (hmEmpProfile == null) {
		hmEmpProfile = new HashMap<String, String>();
	}

	List<List<String>> elementouterList = (List<List<String>>) request
			.getAttribute("elementouterList");
	Map<String, List<List<String>>> hmElementAttribute = (Map<String, List<List<String>>>) request
			.getAttribute("hmElementAttribute");
	Map<String, String> hmScoreAggregateMap = (Map<String, String>) request
			.getAttribute("hmScoreAggregateMap");

	/* 
	 String strTitle = "";
	 if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)  && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)){
	 strTitle = (String) hm.get("NAME")+"'s Profile";	
	 }else{
	 strTitle = "My Profile";
	
	 } */
%>
<g:compress>
	<script type="text/javascript">

/* $("input[name='btnfinish']").click(function(){
		$('.validateRequired').filter(':hidden').prop('required',false);
		$('.validateRequired').filter(':visible').prop('required',true);
	}); */

	function getStepTabContent(id, EmpId, userType, currentLevel, role, appFreqId, levelCount) {
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $.ajax({
	    	url: "StaffAppraisal.action?id="+id+"&empID="+EmpId+"&userType="+userType+"&currentLevel="+currentLevel+"&role="+role+"&appFreqId="+appFreqId+"&levelCount="+levelCount, 
	    	type: "GET",
	    	success: function(result){
	        	$("#divResult").html(result);
	    	}
	    });
	}
	
	
	function readFileURL(input, targetDiv) {
		//alert("targetDiv ===>> " + targetDiv);
        if (input.files && input.files[0]) {
        	//alert("input.files[0] ===>> " + input.files[0]);
            var reader = new FileReader();
            reader.onload = function (e) {
            	//alert(" ===>> 111");
                $('#'+targetDiv).attr('path', e.target.result);
                //alert(" ===>> 222");
            };
            //alert(" ===>> 333");
            reader.readAsDataURL(input.files[0]);
        }
    }
	
	
	/* $(function() { */
		$("input[name='submit']").click(function(e){
			e.preventDefault();
			var flag13QueId = 1;
			var flag11QueId = 1;
			/* Start Dattatray  */
			var flag1QueId = 1;
			var flag2QueId = 1;
			var flag3QueId = 1;
			var flag4QueId = 1;
			var flag5QueId = 1;
			var flag6QueId = 1;
			var flag7QueId = 1;
			var flag8QueId = 1;
			var flag9QueId = 1;
			var flag10QueId = 1;
			var flag12QueId = 1;
			/* End Dattatray */
			if(document.getElementById("ans13QueId")) {
				var ans13QueId = document.getElementById("ans13QueId").value;
				var a1 = '';
				if(ans13QueId.trim() != '') {
					a1 = new Array();
					a1 = ans13QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = 'correct'+a1[i];
					if ($('input[name='+v1+']:checked').length > 0) {
					    cnt++;
					}
				}
				if(a1.length != cnt) {
					flag13QueId=0;
				}
			}
			if(document.getElementById("ans11QueId")) {
				var ans11QueId = document.getElementById("ans11QueId").value;
				var a1 = '';
				if(ans11QueId.trim() != '') {
					a1 = new Array();
					a1 = ans11QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = document.getElementById('gradewithrating'+a1[i]).value;
					if (parseFloat(v1) > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag11QueId=0;
				}
			}
			
			/* Start Dattatray */
			if(document.getElementById("ans3QueId")) {
				var ans3QueId = document.getElementById("ans3QueId").value;
				var a1 = '';
				if(ans3QueId.trim() != '') {
					a1 = new Array();
					a1 = ans3QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = document.getElementById('marks'+a1[i]).value;
					if (parseFloat(v1) > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag3QueId=0;
				}
			}
			
			if(document.getElementById("ans5QueId")) {/* Save issue */
				var ans5QueId = document.getElementById("ans5QueId").value;
				var a1 = '';
				if(ans5QueId.trim() != '') {
					a1 = new Array();
					a1 = ans5QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {					
					var v1 = a1[i];
					if ($('input[name='+v1+']:checked').length > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag5QueId=0;
				}
			} 
			
			if(document.getElementById("ans1QueId")) {
				var ans1QueId = document.getElementById("ans1QueId").value;
				var a1 = '';
				if(ans1QueId.trim() != '') {
					a1 = new Array();
					a1 = ans1QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = 'correct'+a1[i];
					var v2 = document.getElementById('multiplewithremark'+a1[i]).value;
					if ($('input[name='+v1+']:checked').length > 0 && v2.trim().length >0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag1QueId=0;
				}
			}
			
			if(document.getElementById("ans2QueId")) {
				var ans2QueId = document.getElementById("ans2QueId").value;
				var a1 = '';
				if(ans2QueId.trim() != '') {
					a1 = new Array();
					a1 = ans2QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = 'correct'+a1[i];
					if ($('input[name='+v1+']:checked').length > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag2QueId=0;
				}
			}
			
			if(document.getElementById("ans8QueId")) {/* Save issue */
				var ans8QueId = document.getElementById("ans8QueId").value;
				var a1 = '';
				if(ans8QueId.trim() != '') {
					a1 = new Array();
					a1 = ans8QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = 'correct'+a1[i];
					if ($('input[name='+v1+']:checked').length > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag8QueId=0;
				}
			}
			
			if(document.getElementById("ans9QueId")) {
				var ans9QueId = document.getElementById("ans9QueId").value;
				var a1 = '';
				if(ans9QueId.trim() != '') {
					a1 = new Array();
					a1 = ans9QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = 'correct'+a1[i];
					if ($('input[name='+v1+']:checked').length > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag9QueId=0;
				}
			}
			
			if(document.getElementById("ans12QueId")) {
				var ans12QueId = document.getElementById("ans12QueId").value;
				var a1 = '';
				if(ans12QueId.trim() != '') {
					a1 = new Array();
					a1 = ans12QueId.split(",");
				}
				console.log(ans12QueId);
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = document.getElementById(a1[i]).value;
					if (v1.trim().length > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag12QueId=0;
				}
			}
			
			 if(document.getElementById("ans4QueId")) {
					var ans4QueId = document.getElementById("ans4QueId").value;
					var a1 = '';
					if(ans4QueId.trim() != '') {
						a1 = new Array();
						a1 = ans4QueId.split(",");
					}
					console.log(ans4QueId);
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v1 = a1[i];
						console.log(v1);
						if ($('input[name='+v1+']:checked').length > 0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag4QueId=0;
					}
				}
			 
			 if(document.getElementById("ans7QueId")) {
					var ans7QueId = document.getElementById("ans7QueId").value;
					var a1 = '';
					if(ans7QueId.trim() != '') {
						a1 = new Array();
						a1 = ans7QueId.split(",");
					}
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v2 = document.getElementById('remarkwithscore'+a1[i]).value;
						var marks = document.getElementById('marks'+a1[i]).value;
						if (v2.trim().length > 0 && parseInt(marks)>0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag7QueId=0;
					}
				}
			 if(document.getElementById("ans6QueId")) {/* Save issue  */
					var ans6QueId = document.getElementById("ans6QueId").value;
					var a1 = '';
					if(ans6QueId.trim() != '') {
						a1 = new Array();
						a1 = ans6QueId.split(",");
					}
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v1 = a1[i];
						if ($('input[name='+v1+']:checked').length > 0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag6QueId=0;
					}
				}
			 if(document.getElementById("ans10QueId")) {
					var ans10QueId = document.getElementById("ans10QueId").value;
					var a1 = '';
					if(ans10QueId.trim() != '') {
						a1 = new Array();
						a1 = ans10QueId.split(",");
					}
					var cnt=0;
					 for(var i=0; i<a1.length; i++) {
						 var vA = document.getElementById('a'+a1[i]).value;
						 var vB = document.getElementById('b'+a1[i]).value;
						 var vC = document.getElementById('c'+a1[i]).value;
						 var vD = document.getElementById('d'+a1[i]).value;
						 var marks = document.getElementById('marks'+a1[i]).value;
						 if ((vA.trim().length >0 || vB.trim().length >0 || vC.trim().length >0 || vD.trim().length >0) && parseInt(marks)>0) {
							    cnt++;
						 }
						
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag10QueId=0;
					}
				}
			/* End dattatray */
				//alert("cnt ===>> " + cnt);&& flag5QueId == 1
			if(flag13QueId == 1 && flag11QueId==1 && flag3QueId == 1 && flag5QueId == 1 && flag1QueId == 1 && flag2QueId == 1 && flag8QueId == 1 && flag9QueId == 1 && flag12QueId == 1 && flag4QueId == 1 && flag7QueId == 1 && flag6QueId == 1 && flag10QueId == 1) {
				submitForm1('submit');
				/* var levelAppSystem = document.getElementById("levelAppSystemId").value;
				var levelcomment = document.getElementById('levelcomment'+levelAppSystem).value;
				if(levelcomment.trim().length>0) {
					submitForm1('submit');
				} else {
					alert("Please fill the comment, then click on Next button.");
				} */
			} else {
				alert("Please answer all the questions, then click on Next button.");
			}
			/* } else {
				
				submitForm1('submit');
			} */
		});
		
		$("input[name='btnSave']").click(function(e){
			e.preventDefault();
			
			submitForm1('btnSave');
		});
		
		
		$("input[name='btnfinish']").click(function(e){
			e.preventDefault();
			/* $('.validateRequired').filter(':hidden').prop('required',false);
			$('.validateRequired').filter(':visible').prop('required',true); */
			var flag13QueId = 1;
			var flag11QueId = 1;
			/* Start dattatray */
			var flag1QueId = 1;
			var flag2QueId = 1;
			var flag3QueId = 1;
			var flag4QueId = 1;
			var flag5QueId = 1;
			var flag6QueId = 1;
			var flag7QueId = 1;
			var flag8QueId = 1;
			var flag9QueId = 1;
			var flag10QueId = 1;
			var flag12QueId = 1;
			
			/* End dattatray */
			if(document.getElementById("ans13QueId")) {
				var ans13QueId = document.getElementById("ans13QueId").value;
				var a1 = '';
				if(ans13QueId.trim() != '') {
					a1 = new Array();
					a1 = ans13QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = 'correct'+a1[i];
					if ($('input[name='+v1+']:checked').length > 0) {
					    cnt++;
					}
				}
				if(a1.length != cnt) {
					flag13QueId=0;
				}
			}
			if(document.getElementById("ans11QueId")) {
				var ans11QueId = document.getElementById("ans11QueId").value;
				var a1 = '';
				if(ans11QueId.trim() != '') {
					a1 = new Array();
					a1 = ans11QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = document.getElementById('gradewithrating'+a1[i]).value;
					if (parseFloat(v1) > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag11QueId=0;
				}
			}
			
			/* Start dattatray */
			if(document.getElementById("ans3QueId")) {
				var ans3QueId = document.getElementById("ans3QueId").value;
				var a1 = '';
				if(ans3QueId.trim() != '') {
					a1 = new Array();
					a1 = ans3QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = document.getElementById('marks'+a1[i]).value;
					if (parseFloat(v1) > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag3QueId=0;
				}
			}
			
			 if(document.getElementById("ans5QueId")) {/* Save issue  */
				var ans5QueId = document.getElementById("ans5QueId").value;
				var a1 = '';
				if(ans5QueId.trim() != '') {
					a1 = new Array();
					a1 = ans5QueId.split(",");
				}
				var cnt=0;
				for(var i=0; i<a1.length; i++) {
					var v1 = a1[i];
					if ($('input[name='+v1+']:checked').length > 0) {
					    cnt++;
					}
				}
				//alert(a1.length+" == "+cnt);
				if(a1.length != cnt) {
					flag5QueId=0;
				}
			}
			 
			 if(document.getElementById("ans1QueId")) {
					var ans1QueId = document.getElementById("ans1QueId").value;
					var a1 = '';
					if(ans1QueId.trim() != '') {
						a1 = new Array();
						a1 = ans1QueId.split(",");
					}
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v1 = 'correct'+a1[i];
						var v2 = document.getElementById('multiplewithremark'+a1[i]).value;
						if ($('input[name='+v1+']:checked').length > 0 && v2.trim().length >0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag1QueId=0;
					}
				}
			 
			 if(document.getElementById("ans2QueId")) {
					var ans2QueId = document.getElementById("ans2QueId").value;
					var a1 = '';
					if(ans2QueId.trim() != '') {
						a1 = new Array();
						a1 = ans2QueId.split(",");
					}
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v1 = 'correct'+a1[i];
						if ($('input[name='+v1+']:checked').length > 0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag2QueId=0;
					}
				}
			 
			 if(document.getElementById("ans8QueId")) {
					var ans8QueId = document.getElementById("ans8QueId").value;
					var a1 = '';
					if(ans8QueId.trim() != '') {
						a1 = new Array();
						a1 = ans8QueId.split(",");
					}
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v1 = 'correct'+a1[i];
						if ($('input[name='+v1+']:checked').length > 0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag8QueId=0;
					}
				}
			 
			 if(document.getElementById("ans9QueId")) {
					var ans9QueId = document.getElementById("ans9QueId").value;
					var a1 = '';
					if(ans9QueId.trim() != '') {
						a1 = new Array();
						a1 = ans9QueId.split(",");
					}
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v1 = 'correct'+a1[i];
						if ($('input[name='+v1+']:checked').length > 0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag9QueId=0;
					}
				}
			 
			 if(document.getElementById("ans12QueId")) {
					var ans12QueId = document.getElementById("ans12QueId").value;
					var a1 = '';
					if(ans12QueId.trim() != '') {
						a1 = new Array();
						a1 = ans12QueId.split(",");
					}
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v1 = document.getElementById(a1[i]).value;
						if (v1.trim().length >0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag12QueId=0;
					}
				}
			 
			 if(document.getElementById("ans4QueId")) {
					var ans4QueId = document.getElementById("ans4QueId").value;
					var a1 = '';
					if(ans4QueId.trim() != '') {
						a1 = new Array();
						a1 = ans4QueId.split(",");
					}
					console.log(ans4QueId);
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v1 = a1[i];
						console.log(v1);
						if ($('input[name='+v1+']:checked').length > 0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag4QueId=0;
					}
				}
			 
			 if(document.getElementById("ans7QueId")) {
					var ans7QueId = document.getElementById("ans7QueId").value;
					var a1 = '';
					if(ans7QueId.trim() != '') {
						a1 = new Array();
						a1 = ans7QueId.split(",");
					}
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v2 = document.getElementById('remarkwithscore'+a1[i]).value;
						var marks = document.getElementById('marks'+a1[i]).value;
						if (v2.trim().length > 0 && parseInt(marks) > 0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag7QueId=0;
					}
				}
			 
			 if(document.getElementById("ans6QueId")) {/* Save issue  */
					var ans6QueId = document.getElementById("ans6QueId").value;
					var a1 = '';
					if(ans6QueId.trim() != '') {
						a1 = new Array();
						a1 = ans6QueId.split(",");
					}
					var cnt=0;
					for(var i=0; i<a1.length; i++) {
						var v1 = a1[i];
						if ($('input[name='+v1+']:checked').length > 0) {
						    cnt++;
						}
					}
					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag6QueId=0;
					}
				}
			 

			 if(document.getElementById("ans10QueId")) {
					var ans10QueId = document.getElementById("ans10QueId").value;
					var a1 = '';
					if(ans10QueId.trim() != '') {
						a1 = new Array();
						a1 = ans10QueId.split(",");
					}
					var cnt=0;
					 for(var i=0; i<a1.length; i++) {
						 var vA = document.getElementById('a'+a1[i]).value;
						 var vB = document.getElementById('b'+a1[i]).value;
						 var vC = document.getElementById('c'+a1[i]).value;
						 var vD = document.getElementById('d'+a1[i]).value;
						 var marks = document.getElementById('marks'+a1[i]).value;
						 if ((vA.trim().length >0 || vB.trim().length >0 || vC.trim().length >0 || vD.trim().length >0) && parseInt(marks)>0) {
							    cnt++;
						 }
						
					}
 					//alert(a1.length+" == "+cnt);
					if(a1.length != cnt) {
						flag10QueId=0;
					}
				} 
			/* End dattatray */
				//alert("cnt ===>> " + cnt);&& flag5QueId == 1 remarkwithoutscore
			if(flag13QueId == 1 && flag11QueId==1 && flag3QueId == 1 && flag5QueId == 1 && flag1QueId == 1 && flag2QueId == 1 && flag8QueId == 1 && flag9QueId == 1 && flag12QueId == 1 && flag4QueId == 1 && flag7QueId == 1 && flag6QueId == 1 && flag10QueId == 1) {/* Created by dattatray */
				var araOfI = 1;
				var araOfS = 1;
				if(document.getElementById("areasOfImprovement")) {
					var araOfIVal = document.getElementById("areasOfImprovement").value;
					//alert("araOfIVal ===>> " + araOfIVal);
					if(araOfIVal.length==0) {
						araOfI=0;
					}
				}
				if(document.getElementById("areasOfStrength")) {
					var araOfSVal = document.getElementById("areasOfStrength").value;
					//alert("araOfSVal ===>> " + araOfSVal);
					if(araOfSVal.length==0) {
						araOfS=0;
					}
				}
				if(araOfSVal==0) {
					alert("Please add areas of Strength.");
				} else if(araOfIVal==0) {
					alert("Please add areas of Improvement.");
				} else { 
					submitForm1('btnfinish');
				}
				/* var levelAppSystem = document.getElementById("levelAppSystemId").value;
				var levelcomment = document.getElementById('levelcomment'+levelAppSystem).value;
				if(levelcomment.trim().length>0) {
					submitForm1('submit');
				} else {
					alert("Please fill the comment, then click on Next button.");
				} */
			} else {
				alert("Please answer all the questions, then click on Preview button.");
			}
			
		});
		
		

	function submitForm1(submit) {
		//e.preventDefault();

		if($("#file").attr('path') !== undefined) {
			var id = document.getElementById("id").value;
			var empID = document.getElementById("empID").value;
			var userType = document.getElementById("userType").value;
			var dataType = document.getElementById("dataType").value;
			var appFreqId = document.getElementById("appFreqId").value;
			/* var fileInput = document.getElementById('levelcommentFile');
			var file = fileInput.files[0];
			var form_data = new FormData();
			form_data('levelcommentFile', file);
			alert("form_data ===>> " + form_data); */
		  	var form_data = new FormData($("#StaffAppraisalFormID")[0]);
		  	//var form_data = $("#StaffAppraisalFormID").serialize();
		  	form_data.append("levelcommentFile", $("#file").attr('path'));
		  	if(submit != null && submit == "btnfinish") {
		  		form_data.append("btnfinish", 'Preview');
		    	//form_data = form_data +"&btnfinish=Preview";
		    } else if(submit != null && submit == "submit") {
		    	form_data.append("submit", 'Next');
		    	//form_data = form_data +"&submit=Next";
		    } else if(submit != null && submit == "btnSave") {
		    	form_data.append("btnSave", 'Save');
		    	//form_data = form_data +"&btnSave=Save";
		    }
		  	//alert("form_data ===>> " + form_data);
		  	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		  	$.ajax({
			   	url: "StaffAppraisal.action", 
			   	type: 'POST',
	      		data: form_data,
	      		contentType: false,
	            cache: false,
	      		processData: false,
			   	success: function(result){
			       	$("#divResult").html(result);
			   	},
			   	error: function(result){
			       	if(submit != null && submit == "btnfinish") {
			       		$.ajax({
							url: 'StaffAppraisalPreview.action?id='+id+'&empID='+empID+'&userType='+userType+'&appFreqId='+appFreqId+'&dataType='+dataType,
							cache: true,
							success: function(result){
								$("#divResult").html(result);
					   		}
						});
			       	}
			   	}
			});
		} else {
			var form_data = $("#StaffAppraisalFormID").serialize();
				if(submit != null && submit == "btnfinish") {
			    	form_data = form_data +"&btnfinish=Preview";
			    } else if(submit != null && submit == "submit") {
			    	form_data = form_data +"&submit=Next";
			    } else if(submit != null && submit == "btnSave") {
			    	form_data = form_data +"&btnSave=Save";
			    }
			   	var id = document.getElementById("id").value;
				var empID = document.getElementById("empID").value;
				var userType = document.getElementById("userType").value;
				var dataType = document.getElementById("dataType").value;
				var appFreqId = document.getElementById("appFreqId").value;
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
				   	url: "StaffAppraisal.action?"+this.name, 
				   	type: "POST",
				   	data: form_data,
				   	success: function(result){
				       	$("#divResult").html(result);
				   	},
				   	error: function(result){
				       	if(submit != null && submit == "btnfinish") {
				       		$.ajax({
								url: 'StaffAppraisalPreview.action?id='+id+'&empID='+empID+'&userType='+userType+'&appFreqId='+appFreqId+'&dataType='+dataType,
								cache: true,
								success: function(result){
									$("#divResult").html(result);
						   		}
							});
				       	}
				   	}
				});
			}
		}
	/* }); */
	

	function appendToLevelCommentBox(levelId) {
		if(document.getElementById("ans13QueId")) {
			var ans13QueId = document.getElementById("ans13QueId").value;
			var a1 = '';
			if(ans13QueId.trim() != '') {
				a1 = new Array();
				a1 = ans13QueId.split(",");
			}
			var strAppendAllComment = '';
			for(var i=0; i<a1.length; i++) {
				var v1 = document.getElementById('anscomment'+a1[i]).value;
				if(v1.length > 0) {
					strAppendAllComment += v1 + "\r\n";
				}
			}
			//alert("cnt ===>> " + cnt);
		}
		document.getElementById('levelcomment'+levelId).value = strAppendAllComment;
	}
	
	/* function isNumber(n,id,val) {
		if((isNaN(parseInt(n)))){
			document.getElementById(id).value='';
			if(n.length>0){
				alert("Not a Number");
			}
		} else {
			document.getElementById(id).value = parseInt(n);
		}
		if(parseInt(n)>parseInt(val)){
			document.getElementById(id).value='';
			alert("Value is greater than Weightage");
		}
	} */
	
	
	/* function showQuestions(){
		document.getElementById("queAsnDiv").style.display = "block";
		document.getElementById("startDiv").style.display = "none";
	} */
	
		$(function() {
			
			$('#default').raty();
			 <%double dblPrimary = 0;
				if (alSkills != null && alSkills.size() != 0) {
					for (int i = 0; i < alSkills.size(); i++) {
						List alInner = (List) alSkills.get(i);%>
					<%if (i == 0) {
							dblPrimary = uF.parseToDouble((String) alInner
									.get(2)) / 2;
						}
					}
				}%>
			$('#skillPrimary').raty({
				  readOnly: true,
				  start:    <%=dblPrimary%>,
				  half: true
				});
				
		<%if (arrEnabledModules != null
						&& ArrayUtils.contains(arrEnabledModules,
								IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
					double dblScorePrimary = 0, aggregeteMarks = 0, totAllAttribMarks = 0;
					int count = 0;

					for (int i = 0; elementouterList != null
							&& i < elementouterList.size(); i++) {
						List<String> innerList = elementouterList.get(i);
						List<List<String>> attributeouterList1 = hmElementAttribute
								.get(innerList.get(0).trim());
						for (int j = 0; attributeouterList1 != null
								&& j < attributeouterList1.size(); j++) {
							List<String> attributeList1 = attributeouterList1
									.get(j);
							totAllAttribMarks += uF
									.parseToDouble(hmScoreAggregateMap
											.get(attributeList1.get(0).trim()));
							count++;
						}
					}
					aggregeteMarks = totAllAttribMarks / count;

					dblScorePrimary = aggregeteMarks / 20;%>
			$('#skillPrimaryAttrib').raty({
				  readOnly: true,
				  start:    <%=dblScorePrimary%>,
				  half: true
				});
			
		
		<%}%>
		});

			
</script>
</g:compress>


<%
	List<String> appraisalList = (List<String>) request
			.getAttribute("appraisalList");
	Map<String, String> hmEmpDetails = (Map) request
			.getAttribute("hmEmpDetails");
	Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request
			.getAttribute("hmQuestion");
	/* List<List<String>> questionList = (List<List<String>>) request.getAttribute("questionList"); */
	Map<String, List<List<String>>> hmLevelQuestion = (Map<String, List<List<String>>>) request
			.getAttribute("hmLevelQuestion");
	Map<String, Map<String, String>> hmSubsection = (Map<String, Map<String, String>>) request
			.getAttribute("hmSubsection");
	List<String> mainLevelList = (List<String>) request
			.getAttribute("mainLevelList");
	//List<String> levelList = (List<String>) request.getAttribute("mainLevelList");
	/* String tab=(String) request.getAttribute("tab"); */
	Map<String, List<List<String>>> answertypeSub = (Map<String, List<List<String>>>) request
			.getAttribute("answertypeSub");
	Map<String, String> hmLevelName = (Map<String, String>) request
			.getAttribute("hmLevelName");
	Map<String, Map<String, String>> questionanswerMp = (Map<String, Map<String, String>>) request
			.getAttribute("questionanswerMp");
	Map<String, List<List<String>>> hmQuestionanswerType = (Map<String, List<List<String>>>) request
			.getAttribute("hmQuestionanswerType");
	String currentLevel = (String) request.getAttribute("currentLevel");
	//System.out.println("SA.jsp/948--currentLevel=="+currentLevel);
	Map<String, String> levelStatus = (Map<String, String>) request
			.getAttribute("LEVEL_STATUS");
	if (hmLevelName == null)
		hmLevelName = new HashMap<String, String>();
	List<String> answerTypeList = new ArrayList<String>();

	Map<String, String> hmEmpList = (Map<String, String>) request
			.getAttribute("hmEmpList");
	Map<String, String> hmMesures = (Map<String, String>) request
			.getAttribute("hmMesures");
	Map<String, String> hmMesuresType = (Map<String, String>) request
			.getAttribute("hmMesuresType");

	CommonFunctions CF = (CommonFunctions) session
			.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmEmpName = (Map<String, String>) request
			.getAttribute("hmEmpName");

	Map<String, List<List<String>>> hmGoalTitle = (Map<String, List<List<String>>>) request
			.getAttribute("hmGoalTitle");

	Map<String, List<List<String>>> hmKRA = (Map<String, List<List<String>>>) request
			.getAttribute("hmKRA");

	List<String> memberList = (List<String>) request
			.getAttribute("memberList");
	Map<String, String> orientationMemberMp = (Map<String, String>) request
			.getAttribute("orientationMemberMp");

	String strUserTypeId = (String) session
			.getAttribute(IConstants.USERTYPEID);
	String id = request.getParameter("id");

	Map<String, String> hmTarget = (Map<String, String>) request
			.getAttribute("hmTarget");

	String strSessionEmpId = (String) session
			.getAttribute(IConstants.EMPID);

	Map<String, String> hmMesures1 = (Map<String, String>) request
			.getAttribute("hmMesures1");
	Map<String, String> hmMesuresType1 = (Map<String, String>) request
			.getAttribute("hmMesuresType1");

	Map<String, List<List<String>>> hmGoalTitle1 = (Map<String, List<List<String>>>) request
			.getAttribute("hmGoalTitle1");

	LinkedHashMap<String, List<List<String>>> hmKRA1 = (LinkedHashMap<String, List<List<String>>>) request
			.getAttribute("hmKRA1");

	List<String> memberList1 = (List<String>) request
			.getAttribute("memberList1");
	Map<String, String> orientationMemberMp1 = (Map<String, String>) request
			.getAttribute("orientationMemberMp1");

	Map<String, String> hmKRARating = (Map<String, String>) request
			.getAttribute("hmKRARating");

	String userType = request.getParameter("userType");
	boolean levelFlag = (Boolean) request.getAttribute("levelFlag");
	boolean existLevelFlag = (Boolean) request
			.getAttribute("existLevelFlag");

	List<String> listRemainOrientType = (List<String>) request
			.getAttribute("listRemainOrientType");

	Map<String, String> hmSectionGivenAllQueFlag = (Map<String, String>) request
			.getAttribute("hmSectionGivenAllQueFlag");
	if (hmSectionGivenAllQueFlag == null)
		hmSectionGivenAllQueFlag = new HashMap<String, String>();
	List<String> questionTotalList = (List<String>) request.getAttribute("questionTotalList");/* Create by dattatray */
		
	Map<String, String> hmUsersFeedbackReopenComment = (Map<String, String>)request.getAttribute("hmUsersFeedbackReopenComment");
	if(hmUsersFeedbackReopenComment==null) hmUsersFeedbackReopenComment = new HashMap<String, String>();
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	

/* ===start parvez date: 27-02-2023=== */
	Map<String, String> othrQueType = (Map<String, String>) request.getAttribute("othrQueType");
	if(othrQueType==null) othrQueType = new HashMap<String, String>();;
/* ===end parvez date: 27-02-2023=== */
	
%>


<div class="leftbox reportWidth" id="divResult">
	<div class="addgoaltoreview">
		<h4><%=appraisalList.get(1)%></h4>
	
	<!-- ===start parvez date: 23-03-2022=== -->	
		<div style="line-height: 12px;" id="textlabel"><%=appraisalList.get(2)%></div>
	<!-- ===end parvez date: 23-03-2022=== -->	
		
		<!-- <div class="addgoaltoreview-arrow"></div> -->
		<div style="float: right; margin: 0px 20px 0px 0px;">
			<!-- font-size: 14px; -->
			<table>
				<tr>
					<td>Review feedback for <b><%=hmEmpName.get(empID)%></b>
					</td>
					<td class="textblue">[Role- <b><%=hmEmpDetails.get("ORIENTATION")%></b>]</td>
				</tr>
			</table>
		</div>
	</div>
	
<!-- ===start parvez date: 07-07-2022=== -->	
	<%-- <% System.out.println("levelCount="+request.getAttribute("levelCount")+"---status="+hmUsersFeedbackReopenComment); %> --%>
	<% if ((uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_REVIEW_REOPEN_BY_HR_GHR_FOR_UPDATE_FEEDBACK)) || uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_REVIEW_REOPEN_BY_MANAGER_FOR_UPDATE_FEEDBACK))) 
			&& hmUsersFeedbackReopenComment != null && !hmUsersFeedbackReopenComment.isEmpty() && hmUsersFeedbackReopenComment.size()>0) { %>
		<div>
			<div style="line-height: 12px; " id="textlabel"><b>Reopen FeedBack</b></div>
			<div style="line-height: 12px; padding-top: 10px;" id="textlabel"><%=hmUsersFeedbackReopenComment.get("REOPEN_REASON") %></div>
		</div>
	<% } %>
<!-- ===end parvez date: 07-07-2022=== -->	

	<div id="queAsnDiv">
		<%
			int size = 100 / mainLevelList.size();
			/* String sectionCount = (String)request.getAttribute("sectionCount"); */
			String questionCount = (String) request.getAttribute("questionCount");/* Create by dattatray */
			double completePercent = (uF.parseToDouble(questionCount) / uF.parseToDouble("" + questionTotalList.size())) * 100;
			long intcompletePercent = Math.round(completePercent);
		%>
		<br />
		<%
			if (intcompletePercent < 33.33) {
		%>
		<span class="badge bg-red marginbottom5"><%=intcompletePercent%>%</span>
		<div class="progress progress-xs">
			<div class="progress-bar progress-bar-danger"
				style="width: <%=intcompletePercent%>%;"></div>
		</div>
		<%
			} else if (intcompletePercent >= 33.33
					&& intcompletePercent < 66.67) {
		%>
		<span class="badge progress-bar-yellow marginbottom5"><%=intcompletePercent%>%</span>
		<div class="progress progress-xs">
			<div class="progress-bar progress-bar-yellow"
				style="width: <%=intcompletePercent%>%;"></div>
		</div>
		<%
			} else if (intcompletePercent >= 66.67) {
		%>
		<span class="badge bg-green marginbottom5"><%=intcompletePercent%>%</span>
		<div class="progress progress-xs">
			<div class="progress-bar progress-bar-green"
				style="width: <%=intcompletePercent%>%;"></div>
		</div>
		<%
			}
		%>

		<div class="reviewbar">
			<div class="step-tab instruction-step-tab">
				<a href="javascript:void(0)"
					onclick="getStepTabContent(<s:property value="id"/>,<s:property value="empID"/>,<s:property value="userType"/>,<%=currentLevel%>,<s:property value="role"/>,<s:property value="appFreqId"/>,null)">Instruction</a>
			</div>
			<%
				size = mainLevelList.size();
			
				for (int i = 0; i < mainLevelList.size(); i++) {
					if (request.getAttribute("levelCount").toString().equals("1")) {
						size = 0;
					}
					if (currentLevel.equals(mainLevelList.get(i))
							&& !request.getAttribute("levelCount").toString()
									.equals("1")) {
						//size = i + 1;
						if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) || (uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(userType) != 13)){
							size = i + 1;
						}
						//System.out.println("SA/1115--size="+size+"---mainLevelList=="+mainLevelList);
			%>
			<div class="step-tab">
				<img src="images1/icons/bullet-green.png">
			</div>
			<%
				} else {
						if (uF.parseToBoolean(hmSectionGivenAllQueFlag
								.get(mainLevelList.get(i)))) {
							if (levelStatus.get(mainLevelList.get(i)) != null) {
			%>
			<div class="step-tab">
				<a href="javascript:void(0)"
					onclick="getStepTabContent(<s:property value="id"/>,<s:property value="empID"/>,<s:property value="userType"/>,<%=mainLevelList.get(i)%>,'<s:property value="role"/>',<s:property value="appFreqId"/>,<%=uF.parseToInt(request.getAttribute(
									"levelCount").toString()) + 1%>)"><img
					src="images1/icons/bullet-white-1.png">
				</a>
			</div>
			<%
				} else {
			%>
			<div class="step-tab">
				<a href="javascript:void(0)"
					onclick="getStepTabContent(<s:property value="id"/>,<s:property value="empID"/>,<s:property value="userType"/>,<%=mainLevelList.get(i)%>,'<s:property value="role"/>',<s:property value="appFreqId"/>,<%=uF.parseToInt(request.getAttribute(
									"levelCount").toString()) + 1%>)"><img
					src="images1/icons/bullet-white-1.png">
				</a>
			</div>
			<%
				}
						} else {
			%>
			<div class="step-tab">
				<img src="images1/icons/bullet-white-1.png">
			</div>
			<%
				}
					}
				}
			%>
		</div>

		<div class="step-tab-content">
			<%
				int intQueCnt = 0;
				if (!request.getAttribute("levelCount").toString().equals("1")) {
			%>
			<div class="addgoaltoreview">
				<%
					if (listRemainOrientType != null
								&& !listRemainOrientType.equals("")
								&& !listRemainOrientType.isEmpty()) {
				%>
				<br />
				<div>
					<span style="float: left;"> <img src="images1/warning.png"
						style="width: 20px; margin-left: 9px;">
					</span> <span
						style="float: left; margin-left: 3px; margin-top: 3px; font-size: 18px; color: red;"><b>Waiting
							For <%=listRemainOrientType%> Workflow.</b>
					</span>
				</div>
				<%
					} else if (existLevelFlag == true) {
							intQueCnt++;
				%>
				<br />
				<h4>You Already Filled This Section.</h4>
				<br />
				<%
					} else {
						//System.out.println("SAp.jsp/1191---othrquetype===>"+request.getAttribute("othrquetype"));
				%>
			<!-- ===start parvez date: 27-02-2023=== -->	
				<table style="width: 98%;">
					<tr>
						<td>
							<h4><%=size%>)&nbsp;<%=uF.showData(hmLevelName.get("LEVEL_NAME"), "")%>
								<input type="hidden" name="hideSectionId" id="hideSectionId"
											value="<%=uF.showData(hmLevelName.get("APP_LEVEL_ID"), "")%>" />
							</h4>
						</td>
						<td>
					
					<div class="pull-right">Weightage: <%=uF.showData(hmLevelName.get("LEVEL_WEIGHTAGE"), "")%></div></td>
					</tr>
				</table>
			<!-- ===end parvez date: 27-02-2023=== -->	
				<div style="line-height: 12px;"><%=uF.showData(hmLevelName.get("LEVEL_SDESC"), "")%></div>
				<div style="line-height: 12px;"><%=uF.showData(hmLevelName.get("LEVEL_LDESC"), "")%></div>
				<div class="addgoaltoreview-arrow"></div>
				<%
					}
				%>
			</div>
			<%
				}
			%>
			<s:form action="StaffAppraisal" id="StaffAppraisalFormID"
				enctype="multipart/form-data" method="POST" theme="simple">
				<s:hidden name="empID" id="empID" />
				<s:hidden name="id" id="id" />
				<s:hidden name="appFreqId" id="appFreqId" />
				<s:hidden name="levelId" />
				<s:hidden name="currentLevel" />
				<s:hidden name="userType" id="userType" />
				<s:hidden name="role" />
				<s:hidden name="levelCount" />
				<input type="hidden" id="dataType" value="<%=request.getAttribute("dataType") %>" name="dataType" />
				<%
					if (listRemainOrientType != null
								&& !listRemainOrientType.equals("")
								&& !listRemainOrientType.isEmpty()) {
				%>

				<%
					} else if (existLevelFlag == true) {
				%>

				<%
					} else if (request.getAttribute("levelCount").toString()
								.equals("1")) {
							if (appraisalList.get(5) != null
									&& !appraisalList.get(5).equals("")) {
								
				%>
			<!-- ===start parvez date: 23-03-2022=== -->	
				<span id="textlabel" ><%=appraisalList.get(5)%></span>
			<!-- ===end parvez date: 23-03-2022=== -->	
				<%
					} else {
				%>
				<div>No instructions provided.</div>
				<%
					}
						} else {
							List<String> alUserTypeForFeedback = new ArrayList<String>();
							if (appraisalList.get(6) != null) {
								alUserTypeForFeedback = Arrays.asList(appraisalList
										.get(6).split(","));
							}
					
				%>
				<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_HOD_FEEDBACK_FOR_FINAL_RATING_AND_COMMENT)) && uF.parseToInt(userType) == 13){ %>
						
					<% 	
						intQueCnt++;
						Map<String, String> hmUsersFeedbackDetails = (Map<String, String>)request.getAttribute("hmUsersFeedbackDetails");
						if(hmUsersFeedbackDetails == null) hmUsersFeedbackDetails = new HashMap<String, String>();
						
						double weightage = uF.parseToInt(hmUsersFeedbackDetails.get("WEIGHTAGE"));
						double starweight = weightage*20/100;
					%>
						<%-- <input type="hidden" id="dataType" value="<%=request.getAttribute("dataType") %>" name="dataType" /> --%>
						
						<input type="hidden" name="levelAppSystem" id="levelAppSystemId"
									value="<%=hmLevelName.get("APP_LEVEL_ID")%>" />
						
						<div class="notranslate" id="starPrimary"></div>
						<input type="hidden" id="gradewithrating" value="<%=hmUsersFeedbackDetails.get("MARKS") != null ? uF.parseToInt(hmUsersFeedbackDetails.get("MARKS")) / 20 + "" : "0"%>" name="gradewithrating" />
						
						<script type="text/javascript">
							$(function() {
								$('#starPrimary').raty({
									readOnly: false,
									start: <%=hmUsersFeedbackDetails.get("MARKS") != null ? uF.parseToDouble(hmUsersFeedbackDetails.get("MARKS")) / starweight + "" : "0"%>,
									half: true,
									targetType: 'number',
									click: function(score, evt) {
										$('#gradewithrating').val(score);
									}
								});
							});
							
						</script>
						
						<div>
							<div>
								<b>Comment:</b>
							</div>
							<textarea rows="3" cols="100" style="width: 98% !important;" name="levelcomment"><%=uF.showData((String)hmUsersFeedbackDetails.get("COMMENT"), "") %></textarea>
							
						</div>
						
				<% } else{ %>					
				<%			
							//System.out.println("StAp.jsp/1311---alUserTypeForFeedback="+alUserTypeForFeedback);

							if (!alUserTypeForFeedback.contains(userType)) {
							
								int subsectioncnt = 0;
								String strSectionComment = null;
								Set keys = hmLevelQuestion.keySet();
								Iterator it = keys.iterator();
								StringBuilder sbAnsType13QueId = null;
								StringBuilder sbAnsType11QueId = null;//Grade with Rating
								
								StringBuilder sbAnsType1QueId = null;//Multiple Choice With Remark
								StringBuilder sbAnsType2QueId = null;//Multiple Choice Without Remark
								StringBuilder sbAnsType3QueId = null;//Score
								StringBuilder sbAnsType4QueId = null;//Grade with Excellence
								StringBuilder sbAnsType5QueId = null;//Yes/No
								StringBuilder sbAnsType6QueId = null;//Yes/No
								StringBuilder sbAnsType7QueId = null;//Remark With Score
								StringBuilder sbAnsType8QueId = null;//Single Choice
								StringBuilder sbAnsType9QueId = null;//Multiple Choice
								StringBuilder sbAnsType10QueId = null;//Multiple Remark With Score
								StringBuilder sbAnsType12QueId = null;//Remark Without Score
								//System.out.println("StAp.jsp/1333---hmLevelQuestion="+hmLevelQuestion);
								
								while (it.hasNext()) {
									subsectioncnt++;
									String key = (String) it.next();
									List<List<String>> questionList = hmLevelQuestion
											.get(key);

									Map<String, String> hmSubsectionDetails = hmSubsection
											.get(key);
									
									if(hmSubsectionDetails==null) hmSubsectionDetails = new HashMap<String, String>();
									
									
				%>
				<div class="addgoaltoreview">
					<h4><%=size%>.<%=subsectioncnt%>)&nbsp;<%=uF.showData(
									hmSubsectionDetails.get("LEVEL_NAME"), "")%>
						<input type="hidden" name="hideSubsectionId" id="hideSubsectionId"
							value="<%=uF.showData(
									hmSubsectionDetails.get("APP_LEVEL_ID"), "")%>" />
					</h4>
					<div style="line-height: 12px;">
						<%=uF.showData(
									hmSubsectionDetails.get("LEVEL_SDESC"), "")%>
					</div>
					<div style="line-height: 12px;">
						<%=uF.showData(
									hmSubsectionDetails.get("LEVEL_LDESC"), "")%>
					</div>
					<div class="addgoaltoreview-arrow"></div>
				</div>
				<input type="hidden" name="levelAppSystem" id="levelAppSystemId"
					value="<%=hmLevelName.get("APP_LEVEL_ID")%>" />
				<%
					for (int i = 0; questionList != null
											&& i < questionList.size(); i++) {
										intQueCnt++;
										List<String> innerlist = (List<String>) questionList
												.get(i);
										List<String> questioninnerList = hmQuestion
												.get(innerlist.get(1));

										//System.out.println("innerlist.get(14) ===>>"+ innerlist.get(14)+ " -- innerlist.get(15) ===>> "+ innerlist.get(15));

										Map<String, String> innerMp = null;
										if (innerlist.get(14) != null
												&& !innerlist.get(14).equals("")) {
											innerMp = questionanswerMp.get(innerlist
													.get(14)
													+ "question"
													+ innerlist.get(1));
										} else if (innerlist.get(15) != null
												&& !innerlist.get(15).equals("")) {
											innerMp = questionanswerMp.get(innerlist
													.get(15)
													+ "question"
													+ innerlist.get(1));
										} else {
											innerMp = questionanswerMp.get("question"
													+ innerlist.get(1));
										}
										//System.out.println(innerlist.get(1)+" -- innerMp ===>> " +innerMp);

										if (innerMp == null)
											innerMp = new HashMap<String, String>();
										if (innerMp.get("LEVEL_COMMENT") != null) {
											strSectionComment = innerMp
													.get("LEVEL_COMMENT");
											//System.out.println("strSectionComment ===>> " + strSectionComment);
										}
										//System.out.println("othrQueType ===>>"+ othrQueType.get(hmSubsectionDetails.get("APP_LEVEL_ID"))+"---APP_LEVEL_ID=="+hmSubsectionDetails.get("APP_LEVEL_ID"));
				%>
				<div style="width: 100%;">
					<ul>
					<%-- <% System.out.println("SAp.jsp/1293---questioninnerList.get(8)="+questioninnerList.get(8));
					System.out.println("SAp.jsp/1294---App_System_type="+questioninnerList.get(16));
					%> --%>		
						<%-- <li><b><%=size%>.<%=subsectioncnt%>.<%=(i + 1)%>)&nbsp;&nbsp;<%=questioninnerList.get(1)%> --%>
						<li><b><%=size%>.<%=subsectioncnt%>.<%=(i + 1)%>)&nbsp;&nbsp;<%=uF.showData(questioninnerList.get(1),"")%>
						</b> 
						<div class="pull-right">Weightage: <%=uF.showData(questioninnerList.get(18),"")%></div>
					
						<s:if test="innerlist.get(3)!=null">(<%=innerlist.get(12)%>)</s:if>
						</li>
					<!-- ===start parvez date: 27-02-2023=== -->	
						<%if(othrQueType.get(hmSubsectionDetails.get("APP_LEVEL_ID")) == null || (othrQueType.get(hmSubsectionDetails.get("APP_LEVEL_ID")) !=null && !othrQueType.get(hmSubsectionDetails.get("APP_LEVEL_ID")).equals("Without Short Description"))){ %>
						<li>
							Description: <%=uF.showData(questioninnerList.get(17),"")%>
						</li>
						<%} %>
					<!-- ===end parvez date: 27-02-2023=== -->	
						<li>

							<ul style="margin: 10px 10px 10px 30px">
								<li>
									<%
										if (uF.parseToInt(questioninnerList.get(8)) == 1) {
																if (!answerTypeList.contains("1")) {
																	answerTypeList.add("1");
																	//System.out.println("answerTypeList : "+answerTypeList);
																}
																/* Start Dattatray */
																if (sbAnsType1QueId == null) {
									 								sbAnsType1QueId = new StringBuilder();
									 								sbAnsType1QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
									 							} else {
									 								sbAnsType1QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
									 							}/* End Dattatray */
									%>
									<div>
										a) <input type="checkbox" value="a" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> /><%=questioninnerList.get(2)%><br /> 
										b) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

										c) <input type="checkbox" value="c" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> /><%=questioninnerList.get(4)%><br /> 
										d) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />

										<!-- Created by dattatray -->
										<textarea rows="5" cols="100" name="multiplewithremark<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="multiplewithremark<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=innerMp.get("REMARK") != null ? innerMp.get("REMARK") : ""%></textarea>
									</div>
									<div
										id="ansType1cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
											 	} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
											 							if (!answerTypeList.contains("2")) {
											 								answerTypeList.add("2");
											 								//System.out.println("answerTypeList : "+answerTypeList);
											 							}
											 							/* Start Dattatray */
																		if (sbAnsType2QueId == null) {
																			sbAnsType2QueId = new StringBuilder();
																			sbAnsType2QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
											 							} else {
											 								sbAnsType2QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
											 							}/* End Dattatray */
											 %>
									<div>
										a) <input type="checkbox" value="a"
											<%if (innerMp.get("ANSWER") != null&& innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />

										b) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

										c) <input type="checkbox" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />

										d) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%>
									</div>
									<div
										id="ansType2cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> 
											<%
					 							} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
						 							if (!answerTypeList.contains("3")) {
						 								answerTypeList.add("3");
						 								//System.out.println("answerTypeList : "+answerTypeList);
						 							}
													/*  Start Dattatray*/
						 							if (sbAnsType3QueId == null) {
						 								sbAnsType3QueId = new StringBuilder();
						 								sbAnsType3QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
						 							} else {
						 								sbAnsType3QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
						 							}/* End Dattatray  */
					 						%>
									<div>
										<%-- <input type="text" name="marks<%=innerlist.get(1)%>"
											id="marks<%=i%>" style="width: 31px;"
											onkeyup="isNumber(this.value,this.id,'<%=innerlist.get(2)%>');"
											value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : ""%>" />/<%=innerlist.get(2)%> --%>
											<!-- Start Dattatray -->
										<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="marks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 31px;" />

										<script type="text/javascript">
										$(function() {
											$("#sliderscore"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>).slider({
												value : <%=innerMp.get("MARKS") != null ? innerMp
											.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(2)%>,
												step : 1,
												slide : function(event, ui) {
													$("#marks"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").val(ui.value);
													$("#slidemarksscore"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").val($("#sliderscore"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
											$("#slidemarksscore"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").html($("#sliderscore"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
										});
									</script>
										<br />
										<div id="slidemarksscore<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 25%; text-align: center;"></div>
										<div id="sliderscore<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 25%; float: left;"></div>
										<div id="marksscore<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 25%;">0 <span style="float: right;"><%=innerlist.get(2)%></span>
										</div><!-- End Dattatray -->
									</div>
									<div
										id="ansType3cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
							 					} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
							 							if (!answerTypeList.contains("4")) {
							 								answerTypeList.add("4");
							 								//System.out.println("answerTypeList : "+answerTypeList);
							 							}
							 							 /* Start Dattatray  */
							 							if (sbAnsType4QueId == null) {
 															sbAnsType4QueId = new StringBuilder();
 															sbAnsType4QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
 							 							} else {
 							 								sbAnsType4QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
 							 							}/* End Dattatray */
 														List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
 														
 												%>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
												List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").equals(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<%
											}
										%>
									</div>
									<div
										id="ansType4cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />	
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
										 	} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
										 			if (!answerTypeList.contains("5")) {
										 					answerTypeList.add("5");
										 					//System.out.println("answerTypeList : "+answerTypeList);
										 			}
										 			/* Start Dattatray */
										 			if (sbAnsType5QueId == null) {
										 				sbAnsType5QueId = new StringBuilder();
										 				sbAnsType5QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
						 							} else {
						 								sbAnsType5QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
						 							}/* End Dattatray */
										 			List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
 								%>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
																		List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<% } %>
									</div>
									<div
										id="ansType5cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=uF.showData(innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
					 							} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
					 							if (!answerTypeList.contains("6")) {
					 								answerTypeList.add("6");
					 								//System.out.println("answerTypeList : "+answerTypeList);
					 							}
					 							/* Start Dattatray */
									 			if (sbAnsType6QueId == null) {
									 				sbAnsType6QueId = new StringBuilder();
									 				sbAnsType6QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
					 							} else {
					 								sbAnsType6QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
					 							}/* End Dattatray */
					 							List<List<String>> outer = answertypeSub.get(questioninnerList.get(8));
 %>
									<div>
										<%
											for (int j = 0; j < outer.size(); j++) {
													List<String> inner = outer.get(j);
										%>
										<input type="radio" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains(inner.get(0))) {%>
											checked <%}%> value="<%=inner.get(0)%>" /><%=inner.get(1)%><br />
										<%
											}
										%>
									</div>
									<div
										id="ansType6cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
						 							} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
						 							if (!answerTypeList.contains("7")) {
						 								answerTypeList.add("7");
						 								//System.out.println("answerTypeList : "+answerTypeList);
						 							}
						 							/*  Start Dattatray*/
						 							if (sbAnsType7QueId == null) {
						 								sbAnsType7QueId = new StringBuilder();
						 								sbAnsType7QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
						 							} else {
						 								sbAnsType7QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
						 							}/* End Dattatray  */
 											%>
									<div>
									<!-- Start Dattatray -->
										<textarea rows="5" cols="100" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="remarkwithscore<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
										
										<br />
										<%-- <input type="text" name="marks<%=innerlist.get(1)%>"
											id="marks<%=i%>" style="width: 31px;"
											onkeyup="isNumber(this.value,this.id,'<%=innerlist.get(2)%>');"
											value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : ""%>" />/<%=innerlist.get(2)%> --%>
										<input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="outofmarks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" value="<%=innerlist.get(2)%>" /> 
										<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="marks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 31px;" />
										<script type="text/javascript">
										$(function() {
											$("#slidersingleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>).slider({value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(2)%>,
												step : 1,
												slide : function(event, ui) {
													$("#marks"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").val(ui.value);
													$("#slidemarkssingleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").val($("#slidersingleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
											$("#slidemarkssingleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").html($("#slidersingleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
										});
									</script>
										<br />
										<div id="slidemarkssingleopen<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 25%; text-align: center;"></div>
										<div id="slidersingleopen<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 25%; float: left;"></div>
										<div id="markssingleopen<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 25%;">0 <span style="float: right;"><%=innerlist.get(2)%></span>
										</div><!-- End Dattatray -->
									</div>
									<div
										id="ansType7cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
					 							} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
							 							if (!answerTypeList.contains("8")) {
							 								answerTypeList.add("8");
							 								//System.out.println("answerTypeList : "+answerTypeList);
							 							}
							 							/* Start Dattatray  */
							 							if (sbAnsType8QueId == null) {
							 								sbAnsType8QueId = new StringBuilder();
							 								sbAnsType8QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
							 							} else {
							 								sbAnsType8QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
							 							}/* End Dattatray  */
											%>
									<div>
										a) <input type="radio" value="a"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />

										b) <input type="radio" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null
											&& innerMp.get("ANSWER").contains("b")) {%> checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

										c) <input type="radio" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />

										d) <input type="radio" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div>
									<div
										id="ansType8cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=uF.showData(innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
										 			} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
										 				if (!answerTypeList.contains("9")) {
										 					answerTypeList.add("9");
										 					//System.out.println("answerTypeList : "+answerTypeList);
										 				}
										 				/* Start Dattatray  */
							 							if (sbAnsType9QueId == null) {
							 								sbAnsType9QueId = new StringBuilder();
							 								sbAnsType9QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
							 							} else {
							 								sbAnsType9QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
							 							}/* End Dattatray  */
										 %>
									<div>
										a) <input type="checkbox" value="a" 
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />

										b) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

										c) <input type="checkbox" value="c"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />

										d) <input type="checkbox" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
									</div>
									<div
										id="ansType9cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
					 							} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
						 							if (!answerTypeList.contains("10")) {
						 								answerTypeList.add("10");
						 								//System.out.println("answerTypeList : "+answerTypeList);
						 							}
						 							//System.out.println("innerlist : "+innerlist);
						 							//System.out.println("questioninnerList : "+questioninnerList);
						 							/* Start Dattatray  */
						 							 if (sbAnsType10QueId == null) {
						 								sbAnsType10QueId = new StringBuilder();
						 								sbAnsType10QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
						 							} else {
						 								sbAnsType10QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
						 							}/* End Dattatray  */
						 							
						 							String[] a = null;
						 							if (innerMp.get("ANSWER") != null) {
						 								a = innerMp.get("ANSWER").split(":_:");
						 								System.out.print("a[0] : "+a);
						 							}
						 							
					 						
						 							
 											%>
									<div>
										<div style="float: left; margin: 30px 10px 0px 0px;">a)
										</div>
										<div>
											<textarea rows="5" cols="100" name="a<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="a<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=a != null ? a[0] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">b)
										</div>
										<div>
											<textarea rows="5" cols="100" name="b<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="b<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=a != null ? a[1] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">c)
										</div>
										<div>
											<textarea rows="5" cols="100" name="c<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="c<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=a != null ? a[2] : ""%></textarea>
											<br />
										</div>
										<div style="float: left; margin: 30px 10px 0px 0px;">d)
										</div>
										<div>
											<textarea rows="5" cols="100" name="d<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="d<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=a != null ? a[3] : ""%></textarea>
											<br />
										</div>
										<%-- <input type="text" name="marks<%=innerlist.get(1)%>"
											value="<%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : ""%>"
											id="marks<%=i%>" style="width: 31px;"
											onkeyup="isNumber(this.value,this.id,'<%=innerlist.get(2)%>');" />/<%=innerlist.get(2)%> --%>
											<!-- Start Dattatray -->
										<input type="hidden" name="outofmarks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="outofmarks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" value="<%=innerlist.get(2)%>" /> 
										<input type="hidden" name="marks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="marks<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 31px;" />

										<script type="text/javascript">
										$(function() {
											$("#slidermultipleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>).slider({value : <%=innerMp.get("MARKS") != null ? innerMp.get("MARKS") : "0"%>,
												min : 0,
												max : <%=innerlist.get(2)%>,
												step : 1,
												slide : function(event, ui) {
													$("#marks"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").val(ui.value);
													$("#slidemarksmultipleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").html(ui.value);
												}
											});
											$("#marks"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").val($("#slidermultipleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
											$("#slidemarksmultipleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>+"").html($("#slidermultipleopen"+<%=innerlist.get(1)%>+"_"+<%=questioninnerList.get(9)%>).slider("value"));
										});
									</script>
										<br />
										<div id="slidemarksmultipleopen<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 25%; text-align: center;"></div>
										<div id="slidermultipleopen<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 25%; float: left;"></div>
										<div id="marksmultipleopen<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 25%;"> 0 <span style="float: right;"><%=innerlist.get(2)%></span></div>
									</div><!-- End Dattatray -->
									<div id="ansType10cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100" name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=uF.showData(innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
 							} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
			 							if (!answerTypeList.contains("11")) {
			 								answerTypeList.add("11");
			 								//System.out.println("answerTypeList : "+answerTypeList);
			 							}
			 							if (sbAnsType11QueId == null) {
			 								sbAnsType11QueId = new StringBuilder();
			 								sbAnsType11QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
			 							} else {
			 								sbAnsType11QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
			 							}
			 							double weightage = uF.parseToInt(innerMp.get("WEIGHTAGE"));
			 						//===start parvez date: 09-03-2023===	
			 							//double starweight = weightage * 20 / 100;
			 							double starweight = hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? (weightage * 10 / 100) : (weightage * 20 / 100);
			 						//===end parvez date: 09-03-2023===	
			 							//System.out.println("starweight ::::: "+starweight+"---10=="+(weightage * 10 / 100));
 							//System.out.println("innerlist.get(1)_questioninnerList.get(9) ::::: "+innerlist.get(1)+"_"+questioninnerList.get(9));
 %>
									<div class="notranslate" id="starPrimary<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"></div>
									<input type="hidden" id="gradewithrating<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" value="<%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS"))/ starweight + "": "0"%>" name="gradewithrating<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" />
									<script type="text/javascript">
										$(function() {
									    	$('#starPrimary<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>').raty({
									        	readOnly: false,
									        	start: <%=innerMp.get("MARKS") != null ? uF.parseToDouble(innerMp.get("MARKS"))/ starweight + "": "0"%>,
									       /* ===start parvez date: 09-03-2023=== */ 	
									        	number: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? 10 : 5 %>,
									        	half: <%=hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_TEN_STAR_RATING_FOR_REVIEW)) ? false : true %>,
									        /* ===end parvez date: 09-03-2023=== */	
									        	targetType: 'number',
									        	click: function(score, evt) {
									        		$('#gradewithrating<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>').val(score);
												}
											});
										});
									</script>
									<div
										id="ansType11cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> <%
					 							} else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
						 							if (!answerTypeList.contains("12")) {
						 								answerTypeList.add("12");
						 								//System.out.println("answerTypeList : "+answerTypeList);
						 							}
						 							/* Start Dattatray */
						 							if (sbAnsType12QueId == null) {
						 								sbAnsType12QueId = new StringBuilder();
						 								sbAnsType12QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
						 							} else {
						 								sbAnsType12QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
						 							}/* End Dattatray */
 											%>
									<div><!-- Created by Dattatray Note : Id set -->
										<textarea rows="5" cols="100" name="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" id="<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" style="width: 100% !important;"><%=innerMp.get("ANSWER") != null ? innerMp.get("ANSWER") : ""%></textarea>
									</div>
									<div id="ansType12cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> 
						<% } else if (uF.parseToInt(questioninnerList.get(8)) == 13) {
	 							if (!answerTypeList.contains("13")) {
	 								answerTypeList.add("13");
	 								//System.out.println("answerTypeList : "+answerTypeList);
	 							}
	 							if (sbAnsType13QueId == null) {
	 								sbAnsType13QueId = new StringBuilder();
	 								sbAnsType13QueId.append(innerlist.get(1)+ "_"+ questioninnerList.get(9));
	 							} else {
	 								sbAnsType13QueId.append(","+ innerlist.get(1) + "_"+ questioninnerList.get(9));
	 							}
	
	 							List<String> al = new ArrayList<String>();
	
	 							al.add("<input type=\"radio\" value=\"a\" "+ ((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) ? "checked": "") + " name='correct"+ innerlist.get(1) + "_"+ questioninnerList.get(9) + "' />"+ questioninnerList.get(2)+ "<br />");
	
	 							al.add("<input type=\"radio\" value=\"b\" "+ ((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) ? "checked": "") + " name='correct"+ innerlist.get(1) + "_"+ questioninnerList.get(9) + "' />"+ questioninnerList.get(3)+ "<br />");
	
	 							al.add("<input type=\"radio\" value=\"c\" "+ ((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) ? "checked": "") + " name='correct"+ innerlist.get(1) + "_"+ questioninnerList.get(9) + "' />"+ questioninnerList.get(4)+ "<br />");
	
	 							al.add("<input type=\"radio\" value=\"d\" "+ ((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) ? "checked": "") + " name='correct"+ innerlist.get(1) + "_"+ questioninnerList.get(9) + "' />"+ questioninnerList.get(5)+ "<br />");
	
	 							al.add("<input type=\"radio\" value=\"e\" "+ ((innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("e")) ? "checked": "") + " name='correct"+ innerlist.get(1) + "_"+ questioninnerList.get(9) + "' />"+ questioninnerList.get(10)+ "<br />");
	
	 							Collections.shuffle(al);
	 %>

									<div>
										<%-- a) <input type="radio" value="a" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("a")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(2)%><br />
										b) <input type="radio" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("b")) {%>
											checked <%}%> value="b" /><%=questioninnerList.get(3)%><br />

										c) <input type="radio" value="c"<%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("c")) {%>
											checked <%}%> name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" /><%=questioninnerList.get(4)%><br />

										d) <input type="radio" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("d")) {%>
											checked <%}%> value="d" /><%=questioninnerList.get(5)%><br />
											
										e) <input type="radio" name="correct<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>" <%if (innerMp.get("ANSWER") != null && innerMp.get("ANSWER").contains("e")) {%>
											checked <%}%> value="e" /><%=questioninnerList.get(10)%><br /> --%>
										a)<%=al.get(0)%>
										b)<%=al.get(1)%>
										c)<%=al.get(2)%>
										d)<%=al.get(3)%>
										e)<%=al.get(4)%>
									</div>
									<div
										id="ansType13cmnt<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>">
										<br />
										<b>Comment:</b><br />
										<textarea rows="3" cols="100"
											name="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											id="anscomment<%=innerlist.get(1)%>_<%=questioninnerList.get(9)%>"
											style="width: 100% !important;"
											onkeyup="appendToLevelCommentBox('<%=hmLevelName.get("APP_LEVEL_ID")%>');"><%=uF.showData(
											innerMp.get("ANSWERCOMMENT"), "")%></textarea>
									</div> 
						<% } %>			
				<!-- ===start parvez date: 24-12-2021=== -->					
							<% if(questioninnerList.get(16)!=null && uF.parseToInt(questioninnerList.get(16)) == 6 && strUserType.equals(IConstants.EMPLOYEE)){ %>
								<div>
									<input type="hidden" name="appSystemType" id="appSystemType" value="<%=questioninnerList.get(16)%>"></input>	
									<input type="text" name="goalTitle" id="goalTitle" class="validateRequired form-control" required=true></input>
									<br/><span style="font-style: italic;">Please Enter Goal Title</span>
								</div>
							<% } %>		
				<!-- ===end parvez date: 24-12-2021=== -->					
							
								</li>
							</ul></li>
					</ul>
				</div>
				<hr>
				<%
					}
								}
				%>
				<%
					if (intQueCnt > 0) {
				%>
				<div id="levelCmnt">
					<input type="hidden" name="ans13QueId" id="ans13QueId" value="<%=sbAnsType13QueId != null ? sbAnsType13QueId.toString() : ""%>" />
					<input type="hidden" name="ans11QueId" id="ans11QueId" value="<%=sbAnsType11QueId != null ? sbAnsType11QueId.toString() : ""%>" />
					<!-- Stat Dattatray -->
					<input type="hidden" name="ans1QueId" id="ans1QueId" value="<%=sbAnsType1QueId != null ? sbAnsType1QueId.toString() : ""%>" />
					<input type="hidden" name="ans2QueId" id="ans2QueId" value="<%=sbAnsType2QueId != null ? sbAnsType2QueId.toString() : ""%>" />
					<input type="hidden" name="ans3QueId" id="ans3QueId" value="<%=sbAnsType3QueId != null ? sbAnsType3QueId.toString() : ""%>" />
					<input type="hidden" name="ans4QueId" id="ans4QueId" value="<%=sbAnsType4QueId != null ? sbAnsType4QueId.toString() : ""%>" />
					<input type="hidden" name="ans5QueId" id="ans5QueId" value="<%=sbAnsType5QueId != null ? sbAnsType5QueId.toString() : ""%>" />
					<input type="hidden" name="ans6QueId" id="ans6QueId" value="<%=sbAnsType6QueId != null ? sbAnsType6QueId.toString() : ""%>" />
					<input type="hidden" name="ans7QueId" id="ans7QueId" value="<%=sbAnsType7QueId != null ? sbAnsType7QueId.toString() : ""%>" />
					<input type="hidden" name="ans8QueId" id="ans8QueId" value="<%=sbAnsType8QueId != null ? sbAnsType8QueId.toString() : ""%>" />
					<input type="hidden" name="ans9QueId" id="ans9QueId" value="<%=sbAnsType9QueId != null ? sbAnsType9QueId.toString() : ""%>" />
					<input type="hidden" name="ans10QueId" id="ans10QueId" value="<%=sbAnsType10QueId != null ? sbAnsType10QueId.toString() : ""%>" />
					<input type="hidden" name="ans12QueId" id="ans12QueId" value="<%=sbAnsType12QueId != null ? sbAnsType12QueId.toString() : ""%>" />
					<!-- End Dattatray -->
					<br />
					<b>Comment:</b><br />
					<%-- <sup>*</sup>  --%>
					<textarea rows="3" cols="100"
						name="levelcomment<%=hmLevelName.get("APP_LEVEL_ID")%>"
						id="levelcomment<%=hmLevelName.get("APP_LEVEL_ID")%>"
						required="true" style="width: 100% !important;"><%=uF.showData(strSectionComment, "")%></textarea>
					<!-- class="validateRequired"  -->
					<div style="margin-top: 5px;">
						<span id="file"></span>
						<s:file name="levelcommentFile" id="levelcommentFile"
							accept=".jpg,.png,.xlsx,.xls,.pdf,.doc,.docs,.docx"
							onchange="readFileURL(this, 'file');"></s:file>
					</div>
				</div>
				<%
					}
				%>
				<%
					} else {
								String strSectionComment = null;
				%>
				<div id="levelCmnt">
					<input type="hidden" name="levelAppSystem" id="levelAppSystemId"
						value="<%=hmLevelName.get("APP_LEVEL_ID")%>" /> <br />
					<b>Comment:<sup>*</sup>
					</b><br />
					<textarea rows="3" cols="100"
						name="levelcomment<%=hmLevelName.get("APP_LEVEL_ID")%>"
						id="levelcomment<%=hmLevelName.get("APP_LEVEL_ID")%>"
						class="validateRequired" required="true"
						style="width: 100% !important;"><%=uF.showData(
								hmLevelName.get("SECTION_COMMENT"), "")%></textarea>
					<div style="margin-top: 5px;">
						<span id="file"></span>
						<s:file name="levelcommentFile" id="levelcommentFile"
							accept=".jpg,.png,.xlsx,.xls,.pdf,.doc,.docs,.docx"
							onchange="readFileURL(this, 'file');"></s:file>
					</div>
				</div>
				<%
					}
				%>

				<%
					if (mainLevelList.size() == size && intQueCnt > 0) {
				%>
				<div id="reviewCmnt">
					<input type="hidden" name="isAreasOfStrengthAndImprovement"
						value="true">
					<div style="float: left; width: 47%; margin: 5px;">
						<br />
						<b>Areas of Strength:<sup>*</sup>
						</b><br />
						<textarea rows="3" cols="100" name="areasOfStrength"
							id="areasOfStrength" class="validateRequired"
							style="width: 100% !important;"><%=uF.showData("", "N/A")%></textarea>
					</div>
					<div style="float: left; width: 47%; margin: 5px;">
						<br />
						<b>Areas of Improvement:<sup>*</sup>
						</b><br />
						<textarea rows="3" cols="100" name="areasOfImprovement"
							id="areasOfImprovement" class="validateRequired"
							style="width: 100% !important;"><%=uF.showData("", "N/A")%></textarea>
					</div>
				</div>
				<%
					}
				%>

				<%
					if (answerTypeList.contains("4")
									|| answerTypeList.contains("5")
									|| answerTypeList.contains("6")) {
				%>
				<div class="addgoaltoreview">
					<fieldset style="margin: 0px 15px 0px 10px;">
						<legend>Answer Type Structure</legend>
						<table class="table_font" style="margin: 10px 10px 10px 30px;">
							<tr>
								<%
									int k = 1;
												for (int i = 0; i < answerTypeList.size(); i++) {
													List<List<String>> outerList = hmQuestionanswerType
															.get(answerTypeList.get(i));
								%>
								<td valign="top">
									<table class="table_font">
										<%
											for (int j = 0; outerList != null
																	&& j < outerList.size(); j++) {
																List<String> innerlist = (List<String>) outerList
																		.get(j);
										%>
										<tr>
											<%
												if (j == 0) {
											%>
											<td><b><%=k++%>).</b>
											</td>
											<%
												} else {
											%>
											<td>&nbsp;</td>
											<%
												}
											%>
											<td style="text-align: left; min-width: 100px;"><%=innerlist.get(0)%>-<%=innerlist.get(1)%></td>
										</tr>
										<%
											}
										%>
									</table></td>
								<%
									}
								%>
							</tr>
						</table>
					</fieldset>
					<!-- <div class="addgoaltoreview-arrow"></div> -->
				</div>
				<%
					}
				%>

				<%
					}
				%>
				<% } %>
				<div class="clr margintop20">
					<%
						if (mainLevelList.size() == size && intQueCnt > 0) {
					%>
					<%-- <s:submit value="Finish" cssClass="btn btn-primary" name="btnfinish" /> --%>
					<s:submit value="Preview" cssClass="btn btn-primary cstm-validate" name="btnfinish" />
					<s:submit value="Save" cssClass="btn btn-primary" name="btnSave" />
					<!-- <input type="button" value="Finish" class="btn btn-primary" name="btnfinish" onclick="finishForm();"/> -->
					<%
						} else {
							if (request.getAttribute("levelCount").toString().equals("1")) {
					%>
				<!-- ===start parvez date: 22-02-2023=== -->	
					<%-- <s:submit value="Take Assessment" cssClass="btn btn-primary"
						name="submit" /> --%>
						
					<s:submit value="Take the Review" cssClass="btn btn-primary" name="submit" />	
					<%-- <% if(strUserType.equals(IConstants.EMPLOYEE)){ %>	
						<s:submit value="Take the Review" cssClass="btn btn-primary" name="submit" />
					<%}else{ %>	
						<s:submit value="Take Assessment" cssClass="btn btn-primary"
							name="submit" />
					<%} %> --%>
					
				<!-- ===end parvez date: 22-02-2023=== -->		
					<%
						} else if (intQueCnt > 0) {
					%>
					<s:submit value="Next" cssClass="btn btn-primary" name="submit" />
					<s:submit value="Save" cssClass="btn btn-primary" name="btnSave" />
					<%
						} else {
					%>
					<h5>No assessment available, please talk to you HR.</h5>
					<br />
					<%
						}
							}
					%>
				</div>
			</s:form>
		</div>
	</div>



	<div></div>
</div>
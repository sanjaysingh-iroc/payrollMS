<%@ taglib prefix="s" uri="/struts-tags"%>
<style>
.inlineDivs{
display: inline-block;
padding-top: 5px;
}
</style>

<script type="text/javascript">
		function getOrgLocationDepartLevelDesigGradeOrient() {
			//alert("value ===>> " + value);
			xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
	            alert("Browser does not support HTTP Request");
	            return;
		    } else {
		    	var strOrg = getSelectedValue("strOrgOrient");
	            var xhr = $.ajax({
                    url : "GetOrgwiseLocationDepartLevelDesigGrade.action?strOrgId=" + strOrg +'&page=SOrient',
                    cache : false,
                    success : function(data) {
                    	if(data == "") {
                    	} else {
                    		//alert("data --------->> " + data);     
                    		var allData = data.split("::::");
                            document.getElementById("wlocDivOrient").innerHTML = allData[0];
                            document.getElementById("departDivOrient").innerHTML = allData[1];
                           	document.getElementById("levelDivOrient").innerHTML = allData[2];
                           	document.getElementById("myDesigOrient").innerHTML = allData[3];
                           	document.getElementById("myGradeOrient").innerHTML = allData[4];
                           	getEmployeebyOrgOrient();
                    	}
                    }
	            });
		    }
		}
		
		function getEmployeebyOrgOrient(){
			var strOrg = getSelectedValue("strOrgOrient");
			var type = document.getElementById("type").value;
			//alert("strOrg == "+strOrg);
			xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
		       alert("Browser does not support HTTP Request");
		       return;
		    } else {
		    	var action = 'getEmployeeList.action?strOrg=' + strOrg+'&page=SOrient' + "&type="+type;
		       	var xhr = $.ajax({
		           	url : action,
		           	cache : false,
		           	success : function(data) {
			           	if(data == "") {
			           	} else {
		                  	document.getElementById("myEmployeeshoworient").innerHTML = data;
	                  		$("select[name='strOrg']").multiselect().multiselectfilter(); 
	        				$("select[name='strWlocation']").multiselect().multiselectfilter(); 
	        		   		$("select[name='strDepart']").multiselect().multiselectfilter(); 
	        		   		$("select[name='strLevel']").multiselect().multiselectfilter(); 
	        		   		$("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); 
	        		   		$("select[name='empGrade']").multiselect().multiselectfilter();
	        		   		$("select[name='employeeOrient']").multiselect().multiselectfilter();   
			           	}
		           	}
		       	});
		    }
			
			/* var action = 'getEmployeeList.action?strOrg=' + strOrg+'&page=SOrient' + "&type="+type;
			getContent('myEmployeeshoworient', action);
			setTimeout(function(){ 
				$("select[name='strOrg']").multiselect().multiselectfilter(); 
				$("select[name='strWlocation']").multiselect().multiselectfilter(); 
		   		$("select[name='strDepart']").multiselect().multiselectfilter(); 
		   		$("select[name='strLevel']").multiselect().multiselectfilter(); 
		   		$("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); 
		   		$("select[name='empGrade']").multiselect().multiselectfilter();
		   		$("select[name='employeeOrient']").multiselect().multiselectfilter();  
		   	}, 500); */
			
		}
		
		
		function getEmployeebyLocationOrient() {
			var location = getSelectedValue("wlocationOrient");
			var strOrg = getSelectedValue("strOrgOrient");
			var type = document.getElementById("type").value;
			//alert("strOrg == "+strOrg +" location == "+location);
			xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
		       alert("Browser does not support HTTP Request");
		       return;
		    } else {
		    	var action = 'getEmployeeList.action?strOrg='+ strOrg+'&location='+ location+'&page=SOrient' + "&type="+type;
		       	var xhr = $.ajax({
		           	url : action,
		           	cache : false,
		           	success : function(data) {
			           	if(data == "") {
			           	} else {
		                  	document.getElementById("myEmployeeshoworient").innerHTML = data;
	        		   		$("select[name='employeeOrient']").multiselect().multiselectfilter();   
			           	}
		           	}
		       	});
		    }
			
			/* var action = 'getEmployeeList.action?strOrg='+ strOrg+'&location='+ location+'&page=SOrient' + "&type="+type;
			getContent('myEmployeeshoworient', action);
			
			setTimeout(function(){ $("select[name='strOrg']").multiselect().multiselectfilter(); 
				$("select[name='strWlocation']").multiselect().multiselectfilter(); 
		   		$("select[name='strDepart']").multiselect().multiselectfilter(); 
		   		$("select[name='strLevel']").multiselect().multiselectfilter(); 
		   		$("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); 
		   		$("select[name='empGrade']").multiselect().multiselectfilter();
		   		$("select[name='employeeOrient']").multiselect().multiselectfilter();  
		   	}, 500); */
		
		}
		
		
		function getEmployeebyDepartOrient() {
			var location = getSelectedValue("wlocationOrient");
			var depart = getSelectedValue("departOrient");
			var type = document.getElementById("type").value;
			xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
		       alert("Browser does not support HTTP Request");
		       return;
		    } else {
		    	var action = 'getEmployeeList.action?depart=' + depart+'&page=SOrient' + "&type="+type;
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
		                  	document.getElementById("myEmployeeshoworient").innerHTML = data;
	        		   		$("select[name='employeeOrient']").multiselect().multiselectfilter();   
			           	}
		           	}
		       	});
		    }

		    /* var action = 'getEmployeeList.action?depart=' + depart+'&page=SOrient' + "&type="+type;
			if (location == '') {
			} else {
				if (location != '') {
					action += '&location=' + location;
				}
			}
			getContent('myEmployeeshoworient', action);
			setTimeout(function() {
				$("select[name='strOrg']").multiselect().multiselectfilter(); 
				$("select[name='strWlocation']").multiselect().multiselectfilter(); 
		   		$("select[name='strDepart']").multiselect().multiselectfilter(); 
		   		$("select[name='strLevel']").multiselect().multiselectfilter(); 
		   		$("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); 
		   		$("select[name='empGrade']").multiselect().multiselectfilter();
		   		$("select[name='employeeOrient']").multiselect().multiselectfilter();  
		   	}, 500); */
			
		}
		
		
		function getLevelwiseDesigGrade(value) {
			//alert("value ===>> " + value);
			xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
		       alert("Browser does not support HTTP Request");
		       return;
		    } else {
		       	var xhr = $.ajax({
		           	url : "GetLevelwiseDesigAndGrade.action?strLevelId=" + value+"&page=SOrient",
		           	cache : false,
		           	success : function(data) {
			           	if(data == "") {
			           	} else {
			           		//alert("data --------->> " + data);     
			           		var allData = data.split("::::");
		                  	document.getElementById("myDesigOrient").innerHTML = allData[0];
		                  	document.getElementById("myGradeOrient").innerHTML = allData[1];
		                  	$("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); 
		    		   		$("select[name='empGrade']").multiselect().multiselectfilter();
			           	}
		           	}
		       	});
		    }
		}
		
		
		function getEmployeebyLevelOrient() {
			var location = getSelectedValue("wlocationOrient");
			var depart = getSelectedValue("departOrient");
			var Level = getSelectedValue("strLevelOrient");
			var type = document.getElementById("type").value;
			
			getLevelwiseDesigGrade(Level);
			
			xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
		       alert("Browser does not support HTTP Request");
		       return;
		    } else {
		    	var action = 'getEmployeeList.action?level=' + Level+'&page=SOrient' + "&type="+type;
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
		                  	document.getElementById("myEmployeeshoworient").innerHTML = data;
		    		   		$("select[name='employeeOrient']").multiselect().multiselectfilter();  
			           	}
		           	}
		       	});
		    }
		    
			/*  var action = 'getEmployeeList.action?level=' + Level+'&page=SOrient' + "&type="+type;
			if (location == '' && depart == '') {
			} else {
				if (location != '') {
					action += '&location=' + location;
				}
				if (depart != '') {
					action += '&depart=' + depart;
				}
			}
			getContent('myEmployeeshoworient', action); */
			
		}
		
		
		function getEmployeebyDesigOrient() {
			var location = getSelectedValue("wlocationOrient");
			var depart = getSelectedValue("departOrient");
			var Level = getSelectedValue("strLevelOrient");
			var design = getSelectedValue("desigIdVOrient");
			var type = document.getElementById("type").value;
			xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
		       alert("Browser does not support HTTP Request");
		       return;
		    } else {
		    	var action = 'getEmployeeList.action?design=' + design+'&page=SOrient' + "&type="+type;
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
		                  	document.getElementById("myEmployeeshoworient").innerHTML = data;
		    		   		$("select[name='employeeOrient']").multiselect().multiselectfilter();  
			           	}
		           	}
		       	});
		    }
			
		    
		    xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
		       alert("Browser does not support HTTP Request");
		       return;
		    } else {
		    	var action = 'getGrade.action?strDesignation=' + design+'&page=SOrient';
		       	var xhr = $.ajax({
		           	url : action,
		           	cache : false,
		           	success : function(data) {
			           	if(data == "") {
			           	} else {
		                  	document.getElementById("myGradeOrient").innerHTML = data;
		                  	$("select[name='empGrade']").multiselect().multiselectfilter();  
			           	}
		           	}
		       	});
		    }
		    
		    
			/* var action = 'getEmployeeList.action?design=' + design+'&page=SOrient' + "&type="+type;
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
			getContent('myEmployeeshoworient', action);
			setTimeout(function(){ 
				$("select[name='strOrg']").multiselect().multiselectfilter(); 
		   		$("select[name='strWlocation']").multiselect().multiselectfilter(); 
		   		$("select[name='strDepart']").multiselect().multiselectfilter(); 
		   		$("select[name='strLevel']").multiselect().multiselectfilter(); 
		   		$("select[name='strDesignationUpdate']").multiselect().multiselectfilter(); 
		   		$("select[name='empGrade']").multiselect().multiselectfilter();
		   		$("select[name='employeeOrient']").multiselect().multiselectfilter();  
	   		}, 500);
			window.setTimeout(function() {
				getContent('myGradeOrient', 'getGrade.action?strDesignation=' + design+'&page=SOrient');
				$("select[name='empGrade']").multiselect().multiselectfilter();
			}, 500); */
				
		}
		
		
		function getEmployeebyGradeOrient() {
			var location = getSelectedValue("wlocationOrient");
			var depart = getSelectedValue("departOrient");
			var Level = getSelectedValue("strLevelOrient");
			var design = getSelectedValue("desigIdVOrient");
			var grade = getSelectedValue("gradeIdVOrient");
			var type = document.getElementById("type").value;
			
			xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
		       alert("Browser does not support HTTP Request");
		       return;
		    } else {
		    	var action = 'getEmployeeList.action?grade=' + grade+'&page=SOrient' + "&type="+type;
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
		                  	document.getElementById("myEmployeeshoworient").innerHTML = data;
		                  	$("select[name='employeeOrient']").multiselect().multiselectfilter();  
			           	}
		           	}
		       	});
		    }
		    
		    
			/* var action = 'getEmployeeList.action?grade=' + grade+'&page=SOrient' + "&type="+type;
			document.getElementById("employeeOrient").selectedIndex = 0;
			if (location != '')  {
				action += '&location=' + location;
			}if (depart != '')  {
				action += '&depart=' + depart;
			}if (Level != '')  {
				action += '&level=' + Level;
			}if (design != '')  {
				action += '&design=' + design;
			}
			getContent('myEmployeeshoworient', action);
			setTimeout(function(){ 
				$("select[name='strOrg']").multiselect({noneSelectedText: 'Select Organisation'}).multiselectfilter(); 
		   		$("select[name='strWlocation']").multiselect({noneSelectedText: 'Select Location'}).multiselectfilter(); 
		   		$("select[name='strDepart']").multiselect({noneSelectedText: 'Select Department'}).multiselectfilter(); 
		   		$("select[name='strLevel']").multiselect({noneSelectedText: 'Select Level'}).multiselectfilter(); 
		   		$("select[name='strDesignationUpdate']").multiselect({noneSelectedText: 'Select Designation'}).multiselectfilter(); 
		   		$("select[name='empGrade']").multiselect({noneSelectedText: 'Select Grade'}).multiselectfilter();
		   		$("select[name='employeeOrient']").multiselect({noneSelectedText: 'Select Employee'}).multiselectfilter();  
	   		}, 500); */
			
		}
		 
		
		
		function setEmployeeAtOrientation(hideID, lblID,from) {
			//var employee = document.getElementById("employee").value;
			var allEmpid ="";
			var employee=document.getElementById("employeeOrient");
			  for (var i = 0; i < employee.options.length; i++) {
			     if(employee.options[i].selected ==true){
			          //alert(employee.options[i].value);
			          allEmpid = allEmpid + "" + employee.options[i].value +","; 
			      }
			  }
			  //alert("allEmpid == "+allEmpid);
		    xmlhttp = GetXmlHttpObject();
		    if (xmlhttp == null) {
	            alert("Browser does not support HTTP Request");
	            return;
		    } else {
	            var xhr = $.ajax({
                    url : "SetEmployeeToOrientation.action?employee=" + allEmpid,
                    cache : false,
                    success : function(data) {
                    	//alert("lblID "+lblID + "hideID "+hideID);
                    	//alert("data "+data);
                    	if(data == "") {
                    		
                    	} else {
                    		var allData = data.split("::::");
                            document.getElementById(lblID).innerHTML = allData[0];
                            document.getElementById(hideID).value = allData[1];
                            //document.getElementById("statetitle").style.display = 'block';
                    	}
                    }
	            });
		    }
		    
		    if(from != "" & from == "EA"){
		    	$("#modalInfo1").hide();
		    } else {
		  	  $("#modalInfo1").hide();
		    }
		}
		
		function GetXmlHttpObject() {
		    if (window.XMLHttpRequest) {
		            // code for IE7+, Firefox, Chrome, Opera, Safari
		            return new XMLHttpRequest();
		    }
		    if (window.ActiveXObject) {
		            // code for IE6, IE5
		            return new ActiveXObject("Microsoft.XMLHTTP");
		    }
		    return null;
		}
		$(function(){
			$("select[name='strOrg']").multiselect({noneSelectedText: 'Select Organisation'}).multiselectfilter(); 
	   		$("select[name='strWlocation']").multiselect({noneSelectedText: 'Select Location'}).multiselectfilter(); 
	   		$("select[name='strDepart']").multiselect({noneSelectedText: 'Select Department'}).multiselectfilter(); 
	   		$("select[name='strLevel']").multiselect({noneSelectedText: 'Select Level'}).multiselectfilter(); 
	   		$("select[name='strDesignationUpdate']").multiselect({noneSelectedText: 'Select Designation'}).multiselectfilter(); 
	   		$("select[name='empGrade']").multiselect({noneSelectedText: 'Select Grade'}).multiselectfilter();
	   		$("select[name='employeeOrient']").multiselect({noneSelectedText: 'Select Employeee'}).multiselectfilter();
		});
</script>
	
<%  String hideID = (String)request.getAttribute("hideID");
	String lblID = (String)request.getAttribute("lblID");
	String type =(String)request.getParameter("type");
	String from =(String)request.getParameter("from");
%>

	<input type="hidden" name="type" id="type" value="<%=type%>"/>
	<table class="table table_no_border">
		<tr>
              <th style="text-align:right" valign="top">Group</th>
              <td> 
              		<div class="inlineDivs">
              		<s:hidden name="type" id="type"></s:hidden>
                       <s:select theme="simple" cssClass="validateRequired" name="strOrg" list="organisationList" id="strOrgOrient" listKey="orgId" listValue="orgName" 
                            required="true" onchange="getOrgLocationDepartLevelDesigGradeOrient();" multiple="true" 
                           size="4" cssStyle="width:150px;"/> <!-- getOrgLocationDepartLevelDesigGrade(this.value), -->
              		</div>                                  
              
               	<div id="wlocDivOrient" class="inlineDivs">
                       <s:select theme="simple" cssClass="validateRequired" name="strWlocation" list="workList" id="wlocationOrient" listKey="wLocationId" listValue="wLocationName" 
                           required="true" value="wlocationvalue" onchange="getEmployeebyLocationOrient();" multiple="true" size="4" cssStyle="width:150px;"/>
                      
                  </div>
                            
               	<div id="departDivOrient" class="inlineDivs">
                       <s:select theme="simple" cssClass="validateRequired" name="strDepart" list="departmentList" id="departOrient" listKey="deptId" listValue="deptName"
                           required="true" onchange="getEmployeebyDepartOrient();" multiple="true" size="4" value="departmentvalue" cssStyle="width:150px;"/>
              		</div>
              
               	<div id="levelDivOrient" class="inlineDivs">
                       <s:select theme="simple" cssClass="validateRequired" name="strLevel" list="levelList" listKey="levelId" id="strLevelOrient" listValue="levelCodeName" 
                           required="true" onchange="getEmployeebyLevelOrient()" multiple="true" size="4" value="levelvalue" cssStyle="width:150px;"/>
				</div>
                   
                   <div id="myDesigOrient" class="inlineDivs">
                           <s:select theme="simple" cssClass="validateRequired" name="strDesignationUpdate" list="desigList" listKey="desigId" id="desigIdVOrient" listValue="desigCodeName"
                               onchange="getEmployeebyDesigOrient();" multiple="true" size="4" value="desigvalue" cssStyle="width:150px;"/>
                   </div>
              
                   <div id="myGradeOrient" class="inlineDivs">
                           <s:select theme="simple" cssClass="validateRequired" name="empGrade" list="gradeList" listKey="gradeId" listValue="gradeCode" id="gradeIdVOrient"
                               onchange="getEmployeebyGradeOrient();" multiple="true" size="4" value="gradevalue" cssStyle="width:150px;"/>
                   </div>
               </td>
           </tr>                       
           <tr>
               <th style="text-align:right" valign="top">Employee</th>
               <td id="myEmployeeshoworient">
                       <s:select theme="simple" cssClass="validateRequired" name="employeeOrient" id="employeeOrient" list="empList" theme="simple" listKey="employeeId"  
                       listValue="employeeCode" required="true" multiple="true" size="4" value="empvalue"/>
                 </td>
           </tr>
	</table>                        
	
	<div align="center">
		<input type="button" value="Save" class="btn btn-primary" name="ok" onclick="setEmployeeAtOrientation('<%=hideID%>','<%=lblID%>','<%=from%>');" />
	</div>
<%-- </s:form> --%>
	
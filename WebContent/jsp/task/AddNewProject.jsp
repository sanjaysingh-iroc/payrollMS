<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*"%>
 <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Add New Project" name="title" />
</jsp:include> 
<script>

// 	Multiple file upload

var cnt=0;
function addExtraDoc() {
    cnt++;
    var divTag = document.createElement("div");
    divTag.id = "p_type"+cnt;
    divTag.setAttribute("style","float:left;");
        divTag.innerHTML = "<table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" name=\"doc_name1\" style=\"width:150px\"/></td><td><input type=\"file\" name=\"document1\" /></td>"+
        "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
        +"<a href=\"javascript:void(0)\"  onclick=\"removeExtraDoc(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a></td></tr></table>";
         
    document.getElementById("tblDiv").appendChild(divTag);
}


function removeExtraDoc(removeId) {
    var remove_elem = "p_type"+removeId;
    var row_skill = document.getElementById(remove_elem);
    document.getElementById("tblDiv").removeChild(row_skill);
    
}

$(document).ready(function() {		
		
	var docSrNo='<%= (String)request.getAttribute("docSrNo")%>'; 
    var docName='<%= (String)request.getAttribute("docName")%>';
    var docTitle='<%= (String)request.getAttribute("docTitle")%>';
	var docSrNoarray=docSrNo.split(",");       
	var docNamearray=docName.split(",");
	var docTitlearray=docTitle.split(",");

    if(docSrNo=='' || docSrNo=='null' ){ 
        var divTag = document.createElement("div");                
            divTag.id = "p_type"+cnt;
            divTag.innerHTML ="<table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" name=\"doc_name1\" style=\"width:150px\"/></td><td><input type=\"file\" name=\"document1\" /></td>"+
            "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
            +"</td></tr></table>";
            
            document.getElementById("tblDiv").appendChild(divTag);    

        }else
        for(i=0;i<docSrNoarray.length;i++ ){        
            var divTag = document.createElement("div");
          
            divTag.id = "p_type"+cnt;
            if(i==0){
            divTag.innerHTML ="<input type=\"hidden\" name=\"docid\" value=\""+docSrNoarray[i]+"\"><table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" name=\"update_doc_title\" value=\""+docTitlearray[i]+"\" style=\"width:150px\"/></td><td><a href=\""+docNamearray[i]+"\" target=\"blank\">"+docTitlearray[i]+"</a></td>"+
                    "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
                    /* +"<a href=\"javascript:void(0)\"  onclick=\"removeExtraDoc(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a></td></tr></table>" */;
          
            }else{
            divTag.innerHTML ="<input type=\"hidden\" name=\"docid\" value=\""+docSrNoarray[i]+"\"><table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" name=\"update_doc_title\" value=\""+docTitlearray[i]+"\" style=\"width:150px\"/></td><td><a href=\""+docNamearray[i]+"\" target=\"blank\">"+docTitlearray[i]+"</a></td>"+
                    "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
                    /* +"<a href=\"javascript:void(0)\"  onclick=\"removeExtraDoc(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a></td></tr></table>" */;
                     cnt++;               
            }                
            document.getElementById("tblDiv").appendChild(divTag);    
            cnt++;
        } 
});


// Dynamic select option


	var xmlhttp = "";
	var xhttp = "";
	var xmlhttptl = "";
	var xhttptl = "";
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
	
	function GetXmlHttpObjectforTeamLead() {
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
	function getTeamLeadList(opt) {
		
		  var frm = document.frmProject;  
		     var opt = frm.skill;  
		
		 var numofoptions = opt.length;
		     var selValue = new Array;  
		   
		     var j = 0;
		     for (i=0; i<numofoptions; i++)  
		     {  
		       if (opt[i].selected === true)  
		       {  
		         selValue[j] = opt[i].value;  
		         j++;  
		       }  
		     }  
		   
		    selValue = selValue.join(":");  
		
		
		xmlhttptl = GetXmlHttpObjectforTeamLead();
		if (xmlhttptl == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {       
			var url = "GetTeamLeadListAjax.action";
			url += "?skills=" + selValue;

			xmlhttptl.onreadystatechange = stateChanged_getTeamleadtypeList;
			xmlhttptl.open("GET", url, true);
			xmlhttptl.send(null);
		}
		getEmployee(opt);

	}
	function stateChanged_getTeamleadtypeList() {
		if (xmlhttptl.readyState == 4) {

			var res1 = xmlhttptl.responseText;
			//alert(res);

			document.getElementById("teamLeadListID").innerHTML = res1;
		}
		
	}
//===========================Team lead upto Here===================
	function getEmployee(opt) {
		
		  var frm = document.frmProject;  
		     var opt = frm.skill;  
		
		 var numofoptions = opt.length;
		     var selValue = new Array;  
		   
		     var j = 0;
		     for (i=0; i<numofoptions; i++)  
		     {  
		       if (opt[i].selected === true)  
		       {  
		         selValue[j] = opt[i].value;  
		         j++;  
		       }  
		     }  
		   
		    selValue = selValue.join(":");  
		
		
		xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var url = "GetEmployeeListAjax.action";
			url += "?skill=" + selValue;

			xmlhttp.onreadystatechange = stateChanged_getEmployeetypeList;
			xmlhttp.open("GET", url, true);
			xmlhttp.send(null);
		}

	}
	function stateChanged_getEmployeetypeList() {
		if (xmlhttp.readyState == 4) {
			var res = xmlhttp.responseText;
			document.getElementById("employeeListID").innerHTML = res;

		}
	}
	
	
//===========================Team lead upto Here===================	
	function getClientPoc(id) { 
		
		xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var url = "GetClientPocAjax.action";
			url += "?clientId=" + id;

			xmlhttp.onreadystatechange = stateChanged_getClientPoc;
			xmlhttp.open("GET", url, true);
			xmlhttp.send(null);
		}

	}
	
	function stateChanged_getClientPoc() {
		if (xmlhttp.readyState == 4) {

			var res = xmlhttp.responseText;
			//alert(res);

			document.getElementById("clientPoc").innerHTML = res;
			//                 document.getElementById("employeeList").innerHTML=res;

		}
	}
	
	function getSkillList(id) { 
		
		xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
			var url = "GetSkillListAjax.action";
			url += "?serviceId=" + id;

			xmlhttp.onreadystatechange = stateChanged_getSkillList;
			xmlhttp.open("GET", url, true);
			xmlhttp.send(null);
		}

	}
	
	function stateChanged_getSkillList() {
		if (xmlhttp.readyState == 4) {

			var res = xmlhttp.responseText;
			//alert(res);

			document.getElementById("skillListID").innerHTML = res;
			//                 document.getElementById("employeeList").innerHTML=res;

		}
	}
	
	
	$(function() {
		$("#deadline1").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		$("#startDate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		$("#monthlyStartDate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		
		
	});

	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		jQuery("#formID").validationEngine();
	});

	addLoadEvent(prepareInputsForHints);
	
	
	
	function checkType(id) {
		
		if(document.getElementById(id).checked==true ){
		
			document.getElementById("monthly").style.display='table-row';
			document.getElementById("monthlyDate").style.display='table-row';
			document.getElementById("notMonthlyStartDate").style.display='none';
			
			
		}else{
			document.getElementById("monthly").style.display='none';
			document.getElementById("monthlyDate").style.display='none';
			document.getElementById("notMonthlyStartDate").style.display='table-row';
		}

	}
</script>

<div class="leftbox reportWidth">
	<!-- <br />
	<div class="pagetitle" align="center" style="margin: 0px;">Add
		New Project</div> -->

	<s:form id="formID" cssClass="formcss" action="AddNewProject" name="frmProject"
		method="post" enctype="multipart/form-data" theme="simple">
		<div style="float: left" id="tblDiv">
			<table class="formcss" >
				<s:hidden name="operation"></s:hidden>
				<s:hidden name="pro_id"></s:hidden>
				<tr>
					<td class="txtlabel alignRight">Project Name<sup>*</sup></td>
					<td><s:textfield id="prjectname" cssClass="validateRequired"
							name="prjectname" label="Project Name" required="true" />
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Project Code<sup>*</sup></td>
					<td><s:textfield id="prjectCode" cssClass="validateRequired"
							name="prjectCode" required="true" />
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Select Client<sup>*</sup>
					</td>
					<td><s:select label="Select Client" name="client"
							listKey="clientId" cssClass="validateRequired" headerKey=""
							headerValue="Select Client" listValue="clientName"
							list="clientList" key="" required="true" onchange="getClientPoc(this.value)"/>
					</td>
				</tr>
				<tr>
				<td class="txtlabel alignRight">Select SPOC<sup>*</sup>
				<td id="clientPoc">
						<s:select theme="simple" name="clientPoc" listKey="clientPocId"  
			           listValue="clientPocName" headerKey="" headerValue="Select SPOC" 
			           cssClass="validateRequired"
			           list="clientPocList" key="" required="true" />
					</td>
				</tr>
				
				<%-- <tr>
				<td class="txtlabel alignRight">Is Monthly Project</td>
				<td><s:checkbox name="isMonthly" onclick="checkType(this.id);" label="Is Monthly" /></td>
				</tr> --%>
					
					<tr id="monthly" style="display: none;">
					<td class="txtlabel alignRight"  valign="top" >Enter Months<sup>*</sup></td>
					<td><s:textfield id="months" cssClass="validateRequired"
							name="months" required="true" />
					</td>
				</tr>
				<tr id="monthlyDate" style="display: none;">
					<td class="txtlabel alignRight"  valign="top" >Start Date of (Monthly) Project<sup>*</sup></td>
					<td><s:textfield id="monthlyStartDate" cssClass="validateRequired"
							name="startDate" required="true" />
					</td>
				</tr>
				
					 
				<tr>
					<td class="txtlabel alignRight" valign="top">Description<sup>*</sup></td>
					<td colspan="2"><s:textarea name="description" cols="50"
							rows="05" label="Description" cssClass="validateRequired"
							required="true" />
					</td>
				</tr>
				
				<%-- <tr>
					<td class="txtlabel alignRight" valign="top">Select Days to work<sup>*</sup>
					</td>
					<td><s:select label="Select Priority" name="days"  cssClass="validateRequired"
							listKey="dayId" headerKey="" headerValue="Select Days"
							listValue="dayName" list="daysList" key="" multiple="true"  size="4"/>
					</td>
				</tr> --%>
				
				<tr>
					<td class="txtlabel alignRight">Select Priority<sup>*</sup>
					</td>
					<td><s:select label="Select Priority" name="priority"  cssClass="validateRequired"
							listKey="priId" headerKey="" headerValue="Select Priority"
							listValue="proName" list="priorityList" key="" />
					</td>
				</tr>

				<tr id="notMonthlyStartDate">
					<td class="txtlabel alignRight">Start Date<sup>*</sup></td>
					<td><s:textfield id="startDate" name="startDate"
							 cssClass="validateRequired" required="true"></s:textfield>
					
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Deadline<sup>*</sup></td>
					<td><s:textfield id="deadline1" name="deadline"
							label="Deadline" cssClass="validateRequired" required="true"></s:textfield><span
						class="hint">End Date.<span class="hint-pointer">&nbsp;</span>
					</span>
					</td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Select Service<sup>*</sup>
					</td>
					<td><s:select label="Select Service" name="service"
							listKey="serviceId" cssClass="validateRequired" headerKey=""
							headerValue="Select Service" listValue="serviceName"
							list="serviceList" key="" required="true"
							onchange="getSkillList(this.value)" />
					</td>
				</tr>
				<tr>
				<td class="txtlabel alignRight" valign="top">Select Skill<sup>*</sup>
				<td id="skillListID">
				<s:select theme="simple" label="Select Skill" name="skill" listKey="skillsName" 
			           listValue="skillsName" headerKey="" headerValue="Select Skill"
			           multiple="true"  size="6" cssClass="validateRequired"
			           list="skillList" key="" required="true" onclick="getTeamLeadList(this);"/>
					</td>
					
					</tr>
					<tr>
					
					<td class="txtlabel alignRight"  valign="top">Select Team Leader<sup>*</sup></td>
					<td  id="teamLeadListID"><s:select theme="simple" 
						label="Select Employees" name="teamleadId" listKey="employeeId"  size="6" 
			           	listValue="employeeName" headerKey="" headerValue="Select Team Leader" 
			           	multiple="true" list="teamleadNamesList" key="" required="true" />
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight"  valign="top">Select Team <sup>*</sup></td>
					<td  id="employeeListID"><s:select theme="simple" label="Select Employees"
							name="empId" listKey="employeeId" size="6"
							listValue="employeeName" headerKey=""
							headerValue="Select Employee" multiple="true" list="empNamesList"
							key="" required="true" />
				</td>
					
					
				</tr>
				
				<%-- <tr>
					<td class="txtlabel alignRight">Attachment</td>
					<td><s:textfield name="doc_name1"></s:textfield>
					</td>
					<td><s:file label="Upload Document" name="document1" />
					</td>
					<td><a href="javascript:void(0)"  onclick="addExtraDoc()" class="add" title="Add">Add</a></td>
				</tr> --%>
				<%-- <tr>
					<td class="txtlabel alignRight"></td>
					<td><s:submit value="Submit" cssClass="input_button"
							name="submit"></s:submit>
					</td>
				</tr> --%>
			</table>
		</div>
		<div class="clr"></div>
		<div style="margin:0px 0px 0px 210px">
		<table class="formcss" >
		<tr><td class="txtlabel alignLeft"><s:submit value="Submit" cssClass="input_button"
							name="submit" onclick="return showd();"></s:submit></td><td>  </td></tr>				
		 </table>
		 </div>   
		
		
	</s:form>

</div>


<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="com.konnect.jpms.util.*"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script src="<%= request.getContextPath()%>/scripts/color.js" type="text/javascript"></script>
<script type="text/JavaScript">
	var cp = new ColorPicker('window'); 
	var cp2 = new ColorPicker('window');
	
	
</script>

 
<script type="text/javascript">

	
	 function compStartDate()
     {
		 var pro_deadline='<%=(String) request.getAttribute("pro_deadline")%>'; 
		 var pro_startDate='<%=(String) request.getAttribute("pro_startDate")%>'; 
         var startDate= document.getElementById('startDate').value;
             
            /*  if(startDate < pro_startDate )
             {
             document.getElementById("err_strt_date").innerHTML="Please select start date greater than  start date of project.";
             document.getElementById('startDate').focus();
             return false;
             }else if(startDate > pro_deadline)
             {
                 document.getElementById("err_strt_date").innerHTML="Please select start date less than  deadline of project.";
                 document.getElementById('startDate').focus();
                 return false;
                 }
             else
             {

             document.getElementById("err_strt_date").innerHTML="";
             } */
     return false;
     }
	 function compEndDate()
     {
		 var pro_deadline='<%=(String) request.getAttribute("pro_deadline")%>'; 
		 var pro_startDate='<%=(String) request.getAttribute("pro_startDate")%>'; 
             var deadline= document.getElementById('deadline2').value;
             var startDate= document.getElementById('startDate').value;
            /*  if(deadline < pro_startDate )
             {
             document.getElementById("err_end_date").innerHTML="Please select deadline of task greater than  start date of project.";
             document.getElementById('deadline2').focus();
             return false;
             }else if(deadline > pro_deadline)
             {
                 document.getElementById("err_end_date").innerHTML="Please select deadline of task less than  deadline of project.";
                 document.getElementById('deadline2').focus();
                 return false;
                 }else if(deadline > startDate)
             {
                 document.getElementById("err_end_date").innerHTML="Please select deadline of task greater than  start date of task.";
                 document.getElementById('deadline2').focus();
                 return false;
                 }
             else
             {

             document.getElementById("err_end_date").innerHTML="";
             } */
     return false;
     }
	<%-- 
var cnt=0;
function addExtraDoc() {
    cnt++;
    var divTag = document.createElement("div");
    divTag.id = "p_type"+cnt;
    divTag.setAttribute("style","float:left;");
        divTag.innerHTML = "<table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" style=\"width: 150px;\" name=\"doc_name1\"/></td><td><input type=\"file\" name=\"document1\" /></td>"+
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
	
	var docSrNo='<%=(String) request.getAttribute("docSrNo")%>'; 
    var docName='<%=(String) request.getAttribute("docName")%>';
    var docTitle='<%=(String) request.getAttribute("docTitle")%>';
						var docSrNoarray = docSrNo.split(",");
						var docNamearray = docName.split(",");
						var docTitlearray = docTitle.split(",");

						if (docSrNo == '' || docSrNo == 'null') {
							var divTag = document.createElement("div");
							divTag.id = "p_type" + cnt;
							divTag.innerHTML = "<table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" style=\"width: 150px;\" name=\"doc_name1\" style=\"width:150px\"/></td><td><input type=\"file\" name=\"document1\" /></td>"
									+ "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
									+ "</td></tr></table>";

							document.getElementById("tblDiv").appendChild(
									divTag);

						} else
							for (i = 0; i < docSrNoarray.length; i++) {
								var divTag = document.createElement("div");

								divTag.id = "p_type" + cnt;
								if (i == 0) {
									/* divTag.innerHTML = "<input type=\"hidden\" name=\"docid\" value=\""+docSrNoarray[i]+"\"><table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" style=\"width: 150px;\" name=\"update_doc_title\" value=\""+docTitlearray[i]+"\" style=\"width:150px\"/></td><td><a href=\""+docNamearray[i]+"\" target=\"blank\">"+docTitlearray[i]+"</a></td>"
											+ "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
											+ "</td></tr></table>"; */
									divTag.innerHTML ="<input type=\"hidden\" name=\"docid\" value=\""+docSrNoarray[i]+"\"><table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" name=\"update_doc_title\" value=\""+docTitlearray[i]+"\" style=\"width:150px\"/></td><td><a href=\""+docNamearray[i]+"\" target=\"blank\">"+docTitlearray[i]+"</a></td>"+
				                    "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
				                    /* +"<a href=\"javascript:void(0)\"  onclick=\"removeExtraDoc(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a></td></tr></table>" */;
				          

								} else {
									divTag.innerHTML ="<input type=\"hidden\" name=\"docid\" value=\""+docSrNoarray[i]+"\"><table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" name=\"update_doc_title\" value=\""+docTitlearray[i]+"\" style=\"width:150px\"/></td><td><a href=\""+docNamearray[i]+"\" target=\"blank\">"+docTitlearray[i]+"</a></td>"+
				                    "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
				                    /* +"<a href=\"javascript:void(0)\"  onclick=\"removeExtraDoc(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a></td></tr></table>" */;
				                     cnt++;     
											
											/* divTag.innerHTML = "<input type=\"hidden\" name=\"docid\" value=\""+docSrNoarray[i]+"\"><table class=\"formcss\" ><tr><td class=\"txtlabel alignRight\">Attachment</td><td><input type=\"text\" style=\"width: 150px;\" name=\"update_doc_title\" value=\""+docTitlearray[i]+"\" style=\"width:150px\"/></td><td><a href=\""+docNamearray[i]+"\" target=\"blank\">"+docTitlearray[i]+"</a></td>"
											+ "<td><a href=\"javascript:void(0)\"  onclick=\"addExtraDoc()\" class=\"add\" title=\"Add\">Add</a>"
											+ "<a href=\"javascript:void(0)\"  onclick=\"removeExtraDoc(this.id)\" id=\""
											+ cnt+ "\" class=\"remove\">Remove</a></td></tr></table>"; */
											
									
								}
								document.getElementById("tblDiv").appendChild(
										divTag);
								cnt++;
							}
					});
 --%>
	$(function() {
		$("#deadline2").datepicker({
			dateFormat : 'dd/mm/yy'
		});

		$("#startDate").datepicker({
			dateFormat : 'dd/mm/yy'
		});

	});

	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		jQuery("#formID").validationEngine();
	});

	addLoadEvent(prepareInputsForHints);
</script>

<div class="leftbox reportWidth" id="div_id_docs">

	<!-- <br>
	<div class="pagetitle" align="center" style="margin: 0px;">Add
		New Task</div> -->
	<s:form id="formID" action="AddNewMilestone" method="post" name="frm"
		cssClass="formcss" enctype="multipart/form-data" theme="simple">

		<s:hidden name="pro_id" />
		<s:hidden name="task_id" />
		<s:hidden name="operation" value="I" />
		<div style="float: left" id="tblDiv">
			<table class="formcss">
				<tr>
					<td class="txtlabel alignRight">Milestone Name<sup>*</sup></td>
					<td><s:textfield id="milestone_name"
							cssClass="validateRequired" name="milestone_name"
							label="Milestone Name" required="true" /></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight" valign="top">Description<sup>*</sup></td>
					<td colspan="2"><s:textarea name="comment" cols="50" rows="05"
							label="Description" cssClass="validateRequired" required="true" />
					</td>

				</tr>

				<tr>
					<td class="txtlabel alignRight">Start Date<sup>*</sup></td>
					<td><s:textfield cssClass="validateRequired" id="startDate"
							name="startDate" required="true" onchange="compStartDate();"></s:textfield></td><td><div style="color:red;" id="err_strt_date"></div></td>
				</tr>

				 <tr>
					<td class="txtlabel alignRight">Deadline<sup>*</sup></td>
					<td><s:textfield cssClass="validateRequired"
							label="Deadline" id="deadline2" name="deadline" required="true" onchange="compEndDate();"></s:textfield></td><td><div style="color:red;" id="err_end_date"></div></td>
				</tr> 
				
			</table>
		</div>

		<div class="clr"></div>
		<div style="margin: 0px 0px 0px 210px">
			<table class="formcss">
				<tr>
					<td class="txtlabel alignLeft"><s:submit value="Submit"
							cssClass="input_button" name="submit"></s:submit>
					</td>
					<td></td>
				</tr>
			</table>
		</div>
	</s:form>

</div>



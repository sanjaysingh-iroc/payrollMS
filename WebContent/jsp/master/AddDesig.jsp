<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script>

$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	$("#education").multiselect({
		noneSelectedText: 'Select Education(required)'
	}).multiselectfilter();
	$("#skill").multiselect({
		noneSelectedText: 'Select Skills(required)'
	}).multiselectfilter();
});	

function validateForm(){
	if(document.formAddNewRow.desigCode==''){
		alert ("Desig code is required, please enter the code");
		return false;
	}else{
		return true;
	}
}
 function viewAttributeData(id,check){
	 
	 if(check==true){
		 document.getElementById("attributeid"+id).style.display="table-row";
		 if(id === '1'){
			 $("#education").prop('required',true);
		 }
		 if(id === '5'){
			 $("#skill").prop('required',true);
		 }
		 
	 }else{
		 document.getElementById("attributeid"+id).style.display="none";
		 if(id === '1'){
			 $("#education").prop('required',false);
		 }
		 if(id === '5'){
			 $("#skill").prop('required',false);
		 }
	 }
	 
 }

 
 /* function addNewKRA(kraCnt, count) {
	 kraCnt++;
		
		var a = "<tr id=\"kra_TR"+kraCnt+"\" > <td> <input type=\"text\" name=\"designKRA\" />" + 
						"<a href=\"javascript:void(0)\"  onclick=\"removeKRA(this.id)\" id=\""+kraCnt+"\" class=\"remove\" >&nbsp;</a>"+
						"<a href=\"javascript:void(0)\" style=\"float: right\" onclick=\"addNewKRA('"+kraCnt+"')\" class=\"add\">&nbsp;</a></td></tr>";
						alert(a);
		document.getElementById("tbl_all_kras").append(a);
	} */
 
 
 
 function addNewKRADesig() {
	var kraCnt = document.getElementById("kracount").value;
		var val=(parseInt(kraCnt)+1);
	    //alert(val);
	    var table = document.getElementById("tbl_all_kras_modal");
	    var rowCount = table.rows.length;
	    //alert("rowCount  " + rowCount);
	    //var rowid=(parseInt(rowCount)+1);
	    var row = table.insertRow(rowCount);
	    row.id="kra_TR"+val;
	    var cell1 = row.insertCell(0);
	    cell1.setAttribute('style', 'border-bottom: 1px solid #ECECEC' );
	    
	    cell1.innerHTML = "<table class=\"table\"><tr><td>"+"<%=(String)request.getAttribute("elementSelectBox") %>"+
	    "</span></td><td>"+
	    "<span id=\"attributeDiv"+val+"\"><select name=\"elementAttribute\" style=\"width: 130px !important;\"><option value=\"\">Select Attribute</option></select></span></td></tr>"+
	    "<tr><td colspan=2><p>KRA: <input type=\"text\" name=\"designKRA\"/></p>"+
	    "<p>Task: <input type=\"text\" name=\"designKRATask\"/></p>" + 
		"<a href=\"javascript:void(0)\" style=\"float: right;\" onclick=\"removeKRADesig(kra_TR"+val+")\" class=\"remove-font\" title=\"Remove KRA\"></a>"+
		"<a href=\"javascript:void(0)\" style=\"float: right;\" onclick=\"addNewKRADesig()\" class=\"add-font\" title=\"Add New KRA\"></a></td></tr>";
	    document.getElementById("kracount").value = val;
	    
	    document.getElementById("goalElements").id="goalElements"+val;
		document.getElementById("goalElements"+val).setAttribute("onchange", "getAttributes(this.value,document.formAddNewRow.orgId.value, '"+val+"');");
	}
 
	/* <select name=\"goalElements\" id=\"goalElements\" style=\"width: 130px;\" onchange=\"getAttributes(this.value,document.formAddNewRow.orgId.value, '"+val+"');\">"+
    "<option value=\"\">Select Element</option>"+elementOpt+"</select> */
    
	<%-- "+<%=(String)request.getAttribute("elementOptions") %>+" --%>
	
 function removeKRADesig(trIndex) {
	 //alert(trIndex);
	 return trIndex.parentNode.removeChild(trIndex);
	    //document.getElementById("tbl_all_kras_modal").deleteRow(trIndex);
	}
 
 
 function getAttributes(value, orgId, count) {
		var action = 'GetElementwiseAttributeList.action?elementID=' + value + '&orgId=' + orgId + '&count=' + count;
		getContent('attributeDiv'+count, action);
		//alert("value ===> " +value +" orgId ===>" +orgId + " count ===> " + count);
		jQuery("#formAddNewRow").validationEngine();
		document.getElementById("elementAttribute"+count).setAttribute("class", "validateRequired");
	}
 
 	function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
 
 	function isOnlyNumberKey(evt) {
  	   var charCode = (evt.which) ? evt.which : event.keyCode;
  	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
  	      return true; 
  	   }
  	   return false;
  	}
 
</script>
<%
UtilityFunctions uF=new UtilityFunctions();
List<List<String>> outerList=(List<List<String>>)request.getAttribute("attributeList"); 
Map<String,String> hmDesigAttribute=(Map<String,String>)request.getAttribute("hmDesigAttribute");
if(hmDesigAttribute==null) hmDesigAttribute=new HashMap<String,String>();
String desigId = (String)request.getAttribute("desigId"); 
String noOfGrades = (String)request.getAttribute("noOfGrades");
%>

	<s:form theme="simple" id="formAddNewRow" name="formAddNewRow" action="AddDesig" method="POST" cssClass="formcss">
	<input type="hidden" name="desiglevel" value="<%=request.getParameter("param") %>" />
	<s:hidden name="desigId" />
	<s:hidden name="orgId" />
	<s:hidden name="fromPage" />
	<s:hidden name="empId" />
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
 
	<div style="float:left;">
	<!-- <table class="formcss" style="width: 675px"> -->
		<table class="table table_no_border" style="border-right: 1px solid #ECECEC;">
			
			<tr>
				<th class="txtlabel alignRight" nowrap="nowrap">Designation Code:<sup>*</sup></th>
				<td>
					<s:textfield name="desigCode" id="desigCode"  cssClass="validateRequired" /> 
					<span class="hint">Designation Code<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
		
			<tr>
				<th class="txtlabel alignRight" nowrap="nowrap">Designation Name:<sup>*</sup></th>
				<td>
					<s:textfield name="desigName" id="desigName"  cssClass="validateRequired" /> 
					<span class="hint">Designation Name<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><h4>Job Description</h4><hr style="border:solid 1px #ECECEC"/></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight" nowrap="nowrap" valign="top">Designation Description:</th>
				<td>
					<textarea name="desigDesc" id="editor1" rows="10"  cols="5" style="width:350px !important; height:100px;">
					<%=request.getAttribute("desc")!=null ? (String)request.getAttribute("desc"): "" %>
					</textarea> 
					<span class="hint">Designation Description<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			<tr>
					<th class="txtlabel alignRight" valign="top">Ideal Candidate:</th>
					<td>
						<textarea name="idealcandidate" id="editor2" rows="10"  cols="5" style="width:350px !important; height:100px;">
							<%=request.getAttribute("ideal_candidate")!=null ? (String)request.getAttribute("ideal_candidate") : ""%>
						</textarea> 
					</td>
				</tr> 
			<tr>
					<th class="txtlabel alignRight" valign="top">Profile:</th>
					<td>
						<textarea name="profile" id="editor3" rows="10" cols="5" style="width:350px !important; height:100px;">
						<%=request.getAttribute("profile_desc")!=null ? (String)request.getAttribute("profile_desc") : ""  %>
						</textarea> 
					</td>
				</tr>
			<%if(uF.parseToInt((String)request.getAttribute("salaryStructure")) == IConstants.S_GRADE_WISE){ %>
				<tr>
					<td class="txtlabel alignRight">No. of grades to be added:<sup>*</sup></td>
					<td>
						<% if(uF.parseToInt(desigId) > 0 && uF.parseToInt(noOfGrades) > 0) { %>
							<s:textfield name="noOfGrades" id="noOfGrades" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)" disabled="true" cssStyle="text-align: right; width: 32px !important;"/>
						<%} else { %>
							<s:textfield name="noOfGrades" id="noOfGrades" cssClass="validateRequired" onkeypress="return isOnlyNumberKey(event)" cssStyle="text-align: right; width: 32px !important;"/>
						<%} %> 
					</td>  
				</tr>
			<%} %>	
			
			<tr>
				<td></td>
				<td>
				<% if(uF.parseToInt(desigId) > 0) { %>
					<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewRowOk"/>
				<% } else { %>
					<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
				<% } %>	
				</td>
			</tr>
	
	
		</table>
	</div>
	
	
	<div style="float:left; width: 33%;">
	<table class="formcss" style="margin-left: 10px;">
	<tr><td colspan="2"><b>Job Requirement Attributes </b></td></tr>
	<%
		String attribute_ids = (String)request.getAttribute("attribute_ids");
		for(int i=0;outerList!=null && i<outerList.size();i++){
			List<String> innerList=(List<String>)outerList.get(i);
			
			if(i%2==0){
		%> 
			<tr>
		<% } %>	
		
			<td class="alignRight" nowrap="nowrap">
				<input type="checkbox" name="attributeid" value="<%=innerList.get(0)%>"
				onclick="viewAttributeData('<%=innerList.get(0)%>',this.checked);" 
				<%if(attribute_ids!=null && attribute_ids.contains(innerList.get(0))) { %>checked="checked"<% } %>/>
			</td>
			<td><%=innerList.get(1)%></td>			
	
			<%	if(i%2==0) { %>
				</tr>
			<% } %>
				<%
				String showtr="none";
				if(attribute_ids!=null && attribute_ids.contains(innerList.get(0))) { 
					showtr="table-row";	
				} 
				%>
				<tr id="attributeid<%=innerList.get(0)%>" style="display:<%=showtr %>;">
					<td>&nbsp;</td>
					<td><div>
					<%if(innerList.get(0).equals("1")) { %>
						<s:select name="education" id="education" theme="simple" listKey="eduId" listValue="eduName" list="educationalList" 
								size="4" multiple="true" value="educationID" />
								
					<%}else if(innerList.get(0).equals("2")) {
						String tyear="0";
						String tmonth="0";
						
						if(hmDesigAttribute.get(innerList.get(0))!=null && !hmDesigAttribute.get(innerList.get(0)).equals("")) {
							String[] temp = hmDesigAttribute.get(innerList.get(0)).trim().split("_");
							tyear=temp[0];
							tmonth=temp[1];
						}
						%>
						Year&nbsp;&nbsp;<select name="totalexpYear" id="totalexpYear" style="width: 50px !important;">
						<%for (int ii = 0; ii <= 30; ii++) {							
						%>										
						<option value="<%=ii%>" <%if(uF.parseToInt(tyear)==ii) { %>selected="selected" <% } %>><%=ii%></option> 
						<% } %>
						</select>&nbsp;&nbsp;Month &nbsp;&nbsp;<select name="totalexpMonth" id="totalexpMonth" style="width: 50px !important;">
						<%
							for (int iii = 0; iii < 12; iii++) {										
						%>
						<option value="<%=iii%>" <%if(uF.parseToInt(tmonth)==iii) { %>selected="selected" <% } %>><%=iii%></option>
						<% } %>
					</select>
					<% } else if(innerList.get(0).equals("3")) {
						String ryear="0";
						String rmonth="0";
						
						if(hmDesigAttribute.get(innerList.get(0))!=null && !hmDesigAttribute.get(innerList.get(0)).equals("")) {
							String[] temp = hmDesigAttribute.get(innerList.get(0)).trim().split("_");
							ryear=temp[0];
							rmonth=temp[1];
						}
					%>
						Year&nbsp;&nbsp;<select name="relevantYear" id="relevantYear" style="width: 50px !important;">
						<%for (int ii = 0; ii <= 30; ii++) { %>										
						<option value="<%=ii%>" <%if(uF.parseToInt(ryear)==ii) { %>selected="selected" <% } %>><%=ii%></option>
						<% } %>
						</select>&nbsp;&nbsp;Month &nbsp;&nbsp;<select name="relevantMonth" id="relevantMonth" style="width: 50px !important;">
						<% for (int iii = 0; iii < 12; iii++) { %>
						<option value="<%=iii%>" <%if(uF.parseToInt(rmonth)==iii) { %>selected="selected" <% } %>><%=iii%></option>
						<% } %>
					</select>
				<% } else if(innerList.get(0).equals("4")) {
					String eyear="0";
					String emonth="0";
					
					if(hmDesigAttribute.get(innerList.get(0))!=null && !hmDesigAttribute.get(innerList.get(0)).equals("")) {
						String[] temp = hmDesigAttribute.get(innerList.get(0)).trim().split("_");
						eyear=temp[0];
						emonth=temp[1];
					}
					%>
						Year&nbsp;&nbsp;<select name="expusYear" id="expusYear" style="width: 50px !important;">
						<% for (int ii = 0; ii <= 30; ii++) { %>										
						<option value="<%=ii%>" <%if(uF.parseToInt(eyear)==ii) { %>selected="selected" <% } %>><%=ii%></option>
						<% } %>
						</select>&nbsp;&nbsp;Month &nbsp;&nbsp;<select name="expusMonth" id="expusMonth" style="width: 50px !important;">
						<% for (int iii = 0; iii < 12; iii++) { %>
						<option value="<%=iii%>" <%if(uF.parseToInt(emonth)==iii){ %>selected="selected" <% } %>><%=iii%></option>
						<% } %>
					</select>						
				<% } else if(innerList.get(0).equals("5")) { %>
				
					<s:select name="skill" id="skill" headerKey=""  theme="simple" listKey="skillsId" 
						listValue="skillsName" list="skillsList" size="4" multiple="true" value="skillsID" />
					
				<% } else if(innerList.get(0).equals("6")) { %>
					<s:select name="strGender" listKey="genderId" list="genderList" cssStyle="width:150px !important;"
					 listValue="genderName" headerKey="0"  headerValue="Any"/>
				
				<% } else if(innerList.get(0).equals("7")) { %>
					<s:select name="strMinAge" list="#{'18':'18', '19':'19', '20':'20', '21':'21', '22':'22', '23':'23', '24':'24', '25':'25', '26':'26', '27':'27', '28':'28', '29':'29', '30':'30', '31':'31', '32':'32', '33':'33', '34':'34', '35':'35', '36':'36', '37':'37', '38':'38',
					'39':'39', '40':'40', '41':'41', '42':'42', '43':'43', '44':'44', '45':'45', '46':'46', '47':'47', '48':'48', '49':'49', '50':'50', '51':'51', '52':'52', '53':'53', '54':'54', '55':'55', '56':'56', '57':'57', '58':'58', '59':'59', '60':'60'}" 
					cssStyle="width:150px !important;" headerKey="0" headerValue="Select Age"/>
				<% } else if(innerList.get(0).equals("8")) { %>
					<div style="margin: 0px 0px 7px;">
						Min CTC: &nbsp;<s:textfield name="strMinCTC" cssStyle="width:120px !important;" onkeypress="return isNumberKey(event)"/>
					</div>
					<div style="margin: 3px 0px 0px;">
						Max CTC: <s:textfield name="strMaxCTC" cssStyle="width:120px !important;" onkeypress="return isNumberKey(event)"/>
					</div>
				<% } %>
					</div>
					</td>
			</tr>
			
			<%}%>
		
	</table>
	

	<% 
		List<List<String>> desigKraDetails = (List<List<String>>) request.getAttribute("desigKraDetails");
	%> 
	<table id="tbl_all_kras_modal" class="table" style="border-top: 2px solid #ECECEC; margin-left: 10px;">
	
	<tr><td><b>Key Responsibility Areas</b></td></tr>
	<% if(desigKraDetails != null && desigKraDetails.size() > 0) { %> 
	<tr><td><input type="hidden" name="kracount" id="kracount" value="<%=desigKraDetails.size()-1 %>"/></td></tr>
		<% 
		for(int i=0; i<desigKraDetails.size(); i++) {
			List<String> innerList = desigKraDetails.get(i);
		%>
		<tr id="kra_TR<%=i %>"><td style="border-bottom: 1px solid #ECECEC;">
			<span style="float: left; margin-right: 10px;">
				<select name="goalElements" id="goalElements<%=i %>" style="width: 130px !important;" onchange="getAttributes(this.value,document.formAddNewRow.orgId.value, '<%=i %>');">
				<option value="">Select Element (Performance/ Potential)</option><%=innerList.get(2) %></select>
			</span>
			<span id="attributeDiv<%=i %>" style="float: left;"><select name="elementAttribute" id="elementAttribute<%=i %>" class="validateRequired" style="width: 130px !important;">
			<option value="">Select Attribute</option><%=innerList.get(3) %></select>
			</span>
	
			<input type="hidden" name="desigKraId" id="desigKraId" value="<%=innerList.get(0) %>"/>
			<span style="float: left;">KRA: <input type="text" name="designKRA" style="margin: 5px 0px;" value="<%=uF.showData(innerList.get(1), "") %>"/></span>
			<span style="float: left;">Task: <input type="text" name="designKRATask" style="margin: 5px 0px;" value="<%=uF.showData(innerList.get(4), "") %>"/></span>
			<%if(i > 2) { %>
				<a href="javascript:void(0)" style="margin: 0px;" onclick="removeKRADesig(this.parentNode.parentNode.rowIndex)" class="remove-font" ></a>
			<% } %>
			<%if(i > 1 || i == desigKraDetails.size()-1) { %>
				<a href="javascript:void(0)" style="float: right; margin: 0px;" onclick="addNewKRADesig();" class="add-font"></a>
			<% } %>
		</td></tr>
		<% } %>	
	<% } else { %>
	<tr id="kra_TR0"><td style="border-bottom: 1px solid #ECECEC;">
	<span style="float: left; margin-right: 10px;">
		<select name="goalElements" id="goalElements0" style="width: 130px !important;" onchange="getAttributes(this.value,document.formAddNewRow.orgId.value, '0');">
		<option value="">Select Element</option>
		<%=request.getAttribute("elementOptions") %>
		</select>
	</span>
	<span id="attributeDiv0" style="float: left;"><select name="elementAttribute" id="elementAttribute0" style="width: 130px !important;"><option value="">Select Attribute</option></select></span>
	<input type="hidden" name="kracount" id="kracount" value="2"/>
	<span style="float: left;">KRA: <input type="text" name="designKRA" style="margin: 5px 0px;"/></span>
	<span style="float: left;">Task: <input type="text" name="designKRATask" style="margin: 5px 0px;"/></span>
	</td></tr>
	
	<tr id="kra_TR1"><td style="border-bottom: 1px solid #ECECEC;">
	<span style="float: left; margin-right: 10px;">
		<select name="goalElements" id="goalElements1" style="width: 130px !important;" onchange="getAttributes(this.value,document.formAddNewRow.orgId.value, '1');">
		<option value="">Select Element</option>
		<%=request.getAttribute("elementOptions") %>
		</select>
	</span>
	<span id="attributeDiv1" style="float: left;"><select name="elementAttribute" id="elementAttribute1" style="width: 130px !important;"><option value="">Select Attribute</option></select></span>
	<span style="float: left;">KRA: <input type="text" name="designKRA" style="margin: 5px 0px;"/></span>
	<span style="float: left;">Task: <input type="text" name="designKRATask" style="margin: 5px 0px;"/></span>
	</td> </tr>
	
	<tr id="kra_TR2"><td style="border-bottom: 1px solid #ECECEC;">
	<span style="float: left; margin-right: 10px;">
		<select name="goalElements" id="goalElements2" style="width: 130px !important;" onchange="getAttributes(this.value,document.formAddNewRow.orgId.value, '2');">
		<option value="">Select Element</option>
		<%=request.getAttribute("elementOptions") %>
		</select>
	</span>
	<span id="attributeDiv2" style="float: left;"><select name="elementAttribute" id="elementAttribute2" style="width: 130px !important;"><option value="">Select Attribute</option></select></span>
	<span style="float: left;">KRA: <input type="text" name="designKRA" style="margin: 5px 0px;"/></span>
	<span style="float: left;">Task: <input type="text" name="designKRATask" style="margin: 5px 0px;"/></span>
		<a href="javascript:void(0)" style="float: right; margin: 0px;" onclick="addNewKRADesig();" class="add-font"></a>
	</td> </tr>
	<% } %>
	</table>
	
	
	</div>
	
	<script>
			// Replace the <textarea id="editor1"> with an CKEditor instance.
			
			CKEDITOR.replace( 'editor1', {
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
			
			CKEDITOR.replace( 'editor2', {
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
			
			CKEDITOR.replace( 'editor3', {
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
			</script>
</s:form>

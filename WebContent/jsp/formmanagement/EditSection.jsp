<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	String anstype = (String) request.getAttribute("anstype");
	String formId = (String) request.getAttribute("formId");
	String totalWeightage = (String) request.getAttribute("totalWeightage");
	int nWeightage = uF.parseToInt(totalWeightage);
	
	String answerType = (String) request.getAttribute("answerType");
	String ansFlag = (String) request.getAttribute("ansFlag");
	
	Map<String, String> hmSection = (Map<String, String>) request.getAttribute("hmSection");
	if(hmSection == null) hmSection = new HashMap<String, String>();
%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(document).ready( function () {
	$("#frmEditSection_submit").click(function(){
		$(".validateRequired").prop('required',true);
		$(".validateNumber").prop('type','number');	 
	});
});
</script>
<script type="text/javascript">
$(document).ready( function () {
	
	showAnswerTypeDiv1('<%=request.getAttribute("answerType")%>');
});	


function showAnswerTypeDiv1(ansType) {
	var action = 'ShowAnswerType.action?ansType=' + ansType;
	getContent("editAnstypediv", action);
} 

function validateSECANDSUBSECScore1(value,weightageid,weightagehideid,totweightage) {
	var singleWeightage = document.getElementById(weightagehideid).value;
	var othertotweight = parseFloat(totweightage) - parseFloat(singleWeightage);
	var remainWeightage = 100 - parseFloat(othertotweight);
	if(parseFloat(value) > parseFloat(remainWeightage)){
		alert("Entered value greater than Weightage");
		document.getElementById(weightageid).value = remainWeightage;
	}else if(parseFloat(value) <= 0 ){
		alert("Invalid Weightage");
		document.getElementById(weightageid).value = remainWeightage;
	}
}

</script>


<div>

	<s:form name="frmEditSection" id="frmEditSection" theme="simple" action="EditSection" method="POST">
		<s:hidden name="formId" id="formId"></s:hidden>
		<s:hidden name="sectionId" id="sectionId"></s:hidden>
		<s:hidden name="operation" value="U"/>
		<s:hidden name="strOrg" id="strOrg"></s:hidden>
       	<s:hidden name="userscreen" id="userscreen"></s:hidden>
		<s:hidden name="navigationId" id="navigationId"></s:hidden>
		<s:hidden name="toPage" id="toPage"></s:hidden>
		<div>
			<table class="table table_no_border">
				<tr>
					<th class="txtlabel alignRight">Section Title:<sup>*</sup></th>
					<td colspan="5"><input type="text" name="sectionTitle" id="sectionTitle" class="validateRequired" style="width:39%" value="<%=uF.showData(hmSection.get("SECTION_NAME"),"") %>"/></td> 
				</tr>
				<tr>
					<th class="txtlabel alignRight" valign="top">Short Description:</th>
					<td colspan="5"><textarea rows="5" cols="50" name="shortDesrciption" id="editor3"><%=uF.showData(hmSection.get("SECTION_SHORT_DESCRIPTION"),"") %></textarea></td> 
				</tr>
				<tr>
					<th class="txtlabel alignRight" valign="top">Long Description:</th>
					<td colspan="5"><textarea rows="5" cols="50" name="longDesrciption" id="editor4"><%=uF.showData(hmSection.get("SECTION_LONG_DESCRIPTION"),"") %></textarea></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">Weightage %:<sup>*</sup></th>
					<td>
						<input type="text" name="sectionWeightage"	id="sectionWeightage1" class="validateNumber" value="<%=uF.showData(hmSection.get("SECTION_WEIGHTAGE"),"0") %>"onkeyup="validateSECANDSUBSECScore1(this.value,'sectionWeightage1','hidesectionWeightage1','<%=nWeightage%>');" onkeypress="return isNumberKey(event)" style="text-align: right; width: 40px;"/>
						<input type="hidden" name="hidesectionWeightage" id="hidesectionWeightage1" value="<%=uF.showData(hmSection.get("SECTION_WEIGHTAGE"),"0") %>" />
					</td>
				</tr>
				<tr>
					<th class="txtlabel alignRight" valign="top">Select Answer Type:<sup>*</sup></th>
					<td>
						<div>
							<%if(uF.parseToBoolean(ansFlag)){ %>
								<%=uF.showData(hmSection.get("SECTION_ANSWER_TYPE"),"") %>
								<input type="hidden" name="ansType" id="ansType" value="<%=uF.showData(hmSection.get("SECTION_ANSWER_TYPE_ID"),"0") %>" />
							<%} else { %>
							<select name="ansType" id="ansType" onchange="showAnswerTypeDiv1(this.value)" class="validateRequired">
								<%=anstype %>
							</select>
							<%} %>
						</div>
						<div>
							<div id="editAnstypediv" class="margintop20">
								<div id="anstype9">
									a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"/><br />
									c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"/><br />
								</div>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<th class="txtlabel alignRight">&nbsp;</th>
					<td><s:submit value="Update" cssClass="btn btn-primary" name="submit"></s:submit></td>
				</tr>
			</table>
		</div>
		
	</s:form>
</div>

<g:compress>
	<script>
		// Replace the <textarea id="editor1"> with an CKEditor instance.
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
		
		
		// Replace the <textarea id="editor2"> with an CKEditor instance.
		CKEDITOR.replace( 'editor4', {
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
</g:compress>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<script src="scripts/ckeditor_cust/ckeditor.js"></script>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<g:compress>
<script>

	CKEDITOR.on( 'instanceReady', function( ev ) {
		// Show the editor name and description in the browser status bar.
		if(document.getElementById( 'eMessage' )) {
			document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
		}
	
		// Show this sample buttons.
		if(document.getElementById( 'eButtons' )) {
		document.getElementById( 'eButtons' ).style.display = 'block';
		}
	}); 
	function InsertHTML() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		var value = document.getElementById( 'htmlArea' ).value;
	
		// Check the active editing mode.
		if ( editor.mode == 'wysiwyg' )
		{
			// Insert HTML code.
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertHtml
			editor.insertHtml( value );
		}
		else
			alert( 'You must be in WYSIWYG mode!' );
	}
	
	function InsertText() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		var value = document.getElementById( 'txtArea' ).value;
	
		// Check the active editing mode.
		if ( editor.mode == 'wysiwyg' )
		{
			// Insert as plain text.
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertText
			editor.insertText( value );
		}
		else
			alert( 'You must be in WYSIWYG mode!' );
	}
	
	function SetContents() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		var value = document.getElementById( 'htmlArea' ).value;
	
		// Set editor contents (replace current contents).
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-setData
		editor.setData( value );
	}
	
	function GetContents() {
		// Get the editor instance that you want to interact with.
		var editor = CKEDITOR.instances.editor1;
	
		// Get editor contents
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-getData
		alert( editor.getData() );
	}
	
	function ExecuteCommand( commandName ) {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
	
		// Check the active editing mode.
		if ( editor.mode == 'wysiwyg' )
		{
			// Execute the command.
			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-execCommand
			editor.execCommand( commandName );
		}
		else
			alert( 'You must be in WYSIWYG mode!' );
	}
	
	function CheckDirty() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		// Checks whether the current editor contents present changes when compared
		// to the contents loaded into the editor at startup
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-checkDirty
		alert( editor.checkDirty() );
	}
	
	function ResetDirty() {
		// Get the editor instance that we want to interact with.
		var editor = CKEDITOR.instances.editor1;
		// Resets the "dirty state" of the editor (see CheckDirty())
		// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-resetDirty
		editor.resetDirty();
		alert( 'The "IsDirty" status has been reset' );
	}
	
	function Focus() {
		CKEDITOR.instances.editor1.focus();
	}
	
	function onFocus() {
		if(document.getElementById( 'eMessage' )) {
			document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
		}
	}
	
	function onBlur() {
		if(document.getElementById( 'eMessage' )) {
			document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
		}
	}

	CKEDITOR.config.width='700px';
	
</script>
	</g:compress>
	
<script type="text/javascript">
$(function() {
	//alert("111");

	$("#goal").multiselect().multiselectfilter();
	$("#from").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#to').datepicker('setStartDate', minDate);
    });
    
    $("#to").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#from').datepicker('setEndDate', minDate);
    });
})
function getGoals(value, goalCnt) {
	//alert(value);
	 var strID = null;
	 var action = 'GetGoalsList.action?empId='+value+'&goalCnt='+goalCnt;
	getContent('goalDiv'+goalCnt, action);

 }
 


 
    
</script>
<%
	Map<String, String> orientPosition = (Map<String, String>)request.getAttribute("orientPosition");
 	String attribute = (String) request.getAttribute("attribute");
	String anstype = (String) request.getAttribute("anstype");
	String id = (String) request.getAttribute("id");
	String step = (String) request.getAttribute("step");
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	//System.out.println("id===> " + id);
	
	List<String> OneToOneList = (List<String>)request.getAttribute("OneToOneList");
	if(OneToOneList == null)OneToOneList = new ArrayList<String>();
	System.out.println("OneToOneList::::"+OneToOneList);
	System.out.println("OneToOneList::::"+OneToOneList.get(3));
	
%>

<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-primary">
		<div class="box-body"
			style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<div class="reportWidth">
			<s:form action="CreateOneToOne" id="formID" name="formID" method="POST" theme="simple">
					<div>
						<% if(OneToOneList != null && OneToOneList.size() > 0  ){%>
							<table class="table" width="100%">
							<input type="hidden" name="oneToOneId" id="oneToOneId" value="<%=OneToOneList.get(0) %>"/>
								
							<tr>
								<th width="15%" style="text-align: right">Name:<sup>*</sup>
								</th>
								<td colspan="6">
									<input type="text" cssClass="validateRequired form-control " name="OneToOneName" value ="<%=OneToOneList.get(1)%>"  cssStyle="display:inline;"/> 
								</td>
							</tr>
							 <tr>
	                   			<th style="text-align: right;" valign="top">Description:</th>
	                  			 <td colspan="6">
	                     		 <textarea rows="3" cols="72" name="oneToOne_description" id="editor2"><%=OneToOneList.get(2) %></textarea>
	                 			 </td>
	              			 </tr>
							
							<tr>
								<th style="text-align: right;">Reviewee:</th>
								<td colspan="6">
									<span style="float: left; margin-right: 10px;width:300px;"> 
										<input type="hidden" name="oldRevieweeVal" id="oldRevieweeVal" value="<%=OneToOneList.get(3) %>"/>
										<s:select theme="simple" name="reviewerId" id = "reviewerId" list="reviewerList" listKey="employeeId" listValue="employeeCode" 
		                       	 		value="oldRevieweeVal" cssClass="form-control "/>  <!-- onchange="showMembersSelectEdit(this.value);" -->
									</span>
									<span id="goalDiv" style="float: left;"> 
									Goals:
									<%if( request.getAttribute("sbGoals")!=null){%>
										 <select name="goal" id="goal"  multiple="multiple">
												<%=(String)request.getAttribute("sbGoals") %>
										</select>
		                   			<%}else{ %>
											<s:select name="goal" id ="goal" listKey="id"
											listValue="name" headerKey="" headerValue="Select Goal"
											list="goalList" key="" cssClass="validateRequired" multiple="true">
											 </s:select>
									<%} %> 
									</span>
										
								</td>
						</tr>
						<tr>
								<th style="text-align: right">Start Date:<sup>*</sup></th>
								<td colspan="6">
								<span style="float: left; margin-right: 10px; width:300px;"> 
									<input type="text" name="from" id="from" class="validateRequired form-control" style="width: 150px !important;text-align:center;" value = "<%=OneToOneList.get(4)%>"/>
								</span>
								<span id="Div" style="float: left;">
									End Date:<sup>*</sup>
									<input type="text" name="to" id="to" class="validateRequired form-control" style="width: 150px !important;text-align:center;" value = "<%=OneToOneList.get(5)%>" />
								</span>
							</td>	
						</tr>
						</table>	
						
						<% }else{%>
					
						<table class="table" width="100%">

							<tr>
								<th width="15%" style="text-align: right">Name:<sup>*</sup>
								</th>
								<td colspan="6"><s:textfield cssClass="validateRequired form-control " name="OneToOneName" cssStyle="display:inline;"></s:textfield> 
								</td>
							</tr>
							<tr>
								<th style="text-align: right" valign="top">Description:</th>
								<td colspan="6"><textarea rows="3" cols="72" name="oneToOne_description" id="editor2" class="form-control "></textarea></td>
							</tr>
							<tr>
							<th style="text-align: right">Reviewee:</th>
							<td colspan="6">
								<span style="float: left; margin-right: 10px;"> 
									<s:select theme="simple" name="reviewerId" id="reviewerId" cssClass="validateRequired" 
									list="reviewerList" listKey="employeeId" listValue="employeeCode" headerKey=""  headerValue="Select" onchange="getGoals(this.value, '');"/>
								</span>
								
								<span id="goalDiv" style="float: left;">  
									Goals: <s:select theme="simple" name="goal" id="goal" cssClass="validateRequired  " 
									list="goalList" listKey="id" listValue="name" headerKey="" multiple="true"  headerValue="Select Goal"/>
								</span>
							</td>
							</tr>
							
							<tr>
								<th style="text-align: right">Start Date:<sup>*</sup></th>
								<td><input type="text" name="from" id="from" class="validateRequired form-control" style="width: 85px !important;" /></td>
								<th style="text-align: right">End Date:<sup>*</sup></th>
								<td colspan="4"><input type="text" name="to" id="to" class="validateRequired form-control" style="width: 85px !important;" /></td>
							</tr>
						</table>	
						<%} %>
					</div>		
					<div id="firstdiv" style="float: left; margin-top: 20px;">
						<% if(OneToOneList != null && OneToOneList.size() > 0  ){%>
							<s:submit value="update" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%}else{ %>
							<s:submit value="save" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%} %>						<s:if test="step==3">
							<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
							</s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
					</div>

					<div id="seconddiv" style="float: left; margin-top: 20px; display: none">
						<% if(OneToOneList != null && OneToOneList.size() > 0  ){%>
							<s:submit value="update" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%}else{ %>
							<s:submit value="save" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%} %>
						
						<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<s:if test="id != null">
							<s:submit value="Save & Add New Section" cssClass="btn btn-primary" name="saveandnew"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
							<s:submit value="Save & Add New Subsection" cssClass="btn btn-primary" name="saveandnewsystem"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
							 </s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
					</div>

					<div id="secWeightdiv" style="float: left; margin-top: 20px; display: none">
						<% if(OneToOneList != null && OneToOneList.size() > 0  ){%>
							<s:submit value="update" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%}else{ %>
							<s:submit value="save" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%} %>						<s:if test="id != null">
							<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
								<s:submit value="Save & Add New Subsection" cssClass="btn btn-primary" name="saveandnewsystem"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
							</s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
					</div>

					<div id="subSecWeightdiv" style="float: left; margin-top: 20px; display: none">
						<% if(OneToOneList != null && OneToOneList.size() > 0  ){%>
							<s:submit value="update" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%}else{ %>
							<s:submit value="save" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%} %>						<s:if test="id != null">
							<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
								<s:submit value="Save & Add New Section" cssClass="btn btn-primary" name="saveandnew"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
					 		</s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
					</div>

					<div id="subSecSecWeightdiv" style="float: left; margin-top: 20px; display: none">
						<% if(OneToOneList != null && OneToOneList.size() > 0  ){%>
							<s:submit value="update" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%}else{ %>
							<s:submit value="save" cssClass="btn btn-primary" name="submit" id="submit"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
						<%} %>						<s:if test="id != null">
							<s:submit value="Save & Publish" cssClass="btn btn-primary" name="submitandpublish"></s:submit>&nbsp;&nbsp;&nbsp;&nbsp;
							</s:if>
						<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeForm();">
					</div>
					<s:hidden name="plancount" id="plancount"></s:hidden>
							
					
				</s:form>
			</div>
		</div>
	 </div>
	 </section>
 </div>
 </section>
 
<g:compress>
	<script>
			// Replace the <textarea id="editor1"> with an CKEditor instance.
			if(document.getElementById("editor2")) {
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
			}
			
</script>
</g:compress>
	 				
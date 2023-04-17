<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<script src="scripts/ckeditor_cust/ckeditor.js"></script> 
<g:compress>
   <link href="scripts/ckeditor/samples/sample.css" rel="stylesheet">
   <script>
      // The instanceReady event is fired, when an instance of CKEditor has finished
      // its initialization.
      CKEDITOR.on( 'instanceReady', function( ev ) {
      	// Show the editor name and description in the browser status bar.
      	document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
      
      	// Show this sample buttons.
      	document.getElementById( 'eButtons' ).style.display = 'block';
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
      	document.getElementById( 'eMessage' ).innerHTML = '<b>' + this.name + ' is focused </b>';
      }
      
      function onBlur() {
      	document.getElementById( 'eMessage' ).innerHTML = this.name + ' lost focus';
      }
      	
   </script>
</g:compress>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8">

$(function() {
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
});
   CKEDITOR.config.width ='500px';
   
    
   
   hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
   hs.outlineType = 'rounded-white';
   hs.wrapperClassName = 'draggable-header';
   
   function addDesig(orgid, levelid, userscreen, navigationId, toPage) { 
   
   var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Designation');
	if($(window).width() >= 1100){
		$(".modal-dialog").width(1100);
	}
	$.ajax({
			url : 'AddDesig.action?orgId='+orgid+'&param='+levelid+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
   }
   
   function editDesig(orgid, desigid, levelid, userscreen, navigationId, toPage) { 
   
   var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Designation');
	if($(window).width() >= 1100){
		$(".modal-dialog").width(1100);
	}
	$.ajax({
			url : 'AddDesig.action?orgId='+orgid+'&operation=E&ID='+desigid+'&param='+levelid+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
   }
   
   function addLevel(userscreen, navigationId, toPage) { 
   
   var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Level');
	$.ajax({
			url : 'AddLevel.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
   }
   
   function editLevel(levelid, userscreen, navigationId, toPage) { 
   
   var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Level');
	$.ajax({
			url : 'AddLevel.action?operation=E&ID='+levelid+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
   }
   
   function addGrade(strOrgId, strDesigId, userscreen, navigationId, toPage) { 
   
   var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Grade');
	$.ajax({
			url : 'AddGrade.action?orgId='+strOrgId+'&param='+strDesigId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
   }
   
   function editGrade(strOrgId, gradeId, userscreen, navigationId, toPage) { 
   var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Grade');
	$.ajax({ 
			url : 'AddGrade.action?orgId='+strOrgId+'&operation=E&ID='+gradeId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
   }
   
</script>

<%
   CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
   UtilityFunctions uF = new UtilityFunctions();
   
   Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
   Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
   String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
   
   Map<String, List<List<String>>> hmLevelMapOrgwise = (Map<String, List<List<String>>>) request.getAttribute("hmLevelMapOrgwise");
   Map<String, String> hmOrgName = (Map<String, String>)request.getAttribute("hmOrgName");
   
   /* Map hmLevelMap = (java.util.Map)request.getAttribute("hmLevelMap"); */
   Map hmDesigMap = (Map)request.getAttribute("hmDesigMap");
   Map hmGradeMap = (Map)request.getAttribute("hmGradeMap");
   Map<String, String> hmEmpGradeMap = (Map<String, String>)request.getAttribute("hmEmpGradeMap");
   Map<String, String> hmLevelEmpCount = (Map<String, String>)request.getAttribute("hmLevelEmpCount");
   Map<String, String> hmDesigEmpCount = (Map<String, String>)request.getAttribute("hmDesigEmpCount");
   Map<String, String> hmCriteriaId = (Map<String, String>) request.getAttribute("hmCriteriaId");
   String []arrEnabledModules = CF.getArrEnabledModules();
   
   //out.println("hmLevelMap==>"+hmLevelMap);
   
   	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
   %>
	
	<div class="box-body">
			
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frm" action="MyDashboard" theme="simple">
					<s:hidden name="userscreen" />
					<s:hidden name="navigationId" />
					<s:hidden name="toPage" />
					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
								<% } %>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
		
	<div class="col-md-12">
		<a href="javascript:void(0)" onclick="addLevel('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New Level"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Level</a>
	</div>
	
	<div class="col-md-12">
		<ul class="level_list">
         <% 
            Set setLevelMap = hmLevelMapOrgwise.keySet();
            Iterator it = setLevelMap.iterator();
            
            while(it.hasNext()) {
            	String strOrgId = (String)it.next();
            	List<List<String>> levelList = (List<List<String>>)hmLevelMapOrgwise.get(strOrgId);
            %>
         <li>
            <strong><%=hmOrgName.get(strOrgId) %> </strong>
            <ul class="level_list">
               <%
                  for(int i=0; levelList != null && !levelList.isEmpty() && i<levelList.size(); i++) {
                  	List<String> alLevel = levelList.get(i);
                  	%>
               <li>
                  <%if(uF.parseToInt(hmLevelEmpCount.get(alLevel.get(0))) > 0) { 
                     String strMsg = "Sorry! You have " + uF.parseToInt(hmLevelEmpCount.get(alLevel.get(0))) + " employees added with this Level, therefore we cannot delete the Level. To consider this option, please ensure that you have ZERO Employees added.";
                     %>
                  <a href="javascript:void(0);" onclick="alert('<%=strMsg %>')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
                  <% } else { %>
                  <a href="AddLevel.action?strOrg=<%=strOrgId %>&operation=D&ID=<%=alLevel.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" title="Delete Level"  onclick="return confirm('If you delete the level all the Designations and Grades related to it will be deleted.')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
                  <%} %>
                  <a href="javascript:void(0)" onclick="editLevel('<%=alLevel.get(0)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Level"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                  <strong><%=alLevel.get(2)%> [<%=alLevel.get(1)%>]</strong>
                  <ul>
                     <li class="addnew desgn">
                        <a href="javascript:void(0)"  onclick="addDesig('<%=strOrgId %>','<%=alLevel.get(0) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add Designation"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Desig</a>
                     </li>
                     <li class="desgn">
                        <%
                           List alDesig = (List)hmDesigMap.get(alLevel.get(0));
                           if(alDesig==null)alDesig=new ArrayList();
                           
                           	for(int d=0; d<alDesig.size(); d+=3) {
                           	String strDesigId = (String)alDesig.get(d);
                           	List alGrade = (List)hmGradeMap.get(strDesigId);
                           	if(alGrade==null)alGrade=new ArrayList();
                           %>
                        <%if(uF.parseToInt(hmDesigEmpCount.get(alDesig.get(d))) > 0) {
                           String strMsg = "Sorry! You have " + uF.parseToInt(hmDesigEmpCount.get(alDesig.get(d))) + " employees added with this Designation, therefore we cannot delete the Designation. To consider this option, please ensure that you have ZERO Employees added.";
                           %>
                        <a href="javascript:void(0);" class="del" onclick="alert('<%=strMsg %>')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
                        <% } else { %>
                        <a href="AddDesig.action?orgId=<%=strOrgId %>&operation=D&ID=<%=alDesig.get(d)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" title="Delete Designation" onclick="return confirm('If you delete the designation all the Grades related to it will be deleted.')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i></a>
                        <%} %>
                        <a href="javascript:void(0)" class="edit_lvl" onclick="editDesig('<%=strOrgId %>','<%=alDesig.get(d)%>','<%=alLevel.get(0) %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Designation"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                        <strong><%=alDesig.get(d+2)%> [<%=alDesig.get(d+1)%>]</strong>                   
                        <ul>
                           <li class="addnew desgn">
                              <a href="javascript:void(0);" onclick="addGrade('<%=strOrgId %>','<%=strDesigId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Grade</a>
                           </li>
                           <% for(int g=0; g<alGrade.size(); g+=3) { %>
                           <li>
                              <%if(uF.parseToInt(hmEmpGradeMap.get((String)alGrade.get(g))) > 0) { 
                                 String strMsg = "Sorry! You have " + uF.parseToInt(hmEmpGradeMap.get((String)alGrade.get(g))) + " employees added with this Grade, therefore we cannot delete the Grade. To consider this option, please ensure that you have ZERO Employees added.";
                                 %>
                              <a href="javascript:void(0);" class="del" onclick="alert('<%=strMsg %>')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
                              <% } else { %>
                              <a href="AddGrade.action?orgId=<%=strOrgId %>&operation=D&ID=<%=alGrade.get(g) %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" title="Delete Grade" onclick="return confirm('Are you sure you wish to delete this grade?')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
                              <%} %>
                              <a href="javascript:void(0);" class="edit_lvl" title="Edit Grade" onclick="editGrade('<%=strOrgId %>','<%=alGrade.get(g)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                              <strong><%=alGrade.get(g+2)%> [<%=alGrade.get(g+1)%>]</strong>
                              <span class="user_no"> : <%=uF.showData((String)hmEmpGradeMap.get((String)alGrade.get(g)), "") %></span>
                           </li>
                           <% } %>
                        </ul>
                        <% } %>		
                     </li>
                  </ul>
               </li>
               <% } %>
            </ul>
         </li>
         <% } %>
      </ul>
   </div>
</div>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

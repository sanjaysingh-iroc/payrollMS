<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<script src="scripts/ckeditor_cust/ckeditor.js"></script> 
<g:compress>

<style>


.level_list li
  {
    list-style-type:none;
	font-size:12px;
	margin:0px 0px 1px 0px;
	line-height:25px; 
	/* background: none repeat scroll 0 0 #f4f4f4;  */
    border-bottom: 1px solid #CCCCCC;
	padding:5px 10px;
	  }

.level_list ul li
{
  list-style-type:none;
  font-weight:normal;
  padding:0px 0px 0px 20px;
  border:0px #ccc solid;
}

.level_list ul li ul li
{
  padding:0px 0px 0px 0px;
}
.level_list ul li ul li 
{
     padding:0px 0px 0px 25px;
}

.addnew
{
  padding:4px 5px;
  
  color:#666666;
  font-size:12px;
  display:block;
  width:245px;
}

.addnew a
 {
   color:#666666;
   font-weight:normal;
 }

.del
{
  
  text-decoration:none;
  width:25px;
  
}

.level_list ul li .desgn
{
  margin:3px 0px 3px 0px;
  
}

</style>


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
	$("body").on('click','#close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
});

CKEDITOR.config.height='500px';

hs.graphicsDir = '<%=request.getContextPath()%>/images/highslide/graphics/';
hs.outlineType = 'rounded-white';
hs.wrapperClassName = 'draggable-header';


function addDocument(orgId, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Document');
	if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	$.ajax({
		url : 'AddDocument.action?operation=A&orgId='+orgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}



function editDocument(docid, orgId, userscreen, navigationId, toPage) {
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Document');
	if($(window).width() >= 900){
		$(".modal-dialog").width(900);
	}
	$.ajax({
		url : 'AddDocument.action?operation=E&param='+docid+'&orgId='+orgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addCollateral(orgId, userscreen, navigationId, toPage) {
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Manage Collaterals');
	$.ajax({
		url : 'AddCollateral.action?operation=A&orgId='+orgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addSignature(orgId, userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Manage Signatures');
	$.ajax({
		url : 'AddSignature.action?orgId='+orgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}		


function showAllDocuments(value) {
	//alert(value);
	var status = document.getElementById("hideDocDivStatus"+value).value;
	if(status == '0'){
		document.getElementById("hideDocDivStatus"+value).value = "1";
		document.getElementById("oldDocsDIv"+value).style.display = "block";
		document.getElementById("CuparrowSpan"+value).style.display = "block";
		document.getElementById("CdownarrowSpan"+value).style.display = "none";
	} else {
		document.getElementById("hideDocDivStatus"+value).value = "0";
		document.getElementById("oldDocsDIv"+value).style.display = "none";
		document.getElementById("CuparrowSpan"+value).style.display = "none";
		document.getElementById("CdownarrowSpan"+value).style.display = "block";
	}
}

</script>



<%
	UtilityFunctions uF = new UtilityFunctions(); 
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	List alReport = (List)request.getAttribute("alReport");
	Map<String,List<List<String>>> hmOldReport =(Map<String,List<List<String>>>)request.getAttribute("hmOldReport");
	if(hmOldReport==null) hmOldReport= new HashMap<String, List<List<String>>>();
	
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
			<s:form name="form" action="MyDashboard" theme="simple">
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
								<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.form.submit();"></s:select>
							<% } else { %>
								<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.form.submit();"></s:select>
							<% } %>
						</div>
					</div>
				</div>
			</s:form>
		</div>
	</div>
	
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE, ""); %>
 
	<%-- <div class="filter_div">
		<div class="filter_caption">Filter Organisation</div>
		<s:form name="frm" action="DocumentList" theme="simple" cssClass="inline">
			<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
		</s:form>
	</div> --%>


<div class="col-md-12">
	<a href="javascript:void(0);" onclick="addDocument('<%=request.getAttribute("strOrg") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Document</a>
	&nbsp;|&nbsp; <a href="javascript:void(0);" onclick="addCollateral('<%=request.getAttribute("strOrg") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');">Manage Collateral</a>
	&nbsp;|&nbsp; <a href="javascript:void(0);" onclick="addSignature('<%=request.getAttribute("strOrg") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');">Manage Signature</a>
</div>

	<div class="col-md-12">
	
         <ul class="level_list">
			<% 
			for(int i=0; i<alReport.size(); i++){
				List alInner = (List)alReport.get(i);
				if(alInner==null)alInner = new ArrayList();
			%>
				<li>
					<a title="Delete" href="AddDocument.action?operation=D&param=<%=alInner.get(0)%>&orgId=<%=request.getAttribute("strOrg") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this document?')" style="color:red;" ><i class="fa fa-trash" aria-hidden="true"></i></a> 
					<a title="View and Edit" href="javascript:void(0);" onclick="editDocument('<%=alInner.get(0)%>', '<%=alInner.get(1)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					<%=alInner.get(5)%>
					 Title: <strong><%=alInner.get(3)%></strong>&nbsp;&nbsp;&nbsp; Created By: <strong><%=alInner.get(4)%></strong> &nbsp;&nbsp;&nbsp; Timestamp: <strong><%=alInner.get(2)%></strong> &nbsp;&nbsp;&nbsp; Node aligned: <strong><%=alInner.get(6)%></strong><a href="DocumentPreview.action?doc_id=<%=alInner.get(0)%>" style="float:right">Preview Document</a>
					 <% if(hmOldReport.get(alInner.get(7))!=null) { %> 
					 		<div> 
								<input type="hidden" name="hideDocDivStatus" id="hideDocDivStatus<%=i %>" value = "0"/>
								<a href="javascript: void(0);" onclick="showAllDocuments('<%=i %>');">
									<span id="CdownarrowSpan<%=i %>">
									
									<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i>
									</span>
									<span id="CuparrowSpan<%=i %>" style="display: none;">
									<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
								</span>
								</a>
							</div>
							<div id="oldDocsDIv<%=i %>" style="display: none; width: 100%;">  
								<ul class="level_list" style="padding-left: 10px;">
									<li><h4>Archive:</h4></li>
								<%
									List<List<String>> alOldReport =hmOldReport.get(alInner.get(7));
									for(int j=0; j<alOldReport.size(); j++){
										List alOldInner = (List)alOldReport.get(j);
										if(alOldInner==null)alOldInner = new ArrayList();
								%>
									<li style="border-bottom: 1px solid rgb(236, 236, 236);padding-top: 5px;padding-bottom: 5px;">
										<%=alOldInner.get(5)%>
										Title: <strong><%=alOldInner.get(3)%></strong>&nbsp;&nbsp;&nbsp; Created By: <strong><%=alOldInner.get(4)%></strong> &nbsp;&nbsp;&nbsp; Timestamp: <strong><%=alOldInner.get(2)%></strong> &nbsp;&nbsp;&nbsp; Node aligned: <strong><%=alOldInner.get(6)%></strong><a href="DocumentPreview.action?doc_id=<%=alOldInner.get(0)%>"> &nbsp&nbspPreview Document</a>
										<%} %>
									</li> 
								</ul>
							</div>	
						<%} %>
					</li> 
				<% } %>
		 
		 </ul>  
         
     </div>

     <!-- Legends -->
	
	<div style="float: left; width: 25%;margin-top:50px">
	<div class="label"><strong>Status Legends</strong></div>
		<div><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i> Published</div>
		<div><i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>Saved as Draft</div>
	</div>
</div>


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" id="close" class="close" data-dismiss="modal">&times;</button>
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


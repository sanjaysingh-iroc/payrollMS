<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script src="scripts/ckeditor_cust/ckeditor.js"></script>
 

<style type="text/css">
	/* CSS for the demo. CSS needed for the scripts are loaded dynamically by the scripts */
		
	.ui-tabs-hide{
		display: none !important;
	}
	
	.ui-state-default{
		padding-right: 5px !important;
		padding-left: 5px !important;
		padding-top: 5px !important;
		padding-bottom: 5px !important;
	}

	.ui-widget {
		font-family: 'Source Sans Pro','Helvetica Neue',Helvetica,Arial,sans-serif;
		font-weight: 400;
		font-size: 14px;
	}
	
	.ui-widget input, .ui-widget select, .ui-widget textarea, .ui-widget button {
		font-family: 'Source Sans Pro','Helvetica Neue',Helvetica,Arial,sans-serif;
		font-weight: 400;
		font-size: 14px;
	}
	
	.text1
	{
	 background-image:url("images1/icons/icons/pencil_gray.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:0.6;
	 width: 75px;
	 height: 40px;
	 display: block;
	 border-bottom: 1px solid #999999;
	 padding-bottom: 7px;
	}
	
	.text1:FOCUS
	{
	 background-image:url("images1/icons/icons/pencil_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	.text1selected
	{
	 background-image:url("images1/icons/icons/pencil_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	.text1:hover
	{
	 opacity:1;
	}

	.image1
	{
	 background-image:url("images1/icons/icons/camera_gray.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:0.6;
	 width: 75px;
	 height: 40px;
	 display: block;
	 border-bottom: 1px solid #999999;
	 padding-bottom: 7px;
	}
	
	.image1:FOCUS
	{
	 background-image:url("images1/icons/icons/camera_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	.image1selected
	{
	 background-image:url("images1/icons/icons/camera_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	
	.image1:hover
	{
	 opacity:1;
	}

	.video1
	{
	 background-image:url("images1/icons/icons/video_gray.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:0.6;
	 width: 75px;
	 height: 40px;
	 display: block;
	 border-bottom: 1px solid #999999;
	 padding-bottom: 7px;
	}
	
	.video1:FOCUS
	{
	 background-image:url("images1/icons/icons/video_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	.video1selected
	{
	 background-image:url("images1/icons/icons/video_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	.video1:hover
	{
	 opacity:1;
	}

	.pdf1
	{
	 background-image:url("images1/icons/icons/pdf_gray.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:0.6;
	 width: 75px;
	 height: 40px;
	 display: block;
	 border-bottom: 1px solid #999999;
	 padding-bottom: 7px;
	}
	
	.pdf1:FOCUS
	{
	 background-image:url("images1/icons/icons/pdf_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	
	.pdf1selected
	{
	 background-image:url("images1/icons/icons/pdf_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	
	.pdf1:hover
	{
	 opacity:1;
	}
	
/* ===start parvez date:11-01-2023=== */
	.ppt1
	{
	 background-image:url("images1/icons/icons/ppt_gray.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:0.6;
	 width: 75px;
	 height: 40px;
	 display: block;
	 border-bottom: 1px solid #999999;
	 padding-bottom: 7px;
	}
	
	.ppt1:FOCUS
	{
	 background-image:url("images1/icons/icons/ppt_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	
	.ppt1selected
	{
	 background-image:url("images1/icons/icons/ppt_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	
	.ppt1:hover
	{
	 opacity:1;
	}
/* ===end parvez date: 11-01-2023=== */	
	
	.attach1
	{
	 background-image:url("images1/icons/icons/attachment_gray.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:0.6;
	 width: 75px;
	 height: 40px;
	 display: block;
	 border-bottom: 1px solid #999999;
	 padding-bottom: 7px;
	}
	
	.attach1:FOCUS
	{
	 background-image:url("images1/icons/icons/attachment_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	.attach1selected
	{
	 background-image:url("images1/icons/icons/attachment_green.png");
	 background-repeat:no-repeat;
	 background-position: center;
	 opacity:1;
	}
	.attach1:hover
	{
	 opacity:1;
	}
	
	.arrow {
	
		position: relative;
	 	background: #88b7d5; 
	 	border: 4px solid #666666;
	}		
	.arrow:after, .arrow:before 
	{
		 right: 100%; 
		 top: 50%; 
		 border: solid transparent; 
		 content: " ";
		 height: 0;
		 width: 0;
		 position: absolute;
		 pointer-events: none;
	}
	.arrow:after 
	{ 
		border-color: rgba(136, 183, 213, 0); 
		border-right-color: #88b7d5; 
		border-width: 30px; 
		margin-top: -30px; 
	}
	.arrow:before 
	{ 
	border-color: rgba(194, 225, 245, 0); 
	border-right-color: #c2e1f5; 
	border-width: 36px; 
	margin-top: -36px; 
	}
	.txtlbl {
		color: #777777;
	    font-family: verdana,arial,helvetica,sans-serif;
	    font-size: 11px;
	    font-style: normal;
	    font-weight: 600;
	    width: 100px;
	}
	
	a.close-font:before{
	 font-size: 24px;
    }
</style>
<script> 
    
    	// The instanceReady event is fired, when an instance of CKEditor has finished
    	// its initialization.  
    	CKEDITOR.on( 'instanceReady', function( ev ) {
    		// Show the editor name and description in the browser status bar.
    		if(document.getElementById( 'eMessage' )) {
    			document.getElementById( 'eMessage' ).innerHTML = 'Instance <code>' + ev.editor.name + '<\/code> loaded.';
    		}
    		    	
    		// Show this sample buttons.
    		if(document.getElementById( 'ftext' )) {
    			document.getElementById('ftext').focus();
    		}
    		
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
    		if ( editor.mode == 'wysiwyg'){
    			// Insert as plain text.
    			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-insertText
    			editor.insertText( value );
    		} else
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
    		if ( editor.mode == 'wysiwyg' ) {
    			// Execute the command.
    			// http://docs.ckeditor.com/#!/api/CKEDITOR.editor-method-execCommand
    			editor.execCommand( commandName );
    		} else
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
	
   
    	function addtextclass()
    	{
    		//alert("text1selected ====================> ");
    		$('.text1').addClass('text1selected');
    		$('.image1').removeClass('image1selected');
    		$('.video1').removeClass('video1selected');
    		$('.pdf1').removeClass('pdf1selected');
    		$('.ppt1').removeClass('ppt1selected');
    		$('.attach1').removeClass('attach1selected');
    	}
    	function addimageclass()
    	{
    		//alert("text1selected ====================> ");
    		$('.text1').removeClass('text1selected');
    		$('.image1').addClass('image1selected');
    		$('.video1').removeClass('video1selected');
    		$('.pdf1').removeClass('pdf1selected');
    		$('.ppt1').removeClass('ppt1selected');
    		$('.attach1').removeClass('attach1selected');
    	}
    	function addvideoclass()
    	{
    		//alert("text1selected ====================> ");
    		$('.text1').removeClass('text1selected');
    		$('.image1').removeClass('image1selected');
    		$('.video1').addClass('video1selected');
    		$('.pdf1').removeClass('pdf1selected');
    		$('.ppt1').removeClass('ppt1selected');
    		$('.attach1').removeClass('attach1selected');
    	}
    	function addpdfclass()
    	{
    		//alert("text1selected ====================> ");
    		$('.text1').removeClass('text1selected');
    		$('.image1').removeClass('image1selected');
    		$('.video1').removeClass('video1selected');
    		$('.pdf1').addClass('pdf1selected');
    		$('.ppt1').removeClass('ppt1selected');
    		$('.attach1').removeClass('attach1selected');
    	}
    	function addPPTClass()
    	{
    		$('.text1').removeClass('text1selected');
    		$('.image1').removeClass('image1selected');
    		$('.video1').removeClass('video1selected');
    		$('.pdf1').removeClass('pdf1selected');
    		$('.ppt1').addClass('ppt1selected');
    		$('.attach1').removeClass('attach1selected');
    	}
    	function addattach1class()
    	{
    	//alert("text1selected ====================> ");
    		$('.text1').removeClass('text1selected');
    		$('.image1').removeClass('image1selected');
    		$('.video1').removeClass('video1selected');
    		$('.pdf1').removeClass('pdf1selected');
    		$('.ppt1').removeClass('ppt1selected');
    		$('.attach1').addClass('attach1selected');
    	}
   
    
    
    var cxtpath='<%=request.getContextPath()%>';
    
    var divcnt = 0;
    function addNewSubchapter(chaptercnt, subchaptercnt) {
    	
    	if(subchaptercnt > 0){
    		divcnt = subchaptercnt;	
    	}
    	//alert("divcnt ===> " + divcnt);
    	divcnt++;
    	var divTag = document.createElement("div");
        divTag.id = "subchapter"+chaptercnt+"_"+divcnt;
        divTag.setAttribute("style", "float:left;width: 100%;margin-top: 25px;");
        //divTag.setAttribute("class", "row_hobby");
    	divTag.innerHTML =  "<table border=\"0\" class=\"table\" style=\"width: 85%;\">"+
						    	"<tr><td class=\"txtlabel\" style=\"vertical-align: top; text-align: right\"><input type=\"hidden\" name=\"subchapterCount"+chaptercnt+"\" value=\""+divcnt+"\"/>Subchapter Name:<sup>*</sup></td>"
						    		+"<td><span style=\"float: left;\"><input type=\"text\" name=\"subchapterName\" class=\"validateRequired form-control \" style=\"width: 450px;\"/></span>"
						    		+"<input type=\"hidden\" name=\"descStatus\" id=\"descStatus"+chaptercnt+"_"+divcnt+"\" value=\"0\">"
						    		+"<span id=\"addDescSpan"+chaptercnt+"_"+divcnt+"\" style=\"float: left; margin-left: 5px;\"><a class=\"add_lvl\" href=\"javascript:void(0)\" onclick=\"addAndRemoveDescription('"+chaptercnt+"','"+divcnt+"');\"> Add Description </a></span>"
						    		+"<span id=\"removeDescSpan"+chaptercnt+"_"+divcnt+"\" style=\"display: none; float: left; margin-left: 5px;\"><a href=\"javascript:void(0)\" onclick=\"addAndRemoveDescription('"+chaptercnt+"','"+divcnt+"');\" title=\"Remove Description\">"
						    		//+"<img border=\"0\"  src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\"/> Remove Description</a></span>" 
						    		+"<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i> Remove Description</a></span>"
						    		 //+"<span style=\"float: right;\"><img border=\"0\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Subchapter\" onclick=\"removeSubchapter('"+chaptercnt+"','"+divcnt+"');\"/></span>"
						    		 +"<span style=\"float: right;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove Subchapter\" onclick=\"removeSubchapter('"+chaptercnt+"','"+divcnt+"');\"></i></span>"
						    	+"</td></tr>"+
						    	"<tr id=\"subDescTR"+chaptercnt+"_"+divcnt+"\" style=\"display: none;\">"+ // 
						    		"<td class=\"txtlabel\" style=\"vertical-align: top; text-align: right\">Subchapter Description:</td>"+
						    		"<td><textarea rows=\"3\" cols=\"72\" name=\"subchapterDescription\" id=\"editorrr"+chaptercnt+"_"+divcnt+"\" class=\" form-control \"></textarea></td>"+
						    	"</tr>"+
						    	"<tr><td colspan=\"2\"><div style=\"float: left; margin-left: 100px; margin-top: 15px;\">"+
						    			"<a href=\"javascript:void(0)\" onclick=\"showContentDiv('"+chaptercnt+"','"+divcnt+"');\"> +Content</a> &nbsp;"+ 
						    			"<a href=\"javascript:void(0)\" onclick=\"showAssessmentDiv('"+chaptercnt+"','"+divcnt+"');\"> +Assessment</a>"+
						    		"</div></td></tr></table>"+
					    		"<div id=\"contentDiv"+chaptercnt+"_"+divcnt+"\" style=\"float: left; width: 90%; margin-left: 100px; margin-bottom: 30px; display: none;\">"+ //border: 1px solid; 
					    		"&nbsp;&nbsp;&nbsp;Add content here<br/><div style=\"float: left; width: 11%; min-height: 200px; margin: 5px 5px 5px 5px; background: none repeat scroll 0% 0% rgba(0, 0, 0, 0.04);\">"+
					    		"<div style=\"width: 75%; height: 40px; margin: 15px;\"> <a href=\"javascript:void(0)\" class=\"text1\" onclick=\"addNewTextarea('0','"+chaptercnt+"','"+divcnt+"');\" autofocus></a></div>"+ //style=\"background-image: url(&quot;images1/icons/icons/pencil_green.png&quot;); background-repeat: no-repeat; width: 40px; height: 40px; display: block;\" onmouseout=\"alert('hi');\" 
					    		"<div style=\"width: 75%; height: 40px; margin: 15px;\"> <a href=\"javascript:void(0)\" class=\"image1\" onclick=\"addNewImage('0','"+chaptercnt+"','"+divcnt+"');\"></a></div>"+
					    		"<div style=\"width: 75%; height: 40px; margin: 15px;\"> <a href=\"javascript:void(0)\" class=\"video1\" onclick=\"addNewVideo('0','"+chaptercnt+"','"+divcnt+"');\"></a></div>"+
					    		"<div style=\"width: 75%; height: 40px; margin: 15px;\"> <a href=\"javascript:void(0)\" class=\"pdf1\" onclick=\"addNewPDF('0','"+chaptercnt+"','"+divcnt+"');\"></a></div>"+
					    		"<div style=\"width: 75%; height: 40px; margin: 15px;\"> <a href=\"javascript:void(0)\" class=\"ppt1\" onclick=\"addNewPPT('0','"+chaptercnt+"','"+divcnt+"');\"></a></div>"+
					    		"<div style=\"width: 75%; height: 40px; margin: 15px;\"> <a href=\"javascript:void(0)\" class=\"attach1\" onclick=\"addNewAttachment('0','"+chaptercnt+"','"+divcnt+"');\"></a></div>"+
					    		"</div>"+
					    		"<div id=\"addcontentDiv"+chaptercnt+"_"+divcnt+"\" style=\"float: left; width: 75%; min-height: 300px; margin: 5px; box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.04) inset; border: 1px solid rgb(228, 228, 228); padding: 10px; background-color: white;\"></div>"+ //border: 1px solid; 
					    		"</div>"+
					    		"<div id=\"assessmentDiv"+chaptercnt+"_"+divcnt+"\" style=\"float: left; width: 90%; margin-bottom: 30px; min-height: 200px; display: none;\">"+
					    		"<ul class=\"level_list ul_class\">"+
					    		//"<li style=\"margin-left: 100px;\"><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"getQuestion('0','','"+chaptercnt+"','"+divcnt+"');\">Add Assessment</a></li>"+
					    		"<li id=\"questionLi"+chaptercnt+"_"+divcnt+"\" style=\"margin-left: 100px;\"></li></ul>"+
					    		"</div>";
    		 
       							 document.getElementById("subchapterDiv"+chaptercnt).appendChild(divTag);
                       
						     // Replace the <textarea id="editor2"> with an CKEditor instance.
						    	CKEDITOR.replace( 'editorrr'+chaptercnt+'_'+divcnt, {
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
    
    
    function removeSubchapter(chaptercnt, removeId) {
    	
    	var remove_elem = "subchapter"+chaptercnt+"_"+removeId;
    	var row_skill = document.getElementById(remove_elem); 
    	document.getElementById("subchapterDiv"+chaptercnt).removeChild(row_skill);
    }
    
    function changeStatus(id) {
    	//alert("id==>"+id);
		if (document.getElementById('addFlag' + id).checked == true) {
			document.getElementById('status' + id).value = '1';
		} else {
			document.getElementById('status' + id).value = '0';
		}
	}
    function removeEditSubchapter(chaptercnt, removeId, delId, type) {
    	if(confirm('Are you sure, you want to remove this subchapter?')) {
    		//alert("delId ===> "+  delId + " type ===> " + type);
    			var action = 'DeleteCourseAssessmentContentAndSubchapter.action?delId=' + delId + '&type=' + type;
    			getContent("", action);
    		var remove_elem = "subchapter"+chaptercnt+"_"+removeId;
    		var row_skill = document.getElementById(remove_elem); 
    		document.getElementById("subchapterDiv"+chaptercnt).removeChild(row_skill);
    	}
    }
    
    function removeContent(removeDiv, chaptercnt, divCntId, removeId) {
    	if(confirm('Are you sure, you want to remove this content?')) {
    	var remove_elem = removeDiv+chaptercnt+"_"+divCntId+"_"+removeId;
    	var row_skill = document.getElementById(remove_elem); 
    	document.getElementById("addcontentDiv"+chaptercnt+"_"+divCntId).removeChild(row_skill);
    	}
    }
     
    function removeEditContent(removeDiv, chaptercnt, divCntId, removeId, delId, type) {
    	if(confirm('Are you sure, you want to remove this content?')) {
    	//alert("delId ===> "+  delId + " type ===> " + type);
    		var action = 'DeleteCourseAssessmentContentAndSubchapter.action?delId=' + delId + '&type=' + type;
    		getContent("", action);
    	var remove_elem = removeDiv+chaptercnt+"_"+divCntId+"_"+removeId;
    	var row_skill = document.getElementById(remove_elem); 
    	document.getElementById("addcontentDiv"+chaptercnt+"_"+divCntId).removeChild(row_skill);
    	}
    }
    
    
    var addTextCnt = 0;
    var oldDivCnt = 0;
    function addNewTextarea(oldcnt, chaptercnt, divcnt) {
    	
    	if(confirm('Are you sure, you want to add text content?')) {
    	if(oldDivCnt != divcnt) {
    		addTextCnt = oldcnt;
    	}
    	addTextCnt++;
    	var divTag = document.createElement("div");
        divTag.id = "textareaDiv"+chaptercnt+"_"+divcnt+"_"+addTextCnt;
        divTag.setAttribute("style", "float:left;width: 100%;margin-top: 15px;");
        //divTag.setAttribute("class", "row_hobby");
    	divTag.innerHTML = "<table width=\"100%\" class=\"table\"><tr><td><input type=\"hidden\" name=\"textSubchapterId"+chaptercnt+"\" value=\""+divcnt+"\"/><textarea rows=\"3\" cols=\"72\" name=\"contentTextarea\" id=\"editorr"+chaptercnt+"_"+divcnt+"_"+addTextCnt+"\" class=\" form-control \"></textarea></td>"
    	/* +"<td valign=\"top\"><span style=\"float: right;\"><img border=\"0\"  src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove This Text\" onclick=\"removeContent('textareaDiv','"+chaptercnt+"','"+divcnt+"','"+addTextCnt+"');\"/></span></td></tr></table>"+ */
    	+"<td valign=\"top\"><span style=\"float: right;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove This Text\" onclick=\"removeContent('textareaDiv','"+chaptercnt+"','"+divcnt+"','"+addTextCnt+"');\"></i></span></td></tr></table>"+
    		"</div>";
    		document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).innerHTML = '';	                
        document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).appendChild(divTag);
        
     // Replace the <textarea id="editor2"> with an CKEditor instance.
    	CKEDITOR.replace( 'editorr' +chaptercnt + '_' + divcnt + '_' + addTextCnt, {
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
    	oldDivCnt = divcnt;
    	}
    	addtextclass();
    }
      
    var addImgCnt = 0;
    function addNewImage(oldcnt, chaptercnt, divcnt) {
    	if(confirm('Are you sure, you want to add image content?')) {
    	if(oldDivCnt != divcnt){
    		addImgCnt = oldcnt;
    	}
    	addImgCnt++;
    	var divTag = document.createElement("div");
        divTag.id = "imageDiv"+chaptercnt+"_"+divcnt+"_"+addImgCnt;
        divTag.setAttribute("style", "float:left;width: 100%;margin-top: 15px;");
    	divTag.innerHTML = "<table width=\"100%\" class=\"table\"><tr><td><input type=\"hidden\" name=\"imageSubchapterId"+chaptercnt+"\" value=\""+divcnt+"\"/>Select Image:<sup>*</sup</td><td><input type=\"file\" class=\"validateRequired form-control\" name=\"contentImage\" id=\"contentImage"+chaptercnt+"_"+divcnt+"_"+addImgCnt+"\" onChange = \"readImageURL(this,'"+chaptercnt+"', '"+divcnt+"','"+addImgCnt+"','contentImgIframe');\"/> </td>"
    	//+"<td valign=\"top\"><span style=\"float: right;\"><img border=\"0\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove This Image\" onclick=\"removeContent('imageDiv','"+chaptercnt+"','"+divcnt+"','"+addImgCnt+"');\"/></span></td></tr>"
    	+"<td valign=\"top\"><span style=\"float: right;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove This Image\" onclick=\"removeContent('imageDiv','"+chaptercnt+"','"+divcnt+"','"+addImgCnt+"');\"></i></span></td></tr>"
    	+"<tr><td colspan=\"3\"><img height=\"300\" width=\"500\" id=\"contentImgIframe"+chaptercnt+"_"+divcnt+"_"+addImgCnt+"\" src=\"#\"/></td> </tr>"
    	+"<tr><td> Image Title:</td><td colspan=\"2\"><input type=\"text\" name=\"imageTitle\" class=\"validateRequired form-control \" style=\"width: 250px;\"/></td> </tr></table>"
    	+"</div>";
    	document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).innerHTML = '';
        document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).appendChild(divTag);
     
    	oldDivCnt = divcnt; 
    	}
    	addimageclass();
    }
    
    var fileId =[];
    function readImageURL(input, chaptercnt, divcnt, addImgCnt, targetDiv) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#'+targetDiv+chaptercnt+'_'+divcnt+'_'+addImgCnt)
                    .attr('src', e.target.result)
                    .width(500)
                    .height(300);
            };
            reader.readAsDataURL(input.files[0]);
            fileId.push(targetDiv+chaptercnt+"_"+divcnt+"_"+addImgCnt);
        }
    }
    
    
    function readPdfURL(input, chaptercnt, divcnt, addImgCnt, targetDiv) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#'+targetDiv+chaptercnt+'_'+divcnt+'_'+addImgCnt)
                    .attr('src', e.target.result)
                    .width('96%')
                    .height(300);
            };
            reader.readAsDataURL(input.files[0]);
            fileId.push(targetDiv+chaptercnt+"_"+divcnt+"_"+addImgCnt);
        }
        
    }
    
    
    function readPptURL(input, chaptercnt, divcnt, addImgCnt, targetDiv) {
        if (input.files && input.files[0]) {
            var frameV = $('#'+targetDiv+chaptercnt+'_'+divcnt+'_'+addImgCnt).contents().find('body');
            var vlu = "<div style=\"float: left;width: 100%;margin-left:10px;height: 300px; background-color: #CCCCCC;\"><div style=\"text-align: center; font-size: 24px; padding: 150px;\">Preview not available</div></div>"; 
            frameV.html(vlu);
        }
    }
    
    var addPDFCnt = 0;
    function addNewPDF(oldcnt, chaptercnt, divcnt) {
    	if(confirm('Are you sure, you want to add pdf content?')) {
    	if(oldDivCnt != divcnt){
    		addPDFCnt = oldcnt;
    	}
    	addPDFCnt++;
    	var divTag = document.createElement("div");
        divTag.id = "pdfDiv"+chaptercnt+"_"+divcnt+"_"+addPDFCnt;
        divTag.setAttribute("style", "float:left;width: 100%;margin-top: 15px;");
        //divTag.setAttribute("class", "row_hobby");
    	divTag.innerHTML = "<table width=\"100%\" class=\"table\"><tr><td><input type=\"hidden\" name=\"pdfSubchapterId"+chaptercnt+"\" value=\""+divcnt+"\"/>Select PDF:<sup>*<sup></td><td><input type=\"file\" name=\"contentPdf\" class=\"validateRequired form-control \" id=\"contentPdf"+chaptercnt+"_"+divcnt+"_"+addPDFCnt+"\" onChange = \"readPdfURL(this,'"+chaptercnt+"', '"+divcnt+"','"+addPDFCnt+"','contentPDFIframe');\"/> </td>"
    	//+"<td valign=\"top\"><span style=\"float: right;\"><img border=\"0\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove This PDF\" onclick=\"removeContent('pdfDiv','"+chaptercnt+"','"+divcnt+"','"+addPDFCnt+"');\"/></span></td></tr>"
    	+"<td valign=\"top\"><span style=\"float: right;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove This PDF\" onclick=\"removeContent('pdfDiv','"+chaptercnt+"','"+divcnt+"','"+addPDFCnt+"');\"></i></span></td></tr>"
    	
    	+"<tr><td> Pdf Title:<sup>*<sup></td><td colspan=\"2\"><input type=\"text\" name=\"pdfTitle\" class=\"validateRequired form-control \" style=\"width: 250px;\"/></td> </tr>"
    	+"<tr><td colspan=\"3\"><iframe height=\"300\" width=\"96%\" id=\"contentPDFIframe"+chaptercnt+"_"+divcnt+"_"+addPDFCnt+"\" /></td> </tr>"
    	+"</table>"
    	+"</div>";
    	//alert("divTag ===>  "+divTag);
    	document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).innerHTML = '';
        document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).appendChild(divTag);
     
    	oldDivCnt = divcnt; 
    	}
    	addpdfclass();
    }
    
/* ===start parvez date: 11-01-2023=== */
	var addPPTCnt = 0;
    function addNewPPT(oldcnt, chaptercnt, divcnt) {
    	if(confirm('Are you sure, you want to add PPT content?')) {
    	if(oldDivCnt != divcnt){
    		addPPTCnt = oldcnt;
    	}
    	addPPTCnt++;
    	var divTag = document.createElement("div");
        divTag.id = "pptDiv"+chaptercnt+"_"+divcnt+"_"+addPPTCnt;
        divTag.setAttribute("style", "float:left;width: 100%;margin-top: 15px;");
        
    	divTag.innerHTML = "<table width=\"100%\" class=\"table\"><tr><td><input type=\"hidden\" name=\"pptSubchapterId"+chaptercnt+"\" value=\""+divcnt+"\"/>Select PPT:<sup>*<sup></td><td><input type=\"file\" name=\"contentPPT\" class=\"validateRequired form-control \" id=\"contentPPT"+chaptercnt+"_"+divcnt+"_"+addPPTCnt+"\" onChange = \"readPptURL(this,'"+chaptercnt+"', '"+divcnt+"','"+addPPTCnt+"','contentPPTIframe');\"/> </td>"
    	
    	+"<td valign=\"top\"><span style=\"float: right;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove This PPT\" onclick=\"removeContent('pptDiv','"+chaptercnt+"','"+divcnt+"','"+addPPTCnt+"');\"></i></span></td></tr>"
    	
    	+"<tr><td> PPT Title:<sup>*<sup></td><td colspan=\"2\"><input type=\"text\" name=\"pptTitle\" class=\"validateRequired form-control \" style=\"width: 250px;\"/></td> </tr>"
    	+"<tr><td colspan=\"3\"><iframe height=\"300\" width=\"96%\" id=\"contentPPTIframe"+chaptercnt+"_"+divcnt+"_"+addPPTCnt+"\" /></td> </tr>"
    	/* +"<tr><td colspan=\"3\">Preview not available</td> </tr>" */
    	+"</table>"
    	+"</div>";
    	
    	document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).innerHTML = '';
        document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).appendChild(divTag);
     
    	oldDivCnt = divcnt; 
    	}
    	addPPTClass();
    }
/* ===end parvez date: 11-01-2023=== */    
    
    var addVideoCnt = 0;
    function addNewVideo(oldcnt, chaptercnt, divcnt) {
    	if(confirm('Are you sure, you want to add video content?')) {
    	if(oldDivCnt != divcnt){
    		addVideoCnt = oldcnt;
    	}
    	addVideoCnt++;
    	var divTag = document.createElement("div");
        divTag.id = "videoDiv"+chaptercnt+"_"+divcnt+"_"+addVideoCnt;
        divTag.setAttribute("style", "float:left;width: 100%;margin-top: 15px;");
        //divTag.setAttribute("class", "row_hobby");
    	divTag.innerHTML = "<table width=\"100%\" class=\"table\"><tr><td><input type=\"hidden\" name=\"videoSubchapterId"+chaptercnt+"\" value=\""+divcnt+"\"/>Select Video:<sup>*</sup></td><td><input type=\"file\" name=\"contentVideo\" class=\"validateRequired form-control\" id=\"contentVideo"+chaptercnt+"_"+divcnt+"_"+addImgCnt+"\"/></td>"
    	//+"<td valign=\"top\"><span style=\"float: right;\"><img border=\"0\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove This Video\" onclick=\"removeContent('videoDiv','"+chaptercnt+"','"+divcnt+"','"+addVideoCnt+"');\"/></span></td></tr>"
    	+"<td valign=\"top\"><span style=\"float: right;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove This Video\" onclick=\"removeContent('videoDiv','"+chaptercnt+"','"+divcnt+"','"+addVideoCnt+"');\"></i></span></td></tr>"
    	
    	+"<tr><td> Video URL:<sup>*</sup></td><td colspan=\"2\"><input type=\"text\" name=\"videoUrl\" class=\"validateRequired form-control \" style=\"width: 250px;\"/></td> </tr>"
    	+"<tr><td> Video Title:<sup>*</sup></td><td colspan=\"2\"><input type=\"text\" name=\"videoTitle\" class=\"validateRequired form-control \" style=\"width: 250px;\"/></td> </tr></table>"
    	+"</div>";
    	document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).innerHTML = '';	                
        document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).appendChild(divTag);
     
    	oldDivCnt = divcnt; 
    	}
    	addvideoclass();
    }
    
    
    var addAttachCnt = 0;
    function addNewAttachment(oldcnt, chaptercnt, divcnt) {
    	if(confirm('Are you sure, you want to add attachment content?')) {
    	if(oldDivCnt != divcnt){
    		addAttachCnt = oldcnt;
    	}
    	addAttachCnt++;
    	var divTag = document.createElement("div");
        divTag.id = "attachDiv"+chaptercnt+"_"+divcnt+"_"+addAttachCnt;
        divTag.setAttribute("style", "float:left;width: 100%;margin-top: 15px;");
        //divTag.setAttribute("class", "row_hobby");
    	divTag.innerHTML = "<table width=\"100%\" class=\"table\"><tr><td><input type=\"hidden\" name=\"attachSubchapterId"+chaptercnt+"\" value=\""+divcnt+"\"/>Select Attachment:<sup>*</sup></td><td><input type=\"file\" name=\"contentAttach\" id=\"contentAttach"+chaptercnt+"_"+divcnt+"_"+addImgCnt+"\"  class=\"validateRequired form-control\"/></td>"
    	//+"<td valign=\"top\"><span style=\"float: right;\"><img border=\"0\" src=\""+cxtpath+"/images1/icons/icons/close_button_icon.png\" title=\"Remove This Attachment\" onclick=\"removeContent('attachDiv','"+chaptercnt+"','"+divcnt+"','"+addAttachCnt+"');\"/></span></td></tr>"
    	+"<td valign=\"top\"><span style=\"float: right;\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove This Attachment\" onclick=\"removeContent('attachDiv','"+chaptercnt+"','"+divcnt+"','"+addAttachCnt+"');\"></i></span></td></tr>"
    	+"<tr><td> Attachment Title:<sup>*</sup></td><td colspan=\"2\"><input type=\"text\" name=\"attachTitle\" class=\"validateRequired form-control \" style=\"width: 250px;\"/></td> </tr></table>"
    	+"</div>";
    	document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).innerHTML = '';
        document.getElementById("addcontentDiv"+chaptercnt+"_"+divcnt).appendChild(divTag);
    	oldDivCnt = divcnt; 
    	}
    	addattach1class();
    }
    
    
    var questionCnt = 0;
    var anstype = '<%=(String) request.getAttribute("anstype")%>';
    
    function showAnswerTypeDiv(ansType, cnt, id, id1, divCnt) {
    	//alert("ansType ===> " + ansType); 
    var action = 'ShowAnswerType.action?ansType=' + ansType;
    getContent("anstypediv"+divCnt+"_"+cnt, action);
    changeNewAnswerType(ansType, cnt, id, id1,divCnt);
    }
    
    
    function changeNewAnswerType(val, cnt, id, id1,divCnt) {
   // console.log("id ===> " + id + " id1 ===> " + id1 + " val ===> " + val);
     if (val == 1 || val == 2 || val == 8) {
    	addQuestionType1(id,cnt,divCnt);
    	document.getElementById(id).style.display = 'table-row';
    
    	addQuestionType2(id1,cnt,divCnt);
    	document.getElementById(id1).style.display = 'table-row';
    } else if (val == 9) {
    	addQuestionType3(id,cnt,divCnt);
    	document.getElementById(id).style.display = 'table-row';
    
    	addQuestionType4(id1,cnt,divCnt);
    	document.getElementById(id1).style.display = 'table-row';
    
     }else if (val == 6) {
    	addTrueFalseType(id,cnt,divCnt);
    	document.getElementById(id).style.display = 'table-row';
    	document.getElementById(id1).innerHTML ="";
    	document.getElementById(id1).style.display = 'none';
    
    }else if (val == 5) {
    	addYesNoType(id,cnt,divCnt);
    	document.getElementById(id).style.display = 'table-row';
    	document.getElementById(id1).innerHTML ="";
    	document.getElementById(id1).style.display = 'none';
    
    } else {
    	addQuestionType1(id,cnt,divCnt);
    	addQuestionType2(id1,cnt,divCnt);
    	document.getElementById(id).style.display = 'none';
    	document.getElementById(id1).style.display = 'none';
    }
    
    }
    
    
    function addTrueFalseType(id,cnt,divCnt){
    document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"2\"><input type=\"hidden\" name=\"optiona"+divCnt+"\"/><input type=\"hidden\" name=\"optionb"+divCnt+"\"/><input type=\"hidden\" name=\"optionc"+divCnt+"\"/><input type=\"hidden\" name=\"optiond"+divCnt+"\"/><input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True"
    + "<input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+ "\" value=\"0\">False</td>";
    }
    function addYesNoType(id,cnt,divCnt){
    document.getElementById(id).innerHTML = "<th></th><th></th><td colspan=\"2\"><input type=\"hidden\" name=\"optiona"+divCnt+"\"/><input type=\"hidden\" name=\"optionb"+divCnt+"\"/><input type=\"hidden\" name=\"optionc"+divCnt+"\"/><input type=\"hidden\" name=\"optiond"+divCnt+"\"/><input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes"
    + "<input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+ "\" value=\"0\">No</td>";
    }
    function addQuestionType1(id,cnt,divCnt) {
    	document.getElementById(id).innerHTML = "<th></th><th></th><td>a)<input type=\"text\" name=\"optiona"+divCnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+divCnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb"+divCnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"b\" /></td>";
    }
    function addQuestionType2(id1,cnt,divCnt) {
    document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)<input type=\"text\" name=\"optionc"+divCnt+"\"  class=\"validateRequired  form-control \"  /> <input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond"+divCnt+"\" class=\"validateRequired form-control \" /> <input type=\"radio\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"d\" /></td>";
    }
    function addQuestionType3(id,cnt,divCnt) {
    document.getElementById(id).innerHTML = "<th></th><th></th><td>a)<input type=\"text\" name=\"optiona"+divCnt+"\" class=\"validateRequired form-control \"/> <input type=\"checkbox\" value=\"a\" name=\"correct"+divCnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)<input type=\"text\" name=\"optionb"+divCnt+"\"  class=\"validateRequired form-control \" /> <input type=\"checkbox\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"b\" /></td>";
    }
    function addQuestionType4(id1,cnt,divCnt) {
    document.getElementById(id1).innerHTML = "<th></th><th></th><td>c)<input type=\"text\" name=\"optionc"+divCnt+"\"  class=\"validateRequired form-control \" /> <input type=\"checkbox\" name=\"correct"+divCnt+"_"+ cnt+"\"  value=\"c\"  /></td><td colspan=\"2\">d)<input type=\"text\" name=\"optiond"+divCnt+"\"  class=\" validateRequired form-control \" /> <input type=\"checkbox\" name=\"correct"+divCnt+"_"+ cnt+"\" value=\"d\" /></td>";
    }
    
    
    function getQuestion(oldcnt, callFrom, chaptercnt, divcnt) {
    	//console.log("oldcnt ===> " + oldcnt + " divCnt ===> " + divcnt + " oldDivCnt ===> " + oldDivCnt);
      	var totweight=0;
      	if(parseInt(oldDivCnt) != parseInt(divcnt)) {
      		questionCnt = oldcnt;
    	}
      	//alert("questionCnt ===> " + questionCnt);
    	for(var i=1; i <= parseInt(questionCnt); i++){
    		var weight = document.getElementById("weightage"+chaptercnt+"_"+divcnt+"_"+i);
    		if (weight == null){
    			continue;	
    		}
    		weight = document.getElementById("weightage"+chaptercnt+"_"+divcnt+"_"+i).value;
    		if(weight == undefined){
    			weight = 0;
    		}
    		totweight = totweight + parseFloat(weight);
    	}
    	//alert("questionCnt 1 ===> " + questionCnt);
    	var remainweight = 100 - parseFloat(totweight);
    	if(parseInt(remainweight) <= 0){
    		alert("Unable to add questions because of no weightage available");			
    	}else{
    		//alert("questionCnt 2 ===> " + questionCnt);
    	questionCnt++;
    	var cnt=questionCnt;
    	//alert("questionCnt 3 ===> " + questionCnt);
    	var ultag = document.createElement('ul');
    	var aa = getQuestoinContentType(cnt,callFrom, chaptercnt, divcnt);
    	//alert("questionCnt 4 ===> " + questionCnt);
    	ultag.id = "questionUl"+chaptercnt+"_"+divcnt+"_"+cnt;
    	 var a = "<li><table class=\"table sectionfont\" width=\"100%\">"
    			+ "<tr><th>"+chaptercnt+"."+divcnt+"."+questionCnt+")</th><th width=\"17%\" style=\"text-align: right;\">Add Question:<sup>*</sup></th>"
    			+ "<td colspan=\"3\"><span id=\"newquespan"+chaptercnt+"_"+divcnt+"_"+cnt+"\" style=\"float: left; \"><input type=\"hidden\" name=\"hidequeid"+chaptercnt+"_"+divcnt+"\" id=\"hidequeid"+chaptercnt+"_"+divcnt+"_"+cnt+"\" value=\"0\"/>"
    			+"<textarea rows=\"2\" name=\"question"+chaptercnt+"_"+divcnt+"\" id=\"question"+chaptercnt+"_"+divcnt+"_"+cnt+"\" class=\"validateRequired form-control \"  style=\"width: 330px;\"></textarea>"
    			//+"<input type=\"text\" name=\"question\" id=\"question"+cnt+"\" style=\"width: 330px;\"/>"
    			+"</span>"
    
    			+ "&nbsp;<span style=\"float: left; margin-left: 10px;\"><input type=\"hidden\" name=\"orientt"+chaptercnt+"_"+divcnt+"\" value=\""+cnt+"\"/>"
    			+"<input type=\"text\" style=\"width: 35px !important;\" name=\"weightage"+chaptercnt+"_"+divcnt+"\" id=\"weightage"+chaptercnt+"_"+divcnt+"_"+cnt+"\" class=\"validateRequired form-control \" value=\"100\" onkeypress=\"return isNumberKey(evt)\" onkeyup=\"validateScore(this.value,'weightage"+chaptercnt+"_"+divcnt+"_"+cnt+"','hideweightage"+chaptercnt+"_"+divcnt+"_"+cnt+"');\"/>"
    			+"<sup>*</sup><input type=\"hidden\" name=\"hideweightage"+chaptercnt+"_"+divcnt+"\" id=\"hideweightage"+chaptercnt+"_"+divcnt+"_"+cnt+"\" value=\"100\"/></span>&nbsp;&nbsp;"
    			+"<span style=\"float: left; margin-left: 10px;\"><a href=\"javascript:void(0)\" title=\"Select from Question Bank\" onclick=\"openQuestionBank('"+cnt+"','"+callFrom+"', '"+chaptercnt+"', '"+divcnt+"');\" > +Q </a></span>&nbsp;"
    			+"<span id=\"checkboxspan"+chaptercnt+"_"+divcnt+"_"+cnt+"\" style=\"float: left; margin-left: 10px;\"><input name=\"addFlag"+chaptercnt+"_"+divcnt+"\" type=\"checkbox\" id=\"addFlag"+chaptercnt+"_"+divcnt+"_"+ cnt+ "\" title=\"Add to Question Bank\" onclick=\"changeStatus('"+chaptercnt+"_"+divcnt+"_"+ cnt+ "')\" />"
    			+"<input type=\"hidden\" name=\"status"+chaptercnt+"_"+divcnt+"\" id=\"status"+chaptercnt+"_"+divcnt+"_"+cnt+"\" value=\"0\"/></span>"
    			
    			+"<a href=\"javascript:void(0)\" title=\"Add New Question\" onclick=\"getQuestion('"+cnt+"','"+callFrom+"','"+chaptercnt+"','"+divcnt+"')\" ><i class=\"fa fa-plus-circle\"></i></a>&nbsp;&nbsp; "
    			+"<a href=\"javascript:void(0)\" title=\"Remove Question\" onclick=\"removeQuestion('questionUl"+chaptercnt+"_"+divcnt+"_"+cnt+"')\" class=\"close-font\"></a>"
    			//+"<a href=\"javascript:void(0)\" class=\"add_lvl\" title=\"Remove Question\" onclick=\"removeOtherquestion('otherQuestionUl"+cnt+"')\" ></a>"
    			+"<input type=\"hidden\" name=\"questiontypename"+chaptercnt+"_"+divcnt+"\" value=\""+ cnt+"\" /></td></tr>" //+othrQtype
    			+"<tr><th></th><th style=\"text-align: right;\">Select Answer Type:</th><td><select name=\"ansType"+chaptercnt+"_"+divcnt+"\" class=\" form-control \" id=\"ansType"+chaptercnt+"_"+divcnt+"_"+cnt+"\" onchange=\"showAnswerTypeDiv(this.value, '"+cnt+"', 'answerType"+chaptercnt+"_"+divcnt+"_"+cnt+"', 'answerType1"+chaptercnt+"_"+divcnt+"_"+cnt+"', '"+chaptercnt+"_"+divcnt+"');\"> <option value=\"\">Select</option>" +anstype+"</select>"
    			+"</td><td><div id=\"anstypediv"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><div id=\"anstype9\">"
    			+"a) Option1&nbsp;<input type=\"checkbox\" value=\"a\" name=\"correct\" disabled=\"disabled\"/> b) Option2&nbsp;<input type=\"checkbox\" name=\"correct\" value=\"b\" disabled=\"disabled\"/><br />"
    			+"c) Option3&nbsp;<input type=\"checkbox\" value=\"c\" name=\"correct\" disabled=\"disabled\"/> d) Option4&nbsp;<input type=\"checkbox\" name=\"correct\" value=\"d\" disabled=\"disabled\"/><br />"
    			+"</div></div></td></tr>"
    			+aa
    			+"</table></li>";
    			//alert("questionCnt 5 ===> " + questionCnt);
    	ultag.innerHTML = a;
    		document.getElementById("questionLi"+chaptercnt+"_"+divcnt).appendChild(ultag);
    		//alert("questionCnt 6 ===> "+questionCnt);
    	
    	document.getElementById("weightage"+chaptercnt+"_"+divcnt+"_"+cnt).value = remainweight;
    	document.getElementById("hideweightage"+chaptercnt+"_"+divcnt+"_"+cnt).value = remainweight;
    	//alert("questionCnt 7 ===> " + questionCnt);
     }	
    	oldDivCnt = divcnt;
    }
    
    
    function removeQuestion(id){
    var row_skill = document.getElementById(id);
    if (row_skill && row_skill.parentNode
    		&& row_skill.parentNode.removeChild) {
    	row_skill.parentNode.removeChild(row_skill);
    }
    }
    
    function removeEditQuestion(id, delId, type) {
    	if(confirm('Are you sure, you want to delete this assessment?')) {
    	//alert("delId ===> "+  delId + " type ===> " + type);
    		var action = 'DeleteCourseAssessmentContentAndSubchapter.action?delId=' + delId + '&type=' + type;
    		getContent("", action);
    	
    		var row_skill = document.getElementById(id);
    		if (row_skill && row_skill.parentNode
    				&& row_skill.parentNode.removeChild) {
    			row_skill.parentNode.removeChild(row_skill);
    		}
    	}
    	}
    
    
    function getQuestoinContentType(cnt, callFrom, chaptercnt, divcnt){
    var val = 9;
    
    var a="";
    if(val == 8){
    	a="<tr id=\"answerType"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona"+chaptercnt+"_"+divcnt+"\" id=\"optiona"+cnt+"\" class=\" form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb"+chaptercnt+"_"+divcnt+"\" id=\"optionb"+cnt+"\" class=\" form-control \"/><input type=\"radio\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"b\" /></td></tr>"
    	+ "<tr id=\"answerType1"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc"+chaptercnt+"_"+divcnt+"\" id=\"optionc"+cnt+"\" class=\" form-control \"/> <input type=\"radio\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond"+chaptercnt+"_"+divcnt+"\" id=\"optiond"+cnt+"\" class=\" form-control \"/> <input type=\"radio\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"d\" /></td></tr>";
    
    }else if (val == 1 || val == 2 || val == 9) {
    	a="<tr id=\"answerType"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><th></th><th></th><td>a)&nbsp;<span id=\"aspan\"><input type=\"text\" name=\"optiona"+chaptercnt+"_"+divcnt+"\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/></span> <input type=\"checkbox\" value=\"a\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<span id=\"bspan\"><input type=\"text\" name=\"optionb"+chaptercnt+"_"+divcnt+"\" id=\"optionb"+cnt+"\" class=\" validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"b\" /></td></tr>"
    	+"<tr id=\"answerType1"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><th></th><th></th><td>c)&nbsp;<span id=\"cspan\"><input type=\"text\" name=\"optionc"+chaptercnt+"_"+divcnt+"\" id=\"optionc"+cnt+"\" class=\"validateRequired  form-control \"/></span> <input type=\"checkbox\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<span id=\"dspan\"><input type=\"text\" name=\"optiond"+chaptercnt+"_"+divcnt+"\" id=\"optiond"+cnt+"\" class=\" validateRequired form-control \"/></span> <input type=\"checkbox\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"d\" /></td></tr>";
    
     }else if (val == 6) {
    	a= "<tr id=\"answerType"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona"+chaptercnt+"_"+divcnt+"\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb"+chaptercnt+"_"+divcnt+"\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc"+chaptercnt+"_"+divcnt+"\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond"+chaptercnt+"_"+divcnt+"\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+ "\"  checked=\"checked\"  value=\"1\">True&nbsp;"
    		+ "<input type=\"radio\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+ "\" value=\"0\">False</td></tr>";
    
    }else if (val == 5) {
    	a= "<tr id=\"answerType"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><th></th><th></th><td><input type=\"hidden\" name=\"optiona"+chaptercnt+"_"+divcnt+"\" id=\"optiona"+cnt+"\"/><input type=\"hidden\" name=\"optionb"+chaptercnt+"_"+divcnt+"\" id=\"optionb"+cnt+"\"/><input type=\"hidden\" name=\"optionc"+chaptercnt+"_"+divcnt+"\" id=\"optionc"+cnt+"\"/><input type=\"hidden\" name=\"optiond"+chaptercnt+"_"+divcnt+"\" id=\"optiond"+cnt+"\"/><input type=\"radio\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+ "\"  checked=\"checked\"  value=\"1\">Yes&nbsp;"
    	+ "<input type=\"radio\" name=\"correct"+chaptercnt+"_"+divcnt+"_"+ cnt+ "\" value=\"0\">No</td></tr>";
    } else if(val == 13) {
		a="<tr id=\"answerType"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><th></th><th></th><td>a)&nbsp;<input type=\"text\" name=\"optiona"+chaptercnt+"_"+divcnt+"\" id=\"optiona"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" value=\"a\" name=\"correct"+ chaptercnt+"_"+divcnt+"_"+ cnt+"\" /> </td><td colspan=\"2\">b)&nbsp;<input type=\"text\" name=\"optionb"+chaptercnt+"_"+divcnt+"\" id=\"optionb"+cnt+"\" class=\"validateRequired form-control \"/><input type=\"radio\" name=\"correct"+ chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"b\" /></td></tr>"
		+ "<tr id=\"answerType1"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optionc"+chaptercnt+"_"+divcnt+"\" id=\"optionc"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"c\" /></td><td colspan=\"2\">d)&nbsp;<input type=\"text\" name=\"optiond"+chaptercnt+"_"+divcnt+"\" id=\"optiond"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"d\" /></td></tr>";
		+ "<tr id=\"answerType2"+chaptercnt+"_"+divcnt+"_"+cnt+"\"><th></th><th></th><td>c)&nbsp;<input type=\"text\" name=\"optione"+chaptercnt+"_"+divcnt+"\" id=\"optione"+cnt+"\" class=\"validateRequired form-control \"/> <input type=\"radio\" name=\"correct"+ chaptercnt+"_"+divcnt+"_"+ cnt+"\" value=\"e\" /></td><td colspan=\"2\">&nbsp;</td></tr>";
	
	} else {
    	a="";
    }
    return a;
    }
    
    var dialogEdit = '#SelectQueDiv';
    function openQuestionBank(count,callFrom,chaptercnt,divcnt) {
    	var ansType = document.getElementById('ansType'+chaptercnt+"_"+divcnt+'_'+count).value;
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Question Bank');
    	$("#modalInfo").show();
    	$.ajax({
			url : "SelectCourseAssessmentQuestion.action?count="+count+"&ansType="+ansType+"&chaptercnt="+chaptercnt+"&divCnt="+divcnt,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    
    function setQuestionInTextfield() {
    	var queid = document.getElementById("questionSelect").value;
    	var count = document.getElementById("count").value;
    	var divCnt = document.getElementById("divCnt").value;
    	var chaptercnt = document.getElementById("chaptercnt").value;
    	//alert("divCnt ---> "+divCnt);
        xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
                alert("Browser does not support HTTP Request");
                return;
        } else {
                var xhr = $.ajax({
                        url : "SetCourseAssessmentToTextfield.action?queid=" + queid + '&count=' +count + '&chaptercnt=' +chaptercnt + '&divCnt=' +divCnt,
                        cache : false,
                        success : function(data) {
                        	if(data != "" && data.trim().length > 0){
                        		var allData = data.split("::::");
                                document.getElementById("newquespan"+chaptercnt+"_"+divCnt+"_"+count).innerHTML = allData[0];
                                document.getElementById("answerType"+chaptercnt+"_"+divCnt+"_"+count).innerHTML = allData[1];
                                if(allData.length > 2){
                                	document.getElementById("answerType1"+chaptercnt+"_"+divCnt+"_"+count).style.display = 'table-row';
                                	document.getElementById("answerType1"+chaptercnt+"_"+divCnt+"_"+count).innerHTML = allData[2];
                                }else{
                                	document.getElementById("answerType1"+chaptercnt+"_"+divCnt+"_"+count).style.display = 'none';
                                }
                        	}
                        }
                });
        }
        
        $(".modal").hide();
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
    
    
    function validateScore(value1,weightageid,weightagehideid) {
    	var remainWeightage = document.getElementById(weightagehideid).value;
    	  
    	  if(parseFloat(value1) > parseFloat(remainWeightage)){
    			alert("Entered value greater than Weightage");
    			document.getElementById(weightageid).value = remainWeightage;
    		}else if(parseFloat(value1) <= 0 ){
    			alert("Invalid Weightage");
    			document.getElementById(weightageid).value = remainWeightage;
    		}  
    }
    
    
    function validateScoreEdit(value,weightageid,weightagehideid,totweightage) {
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
    
    
    function showContentDiv(chaptercnt,value) {
    	//alert("value ==> "+value);
    	//contentDiv assessmentDiv
    	document.getElementById("contentDiv"+chaptercnt+"_"+value).style.display = "block";
    	document.getElementById("addcontentDiv"+chaptercnt+"_"+value).innerHTML = "";
    	addNewTextarea(0,chaptercnt,value);
    	document.getElementById("questionLi"+chaptercnt+"_"+value).innerHTML = "";
    	document.getElementById("assessmentDiv"+chaptercnt+"_"+value).style.display = "none";
    }
    
    function showAssessmentDiv(chaptercnt,value) {
    	//alert("value ==> "+value);
    	if(confirm('Are you sure, you want to add assessment?')) {
    		document.getElementById("assessmentDiv"+chaptercnt+"_"+value).style.display = "block";
    		document.getElementById("addcontentDiv"+chaptercnt+"_"+value).innerHTML = "";
    		document.getElementById("questionLi"+chaptercnt+"_"+value).innerHTML = "";
    		getQuestion(0,'',chaptercnt, value);
    		document.getElementById("contentDiv"+chaptercnt+"_"+value).style.display = "none";
    	}
    }
    
    
    function addAndRemoveDescription(chapterCnt, subchapterCnt) {
    	var descStatus = document.getElementById("descStatus"+chapterCnt+"_"+subchapterCnt).value;
    	//alert("descStatus ===> " +descStatus+ " chapterCnt == "+ chapterCnt + " subchapterCnt == " + subchapterCnt);
    	if(descStatus == "0") {
    		//addDescSpan removeDescSpan subDescTR
    		//alert("descStatus0 ===> " +descStatus);
    		document.getElementById("subDescTR"+chapterCnt+"_"+subchapterCnt).style.display = "table-row";
    		document.getElementById("descStatus"+chapterCnt+"_"+subchapterCnt).value = "1";
    		//alert("descStatus 1 ===> " +descStatus);
    		//document.getElementById("editorrr"+chapterCnt+"_"+subchapterCnt).value = "";
    		document.getElementById("removeDescSpan"+chapterCnt+"_"+subchapterCnt).style.display = "block";
    		//alert("descStatus 2 ===> " +descStatus);
    		document.getElementById("addDescSpan"+chapterCnt+"_"+subchapterCnt).style.display = "none";
    		//alert("descStatus 3 ===> " +descStatus);
    	} else {
    		document.getElementById("subDescTR"+chapterCnt+"_"+subchapterCnt).style.display = "none";
    		document.getElementById("descStatus"+chapterCnt+"_"+subchapterCnt).value = "0";
    		//document.getElementById("editorrr"+chapterCnt+"_"+subchapterCnt).value = "";
    		document.getElementById("removeDescSpan"+chapterCnt+"_"+subchapterCnt).style.display = "none";
    		document.getElementById("addDescSpan"+chapterCnt+"_"+subchapterCnt).style.display = "block";
    	}
    }
    
    
    function viewCourseDetail(courseId, courseName) {
    	//alert("openQuestionBank id "+ id)
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html(''+courseName+'');
    	$("#modalInfo").show();
    	$.ajax({
			url : "ViewCourseDetails.action?courseId="+courseId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    	
    
    function closeForm() {
    	$("#divCDResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		url:'CourseDashboardData.action?dataType=C',
    		cache:false,
    		success:function(data){
    			$("#divCDResult").html(data);
    		}
    	});
    	
    }
    
    function isNumberKey(evt){
   	  var charCode = (evt.which) ? evt.which : event.keyCode;
   	  if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
   	     return false;
   	  }
   	  return true;
   	}
    
</script>
<%
    String op = (String) request.getAttribute("operation");
    if (op != null && op.equals("E")) {
    %>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value=" Edit Course" name="title" />
    </jsp:include> --%>
<%
    } else {
    %>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value=" Add Course" name="title" />
    </jsp:include> --%>
<%
    }
    %>
<section class="content">
    <div class="row jscroll">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                        <%
                            UtilityFunctions uF = new UtilityFunctions();
                            String anstype = (String) request.getAttribute("anstype");
                            List<List<String>> ansTypeList = (List<List<String>>) request.getAttribute("ansTypeList");
                            String tab = (String)request.getAttribute("tab");
                            String courseId = (String)request.getAttribute("courseId");
                            String crsPreface = (String)request.getAttribute("crsPreface");
                            List<List<String>> chapterList = (List<List<String>>) request.getAttribute("chapterList");
                            Map<String, List<List<String>>> hmSubchapterData = (Map<String, List<List<String>>>) request.getAttribute("hmSubchapterData");
                            Map<String, List<List<String>>> hmAssessmentData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentData");
                            Map<String, String> hmAssessTotWeight = (Map<String, String>) request.getAttribute("hmAssessTotWeight");
                            Map<String, List<List<String>>> hmContentData = (Map<String, List<List<String>>>) request.getAttribute("hmContentData");
                            Map<String, String> hmContentImg = (Map<String, String>) request.getAttribute("hmContentImg");
                            
                            %>
                        <div style="width: 100%; background-color: #868686; box-shadow:3px 4px 12px #616161;height: 80%;">
                        	<div class="pull-right">
								<a href="javascript:void(0);" onclick="closeForm()" class="close-font" style="margin-right:20px;"/>
							</div>
                            <div id="container1" style="width: 99%; float: left;height:98%;">
                                <ul>
                                    <li><a href="#course"><span>Course</span></a></li>
                                   <%if(courseId != null){ %>
                                    <li id="tabindex" ><a href="#index"><span>Index</span> </a></li>
                                    <% if(chapterList != null && !chapterList.isEmpty()){
                                        for(int i=0; i< chapterList.size(); i++){
                                        	%>
                                    <li id="tabchapter<%=i+1 %>"><a href="#chapter<%=i+1 %>"><span>Chapter <%=i+1 %> </span></a></li>
                                    <%
                                        }
                                        }
                                        %>
                                    <li id="tabchapter<%=uF.parseToInt(""+chapterList.size())+1 %>"><a href="#chapter<%=uF.parseToInt(""+chapterList.size())+1 %>"><span> + </span></a></li>
                                    <%-- <li  id="tabchapter"><a href="#chapter"><span>Chapter </span></a></li> --%>
                                    <%} %>
                                </ul>
                                <div style="border: solid 0px #ff0000; width:96%;" id="course">
                                    <!-- <div class="cat_heading"><h3>Tablar Data</h3></div> -->
                                    <!-- <div id="course1"> -->
                                    <s:form theme="simple" action="AddNewCourse" name="frm_AddNewCourse" id="frm_AddNewCourse" method="POST" cssClass="formcss" enctype="multipart/form-data">
                                        <s:hidden name="operation"></s:hidden>
                                        <s:hidden name="courseId"></s:hidden>
                                        <s:hidden name="assignToExist"></s:hidden>
                                        <%-- <s:hidden name="tab"></s:hidden> --%>
                                        <input type="hidden" name="tab" value="0"/>
                                        <div style="float: left; width: 100%;">
                                            <table border="0" class="table table_no_border" style="width: 85%;">
                                                <tr>
                                                    <td class="tdLabelheadingBg alignCenter" colspan="2">Course</td>
                                                </tr>
                                                <!-- <tr>
                                                    <td height="10px">&nbsp;</td>
                                                    </tr> -->
                                                <tr>
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Course Name:<sup>*</sup></td>
                                                    <td>
                                                        <s:textfield name="courseName" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Subject:<sup>*</sup></td>
                                                    <td>
                                                        <span>
                                                            <s:select theme="simple" name="courseSubject" headerKey="" headerValue="Select Subject" list="subjectList" 
                                                                listKey="subjectId" id="courseSubject" listValue="subjectName" cssClass="validateRequired form-control " value="subjectID"/>
                                                        </span>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Author:<sup>*</sup></td>
                                                    <td>
                                                        <s:textfield name="courseAuthor" cssClass="validateRequired form-control " required="true"></s:textfield>
                                                        <%-- <s:textarea name="trainingObjective" cssClass="validateRequired text-input" required="true" rows="5" cols="70"></s:textarea> --%>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Version:<sup>*</sup></td>
                                                    <td>
                                                        <s:textfield name="courseVersion" cssClass="validateRequired form-control " required="true"></s:textfield>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Preface:</td>
                                                    <td>
                                                        <textarea rows="3" cols="72" name="coursePreface" id="editor1" class="form-control "><%=uF.showData(crsPreface, "") %></textarea>
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div style="width: 100%; float: right;">
                                            
                                            <s:submit cssClass="btn btn-primary" cssStyle="width:170px; float:right; margin-right: 5px;" name="stepSubmit" value="Submit And Proceed"/>
                                            <%-- <%if(op != null && o<%-- <%if(op != null && op.equals("E")) { %> --%>
                                            <s:submit cssClass="btn btn-primary" cssStyle="width:120px; float:right; margin-right: 5px;" name="stepSave" value="Save And Exit"/>
                                            <%-- <% } %> --%>
                                        </div>
                                    </s:form>
                                    <!-- </div> -->
                                </div>
                                <% if(courseId != null) { %>
                                <div style="border: solid 0px #ff0000;width:96% " id="index">
                                    <s:form theme="simple" action="AddNewCourse" id="frmAddNewCourse1" method="POST" cssClass="formcss" enctype="multipart/form-data">
                                        <s:hidden name="operation"></s:hidden>
                                        <s:hidden name="courseId"></s:hidden>
                                        <s:hidden name="assignToExist"></s:hidden>
                                        <%-- <s:hidden name="tab"></s:hidden> --%>
                                        <input type="hidden" name="tab" value="1"/>
                                        <div style="float: left; width: 100%; min-height: 355px;">
                                            <table border="0" class="table table_no_border" style="width: 85%;">
                                                <tr>
                                                    <td class="tdLabelheadingBg alignCenter" colspan="2">Index</td>
                                                </tr>
                                                <!-- <tr>
                                                    <td height="10px">&nbsp;</td>
                                                    </tr> -->
                                                <% 
                                                    if(chapterList != null && !chapterList.isEmpty()) {
                                                    for(int i=0; i< chapterList.size(); i++){
                                                    List<String> innerList = chapterList.get(i);
                                                    %> 
                                                <tr>
                                                    <td class="txtlbl" height="10px" align="right"><%=i+1 %>)&nbsp;&nbsp;</td>
                                                    <td class="txtlbl" height="10px" colspan="2"><%=innerList.get(1) %></td>
                                                </tr>
                                                <%
                                                    if(hmSubchapterData != null && !hmSubchapterData.isEmpty()) {
                                                    	List<List<String>> subchapterList = hmSubchapterData.get(innerList.get(0));
                                                    	
                                                    		for(int j=0; subchapterList != null && !subchapterList.isEmpty() && j< subchapterList.size(); j++) {
                                                    			List<String> subinnerList = subchapterList.get(j);
                                                    %>  
                                                <tr>
                                                    <td class="txtlbl" height="10px">&nbsp;</td>
                                                    <td class="txtlbl" height="10px" colspan="2"><%=i+1 %>.<%=j+1 %>)&nbsp;&nbsp;&nbsp; <%=subinnerList.get(1) %> </td>
                                                </tr>
                                                <% } } %> 
                                                <% } } %>
                                                <% if(hmSubchapterData == null || hmSubchapterData.isEmpty()){ %>
                                                <tr>
                                                    <td class="txtlbl" colspan="3"><b>'No Content Added'</b></td>
                                                </tr>
                                                <% } %>
                                            </table>
                                        </div>
                                        <div style="width: 100%; float: right;">
                                            
                                            <s:submit cssClass="btn btn-primary" cssStyle="width:170px; float:right; margin-right: 5px;" name="stepSubmit" value="Submit And Proceed"/>
                                            <%-- <%if(op != null && o<%-- <%if(op != null && op.equals("E")) { %> --%>
                                            <s:submit cssClass="btn btn-primary" cssStyle="width:120px; float:right; margin-right: 5px;" name="stepSave" value="Save And Exit"/>
                                            <input type="button" value="Preview" style="float:right; margin-right: 5px;" class="btn btn-primary" name="preview" onclick="viewCourseDetail('<%=courseId %>','<%=request.getAttribute("courseName") %>');">
                                            <%-- <% } %> --%>
                                        </div>
                                    </s:form>
                                </div>
                                <% 
                                    String chapterCnt = (String) request.getAttribute("chapterCnt");
                                    if(chapterList != null && !chapterList.isEmpty()){
                                    for(int i=0; i< chapterList.size(); i++){
                                    	List<String> innerList = chapterList.get(i);
                                    	
                                    %>
                                <div style="border: solid 0px #ff0000;width:96%" id="chapter<%=i+1 %>">
                                    <form action="AddNewCourse.action" id="frmAddNewCourse2_<%=i+1 %>" method="POST" class="formcss" enctype="multipart/form-data">
                                        <s:hidden name="operation"></s:hidden>
                                        <s:hidden name="courseId"></s:hidden>
                                        <s:hidden name="assignToExist"></s:hidden>
                                        <%-- <s:hidden name="chapterCnt"></s:hidden> --%>
                                        <input type="hidden" name="chapterCnt" value="<%=i+1 %>"/>
                                        <input type="hidden" name="tab" value="<%=i+2 %>"/>
                                        <table border="0" class="table table_no_border" style="width: 85%;">
                                            <tr>
                                                <td class="tdLabelheadingBg alignCenter" colspan="2"> Chapter <%=i+1 %></td>
                                            </tr>
                                            <!-- <tr>
                                                <td height="10px">&nbsp;</td>
                                                </tr> -->
                                            <tr>
                                                <td class="txtlabel" style="vertical-align: top; text-align: right">Chapter Name:<sup>*</sup>
                                                    <input type="hidden" name="hidechapterid" value="<%=innerList.get(0) %>">
                                                </td>
                                                <td><input type="text" name="chapterName" class="validateRequired form-control " style="width: 450px;" value="<%=uF.showData(innerList.get(1), "") %>"/>
                                                    <%-- <s:textfield name="chapterName" cssClass="validateRequired text-input" cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="txtlabel" style="vertical-align: top; text-align: right">Chapter Description:</td>
                                                <td>
                                                    <textarea rows="3" cols="72" name="chapterDescription" id="editor<%=i+2 %>" class="form-control "><%=uF.showData(innerList.get(2), "") %></textarea>
                                                </td>
                                            </tr>
                                        </table>
                                        <div id="subchapterDiv<%=i+1 %>">
                                            <% 
                                                List<List<String>> subchapterList = null;
                                                if(hmSubchapterData != null && !hmSubchapterData.isEmpty()){
                                                	subchapterList = hmSubchapterData.get(innerList.get(0));
                                                	
                                                		for(int j=0; subchapterList != null && !subchapterList.isEmpty() && j< subchapterList.size(); j++) {
                                                			List<String> subinnerList = subchapterList.get(j);
                                                	%>
                                            <div id="subchapter<%=i+1 %>_<%=j+1 %>" style="float: left; width: 100%; margin-top: 25px;">
                                                <table border="0" class="table table_no_border" style="width: 85%;">
                                                    <tr>
                                                        <td class="txtlabel" style="vertical-align: top; text-align: right">Subchapter Name:<sup>*</sup>
                                                            <input type="hidden" name="hidesubchapterid<%=i+1 %>" value="<%=subinnerList.get(0) %>">
                                                            <input type="hidden" name="subchapterCount<%=i+1 %>" value="<%=j+1 %>"/>
                                                        </td>
                                                        <td><span style="float: left;">
                                                            <input type="text" name="subchapterName" class="validateRequired form-control " style="width: 450px;" value="<%=uF.showData(subinnerList.get(1), "") %>"/></span>
                                                            <input type="hidden" name="descStatus" id="descStatus<%=i+1 %>_<%=j+1 %>" value="<%=(subinnerList.get(2) == null || subinnerList.get(2).equals("")) ? "0" : "1" %>">
                                                            <span id="addDescSpan<%=i+1 %>_<%=j+1 %>" style="display: <%if(subinnerList.get(2) == null || subinnerList.get(2).equals("")) { %>block; <% } else { %>none; <% } %>; float: left; margin-left: 5px;">
                                                            <a class="add_lvl" href="javascript:void(0)" onclick="addAndRemoveDescription('<%=i+1 %>','<%=j+1 %>');"> Add Description </a>
                                                            </span> 
                                                            <span id="removeDescSpan<%=i+1 %>_<%=j+1 %>" style="display: <%if(subinnerList.get(2) == null || subinnerList.get(2).equals("")) { %>none; <% } else { %>block; <% } %>; float: left; margin-left: 5px;">
                                                            <a href="javascript:void(0)" onclick="addAndRemoveDescription('<%=i+1 %>','<%=j+1 %>');" title="Remove Description">
                                                            <%-- <img border="0" src="<%=request.getContextPath() %>/images1/icons/icons/close_button_icon.png"/>  --%>
                                                            <i class="fa fa-times-circle cross" aria-hidden="true" ></i>Remove Description
                                                            </a>
                                                            </span>
                                                            <%-- <span style="float: right;"><img border="0" src="<%=request.getContextPath() %>/images1/icons/icons/close_button_icon.png" title="Remove Subchapter" onclick="removeEditSubchapter('<%=i+1 %>','<%=j+1 %>','<%=subinnerList.get(0) %>', 'subchapter');"/></span> --%>
                                                            <span style="float: right;"><i class="fa fa-times-circle cross" aria-hidden="true" title="Remove Subchapter" onclick="removeEditSubchapter('<%=i+1 %>','<%=j+1 %>','<%=subinnerList.get(0) %>', 'subchapter');"></i></span>
                                                        </td>
                                                    </tr>
                                                    <% String strValid="form-control";
	                                                    /* if(subinnerList.get(2) == null || subinnerList.get(2).equals("")) { 
	                                                    	strValid=" form-control";
	                                                    } */
                                                    %>
                                                    <tr id="subDescTR<%=i+1 %>_<%=j+1 %>" style="display: <%if(subinnerList.get(2) == null || subinnerList.get(2).equals("")) { %>none; <% } else { %>table-row; <% } %>">
                                                        <td class="txtlabel" style="vertical-align: top; text-align: right">Subchapter Description:</td>
                                                        <td>
                                                            <textarea rows="3" cols="72" class="<%=strValid%>" name="subchapterDescription" id="editorrr<%=i+2 %>_<%=j+1 %>" class="form-control"><%=uF.showData(subinnerList.get(2), "") %></textarea>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="2">
                                                            <a href="javascript:void(0)" onclick="showContentDiv('<%=i+1 %>','<%=j+1 %>');"> +Content</a> &nbsp; 
                                                            <a href="javascript:void(0)" onclick="showAssessmentDiv('<%=i+1 %>','<%=j+1 %>');"> +Assessment</a>
                                                        </td>
                                                    </tr>
                                                </table>
                                                <%
                                                    String showContentDiv = "none";
                                                    if(hmContentData != null && !hmContentData.isEmpty()) {
                                                    	List<List<String>> contentList = hmContentData.get(subinnerList.get(0));
                                                    	if(contentList != null) { 
                                                    		showContentDiv = "block";
                                                    	} }
                                                    %>
                                                <div id="contentDiv<%=i+1 %>_<%=j+1 %>" style="float: left; width: 100%; display: <%=showContentDiv %>;">
                                                    <!-- border: 1px solid; -->
                                                    &nbsp;&nbsp;&nbsp;Add content here<br/>
                                                    <div style="float: left; width: 11%; min-height: 200px; margin: 5px 5px 5px 5px; background: none repeat scroll 0% 0% rgba(0, 0, 0, 0.04);">
                                                        <div style="width: 75%; margin: 15px;"> <a href="javascript:void(0)" class="text1" onclick="addNewTextarea('0','<%=i+1 %>','<%=j+1 %>');"></a></div>
                                                        <div style="width: 75%; margin: 15px;"> <a href="javascript:void(0)" class="image1" onclick="addNewImage('0','<%=i+1 %>','<%=j+1 %>');"></a></div>
                                                        <div style="width: 75%; margin: 15px;"> <a href="javascript:void(0)" class="video1" onclick="addNewVideo('0','<%=i+1 %>','<%=j+1 %>');"></a></div>
                                                        <div style="width: 75%; margin: 15px;"> <a href="javascript:void(0)" class="pdf1" onclick="addNewPDF('0','<%=i+1 %>','<%=j+1 %>');"></a></div>
                                                   <!-- ===start parvez date: 11-01-2023=== -->
                                                   		<div style="width: 75%; margin: 15px;"> <a href="javascript:void(0)" class="ppt1" onclick="addNewPPT('0','<%=i+1 %>','<%=j+1 %>');"></a></div>
                                                   <!-- ===end parvez date: 11-01-2023=== -->     
                                                        <div style="width: 75%; margin: 15px;"> <a href="javascript:void(0)" class="attach1" onclick="addNewAttachment('0','<%=i+1 %>','<%=j+1 %>');"></a></div>
                                                    </div>
                                                    <div id="addcontentDiv<%=i+1 %>_<%=j+1 %>" style="float: left; width: 75%; min-height: 300px; margin: 5px; box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.04) inset; border: 1px solid rgb(228, 228, 228); padding: 10px; background-color: white;">
                                                        <!-- border: 1px solid; -->
                                                        <%
                                                            if(hmContentData != null && !hmContentData.isEmpty()) {
                                                            	List<List<String>> contentList = hmContentData.get(subinnerList.get(0));
                                                            	if(contentList != null) { 
                                                            
                                                            	for(int k=0; contentList != null && k< contentList.size(); k++) {
                                                            		List<String> contentinnerList = contentList.get(k);
                                                            		if(contentinnerList != null) {
                                                            %>
                                                        <% if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("IMAGE")) { %>
                                                        <div id="imageDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
                                                            <table width="100%" class="table table_no_border">
                                                                <tr>
                                                                    <td><input type="hidden" value="<%=j+1 %>" name="imageSubchapterId<%=i+1 %>">
                                                                        <input type="hidden" name="hideimageid<%=i+1 %>" value="<%=contentinnerList.get(0) %>">
                                                                        Select Image:<sup>*<sup>
                                                                    </td>
                                                                    <td><input type="file" class=" validateRequired form-control " onchange="readImageURL(this,'<%=i+1 %>', '<%=j+1 %>','<%=k+1 %>','contentImgIframe');" id="contentImage<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentImage"> </td>
                                                                    <%-- <td valign="top"><img border="0" onclick="removeEditContent('imageDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Image" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"></td> --%>
                                                                    <td valign="top"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="removeEditContent('imageDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Image"></i></td>
                                                                    
                                                                </tr>
                                                                <tr>
                                                                    <td colspan="3"><img width="500" height="300" src="<%=hmContentImg.get(contentinnerList.get(0)) %>" id="contentImgIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>"></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Image Title:<sup>*<sup></td>
                                                                    <td colspan="2"><input type="text" class=" validateRequired form-control " name="imageTitle" value="<%=contentinnerList.get(7) != null ? contentinnerList.get(7) : "" %>"> </td>
                                                                </tr>
                                                            </table>
                                                        </div>
                                                        <% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("TEXT")) { %>
                                                        <div id="textareaDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
                                                            <table width="100%" class="table table_no_border">
                                                                <tr>
                                                                    <td><input type="hidden" value="<%=j+1 %>" name="textSubchapterId<%=i+1 %>">
                                                                        <input type="hidden" name="hidetextid<%=i+1 %>" value="<%=contentinnerList.get(0) %>">
                                                                        <textarea id="editorr<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" class="form-control " name="contentTextarea" cols="72" rows="3"><%=contentinnerList.get(1) %></textarea>
                                                                    </td>
                                                                    <%-- <td valign="top"><img border="0" onclick="removeEditContent('textareaDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Text" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"></td> --%>
                                                                    <td valign="top"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="removeEditContent('textareaDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Text"></i></td>
                                                                </tr>
                                                            </table>
                                                        </div>
                                                        <script>
                                                            //Replace the <textarea id="editor2"> with an CKEditor instance.
                                                            CKEDITOR.replace( 'editorr<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>', {
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
                                                        <% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("VIDEO")) { %>
                                                        <div id="videoDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
                                                            <table width="100%" class="table table_no_border">
                                                                <tr>
                                                                    <td><input type="hidden" value="<%=j+1 %>" name="videoSubchapterId<%=i+1 %>">
                                                                        <input type="hidden" name="hidevideoid<%=i+1 %>" value="<%=contentinnerList.get(0) %>">
                                                                        Select Video:<sup>*</sup>
                                                                    </td>
                                                                    <td><input type="file" id="contentVideo<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentVideo" class=" validateRequired form-control "></td>
                                                                    <%-- <td valign="top"><img border="0" onclick="removeEditContent('videoDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Text" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"></td> --%>
                                                                    <td valign="top"><i class="fa fa-times-circle cross" onclick="removeEditContent('videoDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Text" aria-hidden="true" ></i></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Video URL:<sup>*</sup></td>
                                                                    <td colspan="2"><input type="text" class=" validateRequired form-control " name="videoUrl" value="<%=contentinnerList.get(8) != null ? contentinnerList.get(8) : "" %>"> </td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Video Title:<sup>*</sup></td>
                                                                    <td colspan="2"><input type="text" class=" validateRequired form-control " name="imageTitle" value="<%=contentinnerList.get(7) != null ? contentinnerList.get(7) : "" %>"> </td>
                                                                </tr>
                                                            </table>
                                                        </div>
                                                        <% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("PDF")) { %>
                                                        <div id="pdfDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
                                                            <table width="100%" class="table table_no_border">
                                                                <tr>
                                                                    <td><input type="hidden" value="<%=j+1 %>" name="pdfSubchapterId<%=i+1 %>">
                                                                        <input type="hidden" name="hidepdfid<%=i+1 %>" value="<%=contentinnerList.get(0) %>">
                                                                        Select PDF:<sup>*</sup>
                                                                    </td>
                                                                    <td><input type="file" onchange="readPdfURL(this, '<%=i+1 %>', '<%=j+1 %>','<%=k+1 %>','contentPDFIframe');" id="contentPdf<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentPdf" class=" validateRequired form-control "> </td>
<%--                                                                    <td valign="top"><img border="0" onclick="removeEditContent('pdfDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Pdf" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"></td> --%>
                                                                    <td valign="top"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="removeEditContent('pdfDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Pdf"></i></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Pdf Title:<sup>*</sup></td>
                                                                    <td colspan="2"><input type="text" class=" validateRequired form-control " name="pdfTitle" value="<%=contentinnerList.get(7) != null ? contentinnerList.get(7) : "" %>"> </td>
                                                                </tr>
                                                                <tr>
                                                                	
                                                                    <td colspan="3"><iframe width="96%" height="300" src="<%=hmContentImg.get(contentinnerList.get(0)) %>#toolbar=0" id="contentPDFIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>"></iframe></td>
                                                                </tr>
                                                            </table>
                                                        </div>
                                                        <% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("PPT")) { %>
                                                        <div id="pptDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
                                                            <table width="100%" class="table table_no_border">
                                                                <tr>
                                                                    <td><input type="hidden" value="<%=j+1 %>" name="pptSubchapterId<%=i+1 %>">
                                                                        <input type="hidden" name="hidepptid<%=i+1 %>" value="<%=contentinnerList.get(0) %>">
                                                                        Select PPT:<sup>*</sup>
                                                                    </td>
                                                                    <td><input type="file" onchange="readPptURL(this, '<%=i+1 %>', '<%=j+1 %>','<%=k+1 %>','contentPPTIframe');" id="contentPPT<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentPPT" class=" validateRequired form-control "> </td>
<%--                                                                    <td valign="top"><img border="0" onclick="removeEditContent('pdfDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Pdf" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"></td> --%>
                                                                    <td valign="top"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="removeEditContent('pptDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This PPT"></i></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>PPT Title:<sup>*</sup></td>
                                                                    <td colspan="2"><input type="text" class=" validateRequired form-control " name="pptTitle" value="<%=contentinnerList.get(7) != null ? contentinnerList.get(7) : "" %>"> </td>
                                                                </tr>
                                                                <tr>
                                                                	
                                                                    <td colspan="3"><iframe width="96%" height="300" src="https://docs.google.com/gview?url=<%=hmContentImg.get(contentinnerList.get(0)) %>&embedded=true#toolbar=0" id="contentPPTIframe<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" sandbox="allow-scripts allow-same-origin" ></iframe></td>
                                                                </tr>
                                                            </table>
                                                        </div>
                                                        <% } else if (contentinnerList.get(2) != null &&  contentinnerList.get(2).equals("ATTACH")) { %>
                                                        <div id="attachDiv<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" style="float:left;width: 100%;margin-top: 15px;">
                                                            <table width="100%" class="table table_no_border">
                                                                <tr>
                                                                    <td><input type="hidden" value="<%=j+1 %>" name="attachSubchapterId<%=i+1 %>">
                                                                        <input type="hidden" name="hidedocsid<%=i+1 %>" value="<%=contentinnerList.get(0) %>">
                                                                        Select Attachment:<sup>*</sup>
                                                                    </td>
                                                                    <td><input type="file" id="contentAttach<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>" name="contentAttach" class="validateRequired form-control"></td>
                                                                    <%-- <td valign="top"><img border="0" onclick="removeEditContent('attachDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Text" src="<%=request.getContextPath()%>/images1/icons/icons/close_button_icon.png"></td> --%>
                                                                    <td valign="top"><i class="fa fa-times-circle cross" aria-hidden="true" onclick="removeEditContent('attachDiv','<%=i+1 %>','<%=j+1 %>','<%=k+1 %>', '<%=contentinnerList.get(0) %>', 'content');" title="Remove This Text" ></i></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Attachment Title:<sup>*</sup></td>
                                                                    <td colspan="2"><input type="text" name="attachTitle" class=" form-control " value="<%=contentinnerList.get(7) != null ? contentinnerList.get(7) : "" %>"> </td>
                                                                </tr>
                                                            </table>
                                                        </div>
                                                        <% } %>
                                                        <% } } } } %>	
                                                    </div>
                                                </div>
                                                <% 
                                                    String showAssessDiv = "none";
                                                    int assessSize = 0;
                                                    if(hmAssessmentData != null && !hmAssessmentData.isEmpty()) {
                                                    	List<List<String>> assessmentList = hmAssessmentData.get(subinnerList.get(0));
                                                    	
                                                    	if(assessmentList != null) { 
                                                    		showAssessDiv = "block";
                                                    		assessSize = assessmentList.size();
                                                    	} 
                                                    }
                                                    //System.out.println("assessSize ===> " + assessSize);
                                                    %>
                                                <div id="assessmentDiv<%=i+1 %>_<%=j+1 %>" style="float: left; width: 100%; min-height: 200px; display: <%=showAssessDiv %>;">
                                                    <!-- border: 1px solid; display: none; -->
                                                    <ul class="level_list ul_class">
                                                        <%-- <li style="margin-left: 100px;"><a href="javascript:void(0)" class="add_lvl" onclick="getQuestion('<%=assessSize %>','','<%=i+1 %>','<%=j+1 %>');">Add Assessment</a></li> --%>
                                                        <li id="questionLi<%=i+1 %>_<%=j+1 %>" style="margin-left: 100px;">
                                                            <%
                                                                if(hmAssessmentData != null && !hmAssessmentData.isEmpty()) {
                                                                	List<List<String>> assessmentList = hmAssessmentData.get(subinnerList.get(0));
                                                                	String totWeightage = hmAssessTotWeight.get(subinnerList.get(0));
                                                                		if(assessmentList != null) {
                                                                	for(int k=0; assessmentList != null && k < assessmentList.size(); k++) {
                                                                		List<String> assessinnerList = assessmentList.get(k);
                                                                		if(assessinnerList != null) {
                                                                %>
                                                            <ul id="questionUl<%=(i+1)+"_"+(j+1)+"_"+(k+1) %>">
                                                                <li>
                                                                    <table class="table table_no_border" width="100%">
                                                                        <tr>
                                                                            <th><%=i+1 %>.<%=j+1 %>.<%=k+1 %>)</th>
                                                                            <th width="17%" style="text-align: right;">Add Question:<sup>*</sup>
                                                                            </th>
                                                                            <td colspan="3">
                                                                                <input type="hidden" name="questionID<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(2)%>" />
                                                                                <input type="hidden" name="hideassessmentid<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(3) %>">
                                                                                <span id="newquespan<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" style="float: left;"><input type="hidden" name="hidequeid<%=i+1 %>_<%=j+1 %>" id="hidequeid<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="<%=assessinnerList.get(3) %>"/>
                                                                                <textarea rows="2" name="question<%=i+1 %>_<%=j+1 %>" id="question<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" class="validateRequired form-control " style="width: 330px;"><%=assessinnerList.get(0) %></textarea>
                                                                                </span>
                                                                                <span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt<%=i+1 %>_<%=j+1 %>" value="<%=k+1 %>"/>
                                                                                <input type="text" style="width: 35px !important;" name="weightage<%=i+1 %>_<%=j+1 %>" id="weightage<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" class="validateRequired form-control " value="<%=assessinnerList.get(1) %>" onkeypress="return isNumberKey(event)"onkeyup="validateScoreEdit(this.value,'weightage<%=i+1 %>_<%=j+1 %>_<%=k+1 %>','hideweightage<%=i+1 %>_<%=j+1 %>_<%=k+1 %>','<%=totWeightage %>')"/> <%-- <%=CGOMtotSQueWeight %> --%>
                                                                                <sup>*</sup>
                                                                                <input type="hidden" name="hideweightage<%=i+1 %>_<%=j+1 %>" id="hideweightage<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="<%=assessinnerList.get(1) %>" />
                                                                                </span><span style="float: left; margin-left: 10px;"><a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=k+1 %>','','<%=i+1 %>','<%=j+1 %>');" > +Q </a></span>
                                                                                <span id="checkboxspan<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" style="float: left; margin-left: 10px;">
                                                                               	
                                                                                <input name="addFlag<%=i+1 %>_<%=j+1 %>" type="checkbox" id="addFlag<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" title="Add to Question Bank" onclick="changeStatus('<%=i+1 %>_<%=j+1 %>_<%=k+1 %>')" 
                                                                                <%if(assessinnerList.get(10) != null && uF.parseToBoolean(assessinnerList.get(10))) { %>checked<%} %>/>
                                                                                <input type="hidden" id="status<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" name="status<%=i+1 %>_<%=j+1 %>" 
                                                                                <%if(assessinnerList.get(10) != null && uF.parseToBoolean(assessinnerList.get(10))) { %>value="1"<%}else { %>
                                                                                 value="0"<%} %> /></span>
                                                                                <a href="javascript:void(0)" title="Add New Question" onclick="getQuestion('<%=assessSize %>','','<%=i+1 %>','<%=j+1 %>')" ><i class="fa fa-plus-circle"></i></a>&nbsp;&nbsp;
                                                                                <a href="javascript:void(0)" title="Remove Question" onclick="removeEditQuestion('questionUl<%=i+1 %>_<%=j+1 %>_<%=k+1 %>','<%=assessinnerList.get(3) %>','assess')" class="close-font"></a>
                                                                                <input type="hidden" name="questiontypename<%=i+1 %>_<%=j+1 %>" value="<%=k+1 %>" />
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <th></th>
                                                                            <th style="text-align: right;">Select Answer Type:</th>
                                                                            <td>
                                                                                <select class="form-control " name="ansType<%=i+1 %>_<%=j+1 %>" id="ansType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" onchange="showAnswerTypeDiv(this.value, '<%=k+1 %>', 'answerType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>', 'answerType1<%=i+1 %>_<%=j+1 %>_<%=k+1 %>', '<%=i+1 %>_<%=j+1 %>');">
                                                                                    <option value="">Select</option>
                                                                                    <%
                                                                                        for(int b = 0; ansTypeList != null && !ansTypeList.isEmpty() && b< ansTypeList.size(); b++){
                                                                                         String checkStatus = "";
                                                                                         List<String> ansInnerList = ansTypeList.get(b);
                                                                                         
                                                                                         if(ansInnerList.get(0).equals(assessinnerList.get(9))){
                                                                                        	 checkStatus = "selected";
                                                                                         }
                                                                                         %>
                                                                                    <option value="<%=ansInnerList.get(0) %>" <%=checkStatus %>><%=ansInnerList.get(1) %></option>
                                                                                    <%
                                                                                        }
                                                                                        %>
                                                                                </select>
                                                                            </td>
                                                                            <td>
                                                                                <div id="anstypediv<%=i+1 %>_<%=j+1 %>_<%=k+1 %>">
                                                                                    <%
                                                                                        int getanstype = uF.parseToInt(assessinnerList.get(9));
                                                                                        if(getanstype == 1){ %>
                                                                                    <div id="anstype1">
                                                                                        a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/><br />
                                                                                        c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/><br />
                                                                                        <textarea rows="2" cols="50" name="textara" style="width:200px;" disabled="disabled" class=" form-control "></textarea>
                                                                                    </div>
                                                                                    <%}else if(getanstype == 2){ %>
                                                                                    <div id="anstype2">
                                                                                        a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/><br />
                                                                                        c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/><br />
                                                                                    </div>
                                                                                    <%}else if(getanstype == 3){ %>
                                                                                    <div id="anstype3">
                                                                                        <img border="0" style="padding: 5px 2px 0pt; height: 30px; width: 31%;" src="<%=request.getContextPath()%>/images1/scorebar_img.png"/>
                                                                                        <div id="marksscore" style="width:31%;">0 <span style="float:right;">100</span></div>
                                                                                    </div>
                                                                                    <%}else if(getanstype == 4){ %>
                                                                                    <div id="anstype4">
                                                                                        <input type="radio" name="excellent" value="Excellent" disabled="disabled"/>Excellent 
                                                                                        <input type="radio" name="verygood" value="Very Good" disabled="disabled"/>Very Good 
                                                                                        <input type="radio" name="average" value="Average" disabled="disabled"/>Average 
                                                                                        <input type="radio" name="good" value="Good" disabled="disabled"/>Good 
                                                                                        <input type="radio" name="poor" value="Poor" disabled="disabled"/>Poor 
                                                                                        <!-- <input type="radio" name="bad" value="Bad" disabled="disabled"/>Bad  -->
                                                                                    </div>
                                                                                    <%}else if(getanstype == 5){ %>
                                                                                    <div id="anstype5">
                                                                                        <input type="radio" name="yes" value="Yes" disabled="disabled"/>Yes &nbsp;
                                                                                        <input type="radio" name="no" value="No" disabled="disabled"/>No &nbsp;
                                                                                    </div>
                                                                                    <%}else if(getanstype == 6){ %>
                                                                                    <div id="anstype6">
                                                                                        <input type="radio" name="true" value="True" disabled="disabled"/>True &nbsp;
                                                                                        <input type="radio" name="false" value="False" disabled="disabled"/>False &nbsp;
                                                                                    </div>
                                                                                    <%}else if(getanstype == 7){ %>
                                                                                    <div id="anstype7">
                                                                                        <div class="divvalign">
                                                                                            <textarea rows="2" cols="50" name="singleopentext" style="width:200px" disabled="disabled" class="validateRequired form-control "></textarea>
                                                                                        </div>
                                                                                        <img border="0" style="padding: 5px 2px 0pt; height: 30px; width: 21%;" src="<%=request.getContextPath()%>/images1/scorebar_img.png"/>
                                                                                        <div id="markssingleopen" style="width:21%;">0 <span style="float:right;">100</span></div>
                                                                                    </div>
                                                                                    <%}else if(getanstype == 8){ %>
                                                                                    <div id="anstype8">
                                                                                        a) Option1&nbsp;<input type="radio" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="radio" name="correct" value="b" disabled="disabled"/><br />
                                                                                        c) Option3&nbsp;<input type="radio" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="radio" name="correct" value="d" disabled="disabled"/><br />
                                                                                    </div>
                                                                                    <%}else if(getanstype == 9){ %>
                                                                                    <div id="anstype9">
                                                                                        a) Option1&nbsp;<input type="checkbox" value="a" name="correct" disabled="disabled"/> b) Option2&nbsp;<input type="checkbox" name="correct" value="b" disabled="disabled"/><br />
                                                                                        c) Option3&nbsp;<input type="checkbox" value="c" name="correct" disabled="disabled"/> d) Option4&nbsp;<input type="checkbox" name="correct" value="d" disabled="disabled"/><br />
                                                                                    </div>
                                                                                    <%}else if(getanstype == 10){ %>
                                                                                    <div id="anstype10">
                                                                                        <div class="divvalign" style="vertical-align: text-top; float: left; width: 100%; margin-bottom: 10px;">
                                                                                            <span style="float: left; vertical-align: text-top; margin-right: 7px;">a)</span><span style="float: left; vertical-align: text-top; margin-right: 20px;"><textarea rows="2" cols="50" name="amultiopen" style="width:170px;" disabled="disabled" class=" validateRequired form-control "></textarea></span>
                                                                                            <span style="float: left; vertical-align: text-top; margin-right: 7px;">b)</span><span style="vertical-align: text-top; float: left"><textarea rows="2" cols="50" name="bmultiopen" style="width:170px;" disabled="disabled" class=" validateRequired form-control"></textarea></span>
                                                                                        </div>
                                                                                        <div class="divvalign" style="vertical-align: text-top; float: left; width: 100%;">
                                                                                            <span style="float: left; vertical-align: text-top; margin-right: 7px;">c)</span><span style="float: left; vertical-align: text-top; margin-right: 20px;"><textarea rows="2" cols="50" name="amultiopen" style="width:170px;" disabled="disabled" class=" validateRequired form-control"></textarea></span>
                                                                                            <span style="float: left; vertical-align: text-top; margin-right: 7px;">d)</span><span style="vertical-align: text-top; float: left"><textarea rows="2" cols="50" name="bmultiopen" style="width:170px;" disabled="disabled" class=" validateRequired form-control"></textarea></span>
                                                                                        </div>
                                                                                        <img border="0" style="padding: 5px 2px 0pt; height: 30px; width: 31%;" src="<%=request.getContextPath()%>/images1/scorebar_img.png"/>
                                                                                        <div id="marksmultipleopen" style="width:31%;">0 <span style="float:right;">100</span></div>
                                                                                    </div>
                                                                                    <%}else if(getanstype == 11){ %>						
                                                                                    <div id="anstype11">
                                                                                        <img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
                                                                                        <img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
                                                                                        <img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
                                                                                        <img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
                                                                                        <img border="0" style="padding: 5px 2px 0pt; height: 15px; width: 15px;" src="<%=request.getContextPath()%>/images1/star_img.png"/>
                                                                                    </div>
                                                                                    <%}else if(getanstype == 12){ %>		
                                                                                    <div id="anstype12">
                                                                                        <textarea rows="2" cols="50" name="singleopenwithoutmarks" style="width:200px;" disabled="disabled" class="validateRequired form-control "></textarea>
                                                                                    </div>
                                                                                    <%} %>
                                                                                </div>
                                                                            </td>
                                                                        </tr>
                                                                        <%
                                                                            if(getanstype == 1 || getanstype == 2 || getanstype == 8){ %>
                                                                        <tr id="answerType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td>a)&nbsp;<input type="text" name="optiona<%=i+1 %>_<%=j+1 %>" class=" validateRequired form-control " value="<%=assessinnerList.get(4)%>"/> <input type="radio" value="a" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>"
                                                                                <%if(assessinnerList.get(8).contains("a")){ %> checked="checked" <%} %> /> </td>
                                                                            <td colspan="2">b)&nbsp;<input type="text" name="optionb<%=i+1 %>_<%=j+1 %>" class=" validateRequired form-control " value="<%=assessinnerList.get(5)%>"/><input type="radio" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="b" 
                                                                                <%if(assessinnerList.get(8).contains("b")){ %> checked="checked" <%} %> /></td>
                                                                        </tr>
                                                                        <tr id="answerType1<%=i+1 %>_<%=j+1 %>_<%=k+1 %>">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td>c)&nbsp;<input type="text" name="optionc<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(6)%>" class=" validateRequired form-control "/> <input type="radio" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="c"
                                                                                <%if(assessinnerList.get(8).contains("c")){ %> checked="checked" <%} %> /></td>
                                                                            <td colspan="2">d)&nbsp;<input type="text" name="optiond<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(7)%>" class=" validateRequiredform-control "/> <input type="radio" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="d"
                                                                                <%if(assessinnerList.get(8).contains("d")){ %> checked="checked" <%} %> /></td>
                                                                        </tr>
                                                                        <%}else if(getanstype == 9){ %>
                                                                        <tr id="answerType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td>a)&nbsp;<input type="text" name="optiona<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(4)%>" class="validateRequired form-control "/> <input type="checkbox" value="a" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>"
                                                                                <%if(assessinnerList.get(8).contains("a")){ %> checked="checked" <%} %> /> </td>
                                                                            <td colspan="2">b)&nbsp;<input type="text" name="optionb<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(5)%>" class=" validateRequired form-control "/> <input type="checkbox" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="b" 
                                                                                <%if(assessinnerList.get(8).contains("b")){ %> checked="checked" <%} %> /></td>
                                                                        </tr>
                                                                        <tr id="answerType1<%=i+1 %>_<%=j+1 %>_<%=k+1 %>">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td>c)&nbsp;<input type="text" name="optionc<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(6)%>" class="validateRequired form-control "/> <input type="checkbox" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="c"
                                                                                <%if(assessinnerList.get(8).contains("c")){ %> checked="checked" <%} %> /></td>
                                                                            <td colspan="2">d)&nbsp;<input type="text" name="optiond<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(7)%>" class="validateRequired form-control "/> <input type="checkbox" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="d" 
                                                                                <%if(assessinnerList.get(8).contains("d")){ %> checked="checked" <%} %> /></td>
                                                                        </tr>
                                                                        <%}else if(getanstype == 6){ %>
                                                                        <tr id="answerType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td colspan="3"><input type="hidden" name="optiona<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(4)%>"/><input type="hidden" name="optionb<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(5)%>"/>
                                                                                <input type="hidden" name="optionc<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(6)%>"/><input type="hidden" name="optiond<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(7)%>"/>
                                                                                <input type="radio" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="1" <%if(assessinnerList.get(8).contains("1")){ %>checked="checked"<%} %>
                                                                                    >True&nbsp; <input type="radio" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="0" <%if(assessinnerList.get(8).contains("0")){ %> checked="checked" <%} %> >False
                                                                            </td>
                                                                        </tr>
                                                                        <tr id="answerType1<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" style="display: none">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td colspan="3"></td>
                                                                        </tr>
                                                                        <%}else if(getanstype == 5){ %>
                                                                        <tr id="answerType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td colspan="3"><input type="hidden" name="optiona<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(4)%>"/><input type="hidden" name="optionb<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(5)%>"/>
                                                                                <input type="hidden" name="optionc<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(6)%>"/><input type="hidden" name="optiond<%=i+1 %>_<%=j+1 %>" value="<%=assessinnerList.get(7)%>"/>
                                                                                <input type="radio" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="1"  <%if(assessinnerList.get(8).contains("1")){ %> checked="checked" <%} %>
                                                                                    >Yes&nbsp; <input type="radio" name="correct<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" value="0"  <%if(assessinnerList.get(8).contains("0")){ %> checked="checked" <%} %> >No
                                                                            </td>
                                                                        </tr>
                                                                        <tr id="answerType1<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" style="display: none">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td colspan="3"></td>
                                                                        </tr>
                                                                        <%} else { %>
                                                                        <tr id="answerType<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" style="display: none">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td colspan="3"></td>
                                                                        </tr>
                                                                        <tr id="answerType1<%=i+1 %>_<%=j+1 %>_<%=k+1 %>" style="display: none">
                                                                            <th></th>
                                                                            <th></th>
                                                                            <td colspan="3"></td>
                                                                        </tr>
                                                                        <%} %>
                                                                    </table>
                                                                </li>
                                                            </ul>
                                                            <% } } } } %>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </div>
                                            <% } } 
                                                int subChapterCnt = subchapterList != null ? subchapterList.size() : 0;
                                                %>
                                        </div>
                                        <div style="margin-left: 100px; float: left;">
                                            <a href="javascript:void(0)" onclick="addNewSubchapter('<%=i+1 %>','<%=subChapterCnt %>');"> +Add Subchapter</a> 
                                        </div>
                                        <div style="width: 100%; float: right;">
                                            
                                            <s:submit cssClass="btn btn-primary" cssStyle="width:170px; float:right; margin-right: 5px;" name="stepSubmit" value="Submit And Proceed"/>
                                            
                                            <s:submit cssClass="btn btn-primary" cssStyle="width:120px; float:right; margin-right: 5px;" name="stepSave" value="Save And Exit"/>
                                            <input type="button" value="Preview" style="float:right; margin-right: 5px;" class="btn btn-primary" name="preview" onclick="viewCourseDetail('<%=courseId %>','<%=request.getAttribute("courseName") %>');">
                                            <%-- <% } %> --%>
                                        </div>
                                    </form>
                                   </div>
                                <%
                                    }
                                    }
                                    %>
                                <div style="border: solid 0px #ff0000;width:96%" id="chapter<%=uF.parseToInt(""+chapterList.size())+1 %>">
                                  
                                    <form action="AddNewCourse.action" id="frmAddNewCourse3" method="POST" class="formcss" enctype="multipart/form-data">
                                        <s:hidden name="operation"></s:hidden>
                                        <s:hidden name="courseId"></s:hidden>
                                        <s:hidden name="assignToExist"></s:hidden>
                                        <input type="hidden" name="chapterCnt" value="<%=uF.parseToInt(""+chapterList.size())+1 %>"/>
                                        <input type="hidden" name="tab" value="<%=uF.parseToInt(""+chapterList.size())+2 %>"/>
                                        <table border="0" class="table table_no_border" style="width: 85%;">
                                            <tr>
                                                <td class="tdLabelheadingBg alignCenter" colspan="2">Chapter <%=uF.parseToInt(""+chapterList.size())+1 %></td>
                                            </tr>
                                            <tr>
                                                <td height="10px">&nbsp;</td>
                                            </tr>
                                            <tr>
                                                <td class="txtlabel" style="vertical-align: top; text-align: right">Chapter Name :<sup>*</sup></td>
                                                <td><input type="text" name="chapterName" class="validateRequired form-control " style="width: 450px;"/>
                                                    <%-- <s:textfield name="chapterName" cssClass="validateRequired text-input" cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="txtlabel" style="vertical-align: top; text-align: right">Chapter Description :</td>
                                                <td>
                                                    <textarea rows="3" cols="72" name="chapterDescription" id="editor<%=uF.parseToInt(""+chapterList.size())+2 %>" class="  form-control "></textarea>
                                                </td>
                                            </tr>
                                        </table>
                                        <div id="subchapterDiv<%=uF.parseToInt(""+chapterList.size())+1 %>"></div>
                                        <div style="margin-left: 100px; float: left;">
                                            <a href="javascript:void(0)" onclick="addNewSubchapter('<%=uF.parseToInt(""+chapterList.size())+1 %>','0');"> +Add Subchapter</a> 
                                        </div>
                                        <div style="width: 100%; float: right;">
                                            
                                            <s:submit cssClass="btn btn-primary" cssStyle="width:170px; float:right; margin-right: 5px;" name="stepSubmit" value="Submit & Proceed"/>
                                            <%-- <%if(op != null && o<%-- <%if(op != null && op.equals("E")) { %> --%>
                                            <s:submit cssClass="btn btn-primary" cssStyle="width:120px; float:right; margin-right: 5px;" name="stepSave" value="Save & Exit"/>
                                            <input type="button" value="Preview" style="float:right; margin-right: 5px;" class="btn btn-primary" name="preview" onclick="viewCourseDetail('<%=courseId %>','<%=request.getAttribute("courseName") %>');">
                                            <%-- <% } %> --%>
                                        </div>
                                    </form>
                                </div>
                                <% } %>
                            </div>
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
    </div>
</section>
<div id="debug"></div>
<div id="SelectQueDiv"></div>

<script>
	$(document).ready(function() {			
		$('#container1').tabs({
			fxAutoHeight : true
		});
		$('#container1').tabs( "option",'active', <%=request.getAttribute("tab")%>);
	});
	$(function() {
		$("input[type='submit']").click(function(){
			for ( instance in CKEDITOR.instances ) {
	            CKEDITOR.instances[instance].updateElement();
	        }
			//$(".validateRequired").prop('required',true);
			$("#"+this.form.id).find('.validateRequired').filter(':hidden').prop('required',false);
			$("#"+this.form.id).find('.validateRequired').filter(':visible').prop('required',true);
		});
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

    // Replace the <textarea id="editor1"> with an CKEditor instance.
    if(document.getElementById("editor1")) {
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
    }
    <% if(courseId != null) { %>
    <% if(chapterList != null && !chapterList.isEmpty()){
        for(int i=0; i< chapterList.size(); i++){
        	List<String> innerList = chapterList.get(i);
        	%>
    // Replace the <textarea id="editor2"> with an CKEditor instance.
    if(document.getElementById("editor<%=i+2 %>")) {
	    CKEDITOR.replace( 'editor<%=i+2 %>', {
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
    <% if(hmSubchapterData != null && !hmSubchapterData.isEmpty()){
        List<List<String>> subchapterList = hmSubchapterData.get(innerList.get(0));
        	for(int j=0; subchapterList != null && j< subchapterList.size(); j++){
        		List<String> subinnerList = subchapterList.get(j);
        %>
    // Replace the <textarea id="editor2"> with an CKEditor instance.
     if(document.getElementById("editorrr<%=i+2 %>_<%=j+1 %>")) {
	    CKEDITOR.replace( 'editorrr<%=i+2 %>_<%=j+1 %>', {
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
    <% } } %>
    <% } } %>
    // Replace the <textarea id="editor2"> with an CKEditor instance.
     if(document.getElementById("editor<%=uF.parseToInt(""+chapterList.size())+2 %>")) {
	    CKEDITOR.replace( 'editor<%=uF.parseToInt(""+chapterList.size())+2 %>', {
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
    
      <% } %>		
      
      var submitActor = null;
      var submitButtons = $('form').find('input[type=submit]').filter(':visible');
     
      $("form").bind('submit',function(event) { 
    	  event.preventDefault();
    	  if (null === submitActor) {
    		  /* submitActor = submitButtons[0]; */
	          submitActor = event.originalEvent.submitter.id;
	      }
     	
    	  if((fileId !== undefined && fileId.length !== 0) || ($('input[name = contentVideo]').val() !== undefined) || ($('input[name = contentAttach]').val() !== undefined) || ($('input[name = contentPPT]').val() !== undefined)){
    		  var imgId;
    		  var pdfId;
  			for(var i=0; i < fileId.length; i++){
  				
  				var y = fileId[i].toString();
  				if(y.includes("contentImgIframe")){
  					imgId = fileId[i];
  				}else if(y.includes("contentPdfIframe")){
  					pdfId = fileId[i];
  				}
  			}
  			
  			var form_data = new FormData($("#"+this.id)[0]);
  			if($('#'+imgId).attr('src') !== undefined){
  				form_data.append("contentImage", $('img').attr('src'));
  			}
  			if($('#'+pdfId).attr('src') !== undefined){
  				form_data.append("contentPdf", $('img').attr('src'));
  			}
  			if(($('input[name = contentVideo]').val() !== undefined)){
  				form_data.append("contentVideo",$('input[name = contentVideo]').val());
  			}
  			if(($('input[name = contentAttach]').val() !== undefined)){
  				form_data.append("contentAttach",$('input[name = contentAttach]').val());
  			}
  			if(($('input[name = contentPPT]').val() !== undefined)){
  				form_data.append("contentPPT",$('input[name = contentPPT]').val());
  			}
  			
  			var stepSubmit=$('input[name = stepSubmit]').val();
   			var stepSave=$('input[name = stepSave]').val();
			var submit = submitActor;
			
			if(submit != null && submit == "stepSave") { 
	   			form_data.append("stepSave",$('input[name = stepSave]').val());
			} else if(submit != null && submit == "stepSubmit"){
	   			form_data .append("stepSubmit",$('input[name = stepSubmit]').val());
			}
  						
  			$("#divCDResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
  			$.ajax({
  			   	url: "AddNewCourse.action",
  			   	type: 'POST',
  			   	data: form_data,
  			   	contentType: false,
  		  	    cache: false,
  		  	    processData: false,
  			   	success: function(result){
  			   	    $("#divCDResult").html(result);
  			   	},
  			   	error : function(result) {
  				   	$.ajax({
  						url: "CourseDashboardData.action?fromPage=LD&dataType=C",
  						cache: true,
  						success: function(result){
  							 $("#divCDResult").html(result);
  						}	
  					});
  			   	}
  			});
  			
  		} else{
  			var form_data = $("#"+this.id).serialize();
  			var stepSubmit=$('input[name = stepSubmit ]').val();
  		   	var stepSave=$('input[name = stepSave ]').val();
  					/* var submit = submitActor.name; */
  			var submit = submitActor;
  	         
  			if(submit != null && submit == "stepSave") {
  	       		form_data = form_data +"&stepSave=Save And Exit";
  			} else if(submit != null && submit == "stepSubmit"){
  	       		form_data = form_data +"&stepSubmit=Submit And Proceed";
  			}
  				
  			$("#divCDResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
  	   	    $.ajax({
  	   	    	url: "AddNewCourse.action",
  	   	     	type: 'POST',
  	   	     	data: form_data,
  	   	     	success: function(result){
  	   	     		$("#divCDResult").html(result);
  	   	     	},
  	   	     	error : function(result) {
  	   	     		$.ajax({
	  			   	    url: "CourseDashboardData.action?fromPage=LD&dataType=C",
	  			   	    cache: true,
	  			   	    success: function(result){
	  				     	$("#divCDResult").html(result);
	  				    }	
  		   	     	});
  	   	     	}
  	   	     });
  		}
     	     
    });
   	 
   	submitButtons.click(function(event) {
        submitActor = this;
    });

</script>
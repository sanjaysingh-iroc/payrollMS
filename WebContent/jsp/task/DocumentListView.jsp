<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<g:compress>
<%--  	<script src="<%= request.getContextPath()%>/js/jquery-1.7.2.min.js" type="text/javascript"></script> --%>
</g:compress>   


<script src="<%= request.getContextPath()%>/js/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath() %>/scripts/EasyTree/jquery.easytree.js"></script> 
<script src="<%= request.getContextPath()%>/scripts/EasyTree/jquery.easytree.min.js"></script>
<link href="<%=request.getContextPath() %>/scripts/EasyTree/skin-win8/ui.easytree.css" rel="stylesheet"/>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/jquery-ui-1.8.6.custom.min.js"> </script>

<%-- <script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery.gdocsviewer.min.js"> </script> --%>

<style>
	.tdBorderLine{
		padding: 8px;
		border-top: 1px solid #f4f4f4;
	} 
	
	.formcss select,.formcss button{
	width: 125px !important;
	} 
	
</style>

<script type="text/javascript">
var timerId;
	function getFolderAndFiles(strId, type,strSearch) {
		//alert("strId ===>> " + strId + " type ===>> " + type);
		document.getElementById("folderAndFileDiv").innerHTML = "";
		
		$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#folderAndFileDiv");
		getContent('folderAndFileDiv', 'ProDocumentList.action?strId='+strId+'&type='+type+'&strSearchDoc='+strSearch);
		timerId = window.setInterval(function() {
			getFolderCount(); 
		}, 400);
	}
</script>

<!-- <link href="http://www.jqueryscript.net/css/jquerysctipttop.css" rel="stylesheet" type="text/css"/> -->

<script>  
	jQuery(document).ready(function() {
		//jQuery("#formProjectDocuments").validationEngine();
		var strSearch = document.getElementById("strSearchDoc").value;
		//alert("strSearch======>"+strSearch);
		getFolderAndFiles('0', 'C',strSearch);
	});
	
	function getFolderCount() {
		var nFolder = document.getElementById("folderCnt").value;
		var nSubFolder = document.getElementById("subFolderCnt").value;
		var nFiles = document.getElementById("fileCnt").value;
		//alert("nFolder======>"+nFolder);
		//alert("nSubFolder=======>"+nSubFolder);
		//alert("nFiles=======>"+nFiles);
		if(document.getElementById("spanFolderId")) {
			document.getElementById("spanFolderId").innerHTML = ""+parseInt(nFolder);
		}
		document.getElementById("spanSubFolderId").innerHTML = ""+parseInt(nSubFolder);
		document.getElementById("spanFilesId").innerHTML = ""+parseInt(nFiles);
		
		clearTimeout(timerId);
		timerId = null;
	}
	
	function openCloseDocs(folderCnt,subFolderSize,docFolderSize) {
		if(document.getElementById("hideFolder_"+folderCnt)) {
			var status = document.getElementById("hideFolder_"+folderCnt).value;
			if(status == '0') {
				for(var i=0; i< parseInt(subFolderSize);i++) {
					document.getElementById("folderTR_"+folderCnt+"_"+i).style.display = "block";
				}
				for(var i=0; i< parseInt(docFolderSize);i++) {
					document.getElementById("docFolderTR_"+folderCnt+"_"+i).style.display = "block";
				}
				
				document.getElementById("hideFolder_"+folderCnt).value = '1';
				if(document.getElementById("FDDownarrowSpan"+folderCnt)) {
					document.getElementById("FDDownarrowSpan"+folderCnt).style.display = 'none';
					document.getElementById("FDUparrowSpan"+folderCnt).style.display = 'block';
				}
			} else {
				for(var i=0; i< parseInt(subFolderSize);i++) {
					document.getElementById("folderTR_"+folderCnt+"_"+i).style.display = "none";
				}
				for(var i=0; i< parseInt(docFolderSize);i++){
					document.getElementById("docFolderTR_"+folderCnt+"_"+i).style.display = "none";
				}
				
				document.getElementById("hideFolder_"+folderCnt).value = '0';
				if(document.getElementById("FDDownarrowSpan"+folderCnt)) {
					document.getElementById("FDDownarrowSpan"+folderCnt).style.display = 'block';
					document.getElementById("FDUparrowSpan"+folderCnt).style.display = 'none';
				}
			}
		}
	}
	
	
	function openCloseDocs1(folderCnt,subFolderCnt,subDocSize) {
		if(document.getElementById("hideFolder_"+folderCnt+"_"+subFolderCnt)) {
			var status = document.getElementById("hideFolder_"+folderCnt+"_"+subFolderCnt).value;
			if(status == '0') {
				for(var i=0; i< parseInt(subDocSize);i++) {
					document.getElementById("folderTR_"+folderCnt+"_"+subFolderCnt+"_"+i).style.display = "block";
				}
				
				document.getElementById("hideFolder_"+folderCnt+"_"+subFolderCnt).value = '1';
				if(document.getElementById("FDDownarrowSpan"+folderCnt+"_"+subFolderCnt)) {
					document.getElementById("FDDownarrowSpan"+folderCnt+"_"+subFolderCnt).style.display = 'none';
					document.getElementById("FDUparrowSpan"+folderCnt+"_"+subFolderCnt).style.display = 'block';
				}
			} else {
				for(var i=0; i< parseInt(subDocSize);i++) {
					document.getElementById("folderTR_"+folderCnt+"_"+subFolderCnt+"_"+i).style.display = "none";
				}
				
				document.getElementById("hideFolder_"+folderCnt+"_"+subFolderCnt).value = '0';
				if(document.getElementById("FDDownarrowSpan"+folderCnt+"_"+subFolderCnt)) {
					document.getElementById("FDDownarrowSpan"+folderCnt+"_"+subFolderCnt).style.display = 'block';
					document.getElementById("FDUparrowSpan"+folderCnt+"_"+subFolderCnt).style.display = 'none';
				}
			}
		}
	}
	
// ***************************************** Start Add New Folder *****************************************************

	function projectDocFact(clientId, proId, docName, proFolderId,type,filePath,fileDir) {
		removeLoadingDiv("the_div");
		var dialogEdit = '#projectDocFactDiv';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,  
			height : 550,
			width : '70%', 
			modal : true,
			title : 'View '+docName+' file',
			open : function() {
				var xhr = $.ajax({
					url : "ProjectDocumentFact.action?clientId="+clientId+"&proId="+proId+"&folderName="+docName+"&proFolderId="+proFolderId+"&type="+type+"&filePath="+encodeURIComponent(filePath)+"&fileDir="+encodeURIComponent(fileDir),
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	}
	
	
	function checkStatus(strCheckBox, hideId) {
		if(strCheckBox.checked == true) {
			document.getElementById(hideId).value = '1';
		} else {
			document.getElementById(hideId).value = '0';
		}
	}
	
	function clearData(elementId){
		var val = document.getElementById(elementId).value;
		if(val=="Description" || val=="Folder Name" || val=="Document Scope" || val=="Sub Folder Name" ){
			document.getElementById(elementId).value = '';
			document.getElementById(elementId).style.color = '';
		}
	}
	
	function fillData(elementId, num){
		if(document.getElementById(elementId).value=='' && num==1){
			document.getElementById(elementId).value="Description";
			document.getElementById(elementId).style.color = 'gray';
		} else if(document.getElementById(elementId).value=='' && num==2){
			document.getElementById(elementId).value="Folder Name";
			document.getElementById(elementId).style.color = 'gray';
		} else if(document.getElementById(elementId).value=='' && num==3){
			document.getElementById(elementId).value="Document Scope";
			document.getElementById(elementId).style.color = 'gray';
		} else if(document.getElementById(elementId).value=='' && num==4){
			document.getElementById(elementId).value="Sub Folder Name";
			document.getElementById(elementId).style.color = 'gray';
		}
	}
	
	
	function addNewFolder(tableName, rowCountName, tableId) {
		if(document.getElementById("divSubmitCancel_"+tableId)) {
			document.getElementById("divSubmitCancel_"+tableId).style.display = "block";
		}
		var fdCnt = document.getElementById(rowCountName).value;
		var cnt=(parseInt(fdCnt)+1);
	   // alert("addNewFolder cnt ===>> " + cnt);
	   var orgCategories = document.getElementById("orgCategories").value;
	   var orgProjects = document.getElementById("orgProjects").value;
	   var orgResources = document.getElementById("orgResources").value;
	   var orgPoc = document.getElementById("orgSPOC").value;
	   
	   if(document.getElementById("submitDiv_0")) {
			document.getElementById("submitDiv_0").style.display = 'block';
		}
	   
	    var table = document.getElementById(tableName);
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    row.id="folderTR"+cnt;
	    var cell0 = row.insertCell(0);
	    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
	    cell0.setAttribute('style','width: 550px;');
	    cell0.setAttribute('class','tdBorderLine');
	    cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"folderTRId\" id=\"folderTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strFolderName\" id=\"strFolderName"+cnt+"\" style=\"width:200px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 2);\" value=\"Folder Name\"/></span>"
        +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewFolder('"+tableName+"', '"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Create New Folder \">&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolder('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Folder\">&nbsp;</a></span>"
        +"<span style=\"float:left; width: 100%; margin-top: 5px;\"><textarea rows=\"3\" name=\"strFolderDescription\" id=\"strFolderDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        +"<span style=\"float:left; width: 100%;\"><a href=\"javascript:void(0);\" style=\"margin: 0px 0px 0px 50px;\" onclick=\"addNewSubFolder('"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"', '"+tableId+"');\"> +Add Folder</a>"
        +"<a href=\"javascript:void(0);\" style=\"margin: 0px 0px 0px 15px;\" onclick=\"addNewFolderDocs('"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"', '"+tableId+"');\"> +Add Document</a></span>";
        
	    var cell1 = row.insertCell(1);
	    cell1.setAttribute("valign","top");
	    cell1.setAttribute('class','tdBorderLine');
	    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeFolder\" id=\"proCategoryTypeFolder"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'orgCatagorySpan"+cnt+"', 'orgProjectSpan"+cnt+"','folderSharingType"+cnt+"');\">"+
	    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
	    
	    var cell2 = row.insertCell(2);
	    cell2.setAttribute("valign","top");
	    cell2.setAttribute('class','tdBorderLine');
	    cell2.innerHTML = "<span id=\"orgCatagorySpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"strOrgCategory"+cnt+"\" id=\"strOrgCategory"+cnt+"\" style=\"width:100px !important;\">"+
	     	""+orgCategories+"</select></span><span id=\"orgProjectSpan"+cnt+"\" style=\"float: left;\"><select name=\"strOrgProject"+cnt+"\" id=\"strOrgProject"+cnt+"\" style=\"width:100px !important;\">"+
	     	""+orgProjects+"</select></span>";
		
	    var cell3 = row.insertCell(3);
	    cell3.setAttribute("valign","top");
	    cell3.setAttribute('class','tdBorderLine');
	    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderSharingType\" id=\"folderSharingType"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, 'orgResourceSpan"+cnt+"')\">"
		    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
		    +"<span id=\"orgResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgResources"+cnt+"\" id=\"strOrgResources"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgResources+"</select></span>"
		    +"</div>"
		    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('orgPocSpan"+cnt+"','"+cnt+"')\">share customer</a></span>"
		    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"
		    +"<span id=\"orgPocSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgPoc"+cnt+"\" id=\"strOrgPoc"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgPoc+"</select></span>"
		    +"</div>";
		
	    var cell4 = row.insertCell(4);
	    cell4.setAttribute("valign","top");
	    cell4.setAttribute('class','tdBorderLine');
	    cell4.setAttribute('nowrap','nowrap');
	    cell4.innerHTML = "<span id=\"isFolderEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderEdit\" id=\"isFolderEdit"+cnt+"\" value=\"1\">"+
	    	"<input type=\"checkbox\" name=\"folderEdit\" id=\"folderEdit"+cnt+"\" onclick=\"checkStatus(this, 'isFolderEdit"+cnt+"');\" checked/> Edit</span>"+
	    	"<span id=\"isFolderDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDelete\" id=\"isFolderDelete"+cnt+"\" value=\"0\">"+
	    	"<input type=\"checkbox\" name=\"folderDelete\" id=\"folderDelete"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDelete"+cnt+"');\"/> Delete</span>";
		
	    document.getElementById(rowCountName).value = cnt;
	    $("#strOrgResources"+cnt).multiselect().multiselectfilter();
	    $("#strOrgPoc"+cnt).multiselect().multiselectfilter();
	}
	
	
	function cancelFolderAndDocsBlock(tableName) {
		if(confirm('Are you sure, you want to close this form?')) {
			/* var trIndex = document.getElementById("folderTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex); */
		    
		    var table = document.getElementById(tableName);
		    var rowCnt = table.rows.length;
		    for(var a=1; a< rowCnt; a++) {
		    	document.getElementById(tableName).deleteRow(a);
		    }
		    var rowCount = table.rows.length;
		    if(parseInt(rowCount) == 1) {
			    if(document.getElementById("submitDiv_0")) {
			    	document.getElementById("submitDiv_0").style.display = 'none';
				}
			    if(document.getElementById("folderTR0")) {
			    	document.getElementById("folderTR0").style.display = 'none';
			    }
		    }
		}
	}
	
	function deleteFolder(count, tableName) {
		if(confirm('Are you sure, you want to delete this folder?')) {
			var trIndex = document.getElementById("folderTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		    
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    if(parseInt(rowCount) == 1) {
			    if(document.getElementById("submitDiv_0")) {
			    	document.getElementById("submitDiv_0").style.display = 'none';
				}
			    if(document.getElementById("folderTR0")) {
			    	document.getElementById("folderTR0").style.display = 'none';
			    }
		    }
		}
	}
	
	function changeCategoryType(val, categorySpan, projectSpan,sharingTypeId) {
		if(val == '1') {
			document.getElementById(categorySpan).style.display = "none";
			document.getElementById(projectSpan).style.display = "block";
			 
			var theSelect = document.getElementById(sharingTypeId);
		    var option = document.createElement("option");
			option.value = "1";
		    option.text = "Private Team";
		    theSelect.add(option,1);
			
		} else if(val == '2') {
			document.getElementById(categorySpan).style.display = "block";
			document.getElementById(projectSpan).style.display = "none";
			
			var theSelect = document.getElementById(sharingTypeId);
			theSelect.remove(1);
			
		}
	}
	
	
	function addNewSubFolder(folderTRId, rwIndex, tableName, rowCountName, tableId) { 
		//alert("addNewSubFolder rwIndex ===>> " + rwIndex);
		  if(document.getElementById("divSubmitCancel_"+tableId)){
			document.getElementById("divSubmitCancel_"+tableId).style.display = "block";
		  }
			var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);
		   // alert("addNewFolder cnt ===>> " + cnt);
		   var orgCategories = document.getElementById("orgCategories").value;
		   var orgProjects = document.getElementById("orgProjects").value;
		   var orgResources = document.getElementById("orgResources").value;
		   var orgPoc = document.getElementById("orgSPOC").value;
		   
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    row.id="folderTR"+cnt;
		    var cell0 = row.insertCell(0);
		    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"subFolderTRId"+folderTRId+"\" id=\"subFolderTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strSubFolderName"+folderTRId+"\" id=\"strSubFolderName"+cnt+"\" style=\"width:200px !important; color: gray;\"  onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 4);\" value=\"Sub Folder Name\"/></span>"
		    +"<span style=\"float:left;\"><a href=\"javascript:void(0);\" onclick=\"addNewSubFolder('"+folderTRId+"', '"+rwIndex+"', '"+tableName+"' ,'"+rowCountName+"', '"+tableId+"');\" class=\"fa fa-fw fa-plus\" title=\"Create New Folder\">&nbsp;</a>"
		    +"<a href=\"javascript:void(0)\" onclick=\"deleteSubFolder('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Folder\">&nbsp;</a></span>"
		    
	        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strSubFolderDescription"+folderTRId+"\" id=\"strSubFolderDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<a href=\"javascript:void(0);\" style=\"float:left; width:86%; margin: 0px 0px 0px 70px;\" onclick=\"addNewSubFolderDocs('"+cnt+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"', '"+tableId+"');\"> +Add Document</a>";
	        
		    var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeSubFolder"+folderTRId+"\" id=\"proCategoryTypeSubFolder"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'orgCatagorySubFolderSpan"+cnt+"', 'orgProjectSubFolderSpan"+cnt+"','SubFolderSharingType"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"orgCatagorySubFolderSpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"strOrgCategorySubFolder"+folderTRId+"_"+cnt+"\" id=\"strOrgCategorySubFolder"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+orgCategories+"</select></span><span id=\"orgProjectSubFolderSpan"+cnt+"\" style=\"float: left;\"><select name=\"strOrgProjectSubFolder"+folderTRId+"_"+cnt+"\" id=\"strOrgProjectSubFolder"+cnt+"\" style=\"width:100px !important;\">"+
		     	""+orgProjects+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubFolderSharingType"+folderTRId+"\" id=\"SubFolderSharingType"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, 'orgSubFolderResourceSpan"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
			    +"<span id=\"orgSubFolderResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgResourcesSubFolder"+folderTRId+"_"+cnt+"\" id=\"strOrgResourcesSubFolder"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgResources+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('orgSubFolderPocSpan"+cnt+"','"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"
			    +"<span id=\"orgSubFolderPocSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgPocSubFolder"+folderTRId+"_"+cnt+"\" id=\"strOrgPocSubFolder"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgPoc+"</select></span>"
			    +"</div>";
			
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.setAttribute('nowrap','nowrap');
		    cell4.innerHTML = "<span id=\"isSubFolderEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderEdit"+folderTRId+"\" id=\"isSubFolderEdit"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"subFolderEdit"+folderTRId+"\" id=\"subFolderEdit"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderEdit"+cnt+"');\" checked/> Edit</span>"+
		    	"<span id=\"isSubFolderDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDelete"+folderTRId+"\" id=\"isSubFolderDelete"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDelete"+folderTRId+"\" id=\"subFolderDelete"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDelete"+cnt+"');\"/> Delete</span>";
				
		    document.getElementById(rowCountName).value = cnt;
		    
		    $("#strOrgResourcesSubFolder"+cnt).multiselect().multiselectfilter();
		    $("#strOrgPocSubFolder"+cnt).multiselect().multiselectfilter();
		}
	
	
	function deleteSubFolder(count, tableName) {
		if(confirm('Are you sure, you want to delete this folder?')) {
			var trIndex = document.getElementById("folderTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	
	function addNewSubFolderDocs(subFolderTRId, rwIndex, tableName, rowCountName, tableId) {
		//alert("addNewSubFolderDocs rwIndex ===>> " + rwIndex);
		document.getElementById("divSubmitCancel_"+tableId).style.display = "block";
		var fdCnt = document.getElementById(rowCountName).value;
		var cnt=(parseInt(fdCnt)+1);
		var val=(parseInt(rwIndex)+1);
		
		var orgCategories = document.getElementById("orgCategories").value;
	    var orgProjects = document.getElementById("orgProjects").value;
	    var orgResources = document.getElementById("orgResources").value;
	    var orgPoc = document.getElementById("orgSPOC").value;
		   
	    var table = document.getElementById(tableName);
	    var rowCount = table.rows.length;
	    var row = table.insertRow(val);
	    
	    row.id="folderTR"+cnt;
	    var cell0 = row.insertCell(0);
	    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
	    cell0.setAttribute('style','width: 500px;');
	    cell0.setAttribute('class','tdBorderLine');
	    
	    /* cell0.innerHTML = "<span style=\"float:left; margin-left: 100px; margin-right: 9px;\"><input type=\"hidden\" name=\"subFolderDocsTRId"+subFolderTRId+"\" id=\"subFolderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strSubFolderScopeDoc"+subFolderTRId+"\" id=\"strSubFolderScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
	    "<input type=\"file\" name=\"strSubFolderDoc"+subFolderTRId+"\" id=\"strSubFolderDoc"+cnt+"\" size=\"5\"/></span>"
	    +"<a href=\"javascript:void(0)\" onclick=\"addNewSubFolderDocs('"+subFolderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"' ,'"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteSubFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
        +"<span style=\"float:left; width: 100%; margin-left: 100px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strSubFolderDocDescription"+subFolderTRId+"\" id=\"strSubFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        ; */
        
        //===start parvez date: 13-09-2021=== 
        cell0.innerHTML = "<span style=\"float:left; margin-left: 100px; margin-right: 9px;\"><input type=\"hidden\" name=\"subFolderDocsTRId"+subFolderTRId+"\" id=\"subFolderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strSubFolderScopeDoc"+subFolderTRId+"\" id=\"strSubFolderScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
	    "<input type=\"file\" name=\"strSubFolderDoc"+subFolderTRId+"\" id=\"strSubFolderDoc"+cnt+"\" size=\"5\" required/></span>"
	    +"<a href=\"javascript:void(0)\" onclick=\"addNewSubFolderDocs('"+subFolderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"' ,'"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteSubFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
        +"<span style=\"float:left; width: 100%; margin-left: 100px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strSubFolderDocDescription"+subFolderTRId+"\" id=\"strSubFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        ;
	    //===end parvez date: 13-09-2021=== 
        
	    var cell1 = row.insertCell(1);
	    cell1.setAttribute("valign","top");
	    cell1.setAttribute('class','tdBorderLine');
	    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeSubFolderDoc"+subFolderTRId+"\" id=\"proCategoryTypeSubFolderDoc"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'orgCatagorySubFolderDocSpan"+cnt+"', 'orgProjectSubFolderDocSpan"+cnt+"','SubFolderDocSharingType"+cnt+"');\">"+
	    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
	    
	    var cell2 = row.insertCell(2);
	    cell2.setAttribute("valign","top");
	    cell2.setAttribute('class','tdBorderLine');
	    cell2.innerHTML = "<span id=\"orgCatagorySubFolderDocSpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"strOrgCategorySubFolderDoc"+subFolderTRId+"_"+cnt+"\" id=\"strOrgCategorySubFolderDoc"+cnt+"\" style=\"width:100px !important;\">"+
	     	""+orgCategories+"</select></span><span id=\"orgProjectSubFolderDocSpan"+cnt+"\" style=\"float: left;\"><select name=\"strOrgProjectSubFolderDoc"+subFolderTRId+"_"+cnt+"\" id=\"strOrgProjectSubFolderDoc"+cnt+"\" style=\"width:100px !important;\">"+
	     	""+orgProjects+"</select></span>";
	     	
	    var cell3 = row.insertCell(3);
	    cell3.setAttribute("valign","top");
	    cell3.setAttribute('class','tdBorderLine');
	    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubFolderDocSharingType"+subFolderTRId+"\" id=\"SubFolderDocSharingType"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, 'orgSubFolderDocResourceSpan"+cnt+"')\">"
		    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
		    +"<span id=\"orgSubFolderDocResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgResourcesSubFolderDoc"+subFolderTRId+"_"+cnt+"\" id=\"strOrgResourcesSubFolderDoc"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgResources+"</select></span>"
		    +"</div>"
		    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('orgSubFolderDocPocSpan"+cnt+"','"+cnt+"')\">share customer</a></span>"
		    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"
		    +"<span id=\"orgSubFolderDocPocSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgPocSubFolderDoc"+subFolderTRId+"_"+cnt+"\" id=\"strOrgPocSubFolderDoc"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgPoc+"</select></span>"
		    +"</div>";
	    var cell4 = row.insertCell(4);
	    cell4.setAttribute("valign","top");
	    cell4.setAttribute('class','tdBorderLine');
	    cell4.setAttribute('nowrap','nowrap');
	    cell4.innerHTML = "<span id=\"isSubFolderDocEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDocEdit"+subFolderTRId+"\" id=\"isSubFolderDocEdit"+cnt+"\" value=\"1\">"+
	    	"<input type=\"checkbox\" name=\"subFolderDocEdit"+subFolderTRId+"\" id=\"subFolderDocEdit"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDocEdit"+cnt+"');\" checked/> Edit</span>"+
	    	"<span id=\"isSubFolderDocDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDocDelete"+subFolderTRId+"\" id=\"isSubFolderDocDelete"+cnt+"\" value=\"0\">"+
	    	"<input type=\"checkbox\" name=\"subFolderDocDelete"+subFolderTRId+"\" id=\"subFolderDocDelete"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDocDelete"+cnt+"');\"/> Delete</span>";

		document.getElementById(rowCountName).value = cnt;
		
		$("#strOrgResourcesSubFolderDoc"+cnt).multiselect().multiselectfilter();
	    $("#strOrgPocSubFolderDoc"+cnt).multiselect().multiselectfilter();
	}
	
	
	function deleteSubFolderDocs(count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("folderTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	
	function addNewDocs(tableName, rowCountName, tableId) { 
		if(document.getElementById("divSubmitCancel_"+tableId)){
		document.getElementById("divSubmitCancel_"+tableId).style.display = "block";
		}
		var fdCnt = document.getElementById(rowCountName).value;
		var cnt=(parseInt(fdCnt)+1);
		//alert("addNewDocs cnt ===>> " + cnt);
	    //alert("taskCnt ===>> " + taskCnt);
	    var orgCategories = document.getElementById("orgCategories").value;
	    var orgProjects = document.getElementById("orgProjects").value;
	    var orgResources = document.getElementById("orgResources").value;
	    var orgPoc = document.getElementById("orgSPOC").value;
	    
	    if(document.getElementById("submitDiv_0")) {
			document.getElementById("submitDiv_0").style.display = 'block';
		}
	    
	    var table = document.getElementById(tableName);
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    row.id="docTR"+cnt;
	    var cell0 = row.insertCell(0);
	    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
	    cell0.setAttribute('style','width: 550px;');
	    cell0.setAttribute('class','tdBorderLine');
	    
	    /* cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"docsTRId\" id=\"docsTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strScopeDoc\" id=\"strScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
	    "<input type=\"file\" name=\"strDoc\" id=\"strDoc"+cnt+"\" size=\"5\"/></span>"
	    +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewDocs('"+tableName+"', '"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\" >&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\" >&nbsp;</a></span>"
        +"<span style=\"float:left; width: 100%; margin-top: 5px;\"><textarea rows=\"3\" name=\"strDocDescription\" id=\"strDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        ; */
        
        //===start parvez date: 13-09-2021=== 
        cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"docsTRId\" id=\"docsTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strScopeDoc\" id=\"strScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
	    "<input type=\"file\" name=\"strDoc\" id=\"strDoc"+cnt+"\" size=\"5\" required /></span>"
	    +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewDocs('"+tableName+"', '"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\" >&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\" >&nbsp;</a></span>"
        +"<span style=\"float:left; width: 100%; margin-top: 5px;\"><textarea rows=\"3\" name=\"strDocDescription\" id=\"strDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        ;
	    //===end parvez date: 13-09-2021=== 
        
	    /* var cell4 = row.insertCell(1);
	    cell4.setAttribute("valign","top");
	    cell4.innerHTML = "<input type=\"text\" name=\"strScopeDoc\" id=\"strScopeDoc"+cnt+"\" style=\"width:150px;\"/>"; */
	    
	    var cell1 = row.insertCell(1);
	    cell1.setAttribute("valign","top");
	    cell1.setAttribute('class','tdBorderLine');
	    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeDoc\" id=\"proCategoryTypeDoc"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'orgCatagoryDocSpan"+cnt+"', 'orgProjectDocSpan"+cnt+"','docSharingType"+cnt+"');\">"+
	    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
	    
	    var cell2 = row.insertCell(2);
	    cell2.setAttribute("valign","top");
	    cell2.setAttribute('class','tdBorderLine');
	    cell2.innerHTML = "<span id=\"orgCatagoryDocSpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"strOrgCategoryDoc"+cnt+"\" id=\"strOrgCategoryDoc"+cnt+"\" style=\"width:100px !important;\">"+
	     	""+orgCategories+"</select></span><span id=\"orgProjectDocSpan"+cnt+"\" style=\"float: left;\"><select name=\"strOrgProjectDoc"+cnt+"\" id=\"strOrgProjectDoc"+cnt+"\" style=\"width:100px !important;\">"+
	     	""+orgProjects+"</select></span>";
	     	
	    var cell3 = row.insertCell(3);
	    cell3.setAttribute("valign","top");
	    cell3.setAttribute('class','tdBorderLine');
	    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"docSharingType\" id=\"docSharingType"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, 'orgDocResourceSpan"+cnt+"')\">"
		    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
		    +"<span id=\"orgDocResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgResourcesDoc"+cnt+"\" id=\"strOrgResourcesDoc"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgResources+"</select></span>"
		    +"</div>"
		    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('orgDocPocSpan"+cnt+"','"+cnt+"')\">share customer</a></span>"
		    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"
		    +"<span id=\"orgDocPocSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgPocDoc"+cnt+"\" id=\"strOrgPocDoc"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgPoc+"</select></span>"
		    +"</div>";
	
	    var cell4 = row.insertCell(4);
	    cell4.setAttribute("valign","top");
	    cell4.setAttribute('class','tdBorderLine');
	    cell4.setAttribute('nowrap','nowrap');
	    cell4.innerHTML = "<span id=\"isDocEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isDocEdit\" id=\"isDocEdit"+cnt+"\" value=\"1\">"+
	    	"<input type=\"checkbox\" name=\"docEdit\" id=\"docEdit"+cnt+"\" onclick=\"checkStatus(this, 'isDocEdit"+cnt+"');\" checked/> Edit</span>"+
	    	"<span id=\"isDocDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isDocDelete\" id=\"isDocDelete"+cnt+"\" value=\"0\">"+
	    	"<input type=\"checkbox\" name=\"docDelete\" id=\"docDelete"+cnt+"\" onclick=\"checkStatus(this, 'isDocDelete"+cnt+"');\"/> Delete</span>";

	    document.getElementById(rowCountName).value = cnt;
	    
	    $("#strOrgResourcesDoc"+cnt).multiselect().multiselectfilter();
	    $("#strOrgPocDoc"+cnt).multiselect().multiselectfilter();
	}
	
	
	function deleteDocs(count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("docTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		    
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    if(parseInt(rowCount) == 1) {
			    if(document.getElementById("submitDiv_0")) {
			    	document.getElementById("submitDiv_0").style.display = 'none';
				}
			    if(document.getElementById("folderTR0")) {
			    	document.getElementById("folderTR0").style.display = 'none';
			    }
		    }
		}
	}
	
	
	function addNewFolderDocs(folderTRId, rwIndex, tableName, rowCountName, tableId) {
		//alert("rwIndex ===>> " + rwIndex);
		if(document.getElementById("divSubmitCancel_"+tableId)){
		document.getElementById("divSubmitCancel_"+tableId).style.display = "block";
		}
		var fdCnt = document.getElementById(rowCountName).value;
		var cnt=(parseInt(fdCnt)+1);
		var val=(parseInt(rwIndex)+1);
	    
		var orgCategories = document.getElementById("orgCategories").value;
	    var orgProjects = document.getElementById("orgProjects").value;
	    var orgResources = document.getElementById("orgResources").value;
	    var orgPoc = document.getElementById("orgSPOC").value;
	    
	    var table = document.getElementById(tableName);
	    var rowCount = table.rows.length;
	    var row = table.insertRow(val);
	    
	    row.id="folderTR"+cnt;
	    var cell0 = row.insertCell(0);
	    //cell1.setAttribute('style', 'border-bottom: 1px solid #B6B6B6' );
	    cell0.setAttribute('style','width: 500px;');
	    cell0.setAttribute('class','tdBorderLine');
	    
	    /* cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"folderDocsTRId"+folderTRId+"\" id=\"folderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strFolderScopeDoc"+folderTRId+"\" id=\"strFolderScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
	    "<input type=\"file\" name=\"strFolderDoc"+folderTRId+"\" id=\"strFolderDoc"+cnt+"\" size=\"5\"/></span>"
	    +"<a href=\"javascript:void(0)\" onclick=\"addNewFolderDocs('"+folderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strFolderDocDescription"+folderTRId+"\" id=\"strFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        ; */
        
        //===start parvez date: 13-09-2021=== 
        cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"folderDocsTRId"+folderTRId+"\" id=\"folderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strFolderScopeDoc"+folderTRId+"\" id=\"strFolderScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
	    "<input type=\"file\" name=\"strFolderDoc"+folderTRId+"\" id=\"strFolderDoc"+cnt+"\" size=\"5\" required/></span>"
	    +"<a href=\"javascript:void(0)\" onclick=\"addNewFolderDocs('"+folderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strFolderDocDescription"+folderTRId+"\" id=\"strFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        ;
        //===end parvez date: 13-09-2021=== 
        
	    /* var cell4 = row.insertCell(1);
	    cell4.setAttribute("valign","top");
	    cell4.innerHTML = "<input type=\"text\" name=\"strFolderScopeDoc"+folderTRId+"\" id=\"strFolderScopeDoc"+cnt+"\" style=\"width:150px;\"/>";
	     */
	    var cell1 = row.insertCell(1);
	    cell1.setAttribute("valign","top");
	    cell1.setAttribute('class','tdBorderLine');
	    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeFolderDoc"+folderTRId+"\" id=\"proCategoryTypeFolderDoc"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'orgCatagoryFolderDocSpan"+cnt+"', 'orgProjectFolderDocSpan"+cnt+"','folderDocSharingType"+cnt+"');\">"+
	    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
	    
	    var cell2 = row.insertCell(2);
	    cell2.setAttribute("valign","top");
	    cell2.setAttribute('class','tdBorderLine');
	    cell2.innerHTML = "<span id=\"orgCatagoryFolderDocSpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"strOrgCategoryFolderDoc"+folderTRId+"_"+cnt+"\" id=\"strOrgCategoryFolderDoc"+cnt+"\" style=\"width:100px !important;\">"+
	     	""+orgCategories+"</select></span><span id=\"orgProjectFolderDocSpan"+cnt+"\" style=\"float: left;\"><select name=\"strOrgProjectFolderDoc"+folderTRId+"_"+cnt+"\" id=\"strOrgProjectFolderDoc"+cnt+"\" style=\"width:100px !important;\">"+
	     	""+orgProjects+"</select></span>";
	     	
	    var cell3 = row.insertCell(3);
	    cell3.setAttribute("valign","top");
	    cell3.setAttribute('class','tdBorderLine');
	    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderDocSharingType"+folderTRId+"\" id=\"folderDocSharingType"+cnt+"\" style=\"width:100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, 'orgFolderDocResourceSpan"+cnt+"')\">"
		    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select></span>"
		    +"<span id=\"orgFolderDocResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgResourcesFolderDoc"+folderTRId+"_"+cnt+"\" id=\"strOrgResourcesFolderDoc"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgResources+"</select></span>"
		    +"</div>"
		    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('orgFolderDocPocSpan"+cnt+"','"+cnt+"')\">share customer</a></span>"
		    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"
		    +"<span id=\"orgFolderDocPocSpan"+cnt+"\" style=\"display: none; float: left; margin-top: 3px;\"><select name=\"strOrgPocFolderDoc"+folderTRId+"_"+cnt+"\" id=\"strOrgPocFolderDoc"+cnt+"\" style=\"width:100px !important;\" multiple size=\"4\">"+orgPoc+"</select></span>"
		    +"</div>";
	
	    var cell4 = row.insertCell(4);
	    cell4.setAttribute("valign","top");
	    cell4.setAttribute('class','tdBorderLine');
	    cell4.setAttribute('nowrap','nowrap');
	    cell4.innerHTML = "<span id=\"isFolderDocEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDocEdit"+folderTRId+"\" id=\"isFolderDocEdit"+cnt+"\" value=\"1\">"+
	    	"<input type=\"checkbox\" name=\"folderDocEdit"+folderTRId+"\" id=\"folderDocEdit"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDocEdit"+cnt+"');\" checked/> Edit</span>"+
	    	"<span id=\"isFolderDocDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDocDelete"+folderTRId+"\" id=\"isFolderDocDelete"+cnt+"\" value=\"0\">"+
	    	"<input type=\"checkbox\" name=\"folderDocDelete"+folderTRId+"\" id=\"folderDocDelete"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDocDelete"+cnt+"');\"/> Delete</span>";

	    document.getElementById(rowCountName).value = cnt;
	    
	    $("#strOrgResourcesFolderDoc"+cnt).multiselect().multiselectfilter();
	    $("#strOrgPocFolderDoc"+cnt).multiselect().multiselectfilter();
	}
	
	
	function deleteFolderDocs(count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("folderTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	function showHideResources(val, spanName) {
		if(val == '2') {
			document.getElementById(spanName).style.display = 'block';
		} else {
			document.getElementById(spanName).style.display = 'none';
		}
	}
	
	function showPoc(id,count) {
		var val = document.getElementById("showPocType"+count).value;
		if(val == '1') {
			document.getElementById(id).style.display = 'block';
			document.getElementById("showPocType"+count).value = '0';
		} else {
			document.getElementById(id).style.display = 'none';
			document.getElementById("showPocType"+count).value = '1';
		}
	}
	
	function showTblHeader() {
		document.getElementById("folderTR0").style.display = 'table-row';
		//document.getElementById("submitDiv_0").style.display = 'block';
	}
	
//	***************************************************************** End Add New Folder **************************************************	
	
	
	function deleteProjectDocs(type, divName, folderName, proDocID, mainPath) {
		var msg ="Are you sure, you wish to delete this Folder?";
		if(type != 'F' && type != 'SF') {
			msg ="Are you sure, you wish to delete this File?";
		}
		if(confirm(msg)) {
			var xmlhttp;
			if (window.XMLHttpRequest) {
		        // code for IE7+, Firefox, Chrome, Opera, Safari
		        xmlhttp = new XMLHttpRequest();
			}
		    if (window.ActiveXObject) {
		        // code for IE6, IE5
		    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		    }

		    if (xmlhttp == null) {
		            alert("Browser does not support HTTP Request");
		            return;
		    } else {
	            var xhr = $.ajax({
	                url : 'DeleteProjectDocuments.action?operation=D&type='+type+'&proDocID='+proDocID +'&mainPath='+encodeURIComponent(mainPath),
	                cache : false,
	                success : function(data) {
	                	//alert("data=====>"+data);
						if(data.trim() == 'yes') {
							//alert("data=====>");
							document.getElementById(divName).innerHTML = '';
						}
	                }
	            });
		    }
		}
	}
	
	function executeFolderActions(val, cnt, type, strId, filePath, fileDir, folderName, divName, savePath) {
	//alert("strId ===>> " + strId);
		if(val == '1') {
			editFolder(strId, type, filePath, fileDir, folderName);
		} else if(val == '2') {
			deleteProjectDocs(type, divName, folderName, strId, savePath);
		} else if(val == '3') {
			copyFile(strId, type, filePath, fileDir, folderName,savePath);
		} 
	}


	function editFolder(strId, type, filePath, fileDir, folderName) {
		$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#folderAndFileDiv");
		getContent('folderAndFileDiv', 'EditDocumentFolderAndFile.action?strId='+strId+'&type='+type+'&filePath='+encodeURIComponent(filePath)+'&fileDir='+encodeURIComponent(fileDir));
	}
	
	function addNewDocumentInSubFolder(divId, type, strId, tableId) {
		//alert("divId ===>> " + divId);
		document.getElementById(divId).style.display = 'block';
		$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#"+divId);
		getContent(''+divId, 'EditDocumentFolderAndFile.action?operation=ADD&strId='+strId+'&type='+type+'&tableId='+tableId);
	}
	
	function addNewSubFolderORDocumentInFolder(divId, type, strId, tableId) {
		document.getElementById(divId).style.display = 'block';
		$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#"+divId);
		getContent(''+divId, 'EditDocumentFolderAndFile.action?operation=ADD&strId='+strId+'&type='+type+'&tableId='+tableId);
	}
	
	function closeForm() {
		window.location = "DocumentListView.action";
	}
	
	
	function copyFile(strId, type, filePath, fileDir, folderName,existPath) {
		$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#folderAndFileDiv");
		getContent('folderAndFileDiv', 'CopyDocument.action?strId='+strId+'&type='+type+'&filePath='+encodeURIComponent(filePath)+'&fileDir='+encodeURIComponent(fileDir) +'&existPath='+encodeURIComponent(existPath));
	}
	
	
	/* function editDoc(clientId, proId, docName, proFolderId,type,filePath,fileDir) {
		removeLoadingDiv("the_div");
		var dialogEdit = '#editDoc';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,  
			height : 350,
			width : '80%', 
			modal : true,
			title : 'Update '+docName+' file',
			open : function() {
				var xhr = $.ajax({
					url : "UpdateProjectDocumentFile.action?clientId="+clientId+"&proId="+proId+"&folderName="+docName+"&proFolderId="+proFolderId+"&type="+type+"&filePath="+filePath+"&fileDir="+fileDir,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});
		$(dialogEdit).dialog('open');
	} */
	
	function viewVersionHistory(strProDocId,docStatusId,docVersionDivId,downSpanId,upSpanId,type,filePath,fileDir){
		var status = document.getElementById(docStatusId).value;
		if(status == '0') {
			document.getElementById(docStatusId).value = '1';
			document.getElementById(downSpanId).style.display = 'none';
			document.getElementById(upSpanId).style.display = 'block';
			
			document.getElementById(docVersionDivId).style.display = 'block';
	    	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#"+docVersionDivId);
	    	var action = 'ProDocVersionHistory.action?proDocumentId='+ strProDocId+'&type='+type+'&filePath='+encodeURIComponent(filePath)+'&fileDir='+encodeURIComponent(fileDir);
	    	getContent(docVersionDivId, action);
			
		} else {
			document.getElementById(docStatusId).value = '0';
			document.getElementById(downSpanId).style.display = 'block';
			document.getElementById(upSpanId).style.display = 'none';
			
			document.getElementById(docVersionDivId).innerHTML = '';
			document.getElementById(docVersionDivId).style.display = 'none';
		}
	}
	
</script> 
<script type="text/javascript">
    function showLoading() {
        var div = document.createElement('div');
        var img = document.createElement('img');
        /* img.src = 'loading_bar.GIF'; */
        div.innerHTML = "Uploading...<br />";
        div.style.cssText = 'position: fixed; top: 50%; left: 40%; z-index: 5000; width: 222px; text-align: center; background: #EFEFEF; border: 1px solid #000';
        /* div.appendChild(img); */
        $(div).appendTo('body');
       // document.body.appendChild(div);
        return true;
        
    }
</script>

	<%-- <jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Documents" name="title" />
	</jsp:include> --%>

<section class="content">

	<div class="row">
		<div class="col-md-12">	
			<div class="box box-warning direct-chat direct-chat-warning collapsed-box">
				<%
				UtilityFunctions uF = new UtilityFunctions();
				String sbData = (String) request.getAttribute("sbData");
				String strSearchDoc = (String) request.getAttribute("strSearchDoc");
				%>
                    <!-- Conversations are loaded here -->
					<div class="direct-chat-messages" style="height: auto;">
						<div style="float:left; margin-left: 10px;"><a href="javascript:void(0);" onclick="window.location='DocumentListView.action'" style="font-weight: normal;">Reset to default</a></div>
							<form method="post" name="DocumentListView" action="DocumentListView.action" >
								<div style="float:left; margin-left: 250px;">
									<div style="margin:0px 0px 0px 5px; float:right; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
										<div style="float:left">
											<input type="text" id="strSearchDoc" name="strSearchDoc" placeholder="Search Documents" style="margin-left: 0px; border:1px solid #ccc; width:282px; box-shadow:0px 0px 0px #ccc" value="<%=uF.showData(strSearchDoc,"") %>"/> 
										</div>
										<div style="float:right">
											<input type="submit" value="Search" class="btn btn-primary" style="padding: 2px 7px;">
										</div>
									</div>
								</div> 
							</form>
						<script>
							$( "#strSearchDoc" ).autocomplete({
								source: [ <%=uF.showData(sbData,"") %> ]
							});
						</script>
					</div><!--/.direct-chat-messages-->
			</div>
		</div>
	</div>
	
	<% 
		List<List<String>> categoryList = (List<List<String>>) request.getAttribute("categoryList");
		Map<String, List<List<String>>> hmFolderData = (Map<String, List<List<String>>>) request.getAttribute("hmFolderData");
		Map<String, List<List<String>>> hmSubFolderData = (Map<String, List<List<String>>>) request.getAttribute("hmSubFolderData");
		if(hmSubFolderData == null) hmSubFolderData = new LinkedHashMap<String, List<List<String>>>();
		Map<String, List<List<String>>> hmProFolderData = (Map<String, List<List<String>>>) request.getAttribute("hmProFolderData");
		
		List<List<String>> projectList = (List<List<String>>) request.getAttribute("projectList");
		List<List<String>> allFolderList = (List<List<String>>) request.getAttribute("allFolderList");
		//System.out.println("categoryList ------> " + categoryList);
		
		String docType = (String) request.getAttribute("docType");
	%>
	
	<%=uF.showData((String) session.getAttribute("MESSAGE"), "") %>
	<% session.setAttribute("MESSAGE", ""); %>
	
	<div class="row">
		<input type="hidden" name="orgCategories" id="orgCategories" value="<%=(String)request.getAttribute("sbOrgCategory") %>" />
		<input type="hidden" name="orgProjects" id="orgProjects" value="<%=(String)request.getAttribute("sbOrgProjects") %>" />
		<input type="hidden" name="orgResources" id="orgResources" value="<%=(String)request.getAttribute("sbOrgResources") %>" />
		<input type="hidden" name="orgSPOC" id="orgSPOC" value="<%=(String)request.getAttribute("sbOrgSPOC") %>" />
		
		<div class="col-md-4">
			<div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                	<li class="<%=((docType == null || docType.equals("") || docType.equals("null") || docType.equalsIgnoreCase("ALL")) ? "active" : "") %>"><a href="#documents" style="padding: 5px;" onclick="window.location.href='DocumentListView.action?docType=ALL'" data-toggle="tab">All </a></li>		<%-- (<%=(String)request.getAttribute("allCnt") %>) --%>
                	<li class="<%=((docType != null && docType.equalsIgnoreCase("C")) ? "active" : "") %>"><a href="#documents" style="padding: 5px;" onclick="window.location.href='DocumentListView.action?docType=C'" data-toggle="tab">Categories </a></li>
                	<li class="<%=((docType != null && docType.equalsIgnoreCase("P")) ? "active" : "") %>"><a href="#documents" style="padding: 5px;" onclick="window.location.href='DocumentListView.action?docType=P'" data-toggle="tab">Projects </a></li>
                	<li class="<%=((docType != null && docType.equalsIgnoreCase("F")) ? "active" : "") %>"><a href="#documents" style="padding: 5px;" onclick="window.location.href='DocumentListView.action?docType=F'" data-toggle="tab">Folders </a></li>
                </ul>
                <div class="tab-content box-body">
	                <div class="active tab-pane" id="#documents">
	                <%if(docType == null || docType.equals("") || docType.equals("null") || docType.equalsIgnoreCase("ALL")) { %>
	                	<div style="float: left; width: 100%;margin-left: 10px;"><strong>Categories</strong></div>
	                <% } %>	
	                <%if(docType == null || docType.equals("") || docType.equals("null") || docType.equalsIgnoreCase("ALL") || docType.equalsIgnoreCase("C")) { %>
		                <div id="demo1_menu" style="float: left; width: 100%;">
			                <ul> <!-- class="products-list product-list-in-box" -->
				                <% 
								//System.out.println("categoryList ==>> " + categoryList);
								for(int i=0; categoryList!=null && !categoryList.isEmpty() && i<categoryList.size(); i++) { 
									List<String> innerList = categoryList.get(i);
								%>
								  <li class="isFolder">
								  <a href="javascript:getFolderAndFiles('<%=innerList.get(0) %>', 'C', '');"><%=innerList.get(1) %></a>
								  <% List<List<String>> folderList = hmFolderData.get(innerList.get(0));
								  	if(folderList!=null && !folderList.isEmpty()) {
								  %>
								    <ul style="margin-bottom: 0px; margin-top: 0px;">
								      <% 
								      	for(int j=0; j<folderList.size(); j++) {
								      		List<String> finnerList = folderList.get(j);
								      %>
									      <li class="isFolder"><a href="javascript:getFolderAndFiles('<%=finnerList.get(0) %>', 'CF', '');"><%=finnerList.get(1) %></a>
								        <% 
								        //System.out.println("hmSubFolderData ===>> " + hmSubFolderData);
								       		List<List<String>> subFolderList = hmSubFolderData.get(finnerList.get(0));
										  	if(subFolderList!=null && !subFolderList.isEmpty()) {
										  %>
									        <ul style="margin-bottom: 0px; margin-top: 0px;">
									          <% 
										      	for(int k=0; subFolderList!=null && !subFolderList.isEmpty() && k<subFolderList.size(); k++) {
										      		List<String> sfinnerList = subFolderList.get(k);
										      %>
									          <li class="isFolder"><a href="javascript:getFolderAndFiles('<%=sfinnerList.get(0) %>', 'CSF', '');"><%=sfinnerList.get(1) %></a></li>
									          <% } %>
									        </ul>
									        <% } %>
									      </li>
								      <% } %>
								    </ul>
								    <% } %>
								   </li>
								  <%} %>
							  </ul>
	                  		</div>
							<%if(categoryList == null || categoryList.size() == 0) { %>
								<div class="alert" style="float: left; width: 100%; background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No categories available.</div>
							<% } %>
		                  
					<% } %>
					
					<%if(docType == null || docType.equals("") || docType.equals("null") || docType.equalsIgnoreCase("ALL")) { %> 	
						<div style="float: left; width: 100%;border-bottom: 1px solid #CCCCCC;"></div>
						<div style="float: left; width: 100%;margin-left: 10px;"><strong>Projects</strong></div>
					<% } %>
					<%if(docType == null || docType.equals("") || docType.equals("null") || docType.equalsIgnoreCase("ALL") || docType.equalsIgnoreCase("P")) { %>
						<div id="demo2_menu" style="float: left; width: 100%;">
							<ul>
							<% for(int i=0; projectList!=null && !projectList.isEmpty() && i<projectList.size(); i++) { 
								List<String> innerList = projectList.get(i);
							%>
							  <li class="isFolder"><a href="javascript:getFolderAndFiles('<%=innerList.get(0) %>', 'P', '');"><%=innerList.get(1) %></a>
							  <% List<List<String>> folderList = hmProFolderData.get(innerList.get(0));
							  	if(folderList!=null && !folderList.isEmpty()) {
							  %>
							    <ul style="margin-bottom: 0px; margin-top: 0px;">
							      <% 
							      	for(int j=0; j<folderList.size(); j++) {
							      		List<String> finnerList = folderList.get(j);
							      %>
								      <li class="isFolder"><a href="javascript:getFolderAndFiles('<%=finnerList.get(0) %>', 'PF', '');"><%=finnerList.get(1) %></a>
								        <% List<List<String>> subFolderList = hmSubFolderData.get(finnerList.get(0));
										  	if(subFolderList!=null && !subFolderList.isEmpty()) {
										  %>
								        <ul style="margin-bottom: 0px; margin-top: 0px;">
								          <% 
									      	for(int k=0; subFolderList!=null && !subFolderList.isEmpty() && k<subFolderList.size(); k++) {
									      		List<String> sfinnerList = subFolderList.get(k);
									      %>
								         	 <li class="isFolder"><a href="javascript:getFolderAndFiles('<%=sfinnerList.get(0) %>', 'PSF', '');"><%=sfinnerList.get(1) %></a></li>
								          <% } %>
								        </ul>
								        <% } %>
								      </li>
							      <% } %>
							    </ul>
							    <% } %>
							   </li>
							  <%} %>
							</ul>
						</div>
						  <%if(projectList == null || projectList.size() == 0) { %>
							<div class="alert" style="float: left; width: 100%; background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No projects available.</div>
						  <% } %>
							
					<% } %>
					
					<%if(docType == null || docType.equals("") || docType.equals("null") || docType.equalsIgnoreCase("ALL")) { %>
						<div style="float: left; width: 100%;border-bottom: 1px solid #CCCCCC;"></div>
						<div style="float: left; width: 100%;margin-left: 10px;"><strong>Folders</strong></div>
					<% } %>
					<%if(docType == null || docType.equals("") || docType.equals("null") || docType.equalsIgnoreCase("ALL") || docType.equalsIgnoreCase("F")) { %>
						<div id="demo3_menu" style="float: left; width: 100%;">
							<ul>
							  	<% 
							  	  if(allFolderList!=null && !allFolderList.isEmpty()) { 
							      	for(int j=0; j<allFolderList.size(); j++) {
							      		List<String> finnerList = allFolderList.get(j);
							      		
							      %>
								      <li class="isFolder"><a href="javascript:getFolderAndFiles('<%=finnerList.get(0) %>', 'AF', '');" onclick="toggle('node1')"><%=finnerList.get(1) %></a>
								        <% List<List<String>> subFolderList = hmSubFolderData.get(finnerList.get(0));
										  	if(subFolderList!=null && !subFolderList.isEmpty()) {
										  %>
								        <ul  id="node1" style="margin-bottom: 0px; margin-top: 0px;">
								          <% 
									      	for(int k=0; subFolderList!=null && !subFolderList.isEmpty() && k<subFolderList.size(); k++) {
									      		List<String> sfinnerList = subFolderList.get(k);
									      %>
								          	<li class="isFolder"><a href="javascript:getFolderAndFiles('<%=sfinnerList.get(0) %>', 'ASF', '');"><%=sfinnerList.get(1) %></a></li>
								          <% } %>
								        </ul>
								        <% } %>
								        </li>
							      <% } %>
							    <%} %>
							    </ul>
							</div>
						    <%if(allFolderList == null || allFolderList.size() == 0) { %>
								<div class="alert" style="float: left; width: 100%; background-color: #FCF8E3 !important; border-color: #FAEBCC; color: #8A6D3B !important; padding: 10px;">No folders available.</div>
							<% } %>
		             <% } %> 
		             
                </div>
			</div>
		</div>



			<script>
			  function dropped1(event, nodes, isSourceNode, source, isTargetNode, target) {
	             if (isSourceNode && !isTargetNode) {
	                 easyTree2.rebuildTree();
	                 easyTree3.rebuildTree();
	                
	             }
	         }
	         function dropped2(event, nodes, isSourceNode, source, isTargetNode, target) {
	             if (isSourceNode && !isTargetNode) { 
	                 easyTree1.rebuildTree();
	                 easyTree3.rebuildTree();
	             }
	         }
	         
	         function dropped3(event, nodes, isSourceNode, source, isTargetNode, target) {
	             if (isSourceNode && !isTargetNode) {
	                 easyTree1.rebuildTree();
	                 easyTree2.rebuildTree();
	             }
	         } 
	         
	         function toggled1(event, nodes, node) {
	        	 easyTree2.rebuildTree();
	             easyTree3.rebuildTree();
	         }
	         function toggled2(event, nodes, node) {
	        	 easyTree1.rebuildTree();
	             easyTree3.rebuildTree();
	         }
	         function toggled3(event, nodes, node) {
	        	 easyTree1.rebuildTree();
	             easyTree2.rebuildTree();
	         }
	         
	         var easyTree1 = $('#demo1_menu').easytree({
	             enableDnd: true, 
	            //dropped: dropped1
	             toggled: toggled1
	         });
	         var easyTree2 = $('#demo2_menu').easytree({
	             enableDnd: true,
	             //dropped: dropped2
	             toggled: toggled2
	         });
	         var easyTree3 = $('#demo3_menu').easytree({
	             enableDnd: true,
	             //dropped: dropped3
	             toggled: toggled3
	         }); 
	         
	      
			
			 /* $('#demo1_menu').easytree();
			 $('#demo2_menu').easytree();
			 $('#demo3_menu').easytree(); */
			</script>

		</div>
            	
            	
		<div  class="col-lg-8 col-md-8 col-sm-12">
			<!-- <div class="box box-body"> -->
				<div id="folderAndFileDiv" style="min-height: 300px;">
			<!-- </div> -->
			</div>
		</div>

	</div>
</section>


<div id="editFolder"></div>
<div id="editDoc"></div>
<div id="projectDocFactDiv"></div>
<div id="copyFileDiv"></div>
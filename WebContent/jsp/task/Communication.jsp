<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@page import="java.util.Iterator"%> 
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@taglib uri="/struts-tags" prefix="s"%>
<style> 
.ann-stat-img img{
 width: 14px !important; 
 height: 14px !important;
 }
 
.listMenu1 .icon .fa{  
font-size: 55px;
vertical-align: top;
margin-top: 20px;
}
.box-comments .text-muted {
font-weight: 400;
font-size: 14px;
}



</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script> 
<script src="<%= request.getContextPath()%>/js/jquery-ui-1.8.16.custom.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath() %>/scripts/EasyTree/jquery.easytree.js"></script> 
<script src="<%= request.getContextPath()%>/scripts/EasyTree/jquery.easytree.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
<link href="<%=request.getContextPath() %>/scripts/EasyTree/skin-win8/ui.easytree.css" rel="stylesheet"/>

<script type="text/javascript">

$(document).ready(function() {
	$.fn.gdocsViewer = function(options) {
		
		var settings = {
			width  : '98%',
			height : '742'
		};
		
		if (options) { 
			$.extend(settings, options);
			
		}
		
		return this.each(function() {
			var file = $(this).attr('href');
            // int SCHEMA = 2, DOMAIN = 3, PORT = 5, PATH = 6, FILE = 8, QUERYSTRING = 9, HASH = 12
            
            
            var ext=file.substring(file.lastIndexOf(".")+1);
            
          //  console.log("Extension : "+ext);
          if(ext === "docx" || ext === "pptx") {
        	   alert("Unsupported file format!");
           }else {
			if (/^(tiff|pdf|ppt|pptx|pps|doc|txt|xls|xlsx)$/.test(ext)) {
				$(this).after(function () {
					var id = $(this).attr('id');
					var gdvId = (typeof id !== 'undefined' && id !== false) ? id + '-gdocsviewer' : '';
					return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 742px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 742px; border: none;margin : 0 auto; display : block;"></iframe></div>';
				})
			}
           }
		});
	};
	
	$('a.embed').gdocsViewer();
});
//(function($){

//})(jQuery);

</script>

<script>
    $(document).ready(function(){
    	
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
    	});

    	var fromPage = '<%=(String)request.getAttribute("fromPage") %>';
    	if(fromPage != 'null' && fromPage != '' && fromPage == 'COMMUNICATION') {
			getFolderAndFiles('0','C','');
    	} else {
			getFeeds();
    	}
    	
    	
    	$("#strStartDate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strEndDate').datepicker('setStartDate', minDate);
        });
        
        $("#strEndDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#strStartDate').datepicker('setEndDate', minDate);
        });
        
        $("#displayStartDate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#displayEndDate').datepicker('setStartDate', minDate);
        });
        
        $("#displayEndDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#displayStartDate').datepicker('setEndDate', minDate);
        });

    	$("#strLevel").multiselect().multiselectfilter();
    });  
   

    $(function(){
    	$('input[type="submit"]').click(function(){
    		$("#"+this.form.id).find('.validateRequired').filter(':hidden').prop('required',false);
            $("#"+this.form.id).find('.validateRequired').filter(':visible').prop('required',true);
    	});
    });
    
    <% String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE)); %>
    
    			function readImageURL(input, targetDiv) {
    				//alert("notice targetDiv==>"+targetDiv);
    			    if (input.files && input.files[0]) {
    			        var reader = new FileReader();
    			        reader.onload = function (e) {
    			            $('#'+targetDiv)
    			                .attr('src', e.target.result)
    			                .width(60)
    			                .height(60);
    			        };
    			        reader.readAsDataURL(input.files[0]);
    			    }
    			}
    			
    			function showEditPhoto(){
    		    	document.getElementById("uploadPhotoDiv").style.display= "block";
    		    }
    			
    			
    			function hideEditPhoto(){
    		    	document.getElementById("uploadPhotoDiv").style.display= "none";    	
    		    }
    			

    			
    			function loadMoreQuotes() {
    				//alert("loadMoreFeeds ===>> ");
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
    			    	
    			    	document.getElementById("loadingQuotesDiv").style.display = 'block';
    			    	var offsetCnt = document.getElementById("quotehideOffsetCnt").value;
    			    	var lastQuoteId = document.getElementById("lastQuoteId").value;
    			    	//alert("offsetCnt ===>> " + offsetCnt+"==>lastQuoteId==>"+lastQuoteId);
    		          var xhr = $.ajax({
    		              url : 'QuotesByAjax.action?offsetCnt='+offsetCnt+'&lastQuoteId='+lastQuoteId,
    		              cache : false,
    		              success : function(data) {
    		            	  if(data == "") {
    		               	} else {
    		               		var allData = data.split("::::");
    			            //  	alert("type ===>> " + type);
    			              	//document.getElementById("allFeedsDiv").innerHTML = data;
    			             // 	alert("allData[0] ===>> " + allData[0] + " -- allData[1] ===>> " + allData[1]);
    			              	document.getElementById("lastQuoteId").value = allData[0].trim();
    			              	if(allData[1].trim() == 'NO') {
    			              		document.getElementById("loadMoreQuotesDiv").style.display = 'none';
    			              	}
    			              	var divTag2 = document.createElement("div");
    							//divTag2.id = "comment_"+allData[1].trim();
    							divTag2.setAttribute("style","float:left; width: 100%;");
    							divTag2.innerHTML = allData[2];
    							document.getElementById("allQuotesDiv").appendChild(divTag2);
    							document.getElementById("loadingQuotesDiv").style.display = 'none';
    							
    							$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    			      	       }
    		              }
    		          });
    				}
    			}
    			
    			function loadMoreEvents() {
    				//alert("loadMoreFeeds ===>> ");
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
    			    	
    			    	document.getElementById("loadingEventsDiv").style.display = 'block';
    			    	var eventOffsetCnt = document.getElementById("eventhideOffsetCnt").value;
    			    	var lastEventId = document.getElementById("lastEventId").value;
    			    	//alert("eventOffsetCnt ===>> " + eventOffsetCnt+"==>lastEventId==>"+lastEventId);
    		          var xhr = $.ajax({
    		              url : 'EventsByAjax.action?eventOffsetCnt='+eventOffsetCnt+'&lastEventId='+lastEventId,
    		              cache : false,
    		              success : function(data) {
    		            	  if(data == "") {
    		               	} else {
    		               		var allData = data.split("::::");
    			            //  	alert("type ===>> " + type);
    			              	//document.getElementById("allFeedsDiv").innerHTML = data;
    			             // 	alert("allData[0] ===>> " + allData[0] + " -- allData[1] ===>> " + allData[1]);
    			              	document.getElementById("lastEventId").value = allData[0].trim();
    			              	if(allData[1].trim() == 'NO') {
    			              		document.getElementById("loadMoreEventsDiv").style.display = 'none';
    			              	}
    			              	var divTag2 = document.createElement("div");
    							//divTag2.id = "comment_"+allData[1].trim();
    							divTag2.setAttribute("style","width: 100%;");
    							divTag2.innerHTML = allData[2];
    							document.getElementById("allEventsDiv").appendChild(divTag2);
    							document.getElementById("loadingEventsDiv").style.display = 'none';
    							
    							$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    			              }
    		              }
    		          });
    				}
    			}
    			
    			function loadMoreNotices() {
    				//alert("loadMoreFeeds ===>> ");
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
    			    	
    			    	document.getElementById("loadingNoticesDiv").style.display = 'block';
    			    	var noticeOffsetCnt = document.getElementById("noticethideOffsetCnt").value;
    			    	var lastNoticeId = document.getElementById("lastNoticeId").value;
    			    	//alert("noticeOffsetCnt ===>> " + noticeOffsetCnt+"==>lastNoticeId==>"+lastNoticeId);
    		          var xhr = $.ajax({
    		              url : 'NoticesByAjax.action?noticeOffsetCnt='+noticeOffsetCnt+'&lastNoticeId='+lastNoticeId,
    		              cache : false,
    		              success : function(data) {
    		            	  if(data == "") {
    		               	} else {
    		               		var allData = data.split("::::");
    			            //  	alert("type ===>> " + type);
    			              	//document.getElementById("allFeedsDiv").innerHTML = data;
    			             // 	alert("allData[0] ===>> " + allData[0] + " -- allData[1] ===>> " + allData[1]);
    			              	document.getElementById("lastNoticeId").value = allData[0].trim();
    			              	if(allData[1].trim() == 'NO') {
    			              		document.getElementById("loadMoreNoticesDiv").style.display = 'none';
    			              	}
    			              	var divTag2 = document.createElement("div");
    							//divTag2.id = "comment_"+allData[1].trim();
    							divTag2.setAttribute("style","float:left; width: 100%;");
    							divTag2.innerHTML = allData[2];
    							document.getElementById("allNoticesDiv").appendChild(divTag2);
    							document.getElementById("loadingNoticesDiv").style.display = 'none';
    							
    							$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    			             }
    		              }
    		          });
    				}
    			}
    			
    
	/* function uploader(input, options) {
    //	alert("inside notice report uploader");
    	var $this = this;
    
    	// Default settings (mostly debug functions)
    	this.settings = {
    		prefix:'eventImage',
    		multiple:false,
    		autoUpload:false,
    		url:window.location.href,
    		onprogress:function(ev){ console.log('onprogress'); console.log(ev); },
    		error:function(msg){ console.log('error'); console.log(msg); },
    		success:function(data){ console.log('success'); console.log(data); }
    	};
    	$.extend(this.settings, options);
    
    	this.input = input;
    	this.xhr = new XMLHttpRequest();
    
    	this.send = function(){
    		// Make sure there is at least one file selected
    		if($this.input.files.length < 1) {
    			if($this.settings.error) $this.settings.error('Must select a file to upload');
    			return false;
    		}
    		// Don't allow multiple file uploads if not specified
    		if($this.settings.multi === false && $this.input.files.length > 1) {
    			if($this.settings.error) $this.settings.error('Can only upload one file at a time');
    			return false;
    		}
    		// Must determine whether to send one or all of the selected files
    		if($this.settings.multi) {
    			$this.multiSend($this.input.files);
    		}
    		else {
    			$this.singleSend($this.input.files[0]);
    		}
    	};
    
    	// Prep a single file for upload
    	this.singleSend = function(file){
    		var data = new FormData();
    		data.append(String($this.settings.prefix),file);
    		$this.upload(data);
    	};
    
    	// Prepare all of the input files for upload
    	this.multiSend = function(files){
    		var data = new FormData();
    		for(var i = 0; i < files.length; i++) data.append(String($this.settings.prefix)+String(i), files[i]);
    		$this.upload(data);
    	};
    
    	// The actual upload calls
    	this.upload = function(data){
    		$this.xhr.open('POST',$this.settings.url, true);
    		$this.xhr.send(data);
    	};
    
    	// Modify options after instantiation
    	this.setOpt = function(opt, val){
    		$this.settings[opt] = val;
    		return $this;
    	};
    	this.getOpt = function(opt){
    		return $this.settings[opt];
    	};
    
    	// Set the input element after instantiation
    	this.setInput = function(elem){
    		$this.input = elem;
    		return $this;
    	};
    	this.getInput = function(){
    		return $this.input;
    	};
    
    	// Basic setup for the XHR stuff
    	if(this.settings.progress) this.xhr.upload.addEventListener('progress',this.settings.progress,false);
    	this.xhr.onreadystatechange = function(ev){
    		if($this.xhr.readyState == 4) {
    			console.log('done!');
    			if($this.xhr.status == 200) {
    				if($this.settings.success) $this.settings.success($this.xhr.responseText,ev);
    				$this.input.value = '';
    			}
    			else {
    				if($this.settings.error) $this.settings.error(ev);
    			}
    		}
    	};
    	// onChange event for autoUploads
    	if(this.settings.autoUpload) this.input.onchange = this.send;
    } */
    
    
    var timerId;
	function getFolderAndFiles(strId, type,strSearch) {
		document.getElementById("divResult").innerHTML = '<div id="the_div"><div id="ajaxLoadImage"></div></div>';
		//$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#folderAndFileDiv");
		getContent('divResult', 'ProDocumentList.action?fromPage=COMMUNICATION&strId='+strId+'&type='+type+'&strSearchDoc='+strSearch);
		timerId = window.setInterval(function() {
			getFolderCount(); 
		}, 400);
	}
	
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
	    
	    cell0.innerHTML = "<span style=\"float:left; margin-left: 100px; margin-right: 9px;\"><input type=\"hidden\" name=\"subFolderDocsTRId"+subFolderTRId+"\" id=\"subFolderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strSubFolderScopeDoc"+subFolderTRId+"\" id=\"strSubFolderScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
	    "<input type=\"file\" name=\"strSubFolderDoc"+subFolderTRId+"\" id=\"strSubFolderDoc"+cnt+"\" size=\"5\"/></span>"
	    +"<a href=\"javascript:void(0)\" onclick=\"addNewSubFolderDocs('"+subFolderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"' ,'"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteSubFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
        +"<span style=\"float:left; width: 100%; margin-left: 100px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strSubFolderDocDescription"+subFolderTRId+"\" id=\"strSubFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        ;
	    
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
	    
	    cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"docsTRId\" id=\"docsTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strScopeDoc\" id=\"strScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
	    "<input type=\"file\" name=\"strDoc\" id=\"strDoc"+cnt+"\" size=\"5\"/></span>"
	    +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewDocs('"+tableName+"', '"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\" >&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\" >&nbsp;</a></span>"
        +"<span style=\"float:left; width: 100%; margin-top: 5px;\"><textarea rows=\"3\" name=\"strDocDescription\" id=\"strDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        ;
	    
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
	    
	    cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"folderDocsTRId"+folderTRId+"\" id=\"folderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"text\" name=\"strFolderScopeDoc"+folderTRId+"\" id=\"strFolderScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray; margin-bottom: 5px;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
	    "<input type=\"file\" name=\"strFolderDoc"+folderTRId+"\" id=\"strFolderDoc"+cnt+"\" size=\"5\"/></span>"
	    +"<a href=\"javascript:void(0)\" onclick=\"addNewFolderDocs('"+folderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"', '"+tableId+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strFolderDocDescription"+folderTRId+"\" id=\"strFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
        ;
	    
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
		document.getElementById("divResult").innerHTML = '<div id="the_div"><div id="ajaxLoadImage"></div></div>';
		getContent('divResult', 'EditDocumentFolderAndFile.action?fromPage=COMMUNICATION&strId='+strId+'&type='+type+'&filePath='+encodeURIComponent(filePath)+'&fileDir='+encodeURIComponent(fileDir));
	}
	
	function addNewDocumentInSubFolder(divId, type, strId, tableId) {
		//alert("divId ===>> " + divId);
		document.getElementById(divId).style.display = 'block';
		$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#"+divId);
		getContent(''+divId, 'EditDocumentFolderAndFile.action?fromPage=COMMUNICATION&operation=ADD&strId='+strId+'&type='+type+'&tableId='+tableId);
	}
	
	function addNewSubFolderORDocumentInFolder(divId, type, strId, tableId) {
		document.getElementById(divId).style.display = 'block';
		$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#"+divId);
		getContent(''+divId, 'EditDocumentFolderAndFile.action?fromPage=COMMUNICATION&operation=ADD&strId='+strId+'&type='+type+'&tableId='+tableId);
	}
	
	function closeForm() {
		if(confirm('Are you sure, you want to close this form?')) {
			//window.location = "Communication.action?fromPage=COMMUNICATION";
			getFolderAndFiles('0','C','');
		}
	}
	
	
	function copyFile(strId, type, filePath, fileDir, folderName,existPath) {
		document.getElementById("divResult").innerHTML = '<div id="the_div"><div id="ajaxLoadImage"></div></div>';
		getContent('divResult', 'CopyDocument.action?fromPage=COMMUNICATION&strId='+strId+'&type='+type+'&filePath='+encodeURIComponent(filePath)+'&fileDir='+encodeURIComponent(fileDir) +'&existPath='+encodeURIComponent(existPath));
	}
	
	
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

    <script type="text/javascript">
    
    function getPeople() {
    	document.getElementById("divResult").innerHTML = '<div id="the_div"><div id="ajaxLoadImage"></div></div>';
    	getContent('divResult', 'SearchEmployee.action?fromPage=COMMUNICATION&strFirstName=');
    }
    
    function getFeeds() {
    	document.getElementById("divResult").innerHTML = '<div id="the_div"><div id="ajaxLoadImage"></div></div>';
    	getContent('divResult', 'Feeds.action?pageFrom=MyHub');
    }
	
    
    function getHubContent(type) {
    	/* $("divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>'); */
    	document.getElementById("divResult").innerHTML = '<div id="the_div"><div id="ajaxLoadImage"></div></div>';
    	getContent('divResult', 'AddUpdateViewHubContent.action?type='+type);
    }
    
    
	function addEvent() { 
   		var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 $(".modal-title").html('Add Event');				
   		 $.ajax({  
			url : 'AddUpdateViewHubContent.action?type=E&operation=A',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data); 
			}
		});
   	}
	
	
	function editEventPopup(eventId, type){
		var dialogEdit = '#modalInfo .modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show(); 
		$("#modalInfo .modal-title").html('Edit Event ');
		$.ajax({
            url : 'EditAndDeleteEvent.action?eventId='+eventId+'&operation='+type,
            cache : false,
            success : function(data) {
            	//alert("data==>"+data);
            	$(dialogEdit).html(data);  		                	
            	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	
            }
        });
	}
	
	
	function addQuote() { 
   		var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 $(".modal-title").html('Add Quote');				
   		 $.ajax({  
			url : 'AddUpdateViewHubContent.action?type=Q&operation=A',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data); 
			}
		});
   	}
	
	
	function editQuotePopup(thoughtId, type){
		var dialogEdit = '#modalInfo .modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$("#modalInfo .modal-title").html('Edit Quote ');
		$.ajax({
            url : 'EditAndDeleteQuotes.action?thoughtId='+thoughtId+'&operation='+type,
            cache : false,
            success : function(data) {
            	//alert("data==>"+data);
            	$(dialogEdit).html(data);  		                	
            	
            }
        });
	}
	
	
	function deleteYourQuotes(thoughtId, type) {
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
       		if(confirm("Are you sure, you want to delete this Quote? ")) {
       			var xhr = $.ajax({
	                url : 'EditAndDeleteQuotes.action?thoughtId='+thoughtId+'&operation='+type,
	                cache : false,
	                success : function(data) {
		                $("#mainQuoteDiv_"+thoughtId).remove();
					}
				});
			}
		}
	}
	
	
	function addAnnouncement() { 
   		var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 $(".modal-title").html('Add Announcement');				
   		 $.ajax({  
			url : 'AddUpdateViewHubContent.action?type=A&operation=A',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data); 
			}
		});
   	}
	
	
	function editAndDeleteAnnouncement(noticeId, type) {
		if(type=='E') {
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$("#modalInfo").show();
			$(".modal-title").html('Edit Announcement');
			$.ajax({
	            url : 'EditAndDeleteNotice.action?noticeId='+noticeId+'&operation='+type,
	            cache : false,
	            success : function(data) {
	            	//alert("data==>"+data);
	            	$(dialogEdit).html(data);  		                	
	            }
	        });
		} else {
			 if(confirm("Are you sure, you want to delete this Announcement?")) {
       			var xhr = $.ajax({
	                url : 'EditAndDeleteNotice.action?noticeId='+noticeId+'&operation='+type,
	                cache : false,
	                success : function(data) {
	                	//alert("data ===>> " + data);
	                	$("#mainNoticeDiv_"+noticeId).remove();
					}, 
					error : function(err) {
						$("#mainNoticeDiv_"+noticeId).remove();
					}
				});
			}
		}
	}
	
	
	function addFAQ() { 
   		var dialogEdit = '.modal-body';
   		 $(dialogEdit).empty();
   		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   		 $("#modalInfo").show();
   		 $(".modal-title").html('Add FAQ');				
   		 $.ajax({  
			url : 'AddUpdateViewHubContent.action?type=FAQ&operation=A',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data); 
			}
		});
   	}
	
	
	function AddOrEditFaq(faqId, operation) {
    	//alert("value == >"+value);
    	if(operation == 'D') {
	    	//	alert("type ===>> " + type);
            if(confirm("Are you sure, you want to delete this FAQ? ")) {
       			var xhr = $.ajax({
	                url : 'EditAndDeleteFaq.action?faqId='+faqId+'&operation='+operation,
	                cache : false,
	                success : function(data) {
	                	//alert("data ===>> " + data);
	                	$("#faqDataDiv_"+faqId).remove();
					}, 
					error : function(err) {
						$("#faqDataDiv_"+faqId).remove();
					}
				});
       			
			}
		} else {
			var strAction = "EditAndDeleteFaq.action?faqId="+faqId+"&operation="+operation;
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$("#modalInfo").show();
			$(".modal-title").html('Edit FAQ');
			if($(window).width() >= 700) {
			 $(".modal-dialog").width(700);
			}
			$.ajax({
	             url : strAction,
	             cache : false,
	             success : function(data) {
	             	//alert("data==>"+data);
	             	$(dialogEdit).html(data);  		                	
	             }
	      	});
		}
	}
   	
	
	
</script>

<%
    UtilityFunctions uF = new UtilityFunctions();
    
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    String strOrgId = (String)session.getAttribute(IConstants.ORGID);
    
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    String []arrEnabledModules = CF.getArrEnabledModules();
    String type = (String) request.getAttribute("type");
    String operation = (String) request.getAttribute("operation");
    
    String docRetriveLocation = (String)request.getAttribute("DOC_RETRIVE_LOCATION");
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
      	if (hmEmpProfile == null) {
      		hmEmpProfile = new HashMap<String, String>();
      	}
      	String strUITheme = CF.getStrUI_Theme();
      	
      	Map<String, String> hmClientName = (Map<String, String>) request.getAttribute("hmClientName");
      	Map<String, String> hmProjectName = (Map<String, String>) request.getAttribute("hmProjectName");
      	Map<String, String> hmTaskName = (Map<String, String>) request.getAttribute("hmTaskName");
      
 /* ====start parvez on 27-10-2022===== */     	
    Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
    Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
 /* ====end parvez on 27-10-2022===== */   
 %>

<section class="content">
    <div class="row jscroll">
    	<input type="hidden" name="orgCategories" id="orgCategories" value="<%=(String)request.getAttribute("sbOrgCategory") %>" />
		<input type="hidden" name="orgProjects" id="orgProjects" value="<%=(String)request.getAttribute("sbOrgProjects") %>" />
		<input type="hidden" name="orgResources" id="orgResources" value="<%=(String)request.getAttribute("sbOrgResources") %>" />
		<input type="hidden" name="orgSPOC" id="orgSPOC" value="<%=(String)request.getAttribute("sbOrgSPOC") %>" />
		
    	<section class="col-sm-12 col-md-6 col-lg-3 connectedSortable">
            <div class="box box-widget widget-user widget-user1">
                <!-- Add the bg color to the header using any of the bg-* classes -->
                <div class="widget-user-header bg-aqua-active">
               <!-- ====start parvez on 27-10-2022===== --> 
                    <%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'> --%>
                    <%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
						List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
					%>
                    	<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
                    <% } else{ %>
                    	<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
                    <% } %>
               <!-- ====end parvez on 27-10-2022===== -->     
                    <h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: <%=uF.parseToInt(strUITheme)==1 ? "-135px;" : "0px;" %>;"><span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span>
                        <span style="float: right;"><a href="MyProfile.action" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
                    </h3>
                    <h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
                </div>
                <div class="widget-user-image">
                    <%if(docRetriveLocation==null) { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
                    <%} else { %>
                    <img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
                    <%} %>
                </div>
                <div class="box-footer">
                    <div class="row" style="margin: 0px -10px; border-bottom: 1px solid #CFCFCF">
                        <!-- <div class="col-sm-12"> -->
                            <div class="description-block">
                                <div class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> </div>
                                <%-- [<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>] --%>
                                <div class="description-text"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%> </div> <%-- [<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>] --%>
                                <div class="description-text"><%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%> </div>
                                <div class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></div>
                                <%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
									<div class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong> </div>
                                <% } else { %>
									<div class="description-text">You don't have a reporting manager.</div>
                                <% } %>
                            </div>
                            <!-- /.description-block -->
                        <!-- </div> -->
                    </div>
                    <!-- /.row -->
                    <% String feedsCount = (String) request.getAttribute("feedsCount"); 
                    String totalNoticeCount = (String) request.getAttribute("totalNoticeCount");
                    String eventsCount = (String) request.getAttribute("eventsCount");
                    String quotesCount = (String) request.getAttribute("quotesCount");
                    String manualCount = (String) request.getAttribute("manualCount");
                    String faqCount = (String) request.getAttribute("faqCount");
                    %>
                    <div class="row" style="margin: 0px -10px 1px; border-bottom: 1px solid #CFCFCF">
                        <div class="description-block" style="float: left; width: 100%;">
                            <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" onclick="getFeeds();" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-rss" aria-hidden="true"></i> Feeds<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=feedsCount %></div></a> </div>
                            <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" onclick="getPeople();" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-group" aria-hidden="true"></i> People<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%="0" %></div></a> </div>
                            <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" onclick="getFolderAndFiles('0','C','');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-folder-open" aria-hidden="true"></i> Files<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%="0" %></div></a> </div>
                            <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" onclick="getHubContent('A');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-bullhorn" aria-hidden="true"></i> Announcements<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=totalNoticeCount %></div></a> </div>
                            <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" onclick="getHubContent('E');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-calendar-o" aria-hidden="true"></i> Events<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=eventsCount %></div></a> </div>
                            <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" onclick="getHubContent('Q');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-quote-right" aria-hidden="true"></i> Quotes<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=quotesCount %></div></a> </div>
                            <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" onclick="getHubContent('M');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-book" aria-hidden="true"></i> Manual<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=manualCount %></div></a> </div>
                            <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" onclick="getHubContent('FAQ');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-question-circle" aria-hidden="true"></i> FAQs<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=faqCount %></div></a> </div>
                        </div>
                        <!-- /.description-block -->
                    </div>
                    
                    <div class="box box-default collapsed-box">
						<div class="box-header with-border">
						    <h3 class="box-title"><b>Channels</b></h3>
						    <div class="box-tools pull-right">
						        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						    </div>
						</div>
						<div class="box-body" style="padding: 5px; overflow-y: auto;">
							<div class="row row_without_margin">
								<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
										<p style="padding-left: 5px;"><b>Clients</b></p>
									</div>
									<% if(hmClientName!=null && hmClientName.size()>0) {
										Iterator<String> it = hmClientName.keySet().iterator();
										while(it.hasNext()) {
										String strClientId = it.next();
									%>
										<div class="col-lg-12 col-md-12 col-sm-12 paddingleftright5">
											<p style="padding-left: 5px;"><%=uF.showData(hmClientName.get(strClientId), "") %></p>
										</div>
										<% } %>
									<% } %>
								</div>
								
								<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
										<p style="padding-left: 5px;"><b>Projects</b></p>
									</div>
									<% if(hmProjectName!=null && hmProjectName.size()>0) {
										Iterator<String> it = hmProjectName.keySet().iterator();
										while(it.hasNext()) { 
										String strProId = it.next();
									%>
										<div class="col-lg-12 col-md-12 col-sm-12 paddingleftright5">
											<p style="padding-left: 5px;"><%=uF.showData(hmProjectName.get(strProId), "") %></p>
										</div>
										<% } %>
									<% } %>
								</div>
								
								<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
										<p style="padding-left: 5px;"><b>Tasks</b></p>
									</div>
									<% if(hmTaskName!=null && hmTaskName.size()>0) {
										Iterator<String> it = hmTaskName.keySet().iterator();
										while(it.hasNext()) { 
										String strTaskId = it.next();
									%>
										<div class="col-lg-12 col-md-12 col-sm-12 paddingleftright5">
											<p style="padding-left: 5px;"><%=uF.showData(hmTaskName.get(strTaskId), "") %></p>
										</div>
										<% } %>
									<% } %>
								</div>
							</div>
						</div>
					</div>
					
					
                </div>
            </div>
            
        </section>
        
        <section class="col-sm-12 col-md-6 col-lg-7 connectedSortable">
            <div class="box box-widget widget-user">
            	<div id="divResult"> </div>
            </div>
        </section>
        
    </div>
</section>


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

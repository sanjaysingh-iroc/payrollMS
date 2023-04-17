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

.description-block .description-header a{ 
    color: "#898C92";
    text-decoration: none;
}

.description-block .description-header a:hover{ 
	color: #4d4e4b;
    text-decoration: underline;

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
           } else {
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

    	getFeeds();
    	
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
    
  	
    function getFeeds() {
    	
    	setLinkBackground('Feed');
    	document.getElementById("divResult").innerHTML = '<div id="the_div"><div id="ajaxLoadImage"></div></div>';
    	getContent('divResult', 'Feeds.action?pageFrom=MyHub');
    }
	
    function setLinkBackground(id){
    	//debugger;
    
    	$(".description-header > a:not(#"+id+")").css({color:"#898C92"})
    	$('#'+id).css({ color: "#4d4e4b" });
    	$(window).scrollTop(0);
    }
    
    
    function getHubContent(type) {
    	/* $("divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>'); */
    	setLinkBackground(type);
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
    
    Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
    Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
    String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
    
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
    %>

<section class="content" style="background-color: #f3f3f359 !important;min-height: 200px !important; ">
    <div class="row jscroll">
    
    
    
    <section class="col-sm-12 col-md-6 col-lg-3 connectedSortable"> <!-- style="margin-top: 2%;" position: fixed; -->
            <div class="box box-widget widget-user widget-user1" style="font-family: -apple-system,system-ui,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica Neue,Fira Sans,Ubuntu,Oxygen,Oxygen Sans,Cantarell,Droid Sans,Apple Color Emoji,Segoe UI Emoji,Segoe UI Symbol,Lucida Grande,Helvetica,Arial,sans-serif;">
               	 <!-- Add the bg color to the header using any of the bg-* classes -->
                <!-- <div class="widget-user-header bg-aqua-active"> -->
               <!-- ====start parvez on 27-10-2022===== --> 
                    <%-- <img class="lazy" style="width: 98% !important;position: absolute;" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'> --%>
                    <%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
						List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
					%>
					<div class="widget-user-header bg-aqua-active" style="height:100px !important">
						<img class="lazy" style="width: 98% !important;position: absolute; height:auto !important;" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
					<% } else{ %>
					<div class="widget-user-header bg-aqua-active">
						<img class="lazy" style="width: 98% !important;position: absolute;" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
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
                <div class="box-footer" style="padding-top: 7% !important;">
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
                    <div class="row" style="margin: 0px -10px;">
                            <div class="description-block" style="float: left; width: 100%;">
                                <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" id='Feed' onclick="getFeeds();" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-rss" aria-hidden="true"></i> Feeds<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=feedsCount %></div></a> </div>
                                <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" id='A' onclick="getHubContent('A');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-bullhorn" aria-hidden="true"></i> Announcements<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=totalNoticeCount %></div></a> </div>
                                <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" id='E' onclick="getHubContent('E');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-calendar-o" aria-hidden="true"></i> Events<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=eventsCount %></div></a> </div>
                                <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" id='Q'  onclick="getHubContent('Q');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-quote-right" aria-hidden="true"></i> Quotes<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=quotesCount %></div></a> </div>
                                <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" id='M' onclick="getHubContent('M');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-book" aria-hidden="true"></i> Manual<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=manualCount %></div></a> </div>
                                <div class="description-header" style="float: left; width: 100%; margin: 1px;"><a href="javascript:void(0);" id='FAQ' onclick="getHubContent('FAQ');" class="small-box-footer"><div style="float: left; padding-left: 10px;"><i class="fa fa-question-circle" aria-hidden="true"></i> FAQs<i class="fa fa-arrow-circle-right"></i></div> <div style="float: right;; padding-right: 30px;"><%=faqCount %></div></a> </div>
                                <%-- <div class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></div> --%>
                            </div>
                            <!-- /.description-block -->
                    </div>
                </div>
            </div>
            
            
            <div class="box box-success">
                <div class="box-header with-border">
                    <h3 class="box-title">My Updates</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                    <div class="widget-content nopadding updates" id="collapseG3" style="height: auto;">
                        <%
                            String strThought = (String) request.getAttribute("DAY_THOUGHT_TEXT");
                            String strThoughtBy = (String) request.getAttribute("DAY_THOUGHT_BY");
                            if(strThought!=null) {
                            %>
                        <div class="new-update clearfix">
                            <i class="fa fa-lightbulb-o"></i>
                            <div class="update-done"><%=strThought %><span><strong>- <%=strThoughtBy %></strong></span> </div>
                        </div>
                        <%} 
                            Map<String,List<String>> hmEventUpdates = (Map<String,List<String>>) request.getAttribute("eventUpdates");
                            Map<String,List<String>> hmQuoteUpdates = (Map<String,List<String>>) request.getAttribute("quoteUpdates");
                            Map<String,List<String>> hmNoticeUpdates = (Map<String,List<String>>) request.getAttribute("noticeUpdates");
                            List<String> holidayList = (List<String>) request.getAttribute("holidays");
                            if(holidayList == null ) holidayList = new ArrayList<String>();
                            if(hmEventUpdates == null){
                            	hmEventUpdates = new LinkedHashMap<String,List<String>>();
                            }
                            
                            if(hmQuoteUpdates == null){
                            	hmQuoteUpdates = new LinkedHashMap<String,List<String>>();
                            }
                            if(hmNoticeUpdates == null){
                            	hmNoticeUpdates = new LinkedHashMap<String,List<String>>();
                            }
                            
                            if(hmQuoteUpdates != null && hmQuoteUpdates.size()>0){
                            	Set<String> quoteSet = hmQuoteUpdates.keySet();
                            	Iterator<String> qit = quoteSet.iterator();
                            	while(qit.hasNext()){
                            		String quoteId = qit.next();
                            		List<String> quoteList  = hmQuoteUpdates.get(quoteId);  
                            		if(quoteList == null ) quoteList = new ArrayList<String>();
                            		if(quoteList != null && quoteList.size()>0){
                            			
                            %>
                        <div class="new-update clearfix">
                            <i class="fa fa-lightbulb-o"></i>
                            <div class="update-done"><%=quoteList.get(1) %><span><%=quoteList.get(2) %></span> </div>
                            <div class="update-date"><span class="update-day"><%=quoteList.get(4) %></span><%=quoteList.get(5) %></div>
                        </div>
                        <%
                            }
                            }
                            }
                            %>
                        <%
                            String strResignationStatus = (String) request.getAttribute("RESIG_STATUS");
                            String nRemaining = (String) request.getAttribute("RESIGNATION_REMAINING");
                            String strResignationStatusD = (String) request.getAttribute("RESIGNATION_STATUS_D");
                            String strRADay = (String) request.getAttribute("strRADay");
                            String strRAMonth = (String) request.getAttribute("strRAMonth");
                            if(strResignationStatusD!=null) {
                            %>
                        <div class="new-update clearfix">
                            <i class="fa fa-bell-o"></i>
                            <div class="update-done"><%=strResignationStatusD %>
                                <%if(uF.parseToInt(strResignationStatus) == 1) { %>
                                <span><%=uF.showData(nRemaining+"", "0") %></span> 
                                <% } %>
                            </div>
                            <div class="update-date"><span class="update-day"><%=strRADay %></span><%=strRAMonth %></div>
                        </div>
                        <% } %>
                        <%
                            String nMailCount = (String)request.getAttribute("MAIL_COUNT");
                            if(uF.parseToInt(nMailCount)>0) { %>
                        <div class="new-update clearfix">
                            <i class="fa fa-envelope-o"></i>
                            <div class="update-done">You have <a href="MyMail.action" title="My Mail"><strong><%=nMailCount %> new</strong></a> mails.</div>
                        </div>
                        <% } %>
                        <%
                            List<List<String>> alBirthDays = (List<List<String>>)request.getAttribute("alBirthDays");
                            
                            for(int i=0; alBirthDays!=null && i<alBirthDays.size(); i++) { 
                            	List<String> innerList = alBirthDays.get(i);
                            	if(innerList.size()>0) {
                            %>
                        <div class="new-update clearfix">
                            <i class="fa fa-birthday-cake"></i>
                            <div class="update-done"><%=innerList.get(0)%></div>
                            <div class="update-date"><span class="update-day"><%=innerList.get(1)%></span><%=innerList.get(2)%></div>
                        </div>
                        <% } %>
                        <% } %>
                       
                  <!-- ===start parvez date: 28-10-2022===  -->      
                        <%
                            List<List<String>> alWorkAnniversary = (List<List<String>>)request.getAttribute("alWorkAnniversary");
                            
                            for(int i=0; alWorkAnniversary!=null && i<alWorkAnniversary.size(); i++) { 
                            	List<String> innerList = alWorkAnniversary.get(i);
                            	if(innerList.size()>0) {
                        %>
			                        <div class="new-update clearfix">
			                            <i class="fa fa-birthday-cake"></i>
			                            <div class="update-done"><%=innerList.get(0)%></div>
			                            <div class="update-date"><span class="update-day"><%=innerList.get(1)%></span><%=innerList.get(2)%></div>
			                        </div>
                        	<% } %>
                        <% } %>
                <!-- ===end parvez date: 28-10-2022=== -->        
                        
                        <%
                            if(hmNoticeUpdates != null && hmNoticeUpdates.size()>0) {	
                            	Set<String> noticeSet = hmNoticeUpdates.keySet();
                            	Iterator<String> nit = noticeSet.iterator();
                            	while(nit.hasNext()){
                            		String noticeId = nit.next();
                            		List<String> noticeList  = hmNoticeUpdates.get(noticeId);  
                            		if(noticeList == null ) noticeList = new ArrayList<String>();
                            		if(noticeList != null && noticeList.size()>0) {
                            %>
                        <div class="new-update clearfix">
                            <i class="fa fa-bullhorn"></i>
                            <div class="update-done"><%=noticeList.get(2) %>
                                <span><a href="<%=noticeList.get(0) %>">
                                <i class="fa fa-forward" aria-hidden="true" title="Go to Announcements.."></i>
                                
                                </a></span> 
                            </div>
                            <div class="update-date"><span class="update-day"><%=noticeList.get(8) %></span><%=noticeList.get(9) %></div>
                        </div>
                        <%
                            }
                            }
                            }
                            %>
                        <%
                            if(hmEventUpdates != null && hmEventUpdates.size()>0){	
                            	Set<String> eventSet = hmEventUpdates.keySet();
                            	Iterator<String> eit = eventSet.iterator();
                            	while(eit.hasNext()){
                            		String eventId = eit.next();
                            		List<String> eventList  = hmEventUpdates.get(eventId);  
                            		if(eventList == null ) eventList = new ArrayList<String>();
                            		if(eventList != null && eventList.size()>0){
                            %>
                        <div class="new-update clearfix">
                            <i class="fa fa-calendar-o"></i>
                            <div class="update-done">
                                <a href="<%=eventList.get(0) %>"><strong><%=eventList.get(2) %></strong></a> organised at <%=eventList.get(6) %> 
                                from <%=eventList.get(4)%> to <%=eventList.get(5)%> 
                            </div>
                        </div>
                        <%
                            }
                            }
                            }
                            if(holidayList != null && holidayList.size()>0){
                            Iterator hit  = holidayList.iterator();
                            while(hit.hasNext()){
                            	String holidayData = (String) hit.next();	
                            
                            %>		
                        <div class="new-update clearfix">
                            <i class="fa fa-bell-o"></i>
                            <div class="update-done"><%=holidayData%></div>
                        </div>
                        <%
                            }
                            }
                            %>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            
            
            <div class="box box-danger">
                <%//if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { 
                    Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
                    if(hmEmp==null) hmEmp=new HashMap<String,String>();
                   	Map<String,String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
                   	if(empImageMap==null) empImageMap=new HashMap<String,String>();
                    %> 
                <div class="box-header with-border">
                    <h3 class="box-title">My Team</h3>
                    <div class="box-tools pull-right">
                        <span class="label label-danger"><%=hmEmp.size() %> Members</span>
                        <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                        </button>
                        <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i>
                        </button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body no-padding" style="max-height: 350px;overflow-y:auto;">
                    <ul class="users-list clearfix">
                        <%
                            Iterator<String> it=hmEmp.keySet().iterator();
                            int i=0;
                            while(it.hasNext()) {
                            	i++;
                            	String empId=it.next();
                            	String empName=hmEmp.get(empId);
                            	if(i > 24) {
                            break;
                            }
                            %>
                        <li>
                            <a href="javascript:void(0);" onclick="getEmpProfile('<%=empId %>');" title="<%=empName %>">
                            <%if(docRetriveLocation==null) { %>
                            <img class="lazy img-circle" src="userImages/avatar_photo.png" style="height: auto !important; max-height: 60px;" data-original="<%=IConstants.DOCUMENT_LOCATION + empImageMap.get(empId.trim())%>" >
                            <% } else { %>
                            <img class="lazy img-circle" src="userImages/avatar_photo.png" style="height: auto !important; max-height: 60px;" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId.trim()+"/"+IConstants.I_60x60+"/"+empImageMap.get(empId.trim())%>" />
                            <% } %>    
                            <span class="users-list-name"><%=empName %></span>
                            </a>
                        </li>
                        <%  } %>
                        <% if(hmEmp == null || hmEmp.isEmpty()) { %>
                        <div class="content1" style="max-height: 300px;padding: 5px;">
                            <div class="tdDashLabel">No team available.</div>
                        </div>
                        <% } %>
                    </ul>
                    <!-- /.users-list -->
                </div>
                <!-- /.box-body -->
                <%if(hmEmp != null && hmEmp.size() > 24) { %>
                <div class="box-footer text-center">
                    <a href="javascript:void(0);" onclick="getTeamMembers();" class="uppercase">View All Users</a>
                </div>
                <%} %>
                <!-- /.box-footer -->
            </div>
            
            
        </section>
        
        <section class="col-sm-12 col-md-6 col-lg-3 connectedSortable" style="float: right;">  <!-- margin-top: 2%; padding-top: 2%; -->
            
            <div class="box box-primary">
                <% List<List<String>> alJobList = (List<List<String>>) request.getAttribute("alJobList"); %> 
                <div class="box-header with-border">
                    <h3 class="box-title">Live Jobs</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                    <div class="rosterweek" >
                        <div class="content1">
                            <div class="holder">
                                <div style="width:100%; float:left;">
                                    <% for(i=0; alJobList !=null && i<alJobList.size(); i++) { 
                                    	String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+ "0123456789" + "abcdefghijklmnopqrstuvxyz";
                        				int n=25;
                        				StringBuilder sb = new StringBuilder(n); 
                        		        for (int a=0; a<n; a++) {
                        		            int index = (int)(AlphaNumericString.length() * Math.random()); 
                        		            sb.append(AlphaNumericString.charAt(index)); 
                        		        }
                        		        List<String> alInner = alJobList.get(i);
                                    %>
                                    <div style="margin: 5px 0px;">
                                    	<a href="JobOpportunities.action?RecrtNm=<%=sb.toString() %>&strRecruitId=<%=alInner.get(0) %>&refEmpId=<%=strEmpId %>"><%=uF.showData(alInner.get(2), "") %></a>
	                                    <span style="float: right; font-weight: bold; padding-right: 10px;" ><%=uF.showData(alInner.get(3), "") %></span>
	                                    <!-- <a style="color: #ff8826;" href="Login.action?role=3&product=3&userscreen=CEODashboard"><i class="fa fa-caret-right" aria-hidden="true"></i><u>CEO Dashboard (Project)</u></a> -->
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            
            
            <div class="box box-primary">
                <% 
                    String BASEUSERTYPE = (String)session.getAttribute(IConstants.BASEUSERTYPE);
                    String poFlag = (String)request.getAttribute("poFlag"); 
                    %> 
                <div class="box-header with-border">
                    <h3 class="box-title">Quick Links</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 300px;">
                    <div class="rosterweek" >
                        <div class="content1">
                            <div class="holder">
                                <div style="width:100%; float:left;">
                                    <% if(CF.isTaskRig() && BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.CEO)) { %>
                                    <div style="margin: 5px 0px;"><a style="color: #ff8826;" href="Login.action?role=3&product=3&userscreen=CEODashboard"><i class="fa fa-caret-right" aria-hidden="true"></i><u>CEO Dashboard (Project)</u></a></div>
                                    <% } %>
                                    <% if(CF.isWorkRig() && BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.CEO)) { %>
                                    <div style="margin: 5px 0px;"><a style="color: #ff8826;" href="Login.action?role=2&product=2"><i class="fa fa-caret-right" aria-hidden="true"></i><u>CEO Dashboard (HR)</u></a></div>
                                    <% } %>
                                    <% if(CF.isWorkRig()) { %>
                                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
                                    <div style="margin: 5px 0px;"><a href="MyTime.action?callFrom=MyDashLeaveSummary"><i class="fa fa-caret-right" aria-hidden="true"></i>Apply Leave</a></div>
                                    <% } %>
                                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>
                                    <div style="margin: 5px 0px;"><a href="MyPay.action?callFrom=MyDashReimbursements"><i class="fa fa-caret-right" aria-hidden="true"></i>Apply Reimbursement</a></div>
                                    <% } %>	
                                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
                                    <div style="margin: 5px 0px;"><a href="MyPay.action"><i class="fa fa-caret-right" aria-hidden="true"></i>Check Payroll</a></div>
                                    <% } %>
                                    <div style="margin: 5px 0px;"><a href="MyDashboard.action"><i class="fa fa-caret-right" aria-hidden="true"></i>My Dashboard</a></div>
                                    <div style="margin: 5px 0px;"><a href="MyProfile.action"><i class="fa fa-caret-right" aria-hidden="true"></i>My Profile</a></div>
                                    <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_HOME_MY_TEAM)) && hmFeatureUserTypeId.get(IConstants.F_MY_HOME_MY_TEAM)!=null && hmFeatureUserTypeId.get(IConstants.F_MY_HOME_MY_TEAM).contains(strUsertypeId)) { %>
                                    <div style="margin: 5px 0px;"><a href="OrganisationalChart.action"><i class="fa fa-caret-right" aria-hidden="true"></i>My Team</a></div>
                                    <% } %>
                                    <!--code onboard_processor  -->
                                    
 										<div class="col-md-12 col_no_padding" style="margin: 5px 0px;">
									        <a href="javascript:void(0)" onclick="onBoardProcessingFun()" title="onBoard Processing"><i class="fa fa-caret-right" aria-hidden="true"></i>OnBoard Processing</a>
										</div>
                                   
                                    <!-- end of onboard_processor -->
                                    <% } %>
                                    <% if(CF.isTaskRig()) { %>
                                    <% if(uF.parseToBoolean(poFlag) || BASEUSERTYPE != null && BASEUSERTYPE.equals(IConstants.MANAGER)) { %>
                                    <div style="margin: 5px 0px;"><a href="Login.action?role=3&product=3&userscreen=myProjects"><i class="fa fa-caret-right" aria-hidden="true"></i>Go to Projects</a></div>
                                    <% } else if(BASEUSERTYPE != null && (BASEUSERTYPE.equals(IConstants.HRMANAGER) || BASEUSERTYPE.equals(IConstants.ADMIN))) { %>
                                    <div style="margin: 5px 0px;"><a href="Login.action?role=3&product=3&userscreen=allProjects"><i class="fa fa-caret-right" aria-hidden="true"></i>Go to Projects</a></div>
                                    <% } %>
                                    <div style="margin: 5px 0px;"><a href="Login.action?role=3&product=3&userscreen=myTimesheet"><i class="fa fa-caret-right" aria-hidden="true"></i>Update Timesheets</a></div>
                                    <div style="margin: 5px 0px;"><a href="Login.action?role=3&product=3"><i class="fa fa-caret-right" aria-hidden="true"></i>My Project Dashboard</a></div>
                                    <div style="margin: 5px 0px;"><a href="Login.action?role=3&product=3&userscreen=myWorkTasks"><i class="fa fa-caret-right" aria-hidden="true"></i>My Work</a></div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
            
            
            <div class="box box-info">
                <%List<String> event = (List<String>)request.getAttribute("alInner"); %>
                <div class="box-header with-border">
                    <h3 class="box-title">Upcoming Event</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
                    <div class="rosterweek" style="width: 100%;">
                        <div class="content1">
                            <div class="holder" style="padding: 0px;">
                                <% if(event != null && !event.isEmpty() ) { %>
                                <div id="mainEventDiv">
                                    <div style="float: left;  width: 100%; padding: 5px 5px;">
                                        <div id="eventDataDiv" style="float: left; width: 100%;">
                                            <div style="float:left;width:100%;">
                                                <div style="float: left;width:94%; margin:7px;">
                                                    <% if(event.get(9) != null && !event.get(9).equals("")) { %>	
                                                    <a href="javascript:void(0)" onclick="openEventPopup(<%=event.get(0)%>)"><%=event.get(12)%></a>
                                                    <% } else { %>
                                                  
                                                    <a href="javascript:void(0)" onclick="openEventPopup(<%=event.get(0)%>)">
                                                        <div style="float:left; padding: 30px 40px; font-size: 14px; border: 1px solid #CCCCCC;">Image Preview not available</div>
                                                    </a>
                                                    <% } %>							
                                                </div>
                                            </div>
                                            <div style="float:left;width:95%;margin:3px 7px 3px 7px;">
                                                <div style="float: left; width: 100%;">
                                                    <div style="float: left;width:100%;font-size:14px;font-weight:bold;color:#00688B;margin-top:10px;font-style: italic; margin-left:7px;"><a href="javascript:void(0)" onclick="openEventPopup(<%=event.get(0)%>)"><%=event.get(4)%></a></div>
                                                </div>
                                                <div style="float: left; width: 100%;margin-bottom: -4px;">
                                                    <div id="blueLikeDiv_<%=event.get(0) %>" style="display: block;font-size:12px;float: left;color:gray;width:90%;margin-right: 5px">
                                                        <div style="float: left; margin-left: 7px; margin-top: -2px;"> Organised at <%=event.get(7) %> from <%=event.get(1) %> to <%=event.get(2) %></div>
                                                    </div>
                                                    <div id="grayLikeDiv_<%=event.get(0) %>" style="display: block;font-size:12px;width:90%;color:gray; float: left; margin-right: 5px;">
                                                        <div style="float: left; margin-left: 7px; margin-top: -2px;">Timing: <%=event.get(14)%> To <%=event.get(15)%></div>
                                                    </div>
                                                </div>
                                                <div style="float: left; width: 92%;margin-left:15px;">
                                                    <div class="eventExpandDiv">
                                                        <p style="font-size:11px;padding-top:5px;"><%=event.get(5) %></p>
                                                    </div>
                                                </div>
                                                <div style="float: left;width:96%;font-style:italic;font-size:11px;margin-left:7px;color: gray;">Posted on: <%=event.get(3) %>.</div>
                                                <%-- <div style="float: left; width:96%;margin-left: 5px;margin-top:-7px;font-style:italic;font-size:11px;color:gray;">Shared with: <%=event.get(8) %> .</div> --%>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <% } else { %>
                                <div id="mainEventDiv">
                                    <div style="float:left; width:100%;">
                                        <img src="images1/no-events.jpg" style="width: 100%;">
                                    </div>
                                </div>
                                <% } %>
                                <div class="clr"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- /.box-body -->
                <div class="box-footer text-center">
                    <a href="Hub.action?type=E">View All Events</a>
                </div>
            </div>
            
        </section>
        
        
        <section class="col-sm-12 col-md-6 col-lg-6 connectedSortable" style="float: right;">  <!-- margin-top: 2%; padding-top: 2%; -->
            <div class="box box-widget widget-user" style="padding: unset; border: 0px none ! important;">
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








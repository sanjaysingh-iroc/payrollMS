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

$(document).ready(function(){
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
    <% String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
    %>
    
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
    			
    			function viewEventFilePopup(eventId) {
    				var dialogEdit = '#modalInfo .modal-body';
	   				 $(dialogEdit).empty();
	   				 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   				 $("#modalInfo").show(); 
	   				 $("#modalInfo .modal-title").html('View Event File ');
	   				 $.ajax({
	   					 url : 'ViewEventFilePopup.action?strEventId='+eventId,
	   		                cache : false,
	   		                success : function(data) {
	   		                	//alert("data==>"+data);
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

    			function editNoticePopup(noticeId, type){
    				var dialogEdit = '#modalInfo .modal-body';
    				$(dialogEdit).empty();
    				$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    				$("#modalInfo").show();
    				$("#modalInfo .modal-title").html('Edit FAQ');
    				$.ajax({
   		                url : 'EditAndDeleteNotice.action?noticeId='+noticeId+'&operation='+type,
   		                cache : false,
   		                success : function(data) {
   		                	//alert("data==>"+data);
   		                	$(dialogEdit).html(data);  		                	
   		                }
   		            });
    			 }
    			 
    			
    			 
    			function editYourEvent(eventId, type) {
    				
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
    			    	if(type == 'E_E') {
    			            var xhr = $.ajax({
    			                url : 'EditAndDeleteEvent.action?eventId='+eventId+'&operation='+type,
    			                cache : false,
    			                success : function(data) {
    			                	document.getElementById("eventDataDiv_"+eventId).innerHTML = data;
    			                	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    			                }
    			            });
    		           	} else {
    		           		if(confirm("Are you sure, you want to delete this Event? ")) {
    		           			var xhr = $.ajax({
    		    	                url : 'EditAndDeleteEvent.action?eventId='+eventId+'&operation='+type,
    		    	                cache : false,
    		    	                success : function(data) {
    		    	                	//alert("postId ===>> " + postId);
    		    	                	document.getElementById("mainEventDiv_"+eventId).innerHTML = '';
    		    	               
    								}
    							});
    						}
    					}
    				}
    			}
    				
    				
    				function updateCancelEvent(eventId, type) {
    					
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
    				    	//alert("Are you sure, you want to update this Event? for " +eventId+"==opeartion==>"+type);
    				    	var strEventName1 = "";
    				    	var strSharing1 = "";
    				    	var strEventdesc1 ="";
    				    	var strStartDate1 = "";
    				    	var strEndDate1 = "";
    				    	var startTime1 = "";
    				    	var endTime1 = "";
    				    	var strEventImage1 = "";
    				    	var strLocation1 = "";
    				    	//alert("event=="+ document.getElementById("strEventName_"+eventId).value+"event_desc"+document.getElementById("strEventdesc_"+eventId).value);
    				    	
    				    	if( document.getElementById("strEventName_"+eventId)){
    				    		strEventName1 = document.getElementById("strEventName_"+eventId).value;
    				    		
    				    	}
    				    	if(document.getElementById("strSharing_"+eventId)){
    				    		strSharing1 = getSelectedValue("strSharing_"+eventId); 
    				    		
    				    	}
    				    	if(document.getElementById("strEventdesc_"+eventId)){
    				    		strEventdesc1 = document.getElementById("strEventdesc_"+eventId).value; 
    				    	}
    				    	 
    				    	if(document.getElementById("strLocation_"+eventId)){
    				    		strLocation1 = document.getElementById("strLocation_"+eventId).value;  
    				    	}
    				    	if(document.getElementById("strStartDate_"+eventId)){
    				    		strStartDate1 = document.getElementById("strStartDate_"+eventId).value; 
    				    	}
    				    	if(document.getElementById("strEndDate_"+eventId)){
    				    		strEndDate1 = document.getElementById("strEndDate_"+eventId).value; 
    				    	}
    				    	if(document.getElementById("startTime_"+eventId)){
    				    		startTime1 = document.getElementById("startTime_"+eventId).value; 
    				    	}
    				    	if(document.getElementById("endTime_"+eventId)){
    				    		endTime1 = document.getElementById("endTime_"+eventId).value; 
    				    	}
    				    	
    				    	if(document.getElementById("strEventImage_"+eventId)){
    				    		strEventImage1 = document.getElementById("strEventImage_"+eventId).files[0]; 
    				    	}

    				    	
    				    	
    				 		//alert("emage url==>"+strEventImage1);
    				 		if( type == 'U'){
    				 			//alert("emage url==>"+strEventImage1.files[0]);
    				 			if(strEventName1 != "" &&  strLocation1 !="" && strEventdesc1 != "" && strStartDate1 != "" && strEndDate1 != "" && startTime1 != "" && endTime1!= ""){
    					            var xhr = $.ajax({
    					                url : 'EditAndDeleteEvent.action?eventId='+eventId+'&operation='+type+'&strEventName1='+strEventName1+'&strSharing1='+strSharing1+'&strEventdesc1='+strEventdesc1
    					                		+'&strLocation1='+strLocation1+'&strStartDate1='+strStartDate1+'&strEndDate1='+strEndDate1+'&startTime1='+startTime1+'&endTime1='+endTime1,
    					               //	type  : 'POST',
    					               // data  :	formData,	
    					                cache : false,
    					              //  processData: false,
    					              //  contentType: false,
    					                success : function(data) {
    					                	//alert("data ===>> " + data);
    					                	document.getElementById("eventDataDiv_"+eventId).innerHTML = data;
    					                	
    					                	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    					                	 
    					                }
    					            });
    						   	 }else{
    						   		alert("Enter Required Fields Data...!");
    						   	 }
    				    	}
    					 		
    					 	if(type == 'C'){
    					 		var xhr = $.ajax({
    				                url : 'EditAndDeleteEvent.action?eventId='+eventId+'&operation='+type+'&strEventName1='+strEventName1+'&strSharing1='+strSharing1+'&strEventdesc1='+strEventdesc1
    				                		+'&strLocation1='+strLocation1+'&strStartDate1='+strStartDate1
    				                		+'&strEndDate1='+strEndDate1+'&startTime1='+startTime1+'&endTime1='+endTime1,
    				                cache : false,
    				                success : function(data) {
    				                	//alert("data ===>> " + data);
    				                	document.getElementById("eventDataDiv_"+eventId).innerHTML = data;
    				                	
    				                	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    				                	  
    				                }
    				            }); 
    					 	 }	
    					}
    				}
    				
    				
    			function getSelectedValue(selectId) {
    				var choice = document.getElementById(selectId);
    				var exportchoice = "";
    				for ( var i = 0, j = 0; i < choice.options.length; i++) {
    					var value = choice.options[i].value;
    					if(choice.options[i].selected == true && value != "") {
    						
    						if (j == 0) {
    							exportchoice = "," + choice.options[i].value + ",";
    							j++;
    						} else {
    							exportchoice += choice.options[i].value + ",";
    							j++;
    						}
    					}else if(choice.options[i].selected == true && value == ""){
    						exportchoice = "";
    						break;
    					}
    					
    				}
    				//alert("exportchoice==>"+exportchoice);
    				return exportchoice;
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
						}
					});
           			$("#faqDataDiv_"+faqId).remove();
				}
			} else {
				
				var strAction = "EditAndDeleteFaq.action?faqId="+faqId+"&operation="+operation;
				var dialogEdit = '.modal-body';
				$(dialogEdit).empty();
				$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$("#modalInfo").show();
				$(".modal-title").html('Add FAQ');
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
    		
    			
  			function editYourNotice(noticeId, type) {
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
  			    	if(type == 'E') {
  			    	//	alert("type ===>> " + type);
  			            var xhr = $.ajax({
  			                url : 'EditAndDeleteNotice.action?noticeId='+noticeId+'&operation='+type,
  			                cache : false,
  			                success : function(data) {
  			              //  alert("type ===>> " + type);
  			                	document.getElementById("noticeDataDiv_"+noticeId).innerHTML = data;
  			                	$( "#displayStartDate_"+noticeId).datepicker({format: 'dd/mm/yyyy'});
  			                	$( "#displayEndDate_"+noticeId).datepicker({format: 'dd/mm/yyyy'});
  			                	
  			                	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
  			                }
  			            });
  		           	} else {
  		           		if(confirm("Are you sure, you want to delete this Announcement? ")) {
  		           			var xhr = $.ajax({
  		    	                url : 'EditAndDeleteNotice.action?noticeId='+noticeId+'&operation='+type,
  		    	                cache : false,
  		    	                success : function(data) {
  		    	                	$("#mainNoticeDiv_"+noticeId).remove();
  								}
  							});
  						}
  					}
  				}
  			}
    			
    			
    			function updateCancelNotice(noticeId, type) {
    				//alert("inside update");
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
    			    	//$("input[name='btnUpdate']").val("Updating...");
    			    //	alert("Are you sure, you want to update this Notice? for " +noticeId+"==opeartion==>"+type);
    			    	var heading1 = "";
    			    	var content1 = "";
    			    	var displayStartDate1 ="";
    			    	var displayEndDate1 = "";
    			        var ispublish1 = "false";
    			    	if( document.getElementById("heading_"+noticeId)){
    			    		heading1 = document.getElementById("heading_"+noticeId).value;
    			    		
    			    	}
    			    	if(document.getElementById("content_"+noticeId)) {
    			    		content1 = document.getElementById("content_"+noticeId).value; 
    			    	}
    			    	if(document.getElementById("displayStartDate_"+noticeId)){
    			    		displayStartDate1 = document.getElementById("displayStartDate_"+noticeId).value; 
    			    	}
    			    	 
    			    	if(document.getElementById("displayEndDate_"+noticeId)){
    			    		displayEndDate1 = document.getElementById("displayEndDate_"+noticeId).value;  
    			    	}
    			    		
    			    	if(document.getElementById("ispublish1_"+noticeId)){		
    			    		ispublish1 = document.getElementById("ispublish1_"+noticeId).checked;  
    			    	}
    			    	
    			    	
    			 	
    			 		if(type == 'U' ){
    			 			if( heading1 != "" && content1 != "" && displayStartDate1 !="" && displayEndDate1 !=""){ 
    				            var xhr = $.ajax({
    				                url : 'EditAndDeleteNotice.action?noticeId='+noticeId+'&operation='+type+'&heading1='+heading1+'&content1='+content1+'&displayStartDate1='+displayStartDate1
    				                		+'&displayEndDate1='+displayEndDate1+'&ispublish1='+ispublish1,
    				                cache : false,
    				                success : function(data) {
    				                	$(".modal").hide();
    				                	//alert("data ===>> " + data);
    				                	document.getElementById("noticeDataDiv_"+noticeId).innerHTML = data;
    				                	
    				                	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    				                	
    				                }
    				            });
    				 		}else{
    				 			alert("Enter Required Fields Data...!");
    				 		}
    			    	}
    			 		
    			 		if(type == 'C'){
    			 			var xhr = $.ajax({
    			                url : 'EditAndDeleteNotice.action?noticeId='+noticeId+'&operation='+type+'&heading1='+heading1+'&content1='+content1+'&displayStartDate1='+displayStartDate1
    			                		+'&displayEndDate1='+displayEndDate1+'&ispublish1='+ispublish1,
    			                cache : false,
    			                success : function(data) {
    			                	$(".modal").hide();
    			                	//alert("data ===>> " + data);
    			                	document.getElementById("noticeDataDiv_"+noticeId).innerHTML = data;
   			                	
    			                	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    			                }
    			            });
    			 		}
    				}
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
    			
    			
    			function updateCancelQuotes(thoughtId, type) {
    				//alert("inside update");
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
    			    	//alert("Are you sure, you want to update this thought? for " +thoughtId+"==opeartion==>"+type);
    			    	var strQuoteBy1 = "";
    			    	var strQuotedesc1 = "";
    			    	
    			    	if( document.getElementById("strQuoteBy_"+thoughtId)){
    			    		var str = document.getElementById("strQuoteBy_"+thoughtId).value;
    			    		strQuoteBy1 = str.replace("&", "::");
    			    		
    			    	}
    			    	if(document.getElementById("strQuotedesc_"+thoughtId)){
    			    		var str = document.getElementById("strQuotedesc_"+thoughtId).value;
    			    		strQuotedesc1 = str.replace("&", "::");
    			    	}
    			    	
    			    	//alert("strQuoteBy1==>"+strQuoteBy1+'==>strQuotedesc1==>'+strQuotedesc1);   	 
    			    	if(type == 'U'){
    			    			 		
   				            var xhr = $.ajax({
   				                url : 'EditAndDeleteQuotes.action?thoughtId='+thoughtId+'&operation='+type+'&strQuoteBy1='+strQuoteBy1+'&strQuotedesc1='+strQuotedesc1,
   				                		
   				                cache : false,
   				                success : function(data) {
   				                	//alert("data ===>> " + data);
   				                	document.getElementById("quoteDataDiv_"+thoughtId).innerHTML = data;
   				                	
   				                	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
   				                }
   				            });
    				    	
    			    	}
    			    	
    				}
    			    $('.modal').hide();
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
    			
    	function editYourManual(orgId,type,manualId) {
    				//alert("orgId==>"+orgId+"\ttype==>"+type+"\tmanualId==>"+manualId);
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
    			    	
    		           		//alert("in delete");
    		           		if(confirm("Are you sure, you want to delete this Manual? ")) {
    		           			var xhr = $.ajax({
    		    	                url : 'AddCompanyManual.action?orgId='+orgId+'&D='+manualId+'&pageFrom=MyHub',
    		    	                cache : false,
    		    	                success : function(data) {
    		    	               // document.getElementById("manualDiv_"+manualId).innerHTML = '';
    		    	                document.getElementById("manualDiv_"+manualId).style.display = 'none';
    								}
    							});
    						}
    					}
    			}
    			
    			function addManual(){
    				
    				document.getElementById("addManual").style.display = 'block';
    				//alert("add_Manual");
    				document.getElementById("list_Manual").style.display = 'block';
    				document.getElementById("show_Manual").style.display = 'none';
    				document.getElementById("addLink").style.display = 'none';
    				document.getElementById("editLink").style.display = 'none';
    				/* var dialogEdit = '#modalInfo .modal-body';
      				 $(dialogEdit).empty();
      				 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
      				 $("#modalInfo").show();
      				 $("#modalInfo .modal-title").html('Add New Manual');
      				 if($(window).width() >= 900){
      					 $(".modal-dialog").width(900);
      				 }
			           $.ajax({
			                url : 'AddCompanyManual.action',
			                cache : false,
			                success : function(data) {
			                	$(dialogEdit).html(data);
			                }
			            }); */
    			}
    			
    			function viewManual(manualId, strManualIds) {
    				
    				alert("1");
    				var strIds = strManualIds.split(",");
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
    			    	alert("2");
    			    	var dialogEdit = '#modalInfo .modal-body';
	       				 $(dialogEdit).empty();
	       				alert("3");
	       				 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	       				 $("#modalInfo").show();
	       				alert("4");
	       				 $("#modalInfo .modal-title").html('Manual Preview');
	       				alert("5");
    			            var xhr = $.ajax({
    			                url : 'ViewCompanyManual.action?E='+manualId+'&pageFrom=MyHub',
    			                cache : false,
    			                success : function(data) {
    			                	//alert("data ===>> " + data);
    			                	/* if(document.getElementById("addLink")) {
    			                		document.getElementById("addLink").style.display = 'none';
    			                	}
    			                	if(document.getElementById("editLink")) {
    			                		document.getElementById("editLink").style.display = 'none';
    			                	} */
    			                	//document.getElementById("show_Manual").style.display = 'block';
    			                	//document.getElementById("addManual").style.display = 'block';
    			                	$(dialogEdit).html(data);
    			                	/* for(i =0;i<strIds.length;i++) {
    			                		if(strIds[i] == manualId) {
    			                			document.getElementById("manualDiv_"+strIds[i]).style.background = '#efefef';
    			                		} else {
    			                			document.getElementById("manualDiv_"+strIds[i]).style.background = '#fff';
    			                		}
    			                	} */
    			                }
    			            });
    		           	}
    				}
    			
    			function editManual(manualId,strManualIds){
    			//	alert("in edit manual strIds==>"+strManualIds);
    				//document.getElementById("addLink").style.display = 'none';
    				
    				var strIds = strManualIds.split(",");
    				
    				document.getElementById("manualDiv_"+manualId).style.background = '#efefef';
    				/* for(i =0;i<strIds.length;i++){
                		if(strIds[i] == manualId){
                			
                			document.getElementById("manualDiv_"+strIds[i]).style.background = '#efefef';
                		}else{
                			document.getElementById("manualDiv_"+strIds[i]).style.background = '#fff';
                		}
                	} */
    				
    			}

    function uploader(input, options) {
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
    
    String DOC_RETRIVE_LOCATION = (String)request.getAttribute("DOC_RETRIVE_LOCATION");
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
      	if (hmEmpProfile == null) {
      		
      		hmEmpProfile = new HashMap<String, String>();
      	}
      	String strEmpID = (String) session.getAttribute(IConstants.EMPID);
    %>

<section class="content">
    <div class="row jscroll">
    <section class="col-lg-12 connectedSortable">
        <div class="box box-primary">
            <div class="box-header with-border">

                <%
                   String t = (String) request.getAttribute("type");
                   if(t == null) t = "F";
                %>
                <div class="listMenu1">
                 <% String feedsCount = (String) request.getAttribute("feedsCount"); %>
                  <div class="col-lg-2 col-xs-6 paddingright0">
			          <!-- small box -->
			          <div class="small-box bg-aqua">
			            <div class="inner">
			              <h3><%=feedsCount%></h3>
			
			              <p>Feeds</p>
			            </div>
			            <div class="icon">
			              <i class="fa fa-rss" aria-hidden="true"></i>
			            </div>
			            <a href="Hub.action?type=F" class="small-box-footer">Show Feeds<i class="fa fa-arrow-circle-right"></i></a>
			          </div>
			        </div>
			        <% String totalNoticeCount = (String) request.getAttribute("totalNoticeCount"); %>
			        <div class="col-lg-2 col-xs-6 paddingright0">
			          <!-- small box -->
			          <div class="small-box bg-green">
			            <div class="inner">
			              <h3><%=totalNoticeCount%></h3>
			              <p>Announcements</p>
			            </div>
			            <div class="icon">
			              <i class="fa fa-bullhorn" aria-hidden="true"></i>
			            </div>
			            <a href="Hub.action?type=A" class="small-box-footer">Show Announcements<i class="fa fa-arrow-circle-right"></i></a>
			          </div>
			        </div>
			        <!-- ./col -->
			        <% String eventsCount = (String) request.getAttribute("eventsCount"); %>
			        <div class="col-lg-2 col-xs-6 paddingright0">
			          <!-- small box -->
			          <div class="small-box bg-yellow">
			            <div class="inner">
			              <h3><%=eventsCount%><sup style="font-size: 20px"></sup></h3>
			              <p>Events</p>
			            </div>
			            <div class="icon">
			              <i class="fa fa-calendar-o" aria-hidden="true"></i>
			            </div>
			            <a href="Hub.action?type=E" class="small-box-footer">Show Events <i class="fa fa-arrow-circle-right"></i></a>
			          </div>
			        </div>
			        <!-- ./col -->
			        <% String quotesCount = (String) request.getAttribute("quotesCount"); %>
			        <div class="col-lg-2 col-xs-6 paddingright0">
			          <!-- small box -->
			          <div class="small-box bg-blue">
			            <div class="inner">
			              <h3><%=quotesCount%></h3>
			              <p>Quotes</p>
			            </div>
			            <div class="icon">
			              <i class="fa fa-quote-left" aria-hidden="true"></i>
			            </div>
			            <a href="Hub.action?type=Q" class="small-box-footer">Show Quotes<i class="fa fa-arrow-circle-right"></i></a>
			          </div>
			        </div>
			        <!-- ./col -->
			         <% String manualCount = (String) request.getAttribute("manualCount"); %>
			        <div class="col-lg-2 col-xs-6 paddingright0">
			          <!-- small box -->
			          <div class="small-box bg-red">
			            <div class="inner">
			              <h3><%=manualCount%></h3>
			              <p>Manual</p>
			            </div>
			            <div class="icon">
			              <i class="fa fa-book" aria-hidden="true"></i>
			            </div>
			            <a href="Hub.action?type=M" class="small-box-footer">Show Manual<i class="fa fa-arrow-circle-right"></i></a>
			          </div>
			        </div>
			        <% String faqCount = (String) request.getAttribute("faqCount"); %>
			        <div class="col-lg-2 col-xs-6 paddingright0">
			          <!-- small box -->
			          <div class="small-box bg-grey">
			            <div class="inner">
			              <h3><%=faqCount%></h3>
			              <p>FAQs</p>
			            </div>
			            <div class="icon">
			              <i class="fa fa-book" aria-hidden="true"></i>
			            </div>
			              <a href="Hub.action?type=FAQ" class="small-box-footer">Show FAQs<i class="fa fa-arrow-circle-right"></i></a>
			      	  </div>
			        </div>
			        
			        <!-- ./col -->
			      </div>
            </div>

            <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                    <%-- <div style="float: left; width: 32%; margin-right: 12px;">
                        <%
                            if((type == null || type.equals("") || type.equals("A")) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {%>
                        <div id="listMenu" style="width:100%;margin-left:3px;margin-top:0px;height:200px; float:left;">
                            <fieldset >
                                <legend>Announcement Status</legend>
                                <div> <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/approved.png">Published And Live </div>
                                <div> <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/pending.png"> Waiting for Live </div>
                                <div> <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/re_submit.png">Unpublished </div>
                                <div> <img style="padding: 5px 5px 0 5px;" border="0" src="<%=request.getContextPath()%>/images1/icons/denied.png">Closed </div>
                            </fieldset>
                        </div>
                        <% } %>
                    </div> --%>
                    <div class="rightMenu" style="margin-top:2px;">
                        <div style="float:left; width:100%;">
                            <%
                                if(type != null && type.equals("E")) {    
                                
                                Map<String, List<String>> hmEvents = (Map<String, List<String>>) request.getAttribute("hmEvents");
                                Map<String, String> hmEventIds = (Map<String, String>)request.getAttribute("hmEventIds");
                                List<String> availableExt = (List<String>)request.getAttribute("availableExt");
                                if(availableExt == null) availableExt = new ArrayList<String>();
                                
                                if(hmEvents==null){
                                	hmEvents = new LinkedHashMap<String, List<String>>();
                                }
                                if(hmEventIds==null){
                                	hmEventIds = new LinkedHashMap<String, String>();
                                }
                                Set<String> setEvents = hmEventIds.keySet();
                                Iterator<String> it = setEvents.iterator();
                                
                                
                                %>	
                                <% 
                                    if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) {	
                                    %>
                                    <p><a href="javascript:{}" data-toggle="modal" data-target="#addEventModal"><i class="fa fa-plus-circle"></i>Add Event</a></p>		 
	                                <div class="modal fade" id="addEventModal" role="dialog">
	                                	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
									    <div class="modal-dialog">
									      <!-- Modal content-->
									      <div class="modal-content">
									        <div class="modal-header">
									          <button type="button" class="close" data-dismiss="modal">&times;</button>
									          <h4 class="modal-title">Add Event</h4>
									        </div>
									        <div class="modal-body">
									          	<div style="float: left; width: 100%; padding-bottom: 5px;">
				                                    <s:form name="frm_events" id = "frm_event" action="Hub" theme="simple" method="Post" cssClass="formcss" enctype="multipart/form-data">
				                                        <s:hidden id ="type" name="type"></s:hidden>
				                                        <table class="table table_no_border form-table">
				                                        	<tr>
				                                        		<td>Title:<sup>*</sup></td>
				                                        		<td colspan="2"><s:textfield  name="strEventName" id="strEventName" cssStyle="font-size: 11px;" cssClass="validateRequired" ></s:textfield></td>
				                                        	</tr>
				                                        	<tr>
				                                        		<td>Event:<sup>*</sup></td>
				                                        		<td colspan="2"><s:textarea rows="3" name="strEventdesc" id="strEventdesc" cssClass="validateRequired" cssStyle="font-size: 11px; width: 78%;" ></s:textarea></td>
				                                        	</tr>
				                                        	<tr>
				                                        		<td>Share with:<sup>*</sup></td>
				                                        		<td colspan="2"><s:select name="strLevel" list="levelList" listKey="levelId" id="strLevel" listValue="levelCodeName" 
				                                                                required="true"  multiple="true" value="levelvalue" cssClass="validateRequired">
				                                                            </s:select></td>
				                                        	</tr>
				                                        	<tr>
				                                        		<td></td>
				                                        		<td>Start Date:<sup>*</sup><br/><s:textfield name="strStartDate" id="strStartDate" cssClass="validateRequired" cssStyle="width:85px;" ></s:textfield></td>
				                                        		<td>End Date:<sup>*</sup><br/><s:textfield name="strEndDate" id="strEndDate"  cssClass="validateRequired" cssStyle="width:85px"></s:textfield></td>
				                                        	</tr>
				                                        	<%-- <%
	                                                        String time = uF.timeNow();
	                                                           String newTime = time.substring(0,time.lastIndexOf(":"));
	                                                        %> --%>
				                                        	<tr>
				                                        		<td></td>
				                                        		<td>Start Time11:<sup>*</sup><br><input type="text" id="startTime" name="startTime" style="width:60px;" class="validateRequired startTime"/></td>
				                                        		<td>End Time:<sup>*</sup><br><input type="text" id="endTime" name="endTime" style="width:60px;" class="validateRequired endTime"/></td>
				                                        	</tr>
				                                        	<tr>
				                                        		<!--  <td>Location:<sup>*</sup></td>
				                                        		<td colspan="2"><s:textfield name="strLocation" id="strLocation" cssClass="validateRequired"></s:textfield></td>-->
				                                        		<td>
		                                                			Location:<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" />
		                           								</td>
		                           								<td>
		                           									Department:<s:select name="f_department" id="f_department" headerValue = "select option" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"/>
		                           								</td>
				                                        	</tr>
				                                        	
				                                        	<tr>
				                                        		<td></td>
				                                        		<td colspan="2">
				                                        			<img height="62" width="70" class="lazy" id="eventImage" style="border: 1px solid #CCCCCC;" src="userImages/event_icon.png" data-original="/" /> <!-- userImages/avatar_photo.png -->
				                                                    <input type="file" accept=".gif,.jpg,.png,.tif,.svg, .svgz" id="strEventImage" name="strEventImage"  size="5" style="font-size: 10px; height: 22px;margin-top:10px; vertical-align: top;" onchange="readImageURL(this, 'eventImage');">
				                                        		</td>
				                                        	</tr>
				                                        	<tr>
				                                        		<td></td>
				                                        		<td colspan="2"><s:submit name="eventPost" cssClass="btn btn-primary" cssStyle="margin-top:10px;" value="Post" /></td>
				                                        	</tr>
				                                        </table>
				                                        <script>
				                                        $(function () {
				                                        	$("input[name='eventPost']").click(function(){
				                                        		$(".validateRequired").prop('required',true);
				                                        	});
				                                        	var date_yest = new Date();
				                                            var date_tom = new Date();
				                                            date_yest.setHours(0,0,0);
				                                            date_tom.setHours(23,59,59); 
				                                           
				                                        	$('.startTime').datetimepicker({ 
				                                        		format: 'HH:mm',
				                                        		minDate: date_yest,
				                                        		defaultDate: date_yest
				                                            }).on('dp.change', function(e){ 
				                                            	$('.endTime').data("DateTimePicker").minDate(e.date);
				                                            });
				                                        	
				                                        	$('.endTime').datetimepicker({
				                                        		format: 'HH:mm',
				                                        		maxDate: date_tom,
				                                        		defaultDate: date_tom
				                                            }).on('dp.change', function(e){ 
				                                            	$('.startTime').data("DateTimePicker").maxDate(e.date);
				                                            });
				                                        });
				                                        </script>
				                                    </s:form>
				                                </div>
									        </div>
									        <div class="modal-footer clr">
									          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
									        </div>
									      </div>
									      
									    </div>
									</div>
	                                
	                                    <%
                                        }
                                        %>
                                        <div class="clr"></div>
                                        <div id="allEventsDiv">
		                                <input type="hidden" name="eventhideOffsetCnt" id="eventhideOffsetCnt" value="10" />
		                                <s:hidden name="lastEventId" id="lastEventId"/>
		                                <%
                                            int eventCount = 0;
                                            while(it.hasNext()){
                                            	String strEventId = (String)it.next();
                                            	List<String> eventList = hmEvents.get(strEventId);
                                            	
                                            	if(eventList == null) eventList = new ArrayList<String>();
                                            	if(eventList != null && eventList.size()>0 && !eventList.equals("")){
                                            		
                                            		List<String> event = eventList;
                                            		boolean flag = false;
                                            		if(availableExt.contains(event.get(11))) {
                                            			flag = true;
                                            		}
                                            %>
                                            
				                            <div class="box box-widget" id="mainEventDiv_<%=event.get(0) %>">
									            <div class="box-header with-border" style="padding: 5px;">
									              <div class="user-block">
									                <img class="img-circle" src="userImages/avatar_photo.png" alt="User Image">
									                <span class="username"><%=event.get(6) %> has posted an Event.</span>
									                <span class="description"><%=event.get(3)%></span>
									              </div>
									              <!-- /.user-block -->
									              <% if(((uF.parseToInt(strEmpId) == uF.parseToInt(event.get(10))) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
									              <div class="box-tools">
									                <a href="javascript:void(0);" onclick="editEventPopup('<%=event.get(0) %>', 'E_E');">
				                                       <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
				                                    </a>  
				                                    <a href="javascript:void(0);" onclick="editYourEvent('<%=event.get(0) %>', 'E_D');">
				                                       <i class="fa fa-trash" aria-hidden="true"></i>
				                                    </a>
									              </div>
									              
									              <%} %>
									              <!-- /.box-tools -->
									            </div>
									            <!-- /.box-header -->
									            <div class="box-body" id="eventDataDiv_<%=event.get(0) %>">
									              <!-- post text -->
									              <p><%=event.get(5) %></p>
									
									              <!-- Attachment -->
									              <div class="attachment-block clearfix">
									                 <% if(event.get(9) != null && !event.get(9).equals("")) {
															if(event.get(11)!=null && (event.get(11).equalsIgnoreCase("jpg") || event.get(11).equalsIgnoreCase("jpeg") || event.get(11).equalsIgnoreCase("png") || event.get(11).equalsIgnoreCase("bmp") || event.get(11).equalsIgnoreCase("gif"))){ 
														%>	
																<%=event.get(12)%>
														  <% } else { 
																if(flag && event.get(17)!=null && !(event.get(17)).equals("")){
														  %>
														  			<div id="tblDiv">
																		<a href="javascript:void(0);" onclick="viewEventFilePopup('<%=event.get(0)%>');event.preventDefault();"  style="color:gray;">&nbsp;<%=event.get(9)%></a>
																	</div>
															<%			
																}else{
															%>
																	<div style="float:left; padding: 60px 40px; font-size: 14px; border: 1px solid #CCCCCC;">Image Preview not available.</div>		
															<% }
														  }
														}	
													%>							
									               
									                <div class="attachment-pushed">
									                  <h4 class="attachment-heading"><%=event.get(4)%></h4>
													  <%=event.get(18) %>
									                  <div class="attachment-text">
									                     Organised at <%=event.get(7) %><br>
									             		 From <b><%=event.get(1) %> </b> to <b><%=event.get(2) %></b><br>
									             		 Timing: <b><%=event.get(14)%></b> To <b><%=event.get(15)%></b><br>
									             		 
									                  </div>
									                  <!-- /.attachment-text -->
									                </div>
									                <!-- /.attachment-pushed -->
									              </div>
									              <!-- /.attachment-block -->
									            </div>
									            <!-- /.box-body -->
									        </div>	
                                        <%	eventCount ++;
                                            }
                                            %>
                                        <% } %>
                                        <% if(eventCount == 0) { %>
                                        <div class="nodata msg" style="margin-left: 25px; width: 95%;"><span>No events available.</span></div>
                                        <% } %>
                                    <!-- </div> -->
                                    <% if(eventCount==10) { %>
                                    <div id="loadingEventsDiv" style="display: none; float: left; width: 100%; text-align: center;"> <img src="images1/new_loading.gif"> </div>
                                    <div id="loadMoreEventsDiv" style="float: left; width: 100%; text-align: center;"> <a href="javascript:void(0)" onclick="loadMoreEvents()">load more ...</a> </div>
                                    <% } %>
                                </div>
                                <% } else if(type != null && type.equals("FAQ")) {
                                	Map<String, List<List<String>>> hmFaqs =( Map<String, List<List<String>>>)request.getAttribute("hmFaqs");
                                	if(hmFaqs==null) hmFaqs = new HashMap<String, List<List<String>>>();
                                	
                                    Map<String,String> hmFaqSection =(Map<String,String>)request.getAttribute("hmFaqSection");
                                   	Set<String> setfaq = hmFaqs.keySet();
                                  	Iterator<String> faqs = setfaq.iterator();
                                 %> 
                                    
                                <div id = "FAQ" style="float:left; width:99%; margin:10px 2px 1px 7px;">
								<%if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.RECRUITER))) { %>
								 	<p style="padding-right: 25px; text-align: right;"><a href="javascript:void(0)" onclick="AddOrEditFaq('', 'A');"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add FAQ</a></p>
								<%} %>
								 <% 
								    while(faqs.hasNext()) {
								    	 String strSectionId = faqs.next();
								    	List<List<String>> faqList = hmFaqs.get(strSectionId);
								    	//System.out.println("faqlist: "+faqList);
								    	//System.out.println("faqlist size: "+faqList.size());
								    	%>
									<div id="allSection">
								   	 	<h3 class="box-title" style="text-align:center;color:#337ab7;"><%=hmFaqSection.get(strSectionId) %></h3>
										<div id="allfaq">
								    	<% 
								    	if(faqList != null  && faqList.size()>0 && !faqList.equals("")) {
                         					for(int i = 0;i<faqList.size();i++) {
								    		List<String> faq = faqList.get(i);
								    		//System.out.println("faqlist size: "+faqList);
                         				%>
								     
								  
									   <div class="box-footer box-comments" id="mainFaqDiv_" style="border-top: 1px solid #ECECEC;background: #FDFDFD;">
										  <!--  <div class="box-footer box-comments" id="mainFaqDiv_" style="border-top: 1px solid #ECECEC;background: #FDFDFD;">-->
										   	<div class="box-comment" id="faqDataDiv_<%=faq.get(0) %>">
										   		<div>
										   			<div class="box box-default collapsed-box">
														<div class="box-header with-border">
															<h3 class="box-title"><%=faq.get(1) %></h3>
															<div class="box-tools pull-right">
															<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
															<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
															<%if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.RECRUITER))) { %>
																<a href="javascript:void(0)" onclick="AddOrEditFaq('<%=faq.get(0) %>', 'D');" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
																<a href="javascript:void(0)" onclick="AddOrEditFaq('<%=faq.get(0) %>', 'E');"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
															<% } %>	
														</div>
														</div>
														<div class="box-body" style="overflow-y: auto;">
															<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
																<p style="padding-left: 5px;"><%=faq.get(2) %></p>
															</div>
														</div>
										   		</div>
											</div>
										</div>
									</div>
									<% } %>
								<% } %>
								</div>
							<% } %>
									 
								 <% if(hmFaqs==null || hmFaqs.size()==0) { %>
								 	<div class="nodata msg" style="margin-left: 25px; width: 95%;"><span>No FAQs available.</span></div>
								 <% } %>
							</div>
                                
                              <% } else if(type != null && type.equals("Q")) {
                                    //List<List<String>> quotesList = (List<List<String>>)request.getAttribute("quoteList");
                                    Map<String, List<String>> hmQuotes = (Map<String, List<String>>) request.getAttribute("hmQuotes");
                                    Map<String, String> hmQuoteIds = (Map<String, String>)request.getAttribute("hmQuoteIds");
                                    if(hmQuotes==null){
                                    	hmQuotes = new LinkedHashMap<String, List<String>>();
                                    }
                                    if(hmQuoteIds==null){
                                    	hmQuoteIds = new LinkedHashMap<String, String>();
                                    }
                                    Set<String> setQuotes = hmQuoteIds.keySet();
                                    Iterator<String> lit = setQuotes.iterator();
                                    
                                    %>	
                                  
                                <div id = "quotes" style="float:left; width:100%; margin:2px 2px 1px 1px;">
                                    <%
                                        if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) {	
                                        %>
                                        
                                        
                                    <p><a href="javascript:{}" data-toggle="modal" data-target="#addQuote"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Quote</a></p>
                                    <div class="modal fade" id="addQuote" role="dialog">
                                    	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
									    <div class="modal-dialog">
									      <!-- Modal content-->
									      <div class="modal-content">
									        <div class="modal-header">
									          <button type="button" class="close" data-dismiss="modal">&times;</button>
									          <h4 class="modal-title">Add Quote</h4>
									        </div>
									        <div class="modal-body">
									          <s:form name="frm_quotes" id = "frm_quote" action="Hub" theme="simple" method="Post" cssClass="formcss" enctype="multipart/form-data">
	                                            <s:hidden id ="type" name="type"></s:hidden>
	                                            <table class="table table_no_border form-table">
	                                            	<tr>
	                                            		<td>Quote By:<sup>*</sup></td>
	                                            		<td><s:textfield  name="strQuoteBy" id="strQuoteBy" cssClass="validateRequired" ></s:textfield></td>
	                                            	</tr>
	                                            	<tr>
	                                            		<td>Quote:<sup>*</sup></td>
	                                            		<td><s:textarea rows="3" name="strQuotedesc" id="strQuotedesc" cssClass="validateRequired" ></s:textarea></td>
	                                            	</tr>
	                                            	<tr>
	                                            		<td></td>
	                                            		<td><s:submit name="quotePost" cssClass="btn btn-primary" value="Post" /></td>
	                                            	</tr>
	                                            </table>
	                                        </s:form>
									        </div>
									      
									        <div class="modal-footer">
									          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
									        </div>
									      </div>
									      
									    </div>
									</div>
                                    <%
                                        }
                                        %>	
                                        
                                    <div id="allQuotesDiv">
                                        <input type="hidden" name="quotehideOffsetCnt" id="quotehideOffsetCnt" value="10" />
                                        <s:hidden name="lastQuoteId" id="lastQuoteId"/>
                                        <%
                                            int quoteCount = 0;
                                            while(lit.hasNext()) {
                                            	String strQuoteId = lit.next();
                                            	List<String> quotesList = hmQuotes.get(strQuoteId);
                                            	if(quotesList == null) quotesList = new ArrayList<String>();
                                            	if(quotesList != null && quotesList.size()>0 && !quotesList.equals("")){
                                            		List<String> quote = quotesList ;
                                            %>
                                            <div class="box-footer box-comments" id="mainQuoteDiv_<%=quote.get(0) %>" style="border-top: 1px solid #ECECEC;background: #FDFDFD;">
								              <div class="box-comment" id="quoteDataDiv_<%=quote.get(0) %>">
								                <!-- User image -->
								                <!-- <img class="img-circle img-sm" src="userImages/avatar_photo.png" alt="User Image" style="width: 40px !important;height: 40px !important;"> -->
												<%=quote.get(7) %><!--  Please make this return image path to put in above src -->
								                <div class="comment-text" style="margin-left: 55px;">
								                      <span class="username" style="font-size: 14px;color: #0089B4;">
								                        <%=quote.get(4) %> <span style="font-weight:400;">has posted a quote by </span><%=quote.get(2) %>
								                        <span class="text-muted pull-right"><%=quote.get(5) %><% if(((uF.parseToInt(strEmpId) == uF.parseToInt(quote.get(6))) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
                                                                <div style="float: right;">
                                                                    <a href="javascript:void(0);" onclick="editQuotePopup('<%=quote.get(0) %>', 'Q_E');">
                                                                    <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
                                                                    </a>
                                                                    <a href="javascript:void(0);" onclick="deleteYourQuotes('<%=quote.get(0) %>', 'Q_D');">
                                                                    <i class="fa fa-trash" aria-hidden="true"></i>
                                                                    </a>
                                                                </div>
                                                                <%
                                                                    }
                                                                    %></span>
								                      </span><!-- /.username -->
								                  <p style="font-size: 14px;width: 60%;font-style:italic;color: rgb(156, 156, 156);"><sup style="color: rgb(109, 109, 109) !important"><i class="fa fa-quote-left" aria-hidden="true"></i></sup><%=quote.get(3) %><sup style="color: rgb(109, 109, 109) !important;"><i class="fa fa-quote-right" aria-hidden="true"></i></sup></p>
								                </div>
								                <!-- /.comment-text -->
								              </div>
								              <!-- /.box-comment -->
								            </div>

                                        <%	quoteCount++;
                                            }
                                            %>
                                        <% } %>
                                        <% if(quoteCount == 0) { %>
                                        <div class="nodata msg" style="margin-left: 25px; width: 95%;"><span>No quotes available.</span></div>
                                        <% } %>
                                    </div>
                                    <% if(quoteCount==10) { %>
                                    <div id="loadingQuotesDiv" style="display: none; float: left; width: 100%; text-align: center;"> <img src="images1/new_loading.gif"> </div>
                                    <div id="loadMoreQuotesDiv" style="float: left; width: 100%; text-align: center;"> <a href="javascript:void(0)" onclick="loadMoreQuotes()">load more ...</a> </div>
                                    <% } %>
                                </div>
                                <% } else if(type!=null && type.equals("M")) {
                                	
			                               	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
			                           		String sessionEmpId = (String)session.getAttribute(IConstants.EMPID);
                                            String strManualId = (String)request.getAttribute("MANUAL_ID");
                                            String strTitle = (String)request.getAttribute("TITLE");
                            		   		String strBody = (String)request.getAttribute("BODY");
                            		   		String strDate = (String)request.getAttribute("DATE");
                            		   		List<String> availableExt = (List<String>)request.getAttribute("availableExt");
                            		   		String manualDocPath = (String)request.getAttribute("manualDocPath");
                            		   		String extention = (String)request.getAttribute("extention");

                            		   		if(availableExt == null) availableExt = new ArrayList<String>(); 
                            		   		boolean flag = true;
                            		   		if(!availableExt.contains(extention)) {
                            		   			flag = false;
                            		   		}
                                     		//System.out.println("strManualId ===>> " + strManualId);
                                     	%>
                                <div id = "manual" style="float: left; width: 99%; margin: 10px 2px 1px 7px;">
									<% if(strUserType!=null && (strUserType.equals(IConstants.EMPLOYEE) || strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) {
                                        String strDisplay ="block";
                                        String editDisplay ="none";
                                        if(operation != null && operation.equals("E")){
                                        	strDisplay ="none";
                                        	editDisplay ="block";
                                        }
                                        %>
                                    <% String message =  (String)session.getAttribute("MESSAGE");%>
	                                    <div style = "width:100%;float:left;">
	                                        <%if(message!=null && !message.equals("")){ %>
	                                        <%=message %>
	                                        <% }
	                                          session.setAttribute("MESSAGE","");
	                                        %>
	                                    </div>
                                    <% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
	                                    <%-- <div id = "addLink" style="float:left;margin-right:45px;padding:9px 5px;display:<%=strDisplay%>;">
	                                        <a href="#" onclick="addManual();"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Manual</a>
	                                    </div> --%> 
                                    <% } %>
                                    
                                      <%-- <div id = "editLink" style="float:left;font-weight:bold;font-size:14px;margin:15px 0px 0px 5px;padding:9px 5px;color:#68AC3B;display:<%=editDisplay%>;">Edit Manual :</div> --%>
                                      <div id = "addManual"style="float: left; width: 97%; margin: 5px 0px; margin-left:5px; padding: 5px;display: <%=(operation != null && operation.equals("E")) ? "block" : "none" %>;">
                                        <div style="float: left; width: 100%; margin: 5px 0px;">
                                            <%
                                                String manualId = (String)request.getAttribute("manualId");
                                            	if(operation != null && operation.equals("E") && manualId!=null && uF.parseToInt(manualId)>0) {
                                                %>
		                                            <s:action name="AddCompanyManual" executeResult="true">
		                                                <s:param name="pageFrom">MyHub</s:param>
		                                                <s:param name="E"><%=manualId %></s:param>
		                                                <s:param name="orgId"><%=(String)request.getAttribute("strOrg")%></s:param>
		                                            </s:action>
                                            <% } else { %>
		                                            <s:action name="AddCompanyManual" executeResult="true">
		                                                <s:param name="pageFrom">MyHub</s:param>
		                                                <s:param name="orgId"><%=(String)request.getAttribute("strOrg")%></s:param>
		                                            </s:action>
                                            <% } %>
                                        </div>
                                    </div> 
                                   
                                    <div id="list_Manual" style="float: left; width: 100%; margin: 5px 0px; ">
                                       <%
										  List<List<String>> reportList = (List<List<String>>)request.getAttribute("reportList");
										  String strManualIds = (String)request.getAttribute("strManualIds"); 
					
										  if(strManualIds == null ) strManualIds = new String();
										  if(reportList == null) reportList = new ArrayList<List<String>>();
										%>
										   <div>
										 <% 
					       					 if(reportList!=null && reportList.size()>0) {
												for(int i=0; i<reportList.size(); i++) {
													List<String> alInner = reportList.get(i);
													if(alInner==null) alInner = new ArrayList<String>();
													if(alInner!=null && alInner.size()>0) {
														String strColor = "#fff";
														if(operation!=null && operation.equals("E") && manualId!=null && uF.parseToInt(manualId)>0) {
															if(alInner.get(3)!=null && String.valueOf(alInner.get(3)).length()>0 && alInner.get(3).equals(manualId)) {
																strColor = "#efefef";
															}
														} else if((operation == null || !operation.equals("E")) && alInner.get(3)!=null && String.valueOf(alInner.get(3)).length()>0 && strManualId != null && alInner.get(3).equals(strManualId)) {
															strColor = "#efefef";
														}
													%>
														
												<% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
													<div id ="manualDiv_<%=alInner.get(3) %>" style="float:left; width:98%; margin:5px 7px 5px 8px; border-bottom:1px solid #efefef; background:<%=strColor%>">
														<div style="float:left;margin-left:3px;width:100%;">
														   <div style="float: left;">
																<a href="AddCompanyManual.action?orgId=<%=request.getAttribute("strOrg") %>&D=<%=alInner.get(3)%>&pageFrom=MyHub" class="del" onclick="return confirm('Are you sure you wish to delete this manual?')"> <i class="fa fa-trash" aria-hidden="true"></i> </a> 
																<a href="Hub.action?type=M&operation=E&manualId=<%=alInner.get(3)%>" onclick="editManual('<%=alInner.get(3) %>','<%=strManualIds %>');"class="edit_lvl"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
															</div>
															<div style="float:left; margin-top:4px;"><%=alInner.get(5)%></div>
															<div style="float:left; margin-left:5px;">
															 	Title: <strong><%=alInner.get(1)%></strong>&nbsp;&nbsp;&nbsp;Organisation : <strong><%=alInner.get(4)%></strong>
															</div>
														</div> 
														
														<div style="float:left; margin-left:63px; width:92%;">
															<div style="float:left;">Status: <strong><span id="myDiv<%=i %>"><%=alInner.get(2)%></span></strong></div>
															<div style="float:left; margin-left:10px;">Last Updated :<strong><%=alInner.get(0)%></strong> </div>
															<div style="float:right; margin-right:5px;">
																<%if(alInner.get(6) != null && !alInner.get(6).equals("")) { %>
																	<a href="javascript:void(0)" onclick="viewManual('<%=alInner.get(3) %>','<%=strManualIds %>');event.preventDefault();" style="float:right">Preview Manual</a>
																<% } else if(alInner.get(7) != null && !alInner.get(7).equals("")){ %>
																	 <% 
																	if(docRetriveLocation==null) {
																		manualDocPath = IConstants.DOCUMENT_LOCATION+"/"+alInner.get(3)+"/"+alInner.get(7); // +"/"+sessionEmpId
																	} else {
																		manualDocPath = docRetriveLocation+IConstants.I_COMPANY_MANUAL+"/"+alInner.get(3)+"/"+alInner.get(7); //+"/"+sessionEmpId
																	}
																	%>
																	<a href="<%=manualDocPath %>" target="_blank" style="float:right">View Manual</a>
																	<%-- <a href="javascript:void(0)" onclick="viewManual('<%=alInner.get(3) %>','<%=strManualIds %>');event.preventDefault();" style="float:right">Preview Manual</a> --%>
																<% } %>
															</div>
														</div>
													</div>
											 	<% } else { %>
											 			<div id ="manualDiv_<%=alInner.get(3) %>" style="float:left; width:98%; margin:5px 7px 5px 7px; border-bottom:1px solid #efefef; background:<%=strColor%>">
															<div style="float:left;margin-left:3px;width:99%;">
																<div style="float:left; margin-left:5px;"><strong><%=alInner.get(1)%></strong></div>
															</div> 
															<div style="float:left; margin-left:3px; width:99%;">
																<div style="float:left; margin-left:5px;">Last Updated :<strong><%=alInner.get(0)%></strong> </div>
																<div style="float:right; margin-right:5px;">
																<%if(alInner.get(6) != null && !alInner.get(6).equals("")) { %>
																		<a href="javascript:void(0)" onclick="viewManual('<%=alInner.get(3) %>','<%=strManualIds %>');event.preventDefault();" style="float:right">Preview Manual</a>
																<% } else if(alInner.get(7) != null && !alInner.get(7).equals("")){ %>
																		<% 
																	if(docRetriveLocation==null) {
																		manualDocPath = IConstants.DOCUMENT_LOCATION+"/"+alInner.get(3)+"/"+alInner.get(7); // +"/"+sessionEmpId
																	} else {
																		manualDocPath = docRetriveLocation+IConstants.I_COMPANY_MANUAL+"/"+alInner.get(3)+"/"+alInner.get(7); //+"/"+sessionEmpId
																	}
																	%>
																	<a href="<%=manualDocPath %>" target="_blank" style="float:right">View Manual</a>
																	<%-- <a href="javascript:void(0);" onclick="viewManual('<%=alInner.get(3)%>','')" style="float:right">Preview Manual</a> --%>
																<% } %>
																</div>
															</div>
														</div>
											 		<% } %>
											<%		}
												}
					       				 	}
									  	  %>
		 						   	  </div>
                        		   </div>
                                    	<%-- <% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
								   			<div id = "addLink" style="float:left;margin-right:45px;padding:9px 5px;display:<%=strDisplay%>;">
												<a href="#" onclick="addManual();event.preventDefault();"> + Add New Manual</a>
											</div>
										<% } %> --%>
                                    <% } %>
                                     	<div id="show_Manual" style="float: left; width:99%; display: <%=(operation != null && operation.equals("E")) ? "none" : "block" %>">
									        <% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
										        <div style="float:left;width:98%; margin:7px 0px 0px 7px;padding:15px 15px; background:#efefef; border-top:solid 1px #fefefe;border-bottom:solid 5px #ccc;">
										           <div class="manual_title">
										           	<%if(strTitle!=null && !strTitle.equals("")) { %>
													  <%=strTitle %>
													  <% } else { %>
													    Not Available...!
													    <% } %>
										            </div>
										             <div class="clr"></div>
										             <%
										             	String date = "Not Available.";
										             	if(strDate!=null && !strDate.equals("")) {
										             		date= strDate;
										             	}
										             %>
										            <div style="float:right; padding:0px 20px"><span style="font-style:italic; color:#666666">Last Updated on:</span><%=date%></div>
										        </div>
											<% } %>
											
									        <div class="clr"></div>
									        <div class="manual_body">
									          <%if(strBody != null && !strBody.equals("") && (manualDocPath == null || manualDocPath.equals(""))) { %>
									          		<%=strBody%>
									          <%} else if(manualDocPath != null && !manualDocPath.equals("")) {
										        	  if(flag) {%>
										          			<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;">
																<a href="<%=manualDocPath %>" class="embed" id="test">&nbsp;</a>
															</div>
									  				<% } else {	%>
															<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;height: 500px; background-color: #CCCCCC;">
																<div style="text-align: center; font-size: 24px; padding: 150px;">Preview not available</div>
															</div>
												    <% }%>
											    <% } %>
									        </div>
									        
								        	<div class="clr"></div> 
	        							</div>
	        							
                                </div>
                                
                                
                                
                                
                                <% } else if(type!=null && type.equals("F")) { %>
                                <div style="margin: 5px 0px; margin-left:6px; padding: 5px;">
                                    <s:action name="Feeds" executeResult="true">
                                        <s:param name="pageFrom">MyHub</s:param>
                                    </s:action>
                                </div>
                                <% } else {
                                    //List<List<String>> noticeList = (List<List<String>>)request.getAttribute("noticeList");
                                    Map<String, List<String>> hmNotices = (Map<String, List<String>>) request.getAttribute("hmNotices");
                                    Map<String, String> hmNoticeIds = (Map<String, String>)request.getAttribute("hmNoticeIds");
                                    if(hmNotices==null){
                                    	hmNotices = new LinkedHashMap<String, List<String>>();
                                    }
                                    if(hmNoticeIds==null){
                                    	 hmNoticeIds = new LinkedHashMap<String, String>();
                                    }
                                    
                                    Set<String> setNotices = hmNoticeIds.keySet();
                                    Iterator<String> nit = setNotices.iterator();
                                    
                                    %>
                                <div id = "notices" style="float:left;width:100%;margin:2px 2px 1px 1px;" >
                                    <% if(strUserType!=null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
                                    <p><a href="javascript:void(0)" data-toggle="modal" data-target="#addAnn"><i class="fa fa-plus-circle"></i>Add Announcement</a></p>
                                    <div class="modal fade" id="addAnn" role="dialog">
									    <div class="modal-dialog">
									      <!-- Modal content-->
									      <div class="modal-content">
									        <div class="modal-header">
									          <button type="button" class="close" data-dismiss="modal">&times;</button>
									          <h4 class="modal-title">Add Announcement</h4>
									        </div>
									        <div class="modal-body">
									        	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
									        	
									          	<s:form name="frm_notices" id = "frm_notice" action="Hub" theme="simple" method="Post" cssClass="formcss" enctype="multipart/form-data">
		                                            <s:hidden id ="type" name="type"></s:hidden>
		                                            <div>
		                                                <table class="table table_no_border form-table">
		                                                	<tr>
		                                                		<td>Title:<sup>*</sup></td>
		                                                		<td colspan="2"><s:textfield  name="heading" id="heading" cssClass="validateRequired" ></s:textfield></td>
		                                                	</tr>
		                                                	<tr>
		                                                		<td>Notice:<sup>*</sup></td>
		                                                		<td colspan="2"><s:textarea rows="3" name="content" id="content"  cssClass="validateRequired"></s:textarea></td>
		                                                	</tr>
		                                                	<tr>
		                                                		<td></td>
		                                                		<td>Start Date:<sup>*</sup><br><s:textfield name="displayStartDate" id="displayStartDate"  cssClass="validateRequired" /></td>
		                                                		<td>End Date:<sup>*</sup><br><s:textfield name="displayEndDate" id="displayEndDate"  cssClass="validateRequired" /></td>
		                                                	</tr>
		                                                	<tr>
		                                                		<td></td>
		                                                		<td>Location:<br><s:select theme="simple" name="a_strWLocation" id="a_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" /></td>
		                           								<td>Department:<br><s:select name="a_department" id="a_department" headerValue = "select option" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"/></td>	
		                           							</tr>
		                                                	<tr>
		                                                		<td></td>
		                                                		<td colspan="2"><s:radio label="ispublish" name="ispublish" list="#{'1':'Publish','2':'Unpublish'}" value="2" /></td>
		                                                	</tr>
		                                                	<tr>
		                                                		<td></td>
		                                                		<td colspan="2"><s:submit name="noticePost" cssClass="btn btn-primary" value="Post" /></td>
		                                                	</tr>
		                                                </table>
		                                            </div>
		                                        </s:form>
		                                        <script>
				                                        $(document).ready( function () {
				                                        	$("input[name='noticePost']").click(function(){
				                                        		$(".validateRequired").prop('required',true);
				                                        	});
				                                        });
				                                </script>
									        </div>
									        <div class="modal-footer">
									          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
									        </div>
									      </div>
									      
									    </div>
									</div>
                                    <% } %>
                                    <div id="allNoticesDiv" style="float: left; width: 100%; margin-top:-4px;">
                                        <input type="hidden" name="noticethideOffsetCnt" id="noticethideOffsetCnt" value="10" />
                                        <s:hidden name="lastNoticeId" id="lastNoticeId"/>
                                        <%	
                                            int noticeCount = 0;
                                            while(nit.hasNext()) {
                                            	String strNoticeId = nit.next();
                                            	List<String> noticeList = hmNotices.get(strNoticeId);
                                            	
                                            	if(noticeList == null) noticeList = new ArrayList<String>();
                                            	if(noticeList != null  && noticeList.size()>0 && !noticeList.equals("")){
                                            					
                                            		List<String> notice = noticeList;
                                            %>
                                        <div class="box-footer box-comments" id="mainNoticeDiv_<%=notice.get(0) %>" style="border-top: 1px solid #ECECEC;background: #FDFDFD;padding: 5px;">
								              <div class="box-comment" id="noticeDataDiv_<%=notice.get(0) %>">
								                <!-- User image -->
								                <img class="img-circle img-sm" src="userImages/avatar_photo.png" alt="User Image" style="width: 40px !important;height: 40px !important;">
												<%-- <%=notice.get(12) %> Please make this return image path to put in above src--%> 
								                <div class="comment-text" style="margin-left: 55px;">
								                      <span class="username" style="font-size: 14px;color: #0089B4;">
								                        <%=notice.get(8) %> <span style="font-weight:400;">has posted an Announcement of </span><%=notice.get(3) %>
								                        <span class="text-muted pull-right">
								                        	<% if((uF.parseToInt(strEmpId) == uF.parseToInt(notice.get(6)) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
                                                                <div style="float: left;">
                                                                    <a href="javascript:void(0);" onclick="editNoticePopup('<%=notice.get(0) %>', 'E');">
                                                                    <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
                                                                    </a>
                                                                    <a href="javascript:void(0);" onclick="editYourNotice('<%=notice.get(0) %>', 'D');">
                                                                    <i class="fa fa-trash" aria-hidden="true"></i>
                                                                    </a>
                                                                </div>
                                                                <% } %>
                                                                <%=notice.get(7) %><%=notice.get(11)%></span>
								                      </span><!-- /.username -->
								                  <% if((uF.parseToInt(strEmpId) == uF.parseToInt(notice.get(6)) && strUserType!=null && (strUserType.equals(IConstants.HRMANAGER)) || strUserType.equals(IConstants.ADMIN))) { %>
								                  <p style="font-size: 14px;width: 60%;font-style:italic;color: rgb(156, 156, 156);"><%=notice.get(4) %></p>
								                  <p><span class="label label-warning">Start Date: <%=notice.get(1) %></span>&nbsp&nbsp<span class="label label-danger"> End Date: <%=notice.get(2) %></span></p>
								                  <% } else { %>
								                  <p style="font-size: 14px;width: 60%;font-style:italic;color: rgb(156, 156, 156);"><%=notice.get(4) %></p>
								                  <% } %>
								                </div>
								                <!-- /.comment-text -->
								              </div>
								              <!-- /.box-comment -->
								            </div>
                                        
                                        <%	noticeCount++;
                                            }
                                            %>
                                        <% } %>
                                        <% if(noticeCount == 0) { %>
                                        <div class="nodata msg" style="margin-left: 25px; width: 95%;"><span>No announcements available.</span></div>
                                        <% } %>
                                    </div>
                                    <% if(noticeCount==10 ) { %>
                                    <div id="loadingNoticesDiv" style="display: none; float: left; width: 100%; text-align: center;"> <img src="images1/new_loading.gif"> </div>
                                    <div id="loadMoreNoticesDiv" style="float: left; width: 100%; text-align: center;"> <a href="javascript:void(0)" onclick="loadMoreNotices()">load more ...</a> </div>
                                    <% } %>
                                </div>
                                <% } %>
                            </div>
                        </div>
                </div>
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

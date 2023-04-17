<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@taglib prefix="sx" uri="/struts-dojo-tags"%>

<%@ page import="java.util.*"%>

<style>
#calendar {
	width: 450px;
	margin: 0 auto;
}

.box-widget {
	border: 1px solid #F1F1F1;
	padding: 4px;
}

.box-footer {
	padding: 5px;
}

.box-comments .box-comment {
	padding: 3px 0;
}

.box-comments .username {
	display: inline;
}

.box-comment #the_div {
	height: 50px;
}

.box-comment #ajaxLoadImage {
	height: 110%;
}

.addCommentDiv {
	clear: both;
	border-top: none;
	padding-top: 0px;
}

.formcss select,.formcss button {
	width: 150px !important;
}

.formcss textarea {
	width: 100% !important;
	padding-left: 2%;
}

.users-list>li img {
	max-width: 100% !important;
}

/*************** Updated CSS **************/
.card-body {
	border-top-left-radius: 0;
	border-top-right-radius: 0;
	border-bottom-right-radius: 3px;
	border-bottom-left-radius: 3px;
}

.card {
	box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1), 0 2px 3px rgba(0, 0, 0, 0.1);
	transition: 0.3s;
	border-radius: 4px;
	margin: 2%;
	background-color: white;
}

.card-header {
	padding-top: 2%;
	padding-left: 1.3%;
	font-family: -apple-system, system-ui, BlinkMacSystemFont, Segoe UI,
		Roboto, Helvetica Neue, Fira Sans, Ubuntu, Oxygen, Oxygen Sans,
		Cantarell, Droid Sans, Apple Color Emoji, Segoe UI Emoji,
		Segoe UI Symbol, Lucida Grande, Helvetica, Arial, sans-serif;
}

::-webkit-scrollbar {
	width: 0.1%;
}

::-webkit-scrollbar-button {
	background: #ccc
}

::-webkit-scrollbar-track-piece {
	background: #888
}

::-webkit-scrollbar-thumb {
	background: #eee
}

.showLeft {
	text-shadow: none !important;
	color: #312f2f !important;
}

.icons li {
	background: none repeat scroll 0 0 #fff;
	height: 7px;
	width: 7px;
	line-height: 0;
	list-style: none outside none;
	margin-right: 15px;
	margin-top: 3px;
	vertical-align: top;
	border-radius: 50%;
	pointer-events: none;
}

.btn-left {
	left: 0.4em;
}

.btn-right {
	right: 0.4em;
}

.btn-left,.btn-right {
	position: absolute;
}

.dropbtn {
	color: white;
	font-size: 16px;
	border: none;
	cursor: pointer;
}

.dropbtn:hover,.dropbtn:focus {
	background-color: #rgba ( 0, 0, 0, 0.1 );
}

.customDropdown {
	position: absolute;
	display: inline-block;
	right: 0.4em;
	margin-right: 5%;
}

.customDropdown-content {
	display: none;
	position: relative;
	margin-top: 60px;
	background-color: #f9f9f9;
	min-width: 160px;
	overflow: auto;
	box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
	z-index: 1;
}

.customDropdown-content a {
	color: black;
	padding: 5px 16px;
	text-decoration: none;
	display: block;
}

.customDropdown a:hover {
	background-color: #f1f1f1
}

.show {
	display: block;
}

.customeDropdown ul {
	padding-left: 0px;
}

.postLikeHr {
	height: 0.5px;
	background-color: #d2cbcb;
	width: 100%;
}

.postLikeDiv {
	padding-top: 1%;
	width: 100%;
}

.postActions {
	padding-top: 1%;
}

.postContent {
	margin-top: 1.5%;
	font-family: -apple-system, system-ui, BlinkMacSystemFont, Segoe UI,
		Roboto, Helvetica Neue, Fira Sans, Ubuntu, Oxygen, Oxygen Sans,
		Cantarell, Droid Sans, Apple Color Emoji, Segoe UI Emoji,
		Segoe UI Symbol, Lucida Grande, Helvetica, Arial, sans-serif;
}

.postSystemContent {
	margin-top: 5.5%;
	font-family: -apple-system, system-ui, BlinkMacSystemFont, Segoe UI,
		Roboto, Helvetica Neue, Fira Sans, Ubuntu, Oxygen, Oxygen Sans,
		Cantarell, Droid Sans, Apple Color Emoji, Segoe UI Emoji,
		Segoe UI Symbol, Lucida Grande, Helvetica, Arial, sans-serif;
}

.txtPostClass {
	width: 96% !important;
	margin-left: 2%;
	height: 60px !important;
	cursor: pointer;
	margin-top: 1%;
	font-size: large;
	font-style: italic;
	color: #5d5f63;
	padding-left: 2% !important;
}

.attachmentDiv img {
	width: 20% !important;
	height: 20% !important;
	padding-top: 1% !important;
}
</style>
<% 
    //List alEmp = (List)request.getAttribute("reportListEmp");
    UtilityFunctions uF = new UtilityFunctions();
    %>



<style>
.tb_style tr td {
	padding: 5px;
	border: solid 1px #c5c5c5;
}

.tb_style tr th {
	padding: 5px;
	border: solid 1px #c5c5c5;
	background: #efefef
}

ul li.desgn {
	padding: 0px;
	border: solid 1px #ccc
}

.close_div {
	cursor: pointer;
	text-shadow: 0 1px 0 #FFFFFF;
	background-image: url(images1/minus_sign.png);
	background-repeat: no-repeat;
	background-position: 10px 6px;
	background-color: #efefef;
}

.input_btn {
	background-image: url("images1/a.visited_green.png");
	background-repeat: repeat-x;
	border: 1px solid #5D862B;
	border-radius: 3px 3px 3px 3px;
	color: #FFFFFF;
	cursor: pointer;
	font-family: Verdana, arial, helvetica, sans-serif;
	font-size: 10px;
	font-weight: bold;
	height: 21px;
	/* margin: 10px 0 0; */
	outline: 0 none;
	padding: 3px 7px;
	width: auto;
}

.cancel_btn {
	background-image: url("images1/a.visited_red.png");
	background-repeat: repeat-x;
	border: 1px solid red;
	border-radius: 3px 3px 3px 3px;
	color: #FFFFFF;
	cursor: pointer;
	font-family: Verdana, arial, helvetica, sans-serif;
	font-size: 10px;
	font-weight: bold;
	height: 21px;
	/* margin: 10px 0 0; */
	outline: 0 none;
	padding: 3px 7px;
	width: auto;
}
/* a:hover{
    color:#68AC3B;
    text-decoration:underline;
    } */
</style>
<%
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    String strOrgId = (String)session.getAttribute(IConstants.ORGID);
    String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
    
    String DOC_RETRIVE_LOCATION = (String)request.getAttribute("DOC_RETRIVE_LOCATION");
    
    
    %>
<script type="text/javascript">
	$(function(){
		$("select[multiple]").multiselect().multiselectfilter();
	});
	
    function checkVisibility(val, postId) {
    	if(val == 2) {
    		document.getElementById("visibilityWithDiv_"+postId).style.display= 'block';
    	} else {
    		document.getElementById("visibilityWithDiv_"+postId).style.display= 'none';
    	}
    }
    
    
    function getAlignTypeData(val, postId) {
    	var pageFrom = document.getElementById("pageFrom").value;
    	var proId = document.getElementById("proId").value;
    	getContent('alignDataDiv_'+postId, 'GetAlignedTypeData.action?alignedType='+val+'&postId='+postId+'&pageFrom='+pageFrom+'&proId='+proId);
    	var strUsrType = '<%=strUserType %>';
    	var strCustomer = '<%=IConstants.CUSTOMER %>';
    	//alert("strUsrType ==>>> " + strUsrType);
    	if(strUsrType != null && strUsrType == strCustomer) {
    		//alert("strUsrType if ==>>> " + strUsrType);
    		addOrRemoveTeamInShareCust(val, postId);
    	} else {
    		//alert("strUsrType else ==>>> " + strUsrType);
    		addOrRemoveTeamInShare(val, postId);
    	}
    }
    
    function addOrRemoveTeamInShare(val, postId) {
    	//alert("val ===>> " + val);
    	    var len = document.getElementById("strVisibility_"+postId).length;
    	if(parseInt(val) > 0) {
    		//alert("val 1 ===>> " + val);
    		if(parseInt(len) == 2) {
    			var theSelect = document.getElementById("strVisibility_"+postId);
    		    var option = document.createElement("option");
    			option.value = "1";
    		    option.text = "Team";
    		    theSelect.add(option,1);
    		    var option1 = document.createElement("option");
    		    option1.value = "3";
    		    option1.text = "Customer";
    		    theSelect.add(option1,3);
    		}
    	} else {
    		if(parseInt(len) > 2) {
    			var theSelect = document.getElementById("strVisibility_"+postId);
    			theSelect.remove(3);
    			theSelect.remove(1);
    		}
    	}
    }
    
    
    function addOrRemoveTeamInShareCust(val, postId) {
    	//alert("val ===>> " + val);
    	    var len = document.getElementById("strVisibility_"+postId).length;
    	if(parseInt(val) > 0) {
    		//alert("val 1 ===>> " + val);
    		if(parseInt(len) == 1) {
    			var theSelect = document.getElementById("strVisibility_"+postId);
    		    var option = document.createElement("option");
    			option.value = "1";
    		    option.text = "Team";
    		    theSelect.add(option,0);
    		}
    	} else {
    		if(parseInt(len) > 1) {
    			var theSelect = document.getElementById("strVisibility_"+postId);
    			theSelect.remove(0);
    		}
    	}
    }
    
    function checkLikeUnlike(postId, type,likeCount) {
    	//alert("likeCount==>"+likeCount);
    	var likeStatus = document.getElementById("likeStatus_"+postId).value;
    	if(likeStatus == '1') {
    		document.getElementById("likeStatus_"+postId).value = '0';
    		document.getElementById("blueLikeDiv_"+postId).style.display= 'none';
    		document.getElementById("grayLikeDiv_"+postId).style.display= 'inline';
    		getContent('likeCountDiv_'+postId, 'LikeUnlikeAndLikeData.action?likeUnlike=UL&postId='+postId+'&type='+ type+'&likeCount='+likeCount);
    	} else {
    		document.getElementById("likeStatus_"+postId).value = '1';
    		document.getElementById("blueLikeDiv_"+postId).style.display= 'inline';
    		document.getElementById("grayLikeDiv_"+postId).style.display= 'none';
    		getContent('likeCountDiv_'+postId, 'LikeUnlikeAndLikeData.action?likeUnlike=L&postId='+postId+'&type='+ type+'&likeCount='+likeCount);
    	}
    }
    
    function addComment(event, postId, type, mainPostId) {
    	//alert("type==>"+type);
    	debugger;
    	if(type == 'C' || event.keyCode == 27 ) {
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
                    url : 'AddCommentOnPost.action?postId='+postId+'&type=C'+'&mainPostId='+mainPostId,
                    cache : false,
                    success : function(data) {
                    	var allData = data.split("::::");
                    	//alert("postId ===>>> " + postId + "data ===>> " + data);
                    	//alert("allData[0]==>"+allData[0]);
                    	document.getElementById("comment_"+postId).innerHTML = allData[0];
                    //	document.getElementById("strCommentTaggedWith_"+postId).selectedIndex = '-1';
                    	$("select[multiple]").multiselect("uncheckAll");
                    	$("#comment_"+postId).addClass("box-comment");
    					document.getElementById("newCommentDiv_"+mainPostId).style.display = 'block';
    					
    					$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    					
                    }
                });
    	    }
    	} else {
    		var strComment = document.getElementById("strComment_"+postId).value;
    		if(!event.ctrlKey && event.keyCode == 13 && strComment.length>0) {
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
    		    	var strCommentTaggedWith = getSelectedValue("strCommentTaggedWith_"+postId);
    	            var xhr = $.ajax({
    	                url : 'AddCommentOnPost.action?postId='+postId+'&strComment='+strComment+'&type='+type+'&mainPostId='+mainPostId+'&strCommentTaggedWith='+strCommentTaggedWith,
    	                cache : false,
    	                success : function(data) {
    	                	var allData = data.split("::::");
    	                	//alert("postId ===>>> " + postId + "data ===>> " + data);
    						if(type == 'A') {
    		                	var divTag2 = document.createElement("div");
    							divTag2.id = "comment_"+allData[1].trim();
								divTag2.innerHTML = allData[0];
								document.getElementById("commentDiv_"+postId).style.display = 'block';
    							document.getElementById("commentDiv_"+postId).appendChild(divTag2);
    							document.getElementById("strComment_"+postId).value = "";
    							//document.getElementById("strCommentTaggedWith_"+postId).selectedIndex = '-1';
    							$("select[multiple]").multiselect("uncheckAll")
    							
    							$("#"+divTag2.id).addClass("box-comment");
    							//document.getElementById("commentDiv_"+postId).appendChild(divTag1);
    	                	} else {
    	                		document.getElementById("comment_"+postId).innerHTML = allData[0];
    	                		$("#comment_"+postId).addClass("box-comment");
    	                	}
    						document.getElementById("newCommentDiv_"+mainPostId).style.display = 'block';
    						$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    						$("select[multiple]").multiselect("uncheckAll")
    	                }
    	            });
    		    }
    		} else if(event.ctrlKey && event.keyCode == 13) {
    			var strComment = document.getElementById("strComment_"+postId);
    			var strCommentVal = strComment.value;
    			document.getElementById("strComment_"+postId).value = strCommentVal + '\n';
    			auto_grow(strComment);
    		}
    	}
    }
    

    function auto_grow(element) {
        element.style.height = "5px";
        element.style.height = (element.scrollHeight)+"px";
    }
    
    function editYourComment(postId, type, mainPostId) {
    	//alert("feeds edit comment");
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
        	if(type == 'Y_E') {
        		document.getElementById("newCommentDiv_"+mainPostId).style.display = 'none';
        		document.getElementById("comment_"+postId).innerHTML = "<div id='the_div'><div id='ajaxLoadImage'></div></div>";
                var xhr = $.ajax({
                    url : 'EditAndDeleteComment.action?postId='+postId+'&type='+type+'&mainPostId='+mainPostId,
                    cache : false,
                    success : function(data) {
                    	document.getElementById("comment_"+postId).innerHTML = data;
                    	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
                    	$("select[multiple]").multiselect().multiselectfilter();
                    }
                });
              	} else {
              		if(confirm("Are you sure, you want to delete this comment?")) {
              			var xhr = $.ajax({
       	                url : 'EditAndDeleteComment.action?postId='+postId+'&type='+type+'&mainPostId='+mainPostId,
       	                cache : false,
       	                success : function(data) {
       	                	//alert("postId ===>> " + postId);
       	                	document.getElementById("comment_"+postId).innerHTML = '';
       	                	$("#comment_"+postId).remove();
       	                	document.getElementById("comment_"+postId).setAttribute("style","padding: 0px;");
       	                	
       	                	
    					}
    				});
    			}
    		}
        	
    	}
    }
    
    
    function viewMoreComments(postId) {
    	
    	var lastCommentId = document.getElementById("commentCnt_"+postId).value;
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
                   url : 'EditAndDeleteComment.action?postId='+postId+'&type=M_C&lastCommentId='+lastCommentId,
                   cache : false,
                   success : function(data) {
                   	//var divTag1.innerHTML = document.createElement("div");
                   	var divTag1 = document.getElementById("commentDiv_"+postId).innerHTML;
    				
                   	var allData = data.split("::::");
                   	//alert("allData ===>> " + allData);
                   	//alert("allData[0] ===>> " + allData[0]);
                   	//alert("allData[1] ===>> " + allData[1]);
                   	document.getElementById("commentDiv_"+postId).style.display = 'block';
                   	document.getElementById("commentDiv_"+postId).innerHTML = allData[0]+divTag1;
                   	document.getElementById("morecommentDiv_"+postId).innerHTML = allData[1];
                   	
                   	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
                   	$("select[multiple]").multiselect().multiselectfilter();
                   	//document.getElementById("commentDiv_"+postId).appendChild(divTag1);
                   }
               });
        	
    	}
    }
    
    function editYourPostPopup(postId, type, pageFrom, proId, taskId, proFreqId, invoiceId){
    	var dialogEdit = '.modal-body';
    	 $("#modalInfo .modal-body").empty();
    	 $("#modalInfo .modal-body").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $("#modalInfo .modal-title").html('Edit Post ');
    	 $.ajax({
                url : 'EditAndDeleteFeed.action?postId='+postId+'&type='+type+'&pageFrom='+pageFrom+'&proId='+proId+'&taskId='+taskId
                		+'&proFreqId='+proFreqId+'&invoiceId='+invoiceId,
                cache : false,
                success : function(data) {
                	//alert("data==>"+data);
                	$("#modalInfo .modal-body").html(data);
                	//document.getElementById("postDataDiv_"+postId).innerHTML = data;
                	$("select[multiple]").multiselect().multiselectfilter();
                	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
                }
            });
    }
    
    function editYourPost(postId, type, pageFrom, proId, taskId, proFreqId, invoiceId) {
    	//alert("postId ===>> " + postId);
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
        	if(type == 'P_E') {
        		//alert("type ===>> " + type);
        		//document.getElementById("newCommentDiv_"+mainPostId).style.display = 'none';
                var xhr = $.ajax({
                    url : 'EditAndDeleteFeed.action?postId='+postId+'&type='+type+'&pageFrom='+pageFrom+'&proId='+proId+'&taskId='+taskId
                    		+'&proFreqId='+proFreqId+'&invoiceId='+invoiceId,
                    cache : false,
                    success : function(data) {
                    	//alert("type ===>> " + type);
                    	document.getElementById("postDataDiv_"+postId).innerHTML = data;
                    	$("select[multiple]").multiselect().multiselectfilter();
                    	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
                    }
                });
              	} else {
              		if(confirm("Are you sure, you want to delete this feed?")) {
              			var xhr = $.ajax({
       	                url : 'EditAndDeleteFeed.action?postId='+postId+'&type='+type,
       	                cache : false,
       	                success : function(data) {
       	                	//alert("postId ===>> " + postId);
       	                	document.getElementById("mainPostDiv_"+postId).innerHTML = '';
       	                	$("#mainPostDiv_"+postId).remove();
       	                	//document.getElementById("comment_"+postId).setAttribute("style","padding: 0px;");
    					}
    				});
    			}
    		}
    	}
    }
    
    
    function updateCancelFeed(postId, type, pageFrom, proId, taskId, proFreqId, invoiceId) {
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
        	var strAlignWithIds1 = "";
        	var strVisibilityWith1 = "";
        	var strAlignWith1 = "";
        	//strCommunication_ strAlignWith_ strAlignWithIds_ strTaggedWith_ strVisibilityWith_ strVisibility_
        	var strCommunication1 = document.getElementById("strCommunication_"+postId).value;
        	if(document.getElementById("strAlignWith_"+postId)) {
        		strAlignWith1 = document.getElementById("strAlignWith_"+postId).value;
        	}
        	if(document.getElementById("strAlignWithIds_"+postId)) {
        		strAlignWithIds1 = document.getElementById("strAlignWithIds_"+postId).value;
        	}
        	var strTaggedWith1 = getSelectedValue("strTaggedWith_"+postId);
        	if(document.getElementById("strVisibilityWith_"+postId)) {
        		strVisibilityWith1 = getSelectedValue("strVisibilityWith_"+postId);
        	}
        	var strVisibility1 = document.getElementById("strVisibility_"+postId).value;
        	
               var xhr = $.ajax({
                   url : 'EditAndDeleteFeed.action?postId='+postId+'&type='+type+'&strCommunication1='+encodeURIComponent(strCommunication1)
                   	+'&strAlignWith1='+strAlignWith1+'&strAlignWithIds1='+strAlignWithIds1+'&strTaggedWith1='+strTaggedWith1
                   	+'&strVisibilityWith1='+strVisibilityWith1+'&strVisibility1='+strVisibility1+'&pageFrom='+pageFrom+'&proId='+proId
                   	+'&taskId='+taskId+'&proFreqId='+proFreqId+'&invoiceId='+invoiceId,
                   cache : false,
                   success : function(data) {
                   	//alert("data ===>> " + data);
                   	document.getElementById("postDataDiv_"+postId).innerHTML = data;
                   	
                   	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
                   }
               });
    	}
    }
    
    
    function getSelectedValue(selectId) {
    	var choice = document.getElementById(selectId);
    	var exportchoice = "";
    	for ( var i = 0, j = 0; i < choice.options.length; i++) {
    		if (choice.options[i].selected == true) {
    			if (j == 0) {
    				exportchoice = "," + choice.options[i].value + ",";
    				j++;
    			} else {
    				exportchoice += choice.options[i].value + ",";
    				j++;
    			}
    		}
    	}
    	return exportchoice;
    }
    
    function readImageURL(input, targetDiv) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#'+targetDiv)
                    .attr('src', e.target.result)
                    .css({ display: "block" })
                    .width(60)
                    .height(60);
            };
            reader.readAsDataURL(input.files[0]);
        }
    }
    
    
    function openCloseTaggedWith(postId) {
    	var status = document.getElementById("hideTaggedWith_"+postId).value;
    	if(status == 0) {
    		document.getElementById("commentTaggedWith_"+postId).style.display = 'inline';
    		document.getElementById("hideTaggedWith_"+postId).value = "1";
    	} else {
    		document.getElementById("strCommentTaggedWith_"+postId).selectedIndex = '-1';
    		document.getElementById("commentTaggedWith_"+postId).style.display = 'none';
    		document.getElementById("hideTaggedWith_"+postId).value = "0";
    	}
    }
    
    
    function loadMoreFeeds() {
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
        	
        	document.getElementById("loadingDiv").style.display = 'block';
        	var offsetCnt = document.getElementById("hideOffsetCnt").value;
        	var lastComunicationId = document.getElementById("lastComunicationId").value;
        	//alert("offsetCnt ===>> " + offsetCnt);
             var xhr = $.ajax({
                 url : 'FeedsByAjax.action?offsetCnt='+offsetCnt+'&lastComunicationId='+lastComunicationId,
                 cache : false,
                 success : function(data) {
               	  if(data == "") {
                  	} else {
                  		var allData = data.split("::::");
                  	//alert("type ===>> " + type);
                  	//document.getElementById("allFeedsDiv").innerHTML = data;
                  	//alert("allData[0] ===>> " + allData[0] + " -- allData[1] ===>> " + allData[1]);
                  	document.getElementById("lastComunicationId").value = allData[0].trim();
                  	
                  	if(allData[1].trim() == 'NO') {
                  		document.getElementById("loadMoreFeedDiv").style.display = 'none';
                  	}
                  	var divTag2 = document.createElement("div");
    				//divTag2.id = "comment_"+allData[1].trim();
    				divTag2.setAttribute("style","float:left; width: 100%;");
    				divTag2.innerHTML = allData[2];
    				document.getElementById("allFeedsDiv").appendChild(divTag2);
    				
                  	//var div = document.getElementById('allFeedsDiv');
                  	//div.innerHTML = div.innerHTML + allData[2];
                  	
                  	//offsetCnt = parseInt(offsetCnt) + 10;
                  	//alert("offsetCnt ===>> " + offsetCnt);
                  	//document.getElementById("hideOffsetCnt").value = offsetCnt;
                  	//var allFeedsCount = document.getElementById("allFeedsCount").value;
                  	//var currFeedCnt = document.getElementById("hideCurrFeedCnt").value;
                  	//currFeedCnt = parseInt(currFeedCnt) + 1;
                  	//document.getElementById("hideCurrFeedCnt").value = currFeedCnt;
                  	
                  	document.getElementById("loadingDiv").style.display = 'none';
                  	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
                  	$("select[multiple]").multiselect().multiselectfilter();
                  	//div.appendChild(data);
                  	
                  }
                 }
             });
    	}
    }
    
    function openLikesPopup(likeIds,clientLikeIds){
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 $(".modal-title").html('Liked by: ');
    	 $.ajax({
    			
    			url :"FeedLikesPopup.action?likeIds="+likeIds+"&clientLikeIds="+clientLikeIds,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    			}
    		});
    }
    
    function openImagePopup(communicationId,communication,pageFrom){
    	
    	var com = communication;
    	
    	var dialogEdit = '.modal-body';
    	 $(dialogEdit).empty();
    	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	 $("#modalInfo").show();
    	 if(com === ""){
    		$(".modal-title").html("&nbsp;");
     	 }else{
     		$(".modal-title").html(com);
     	 }
    	 if($(window).width() >= 900){
    		 $(".modal-dialog").width(900);
    	 }
    	 $.ajax({
    			
    			url :"FeedsImageViewPopUp.action?communicationId="+communicationId+"&pageFrom="+pageFrom,
    			cache : false,
    			success : function(data) {
    				$(dialogEdit).html(data);
    				
    			}
    		});
    }
    
    function viewFeedsFilePopup(feedId) {
	   	$("#modalInfo .modal-body").empty();
   	 	$("#modalInfo .modal-body").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	 	$("#modalInfo").show();
   	 	$("#modalInfo .modal-title").html('View Feeds File ');
   	  	$("#modalInfo .modal-content").height(450);
	   	 $.ajax({
	   			
	   	  		url : 'ViewFeedsFilePopup.action?strFeedId='+feedId,
	   			cache : false,
	   			success : function(data) {
	   				$("#modalInfo .modal-body").html(data);
	   			}
	   		});
	}
</script>
<%
    String pageFrom = (String) request.getAttribute("pageFrom");
    String proId = (String) request.getAttribute("proId");
    String taskId = (String) request.getAttribute("taskId");
    String proFreqId = (String) request.getAttribute("proFreqId");
    String invoiceId = (String) request.getAttribute("invoiceId");
    
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    String []arrEnabledModules = CF.getArrEnabledModules();
    
    List<List<String>> alSkills = (List<List<String>>) request.getAttribute("alSkills");
    
    Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
      	if (hmEmpProfile == null) {
      		hmEmpProfile = new HashMap<String, String>();
      	}
      	String strEmpID = (String) session.getAttribute(IConstants.EMPID);
      	
    %>
<%
    Map<String, List<String>> hmFeeds = (Map<String, List<String>>) request.getAttribute("hmFeeds");
    Map<String, String> hmFeedIds = (Map<String, String>)request.getAttribute("hmFeedIds");
    
    Map<String, List<List<String>>> hmComments = (Map<String, List<List<String>>>) request.getAttribute("hmComments");
    Map<String, String> hmLastCommentId = (Map<String, String>) request.getAttribute("hmLastCommentId");
    %>
<%if(pageFrom==null || (!pageFrom.trim().equalsIgnoreCase("Project") && !pageFrom.trim().equalsIgnoreCase("MyHub")) ) { %>
<div class="leftbox reportWidth">
	<% } %>
	<%-- <%=uF.showData((String) session.getAttribute("MESSAGE"), "")%> --%>
	<%
        String middleWidth = "60%";
        String rightWidth = "37%";
        
        %>
	<section>
	<div class="row jscroll" style="background-color: #f3f3f359;">
		<%-- <section class="col-lg-4 connectedSortable">
                <% 	if(pageFrom==null || (!pageFrom.trim().equalsIgnoreCase("MyHub")) ) { 
                    middleWidth = "40%";
                    rightWidth = "29%"; %>
                <div class="box box-primary" style="border-top: none;">
                    <div class="box-body" style="padding: 5px; overflow-y: auto;">
                        <div style=" margin-right: 7px;">
                            <div id="profilecontainer">
                                <div class="trow" style="background:#fff; margin:0px; width:100%;">
                                    <div>
                                        <div style="height:100px; width:100px; float:left; margin:2px 10px 0px 0px">
                                            <%if(DOC_RETRIVE_LOCATION==null) { %>
                                            <img height="100" width="100" class="lazy img-circle" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>" />
                                            <%} else { %>
                                            <img height="100" width="100" class="lazy img-circle" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=DOC_RETRIVE_LOCATION +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+strEmpID+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>" />
                                            <% } %> 
                                        </div>
                                        <!-- <div style="text-align:center; float:left; width:85px"><a href="javascript:void" onclick="showEditPhoto();">Edit Photo</a></div> -->
                                    </div>
                                    <div>
                                        <table class="table_font">
                                            <tr>
                                                <td class="textblue" style="font-size: 12px; font-weight: bold;"><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%>]</td>
                                            </tr>
                                            <tr>
                                                <td><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%>]</td>
                                            </tr>
                                            <tr>
                                                <td>Working as <%=uF.showData((String) hmEmpProfile.get("EMPLOYEE_CONTRACTOR"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td>Date of Joining: <%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td>Reporting to: <%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></td>
                                            </tr>
                                            <tr>
                                                <td>Profile: <%=uF.showData((String) hmEmpProfile.get("PROFILE"), "-")%></td>
                                            </tr>
                                            <% if(alSkills!=null && alSkills.size()!=0) { %>
                                            <tr>
                                                <td>
                                                    <% for(int i=0; i<alSkills.size(); i++) { %>
                                                    <strong><%=(i<alSkills.size()-1) ? ((List)alSkills.get(i)).get(1) + ", " : ((List)alSkills.get(i)).get(1)%></strong>
                                                    <% } %>
                                                </td>
                                            </tr>
                                            <% } %>    
                                            <% if(alSkills!=null && alSkills.size()!=0) { %>    
                                            <tr>
                                                <td>
                                                    <div id="skillPrimary"></div>
                                                </td>
                                            </tr>
                                            <% } %>
                                            <%if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) { %> 
                                            <tr>
                                                <td>
                                                    <div style="width: 75px; float: left; margin-right: 10px; font-weight: bold;">Overall:</div>
                                                    <div id="skillPrimaryOverall" style="float: left;"></div>
                                                </td>
                                            </tr>
                                            <% } %>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">My updates</h3>
                        <div class="box-tools pull-right">
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body" style="padding: 5px; overflow-y: auto;max-height: 250px; ">
                    	<div class="widget-content nopadding updates" id="collapseG3" style="height: auto;">
	                        <%
	                            String strThought = (String) request.getAttribute("DAY_THOUGHT_TEXT");
	                            String strThoughtBy = (String) request.getAttribute("DAY_THOUGHT_BY");
	                            if(strThought!=null){ %>
			                         <div class="new-update clearfix">
		                                <i class="fa fa-lightbulb-o"></i>
		                                <div class="update-done"><%=strThought %><span><strong>- <%=strThoughtBy %></strong></span> </div>
		                            </div>
	                        	<%} %>
	                        <%
	                            String strResignationStatus = (String) request.getAttribute("RESIGNATION_STATUS");
	                            String nRemaining = (String) request.getAttribute("RESIGNATION_REMAINING");
	                            String strResignationStatusD = (String) request.getAttribute("RESIGNATION_STATUS_D");
	                            
	                            if(strResignationStatus!=null) {
	                            %>
	                        <div class="new-update clearfix">
                                <i class="fa fa-bell-o"></i>
                                <div class="update-done"><%=strResignationStatusD %>
                                    <%if(uF.parseToInt(strResignationStatus) == 1) { %>
                                    <span><%=uF.showData(nRemaining+"", "0") %></span> 
                                    <% } %>
                                </div>
                            </div>
	                        <% } %>
	                        <%
	                            String nMailCount = (String)request.getAttribute("MAIL_COUNT");
	                            if(uF.parseToInt(nMailCount)>0) { %>
	                       		<div class="new-update clearfix">
	                                <i class="fa fa-envelope"></i>
	                                <div class="update-done">You have <a href="MyMail.action" title="My Mail"><strong><%=nMailCount %> new</strong></a> mails.
	                                </div>
	                            </div>
	                        <% } %>
                        </div>
                    </div>
                    <!-- /.box-body -->
                </div>
                <div class="box box-danger">
                    <% //if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){ 
                        Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
                              	if(hmEmp == null) hmEmp = new HashMap<String, String>();
                        	Map<String, String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
                        	if(empImageMap == null) empImageMap = new HashMap<String, String>();
                        %> 
                    <div class="box-header with-border">
                        <h3 class="box-title">My Team</h3>
                        <div class="box-tools pull-right">
                        	<span class="label label-danger"><%=hmEmp.size() %> Members</span>
                            <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                            <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                        </div>
                    </div>
                    <!-- /.box-header -->
                    <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 350px;">
                        <ul class="users-list clearfix">
                            <%
                                Iterator<String> itt = hmEmp.keySet().iterator();
                                while(itt.hasNext()) {
                                	String empId = itt.next();
                                	String empName = hmEmp.get(empId);
                                %>
                            <li>
                                <%if(DOC_RETRIVE_LOCATION==null) { %>
                                <img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empImageMap.get(empId.trim())%>">
                                <%} else if(empImageMap.get(empId.trim()) != null && !empImageMap.get(empId.trim()).equalsIgnoreCase("null")) { %>
                                <img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=DOC_RETRIVE_LOCATION +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId.trim()+"/"+IConstants.I_60x60+"/"+empImageMap.get(empId.trim())%>" />
                                <%} else { %>
                                <img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="userImages/avatar_photo.png" />
                                <% } %>    
                                <a class="users-list-name" href="javascript:void(0);" onclick="getEmpProfile('<%=empId %>');" title="<%=empName %>"><%=empName %></a>
                            </li>
                            <%  } %>
                            <% if(hmEmp == null || hmEmp.isEmpty()) { %>
                            <div class="tdDashLabel">No team available.</div>
                            <% } %>
                        </ul>
                    </div>
                    <!-- /.box-body -->
                </div>
            </section> --%>

		<section class="col-sm-12 col-md-12 col-lg-12 connectedSortable">

		<div class="box box-primary"
			style="background-color: #f3f3f359; box-shadow: unset;">

			<div class="card-body" style="padding: 5px; overflow-y: auto;">

				<div id="allFeedsDiv"
					style="height: 1000px; overflow-y: scroll; overflow-x: hidden;">
					<input type="hidden" name="hideOffsetCnt" id="hideOffsetCnt"
						value="10" />
					<s:hidden name="lastComunicationId" id="lastComunicationId" />

					<input type="text" id="txtPost" value="What's on your mind?"
						style="width: 96% !important; margin-left: 2%; height: 60px !important; cursor: pointer; margin-top: 1%; font-size: large; font-style: italic; color: #5d5f63; padding-left: 7% !important;">
					<span
						style="float: left; margin-left: 23px; margin-top: -43px; position: relative; z-index: 2;">
						<i class="fa fa-pencil-square-o fa-2x" aria-hidden="true"></i> </span>

					<!-- Modal -->
					<div class="modal" id="modalInfo2" role="dialog">
						<div class="modal-dialog model-dialog2">
							<!-- Modal content-->
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" id="closeButton2" class="close"
										data-dismiss="modal">&times;</button>
									<h4 class="modal-title modal-title2">Add Post</h4>
								</div>
								<div class="modal-body" id="modal-body2" style="height: 275px;">
									<s:form name="frm_feeds" action="Feeds" theme="simple"
										method="POST" cssClass="formcss" enctype="multipart/form-data">
										<s:hidden name="proId" id="proId"></s:hidden>
										<s:hidden name="pageFrom" id="pageFrom"></s:hidden>
										<s:hidden name="pageType" id="pageType"></s:hidden>
										<s:hidden name="taskId" id="taskId"></s:hidden>
										<s:hidden name="proFreqId" id="proFreqId"></s:hidden>
										<s:hidden name="invoiceId" id="invoiceId"></s:hidden>
										<div>
											<div style="float: left; padding-right: 2%; padding-top: 1%;"><%=(String)request.getAttribute("MYLargeImg") %></div>
											<div style="float: left; width: 82%;">
												<textarea rows="4" name="strCommunication"
													id="strCommunication" placeholder="What's on your mind?"
													style="border-radius: 2rem;"></textarea>
											</div>
										</div>

										<div style="padding: 2%; padding-top: 16%;">
											<div style="float: left;">
												<div style="float: right;">
													<div style="float: right; margin-left: 39px;">
														<div
															style="float: left; font-size: 12px; color: gray; font-style: italic;">
															&nbsp;</div>
														<br />
														<%if(pageFrom==null || pageFrom.trim().equalsIgnoreCase("null") || pageFrom.trim().equalsIgnoreCase("") || pageFrom.trim().equalsIgnoreCase("MyHub")) { %>
														<s:select name="strAlignWith" headerKey=""
															headerValue="Not aligned" list="alignTypeList"
															listKey="alignTypeId" listValue="alignTypeName"
															title="Select aligned type for your post"
															cssStyle="font-size: 12px;"
															onchange="getAlignTypeData(this.value, '0')" />
														<% } else if(pageFrom != null && (pageFrom.trim().equalsIgnoreCase("Project") || pageFrom.trim().equalsIgnoreCase("VAPProject"))) { %>
														<s:select name="strAlignWith" headerKey=""
															headerValue="Project" list="alignTypeList"
															listKey="alignTypeId" listValue="alignTypeName"
															title="Select aligned type for your post"
															cssStyle=" font-size: 12px;"
															onchange="getAlignTypeData(this.value, '0')" />
														<% } %>
														<div id="alignDataDiv_0">&nbsp;</div>
													</div>

												</div>
												<div style="float: left; margin-right: 5px;">
													<div
														style="float: left; font-size: 12px; color: gray; font-style: italic;">
														Tag</div>
													<br />
													<s:select name="strTaggedWith" id="strTaggedWith"
														list="resourceList" listKey="employeeId"
														listValue="employeeName"
														title="Tag resources for your post"
														cssStyle=" font-size: 12px;" multiple="true" />
													<!-- headerKey="" headerValue="Select"  -->
												</div>
											</div>
											<div style="float: right; margin-right: 5px;">
												<div>
													<div
														style="float: left; font-size: 12px; color: gray; font-style: italic;">
														&nbsp;</div>
													<br />
													<%if(strUserType!=null && strUserType.equals(IConstants.CUSTOMER)) { %>
													<%if(pageFrom==null || pageFrom.trim().equals("") || pageFrom.trim().equalsIgnoreCase("null")) { %>
													<s:select name="strVisibility" id="strVisibility_0"
														list="#{'2':'Resource'}" cssStyle=" font-size: 12px;"
														onchange="checkVisibility(this.value, '0')" />
													<!-- headerKey="2" headerValue="" -->
													<% } else { %>
													<s:select name="strVisibility" id="strVisibility_0"
														headerKey="1" headerValue="Team" list="#{'2':'Resources'}"
														cssStyle=" font-size: 12px;"
														onchange="checkVisibility(this.value, '0')" />
													<% } %>
													<% } else { %>
													<%if(pageFrom==null || pageFrom.trim().equals("") || pageFrom.trim().equalsIgnoreCase("null")) { %>
													<s:select name="strVisibility" id="strVisibility_0"
														headerKey="0" headerValue="Public"
														list="#{'2':'Resources'}" cssStyle=" font-size: 12px;"
														onchange="checkVisibility(this.value, '0')" />
													<% } else { %>
													<s:select name="strVisibility" id="strVisibility_0"
														headerKey="0" headerValue="Public"
														list="#{'1':'Team', '2':'Resources', '3':'Customer'}"
														cssStyle=" font-size: 12px;"
														onchange="checkVisibility(this.value, '0')" />
													<% } %>
													<% } %>
												</div>
												<div id="visibilityWithDiv_0"
													style="display: <%=((strUserType!=null && strUserType.equals(IConstants.CUSTOMER)) && (pageFrom==null || pageFrom.trim().equals("") || pageFrom.trim().equalsIgnoreCase("null"))) ? "block" : "none" %>; float: right; margin-right: 5px;">
													<div
														style="float: left; font-size: 12px; color: gray; font-style: italic;">
														Share</div>
													<br />
													<s:select name="strVisibilityWith" id="strVisibilityWith"
														list="resourceList" listKey="employeeId"
														listValue="employeeName" title="Visibility for your post"
														cssStyle="width: 80px; font-size: 12px;" multiple="true" />
													<!-- headerKey="" headerValue="Select"  -->
												</div>
											</div>
										</div>
										<div id="addPostActions"
											style="margin-left: 80%; margin-top: 17%;">
											<img height="0" width="0" class="lazy img-circle"
												name="feedDocOrImg" id="feedDocOrImg"
												style="display: none; border: 1px solid rgb(204, 204, 204); width: 62px; height: 62px; float: left; margin-left: -29%;"
												src="" data-original="" />
											<!-- userImages/avatar_photo.png -->
											<input type="file"
												accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs"
												id="strFeedDoc" name="strFeedDoc"
												style="display: none; font-size: 12px; height: 22px; vertical-align: top;"
												onchange="readImageURL(this, 'feedDocOrImg');">
											<div style="float: left; margin-top: 5%;">
												<div id="postAttachment">
													<i class="fa fa-paperclip fa-lg" title="Add attachment"
														aria-hidden="true"></i>
												</div>
											</div>
											<div>
												<s:submit name="btnPost" cssClass="btn btn-primary"
													cssStyle="margin-left: 5px;margin-top: 3%;" value="Post" />
											</div>
										</div>
									</s:form>
								</div>
							</div>
						</div>
					</div>



					<% 
                                    List<String> availableExt = (List<String>)request.getAttribute("availableExt");
                                    if(availableExt == null) availableExt = new ArrayList<String>();
                                    
                                    	Set<String> setFeeds = hmFeedIds.keySet();
                                    	Iterator<String> it = setFeeds.iterator();
                                    	
                                    	while(it.hasNext()) {
                                    		String strPostId = it.next();
                                    		//System.out.println("strPostId ===>> " + strPostId);
                                    		String postContentClass = "";
                                    		List<String> innerList = hmFeeds.get(strPostId);
                                    		if(innerList == null) innerList = new ArrayList<String>();
                                    		if(innerList != null && !innerList.isEmpty() && !innerList.equals("")) {
                                    		boolean flag = false;
                                    		if(availableExt.contains(innerList.get(15))) {
                                    			flag = true;
                                    		}
                                    %>
					<div class="card" id="mainPostDiv_<%=innerList.get(0) %>">
						<div class="card-header" id="postDataDiv_<%=innerList.get(0) %>">
							<div class="user-block" style="width: 95%; float: left;">
								<% if(uF.parseToInt(innerList.get(17)) != IConstants.FT_ACTIVITY) { 
							              		postContentClass = "postSystemContent";
							              	%>
								<%=innerList.get(9) %>
								<span class="username"><%=innerList.get(10) %> <% if(innerList.get(2) != null && !innerList.get(2).equals("")) { %>
									has posted a message on <%=innerList.get(2) %> <%=innerList.get(3) %>
									<% } %> <% if(innerList.get(4) != null && !innerList.get(4).equals("")) { %>
									with <%=innerList.get(4) %> <% } %> </span>
								<% } else {
							                	postContentClass = "postContent";
							                }%>
								<span class="description"><%=innerList.get(11) %></span>
							</div>
							<!-- /.user-block -->


							<!-- menu -->

							<% if(uF.parseToInt(strEmpId) == uF.parseToInt(innerList.get(13)) && uF.parseToInt(innerList.get(17)) != IConstants.FT_ACTIVITY) { %>
							<div style="position: relative; margin-left: 60%;"
								onclick="showCustomDropdown(<%=innerList.get(0) %>)">
								<div class="customDropdown">
									<!-- three dots -->
									<ul class="dropbtn icons btn-right showLeft"
										style="padding-left: 0px !important;">
										<i class="fa fa-ellipsis-h" aria-hidden="true"></i>
									</ul>

									<div id="listCustomDropdown_<%=innerList.get(0) %>"
										class="customDropdown-content" style="margin-top: 14%;">
										<a href="javascript:void(0);"
											onclick="editYourPostPopup('<%=innerList.get(0) %>', 'P_E', '<%=pageFrom %>', '<%=proId %>', '<%=taskId %>', '<%=proFreqId %>', '<%=invoiceId %>');"><i
											class="fa fa-pencil-square-o"></i>Edit post</a> <a
											href="javascript:void(0);"
											onclick="editYourPost('<%=innerList.get(0) %>', 'P_D', '<%=pageFrom %>', '<%=proId %>', '<%=taskId %>', '<%=proFreqId %>', '<%=invoiceId %>');"><i
											class="fa fa-trash"></i>Delete post</a>
									</div>
								</div>
							</div>
							<%} %>


						</div>
						<!-- /.box-header -->

						<div class="box-body" style="display: block; padding-top: 2%;">
							<% if(innerList.get(14) != null && !innerList.get(14).equals("")) {
                                            String filePath = innerList.get(16)+"/"+innerList.get(14);
                                            %>

							<%if(innerList.get(15)!=null && (innerList.get(15).equalsIgnoreCase("jpg") || innerList.get(15).equalsIgnoreCase("jpeg") || innerList.get(15).equalsIgnoreCase("png") || innerList.get(15).equalsIgnoreCase("bmp") || innerList.get(15).equalsIgnoreCase("gif"))){ %>
							<a href="javascript:void(0);"
								onclick="openImagePopup('<%=innerList.get(0)%>','<%=innerList.get(1).replaceAll("\\n"," ").replaceAll("\r"," ")%>','<%=pageFrom%>')"><img
								class="lazy img-responsive pad" src="<%=filePath %>"
								data-original="<%=filePath %>"
								style="max-width: 100%; max-height: 250px;">
							</a>
							<% } else {
                                                if(flag) {
                                                %>
							<div id="tblDiv" style="padding-top: 6% !important;margin-bottom: -4% !important;">
								<a href="javascript:void(0);"
									onclick="viewFeedsFilePopup('<%=innerList.get(0)%>');event.preventDefault();"
									style="color: gray;"><i class="fa fa-file-text-o fa-2x" aria-hidden="true"></i></a>
							</div>
							<%	} else { %>
							<div id="tblDiv">
								<div
									style="text-align: center; font-size: 20px; padding: 150px;">Preview
									not available</div>
							</div>
							<%	} } }  %>
							<div id="postContent" class="<%=postContentClass %>">
								<%=innerList.get(1) %>
							</div>

							<div id="postLikeDiv" class="postLikeDiv">
								<span id="likeCountDiv_<%=innerList.get(0) %>"> <%if((innerList.get(18)!=null && !innerList.get(18).equals("") && !innerList.get(18).equals(",")) || (innerList.get(19)!=null && !innerList.get(19).equals("") && !innerList.get(19).equals(","))){ %>
									<a href="javascript:void(0)" style="color: gray;"
									onclick="openLikesPopup('<%=innerList.get(18)%>','<%=innerList.get(19)%>');">
										<%=innerList.get(20) %> likes</a> <%} else{%> <%=innerList.get(20) %>
									likes <%} %> </span>

								<hr class="postLikeHr">
							</div>
							<div id="postActions" class="postActions">
								<%
                                            String blueLikeDiv = "none";
                                            String grayLikeDiv = "block";
                                            String likeStatus = "0";
                                            if(innerList.get(12) != null && innerList.get(12).equalsIgnoreCase("Y")) {
                                            	blueLikeDiv = "block";
                                            	grayLikeDiv = "none";
                                            	likeStatus = "1";
                                            } 
										%>

								<div id="blueLikeDiv_<%=innerList.get(0) %>"
									style="display: <%=blueLikeDiv %>;float:left;">
									<input type="hidden" name="likeStatus"
										id="likeStatus_<%=innerList.get(0) %>"
										value="<%=likeStatus %>" /> <a href="javascript:void(0)"
										onclick="checkLikeUnlike('<%=innerList.get(0) %>', 'P','<%=innerList.get(20) %>');">
										<button type="button" class="btn btn-default btn-xs"
											style="color: #007FFF;">
											<i class="fa fa-thumbs-o-up"></i> Like
										</button> </a>
								</div>
								<div id="grayLikeDiv_<%=innerList.get(0) %>"
									style="display: <%=grayLikeDiv %>;float:left;">
									<a href="javascript:void(0)"
										onclick="checkLikeUnlike('<%=innerList.get(0) %>', 'P','<%=innerList.get(20) %>');">
										<button type="button" class="btn btn-default btn-xs">
											<i class="fa fa-thumbs-o-up"></i> Like
										</button> </a>
								</div>

								<div id="actionComment_<%=innerList.get(0) %>"
									style="display: block; float: left; padding-left: 0.5%;">
									<a href="javascript:void(0)"
										onclick="enableCommenting('<%=innerList.get(0) %>');">
										<button type="button" class="btn btn-default btn-xs">
											<i class="fa fa-commenting-o"></i> Comment
										</button> </a>
								</div>
							</div>
						</div>

						<div class="box-footer addCommentDiv"
							style="display: none; padding-top: unset;"
							id="newCommentDiv_<%=innerList.get(0) %>">
							<div id="commentUserImg" style="float: left; margin-left: 2%;">
								<%=(String)request.getAttribute("MYImg") %>
							</div>
							<textarea class="form-control"
								placeholder="Press enter to post comment"
								style="width: 90% !important; vertical-align: top; margin-left: 2%; height: 40px; border-radius: 3rem; resize: none; overflow: hidden;"
								name="strComment" id="strComment_<%=innerList.get(0) %>"
								onkeydown="addComment(event, '<%=innerList.get(0) %>', 'A', '<%=innerList.get(0) %>');"></textarea>
							<input type="hidden" name="hideTaggedWith_<%=innerList.get(0) %>"
								id="hideTaggedWith_<%=innerList.get(0) %>" value="0" /> <br />
							<div style="margin-left: 40px; margin-right: 5px;">
								<a href="javascript:void(0);" style="font-weight: normal;"
									onclick="openCloseTaggedWith('<%=innerList.get(0) %>');">Tag</a>
							</div>
							<div id="commentTaggedWith_<%=innerList.get(0) %>"
								style="display: none; margin-right: 5px; margin-left: 40px;">
								<select name="strCommentTaggedWith_<%=innerList.get(0) %>"
									id="strCommentTaggedWith_<%=innerList.get(0) %>"
									title="Tag resources for your post" style="margin-top: 10px;"
									size="3" multiple="true">
									<%=(String)request.getAttribute("sbTaggedWithOption") %>
								</select>
							</div>
						</div>
						<% 
                                                List<List<String>> alComments = new ArrayList<List<String>>();
							            		//System.out.println("hmComments size() ===>> " + hmComments != null ? hmComments.size() : "");
                                                if(hmComments != null && !hmComments.isEmpty()) {
                                                	alComments = hmComments.get(innerList.get(0));
                                                	//System.out.println("hmComments ===>> " + hmComments);
                                                %>
						<div class="box-footer box-comments clr"
							style="padding: 8px;display: <% if(alComments != null && !alComments.isEmpty()) { %>block; <% } else { %>none; <% } %>"
							id="commentDiv_<%=innerList.get(0) %>">
							<% 
                                                	for(int i=0; alComments != null && !alComments.isEmpty() && i<alComments.size(); i++) {
                                                		List<String> cInnerList = alComments.get(i);
                                                		
                                                		String blueLikeDiv1 = "none";
                                                		String grayLikeDiv1 = "inline";
                                                		String likeStatus1 = "0";
                                                		
                                                		if(cInnerList.get(7) != null && cInnerList.get(7).equalsIgnoreCase("Y")) {
                                                			blueLikeDiv1 = "inline";
                                                			grayLikeDiv1 = "none";
                                                			likeStatus1 = "1";
                                                		}
                                                %>
							<div class="box-comment" id="comment_<%=cInnerList.get(0) %>">
								<%=cInnerList.get(4) %>
								<div class="comment-text">
									<span class="username"> <%=cInnerList.get(5) %> <span
										class="text-muted pull-right"> <% if(uF.parseToInt(strEmpId) == uF.parseToInt(cInnerList.get(8))) { %>

											<div style="position: relative; margin-right: -4%;"
												onclick="showCommentCustomDropdown(<%=innerList.get(0) %>)">
												<div class="customDropdown">
													<!-- three dots -->
													<ul class="dropbtn icons btn-right showLeft"
														style="padding-left: 0px !important;">
														<i class="fa fa-ellipsis-h" aria-hidden="true"></i>
													</ul>

													<div id="listCommentCustomDropdown_<%=innerList.get(0) %>"
														class="customDropdown-content" style="margin-top: 14%;">
														<a href="javascript:void(0);"
															onclick="editYourComment('<%=cInnerList.get(0) %>', 'Y_E', '<%=innerList.get(0) %>');"><i
															class="fa fa-pencil-square-o"></i>Edit post</a> <a
															href="javascript:void(0);"
															onclick="editYourComment('<%=cInnerList.get(0) %>', 'Y_D', '<%=innerList.get(0) %>');"><i
															class="fa fa-trash"></i>Delete post</a>
													</div>
												</div>
											</div> <% } %> </span> </span>
									<!-- /.username -->
									<%=cInnerList.get(1) %>
									<div>
										<input type="hidden" name="likeStatus"
											id="likeStatus_<%=cInnerList.get(0) %>"
											value="<%=likeStatus1 %>" />
										<div id="blueLikeDiv_<%=cInnerList.get(0) %>"
											style="display: <%=blueLikeDiv1 %>;">
											<a href="javascript:void(0)"
												onclick="checkLikeUnlike('<%=cInnerList.get(0) %>', 'C','<%=cInnerList.get(12) %>');">
												<button type="button" class="btn btn-default btn-xs"
													style="color: #007FFF;">
													<i class="fa fa-thumbs-o-up"></i>UnLike
												</button> </a>
										</div>
										<div id="grayLikeDiv_<%=cInnerList.get(0) %>"
											style="display: <%=grayLikeDiv1 %>;">
											<a href="javascript:void(0)"
												onclick="checkLikeUnlike('<%=cInnerList.get(0) %>', 'C','<%=cInnerList.get(12) %>');">
												<button type="button" class="btn btn-default btn-xs"
													style="color: #007FFF;">
													<i class="fa fa-thumbs-o-up"></i> Like
												</button> </a>
										</div>
										<div id="likeCountDiv_<%=cInnerList.get(0) %>"
											style="line-height: 15px; color: #006699;" class="inline">
											<%if((cInnerList.get(10)!=null && !cInnerList.get(10).equals("") && !cInnerList.get(10).equals(",")) || (cInnerList.get(11)!=null && !cInnerList.get(11).equals("") && !cInnerList.get(11).equals(","))){ %>
											<a href="javascript:void(0)" style="color: gray;"
												onclick="openLikesPopup('<%=cInnerList.get(10)%>','<%=cInnerList.get(11)%>');">
												<%=cInnerList.get(12) %></a>
											<%} else{%>
											<%=cInnerList.get(12) %>
											<%} %>
										</div>
									</div>
									<% if(cInnerList.get(9) != null && !cInnerList.get(9).equalsIgnoreCase("")) { %>
									<div style="width: 100%;">
										Tagged with:
										<%=cInnerList.get(9) %></div>
									<% } %>
								</div>
							</div>
							<% } %>
							<%if(hmLastCommentId !=null && uF.parseToInt(hmLastCommentId.get(innerList.get(0)+"_REMAIN_COMEENT_COUNT")) > 0) { %>
							<div id="morecommentDiv_<%=innerList.get(0) %>">
								<input type="hidden" name="commentCnt_<%=innerList.get(0) %>"
									id="commentCnt_<%=innerList.get(0) %>"
									value="<%=(hmLastCommentId !=null && hmLastCommentId.get(innerList.get(0)+"_LAST_COMEENT_ID") != null) ? hmLastCommentId.get(innerList.get(0)+"_LAST_COMEENT_ID") : "0" %>" />
								<a href="javascript:void(0);"
									onclick="viewMoreComments('<%=innerList.get(0) %>')"
									style="font-weight: normal; color: #006699;">view <%=uF.parseToInt(hmLastCommentId.get(innerList.get(0)+"_REMAIN_COMEENT_COUNT")) %>
									more comments</a>
							</div>
							<% } %>
						</div>
						<% } %>
					</div>
					<% } %>
					<% } %>
				</div>
				<div id="loadingDiv" style="display: none;">
					<div id="the_div">
						<div id="ajaxLoadImage"></div>
					</div>
				</div>
				<div id="loadMoreFeedDiv"
					style="float: left; width: 100%; text-align: center; display: none;">
					<a href="javascript:void(0)" onclick="loadMoreFeeds()">load
						more ...</a>
				</div>
			</div>
		</div>
		</section>
	</div>
	</section>
	<%if(pageFrom==null || (!pageFrom.trim().equalsIgnoreCase("Project") && !pageFrom.trim().equalsIgnoreCase("MyHub")) ) { %>
</div>
<% } %>
<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" id="closeButton" class="close"
					data-dismiss="modal">&times;</button>
				<h4 class="modal-title">Candidate Information</h4>
			</div>
			<div class="modal-body"
				style="height: 390px; overflow-y: auto; padding-left: 25px;">
			</div>
		</div>
	</div>
</div>
<div class="modal" id="modalInfo1" role="dialog">
	<div class="modal-dialog modal-dialog1">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title modal-title1"></h4>
			</div>
			<div class="modal-body" id="modal-body1"
				style="height: 400px; overflow-y: auto; padding-left: 25px;">
			</div>
			<div class="modal-footer">
				<button type="button" id="closeButton1" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    <%
        if (alSkills != null && alSkills.size() != 0) {
        	double dblOverall = 0.0d;
        	int nOverall = 0;
        	for (int i = 0; i < alSkills.size(); i++) {
        		List<String> alInner = alSkills.get(i);
        		nOverall++;
         			dblOverall += uF.parseToDouble(alInner.get(2));
        %>
    	$('#star<%=i %>').raty({
    		  readOnly: true,
    		  start: <%=uF.parseToDouble(alInner.get(2)) / 2 %>,
    		  half: true
    		});
    	<%}
        double dblOverallRating = 0.0d;
        if(dblOverall > 0.0d){
        	dblOverallRating = (dblOverall/nOverall) / 2;
        }
        	%>
    $('#skillPrimaryOverall').raty({
    	readOnly: true,
    	start: <%=dblOverallRating%>,
    	half: true,
          	targetType: 'number'
    });
    <% //System.out.println("dblOverallRating end ===>>>> " + dblOverallRating); 
        } %>
    
    
    
    function getEmpProfile(val) {
    var dialogEdit = '.modal-body';
    $(dialogEdit).empty();
    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    $("#modalInfo").show();
    $(".modal-title").html('Employee Profile');
    $.ajax({
    	url : "AppraisalEmpProfile.action?empId=" + val,
    	cache : false,
    	success : function(data) {
    		$(dialogEdit).html(data);
    	}
    });
    }
    
    
    function showEditPhoto(){
     	document.getElementById("uploadPhotoDiv").style.display= "block";
     }
    
    
     function hideEditPhoto(){
     	document.getElementById("uploadPhotoDiv").style.display= "none";    	
     }
     
     function enableCommenting(id){
       	var divId = "newCommentDiv_"+id;
      	document.getElementById(divId).style.display= "block";    	
      }
     
</script>


<script type='text/javascript'>
       $(document).ready(function(){
       	
       	$("body").on('click','#closeButton',function(){
       		debugger;
       		$(".modal-dialog").removeAttr('style');
       		$("#modalInfo").hide();
       		$("#modalInfo").removeData();
           });
       	
       	$("body").on('click','#closeButton1',function(){
       		debugger;
       		$(".modal-dialog1").removeAttr('style');
    		$("#modalInfo1").hide();
    		$("#modalInfo1").removeData();
        });
       	$("body").on('click','#closeButton2',function(){
       		debugger;
       		$(".modal-dialog2").removeAttr('style');
    		$("#modalInfo2").hide();
    		$("#modalInfo2").removeData();
        });
   	});        
       
       $("#allFeedsDiv").scroll(function(){
    	   debugger;
    	   
    	   var elem = $("#allFeedsDiv");
    	   var val = elem[0].scrollHeight - Math.ceil(elem.scrollTop())
    	    if (val >= elem.outerHeight() - 10 &&  val <= elem.outerHeight())
    	    {
    	    	loadMoreFeeds();
    	    
    	    }
      });
       
       function showCustomDropdown(id) {
    	   debugger;
       	var drpDwnId = "listCustomDropdown_"+id;
           document.getElementById(drpDwnId).classList.toggle("show");
       }

       
       function showCommentCustomDropdown(id) {
    	   debugger;
       	var drpDwnId = "listCommentCustomDropdown_"+id;
           document.getElementById(drpDwnId).classList.toggle("show");
       }
       
       // Close the dropdown if the user clicks outside of it
       window.onclick = function(event) {
           if (!event.target.matches('.fa.fa-ellipsis-h')) {
               var dropdowns = document.getElementsByClassName("customDropdown-content");
               var i;
               for (i = 0; i < dropdowns.length; i++) {
                   var openDropdown = dropdowns[i];
                   if (openDropdown.classList.contains('show')) {
                       openDropdown.classList.remove('show');
                   }
               }
           }
       }
       
       $('#postAttachment').bind("click" , function () {
           $('#strFeedDoc').click();
       });
       
       $('#txtPost').on('click', function(){
    	   debugger;
    	   $('.modal').each(function () {
    	        $(this).modal('hide');
    	    });
    	   $('#modalInfo2').modal('show');
   	    });    
       
       $(window).bind('mousewheel DOMMouseScroll', function(event){
       
    	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
    	        // scroll up
    	        if($(window).scrollTop() == 0 && $("#allFeedsDiv").scrollTop() != 0){
    	        	$("#allFeedsDiv").scrollTop($("#allFeedsDiv").scrollTop() - 30);
    	        }
    	    }
    	    else {
    	        // scroll down
    	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
		    		   $("#allFeedsDiv").scrollTop($("#allFeedsDiv").scrollTop() + 30);
    	   		}
    	    }
    	});
       
       $(window).keydown(function(event){
    	   
       		if(event.which == 40 || event.which == 34)
       		{
       			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
		    		   $("#allFeedsDiv").scrollTop($("#allFeedsDiv").scrollTop() + 50);
 	   			}
       		}
       		else if(event.which == 38 || event.which == 33)
      		{
    		   if($(window).scrollTop() == 0 && $("#allFeedsDiv").scrollTop() != 0){
	        		$("#allFeedsDiv").scrollTop($("#allFeedsDiv").scrollTop() - 50);
	        	}
	   		}
       });
       
</script>
<!-- Latest compiled and minified JavaScript -->


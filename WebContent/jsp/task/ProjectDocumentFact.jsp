<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*,com.konnect.jpms.util.UtilityFunctions,com.konnect.jpms.task.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

        
<script type="text/javascript" src="scripts/charts/jquery.min.js"> </script>

<script type="text/javascript">
(function($){
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
            console.log("Extension : "+ext);
			if (/^(tiff|pdf|ppt|pptx|pps|doc|docx|txt|xls|xlsx)$/.test(ext)) {
				$(this).after(function () {
					var id = $(this).attr('id');
					var gdvId = (typeof id !== 'undefined' && id !== false) ? id + '-gdocsviewer' : '';
					return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 742px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 742px; border: none;margin : 0 auto; display : block;"></iframe></div>';
				})
			}
		});
	};
})( jQuery );

/* return '<div id="' + gdvId + '" class="gdocsviewer"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '" width="' + settings.width + '" height="' + settings.height + '" style="border: none;margin : 0 auto; display : block;"></iframe></div>'; */
$(document).ready(function () {
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
   	
	$("#strTaggedWith").multiselect().multiselectfilter();
	
    $('a.embed').gdocsViewer();
    //$('#embedURL').gdocsViewer();
});

</script>

<script type="text/javascript"> 


function getVersionHistory() {
		var status = document.getElementById("hideVerDocSpanStatus").value;
		if(status == '0') {
			document.getElementById('hideVerDocSpanStatus').value = '1';
			document.getElementById('verDownarrowSpan').style.display = 'none';
			document.getElementById('verUparrowSpan').style.display = 'inline';
			
			document.getElementById('versionTr').style.display = 'table-row';
		} else {
			document.getElementById('hideVerDocSpanStatus').value = '0';
			document.getElementById('verDownarrowSpan').style.display = 'inline';
			document.getElementById('verUparrowSpan').style.display = 'none';
			
			document.getElementById('versionTr').style.display = 'none';
		}
	}



function checkLikeUnlike(postId, type, divCount) {
	var likeStatus = document.getElementById("likeStatus_"+postId).value;
	if(likeStatus == '1') {
		document.getElementById("likeStatus_"+postId).value = '0';
		document.getElementById("blueLikeDiv_"+postId).style.display= 'none';
		document.getElementById("grayLikeDiv_"+postId).style.display= 'inline';
		getContent('likeCountDiv_'+postId, 'LikeUnlikeAndLikeData.action?likeUnlike=UL&postId='+postId+'&type='+ type);
	} else {
		document.getElementById("likeStatus_"+postId).value = '1';
		document.getElementById("blueLikeDiv_"+postId).style.display= 'inline';
		document.getElementById("grayLikeDiv_"+postId).style.display= 'none';
		getContent('likeCountDiv_'+postId, 'LikeUnlikeAndLikeData.action?likeUnlike=L&postId='+postId+'&type='+ type);
	}
}

function addComment(event, postId, type, mainPostId) {
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
            var xhr = $.ajax({
                url : 'AddCommentOnPost.action?postId='+postId+'&strComment='+strComment+'&type='+type+'&mainPostId='+mainPostId,
                cache : false,
                success : function(data) {
                	var allData = data.split("::::");
                	//alert("data ===>> " + data);
					if(type == 'A') {
	                	var divTag2 = document.createElement("div");
						divTag2.id = "comment_"+allData[1].trim();
						divTag2.setAttribute("style","float:left; width: 100%; padding: 3px 0px;");
						divTag2.innerHTML = allData[0];
	                    
						document.getElementById("commentDiv_"+postId).appendChild(divTag2);
						document.getElementById("strComment_"+postId).value = "";
						
                	} else {
                		document.getElementById("comment_"+postId).innerHTML = allData[0];
                	}
					document.getElementById("newCommentDiv_"+mainPostId).style.display = 'block';
                }
            });
	    }
	} else if(event.ctrlKey && event.keyCode == 13) {
		var strComment = document.getElementById("strComment_"+postId).value;
		document.getElementById("strComment_"+postId).value = strComment + '\n';
	}
}


function editYourComment(postId, type, mainPostId) {
	
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
            var xhr = $.ajax({
                url : 'EditAndDeleteComment.action?postId='+postId+'&type='+type+'&mainPostId='+mainPostId,
                cache : false,
                success : function(data) {
                	//alert("type ===>> " + type);
                	document.getElementById("comment_"+postId).innerHTML = data;
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
            	var divTag1 = document.getElementById("commentDiv_"+postId).innerHTML;
				
            	var allData = data.split("::::");
            	//alert("allData ===>> " + allData);
            	document.getElementById("commentDiv_"+postId).innerHTML = allData[0]+divTag1;
            	document.getElementById("morecommentDiv_"+postId).innerHTML = allData[1];
            	
            }
        });
    	
	}
}


function addGetTaggedWith(postId) {
	var strTaggedWith = getSelectedValue("strTaggedWith");
	getContent('taggedWithDiv', 'AddDocumentTaggedWith.action?strFeedId='+postId+'&strTaggedWith='+ strTaggedWith);
	$("#strTaggedWith").multiselect().multiselectfilter();
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

</script> 
<%String fileName = "View "+(String) request.getAttribute("folderName")+" file"; %>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=fileName %>" name="title" />
</jsp:include> --%>

<!-- <div class="leftbox reportWidth" style="font-size: 12px;"> -->
	<%
		UtilityFunctions uF = new UtilityFunctions();
		String filePath = (String) request.getAttribute("filePath");
		String fileDir = (String) request.getAttribute("fileDir");
		String strEmpId = (String)session.getAttribute(IConstants.EMPID);
		String strOrgId = (String)session.getAttribute(IConstants.ORGID);
		
		//System.out.println("filePath=======>"+filePath);
		//System.out.println("fileDir=======>"+fileDir);
		Map<String, String> hmProDocumentDetails = (Map<String, String>) request.getAttribute("hmProDocumentDetails");
		List<Map<String, String>> alVersion = (List<Map<String, String>>)request.getAttribute("alVersion");
		if(alVersion == null) alVersion = new ArrayList<Map<String,String>>();
		Map<String, String> hmFileIcon = (Map<String, String>) request.getAttribute("hmFileIcon");
		if(hmFileIcon == null) hmFileIcon = new HashMap<String, String>();
		
		List<String> availableExt = (List<String>)request.getAttribute("availableExt");
		if(availableExt == null) availableExt = new ArrayList<String>();
		
		List<String> innerList = (List<String>)request.getAttribute("innerList");
		Map<String, List<List<String>>> hmComments = (Map<String, List<List<String>>>) request.getAttribute("hmComments");
		Map<String, String> hmLastCommentId = (Map<String, String>) request.getAttribute("hmLastCommentId");
		
		if(hmProDocumentDetails !=null && !hmProDocumentDetails.isEmpty() && hmProDocumentDetails.size() > 0){
			String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
			if(hmFileIcon.containsKey(hmProDocumentDetails.get("FILE_EXTENSION"))){ 
				fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmProDocumentDetails.get("FILE_EXTENSION"));
			}
			boolean flag = false;
			if(availableExt.contains(hmProDocumentDetails.get("FILE_EXTENSION"))){
				flag = true;
			}
	%>
	
	<section class="content">
	          <!-- title row -->
		<div class="row">
			<div class="col-md-12">
				<div class="box box-body">
					<p><strong><%=uF.showData(hmProDocumentDetails.get("DOCUMENT_NAME"),"") %></strong></p>
					<a target="_blank" href="<%=filePath %>" style="font-weight: normal; float:right;">Download</a>
					<p>Size: <%=uF.showData(hmProDocumentDetails.get("FILE_SIZE"),"") %></p>
					<p style="font-size: 12px; font-style: italic;">Last update at <%=uF.showData(hmProDocumentDetails.get("ENTRY_TIME"),"") %> on <%=uF.showData(hmProDocumentDetails.get("ENTRY_DATE"),"") %> by <%=uF.showData(hmProDocumentDetails.get("ADDED_BY"),"") %></p>
				
	
					<%if(hmProDocumentDetails.get("FILE_EXTENSION")!=null && (hmProDocumentDetails.get("FILE_EXTENSION").equals("jpg") || hmProDocumentDetails.get("FILE_EXTENSION").equals("jpeg") || hmProDocumentDetails.get("FILE_EXTENSION").equals("png") || hmProDocumentDetails.get("FILE_EXTENSION").equals("bmp") || hmProDocumentDetails.get("FILE_EXTENSION").equals("gif"))){ %>
						<div id="tblDiv" style=" float: left; height:150px; width:150px; margin:2px 10px 0px 0px; border: 1px solid gray;">
							<%-- <img height="150" width="150" src="<%=filePath %>" /> --%>
							<img height="150" width="150" class="lazy" src="userImages/avatar_photo.png" data-original="<%=filePath%>" />
						</div>
					<%} else {
						if(flag) {
					%>
						<div style="float: left; width: 55%; text-align:center">
							<iframe src="https://docs.google.com/viewer?url=<%=filePath %>&embedded=true" frameborder="0" height="600px" width="99%"></iframe>
						</div>
						<%-- <div id="tblDiv" style="float: left; width: 55%;">
							<a href="<%=filePath %>" class="embed" id="test">&nbsp;</a>
						</div> --%>
					<%	} else {
					%>
							<div id="tblDiv" style="float: left; width: 55%; height: 500px; background-color: #CCCCCC;">
								<div style="text-align: center; font-size: 24px; padding: 150px;">Preview not available</div>
							</div>
					<%	}
					}%>
				
				
				<div class="box-body table-responsive no-padding" style="float: left; min-height: 400px; width: 32%; padding-left: 6px;">
					<table class="table table-hover" id="documentTable" style="padding-left: 10px;">
						<tr>
							<td nowrap="nowrap" style="width: 150px;"><strong>Version History:</strong></td>
							<td nowrap="nowrap">
								<input type="hidden" name="hideVerDocSpanStatus" id="hideVerDocSpanStatus" value = "0"/>
								<span style="float: left; margin-left: 2px; margin-top: 5px; width:100% "> 
									<a target="_blank" href="<%=filePath %>" style="font-weight: normal; color: blue;"> 
										<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=uF.showData(hmProDocumentDetails.get("DOCUMENT_NAME"),"") %>
									</a>
								</span>
								<a href="javascript:void(0)" onclick="getVersionHistory();">
									<span id="verDownarrowSpan" style="float: left; margin-left: 2px; margin-top: 5px;"> 
										<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
									</span>
									<span id="verUparrowSpan" style="float: left; margin-left: 2px; margin-top: 5px; display: none;">
										<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
									</span>
								</a>
							</td>
						</tr>
						
						<tr id="versionTr" style="display: none;">
							<td nowrap="nowrap" style="width: 150px;">&nbsp;</td>
							<td>
							<%
								for(int i = 0; i < alVersion.size(); i++){ 
									Map<String, String> hmVersionHistory = (Map<String, String>) alVersion.get(i);
									String fileIcon1 = request.getContextPath()+"/images1/icons/icons/file_icon.png";
									if(hmFileIcon.containsKey(hmVersionHistory.get("FILE_EXTENSION"))){
										fileIcon1 = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmVersionHistory.get("FILE_EXTENSION"));
									}
							%>
									<table class="table_style">
										<tr>
											<td nowrap="nowrap"><%=(i+1) %>.&nbsp;
												<a target="_blank" href="<%=fileDir+"/"+hmVersionHistory.get("DOCUMENT_NAME") %>" style="font-weight: normal; color: blue;"> 
													<img height="18" width="18" src="<%=fileIcon1 %>" />&nbsp;<%=uF.showData(hmVersionHistory.get("DOCUMENT_NAME"),"") %>
												</a>
											</td>
										</tr>
									</table>
									
							<%	} %>
							</td>
						</tr>
						<tr>
							<td nowrap="nowrap"><strong>Category:</strong></td>
							<td><%=uF.showData(hmProDocumentDetails.get("CATEGORY"),"") %></td>
						</tr>
						<tr>
							<td nowrap="nowrap"><strong>Aligned:</strong></td>
							<td><%=uF.showData(hmProDocumentDetails.get("ALIGN"),"") %></td>
						</tr>
						<tr>
							<td nowrap="nowrap"><strong>Sharing:</strong></td>
							<td><%=uF.showData(hmProDocumentDetails.get("SHARING_RESOURCES"),"") %></td>
						</tr>
						<tr>
							<td nowrap="nowrap" valign="top"><strong>Description:</strong></td>
							<td><%=uF.showData(hmProDocumentDetails.get("DESCRIPTION"),"") %></td>
						</tr>
						
						<% if(innerList != null && !innerList.isEmpty()) { %>
						<tr>
							<td nowrap="nowrap" valign="top"><strong>Tagged with:</strong></td>
							<td><div id="taggedWithDiv">
									<div style="float: left; width: 100%;"><%=innerList.get(16) %></div>
									<div style="float: left; width: 100%;"><s:select theme="simple" name="strTaggedWith" id="strTaggedWith" list="resourceList" listKey="employeeId" listValue="employeeName" 
											title="Tag resources for your document" size="3" multiple="true" value="taggedRes"/>
											<!-- <i>Hold CTRL for multiple selection</i> -->
										<input type="button" name="btnTagged" class="btn btn-primary" value="Click for Tag" onclick="addGetTaggedWith('<%=uF.showData(hmProDocumentDetails.get("FEED_ID"),"0") %>');"/>
									</div>
								</div>	
							</td>
						</tr>
						<% } %>
						
						<tr>
							<td colspan="2" valign="top"><strong>Comment:</strong>
							
								<div style="float: left; width: 100%; font-size: 12px;">
								<% if(innerList != null && !innerList.isEmpty()) { %>
							
									<div id="mainPostDiv_<%=innerList.get(0) %>">
										<div style="float: left; width: 96%; margin: 5px 0px; padding: 5px 7px; border: 1px solid #CCCCCC;">
											<% 
											String blueLikeDiv = "none";
											String grayLikeDiv = "block";
											String likeStatus = "0";
											if(innerList.get(12) != null && innerList.get(12).equals("Y")) {
												blueLikeDiv = "block";
												grayLikeDiv = "none";
												likeStatus = "1";
											} %>
											<div style="float: left; width: 100%; padding-top: 5px; margin-bottom: -4px;">
												<input type="hidden" name="likeStatus" id="likeStatus_<%=innerList.get(0) %>" value="<%=likeStatus %>"/>
												<div id="blueLikeDiv_<%=innerList.get(0) %>" style="display: <%=blueLikeDiv %>; float: left; margin-right: 5px">
													<a href="javascript:void(0)" onclick="checkLikeUnlike('<%=innerList.get(0) %>', 'P');" style="color: #007FFF;">
														<div style="float: left;"><img src="images1/icons/thumbs_up_blue.png" height="15" width="15"></div> 
														<div style="float: left; margin-left: 3px; margin-top: -2px;"> Like</div>
													</a>
												</div>
												<div id="grayLikeDiv_<%=innerList.get(0) %>" style="display: <%=grayLikeDiv %>; float: left; margin-right: 5px;">
													<a href="javascript:void(0)" onclick="checkLikeUnlike('<%=innerList.get(0) %>', 'P');" style="color: gray;">
														<div style="float: left;"><img src="images1/icons/thumbs_up_gray.png" height="15" width="15"></div> 
														<div style="float: left; margin-left: 3px; margin-top: -2px;"> Like</div>
													</a>
												</div>
											</div>
										</div>
										
										<div style="float: left; width: 96%; margin: -5px 0px 5px 0px; padding: 5px 7px; background-color: #EFEFEF; border-bottom: 1px solid #CCCCCC; border-left: 1px solid #CCCCCC; border-right: 1px solid #CCCCCC;">
											<div id="likeCountDiv_<%=innerList.get(0) %>" style="float: left; width: 100%; padding-bottom: 5px; line-height: 12px; border-bottom: 1px solid #CCCCCC;">
											<% if(uF.parseToInt(innerList.get(7)) > 0) { %>
												<a href="#" style="color:gray;" onclick="openLikesPopup('<%=innerList.get(17) %>','<%=innerList.get(18) %>')"><%=innerList.get(7) %> People like this .</a>
											<% } else { %>
												<%=innerList.get(7) %>
											<% } %>
											</div>
											<%if(hmLastCommentId !=null && uF.parseToInt(hmLastCommentId.get(innerList.get(0)+"_REMAIN_COMEENT_COUNT")) > 0) { %>
											<div id="morecommentDiv_<%=innerList.get(0) %>" style="float: left; width: 100%;">
												<input type="hidden" name="commentCnt_<%=innerList.get(0) %>" id="commentCnt_<%=innerList.get(0) %>" value="<%=(hmLastCommentId !=null && hmLastCommentId.get(innerList.get(0)+"_LAST_COMEENT_ID") != null) ? hmLastCommentId.get(innerList.get(0)+"_LAST_COMEENT_ID") : "0" %>" />
												<a href="javascript:void(0);" onclick="viewMoreComments('<%=innerList.get(0) %>')" style="font-weight: normal; color: #006699;">view <%=uF.parseToInt(hmLastCommentId.get(innerList.get(0)+"_REMAIN_COMEENT_COUNT")) %> more comments</a>
											</div>
											<% } %>
											<div id="commentDiv_<%=innerList.get(0) %>" style="float: left; width: 100%; ">
											<% 
											List<List<String>> alComments = new ArrayList<List<String>>();
											if(hmComments != null && !hmComments.isEmpty()) {
												alComments = hmComments.get(innerList.get(0));
												for(int i=0; alComments != null && !alComments.isEmpty() && i<alComments.size(); i++) {
													List<String> cInnerList = alComments.get(i);
													
													String blueLikeDiv1 = "none";
													String grayLikeDiv1 = "block";
													String likeStatus1 = "0";
													if(cInnerList.get(7) != null && cInnerList.get(7).equals("Y")) {
														blueLikeDiv1 = "block";
														grayLikeDiv1 = "none";
														likeStatus1 = "1";
													}
											%>
												<div id="comment_<%=cInnerList.get(0) %>" style="float: left; width: 100%; padding: 3px 0px;">
													<div style="float: left; margin-right: 5px; width: 7%;"><%=cInnerList.get(4) %></div>
													<div style="float: left; width: 91%; line-height: 16px;">
														<div style="float: left; width: 100%;">
															<div style="float: left; margin-right: 5px; font-weight: bold;"><%=cInnerList.get(5) %></div>
															<div style="float: left;"><%=cInnerList.get(1) %></div>
															<% if(uF.parseToInt(strEmpId) == uF.parseToInt(cInnerList.get(8))) { %>
															<div style="float: right;">
																<a href="javascript:void(0)" onclick="editYourComment('<%=cInnerList.get(0) %>', 'Y_E', '<%=innerList.get(0) %>');">
																	<i class="fa fa-pencil" aria-hidden="true"></i>
																</a>
																<a href="javascript:void(0)" onclick="editYourComment('<%=cInnerList.get(0) %>', 'Y_D', '<%=innerList.get(0) %>');">
																	<i class="fa fa-trash" aria-hidden="true"></i>
																</a>
															</div>
															<% } %>
														</div>
														<div style="float: left; width: 100%;">
															<input type="hidden" name="likeStatus" id="likeStatus_<%=cInnerList.get(0) %>" value="<%=likeStatus1 %>"/>
															<div id="blueLikeDiv_<%=cInnerList.get(0) %>" style="display: <%=blueLikeDiv1 %>; float: left; margin-right: 5px">
																<a href="javascript:void(0)" onclick="checkLikeUnlike('<%=cInnerList.get(0) %>', 'C');" style="color: #006699; font-weight: normal;">Unlike . </a>
															</div>
															<div id="grayLikeDiv_<%=cInnerList.get(0) %>" style="display: <%=grayLikeDiv1 %>; float: left; margin-right: 5px;">
																<a href="javascript:void(0)" onclick="checkLikeUnlike('<%=cInnerList.get(0) %>', 'C');" style="color: #006699; font-weight: normal;">Like . </a>
															</div>
															<div style="float: left; line-height: 15px; margin-right: 5px;"><img src="images1/icons/thumbs_up_blue_border.png" height="15" width="15"> </div>
															<div id="likeCountDiv_<%=cInnerList.get(0) %>" style="float: left; line-height: 15px; margin-right: 5px; color: #006699;">
															<% if(uF.parseToInt(cInnerList.get(2)) > 0) { %>
																<a href="#" style="color:gray;" onclick="openLikesPopup('<%=cInnerList.get(9) %>','<%=cInnerList.get(10) %>')"><%=cInnerList.get(2) %></a>
															<% } else { %>
																<%=cInnerList.get(2) %>
															<% } %>
															</div>
														</div>
													</div>
												</div>	
												<% } %>
											<% } %>
											</div>
											<div id="newCommentDiv_<%=innerList.get(0) %>" style="float: left; width: 100%; padding: 3px 0px;">
												<div style="float: left; margin-right: 5px; width: 7%;"><%=(String)request.getAttribute("MYImg") %></div>
												<div style="float: left; width: 91%;"><textarea name="strComment" id="strComment_<%=innerList.get(0) %>" rows="1" style="font-size: 12px; width: 94%" onkeydown="addComment(event, '<%=innerList.get(0) %>', 'A', '<%=innerList.get(0) %>');"></textarea></div>
											</div>
										</div>
									</div>
								<% } %>
								</div>
							</td>
						</tr>
					</table>	
				</div>
			<% } %>
				</div>
			</div>
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
            <div class="modal-body" style="height:300px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


<script>
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>
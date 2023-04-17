<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    

<%
	String strEmpId = (String)session.getAttribute(IConstants.EMPID);
	String strOrgId = (String)session.getAttribute(IConstants.ORGID);
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);


	Map<String, List<List<String>>> hmComments = (Map<String, List<List<String>>>) request.getAttribute("hmComments");
	Map<String, String> hmLastCommentId = (Map<String, String>) request.getAttribute("hmLastCommentId");
	if(hmComments == null){
		hmComments = new HashMap<String, List<List<String>>>();
	}
	 
	if(hmLastCommentId == null){
		hmLastCommentId = new HashMap<String, String>();
	}
	
	//System.out.println("hmComments==>"+hmComments.size());
	List<String> alInner = (List<String>)request.getAttribute("innerList");
	UtilityFunctions uF = new UtilityFunctions();
	if(alInner == null) alInner = new ArrayList<String>();
	if(alInner!=null && alInner.size()>0 ){
%>
<style>
  /* #imageContent { position: relative;width:100%; height:800px; background-color:black; }
  #img1 { position: absolute;top: 50%;left: 50%; } */
</style>


<script type="text/javascript">
/* $(document).ready(function() {
    $('#img1').load(function() {
        // Get the target image...
        var srcImg = $("#img1");
        var newImage = new Image();
        var container = $("#imageContent");
       // alert("container==>"+container)
        newImage.src = srcImg.attr("src");
        var imageWidth = newImage.width;
        var imageHeight = newImage.height;
        if(imageWidth > container.width()){
        	imageWidth = container.width();
        }
               
        if(imageHeight > container.height()){
        	imageHeight = container.height();
        }
        
        var marginTop = -Math.abs(imageHeight / 2);
        var marginLeft = -Math.abs(imageWidth / 2);
		 
        $("#img1").css( {"margin-top": marginTop + "px", "margin-left": marginLeft + "px" });
        $("#img1").css( {"width": imageWidth + "px", "height":  imageHeight +"px" });
       
    });
}); */

function checkLikeUnlike1(postId, type, likeCount) {
	
	var likeStatus = document.getElementById("likeStatus1_"+postId).value;
	if(likeStatus == '1') {
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
	        	url : 'LikeUnlikeAndLikeData.action?likeUnlike=UL&postId='+postId+'&type='+ type+'&likeCount='+likeCount,
	        	cache : false,
	            success : function(data) {
	            	//alert("data1==>"+data);
	            	document.getElementById("likeStatus1_"+postId).value = '0';
	        		document.getElementById("blueLikeDiv1_"+postId).style.display= 'none';
	        		document.getElementById("grayLikeDiv1_"+postId).style.display= 'inline';
	        		
	        		document.getElementById("likeCountDiv1_"+postId).innerHTML = data;
	            	
	            	document.getElementById("likeStatus_"+postId).value = '0';
	        		document.getElementById("blueLikeDiv_"+postId).style.display= 'none';
	        		document.getElementById("grayLikeDiv_"+postId).style.display= 'inline';
	            	document.getElementById("likeCountDiv_"+postId).innerHTML = data;
	            	
	            }
	        });
	    }
		
	} else {
		
		
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
	        	url : 'LikeUnlikeAndLikeData.action?likeUnlike=L&postId='+postId+'&type='+ type+'&likeCount='+likeCount,
	        	cache : false,
	            success : function(data) {
	            	//alert("data 0==>"+data);
	            	document.getElementById("likeStatus1_"+postId).value = '1';
	        		document.getElementById("blueLikeDiv1_"+postId).style.display= 'inline';
	        		document.getElementById("grayLikeDiv1_"+postId).style.display= 'none';
	        		
	            	document.getElementById("likeStatus_"+postId).value = '1';
	        		document.getElementById("blueLikeDiv_"+postId).style.display= 'inline';
	        		document.getElementById("grayLikeDiv_"+postId).style.display= 'none';
	            	
	        		document.getElementById("likeCountDiv_"+postId).innerHTML = data;
	            	document.getElementById("likeCountDiv1_"+postId).innerHTML = data;
	            	
	            }
	        });
	    }
	
	}
}

function viewMoreComments1(postId) {
	
	var lastCommentId = document.getElementById("commentCnt1_"+postId).value;
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
            url : 'EditAndDeleteComment.action?pageFrm=FP&postId='+postId+'&type=M_C&lastCommentId='+lastCommentId,
            cache : false,
            success : function(data) {
            	var divTag1 = document.getElementById("commentDiv1_"+postId).innerHTML;
				var allData = data.split("::::");
            	document.getElementById("commentDiv1_"+postId).innerHTML = allData[0]+divTag1;
            	document.getElementById("morecommentDiv1_"+postId).innerHTML = allData[1];
            }
        });
    }
}

function addComment1(event, postId, type, mainPostId) {
	
	if(type == 'C') {
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
                url : 'AddCommentOnPost.action?pageFrm=FP&postId='+postId+'&type='+type+'&mainPostId='+mainPostId,
                cache : false,
                success : function(data) {
                	var allData = data.split("::::");
                	$("comment1_"+postId).addClass("box-comment");
                    document.getElementById("comment1_"+postId).innerHTML = allData[0];
					document.getElementById("newCommentDiv1_"+mainPostId).style.display = 'block';
					$("select[multiple]").multiselect("uncheckAll");
                }
            });
	    }
	} else {
		var strComment = document.getElementById("strComment1_"+postId).value;
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
		    	var strCommentTaggedWith = getSelectedValue("strCommentTaggedWith1_"+postId);
	            var xhr = $.ajax({
	                url : 'AddCommentOnPost.action?pageFrm=FP&postId='+postId+'&strComment='+strComment+'&type='+type+'&mainPostId='+mainPostId+'&strCommentTaggedWith='+strCommentTaggedWith,
	                cache : false,
	                success : function(data) {
	                	var allData = data.split("::::");
	                	if(type == 'A') {
		                	var divTag2 = document.createElement("div");
							divTag2.id = "comment1_"+allData[2].trim();
							divTag2.innerHTML = allData[0];
							document.getElementById("commentDiv1_"+postId).appendChild(divTag2);
							document.getElementById("strComment1_"+postId).value = "";
							document.getElementById("strCommentTaggedWith1_"+postId).selectedIndex = '-1';
							
							var divTag1 = document.createElement("div");
							divTag1.id = "comment_"+allData[2].trim();
							$("#"+divTag2.id).addClass("box-comment");
							divTag1.innerHTML = allData[1];
							
							document.getElementById("commentDiv_"+postId).appendChild(divTag1);
							document.getElementById("strComment_"+postId).value = "";
							document.getElementById("strCommentTaggedWith_"+postId).selectedIndex = '-1';
						
						} else {
							$("comment1_"+postId).addClass("box-comment");
	                		document.getElementById("comment1_"+postId).innerHTML = allData[0];
	                		document.getElementById("comment_"+postId).innerHTML = allData[1];
	                		
	                	}
						document.getElementById("newCommentDiv1_"+mainPostId).style.display = 'block';
						document.getElementById("newCommentDiv_"+mainPostId).style.display = 'block';
						$("select[multiple]").multiselect("uncheckAll");
	                }
	            });
		    }
		} else if(event.ctrlKey && event.keyCode == 13) {
			var strComment = document.getElementById("strComment1_"+postId).value;
			document.getElementById("strComment1_"+postId).value = strComment + '\n';
			document.getElementById("strComment_"+postId).value = strComment + '\n';
			$("select[multiple]").multiselect("uncheckAll");
		}
	}
}


function editYourComment1(postId, type, mainPostId) {
  // alert("type==>"+type);
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
    	
    		document.getElementById("newCommentDiv1_"+mainPostId).style.display = 'none';
    		
            var xhr = $.ajax({
                url : 'EditAndDeleteComment.action?pageFrm=FP&postId='+postId+'&type='+type+'&mainPostId='+mainPostId,
                cache : false,
                success : function(data) {
                	document.getElementById("comment1_"+postId).innerHTML = data;
                	$("select[multiple='true']").multiselect().multiselectfilter();
                }
            });
        } else {
       		if(confirm("Are you sure, you want to delete this comment?")) {
       			var xhr1 = $.ajax({
	                url : 'EditAndDeleteComment.action?postId='+postId+'&type='+type+'&mainPostId='+mainPostId,
	                cache : false,
	                success : function(data) {
	                	//alert("deleting==>"postId);
	                	document.getElementById("comment1_"+postId).innerHTML = '';
	                	document.getElementById("comment1_"+postId).setAttribute("style","padding: 0px;");
	                	
	                	document.getElementById("comment_"+postId).innerHTML = '';
	                	document.getElementById("comment_"+postId).setAttribute("style","padding: 0px;");
	                	$("select[multiple]").multiselect("uncheckAll");
					}
				});
       			
       		}
		}
    	
	}
}

function openLikesPopup1(likeIds,clientLikeIds){
	var dialogEdit = '#modal-body1';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo1").show();
	 $(".modal-title1").html('Liked by...');
	 $.ajax({
		url :"FeedLikesPopup.action?likeIds="+likeIds+"&clientLikeIds="+clientLikeIds,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
function getSelectedValue1(selectId) {
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

function readImageURL1(input, targetDiv) {
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


function openCloseTaggedWith1(postId) {
	var status = document.getElementById("hideTaggedWith1_"+postId).value;
	if(status == 0) {
		document.getElementById("commentTaggedWith1_"+postId).style.display = 'block';
		document.getElementById("hideTaggedWith1_"+postId).value = "1";
	} else {
		document.getElementById("strCommentTaggedWith1_"+postId).selectedIndex = '-1';
		document.getElementById("commentTaggedWith1_"+postId).style.display = 'none';
		document.getElementById("hideTaggedWith1_"+postId).value = "0";
	}
}

$(function(){
	$("select[multiple='true']").multiselect().multiselectfilter();
});
</script>
	<div class="row row_without_margin">
		<div class="col-lg-8 col_no_padding" style="background: black;height: 450px;">
			<% if(alInner.get(3) != null && !alInner.get(3).equals("")) {
					String filePath = alInner.get(5)+"/"+alInner.get(3);
			%>
				
				<div id="imageContent" style="height: 100%;width: 100%;display: flex;justify-content: center;align-items: center;">
    				<img id ="img1" src="<%=filePath%>" data-original="<%=filePath %>" class="img-responsive" style="padding: 5px;"/> 
 				</div>
			<%			
				}else{
			%>
					<div style="float:left; padding: 60px 40px; font-size: 14px; border: 1px solid #CCCCCC;">Image Preview not available.</div>
			<%
				}
			%>
		</div>
		<div class="col-lg-4" style="height: 450px;overflow: auto;">
			<div class="user-block">
               <%=alInner.get(6)%>
               <span class="username"><%=alInner.get(7) %></span>
               <span class="description">Posted on: <%=alInner.get(8) %></span>
             </div>
				  <%
					String blueLikeDiv = "none";
					String grayLikeDiv = "block";
					String likeStatus = "0";
					
					if(alInner.get(12) != null && alInner.get(12).equalsIgnoreCase("Y")) {
						blueLikeDiv = "block";
						grayLikeDiv = "none";
						likeStatus = "1";
					} %>
					<div class="margintop10">
						<input type="hidden" name="likeStatus" id="likeStatus1_<%=alInner.get(0) %>" value="<%=likeStatus %>"/>
						<div id="blueLikeDiv1_<%=alInner.get(0) %>" style="display: <%=blueLikeDiv %>;">
							<a href="javascript:void(0)" onclick="checkLikeUnlike1('<%=alInner.get(0) %>', 'P','<%=alInner.get(11) %>');" style="color: #007FFF;">
								<button type="button" class="btn btn-default btn-xs" style="color: #007FFF;"><i class="fa fa-thumbs-o-up"></i> Like</button>
							</a>
						</div>
						<div id="grayLikeDiv1_<%=alInner.get(0) %>" style="display: <%=grayLikeDiv %>;">
							<a href="javascript:void(0)" onclick="checkLikeUnlike1('<%=alInner.get(0) %>', 'P','<%=alInner.get(11) %>');" style="color: gray;">
								<button type="button" class="btn btn-default btn-xs"><i class="fa fa-thumbs-o-up"></i> Like</button>
							</a>
						</div>
					</div>
				  
				  <div id="likeCountDiv1_<%=alInner.get(0) %>" class="clr">
					<%
					//System.out.println("alInner.get(9)==>"+alInner.get(9)+"==>alInner.get(10)==>"+alInner.get(10));
					if((alInner.get(9)!=null && !alInner.get(9).equals("") && !alInner.get(9).equals(",")) || (alInner.get(10)!=null && !alInner.get(10).equals("") && !alInner.get(10).equals(","))){ %>
						<a href="#" style="color: gray;"onclick="openLikesPopup('<%=alInner.get(9)%>','<%=alInner.get(10)%>');">  <%=alInner.get(11) %> People like this.</a>
					<%} else{%>
							<%=alInner.get(11) %> People like this.</a>
					<%} %>
					</div>
				 
				  <%
				  	 if(hmLastCommentId !=null && uF.parseToInt(hmLastCommentId.get(alInner.get(0)+"_REMAIN_COMEENT_COUNT")) > 0) { %>
						<div id="morecommentDiv1_<%=alInner.get(0) %>" style="float: left; width: 100%;background-color:#efefef;padding:5px 5px ;">
							<input type="hidden" name="commentCnt_<%=alInner.get(0) %>" id="commentCnt1_<%=alInner.get(0) %>" value="<%=(hmLastCommentId !=null && hmLastCommentId.get(alInner.get(0)+"_LAST_COMEENT_ID") != null) ? hmLastCommentId.get(alInner.get(0)+"_LAST_COMEENT_ID") : "0" %>" />
							<a href="javascript:void(0);"  onclick="viewMoreComments1('<%=alInner.get(0) %>')" style="font-weight: normal; color: #006699;">view <%=uF.parseToInt(hmLastCommentId.get(alInner.get(0)+"_REMAIN_COMEENT_COUNT")) %> more comments</a>
						</div>
						<% } %>
			<%
		  
	  			List<List<String>> alComments = new ArrayList<List<String>>();
				if(hmComments != null && !hmComments.isEmpty()) {
			%>
				  <div id="commentDiv1_<%=alInner.get(0) %>" class="box-footer box-comments margintop10" style="border-bottom: 1px solid #eee;">
				<%
					alComments = hmComments.get(alInner.get(0));
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
			 		<div class="box-comment" id ="comment1_<%=cInnerList.get(0) %>">
		                <!-- <img class="img-circle img-sm" src="../dist/img/user3-128x128.jpg" alt="User Image"> -->
		                <%=cInnerList.get(4) %>
		                <div class="comment-text">
		                      <span class="username">
		                        <%=cInnerList.get(5) %>
		                        <span class="text-muted pull-right">
		                        	<% if(uF.parseToInt(strEmpId) == uF.parseToInt(cInnerList.get(8))) { %>
										<a href="javascript:void(0)" onclick="editYourComment1('<%=cInnerList.get(0) %>', 'Y_E', '<%=alInner.get(0) %>');">
											<i class="fa fa-pencil" aria-hidden="true"></i>
										</a>
										<a href="javascript:void(0)" onclick="editYourComment1('<%=cInnerList.get(0) %>', 'Y_D', '<%=alInner.get(0) %>');">
											<i class="fa fa-trash" aria-hidden="true"></i>
										</a>
									<% } %>
		                        </span>
		                      </span>
		                  <%=cInnerList.get(1) %>
		                  <div>
                              <input type="hidden" name="likeStatus" id="likeStatus1_<%=cInnerList.get(0) %>" value="<%=likeStatus1 %>"/>
                              <div id="blueLikeDiv1_<%=cInnerList.get(0) %>"  style="display: <%=blueLikeDiv1 %>;">
                                  <a href="javascript:void(0)" onclick="checkLikeUnlike1('<%=cInnerList.get(0) %>', 'C','<%=cInnerList.get(12) %>');" style="color: #006699; font-weight: normal;"><i class="fa fa-thumbs-o-up" aria-hidden="true"></i>Unlike</a>
                              </div>
                              <div id="grayLikeDiv1_<%=cInnerList.get(0) %>" style="display: <%=grayLikeDiv1 %>;">
                                  <a href="javascript:void(0)" onclick="checkLikeUnlike1('<%=cInnerList.get(0) %>', 'C','<%=cInnerList.get(12) %>');" style="color: #006699; font-weight: normal;"><i class="fa fa-thumbs-o-up" aria-hidden="true"></i>Like</a>
                              </div>
                              <div id="likeCountDiv1_<%=cInnerList.get(0) %>" style="line-height: 15px; color: #006699;" class="inline">
                                  <%if((cInnerList.get(10)!=null && !cInnerList.get(10).equals("") && !cInnerList.get(10).equals(",")) || (cInnerList.get(11)!=null && !cInnerList.get(11).equals("") && !cInnerList.get(11).equals(","))){ %>
								<a href="#" style="color: gray;"onclick="openLikesPopup1('<%=cInnerList.get(10)%>','<%=cInnerList.get(11)%>');">  <%=cInnerList.get(12) %></a>
								<%} else{%>
									<%=cInnerList.get(12) %></a>
								<%} %>
                                    </div>
                                </div>
							<% if(cInnerList.get(9) != null && !cInnerList.get(9).equalsIgnoreCase("")) { %>
								<div style="width: 100%;">Tagged with: <%=cInnerList.get(9) %></div>
							<% } %>
		                </div>
		                
		              </div>
						<%
								}
							}
						%>	
					  </div>
					
					<div class="box-footer addCommentDiv box-comments" style="display: block;padding-top: 5px;" id="newCommentDiv1_<%=alInner.get(0) %>">
		                <!-- <img class="lazy img-circle" border="0" height="25" width="25" src="userImages/avatar_photo.png" data-original="http://192.168.1.6/Logo/People/Image/1/22x22/-1144966875blob.jpg"> -->
		                <%=(String)request.getAttribute("MYImg") %>
		                <textarea class="input-sm" placeholder="Press enter to post comment" style="width: 85% !important;" name="strComment" id="strComment1_<%=alInner.get(0) %>" rows="2" onkeydown="addComment1(event, '<%=alInner.get(0) %>', 'A', '<%=alInner.get(0) %>');"></textarea>
		                <input type="hidden" name="hideTaggedWith_<%=alInner.get(0) %>" id="hideTaggedWith1_<%=alInner.get(0) %>" value="0" />
		            	<br>
		            	<div style="margin-left: 40px; margin-right: 5px;">
                            <a href="javascript:void(0);" style="font-weight: normal;" onclick="openCloseTaggedWith1('<%=alInner.get(0) %>');">Tag</a>
                        </div>
                        <div id="commentTaggedWith1_<%=alInner.get(0) %>" style="display: none; margin-right: 5px; margin-left: 40px;">
                            <select name="strCommentTaggedWith_<%=alInner.get(0) %>" id="strCommentTaggedWith1_<%=alInner.get(0) %>" title="Tag resources for your post" style="margin-top: 10px;" size="3" multiple="true">
                                <%=(String)request.getAttribute("sbTaggedWithOption") %>
                            </select>
                        </div>
		            </div>
		</div>
	</div>
<%
	}
%>

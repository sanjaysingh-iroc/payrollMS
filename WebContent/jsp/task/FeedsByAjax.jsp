<%=(String)request.getAttribute("lastComunicationId") %>::::<%=(String)request.getAttribute("remainFeeds") %>::::
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.task.GetPriorityList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.itextpdf.text.Utilities"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ page import="java.util.*"%>


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

ul li.desgn { padding:0px ; border:solid 1px #ccc}

</style>

<script type="text/javascript">
    $(function() {
        
        //initialize the pqSelect widget.
        $("#strTaggedWith").pqSelect({
            multiplePlaceholder: 'Select',
            checkbox: true //adds checkbox to options    
        }).on("change", function(evt) {
            var val = $(this).val();
        }).pqSelect('close');
        
      //initialize the pqSelect widget.
        $("#strVisibilityWith").pqSelect({
            multiplePlaceholder: 'Select',
            checkbox: true //adds checkbox to options    
        }).on("change", function(evt) {
            var val = $(this).val();
        }).pqSelect('close');
        
    });
</script>

<%
	String strEmpId = (String)session.getAttribute(IConstants.EMPID);
	String strOrgId = (String)session.getAttribute(IConstants.ORGID);
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	
	String DOC_RETRIVE_LOCATION = (String)request.getAttribute("DOC_RETRIVE_LOCATION");
	
	UtilityFunctions uF = new UtilityFunctions();
%>


<script type="text/javascript">
(function($){
	$.fn.gdocsViewer = function(options) {
	
		var settings = {
			width  : '98%',
			height : '342'
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
					return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 342px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 342px; border: none;margin : 0 auto; display : block;"></iframe></div>';
				})
			}
		});
	};
})( jQuery );

/* return '<div id="' + gdvId + '" class="gdocsviewer"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '" width="' + settings.width + '" height="' + settings.height + '" style="border: none;margin : 0 auto; display : block;"></iframe></div>'; */
$(document).ready(function () {
    $('a.embed').gdocsViewer();
    //$('#embedURL').gdocsViewer();
});

</script>

<%
	String pageFrom = (String) request.getAttribute("pageFrom"); 
	String proId = (String) request.getAttribute("proId");
	String taskId = (String) request.getAttribute("taskId");
	String proFreqId = (String) request.getAttribute("proFreqId");
	String invoiceId = (String) request.getAttribute("invoiceId");
%>

	<%
	Map<String, List<String>> hmFeeds = (Map<String, List<String>>) request.getAttribute("hmFeeds");
	Map<String, String> hmFeedIds = (Map<String, String>)request.getAttribute("hmFeedIds");
	
	Map<String, List<List<String>>> hmComments = (Map<String, List<List<String>>>) request.getAttribute("hmComments");
	Map<String, String> hmLastCommentId = (Map<String, String>) request.getAttribute("hmLastCommentId");
	
	%>
	
		<% 
		List<String> availableExt = (List<String>)request.getAttribute("availableExt");
		if(availableExt == null) availableExt = new ArrayList<String>();
		
			Set<String> setFeeds = hmFeedIds.keySet();
			Iterator<String> it = setFeeds.iterator();
			
			while(it.hasNext()) {
				String strPostId = it.next();
				//System.out.println("strPostId ===>> " + strPostId);
				
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
		              <div class="user-block" style="width: 95%;float: left;">
		              	<% if(uF.parseToInt(innerList.get(17)) != IConstants.FT_ACTIVITY) { %> 
			                <%=innerList.get(9) %>
			                <span class="username"><%=innerList.get(10) %>
				                <% if(innerList.get(2) != null && !innerList.get(2).equals("")) { %>
                                            has posted a message on <%=innerList.get(2) %>
                                            <%=innerList.get(3) %>
                                            <% } %>
                                            <% if(innerList.get(4) != null && !innerList.get(4).equals("")) { %>
                                            with <%=innerList.get(4) %>
                                         <% } %>
			                </span>
		                 <%} %> 
		                <span class="description"><%=innerList.get(11) %></span>
		              </div>
		              <!-- /.user-block -->
		              <% if(uF.parseToInt(strEmpId) == uF.parseToInt(innerList.get(13)) && uF.parseToInt(innerList.get(17)) != IConstants.FT_ACTIVITY) { %>
							              			<div style="position: relative;margin-left: 60%;" onclick="showCustomDropdown(<%=innerList.get(0) %>)">
							              			<div class="customDropdown" >
                    										<!-- three dots -->
					                    			<ul class="dropbtn icons btn-right showLeft" style="padding-left: 0px !important;">
					                    				<i class="fa fa-ellipsis-h" aria-hidden="true"></i>
                    					    		</ul>
                    								
							              			<div id="listCustomDropdown_<%=innerList.get(0) %>" class="customDropdown-content" style="margin-top: 14%;">		
							              				<a href="javascript:void(0);" onclick="editYourPostPopup('<%=innerList.get(0) %>', 'P_E', '<%=pageFrom %>', '<%=proId %>', '<%=taskId %>', '<%=proFreqId %>', '<%=invoiceId %>');"><i class="fa fa-pencil-square-o"></i>Edit post</a>
									              		<a href="javascript:void(0);" onclick="editYourPost('<%=innerList.get(0) %>', 'P_D', '<%=pageFrom %>', '<%=proId %>', '<%=taskId %>', '<%=proFreqId %>', '<%=invoiceId %>');"><i class="fa fa-trash"></i>Delete post</a>
							              			</div>
							              			</div> 
							              			</div>
							              		 <%} %>
		              <!-- /.box-tools -->
		            </div>
		            <!-- /.box-header -->

							            <div class="box-body" style="display: block; padding-top: 2%;">
							              <% if(innerList.get(14) != null && !innerList.get(14).equals("")) {
                                            String filePath = innerList.get(16)+"/"+innerList.get(14);
                                            %>
                                       
                                            <%if(innerList.get(15)!=null && (innerList.get(15).equalsIgnoreCase("jpg") || innerList.get(15).equalsIgnoreCase("jpeg") || innerList.get(15).equalsIgnoreCase("png") || innerList.get(15).equalsIgnoreCase("bmp") || innerList.get(15).equalsIgnoreCase("gif"))){ %>
                                            	<a href="javascript:void(0);" onclick="openImagePopup('<%=innerList.get(0)%>','<%=innerList.get(1).replaceAll("\\n"," ").replaceAll("\r"," ")%>','<%=pageFrom%>')"><img class="lazy img-responsive pad" src="<%=filePath %>" data-original="<%=filePath %>" style="max-width: 100%; max-height: 250px;"></a>
                                            <% } else {
                                                if(flag) { 
                                                %>
		                                            <div id="tblDiv">
		                                                 <a href="javascript:void(0);" onclick="viewFeedsFilePopup('<%=innerList.get(0)%>');event.preventDefault();"  style="color:gray;">&nbsp;<%=innerList.get(14)%></a>
		                                            </div>
                                            <%	} else { %>
                                            <div id="tblDiv">
                                                <div style="text-align: center; font-size: 20px; padding: 150px;">Preview not available</div>
                                            </div>
                                            <%	} } }  %>
										<div id="postContent" class="postContent" >
                                        	<%=innerList.get(1) %>
                                        </div>
                                       
                                       <div id = "postLikeDiv" class="postLikeDiv">
										<span id="likeCountDiv_<%=innerList.get(0) %>">
							              <%if((innerList.get(18)!=null && !innerList.get(18).equals("") && !innerList.get(18).equals(",")) || (innerList.get(19)!=null && !innerList.get(19).equals("") && !innerList.get(19).equals(","))){ %>
                                            <a href="javascript:void(0)" style="color: gray;"onclick="openLikesPopup('<%=innerList.get(18)%>','<%=innerList.get(19)%>');">  <%=innerList.get(20) %> likes</a>
                                            <%} else{%>
                                            <%=innerList.get(20) %> likes
                                            <%} %>
							            </span>

										<hr class="postLikeHr">
										</div>
				<div id="postActions" class="postActions" >
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

 										<div id="blueLikeDiv_<%=innerList.get(0) %>" style="display: <%=blueLikeDiv %>;float:left;">
								              <input type="hidden" name="likeStatus" id="likeStatus_<%=innerList.get(0) %>" value="<%=likeStatus %>"/>
								              <a href="javascript:void(0)" onclick="checkLikeUnlike('<%=innerList.get(0) %>', 'P','<%=innerList.get(20) %>');">
								              	<button type="button" class="btn btn-default btn-xs"  style="color: #007FFF;"><i class="fa fa-thumbs-o-up"></i> Like</button>
								              </a>
							            </div>
							            <div id="grayLikeDiv_<%=innerList.get(0) %>" style="display: <%=grayLikeDiv %>;float:left;">
                                                <a href="javascript:void(0)" onclick="checkLikeUnlike('<%=innerList.get(0) %>', 'P','<%=innerList.get(20) %>');">
                                                    <button type="button" class="btn btn-default btn-xs"><i class="fa fa-thumbs-o-up"></i> Like</button>
                                                </a>
                                        </div>
                                        
                                        <div id="actionComment_<%=innerList.get(0) %>" style="display: block;float:left;padding-left: 0.5%;">
                                                <a href="javascript:void(0)" onclick="enableCommenting('<%=innerList.get(0) %>');">
                                                    <button type="button" class="btn btn-default btn-xs"><i class="fa fa-commenting-o"></i> Comment</button>
                                                </a>
                                        </div>
                                        
                                        
 				</div>
                                        </div>
		            
							            <div class="box-footer addCommentDiv" style="display: none;padding-top: unset;" id="newCommentDiv_<%=innerList.get(0) %>">
											<div id="commentUserImg" style="float: left;">
							                	<%=(String)request.getAttribute("MYImg") %>
											</div>
							                <textarea  class="form-control" placeholder="Press enter to post comment" style="width: 90% !important;vertical-align: top;margin-left: 35px;height:40px;border-radius: 3rem" name="strComment" id="strComment_<%=innerList.get(0) %>" 
											onkeydown="addComment(event, '<%=innerList.get(0) %>', 'A', '<%=innerList.get(0) %>');"></textarea>
							                <input type="hidden" name="hideTaggedWith_<%=innerList.get(0) %>" id="hideTaggedWith_<%=innerList.get(0) %>" value="0" />
							            	<br/>
							            	<div style="margin-left: 40px; margin-right: 5px;">
                                                <a href="javascript:void(0);" style="font-weight: normal;" onclick="openCloseTaggedWith('<%=innerList.get(0) %>');">Tag</a>
                                            </div>
                                            <div id="commentTaggedWith_<%=innerList.get(0) %>" style="display: none; margin-right: 5px;margin-left: 40px;">
                                                <select name="strCommentTaggedWith_<%=innerList.get(0) %>" id="strCommentTaggedWith_<%=innerList.get(0) %>" title="Tag resources for your post" style="margin-top: 10px;" size="3" multiple="true">
                                                    <%=(String)request.getAttribute("sbTaggedWithOption") %>
                                                </select>
                                            </div>
							            </div>
		            	<% 
                                           List<List<String>> alComments = new ArrayList<List<String>>();
                                           if(hmComments != null && !hmComments.isEmpty()) { 
                                           	alComments = hmComments.get(innerList.get(0));
                                           %>
                                           <div class="box-footer box-comments clr" style="display: <% if(alComments != null && !alComments.isEmpty()) { %>block; <% } else { %>none; <% } %>" id="commentDiv_<%=innerList.get(0) %>">
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
					                      <span class="username">
					                        <%=cInnerList.get(5) %>
					                        <span class="text-muted pull-right">
					                        	<% if(uF.parseToInt(strEmpId) == uF.parseToInt(cInnerList.get(8))) { %>
                                                        	<div style="position: relative;margin-right: -4%;" onclick="showCommentCustomDropdown(<%=innerList.get(0) %>)">
							              						<div class="customDropdown" >
                    												<!-- three dots -->
					                    								<ul class="dropbtn icons btn-right showLeft" style="padding-left: 0px !important;">
					                    									<i class="fa fa-ellipsis-h" aria-hidden="true"></i>
                    					    							</ul>
                    								
							              						<div id="listCommentCustomDropdown_<%=innerList.get(0) %>" class="customDropdown-content" style="margin-top: 14%;">		
							              							<a href="javascript:void(0);" onclick="editYourComment('<%=cInnerList.get(0) %>', 'Y_E', '<%=innerList.get(0) %>');"><i class="fa fa-pencil-square-o"></i>Edit post</a>
									              					<a href="javascript:void(0);" onclick="editYourComment('<%=cInnerList.get(0) %>', 'Y_D', '<%=innerList.get(0) %>');"><i class="fa fa-trash"></i>Delete post</a>
							              						</div>
							              						</div> 
							              						</div>
                                                   <% } %>
					                        </span>
					                      </span><!-- /.username -->
					                  <%=cInnerList.get(1) %>
					                  <div>
                                                     <input type="hidden" name="likeStatus" id="likeStatus_<%=cInnerList.get(0) %>" value="<%=likeStatus1 %>"/>
		                                                        <div id="blueLikeDiv_<%=cInnerList.get(0) %>" style="display: <%=blueLikeDiv1 %>;">
		                                                         <a href="javascript:void(0)" onclick="checkLikeUnlike('<%=cInnerList.get(0) %>', 'C','<%=cInnerList.get(12) %>');">
								              						<button type="button" class="btn btn-default btn-xs"  style="color: #007FFF;"><i class="fa fa-thumbs-o-up"></i>UnLike</button>
								              					 </a>
		                                                        </div>
		                                                        <div id="grayLikeDiv_<%=cInnerList.get(0) %>" style="display: <%=grayLikeDiv1 %>;">
		                                                         <a href="javascript:void(0)" onclick="checkLikeUnlike('<%=cInnerList.get(0) %>', 'C','<%=cInnerList.get(12) %>');">
								              						<button type="button" class="btn btn-default btn-xs"  style="color: #007FFF;"><i class="fa fa-thumbs-o-up"></i> Like</button>
								              					 </a>
		                                                        </div>
                                                     <div id="likeCountDiv_<%=cInnerList.get(0) %>" style="line-height: 15px; color: #006699;" class="inline">
                                                         <%if((cInnerList.get(10)!=null && !cInnerList.get(10).equals("") && !cInnerList.get(10).equals(",")) || (cInnerList.get(11)!=null && !cInnerList.get(11).equals("") && !cInnerList.get(11).equals(","))){ %>
                                                         <a href="#" style="color: gray;"onclick="openLikesPopup('<%=cInnerList.get(10)%>','<%=cInnerList.get(11)%>');">  <%=cInnerList.get(12) %></a>
                                                         <%} else{%>
                                                         <%=cInnerList.get(12) %>
                                                         <%} %>
                                                     </div>
                                                 </div>	
                                                 <% if(cInnerList.get(9) != null && !cInnerList.get(9).equalsIgnoreCase("")) { %>
                                                 	<div style="width: 100%;">Tagged with: <%=cInnerList.get(9) %></div>
                                                 <% } %>
					                </div>
					             </div>
                                           <% } %>
                                           <%if(hmLastCommentId !=null && uF.parseToInt(hmLastCommentId.get(innerList.get(0)+"_REMAIN_COMEENT_COUNT")) > 0) { %>
                                     <div id="morecommentDiv_<%=innerList.get(0) %>">
                                         <input type="hidden" name="commentCnt_<%=innerList.get(0) %>" id="commentCnt_<%=innerList.get(0) %>" value="<%=(hmLastCommentId !=null && hmLastCommentId.get(innerList.get(0)+"_LAST_COMEENT_ID") != null) ? hmLastCommentId.get(innerList.get(0)+"_LAST_COMEENT_ID") : "0" %>" />
                                         <a href="javascript:void(0);"  onclick="viewMoreComments('<%=innerList.get(0) %>')" style="font-weight: normal; color: #006699;">view <%=uF.parseToInt(hmLastCommentId.get(innerList.get(0)+"_REMAIN_COMEENT_COUNT")) %> more comments</a>
                                     </div>
                                     <% } %>
                                       </div>
                                    <% } %>
		          </div>
			<% } %>
		<% } %>
	
			
	
<%-- <script>
alert("before img.lazy");
	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
alert("after img.lazy");
</script> --%>

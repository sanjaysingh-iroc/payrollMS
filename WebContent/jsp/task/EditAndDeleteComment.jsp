<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<% 
    String pageFrm = (String) request.getAttribute("pageFrm");
    String type = (String) request.getAttribute("type");
    UtilityFunctions uF = new UtilityFunctions();
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    
    //	System.out.println("pageFrm ===>> " + pageFrm);
    
    if(type != null && type.equals("Y_E")) {
    String mainPostId = (String) request.getAttribute("mainPostId");
    List<String> innerList = (List<String>) request.getAttribute("innerList");
    //System.out.println("innerList ===>> " + innerList);
    if(innerList != null && !innerList.isEmpty()) {
    	String tagDisplay = "none";
    	if(innerList.get(6) != null && !innerList.get(6).equals("")) {
    		tagDisplay = "inline;";
    	}
    %>
<%if(pageFrm != null && pageFrm.equals("FP")){ %>

<%=innerList.get(3) %>
<div class="comment-text">
    <span class="username">
	    <span class="text-muted pull-right">
		    <a href="javascript:void(0);" onclick="addComment1(event, '<%=innerList.get(0) %>', 'C', '<%=mainPostId %>');">
		   		 <!-- <img src="images1/icons/icons/close_button_icon.png" style="width: 20px !important;height: 20px !important;"> -->
		   		 <i class="fa fa-times-circle cross" aria-hidden="true" style="margin-left: -45%;width: 20px !important;height: 20px !important;" onclick="addComment1(event, '<%=innerList.get(0) %>', 'C', '<%=mainPostId %>');"></i>
		    </a> 
	    </span>
    </span>
    <textarea name="strComment" class="form-control" id="strComment1_<%=innerList.get(0) %>" style="width: 90% !important;vertical-align: top;margin-left: 15px;height:40px;border-radius: 3rem; overflow: hidden; resize: none;" 
     onkeydown="addComment1(event, '<%=innerList.get(0) %>', 'E', '<%=mainPostId %>');"><%=innerList.get(1) %></textarea>
    <div>
        <input type="hidden" name="hideTaggedWith_<%=innerList.get(0) %>" id="hideTaggedWith1_<%=innerList.get(0) %>" value="0" />
        <div style="margin-right: 5px;">
            <a href="javascript:void(0);" style="font-weight: normal;" onclick="openCloseTaggedWith1('<%=innerList.get(0) %>');">Tag</a>
        </div>
        <div id="commentTaggedWith1_<%=innerList.get(0) %>" style="display: <%=tagDisplay %>; margin-right: 5px;">
            <select name="strCommentTaggedWith_<%=innerList.get(0) %>" id="strCommentTaggedWith1_<%=innerList.get(0) %>" title="Tag resources for your post" size="3" multiple="true">
            	<%=(String)request.getAttribute("sbTaggedWithOption") %>
            </select>
            <script>
                $(function(){
                	$("#strCommentTaggedWith1_<%=innerList.get(0) %>").multiselect().multiselectfilter();
                });
            </script>
        </div>
    </div>
</div>
<%}else{ %>
<%=innerList.get(3) %>
<div class="comment-text">
    <span class="username">
    
    <span class="text-muted pull-right">
    <a href="javascript:void(0);" onclick="addComment(event, '<%=innerList.get(0) %>', 'C', '<%=mainPostId %>');">
    <!-- <img src="images1/icons/icons/close_button_icon.png" style="width: 20px !important;height: 20px !important;"> -->
    <i class="fa fa-times-circle cross" aria-hidden="true" style="margin-left: -45%;width: 20px !important;height: 20px !important;" ></i>
    </a> 
    </span>
    </span>
    <textarea name="strComment" class="form-control" id="strComment_<%=innerList.get(0) %>" style="width: 90% !important;vertical-align: top;margin-left: 15px;height:40px;border-radius: 3rem; overflow: hidden; resize: none;" 
    onkeydown="addComment(event, '<%=innerList.get(0) %>', 'E', '<%=mainPostId %>');"><%=innerList.get(1) %></textarea>
    <div>
        <input type="hidden" name="hideTaggedWith_<%=innerList.get(0) %>" id="hideTaggedWith_<%=innerList.get(0) %>" value="0" />
        <div style="margin-right: 5px;">
            <a href="javascript:void(0);" style="font-weight: normal;" onclick="openCloseTaggedWith('<%=innerList.get(0) %>');">Tag</a>
        </div>
        <div id="commentTaggedWith_<%=innerList.get(0) %>" style="display: <%=tagDisplay %>; margin-right: 5px;">
            <select name="strCommentTaggedWith_<%=innerList.get(0) %>" id="strCommentTaggedWith_<%=innerList.get(0) %>" title="Tag resources for your post" style="width: 80px;" size="3" multiple="true">
            <%=(String)request.getAttribute("sbTaggedWithOption") %>
            </select>
            <script>
                $(function(){
                	$("#strCommentTaggedWith_<%=innerList.get(0) %>").multiselect().multiselectfilter();
                });
            </script>
        </div>
    </div>
</div>
<% } %>
<% } %>
<% } else if(type != null && type.equals("M_C")) { %>
<% 
    String strLastCommentId = (String) request.getAttribute("strLastCommentId");
    String remainCommentCnt = (String) request.getAttribute("remainCommentCnt");
    String postId = (String) request.getAttribute("postId");
    List<List<String>> alComments = (List<List<String>>) request.getAttribute("alComments");
    /*	for(int i=0; alComments != null && !alComments.isEmpty() && i<alComments.size(); i++) {
    	List<String> cInnerList = alComments.get(i);
    	
    	String blueLikeDiv1 = "none";
    	String grayLikeDiv1 = "block";
    	String likeStatus1 = "0";
    	if(cInnerList.get(7) != null && cInnerList.get(7).equals("Y")) {
    		blueLikeDiv1 = "block";
    		grayLikeDiv1 = "none";
    		likeStatus1 = "1";
    	}*/
    %>
<% if(pageFrm != null && pageFrm.equals("FP")){
    for(int i=0; alComments != null && !alComments.isEmpty() && i<alComments.size(); i++) {
    List<String> cInnerList = alComments.get(i);
    
    String blueLikeDiv1 = "none";
    String grayLikeDiv1 = "inline";
    String likeStatus1 = "0";
    if(cInnerList.get(7) != null && cInnerList.get(7).equals("Y")) {
    	blueLikeDiv1 = "inline";
    	grayLikeDiv1 = "none";
    	likeStatus1 = "1";
    }
    %>
<div class="box-comment" id="comment1_<%=cInnerList.get(0) %>">
    <%=cInnerList.get(4) %>
    <div class="comment-text">
        <span class="username">
        <%=cInnerList.get(5) %>
        <span class="text-muted pull-right">
        <% if(uF.parseToInt(strEmpId) == uF.parseToInt(cInnerList.get(8))) { %>
        <a href="javascript:void(0)" onclick="editYourComment1('<%=cInnerList.get(0) %>', 'Y_E', '<%=postId %>');">
        <i class="fa fa-pencil" aria-hidden="true"></i>
        </a>
        <a href="javascript:void(0)" onclick="editYourComment1('<%=cInnerList.get(0) %>', 'Y_D', '<%=postId %>');">
        <i class="fa fa-trash" aria-hidden="true"></i> 
        </a>
        <% } %>
        </span>
        </span><!-- /.username -->
        <%=cInnerList.get(1) %>
        <div>
            <input type="hidden" name="likeStatus" id="likeStatus1_<%=cInnerList.get(0) %>" value="<%=likeStatus1 %>"/>
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
            <div id="likeCountDiv1_<%=cInnerList.get(0) %>" style="line-height: 15px; color: #006699;" class="inline"><%=cInnerList.get(2) %>
            </div>
        </div>
    </div>
</div>
<% } %>
::::
<input type="hidden" name="commentCnt_<%=postId %>" id="commentCnt1_<%=postId %>" value="<%=strLastCommentId !=null ? strLastCommentId : "0" %>" />
<% if(uF.parseToInt(remainCommentCnt)>0) { %>
<a href="javascript:void(0);" onclick="viewMoreComments1('<%=postId %>')" style="font-weight: normal; color: #006699;">view <%=uF.parseToInt(remainCommentCnt) %> more comments</a>
<% } %>
<% }else{
    for(int i=0; alComments != null && !alComments.isEmpty() && i<alComments.size(); i++) {
    	List<String> cInnerList = alComments.get(i);
    	
    	String blueLikeDiv1 = "none";
    	String grayLikeDiv1 = "inline";
    	String likeStatus1 = "0";
    	if(cInnerList.get(7) != null && cInnerList.get(7).equals("Y")) {
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
        <a href="javascript:void(0)" onclick="editYourComment('<%=cInnerList.get(0) %>', 'Y_E', '<%=postId %>');">
        <i class="fa fa-pencil" aria-hidden="true"></i>
        </a>
        <a href="javascript:void(0)" onclick="editYourComment('<%=cInnerList.get(0) %>', 'Y_D', '<%=postId %>');">
        <i class="fa fa-trash" aria-hidden="true"></i> 
        </a>
        <% } %>
        </span>
        </span>
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
                <%=cInnerList.get(2) %>
            </div>
        </div>
    </div>
</div>
<% } %>
::::
<input type="hidden" name="commentCnt_<%=postId %>" id="commentCnt_<%=postId %>" value="<%=strLastCommentId !=null ? strLastCommentId : "0" %>" />
<% if(uF.parseToInt(remainCommentCnt)>0) { %>
<a href="javascript:void(0);" onclick="viewMoreComments('<%=postId %>')" style="font-weight: normal; color: #006699;">view <%=uF.parseToInt(remainCommentCnt) %> more comments</a>
<% } %>
<% } %>
<% } %>
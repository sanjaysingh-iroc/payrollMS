
<%@page import="java.util.List"%>


<%
String pageFrm = (String)request.getAttribute("pageFrm");

String mainPostId = (String) request.getAttribute("mainPostId");
	List<String> innerList = (List<String>) request.getAttribute("innerList");
	//System.out.println("innerList ===>> " + innerList);
	if(innerList != null && !innerList.isEmpty()) {
		
		String blueLikeDiv = "none";
		String grayLikeDiv = "inline";
		String likeStatus = "0";
		if(innerList.get(7) != null && innerList.get(7).equals("Y")) {
			blueLikeDiv = "inline";
			grayLikeDiv = "none";
			likeStatus = "1";
		} 
%>
<%if(pageFrm != null && pageFrm.equals("FP")){ %>
	      <%=innerList.get(4) %>
	      <div class="comment-text">
	            <span class="username">
	              <%=innerList.get(5) %> 
	              <span class="text-muted pull-right">
                      <a href="javascript:void(0)" onclick="editYourComment1('<%=innerList.get(0) %>', 'Y_E', '<%=mainPostId %>');">
                      <i class="fa fa-pencil" aria-hidden="true"></i>
                      </a>
                      <a href="javascript:void(0)" onclick="editYourComment1('<%=innerList.get(0) %>', 'Y_D', '<%=mainPostId %>');">
                      <i class="fa fa-trash" aria-hidden="true"></i> 
                      </a>
	              </span>
	            </span>
	        <%=innerList.get(1) %>
	        <div>
                <input type="hidden" name="likeStatus" id="likeStatus1_<%=innerList.get(0) %>" value="<%=likeStatus %>"/>
                
                <div id="blueLikeDiv_<%=innerList.get(0) %>" style="display: <%=blueLikeDiv %>;">
		          <a href="javascript:void(0)" onclick="checkLikeUnlike1('<%=innerList.get(0) %>', 'C');">
						<button type="button" class="btn btn-default btn-xs"  style="color: #007FFF;"><i class="fa fa-thumbs-o-up"></i>UnLike</button>
				  </a>
		        </div>
		        <div id="grayLikeDiv_<%=innerList.get(0) %>" style="display: <%=grayLikeDiv %>;">
		          <a href="javascript:void(0)" onclick="checkLikeUnlike1('<%=innerList.get(0) %>', 'C');">
						<button type="button" class="btn btn-default btn-xs"  style="color: #007FFF;"><i class="fa fa-thumbs-o-up"></i> Like</button>
				 </a>
		        </div>
                
                <div id="likeCountDiv1_<%=innerList.get(0) %>" style="line-height: 15px; color: #006699;" class="inline">
                    <%=innerList.get(2) %>
                </div>
            </div>	
            <% if(innerList.get(9) != null && !innerList.get(9).equalsIgnoreCase("")) { %>
            	<div style="width: 100%;">Tagged with: <%=innerList.get(9) %></div>
            <% } %>
	      </div>
		::::
	<% } %>
	<%=innerList.get(4) %>
	      <div class="comment-text">
	            <span class="username">
	              <%=innerList.get(5) %> 
	              <span class="text-muted pull-right">
                      <div style="position: relative;margin-right: -4%;" onclick="showCommentCustomDropdown(<%=innerList.get(0) %>)">
							<div class="customDropdown" >
                    			<!-- three dots -->
					            <ul class="dropbtn icons btn-right showLeft" style="padding-left: 0px !important;">
					            	<i class="fa fa-ellipsis-h" aria-hidden="true"></i>
                    			</ul>
                    								
							    <div id="listCommentCustomDropdown_<%=innerList.get(0) %>" class="customDropdown-content" style="margin-top: 14%;">		
							    	<a href="javascript:void(0);" onclick="editYourComment('<%=innerList.get(0) %>', 'Y_E', '<%=mainPostId %>');"><i class="fa fa-pencil-square-o"></i>Edit post</a>
									<a href="javascript:void(0);" onclick="editYourComment('<%=innerList.get(0) %>', 'Y_D', '<%=mainPostId %>');"><i class="fa fa-trash"></i>Delete post</a>
							    </div>
							</div> 
					</div>
	              </span>
	            </span>
	        <%=innerList.get(1) %>
	        <div>
                <input type="hidden" name="likeStatus" id="likeStatus_<%=innerList.get(0) %>" value="<%=likeStatus %>"/>
                <div id="blueLikeDiv_<%=innerList.get(0) %>" style="display: <%=blueLikeDiv %>;">
		          <a href="javascript:void(0)" onclick="checkLikeUnlike('<%=innerList.get(0) %>', 'C');">
						<button type="button" class="btn btn-default btn-xs"  style="color: #007FFF;"><i class="fa fa-thumbs-o-up"></i>UnLike</button>
				  </a>
		        </div>
		        <div id="grayLikeDiv_<%=innerList.get(0) %>" style="display: <%=grayLikeDiv %>;">
		          <a href="javascript:void(0)" onclick="checkLikeUnlike('<%=innerList.get(0) %>', 'C');">
						<button type="button" class="btn btn-default btn-xs"  style="color: #007FFF;"><i class="fa fa-thumbs-o-up"></i> Like</button>
				 </a>
		        </div>
                <div id="likeCountDiv_<%=innerList.get(0) %>" style="line-height: 15px; color: #006699;" class="inline">
                    <%=innerList.get(2) %>
                </div>
            </div>	
            <% if(innerList.get(9) != null && !innerList.get(9).equalsIgnoreCase("")) { %>
            	<div style="width: 100%;">Tagged with: <%=innerList.get(9) %></div>
            <% } %>
	      </div>
	::::
	<%=innerList.get(0) %>
<% } %>	
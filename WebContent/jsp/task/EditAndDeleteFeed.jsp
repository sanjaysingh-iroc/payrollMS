
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>
	$(function(){
		$("select[multiple='multiple']").multiselect().multiselectfilter();
	});
	
	$('#postAttachment1').bind("click" , function () {
        $('#strFeedDoc1').click();
    });
	
	function readImageURL1(input, targetDiv) {
		debugger;
		//alert("notice targetDiv==>"+targetDiv);
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
	
</script>
<%
	String type = (String) request.getAttribute("type");
	String postId = (String) request.getAttribute("postId");
	
	String pageFrom = (String) request.getAttribute("pageFrom");
	String proId = (String) request.getAttribute("proId");
	String taskId = (String) request.getAttribute("taskId");
	String proFreqId = (String) request.getAttribute("proFreqId");
	String invoiceId = (String) request.getAttribute("invoiceId");
	
	UtilityFunctions uF = new UtilityFunctions();
	String strEmpId = (String)session.getAttribute(IConstants.EMPID);
	
	String feedImagePath = (String) request.getAttribute("feedImagePath");
	String docOrImage = (String) request.getAttribute("docOrImage");
	
	String extenstion = (String) request.getAttribute("extenstion");
	List<String> availableExt = (List<String>)request.getAttribute("availableExt");
	if(availableExt == null) availableExt = new ArrayList<String>();
	boolean flag = false;
	if(availableExt.contains(extenstion)) {
		flag = true;
	}
	
	if(type != null && type.equals("P_E")) {
%>
	
	<div>
		<s:form name="frm_editDeleteFeed" id="frm_editDeleteFeed" action="EditAndDeleteFeed" theme="simple" enctype="multipart/form-data">
			<s:hidden name="type" id="type" value="U"/>
			<s:hidden name="postId" id="postId"/>
			<s:hidden name="proId" id="proId"></s:hidden>
			<s:hidden name="pageFrom" id="pageFrom"></s:hidden>
			<s:hidden name="taskId" id="taskId"></s:hidden>
			<s:hidden name="proFreqId" id="proFreqId"></s:hidden>
			<s:hidden name="invoiceId" id="invoiceId"></s:hidden>
			
				
				<div id="editPostHeader" style="padding-top: 2%;">
                       <div style="float: left;padding-right: 2%;padding-top: 1%;"><%=(String)request.getAttribute("MYLargeImg") %></div>
                       <div>
                           <textarea rows="4" name="strCommunication" id="strCommunication_<%=postId %>" style="padding-left: 2%;border-radius: 2rem;width: 80% !important;resize: none;"><%=uF.showData((String)request.getAttribute("strCommunication"), "") %></textarea>
                       </div>
                </div>
				
		
			<div id="attachmentDiv" class="attachmentDiv">
						<%
							String filePath = feedImagePath+"/"+docOrImage;
							if(docOrImage!=null && !docOrImage.equals("")){
							if(extenstion!=null && (extenstion.equalsIgnoreCase("jpg") || extenstion.equalsIgnoreCase("jpeg") || extenstion.equalsIgnoreCase("png") || extenstion.equalsIgnoreCase("bmp") || extenstion.equalsIgnoreCase("gif"))){ 
						%>	
								<a href="javascript:void(0);" onclick="openImagePopup('<%=postId%>','<%=uF.showData((String)request.getAttribute("strCommunication"), "") %>','<%=pageFrom%>')">
								<img class="lazy img-circle borderradius0" name="feedDocOrImg1" id="feedDocOrImg1" src="<%=filePath %>" data-original="<%=filePath %>" style="width: 242px; height: 110px;"></a>
						<% } else { 
									if(flag && filePath!=null && !filePath.equals("")){
							%>
										<div id="tblDiv">
											<%-- <a href="<%=filePath%>" class="embed" id="test" style="color:gray;">&nbsp;<%=docOrImage%></a> --%>
											<p style="color:gray;">&nbsp;<%=docOrImage%></p>
										</div>
										<img height="0" width="0" class="lazy img-circle borderradius0" name="feedDocOrImg1" id="feedDocOrImg1" style="border: 1px solid #CCCCCC;" src="" data-original="" /> 
								<%			
									}else{
								%>
										<div style="border: 1px solid #CCCCCC;">Image Preview not available.</div>		
								<% }
							  }
						} %>
				</div>
				
				<div id="postOptions" style="margin-top: 5%; padding-left: 5%; width: 100% !important;float: left;">
					
					<div id="alignWith" style="width: 125px !important;float: left;">
						<div class="tdHeader"> Align with</div>
					
						<% if(pageFrom!=null && (pageFrom.trim().equalsIgnoreCase("Task") || pageFrom.trim().equalsIgnoreCase("Timesheet") || pageFrom.trim().equalsIgnoreCase("Invoice"))) { %>
						
						<% } else { %>
								<select name="strAlignWith" id="strAlignWith_<%=postId %>" style="width: 100% !important;"
								title="Select aligned type for your post" onchange="getAlignTypeData(this.value, '<%=postId %>')">
									<%if(pageFrom==null || !pageFrom.trim().equalsIgnoreCase("Project")) { %>
										<option value="">Not aligned</option>
									<% } else { %>
										<option value="">Project</option>
									<% } %>
									<%=(String)request.getAttribute("sbAlignTypeOption") %>
								</select>
							
							<div id="alignDataDiv_<%=postId %>" style="margin-top: 10px;">
							<% 
								String strAlignWith = (String) request.getAttribute("strAlignWith");
								if(uF.parseToInt(strAlignWith) == IConstants.PROJECT) {
									if(pageFrom==null || !pageFrom.trim().equalsIgnoreCase("Project")) {
							%>
								<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" style="width: 95% !important;"
									title="Select align data for your post">
								
									<!-- <option value="">Select</option> -->
									<%=(String)request.getAttribute("sbProjectsOption") %>
								</select>
								<% } else { %>
								<input type="hidden" name="strAlignWithIds" style="width: 95% !important;" id="strAlignWithIds_<%=postId %>" />
								<% } %>
							<% } else if(uF.parseToInt(strAlignWith) == IConstants.TASK) { %>
								<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" style="width: 95% !important;font-size: 10px;" 
								title="Select align data for your post">
									<!-- <option value="">Select</option> -->
									<%=(String)request.getAttribute("sbTaskOption") %>
								</select>
							<% } else if(uF.parseToInt(strAlignWith) == IConstants.PRO_TIMESHEET) { %>
								<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" style="width: 95% !important;font-size: 10px;" 
								title="Select align data for your post">
									<!-- <option value="">Select</option> -->
									<%=(String)request.getAttribute("sbProTimesheetOption") %>
								</select>
							<% } else if(uF.parseToInt(strAlignWith) == IConstants.DOCUMENT) { %>
								<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" style="width: 95% !important;font-size: 10px;" 
								title="Select align data for your post">
									<!-- <option value="">Select</option> -->
									<%=(String)request.getAttribute("sbDocumentsOption") %>
								</select>
							<% } else if(uF.parseToInt(strAlignWith) == IConstants.INVOICE) { %>
								<select name="strAlignWithIds" id="strAlignWithIds_<%=postId %>" style="width: 95% !important;font-size: 10px;" 
								title="Select align data for your post">
									<!-- <option value="">Select</option> -->
									<%=(String)request.getAttribute("sbProInvoiceOption") %>
								</select>
							<% } %>
							</div>
						<% } %>
					</div>
				
					<div id="tagWith" style="margin-left: 5%;float: left;">
					<div class="tdHeader">Tag </div>
							<select name="strTaggedWith" id="strTaggedWith_<%=postId %>" 
							title="Tag resources for your post" multiple="multiple">
								<%=(String)request.getAttribute("sbTaggedWithOption") %>
							</select>
					</div>
				
					<div id="shareWith" style="width: 125px !important;float: left;margin-left: 5%;">
					<div class="tdHeader"> Share </div>
						<% 
							String visibility = (String) request.getAttribute("strVisibility");
							String strDisplay = "none";
							if(uF.parseToInt(visibility) == IConstants.S_RESOURCE) {
								strDisplay = "block";
							}
						%>
							<select name="strVisibility" id="strVisibility_<%=postId %>"
							style="width: 100% !important;" 
							onchange="checkVisibility(this.value, '<%=postId %>')">
								<%=(String)request.getAttribute("sbVisibilityOption") %>
							</select>
							<div id="visibilityWithDiv_<%=postId %>" style="display: <%=strDisplay %>;">
							<select name="strVisibilityWith" id="strVisibilityWith_<%=postId %>" title="Visibility for your post" multiple="multiple">
								<option value="">Select</option>
								<%=(String)request.getAttribute("sbVisibilityWithOption") %>
							</select>
						</div>
						</div>
				
				</div>
				
				<div id="editPostActions" style="padding-top: 22% !important;margin-left: 78%;">
					<input type="file" style="display:none" id="strFeedDoc1" name="strFeedDoc" onchange="readImageURL1(this, 'feedDocOrImg1');">
						<div style="float: left;padding-right: 10%;">
                              <div id="postAttachment1"><i class="fa fa-paperclip fa-lg" title="Add attachment" aria-hidden="true"></i></div>
                        </div>
						<input type="submit" name="submit" class="btn btn-primary" value="Update"/>
					</div>
				
		</s:form>
	</div>
	
<% } else if(type != null && (type.equals("U") || type.equals("C"))) { %>

	<% 
		List<String> innerList = (List<String>)request.getAttribute("innerList");
		if(innerList != null && !innerList.isEmpty()) {
	%>
		<div style="float: left; width: 100%; margin-bottom: 5px;">
			<div style="float: left; margin-right: 5px; width: 7%;"><%=innerList.get(9) %></div> 
			<div style="float: left; width: 91%; line-height: 16px">
				<div style="float: left; width: 100%;">
					<div style="float: left; margin-right: 5px; font-weight: bold;"><%=innerList.get(10) %></div>
					<% if(innerList.get(2) != null && !innerList.get(2).equals("")) { %>
						<div style="float: left; margin-right: 5px; color: gray;">has posted a message on <%=innerList.get(2) %></div>
						<div style="float: left; margin-right: 5px;"><%=innerList.get(3) %></div>
					<% } %>
					<% if(innerList.get(4) != null && !innerList.get(4).equals("")) { %>
						<div style="float: left; margin-right: 5px; color: gray;"> with</div>
						<div style="float: left; margin-right: 5px;"><%=innerList.get(4) %></div>
					<% } %>
				</div>
				<div style="float: left; width: 100%;">
					<div style="float: left; margin-right: 5px; color: gray;"><%=innerList.get(11) %>.</div>
					<div style="float: left; margin-right: 5px; margin-top: 3px;"><img src="images1/icons/globe.png" height="10" width="10" title="Shared with: <%=innerList.get(5) %>"> </div>
					
					<% if(uF.parseToInt(strEmpId) == uF.parseToInt(innerList.get(13))) { %>
					<div style="float: right;">
						<a href="javascript:void(0);" onclick="editYourPost('<%=innerList.get(0) %>', 'P_E', '<%=pageFrom %>', '<%=proId %>', '<%=taskId %>', '<%=proFreqId %>', '<%=invoiceId %>');">
							<img src="images1/icons/pen.png" height="12" width="12">
						</a>
						<a href="javascript:void(0);" onclick="editYourPost('<%=innerList.get(0) %>', 'P_D', '<%=pageFrom %>', '<%=proId %>', '<%=taskId %>', '<%=proFreqId %>', '<%=invoiceId %>');">
							<img src="images1/icons/delete_gray.png" height="12" width="12">
						</a>
					</div>
					<% } %>
				</div>
			</div>
		</div>
		<div style="float: left; width: 100%; padding-bottom: 10px; border-bottom: 1px solid #CCCCCC;"><%=innerList.get(1) %></div>
	<% } %>
<% } %>
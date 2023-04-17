
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script src="scripts/ckeditor_cust/ckeditor.js"></script> --%>
<script type="text/javascript">

	$ (function () {
		$("#formEditAppraisalSectionAndSubsection_submit").click(function(){
			$(".validateRequired").prop('required',true);
			$(".validateNumber").prop('type','number');
			$(".validateNumber").prop('step','any');	 		
		});
	}); 
	
</script>


<%
UtilityFunctions uF = new UtilityFunctions();
List<String> sectionList =(List<String>)request.getAttribute("sectionList");
List<String> subSectionList =(List<String>)request.getAttribute("subSectionList");
String fromPage =(String)request.getAttribute("fromPage");
String id =(String)request.getAttribute("id");
String appFreqId =(String)request.getAttribute("appFreqId");
String type =(String)request.getAttribute("type");
Map<String, Map<String, String>> hmOrientPosition =(Map<String, Map<String, String>>)request.getAttribute("hmOrientPosition");
%>
<div style="border: 1px; border-color : black;">
	<s:form action="EditMyReviewSectionAndSubsection" id="formEditAppraisalSectionAndSubsection" method="POST" theme="simple">
		<div id="appraisalHeadingDiv" style="width:100%;float:left;margin-top:10px;">
			<s:hidden name="id"></s:hidden>
			<s:hidden name="type"></s:hidden>
			<s:hidden name="SID"></s:hidden>
			<s:hidden name="sectionID"></s:hidden>
			<s:hidden name="appFreqId"></s:hidden>
			<s:hidden name="fromPage"></s:hidden>	
			<%if(request.getAttribute("type").equals("section")) {
				if(sectionList != null) {
						Map<String, String> orientPosition = hmOrientPosition.get(sectionList.get(0));	
					%>
					<table class="table table_no_border" width="98%"> 
						<tr>
							<th align="right" style="padding-right:20px" width="20%"><%=request.getAttribute("sNO") %>)&nbsp;Section Title<sup>*</sup></th>
							<td colspan="5">
							<input type="text" name="levelTitle" id="levelTitle" class="validateRequired" style="width:80%" value="<%=uF.showData(sectionList.get(1),"") %>"/>
							</td>
						</tr>
						<tr>
							<th align="right" style="padding-right:20px">Short Description</th>
							<td colspan="5">
							<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="shortDesrciption"><%=uF.showData(sectionList.get(2),"") %></textarea>  <!-- id="editorOne" -->
							<%-- <input type="text" name="shortDesrciption" id="shortDesrciption" style="width:80%" value="<%=uF.showData(sectionList.get(2),"") %>"/> --%>
							</td> 
						</tr>
						<tr>
							<th align="right" style="padding-right:20px" valign="top">Long Description</th>
							<td colspan="5">
							<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="longDesrciption"><%=uF.showData(sectionList.get(3),"") %></textarea>  <!-- id="editorTwo" -->
							<%-- <textarea rows="4" cols="10" name="longDesrciption" id="longDesrciption" style="width:80%"><%=uF.showData(sectionList.get(3),"") %></textarea> --%>
							</td>
						</tr>
						<tr>
							<th align="right" style="padding-right:20px">Weightage %<sup>*</sup></th>
							<td>
								<input type="text" name="sectionWeightage"	id="sectionWeightage" class="validateRequired" onkeypress="return isNumberKey(event)"  value="<%=uF.showData(sectionList.get(6),"") %>" onkeyup="validateScoreEdit(this.value,'sectionWeightage','hidesectionWeightage','<%=request.getAttribute("totWeightage") %>');"/>
								<input type="hidden" name="hidesectionWeightage" id="hidesectionWeightage" value="<%=uF.showData(sectionList.get(6),"") %>" />
							</td>
						</tr>
							<tr><th align="right" style="padding-right:20px">Select Attribute</th>
							<td><select name="attribute" id="attribute" class="validateRequired form-control autoWidth "><option value="<%=uF.showData(sectionList.get(5),"") %>"><%=uF.showData(sectionList.get(7),"") %></option></select></td>
						</tr>
					
						<tr>
							<th align="right" style="padding-right:20px">Work Flow</th>
							<td colspan="5">
								<%
									String member1=(String)request.getAttribute("member");
									String[] memberArray1=member1.split(",");
									//System.out.println("member ==== > "+member);
									for(int i=0;i<memberArray1.length;i++){
								%>
									<span style="float: left; width: 60px; text-align: center;">Step<%=i+1%></span>
								<%} %>
							</td>
						</tr>
					
					<%
						String member=(String)request.getAttribute("member");
						String[] memberArray=member.split(",");
						//System.out.println("member ==== > "+member);
						for(int i=0;i<memberArray.length;i++) {
					%>			
							<tr>
								<th align="right" style="padding-right:20px"><%=memberArray[i]%></th>
								<td colspan="5">
									<%for(int j=1; j<=memberArray.length; j++) { %>
										<span style="float: left; width: 60px; text-align: center;">
											<input type="radio" name="<%=memberArray[i]%>" value="<%=j %>" <%if(orientPosition != null && uF.parseToInt(orientPosition.get(memberArray[i]))==j) { %> checked="checked"<% }else if(j==1) { %>checked="checked"<% } %>/>
										</span>
									<% } %>
								</td>
							</tr>
						<%}%>
					</table>
					<script>
					 $(function(){
						 CKEDITOR.replace( 'editorOne', {
								on: {
									focus: onFocus,
									blur: onBlur,

									// Check for availability of corresponding plugins.
									pluginsLoaded: function( evt ) {
										var doc = CKEDITOR.document, ed = evt.editor;
										if ( !ed.getCommand( 'bold' ) )
											doc.getById( 'exec-bold' ).hide();
										if ( !ed.getCommand( 'link' ) )
											doc.getById( 'exec-link' ).hide();
									}
								}
							});
							
							
							// Replace the <textarea id="editor2"> with an CKEditor instance.
							CKEDITOR.replace( 'editorTwo', {
								on: {
									focus: onFocus,
									blur: onBlur,

									// Check for availability of corresponding plugins.
									pluginsLoaded: function( evt ) {
										var doc = CKEDITOR.document, ed = evt.editor;
										if ( !ed.getCommand( 'bold' ) )
											doc.getById( 'exec-bold' ).hide();
										if ( !ed.getCommand( 'link' ) )
											doc.getById( 'exec-link' ).hide();
									}
								}
							}); 
						 
					 });
					</script>
				<%}
			}else { 
				if(subSectionList != null) {
				%>
					<div id="sectiondiv" style="width:100%;float:left;margin-top:30px;">
						<p style="padding-left: 5px; text-align: left; font-size: 12px; font-weight: bold; margin-bottom: 10px;"><%=request.getAttribute("sNO")%> Subsection of " <%=request.getAttribute("sectionName")%> " </p>
						<table class="table" width="100%">
							<tr id="sectionnameTr">
								<th align="right" style="padding-right:20px" width="25%"><%=request.getAttribute("sNO") %>)&nbsp;Subsection Title<sup>*</sup></th>
								<td colspan="5">
									<input type="text" name="subsectionname" id="subsectionname" class="validateRequired" style="width: 450px;" value="<%=uF.showData(subSectionList.get(1),"") %>"/>
								</td>
							</tr>
							<tr id="sectionDescTr">
								<th align="right" style="padding-right:20px">Subsection Short Description</th>
								<td colspan="5">
									<textarea rows="3" cols="72" name="subsectionDescription"><%=uF.showData(subSectionList.get(2),"") %></textarea>  <!-- id="editorThree" -->
								</td>
							</tr>
							<tr id="sectionLongDescTr">
								<th align="right" style="padding-right:20px">Subsection Long Description</th>
								<td colspan="5">
									<textarea rows="3" cols="72" name="subsectionLongDescription"><%=uF.showData(subSectionList.get(3),"") %></textarea>  <!-- id="editorFour" -->
								</td>
							</tr>
							<tr>
								<th align="right" style="padding-right:20px">Weightage %<sup>*</sup></th>
								<td>
									<input type="text" name="subSectionWeightage"	id="subSectionWeightage" class="validateRequired validateNumber" value="<%=uF.showData(subSectionList.get(6),"") %>" onkeyup="validateScoreEdit(this.value,'subSectionWeightage','hidesubSectionWeightage','<%=request.getAttribute("totWeightage") %>');" onkeypress="return isNumberKey(event)"/>
									<input type="hidden" name="hidesubSectionWeightage" id="hidesubSectionWeightage" value="<%=uF.showData(subSectionList.get(6),"") %>"/>
								</td>
							</tr>
					  </table>	
				 </div> 
				 <script>
					 $(function(){
							
							// Replace the <textarea id="editor3"> with an CKEditor instance.
							CKEDITOR.replace( 'editorThree', {
								on: {
									focus: onFocus,
									blur: onBlur,

									// Check for availability of corresponding plugins.
									pluginsLoaded: function( evt ) {
										var doc = CKEDITOR.document, ed = evt.editor;
										if ( !ed.getCommand( 'bold' ) )
											doc.getById( 'exec-bold' ).hide();
										if ( !ed.getCommand( 'link' ) )
											doc.getById( 'exec-link' ).hide();
									}
								}
							});
							
							// Replace the <textarea id="editor2"> with an CKEditor instance.
							CKEDITOR.replace( 'editorFour', {
								on: {
									focus: onFocus,
									blur: onBlur,

									// Check for availability of corresponding plugins.
									pluginsLoaded: function( evt ) {
										var doc = CKEDITOR.document, ed = evt.editor;
										if ( !ed.getCommand( 'bold' ) )
											doc.getById( 'exec-bold' ).hide();
										if ( !ed.getCommand( 'link' ) )
											doc.getById( 'exec-link' ).hide();
									}
								}
							});
					 });
					</script>
				<%
				  } 
			  } %>
		 <div align="center">
		 	<%if(fromPage != null && fromPage.equals("SRR")) { %>
		 		<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="updateMyReviewSection('<%=id%>','<%=appFreqId%>','<%=fromPage%>')">
		 	<%} else { %>
				<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
			<% } %>
			<!-- <input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closeEditPopup();"/> -->
		 </div> 
	  </div>
  </s:form>
</div>

<g:compress>
<script>
function updateMyReviewSection(appId,appFreqId,fromPage){
//	alert("updateMyReview jsp ==appId==>"+appId+"==>appFreqId==>"+appFreqId);
	var form_data = $("#formEditAppraisalSectionAndSubsection").serialize();
	$.ajax({ 
		type : 'POST',
	//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
		url: "EditMyReviewSectionAndSubsection.action",
		data: form_data+"&submit=Save",
		cache: true,
		success: function(result){
			getMyReviewSummary('MyReviewSummary',appId,appFreqId,fromPage);
   		},
		error: function(result){
			getMyReviewSummary('MyReviewSummary', appId, appFreqId, fromPage);
		}
	});
}
	
	

			
			
	</script>
</g:compress>


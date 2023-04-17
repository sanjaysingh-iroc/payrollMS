
<%@page import="com.konnect.jpms.performance.FillAttribute"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<script type="text/javascript">
	$(function(){
		$(".submitButton").click(function(){
			$('#formEditAppraisalSectionAndSubsection').find('.validateRequired').filter(':hidden').prop('required',false);
			$('#formEditAppraisalSectionAndSubsection').find('.validateRequired').filter(':visible').prop('required',true);
		});
	});
</script>
<%
UtilityFunctions uF = new UtilityFunctions();

List<String> sectionList =(List<String>)request.getAttribute("sectionList");
List<String> subSectionList =(List<String>)request.getAttribute("subSectionList");
Map<String, Map<String, String>> hmOrientPosition =(Map<String, Map<String, String>>)request.getAttribute("hmOrientPosition");
if(sectionList == null) sectionList = new ArrayList<String>();
if(subSectionList == null) subSectionList = new ArrayList<String>();
if(hmOrientPosition == null) hmOrientPosition = new HashMap<String, Map<String, String>>();
String appFreqId = (String)request.getAttribute("appFreqId");
String id = (String)request.getAttribute("id");
String fromPage = (String)request.getAttribute("fromPage");
%>
<div style="border: 1px; border-color : black;">
	<s:form action="EditAppraisalSectionAndSubsection" id="formEditAppraisalSectionAndSubsection" method="POST" theme="simple">
		<div id="appraisalHeadingDiv" style="margin-top:10px;">
			<s:hidden name="id"></s:hidden>
			<s:hidden name="type"></s:hidden>
			<s:hidden name="SID"></s:hidden>
			<s:hidden name="sectionID"></s:hidden>
			<s:hidden name="appFreqId"></s:hidden>
			<%if(request.getAttribute("type").equals("section")){
		 		 if(sectionList != null){
					Map<String, String> orientPosition = hmOrientPosition.get(sectionList.get(0));	
					%>
					<table class="table table_no_border">
						<tr>
							<th align="right" style="padding-right:20px" width="20%"><%=request.getAttribute("sNO") %>)&nbsp;Section Title<sup>*</sup></th>
							<td colspan="5">
								<input type="text" name="levelTitle" id="levelTitle" class="validateRequired form-control " style="width:80%" value="<%=uF.showData(sectionList.get(1),"") %>"/>
							</td>
						</tr>
						<tr>
							<th align="right" style="padding-right:20px">Short Description</th>
							<td colspan="5">
								<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="shortDesrciption"><%=uF.showData(sectionList.get(2),"") %></textarea>  <!-- id="editor1" -->
							</td> 
						</tr>
						<tr>
							<th align="right" style="padding-right:20px" valign="top">Long Description</th>
							<td colspan="5">
								<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="longDesrciption"><%=uF.showData(sectionList.get(3),"") %></textarea>  <!-- id="editor2" -->
							</td>
						</tr>
						<tr>
							<th align="right" style="padding-right:20px">Weightage %<sup>*</sup></th>
							<td>
								<input type="number" name="sectionWeightage"	id="sectionWeightage" class="validateRequired form-control "  value="<%=uF.showData(sectionList.get(6),"0") %>" onkeyup="validateScoreEdit(this.value,'sectionWeightage','hidesectionWeightage','<%=request.getAttribute("totWeightage") %>');" onkeypress="return isNumberKey(event)"/>
								<input type="hidden" name="hidesectionWeightage" id="hidesectionWeightage" value="<%=uF.showData(sectionList.get(6),"0") %>" />
							</td>
						</tr>
						<tr><th align="right" style="padding-right:20px">Select Attribute</th>
							<td>
								<select name="attribute" id="attribute" class="validateRequired form-control  ">
								<%
									List<FillAttribute> attributeList = (List<FillAttribute>) request.getAttribute("attributeList");
									for (int i = 0; i < attributeList.size(); i++) {
										FillAttribute fillAttribute = attributeList.get(i);
										/* sb.append("<option value=\"" + fillAttribute.getId() + "\">"
												+ fillAttribute.getName() + "</option>"); */
										if(fillAttribute.getId().equals(sectionList.get(5))){
											%>
											<option value="<%=fillAttribute.getId() %>" selected="selected"><%=fillAttribute.getName() %></option>
											<%
										}else{
											%>
											<option value="<%=fillAttribute.getId() %>"><%=fillAttribute.getName() %></option>
											<%
										}
									}
									%>
									<%-- <option value="<%=uF.showData(sectionList.get(5),"") %>"><%=uF.showData(sectionList.get(7),"") %></option> --%>
									</select>
							  </td>
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
								<% } %>
							</td>
						</tr>
								
				<%
						String member=(String)request.getAttribute("member");
						String[] memberArray=member.split(",");
						//System.out.println("member ==== > "+member);
						for(int i=0;i<memberArray.length;i++) {
							//System.out.println("memberArray[i] ==== > "+memberArray[i]);
							//System.out.println("orientPosition.get(memberArray[i]) ==== > "+orientPosition.get(memberArray[i]));
				%>			
							<tr>
								<th align="right" style="padding-right:20px"><%=memberArray[i]%></th>
								<td colspan="5">
									<%for(int j=1; j<=memberArray.length; j++) { %> 
										<span style="float: left; width: 60px; text-align: center;">
											<input type="radio" name="<%=memberArray[i]%>" value="<%=j %>" <%if(orientPosition != null && uF.parseToInt(orientPosition.get(memberArray[i]))==j) { %> checked="checked"<%}else if(j==1){ %>checked="checked"<%} %>/>
										</span>
									<% } %>
								</td>
							</tr>
						<% } %>
					</table>
				<% }
				} else {
				if(subSectionList != null) {
			%>
			    	<div id="sectiondiv">
						<p style="padding-left: 5px; text-align: left; font-size: 16px; font-weight: 600; margin-bottom: 10px;"><%=request.getAttribute("sNO")%> Subsection of " <%=request.getAttribute("sectionName")%> " </p>
						<table class="table table_no_border">
							<tr id="sectionnameTr">
									<th align="right" style="padding-right:20px" width="25%"><%=request.getAttribute("sNO") %>)&nbsp;Subsection Title<sup>*</sup></th>
									<td colspan="5">
									<input type="text" name="subsectionname" id="subsectionname" class="validateRequired form-control " style="width: 450px;" value="<%=uF.showData(subSectionList.get(1),"") %>"/>
									</td>
								</tr>
								<tr id="sectionDescTr">
									<th align="right" style="padding-right:20px">Subsection Short Description</th>
									<td colspan="5">
									<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="subsectionDescription"><%=uF.showData(subSectionList.get(2),"") %></textarea>  <!-- id="editor333" -->
									<%-- <input type="text" name="subsectionDescription" id="subsectionDescription" style="width: 450px;" value="<%=uF.showData(subSectionList.get(2),"") %>"/> --%>
									</td>
								</tr>
								<tr id="sectionLongDescTr">
									<th align="right" style="padding-right:20px">Subsection Long Description</th>
									<td colspan="5">
									<textarea rows="3" cols="72" style="width: 100% !important; resize: none;" name="subsectionLongDescription"><%=uF.showData(subSectionList.get(3),"") %></textarea>  <!-- id="editor444" -->
									<%-- <input type="text" name="subsectionDescription" id="subsectionDescription" style="width: 450px;" value="<%=uF.showData(subSectionList.get(2),"") %>"/> --%>
									</td>
								</tr>
								<tr>
									<th align="right" style="padding-right:20px">Weightage %<sup>*</sup></th>
									<td>
										<input type="number" name="subSectionWeightage"	id="subSectionWeightage" class="validateRequired form-control " value="<%=uF.showData(subSectionList.get(6),"0") %>" onkeyup="validateScoreEdit(this.value,'subSectionWeightage','hidesubSectionWeightage','<%=request.getAttribute("totWeightage") %>');" onkeypress="return isNumberKey(event)"/>
										<input type="hidden" name="hidesubSectionWeightage" id="hidesubSectionWeightage" value="<%=uF.showData(subSectionList.get(6),"0") %>"/>
									</td>
								</tr>
						</table>	
					</div> 
				<%} 
			} %>
			<div align="center">
				<%if(fromPage != null && fromPage.equals("AD")) { %>
            		<input type="submit" value="Save" class="btn btn-primary submitButton"  name="submit"/>
            	<% } else { %>
					<s:submit value="Save" type="submit" cssClass="btn btn-primary submitButton"  name="submit"></s:submit>
				<%} %>
			</div> 
	    </div>
	</s:form>
</div>

<g:compress>
<script>

<% if(request.getAttribute("type").equals("section")){ %>
			// Replace the <textarea id="editor1"> with an CKEditor instance.
			if(document.getElementById("editor1")) {
				CKEDITOR.replace( 'editor1', {
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
			}
			
			// Replace the <textarea id="editor2"> with an CKEditor instance.
			if(document.getElementById("editor2")) {
				CKEDITOR.replace( 'editor2', {
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
			}
			<% } else { %>
			// Replace the <textarea id="editor3"> with an CKEditor instance.
			if(document.getElementById("editor333")) {
				CKEDITOR.replace( 'editor333', {
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
			}
			// Replace the <textarea id="editor2"> with an CKEditor instance.
			if(document.getElementById("editor444")) {
				CKEDITOR.replace( 'editor444', {
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
			}
		<%}%>	
		
		$("#formEditAppraisalSectionAndSubsection").submit(function(event){
			event.preventDefault();
			for ( instance in CKEDITOR.instances ) {
		        CKEDITOR.instances[instance].updateElement();
		    }
			var form_data = $("#formEditAppraisalSectionAndSubsection").serialize();
			$.ajax({ 
				type : 'POST',
			//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
				url: "EditAppraisalSectionAndSubsection.action",
				data: form_data+"&submit=Save",
				success: function(result){
					getReviewSummary('AppraisalSummary','<%=id%>','<%=appFreqId %>','<%=fromPage %>');
		   		},
				error: function(result){
					getReviewSummary('AppraisalSummary','<%=id%>','<%=appFreqId %>','<%=fromPage %>');
				}
			});
			
		}); 

		</script>

</g:compress>
<div id="SelectQueDiv"></div>
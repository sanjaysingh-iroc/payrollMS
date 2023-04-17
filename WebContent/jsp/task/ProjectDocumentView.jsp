<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%
List alProDocuments = (List)request.getAttribute("alProDocuments");
List alTaskDocuments = (List)request.getAttribute("alTaskDocuments");
String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
%>

<script type="text/javascript">
function openCloseDocs(folderCnt) {
	var status = document.getElementById("hideFolder"+folderCnt).value;
	if(status == '0') {
		document.getElementById("hideFolder"+folderCnt).value = '1';
		document.getElementById("folderFileTR_"+folderCnt).style.display = "table-row";
	} else {
		document.getElementById("hideFolder"+folderCnt).value = '0';
		document.getElementById("folderFileTR_"+folderCnt).style.display = "none";
	}
}
</script>

<div class="pagetitle_sub" style="margin-left: 60px; margin-top: 15px;">Project Documents</div>

	<div style="float: left; font-size: 12px;" id="tblDiv">

		<table class="table_style" style="margin-left: 60px; width: 100%;">
			<!-- <tr>
				<td class="txtlabel alignRight" valign="top">Documents:</td>
				<td><a class="add_lvl" href="javascript:void(0);" onclick="addNewFolder(), showTblHeader();">Add Folder</a> <a
					class="add_lvl" href="javascript:void(0);" onclick="addNewDocs(), showTblHeader();">Add Document</a></td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="table_style" id="documentTable" style="width: 100%;">
						<tr id="folderTR0" style="display: none;">
							<td style="width: 50%;"><input type="hidden" name="folderDocscount" id="folderDocscount" value="0" />
								Folder/Documents</td>
							<td style="width: 16%;">Alignment</td>
							<td style="width: 34%;">Sharing</td>
						</tr>

					</table></td>
			</tr>
 -->
			<tr>
				<td colspan="2">
					<div style="width: 100%;">
						<div id="proDocs"></div>
						<div id="proFolders"></div>
					</div> 
					<% 	Map<String, List<List<String>>> hmFolderDocs = (Map<String, List<List<String>>>)request.getAttribute("hmFolderDocs");
						Map<String, List<String>> hmFolderDocsData = (Map<String, List<String>>)request.getAttribute("hmFolderDocsData");
						String proDocMainPath = (String) request.getAttribute("proDocMainPath");
						String proDocRetrivePath = (String) request.getAttribute("proDocRetrivePath");
						String strOrgId = (String) request.getAttribute("strOrgId");
						if(hmFolderDocs != null) {
					%>
					<div
						style="float: left; min-width: 650px; width: 100%; border: 1px solid gray;">

						

							<% if(hmFolderDocsData != null && hmFolderDocsData.size() > 0) { %>
							<!-- <li style="margin-left: 0px; float: left; width: 98%;"> -->
								<table class="" id="proFolderTBL" style="width: 100%;">
									<tr>
										<td colspan="2" style="width: 35%;">Name</td>
										<td style="width: 10%;">Alignment</td>
										<td style="width: 15%;">Sharing</td>
										<td style="width: 8%;">Size</td>
										<td style="width: 15%;">Type</td>
										<td style="width: 10%;">Date</td>
										<!-- <td style="width: 10%;">&nbsp;</td> -->
									</tr>
					<% 	
						int folderCnt = 0;
						Iterator<String> it = hmFolderDocsData.keySet().iterator();
						while(it.hasNext()) {
							String proFolderId = it.next();
							List<String> innerListFolder = hmFolderDocsData.get(proFolderId);
							List<List<String>> listDocs = hmFolderDocs.get(proFolderId);
							String fileCount ="-";
							
							if(listDocs != null && listDocs.size() > 0) {
								fileCount = listDocs.size()+" items";
							}
					%>


					<% 
				//System.out.println("innerListFolder.get(7) ===>> " +innerListFolder.get(7));
					if(innerListFolder != null && innerListFolder.get(7) != null && innerListFolder.get(7).equals("folder")) { %>
							<tr id="folderTR_<%=folderCnt %>">
								<td style="width: 4%;"><input type="hidden"
									name="hideFolder<%=folderCnt %>"
									id="hideFolder<%=folderCnt %>" value="0" /> <span
									style="float: left;"> <a href="javascript:void(0);"
										style="font-weight: normal; color: black;"
										onclick="openCloseDocs('<%=folderCnt %>');"> 
										<%-- <img
											height="15" width="20"
											src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> --%>
											<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i>
									</a> </span>
								</td>
								<td nowrap="nowrap"><%=innerListFolder.get(1) %></td>
								<td><%=innerListFolder.get(11) %></td>
								<td><%=innerListFolder.get(12) %></td>
								<td><%=fileCount %></td>
								<td><%="folder" %></td>
								<td><%=innerListFolder.get(6) %></td>
								<%-- <td><a href="javascript:void(0)" class="del" style="float: right;" title="Delete Folder" onclick="deleteProjectDocs('F','folder','folderTR','<%=folderCnt %>','','<%=innerListFolder.get(1) %>','<%=innerListFolder.get(0) %>','<%=URLEncoder.encode(proDocMainPath+"/"+strOrgId+"/Projects/"+innerListFolder.get(3)+"/"+innerListFolder.get(1), "UTF-8") %>');"> - </a> 
									<a href="javascript:void(0)" class="edit_lvl" style="float: right;" onclick="editFolder('<%=innerListFolder.get(8)%>','<%=innerListFolder.get(3)%>','<%=innerListFolder.get(1) %>','<%=innerListFolder.get(0) %>');" title="Edit Folder">Edit</a>
								</td> --%>
							</tr>
				<% 	} else {
					String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+innerListFolder.get(3)+"/"+innerListFolder.get(1);
				%>
							<tr id="docTR_<%=folderCnt %>">
								<td colspan="2"><a href="<%=filePath1 %>" style="font-weight: normal; color: black;">
									<img height="15" width="15" src="<%=request.getContextPath()%>/images1/icons/icons/file_icon.png" />
									&nbsp;<%=innerListFolder.get(1) %></a>
								</td>
								<td nowrap="nowrap"><%=innerListFolder.get(11) %></td>
								<td nowrap="nowrap"><%=innerListFolder.get(12) %></td>
								<td nowrap="nowrap"><%=innerListFolder.get(9) %></td>
								<td><%=innerListFolder.get(10) %></td>
								<td><%=innerListFolder.get(6) %></td>
								<%-- <td><a href="javascript:void(0)" class="del" style="float: right;" title="Delete File" onclick="deleteProjectDocs('D','file','docTR','<%=folderCnt %>','','<%=innerListFolder.get(1) %>','<%=innerListFolder.get(0) %>','<%=URLEncoder.encode(proDocMainPath+"/"+strOrgId+"/Projects/"+innerListFolder.get(3)+"/"+innerListFolder.get(1), "UTF-8") %>');">-</a></td> --%>
							</tr>
					<% } %>

					<%
						if(innerListFolder != null && innerListFolder.get(7) != null && innerListFolder.get(7).equals("folder")) {
					%>
								<tr id="folderFileTR_<%=folderCnt %>" style="display: none;">
									<td colspan="8">
										<table id="folderFileTBL_<%=folderCnt %>" style="width: 100%;">
					<%
						for(int i=0; listDocs!=null && i<listDocs.size(); i++) {
							List<String> innerList = listDocs.get(i);
							String filePath11 = proDocRetrivePath+strOrgId+"/Projects/"+innerListFolder.get(3)+"/"+innerListFolder.get(1)+"/"+innerList.get(1);
						%>
								<tr id="fileTR_<%=folderCnt %>_<%=i %>">
									<td style="width: 3%;">&nbsp;</td>
									<td style="width: 32%;"><a href="<%=filePath11 %>" style="font-weight: normal; color: black;"> 
										<img height="15" width="15" src="<%=request.getContextPath()%>/images1/icons/icons/file_icon.png" />
										<%=innerList.get(1) %></a>
									</td>
									<td style="width: 10%;"><%=innerList.get(10) %></td>
									<td style="width: 15%;"><%=innerList.get(11) %></td>
									<td nowrap="nowrap" style="width: 8%;"><%=innerList.get(8) %></td>
									<td style="width: 15%;"><%=innerList.get(9) %></td>
									<td style="width: 10%;"><%=innerList.get(5) %></td>
									<%-- <td style="width: 10%;"><a href="javascript:void(0)" class="del" style="float: right;" title="Delete File" onclick="deleteProjectDocs('FD','file','fileTR','<%=folderCnt %>','<%=i %>','<%=innerListFolder.get(1) %>','<%=innerList.get(0) %>','<%=URLEncoder.encode(proDocMainPath+"/"+strOrgId+"/Projects/"+innerListFolder.get(3)+"/"+innerListFolder.get(1)+"/"+innerList.get(1), "UTF-8") %>');">- </a></td> --%>
								</tr>
								<% } %>
							</table></td>
					</tr>
					<% } %>
					<% 	folderCnt++;
					} %>
						</table>
					<% } else { %>
						<ul class="level_list">
							<li style="margin-left: 0px;">
								<div class="nodata msg">
									<span>No documents attached.</span>
								</div></li>
							</ul>	
							<% } %>

					</div> <% } %>
				</td>
			</tr>

		</table>
	</div>

<%-- <ul>

<%
int i=0;
for(i=0; alProDocuments!=null && i<alProDocuments.size(); i+=2){
	
	%> 
	<li>
	<div id="myDivP_<%=i%>" style="margin-left:30px;float:left;width:94%">
		<%=alProDocuments.get(i+1) %> 
		
		<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {%>
		<a style="float:right" href="javascript:void(0)" onclick="getContent('myDivP_<%=i%>', 'DeleteDocument.action?doc_id=<%=alProDocuments.get(i+0) %>')" class="del"></a>
		<%} %>
	</div>
	<div class="clr"></div>
	</li>
	
	<%
}

if(i==0){
	%>
	<li><div class="msg nodata" style="width:96%"><span>No project document attached.</span></div></li>
	<%
}

%>

</ul>


<div class="pagetitle_sub" style="margin: 0px">Task Documents</div>

<ul>

<%
int j=0;
for(j=0; alTaskDocuments!=null && j<alTaskDocuments.size(); j+=2){
	
	%>
	<li>
	<div id="myDivT_<%=j%>" style="margin-left:30px;float:left;width:94%">
		<%=alTaskDocuments.get(j+1) %> 
		<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {%>
		<a style="float:right" href="javascript:void(0)" onclick="getContent('myDivT_<%=j%>', 'DeleteDocument.action?doc_id=<%=alTaskDocuments.get(j+0) %>')" class="del"></a>
		<%} %>
	</div>
	<div class="clr"></div>
	</li>
	<%
}
if(j==0){
	%>
	<li><div class="msg nodata" style="width:96%"><span>No task document attached.</span></div></li>
	<%
}

%>

</ul> --%>
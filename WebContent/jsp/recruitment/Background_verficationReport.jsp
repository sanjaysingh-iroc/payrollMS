<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<% 
    List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
//	System.out.println("reportList::"+reportList);
	Map<String,List<String>> hmDocumentsDetails = (Map<String,List<String>>) request.getAttribute("hmDocumentsDetails");
//	System.out.println("hmDocumentsDetails::"+hmDocumentsDetails);
%>

<script type="text/javascript"> 
 function downloadDocument(candidateID,documentName) {
	 window.location = "BackgroundDocumentsPreview.action?candidateId="+candidateID+"&documentName="+documentName;
}
</script>


<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                   
                      
                        <div style="margin-top:30px;"></div>
                        <table id="lt" class="table table-bordered" style="width:100%;">
                            <thead>
                                <tr>
                                   	
                                     <th style="text-align: left;">Candidate Name</th>
                                     <th style="text-align: left;">Joining Date</th>
                                    <th style="text-align: left;">JD</th>
                                    <th style="text-align: left;">Job Title</th>
                                    <th style="text-align: left;">Background Verfication Documents</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    for (int i = 0;i<reportList.size(); i++) {
                                    List<String> innerList = (List<String>) reportList.get(i);
//                                    System.out.println("innerList:::"+innerList);
                                    %>
                                <tr>
                                    <td><%=innerList.get(0)%></td>
                                    <td><%=innerList.get(1)%></td>
                                    <%if(innerList.get(2)!=null){%>
                                    	<td><%=innerList.get(2)%></td>
                                    	<%}else{%>
                                    		<td></td>
                                    	<%}if(innerList.get(3)!=null){%>
                                    	<td><%=innerList.get(3)%></td>
                                    	<%}else{%>
                                    		<td></td>
                                    	<%}%>
                                    	<td>
                                    	<%if(hmDocumentsDetails.containsKey(innerList.get(4))){
                                    		List<String> alDocumentslist = hmDocumentsDetails.get(innerList.get(4));
                                    		for(int j =0;j<alDocumentslist.size();j++){%>
                                    			<div id="downloadDocuments_<%=innerList.get(4) +"_"+ alDocumentslist.get(j) %>" style="float: left;width:97%;margin:2px 0px 0px 0px;">
                                                    <a href="javascript:void(0);" onclick="downloadDocument('<%=innerList.get(4)%>', '<%=alDocumentslist.get(j)%>');"><%=alDocumentslist.get(j)%></a>
                                                 </div>
                                    	<% 	}
                                    		}
                                    	%>
                                    	</td>
                                    	
                                    	
                                      </tr>
                                <% } %>
                                <% if (reportList.size() == 0 || reportList == null) { %>
                                <tr>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td colspan="12">
                                        <div class="nodata msg">
                                            <span>No application within selection.</span>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                        <br /> <br />
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>

<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%UtilityFunctions uF = new UtilityFunctions(); %>	   

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
					<%session.removeAttribute(IConstants.MESSAGE);%>
					
					<div style="float:left; width:100%; margin-top:10px;">
						<%
						if(((String)session.getAttribute("sbMessage"))!=null) {
							out.println(session.getAttribute("sbMessage"));	
						} 
						session.removeAttribute("sbMessage");
						%>
					</div>
                    <div class="leftbox reportWidth row row_without_margin">
                        <!-- =====================================Format 11(Eagle Burgmann)=================================== -->
                        <div class="col-lg-6 col-md-12 col-sm-12">
	                        <div style="border: 1px solid rgb(204, 204, 204);">
                            	<p class="past">Quick bulk upload your employees Roster (FormatCode -A001)</p>
	                            <div style="text-align: center; padding: 10px;">
	                                <s:form theme="simple" name="UploadRoster" id="UploadRoster" method="POST" action="UploadRoster" enctype="multipart/form-data">    
	                                    <table style="width: 100%;" class="table table_no_border">
	                                        <tbody>
	                                            <tr>
	                                                <td class="txtlabel alignRight">Select File to Import</td>
	                                                <td>
	                                                    <s:file name="fileUpload" label="Select a File to upload" size="20" />
	                                                </td>
	                                            </tr>
	                                            <tr>
	                                                <td align="center" colspan="2"><input type="submit" class="btn btn-primary" value="Import File" id="ImportEmployees_0"></td>
	                                            </tr>
	                                            <tr>
	                                                <td align="right" colspan="2">
	                                                <a title="Download Sample File" href="import/ImportRoster.xlsx" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
	                                                </td>
	                                            </tr>
	                                        </tbody>
	                                    </table>
	                                </s:form>
	                            </div>
	                        </div>
                        </div>
                        
                        <div class="col-lg-6 col-md-12 col-sm-12">
	                        <div style="border: 1px solid rgb(204, 204, 204);">
	                            <p class="past">Quick bulk upload your employees Roster (FormatCode -A002)</p>
	                            <div style="text-align: center; padding: 10px;">
	                                <s:form theme="simple" name="UploadRoster" id="UploadRoster" method="POST" action="UploadRoster" enctype="multipart/form-data">    
	                                    <table style="width: 100%;" class="table table_no_border">
	                                        <tbody>
	                                            <tr>
	                                                <td class="txtlabel alignRight">Select File to Import</td>
	                                                <td>
	                                                    <s:file name="fileUpload2" label="Select a File to upload" size="20" />
	                                                </td>
	                                            </tr>
	                                            <tr>
	                                                <td align="center" colspan="2"><input type="submit" class="btn btn-primary" value="Import File" id="ImportEmployees_0"></td>
	                                            </tr>
	                                            <tr>
	                                                <td align="right" colspan="2">
	                                                	<a title="Download Sample File" href="import/ImportRoster_2.xlsx" target="_blank"><i class="fa fa-download" aria-hidden="true"></i> Sample File</a>
	                                                </td>
	                                            </tr>
	                                        </tbody>
	                                    </table>
	                                </s:form>
	                            </div>
	                        </div>
                        </div>
                        
                    </div>
                    
                    <%
                        java.util.List couterlist = (java.util.List)request.getAttribute("alReport");
                    	//System.out.println("couterlist ===>> " + couterlist);
                        if(couterlist!=null) {
					%>
                    <div style="width: 95%; float: left; margin-top: 40px;">
                        <table style='width:100%;' class="table table_no_border">
                            <tbody>
                                <% for(int i=0; i<couterlist.size(); i++) { %>
                                <tr><td align="left"><%=couterlist.get(i) %></td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <%} %>
                        
                        
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>
<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"></script>

<script type="text/javascript"> 
	
    /* $(function() {
    	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
    }); 
     */   
</script>
<%
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    
    UtilityFunctions uF = new UtilityFunctions();
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String strCandiID = (String) request.getAttribute("CandID");
    String strSessionEmpID = (String) session.getAttribute(IConstants.EMPID);
    
    ArrayList alResumes = (ArrayList) request.getAttribute("alResumes");
    String recruitID= (String)request.getAttribute("recruitId");
    if(alResumes == null) alResumes = new ArrayList();
    
    String candidateID = (String) request.getAttribute("CandID");
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    %>

    	<section class="col-lg-12 col-md-12 col-sm-12 connectedSortable" style="padding: 0px;">
				<div class="active tab-pane" id="resume" >
				<script type="text/javascript">
				//(function($){
					$.fn.gdocsViewer = function(options) {
						var settings = {
							width  : '98%',
							height : '400'
						};
						
						if (options) { 
							$.extend(settings, options);
						}
						
						return this.each(function() {
							var file = $(this).attr('href');
				            
				            var ext=file.substring(file.lastIndexOf(".")+1);
				            
				            console.log("Extension : "+ext);
							if (/^(tiff|pdf|ppt|pptx|pps|doc|docx|txt|xls|xlsx)$/.test(ext)) {
								$(this).after(function () {
									var id = $(this).attr('id');
									var gdvId = (typeof id !== 'undefined' && id !== false) ? id + '-gdocsviewer' : '';
									return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 400px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 742px; border: none;margin : 0 auto; display : block;"></iframe></div>';
								})
							}
						});
					};
				//})( jQuery );
				
				$(document).ready(function () {
				    $('a.embed').gdocsViewer();
				});
				
				</script>
				
				 <%	
                    String filePath = null;
                    String fileExt = null;
                    if(alResumes!=null && alResumes.size()!=0) {
                        for(int i=0; i<alResumes.size(); i++) {
                        	filePath = request.getContextPath()+ IConstants.DOCUMENT_LOCATION + ((ArrayList)alResumes.get(i)).get(4);
                        	fileExt = ((ArrayList)alResumes.get(i)).get(5)!=null ? ((ArrayList)alResumes.get(i)).get(5).toString() : null;
                        	if(docRetriveLocation != null) {
                        		filePath = docRetriveLocation +IConstants.I_CANDIDATE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_ATTACHMENT+"/"+candidateID+"/"+ ((ArrayList)alResumes.get(i)).get(4);
                        	}
                        	String action ="ViewCandidateResume.action?from=candProfile&candId="+((ArrayList)alResumes.get(i)).get(3) +"&documentName="+((ArrayList)alResumes.get(i)).get(1) +"&documentId="+((ArrayList)alResumes.get(i)).get(0)+"&filePath="+URLEncoder.encode(filePath);
                        	//System.out.println("action==>"+action);
                        %>
                    <%}%>
                    <% } %>
				
				<% 
				List<String> availableExt = (List<String>)request.getAttribute("availableExt");
				if(availableExt == null) availableExt = new ArrayList<String>();
				
				System.out.println("fileExt ===>> " + fileExt +" --- availableExt ===>> " + availableExt);
				boolean flag = false;
				if(fileExt!=null && availableExt.contains(fileExt)){
					flag = true;
				}
				
				//String filePath = (String) request.getParameter("filePath"); %>
				
				<% if(flag) { %>
					<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;">
						<a href="<%=filePath %>" class="embed" id="test">&nbsp;</a>
					</div>
				<%	} else { %>
					<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;height: 500px; background-color: #CCCCCC;">
						<div style="text-align: center; font-size: 24px; padding: 150px;">Document not available</div>
					</div>
				<%	} %>
				</div>		
			
    	</section>

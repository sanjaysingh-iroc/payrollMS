<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<style>
.manual_title{
font-size: 18px;
font-weight: bold;
width: 100%;
color: #346897;
padding: 5px;
}
.manual_body {
line-height: 24px;
text-align: left;
padding: 15px;
}
</style>
<script type="text/javascript">
$(function(){
	$.fn.gdocsViewer = function(options) {
		var settings = {
			width  : '98%',
			height : '742'
		};
		
		if (options) { 
			$.extend(settings, options);
		}
		
		return this.each(function() {
			var file = $(this).attr('href');
            // int SCHEMA = 2, DOMAIN = 3, PORT = 5, PATH = 6, FILE = 8, QUERYSTRING = 9, HASH = 12
            var ext=file.substring(file.lastIndexOf(".")+1);
            
            if(ext === "docx" || ext === "pptx") {
         	   alert("Unsupported file format!");
            } else {
				if (/^(tiff|pdf|ppt|pptx|pps|doc|docx|txt|xls|xlsx)$/.test(ext)) {
					$(this).after(function () {
						var id = $(this).attr('id');
						var gdvId = (typeof id !== 'undefined' && id !== false) ? id + '-gdocsviewer' : '';
						return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 742px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 742px; border: none;margin : 0 auto; display : block;"></iframe></div>';
					})
				}
            }
		});
	};
	$('a.embed1').gdocsViewer();
});

/* return '<div id="' + gdvId + '" class="gdocsviewer"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '" width="' + settings.width + '" height="' + settings.height + '" style="border: none;margin : 0 auto; display : block;"></iframe></div>'; */
/* $(function () {
    $('a.embed1').gdocsViewer();
    //$('#embedURL').gdocsViewer();
}); */

</script>
<%
	String strTitle = (String)request.getAttribute("TITLE");
	String strBody = (String)request.getAttribute("BODY");
	String strDate = (String)request.getAttribute("DATE");
	String strE = (String)request.getParameter("E");
	String pageFrom = (String)request.getParameter("pageFrom");
	
	List<String> availableExt = (List<String>)request.getAttribute("availableExt");
	String manualDocPath = (String)request.getAttribute("manualDocPath");
	String extention = (String)request.getAttribute("extention");
	
	if(availableExt == null) availableExt = new ArrayList<String>(); 
	boolean flag = true;
	if(!availableExt.contains(extention)) {
		flag = false;
	}
    %>

<%if(pageFrom==null || pageFrom.trim().equalsIgnoreCase("null") || pageFrom.equals("")) { %>

    <section class="content">
        <div class="row jscroll" style="margin-right: 0px;">
            <div class="box box-none">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    
                        <% } %>   
                       
                        <s:hidden name="pageFrom" id="pageFrom"></s:hidden>
                        <div class="manual_box">
                          
                            <div class="addgoaltoreview">
                                <div class="manual_title">
                                    <%=strTitle %>
                                </div>
                                <div class="clr"></div>
                                <div style="float:right; padding:0px 20px"><span style="font-style:italic; color:#666666">Last Updated on:</span> <%=strDate %></div>
                            </div>
                            <div class="clr"></div>
                            <div class="manual_body">
                                 <%if(strBody != null && !strBody.equals("") && (manualDocPath == null || manualDocPath.equals(""))) { %>
            						<%=strBody%>
					            <% } else if(manualDocPath != null && !manualDocPath.equals("")) {
						        	if(flag) {
						        %>
	        	
										<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;">
											<a href="<%=manualDocPath %>" class="embed1" id="test">&nbsp;</a>
										</div>
								     <% } else { %>
											<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;height: 500px; background-color: #CCCCCC;">
												<div style="text-align: center; font-size: 24px; padding: 150px;">Preview not available</div>
											</div>
								 	  <% }%>
		  						 <% } %>
                            </div>
                            <div class="clr"></div>
                        </div>
                        <%if(pageFrom==null || pageFrom.trim().equalsIgnoreCase("null") || pageFrom.equals("")) { %>
                   </div>
                <!-- /.box-body -->
            </div>
        </div>
    </section>
    <%} %>

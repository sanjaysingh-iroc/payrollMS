<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*,com.konnect.jpms.util.UtilityFunctions,com.konnect.jpms.task.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery.gdocsviewer.min.js"></script>
<script type="text/javascript">


/* return '<div id="' + gdvId + '" class="gdocsviewer"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '" width="' + settings.width + '" height="' + settings.height + '" style="border: none;margin : 0 auto; display : block;"></iframe></div>'; */

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
            
            console.log("Extension : "+ext);
			if (/^(tiff|pdf|ppt|pptx|pps|doc|docx|txt|xls|xlsx)$/.test(ext)) {
				$(this).after(function () {
					var id = $(this).attr('id');
					var gdvId = (typeof id !== 'undefined' && id !== false) ? id + '-gdocsviewer' : '';
					return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 742px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 742px; border: none;margin : 0 auto; display : block;"></iframe></div>';
				})
			}
		});
	};
    
	 $('a.embed').gdocsViewer();




</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Offer Letter" name="title" />
</jsp:include> --%>
<section class="content">
	<div class="row jscroll">
		<section class="col-lg-12 connectedSortable">
			<div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <%
						String filePath = (String) request.getAttribute("filePath");
						System.out.println("filePath: "+filePath);
						if(filePath != null && !filePath.equals("")){
					%>
						<div id="tblDiv">
							<a href="<%=filePath %>" class="embed" id="test">&nbsp;</a>
						</div>
					<% }else{ %>
						<div id="tblDiv">
							<div>Preview not available</div>
						</div>
					<% } %>
					<script>
					
						$('a.embed').gdocsViewer();
					
					</script>
                </div>
            </div>
		</section>
	</div>
</section>



<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
 
<script type="text/javascript">
//(function($){
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
           if(ext === "docx" || ext === "pptx") {
        	   alert("Unsupported file format!");
           }else {
        	   if (/^(tiff|pdf|ppt|pptx|pps|doc|txt|xls|xlsx)$/.test(ext)) {
   				$(this).after(function () {
   					var id = $(this).attr('id');
   					var gdvId = (typeof id !== 'undefined' && id !== false) ? id + '-gdocsviewer' : '';
   					return '<div id="' + gdvId + '" class="gdocsviewer" style="width:98%; height: 742px;"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '"  style="width:98%; height: 742px; border: none;margin : 0 auto; display : block;"></iframe></div>';
   				})
   			}
           }
			
		});
	};
//})( jQuery );

/* return '<div id="' + gdvId + '" class="gdocsviewer"><iframe src="https://docs.google.com/viewer?embedded=true&url=' + encodeURIComponent(file) + '" width="' + settings.width + '" height="' + settings.height + '" style="border: none;margin : 0 auto; display : block;"></iframe></div>'; */
$(document).ready(function () {
    $('a.embed').gdocsViewer();
    //$('#embedURL').gdocsViewer();
});

</script>
<%
String feedFile = (String)request.getAttribute("feedFile");
List<String> availableExt = (List<String>)request.getAttribute("availableExt");
String feedDocPath = (String)request.getAttribute("feedDocPath");
String extention = (String)request.getAttribute("extention");
String pageFrom = (String)request.getParameter("pageFrom");
if(availableExt == null) availableExt = new ArrayList<String>(); 
boolean flag = true;
if(!availableExt.contains(extention)) {
	flag = false;
}

//System.out.println("flag==>"+flag+"\n==>feedDocPath==>"+feedDocPath);
%>

<div class="manual_box">
   <div style="float:left; width:100%; padding:15px 0px; background:#efefef; border-top:solid 1px #fefefe;border-bottom:solid 5px #ccc;">
          <div class="manual_title">
		  <%=feedFile %>
           </div>
   </div>
	
	<div class="clr"></div>
    <div class="manual_body">
       	<% if(flag) {%>
        	   <div id="tblDiv" style="float: left;width: 100%;margin-left:10px;">
					<a href="<%=feedDocPath %>" class="embed" id="test">&nbsp;</a>
			  </div>
	     <% } else {%>
				<div id="tblDiv" style="float: left;width: 100%;margin-left:10px;height: 500px; background-color: #CCCCCC;">
					<div style="text-align: center; font-size: 24px; padding: 150px;">Preview not available</div>
				</div>
	 	  <% }%>
    </div>
       <div class="clr"></div>
</div>
	
<%-- <%if(pageFrom!=null && pageFrom.trim().equalsIgnoreCase("EP")) { %>
</div>
<%} %> --%>


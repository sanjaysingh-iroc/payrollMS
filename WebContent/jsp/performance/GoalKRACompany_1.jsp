<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String dataType = (String)request.getParameter("dataType");
	String currUserType = (String)request.getParameter("currUserType");
	String strEmpId = (String)request.getAttribute("strEmpId");
	
	Map<String, List<String>> hmBscData = (Map<String, List<String>>) request.getAttribute("hmBscData");
	if(hmBscData == null) hmBscData = new LinkedHashMap<String, List<String>>();
	
	//List<String> bscList = (List<String>) request.getAttribute("BscList");
	String bscId0 = "";
%>


	<ul class="products-list product-list-in-box">
		<%if(hmBscData != null && hmBscData.size()>0) { 
			Iterator<String> it = hmBscData.keySet().iterator();
			while(it.hasNext()) {
				String bscId = it.next();
				List<String> innerList = hmBscData.get(bscId);
				if(bscId0.equals("")) {
					bscId0 = bscId;
				}
			%>
				
			<li class="item">
 				<span style="float: left; width: 100%;"> 
 					<a href="javascript:void(0);" <%if(uF.parseToInt(bscId0) == uF.parseToInt(bscId)) { %> class="activelink" <% } %> onclick ="getBscPerspectiveDeatils('BscPerspectiveDetails','<%=bscId %>','<%=dataType %>','<%=currUserType %>')" ><%=innerList.get(1)%></a>
		 		</span>
		 		<span style="float: left; width: 100%;"> 
		 			<%=innerList.get(2)%>
		 		</span>
	 		</li>
	 		<% }
			} else { %>
			<div class="nodata msg">No BSCs added.</div>
		<% } %>
	</ul>
	
 <script type="text/javascript" charset="utf-8">

	$(document).ready(function() {
		
		getBscPerspectiveDeatils('BscPerspectiveDetails','<%=bscId0 %>','<%=dataType %>','<%=currUserType %>');
		
    	$("body").on('click','#closeButton',function(){
    		$('.modal-body').height(400);
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){ 
    		$('.modal-body').height(400);
    		$(".modal-dialog").removeAttr('style');
    		$("#modalInfo").hide();
    	});
    	
	});

	function getBscPerspectiveDeatils(strAction,bscId,dataType,currUserType){
		$("#goalKraDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?strBscId='+bscId+'&dataType='+dataType+'&currUserType='+currUserType,
			cache: true,
			success: function(result){
				//alert("result2==>"+result);
				$("#goalKraDetails").html(result);
	   		}
		});
	}

 
 </script>		
	         
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body"
                style="height: 400px; overflow-y: auto; padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default"
                    data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


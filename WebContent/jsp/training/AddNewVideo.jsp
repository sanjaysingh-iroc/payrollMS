<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.select.FillVideoLink"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

 

<style type="text/css">
	/* CSS for the demo. CSS needed for the scripts are loaded dynamically by the scripts */
	
	.txtlbl {
		color: #777777;
	    font-family: verdana,arial,helvetica,sans-serif;
	    font-size: 11px;
	    font-style: normal;
	    font-weight: 600;
	    width: 100px;
	}
	
	a.close-font:before{
	 font-size: 24px;
    }
</style>
<%
	ArrayList videoLinkList = (ArrayList) request.getAttribute("videoLinkList");
	ArrayList alVideoDetails = (ArrayList) request.getAttribute("alVideoDetails");
	ArrayList alSubVideoDetails = (ArrayList) request.getAttribute("alSubVideoDetails");
%>

<script> 

/* ===added by parvez date: 11-11-2021=== */
/* $("#btnSubmit").click(function(){
	$(".validateRequired").prop('required',true);
}); */
    
    function GetXmlHttpObject() {
    	if (window.XMLHttpRequest) {
    		// code for IE7+, Firefox, Chrome, Opera, Safari
    		return new XMLHttpRequest();
    	}
    	if (window.ActiveXObject) {
    		// code for IE6, IE5
    		return new ActiveXObject("Microsoft.XMLHTTP");
    	}
    	return null;
    }
   
    function closeForm() {
    		
    	/* $("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>'); */
    	$("#divCDResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		url:'VideosDashboard.action?fromPage=LD',   //?fromPage=LD 
    		cache:false,
    		success:function(data){
    			/* $("#divResult").html(data); */
    			$("#divCDResult").html(data);
    		}
    	});
    	
    }
    
    function addNewVideoLink(videoLink,cnt){
    	 
    	if(videoLink == "0"){
    	//	document.getElementById("videoLinkName").disabled=true; 
    	//alert("cnt="+cnt); 
   			var videoLinkLen;
   			if(cnt == "0"){
   				//alert("cnt="+cnt);
   				videoLinkLen = document.getElementById("videoLinkName"+cnt).length;
   	   			//alert("desigLen ===> " + desigLen);
   	   			document.getElementById("videoLinkName"+cnt).selectedIndex = parseInt(videoLinkLen)-1;
   				
   			} else {
   				videoLinkLen = document.getElementById("subVideoLinkName"+cnt).length;
   	   			//alert("desigLen ===> " + desigLen);
   	   			document.getElementById("subVideoLinkName"+cnt).selectedIndex = parseInt(videoLinkLen)-1;
   				
   			}
    		
   			
   			document.getElementById("videoNameTr"+cnt).style.display="table-row";
   			document.getElementById("videoLinkTr"+cnt).style.display="table-row";
    	} else{
    		
   			document.getElementById("videoNameTr"+cnt).style.display="none";
   			document.getElementById("videoLinkTr"+cnt).style.display="none";
   			
    	}
    }
    
    <% if (alSubVideoDetails!=null) {%>
		var divcnt = <%=alSubVideoDetails.size()%>;
	<%}else{%>
		var divcnt = 0;
	<%}%>
    
    function addNewSubchapter(){ 
    	//alert("divcnt="+divcnt); 
    	divcnt++;
    	
    	var divTag = document.createElement("div");
    	divTag.id = "subchapter"+divcnt; 
        divTag.setAttribute("style", "float:left;width: 100%;");
        
      /* ===start parvez date: 11-11-2021 note: remove required tag from videos description=== */  
        divTag.innerHTML = "<table border=\"0\" class=\"table table_no_border\" style=\"width: 85%;\"> <tr>"
            +"<td class=\"txtlabel\" style=\"vertical-align: top; text-align: right\">Sub Title:<sup>*</sup></td> "
            +"<td> <input type=\"text\" name=\"subVideoTitle\" class=\"validateRequired form-control \" style=\"width: 450px;\" ></input></td>"
            +"</tr>"
            +"<tr><td class=\"txtlabel\" style=\"vertical-align: top; text-align: right\">Video Description:</td>"
            +"<td><input type=\"text\" name=\"subVideoDescription\" class=\"form-control \" style=\"width: 450px;\" ></input></td></tr>"
            +"<tr id=\"videoListTr\"><td class=\"txtlabel\" style=\"vertical-align: top; text-align: right\">Video List:<sup>*</sup></td>"
        	+"<td><span><select name=\"subVideoLinkName\" id=\"subVideoLinkName"+divcnt+"\" class=\"validateRequired form-control\" onchange=\"addNewVideoLink(this.value,"+divcnt+")\">"
            +"<%=request.getAttribute("sbVideoList")%>"
            +"<tr id=\"videoNameTr"+divcnt+"\" style=\"display:none;\"><td class=\"txtlabel\" style=\"vertical-align: top; text-align: right\">Video Link Name:<sup>*</sup></td>"
            +"<td><input type=\"text\" name=\"subVideoName\" id=\"subVideoName\" class=\"validateRequired form-control \" style=\"width: 450px;\" ></input> </td></tr>"
            +"<tr id=\"videoLinkTr"+divcnt+"\" style=\"display:none;\"><td class=\"txtlabel\" style=\"vertical-align: top; text-align: right\">Video Link:<sup>*</sup></td>"
            +"<td><input type=\"text\" name=\"subVideoLink\" id=\"subVideoLink\" class=\"validateRequired form-control \" style=\"width: 450px;\" ></input></td></tr></table>"
            +"<div style=\"width:60%\"><span style=\"float: right; \"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\" title=\"Remove Subchapter\" onclick=\"removeSubchapter("+divcnt+");\"></i></span></div>";
	/* ===end parvez date: 11-11-2021=== */	
        document.getElementById("subchapterDiv").appendChild(divTag);
    }
    
    
	function removeSubchapter(removeId) {
    	
    	var remove_elem = "subchapter"+removeId;
    	var row_skill = document.getElementById(remove_elem); 
    	document.getElementById("subchapterDiv").removeChild(row_skill);
    }
    
</script>

<% String strOperation1 = (String)request.getAttribute("strOperation1"); %>

<section class="content">
    <div class="row jscroll">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                       
                        <div style="width: 100%; background-color: #868686; box-shadow:3px 4px 12px #616161;height: 80%;">
                        	<div class="pull-right">
								<a href="javascript:void(0);" onclick="closeForm()" class="close-font" style="margin-right:20px;"/>
								<!-- <a href="VideosDashboard.action?fromPage=LD" class="close-font" style="margin-right:20px;"/> -->
							</div>
                            <div id="container1" style="width: 99%; float: left;height:98%;">
                               
                                <!-- <div style="border: solid 0px #ff0000; width:96%;" id="course"> -->
                                    <!-- <div class="cat_heading"><h3>Tablar Data</h3></div> -->
                                    <!-- <div id="course1"> -->
                                    <s:form theme="simple" action="AddNewVideo" name="frm_AddNewVideo" id="frm_AddNewVideo" method="POST" cssClass="formcss" enctype="multipart/form-data">
                                        <s:hidden name="operation"></s:hidden>
                                        <s:hidden name="videoId"></s:hidden>
                                        
                                        <div style="float: left; width: 100%;">
                                            <!-- <table border="0" class="table table_no_border" style="width: 85%;"> -->
                                                <% 	
				                                if(alVideoDetails!=null && alVideoDetails.size()!=0){
				                                	for(int i=0; i<alVideoDetails.size(); i++) {
				                                %>
				                                <table border="0" class="table table_no_border" style="width: 85%;">
				                                <tr>
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Video Title:<sup>*</sup></td>
                                                    <td>
                                                        <%-- <s:textfield name="videoTitle" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                        <input type="text" name="videoTitle" class="validateRequired form-control " style="width: 450px;" value="<%=((ArrayList)alVideoDetails.get(i)).get(1)%>"></input>
                                                    </td>
                                                </tr>
                                                <tr>
                                                <!-- ===start parvez date: 11-11-2021 Note remove * and validateRequired class from video description=== -->
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Video Description:<%-- <sup>*</sup> --%></td>
                                                    <td>
                                                        <%-- <s:textfield name="videoDescription" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                        <input type="text" name="videoDescription" class="form-control " style="width: 450px;" value="<%=((ArrayList)alVideoDetails.get(i)).get(2)%>" ></input>
                                                    </td>
                                               <!-- ===end parvez date: 11-11-2021=== -->
                                                </tr>
                                                <tr id="videoListTr">
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Video List:<sup>*</sup></td>
                                                    <td>
                                                        <span>
                                                            <%-- <s:select theme="simple" name="videoLinkName" headerKey="" headerValue="Select Video" list="videoLinkList" 
                                                                listKey="videoId" id="videoLinkName0" listValue="videoName" cssClass="validateRequired form-control " onchange="addNewVideoLink(this.value,'0');"/>	<!--  value="videoID" --> --%>
                                                            <select name="videoLinkName" id="videoLinkName0" class="validateRequired form-control " onchange="addNewVideoLink(this.value,'0');">
                                                            	<option value="">Select Video</option>
	                                                            <% for(int j=0; j<videoLinkList.size(); j++){ 
	                                                            	//String strId = alVideoDetails.get(0)+"";
	                                                            	if( (((FillVideoLink)videoLinkList.get(j)).getVideoId()+"").equals( (String)((ArrayList)alVideoDetails.get(i)).get(3) )) {
	                                                            %>
	                                                            	<option value="<%=((FillVideoLink)videoLinkList.get(j)).getVideoId() %>" selected="selected">
	    		                                                    <%=((FillVideoLink)videoLinkList.get(j)).getVideoName() %>
	    		                                                	</option>
	                                                             <%} else{ %>
	                                                             <option value="<%=((FillVideoLink)videoLinkList.get(j)).getVideoId() %>" >
				                                                    <%=((FillVideoLink)videoLinkList.get(j)).getVideoName() %>
				                                                </option>
	                                                             <%} 
	                                                            	}%>
                                                            	<option value="0">Other</option>
                                                            </select>
                                                        </span>
                                                    </td>
                                                </tr>
                                                <tr id="videoNameTr<%=i%>" style="display:none;">
                                                	<td class="txtlabel" style="vertical-align: top; text-align: right">Video Link Name:<sup>*</sup></td>
                                                	<td>
                                                		<%-- <s:textfield name="videoName" id="videoName" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                		<input type="text" name="videoName" id="videoName" class="validateRequired form-control " style="width: 450px;" ></input>
                                                	</td>
                                                </tr>
                                                <tr id="videoLinkTr<%=i%>" style="display:none;">
                                                	<td class="txtlabel" style="vertical-align: top; text-align: right">Video Link:<sup>*</sup></td>
                                                	<td>
                                                		<%-- <s:textfield name="videoLink" id="videoLink" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                		<input type="text" name="videoLink" id="videoLink" class="validateRequired form-control " style="width: 450px;" ></input>
                                                	</td>
                                                </tr>
                                                </table>
                                                <% 
                                                if(alSubVideoDetails!=null && alSubVideoDetails.size()!=0){
                                                	for(int k=0; k<alSubVideoDetails.size(); k++){ %>
                                                <div id="subchapterDiv">
                                                <div id="subchapter<%=k+1 %>" style="float:left;width: 100%;">
	                                                <table border="0" class="table table_no_border" style="width: 85%;">
		                                                <tr>
		                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Sub Title:<sup>*</sup></td>
		                                                    <td>
		                                                    	<input type="hidden" name="subVideoId" value="<%=((ArrayList)alSubVideoDetails.get(k)).get(0)%>" />
		                                                        <input type="text" name="subVideoTitle" class="validateRequired form-control " style="width: 450px;" value="<%=((ArrayList)alSubVideoDetails.get(k)).get(1)%>" ></input>
		                                                    </td>
		                                                </tr>
		                                                <tr>
		                                                <!-- ===start parvez date: 11-11-2021 Note remove * and validateRequired class from video description=== -->
		                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Video Description:<%-- <sup>*</sup> --%></td>
		                                                    <td>
		                                                        <%-- <s:textfield name="videoDescription" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
		                                                        <input type="text" name="subVideoDescription" class="form-control " style="width: 450px;" value="<%=((ArrayList)alSubVideoDetails.get(k)).get(2)%>" ></input>
		                                                    </td>
		                                                <!-- ===end parvez date: 11-11-2021=== -->
		                                                </tr>
		                                                <tr id="videoListTr">
		                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Video List:<sup>*</sup></td>
		                                                    <td>
		                                                        <span>
		                                                            <%-- <s:select theme="simple" name="videoLinkName" headerKey="" headerValue="Select Video" list="videoLinkList" 
		                                                                listKey="videoId" id="videoLinkName0" listValue="videoName" cssClass="validateRequired form-control " onchange="addNewVideoLink(this.value,'0');"/>	<!--  value="videoID" --> --%>
		                                                            <select name="subVideoLinkName" id="subVideoLinkName<%=k+1 %>" class="validateRequired form-control " onchange="addNewVideoLink(this.value,'<%=k+1 %>');">
		                                                            	<option value="">Select Video</option>
			                                                            <% for(int j=0; j<videoLinkList.size(); j++){ 
			                                                            	if( (((FillVideoLink)videoLinkList.get(j)).getVideoId()+"").equals( (String)((ArrayList)alSubVideoDetails.get(k)).get(3) )) {
			                                                            %>
			                                                            	<option value="<%=((FillVideoLink)videoLinkList.get(j)).getVideoId() %>" selected="selected">
			    		                                                    <%=((FillVideoLink)videoLinkList.get(j)).getVideoName() %>
			    		                                                	</option>
			                                                             <%} else{ %>
			                                                             <option value="<%=((FillVideoLink)videoLinkList.get(j)).getVideoId() %>" >
						                                                    <%=((FillVideoLink)videoLinkList.get(j)).getVideoName() %>
						                                                </option>
			                                                             <%} 
			                                                            	}%>
		                                                            	<option value="0">Other</option>
		                                                            	
		                                                            </select>
		                                                        </span>
		                                                    </td>
		                                                </tr>
		                                                <tr id="videoNameTr<%=k+1 %>" style="display:none;">
		                                                	<td class="txtlabel" style="vertical-align: top; text-align: right">Video Link Name:<sup>*</sup></td>
		                                                	<td>
		                                                		<%-- <s:textfield name="videoName" id="videoName" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
		                                                		<input type="text" name="subVideoName" id="subVideoName" class="validateRequired form-control " style="width: 450px;" ></input>
		                                                	</td>
		                                                </tr>
		                                                <tr id="videoLinkTr<%=k+1 %>" style="display:none;">
		                                                	<td class="txtlabel" style="vertical-align: top; text-align: right">Video Link:<sup>*</sup></td>
		                                                	<td>
		                                                		<%-- <s:textfield name="videoLink" id="videoLink" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
		                                                		<input type="text" name="subVideoLink" id="subVideoLink" class="validateRequired form-control " style="width: 450px;" ></input>
		                                                	</td>
		                                                </tr>
		                                                <%-- <tr>
		                                                <td></td>
		                                                <td>
		                                                	<div style="width:60%"><span style="float: right; padding"><i class="fa fa-times-circle cross" aria-hidden="true" title="Remove Subchapter" onclick="removeSubchapter(<%=k+1 %>);"></i></span></div>
		                                                </td>
		                                                </tr> --%>
	                                                </table>
	                                                <div style="width:60%"><span style="float: right; padding"><i class="fa fa-times-circle cross" aria-hidden="true" title="Remove Subchapter" onclick="removeSubchapter(<%=k+1 %>);"></i></span></div>
                                                </div>
                                                </div>
                                                <%} } %>
				                                <%} %>
				                                <%} else{ %>
				                                <table border="0" class="table table_no_border" style="width: 85%;">
                                                <tr>
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Video Title:<sup>*</sup></td>
                                                    <td>
                                                        <%-- <s:textfield name="videoTitle" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                        <input type="text" name="videoTitle" class="validateRequired form-control " style="width: 450px;" ></input>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Video Description:<%-- <sup>*</sup> --%></td>
                                                    <td>
                                                        <%-- <s:textfield name="videoDescription" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                        <input type="text" name="videoDescription" class="form-control " style="width: 450px;" ></input>
                                                    </td>
                                                </tr>
                                                <tr id="videoListTr">
                                                    <td class="txtlabel" style="vertical-align: top; text-align: right">Video List:<sup>*</sup></td>
                                                    <td>
                                                        <span>
                                                            <%-- <s:select theme="simple" name="videoLinkName" headerKey="" headerValue="Select Video" list="videoLinkList" 
                                                                listKey="videoId" id="videoLinkName0" listValue="videoName" cssClass="validateRequired form-control " onchange="addNewVideoLink(this.value,'0');"/>	<!--  value="videoID" --> --%>
                                                            <select name="videoLinkName" id="videoLinkName0" class="validateRequired form-control " onchange="addNewVideoLink(this.value,'0');">
                                                            	<option value="">Select Video</option>
	                                                            <% for(int j=0; j<videoLinkList.size(); j++){ %>
	                                                            	<option value="<%=((FillVideoLink)videoLinkList.get(j)).getVideoId() %>" >
	    		                                                    <%=((FillVideoLink)videoLinkList.get(j)).getVideoName() %>
	    		                                                	</option>
	                                                             <%} %>
                                                            	<option value="0">Other</option>
                                                            </select>
                                                        </span>
                                                    </td>
                                                </tr>
                                                <tr id="videoNameTr0" style="display:none;">
                                                	<td class="txtlabel" style="vertical-align: top; text-align: right">Video Link Name:<sup>*</sup></td>
                                                	<td>
                                                		<%-- <s:textfield name="videoName" id="videoName" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                		<input type="text" name="videoName" id="videoName" class="validateRequired form-control " style="width: 450px;" ></input>
                                                	</td>
                                                </tr>
                                                <tr id="videoLinkTr0" style="display:none;">
                                                	<td class="txtlabel" style="vertical-align: top; text-align: right">Video Link:<sup>*</sup></td>
                                                	<td>
                                                		<%-- <s:textfield name="videoLink" id="videoLink" cssClass="validateRequired form-control " cssStyle="width: 450px;" required="true"></s:textfield> --%>
                                                		<input type="text" name="videoLink" id="videoLink" class="validateRequired form-control " style="width: 450px;" ></input>
                                                	</td>
                                                </tr>
                                                <%} %>
                                            </table>
                                            <div id="subchapterDiv">
                                            </div>
                                            <div style="margin-left: 100px; float: left;">
                                            	<a href="javascript:void(0)" onclick="addNewSubchapter();"> +Add Subchapter</a>
                                            </div>
                                            
                                        </div>
                                        <div style="width: 100%; float: right;">
                                            <%if(strOperation1!=null && strOperation1.equalsIgnoreCase("U")){ %>
                                            	<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="submit" value="Update" id="btnSubmit" />
                                            <%}else{ %>
                                            	<s:submit cssClass="btn btn-primary" cssStyle="float:right; margin-right: 5px;" name="submit" value="Submit" id="btnSubmit" />
                                            <%} %>
                                        </div>
                                    </s:form>
                                    <!-- </div> -->
                                <!-- </div> -->
                              
                                
                            </div>
                        </div>
                </div>
        <!-- <div class="modal" id="modalInfo" role="dialog">
		    <div class="modal-dialog">
		        Modal content
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal">&times;</button>
		                <h4 class="modal-title">Candidate Information</h4>
		            </div>
		            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
		            </div>
		            <div class="modal-footer">
		                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
		            </div>
		        </div>
		    </div>
		</div> -->
    </div>
</section>
<div id="debug"></div>
<div id="SelectQueDiv"></div>

<script>
 		/* $(document).ready(function(){
 			//var x = $("#videoLinkName").val(); 
 			var x = document.getElementById("videoLinkName").value;
 			alert(x);
 		});
      */
      $(function(){
    	  
    	  $("body").on("click","#btnSubmit",function(){
  	    	$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
  			$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
  	    }); 
      
      	 $("form").bind('submit',function(event) { 
   		/* $("#frm_AddNewVideo").submit(function(event) { */
   			//alert("form_data==>");
   			 var form_data = $("#"+this.id).serialize();
             /* $("#divResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>'); */
             $("#divCDResult").html('<div id ="the_div"><div id="ajaxLoadImage"></div></div>');
   	   	  
   	      $.ajax({
   	     		url: "AddNewVideo.action",
   	     		type: 'POST',
   	     		data: form_data,
   	     		/* data: $("#"+this.id).serialize(), */
   	     	    success: function(result){
   	     			/* $("#divResult").html(result); */
   	     	    	//$("#divCDResult").html(result);
	   	     	    $.ajax({
	   	     			url:'VideosDashboard.action?fromPage=LD',    
	   	     			cache:false,
	   	     			success:function(result){
		   	     			/* $("#divResult").html(data); */
		   	     			$("#divCDResult").html(result);
	   	     			}
	   	     		});
   	     	    },
   	 //===start parvez date: 11-10-2021=== 
	     		error: function(res){
	     			$.ajax({
						url: 'VideosDashboard.action?fromPage=LD',
						cache: true,
						success: function(result){
							
							$("#divCDResult").html(result);
				   		}
					});
	  			}
  	//===end parvez date: 11-10-2021=== 
   	       });
   	     
   		});
     });
   		
      

</script>
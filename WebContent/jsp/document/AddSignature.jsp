<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<script type="text/javascript"> 

$(document).ready(function() { 
	$("#btnAddNewRowOk").click(function(){
		  $('.validateRequired').filter(':hidden').prop('required',false);
	      $('.validateRequired').filter(':visible').prop('required',true);
	});
	
	$(function(){
		$("body").on('click','#closeButton',function(){
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
	    });
		$("body").on('click','.close',function(){
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
		});
		
		$("body").on('click','#closeButton1',function(){
			$(".modal-dialog1").removeAttr('style');
			$("#modal-body1").height(400);
			$("#modalInfo1").hide();
	    });
	});
});


function getUsers(value) {
	if (value == '3') {
		//imageTr imageAlignTr textAlignTr
		document.getElementById("trRecruiter").style.display = "table-row";
	} else {
		document.getElementById("trRecruiter").style.display = "none";
	}
}

</script>


<%
	List<Map<String, String>> signatureList = (List<Map<String, String>>) request.getAttribute("signatureList");

	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
%>

<div style="width: 100%; float: left;">
	<s:form name="frmAddSignature" id="frmAddSignature" theme="simple" action="AddSignature" method="POST" cssClass="formcss" enctype="multipart/form-data" >
		<s:hidden name="orgId"/>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		<s:hidden name="operation" value="A" />
		
		<table style="margin:10px 0px" class="table">
			<tr> 
				<td>Signature Type :</td>  
				<td>
					<s:select name="strSignatureType" id="strSignatureType" headerKey="" headerValue="Select Signature Type" 
					list="#{'1':'Authority Signature', '2':'HR Signature', '3':'Recruiter Signature'}" cssClass="validateRequired" onchange="getUsers(this.value);"/>
				</td>
			</tr>
			<tr id="trRecruiter" style="display: none;">
				<td>Recruiter :</td>  
				<td>
					<s:select name="employeeId" cssClass="validateRequired" list="empList" theme="simple" headerKey="" headerValue="Select Recruiter" 
						listKey="employeeId" id="employeeId" listValue="employeeCode" required="true"/>
				</td>
			</tr>
			<tr id="imageTr">  
				<td>Image :</td>  
				<td><s:file name="strSignatureImg" id="strSignatureImg" cssClass="validateRequired"></s:file>
				<br/>100px X 70px is most optimized size. Please upload less than 15kb image. </td> <!-- 80px X 200px is optimized size. Please upload less than 15kb image.  --> 
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<s:submit value="Save" name="btnsubmit" id="btnAddNewRowOk" cssClass="btn btn-primary"></s:submit>
				</td>
			</tr>
		</table>
	</s:form>
</div>


<div  style="border-bottom: 1px solid #cccccc;clear: both;padding-top: 20px;"><h4>Signature List</h4></div>

		<table class="table table_no_border">
			<thead>
				<tr>
					<th>Signature Type</th>
					<th>Signature</th>
					<th>User Name</th>
					<th>Action</th>
				</tr>
			</thead>
			
			<tbody>
			<%
				int i = 0;
				for (; signatureList != null && !signatureList.isEmpty() && i < signatureList.size(); i++) {
					Map<String, String> hmInner = signatureList.get(i);
					if (hmInner == null)
						hmInner = new HashMap<String, String>();
			%>
				<tr>
					<td width="40%" valign="middle" style="padding-left: 50px;"><%=hmInner.get("SIGNATURE_NAME") %></td>
					<td><%=hmInner.get("SIGNATURE_IMAGE")%></td>
					<td><%=hmInner.get("USER_NAME")%></td>
					<td valign="top">
						<a style="float: right;" title="Delete Signature" href="AddSignature.action?operation=D&signatureID=<%=hmInner.get("SIGNATURE_ID")%>&orgId=<%=request.getAttribute("orgId") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this signature?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a>
						<%-- <a style="float:right;" title="Edit" href="javascript:void(0);" onclick="editCollateral('<%=hmInner.get("COLLATERAL_ID")%>', '<%=request.getAttribute("orgId") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> --%>
					</td>						
				</tr>
				<% } %>
			</tbody>
		</table>
	<% if (i == 0) { %>
	 	<li><div class="msg nodata">No Data found</div></li>
	 <%
	 	}
	 %>

<div class="modal" id="modalInfo1" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title modal-title1">Employee Information</h4>
            </div>
            <div class="modal-body" id="modal-body1" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>
//	$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>


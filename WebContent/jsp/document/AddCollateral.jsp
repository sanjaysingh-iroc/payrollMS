<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<script type="text/javascript"> 

$(document).ready(function() { 
	$("#btnAddNewRowOk").click(function(){
		  $("#frmID").find('.validateRequired').filter(':hidden').prop('required',false);
	      $("#frmID").find('.validateRequired').filter(':visible').prop('required',true);
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


function editCollateral(collateralID, orgId, userscreen, navigationId, toPage) {
	var dialogEdit1 = '#modal-body1';
	$(dialogEdit1).empty();
	$(dialogEdit1).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo1").show();
	$('.modal-title1').html('Update Collaterals');
	$.ajax({
		url : 'EditCollateral.action?collateralID='+collateralID+'&orgId='+orgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit1).html(data);
		}
	});
}


function changeType(value) {
	//var value = document.getElementById("trainingSchedulePeriod").value;
	// Created by Dattatray
	if (value == 'F' || value == 'H') {
		//imageTr imageAlignTr textAlignTr
		/* document.getElementById("imageTr").style.display = "none";
		document.getElementById("imageAlignTr").style.display = "none";
		document.getElementById("textAlignTr").style.display = "none";
		document.getElementById("textTr").style.display = "table-row"; */
		
		document.getElementById("imageTr").style.display = "table-row";
		document.getElementById("imageAlignTr").style.display = "none";
		document.getElementById("textAlignTr").style.display = "none";
		document.getElementById("textTr").style.display = "none";
		
	} 
	/* else {
		document.getElementById("imageTr").style.display = "table-row";
		document.getElementById("imageAlignTr").style.display = "none";
		document.getElementById("textAlignTr").style.display = "none";
		document.getElementById("textTr").style.display = "none";
	} */
}

</script>
<%
	List<Map<String, String>> collateralList = (List<Map<String, String>>) request.getAttribute("collateralList");

	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
%>
<div style="width: 100%; float: left;">
	<s:form name="frm" id="frmID" theme="simple" action="AddCollateral" method="POST" cssClass="formcss" enctype="multipart/form-data" >
		<s:hidden name="orgId"/>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table style="margin:10px 0px" class="table">
			<tr> 
				<td>Type :</td>  
				<td>
					<s:radio name="strCollateralType" id="strCollateralType"  list="#{'H':'Header','F':'Footer'}" value="%{'H'}" onclick="changeType(this.value);"/>
				</td>
			</tr> 
			<tr>
				<td>Name :</td>
				<td><s:textfield name="strCollateralName" cssClass="validateRequired"/></td>
			</tr>	   
						
			<tr id="imageTr">  
				<td>Image :</td>  
				<td><s:file name="strCollateralImg" id="strCollateralImg" cssClass="validateRequired"></s:file>
				<br/>40px X 510px is most optimized size. Please upload less than 15kb image. </td> <!-- 80px X 200px is optimized size. Please upload less than 15kb image.  --> 
			</tr>
			<tr id="imageAlignTr" style="display: none;"> 
				<td>Image Alignment :</td>  
				<td><s:radio name="imgAlign" id="imgAlign"  list="#{'L':'Left','C':'Center','R':'Right'}" value="%{'L'}"/></td>
			</tr>
			<tr id="textTr" style="display: none;">
				<td valign="top">Collateral Text :</td>
				<td><s:textarea name="strCollateralText" cols="25" rows="4" cssClass="validateRequired"></s:textarea></td>
			</tr> 
			<tr id="textAlignTr" style="display: none;"> 
				<td>Text Alignment :</td>  
				<td><s:radio name="textAlign" id="textAlign"  list="#{'L':'Left','C':'Center','R':'Right'}" value="%{'L'}"/></td>
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
<div  style="border-bottom: 1px solid #cccccc;clear: both;padding-top: 20px;"><h4>Collateral List</h4></div>

<ul class="level_list" style="padding-left: 20px;">
	<%
		int i = 0;
		for (; collateralList != null && !collateralList.isEmpty()
				&& i < collateralList.size(); i++) {
			Map<String, String> hmInner = collateralList.get(i);
			if (hmInner == null)
				hmInner = new HashMap<String, String>();
	%>
		<li>
			Name: <strong><%=hmInner.get("COLLATERAL_NAME")%></strong>&nbsp;(<%=hmInner.get("COLLATERAL_TYPE")%>)<br/>
			
		<table style="width: 100%;">
			<tbody>
				<tr>
					<%if(hmInner.get("COLLATERAL_IMG_ALIGN")!=null && hmInner.get("COLLATERAL_IMG_ALIGN").equals("R") && hmInner.get("COLLATERAL_PATH") != null && !hmInner.get("COLLATERAL_PATH").equals("")) { %>
						<%-- <td><%=hmInner.get("COLLATERAL_PATH")%></td>
						<td valign="middle" style="padding-left: 50px;"><%=hmInner.get("COLLATERAL_TEXT") %></td> --%>						
						<td width="70%" valign="middle" style="padding-left: 50px;"><%=hmInner.get("COLLATERAL_TEXT") %></td>
						<td align="right"><%=hmInner.get("COLLATERAL_PATH")%></td>
						<td valign="top">
							<a style="float: right;" title="Delete Collateral" href="AddCollateral.action?operation=D&collateralID=<%=hmInner.get("COLLATERAL_ID")%>&orgId=<%=request.getAttribute("orgId") %>" onclick="return confirm('Are you sure you want to delete this collateral?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a>
							<a style="float:right;" title="Edit" href="javascript:void(0);" onclick="editCollateral('<%=hmInner.get("COLLATERAL_ID")%>', '<%=request.getAttribute("orgId") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						</td>						
						
					<%} else if(hmInner.get("COLLATERAL_IMG_ALIGN")!=null && hmInner.get("COLLATERAL_IMG_ALIGN").equals("C") && hmInner.get("COLLATERAL_PATH") != null && !hmInner.get("COLLATERAL_PATH").equals("")) { %>
						<td colspan="2" align="center"><%=hmInner.get("COLLATERAL_PATH")%>
						<br/><%=hmInner.get("COLLATERAL_TEXT") %></td>
						<td valign="top">
							<a style="float: right;" title="Delete Collateral" href="AddCollateral.action?operation=D&collateralID=<%=hmInner.get("COLLATERAL_ID")%>&orgId=<%=request.getAttribute("orgId") %>" onclick="return confirm('Are you sure you want to delete this collateral?')"  style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a>
							<a style="float:right;" title="Edit" href="javascript:void(0);" onclick="editCollateral('<%=hmInner.get("COLLATERAL_ID")%>', '<%=request.getAttribute("orgId") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						</td>
						
					<%} else if(hmInner.get("COLLATERAL_IMG_ALIGN")!=null && hmInner.get("COLLATERAL_IMG_ALIGN").equals("L") && hmInner.get("COLLATERAL_PATH") != null && !hmInner.get("COLLATERAL_PATH").equals("")) { %>
						<%-- <td><%=hmInner.get("COLLATERAL_PATH")%></td>
						<td valign="middle" style="padding-left: 50px;"><%=hmInner.get("COLLATERAL_TEXT") %></td> --%>
						<td colspan="2"><%=hmInner.get("COLLATERAL_PATH")%></td>
						<td valign="top">
							<a style="float: right;" title="Delete Collateral" href="AddCollateral.action?operation=D&collateralID=<%=hmInner.get("COLLATERAL_ID")%>&orgId=<%=request.getAttribute("orgId") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this collateral?')"  style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a>
							<a style="float:right;" title="Edit" href="javascript:void(0);" onclick="editCollateral('<%=hmInner.get("COLLATERAL_ID")%>', '<%=request.getAttribute("orgId") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						</td>
						
					<%} else if(hmInner.get("COLLATERAL_TEXT_ALIGN")!=null && hmInner.get("COLLATERAL_TEXT_ALIGN").equals("R")) { %>
						<td colspan="2" align="right"><%=hmInner.get("COLLATERAL_TEXT") %></td>
						<td valign="top">
						<a style="float: right;" title="Delete Collateral" href="AddCollateral.action?operation=D&collateralID=<%=hmInner.get("COLLATERAL_ID")%>&orgId=<%=request.getAttribute("orgId") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this collateral?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a>
						<a style="float:right;" title="Edit" href="javascript:void(0);" onclick="editCollateral('<%=hmInner.get("COLLATERAL_ID")%>', '<%=request.getAttribute("orgId") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						</td>
						
					<%} else if(hmInner.get("COLLATERAL_TEXT_ALIGN")!=null && hmInner.get("COLLATERAL_TEXT_ALIGN").equals("C")) { %>
						<td colspan="2" align="center"><%=hmInner.get("COLLATERAL_TEXT") %></td>
						<td valign="top">
						<a style="float: right;" title="Delete Collateral" href="AddCollateral.action?operation=D&collateralID=<%=hmInner.get("COLLATERAL_ID")%>&orgId=<%=request.getAttribute("orgId") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this collateral?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a>
						<a style="float:right;" title="Edit" href="javascript:void(0);" onclick="editCollateral('<%=hmInner.get("COLLATERAL_ID")%>', '<%=request.getAttribute("orgId") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						</td>
						
					<%} else if(hmInner.get("COLLATERAL_TEXT_ALIGN")!=null && hmInner.get("COLLATERAL_TEXT_ALIGN").equals("L")) { %>
						<td colspan="2" align="left"><%=hmInner.get("COLLATERAL_TEXT") %></td>
						<td valign="top">
						<a style="float: right;" title="Delete Collateral" href="AddCollateral.action?operation=D&collateralID=<%=hmInner.get("COLLATERAL_ID")%>&orgId=<%=request.getAttribute("orgId") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this collateral?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a>
						<a style="float:right;" title="Edit" href="javascript:void(0);" onclick="editCollateral('<%=hmInner.get("COLLATERAL_ID")%>', '<%=request.getAttribute("orgId") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						</td>
						
					<%} %>
				</tr>
			</tbody>
		</table>
	</li>
	<%
		}
		if (i == 0) {
	%>
	 	<li><div class="msg nodata">No Data found</div></li>
	 <%
	 	}
	 %>
</ul>
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


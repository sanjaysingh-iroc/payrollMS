<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="com.konnect.jpms.select.FillUserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/customAjax.js" ></script>
<script>
function readImageURL(input, targetDiv) {
	//alert("notice targetDiv==>"+targetDiv);
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('#'+targetDiv)
            
                .attr('src', e.target.result)
                .width(60)
                .height(60);
        };
        reader.readAsDataURL(input.files[0]);
    }
}


function getLocationOrg(orgid){
	
	var action='GetLocationOrg.action?strOrg='+orgid ;
	getContent('locationdivid', action);
}

</script>
<%
String bookImgPath = (String) request.getAttribute("bookImgPath");
String bImage = (String) request.getAttribute("bImage");
String bookImage = (String) request.getAttribute("bookImage");
String operation = (String)request.getAttribute("operation");
String bookId = (String)request.getAttribute("bookId");

%>

<script>

function isOnlyNumberKey(evt){
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
	      return true;
	   }
	   return false;
	}
	
function checkQuantity() {
	var issuedQuantity = document.getElementById("issuedQuantity").value;
	var editQuantity = document.getElementById("strBook_quantity").value;
	
	if(parseInt(editQuantity) == 0) {
		alert("Invalid quantity!");
		document.getElementById("strBook_quantity").value ="";
	} else if(parseInt(issuedQuantity) > 0 && (parseInt(editQuantity) < parseInt(issuedQuantity))) {
		alert("Already Issued :"+issuedQuantity+" copies.\n Entered quantity should be greater than or equal to issued quantity!");
		document.getElementById("strBook_quantity").value =issuedQuantity;
	}
}
</script>

	<div id="printDiv" class="leftbox reportWidth">
			<s:form id="frmAddBook" name ="frmAddBook" action="AddBook" method="POST" theme="simple" cssClass="formcss" enctype="multipart/form-data">
				<s:hidden name="bookId" id="bookId"/>
				<s:hidden name="operation" id="operation"/>
				<s:hidden name="issuedQuantity" id="issuedQuantity"/>
					<table border="0" class="table table_no_border form-table" cellpadding="3" cellspacing="2" >
					    <tr>
							<td class="txtlabel alignRight">Select Organization:<sup>*</sup></td>
							<td>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssClass="validateRequired"  
									listValue="orgName" onchange="getLocationOrg(this.value);" list="orgList" />
							</td>
						</tr>	
						<tr>
							<td class="txtlabel alignRight">Select Location:<sup>*</sup></td>
							<td>
								<div id="locationdivid">
								<s:select cssClass="validateRequired" name="location" id="locationid" theme="simple" listKey="wLocationId" 
									listValue="wLocationName" headerKey="" headerValue="Select Location" list="wLocationList" />
								</div>
							</td>
						</tr>	
					    <tr>
							<td class="txtlabel alignRight">Title:<sup>*</sup></td>
							<td><s:textfield  name="strBookTitle" id="strBookTitle" cssClass="validateRequired" /></td>
						</tr>	
						
						 <tr>
							<td class="txtlabel alignRight">Author:<sup>*</sup></td>
							<td><s:textfield  name="strBookAuthor" id="strBookAuthor" cssClass="validateRequired" /></td>
						</tr>	
						
						<tr>
							<td class="txtlabel alignRight">Category:</td>
							<td><s:textfield  name="strBookCategory" id="strBookCategory" /></td>
						</tr>	
						 
						<tr>
							<td class="txtlabel alignRight">Publisher:</td>
							<td><s:textfield  name="strBookPublisher" id="strBookPublisher" /></td>
						</tr>
						
						 <tr>
							<td class="txtlabel alignRight">Published Year:</td>
							<td>
								<s:select name="strBook_year_published" listKey="yearsID" listValue="yearsName" headerKey="" 
							 		headerValue="Select Published Year" list="yearsList" key="" />
							</td>
						</tr>	
						
						 <tr>
							<td class="txtlabel alignRight">ISBN NO.:</td>
							<td><s:textfield name="strBook_isbn_no" id="strBook_isbn_no" /></td>
						</tr>	
						
						 <tr>
							<td class="txtlabel alignRight">Short Description:</td>
							<td><s:textarea rows="3" name="strBook_short_description" id="strBook_short_description" cssStyle="width: 205px;" /></td>
						 </tr>	
						
				         <tr>
							<td class="txtlabel alignRight">Quantity:<sup>*</sup></td>
							<td><s:textfield  name="strBook_quantity" id="strBook_quantity" cssClass="validateRequired validateNumber" onkeyup="checkQuantity()" onkeypress="return isOnlyNumberKey(event)" /></td>
						 </tr>	
						 
						  <tr>
							<td class="txtlabel alignRight">Cover:</td>
							<% if(bookImgPath!=null && !bookImgPath.equals("")) { %>
								<td>
									<div id="tblDiv" style="float: left;"><%=bookImage %></div>
									<div style="float: left; margin-left: 10px;  width: 66%;"><span style="float: left; width: 100%;">
										<input type="file" id="strBookImage" accept = ".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="strBookImage" size="5" style="height: 22px; margin-top: 10px; vertical-align: top; font-size: 11px;" onchange="readImageURL(this, 'bookImage');"></span>
										<span style="float: left; font-size: 11px;">Best size of cover is 300px X 300px</span>
									</div>
								</td>
							<% } else { %>
								<td>
									<img height="62" width="70" class="lazy" id="bookImage" style="float: left; border: 1px solid #CCCCCC;" src="userImages/book_avatar_photo.png" />
									<div style="float: left; margin-left: 10px;"><span style="float: left; width: 100%;">
										<input type="file" accept = ".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" id="strBookImage" name="strBookImage" size="5" style="font-size: 11px; height: 22px; margin-top:10px; vertical-align: top;" onchange="readImageURL(this, 'bookImage');"></span>
										<span style="float: left; font-size: 11px;">Best size of cover is 300px X 300px</span>
									</div>
								</td>
							<% } %>
						</tr>	

						 <tr>
						 	<td colspan="2" align="center">
						 	<%if(operation !=null && operation.equals("E")) { %>
						 		<s:submit name="strUpdate" cssClass="btn btn-primary" value="Update" align="center" />
						 		<!-- <input type="button" class="btn btn-danger" name="strCancel" onclick="closeEditBookPopup();" value="Cancel"> -->
						 	<% } else { %>
						 		<s:submit name="strSubmit" cssClass="btn btn-primary" value="Submit" align="center" />
						 		<!-- <input type="button" class="btn btn-danger" name="strCancel" onclick="closeAddBookPopup();" value="Cancel"> -->
						 	<% } %>
						 	</td>
						 </tr>
					  </table>
			</s:form>
	</div>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>	
<script>
//$("img.lazy2").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
//$("img.lazy2").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

	$(".btn-danger").click(function(){$("#modalInfo").hide();});
	$('#frmAddBook_strSubmit').click(function(){
		$(".validateRequired").prop('required',true);
	});
	$('#frmAddBook_strUpdate').click(function(){
		$(".validateRequired").prop('required',true);
	});
	$(document).ready(function(){
		$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');
	});
    //var timeout = setTimeout(function() { $("img.lazy2").trigger("sporty") }, 1000);

</script>
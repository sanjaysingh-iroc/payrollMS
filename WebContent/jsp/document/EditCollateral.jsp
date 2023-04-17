
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
$(document).ready(function() { 
	$("#btnAddNewRowOk").click(function(){
		  $("#frmID").find('.validateRequired').filter(':hidden').prop('required',false);
	      $("#frmID").find('.validateRequired').filter(':visible').prop('required',true);
	});
});

function changeType(value) {
	
	//var value = document.getElementById("trainingSchedulePeriod").value;
	
	// Created by Dattatray
	if (value == 'F' || value == 'H') {
		//imageTr imageAlignTr textAlignTr
		/* document.getElementById("EdimageTr").style.display = "none";
		document.getElementById("EdimageAlignTr").style.display = "none";
		document.getElementById("EdtextAlignTr").style.display = "none";
		document.getElementById("EdtextTr").style.display = "table-row"; */
		
		document.getElementById("EdimageTr").style.display = "table-row";
		document.getElementById("EdimageAlignTr").style.display = "none";
		document.getElementById("EdtextAlignTr").style.display = "none";
		document.getElementById("EdtextTr").style.display = "none";
	}
	/* else {
		document.getElementById("EdimageTr").style.display = "table-row";
		document.getElementById("EdimageAlignTr").style.display = "none";
		document.getElementById("EdtextAlignTr").style.display = "none";
		document.getElementById("EdtextTr").style.display = "none";
	} */
}

</script>

<div style="width: 100%; float: left;">
	<s:form name="frm" id="frmID" theme="simple" action="EditCollateral" method="POST" cssClass="formcss" enctype="multipart/form-data" >
		<s:hidden name="orgId"/>
		<s:hidden name="collateralID"/>
		<s:hidden name="strImageName"/>
		<s:hidden name="operation" value="U"/>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table style="float:left; margin:10px 0px" class="table" > 
			<tr>
				<td>Name:</td>
				<td><s:textfield name="strCollateralName" cssClass="validateRequired"/></td>
			</tr>
			<tr>
				<td>Type:</td>  
				<td>
				<%
				String strCollateralTypeValue = (String) request.getAttribute("strCollateralTypeValue");
				String collTypeHeader ="";
				String collTypeFooter ="";
				
				if(strCollateralTypeValue != null && strCollateralTypeValue.equals("H")) { 
					collTypeHeader ="checked";
				} else if(strCollateralTypeValue != null && strCollateralTypeValue.equals("F")) {
					collTypeFooter ="checked";
				}
				%>

				
				<input type="radio" name="strCollateralType" id="strCollateralType" value="H" <%=collTypeHeader %> onclick="changeType('H');"/>Header &nbsp;
				<input type="radio" name="strCollateralType" id="strCollateralType" value="F" <%=collTypeFooter %> onclick="changeType('F');"/>Footer &nbsp;
				
				<%-- <s:radio name="strCollateralType" id="strCollateralType"  list="#{'H':'Header','F':'Footer'}" value="%{strCollateralTypeValue}"/> --%>
				</td>
			</tr>
			<tr id="EdimageTr" >
				<td>Image :</td>  
				<td><s:file name="strCollateralImg" id="strCollateralImg" cssClass="validateRequired"></s:file>
				<br/>40px X 510px is most optimized size. Please upload less than 15kb image. </td> 
			</tr>
			<tr id="EdimageAlignTr" style="display: none;">
				<td>Image Alignment :</td> 
				<td>
				<%
				String imgAlignValue = (String) request.getAttribute("imgAlignValue");
				String imgLeft ="";
				String imgCenter ="";
				String imgRight ="";
				
				if(imgAlignValue != null && imgAlignValue.equals("L")) {
					imgLeft ="checked";
				} else if(imgAlignValue != null && imgAlignValue.equals("C")) {
					imgCenter ="checked";
				} else if(imgAlignValue != null && imgAlignValue.equals("R")) {
					imgRight ="checked";
				}
				%>
				<input type="radio" name="imgAlign" id="imgAlign" value="L" <%=imgLeft %>/>Left &nbsp;
				<input type="radio" name="imgAlign" id="imgAlign" value="C" <%=imgCenter %>/>Center &nbsp;
				<input type="radio" name="imgAlign" id="imgAlign" value="R" <%=imgRight %>/>Right &nbsp;
				<%-- <s:radio name="imgAlign" id="imgAlign"  list="#{'L':'Left','C':'Center','R':'Right'}" value="%{imgAlignValue}"/> --%>
				</td>
			</tr>
			<tr id="EdtextTr" style="display: none;">
				<td valign="top">Collateral Text :</td>
				<td><s:textarea name="strCollateralText" cols="25" rows="4" cssClass="validateRequired"></s:textarea></td>
			</tr>
			<tr id="EdtextAlignTr" style="display: none;">
				<td>Text Alignment :</td>  
				<td>
				<%
				String textAlignValue = (String) request.getAttribute("textAlignValue");
				String textLeft ="";
				String textCenter ="";
				String textRight ="";
				
				if(textAlignValue != null && textAlignValue.equals("L")) { 
					textLeft ="checked";
				} else if(textAlignValue != null && textAlignValue.equals("C")) {
					textCenter ="checked";
				} else if(textAlignValue != null && textAlignValue.equals("R")) {
					textRight ="checked";
				}
				%>
				<input type="radio" name="textAlign" id="textAlign" value="L" <%=textLeft %>/>Left &nbsp;
				<input type="radio" name="textAlign" id="textAlign" value="C" <%=textCenter %>/>Center &nbsp;
				<input type="radio" name="textAlign" id="textAlign" value="R" <%=textRight %>/>Right &nbsp;
				<%-- <s:radio name="textAlign" id="textAlign"  list="#{'L':'Left','C':'Center','R':'Right'}" value="%{textAlignValue}"/> --%>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<s:submit value="Save" name="submit" id="btnAddNewRowOk" cssClass="btn btn-primary"/>
				</td>
			</tr>
		</table>
	</s:form>
</div>

<script>

//as its file upload we cant perform ajax

	 //$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});   
</script>

<script>
changeType('<%=(String) request.getAttribute("strCollateralTypeValue") %>');
</script>


<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script>
           
     
	function offerRejectOrAccept(type, reject) {
		var strAction = 'Offers.action';
		var strMsg = 'Are you sure, to accept this offer?';
		if(type == 'reject') {
			strMsg = 'Are you sure, to reject this offer?';
		}
		if(reject == 'CBO') {
			strMsg = 'Are you sure, to candidate back out offer?';
		}
		if(reject == 'HOLD') {
			strAction = 'Applications.action';
			strMsg = 'Are you sure, to hold offer of this candidate?';
		}
		if(confirm(''+strMsg)) {
		
		    $("#frm_OfferAcceptAndRenegotiate").submit(function(event){
		  		event.preventDefault();
		  		
		  		var recruitId = document.getElementById("recruitId").value;
		  		var form_data = $("#frm_OfferAcceptAndRenegotiate").serialize();
				$.ajax({
					type :'POST',
					url  :'OfferAcceptAndRenegotiate.action',
					data :form_data+'&rejectStatus='+type,
					cache:true/* ,
					success : function(result) {
						$("#subSubDivResult").html(result);
					} */
				});
				
				$.ajax({
					url: strAction+'?recruitId='+recruitId,
					cache: true,
					success: function(result){
						$("#subSubDivResult").html(result);
			   		}
				});
		  	});
		}
	}      
</script>

<div id="offerAcccept">

<title>Offer Accept/Reject Remark</title>

<% String rejectType = (String) request.getAttribute("rejectType");
	UtilityFunctions uF = new UtilityFunctions();
%>
<form id="frm_OfferAcceptAndRenegotiate" name="frm_OfferAcceptAndRenegotiate" action="OfferAcceptAndRenegotiate.action" method="post">
	<s:hidden name="candidateID" />
	<s:hidden name="recruitId" id="recruitId" />
	<s:hidden name="updateRemark" value="Update"/>
	<s:hidden name="rejectType" id="rejectType"/>
	<% if(rejectType != null && rejectType.equalsIgnoreCase("CANDIBACKOUT")) { %>
		<div>
			<span style="vertical-align: top;">Remark:</span>
			<textarea id="offerBackoutRemark" name="offerBackoutRemark" rows="2" cols="45"> </textarea>
		</div>
		<div style="margin: 10px;">
			<input type="submit" value="Offer Reject" class="btn btn-danger" name="reject" onclick="offerRejectOrAccept('reject', 'CBO');">
		</div>
	<% } else if(rejectType != null && rejectType.equalsIgnoreCase("CANDIONHOLD")) { %>
		<div>
			<span style="vertical-align: top;">Remark:</span>
			<textarea id="offerOnHoldRemark" name="offerOnHoldRemark" rows="2" cols="45"> </textarea>
		</div>
		<div style="margin: 10px;">
			<input type="submit" value="Offer Hold" class="btn btn-danger" name="reject" onclick="offerRejectOrAccept('hold', 'HOLD');">
		</div>
	<% } else if(rejectType != null && rejectType.equalsIgnoreCase("CANDIONHOLD_REASON")) { %>
		<div>
			<span style="vertical-align: top;">Remark:</span>
			<span style="vertical-align: top; margin-left: 10px;"><%=uF.showData((String)request.getAttribute("offerOnHoldRemark"), "-") %></span>
		</div>
		<div style="margin: 10px; text-align: right;">
			<I>Hold Date: <%=uF.showData((String)request.getAttribute("offerHoldDate"), "-") %></I>
		</div>
	<% } else { %>
		<div>
			<span style="vertical-align: top;">Remark:</span>
			<textarea id="offerAcceptRemark" name="offerAcceptRemark" rows="2" cols="45"> </textarea>
		</div>
		<div style="margin: 10px;">
			<input type="submit" class="btn btn-primary" name="update" value="Offer Accept" onclick="offerRejectOrAccept('', '');"/>&nbsp;&nbsp;
			<input type="submit" value="Offer Reject" class="btn btn-danger" name="reject" onclick="offerRejectOrAccept('reject', '');">
		</div>
	<% } %>
</form>
</div>

<%@taglib prefix="s" uri="/struts-tags" %>
<script>
    function setMailOptions(isSendMail){
    	
    	if(isSendMail.checked){
    		document.getElementById("row1").style.display = "table-row";
    		document.getElementById("row2").style.display = "table-row";
    		//document.getElementById("row3").style.display = "table-row";
    		document.getElementById("row4").style.display = "block";
    	}else{
    		document.getElementById("row1").style.display = "none";
    		document.getElementById("row2").style.display = "none";
    		//document.getElementById("row3").style.display = "none";
    		document.getElementById("row4").style.display = "none";
    	}
    	
    }
</script>
<div class="cat_heading">
    <h4>Send Document:</h4>
</div>
<s:form name="" theme="simple" action="SendMail">
    <table class="table table_no_border form-table">
        <s:hidden name="emp_id"/>
        <s:hidden name="orgId"/>
        <tr>
            <td>Send Document:</td>
            <td>
                <s:select list="documentList" name="strDocument" listKey="documentId" listValue="documentName" headerKey="0" headerValue="Choose Document"></s:select>
            </td>
        </tr>
        <tr>
            <td>Send Mail:</td>
            <td>
                <s:checkbox name="isSendMail" onclick="setMailOptions(this);"></s:checkbox>
            </td>
        </tr>
        <tr id="row1" style="display: none">
            <td>Subject:</td>
            <td>
                <s:textfield name="strSubject"/>
            </td>
        </tr>
        <tr id="row2" style="display: none">
            <td>Mail Body:</td>
            <td>
                <s:textarea name="strMailBody" rows="10" cols="5"> </s:textarea>
            </td>
            <td style="display:none;" id="row4">
            	[EMPCODE]<br/>
		        [EMPFNAME]<br/>
		        [EMPLNAME]<br/>
		        [JOINING_DATE]<br/>
		        [DATE]<br/>
		        [EMP_CTC]<br/>
		        [DESIGNATION]<br/>
		        [LEVEL]<br/>
		        [GRADE]<br/>
		        [WLOCATION]<br/>
		        <br/><br/>
            </td>
        </tr>
        <tr>
        	<td></td>
            <td>
                <s:submit value="Send Document" cssClass="btn btn-primary"></s:submit>
            </td>
        </tr>
    </table>
   
</s:form>
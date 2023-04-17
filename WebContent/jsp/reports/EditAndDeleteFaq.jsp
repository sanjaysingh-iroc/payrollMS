<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>

$(function(){
    $("body").on("click","#btnUpdate", function() {
    	$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
    });
    
});
    
function selectElements(strSectionId) {
	if( strSectionId == 0) {
		document.getElementById("otherSectionTR").style.display = "table-row";
	} else {
		document.getElementById("otherSectionTR").style.display = "none";
	}
}
		
$("#frm_FAQ").submit(function(e) {
	e.preventDefault();
	var form_data = $("form[name='frm_FAQ']").serialize();
 	$.ajax({
 		type: 'POST',
		url : "EditAndDeleteFaq.action",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		}, 
		error : function(err) {
			$.ajax({ 
				url: 'AddUpdateViewHubContent.action?type=FAQ',
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
	});
 	$("#modalInfo").hide();
});

    
</script>
<%
  
    UtilityFunctions uF = new UtilityFunctions();
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    String strUserType =  ((String)session.getAttribute(IConstants.USERTYPE));
    
    %>
<div style="float: left; width: 100%; padding-bottom: 15px;">
    <s:form name="frm_FAQ" id="frm_FAQ" action="EditAndDeleteFaq" theme="simple" method="Post" cssClass="formcss" enctype="multipart/form-data">
    <!-- <form name="frm_editDeleteFaq" id="frm_editDeleteFaq" action="EditAndDeleteFaq"> -->
	    <s:hidden name="operation" id="operation" />
	    <s:hidden name="faqId" id="faqId" />
        <div style="float: left; width: 100%;">
            <div>
                <table class="table form-table table_no_border">
                  	<tr>
                  		 <td>Section:<sup>*</sup></td>
                  		 <td>
                             <s:select name="strfaqSection" id="strfaqSection" listKey="sectionId" theme="simple" cssClass="validateRequired" listValue="sectionName" headerKey="" 
                               headerValue="Select Section" list="faqSectionList" key="" onchange="selectElements(this.value)" />
                               <span class="hint">Select section from the list.<span class="hint-pointer">&nbsp;</span></span>
                        </td>
                  	
                  	</tr>
                  	 <tr id ="otherSectionTR" style="display: none">
                        <td>Section Name:<sup>*</sup></td>
                          <td><input type ="text" name="strSection" id="strSection" class="validateRequired" value = ""/></td>
                    </tr>
                    <tr>
                        <td>Question:<sup>*</sup></td>
                          <td><input type ="text" name="strQuestion" id="strQuestion" class="validateRequired" value = "<%=uF.showData((String)request.getAttribute("faq_question"), "")%>"/></td>
                    </tr>
                    <tr>
                        <td>Answer:<sup>*</sup></td>
	                   <td><textarea rows="3" name="strAnswer" id="strAnswer" class="validateRequired" ><%=uF.showData((String)request.getAttribute("faq_answer"), "")%></textarea></td>
                   </tr>
                   <tr>
                        <td></td>
                        <td colspan="2" align="center"><s:submit name="btnUpdate" id="btnUpdate" cssClass="btn btn-primary" value="Submit"></s:submit></td>
                    </tr>
                </table>
            </div>
        </div>
    <!-- </form> -->
    </s:form>
</div>

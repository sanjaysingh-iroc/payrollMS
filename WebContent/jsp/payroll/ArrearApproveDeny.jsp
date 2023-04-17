<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script src="scripts/customAjax.js"></script>
<script>
    $(function() {
        /* $( "#effectiveDate").datepicker({format: 'dd/mm/yyyy'}); */
        
        $("body").on("click", "#arrearSubmit", function() { 
        	$('.validateRequired').filter(':hidden').prop('required',false);
			$('.validateRequired').filter(':visible').prop('required',true);
	    });
	    
    });
    
    
    $("#frmArrearApproveDeny").submit(function(e) {
		e.preventDefault();
		var form_data = $("form[name='frmArrearApproveDeny']").serialize();
		//alert("form_data ===>> " + form_data);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
			type : 'POST',
			url : "ArrearApproveDeny.action",
			data: form_data,
			cache : false,
			success : function(res) {
				$("#divResult").html(res);
			},
			error : function(res) {
				$.ajax({
					url: 'PayArrears.action',
					cache: true,
					success: function(result){
						$("#divResult").html(result);
			   		}
				});
			}
		});
	});
    
    
</script>

    <div class="row row_without_margin"> 
    	<s:form name="frmArrearApproveDeny" action="ArrearApproveDeny" theme="simple" method="post" id="frmArrearApproveDeny">
	    	<div class="col-lg-12">
		        <s:hidden name="arear_id" />
		        <s:hidden name="actionType" />
		        <s:hidden name="operation" value="U" />
		        <table class="table table_no_border form-table">
		            <tr>
		                <td>Comment:<sup>*</sup> </td>
		                <td><s:textarea name="strApproveDenyComment" cssClass="validateRequired" /></td>
		            </tr>
		            <tr>
		            	<td>&nbsp;</td>
		                <td><input class="btn btn-primary" id="arrearSubmit" type="submit" value="Submit"/></td>
		            </tr>
		        </table>
	    	</div>
    	</s:form>
    </div>
    

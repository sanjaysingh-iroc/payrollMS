<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<section class="content">
	<div class="row jscroll">
        <section class="connectedSortable">
	 		<div class="col-md-12">
       	   		<div class="box box-primary">
       	   			<div class="box-body">
       		  			<div class="active tab-pane" id="divResult" style="min-height: 600px;">
						
			  			</div>
			 		 </div>
       	   		</div>
       	   	</div>
       	</section>
    </div>
</section>    

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		getEmailDashboardData('InboxDashboard');
	});
	
	function getEmailDashboardData(strAction) {
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: strAction+'.action',
			cache: true,
			success: function(result) {
				//alert("result1==>"+result);
				$("#divResult").html(result);
	   		}
		});
	}

</script>
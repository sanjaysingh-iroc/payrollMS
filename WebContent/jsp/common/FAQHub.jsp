
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
.collapsible {
 
  color: blue;
  cursor: pointer;
  padding: 18px;
  width: 100%;
  border: none;
  text-align: left;
  outline: none;
  font-size: 15px;
}

.active, .collapsible:hover {
  background-color: #555;
}

.content {
  padding: 0 18px;
  display: none;
  overflow: hidden;
  background-color: #f1f1f1;
}
</style>
</head>
<script>


	$("#formID").submit(function(event){
		event.preventDefault();
		var form_data = $("#formID").serialize();
		var divResult = 'divResult';
		$.ajax({
			type :'POST',
			url  :'FAQHub.action',
			data :form_data+"&strInsert="+strInsert,
			cache:true
		});
		
		$.ajax({
			url: 'RequirementApproval.action?f_org='+orgID+'&location1='+wlocID+'&designation='+desigID+'&checkStatus='+checkStatus
					+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType,
			cache: true,
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	});
	
	

 	 function updateFaq()
    			    {
 		 				//alert(update);
    				  var operation= "edit";
    			      var strQuestion = document.getElementById("strQuestion").value;
    				  var strAnswer = document.getElementById("strAnswer").value;
    				  var url1=  'FAQHub.action?strQuestion='+strQuestion+'&strAnswer='+strAnswer+'operation='+operation;
    				  //alert(url);
					  var xhr = $.ajax({
	    	                url : 'FAQHub.action?strQuestion='+strQuestion+'&strAnswer='+strAnswer+'operation='+operation,
	    	                cache : false,
	    	                success : function(data) {
	    	                		document.getElementById("mainEventDiv_"+eventId).innerHTML = '';
	    	               }
						});
    			     }
</script>
    			 
<body>
	 <div id = "FAQ" style="float:left; width:99%; margin:10px 2px 1px 7px;">
		  <div class="modal fade" id="addFAQ" role="dialog">
             <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
					 <div class="modal-dialog">
					   <!-- Modal content-->
					  <div class="modal-content">
						 <div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
							<h4 class="modal-title">Add FAQ</h4>
							</div>
						   <div class="modal-body">
							 <s:form name="frm_FAQ" id = "frm_FAQ" action="FAQHub" theme="simple" method="Post" cssClass="formcss" enctype="multipart/form-data">
	                               <table class="table table_no_border form-table">
	                                 <tr>
	                                 <td>Question:<sup>*</sup></td>
	                                  <td><s:textfield  name="strQuestion" id="strQuestion" cssClass="validateRequired" ></s:textfield></td>
	                                   </tr>
	                                   <tr>
	                                   <td>Answer:<sup>*</sup></td>
	                                    <td><s:textarea rows="3" name="strAnswer" id="strAnswer" cssClass="validateRequired" ></s:textarea></td>
	                                    </tr>
	                                    <tr><td colspan="2" align="center"><s:submit name="update" cssClass="btn btn-primary" value="Update"/></td>        	
	                                     </table>
	                                     
		                                  </s:form>
									      </div>
									      <div class="modal-footer">
									          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
									        </div>
									      </div>
									      
									    </div>
									</div>
		 </div>
                                


</body>
</html>
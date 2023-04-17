<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script type="text/javascript">
    $(function() {
    	checkTrainer('1');
    });
     
    function checkTrainer(val){
    	
    	if(val=='1'){
    		//document.getElementById("empFilterID").style.display="table-row"; 
    		document.getElementById("ExtSubmit").style.display="none";
    		document.getElementById("empidlistOuter").style.display="block";
    		document.getElementById("submit").value="Submit & Proceed";
    	}else{
    		//document.getElementById("empFilterID").style.display="none"; 
    		document.getElementById("ExtSubmit").style.display="block";
    		document.getElementById("empidlistOuter").style.display="none";
    		document.getElementById("submit1").value="Go To Next Page";
    	} 
    }
    
    function checkUncheckValue() {
    	var allEmp=document.getElementById("allEmp");
    	var strTrainerId = document.getElementsByName('strTrainerId');
    	if(allEmp.checked==true){
    		 for(var i=0;i<strTrainerId.length;i++){
    			  strTrainerId[i].checked = true;
    		 }
    	}else{		
    		 for(var i=0;i<strTrainerId.length;i++){
    			  strTrainerId[i].checked = false;
    		 }
    		 
    	}	 
    }
    
</script>

<%
    UtilityFunctions uF = new UtilityFunctions();
    List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
    %>
<script type="text/javascript">

function openPanelEmpProfilePopup(empId) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Employee Information');
	 if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	 }
	 $.ajax({
			url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}; 
$(function(){
	$("body").on('click','#closeButton',function(){
		$(".modal-body").height(400);
		$(".modal-dialog").removeAttr('style');
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function(){
		$(".modal-body").height(400);
		$(".modal-dialog").removeAttr('style');
		$("#modalInfo").hide();
	});
});    
</script>
<%String fromPage = (String)request.getAttribute("fromPage");%>
<section class="content">
	<div class="row jscroll">
		 <section class="col-lg-12 connectedSortable">
		 	<div class="box box-primary">
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                    <div class="leftbox reportWidth">
					    <s:form name="frm_CheckTrainer" action="TrainerCheck" id="frm_CheckTrainer" theme="simple" method="post">
					    	<input type="hidden" name="fromPage" id ="fromPage" value="<%=fromPage %>"/>
					        <div style="float: left; width: 100%">
					            <table id="tableID" class="table_style" style="width: 60%; margin-left: 20%;">
					                <tbody>
					                    <tr>
					                        <td style="text-align: right;" class="txtlabel">Trainer:</td>
					                        <td colspan="4">
					                            <s:radio list="#{'1':'Internal Trainer','2':'External Trainer'}" 
					                                name="trainerType" id="trainerType" onclick="checkTrainer(this.value);" value="1"></s:radio>
					                        </td>
					                    </tr>
					                </tbody>
					            </table>
					        </div>
					        <div id="empidlistOuter">
					        	<s:action name="GetEmpListForTrainer" executeResult="true">
					        		<s:param name="fromPage"><%=fromPage%></s:param>
					        	</s:action>
					         <%--<iframe id="iframe" style=" width:100%; height: 500px;" src="GetEmpListForTrainer.action?fromPage=<%=fromPage%>" frameborder="0"  name="myone"></iframe> --%>
					        </div>
					        <div id="ExtSubmit" style="display: none; width: 100%; float: left; text-align: center;">
					            <s:submit cssClass="btn btn-primary" value="Submit & Proceed" name="submit" id="submit1"/>
					        </div>
					    </s:form>
					</div>
                </div>
                <!-- /.box-body -->
            </div>
		 </section>
	</div>
</section>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<script>
 <%if(fromPage != null && fromPage.equals("LD")) { %>
	 $('#submit1').click(function(event){
		 event.preventDefault();
		 var from = document.getElementById("fromPage").value;
		 var trainerType = document.getElementById("trainerType").value;
		 var form_data = $('#frm_CheckTrainer').serialize();
		 var submitB = $("#submit1").val()+"";
		 $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 if(submitB=='Go To Next Page'){
			 $.ajax({
				 type :'POST',
				 url :'AddTrainer.action',
				 data:form_data,
				 cache:true,
				 success:function(result){
					$("#divResult").html(result);
				 },
				 error: function(result) {
					 $("#divResult").html(result);
				}
			 });
		 } else{
			 $.ajax({
				 type :'POST',
				 url :'TrainerCheck.action',
				 data:form_data,
				 cache:true,
				 success:function(result){
					$("#divResult").html(result);
				 },
				 error: function(result) {
					 $("#divResult").html(result);
				}
			 });
		 }
		 /* $.ajax({
			 type :'POST',
			 url :'TrainerCheck.action',
			 data:form_data,
			 cache:true,
			 success:function(result){
				$("#divResult").html(result);
			 },
			 error: function(result) {
				 $("#divResult").html(result);
			}
		 }); */
	 });
	 
 <% }%>
 </script>
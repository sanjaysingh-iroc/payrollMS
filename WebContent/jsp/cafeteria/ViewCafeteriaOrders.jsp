<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>



<script type="text/javascript">
$(function () {
	$('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});
});

</script>

<script>


function confirmDishOrder(dishId, orderId) {
	
	if(confirm('Are you sure, you wish to confirm this order?')) {
		var xmlhttp;
		if (window.XMLHttpRequest) {
	        // code for IE7+, Firefox, Chrome, Opera, Safari
	        xmlhttp = new XMLHttpRequest();
		}
	    if (window.ActiveXObject) {
	        // code for IE6, IE5
	    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	    }

	    if (xmlhttp == null) {
            alert("Browser does not support HTTP Request");
            return;
	    } else {
            var xhr = $.ajax({
                url : 'ConfirmDishOrder.action?dishId='+dishId+'&orderId='+orderId,
                cache : false,
                success : function(data) {
                	
					if(data = 'Confirmed') {
						   document.getElementById("check_"+orderId).innerHTML ="";
			        	   document.getElementById("confirm_"+orderId).innerHTML = data;        	
			        	   if(!document.getElementById("strConfirm")) {
			        		   
			        		   document.getElementById("checkAllDiv").innerHTML ="";
			        		   document.getElementById("unSendSpan").style.display = 'block';
			       			   document.getElementById("sendSpan").style.display = 'none';
			        	   }
			           document.getElementById("sendAll").checked = false;   
					}
                }
            });
	    }
	}
}

   /* function confirmDishOrder(dishId, orderId) {
	  var xhr = $.ajax({
           url : 'ConfirmDishOrder.action?dishId='+dishId+'&orderId='+orderId,
           cache : false,
           success : function(data) {
        	   document.getElementById("check_"+orderId).innerHTML="";
        	   document.getElementById("confirm_"+orderId).innerHTML=data;        	
        	   if(!document.getElementById("strConfirm")) {
        		   document.getElementById("checkAllDiv").innerHTML="";
        		   document.getElementById("unSendSpan").style.display='block';
       			   document.getElementById("sendSpan").style.display='none';
        	   }
        	   document.getElementById("sendAll").checked=false;
           }
       });
   } */
   
   
   function selectall(x,strEmpId) {
	  
		var  status=x.checked; 
		var  arr= document.getElementsByName(strEmpId);
		for(i=0;i<arr.length;i++) {
			
	  		arr[i].checked=status;
	 	}
		
		if(x.checked == true) {
			document.getElementById("unSendSpan").style.display = 'none';
			document.getElementById("sendSpan").style.display = 'block';
		} else {
			document.getElementById("unSendSpan").style.display = 'block';
			document.getElementById("sendSpan").style.display = 'none';
		}
	}
	
	function checkAll() {
		var sendAll = document.getElementById("sendAll");		
		var strConfirm = document.getElementsByName('strConfirm');
		var cnt = 0;
		var chkCnt = 0;
		
		for(var i=0;i<strConfirm.length;i++) {
			cnt++;
			if(strConfirm[i].checked) {
				 chkCnt++;
			}
		 }
	
		if(parseFloat(chkCnt) > 0) {
			document.getElementById("unSendSpan").style.display = 'none';
			document.getElementById("sendSpan").style.display = 'block';
		} else {
			document.getElementById("unSendSpan").style.display = 'block';
			document.getElementById("sendSpan").style.display = 'none';
		}
		
		if(cnt == chkCnt) {
			sendAll.checked = true;
		} else {
			sendAll.checked = false;
		}
	}

</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, List<String>> hmDishOrders = (Map<String, List<String>>)request.getAttribute("hmDishOrders");
	if(hmDishOrders == null ) hmDishOrders = new HashMap<String, List<String>>();
    String dishId = (String)request.getAttribute("dishId");
  
%>

<div>	
	<s:form id = "frmViewCafeteriaOrders" name="frmViewCafeteriaOrders" action="ViewCafeteriaOrders" method = "POST" theme ="simple">
		<s:hidden name="dishId"/>
			<div class="add_delete_toolbar"></div>
			<div style="margin-bottom:20px;">
				<span id="unSendSpan" style="display: none;">
					<input type="button" name="unSend" class="input_reset" value="Confirm Orders" />
				</span>
				<span id="sendSpan">
					<input type="submit" value="Confirm Orders" name="strConfirmOrders" class="btn btn-primary" onclick="return confirm('Are you sure, you want to Confirm this orders?')"/>
				</span>
		  </div>				
		
		   <table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<th align="left"><div id="checkAllDiv"><input type="checkbox" name="sendAll" id="sendAll" onclick="selectall(this,'strConfirm')" checked="checked"/></div></th>
					<th style="text-align: left;">Employee</th>
					<th style="text-align: left;">Order by</th>
					<th style="text-align: left;">Quantity</th>
					<th style="text-align: left;">Ordered On</th>
					<th style="text-align: left;">Confirm</th>
					
				</tr>
			</thead>
			<tbody>
			<% if(hmDishOrders != null && hmDishOrders.size() >0){
				 Set orderSet = hmDishOrders.keySet();
				 Iterator<String> it = orderSet.iterator();
				 while(it.hasNext()) {
				   String orderId = it.next();
				   List<String> dishDetailsList = (List<String>) hmDishOrders.get(orderId);
				   if(dishDetailsList == null) dishDetailsList = new ArrayList<String>();
				   if(dishDetailsList != null && dishDetailsList.size() > 0) {
				%> 
			 		
					<tr id = "mainDiv_<%=orderId %>" >
						<td><div id="check_<%=orderId %>"><input type="checkbox" name="strConfirm" id="strConfirm" value="<%=dishDetailsList.get(0)%>" onclick="checkAll();" checked/></div></td>
						<td><%=dishDetailsList.get(6) %></td>
						<td><%=dishDetailsList.get(5) %></td>
						<td><%=dishDetailsList.get(3)%></td>
						<td><%=dishDetailsList.get(4)%></td>
						<td id="confirm_<%=orderId%>"><input type="button" class="btn btn-primary" style="width: 65px;float:left;" value="Confirm" align="center"  onclick="confirmDishOrder('<%=dishDetailsList.get(1)%>','<%=dishDetailsList.get(0)%>');"/> </td>
					</tr>
				<% 
					}
				  }
				}%>
			</tbody>
		</table>
	</s:form>
</div>
	



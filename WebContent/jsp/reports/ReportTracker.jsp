<%@ taglib prefix="s" uri="/struts-tags" %>
	<title>Demo 2: Drag and drop</title>
	
	<link rel="stylesheet" href="<%= request.getContextPath()%>/css/demos.css" media="screen" type="text/css">
	<style type="text/css">
	/* CSS for the demo. CSS needed for the scripts are loaded dynamically by the scripts */
	#mainContainer{
		width:600px;
		margin:0 auto;
		margin-top:10px;
		border:1px solid #000;
		padding:2px;
	}
	
	.leftColumn{
		width:150px;
		border:1px solid #868686;
		box-shadow: 2px 1px 7px #AAAAAA;
		background-color:#E2EBED;
		padding-bottom:25px;
		height:175px;
	}
	#rightColumn{
		width:200px;
		float:right;
		margin:2px;
		height:400px;
	}	
	.dragableBox{
		width:120px;
		height:10px;
		border:1px solid #666666;
		background-color:#68AC3B;	
		float:left;
		margin-bottom:2px;
		margin-top:3px;
		padding:3px 0 13px;
		font-weight:bold;
		font-size:12px;
		text-align:center;
		margin-left:13px;
		box-shadow:0 3px 3px #919191;
	}
	.dropBox{
		width:700px;
		border:1px solid #666666;
		background-color:#E2EBED;
		height:35px;
		margin-bottom:10px;
		padding:3px;
		overflow:auto;
		box-shadow:0 3px 3px #919191;
	}		
	a{
		color:#F00;
	}
		
	.clear{
		clear:both;
	}
	img{
		border:0px;
	}	
	</style>	
	
	<script type="text/javascript" src="<%= request.getContextPath()%>/js/drag-drop-custom.js"></script>
	
 	<script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery-1.6.2.min.js"></script>
 	<script type="text/javascript" src="<%= request.getContextPath()%>/js/jquery-ui-1.8.16.custom.min.js"></script>
 	<link type="text/css" href="<%= request.getContextPath()%>/css/jquery-ui-1.8.16.custom.css" rel="stylesheet" />
	<script type="text/javascript">
$(document).ready(function() {
		$('#container').tabs({
			fxAutoHeight : true
		});
});
</script>
<div id="printDiv" class="leftbox reportWidth" >

	
		
		<div style="float: left; margin: 10px; ">
		<div id="measure" class="leftColumn">
			<div  style="text-align: center; background-color: #DDDDDD; font-size: 16px;border-bottom:1px solid #868686;padding:6px;color:#444444;"><p><b>Measures</b></p></div>
			<div id="dropContent">
				<div class="dragableBox" id="amount"><div style="float: left; text-align: center; width: 78%;">amount</div></div>
				<div class="dragableBox" id="box2"><div style="float: left; text-align: center; width: 78%;">DOG</div></div>
				<div class="dragableBox" id="box3"><div style="float: left; text-align: center; width: 78%;">HORSE</div></div>
				<div class="dragableBox" id="box4"><div style="float: left; text-align: center; width: 78%;">TIGER</div></div>
			</div>
		</div>
		<div id="rows" class="leftColumn" style="height:308px;margin-top:20px;margin-bottom:20px;">
			<div  style="text-align: center;padding-top:6px;padding-bottom:6px; background-color: #DDDDDD; color:#444444;border-bottom:1px solid #868686;font-size: 16px;"><p><b>Dimensions</b></p></div>
			<div id="dropContent1">
				<div class="dragableBox" id="salary_details">Salary Head</div>
				<div class="dragableBox" id="employee_personal_details">Employee</div>
				<div class="dragableBox" id="box7">Organisation</div>
				<div class="dragableBox" id="box8">Work Location</div>
				<div class="dragableBox" id="box9">CAT</div>
				<div class="dragableBox" id="box10">DOG</div>
				<div class="dragableBox" id="box11">HORSE</div>
				<div class="dragableBox" id="box12">TIGER</div>
			</div>
		</div>
		<div id="filters" class="leftColumn" style="height:300px;">
		<div  style="text-align: center; background-color: #DDDDDD; border-bottom:1px solid #868686; padding:6px;color:#444444;font-size: 16px;"><p><b>Filter</b></p></div>
		</div>
		
		</div>
			<div class="rightColumn" style="float: left;margin-top: 10px; height: auto; width: 1000px;">
			<div style="width: 100%;">
			<div style="float: left; margin-right: 5px;margin-left:10px;margin-left:10px;padding:5px 0 28px 10px; width: 60px;color:#346897; font-size: 14px;"><p><b>Measures</b></p></div>
			<div id="dropBoxMeasure" class="dropBox" style="float: right; width: 900px;">
				
				<div id="dropContent2"></div>		
				
			</div>
			</div>
			<div style="width: 100%;">
			<div style="float: left; margin-right: 5px; margin-left:10px;color:#346897;padding:5px 0 28px 10px; width: 60px; font-size: 14px;">
			<p><b>Row</b></p></div>
			<div id="dropBoxRow" class="dropBox"  style="float: right; width: 900px;">
				
				<div id="dropContent3"></div>		
				
			</div>
			</div>
			<div style="width: 100%;">
			<div style="float: left; margin-right: 5px; color:#346897;margin-left:10px;padding:5px 0 0 10px;width: 60px; font-size:14px;">
			<p><b>Column</b></p></div>
			<div id="dropBoxColumn" class="dropBox"  style="float: right; width: 900px;">
				<div id="dropContent4"></div>		
				
			</div>
			</div>
		</div>
		<div id="contentdata" style="border: 1px solid #868686; border-radius:3px;margin-left:20px;float: left; width: 1000px; height: 800px;">
		<div style="padding:15px 0 0 15px;font-size:16px;font-weight:bold;color:#444444; background-color: #DDDDDD; height: 20%;">
		<div>Filter Summary </div>
		<s:form method="POST" action="ReportTracker">
		<div style="float:left">
		<table>
		<tr><td><s:submit value="Display Data"></s:submit> </td></tr>
		</table>
		</div>
		<div>
		<a href="" style="float:right;"><img src="images1/print.png" style="padding-right:30px;"></a>
		<a href="" style="float:right;"><img src="images1/save.png" style="padding:4px 30px 0 0;"></a>
		</div>
		</s:form>
		</div>
		<div style="width: 100%; background-color: #868686; box-shadow:3px 4px 12px #616161;height: 80%;">
		<div id="container" style="width: 99%; float: left;height:98%;"> 
		 <ul>
				<li><a href="#Project_Details"><span>Tabular</span> </a>
				</li>
				
				<li id="tabammenities" ><a href="#Building_Details"><span>Line Chart</span> </a>
				</li>				
				<li  id="tabbuilding"><a href="#amenitis"><span>Pai Chart </span></a>
				</li>
				
			</ul>
 <div style=" border: solid 0px #ff0000;width:96% " id="Project_Details">
 <div class="cat_heading"><h3>Tablar Data</h3></div>
  </div>
  
  <div style="border: solid 0px #ff0000;width:96% " id="Building_Details">
 <div class="cat_heading"><h3>Line Chart Details</h3></div>
  </div>
  <div style="border: solid 0px #ff0000;width:96% " id="amenitis">
 <div class="cat_heading"><h3>Pai Details</h3></div>
 <div style="width:100%; height: 500px">
	                                    <jsp:include page="/jsp/chart/AttendancePieChart.jsp" />
	                            </div>
  </div>
		</div>
	</div>
	</div>

<div id="debug"></div>
<script type="text/javascript">

// Custom drop actions for <div id="dropBox"> and <div id="leftColumn">
function dropItems(idOfDraggedItem,targetId,x,y)
{
	
	if(targetId=='dropBoxMeasure'){	// Item dropped on <div id="dropBox">
		var obj = document.getElementById(idOfDraggedItem);
		if(obj.parentNode.id=='dropContent2')return;	
		if(obj.parentNode.id!='dropContent')return;	
		document.getElementById('dropContent2').appendChild(obj);	// Appending dragged element as child of target box
		
		
		var divTag = document.createElement("div");
		divTag.id = "col_prev_employer";
		divTag.innerHTML ="<a href=\"#\" onClick=\"onCancelDimension('col_prev_employer','"+idOfDraggedItem+"','dropBoxMeasure');\"><img src=\"images1/close_icon_small.png\"></a>";
		obj.appendChild(divTag);
		
		obj.style.backgroundColor='#346897';
		
		obj.style.color='#ffffff';
		
	}
	if(targetId=='measure'){	// Item dropped on <div id="leftColumn">
		var obj = document.getElementById(idOfDraggedItem);
		if(obj.parentNode.id=='dropContent')return;	
		if(obj.parentNode.id!='dropContent2')return;	
		document.getElementById('dropContent').appendChild(obj);	// Appending dragged element as child of target box
		obj.style.background='none #68AC3B';
		obj.style.color='#000000';
		var row_document = document.getElementById('col_prev_employer'); 
		obj.removeChild(row_document);
	}
	
	
	if(targetId=='dropBoxRow'){	// Item dropped on <div id="leftColumn">
	alert(idOfDraggedItem);
		var obj = document.getElementById(idOfDraggedItem);
		
		if(obj.parentNode.id=='dropContent3')return;	
		if(obj.parentNode.id=='dropContent')return;	
		if(obj.parentNode.id=='dropContent2')return;	
		document.getElementById('dropContent3').appendChild(obj);	// Appending dragged element as child of target box
		getfilter(idOfDraggedItem);
		obj.style.backgroundColor='#346897';
		obj.style.color='#ffffff';
		//checkCodeValidation(idOfDraggedItem);
	}
	if(targetId=='dropBoxColumn'){	// Item dropped on <div id="leftColumn">
		var obj = document.getElementById(idOfDraggedItem);
		if(obj.parentNode.id=='dropContent4')return;
		if(obj.parentNode.id=='dropContent')return;	
		if(obj.parentNode.id=='dropContent2')return;
		document.getElementById('dropContent4').appendChild(obj);	// Appending dragged element as child of target box
		getfilter();
		obj.style.backgroundColor='#346897';
		obj.style.color='#ffffff';
	}
	
	if(targetId=='rows'){	// Item dropped on <div id="leftColumn">
		var obj = document.getElementById(idOfDraggedItem);
		
		if(obj.parentNode.id=='dropContent1')return;
		if(obj.parentNode.id=='dropContent2')return;	
		if(obj.parentNode.id=='dropContent')return;
		document.getElementById('dropContent1').appendChild(obj);	// Appending dragged element as child of target box
		obj.style.backgroundColor='#68AC3B';
		obj.style.color='#000000';
	}
//	alert(targetId);
	
}

function onCancelDimension(id,parentId,targetId){
	alert(id);
	alert(parentId);
	alert(targetId);
	var obj = document.getElementById(parentId);
	var row_document = document.getElementById('col_prev_employer'); 
	obj.removeChild(row_document);
	if(targetId==''){
		var targetobj = document.getElementById(targetId);
		targetobj.appendChild(obj);
	}else{
		alert("Go");
	}
}








function onDragFunction(cloneId,origId)
{
	//self.status = 'Started dragging element with id ' + cloneId;

	//var obj = document.getElementById(cloneId);
	
}

var dragDropObj = new DHTMLgoodies_dragDrop();
dragDropObj.addSource('amount',true,true,true,false,'onDragFunction');	// Make <div id="box1"> dragable. slide item back into original position after drop
dragDropObj.addSource('box2',true,true,true,false,'onDragFunction');	// Make <div id="box2"> dragable. slide item back into original position after drop
dragDropObj.addSource('box3',true,true,true,false,'onDragFunction');	// Make <div id="box3"> dragable. slide item back into original position after drop
dragDropObj.addSource('box4',true,true,true,false,'onDragFunction');	// Make <div id="box4"> dragable. slide item back into original position after drop
dragDropObj.addSource('salary_details',true,true,true,false,'onDragFunction');	// Make <div id="box1"> dragable. slide item back into original position after drop
dragDropObj.addSource('employee_personal_details',true,true,true,false,'onDragFunction');	// Make <div id="box2"> dragable. slide item back into original position after drop
dragDropObj.addSource('box7',true,true,true,false,'onDragFunction');	// Make <div id="box3"> dragable. slide item back into original position after drop
dragDropObj.addSource('box8',true,true,true,false,'onDragFunction');	// Make <div id="box4"> dragable. slide item back into original position after drop
dragDropObj.addSource('box9',true,true,true,false,'onDragFunction');	// Make <div id="box1"> dragable. slide item back into original position after drop
dragDropObj.addSource('box10',true,true,true,false,'onDragFunction');	// Make <div id="box2"> dragable. slide item back into original position after drop
dragDropObj.addSource('box11',true,true,true,false,'onDragFunction');	// Make <div id="box3"> dragable. slide item back into original position after drop
dragDropObj.addSource('box12',true,true,true,false,'onDragFunction');	// Make <div id="box4"> dragable. slide item back into original position after drop

dragDropObj.addTarget('dropBoxMeasure','dropItems');	// Set <div id="dropBox"> as a drop target. Call function dropItems on drop
dragDropObj.addTarget('measure','dropItems'); // Set <div id="leftColumn"> as a drop target. Call function dropItems on drop
dragDropObj.addTarget('dropBoxRow','dropItems');	// Set <div id="dropBox"> as a drop target. Call function dropItems on drop
dragDropObj.addTarget('dropBoxColumn','dropItems');	// Set <div id="dropBox"> as a drop target. Call function dropItems on drop
dragDropObj.addTarget('rows','dropItems');	// Set <div id="dropBox"> as a drop target. Call function dropItems on drop

dragDropObj.init();
</script>

<script>

function getfilter(filterType){
	  
	  
	 
	 var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {

			var xhr = $.ajax({
				url : "ReportTrackerFilter.action?filterType=" +filterType,
				cache : false,
				success : function(data) {
					
					var divTag = document.createElement("div");
					divTag.id = "div_"+filterType;
					divTag.innerHTML =data;
					document.getElementById('filters').appendChild(divTag);
										
				}
			});

		}
		
	
 }
 
function GetXmlHttpObject() {
		if (window.XMLHttpRequest) {
			// code for IE7+, Firefox, Chrome, Opera, Safari
			return new XMLHttpRequest();
		}
		if (window.ActiveXObject) {
			// code for IE6, IE5
			return new ActiveXObject("Microsoft.XMLHTTP");
		}
		return null;
	}
</script>

</div>
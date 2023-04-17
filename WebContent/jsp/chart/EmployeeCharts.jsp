<%@ taglib prefix="s" uri="/struts-tags"%>

<script>

	$(document).ready(function() {
		showChart1("select1");
		showChart2("select2");
		showChart3("select3");
		showChart4("select4");
		showChart5("select5");
	});

	function showChart1(chk) {
		var frm_attr = new Array();
		
		if (chk.checked == 1) {
			document.getElementById("frame_Chart1").setAttribute("style", "");
			if((document.getElementById("frame_Chart1").getAttribute("src")) == null ) 	//load chart only once.
				document.getElementById("frame_Chart1").setAttribute("src", "ShowClockInClockOutChart.action?chart=candlestick");
		
		}else { 
	  		document.getElementById("frame_Chart1").setAttribute("style", "display:none");
	  	}		
	}
	
	function showChart2(chk) {
		
		if (chk.checked == 1) {
			document.getElementById("frame_Chart2").setAttribute("style", "");
			if((document.getElementById("frame_Chart2").getAttribute("src")) == null ) 	{//load chart only once.
				document.getElementById("frame_Chart2").setAttribute("src", "ShowClockInClockOutChart.action?chart=spline");
			}
		}else {
	  		document.getElementById("frame_Chart2").setAttribute("style", "display:none");
	  	}
	}
	
	function showChart3(chk) {
		
		if (chk.checked == 1) {
			document.getElementById("frame_Chart3").setAttribute("style", "");
			if((document.getElementById("frame_Chart3").getAttribute("src")) == null ) 	{//load chart only once.
				document.getElementById("frame_Chart3").setAttribute("src", "ShowClockInClockOutChart.action?chart=pie");
			}
		}else {
	  		document.getElementById("frame_Chart3").setAttribute("style", "display:none");
	  	}
	}
	
	function showChart4(chk) {
		
		if (chk.checked == 1) {
			document.getElementById("frame_Chart4").setAttribute("style", "");
			if((document.getElementById("frame_Chart4").getAttribute("src")) == null ) 	{//load chart only once.
				document.getElementById("frame_Chart4").setAttribute("src", "ShowClockInClockOutChart.action?chart=scatter");
			}
		}else {
	  		document.getElementById("frame_Chart4").setAttribute("style", "display:none");
	  	}
	}
	
	function showChart5(chk) {
		
		if (chk.checked == 1) {
			document.getElementById("frame_Chart5").setAttribute("style", "");
			if((document.getElementById("frame_Chart5").getAttribute("src")) == null ) 	{//load chart only once.
				document.getElementById("frame_Chart5").setAttribute("src", "ShowClockInClockOutChart.action?chart=bar");
			}
		}else {
	  		document.getElementById("frame_Chart5").setAttribute("style", "display:none");
	  	}
	}
	
</script>

<div class="pagetitle">
      <span>Employee Charts</span>
</div>

<div class="leftbox reportWidth">

<div style="height: auto; float: left; width: 70%; margin: 50px 30px 50px 30px;">
	<iframe width="100%" height="520px" id="frame_Chart1">
		<p>Your browser does not support iframes</p>
	</iframe>
	<iframe width="100%" height="520px" id="frame_Chart2">
		<p>Your browser does not support iframes</p>
	</iframe>
	<iframe width="100%" height="520px" id="frame_Chart3">
		<p>Your browser does not support iframes</p>
	</iframe>
	<iframe width="100%" height="520px" id="frame_Chart4">
		<p>Your browser does not support iframes</p>
	</iframe>
	<iframe width="100%" height="520px" id="frame_Chart5">
		<p>Your browser does not support iframes</p>
	</iframe>
</div>

<div style="float: left; margin: 50px 0px 0px 10px; width: 20%;">

Please Select the Chart to Display<br><br>

<s:form id="frm_charts" action="GetChart1" name="frm_charts">
	
		<input type="checkbox" name="select1" id ="select1" value="1" onclick="return showChart1(select1)">
		Clock Entries (Candlestick)<br>
		<input type="checkbox" name="select2" id ="select2" value="2" onclick="return showChart2(select2)">
		Clock Entries (Spline)<br>
		<input type="checkbox" name="select3" id ="select3" value="3" onclick="return showChart3(select3)">
		Leaves Taken and Left<br>
		<input type="checkbox" name="select4" id ="select4" value="4" onclick="return showChart4(select4)">
		Clock Entries per Service<br>
		<input type="checkbox" name="select5" id ="select5" value="5" onclick="return showChart5(select5)">
		Salary Break Up<br>
		
</s:form>

</div>
</div>
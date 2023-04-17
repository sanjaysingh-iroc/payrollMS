
 <%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<script src="http://code.highcharts.com/highcharts.js"></script>
<script src="http://code.highcharts.com/modules/exporting.js"></script>

<!--
<script type="text/javascript">

$(function () {
    $(document).ready(function () {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });

        $('#container').highcharts({
            chart: {
                type: 'spline',
                animation: Highcharts.svg, // don't animate in old IE
                marginRight: 10,
                events: {
                    load: function () {

                        // set up the updating of the chart each second
                        var series = this.series[0];
                        setInterval(function () {
                            var x = (new Date()).getTime(), // current time
                                y = Math.random();
                            series.addPoint([x, y], true, true);
                        }, 1000);
                    }
                }
            },
            title: {
                text: 'Live random data'
            },
            xAxis: {
                type: 'datetime',
                tickPixelInterval: 150
            },
            yAxis: {
                title: {
                    text: 'Value'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + this.series.name + '</b><br/>' +
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                        Highcharts.numberFormat(this.y, 2);
                }
            },
            legend: {
                enabled: false
            },
            exporting: {
                enabled: false
            },
            series: [{
                name: 'Random data',
                data: (function () {
                    // generate an array of random data
                    var data = [],
                        time = (new Date()).getTime(),
                        i;

                    for (i = -19; i <= 0; i += 1) {
                        data.push({
                            x: time + i * 1000,
                            y: Math.random()
                        });
                    }
                    return data;
                }())
            }]
        });
    });
});

</script> -->



<script type="text/javascript">
$(function () {
	
	 var chart;
     $(document).ready(function() {
       chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                type: 'spline'
            },
            title: {
                text: 'Performance %'
            },
            
            xAxis: {
                type: 'linear',
                min:0,
                max:10
            },
            yAxis: {
                title: {
                    text: 'Employee(in No.)'
                },
                min: 0,
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
            	formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                     this.x +', '+ this.y ;
            }
            },
            
           series: [<%=(String)request.getAttribute("data")%>]
        });
    });

    
});
</script>

 
 
 <%-- <script type="text/javascript">
 
 <% 
 Map<String, List<List<String>>> appraisalData = (Map<String, List<List<String>>>) request.getAttribute("appraisalData");
 Map<String, String> appraisalMp = (Map<String, String>) request.getAttribute("appraisalMp");
 String data = (String)request.getAttribute("data");
 System.out.println("data ===>> " +data);
 %>
 
 $(function () {
	    var chart;
	    $(document).ready(function() {
	        chart = new Highcharts.Chart({
	            chart: {
	                renderTo: 'container',
	                type: 'spline'
	            },
	            title: {
	                text: 'Performance %'
	            },
	            
	            xAxis: {
	                //type: 'linear',
	                min:0,
	                max:10
	            },
	            yAxis: {
	                title: {
	                    text: 'Employee(in No.)'
	                },
	                plotLines: [{
	                    value: 0,
	                    width: 1,
	                    color: '#808080'
	                }]
	            },
	            tooltip: {
	            	formatter: function() {
	                    return '<b>'+ this.series.name +'</b><br/>'+
	                     this.x +', '+ this.y ;
	            }
	            },
	            
	            series: [{ name: 'ASSESSMENT SHEET FOR HOD' ,data: [[0,0],[6,1]]},
	                     { name: 'Annual Appraisal' ,data: [[0,0],[4,1]]}]
	        });
	    });
	    
	});
	
  
</script> --%>
 
<div id="container" style="height: 300px; width:100%"></div>

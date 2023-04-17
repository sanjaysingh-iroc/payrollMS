
 <%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>

<script type="text/javascript">
$(function () {
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container22',
                type: 'spline'
            },
            title: {
                text: ''
            },
            
            xAxis: {
                type: 'linear',
                /* categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                             'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'] */
                categories: <%=(String)request.getAttribute("sbCategory")%>
            },
            yAxis: {
                title: {
                    text: 'Rs.'
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
            
            series: [{
                name: 'Net Salary',
                //data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
                data: <%=(String)request.getAttribute("sbSalary")%>
            }]
        });
    });
    
});


/* $(function () {
    $('#container22').highcharts({
        title: {
            text: 'My Growth',
            x: 0 //center 
        },
        subtitle: {
            text: '',
            x: 0
        },
        xAxis: {
            categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
        },
        yAxis: {
            title: {
                text: 'Rs.'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: '°C'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [{
            name: 'Net Salary',
            data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
        }]
    });
}); */
</script>
<div id="container22" style="height: 300px; width:100%"></div>

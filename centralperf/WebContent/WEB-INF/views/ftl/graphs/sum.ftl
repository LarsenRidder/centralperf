
<#if runGraphSeries?exists>
<script type='text/javascript' src='${rc.contextPath}/resources/js/highcharts/highcharts.js'></script>
<script type='text/javascript'>
$(document).ready(function () 
{
    $('#sumChart').highcharts(
    	{
	        chart: {zoomType: 'x', spacingRight: 20},
	        title: {text: 'Summary graph for run ${run.label}[${run.id}]'},
	        subtitle: {
	            text: document.ontouchstart === undefined ?
	                'Click and drag in the plot area to zoom in' :
	                'Drag your finger over the plot to zoom in'
	        },
	        xAxis: {type: 'datetime', title: {text: 'Test duration'}, labels: {rotation: 0}},
	        yAxis: [{title:{text: 'Response Time (ms)'}},{title:{text: 'Number of requests by second'},opposite: true}],
	        tooltip: {shared: true},
	        legend: {enabled: false},
	        plotOptions: {
	            area: {
	                fillColor: {
	                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
	                    stops: [
	                        [0, Highcharts.getOptions().colors[0]],
	                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
	                    ]
	                },
	                lineWidth: 1,
	                marker: {enabled: false},
	                shadow: false,
	                states: {hover: {lineWidth: 1}},
	                threshold: null
	            }
        	},
        	series: [
        	{
        		yAxis: 0,
	            type: 'area',
	            name: 'Response time (ms)',
	            data: [${runGraphSeries.rstSerie}]
        	},{
        		yAxis: 1,
                type: 'spline',
                name: 'Request/s',
                data: [${runGraphSeries.reqSerie}],
                marker: {
                	lineWidth: 2,
                	lineColor: Highcharts.getOptions().colors[3],
                	fillColor: 'white'
                }
            },{
                type: 'pie',
                name: 'HTTP responses',
                tooltip: {valueSuffix: '%'},
                data: [{
                    name: '1xx',
                    y: ${runGraphPie.http1xxRatio?string("0.##")},
                    color: Highcharts.getOptions().colors[0]
                }, {
                    name: '2xx',
                    y: ${runGraphPie.http2xxRatio?string("0.##")},
                    color: Highcharts.getOptions().colors[2]
                }, {
                    name: '3xx',
                    y: ${runGraphPie.http3xxRatio?string("0.##")},
                    color: Highcharts.getOptions().colors[4]
                }, {
                    name: '4xx',
                    y: ${runGraphPie.http4xxRatio?string("0.##")},
                    color: Highcharts.getOptions().colors[6]
                }, {
                    name: '5xx',
                    y: ${runGraphPie.http5xxRatio?string("0.##")},
                    color: Highcharts.getOptions().colors[8]
                }, {
                    name: 'ERR',
                    y: ${runGraphPie.httpErrRatio?string("0.##")},
                    color: Highcharts.getOptions().colors[1]
                }],
                center: [150, 0],
                size: 150,
                showInLegend: true,
                dataLabels: {
                    formatter: function() {
                        return this.y > 5 ? this.point.name : null;
                    },
                    color: 'white',
                    distance: -15
                }
        	}
        	]
    	});
});
</script>
<div id="sumChart"></div>
</#if>
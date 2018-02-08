// Draw price
function addPriceData (jsonString) {
    var json = JSON.parse(jsonString);
    var symbol = json["Meta Data"]["2. Symbol"];

    drawPrice(json, symbol);
}

function drawPrice(json, symbol) {
    var series = json["Time Series (Daily)"];
	var price_data = [];
    var vol_data = [];
    var key_data=[];
    var count = 0;
    for (var key in series) {
        if (count < 121) {
            price_data.unshift(Number(series[key]['4. close']));
            vol_data.unshift(Number(series[key]['5. volume']));
            key_data.unshift(key);
        }
        count++;
    };

	var option = {
            chart: {
                zoomType: 'x'
            },
            title: {
                text: symbol + ' Stock Price and Volume'
            },
            subtitle: {
                useHTML: true,
                text: '<a id="source" href="https://www.alphavantage.co/" target="_blank">Source: Alpha Vantage</a>',
                style: {
                    color: 'rgb(54, 61, 206)'
                }
            },
            xAxis: {
                type: 'category',
                categories: key_data,
                tickInterval: 5,
                labels: {
                    formatter: function () {
                        return this.value.substring(5,7) + '/' + this.value.substring(8);
                    },
                    style: {
                        fontSize: '6px'
                    }
                }
            },
            yAxis: [{
                labels: {
                    format: '{value}'
                },
                title: {
                    text: 'Stock Price'
                },
                tickInterval: 5
            },
                {
                    labels: {
                        formatter: function () {
                            return this.value / 1000000 + 'M';
                        }
                    },
                    title: {
                        text: 'Volume'
                    },
                    tickInterval: 80000000,
                    max: 300000000,
                    opposite: true
                }],
            plotOptions: {
                area: {
                    fillColor: 'rgba(233, 233, 253, 0.8)',
                    lineColor: 'rgb(35, 42, 206)',
                    lineWidth: 1,
                    states: {
                        hover: {
                            lineWidth: 1
                        }
                    },
                    threshold: null
                },
                column: {
                    color: 'rgb(190, 55, 25)',
                    groupPadding: 0.1,
                    pointWidth: 0.2
                },
                series: {
                    marker: {
                        enabled: false,
                    }
                }
            },

            tooltip: {
                formatter: function() {
                    var tip = this.x.substring(5,7) + '/' + this.x.substring(8);
                    tip += '<br/><span style="color:' + this.color + '">\u25CF</span> ' + this.series.name + ': ' + this.y;
                    return tip;
                }
            },

            series: [{
                type: 'area',
                name: symbol,
                data: price_data,
                color: 'rgba(231, 143, 142, 0.80)',
            },
                {
                    type: 'column',
                    name: symbol + " Volume",
                    data: vol_data,
                    yAxis: 1
                }]
        };


    Highcharts.chart('chart_container', option);

    passAndroidOption(option);
};

// Draw indicators
// target: indicator's name
function makeSingle(jsonString, target) {
	var json = JSON.parse(jsonString);
	var title = json["Meta Data"]["2: Indicator"];
	var series = json["Technical Analysis: " + target];
	var keys = [];
	var values = [];
	var count = 0;
	for (var key in series) {
        if (count < 121){
            values.unshift(Number(series[key][target]));
            keys.unshift(key);
        }
        count++;
	}
    var option = {
        chart: {
            zoomType: 'x'
        },
        title: {
            text: title
        },
        subtitle: {
            useHTML: true,
            text: '<a id="source" href="https://www.alphavantage.co/" target="_blank">Source: Alpha Vantage</a>',
            style: {
                color: 'rgb(54, 61, 206)'
            }
        },
        xAxis: {
            type: 'category',
            categories: keys,
            tickInterval: 5,
            labels: {
                formatter: function () {
                    return this.value.substring(5,7) + '/' + this.value.substring(8);
                },
                style: {
                    fontSize: '6px'
                }
            }
        },
        yAxis: {
            labels: {
                format: '{value}'
            },
            title: {
                text: target
            },
            tickInterval: null,
            max: null,
            style: {
                fontSize: '7px'
            }
        },
        tooltip: {
            formatter: function() {
                var tip = this.x.substring(5,7) + '/' + this.x.substring(8);
                tip += '<br/><span style="color:' + this.color + '">\u25CF</span> ' + this.series.name + ': ' + Highcharts.numberFormat(this.y, 2);
                return tip;
            }
        },
        plotOptions: {
            spline: {
                lineWidth: 1.2
            }
        },
        series: [{
            type: 'spline',
            name: target,
            data: values,
            color: 'rgb(176, 181, 206)'
        }]
    };

    Highcharts.chart('chart_container', option);

    passAndroidOption(option);
}

function makeDouble(jsonString, target) {
	var json = JSON.parse(jsonString);
	var title = json["Meta Data"]["2: Indicator"];
	var series = json["Technical Analysis: " + target];
	var keys = [];
	var value1 = [];
	var value2 = [];
	var count = 0;
	for (var key in series) {
		if (count < 121){
            value1.unshift(Number(series[key]["SlowD"]));
            value2.unshift(Number(series[key]["SlowK"]));
            keys.unshift(key);
        }
        count++;
	}

    var option = {
	    chart: {
	        zoomType: 'x'
	    },
	    title: {
	        text: title
	    },
	    subtitle: {
	        useHTML: true,
	        text: '<a id="source" href="https://www.alphavantage.co/" target="_blank">Source: Alpha Vantage</a>',
	        style: {
	            color: 'rgb(54, 61, 206)'
	        }
	    },
	    xAxis: {
	        type: 'category',
	        categories: keys,
	        tickInterval: 5,
	        labels: {
	            formatter: function () {
	                var temp = new Date(this.value);
	                temp = (temp.getUTCMonth() + 1) + '/' + temp.getUTCDate();
	                return temp;
	            },
	            style: {
	                fontSize: '6px'
	            }
	        },
	        crosshair: true
	    },
	    yAxis: {
	        labels: {
	            format: '{value}'
	        },
	        title: {
	            text: title
	        },
	        tickInterval: null,
	        max: null,
	        style: {
	            fontSize: '6px'
	        }
	    },
	    plotOptions: {
	        spline: {
	            lineWidth: 1.2
	        }
	    },

	    tooltip: {
	        formatter: function() {
	            var points = this.points; // array[# of series]
	            var tip = this.x.substring(5,7) + '/' + this.x.substring(8);
	            for (var i in points) {
	                tip += '<br/><span style="color:' + points[i].color + '">\u25CF</span> ' + points[i].series.name + ': ' + Highcharts.numberFormat(points[i].y);
	            }
	            return tip;
	        },
	        shared: true
	    },

	    series: [{
	        type: 'spline',
	        name: target + " SlowD",
	        data: value1,
	        color: 'rgb(184, 44, 11)',
	    },
	        {
	            type: 'spline',
	            name: target + " SlowK",
	            data: value2,
	            color: 'rgb(152, 193, 233)',
	        }]
	};

	Highcharts.chart('chart_container', option);

	passAndroidOption(option);
}

function makeTreble(jsonString, target) {
	var json = JSON.parse(jsonString);
	var title = json["Meta Data"]["2: Indicator"];
	var series = json["Technical Analysis: " + target];
	var keys = [];
	var value1 = [];
	var value2 = [];
	var value3 = [];
	var count = 0;
	if (target === "MACD") {
		var name1 = "MACD_Signal";
		var name2 = "MACD_Hist";
		var name3 = "MACD";
	} else if (target === "BBANDS") {
		var name1 = "Real Upper Band";
		var name2 = "Real Lower Band";
		var name3 = "Real Middle Band";
	}
	for (var key in series) {
		if (count < 121){
			value1.unshift(Number(series[key][name1]));
       		value2.unshift(Number(series[key][name2]));
       		value3.unshift(Number(series[key][name3]));
            keys.unshift(key);
        }
        count++;
	}

    var option = {
	    chart: {
	        zoomType: 'x'
	    },
	    title: {
	        text: title
	    },
	    subtitle: {
	        useHTML: true,
	        text: '<a id="source" href="https://www.alphavantage.co/" target="_blank">Source: Alpha Vantage</a>',
	        style: {
	            color: 'rgb(54, 61, 206)'
	        }
	    },
	    xAxis: {
	        type: 'category',
	        categories: keys,
	        tickInterval: 5,
	        labels: {
	            formatter: function () {
	                return this.value.substring(5,7) + '/' + this.value.substring(8);
	            },
	            style: {
	                fontSize: '6px'
	            }
	        },
	        crosshair: true
	    },
	    yAxis: {
	        labels: {
	            format: '{value}'
	        },
	        title: {
	            text: title
	        },
	        tickInterval: null,
	        max: null,
	        style: {
	            fontSize: '6px'
	        }
	    },
	    plotOptions: {
	        spline: {
	            lineWidth: 1.2
	        }
	    },

	    tooltip: {
	        formatter: function() {
	            var points = this.points; // array[# of series]
	            var tip = this.x.substring(5,7) + '/' + this.x.substring(8);
	            for (var i in points) {
	                tip += '<br/><span style="color:' + points[i].color + '">\u25CF</span> ' + points[i].series.name + ': ' + Highcharts.numberFormat(points[i].y);
	            }
	            return tip;
	        },
	        shared: true
	    },

	    series: [{
	        type: 'spline',
	        name: target + " " + name1,
	        data: value1,
	        color: 'rgb(180, 50, 24)'
	    },
	        {
	            type: 'spline',
	            name: target + " " + name2,
	            data: value2,
	            color: 'rgb(84, 84, 86)'
	        },
	        {
	            type: 'spline',
	            name: target + " " + name3,
	            data: value3,
	            color: 'rgb(180, 230, 162)'
        	}]	
	};

	Highcharts.chart('chart_container', option);

	passAndroidOption(option);
}

function passAndroidOption(option) {
	var optionString = JSON.stringify(option);
	Android.setOption(optionString);
}

function makeHistorical(jsonString) {
	var json = JSON.parse(jsonString);
    var series = json["Time Series (Daily)"];
    var symbol = json["Meta Data"]["2. Symbol"];
	var his_data = [];
    var count = 0;
    for (var key in series) {
    	if (count < 1000) {
            var date = new Date(key);
            his_data.unshift([date.getTime(), Number(series[key]['4. close'])]);
        }
        count++;
    }

    Highcharts.stockChart('chart_container', {

        title: {
            text: symbol + ' Stock Value'
        },

        subtitle: {
            useHTML: true,
            text: '<a id="source" href="https://www.alphavantage.co/" target="_blank">Source: Alpha Vantage</a>',
            style: {
                color: 'rgb(54, 61, 206)'
            }
        },

        tooltip: {
            formatter: function() {
                var points = this.points[0].x; // array[# of series]
                var date = new Date(points);
                var format = "";
                var days = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
                var months = [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ];
                format += days[date.getDay()] + ", " + months[date.getMonth()] + " " + (date.getDate() + 1) + ", " + date.getFullYear();
                format += '<br/><span style="color:' + this.points[0].color + '">\u25CF</span> ' + this.points[0].series.name + ': ' + Highcharts.numberFormat(this.points[0].y)
                return format;
            }
        },

        rangeSelector: {
            selected: 0,
            buttons: [{
                type: 'week',
                count: 1,
                text: '1w'
            },
            {
                type: 'month',
                count: 1,
                text: '1m'
            }, {
                type: 'month',
                count: 3,
                text: '3m'
            }, {
                type: 'month',
                count: 6,
                text: '6m'
            }, {
                type: 'ytd',
                text: 'YTD'
            }, {
                type: 'year',
                count: 1,
                text: '1y'
            }, {
                type: 'all',
                text: 'All'
            }]
        },

        series: [{
            name: symbol,
            type: 'area',
            data: his_data,
            tooltip: {
                valueDecimals: 2
            }
        }],

    });
}

//# dc.js Getting Started and How-To Guide
'use strict';
// Create Chart Objects
var rangeChart = dc.barChart("#range-chart");
var quarterOpenChart = dc.pieChart("#quarter-open-chart");
var statusChart = dc.pieChart("#status-chart");
var severityChart = dc.pieChart("#severity-chart");
var satisfactionChart = dc.pieChart("#satisfaction-chart");
var topicChart = dc.rowChart("#topic-chart");
var dayOfWeekOpenChart = dc.rowChart("#day-of-week-open-chart");
var openedTickets = dc.numberDisplay("#open-tickets");
var closedTickets = dc.numberDisplay("#close-tickets");
var ticketsChart = dc.rowChart("#tickets-summary-chart");
var companyChart = dc.rowChart("#company-chart");
var dasChart = dc.rowChart("#das-chart");
var assigneeChart = dc.rowChart("#assignee-chart");

// Get the raw jason data string
var jsonData = document.getElementById('form:json-data').value;
// Convert the string into a JS json object
var data = JSON.parse(jsonData);
console.log(jsonData);
// Get the first and last dates to establish the date range
var earliest = d3.time.day(new Date(data[0].openEpoch));
var latest = d3.time.day(new Date(data[data.length - 1].openEpoch));
// Get the browser width to set the chart width
var width = window.innerWidth - 600;
// Date format
var dateFormat = d3.time.format("%a %e %b %H:%M");
// Convert and precalculate certain mertics to make the system more efficient
data.forEach(function (d) {
    d.open = new Date(d.openEpoch);
    d.close = new Date(d.closeEpoch);
    d.month = d3.time.month(d.open); // pre-calculate month for better performance
    d.dayOpen = d3.time.day(d.open); // pre-calculate month for better performance
    d.dayClose = d3.time.day(d.close);
    d.dayOpenFmt = dateFormat(d.open);
});
// Create Crossfilter Dimensions and Groups
var ticket = crossfilter(data);
var all = ticket.groupAll();

// Quarterly -------------------------------------------------------------------
var quarterOpenDimension = ticket.dimension(function (d) {
    var month = d.open.getMonth();
    if (month <= 2)
        return "Q1";
    else if (month > 3 && month <= 5)
        return "Q2";
    else if (month > 5 && month <= 8)
        return "Q3";
    else
        return "Q4";
});

quarterOpenChart
        .width(220)
        .height(220)
        .radius(100)
        .innerRadius(30)
        .dimension(quarterOpenDimension)
        .group(quarterOpenDimension.group());

// Week Days -------------------------------------------------------------------
var dayOfWeekOpenDimension = ticket.dimension(function (d) {
    var day = d.open.getDay();
    var name = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    return day + "." + name[day];
});

dayOfWeekOpenChart
        .width(250)
        .height(220)
        .margins({top: 10, left: 10, right: 10, bottom: 20})
        .dimension(dayOfWeekOpenDimension)
        .group(dayOfWeekOpenDimension.group())
        .ordinalColors(['#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#dadaeb'])
        .label(function (d) {
            return d.key.split(".")[1];
        })
        .title(function (d) {
            return d.value;
        })
        .elasticX(true)
        .xAxis().ticks(0);

// Status-----------------------------------------------------------------------
var statusDimension = ticket.dimension(function (d) {
    return d.status;
});

statusChart
        .width(220)
        .height(220)
        .radius(100)
        .innerRadius(30)
        .dimension(statusDimension)
        .group(statusDimension.group());

// Severity---------------------------------------------------------------------
var severityDimension = ticket.dimension(function (d) {
    if (!!d.severity) {
        return d.severity;
    }
});

severityChart
        .width(220)
        .height(220)
        .radius(100)
        .innerRadius(30)
        .dimension(severityDimension)
        .group(severityDimension.group());

// Satisfaction-----------------------------------------------------------------
var satisfactionDimension = ticket.dimension(function (d) {
    if (d.status === "CLOSED") {
        if (d.satisfied === "SATISFIED") {
            return "YES";
        }
        if (d.satisfied === "NOT_SATISFIED") {
            return "NO";
        }
    }
});

satisfactionChart
        .width(220)
        .height(220)
        .radius(100)
        .innerRadius(30)
        .dimension(satisfactionDimension)
        .group(satisfactionDimension.group());

// Topic------------------------------------------------------------------------
var topicDimension = ticket.dimension(function (d) {
    if (!!d.topic) {
        return d.topic.replace("_", " ");
    }
});

topicChart
        .width(250)
        .height(220)
        .margins({top: 10, left: 10, right: 10, bottom: 20})
        .dimension(topicDimension)
        .group(topicDimension.group())
        // assign colors to each value in the x scale domain
        .ordinalColors(['#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#dadaeb'])
        .elasticX(true)
        .xAxis()
        .ticks(0);

// Evg Response-----------------------------------------------------------------
var avgResponseDimension = ticket.dimension(function (d) {
    return d.averageResponseTime;
});
var avgResponseGroup = avgResponseDimension.groupAll().reduce(
        function (p, v) {
            if (v.status === "CLOSED") {
                ++p.n;
                p.total += v.averageResponseTime;
            }
            return p;
        },
        function (p, v) {
            if (v.status === "CLOSED") {
                --p.n;
                p.total -= v.averageResponseTime;
            }
            return p;
        },
        function () {
            return {n: 0, total: 0};
        }
);
dc.numberDisplay("#nothing")
        .group(avgResponseGroup)
        .valueAccessor(function (d) {
            var avg = d.total / d.n;
            var div = document.getElementById('average-response-time');
            millisecondsToStr(avg, div);
        });
// Avg Resolution---------------------------------------------------------------
var avgResolutionDimension = ticket.dimension(function (d) {
    return d.resolutionDuration;
});

var avgResolutionGroup = avgResolutionDimension.groupAll().reduce(
        function (p, v) {
            if (v.status === "CLOSED") {
                ++p.n;
                p.total += v.resolutionDuration;
            }
            return p;
        },
        function (p, v) {
            if (v.status === "CLOSED") {
                --p.n;
                p.total -= v.resolutionDuration;
            }
            return p;
        },
        function () {
            return {n: 0, total: 0};
        }
);

dc.numberDisplay("#nothing")
        .group(avgResolutionGroup)
        .valueAccessor(function (d) {
            var avg = d.total / d.n;
            // Get the div that holds the data
            var div = document.getElementById('average-resolution-time');
            millisecondsToStr(avg, div);
        });

// Open Tickets-----------------------------------------------------------------
var openedTicketDimension = ticket.dimension(function (d) {
    return d.open;
});

var openedTicketGroup = openedTicketDimension.groupAll().reduce(
        function (p, v) {
            if (v.status === "OPEN") {
                ++p.n;
            }
            return p;
        },
        function (p, v) {
            if (v.status === "OPEN") {
                --p.n;
            }
            return p;
        },
        function () {
            return {n: 0};
        }
);

openedTickets.group(openedTicketGroup)
        .valueAccessor(function (d) {
            return d.n;
        })
        .formatNumber(d3.format(".0f"));
// Closed Tickets---------------------------------------------------------------
var closedTicketDimension = ticket.dimension(function (d) {
    return d.close;
});
// Avg response time group
var closedTicketGroup = closedTicketDimension.groupAll().reduce(
        function (p, v) {
            if (v.status === "CLOSED") {
                ++p.n;
            }
            return p;
        },
        function (p, v) {
            if (v.status === "CLOSED") {
                --p.n;
            }
            return p;
        },
        function () {
            return {n: 0};
        }
);

closedTickets.group(closedTicketGroup)
        .valueAccessor(function (d) {
            return d.n;
        })
        .formatNumber(d3.format(".0f"));
// Actual Tickets---------------------------------------------------------------
var ticketsDimension = ticket.dimension(function (d) {
    return d.dayOpenFmt
            + '  |  '
            + d.status
            + '  |  '
            + 'DTX-' + d.id
            + '  |  '
            + d.title;
});

var ticketsGroup = ticketsDimension.group();

ticketsChart
        .width(width)
        .height(ticketsGroup.size() * 30)
        .margins({top: 20, left: 10, right: 10, bottom: 20})
        .dimension(ticketsDimension)
        .group(ticketsGroup)
        .ordinalColors(['#9ecae1'])
        .elasticX(true)
        .xAxis().ticks(0);

// Company----------------------------------------------------------------------
var companyDimension = ticket.dimension(function (d) {
    if (!!d.requesterCompany) {
        return d.requesterCompany;
    }
});

var companyGroup = companyDimension.group();

companyChart
        .width(220)
        .height((companyGroup.size() * 30) + 40)
        .margins({top: 20, left: 10, right: 10, bottom: 20})
        .dimension(companyDimension)
        .group(companyGroup)
        .ordinalColors(['#9ecae1'])
        .elasticX(true)
        .xAxis().ticks(0);

// DAS--------------------------------------------------------------------------
var dasDimension = ticket.dimension(function (d) {
    if (!!d.das) {
        return d.das;
    }
});

var dasGroup = dasDimension.group();

dasChart
        .width(220)
        .height((dasGroup.size() * 30) + 40)
        .margins({top: 20, left: 10, right: 10, bottom: 20})
        .dimension(dasDimension)
        .group(dasGroup)
        .ordinalColors(['#9ecae1'])
        .elasticX(true)
        .xAxis().ticks(0);

// Assignee --------------------------------------------------------------------
var assigneeDimension = ticket.dimension(function (d) {
    if (!!d.assigneeName) {
        return d.assigneeName;
    }
});

var assigneeGroup = assigneeDimension.group();

assigneeChart
        .width(220)
        .height((assigneeGroup.size() * 30) + 40)
        .margins({top: 20, left: 10, right: 10, bottom: 20})
        .dimension(assigneeDimension)
        .group(assigneeGroup)
        .ordinalColors(['#9ecae1'])
        .elasticX(true)
        .xAxis().ticks(0);

// Range -----------------------------------------------------------------------------
var rangeDimension = ticket.dimension(function (d) {
    return d.open;
});

var rangeGroup = rangeDimension.group()
        .reduceCount(function (d) {
            return d.open;
        });

rangeChart
        .width(width)
        .height(50)
        .margins({top: 10, right: 10, bottom: 20, left: 40})
        .dimension(rangeDimension)
        .group(rangeGroup)
        .centerBar(true)
        .gap(rangeGroup.size() / 5)
        .x(d3.time.scale().domain([earliest, latest]))
        .round(d3.time.day.round)
        .alwaysUseRounding(true)
        .xUnits(d3.time.days)
        .yAxis()
        .ticks(0);

// DC---------------------------------------------------------------------------
// Data Count
dc.dataCount(".dc-data-count")
        .dimension(ticket)
        .group(all);

// Rendering
dc.renderAll();

// Version
d3.selectAll("#version").text(dc.version);

// Utils------------------------------------------------------------------------
// Show time in millis as a human readable form
function millisecondsToStr(millis, div) {

    function numberEnding(number) {
        return (number > 1) ? 's ' : ' ';
    }
    if (!!div) {
        // Clear the div
        div.innerHTML = "";
        if (isNaN(millis)) {
            div.innerHTML = 'Not applicable';
        } else {
            var milliseconds = Math.round(millis);
            var temp = Math.floor(milliseconds / 1000);
            if (temp < 60) {
                // Less than 1 min
                div.innerHTML = '< 1 min';
            } else {
                // More than 1 min
                var years = Math.floor(temp / 31536000);
                if (years) {
                    div.innerHTML = years + ' year' + numberEnding(years);
                }
                var weeks = Math.floor((temp %= 31536000) / (86400 * 7));
                if (weeks) {
                    div.innerHTML = div.innerHTML + weeks + ' week' + numberEnding(weeks);
                }
                var days = Math.floor((temp %= 31536000) / 86400);
                if (days) {
                    div.innerHTML = div.innerHTML + days + ' day' + numberEnding(days);
                }
                var hours = Math.floor((temp %= 86400) / 3600);
                if (hours) {
                    div.innerHTML = div.innerHTML + hours + ' hour' + numberEnding(hours);
                }
                var minutes = Math.floor((temp %= 3600) / 60);
                if (minutes) {
                    div.innerHTML = div.innerHTML + minutes + ' minute' + numberEnding(minutes);
                }
            }
        }
    }
}
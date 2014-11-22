//# dc.js Getting Started and How-To Guide
'use strict';
// Create Chart Objects
var rangeChart = dc.barChart("#range-chart");
var quarterOpenChart = dc.rowChart("#quarter-open-chart");
var statusChart = dc.pieChart("#status-chart");
var severityChart = dc.pieChart("#severity-chart");
var satisfactionChart = dc.rowChart("#satisfaction-chart");
var topicChart = dc.rowChart("#topic-chart");
var dayOfWeekOpenChart = dc.rowChart("#day-of-week-open-chart");
var openedTickets = dc.numberDisplay("#open-tickets");
var closedTickets = dc.numberDisplay("#close-tickets");
var ticketsTable = dc.dataTable("#tickets-summary-table");
var companyChart = dc.rowChart("#company-chart");
var dasChart = dc.rowChart("#das-chart");
var assigneeChart = dc.rowChart("#assignee-chart");
// Get the raw jason data string
var jsonData = document.getElementById('form:json-data').value;
// Convert the string into a JS json object
var data = JSON.parse(jsonData);
//console.log(jsonData);
// Get the first and last dates to establish the date range
var earliest = d3.time.day(new Date(data[0].openEpoch));
var latest = d3.time.day(new Date(data[data.length - 1].openEpoch));
// Get the browser width to set the chart width
var width = window.innerWidth - 600;
// Date format
var dateFormat = d3.time.format("%a %e %b");
var timeFormat = d3.time.format("%H:%M");
var numberFormat = d3.format(".1f");
// Convert and precalculate certain mertics to make the system more efficient
data.forEach(function (d) {
    d.open = new Date(d.openEpoch);
    d.close = new Date(d.closeEpoch);
    d.month = d3.time.month(d.open); // pre-calculate month for better performance
    d.dayOpen = d3.time.day(d.open); // pre-calculate month for better performance
    d.dayClose = d3.time.day(d.close);
    d.dayOpenFmt = dateFormat(d.open);
    d.timeOpenFmt = timeFormat(d.open);
    if (d.requesterCompany === null) {
        d.requesterCompany = 'none';
    }
    if (d.das === null) {
        d.das = 'none';
    }
    if (d.assigneeName === null) {
        d.assigneeName = 'none';
    }
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
        .width(200)
        .height(90)
        .margins({top: 10, left: 10, right: 10, bottom: 20})
        .dimension(quarterOpenDimension)
        .group(quarterOpenDimension.group())
        .ordinalColors(['#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#dadaeb'])
        .label(function (d) {
            return "";
        })
        .title(function (d) {
            return d.key + ": " + d.value;
        })
        .elasticX(true)
        .xAxis().ticks(1);
// Week Days -------------------------------------------------------------------
var dayOfWeekOpenDimension = ticket.dimension(function (d) {
    var day = d.open.getDay();
    var name = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    return day + "." + name[day];
});
dayOfWeekOpenChart
        .width(200)
        .height(90)
        .margins({top: 10, left: 10, right: 10, bottom: 20})
        .dimension(dayOfWeekOpenDimension)
        .group(dayOfWeekOpenDimension.group())
        .ordinalColors(['#3182bd', '#6baed6', '#9ecae1', '#c6dbef', '#dadaeb'])
        .label(function (d) {
            return "";
        })
        .title(function (d) {
            return d.key.split(".")[1] + ": " + d.value;
        })
        .elasticX(true)
        .xAxis().ticks(1);
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
        .group(statusDimension.group())
        .label(function (d) {
            return "";
        })
        .legend(dc.legend().x(200).y(10).itemHeight(13).gap(5))
        .ordinalColors(['#3182bd', '#FF9900']);
// Severity---------------------------------------------------------------------
var severityDimension = ticket.dimension(function (d) {
    if (!!d.severity) {
        if (d.severity === 'S1') {
            return 'SERVICE DOWN (S1)';
        }
        if (d.severity === 'S2') {
            return 'SERVICE DISRUPTION (S2)';
        }
        if (d.severity === 'S3') {
            return 'GENERAL SUPPORT (S3)';
        }
    }
});
severityChart
        .width(220)
        .height(220)
        .radius(100)
        .innerRadius(30)
        .dimension(severityDimension)
        .group(severityDimension.group())
        .label(function (d) {
            return "";
        })
        .legend(dc.legend().x(200).y(10).itemHeight(13).gap(5))
        .ordinalColors(['#3182bd', '#6baed6', '#FF9900']);
;
// Satisfaction-----------------------------------------------------------------
var satisfactionDimension = ticket.dimension(function (d) {
    var name = ["NO FEEDBACK", "POOR", "FAIR", "GOOD", "VERY GOOD", "EXCELLENT"];
    return d.satisfied + "_" + name[d.satisfied];
});
satisfactionChart
        .width(250)
        .height(220)
        .margins({top: 10, left: 10, right: 10, bottom: 20})
        .dimension(satisfactionDimension)
        .group(satisfactionDimension.group())
        .ordinalColors(['#FF9900', '#FFCC00', '#E6E6E6', '#9ecae1', '#3182bd'])
        .label(function (d) {
            if (d.key === "1_POOR") {
                return "★";
            }
            if (d.key === "2_FAIR") {
                return "★★";
            }
            if (d.key === "3_GOOD") {
                return "★★★";
            }
            if (d.key === "4_VERY GOOD") {
                return "★★★★";
            }
            if (d.key === "5_EXCELLENT") {
                return "★★★★★";
            }
        })
        .title(function (d) {
            if (d.key.split("_")[1] === "NO FEEDBACK") {
                return d.key.split("_")[1];
            } else {
                return d.key.split("_")[1] + ": " + d.value;
            }
        })
        .data(function (group) {
            return group.all().filter(function (kv) {
                if (kv.key.split("_")[1] !== "NO FEEDBACK") {
                    return kv.value;
                }
            });
        })
        .elasticX(true)
        .xAxis()
        .ticks(2);
// Topic------------------------------------------------------------------------
var topicDimension = ticket.dimension(function (d) {
    if (!!d.topic) {
        var re = new RegExp("_", 'g');
        return d.topic.replace(re, " ");
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
        .ticks(2);
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
    return d.dayOpen;
});
ticketsTable
        .dimension(ticketsDimension)
        .group(function (d) {
            return d.dayOpenFmt;
        })
        .columns([
            function (d) {
                return d.dayOpenFmt;
            },
            function (d) {
                return d.timeOpenFmt;
            },
            function (d) {
                return d.status;
            },
            function (d) {
                if (d.severity === 'S1') {
                    return 'Service Down (S1)';
                }
                if (d.severity === 'S2') {
                    return 'Service Disruption (S2)';
                }
                if (d.severity === 'S3') {
                    return 'General Support (S3)';
                }
            },
            function (d) {
                return "<em><a target='_blank' href='/a/tickets/edit.jsf?ticket=" + d.id + "'>DTX-" + d.id + "</a></em>";
            },
            function (d) {
                return d.title;
            },
            function (d) {
                return d.requesterName;
            },
            function (d) {
                return d.requesterCompany;
            },
            function (d) {
                return d.assigneeName;
            },
            function (d) {
                if (d.satisfied === 0) {
                    return "<span style='font-style: italic; color: #EEEEEE; font-size: 10px;'>Not Rated</span>";
                }
                if (d.satisfied === 1) {
                    return "☆";
                }
                if (d.satisfied === 2) {
                    return "☆☆";
                }
                if (d.satisfied === 3) {
                    return "☆☆☆";
                }
                if (d.satisfied === 4) {
                    return "☆☆☆☆";
                }
                if (d.satisfied === 5) {
                    return "☆☆☆☆☆";
                }
            }
        ])
        .sortBy(function (d) {
            return d.open;
        })
        .order(d3.descending)
        .size(ticket.size())
        .renderlet(function (table) {
            table.selectAll(".dc-table-group").classed("info", true);
        });
// Number of Comments-----------------------------------------------------------
var commentQtyDimension = ticket.dimension(function (d) {
    return d.commentQty;
});
var commentQtyGroup = commentQtyDimension.groupAll().reduce(
        function (p, v) {
            if (v.status === "CLOSED") {
                ++p.total;
                p.qty = p.qty + v.commentQty;
            }
            return p;
        },
        function (p, v) {
            if (v.status === "CLOSED") {
                --p.total;
                p.qty = p.qty - v.commentQty;
            }
            return p;
        },
        function () {
            return {total: 0, qty: 0};
        }
);
dc.numberDisplay("#none")
        .group(commentQtyGroup)
        .valueAccessor(function (d) {
            var div = document.getElementById('average-comments');
            var avg = numberFormat(d.qty / d.total);
            if (!isNaN(avg)) {
                div.innerHTML = "<span class=\'analytics-numbers\'>" + avg + "</span> <span class=\'analytics-units\'> per solved ticket</span>";
            } else {
                div.innerHTML = "<span class=\'analytics-units\'>Not applicable</span>";
            }

        });
// Company----------------------------------------------------------------------
var companyDimension = ticket.dimension(function (d) {
    return d.requesterCompany;
});
var companyGroup = companyDimension.group();
companyChart
        .width(220)
        .height((companyGroup.size() * 30) + 40)
        .margins({top: 20, left: 10, right: 10, bottom: 20})
        .dimension(companyDimension)
        .group(companyGroup)
        .data(function (group) {
            return group.all().filter(function (kv) {
                if (kv.key !== 'none') {
                    return kv.value;
                }
            });
        })
        //.ordinalColors(['#9ecae1'])
        .elasticX(true)
        .xAxis().ticks(0);
// DAS--------------------------------------------------------------------------
var dasDimension = ticket.dimension(function (d) {
    return d.das;
});
var dasGroup = dasDimension.group();
dasChart
        .width(220)
        .height((dasGroup.size() * 30) + 40)
        .margins({top: 20, left: 10, right: 10, bottom: 20})
        .dimension(dasDimension)
        .group(dasGroup)
        .data(function (group) {
            return group.all().filter(function (kv) {
                if (kv.key !== 'none') {
                    return kv.value;
                }
            });
        })
        //.ordinalColors(['#9ecae1'])
        .elasticX(true)
        .xAxis().ticks(0);
// Assignee --------------------------------------------------------------------
var assigneeDimension = ticket.dimension(function (d) {
    return d.assigneeName;
});
var assigneeGroup = assigneeDimension.group();
assigneeChart
        .width(220)
        .height((assigneeGroup.size() * 30) + 40)
        .margins({top: 20, left: 10, right: 10, bottom: 20})
        .dimension(assigneeDimension)
        .group(assigneeGroup)
        .data(function (group) {
            return group.all().filter(function (kv) {
                if (kv.key !== 'none') {
                    return kv.value;
                }
            });
        })
//        .ordinalColors(['#9ecae1'])
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
        .width(width - 700)
        .height(90)
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
        return (number > 1) ? '<span class=\'analytics-units\'>s</span> ' : ' ';
    }
    if (!!div) {
        // Clear the div
        div.innerHTML = "";
        if (isNaN(millis)) {
            div.innerHTML = '<span class=\'analytics-units\'>Not applicable</span>';
        } else {
            var milliseconds = Math.round(millis);
            var seconds = Math.floor(milliseconds / 1000);
            if (seconds < 60) {
                // Less than 1 min
                div.innerHTML = '<span class=\'analytics-units\'>~</span><span class=\'analytics-numbers\'>1</span> <span class=\'analytics-units\'>min</span>';
            } else if (seconds > 864000) {
                // more than 10 days
                div.innerHTML = '<span class=\'analytics-numbers\'>>10</span> <span class=\'analytics-units\'>days</span> <img src="/resources/images/warning-16.png" alt="Warning" style="margin-bottom: 23px; margin-left: -23px;">';
            } else {
                // More than 1 min but less than 10 days
                var days = Math.floor(seconds / 86400);
                seconds -= days * 86400;
                var hours = Math.floor(seconds / 3600) % 24;
                seconds -= hours * 3600;
                var minutes = Math.floor(seconds / 60) % 60;
                seconds -= minutes * 60;
                if (days) {
                    div.innerHTML = div.innerHTML + '<span class=\'analytics-numbers\'>' + days + '</span> <span class=\'analytics-units\'>day</span>' + numberEnding(days);
                }

                if (hours) {
                    div.innerHTML = div.innerHTML + '<span class=\'analytics-numbers\'>' + hours + '</span> <span class=\'analytics-units\'>hour</span>' + numberEnding(hours);
                }

                if (minutes) {
                    div.innerHTML = div.innerHTML + '<span class=\'analytics-numbers\'>' + minutes + '</span> <span class=\'analytics-units\'>min</span>' + numberEnding(minutes);
                }
            }
        }
    }
}
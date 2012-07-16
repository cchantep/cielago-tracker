$("#startDate").on("changeDate", function(e) {
    var end = $("#endDate");
    var v = end.attr("value");

    if (v == null || v.length == 0) {
        var el = new Date(e.date.getTime()+86400/*1d*/);

        end.datepicker("setStartDate", el);
        end.removeAttr("disabled");
    }
});

$("#endDate").on("changeDate", function(e) {
    var sl = new Date(e.date.getTime()-86400/*1d*/);
    var s = $("#startDate");
    
    s.datepicker("setEndDate", sl);
});
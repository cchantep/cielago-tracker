$("document").ready(function() {
    var start = $("#startDateField").attr("value");

    if (start != "") {
        var date = new Date(start);
        var el = new Date(date.getTime()+86400000/*1d*/);
        
        $("#endDate").datepicker("setStartDate", el);
    } // end of if

    var end = $("#endDateField").attr("value");

    if (end != "") {
        var date = new Date(end);
        var sl = new Date(date.getTime()-86400000/*1d*/);

        $("#startDate").datepicker("setEndDate", sl);
    } // end of if

    return true;
});

$("#startDate").on("changeDate", function(e) {
    var date = e.date;

    $("#startDateField").attr("value", _toDateString(date));
    $("#startDate").data("date-format", "yyyy-mm-dd");

    var end = $("#endDate");
    var v = end.attr("value");

    if (v == null || v.length == 0) {
        var ts = date.getTime();
        var el = new Date(ts+86400000/*1d*/);

        end.datepicker("setStartDate", el);
        end.datepicker("setDate", el);
        end.removeAttr("disabled");
    }
});

$("#endDate").on("changeDate", function(e) {
    var date = e.date;

    $("#endDateField").attr("value", _toDateString(date));

    var sl = new Date(date.getTime()-86400000/*1d*/);
    var s = $("#startDate");
    
    s.datepicker("setEndDate", sl);
});

$("#pagination .btn").each(function() {
    var btn = $(this);

    btn.click(function(e) {
        $("#currentPage").val($(this).val());
        $("#trackForm").submit();
    });
});

/**
 * @param date Date object
 * @return yyyy-MM-dd formatted date string
 */
function _toDateString(date) {
    if (date == null) {
        return null;
    } // end of if

    var str = date.toISOString();
    var idx = str.indexOf("T");
    
    return str.substring(0, idx);
} // end of _toDateString

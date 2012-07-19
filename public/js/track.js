$("document").ready(function() {
    $("th.sorting").each(function() {
        var d = $(this);
        var e = d.attr("id");
        var c = (_s.columns[e]) ? _s.columns[e] : e;
        var df = d.parent().parent().parent().data("form");
        
        var o;
        if (o = _s.clauses[c]) {
            d.removeClass("sorting");
            
            if (o == "DESC") {
                d.addClass("sorting_asc");
            } else {
                d.addClass("sorting_desc");
            }
        }
        
        d.click(function() {
            var dir = d.hasClass("sorting_desc") ? "DESC" : "ASC";
            var f = $("#" + df);

            f.children("#order-" + c).remove();

            var i = $(".order", f).length;

            $("<input type=\"hidden\" id=\"order-"+c+"\" " +
              "class=\"order\" name=\"order[" + i + "]\" />").
                appendTo(f).val(c + ":" + dir);
            
            f.submit();
        });
    });
});

$("document").ready(function() {
    var start = $("#startDateField").attr("value");

    if (start != "") {
        var date = new Date(start);
        var el = new Date(date.getTime()+86400/*1d*/);
        
        $("#endDate").datepicker("setStartDate", el);
    } // end of if

    var end = $("#endDateField").attr("value");

    if (end != "") {
        var date = new Date(end);
        var sl = new Date(date.getTime()-86400/*1d*/);

        $("#startDate").datepicker("setEndDate", sl);
    } // end of if

    return true;
});

$("#startDate").on("changeDate", function(e) {
    var date = e.date;

    $("#startDateField").attr("value", _toDateString(date));

    var end = $("#endDate");
    var v = end.attr("value");

    if (v == null || v.length == 0) {
        var ts = date.getTime();
        var el = new Date(ts+86400/*1d*/);

        console.log("##> " + end.datepicker("language").toSource());

        end.datepicker("setStartDate", el);
        end.datepicker("setDate", el);
        end.removeAttr("disabled");
    }
});

$("#endDate").on("changeDate", function(e) {
    var date = e.date;

    $("#endDateField").attr("value", _toDateString(date));

    var sl = new Date(date.getTime()-86400/*1d*/);
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
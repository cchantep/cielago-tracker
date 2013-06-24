(function($) {
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
})(jQuery);

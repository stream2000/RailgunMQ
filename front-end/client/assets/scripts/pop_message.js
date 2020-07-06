(function($) {
    "use strict";

    $('button.user_showtoast').on("click",function () {
        toastr.options = {
            closeButton: false,
            debug: false,
            newestOnTop: true,
            progressBar: false,
            rtl: false,
            positionClass: 'toast-top-center',
            preventDuplicates: true,
            onclick: null,
            showDuration: 300,
            hideDuration: 1000,
            timeOut: 1000,
            extendedTimeOut: 1000,
            showEasing: "swing",
            hideEasing: "linear",
            showMethod: "fadeIn",
            hideMethod: "fadeOut"
        };
        var $toast = toastr['success']('封禁成功！', '提示'); // Wire up an event handler to a button in the toast, if it exists
        return false;
    });
})(jQuery);

(function($) {
    "use strict";

    $('button.music_showtoast').on("click",function () {
        toastr.options = {
            closeButton: false,
            debug: false,
            newestOnTop: true,
            progressBar: false,
            rtl: false,
            positionClass: 'toast-top-center',
            preventDuplicates: true,
            onclick: null,
            showDuration: 300,
            hideDuration: 1000,
            timeOut: 1000,
            extendedTimeOut: 1000,
            showEasing: "swing",
            hideEasing: "linear",
            showMethod: "fadeIn",
            hideMethod: "fadeOut"
        };
        var $toast = toastr['success']('删除成功！', '提示'); // Wire up an event handler to a button in the toast, if it exists
        return false;
    });
})(jQuery);
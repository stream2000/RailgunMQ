/*--------------------------------------------------
Project:        jetrip
Version:        1.0
Author:         Company Name
-----------------------------------------------------
    JS INDEX
    ================================================
    * preloader js
    * sticky menu js
    * slick nav 
    * slick icon slider
    * counter
    * testimonial slider
    * slick icon slider
    * bottom to top
    * Isotop with ImagesLoaded
    * ACCORDION WITH TOGGLE ICONS
    * Google-Map
    ================================================*/

    (function ($) {
        "use strict";
        var $main_window = $(window);
        /*====================================
            preloader js
          ======================================*/
        $main_window.on('load', function () {
            $('#preloader').fadeOut('slow');
        });
        /*====================================
            sticky menu js
          ======================================*/
        var windows = $(window);
        var sticky = $('.header-fixed');
        var banner = $('#banner');
        console.log("sticky",sticky);
        windows.on('scroll', function () {
            var scroll = windows.scrollTop();
            if (scroll < 50) {
                sticky.removeClass('stick');
                banner.removeClass('stick');
            } else {
                sticky.addClass('stick');
                banner.addClass('stick');
            }
        });
        /*====================================
            slick nav
        ======================================*/
        var logo_path = $('.mobile-menu').data('logo');
        $('.navbar-nav').slicknav({
            appendTo: '.mobile-menu',
            removeClasses: true,
            label: '',
            closedSymbol: '<i class="fa fa-angle-right"><i/>',
            openedSymbol: '<i class="fa fa-angle-down"><i/>',
            brand: '<img src="' + logo_path + '" class="img-fluid" alt="logo">'
        });
        /*====================================
            slick  slider
        ======================================*/
        $('.hero-slide').slick({
            infinite: true,
            dots: true,
            speed: 300,
            slidesToShow: 1,
            slidesToScroll: 1,
        });
        $('.hero-slide-three').slick({
            infinite: true,
            dots: true,
            speed: 3000,
            fade: true,
            autoplay: true,
            slidesToShow: 1,
            slidesToScroll: 1,
        });
        /*====================================
            bottom to top
        ======================================*/
        var btn = $('#btn-to-top');
        $(window).on('scroll', function () {
            if ($(window).scrollTop() > 300) {
                btn.addClass('show');
            } else {
                btn.removeClass('show');
            }
        });
        btn.on('click', function (e) {
            e.preventDefault();
            $('html, body').animate({
                scrollTop: 0
            }, '300');
        });
    })(jQuery);
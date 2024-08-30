(function ($) {
  'use strict';
  $(document).on('ready', function () {
    /*====================================
			Mobile Menu
		======================================*/
    $('.menu').slicknav({
      prependTo: '.mobile-nav',
      duration: 300,
      animateIn: 'fadeIn',
      animateOut: 'fadeOut',
      closeOnClick: true,
    });

    /*====================================
		03. Sticky Header JS - header always visible
		======================================*/
    jQuery(window).on('scroll', function () {
      if ($(this).scrollTop() > 200) {
        $('.header').addClass('sticky');
      } else {
        $('.header').removeClass('sticky');
      }
    });

    /*====================================
			Scroll Up JS - header always visible
		======================================*/
    $.scrollUp({
      scrollText: '<span><i class="fa fa-angle-up"></i></span>',
      easingType: 'easeInOutExpo',
      scrollSpeed: 900,
      animation: 'fade',
    });
  });

  /*=====================================
	  Preloader JS
	======================================*/
  //After 2s preloader is fadeOut
  $('.preloader').delay(2000).fadeOut('slow');
  setTimeout(function () {
    //After 2s, the no-scroll class of the body will be removed
    $('body').removeClass('no-scroll');
  }, 2000); //Here you can change preloader time
})(jQuery);

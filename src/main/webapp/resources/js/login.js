$(document).ready(function() {
  $('#login-form-link').click(function(e) {
    $("#login-form").delay(100).fadeIn(100);
    $("#signup-form").fadeOut(100);
    $('#signup-form-link').removeClass('active');
    $(this).addClass('active');
    e.preventDefault();
  });

  $('#signup-form-link').click(function(e) {
    $("#signup-form").delay(100).fadeIn(100);
    $("#login-form").fadeOut(100);
    $('#login-form-link').removeClass('active');
    $(this).addClass('active');
    e.preventDefault();
  });
});


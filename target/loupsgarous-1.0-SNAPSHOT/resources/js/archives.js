$(document).ready(function() {
  $(".table-chatroom").each(function() {
    this.scrollTop = this.scrollHeight;
  });
  
  $("#numeroJour").change(function() {
    $(".chatroom-visible").removeClass("chatroom-visible");
    $(".chatroom-jour-"+$(this).val()).addClass("chatroom-visible");
  });
  
  $("#numeroJour").val(1).trigger("change");
});

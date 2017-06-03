$(document).ready(function() {
  $('.dataTable').dataTable({
      language: {
          url: "//cdn.datatables.net/plug-ins/1.10.13/i18n/French.json"
      }
  });
  
  $(".datepicker").pickadate({
      format: "dd/mm/yyyy"
  });

  $(".timepicker").pickatime({
      format: "HH:i",
      interval: 15
  });

  $("#slider-user").slider({
    range: true,
    min:4,
    max:30,
    values: [5, 20],
    slide: function(event, ui) {
      $("#minParticipants")[0].value = ui.values[0];
      $("#maxParticipants")[0].value = ui.values[1];
      return $("#slider-user-amount").html(ui.values[0] + " - " + ui.values[1]);
    }
  });
  $("#slider-user-amount").html($("#slider-user").slider("values", 0) + " - " + $("#slider-user").slider("values", 1));

  $("#slider-lg").slider({
    range: "min",
    min:0,
    max:100,
    value: 33,
    slide: function(event, ui) {
      $("#proportionLG")[0].value = ui.value;
      return $("#slider-lg-amount").html(ui.value + "%");
    }
  });
  $("#slider-lg-amount").html($("#slider-lg").slider("value") + "%");

  $("#slider-contamination").slider({
    range: "min",
    min: 0,
    max: 100,
    value: 0,
    slide: function(event, ui) {
      $("#probaContamination")[0].value = ui.value;
      return $("#slider-contamination-amount").html(ui.value + "%");
    }
  });
  $("#slider-contamination-amount").html($("#slider-contamination").slider("value") + "%");

  $("#slider-insomnie").slider({
    range: "min",
    min: 0,
    max: 100,
    value: 0,
    slide: function(event, ui) {
      $("#probaInsomnie")[0].value = ui.value;
      return $("#slider-insomnie-amount").html(ui.value + "%");
    }
  });
  $("#slider-insomnie-amount").html($("#slider-insomnie").slider("value") + "%");

  $("#slider-voyance").slider({
    range: "min",
    min: 0,
    max: 100,
    value: 0,
    slide: function(event, ui) {
      $("#probaVoyance")[0].value = ui.value;
      return $("#slider-voyance-amount").html(ui.value + "%");
    }
  });
  $("#slider-voyance-amount").html($("#slider-voyance").slider("value") + "%");

  $("#slider-spiritisme").slider({
    range: "min",
    min: 0,
    max: 100,
    value: 0,
    slide: function(event, ui) {
      $("#probaSpiritisme")[0].value = ui.value;
      return $("#slider-spiritisme-amount").html(ui.value + "%");
    }
  });
  $("#slider-spiritisme-amount").html($("#slider-spiritisme").slider("value") + "%");

});


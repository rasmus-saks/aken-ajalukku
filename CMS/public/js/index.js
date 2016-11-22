var index = new Vue({
  el: '#index',
  data: {
    'pois': [],
    'journeys': []
  },
  created: function () {
    let context = this;
    $.get("/api/data", function (res) {
      context.$set(context, "journeys", res.journeys);
      context.$set(context, "pois", res.pois);
    });

  }

});
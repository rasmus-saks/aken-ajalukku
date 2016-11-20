var view = new Vue({
  el: '#view',
  data: {
    'pois': [],
    'journeys': []
  },
  created: function () {
    var context = this;
    $.get("/api/data", function (res) {
      console.log(context);
      context.$set(context,"journeys",res.journeys);
      context.$set(context,"pois",res.pois);
    });

  }

});
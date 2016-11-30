Vue.use(VeeValidate);
miniToastr.init();

var journey = new Vue({
  el: '#journey',
  data: {
    id: null,
    pois: [],
    title: {EN: null, ET: null},
    description: {EN: null, ET: null},
    clicked: false,
    delMessage: "Delete",
    allpois: []
  },
  created: function () {
    let context = this;
    if (journey_id != -1) {
      $.get({
        url: "/api/journey", data: {id: journey_id}, success: function (res) {
          Vue.set(context, "id", res.id);
          Vue.set(context, "pois", res.pois);
          Vue.set(context, "title", res.title);
          Vue.set(context, "description", res.description);
        }
      });
    } else {
      Vue.set(context, "id", journey_id);

    }
    $.get({
      url: "/api/pois", data: {id: journey_id}, success: function (res) {
        Vue.set(context, "allpois", res)
      }
    });
  },
  methods: {
    submitJourney: function () {
      this.$validator.validateAll();
      if (this.errors.any()) {
        return
      }
      if (journey_id == -1) {
        $.ajax({
          url: '/api/journey',
          type: 'POST',
          data: JSON.stringify({
            pois: this.pois,
            title: this.title,
            description: this.description,
          }),
          dataType: 'json',
          contentType: 'application/json',
          success: function (result) {
            window.location.href = '/';
          },
          error: function (result) {
            miniToastr.error(jQuery.parseJSON(result.responseText).error, "Error");
          }
        });
      } else {
        $.ajax({
          url: '/api/journey',
          type: 'PUT',
          data: JSON.stringify({
            id: this.id,
            pois: this.pois,
            title: this.title,
            description: this.description,
          }),
          dataType: 'json',
          contentType: 'application/json',
          success: function (result) {
            window.location.href = '/';
          },
          error: function (result) {
            miniToastr.error(jQuery.parseJSON(result.responseText).error, "Error");
          }
        });
      }
    },
    del: function () {
      if (this.clicked) {
        $.ajax({
          url: '/api/journey',
          type: 'DELETE',
          data: JSON.stringify({
            id: this.id,
          }),
          dataType: 'json',
          contentType: 'application/json',
          success: function (result) {
            window.location.href = '/';
          },
          error: function (result) {
            miniToastr.error(jQuery.parseJSON(result.responseText).error, "Error");
          }
        });
      } else {
        Vue.set(this, "delMessage", "Click again to DELETE");
        Vue.set(this, "clicked", true);
      }
    }
  }
});
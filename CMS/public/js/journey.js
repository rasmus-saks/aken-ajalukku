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
    allpois: [],
    statustext: "creating"
  },

  created: function () {
    let context = this;
    $.get({
      url: "/api/pois", data: {id: journey_id}, success: function (res) {
        Vue.set(context, "allpois", res);
        if (journey_id != -1) {
          $.get({
            url: "/api/journey", data: {id: journey_id}, success: function (res) {
              Vue.set(context, "id", res.id);
              Vue.set(context, "title", res.title);
              Vue.set(context, "description", res.description);
              context.addIdsToPois(res.pois);
              context.statustext = "editing";
            }
          });
        } else {
          Vue.set(context, "id", journey_id);

        }
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
            pois: this.poisToIdArray(),
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
            pois: this.poisToIdArray(),
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
    },

    findPoi: function (id) {
      for (let poi of this.allpois) {
        if (poi.id == id) {
          return poi;
        }
      }
      miniToastr.error("Something went wrong. Contact administrator.", "Error");
    },

    addPoi: function (id) {
      if (!this.isInPois(id)) {
        this.pois.push(this.findPoi(id));
      } else {
        miniToastr.error("PoI can only be in a journey once", "Error");
      }
    },

    isInPois: function (id) {
      for (let poi of this.pois) {
        if (poi.id == id) {
          return true;
        }
      }
      return false;
    },

    getPoiIndexById: function (id) {
      for (var i = 0; i < this.pois.length; i++) {
        if (this.pois[i].id == id) {
          return i;
        }
      }
    },

    removeFromPois: function (id) {
      this.pois.splice(this.getPoiIndexById(id), 1);
    },

    poisToIdArray: function () {
      let ret = [];
      for (let poi of this.pois) {
        ret.push(poi.id)
      }
      return ret
    },

    addIdsToPois: function (idarray) {
      for (let id of idarray) {
        this.addPoi(id)
      }
    },

    movePoiDown: function (id, event) {
      if (event) event.preventDefault();
      let index = this.getPoiIndexById(id);
      if (index < this.pois.length - 1) {
        let tmp = this.pois[index];
        Vue.set(this.pois, index, this.pois[index + 1]);
        Vue.set(this.pois, index + 1, tmp);
      }
    },

    movePoiUp: function (id, event) {
      if (event) event.preventDefault();
      let index = this.getPoiIndexById(id);
      if (index > 0) {
        let tmp = this.pois[index];
        Vue.set(this.pois, index, this.pois[index - 1]);
        Vue.set(this.pois, index - 1, tmp);
      }
    }
  }
});
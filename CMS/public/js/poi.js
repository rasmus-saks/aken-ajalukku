var poi = new Vue({
  el: '#poi',
  data: {
    id: null,
    title: {EN: null, ET: null},
    description: {EN: null, ET: null},
    lat: null,
    lon: null,
    video: null,
    img: null
  },
  created: function () {
    let context = this;
    if (poi_id != -1) {
      $.get({url: "/api/poi", data: {id: poi_id}, success: function (res) {
        context.$set(context, "id", res.id);
        context.$set(context, "title", res.title);
        context.$set(context, "description", res.description);
        context.$set(context, "lat", res.lat);
        context.$set(context, "lon", res.lon);
        context.$set(context, "video", res.video);
        context.$set(context, "img", res.img);
      }});
    }


  },
  methods: {
    submitPoi: function () {
      if (poi_id == -1) {
        $.post("/api/poi", {
          title: this.title,
          description: this.description,
          lat: this.lat,
          lon: this.lon,
          video: this.video,
          img: this.img
        }, function (result) {
          // Should redirect back to /
        });
      } else {
        $.ajax({
          url: '/api/poi',
          type: 'PUT',
          data: {
            id: this.id,
            title: this.title,
            description: this.description,
            lat: this.lat,
            lon: this.lon,
            video: this.video,
            img: this.img
          },
          success: function (result) {
            // Should redirect back to /
          }
        });
      }
    }
  }

});

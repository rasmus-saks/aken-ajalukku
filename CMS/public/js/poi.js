var poi = new Vue({
  el: '#poi',
  data: {
    id: 0,
    title: {EN: "Town hall square", ET: "Raekoja plats"},
    description: {EN: "Pretty cool square", ET: "Päris jahe ruut"},
    lat: 58.380144,
    lon: 26.7223035,
    video: "https://s3.eu-central-1.amazonaws.com/aken-ajalukku-media/efa0203_f_vi_03014_k_AVI_Microsoft_DV_PAL.mp4",
    img: "http://i.imgur.com/ewugjb2.jpg"
  },
  created: function () {
    let context = this;
    if (poi_id != -1) {
      $.get("/api/poi", {id: poi_id}, function (res) {
        context.$set(context, "id", res.id);
        context.$set(context, "title", res.title);
        context.$set(context, "description", res.description);
        context.$set(context, "lat", res.lat);
        context.$set(context, "lon", res.lon);
        context.$set(context, "video", res.video);
        context.$set(context, "img", res.img);
      });
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

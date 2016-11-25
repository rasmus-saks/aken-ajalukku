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
      $.get({
        url: "/api/poi", data: {id: poi_id}, success: function (res) {
          context.$set(context, "id", res.id);
          context.$set(context, "title", res.title);
          context.$set(context, "description", res.description);
          context.$set(context, "lat", res.lat);
          context.$set(context, "lon", res.lon);
          context.$set(context, "video", res.video);
          context.$set(context, "img", res.img);
        }
      });
    }


  },
  methods: {
    submitPoi: function () {
      if (poi_id == -1) {
        /**$.post("/api/poi", JSON.stringify({
          title: this.title,
          description: this.description,
          lat: this.lat,
          lon: this.lon,
          video: this.video,
          img: this.img
        }),function (result) {
          //TODO Should redirect back to /
        }, 'json');**/
        $.ajax({
          url: '/api/poi',
          type: 'POST',
          data: JSON.stringify({
            title: this.title,
            description: this.description,
            lat: this.lat,
            lon: this.lon,
            video: this.video,
            img: this.img
          }),
          dataType: 'json',
          contentType: 'application/json',
          success: function (result) {
            //TODO Should redirect back to /
          }
        });
      } else {
        $.ajax({
          url: '/api/poi',
          type: 'PUT',
          data: JSON.stringify({
            id: this.id,
            title: this.title,
            description: this.description,
            lat: this.lat,
            lon: this.lon,
            video: this.video,

            img: this.img
          }),
          dataType: 'json',
          contentType: 'application/json',
          success: function (result) {
            //TODO Should redirect back to /
          }
        });
      }
    }
  },
  watch: {
    lat: function (val) {
      marker.setPosition(new google.maps.LatLng(this.lat, this.lon));
    },
    lon: function (val) {
      marker.setPosition(new google.maps.LatLng(this.lat, this.lon));
    }
  }

});


// Based on solution from http://stackoverflow.com/questions/35053426/place-marker-on-click-google-maps-javascript-api

var myLatlng = new google.maps.LatLng(58.3776252, 26.7290063);
if (poi.lat != null && poi.lon != null) {
  myLatlng = new google.maps.LatLng(this.lat, this.lon);
}

var myOptions = {
  zoom: 8,
  center: myLatlng,
  mapTypeId: google.maps.MapTypeId.ROADMAP
};
var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

var marker = new google.maps.Marker({
  draggable: true,
  position: myLatlng,
  map: map,
  title: "Your location"
});

google.maps.event.addListener(marker, 'dragend', function (event) {
  Vue.set(poi, "lat", event.latLng.lat());
  Vue.set(poi, "lon", event.latLng.lng());
});

google.maps.event.addListener(map, 'click', function (event) {
  Vue.set(poi, "lat", event.latLng.lat());
  Vue.set(poi, "lon", event.latLng.lng());
  marker.setPosition(event.latLng);
});


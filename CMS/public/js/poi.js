var poi = new Vue({
  el: '#poi',
  data: {
    id: null,
    title: {EN: null, ET: null},
    description: {EN: null, ET: null},
    lat: null,
    lon: null,
    video: null,
    img: null,
    clicked: false,
    delMessage: "Delete"
  },
  created: function () {
    let context = this;
    if (poi_id != -1) {
      $.get({
        url: "/api/poi", data: {id: poi_id}, success: function (res) {
          Vue.set(context, "id", res.id);
          Vue.set(context, "title", res.title);
          Vue.set(context, "description", res.description);
          Vue.set(context, "lat", res.lat);
          Vue.set(context, "lon", res.lon);
          Vue.set(context, "video", res.video);
          Vue.set(context, "img", res.img);
        }
      });
    } else {
      Vue.set(context, "id", poi_id);
    }


  },
  methods: {
    submitPoi: function () {
      if (poi_id == -1) {
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
            window.location.href = '/';
            //TODO Display errors
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
            window.location.href = '/';
            //TODO Display errors
          }
        });
      }
    },
    del: function () {
      if (this.clicked) {
        $.ajax({
          url: '/api/poi',
          type: 'DELETE',
          data: JSON.stringify({
            id: this.id,
          }),
          dataType: 'json',
          contentType: 'application/json',
          success: function (result) {
            window.location.href = '/';
            //TODO Display errors
          }
        });
      } else {
        Vue.set(this, "delMessage", "Click again to DELETE");
        Vue.set(this, "clicked", true);
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


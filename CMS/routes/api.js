const express = require('express');
const router = express.Router();
const config = require("../config");
const debug = require('debug')('CMS:api');
const storage = require("../storage");
let mapsClient = null;
if (config.googleMapsApiKey) {
  mapsClient = require('@google/maps').createClient({
    key: config.googleMapsApiKey,
    Promise: Promise
  });
}
let data = {
  //Example data just to show the structure. Will be overwritten with persisted data shortly
  pois: [{
    id: 0,
    title: {EN: "Town hall square", ET: "Raekoja plats"},
    description: {EN: "Pretty cool square", ET: "Päris jahe ruut"},
    lat: 58.380144,
    lon: 26.7223035,
    video: "https://s3.eu-central-1.amazonaws.com/aken-ajalukku-media/efa0203_f_vi_03014_k_AVI_Microsoft_DV_PAL.mp4",
    img: "http://i.imgur.com/ewugjb2.jpg"
  }],
  journeys: [{
    id: 0,
    pois: [0, 1],
    title: {EN: "Tartu in the 90's", ET: "Tartu 90ndatel"},
    description: {EN: "I know journeys", ET: "Ma tean teekondi"},
    distance: 123456.45 //Meters
  }]
};

let lastIndex = {
  poi: -1,
  journey: -1
};
storage.init()
  .then(() => storage.get("data"))
  .then(value => {
    data = value || {pois: [], journeys: []};
    return storage.get("lastIndex")
  })
  .then(value => {
    lastIndex = value || {poi: -1, journey: -1}
  });


router.use(function (req, res, next) {
  res.success = function (data) {
    if (typeof data === 'object') {
      res.json(data);
      return;
    }
    res.json({
      data: data
    });
  };
  res.fail = function (err) {
    res.status(400).json({
      error: err.toString()
    });
  };
  next();
});

router.get('/', function (req, res) {
  res.send("Hello")
});

/**
 * GET /api/data
 * Gets all data
 */
router.get("/data", function (req, res) {
  res.success(data);
});

/**
 * GET /api/poi
 * Gets a PoI by its ID
 */
router.get("/poi", function (req, res) {
  let poi = findPoiById(req.query.id);
  if (!poi) return res.fail("Invalid PoI ID");
  res.success(poi);
});

/**
 * GET /api/pois
 * Gets all PoIs
 */
router.get("/pois", function (req, res) {
  res.success(data.pois);
});


/**
 * GET /api/journey
 * Gets a journey by its ID
 */
router.get("/journey", function (req, res) {
  let journey = findJourneyById(req.query.id);
  if (!journey) return res.fail("Invalid journey ID");
  res.success(journey);
});

/**
 * GET /api/journeys
 * Gets all journeys
 */
router.get("/journeys", function (req, res) {
  res.success(data.journeys);
});


router.use(require("simple-auth")(config.username, config.password));

/* === DATA === */

/**
 * DELETE /api/data
 * Deletes all data
 * Returns the empty data object
 */
router.delete("/data", function (req, res) {
  data = {pois: [], journeys: []};
  storage.set("data", data)
    .then(() => res.success(data))
    .catch(err => res.fail(err))
});

/* === POIS === */

/**
 * POST /api/poi
 * Adds a new PoI
 * Returns the newly added PoI (includes the assigned ID)
 */
router.post("/poi", function (req, res) {
  let val = req.body;
  if (!val.title) return res.fail("Title is required");
  if (!val.description) return res.fail("Description is required");
  if (!val.lat) return res.fail("Latitude is required");
  if (!val.lon) return res.fail("Longitude is required");
  val.id = ++lastIndex.poi;
  data.pois.push(val);
  storage.set("data", data)
    .then(() => storage.set("lastIndex", lastIndex))
    .then(() => res.success(val));
});

/**
 * PUT /poi
 * Update a PoI's information
 * Returns the updated PoI
 */
router.put("/poi", function (req, res) {
  let poi = findPoiById(req.body.id);
  if (!poi) return res.fail("Invalid PoI ID");
  for (let k of Object.keys(req.body)) {
    poi[k] = req.body[k]
  }
  storage.set("data", data)
    .then(() => res.success(poi));
});


/**
 * DELETE /api/poi
 * Deletes a poi
 * Returns {data: true}
 */
router.delete("/poi", function (req, res) {
  let poi = findPoiById(req.body.id);
  if (!poi) return res.fail("Invalid PoI ID");
  for (let j of data.journeys) {
    if (j.pois.indexOf(poi.id) != -1) return res.fail("PoI is part of journey ID " + j.id);
  }
  data.pois = data.pois.filter(p => p.id != poi.id);
  storage.set("data", data)
    .then(() => res.success(true));
});

/* === JOURNEYS === */

/**
 * POST /api/journey
 * Adds a new journey
 * Returns the newly added journey (with its assigned ID)
 */
router.post("/journey", function (req, res) {
  let val = req.body;
  if (!val.title) return res.fail("Title is required");
  if (!val.description) return res.fail("Description is required");
  if (!val.pois) return res.fail("PoI list is required");
  if (val.pois.length < 2) return res.fail("PoI list must have at least two PoIs");
  for (let pId of val.pois) {
    if (findPoiById(pId) == null) return res.fail("Invalid PoI ID " + pId);
  }

  val.id = ++lastIndex.journey;
  let mapsPromise = Promise.resolve(null);
  if (mapsClient) {
    let waypoints = [];
    for (let i = 1; i < val.pois.length - 1; i++) {
      let poi = findPoiById(val.pois[i]);
      waypoints.push(poi.lat + "," + poi.lon);
    }
    let origin = findPoiById(val.pois[0]);
    let dest = findPoiById(val.pois[val.pois.length - 1]);
    let opts = {
      origin: origin.lat + "," + origin.lon,
      destination: dest.lat + "," + dest.lon,
      mode: 'walking'
    };
    if (waypoints.length > 0) {
      opts["waypoints"] = waypoints.join("|");
    }
    mapsPromise = mapsClient.directions(opts).asPromise()
  }
  mapsPromise
    .then((response) => {
      if (response) val.distance = response.json.routes[0].legs.map(v => v.distance.value).reduce((s, v) => s + v, 0);
      data.journeys.push(val);
      return storage.set("data", data);
    })
    .then(() => storage.set("lastIndex", lastIndex))
    .then(() => res.success(val))
    .catch((err) => {
      res.fail(err);
      debug(err);
    });
});

/**
 * PUT /api/journey
 * Updates a journey
 * Returns the updated journey
 */
router.put("/journey", function (req, res) {
  let journey = findJourneyById(req.body.id);
  if (!journey) return res.fail("Invalid journey ID");
  if (req.body.pois) {
    for (let pId of req.body.pois) {
      if (findPoiById(pId) == null) return res.fail("Invalid PoI ID " + pId);
    }
  }
  for (let k of Object.keys(req.body)) {
    journey[k] = req.body[k]
  }
  storage.set("data", data)
    .then(() => res.success(journey));
});

/**
 * DELETE /api/journey
 * Deletes a journey
 * Returns {data: true}
 */
router.delete("/journey", function (req, res) {
  let journey = findJourneyById(req.body.id);
  if (!journey) return res.fail("Invalid journey ID");
  data.journeys = data.journeys.filter(p => p.id != journey.id);
  storage.set("data", data)
    .then(() => res.success(true));
});


function findPoiById(id) {
  for (let poi of data.pois) {
    if (poi.id == id) return poi;
  }
  return null;
}

function findJourneyById(id) {
  for (let journey of data.journeys) {
    if (journey.id == id) return journey;
  }
  return null;
}

module.exports = router;

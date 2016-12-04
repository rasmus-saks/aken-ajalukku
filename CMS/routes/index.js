var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', {title: 'Aken Ajalukku'});
});
router.get('/poi/new', function(req, res, next) {
  res.render('poi', {id: -1, title: 'Aken Ajalukku'});
});
router.get('/poi/:id', function(req, res, next) {
  res.render('poi', {id: req.params.id, title: 'Aken Ajalukku'});
});
router.get('/journey/new', function(req, res, next) {
  res.render('journey', {id: -1, title: 'Aken Ajalukku'});
});
router.get('/journey/:id', function(req, res, next) {
  res.render('journey', {id: req.params.id, title: 'Aken Ajalukku'});
});

module.exports = router;

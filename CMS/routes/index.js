var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});
router.get('/poi/new', function(req, res, next) {
  res.render('poi', { id: -1});
});
router.get('/poi/:id', function(req, res, next) {
  res.render('poi', { id: req.params.id});
});
router.get('/journey/new', function(req, res, next) {
  res.render('journey', { id: -1 });
});
router.get('/journey/:id', function(req, res, next) {
  res.render('journey', { id: req.params.id });
});

module.exports = router;

var request = require('supertest');
var express = require('express');
var app = require('../main');
var should = require("should");
var auth = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";
 
function apiRequest(method, path) {
  let r = request(app);
  return r[method]
    .call(r, path)
    .set("Authorization", auth)
    .expect("Content-Type", /json/);
 
}
describe("PoI API", function () {
  describe("DELETE /api/data", function () {
    it("should require authorization", function (done) {
      request(app)
        .delete("/api/data")
        .expect(401, done);
    });
    it("should delete all data and return an empty data object", function (done) {
      apiRequest("delete", "/api/data")
        .expect(200)
        .end(function (err, res) {
          res.body.should.be.eql({
            pois: [], journeys: []
          });
          done();
        });
    });
  });
 
  describe("POST /api/poi", function () {
    it("should require authorization", function (done) {
      request(app)
        .post("/api/poi")
        .expect(401, done);
    });
    it("should require title", function (done) {
      apiRequest("post", "/api/poi")
        .expect(400, done);
    });
    it("should require description", function (done) {
      apiRequest("post", "/api/poi")
        .send({title: {EN: "hi", ET: "hei"}})
        .expect(400, done);
    });
    it("should add a new PoI", function (done) {
      apiRequest("post", "/api/poi")
        .expect(200)
        .send({title: {EN: "hi", ET: "hei"}, description: {EN: "bye", ET: "nägemist"}})
        .end(function (err, res) {
          res.body.should.be.eql({title: {EN: "hi", ET: "hei"}, description: {EN: "bye", ET: "nägemist"}, id: 0});
          done(err);
        });
    });
  });
  describe("PUT /api/poi", function () {
    it("should require authorization", function (done) {
      request(app)
        .put("/api/poi")
        .expect(401, done);
    });
    it("should require id", function (done) {
      apiRequest("put", "/api/poi")
        .expect(400, done);
    });
    it("should update an existing PoI", function (done) {
      apiRequest("put", "/api/poi")
        .expect(200)
        .send({id: 0, title: {EN: "hi2", ET: "hei2"}})
        .end(function (err, res) {
          res.body.should.be.eql({title: {EN: "hi2", ET: "hei2"}, description: {EN: "bye", ET: "nägemist"}, id: 0});
          done(err);
        });
    });
  });
 
  describe("GET /api/poi", function () {
    it("should require id", function (done) {
      apiRequest("get", "/api/poi")
        .expect(400, done);
    });
    it("should get an existing PoI", function (done) {
      apiRequest("get", "/api/poi")
        .expect(200)
        .send({id: 0})
        .end(function (err, res) {
          res.body.should.be.eql({title: {EN: "hi2", ET: "hei2"}, description: {EN: "bye", ET: "nägemist"}, id: 0});
          done(err);
        });
    });
  });
});
describe("Journey API", function () {
  describe("POST /api/journey", function () {
    it("should require authorization", function (done) {
      request(app)
        .post("/api/journey")
        .expect(401, done);
    });
    it("should require title", function (done) {
      apiRequest("post", "/api/journey")
        .expect(400, done);
    });
    it("should require description", function (done) {
      apiRequest("post", "/api/journey")
        .send({title: {EN: "hi", ET: "hei"}})
        .expect(400, done);
    });
    it("should require PoI list", function (done) {
      apiRequest("post", "/api/journey")
        .send({title: {EN: "hi", ET: "hei"}, description: {EN: "bye", ET: "nägemist"}})
        .expect(400, done);
    });
    it("should add a new journey", function (done) {
      apiRequest("post", "/api/journey")
        .expect(200)
        .send({title: {EN: "hi", ET: "hei"}, description: {EN: "bye", ET: "nägemist"}, pois: [0, 1]})
        .end(function (err, res) {
          res.body.should.be.eql({
            title: {EN: "hi", ET: "hei"},
            description: {EN: "bye", ET: "nägemist"},
            pois: [0, 1],
            id: 0
          });
          done(err);
        });
    });
  });
  describe("PUT /api/journey", function () {
    it("should require authorization", function (done) {
      request(app)
        .put("/api/journey")
        .expect(401, done);
    });
    it("should require id", function (done) {
      apiRequest("put", "/api/journey")
        .expect(400, done);
    });
    it("should update an existing journey", function (done) {
      apiRequest("put", "/api/journey")
        .expect(200)
        .send({id: 0, title: {EN: "hi2", ET: "hei2"}})
        .end(function (err, res) {
          res.body.should.be.eql({
            title: {EN: "hi2", ET: "hei2"},
            description: {EN: "bye", ET: "nägemist"},
            id: 0,
            pois: [0, 1]
          });
          done(err);
        });
    });
  });
 
  describe("GET /api/journey", function () {
    it("should require id", function (done) {
      apiRequest("get", "/api/journey")
        .expect(400, done);
    });
    it("should get an existing journey", function (done) {
      apiRequest("get", "/api/journey")
        .expect(200)
        .send({id: 0})
        .end(function (err, res) {
          res.body.should.be.eql({
            title: {EN: "hi2", ET: "hei2"},
            description: {EN: "bye", ET: "nägemist"},
            id: 0,
            pois: [0, 1]
          });
          done(err);
        });
    });
  });
 
 
});

const config = require("./config");
if (global.testing || !config.accessKeyId || !config.secretAccessKey || !config.bucket || !config.region) {
  module.exports = require("node-persist");
  return;
}
const s3 = require("s3");
const Promise = require("bluebird");
const fs = require("fs");
const tempfile = ".s3temp.json";
const AWS = require('aws-sdk');
const data = {};
let client = null;
module.exports = {
  init: function () {
    return new Promise(function (resolve) {
      let awsS3Client = new AWS.S3({
        accessKeyId: config.accessKeyId,
        secretAccessKey: config.secretAccessKey,
        region: config.region
      });
      client = s3.createClient({
        s3Client: awsS3Client
      });
      resolve();
    });
  },
  get: function (key) {
    return new Promise(function (resolve) {
      if (data[key] !== undefined) return resolve(data[key]);
      return load(key).then((value) => {
        data[key] = value;
        return resolve(value);
      });
    });
  },
  set: function (key, value) {
    data[key] = value;
    return store(key, value);
  }
};

function store(key, data) {
  return new Promise(function (resolve, reject) {
    fs.writeFile(tempfile, JSON.stringify(data), function (err) {
      if (err) return reject(err);
      const params = {
        localFile: tempfile,
        s3Params: {
          Bucket: config.bucket,
          Key: key + ".json",
          ACL: "public-read"
        },
      };
      const uploader = client.uploadFile(params);
      uploader.on('error', function (err) {
        reject(err);
      });
      uploader.on('end', function () {
        fs.unlink(tempfile, function (err) {
          if (err) return reject(err);
          resolve();
        })
      });
    });
  });

}

function load(key) {
  return new Promise(function (resolve, reject) {
    const params = {
      localFile: tempfile,
      s3Params: {
        Bucket: config.bucket,
        Key: key + ".json"
      }
    };
    const downloader = client.downloadFile(params);
    downloader.on('error', function (err) {
      if (err.toString().endsWith("http status code 404")) return resolve(undefined);
      reject(err.stack)
    });
    downloader.on('end', function () {
      fs.readFile(tempfile, function (err, data) {
        if (err) return reject(err);
        fs.unlink(tempfile, function (err) {
          if (err) return reject(err);
          resolve(JSON.parse(data));
        });
      });
    });
  });

}
# Aken Ajalukku - CMS
This is the Content Management System for the Aken Ajalukku project. The purpose of this webapp is to serve as the back-end server for the Android app. Administrators are able to add PoIs to the system through
a management interface. The app requests this information in order to display the PoIs on its user's screen.

# Building/running
Make sure you have [node.js](https://nodejs.org/en/) at least version 6.0.

Run `npm install` then rename/copy `config.example.js` to `config` and edit the settings inside it.
To start the server, run `npm start` or `node bin/www`

# Testing
Use `npm test` to run the unit tests

# License
This project is released under the GNU GPLv3 license, for more information see `LICENSE.txt`

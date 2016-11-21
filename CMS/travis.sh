#!/usr/bin/env bash
. $HOME/.nvm/nvm.sh
nvm install stable
nvm use stable
npm install
npm test
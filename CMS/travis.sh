#!/usr/bin/env bash
set -e
. $HOME/.nvm/nvm.sh
nvm install stable
nvm use stable
npm install
npm test
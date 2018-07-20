#!/bin/bash

set -o xtrace
set -o errexit

# Make sure no old images exists.
sudo rm -rf /var/lib/docker

sudo apt-get update
sudo apt-get -y install apt-transport-https
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
sudo apt-get update
sudo apt-get -y install --no-install-recommends docker-ce

sudo docker info

echo "Completed $0."

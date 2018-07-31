======================================
Docker base image for Monasca services
======================================

This image is used as a starting point for images of all Monasca services.


Building monasca-base image
===========================

You need to have Docker installed (minimum tested version is ``17.09``).
Then you can build image inside of this folder by running:

``docker build --no-cache -t monasca-base:1.0.0 .``


Building child image
--------------------

In the ``example`` folder you can find samples of how to start building
new child image using ``monasca-base``.

Requirements
~~~~~~~~~~~~

Every child image has to provide two files:

start.sh
  In this starting script provide all steps that lead to the proper service
  start. Including usage of wait scripts and templating of configuration files.
  You also could provide the ability to allow running container after service
  died for easier debugging.

health_check.py
  This file will be used for checking the status of the application running in
  the container. It should be used to inform Docker that service is operating
  and healthy. It will be useful for container orchestration like Kubernetes
  or Docker Swarm to properly handle services that are still running but
  stopped being responsive. Avoid using `curl` directly and instead, use
  `health_check.py` written with specific service in mind. It will provide more
  flexibility like when creating JSON request body.


Wait scripts
------------

Some Python libraries are already pre-installed: `pykafka` and `PyMySQL`. They
are used by wait scripts and in the process of creating the child image `pip3`
will reinstall them to use proper versions confronting the upper constraints
file.

These wait scripts will be available in every child image and can be used in
`start.sh` to avoid unnecessary errors and restarts of containers when they
are started.

::

    python3 /kafka_wait_for_topics.py || exit 1
    python3 /mysql_check.py || exit 1
    /wait_for.sh 192.168.10.6:5000 || exit 1

Please, check the content of each of these files for documentation of what
environment variables are used and more usage examples.


Useful commands
---------------

List all labels of the image (you need to have ``jq`` installed):

``docker inspect monasca-api:master | jq .[].Config.Labels``

Get all steps of the Docker image build process history:

::

    docker history --no-trunc <IMAGE_ID>
    docker history --no-trunc monasca-base:1.0.0

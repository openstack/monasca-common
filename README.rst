Team and repository tags
========================

.. image:: https://governance.openstack.org/tc/badges/monasca-common.svg
    :target: https://governance.openstack.org/tc/reference/tags/index.html

.. Change things from this point on

Overview
========

``monasca-common`` is a collection of modules containing reusable application
and platform code for building monitoring related services.

Python
======

To install the Python monasca-common modules, git clone the source and
run the following command:

::

   $ sudo python setup.py install

To run the unit tests use:

::

   $ tox -e py35

For information on contributing, see `Contribution Guidelines`_.

* License: Apache License, Version 2.0
* Source: https://opendev.org/openstack/monasca-common
* Bugs: https://storyboard.openstack.org/#!/project/865

.. _`Contribution Guidelines`: https://docs.openstack.org/monasca-api/latest/contributor/index.html

Java
======

Build Instructions
~~~~~~~~~~~~~~~~~~

Download and do mvn install.

::

   $ cd java
   $ mvn clean install

.. caution::

  There is a pom.xml in the base directory but that should only be used
  for the Zuul build.

The issue is that currently StackForge’s bare-precise system only has Maven 2
on it and at least one of the modules of monasca-common requires Maven 3.

In order to get around this problem, the pom.xml in the base directory
uses the exec-maven-plugin to run the script run_maven.sh. This script
checks if the version of mvn is Maven 3 and if it is not, it downloads
Maven 3 and then uses it to run the build in the java directory.

In addition, the run_maven.sh script copies the jar files that get built
from java/``*``/target directories to the target directory in the base
project directory. This is because the StackForge
“monasca-common-localrepo-upload” job uploads any jar files from that
directory to http://tarballs.openstack.org/ci/monasca-common.
Copying the jar files to that directory made it so there didn’t have to
be changes made to the “monasca-common-localrepo-upload” job. The build
for monasca-thresh downloads the jars it depends on from that location on
tarballs.openstack.org.

A change has been submitted to StackForge to switch to bare-trusty for
this build in the hopes that those systems will have maven 3, but it is
not known how long that change will take to be accepted.

Application Specific Sub-Projects
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  `monasca-common-model`_ - Common domain and event models for
   monitoring services.

Platform Sub-Projects
~~~~~~~~~~~~~~~~~~~~~

-  `monasca-common-dropwizard`_ - Utilities for building and testing
   dropwizard services.
-  `monasca-common-hibernate`_ - Hibernate based model of Monasca SQL
   schema
-  `monasca-common-influxdb`_ - InfluxDB related utilities.
-  `monasca-common-kafka`_ - Various utilities for working with Kafka.
-  `monasca-common-persistence`_ - Persistence related infrastructure
   and utilities.
-  `monasca-common-streaming`_ - Streaming related utilities.
-  `monasca-common-testing`_ - A set of testing related dependencies.
-  `monasca-common-util`_ - Various utilities such as for serialization,
   dependency injection, date and time, invocation retries, concurrency,
   etc.



.. _monasca-common-model: https://github.com/openstack/monasca-common/tree/master/java/monasca-common-model
.. _monasca-common-dropwizard: https://github.com/openstack/monasca-common/tree/master/java/monasca-common-dropwizard
.. _monasca-common-hibernate: https://github.com/openstack/monasca-common/tree/master/java/monasca-common-hibernate
.. _monasca-common-influxdb: https://github.com/openstack/monasca-common/tree/master/java/monasca-common-influxdb
.. _monasca-common-kafka: https://github.com/openstack/monasca-common/tree/master/java/monasca-common-kafka
.. _monasca-common-persistence: https://github.com/openstack/monasca-common/tree/master/java/monasca-common-persistence
.. _monasca-common-streaming: https://github.com/openstack/monasca-common/tree/master/java/monasca-common-streaming
.. _monasca-common-testing: https://github.com/openstack/monasca-common/tree/master/java/monasca-common-testing
.. _monasca-common-util: https://github.com/openstack/monasca-common/tree/master/java/monasca-common-util

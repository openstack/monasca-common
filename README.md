Team and repository tags
========================

[![Team and repository tags](https://governance.openstack.org/badges/monasca-common.svg)](https://governance.openstack.org/reference/tags/index.html)

<!-- Change things from this point on -->

# Overview

`monasca-common` is a collection of sub-projects containing reusable application and platform code for building monitoring related services.

### Build Instructions
Download and do mvn install.

    $ cd java
    $ mvn clean install

There is a pom.xml in the base directory but that should only be used for the StackForge build. The issue is that currently StackForge's bare-precise system only has Maven 2 on it and at least one of the modules of monasca-common requires Maven 3.

In order to get around this problem, the pom.xml in the base directory uses the exec-maven-plugin to run the script run_maven.sh. This script checks if the version of mvn is Maven 3 and if it is not, it downloads Maven 3 and then uses it to run the build in the java directory.

In addition, the run_maven.sh script copies the jar files that get built from java/\*/target directories to the target directory in the base project directory. This is because the StackForge "monasca-common-localrepo-upload" job uploads any jar files from that directory to http://tarballs.openstack.org/ci/monasca-common. Copying the jar files to that directory made it so there didn't have to be changes made to the "monasca-common-localrepo-upload" job. The build for monasca-thresh downloads the jars it depends on from that location on tarballs.openstack.org.

A change has been submitted to StackForge to switch to bare-trusty for this build in the hopes that those systems will have maven 3, but it is not known how long that change will take to be accepted.

### Application Specific Sub-Projects

* [monasca-common-model](https://github.com/openstack/monasca-common/tree/master/java/monasca-common-model) - Common domain and event models for monitoring services.

### Platform Sub-Projects

* [monasca-common-dropwizard](https://github.com/openstack/monasca-common/tree/master/java/monasca-common-dropwizard) - Utilities for building and testing dropwizard services.
* [monasca-common-kafka](https://github.com/openstack/monasca-common/tree/master/java/monasca-common-kafka) - Various utilities for working with Kafka.
* [monasca-common-persistence](https://github.com/openstack/monasca-common/tree/master/java/monasca-common-persistence) - Persistence related infrastructure and utilities.
* [monasca-common-service](https://github.com/openstack/monasca-common/tree/master/java/monasca-common-service) - Simple service abstractions and utilities.
* [monasca-common-streaming](https://github.com/openstack/monasca-common/tree/master/java/monasca-common-streaming) - Streaming related utilities.
* [monasca-common-testing](https://github.com/openstack/monasca-common/tree/master/java/monasca-common-testing) - A set of testing related dependencies.
* [monasca-common-util](https://github.com/openstack/monasca-common/tree/master/java/monasca-common-util) - Various utilities such as for serialization, dependency injection, date and time, invocation retries, concurrency, etc.
* [monasca-common-hibernate](https://github.com/openstack/monasca-common/tree/master/java/monasca-common-hibernate) - Hibernate based model of Monasca SQL schema

python monasca-common
======================

To install the python monasca-common modules, git clone the source and run the
following command::

    $ sudo python setup.py install

To run the python monasca-common tests use::

    $ nosetests monasca_common/tests

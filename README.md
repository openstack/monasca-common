# Overview

`monasca-common` is a collection of sub-projects containing reusable application and platform code for building monitoring related services.

### Build Instructions
Download and do mvn install.
```
cd java
mvn clean install
```

There is a pom.xml in the base directory but that should only be used for the StackForge build. The issue is that currently StackForge's bare-precise system only has Maven 2 on it and at least one of the modules of monasca-common requires Maven 3.

In order to get around this problem, the pom.xml in the base directory uses the exec-maven-plugin to run the script run_maven.sh. This script checks if the version of mvn is Maven 3 and if it is not, it downloads Maven 3 and then uses it to run the build in the java directory.

In addition, the run_maven.sh script copies the jar files that get built from java/\*/target directories to the target directory in the base project directory. This is because the StackForge "monasca-common-localrepo-upload" job uploads any jar files from that directory to http://tarballs.openstack.org/ci/monasca-common. Copying the jar files to that directory made it so there didn't have to be changes made to the "monasca-common-localrepo-upload" job. The build for monasca-thresh downloads the jars it depends on from that location on tarballs.openstack.org.

A change has been submitted to StackForge to switch to bare-trusty for this build in the hopes that those systems will have maven 3, but it is not known how long that change will take to be accepted.

### Application Specific Sub-Projects

* [mon-model](https://git.hpcloud.netstackforge/monasca-common/tree/master/mon-model) - Common domain and event models for monitoring services.

### Platform Sub-Projects

* [mon-dropwizard](https://git.hpcloud.netstackforge/monasca-common/tree/master/mon-dropwizard) - Utilities for building and testing dropwizard services.
* [mon-kafka](https://git.hpcloud.netstackforge/monasca-common/tree/master/mon-kafka) - Various utilities for working with Kafka.
* [mon-persistence](https://git.hpcloud.netstackforge/monasca-common/tree/master/mon-persistence) - Persistence related infrastructure and utilities.
* [mon-service](https://git.hpcloud.netstackforge/monasca-common/tree/master/mon-service) - Simple service abstractions and utilities.
* [mon-streaming](https://git.hpcloud.netstackforge/monasca-common/tree/master/mon-streaming) - Streaming related utilities.
* [mon-testing](https://git.hpcloud.netstackforge/monasca-common/tree/master/mon-testing) - A set of testing related dependencies.
* [mon-util](https://git.hpcloud.netstackforge/monasca-common/tree/master/mon-util) - Various utilities such as for serialization, dependency injection, date and time, invocation retries, concurrency, etc.

### Deploying to Nexus

To deploy to nexus, just run the command below, inserting a version number that is greater than the current nexus version (this command is usually performed by Jenkins):

```
mvn deploy -DBUILD_NUM=${BUILD_NUMBER}
```

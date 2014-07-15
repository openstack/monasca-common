# Overview

`mon-common` is a collection of sub-projects containing reusable application and platform code for building monitoring related services.

### Build Instructions
Download and do mvn install.
```
mvn clean install
```

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

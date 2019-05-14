# Example Dockerfile for creating Docker image.
ARG DOCKER_IMAGE=monasca-api
ARG APP_REPO=https://review.opendev.org/openstack/monasca-api

# Branch, tag or git hash to build from.
ARG REPO_VERSION=master
ARG CONSTRAINTS_BRANCH=master
ARG COMMON_VERSION=master

# Extra Python3 dependencies.
ARG EXTRA_DEPS="gunicorn influxdb python-memcached"

# Always start from `monasca-base` image and use specific tag of it.
ARG BASE_TAG=1.0.0
FROM monasca/base:$BASE_TAG

# Environment variables used for our service or wait scripts.
ENV \
    KAFKA_URI=kafka:9092 \
    KAFKA_WAIT_FOR_TOPICS=alarm-state-transitions,metrics \
    MYSQL_HOST=mysql \
    MYSQL_USER=monapi \
    MYSQL_PASSWORD=password \
    MYSQL_DB=mon \
    LOG_LEVEL=INFO \
    STAY_ALIVE_ON_FAILURE="false"

# Copy all neccessary files to proper locations.
COPY config_1.yml.j2 config_2.yml.j2 /

# Run here all additionals steps your service need post installation.
# Stay with only one `RUN` and use `&& \` for next steps to don't create
# unnecessary image layers. Clean at the end to conserve space.
RUN \
    echo "Some steps to do after main installation." && \
    echo "Hello when building."

# Expose port for specific service.
EXPOSE 1234

# Implement start script in `start.sh` file.
CMD ["/start.sh"]

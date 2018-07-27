# TODO(Dobroslaw): Update to Alpine 3.8.
# We can't update to Alpine 3.8 until pyca/cryptography is updated in
# upper constrains file to be bigger than 2.2.2.
# LibreSSL 2.7.x is not supported by older versions:
# https://github.com/pyca/cryptography/pull/4270
FROM python:3.5.5-alpine3.7

COPY wait_for.sh kafka_wait_for_topics.py mysql_check.py /
COPY ashrc /root/.ashrc

ENV \
    ENV="/root/.ashrc" \
    PIP_NO_CACHE_DIR="no" \
    PIP_NO_COMPILE="no" \
    PYTHONIOENCODING="utf-8"

ARG BASE_CREATION_TIME
ARG BASE_GIT_COMMIT

LABEL org.opencontainers.image.created="$BASE_CREATION_TIME"
LABEL org.opencontainers.image.title="monasca-base"
LABEL org.opencontainers.image.revision="$BASE_GIT_COMMIT"
LABEL org.opencontainers.image.licenses="Apache-2.0"

RUN \
    chmod +x /wait_for.sh /kafka_wait_for_topics.py /mysql_check.py && \
    apk add --no-cache \
        su-exec=0.2-r0 \
        tini=0.16.1-r0 \
        # We need this to allow users choose different time zone.
        tzdata=2017c-r0 && \
    printf "Monasca base build date: %s\\n" $BASE_CREATION_TIME >> /VERSIONS && \
    printf "Monasca base revision:   %s\\n" $BASE_GIT_COMMIT >> /VERSIONS && \
    # Cleaning.
    rm -rf /var/cache/apk/* && \
    rm -rf /var/log/* && \
    rm -rf /tmp/*

# Get values from child images
ONBUILD ARG CREATION_TIME
ONBUILD ARG DOCKER_IMAGE
ONBUILD ARG APP_REPO
ONBUILD ARG GITHUB_REPO
ONBUILD ARG REPO_VERSION
ONBUILD ARG GIT_COMMIT
ONBUILD ARG CONSTRAINTS_BRANCH
ONBUILD ARG CONSTRAINTS_FILE
ONBUILD ARG EXTRA_DEPS
ONBUILD ARG COMMON_REPO
ONBUILD ARG COMMON_VERSION
ONBUILD ARG COMMON_GIT_COMMIT

# Build-time metadata as defined at
# https://github.com/opencontainers/image-spec/blob/master/annotations.md
ONBUILD LABEL org.opencontainers.image.created="$CREATION_TIME"
ONBUILD LABEL org.opencontainers.image.title="$DOCKER_IMAGE"
ONBUILD LABEL org.opencontainers.image.source="$APP_REPO"
ONBUILD LABEL org.opencontainers.image.url="$GITHUB_REPO"
ONBUILD LABEL org.opencontainers.image.version="$REPO_VERSION"
ONBUILD LABEL org.opencontainers.image.revision="$GIT_COMMIT"
ONBUILD LABEL org.opencontainers.image.licenses="Apache-2.0"
ONBUILD LABEL org.openstack.constraints_uri="$CONSTRAINTS_FILE?h=$CONSTRAINTS_BRANCH"
ONBUILD LABEL org.openstack.monasca.python.extra_deps="$EXTRA_DEPS"
ONBUILD LABEL org.openstack.monasca.common.source="$COMMON_REPO"
ONBUILD LABEL org.openstack.monasca.common.version="$COMMON_VERSION"
ONBUILD LABEL org.openstack.monasca.common.revision="$COMMON_GIT_COMMIT"

# Every child image need to provide starting and health check script.
# If they're not provided build will fail. We want that for uniformity.
ONBUILD COPY start.sh health_check.py /

ONBUILD WORKDIR /

ONBUILD SHELL ["/bin/ash", "-eo", "pipefail", "-c"]
ONBUILD RUN \
            chmod +x /start.sh && \
            apk add --no-cache --virtual .build-deps \
                g++=6.4.0-r5 \
                git=2.15.2-r0 \
                libffi-dev=3.2.1-r4 \
                libressl-dev=2.6.5-r0 \
                linux-headers=4.4.6-r2 \
                make=4.2.1-r0 && \
            # Clone repository and checkout requested version.
            # This many steps are needed to support gerrit patch sets.
            mkdir -p /app && \
            git -C /app init && \
            git -C /app remote add origin "$APP_REPO" && \
            echo "Cloning app in version: $REPO_VERSION" && \
            git -C /app fetch origin "$REPO_VERSION" && \
            git -C /app reset --hard FETCH_HEAD && \
            wget --output-document /app/upper-constraints.txt \
                "$CONSTRAINTS_FILE"?h="$CONSTRAINTS_BRANCH" && \
            # Download monasca-common
            mkdir -p /monasca-common && \
            git -C /monasca-common init && \
            git -C /monasca-common remote add origin "$COMMON_REPO" && \
            echo "Cloning monasca-common in version: $COMMON_VERSION" && \
            git -C /monasca-common fetch origin "$COMMON_VERSION" && \
            git -C /monasca-common reset --hard FETCH_HEAD && \
            # When creating image from master, stable branch or commit use
            # monasca-common from git repository in /monasca-common,
            # otherwise use the version from upper-constraints.txt
            [ ! "$(git -C /monasca-common tag -l ${COMMON_VERSION})" ] && \
                sed -i "s|monasca-common.*||" \
                    /app/requirements.txt || true && \
            [ ! "$(git -C /monasca-common tag -l ${COMMON_VERSION})" ] && \
                sed -i "s|monasca-common.*||" \
                    /app/upper-constraints.txt || true && \
            pip3 install \
                --editable /monasca-common \
                --constraint /app/upper-constraints.txt && \
            # Install packages needed by wait scripts and used for templating.
            pip3 install \
                pykafka \
                PyMySQL \
                Templer==1.1.4 \
                --constraint /app/upper-constraints.txt && \
            # Install our application with extra dependencies if provided.
            pip3 install \
                /app/. $EXTRA_DEPS \
                --requirement /app/requirements.txt \
                --constraint /app/upper-constraints.txt && \
            # Save info about build to `/VERSIONS` file.
            printf "App:        %s\\n" "$DOCKER_IMAGE" >> /VERSIONS && \
            printf "Repository: %s\\n" "$APP_REPO" >> /VERSIONS && \
            printf "Version:    %s\\n" "$REPO_VERSION" >> /VERSIONS && \
            printf "Revision:   %s\\n" "$GIT_COMMIT" >> /VERSIONS && \
            printf "Build date: %s\\n" "$CREATION_TIME" >> /VERSIONS && \
            printf "Revision:   %s\\n" \
                "$(git -C /app rev-parse FETCH_HEAD)" >> /VERSIONS && \
            printf "Monasca-common version:     %s\\n" "$COMMON_VERSION" \
                >> /VERSIONS && \
            printf "Monasca-common pip version: %s\\n" \
                "$(pip3 freeze 2>&1 | grep 'monasca-common')" >> /VERSIONS && \
            printf "Monasca-common revision:    %s\\n" \
                "$COMMON_GIT_COMMIT" >> /VERSIONS && \
            printf "Constraints file: %s\\n" \
                "$CONSTRAINTS_FILE"?h="$CONSTRAINTS_BRANCH" >> /VERSIONS && \
            # Clean after instalation.
            apk del .build-deps && \
            rm -rf \
                /app \
                /root/.cache/ \
                /monasca-common/java/ \
                /tmp/* \
                /var/cache/apk/* \
                /var/log/* && \
            # Remove all Python pyc and pyo files.
            find /usr/local -depth \
                \( \
                    \( -type d -a \( -name test -o -name tests \) \) \
                    -o \( -type f -a \( -name '*.pyc' -o -name '*.pyo' \) \) \
                \) -exec rm -rf '{}' +

ONBUILD HEALTHCHECK --interval=5s --timeout=2s \
                   CMD python3 health_check.py || exit 1

ENTRYPOINT ["/sbin/tini", "-s", "--"]

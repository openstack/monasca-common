#!/bin/bash

set -x  # Print each script step.
set -eo pipefail  # Exit the script if any statement returns error.

REAL_PATH=$(python -c "import os,sys;print(os.path.realpath('$0'))")
cd "$(dirname "$REAL_PATH")/../docker/"

BASE_GIT_COMMIT=$(git rev-parse --verify HEAD)
[ -z "${BASE_GIT_COMMIT}" ] && echo "No git commit hash found" && exit 1

BASE_CREATION_TIME=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

: "${MONASCA_COMMON_TAG:="master"}"

docker build --no-cache \
    --build-arg BASE_CREATION_TIME="$BASE_CREATION_TIME" \
    --build-arg BASE_GIT_COMMIT="$BASE_GIT_COMMIT" \
    --tag monasca/base:"$MONASCA_COMMON_TAG" .

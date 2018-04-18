#!/usr/bin/env sh

set -e

echo "Login to Docker hub"
echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin

echo "Building latest Docker image"
docker build -t civ5pbem/civ5-pbem-server:latest .
echo "Finished building latest Docker image"

set +e

if [ "${TRAVIS_BRANCH}" = "master" ]; then
    set -e
    echo "Publishing latest Docker image"
    docker push civ5pbem/civ5-pbem-server:latest
    echo "Published latest Docker image!"
else
    echo "Skipping publishing latest because of branch: ${TRAVIS_BRANCH}"
fi

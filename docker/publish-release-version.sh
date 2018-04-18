#!/usr/bin/env sh

set -e

echo "Tagging Docker image: civ5pbem/civ5-pbem-server:${APP_VERSION}"
docker tag civ5pbem/civ5-pbem-server:latest civ5pbem/civ5-pbem-server:${APP_VERSION}
echo "Finished tagging Docker image"

echo "Publishing released Docker image: civ5pbem/civ5-pbem-server:${APP_VERSION}"
docker push civ5pbem/civ5-pbem-server:${APP_VERSION}
echo "Published released Docker image!"

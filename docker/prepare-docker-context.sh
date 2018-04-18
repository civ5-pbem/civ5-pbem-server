#!/usr/bin/env sh

echo "Preparing Docker context"

cp ../build/libs/*.jar .
if [ $(ls . | grep '.jar' | wc -l) -ne 1 ]; then
    echo "Too many files found:\n$(ls . | grep '.jar')"
    exit 1
fi

mv $(ls . | grep '.jar') civ5-pbem-server.jar

echo "Finished preparing Docker context"

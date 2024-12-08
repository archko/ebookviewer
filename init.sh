#!/bin/bash

git submodule update --init --recursive --force
git submodule foreach --recursive git clean -dfx

cd app/jni/mupdf
make generate
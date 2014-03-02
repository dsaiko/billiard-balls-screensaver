#!/bin/bash

version=$(git describe)
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$version


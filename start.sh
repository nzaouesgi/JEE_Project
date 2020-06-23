#!/bin/bash

set -e

for service in "$@"
do
   ./wait-for-it.sh -t 150 "$service"
done

$*
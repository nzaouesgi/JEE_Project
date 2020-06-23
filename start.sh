#!/bin/bash

set -e

for service in "$@"
do
   ./wait-for-it.sh -t 75 "$service"
done
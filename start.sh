#!/bin/bash

set -e

declare -a services=("database:3306" "smtp:25" "storage:10000" "storage:10001")

for service in "${services[@]}"
do
   ./wait-for-it.sh -t 150 "$service"
done

$*
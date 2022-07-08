#!/usr/bin/env bash
set -euo pipefail

files=( "$1"/features/*.jar )
file=${files[0]}
version=${file#*_}
version=${version%.jar}

echo "${version}"
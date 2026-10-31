#!/usr/bin/env sh
DIR="$(cd "$(dirname "$0")/05_neoforge_port" && pwd)"
exec "$DIR/gradlew" -p "$DIR" "$@"

#!/usr/bin/env bash
set -euo pipefail

cd /app

checksum() {
  find src -type f \( \
      -name '*.java' -o -name '*.properties' -o -name '*.yaml' -o \
      -name '*.yml' -o -name '*.xml' -o -name '*.html' -o -name '*.js' -o -name '*.css' \
    \) -print0 2>/dev/null \
    | sort -z \
    | xargs -0 cat 2>/dev/null \
    | md5sum
}

./mvnw -B spring-boot:run -DskipTests &
APP_PID=$!

OLD="$(checksum || true)"

while kill -0 "$APP_PID" 2>/dev/null; do
  sleep 2
  NEW="$(checksum || true)"
  if [[ "$NEW" != "$OLD" ]]; then
    OLD="$NEW"
    echo "Source changed — recompiling..."
    ./mvnw -B compile -DskipTests -q || echo "Compile failed — fix errors and save again."
  fi
done

wait "$APP_PID"

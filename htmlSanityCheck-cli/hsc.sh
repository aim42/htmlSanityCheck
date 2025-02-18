#!/bin/sh

set -euo pipefail

# shellcheck disable=SC2068
exec java -jar /hsc.jar ${*}

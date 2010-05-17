#!/usr/bin/env bash

export SLEEP_INTERVAL=1
export BASE_URL=http://localhost:9998
export CURL_OPTS="--verbose"

function precurl {
  echo ""
  echo "::~~~"

}

function postcurl {
  sleep ${SLEEP_INTERVAL}
  echo ""
  echo "~~~::"
}

function curlit {
  precurl
  echo "::" $1 "::"
  curl ${CURL_OPTS} ${BASE_URL}/$2
  postcurl
}

curlit "Hello - Hello World" "hello/helloworld"
curlit "Hello - Scala Markup" "hello/markup"
curlit "Simple Console - Form" "simple_console/form"
curlit "Simple Console - Form Colors" "simple_console/form/colours"
curl http://127.0.0.1:9998/storage/containers

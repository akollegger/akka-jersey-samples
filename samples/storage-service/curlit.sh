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

curl ${CURL_OPTS} ${BASE_URL}/storage/containers
curl -X PUT ${CURL_OPTS} ${BASE_URL}/storage/containers/quotes
curl ${CURL_OPTS} ${BASE_URL}/storage/containers
curl -X PUT -HContent-type:text/plain --data "Something is rotten in the state of Denmark"  ${CURL_OPTS} ${BASE_URL}/storage/containers/quotes/1
curl -X PUT -HContent-type:text/plain --data "I could be bounded in a nutshell" ${CURL_OPTS} ${BASE_URL}/storage/containers/quotes/2
curl -X PUT -HContent-type:text/plain --data "catch the conscience of the king" ${CURL_OPTS} ${BASE_URL}/storage/containers/quotes/3 
curl -X PUT -HContent-type:text/plain --data "Get thee to a nunnery" ${CURL_OPTS} ${BASE_URL}/storage/containers/quotes/4 
curl ${CURL_OPTS} ${BASE_URL}/storage/containers/quotes
curl ${CURL_OPTS} ${BASE_URL}"/storage/containers/quotes?search=king"
curl ${CURL_OPTS} -X PUT -HContent-type:text/plain --data "The play's the thing Wherein I'll catch the conscience of the king" ${BASE_URL}/storage/containers/quotes/3
curl ${CURL_OPTS} ${BASE_URL}"/storage/containers/quotes?search=king"
curl ${CURL_OPTS} ${BASE_URL}/storage/containers/quotes/3
curl ${CURL_OPTS} ${BASE_URL}"/storage/containers/quotes?search=king"
curl ${CURL_OPTS} -X DELETE ${BASE_URL}/storage/containers/quotes
curl ${CURL_OPTS} ${BASE_URL}/storage/containers

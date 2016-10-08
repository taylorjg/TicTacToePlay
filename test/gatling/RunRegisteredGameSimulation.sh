#!/bin/bash
JAVA_OPTS="-DnumUsers=${1:-100} -Dramp=${2:-25}" \
    $GATLING_HOME/bin/gatling.sh \
    -sf ./simulations \
    -s gatling.simulations.RegisteredGameSimulation

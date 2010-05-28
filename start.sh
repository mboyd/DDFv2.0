#!/bin/bash

# Shutdown any running servers
ps a | grep '[j]ava -jar DDF' | cut -d' ' -f 2 | xargs kill

# Start
java -jar DDFv2.0.jar -server -f local:config.xml

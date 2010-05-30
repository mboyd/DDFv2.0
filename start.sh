#!/bin/bash

# Shutdown any running servers
PID=`ps a | grep '[j]ava -jar DDF' | cut -d' ' -f 2`

if [ $PID > 0 ] ; then
	kill $PID
fi

# Start
java -jar DDFv2.0.jar -server -f local:config.xml

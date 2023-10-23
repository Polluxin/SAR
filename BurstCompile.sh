#!/bin/bash

CLASSPATH="../Irc2Burst.jar"
SOURCE_DIR="../irc"
OUTPUT_DIR="../irc"

javac -cp "$CLASSPATH" -d "$OUTPUT_DIR" "$SOURCE_DIR"/Irc2ForBurst.java



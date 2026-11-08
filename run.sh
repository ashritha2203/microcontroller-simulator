#!/bin/bash
for f in logging_fifo logger_to_core logger_to_ui ui_to_logger; do
  [ -p /tmp/$f ] || mkfifo /tmp/$f
done

rm -rf bin && mkdir bin
javac -d bin $(find src -name "*.java") || exit 1

java -cp bin LoggerProcess &
LOGGER_PID=$!
trap "kill $LOGGER_PID 2>/dev/null" EXIT

sleep 1
java -cp bin UIProcess
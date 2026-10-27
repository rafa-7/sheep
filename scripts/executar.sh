#!/bin/bash
# Compila e roda o analisador no Linux (ou Git Bash). Sem parametro usa o codigo.txt
javac -encoding UTF-8 -d out src/*.java
java -cp out Main "$@"

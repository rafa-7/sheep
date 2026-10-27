@echo off
rem Compila e roda o analisador. Sem parametro usa o codigo.txt
javac -encoding UTF-8 -d out src\*.java
java -cp out Main %*
pause

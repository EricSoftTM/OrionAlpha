@echo off
title OrionAlpha - Game0
set CLASSPATH=.;target\OrionAlpha.jar
java -Xmx400m -DgameID=0 -Dwzpath=data/ -Dio.netty.leakDetection.level=advanced -XX:NewRatio=50 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+HeapDumpOnOutOfMemoryError -XX:-DisableExplicitGC -Xnoclassgc -XX:+UseNUMA -XX:ReservedCodeCacheSize=48m -XX:MaxGCPauseMillis=300 -XX:GCPauseIntervalMillis=400 -XX:+UseTLAB -XX:+AlwaysPreTouch game.GameApp
pause
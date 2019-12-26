@echo off
title OrionAlpha - Shop
set CLASSPATH=.;target\OrionAlpha.jar
java -Xmx550m -Dwzpath=data/ -Dio.netty.leakDetection.level=advanced -XX:NewRatio=50 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+HeapDumpOnOutOfMemoryError -XX:-DisableExplicitGC -Xnoclassgc -XX:+UseNUMA -XX:ReservedCodeCacheSize=48m -XX:MaxGCPauseMillis=300 -XX:GCPauseIntervalMillis=400 -XX:+UseTLAB -XX:+AlwaysPreTouch shop.ShopApp
pause
#!/bin/bash
java -Xms1024m -Xmx1024m -XX:MaxPermSize=120M -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:MaxNewSize=512M -XX:NewSize=512M -jar target/achoo.jar


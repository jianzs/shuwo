#!/bin/bash
nohup mvn compile exec:java -Dexec.mainClass="top.zhengsj.shuwo.Main" > shuwo.log &

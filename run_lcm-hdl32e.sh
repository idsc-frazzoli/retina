#!/bin/bash

mvn exec:java -Dexec.mainClass="ch.ethz.idsc.retina.lcm.lidar.Hdl32eLcmServer" -Dexec.args="center 2368 8308"


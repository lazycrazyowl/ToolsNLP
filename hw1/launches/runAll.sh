#!/bin/bash
for i in 1 2 3 4 5 ; do launches/run$i.sh 2>&1 | tee log$i.log ; done

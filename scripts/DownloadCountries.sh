#!/bin/bash
file=$1
nl=`wc -l $file|cut -d \  -f 1`

i="1"

while [ "$i" -lt "$nl" ] ; do
  n=`head -$i $file|tail -1|sed 's/ /_/g'`
  echo $n
  wget "http://en.wikipedia.org/wiki/$n" -O "$n.html"
  i="$(($i+1))"
done

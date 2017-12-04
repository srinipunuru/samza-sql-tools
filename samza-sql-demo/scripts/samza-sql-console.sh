#!/bin/bash

if [ `uname` == 'Linux' ];
then
  base_dir=$(readlink -f $(dirname $0))
else
  base_dir="$(cd "$(dirname "$0")" && pwd -P)"
fi

if [ "x$LOG4J_OPTS" = "x" ]; then
    export LOG4J_OPTS="-Dlog4j.configuration=file://$base_dir/../config/samza-sql-console-log4j.xml"
fi

if [ "x$HEAP_OPTS" = "x" ]; then
    export HEAP_OPTS="-Xmx1G -Xms1G"
fi

EXTRA_ARGS="-name samza-sql-console -loggc"

exec $base_dir/run-class.sh $EXTRA_ARGS com.linkedin.samza.tools.SamzaSqlConsole "$@"

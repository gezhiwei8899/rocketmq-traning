#!/usr/bin/env bash

BIN_PATH=$(cd "$(dirname "$0")"; pwd)
OUT_FILE="$BIN_PATH/../service.out"
PID_FILE="$BIN_PATH/../service.pid"
CONF_PATH="$BIN_PATH/../conf"
LIBS_PATH="$BIN_PATH/../libs"
MAIN_CLASS="com.jdddata.datahub.msghub.DatahubServer"

# print usage
function print_usage(){
    echo "Usage: bootstrap.sh [-f] COMMAND"
    echo "    where COMMAND is one of:"
    echo "      start         start the service"
    echo "      stop          stop the service"
    echo "      restart       restart the service"
}
# start
function start(){
    CLASSPATH="$CONF_PATH/:$LIBS_PATH/*"
    if [ -f "$OUT_FILE" ]; then
        mv -f "$OUT_FILE" "$OUT_FILE"_$(date +%Y%m%d%H%M%S)
    fi
    echo 'Start the service'
    nohup java -Dfile.encoding=UTF-8 -classpath $CLASSPATH $MAIN_CLASS >"$OUT_FILE" 2>>"$OUT_FILE" &
    echo $! > "$PID_FILE"
}
# check pid exists
function existPid() {
    P=$1
    if [ -n "$P" ]; then
        E=$(ps ax | awk '{ print $1 }' | grep -e "^${P}$")
        if [ -n "$E" ]; then
            echo 1
        else
            echo 0
        fi
    else
        echo 0
    fi
}
# stop
function stop() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if [ -n "$PID" ]; then
            if [ $(existPid "$PID") ];then
                echo -e "Stop service, pid=$PID \c"
                while [ $(existPid "$PID") = 1 ]
                do
                    kill -9 "$PID"
                    echo -e ".\c"
                    sleep 1s
                done
                rm -f "$PID_FILE"
                echo " stopped."
            else
                rm -f "$PID_FILE"
                echo "No service to stop. process $PID not exists."
            fi
        else
            rm -f "$PID_FILE"
            echo "No service to stop. pid file empty."
        fi
    else
        echo "No service to stop. pid file not exist."
    fi
}

if [ $# = 0 ]; then
    print_usage
    exit
fi

COMMAND=$1
# check command
case $COMMAND in
    # usage flags
    --help|-help|-h)
        print_usage
        exit
        ;;
    -f)
        FORCE=1
        shift
        COMMAND=$1
        ;;
esac
# execute command
case $COMMAND in
    start)
        if [ -f "$PID_FILE" ]; then
            if [ "$FORCE" ]; then
                stop
            else
                echo "Cannot start. exists pid file $PID_FILE"
                exit
            fi
        fi
        start
        exit 1
        ;;
    stop)
        stop
        exit 1
        ;;
    restart)
        stop
        start
        exit 1
        ;;
    *)
        print_usage
        exit
        ;;
esac






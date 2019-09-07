#!/bin/bash
echo 'compiling native execution'
gcc nativeexecutionLCMtest.c -o nativeLCMtest -lm -llcm

#gcc nativeexecutionclienttest.c -o clienttest

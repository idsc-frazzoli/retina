#!/bin/bash
echo 'compiling native execution'
gcc nativeexecution.c -o nativeMPC MPCPathFollowing_casadi2forces.o MPCPathFollowing_model_1.o MPCPathFollowing_model_31.o MPCPathFollowing/obj/MPCPathFollowing.o -lm
#gcc nativeexecutionclienttest.c -o clienttest

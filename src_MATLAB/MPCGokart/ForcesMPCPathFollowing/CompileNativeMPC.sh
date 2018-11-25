#!/bin/bash
echo 'compiling native execution'
#gcc nativeexecution.c -o nativeMPC MPCPathFollowing_casadi2forces.o MPCPathFollowing_model_1.o MPCPathFollowing_model_31.o MPCPathFollowing/obj/MPCPathFollowing.o -lm
gcc nativeexecutionLCM.c -o nativeMPCLCM MPCPathFollowing_casadi2forces.o MPCPathFollowing_model_1.o MPCPathFollowing_model_31.o MPCPathFollowing/obj/MPCPathFollowing.o -lm -llcm
#gcc nativeexecutionLCM.c -o nativeMPCLCMtest -lm -llcm

#gcc nativeexecutionclienttest.c -o clienttest

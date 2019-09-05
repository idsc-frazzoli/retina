#!/bin/bash
echo 'compiling native execution'
gcc nativeexecutionLCM.c -o nativeOnlineMPCLCM OnlineMPCPathFollowing_casadi2forces.o OnlineMPCPathFollowing_model_1.o OnlineMPCPathFollowing_model_31.o OnlineMPCPathFollowing/obj/OnlineMPCPathFollowing.o -lm -llcm

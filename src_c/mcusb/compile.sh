#!/bin/bash

# code by jph
cc -Wall -g   -c -o DIn.o DIn.c
cc -o DIn DIn.o -lm -luldaq -llcm


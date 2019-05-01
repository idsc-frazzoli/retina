#!/bin/bash

# code by jph
cc -Wall -g   -c -o u3.o u3.c
cc -Wall -g   -c -o u3adctxt.o u3adctxt.c
cc -Wall -g   -c -o u3adclcm.o u3adclcm.c
cc -o u3adctxt u3adctxt.o u3.o  -lm -llabjackusb
cc -o u3adclcm u3adclcm.o u3.o  -lm -llabjackusb -llcm


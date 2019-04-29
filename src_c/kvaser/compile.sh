#!/bin/bash

# code by jph
#cc -Wall -g   -c -o u3.o u3.c
#cc -Wall -g   -c -o u3adctxt.o u3adctxt.c
#cc -Wall -g   -c -o u3adclcm.o u3adclcm.c
#cc -o u3adctxt u3adctxt.o u3.o  -lm -llabjackusb
#cc -o u3adclcm u3adclcm.o u3.o  -lm -llabjackusb -llcm
cc -Wall -Wextra -Werror -O2  -D_REENTRANT  -I. -D_DEBUG=0 -DDEBUG=0  -L..  tkp2write.c  -lcanlib -lpthread -lm -llcm -o tkp2write

cc -Wall -Wextra -Werror -O2  -D_REENTRANT  -I. -D_DEBUG=0 -DDEBUG=0  -L..  canmonitor.c  -lcanlib -lpthread -lm -llcm -o canmonitor


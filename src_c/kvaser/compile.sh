#!/bin/bash

cc -Wall -Wextra -Werror -O2  -D_REENTRANT  -I. -D_DEBUG=0 -DDEBUG=0  -L..  tkp1write.c  -lcanlib -lpthread -lm -llcm -o tkp1write

cc -Wall -Wextra -Werror -O2  -D_REENTRANT  -I. -D_DEBUG=0 -DDEBUG=0  -L..  tkp2write.c  -lcanlib -lpthread -lm -llcm -o tkp2write

cc -Wall -Wextra -Werror -O2  -D_REENTRANT  -I. -D_DEBUG=0 -DDEBUG=0  -L..  canmonitor.c  -lcanlib -lpthread -lm -llcm -o canmonitor


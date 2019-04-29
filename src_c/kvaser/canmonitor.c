/*
**             Copyright 2017 by Kvaser AB, Molndal, Sweden
**                         http://www.kvaser.com
**
** This software is dual licensed under the following two licenses:
** BSD-new and GPLv2. You may use either one. See the included
** COPYING file for details.
**
** License: BSD-new
** ==============================================================================
** Redistribution and use in source and binary forms, with or without
** modification, are permitted provided that the following conditions are met:
**     * Redistributions of source code must retain the above copyright
**       notice, this list of conditions and the following disclaimer.
**     * Redistributions in binary form must reproduce the above copyright
**       notice, this list of conditions and the following disclaimer in the
**       documentation and/or other materials provided with the distribution.
**     * Neither the name of the <organization> nor the
**       names of its contributors may be used to endorse or promote products
**       derived from this software without specific prior written permission.
**
** THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
** AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
** IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
** ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
** LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
** CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
** SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
** BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
** IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
** ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
** POSSIBILITY OF SUCH DAMAGE.
**
**
** License: GPLv2
** ==============================================================================
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
**
**
** IMPORTANT NOTICE:
** ==============================================================================
** This source code is made available for free, as an open license, by Kvaser AB,
** for use with its applications. Kvaser AB does not accept any liability
** whatsoever for any third party patent or other immaterial property rights
** violations that may result from any usage of this source code, regardless of
** the combination of source code and various applications that it can be used
** in, or with.
**
** -----------------------------------------------------------------------------
*/

/*
 * Kvaser Linux Canlib
 * Read CAN messages and print out their contents
 */

#include <canlib.h>
#include <stdio.h>
#include <signal.h>
#include <errno.h>
#include <unistd.h>
#include <time.h>
#include "vcanevt.h"

#include "../../src_MATLAB/MPCGokart/ForcesMPCPathFollowing/idsc_BinaryBlob.c"

#define READ_WAIT_INFINITE    (unsigned long)(-1)

static unsigned int msgCounter = 0;

static void check(char* id, canStatus stat)
{
  if (stat != canOK) {
    char buf[50];
    buf[0] = '\0';
    canGetErrorText(stat, buf, sizeof(buf));
    printf("%s: failed, stat=%d (%s)\n", id, (int)stat, buf);
  }
}

static void printUsageAndExit(char *prgName)
{
  printf("Usage: '%s <channel>'\n", prgName);
  exit(1);
}

static void sighand(int sig)
{
  (void)sig;
}

static char* busStatToStr(const unsigned long flag) {
    char* tempStr = NULL;
    #define MACRO2STR(x) case x: tempStr = #x; break
    switch (flag) {
        MACRO2STR( CHIPSTAT_BUSOFF        );
        MACRO2STR( CHIPSTAT_ERROR_PASSIVE );
        MACRO2STR( CHIPSTAT_ERROR_WARNING );
        MACRO2STR( CHIPSTAT_ERROR_ACTIVE  );
        default: tempStr = ""; break;
    }
    #undef MACRO2STR
    return tempStr;
}

void notifyCallback(canNotifyData *data) {
  switch (data->eventType) {
  case canEVENT_STATUS:
    printf("CAN Status Event: %s\n", busStatToStr(data->info.status.busStatus));
    break;
  case canEVENT_ERROR:
    printf("CAN Error Event\n");
    break;
  case canEVENT_TX:
    printf("CAN Tx Event\n");
    break;
  case canEVENT_RX:
    //printf("CAN Rx Event\n");
    break;
  }
  return;
}

int main(int argc, char *argv[])
{
  lcm_t *lcm = lcm_create(NULL);
  if (!lcm)
    return 1;

  canHandle hnd;
  canStatus stat;
  int channel;

  if (argc != 2) {
    printUsageAndExit(argv[0]);
  }

  {
    char *endPtr = NULL;
    errno = 0;
    channel = strtol(argv[1], &endPtr, 10);
    if ( (errno != 0) || ((channel == 0) && (endPtr == argv[1])) ) {
      printUsageAndExit(argv[0]);
    }
  }

  printf("Reading messages on channel %d\n", channel);

  /* Allow signals to interrupt syscalls */
  signal(SIGINT, sighand);
  siginterrupt(SIGINT, 1);

  canInitializeLibrary();

  /* Open channel, set parameters and go on bus */
  hnd = canOpenChannel(channel, canOPEN_EXCLUSIVE | canOPEN_REQUIRE_EXTENDED | canOPEN_ACCEPT_VIRTUAL);
  if (hnd < 0) {
    printf("canOpenChannel %d", channel);
    check("", hnd);
    return -1;
  }

  stat = canSetNotify(hnd, notifyCallback, canNOTIFY_RX | canNOTIFY_TX | canNOTIFY_ERROR | canNOTIFY_STATUS | canNOTIFY_ENVVAR, (char*)0);
  check("canSetNotify", stat);

  stat = canSetBusParams(hnd, canBITRATE_1M, 0, 0, 0, 0, 0);
  check("canSetBusParams", stat);
  if (stat != canOK) {
    goto ErrorExit;
  }
  stat = canBusOn(hnd);
  check("canBusOn", stat);
  if (stat != canOK) {
    goto ErrorExit;
  }

  do {
    long id;
    unsigned char msg[8];
    unsigned int dlc;
    unsigned int flag;
    unsigned long time;
    unsigned char blob[10];

    stat = canReadWait(hnd, &id, &msg, &dlc, &flag, &time, READ_WAIT_INFINITE);

    if (stat == canOK) {
      msgCounter++;
      if (flag & canMSG_ERROR_FRAME) {
        printf("(%u) ERROR FRAME", msgCounter);
      }
      else {
        if(msgCounter%1000==0)
          printf("msgCounter=%d\n",msgCounter);

        unsigned j;

        //printf("(%u) id:%ld dlc:%u data: ", msgCounter, id, dlc);
        if (dlc > 8) {
          dlc = 8;
        }
        for (j = 0; j < dlc; j++) {
          //printf("%2.2x ", msg[j]);
          blob[2+j]=msg[j];
        }

        idsc_BinaryBlob binaryBlob;
        binaryBlob.data_length = 2+dlc;
        blob[0]=id;
        blob[1]=id>>8;
        binaryBlob.data = (int8_t*)blob;
        idsc_BinaryBlob_publish(lcm, "can.tkp1", &binaryBlob);

      }
      //printf(" flags:0x%x time:%lu\n", flag, time);
    }
    else {
      if (errno == 0) {
        check("\ncanReadWait", stat);
      }
      else {
        perror("\ncanReadWait error");
      }
    }

  } while (stat == canOK);

ErrorExit:

  stat = canBusOff(hnd);
  check("canBusOff", stat);
  usleep(50*1000); // Sleep just to get the last notification.
  stat = canClose(hnd);
  check("canClose", stat);
  stat = canUnloadLibrary();
  check("canUnloadLibrary", stat);

  return 0;
}

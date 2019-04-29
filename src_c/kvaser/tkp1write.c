// based on copyrighted code by kvaser "simplewrite.c"
// adapted by jph

#include <canlib.h>
#include <stdio.h>
#include <signal.h>
#include <errno.h>
#include <unistd.h>
#include "vcanevt.h"

#include "../../src_MATLAB/MPCGokart/ForcesMPCPathFollowing/idsc_BinaryBlob.c"

#define WRITE_WAIT_INFINITE     (unsigned long)(-1)
#define READ_WAIT_INFINITE      (unsigned long)(-1)

static void check(char* id, canStatus stat) {
  if (stat != canOK) {
    char buf[50];
    buf[0] = '\0';
    canGetErrorText(stat, buf, sizeof(buf));
    printf("%s: failed, stat=%d (%s)\n", id, (int)stat, buf);
  }
}

int intmin(int a, int b) {
  return a < b ? a : b;
}

int intmax(int a, int b) {
  return a > b ? a : b;
}

static void printUsageAndExit(char *prgName) {
  printf("Usage: '%s <channel>'\n", prgName);
  exit(1);
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
    //printf("CAN Tx Event\n");
    break;
  case canEVENT_RX:
    //printf("CAN Rx Event\n");
    break;
  }
  return;
}

void sendCmd(canHandle hnd, char EpsMsgCntr, int enable, int torque) {
  
  canStatus stat;

  int ctr = EpsMsgCntr & 0xff;

  int trq = torque & 0xff;
  trq<<=1;

  char msgId01[8]; // message buffer to send with CAN ID 1

  msgId01[0]=enable; // 0x01 == on
  msgId01[0]|=trq;
  msgId01[1]=(ctr & 0x01) << 7;
  msgId01[2]=(ctr & 0xfe) >> 1;
  msgId01[3]=0;
  msgId01[4]=0;
  msgId01[5]=0;
  msgId01[6]=0;
  msgId01[7]=0;

  // BO_ 1 AboxFr01: 8 ABOX
  // canMSG_EXT
  {
    stat = canWriteWait(hnd, 1, msgId01, 8, 0, WRITE_WAIT_INFINITE);
    // stat = canWrite(hnd, 1, msgId01, 8, 0);
    // printf("sent %3d\n", EpsMsgCntr & 0xff);
    if (errno == 0) {
      check("\ncanWriteWait", stat);
    }
    else {
      perror("\ncanWriteWait error");
    }
    /*
      stat = canWriteSync(hnd, 1000);
      check("canWriteSync", stat);
      if (stat != canOK) {
        goto ErrorExit;
      }
    */
  }
}

int main(int argc, char *argv[]) {

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

  printf("Sending messages on channel %d\n", channel);

  canInitializeLibrary();

  /* Open channel, set parameters and go on bus */
  hnd = canOpenChannel(channel, canOPEN_EXCLUSIVE | canOPEN_REQUIRE_EXTENDED | canOPEN_ACCEPT_VIRTUAL);
  if (hnd < 0) {
    printf("canOpenChannel %d", channel);
    check("", hnd);
    return -1;
  }

  stat = canSetBusParams(hnd, canBITRATE_1M, 0, 0, 0, 0, 0);
  check("canSetBusParams", stat);
  if (stat != canOK) {
    goto ErrorExit;
  }

  stat = canSetNotify(hnd, notifyCallback, canNOTIFY_RX | canNOTIFY_TX | canNOTIFY_ERROR | canNOTIFY_STATUS | canNOTIFY_ENVVAR, (char*)0);
  check("canSetNotify", stat);

  stat = canBusOn(hnd);
  check("canBusOn", stat);
  if (stat != canOK) {
    goto ErrorExit;
  }

  char EpsMsgCntr = 0;

  int commandCount = 0;

  while(1) {

    // ----------------------------------------------------------

    // using the oscilloscope we have established that the TKP2 unit
    // first sends the message with id==0xA and
    //  then sends the message with id==0xB

    {
      long id;
      unsigned char msg[8];
      unsigned int dlc;
      unsigned int flag;
      unsigned long time;

      stat = canReadWait(hnd, &id, &msg, &dlc, &flag, &time, READ_WAIT_INFINITE);

      if (stat == canOK) {
        //printf("recv %ld\n",id);
        // BO_ 11 EpsFr02: 8 EPS
        if (id==11) {

          int ctr = (msg[6] & 0xff) << 3;
          ctr += (msg[5] & 0xe0) >> 5;
          EpsMsgCntr = ctr;
          // when receiving message 11: extract counter
          // SG_ EpsMsgCntr : 24|8@1+ (1,0) [0|255] ""  ABOX
          
        } else //
        if (id==10) {
          // when receiving message 10: reply!

          if (commandCount == 0) {
            sendCmd(hnd, 0, 0, 0);
          } else {
            sendCmd(hnd, EpsMsgCntr, 2*500 < commandCount, 4*500 < commandCount ? 0x10 : 0);
          }
          ++commandCount;
          commandCount = intmin(commandCount, 5*500);
          
          //printf(".");
        } else //
        { // terminate software in case message with unknown id is received
          printf("xknw %ld =======================================\n",id);
          goto ErrorExit;
        }
      }
    }

    // ----------------------------------------------------------



  }

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

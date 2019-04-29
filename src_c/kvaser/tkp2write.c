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

int main(int argc, char *argv[]) {

  lcm_t *lcm = lcm_create(NULL);
  if (!lcm)
    return 1;

  canHandle hnd;
  canStatus stat;
  int channel;
  char msgId01[8]; // message buffer to send with CAN ID 1

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

    // using the oscilloscope we have established that the unit
    // first sends the message with id==0xA and
    //  then sends the message with id==0xB

    for (int recvcnt = 0; recvcnt<2;++recvcnt) {
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
          int relIntAnk = 0;
          relIntAnk  = (msg[0]&0xff)<<8;
          relIntAnk |= (msg[1]&0xff)<<16;
          relIntAnk |= (msg[2]&0xff)<<24;
          relIntAnk /=256;
          // SG_ EpsMsgCntr : 24|8@1+ (1,0) [0|255] ""  ABOX
          EpsMsgCntr = msg[3];
          printf("recv %ld %8d %3d\n", id, relIntAnk, EpsMsgCntr & 0xff);
        } else //
        if (id==10) {
          // SG_ AngSpd : 0|16@1- (0.0457763671875,0) [-1500|1499.95422363281] "rad/sec"  ABOX
          // SG_ TsuTrq : 16|16@1- (0.00048828125,0) [-16|15.99951171875] "Nm"  ABOX
          // SG_ RefMotTrq : 32|16@1- (0.00048828125,0) [-16|15.99951171875] "Nm"  ABOX
          // SG_ EstMotTrq : 48|16@1- (0.00048828125,0) [-16|15.99951171875] "Nm"  ABOX

          int AngSpd  = 0;
          AngSpd  = (msg[0]&0xff)<<16;
          AngSpd |= (msg[1]&0xff)<<24;
          AngSpd /= 256*256;

          // relevant for power steering: user torque
          int TsuTrq  = 0;
          TsuTrq  = (msg[2]&0xff)<<16;
          TsuTrq |= (msg[3]&0xff)<<24;
          TsuTrq /= 256*256;

          int RefMotTrq  = 0;
          RefMotTrq  = (msg[4]&0xff)<<16;
          RefMotTrq |= (msg[5]&0xff)<<24;
          RefMotTrq /= 256*256;

          int EstMotTrq  = 0;
          EstMotTrq  = (msg[6]&0xff)<<16;
          EstMotTrq |= (msg[7]&0xff)<<24;
          EstMotTrq /= 256*256;

          printf("recv %ld %8d %8d %8d %8d\n", id, AngSpd, TsuTrq, RefMotTrq, EstMotTrq);
          //printf(".");
        } else //
        { // terminate software in case message with unknown id is received
          printf("xknw %ld =======================================\n",id);
          goto ErrorExit;
        }
      }
    }

    // ----------------------------------------------------------


    // SG_ ReqMotTrq : 0|16@1- (0.00048828125,0) [-16|15.99951171875] "Nm"  EPS
    msgId01[0]=0; // 0..8
    if(10000<commandCount) {
      msgId01[0]=intmin(commandCount-10000,100);
    }
    msgId01[1]=0; // 8..16
    // SG_ AboxMsgCntr : 16|8@1+ (1,0) [0|255] ""  EPS

    // Das Signal EpsMsgCntr muss in jedem Zyklus auf dem Signal AboxMsgCntr zurückgeschickt werden:
    msgId01[2]=EpsMsgCntr; // 16..24 // AboxMsgCtr = EpsMsgCntr;
    // SG_ ServerEna : 24|1@1+ (1,0) [0|1] ""  EPS
    // SG_ TBLMsg : 25|4@1+ (1,0) [0|15] "" Vector__XXX
    // Das Signal ServerEna schaltet die Einheit aus(0) / an (1):
    msgId01[3]=5000<commandCount?1:0; // ServerEna = 1; TODO check if lo or hi
    // Das Signal TBLMsg muss null sein: TBLMsg = 0; 
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
if (!msgId01[3]) {
  printf("off\n");
} else {
  printf("on with %d\n", msgId01[0]);
}
      ++commandCount;
      commandCount = intmin(commandCount,100000);
      /*
        stat = canWriteSync(hnd, 1000);
        check("canWriteSync", stat);
        if (stat != canOK) {
          goto ErrorExit;
        }
      */
    }

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

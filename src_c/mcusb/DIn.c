// code derived from https://github.com/mccdaq/uldaq
// adapted by jph

#include <stdio.h>
#include <stdlib.h>
#include "uldaq.h"
#include "utility.h"

#include "../../src_MATLAB/MPCGokart/ForcesMPCPathFollowing/idsc_BinaryBlob.c"

#define MAX_DEV_COUNT  100
#define MAX_STR_LENGTH 64

lcm_t * lcm;

int main(void) {
  lcm = lcm_create(NULL);
  if (!lcm)
    return 1;

  int descriptorIndex = 0;
  DaqDeviceDescriptor devDescriptors[MAX_DEV_COUNT];
  DaqDeviceInterface interfaceType = ANY_IFC;
  DaqDeviceHandle daqDeviceHandle = 0;
  unsigned int numDevs = MAX_DEV_COUNT;

  int hasDIO = 0;
  DigitalPortType portType;
  DigitalPortIoType portIoType;

  char portTypeStr[MAX_STR_LENGTH];
  char portIoTypeStr[MAX_STR_LENGTH];

  unsigned long long data = 0;
  UlError err = ERR_NO_ERROR;

  //int i = 0;
  int __attribute__((unused)) ret;
  //char c;

  // Get descriptors for all of the available DAQ devices
  err = ulGetDaqDeviceInventory(interfaceType, devDescriptors, &numDevs);

  if (err != ERR_NO_ERROR)
    goto end;

  // verify at least one DAQ device is detected
  if (numDevs == 0)
  {
    printf("No DAQ device is detected\n");
    goto end;
  }

  //printf("Found %d DAQ device(s)\n", numDevs);
  //for (i = 0; i < (int) numDevs; i++)
  //  printf("  %s: (%s)\n", devDescriptors[i].productName, devDescriptors[i].uniqueId);

  // get a handle to the DAQ device associated with the first descriptor
  daqDeviceHandle = ulCreateDaqDevice(devDescriptors[descriptorIndex]);

  if (daqDeviceHandle == 0)
  {
    printf ("Unable to create a handle to the specified DAQ device\n");
    goto end;
  }

  // verify the device supports digital input
  err = getDevInfoHasDio(daqDeviceHandle, &hasDIO);
  if (!hasDIO)
  {
    printf("The specified DAQ device does not support digital I/O\n");
    goto end;
  }

  //printf("\nConnecting to device %s - please wait ...\n", devDescriptors[descriptorIndex].devString);

  // establish a connection to the DAQ device
  err = ulConnectDaqDevice(daqDeviceHandle);

  if (err != ERR_NO_ERROR)
    goto end;

  // get the first port type (AUXPORT0, FIRSTPORTA, ...)
  err = getDioInfoFirstSupportedPortType(daqDeviceHandle, &portType, portTypeStr);

  // get the I/O type for the fisrt port
  err = getDioInfoFirstSupportedPortIoType(daqDeviceHandle, &portIoType, portIoTypeStr);

  if(portIoType == DPIOT_IO || portIoType == DPIOT_BITIO)
  {
    // configure the first port for input
    err = ulDConfigPort(daqDeviceHandle, portType, DD_INPUT);
  }

  //printf("\n%s ready\n", devDescriptors[descriptorIndex].devString);
  //printf("    Port: %s\n", portTypeStr);
  //printf("    Port I/O type: %s\n", portIoTypeStr);
  //printf("\nHit ENTER to continue\n");

  //ret = scanf("%c", &c);
  //ret = system("clear");

  while (err == ERR_NO_ERROR && !enter_press()) {
    // read the port
    err = ulDIn(daqDeviceHandle, portType, &data);

    //resetCursor();
    //printf("LCM pub 'Enter' to terminate the process\n\n");
    //printf("LCM pub Active DAQ device: %s (%s)\n\n", devDescriptors[descriptorIndex].productName, devDescriptors[descriptorIndex].uniqueId);

    //clearEOL();
    //printf("Data: %lld (0x%llx)\n", data, data);
    idsc_BinaryBlob binaryBlob;
    binaryBlob.data_length = 1;
    binaryBlob.data = (int8_t*) &data;
    idsc_BinaryBlob_publish(lcm, "mcusb.din", &binaryBlob);

    usleep(100000);
  }

  // disconnect from the DAQ device
  ulDisconnectDaqDevice(daqDeviceHandle);

end:

  // release the handle to the DAQ device
  ulReleaseDaqDevice(daqDeviceHandle);

  if(err != ERR_NO_ERROR) {
    char errMsg[ERR_MSG_LEN];
    ulGetErrMsg(err, errMsg);
    printf("Error Code: %d \n", err);
    printf("Error Message: %s \n", errMsg);
  }

  return 0;
}


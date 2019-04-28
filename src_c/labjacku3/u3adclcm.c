//Author: LabJack
//December 27, 2011
//This example program reads analog inputs AI0-AI4 using stream mode.  Requires
//a U3 with hardware version 1.21 or higher.

// adapted by jph

#include "u3.h"
#include "../../src_MATLAB/MPCGokart/ForcesMPCPathFollowing/idsc_BinaryBlob.c"

int StreamData_example(HANDLE hDevice, u3CalibrationInfo *caliInfo, int isDAC1Enabled);

#include "u3adc_config.c"
#include "u3adc_start.c"
#include "u3adc_stop.c"

const uint8 NumChannels = 5;  //For this example to work proper,
                              //SamplesPerPacket needs to be a multiple of
                              //NumChannels.
const uint8 SamplesPerPacket = 25;  //Needs to be 25 to read multiple StreamData
                                    //responses in one large packet, otherwise
                                    //can be any value between 1-25 for 1
                                    //StreamData response per packet.

#include "u3adc_stream.c"


lcm_t * lcm;
int main(int argc, char **argv) {
  lcm = lcm_create(NULL);
  if (!lcm)
    return 1;

  HANDLE hDevice;
  u3CalibrationInfo caliInfo;
  int dac1Enabled;

  //Opening first found U3 over USB
  if( (hDevice = openUSBConnection(-1)) == NULL )
    goto done;

  //Getting calibration information from U3
  if( getCalibrationInfo(hDevice, &caliInfo) < 0 )
    goto close;

  if( ConfigIO_example(hDevice, &dac1Enabled) != 0 )
    goto close;

  //Stopping any previous streams
  StreamStop(hDevice);

  if( StreamConfig_example(hDevice) != 0 )
    goto close;

  if( StreamStart(hDevice) != 0 )
    goto close;

  StreamData_example(hDevice, &caliInfo, dac1Enabled);
  StreamStop(hDevice);

close:
  closeUSBConnection(hDevice);
done:
  return 0;
}

#include "u3adc_data.c"

    idsc_BinaryBlob binaryBlob;
    binaryBlob.data_length = 5 * 4;
    float adcvalues[5];
    adcvalues[0]=voltages[0];
    adcvalues[1]=voltages[1];
    adcvalues[2]=voltages[2];
    adcvalues[3]=voltages[3];
    adcvalues[4]=voltages[4];
    binaryBlob.data = (int8_t*)adcvalues;
    idsc_BinaryBlob_publish(lcm, "labjack.u3.adc", &binaryBlob);
  }
  return 0;
}



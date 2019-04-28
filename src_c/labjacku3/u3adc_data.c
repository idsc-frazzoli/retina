//Reads the StreamData low-level function response in a loop.  All voltages from
//the stream are stored in the voltages 2D array.
int StreamData_example(HANDLE hDevice, u3CalibrationInfo *caliInfo, int isDAC1Enabled) {
  uint16 voltageBytes, checksumTotal;
  double hardwareVersion;
  int recBuffSize, recChars, autoRecoveryOn;
  int packetCounter, currChannel;
  int k, m;
  int totalPackets;  //The total number of StreamData responses read
  int readSizeMultiplier;  //Multiplier for the StreamData receive buffer size
  int responseSize;  //The number of bytes in a StreamData response
                     //(differs with SamplesPerPacket)

  readSizeMultiplier = 5;
  responseSize = 14 + SamplesPerPacket*2;

  /* Each StreamData response contains (SamplesPerPacket / NumChannels) * readSizeMultiplier
   * samples for each channel.
   * Total number of scans = (SamplesPerPacket / NumChannels) * readSizeMultiplier * numReadsPerDisplay * numDisplay
   */
  double voltages[NumChannels];
  uint8 recBuff[responseSize*readSizeMultiplier];

  packetCounter = 0;
  currChannel = 0;
  totalPackets = 0;
  recChars = 0;
  autoRecoveryOn = 0;
  recBuffSize = 14 + SamplesPerPacket*2;
  hardwareVersion = caliInfo->hardwareVersion;

  while (1) {
    
    /* For USB StreamData, use Endpoint 3 for reads.  You can read the
     * multiple StreamData responses of 64 bytes only if 
     * SamplesPerPacket is 25 to help improve streaming performance.  In
     * this example this multiple is adjusted by the readSizeMultiplier
     * variable. */

    //Reading stream response from U3
    recChars = LJUSB_Stream(hDevice, recBuff, responseSize*readSizeMultiplier);
    if (recChars < responseSize*readSizeMultiplier) {
      if (recChars == 0 )
        printf("Error : read failed (StreamData).\n");
      else
        printf("Error : did not read all of the buffer, expected %d bytes but received %d(StreamData).\n", responseSize*readSizeMultiplier, recChars);
      return -1;
    }

    //Checking for errors and getting data out of each StreamData response
    for( m = 0; m < readSizeMultiplier; m++ ) {
      totalPackets++;

      checksumTotal = extendedChecksum16(recBuff + m*recBuffSize, recBuffSize);
      if ((uint8)((checksumTotal / 256) & 0xFF) != recBuff[m*recBuffSize + 5] ) {
        printf("Error : read buffer has bad checksum16(MSB) (StreamData).\n");
        return -1;
      }

      if ((uint8)(checksumTotal & 0xFF) != recBuff[m*recBuffSize + 4]) {
        printf("Error : read buffer has bad checksum16(LBS) (StreamData).\n");
        return -1;
      }

      checksumTotal = extendedChecksum8(recBuff + m*recBuffSize);
      if (checksumTotal != recBuff[m*recBuffSize]) {
        printf("Error : read buffer has bad checksum8 (StreamData).\n");
        return -1;
      }

      if (recBuff[m*recBuffSize + 1] != (uint8)(0xF9) ||
          recBuff[m*recBuffSize + 2] != 4 + SamplesPerPacket ||
          recBuff[m*recBuffSize + 3] != (uint8)(0xC0) ) {
        printf("Error : read buffer has wrong command bytes (StreamData).\n");
        return -1;
      }

      if (recBuff[m*recBuffSize + 11] == 59 ) {
        if( !autoRecoveryOn ) {
          printf("\nU3 data buffer overflow detected in packet %d.\nNow using auto-recovery and reading buffered samples.\n", totalPackets);
          autoRecoveryOn = 1;
        }
      }
      else
      if (recBuff[m*recBuffSize + 11] == 60 ) {
        printf("Auto-recovery report in packet %d: %d scans were dropped.\nAuto-recovery is now off.\n", totalPackets, recBuff[m*recBuffSize + 6] + recBuff[m*recBuffSize + 7]*256);
        autoRecoveryOn = 0;
      }
      else
      if( recBuff[m*recBuffSize + 11] != 0 ) {
        printf("Errorcode # %d from StreamData read.\n", (unsigned int)recBuff[11]);
        return -1;
      }

      if (packetCounter != (int)recBuff[m*recBuffSize + 10]) {
        printf("PacketCounter (%d) does not match with with current packet count (%d)(StreamData).\n", recBuff[m*recBuffSize + 10], packetCounter);
        return -1;
      }

      //int backLog = (int)recBuff[m*48 + 12 + SamplesPerPacket*2];
      //printf("backLog=%5d\n",backLog);

      for ( k = 12; k < (12 + SamplesPerPacket*2); k += 2 ) {
        voltageBytes = (uint16)recBuff[m*recBuffSize + k] + (uint16)recBuff[m*recBuffSize + k+1]*256;
        if ( hardwareVersion >= 1.30 )
          getAinVoltCalibrated_hw130(caliInfo, currChannel, 31, voltageBytes, &(voltages[currChannel]));
        else
          getAinVoltCalibrated(caliInfo, isDAC1Enabled, 31, voltageBytes, &(voltages[currChannel]));

        currChannel++;
        if( currChannel >= NumChannels ) {
          currChannel = 0;
        }
      }

      if( packetCounter >= 255 )
        packetCounter = 0;
      else
        packetCounter++;
    }

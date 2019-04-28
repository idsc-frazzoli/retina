//Sends a StreamConfig low-level command to configure the stream.
int StreamConfig_example(HANDLE hDevice)
{
    uint8 sendBuff[64], recBuff[8];
    uint16 checksumTotal, scanInterval;
    int sendBuffSize, sendChars, recChars, i;

    sendBuffSize = 12+NumChannels*2;

    sendBuff[1] = (uint8)(0xF8);    //Command byte
    sendBuff[2] = 3 + NumChannels;  //Number of data words = NumChannels + 3
    sendBuff[3] = (uint8)(0x11);    //Extended command number
    sendBuff[6] = NumChannels;      //NumChannels
    sendBuff[7] = SamplesPerPacket; //SamplesPerPacket
    sendBuff[8] = 0;  //Reserved
    sendBuff[9] = 1;  //ScanConfig:
                      // Bit 7: Reserved
                      // Bit 6: Reserved
                      // Bit 3: Internal stream clock frequency = b0: 4 MHz
                      // Bit 2: Divide Clock by 256 = b0
                      // Bits 0-1: Resolution = b01: 11.9-bit effective

    scanInterval = 4000;
    sendBuff[10] = (uint8)(scanInterval & (0x00FF));  //Scan interval (low byte)
    sendBuff[11] = (uint8)(scanInterval / 256);  //Scan interval (high byte)

    for( i = 0; i < NumChannels; i++ )
    {
        sendBuff[12 + i*2] = i;  //PChannel = i
        sendBuff[13 + i*2] = 31;  //NChannel = 31: Single Ended
    }

    extendedChecksum(sendBuff, sendBuffSize);

    //Sending command to U3
    sendChars = LJUSB_Write(hDevice, sendBuff, sendBuffSize);
    if( sendChars < sendBuffSize )
    {
        if( sendChars == 0 )
            printf("Error : write failed (StreamConfig).\n");
        else
            printf("Error : did not write all of the buffer (StreamConfig).\n");
        return -1;
    }

    for( i = 0; i < 8; i++ )
        recBuff[i] = 0;

    //Reading response from U3
    recChars = LJUSB_Read(hDevice, recBuff, 8);
    if( recChars < 8 )
    {
        if( recChars == 0 )
            printf("Error : read failed (StreamConfig).\n");
        else
            printf("Error : did not read all of the buffer, %d (StreamConfig).\n", recChars);

        for( i = 0; i < 8; i++ )
            printf("%d ", recBuff[i]);

        return -1;
    }

    checksumTotal = extendedChecksum16(recBuff, 8);
    if( (uint8)((checksumTotal / 256) & 0xFF) != recBuff[5] )
    {
        printf("Error : read buffer has bad checksum16(MSB) (StreamConfig).\n");
        return -1;
    }

    if( (uint8)(checksumTotal & 0xFF) != recBuff[4] )
    {
        printf("Error : read buffer has bad checksum16(LBS) (StreamConfig).\n");
        return -1;
    }

    if( extendedChecksum8(recBuff) != recBuff[0] )
    {
        printf("Error : read buffer has bad checksum8 (StreamConfig).\n");
        return -1;
    }

    if( recBuff[1] != (uint8)(0xF8) || recBuff[2] != (uint8)(0x01) ||
        recBuff[3] != (uint8)(0x11) || recBuff[7] != (uint8)(0x00) )
    {
        printf("Error : read buffer has wrong command bytes (StreamConfig).\n");
        return -1;
    }

    if( recBuff[6] != 0 )
    {
        printf("Errorcode # %d from StreamConfig read.\n", (unsigned int)recBuff[6]);
        return -1;
    }

    return 0;
}


//Sends a ConfigIO low-level command that configures the FIOs, DAC, Timers and
//Counters for this example
int ConfigIO_example(HANDLE hDevice, int *isDAC1Enabled)
{
    uint8 sendBuff[12], recBuff[12];
    uint16 checksumTotal;
    int sendChars, recChars;
    sendBuff[0] = 0;   // <- jan inserted line
    sendBuff[1] = (uint8)(0xF8);  //Command byte
    sendBuff[2] = (uint8)(0x03);  //Number of data words
    sendBuff[3] = (uint8)(0x0B);  //Extended command number
    sendBuff[4] = 0;   // <- jan inserted line
    sendBuff[5] = 0;   // <- jan inserted line
    sendBuff[6] = 13;  //Writemask : Setting writemask for timerCounterConfig (bit 0),
                       //            FIOAnalog (bit 2) and EIOAnalog (bit 3)

    sendBuff[7] = 0;   //Reserved
    sendBuff[8] = 64;  //TimerCounterConfig: Disabling all timers and counters,
                       //                    set TimerCounterPinOffset to 4 (bits 4-7)
    sendBuff[9] = 0;   //DAC1Enable

    sendBuff[10] = 255;  //FIOAnalog : setting all FIOs as analog inputs
    sendBuff[11] = 255;  //EIOAnalog : setting all EIOs as analog inputs
    extendedChecksum(sendBuff, 12);

    //Sending command to U3
    if( (sendChars = LJUSB_Write(hDevice, sendBuff, 12)) < 12 )
    {
        if( sendChars == 0 )
            printf("ConfigIO error : write failed\n");
        else
            printf("ConfigIO error : did not write all of the buffer\n");
        return -1;
    }

    //Reading response from U3
    if( (recChars = LJUSB_Read(hDevice, recBuff, 12)) < 12 )
    {
        if( recChars == 0 )
            printf("ConfigIO error : read failed\n");
        else
            printf("ConfigIO error : did not read all of the buffer\n");
        return -1;
    }

    checksumTotal = extendedChecksum16(recBuff, 12);
    if( (uint8)((checksumTotal / 256 ) & 0xFF) != recBuff[5] )
    {
        printf("ConfigIO error : read buffer has bad checksum16(MSB)\n");
        return -1;
    }

    if( (uint8)(checksumTotal & 0xFF) != recBuff[4] )
    {
        printf("ConfigIO error : read buffer has bad checksum16(LBS)\n");
        return -1;
    }

    if( extendedChecksum8(recBuff) != recBuff[0] )
    {
        printf("ConfigIO error : read buffer has bad checksum8\n");
        return -1;
    }

    if( recBuff[1] != (uint8)(0xF8) || recBuff[2] != (uint8)(0x03) || recBuff[3] != (uint8)(0x0B) )
    {
        printf("ConfigIO error : read buffer has wrong command bytes\n");
        return -1;
    }

    if( recBuff[6] != 0 )
    {
        printf("ConfigIO error : read buffer received errorcode %d\n", recBuff[6]);
        return -1;
    }

    if( recBuff[8] != 64 )
    {
        printf("ConfigIO error : TimerCounterConfig did not get set correctly\n");
        return -1;
    }

    if( recBuff[10] != 255 && recBuff[10] != (uint8)(0x0F) )
    {
        printf("ConfigIO error : FIOAnalog did not set get correctly\n");
        return -1;
    }

    if( recBuff[11] != 255 )
    {
        printf("ConfigIO error : EIOAnalog did not set get correctly (%d)\n", recBuff[11]);
        return -1;
    }

    *isDAC1Enabled = (int)recBuff[9];

    return 0;
}


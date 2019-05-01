//Sends a StreamStop low-level command to stop streaming.
int StreamStop(HANDLE hDevice)
{
    uint8 sendBuff[2], recBuff[4];
    int sendChars, recChars;

    sendBuff[0] = (uint8)(0xB0);  //CheckSum8
    sendBuff[1] = (uint8)(0xB0);  //Command byte

    //Sending command to U3
    sendChars = LJUSB_Write(hDevice, sendBuff, 2);
    if( sendChars < 2 )
    {
        if( sendChars == 0 )
            printf("Error : write failed (StreamStop).\n");
        else
            printf("Error : did not write all of the buffer (StreamStop).\n");
        return -1;
    }

    //Reading response from U3
    recChars = LJUSB_Read(hDevice, recBuff, 4);
    if( recChars < 4 )
    {
        if( recChars == 0 )
            printf("Error : read failed (StreamStop).\n");
        else
            printf("Error : did not read all of the buffer (StreamStop).\n");
        return -1;
    }

    if( normalChecksum8(recBuff, 4) != recBuff[0] )
    {
        printf("Error : read buffer has bad checksum8 (StreamStop).\n");
        return -1;
    }

    if( recBuff[1] != (uint8)(0xB1) || recBuff[3] != (uint8)(0x00) )
    {
        printf("Error : read buffer has wrong command bytes (StreamStop).\n");
        return -1;
    }

    if( recBuff[2] != 0 )
    {
  // this happens here
        //printf("Errorcode # %d from StreamStop read.\n", (unsigned int)recBuff[2]);
        //return -1;
    }

    return 0;
}


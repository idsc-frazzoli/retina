
//Sends a StreamStart low-level command to start streaming.
int StreamStart(HANDLE hDevice)
{
    uint8 sendBuff[2], recBuff[4];
    int sendChars, recChars;

    sendBuff[0] = (uint8)(0xA8);  //CheckSum8
    sendBuff[1] = (uint8)(0xA8);  //command byte

    //Sending command to U3
    sendChars = LJUSB_Write(hDevice, sendBuff, 2);
    if( sendChars < 2 )
    {
        if( sendChars == 0 )
            printf("Error : write failed.\n");
        else
            printf("Error : did not write all of the buffer.\n");
        return -1;
    }

    //Reading response from U3
    recChars = LJUSB_Read(hDevice, recBuff, 4);
    if( recChars < 4 )
    {
        if( recChars == 0 )
            printf("Error : read failed.\n");
        else
            printf("Error : did not read all of the buffer.\n");
        return -1;
    }

    if( normalChecksum8(recBuff, 4) != recBuff[0] )
    {
        printf("Error : read buffer has bad checksum8 (StreamStart).\n");
        return -1;
    }

    if( recBuff[1] != (uint8)(0xA9) || recBuff[3] != (uint8)(0x00) )
    {
        printf("Error : read buffer has wrong command bytes \n");
        return -1;
    }

    if( recBuff[2] != 0 )
    {
        printf("Errorcode # %d from StreamStart read.\n", (unsigned int)recBuff[2]);
        return -1;
    }

    return 0;
}


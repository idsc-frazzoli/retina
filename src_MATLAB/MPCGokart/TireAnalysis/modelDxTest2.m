    B1 = 9;
    C1 = 1;
    D1 = 10;

    B2 = 5.2;
    C2 = 1.1;
    D2 = 10;
    Cf = 0.3;
    param = [B1,C1,D1,B2,C2,D2,Cf];
    
    %VELX,VELY,VELROTZ,BETA,AB,TV
    [ACCX,ACCY,ACCROTZ]= modelDx(7,3,-0.5,0.3,0.1,2, param)
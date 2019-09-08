function [ACCX,ACCY,ACCROTZ] = modelDx_NN_linear_reg0p0001(VELX,VELY,VELROTZ,BETA,AB,TV, param)
    %param = [B1,C1,D1,B2,C2,D2,Ic];
    B1 = param(1);
    C1 = param(2);
    D1 = param(3);
    B2 = param(4);
    C2 = param(5);
    D2 = param(6);
    Ic = param(7);
    %maxA = param(8);
    magic = @(s,B,C,D)D.*sin(C.*atan(B.*s));
    reg = 0.5;
    capfactor = @(taccx)(1-satfun((taccx/D2)^2))^(1/2);
    simpleslip = @(VELY,VELX,taccx)-(1/capfactor(taccx))*VELY/(VELX+reg);
    %simpleslip = @(VELY,VELX,taccx)-VELY/(VELX+reg);
    simplediraccy = @(VELY,VELX,taccx)magic(simpleslip(VELY,VELX,taccx),B2,C2,D2);
    simpleaccy = @(VELY,VELX,taccx)capfactor(taccx)*simplediraccy(VELY,VELX,taccx);
    %acclim = @(VELY,VELX, taccx)(VELX^2+VELY^2)*taccx^2-VELX^2*maxA^2;
    simplefaccy = @(VELY,VELX)magic(-VELY/(VELX+reg),B1,C1,D1);
    %simpleaccy = @(VELY,VELX,taccx)magic(-VELY/(VELX+reg),B2,C2,D2);



    l = 1.19;
    l1 = 0.73;
    l2 = l-l1;
    f1n = l2/l;
    f2n = l1/l;
    w = 1;
    rotmat = @(beta)[cos(beta),sin(beta);-sin(beta),cos(beta)];
    vel1 = rotmat(BETA)*[VELX;VELY+l1*VELROTZ];
    f1y = simplefaccy(vel1(2),vel1(1));
    F1 = rotmat(-BETA)*[0;f1y]*f1n;
    F1x = F1(1);
    F1y = F1(2);
    frontabcorr = F1x;
    F2x = AB;
    F2y1 = simpleaccy(VELY-l2*VELROTZ,VELX,(AB+TV/2)/f2n)*f2n/2;
    F2y2 = simpleaccy(VELY-l2*VELROTZ,VELX,(AB-TV/2)/f2n)*f2n/2;
    F2y = simpleaccy(VELY-l2*VELROTZ,VELX,AB/f2n)*f2n;
    TVTrq = TV*w;
    
    
    ACCROTZ_NOM = (TVTrq + F1y*l1 -F2y*l2)/Ic;
    %ACCROTZ = TVTrq + F1y*l1;
    ACCX_NOM = F1x+F2x+VELROTZ*VELY;
    ACCY_NOM = F1y+F2y1+F2y2-VELROTZ*VELX;
    
    w1 = [0.103721656 -0.5501852 -0.2130562;0.067463145 0.9048563 0.16728602;0.12852472 1.5664511 0.032967947;-0.17076744 -0.53131765 -0.032128476;-0.14838596 0.28139886 0.06859773;-0.018183552 -0.29682347 -1.8631319];
    b1 = [0.0034703258 0.28908738 0.5351767];
    means = [2.6775749832578986 0.001922168301437502 -0.04515304867653743 -0.026195759869689427 0.06460829512732788 -0.0686602322088558];
    stds = [1.907241556285694 0.3079627568782051 0.5515613235298231 0.20101736434516326 0.8309067855283836 0.634574245360539];
    
    input = [VELX,VELY,VELROTZ,BETA,AB,TV];

    normed_input = (input - means) ./ stds;

    disturbance = normed_input * w1 + b1;
    
    ACCX = ACCX_NOM + disturbance(1);
    ACCY = ACCY_NOM + disturbance(2);
    ACCROTZ = ACCROTZ_NOM + disturbance(3);
end


function [ACCX,ACCY,ACCROTZ] = dynamic_NN_0x6_None_reg0p01_symmetric(VELX,VELY,VELROTZ,BETA,AB,TV, param)
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
    
    w1 = [-3.0359029e-34 -8.9958775e-35;0.8403442 0.15388334;1.497745 0.04177298;-0.5487852 -0.16386013;-5.951017e-35 -2.38584e-34;-0.2220607 -1.7644445];
    b1 = [0.0 0.0];
    w2 = [0.08801723;2.8039795e-34;2.4417956e-34;-2.9945484e-34;-0.13290115;-1.6765541e-34];
    b2 = 0.005329717;
    means = [2.6775749832578986 0.001922168301437502 -0.04515304867653743 -0.026195759869689427 0.06460829512732788 -0.0686602322088558];
    stds = [1.907241556285694 0.3079627568782051 0.5515613235298231 0.20101736434516326 0.8309067855283836 0.634574245360539];

    input = [VELX - means(1),VELY,VELROTZ,BETA,AB - means(5),TV];

    normed_input = input ./ stds;
    normed_input_neg = normed_input .* [1,-1,-1,-1,1,-1];
    
    h1 = normed_input * w1 + b1;
    h1_neg = normed_input_neg * w1 + b1;
    
    h2 = normed_input * w2 + b2;
    h2_neg = normed_input_neg * w2 + b2;
    
    h1_odd = (h1 - h1_neg) / 2;
    h1_even = (h2 + h2_neg) / 2;
    
    ACCX = ACCX_NOM + h1_even;
    ACCY = ACCY_NOM + h1_odd(1);
    ACCROTZ = ACCROTZ_NOM + h1_odd(2);
end


function [ACCX,ACCY,ACCROTZ] = modelDx_NN_1(VELX,VELY,VELROTZ,BETA,AB,TV, param)
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
    
    w1 = [0.032839835 0.032839835 0.032839835 0.03283983 0.032839835 0.03283983 0.03283983 0.032839835 0.032839835 0.032839835 0.03283983 0.03283983 0.032839835 0.5138206 0.03283983 0.032839835;0.002941043 0.0029410427 0.002941043 0.002941044 0.002941043 0.0029410436 0.0029410436 0.002941043 0.0029410427 0.002941043 0.0029410436 0.0029410424 0.0029410427 0.046535857 0.0029410436 0.0029410427;0.012729082 0.012729082 0.012729082 0.01272908 0.012729082 0.01272908 0.01272908 0.012729082 0.012729082 0.012729082 0.01272908 0.0127290785 0.012729082 0.13262013 0.01272908 0.012729082;0.0071192253 0.0071192253 0.0071192253 0.0071192244 0.007119225 0.0071192235 0.0071192235 0.0071192253 0.0071192253 0.007119225 0.0071192253 0.007119224 0.0071192253 0.113003254 0.007119224 0.007119225;0.00024308816 0.00024308766 0.00024308766 0.00024308883 0.00024308766 0.00024308867 0.00024308883 0.00024308766 0.00024308813 0.00024308766 0.00024308906 0.00024308967 0.00024308766 -0.018444834 0.00024308867 0.00024308816;0.04767417 0.04767417 0.04767417 0.047674168 0.04767417 0.047674168 0.047674168 0.04767417 0.04767417 0.04767417 0.047674168 0.047674168 0.04767417 0.92916054 0.047674168 0.04767417];
    b1 = [0.0025939757 0.0025939757 0.0025939755 0.002593976 0.0025939757 0.002593976 0.002593976 0.0025939757 0.0025939757 0.0025939757 0.0025939753 0.002593975 0.0025939757 0.36944705 0.0025939757 0.002593975];
    w2 = [0.0011593872 0.0011593873 0.0011593873 0.0011593879 0.001159388 0.001159387 0.07580532 0.0011593875 0.0011593879 0.0011593879 0.0011593872 0.001159387 0.0011593875 0.0011593887 0.001159387 0.0011593871;0.001159387 0.0011593873 0.0011593873 0.0011593874 0.001159388 0.0011593867 0.07580532 0.0011593873 0.0011593871 0.0011593876 0.001159387 0.0011593872 0.0011593876 0.0011593887 0.0011593872 0.001159387;0.0011593878 0.0011593873 0.0011593873 0.0011593876 0.001159388 0.001159387 0.07580532 0.0011593875 0.0011593879 0.0011593879 0.0011593872 0.0011593872 0.0011593876 0.0011593883 0.0011593875 0.0011593875;0.0011593875 0.0011593872 0.0011593874 0.0011593879 0.0011593881 0.0011593874 0.075805314 0.0011593881 0.0011593876 0.0011593879 0.0011593873 0.001159387 0.0011593882 0.0011593893 0.0011593876 0.0011593878;0.0011593875 0.0011593873 0.0011593873 0.0011593876 0.001159388 0.001159387 0.07580532 0.0011593873 0.0011593879 0.0011593876 0.001159387 0.0011593872 0.0011593878 0.0011593888 0.0011593874 0.0011593871;0.0011593878 0.0011593872 0.0011593873 0.0011593876 0.0011593881 0.0011593872 0.075805314 0.0011593882 0.0011593879 0.0011593876 0.0011593873 0.0011593873 0.0011593882 0.0011593892 0.0011593874 0.0011593875;0.0011593878 0.0011593872 0.0011593873 0.0011593879 0.0011593881 0.001159387 0.075805314 0.0011593882 0.0011593876 0.0011593879 0.0011593873 0.0011593873 0.0011593882 0.0011593893 0.0011593876 0.0011593874;0.001159387 0.0011593873 0.0011593873 0.0011593876 0.001159388 0.0011593867 0.07580532 0.0011593873 0.0011593876 0.0011593876 0.0011593872 0.0011593872 0.0011593875 0.0011593887 0.0011593872 0.0011593871;0.001159387 0.0011593873 0.0011593873 0.0011593876 0.001159388 0.0011593867 0.07580532 0.0011593873 0.0011593876 0.0011593876 0.0011593872 0.001159387 0.0011593875 0.0011593887 0.0011593872 0.0011593871;0.001159388 0.0011593873 0.0011593873 0.0011593876 0.0011593879 0.001159387 0.07580532 0.0011593875 0.0011593881 0.0011593876 0.001159387 0.0011593872 0.0011593876 0.0011593887 0.0011593873 0.0011593871;0.0011593876 0.0011593874 0.0011593876 0.0011593873 0.0011593882 0.0011593874 0.075805314 0.001159388 0.0011593879 0.0011593879 0.0011593874 0.0011593871 0.0011593876 0.0011593888 0.0011593876 0.0011593875;0.001159388 0.0011593879 0.0011593876 0.0011593876 0.0011593882 0.0011593874 0.07580531 0.0011593887 0.0011593881 0.0011593876 0.0011593878 0.0011593878 0.0011593876 0.0011593885 0.0011593881 0.0011593875;0.0011593875 0.0011593873 0.0011593873 0.0011593876 0.0011593879 0.001159387 0.07580532 0.0011593875 0.0011593876 0.0011593876 0.0011593872 0.001159387 0.0011593878 0.0011593887 0.0011593875 0.0011593871;-0.04162416 -0.04162416 -0.04162416 -0.041624162 -0.041624166 -0.04162416 1.0718224 -0.041624162 -0.04162416 -0.04162416 -0.04162416 -0.04162416 -0.04162416 -0.041624162 -0.04162416 -0.04162416;0.0011593881 0.0011593872 0.0011593874 0.0011593879 0.0011593881 0.0011593874 0.075805314 0.0011593879 0.0011593881 0.0011593876 0.0011593876 0.0011593873 0.0011593882 0.0011593893 0.0011593876 0.0011593878;0.001159388 0.001159387 0.0011593873 0.0011593874 0.001159388 0.001159387 0.07580532 0.001159388 0.0011593876 0.0011593876 0.0011593872 0.001159387 0.0011593875 0.0011593887 0.0011593874 0.0011593871];
    b2 = [0.004834171 0.0048341723 0.0048341723 0.004834172 0.0048341746 0.0048341714 0.047940105 0.0048341714 0.004834171 0.004834172 0.004834172 0.0048341714 0.004834172 0.0048341737 0.004834172 0.00483417];
    w3 = [-0.014874379 0.029340677 0.08544268;-0.014874379 0.029340673 0.08544268;-0.014874379 0.029340673 0.08544268;-0.014874379 0.02934068 0.08544268;-0.01487438 0.029340677 0.085442685;-0.014874379 0.029340677 0.08544268;0.06694533 0.14587833 -0.97148436;-0.014874377 0.029340677 0.08544268;-0.014874377 0.029340677 0.08544268;-0.014874379 0.029340677 0.08544268;-0.014874379 0.029340677 0.08544268;-0.01487438 0.029340684 0.08544268;-0.014874379 0.029340677 0.08544268;-0.014874379 0.029340666 0.08544268;-0.014874378 0.029340677 0.08544268;-0.014874379 0.02934068 0.08544267];
    b3 = [-0.019273931 0.04805794 0.0923360];
    means = [2.90584489205255,0.081451223672835,0.386123143531355,0.159366692761053,0.081519444796358,0.440426805042303];
    stds = [1.89783537002044,0.326914294413681,0.417186008233721,0.118316736680816,0.872808565216599,0.501676289063183];
    
    sign = tanh(BETA * 100000);
    input = [VELX,sign*VELY,sign*VELROTZ,sign*BETA,AB,sign*TV];

    normed_input = (input - means) ./ stds;

    h1 = log(exp(normed_input * w1 + b1) + 1);
    h2 = log(exp(h1 * w2 + b2) + 1);
    disturbance = h2 * w3 + b3;
    
    disturbance(2) = sign * disturbance(2);
    disturbance(3) = sign * disturbance(3);
    
    ACCX = ACCX_NOM + disturbance(1);
    ACCY = ACCY_NOM + disturbance(2);
    ACCROTZ = ACCROTZ_NOM + disturbance(3);
end


function [ACCX,ACCY,ACCROTZ] = modelDx(VELX,VELY,VELROTZ,BETA,AB,TV, param)

%modelDx: Calculates Accelerations of the Cart based on the slipping
%Tricycle model described in Marc Heims Masters Thesis 2019

%N.B. This Function is Mass invariant as all the forces applied are factors of
%the Normal contract force so the mass cancels out of all equations.
%However The magic formula co-efs (param[1:6]) may change with mass

% BETA : Lenkwinkel (control variable)
% AB : acceleration of hinterachse (control variable)
% TV : torque vectoring (control variable)
% AB-TV rechte achse
% AB+TV linke achse


    %% Model Parameters
    %Front Tire Params (for magic formula)
    B1 = param(1);
    C1 = param(2);
    D1 = param(3);
    %Rear Tire Params (for magic formula)
    B2 = param(4);
    C2 = param(5);
    D2 = param(6);
    
    Ic = param(7); %Moment of inertia

    magic = @(s,B,C,D)D.*sin(C.*atan(B.*s));
    reg = 0.5;
    capfactor = @(taccx)(1-satfun((taccx/D2)^2))^(1/2);
    simpleslip = @(VELY,VELX,taccx)-(1/capfactor(taccx))*VELY/(VELX+reg);
    
    %simplediraccy = @(VELY,VELX,taccx)magic(simpleslip(VELY,VELX,taccx),B2,C2,D2);
    %acclim = @(VELY,VELX, taccx)(VELX^2+VELY^2)*taccx^2-VELX^2*maxA^2;
    
    simpleaccy = @(VELY,VELX,taccx)capfactor(taccx)*magic(simpleslip(VELY,VELX,taccx),B2,C2,D2);    %force/kg  applied by rear tires with Taccx travelling at Velx,Vely 
    simplefaccy = @(VELY,VELX)magic(-VELY/(VELX+reg),B1,C1,D1);     %force/kg applied by front tire travelling at Velx,Vely
    

    l = 1.19;   %Length of the Go-cart
    l1 = 0.73;  %Dist from C.O.M to front Tire
    l2 = l-l1;    %Dist to rear Axle
    f1n = l2/l;   %portion of Mass supported by front tires
    f2n = l1/l;   %portion of Mass supported by rear tires
    w = 1;      %Distance between rear Tires
    
    %% Tire Forces
    rotmat = @(beta)[cos(beta),sin(beta);-sin(beta),cos(beta)];
    vel1 = rotmat(BETA)*[VELX;VELY+l1*VELROTZ];     %Velocity in wheels refrence frame
    f1y = simplefaccy(vel1(2),vel1(1));                       %lateral acceleration on front wheels in wheels ref frame
    F1 = rotmat(-BETA)*[0;f1y]*f1n;                         %accelerations on front wheel in cart ref frame                     
    F1x = F1(1);                                                      %forward acceleration on cart from front wheels
    F1y = F1(2);                                                      %Lateral acceleration on cart from front wheels
    
    F2x = AB;
    F2y1 = simpleaccy(VELY-l2*VELROTZ,VELX,(AB+TV/2)/f2n)*f2n/2;    %Lateral acceleration from from right rear wheel
    F2y2 = simpleaccy(VELY-l2*VELROTZ,VELX,(AB-TV/2)/f2n)*f2n/2;    %Lateral acceleration from from left rear wheel
    F2y = simpleaccy(VELY-l2*VELROTZ,VELX,AB/f2n)*f2n;                   %Lateral acceleration from  rear wheels
    TVTrq = TV*w;                                                                         %Torque from difrence in real wheel accelerations  
    
    %% Cart Accelerations
    ACCROTZ = (TVTrq + F1y*l1 -F2y*l2)/Ic;               %Rotational Acceleration of the cart
    ACCX = F1x+F2x+VELROTZ*VELY;                          %Forward Acceleration of cart
    ACCY = F1y+F2y1+F2y2-VELROTZ*VELX;                %Lateral Acceleration of cart
end


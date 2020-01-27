function [ACCBETA] = modelDb(VELX,VELY,VELROTZ,BETA,DotB,TauC, param)

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
%Front Tire Moment Params (for magic formula)
B3 = 1;
C3 = 1;
D3 = 0;

kRot=0.000;
%Steering Column Parameters
scK= param(7);
scD= param(8);
scJ= param(9);

Ic = param(7); %Moment of inertia

magic = @(s,B,C,D)D.*sin(C.*atan(B.*s));
Lpneu=@(s) D3.*sin(C3.*atan(B3.*s));
reg = 0.5;

simpleMaccy = @(VELY,VELX)magic(-VELY/(VELX+reg),B1,C1,D1)*Lpneu(-VELY/(VELX+reg));

effectiveTorque3 =@(tau)(1*(tau.^3))+0.2*tau;

%effectiveTorque2 =@(tau)(0.5*(tau^2).*sign(tau))+0.05*tau;
%effectiveTorque1 =@(tau)(1.0*abs(tau.^1.5).*sign(tau))+0.2.*tau;
%effectiveTorque0 =@(tau)0.2*tau;%(1.2*(x.^2).*sign(x))+0.01.*x;
l = 1.19;   %Length of the Go-cart
l1 = 0.73;  %Dist from C.O.M to front Tire
l2 = l-l1;    %Dist to rear Axle

%% Tire Forces
rotmat = @(beta)[cos(beta),sin(beta);-sin(beta),cos(beta)];
vel1 = rotmat(BETA)*[VELX;VELY+l1*VELROTZ];     %Velocity in wheels refrence frame
F1m=simpleMaccy(vel1(2),vel1(1));                       %Rot accel of SC from the front tire pacejka moment

dotAckerman = ((-1.8*BETA*BETA)+0.94)*DotB;
%% Cart Accelerations
fSpring=(-(scK/scJ)*BETA);
fDamp=(-(scD/scJ)*DotB);
fTau=effectiveTorque3(TauC)/scJ;
%fTau=TauC/scJ;
fRot=(VELROTZ-dotAckerman)*kRot/scJ;
ACCBETA = fSpring+fDamp+ fTau +F1m+fRot;      %Rotational Acceleration of Steering Column

end


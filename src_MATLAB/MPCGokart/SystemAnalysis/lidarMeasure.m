%code by mheim
function [nx,nP] = lidarMeasure(x,P,dt,dmt,m1,m2,m3,R,Q)
%measure lidar
%R: estimated lidar variance
%m = [posx,posy,theta]'
%m1-3: last 3 measurements [m1,(m2),m3] -> m2 is current measurement

%compute variance and values for position and acceleration

%p = m1;
%R1 = R;
%v = (m1-m2)/dmt;
%R2 = 2*R*(1/dmt);
%a = (m1-2*m2+m3)/(dmt^2);
%R3 = 4*R*(1/dmt^2);
%vector M
M = [m1;m2;m3];
%measurementF:
%F = [zeros(3,6),eye(3);...
%    zeros(3,3),-eye(3)/(dmt),eye(3)/(dmt);...
%   eye(3)*(1/dmt^2),-eye(3)*(2/dmt^2),eye(3)*(1/dmt^2)];
%use other F if EKF is used in online state estimation
F = [zeros(3,3),eye(3),zeros(3,3);...
    -eye(3)/(2*dmt),zeros(3,3),eye(3)/(2*dmt);...
    eye(3)*(1/dmt^2),-eye(3)*(2/dmt^2),eye(3)*(1/dmt^2)];
z = F*M;
%measurment variance (3 because each measurement is used 3 times)
V = 3*blkdiag(R,R,R);
R = F*V*F';
Fx = getEvolution(x);
dotx = Fx*x;
if(dt > 0.00001)
    [px,pP]=Predict(x,P,dotx,Fx,dt,Q);
else
    px = x;
    pP = P;
end
h = x;
Hx = eye(9);
[nx,nP]=kmeasure(px,pP,h,Hx,z,R);
end


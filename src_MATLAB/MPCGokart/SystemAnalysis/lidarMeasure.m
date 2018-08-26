%code by mheim
function [nx,nP] = lidarMeasure(x,P,dt,dmt,m1,m2,m3,R,Q)
%measure lidar
%R: estimated lidar variance
%m = [posx,posy,theta]'
%m1-3: last 3 measurements

%compute variance and values for position and acceleration
fullm = [m1;m2;m3];

p = m1;
R1 = R;
v = (m1-m2)/dmt;
R2 = 2*R*(1/dmt);
a = (m1(1:2)-2*m2(1:2)+m3(1:2))/(dmt^2);
R3 = 4*R(1:2,1:2)*(1/dmt^2);
fullz = [p;v;a];
fullR = blkdiag(R1,R2,R3);
Fx = getEvolution(x);
dotx = Fx*x;
if(dt > 0.00001)
    [px,pP]=Predict(x,P,dotx,Fx,dt,Q*dt);
else
    px = x;
    pP = P;
end
h = x(1:8);
Hx = [eye(8),zeros(8,2)];
[nx,nP]=kmeasure(px,pP,h,Hx,fullz,fullR);
end


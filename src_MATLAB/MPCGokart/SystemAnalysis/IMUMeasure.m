function [nx,nP] = IMUMeasure(x,P,dt,m, R,Q)
%measurement m = [ax,ay,dottheta]
%rotate measurement into world frame
Rot = @(theta)[1,0,0;0,cos(theta),-sin(theta);0,sin(theta),cos(theta)];
mw = Rot(x(3))*m;
Fx = getEvolution(x);
dotx = Fx*x;
if(dt > 0.00001)
    [px,pP]=Predict(x,P,dotx,Fx,dt,Q*dt);
else
    px = x;
    pP = P;
end
h = x(6:8);
Hx = [zeros(3,5),eye(3)];
%R(2,2)=100000;
%R(3,3)=100000;
[nx,nP]=kmeasure(px,pP,h,Hx,mw,R);
end
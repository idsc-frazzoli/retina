%code by mheim
function [nx,nP] = IMUMeasure(x,P,dt,m, R,Q)
% TODO define input parameters
%measurement m = [ax,ay,dottheta]
%rotate measurement into world frame
Rot = @(theta)[cos(theta),-sin(theta);sin(theta),cos(theta)];
fRot = @(theta)[1,0,0;0,cos(theta),-sin(theta);0,sin(theta),cos(theta)];
Fx = getEvolution(x);
dotx = Fx*x;
if(dt > 0.00001)
    [px,pP]=Predict(x,P,dotx,Fx,dt,Q);
else
    px = x;
    pP = P;
end
h = [x(6);Rot(-x(3))*x(7:8)+x(9:10)];
ad = [zeros(1,2);eye(2)];
%ad = zeros(3,2);
Hx = [zeros(3,5),fRot(-x(3)),ad];%there also has to be a term for x(3)
%R(2,2)=100000;
%R(3,3)=100000;
[nx,nP]=kmeasure(px,pP,h,Hx,m,R);
end

function [nx,nP] = IMUMeasure(x,P,dt,m, R,Q)
%measurement m = [ax,ay,dottheta]
Fx = getEvolution(x);
dotx = @(x)Fx*x;
[px,pP]=Predict(x,P,dotx,Fx,dt,Q*dt);
h = @(x)x(5:7);
Hx = [zeros(3,4),eye(3)];
[nx,nP]=kmeasure(px,pP,h,Hx,m,R);
end


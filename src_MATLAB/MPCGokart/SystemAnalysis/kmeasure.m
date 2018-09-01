%code by mheim
% TODO give reference for equations
% the equations deviate from 
% https://en.wikipedia.org/wiki/Kalman_filter
function [nx,nP] = kmeasure(x,P,h,Hx,z,R)
% TODO define input parameters in general terms
    y = z - h;
    S = Hx*P*Hx'+R;
    K = P*Hx'*inv(S);
    nx = x+K*y;
    nP = (eye(numel(x))-K*Hx)*P;
end

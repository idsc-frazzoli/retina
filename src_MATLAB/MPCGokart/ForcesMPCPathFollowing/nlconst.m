function v = nlconst(z,p,points)
global index
%NLCONST Summary of this function goes here
%   Detailed explanation goes here
% variables z = [ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
[splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
wantedpos = [splx;sply];
realPos = z([index.x,index.y]);
%not yet used here
error = realPos-wantedpos;
l = 1;
v1 = (tan(z(index.beta))*z(index.v)^2/l)^2+z(index.ab)^2;
%v1=(tan(z(8))*z(7)^2/l);
v2 = z(index.ab)-casadiGetMaxAcc(z(index.v));
v3 = -2.5+casadiGetMaxNegAcc(z(index.v))-z(index.ab);
%v4 = error'*error;
%v2 = -1;
%v = [v1;v2;v3];
v = [v1];
end


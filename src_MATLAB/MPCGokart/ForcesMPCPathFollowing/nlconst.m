function v = nlconst(z,p,points)
%NLCONST Summary of this function goes here
%   Detailed explanation goes here
% variables z = [ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
[splx,sply] = casadiDynamicBSPLINE(z(9),points);
wantedpos = [splx;sply];
realPos = z(4:5);
error = realPos-wantedpos;
l = 1;
v1 = (tan(z(8))*z(7)^2/l)^2+z(1)^2;
%v1=(tan(z(8))*z(7)^2/l);
v2 = z(1)-casadiGetMaxAcc(z(7));
v3 = -2.5+casadiGetMaxNegAcc(z(7))-z(1);
%v2 = -1;
v = [v1;v2;v3];
end


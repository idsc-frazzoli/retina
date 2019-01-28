function v = nlconst(z,p)
global index
%NLCONST Summary of this function goes here
%   Detailed explanation goes here
% variables z = [ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
l = 1.19;
beta  = z(index.beta);
dotbeta = z(index.dotbeta);
forwardacc = z(index.ab);
slack = z(index.slack);

pointsO = 7;
pointsN = 10;
points = getPointsFromParameters(p, pointsO, pointsN);
radii = getRadiiFromParameters(p, pointsO, pointsN)

[splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
[spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
[splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);
r = casadiDynamicBSPLINERadius(z(index.s),radii);

forward = [spldx;spldy];
sidewards = [splsx;splsy];
%[splx,sply] = casadiBSPLINE(z(9),points);
realPos = z([index.x,index.y]);
centerPos = realPos;%+0.4*forward;
wantedpos = [splx;sply];
error = centerPos-wantedpos;
lagerror = forward'*error;
laterror = sidewards'*error;

%parameters
vmax =  p(index.ps);
maxxacc = p(index.pax);
maxyacc = p(index.pay);
latacclim = p(index.pll);
rotacceffect = p(index.prae);
torqueveceffect = p(index.ptve);
brakeeffect = p(index.pbre);

ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
dAckermannAngle = -0.58*3*beta*beta*dotbeta+0.93*dotbeta;
tangentspeed = z(index.v);
latacc = (tan(ackermannAngle)*tangentspeed^2)/l;
%avoid oversteer
accnorm = ((latacc/maxyacc)^2+(z(index.ab)/maxxacc)^2);

%avoid understeer
rotacc = dAckermannAngle*tangentspeed/l;
frontaxlelatacc = latacc+rotacc*rotacceffect;
torquevectoringcapability = torqueveccapsmooth(forwardacc)*torqueveceffect;
%understeerright
%v6 = frontaxlelatacc - latacclim-torquevectoringcapability-slack;
v6 = frontaxlelatacc - latacclim-slack;
%understeerleft
%v7 = -frontaxlelatacc - latacclim-torquevectoringcapability-slack;
v7 = -frontaxlelatacc - latacclim-slack;


wantedpos = [splx;sply];
realPos = z([index.x,index.y]);
%not yet used here
error = realPos-wantedpos;
l = 1.19;
%v1 = (tan(z(index.beta))*z(index.v)^2/l)^2+z(index.ab)^2;
%v1=(tan(z(8))*z(7)^2/l);
v2 = z(index.ab)-casadiGetSmoothMaxAcc(z(index.v));
v3 = accnorm-slack;
v4 = laterror-r-slack;
v5 = -laterror-r-slack;

%v4 = error'*error;
%v2 = -1;
%v = [v1;v2;v3];
v = [v2;v3;v4;v5]%;v6;v7];
end


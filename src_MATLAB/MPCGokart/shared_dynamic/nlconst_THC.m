function v = nlconst_THC(z,p)
global index
%NLCONST Summary of this function goes here
%   Detailed explanation goes here
% variables z = [ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
l = 1.19;
beta  = z(index.beta);
dotbeta = z(index.dotbeta);
forwardacc = z(index.ab);
VELX = z(index.v);
VELY = z(index.yv);
slack = z(index.slack);

pointsO = index.pointsO;
pointsN = index.pointsN;
points = getPointsFromParameters(p, pointsO, pointsN);
radii = getRadiiFromParameters(p, pointsO, pointsN);

[splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
[spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
[splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);
r = casadiDynamicBSPLINERadius(z(index.s),radii);

forward = [spldx;spldy];
sidewards = [splsx;splsy];
%[splx,sply] = casadiBSPLINE(z(9),points);
realPos = z([index.x,index.y]);
%centerOffset = 0.2*gokartforward(z(index.theta))';
centerPos = realPos;
wantedpos = [splx;sply];
error = centerPos-wantedpos;
lagerror = forward'*error;
laterror = sidewards'*error;

%parameters
vmax =  p(index.ps);

ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
tangentspeed = z(index.v);

maxA = p(index.pax);
acclim = @(VELY,VELX, taccx)(VELX^2+VELY^2)*taccx^2-VELX^2*maxA^2;

l1 = 0.73;
l2 = l-l1;
f1n = l2/l;
f2n = l1/l;

wantedpos = [splx;sply];
realPos = z([index.x,index.y]);
%not yet used here
error = realPos-wantedpos;
%v1 = (tan(z(index.beta))*z(index.v)^2/l)^2+z(index.ab)^2;
%v1=(tan(z(8))*z(7)^2/l);
v1 = z(index.ab)+z(index.tv)-casadiGetSmoothMaxAcc(z(index.v));
v2 = z(index.ab)-z(index.tv)-casadiGetSmoothMaxAcc(z(index.v));
v3 = acclim(VELY,VELX,forwardacc);%-slack;
v4 = laterror-r-0.5*slack;
v5 = -laterror-r-0.5*slack;

%v4 = error'*error;
%v2 = -1;
%v = [v1;v2;v3];
v = [v1;v2;v3;v4;v5];
end


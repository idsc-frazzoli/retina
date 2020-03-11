function v = nlconst(z,p)
global index
%NLCONST Summary of this function goes here
%   Detailed explanation goes here

l = 1.19;
beta  = z(index.beta);
dotbeta = z(index.dotbeta);
forwardacc = z(index.ab);
slack = z(index.slack);

% Splines Control points and radii
pointsO = index.pointsO;
pointsN = index.pointsN;
points = getPointsFromParameters(p, pointsO, pointsN);
radii = getRadiiFromParameters(p, pointsO, pointsN);

% Splines
[splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
[spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
[splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);
r = casadiDynamicBSPLINERadius(z(index.s),radii);

forward = [spldx;spldy];
sidewards = [splsx;splsy];

realPos = z([index.x,index.y]);
centerOffset = 0.4*gokartforward(z(index.theta))';
centerPos = realPos+centerOffset;%+0.4*forward;
wantedpos = [splx;sply];

% Position error
error = centerPos-wantedpos;

% Projection errors
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

%simplified 
accbudget = (1.8-forwardacc)/1.6;
torquevectoringcapability = accbudget*torqueveceffect;
%torquevectoringcapability = torqueveccapsmooth(forwardacc)*torqueveceffect;

%% Constraints
%v1 = (tan(z(index.beta))*z(index.v)^2/l)^2+z(index.ab)^2;
%v1=(tan(z(8))*z(7)^2/l);
v2 = z(index.ab)-casadiGetSmoothMaxAcc(z(index.v));
v3 = accnorm-slack;
v4 = laterror-r-0.5*slack;
v5 = -laterror-r-0.5*slack;
%understeerright
%v6 = frontaxlelatacc - latacclim-torquevectoringcapability-slack;
v6 = frontaxlelatacc -torquevectoringcapability- latacclim-slack;
%understeerleft
%v7 = -frontaxlelatacc - latacclim-torquevectoringcapability-slack;
v7 = -frontaxlelatacc -torquevectoringcapability - latacclim-slack;

v = [v2;v3;v4;v5;v6;v7];
end


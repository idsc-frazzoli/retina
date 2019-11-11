l = 1.19;
beta  = history(i,index.beta+1);
dotbeta = history(i,index.dotbeta+1);
forwardacc = history(i,index.ab+1);
VELX = history(i,index.v+1);
VELY = history(i,index.yv+1);
slack = history(i,index.slack+1);
slack2= history(i,index.slack2+1);
pointsO = 4;
pointsN = 10;
p=problem.all_parameters;
PosVehicle2x=[xs2(1);Pos2(2:end,1);Pos2(end,1)];
PosVehicle2y=[xs2(2);Pos2(2:end,2);Pos2(end,2)];

points1 = getPointsFromParameters(p, pointsO, pointsN);
radii = getRadiiFromParameters(p, pointsO, pointsN);

[splx,sply] = casadiDynamicBSPLINE(history(i,index.s+1),points1);
[spldx, spldy] = casadiDynamicBSPLINEforward(history(i,index.s+1),points1);
[splsx, splsy] = casadiDynamicBSPLINEsidewards(history(i,index.s+1),points1);
r = casadiDynamicBSPLINERadius(history(i,index.s+1),radii);

forward = [spldx;spldy];
sidewards = [splsx;splsy];
%[splx,sply] = casadiBSPLINE(z(9),points);
realPos = history(i,[index.x+1,index.y+1]);
%centerOffset = 0.2*gokartforward(z(index.theta))';
centerPos = realPos';
wantedpos = [splx;sply];
error = centerPos-wantedpos;
lagerror = forward'*error;
laterror = sidewards'*error;
distance_X=(history(i,index.x+1)-PosVehicle2x(1));
distance_Y=(history(i,index.x+1)-PosVehicle2y(2));
squared_distance_array   = distance_X.^2+distance_Y.^2;
%parameters
vmax =  p(index.ps+1);

ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
tangentspeed = history(i,index.v+1);

maxA = p(index.pax);
acclim = @(VELY,VELX, taccx)(VELX^2+VELY^2)*taccx^2-VELX^2*maxA^2;

l = 1.19;
l1 = 0.73;
l2 = l-l1;
f1n = l2/l;
f2n = l1/l;

wantedpos = [splx;sply];
%not yet used here
error = realPos-wantedpos;
%v1 = (tan(z(index.beta))*z(index.v)^2/l)^2+z(index.ab)^2;
%v1=(tan(z(8))*z(7)^2/l);
v1 = history(i,index.ab+1)+history(i,index.tv+1)-casadiGetSmoothMaxAcc(history(i,index.v+1));
v2 = history(i,index.ab+1)-history(i,index.tv+1)-casadiGetSmoothMaxAcc(history(i,index.v+1));
v3 = acclim(VELY,VELX,forwardacc)-slack;
v4 = laterror-r-0.5*slack;
v5 = -laterror-r-0.5*slack;
v6 = squared_distance_array-0.6^2+0.1*slack2;
%v4 = error'*error;
%v2 = -1;
%v = [v1;v2;v3];
%v = [v1;v2;v3;v4;v5;v6];
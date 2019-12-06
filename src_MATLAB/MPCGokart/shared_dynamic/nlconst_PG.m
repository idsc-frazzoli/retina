function v = nlconst_PG(z,p)
global index
%NLCONST Summary of this function goes here

l = 1.19;
l1 = 0.73;
l2 = l-l1;
%f1n = l2/l;
%f2n = l1/l;
beta  = z(index.beta);
dotbeta = z(index.dotbeta);
forwardacc = z(index.ab);
VELX = z(index.v);
VELY = z(index.yv);
slack = z(index.slack);
slack2= z(index.slack2);

beta2  = z(index.beta_k2);
dotbeta2 = z(index.dotbeta_k2);
forwardacc2 = z(index.ab_k2);
VELX2 = z(index.v_k2);
VELY2 = z(index.yv_k2);
slack_k2 = z(index.slack_k2);

dist=p(index.dist);

pointsO = 22;
pointsN = 10;
pointsN2 = 10;
points = getPointsFromParameters(p, pointsO, pointsN);
radii = getRadiiFromParameters(p, pointsO, pointsN);

points2 = getPointsFromParameters(p, pointsO+3*pointsN, pointsN2);
radii2 = getRadiiFromParameters(p, pointsO+3*pointsN, pointsN2);

[splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
[spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
[splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);
r = casadiDynamicBSPLINERadius(z(index.s),radii);

[splx2,sply2] = casadiDynamicBSPLINE(z(index.s_k2),points2);
[spldx2, spldy2] = casadiDynamicBSPLINEforward(z(index.s_k2),points2);
[splsx2, splsy2] = casadiDynamicBSPLINEsidewards(z(index.s_k2),points2);
r2 = casadiDynamicBSPLINERadius(z(index.s_k2),radii2);

% Position Gokart 1
forward = [spldx;spldy];
sidewards = [splsx;splsy];
realPos = z([index.x,index.y]);
centerPos = realPos;
wantedpos = [splx;sply];
error = centerPos-wantedpos;
lagerror = forward'*error;
laterror = sidewards'*error;

% Position Gokart 2
forward2= [spldx2;spldy2];
sidewards2 = [splsx2;splsy2];
realPos2 = z([index.x_k2,index.y_k2]);
centerPos2 = realPos2;
wantedpos2 = [splx2;sply2];
error2 = centerPos2-wantedpos2;
lagerror2 = forward2'*error2;
laterror2 = sidewards2'*error2;

% Euclidean Distance
distance_X=(z(index.x)-z(index.x_k2));
distance_Y=(z(index.y)-z(index.y_k2));
squared_distance_array   = sqrt(distance_X.^2+distance_Y.^2);

%parameters
%vmax =  p(index.ps);
%ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
%tangentspeed = z(index.v);
%ackermannAngle2 = -0.58*beta2*beta2*beta2+0.93*beta2;
%tangentspeed2 = z(index.v_k2);

% Constraints
maxA = p(index.pax);
acclim = @(VELY,VELX, taccx)(VELX^2+VELY^2)*taccx^2-VELX^2*maxA^2;

v1 = z(index.ab)+z(index.tv)-casadiGetSmoothMaxAcc(z(index.v));
v2 = z(index.ab)-z(index.tv)-casadiGetSmoothMaxAcc(z(index.v));
v3 = acclim(VELY,VELX,forwardacc)-slack;
v4 = laterror-r-0.5*slack;
v5 = -laterror-r-0.5*slack;
%v6 = squared_distance_array-dist+slack2;
v7 = z(index.ab_k2)+z(index.tv_k2)-casadiGetSmoothMaxAcc(z(index.v_k2));
v8 = z(index.ab_k2)-z(index.tv_k2)-casadiGetSmoothMaxAcc(z(index.v_k2));
v9 = acclim(VELY2,VELX2,forwardacc2)-slack_k2;
v10 = laterror2-r2-0.5*slack_k2;
v11 = -laterror2-r2-0.5*slack_k2;

v = [v1;v2;v3;v4;v5;v7;v8;v9;v10;v11];%v6;
end


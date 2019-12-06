function v = nlconst_PG(z,p)
global index

% beta  = z(index.beta);
% dotbeta = z(index.dotbeta);
forwardacc = z(index.ab);
VELX = z(index.v);
VELY = z(index.yv);
slack = z(index.slack);

slack2= z(index.slack2);

% beta_k2  = z(index.beta_k2);
% dotbeta_k2 = z(index.dotbeta_k2);
forwardacc_k2 = z(index.ab_k2);
VELX_k2 = z(index.v_k2);
VELY_k2 = z(index.yv_k2);
slack_k2= z(index.slack_k2);

dist=p(index.dist);
pointsO = index.pointsO;
pointsN = index.pointsN;
pointsN2 = index.pointsN2;

points = getPointsFromParameters(p, pointsO, pointsN);
radii = getRadiiFromParameters(p, pointsO, pointsN);

points_k2 = getPointsFromParameters(p, pointsO+3*pointsN, pointsN2);
radii_k2 = getRadiiFromParameters(p, pointsO+3*pointsN, pointsN2);

[splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
[spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
[splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);
r = casadiDynamicBSPLINERadius(z(index.s),radii);

[splx_k2,sply_k2] = casadiDynamicBSPLINE(z(index.s_k2),points_k2);
[spldx_k2, spldy_k2] = casadiDynamicBSPLINEforward(z(index.s_k2),points_k2);
[splsx_k2, splsy_k2] = casadiDynamicBSPLINEsidewards(z(index.s_k2),points_k2);
r_k2 = casadiDynamicBSPLINERadius(z(index.s_k2),radii_k2);

forward = [spldx;spldy];
sidewards = [splsx;splsy];
realPos = z([index.x,index.y]);
centerPos = realPos;
wantedpos = [splx;sply];
error = centerPos-wantedpos;
lagerror = forward'*error;
laterror = sidewards'*error;

forward_k2 = [spldx_k2;spldy_k2];
sidewards_k2 = [splsx_k2;splsy_k2];
realPos_k2 = z([index.x_k2,index.y_k2]);
centerPos_k2 = realPos_k2;
wantedpos_k2 = [splx_k2;sply_k2];
error_k2 = centerPos_k2-wantedpos_k2;
lagerror_k2 = forward_k2'*error_k2;
laterror_k2 = sidewards_k2'*error_k2;

distance_X=(z(index.x)-z(index.x_k2));
distance_Y=(z(index.y)-z(index.y_k2));
squared_distance_array   = sqrt(distance_X.^2+distance_Y.^2);

%parameters
maxA = p(index.pax);
acclim = @(VELY,VELX, taccx)(VELX^2+VELY^2)*taccx^2-VELX^2*maxA^2;

v1 = z(index.ab)+z(index.tv)-casadiGetSmoothMaxAcc(z(index.v));
v2 = z(index.ab)-z(index.tv)-casadiGetSmoothMaxAcc(z(index.v));
v3 = acclim(VELY,VELX,forwardacc)-slack;
v4 = laterror-r-0.5*slack;
v5 = -laterror-r-0.5*slack;
v6 = z(index.ab_k2)+z(index.tv_k2)-casadiGetSmoothMaxAcc(z(index.v_k2));
v7 = z(index.ab_k2)-z(index.tv_k2)-casadiGetSmoothMaxAcc(z(index.v_k2));
v8 = acclim(VELY_k2,VELX_k2,forwardacc_k2)-slack_k2 ;
v9 = laterror_k2 -r_k2 -0.5*slack_k2 ;
v10 = -laterror_k2 -r_k2 -0.5*slack_k2 ;

v11 = squared_distance_array-dist+slack2;

v = [v1;v2;v3;v4;v5;v6;v7;v8;v9;v10;v11];
%v = [v1;v2;v3;v4;v5;v6;v7;v8;v9;v10;v11];
end


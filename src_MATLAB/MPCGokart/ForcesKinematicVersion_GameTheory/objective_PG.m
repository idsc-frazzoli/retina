function f = objective_PG(z,points,radii,points2,radii2,vmax, maxxacc,maxyacc,latacclim,rotacceffect,torqueveceffect, brakeeffect,plagerror, platerror, pprog, pab, pdotbeta, pspeedcost,pslack,pslack2)
    global index

    %get the fancy spline
    l = 1.19;
    [splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
    [spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
    [splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);
    r = casadiDynamicBSPLINERadius(z(index.s),radii);
    
    [splx_k2,sply_k2] = casadiDynamicBSPLINE(z(index.s_k2),points2);
    [spldx_k2, spldy_k2] = casadiDynamicBSPLINEforward(z(index.s_k2),points2);
    [splsx_k2, splsy_k2] = casadiDynamicBSPLINEsidewards(z(index.s_k2),points2);
    r_k2 = casadiDynamicBSPLINERadius(z(index.s_k2),radii2);
    
    forward = [spldx;spldy];
    sidewards = [splsx;splsy];
    
    realPos = z([index.x,index.y]);
    centerOffset = 0.4*gokartforward(z(index.theta))';
    centerPos = realPos+centerOffset;%+0.4*forward;
    wantedpos = [splx;sply];
    error = centerPos-wantedpos;
    lagerror = forward'*error;
    laterror = sidewards'*error;
    
    forward_k2 = [spldx_k2;spldy_k2];
    sidewards_k2 = [splsx_k2;splsy_k2];
    
    realPos_k2 = z([index.x_k2,index.y_k2]);
    centerOffset_k2 = 0.4*gokartforward(z(index.theta_k2))';
    centerPos_k2 = realPos_k2+centerOffset_k2;%+0.4*forward;
    wantedpos_k2 = [splx_k2;sply_k2];
    error_k2 = centerPos_k2-wantedpos_k2;
    lagerror_k2 = forward_k2'*error_k2;
    laterror_k2 = sidewards_k2'*error_k2;
    
    %latdist = abs(laterror);
    %outsideTrack = max(0,latdist-r);
    %trackViolation = outsideTrack^2;
    
    %% Costs objective function
    
    %beta = z(index.beta);
    %tangentspeed = z(index.v);
    %forwardacc = z(index.ab);
    slack = z(index.slack);
    slack2 = z(index.slack2);
    slack_k2 = z(index.slack_k2);
    %dotbeta = z(index.dotbeta);
    %ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
    %dAckermannAngle = -0.58*3*beta*beta*dotbeta+0.93*dotbeta;
    %latacc = (tan(ackermannAngle)*tangentspeed^2)/l;
    %rotacc = dAckermannAngle*tangentspeed/l;
    %frontaxlelatacc = abs(latacc+rotacc*rotacceffect);
    %torquevectoringcapability = torqueveccapsmooth(forwardacc)*torqueveceffect;
    %torquevectoringcapability = 0;
    %understeer = max(0,frontaxlelatacc - latacclim-torquevectoringcapability)^2;
    %accnorm = ((latacc/maxyacc)^2+(z(index.ab)/maxxacc)^2);
    %accviolation = max(0,accnorm-1)^2;
    
    speedcost = speedPunisher(z(index.v),vmax)*pspeedcost;
    lagcost = plagerror*lagerror^2;
    latcost = platerror*laterror^2;
    prog = -pprog*z(index.ds);
    reg = z(index.dotab).^2*pab+z(index.dotbeta).^2*pdotbeta;
    
    speedcost_k2 = speedPunisher(z(index.v_k2),vmax)*pspeedcost;
    lagcost_k2 = plagerror*lagerror_k2^2;
    latcost_k2 = platerror*laterror_k2^2;
    prog_k2 = -pprog*z(index.ds_k2);
    reg_k2 = z(index.dotab_k2).^2*pab+z(index.dotbeta_k2).^2*pdotbeta;
    %f = error'*Q*error+reg+speedcost+over75d*over75d*0.001+1*trackViolation;
    %f = lagcost+latcost+reg+prog+over75d*over75d*0.001+speedcost+accviolation+trackViolation;
    f = lagcost+latcost+reg+prog+pslack*slack+speedcost+...
        lagcost_k2+latcost_k2+reg_k2+prog_k2+pslack*slack_k2+...
        speedcost_k2+pslack2*slack2;
end

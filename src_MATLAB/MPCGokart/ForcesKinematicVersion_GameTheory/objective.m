function f = objective(z,points,radii,vmax, maxxacc,maxyacc,latacclim,rotacceffect,torqueveceffect, brakeeffect,plagerror, platerror, pprog, pab, pdotbeta, pspeedcost,pslack)
    global index

    %get the fancy spline
    l = 1.19;
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
    error = centerPos-wantedpos;
    lagerror = forward'*error;
    laterror = sidewards'*error;
    
    %latdist = abs(laterror);
    %outsideTrack = max(0,latdist-r);
    %trackViolation = outsideTrack^2;
    
    %% Costs objective function
    
    %beta = z(index.beta);
    %tangentspeed = z(index.v);
    %forwardacc = z(index.ab);
    slack = z(index.slack);
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
    
    %f = error'*Q*error+reg+speedcost+over75d*over75d*0.001+1*trackViolation;
    %f = lagcost+latcost+reg+prog+over75d*over75d*0.001+speedcost+accviolation+trackViolation;
    f = lagcost+latcost+reg+prog+pslack*slack+speedcost;
end

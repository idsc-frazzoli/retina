function f = objective(z,points,radii,vmax, maxxacc,maxyacc,latacclim,rotacceffect,torqueveceffect, brakeeffect)
    global index
%[ab,dotbeta,ds, x,y,theta,v,beta,s,braketemp]
    %get the fancy spline
    l = 1.19;
    [splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
    [spldx, spldy] = casadiDynamicBSPLINEforward(z(index.s),points);
    [splsx, splsy] = casadiDynamicBSPLINEsidewards(z(index.s),points);
    r = casadiDynamicBSPLINERadius(z(index.s),radii);
    forward = [spldx;spldy];
    sidewards = [splsx;splsy];
    %[splx,sply] = casadiBSPLINE(z(9),points);
    realPos = z([index.x,index.y]);
    centerPos = realPos;%+0.4*forward;
    %over75d = max(0,z(index.braketemp)-75);
    %wantedpos = p;
    wantedpos = [splx;sply];
    %wantedpos = [5;0];
    %error = realPos-wantedpos;
    error = centerPos-wantedpos;
    lagerror = forward'*error;
    laterror = sidewards'*error;
    latdist = abs(laterror);
    outsideTrack = max(0,latdist-r);
    trackViolation = outsideTrack^2;
    speedcost = speedPunisher(z(index.v),vmax)*0.04;
    beta = z(index.beta);
    tangentspeed = z(index.v);
    forwardacc = z(index.ab);
    slack = z(index.slack);
    dotbeta = z(index.dotbeta);
    ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
    dAckermannAngle = -0.58*3*beta*beta*dotbeta+0.93*dotbeta;
    latacc = (tan(ackermannAngle)*tangentspeed^2)/l;
    rotacc = dAckermannAngle*tangentspeed/l;
    frontaxlelatacc = abs(latacc+rotacc*rotacceffect);
    torquevectoringcapability = torqueveccapsmooth(forwardacc)*torqueveceffect;
    %torquevectoringcapability = 0;
    understeer = max(0,frontaxlelatacc - latacclim-torquevectoringcapability)^2;
    accnorm = ((latacc/maxyacc)^2+(z(index.ab)/maxxacc)^2);
    %accviolation = max(0,accnorm-1)^2;
    lagcost = lagerror^2;
    latcost = laterror^2;
    prog = -0.2*z(index.ds);
    reg = z(index.dotab).^2*0.0004+z(index.dotbeta).^2*0.01;
    
    %f = error'*Q*error+reg+speedcost+over75d*over75d*0.001+1*trackViolation;
    %f = lagcost+latcost+reg+prog+over75d*over75d*0.001+speedcost+accviolation+trackViolation;
    f = lagcost+latcost*0.01+reg+prog+5*slack+understeer+speedcost;
end

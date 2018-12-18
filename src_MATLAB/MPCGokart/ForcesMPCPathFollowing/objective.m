function f = objective(z,points,radii,vmax)
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
    over75d = max(0,z(index.braketemp)-75);
    %wantedpos = p;
    wantedpos = [splx;sply];
    %wantedpos = [5;0];
    error = realPos-wantedpos;
    lagerror = forward'*error;
    laterror = sidewards'*error;
    latdist = abs(laterror);
    outsideTrack = max(0,latdist-r);
    trackViolation = outsideTrack^2;
    speedcost = speedPunisher(z(index.v),vmax)*0.1;
    accnorm = (tan(z(index.beta))*z(index.v)^2/l)^2+z(index.ab)^2;
    accviolation = 0.001*max(0,accnorm-25)^2;
    lagcost = lagerror^2;
    latcost = laterror^2;
    prog = -0.2*z(index.ds);
    reg = z(index.dotab).^2*0.0004+z(index.dotbeta).^2*0.001;
    
    %f = error'*Q*error+reg+speedcost+over75d*over75d*0.001+1*trackViolation;
    %f = lagcost+latcost+reg+prog+over75d*over75d*0.001+speedcost+accviolation+trackViolation;
    f = lagcost+latcost*0.01+reg+prog+over75d*over75d*0.001+speedcost+accviolation+trackViolation;
end

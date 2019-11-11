function f = objectiveHC(z,points,radii,vmax, maxxacc, steeringreg,plag,plat,pprog,pab,pspeedcost,pslack,ptv)
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
    %centerOffset = 0.2*gokartforward(z(index.theta))';
    centerPos = realPos;
    %over75d = max(0,z(index.braketemp)-75);
    %wantedpos = p;
    wantedpos = [splx;sply];
    %wantedpos = [5;0];
    %error = realPos-wantedpos;
    error = centerPos-wantedpos;
    lagerror = forward'*error;
    laterror = sidewards'*error;
    speedcost = speedPunisher(z(index.v),vmax)*pspeedcost;
    slack = z(index.slack);
    tv = z(index.tv);
    %accviolation = max(0,accnorm-1)^2;
    lagcost = plag*lagerror^2;
    latcost = plat*laterror^2;
    prog = -pprog*z(index.ds);
    reg = z(index.dotab).^2*pab+z(index.dotbeta).^2*steeringreg;
    
    %f = error'*Q*error+reg+speedcost+over75d*over75d*0.001+1*trackViolation;
    %f = lagcost+latcost+reg+prog+over75d*over75d*0.001+speedcost+accviolation+trackViolation;
    f = lagcost+latcost+reg+prog+pslack*slack+speedcost+ptv*tv^2;%-0.01*sidewardsspeed^2;
end

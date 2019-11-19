function f = objective_GT(z,points,radii,vmax, maxxacc, steeringreg,plag,plat,pprog,pab,pspeedcost,pslack,pslack2,ptv)
    global index
%[ab,dotbeta,ds, x,y,theta,v,beta,s,braketemp]
    %get the fancy spline
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
    slack2=z(index.slack2);
    tv = z(index.tv);
    %accviolation = max(0,accnorm-1)^2;
    lagcost = plag*lagerror^2;
    latcost = plat*laterror^2;
    prog = -pprog*z(index.ds);
    reg = z(index.dotab).^2*pab+z(index.dotbeta).^2*steeringreg;
    
    %f = error'*Q*error+reg+speedcost+over75d*over75d*0.001+1*trackViolation;
    %f = lagcost+latcost+reg+prog+over75d*over75d*0.001+speedcost+accviolation+trackViolation;
    f = lagcost+latcost+reg+prog+pslack*slack+pslack2*slack2+speedcost+ptv*tv^2;%-0.01*sidewardsspeed^2;
end

function f = objective(z,points,vmax)
    global index
%[ab,dotbeta,ds, x,y,theta,v,beta,s,braketemp]
    %get the fancy spline
    [splx,sply] = casadiDynamicBSPLINE(z(index.s),points);
    %[splx,sply] = casadiBSPLINE(z(9),points);
    realPos = z(index.x:index.y);
    over75d = max(0,z(index.braketemp)-75);
    %wantedpos = p;
    wantedpos = [splx;sply];
    %wantedpos = [5;0];
    error = realPos-wantedpos;
    dist = (error'*error)^0.5;
    outsideTrack = max(0,dist-4);
    %this is cubic test this
    trackViolation = outsideTrack^2;
    speedcost = speedPunisher(z(index.v),vmax);
    Q = eye(2)*0.1;
    reg = z(index.ab).^2*0.01+z(index.dotbeta).^2*0.01+z(index.ds).^2*0.01;
    
    f = error'*Q*error+reg+speedcost+over75d*over75d*0.001+2*trackViolation;
end

function f = objective(z,points,vmax)
%[ab,dotbeta,ds, x,y,theta,v,beta,s,braketemp]
    %get the fancy spline
    [splx,sply] = casadiDynamicBSPLINE(z(9),points);
    %[splx,sply] = casadiBSPLINE(z(9),points);
    realPos = z(4:5);
    over75d = max(0,z(10)-75);
    %wantedpos = p;
    wantedpos = [splx;sply];
    %wantedpos = [5;0];
    error = realPos-wantedpos;
    dist = (error'*error)^0.5;
    outsideTrack = max(0,dist-4);
    %this is cubic test this
    trackViolation = outsideTrack^2;
    speedcost = speedPunisher(z(7),vmax);
    Q = eye(2)*0.1;
    P = diag([1,1,0.01,0,0,0,0,0,0,0])*0.01;
    f = error'*Q*error+z'*P*z+speedcost+over75d*over75d*0.001+2*trackViolation;
end

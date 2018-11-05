function f = objective(z,points,vmax)
%[ab,dotbeta,ds, x,y,theta,v,beta,s,braketemp]
    %get the fancy spline
    [splx,sply] = casadiDynamicBSPLINE(z(9),points);
    %[splx,sply] = casadiBSPLINE(z(9),points);
    realPos = z(4:5);
    %wantedpos = p;
    wantedpos = [splx;sply];
    %wantedpos = [5;0];
    error = realPos-wantedpos;
    speedcost = speedPunisher(z(7),vmax);
    Q = eye(2);
    P = diag([1,1,1,0,0,0,0,0,0,0])*0.0001;
    f = error'*Q*error+z'*P*z-0.1*z(3)+speedcost;
end

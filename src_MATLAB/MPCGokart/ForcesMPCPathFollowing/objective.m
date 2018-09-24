function f = objective(z,p,points)
    %get the fancy spline
    [splx,sply] = acadiBSPLINE(z(9)+2,points);
    realPos = z(4:5);
    %wantedpos = p;
    wantedpos = [splx;sply];
    %wantedpos = [5;0];
    error = realPos-wantedpos;
    Q = eye(2);
    P = diag([1,1,1,0,0,0,0,0,0])*0.0001;
    f = error'*Q*error+z'*P*z-0.1*z(3);
end

function f = objective(z,p,points)
    %get the fancy spline
    [splx,sply] = acadiBSPLINE(z(8)+2,points);
    realPos = z(3:4);
    %wantedpos = p;
    wantedpos = [splx;sply];
    error = realPos-wantedpos;
    Q = eye(2);
    f = error'*Q*error+z'*z*0.001;
end


function f = objective(z,p)
    realPos = z(3:4);
    wantedpos = p;
    error = realPos-wantedpos;
    Q = eye(2);
    f = error'*Q*error+z'*z*0.001;
end


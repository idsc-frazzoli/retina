function acc = getSmoothMaxAcc(x)
%used for testing before using it in casadi
p0 = 2.0892;
pvel = -0.0107;
p0n = -1.5466;
pveln = -0.0293;
st = 0.5;
if(false)
    st = 0.5;
    x0 = st;
    y0 = finalpower(x0,2000);
    x1 = 5;
    y1 = finalpower(x1,2000);
    p = polyfit([x0,x1],[y0,y1],1);
    p0 = p(2);
    pvel = p(1);

    st = 0.5;
    x0 = st;
    y0 = finalpower(x0,-2000);
    x1 = 5;
    y1 = finalpower(x1,-2000);
    p = polyfit([x0,x1],[y0,y1],1);
    p0n = p(2);
    pveln = p(1);
end

cp = @(v)p0+pvel*v;
cn = @(v)p0n+pveln*v;
si = @(x)0.5+1.5*x-2*x^3;
if(x>st)
    acc = cp(x);
elseif(x>-st)
    posval = cp(st);
    negval = -cn(st);
    acc = negval*(1-si(x))+posval*si(x);
else
    acc = -cn(-x);
end


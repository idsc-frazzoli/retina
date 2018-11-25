function fun = getsmoothinter(from,to)
    p0 = @(x)1;
    p1 = @(x)x;
    p2 = @(x)x^2;
    p3 = @(x)x^3;
    dp0 = @(x)0;
    dp1 = @(x)1;
    dp2 = @(x)2*x;
    dp3 = @(x)3*x^2;
    
    A=[p0(from),p1(from),p2(from),p3(from);dp0(from),dp1(from),dp2(from),dp3(from);p0(to),p1(to),p2(to),p3(to);dp0(to),dp1(to),dp2(to),dp3(to);]
    b = [0,0,1,0]'
    facs = A\b
    fun = @(x) facs(1)+facs(2)*x+facs(3)*x^2+facs(4)*x^3;
end


function dx = interstagedx(x,u)
    %evolution:
    %maxacc = casadiGetMaxAcc(x);
    %minacc = casadiGetMaxNegAcc(x);
    %ab = 0.5*(maxacc-minacc)*(u(1)+1);
    ab = u(1);
    dotbeta = u(2);
    ds = u(3);
    brake = u(4);
    %ds = 0.1;
    theta = x(3);
    v = x(4);
    beta = x(5);
    l = 1;
   
    dx = [v*cos(theta);
    v*sin(theta);
    v/l*tan(beta);
    ab-brake;
    dotbeta;
    ds;
    0];
end


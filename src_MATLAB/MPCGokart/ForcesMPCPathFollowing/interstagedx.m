function dx = interstagedx(x,u)
    %[ab,dotbeta,ds,brake / x,y,theta,v,beta,s,braketemp]
    %evolution:
    %maxacc = casadiGetMaxAcc(x);
    %minacc = casadiGetMaxNegAcc(x);
    %ab = 0.5*(maxacc-minacc)*(u(1)+1);
    ab = u(1);
    dotbeta = u(2);
    ds = u(3);
    %ds = 0.03;
    theta = x(3);
    v = x(4);
    beta = x(5);
    speed = x(4);
    temp = x(7);
    %braking=max(0,-ab+casadiGetMaxNegAcc(speed));
    braking = heatupfunction(-ab-1.5);
    l = 1.19;
    
    ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
   
    dx = [v*cos(theta);
    v*sin(theta);
    v/l*tan(ackermannAngle);
    ab;
    dotbeta;
    ds;
    %heatupfunction(brake)+cooldownfunction(temp)];
    %cooldownfunction(temp)];
    braking+cooldownfunction(temp)];
end


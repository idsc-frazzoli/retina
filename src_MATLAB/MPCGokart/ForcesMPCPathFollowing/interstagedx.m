function dx = interstagedx(x,u)
    global index
    %[ab,dotbeta,ds,brake / x,y,theta,v,beta,s,braketemp]
    %evolution:
    %maxacc = casadiGetMaxAcc(x);
    %minacc = casadiGetMaxNegAcc(x);
    %ab = 0.5*(maxacc-minacc)*(u(1)+1);
    dotab = u(index.dotab);
    ab = x(index.ab-index.nu);
    dotbeta = u(index.dotbeta);
    ds = u(index.ds);
    %ds = 0.03;
    theta = x(index.theta-index.nu);
    v = x(index.v-index.nu);
    beta = x(index.beta-index.nu);
    %temp = x(index.braketemp-index.nu);
    %braking=max(0,-ab+casadiGetMaxNegAcc(speed));
    %brakingheatup = heatupfunction(-ab-1.5);
    %brakingcooldown = cooldownfunction(temp);
    l = 1.19;
    ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
   
    import casadi.*
    if isa(x(1), 'double')
        dx = zeros(index.ns,1);
    else
        dx = SX.zeros(index.ns,1);
    end
    dx(index.x-index.nu)=v*cos(theta);
    dx(index.y-index.nu)=v*sin(theta);
    dx(index.theta-index.nu)=v/l*tan(ackermannAngle);
    dx(index.v-index.nu)=ab;
    dx(index.beta-index.nu)=dotbeta;
    dx(index.s-index.nu)=ds;
    %dx(index.braketemp-index.nu)=brakingheatup+brakingcooldown;
    dx(index.ab-index.nu)=dotab;
    
    %dx = [v*cos(theta);
    %v*sin(theta);
    %v/l*tan(ackermannAngle);
    %ab;
    %dotbeta;
    %ds;
    %braking+cooldownfunction(temp)];
end


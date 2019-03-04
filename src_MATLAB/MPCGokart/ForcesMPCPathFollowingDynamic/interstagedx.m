function dx = interstagedx(x,u)
    addpath('../TireAnalysis');
    global index
    
    %just for the moment
    B = 4;
    C = 1.7;
    D = 0.7*9.81;
    maxA = D*0.9;
    Cf = 0.15;
    B1 = B;
    B2 = B;
    C1 = C;
    C2 = C;
    D1 = 0.8*D;
    D2 = D;
    param = [B1,C1,D1,B2,C2,D2,Cf,maxA];

    %[ab,dotbeta,ds,brake / x,y,theta,v,beta,s,braketemp]
    %evolution:
    %maxacc = casadiGetMaxAcc(x);
    %minacc = casadiGetMaxNegAcc(x);
    %ab = 0.5*(maxacc-minacc)*(u(1)+1);
    dotab = u(index.dotab);
    ab = x(index.ab-index.nu);
    tv = u(index.tv);
    dotbeta = u(index.dotbeta);
    ds = u(index.ds);
    %ds = 0.03;
    theta = x(index.theta-index.nu);
    vx = x(index.v-index.nu);
    vy = x(index.yv-index.nu);
    dottheta = x(index.dottheta-index.nu);
    beta = x(index.beta-index.nu);
    %temp = x(index.braketemp-index.nu);
    %braking=max(0,-ab+casadiGetMaxNegAcc(speed));
    %brakingheatup = heatupfunction(-ab-1.5);
    %brakingcooldown = cooldownfunction(temp);
    l = 1.19;
    ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
   
    [ACCX,ACCY,ACCROTZ,frontabcorr] = modelDx(vx,vy,dottheta,ackermannAngle,ab,tv, param);
    
    
    import casadi.*
    if isa(x(1), 'double')
        dx = zeros(index.ns,1);
    else
        dx = SX.zeros(index.ns,1);
    end
    dx(index.x-index.nu)=vx*cos(theta);
    dx(index.y-index.nu)=vx*sin(theta);
    dx(index.theta-index.nu)=vx/l*tan(ackermannAngle);
    dx(index.v-index.nu)=ACCX;
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


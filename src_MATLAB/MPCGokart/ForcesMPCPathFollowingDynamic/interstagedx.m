function dx = interstagedx(x,u)
    addpath('../TireAnalysis');
    global index
    
    %just for the moment
    B = 8;
    C = 1.7;
    D = 0.7*9.81;
    maxA = D*0.95;
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
    %beta = 0;
    %temp = x(index.braketemp-index.nu);
    %braking=max(0,-ab+casadiGetMaxNegAcc(speed));
    %brakingheatup = heatupfunction(-ab-1.5);
    %brakingcooldown = cooldownfunction(temp);
    l = 1.19;
    ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
   
    %(VELX,VELY,VELROTZ,BETA,AB,TV, param)
    %[ACCX,ACCY,ACCROTZ,frontabcorr] = modelDx(vx,vy,dottheta,ackermannAngle,ab,tv, param);
    [ACCX,ACCY,ACCROTZ,frontabcorr] = modelDx(vx,vy,dottheta,ackermannAngle,ab,tv, param);
    
    
    import casadi.*
    if isa(x(1), 'double')
        dx = zeros(index.ns,1);
    else
        dx = SX.zeros(index.ns,1);
    end
    rotmat = @(beta)[cos(beta),-sin(beta);sin(beta),cos(beta)];
    lv = [vx;vy];
    gv = rotmat(theta)*lv;
    dx(index.x-index.nu)=gv(1);
    dx(index.y-index.nu)=gv(2);
    dx(index.dottheta-index.nu)=ACCROTZ;
    dx(index.theta-index.nu)=dottheta;
    %dx(index.theta-index.nu)=vx/l*tan(ackermannAngle);
    dx(index.v-index.nu)=ACCX;
    dx(index.yv-index.nu)=ACCY;
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


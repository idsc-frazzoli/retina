function dx = interstagedx_PG(x,u,p)
    addpath('../TireAnalysis');
    %#codegen
    global index
    
    Cf = p(index.pmoi); % moment of inertia    
    FB = p(index.pacFB);
    FC = p(index.pacFC);
    FD = p(index.pacFD); % gravity acceleration considered

    RB = p(index.pacRB);
    RC = p(index.pacRC);
    RD = p(index.pacRD); % gravity acceleration considered
    param = [FB,FC,FD,RB,RC,RD,Cf];
    
    % go kart 1
    dotab = u(index.dotab);
    tv = u(index.tv);
    dotbeta = u(index.dotbeta);
    ds = u(index.ds);
    ab = x(index.ab-index.nu);
    theta = x(index.theta-index.nu);
    vx = x(index.v-index.nu);
    vy = x(index.yv-index.nu);
    dottheta = x(index.dottheta-index.nu);
    beta = x(index.beta-index.nu); % from steering.
    ackermannAngle = -0.63.*beta.*beta.*beta+0.94*beta; %ackermann Mapping 
    [ACCX,ACCY,ACCROTZ] = modelDx(vx,vy,dottheta,ackermannAngle,ab,tv, param);
    
    % go kart 2
    dotab_k2 = u(index.dotab_k2);
    tv_k2 = u(index.tv_k2);
    dotbeta_k2 = u(index.dotbeta_k2);
    ds_k2 = u(index.ds_k2);
    ab_k2 = x(index.ab_k2-index.nu);
    theta_k2 = x(index.theta_k2-index.nu);
    vx_k2 = x(index.v_k2-index.nu);
    vy_k2 = x(index.yv_k2-index.nu);
    dottheta_k2 = x(index.dottheta_k2-index.nu);
    beta_k2 = x(index.beta_k2-index.nu); % from steering.
    ackermannAngle_k2 = -0.63.*beta_k2.*beta_k2.*beta_k2+0.94*beta_k2; %ackermann Mapping 
    [ACCX_k2,ACCY_k2,ACCROTZ_k2] = modelDx(vx_k2,vy_k2,dottheta_k2,ackermannAngle_k2,ab_k2,tv_k2, param);
    
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
    dx(index.dottheta-index.nu)=ACCROTZ; % dot_dot_Phi
    dx(index.theta-index.nu)=dottheta;   % dot_Phi
    dx(index.v-index.nu)=ACCX;           
    dx(index.yv-index.nu)=ACCY;
    dx(index.beta-index.nu)=dotbeta;
    dx(index.s-index.nu)=ds;
    dx(index.ab-index.nu)=dotab;
    
    lv_k2 = [vx_k2;vy_k2];
    gv_k2 = rotmat(theta_k2)*lv_k2;
    dx(index.x_k2-index.nu)=gv_k2(1);
    dx(index.y_k2-index.nu)=gv_k2(2);
    dx(index.dottheta_k2-index.nu)=ACCROTZ_k2; % dot_dot_Phi
    dx(index.theta_k2-index.nu)=dottheta_k2;   % dot_Phi
    dx(index.v_k2-index.nu)=ACCX_k2;           
    dx(index.yv_k2-index.nu)=ACCY_k2;
    dx(index.beta_k2-index.nu)=dotbeta_k2;
    dx(index.s_k2-index.nu)=ds_k2;
    dx(index.ab_k2-index.nu)=dotab_k2;
end


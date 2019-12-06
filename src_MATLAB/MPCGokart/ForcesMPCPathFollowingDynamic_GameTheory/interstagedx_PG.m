function dx = interstagedx_PG(x,u,p)
    addpath('../TireAnalysis');

    global index
    % parameters
    Cf = p(index.pmoi); % moment of inertia    

    FB = p(index.pacFB);
    FC = p(index.pacFC);
    FD = p(index.pacFD); % gravity acceleration considered

    RB = p(index.pacRB);
    RC = p(index.pacRC);
    RD = p(index.pacRD); % gravity acceleration considered
    param = [FB,FC,FD,RB,RC,RD,Cf];
    
    % inputs kart1
    dotab = u(index.dotab);
    ab = x(index.ab-index.nu);
    tv = u(index.tv);
    dotbeta = u(index.dotbeta);
    ds = u(index.ds);
    
    % states kart1
    theta = x(index.theta-index.nu);
    vx = x(index.v-index.nu);
    vy = x(index.yv-index.nu);
    dottheta = x(index.dottheta-index.nu);
    beta = x(index.beta-index.nu); % from steering.
    
    % inputs kart 2
    dotab2 = u(index.dotab_k2);
    ab2 = x(index.ab_k2-index.nu);
    tv2 = u(index.tv_k2);
    dotbeta2 = u(index.dotbeta_k2);
    ds2 = u(index.ds_k2);
    
    % states kart 2
    theta2 = x(index.theta_k2-index.nu);
    vx2 = x(index.v_k2-index.nu);
    vy2 = x(index.yv_k2-index.nu);
    dottheta2 = x(index.dottheta_k2-index.nu);
    beta2 = x(index.beta_k2-index.nu); % from steering.
    
    ackermannAngle = -0.63.*beta.*beta.*beta+0.94*beta; %ackermann Mapping 
    [ACCX,ACCY,ACCROTZ] = modelDx(vx,vy,dottheta,ackermannAngle,ab,tv, param);
    
    ackermannAngle2 = -0.63.*beta2.*beta2.*beta2+0.94*beta2; %ackermann Mapping 
    [ACCX2,ACCY2,ACCROTZ2] = modelDx(vx2,vy2,dottheta2,ackermannAngle2,ab2,tv2, param);
    
    import casadi.*
    if isa(x(1), 'double')
        dx = zeros(index.ns,1);
    else
        dx = SX.zeros(index.ns,1);
    end
    rotmat = @(beta)[cos(beta),-sin(beta);sin(beta),cos(beta)];
    lv = [vx;vy];
    
    gv = rotmat(theta)*lv;
      
    % Kart 1
    dx(index.x-index.nu)=gv(1);
    dx(index.y-index.nu)=gv(2);
    dx(index.dottheta-index.nu)=ACCROTZ; % dot_dot_Phi
    dx(index.theta-index.nu)=dottheta;   % dot_Phi
    dx(index.v-index.nu)=ACCX;           
    dx(index.yv-index.nu)=ACCY;
    dx(index.beta-index.nu)=dotbeta;
    dx(index.s-index.nu)=ds;
    dx(index.ab-index.nu)=dotab;
    % Kart 2
    lv2=[vx2;vy2];
    gv2 = rotmat(theta2)*lv2;
    dx(index.x_k2-index.nu)=gv2(1);
    dx(index.y_k2-index.nu)=gv2(2);
    dx(index.dottheta_k2-index.nu)=ACCROTZ2; % dot_dot_Phi2
    dx(index.theta_k2-index.nu)=dottheta2;   % dot_Phi2
    dx(index.v_k2-index.nu)=ACCX2;           
    dx(index.yv_k2-index.nu)=ACCY2;
    dx(index.beta_k2-index.nu)=dotbeta2;
    dx(index.s_k2-index.nu)=ds2;
    dx(index.ab_k2-index.nu)=dotab2;
   
end


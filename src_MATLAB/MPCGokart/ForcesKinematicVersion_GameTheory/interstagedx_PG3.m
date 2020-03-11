function dx = interstagedx_PG3(x,u)
    global index
    
    % Inputs 
    dotab = u(index.dotab);
    ab = x(index.ab-index.nu);
    dotbeta = u(index.dotbeta);
    ds = u(index.ds);
    
    dotab_k2 = u(index.dotab_k2);
    ab_k2 = x(index.ab_k2-index.nu);
    dotbeta_k2 = u(index.dotbeta_k2);
    ds_k2 = u(index.ds_k2);
    
    dotab_k3 = u(index.dotab_k3);
    ab_k3 = x(index.ab_k3-index.nu);
    dotbeta_k3 = u(index.dotbeta_k3);
    ds_k3 = u(index.ds_k3);
    
    % States
    theta = x(index.theta-index.nu);
    v = x(index.v-index.nu);
    beta = x(index.beta-index.nu);
    
    theta_k2 = x(index.theta_k2-index.nu);
    v_k2 = x(index.v_k2-index.nu);
    beta_k2 = x(index.beta_k2-index.nu);
    
    theta_k3 = x(index.theta_k3-index.nu);
    v_k3 = x(index.v_k3-index.nu);
    beta_k3 = x(index.beta_k3-index.nu);
    
    l = 1.19;
    ackermannAngle = -0.58*beta*beta*beta+0.93*beta;
    ackermannAngle_k2 = -0.58*beta_k2*beta_k2*beta_k2+0.93*beta_k2;
    ackermannAngle_k3 = -0.58*beta_k3*beta_k3*beta_k3+0.93*beta_k3;
    
    import casadi.*
    if isa(x(1), 'double')
        dx = zeros(index.ns,1);
    else
        dx = SX.zeros(index.ns,1);
    end
    
    % Update states
    dx(index.x-index.nu)=v*cos(theta);
    dx(index.y-index.nu)=v*sin(theta);
    dx(index.theta-index.nu)=v/l*tan(ackermannAngle);
    dx(index.v-index.nu)=ab;
    dx(index.beta-index.nu)=dotbeta;
    dx(index.s-index.nu)=ds;
    dx(index.ab-index.nu)=dotab;
    
    dx(index.x_k2-index.nu)=v_k2*cos(theta_k2);
    dx(index.y_k2-index.nu)=v_k2*sin(theta_k2);
    dx(index.theta_k2-index.nu)=v_k2/l*tan(ackermannAngle_k2);
    dx(index.v_k2-index.nu)=ab_k2;
    dx(index.beta_k2-index.nu)=dotbeta_k2;
    dx(index.s_k2-index.nu)=ds_k2;
    dx(index.ab_k2-index.nu)=dotab_k2;
    
    dx(index.x_k3-index.nu)=v_k3*cos(theta_k3);
    dx(index.y_k3-index.nu)=v_k3*sin(theta_k3);
    dx(index.theta_k3-index.nu)=v_k3/l*tan(ackermannAngle_k3);
    dx(index.v_k3-index.nu)=ab_k3;
    dx(index.beta_k3-index.nu)=dotbeta_k3;
    dx(index.s_k3-index.nu)=ds_k3;
    dx(index.ab_k3-index.nu)=dotab_k3;
    %dx = [v*cos(theta);
    %v*sin(theta);
    %v/l*tan(ackermannAngle);
    %ab;
    %dotbeta;
    %ds;
    %braking+cooldownfunction(temp)];
end


function [maxSpeed,maxxacc,steeringreg,specificmoi,plag,plat,pprog,pab,pspeedcost,pslack,ptv] = DriverConfig(behaviour)
if strcmp(behaviour,'aggressive')==1
    maxSpeed = 10; % in [m/s]
    maxxacc = 5; % in [m/s^-1]
    steeringreg = 0.02;  
    specificmoi = 0.3;
    plag=1;
    plat=0.01;
    pprog=0.2;
    pab=0.0004;
    pspeedcost=0.04;
    pslack=5;
    ptv=0.01;
elseif strcmp(behaviour,'medium')==1
    maxSpeed = 10; % in [m/s]
    maxxacc = 5; % in [m/s^-1]
    steeringreg = 0.05;  
    specificmoi = 0.3;
    plag=1;
    plat=0.06;
    pprog=0.15;
    pab=0.0008;
    pspeedcost=0.08;
    pslack=8;
    ptv=0.01;
elseif strcmp(behaviour,'beginner')==1
    maxSpeed = 10; % in [m/s]
    maxxacc = 5; % in [m/s^-1]
    steeringreg = 0.2;  
    specificmoi = 0.3;
    plag=1;
    plat=0.3;
    pprog=0.4;
    pab=0.04;
    pspeedcost=0.0004;
    pslack=10;
    ptv=0.1;
elseif strcmp(behaviour,'drifting')==1
    maxSpeed = 10; % in [m/s]
    maxxacc = 5; % in [m/s^-1]
    steeringreg = 0.01;  
    specificmoi = 0.3;
    plag=0.2;
    plat=0.01;
    pprog=0.1;
    pab=0.0004;
    pspeedcost=0.04;
    pslack=4;
    ptv=0.05;
elseif strcmp(behaviour,'custom')==1
    maxSpeed = 10; % in [m/s]
    maxxacc = 5; % in [m/s^-1]
    steeringreg = 0.02;  
    specificmoi = 0.3;
    plag=1;
    plat=0.01;
    pprog=0.2;
    pab=0.0004;
    pspeedcost=0.04;
    pslack=5;
    ptv=0.01;
elseif strcmp(behaviour,'collision')==1
    maxSpeed = 3; % in [m/s]
    maxxacc = 5; % in [m/s^-1]
    steeringreg = 0.2;  
    specificmoi = 0.3;
    plag=1;
    plat=1;
    pprog=0.2;
    pab=0.04;
    pspeedcost=1;
    pslack=5;
    ptv=0.01;
else
    warning('Choose a valid parameter configuration!')
    return 
end
end


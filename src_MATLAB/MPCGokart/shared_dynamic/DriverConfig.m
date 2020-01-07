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
    steeringreg = 0.02;  
    specificmoi = 0.3;
    plag=1;
    plat=0.04;
    pprog=0.15;
    pab=0.0008;
    pspeedcost=0.07;
    pslack=8;
    ptv=0.01;
elseif strcmp(behaviour,'beginner')==1
    maxSpeed = 10; % in [m/s]
    maxxacc = 5; % in [m/s^-1]
    steeringreg = 0.02;  
    specificmoi = 0.3;
    plag=1;
    plat=0.2;
    pprog=0.1;
    pab=0.08;
    pspeedcost=0.07;
    pslack=10;
    ptv=0.01;
elseif strcmp(behaviour,'drifting')==1
    maxSpeed = 10; % in [m/s]
    maxxacc = 5; % in [m/s^-1]
    steeringreg = 0.02;  
    specificmoi = 0.3;
    plag=0.2;
    plat=0.01;
    pprog=0.1;
    pab=0.0004;
    pspeedcost=0.04;
    pslack=4;
    ptv=0.05;
elseif strcmp(behaviour,'custom')==1
    maxSpeed = 7; % in [m/s]
    maxxacc = 5; % in [m/s^-1]
    steeringreg = 0.02;  
    specificmoi = 0.3;
    plag=1;
    plat=0.0001;
    pprog=0.2;
    pab=0.04;
    pspeedcost=0.0004;
    pslack=5;
    ptv=0.03;
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


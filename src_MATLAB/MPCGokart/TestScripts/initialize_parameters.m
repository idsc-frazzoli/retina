%%%%%%%%
% initialization of parameters
% these are valid for C-type hatchback that can be found in 
% CarSim libraries

global params;

% mass [kg]
params.m = 1412;

% yawing moment of inertia [kgm2]
params.Iz = 1536.7 + 427.7084; % sprung mass inertia + unsprung mass inertia

% wheel moment of inertia [kgm2]
%params.Iw = 0.9;
params.Iw = 100;

% dimensions for the car

% distance from COG to front end [m]
params.frontL = 1.915;

% distance from COG to rear end [m]
params.rearL = 2.835;

% width of the vehicle [m]
params.width = 1.916;

% front axle distance from COG [m]
params.lF = 1.015;

% rear axle distanc from COG [m]
params.lR = 1.895;

% lateral distance of wheels from COG [m]
params.lw = 1.675/2;

% height of COG [m]
%params.h = 0.54;
params.h = 0;

% wheel radius [m]
params.R = 0.325;

% pacejka model parameters
% 1 - for frint tires, 2 - rear tires
params.B1 = 13.8509; params.C1 = 1.367; params.D1 = 0.9622;
params.B2 = 14.1663; params.C2 = 1.3652; params.D2 = 0.9744;
% params.B = (params.B1 + params.B2)/2;
% params.C = (params.C1 + params.C2)/2;
% params.D = (params.D1 + params.D2)/2;
% params.B1 = 7; params.C1 = 1.4; params.D1 = 1;
% params.B2 = 7; params.C2 = 1.4; params.D2 = 1;

% maximal steering angle [deg]
params.maxDelta = 30;

% Nm per Mpa conversion constant [Nm/Mpa] for Front and Rear brakes
params.press2torF = 250;
params.press2torR = 150;

% max handbrake torque [Nm]
params.maxThb = 2000;

% maximal master cylinder presure [MPa]
params.maxPress = 13;

% maximal motor torque [Nm], with gears included
params.maxTm = 1000;

%rear/total drive ratio; 0 is FWD, 1 is RWD
params.gammaM = 0;

% gravitational acceleration [m/s2]
params.g = 9.81;

% tire road friction coefficient
params.mu = 0.53;

%rolling friction coefficient
params.muRoll = 0;

% dynamic friction coefficient N/(m/s)
params.b = 5;
params.fric = 47;   %coulomb friction

% tolerance below which is speed considered 0
eps = 1e-4;

% dead zone tOLERANCE
params.Dz1 = 0.05;
params.Dz2 = 1*pi/180;

% sampling time for MPC  [s]
params.T = 0.1;


params.maxDeltaRate = 50 * pi/180; % rad/s TODO confirm with @jelavice
params.maxBrakeRate = 5; % 1/s
params.maxHandbrakeRate = 5; % 1/s
params.maxThrottleRate = 5; % 1/s

params.maxThrottle = 1;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%inital states 

speed = 0.5;

% longitudinal and lateral speed in car coordiante frame [m/s]
params.Ux0 = speed/3.6;
params.Uy0 = 0;

% wheel speeds [rad/s], rear and front
params.w1L0 = speed/3.6/params.R;
params.w1R0 = speed/3.6/params.R;
params.w2L0 = speed/3.6/params.R;
params.w2R0 = speed/3.6/params.R;
params.WF0 = speed/3.6/params.R;
params.WR0 = speed/3.6/params.R;
% yawing rate [rad/s2]
params.r0 = 0;

% coordiantes of the car [m]
params.x0 = 0;
params.y0 = 0;

%heading of the car [deg]
params.ksi0 = 0;






















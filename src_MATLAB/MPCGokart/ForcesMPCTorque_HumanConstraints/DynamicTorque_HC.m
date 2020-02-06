%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Dynamic MPC Script
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% code by mh,em,ta
% annotation mcp,ta

%add force path (change that for yourself)
addpath('..');
userDir = getuserdir;
addpath([userDir '/Forces']); % Location of FORCES PRO
%addpath('C:\Users\me\Documents\FORCES_client');
addpath('casadi');
addpath('../shared_dynamic')

clear model
clear problem
clear all
%close all

%% Baseline params

maxSpeed = 10; % in [m/s]
maxxacc = 5; % in [m/s^-1]

% Costs for simulation, change the real values in Java
steeringreg = 0.01;
specificmoi = 0.3;
plag = 1;
plat = 0.01;
pprog = 0.2;
pab = 0.0004;
pspeedcost = 0.02;
pslack = 5;
ptv = 0.01;
ptau = 0.0001;

% Simulation Pacejka constants, real values changalbe in java
FB = 9;
FC = 1;
FD = 7.2;
RB = 5.2;
RC = 1.1;
RD = 7;

% Steering Column
J_steer = 3.3;
b_steer = 0.24;%2.4895;
k_steer = 0.9595;%1.3092;;

% Control Points
pointsO = 21; % number of Parameters
pointsN = 15; % Number of points for B-splines (10 in 3 coordinates)

% Spline
splinestart = 1;
nextsplinepoints = 0;

% Runs
tend = 250;

% Integrator step
eulersteps = 10;
solvetimes = [];
integrator_stepsize = 0.1;

%% global parameters index
global index

index.dotab = 1;
index.dottau = 2;
index.ds = 3;
index.tv = 4;
index.slack = 5;
index.x = 6;
index.y = 7;
index.theta = 8;
index.dottheta = 9;
index.v = 10;
index.yv = 11;
index.ab = 12;
index.beta = 13;
index.s = 14;
index.dotbeta = 15;
index.tau = 16;

%Variable sizes
index.ns = 11; %number of state vars
index.nu = 5; %number of control vars
index.nv = index.ns+index.nu;   % = 16
index.sb = index.nu+1;          % = 6

%Gokart Parameters
index.ps = 1;
index.pax = 2;
index.pbeta = 3;
index.pmoi = 4;

% Cost function parameters
index.pacFB = 5;
index.pacFC = 6;
index.pacFD = 7;
index.pacRB = 8;
index.pacRC = 9;
index.pacRD = 10;
index.steerStiff = 11;
index.steerDamp = 12;
index.steerInertia = 13;
index.plag = 14;
index.plat = 15;
index.pprog = 16;
index.pab = 17;
index.pspeedcost = 18;
index.pslack = 19;
index.ptv = 20;
index.ptau = 21;

index.pointsO = 21; % number of Parameters
index.pointsN = 15;% number of Spline points to use

%% model definition
model.N = 31;                       % Forward horizon

model.nvar = index.nv;              % = 16
model.neq = index.ns;               % = 11

model.eq = @(z,p) RK4( ...
    z(index.sb:end), ...
    z(1:index.nu), ...
    @(x,u,p)interstagedx_THC(x,u,p), ... % PACEJKA PARAMETERS
    integrator_stepsize,...
    p);

model.E = [zeros(index.ns,index.nu), eye(index.ns)];

%% inequality constraints

model.nh = 5;% Number of inequality constraints
model.ineq = @(z,p) nlconst_THC(z,p);
model.hu = [0;0;1;0;0];
model.hl = [-inf;-inf;-inf;-inf;-inf];

%% Control points for trajectory sampling

points = [25,35,45,49,46,37,27,28,35,45,48,45,36,28,22,21,20;...          %x
          34,35,34,38,42,40,42,48,49,46,52,54,52,53,54,47,40; ...    %y
          1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5]';

% points = [36.2,52,57.2,53,52,47,41.8;...          %x
%     44.933,58.2,53.8,49,44,43,38.33; ...           %y
%     1.8,1.8,1.8,0.5,0.5,0.5,1.8]';                      %phi

% points = [28,35,42,55.2,56,51,42,40;...          %x
%           41,60,43,56,43,40,44,31; ...    %y
%           2,1.5,1.2,1.6,0.6,0.8,1.2,1.6]';   %phi
points(:,3)=points(:,3)-0.2;
%% Number of parameters
model.npar = index.pointsO + 3*index.pointsN;

%Model Cost function
for i=1:model.N
    model.objective{i} = @(z,p)objectiveTHC(...
        z,...
        getPointsFromParameters(p, index.pointsO, index.pointsN),...
        getRadiiFromParameters(p, index.pointsO, index.pointsN),...
        p(index.ps),...
        p(index.pax),...
        p(index.pbeta),...
        p(index.plag),...
        p(index.plat),...
        p(index.pprog),...
        p(index.pab),...
        p(index.pspeedcost),...
        p(index.pslack),...
        p(index.ptv),...
        p(index.ptau));
end

model.xinitidx = index.sb:index.nv;

%% Upper & Lower bounds
model.ub = ones(1,index.nv)*inf;
model.lb = -ones(1,index.nv)*inf;

% delta path progress
model.ub(index.ds)=5;
model.lb(index.ds)=-1;

% Forward force lower bound
model.lb(index.ab)=-inf;

% Torque vectoring
model.ub(index.tv)=1.2;
model.lb(index.tv)=-1.2;

% Slack variable
model.lb(index.slack)=0;%Size of buffer zone around walls in meters

% Speed lower bound
model.lb(index.v)=0;

% Steering Angle Bounds
model.ub(index.beta)=0.5;
model.lb(index.beta)=-0.5;

% Path Progress bounds
model.ub(index.s)=index.pointsN-2;
model.lb(index.s)=0;

% Torque Bounds
model.ub(index.tau)=2;
model.lb(index.tau)=-2;

% Variation Torque
model.ub(index.dottau)=15;
model.lb(index.dottau)=-15;

%% CodeOptions for FORCES solver
codeoptions = getOptions('MPCPathFollowing'); % Need FORCES License to run
codeoptions.maxit = 200;    % maximum number of iterations
codeoptions.printlevel = 0; % use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output); % need FORCES license to run


%% Initialization
planintervall = 1;
fpoints = points(1:2,1:2);
pdir = diff(fpoints);
[pstartx,pstarty] = casadiDynamicBSPLINE(0.01,points);
pstart = [pstartx,pstarty];
pangle = atan2(pdir(2),pdir(1));
xs(index.x-index.nu)=pstart(1);
xs(index.y-index.nu)=pstart(2);
xs(index.theta-index.nu)=pangle;
xs(index.v-index.nu)=5;
xs(index.ab-index.nu)=0;
xs(index.beta-index.nu)=0;
xs(index.tau-index.nu)=0;
xs(index.s-index.nu)=0.01;

history = zeros(tend*eulersteps,model.nvar+1);
splinepointhist = zeros(tend,index.pointsN*3+1);
plansx = [];
plansy = [];
planss = [];
targets = [];
planc = 10;
x0 = [zeros(model.N,index.nu),repmat(xs,model.N,1)]';

tstart = 1;

for i = 1:tend
    tstart = i;
    %find bspline
    if(1)
        if xs(index.s-index.nu) > 1
            nextSplinePoints;
            %spline step forward
            splinestart = splinestart+1;
            xs(index.s-index.nu)=xs(index.s-index.nu)-1;
        end
    end
    xs(index.ab-index.nu)=min(casadiGetMaxAcc(xs(index.v-index.nu))-0.0001,xs(index.ab-index.nu));
    problem.xinit = xs';
    ip = splinestart;
    [nkp, ~] = size(points);
    nextSplinePoints = zeros(index.pointsN,3);
    for jj=1:index.pointsN
        while ip>nkp
            ip = ip - nkp;
        end
        nextSplinePoints(jj,:) = points(ip,:);
        ip = ip + 1;
    end
    splinepointhist(i,:) = [xs(index.s-index.nu), nextSplinePoints(:)'];

    problem.all_parameters = repmat (getParametersTHC(maxSpeed,maxxacc,...
        steeringreg,specificmoi,FB,FC,FD,RB,RC,RD,b_steer,k_steer,J_steer,...
        plag,plat,pprog,pab,pspeedcost,...
        pslack,ptv,ptau,nextSplinePoints) , model.N ,1);

    problem.x0 = x0(:);

    % solve mpc
    [output,exitflag,info] = MPCPathFollowing(problem);
    solvetimes(end+1)=info.solvetime;
    if(exitflag==0)
        a = 1;
    end
    if(exitflag~=1 && exitflag ~=0)
        drawT
        return
    end

    %get output
    outputM = reshape(output.alldata,[model.nvar,model.N])';
    x0 = outputM';
    u = repmat(outputM(1,1:index.nu),eulersteps,1);
    [xhist,time] = euler(@(x,u)interstagedx_THC(x,u,problem.all_parameters),xs,u,integrator_stepsize/eulersteps);
    xs = xhist(end,:);
    xs;
    history((tstart-1)*eulersteps+1:(tstart)*eulersteps,:)=[time(1:end-1)+(tstart-1)*integrator_stepsize,u,xhist(1:end-1,:)];
    planc = planc + 1;
    if(planc>planintervall)
        planc = 1;
        plansx = [plansx; outputM(:,index.x)'];
        plansy = [plansy; outputM(:,index.y)'];
        planss = [planss; outputM(:,index.s)'];
        [tx,ty]=casadiDynamicBSPLINE(outputM(end,index.s),nextSplinePoints);
        targets = [targets;tx,ty];
    end
    Percentage_Complete = 100*i/tend
end

drawT

figure
hold on
title('Steering Torque')
axis([-inf inf -2 2])
ylabel('Torque [SCT]')
plot(lhistory(:,1),lhistory(:,index.tau+1));

figure
hold on
title('path progress')
yyaxis left
axis([-inf inf 0 3])
ylabel('progress rate [1/s]')
plot(lhistory(:,1),lhistory(:,index.ds+1));

yyaxis right
ylabel('progress [1]')
axis([-inf inf 0 2])
xlabel('[s]')
plot(lhistory(:,1), lhistory(:,index.s+1));

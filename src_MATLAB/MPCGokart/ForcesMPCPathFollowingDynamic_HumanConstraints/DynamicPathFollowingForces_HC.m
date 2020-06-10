%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Dynamic MPC Script with tunable parameters
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% code by em

%add force path (change that for yourself)
addpath('..');
userDir = getuserdir;
addpath([userDir '\Documents\Forces']); % Location of FORCES PRO
% addpath('C:\Users\me\Documents\FORCES_client');
addpath('casadi');
addpath('../shared_dynamic')

clear model
clear problem
clear all

%% Baseline params
behaviour='aggressive'; %aggressive,medium, beginner,drifting,custom,collision

[maxSpeed,maxxacc,steeringreg,specificmoi,plag,...
    plat,pprog,pab,pspeedcost,pslack,ptv] = DriverConfig(behaviour);
plat = 0.00001;
ptau = 0.01;
FB = 9;
FC = 1;
FD = 10; % gravity acceleration considered
RB = 5.2;
RC = 1.1;
RD = 10;

% Steering Column (not used now)
J_steer = 0.8875;
b_steer = 0.1625;
k_steer = 0.0125;

% Control Points
pointsO = 21; % number of Parameters
pointsN = 15; % Number of points for B-splines (10 in 3 coordinates)
splinestart = 1;
nextsplinepoints = 0;

%% TEND

% Runs
tend = 200;

% Integrator step
eulersteps = 10;
solvetimes = [];
integrator_stepsize = 0.1;

%% global parameters index
global index

index.dotab = 1;
index.dotbeta = 2;
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
index.ns = 9;
index.nu = 5;
index.nv = index.ns+index.nu;   % = 14
index.sb = index.nu+1;          % = 6
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
index.steerStiff=11;
index.steerDamp=12;
index.steerInertia=13;
index.plag = 14;
index.plat = 15;
index.pprog = 16;
index.pab = 17;
index.pspeedcost = 18;
index.pslack = 19;
index.ptv = 20;
index.ptau = 21;

index.pointsO = pointsO; % number of Parameters
index.pointsN = pointsN; % number of Spline points to use

%% model definition
model.N = 31;                       % Forward horizon

model.nvar = index.nv;              % = 14 number of states + inputs
model.neq = index.ns;               % = 9  number of states

model.eq = @(z,p) RK4( ...
    z(index.sb:end), ...
    z(1:index.nu), ...
    @(x,u,p)interstagedx_HC(x,u,p), ... %PACEJKA PARAMETERS
    integrator_stepsize,...
    p);

model.E = [zeros(index.ns,index.nu), eye(index.ns)];

%% inequality constraints

model.nh = 5; % Number of inequality constraints
model.ineq = @(z,p) nlconst_HC(z,p);
model.hu = [0;0;1;0;0];
model.hl = [-inf;-inf;-inf;-inf;-inf];

%% Control points for trajectory sampling

points = [25,35,45,49,46,37,27,28,35,45,48,45,36,28,22,21,20; ...          %x
          34,35,34,38,42,40,42,48,49,46,52,54,52,53,54,47,40; ...    %y
          1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5]';

%points = [10,10,20,10,20,20,40,60,80,90,90,90,80,50,20; ...          %x
%            10,35,45,55,75,90,90,90,80,60,42,15,10,5,5; ...    %y
%            4,3,5,4,3,4,4,6,4,3,2,3,4,4,6]';
% points = [18,22,35,42,55.2,60,51,42,40,30,22;...          %x
%           41,52,55,57,56,43,40,42,31,35,34; ...    %y
%           2.5,2.5,2.5,2.5,2.5,2.3,2.3,2.3,2.3,2.5,2.5]';
points(:,3) = points(:,3) - 0.1;

% trajectorytimestep = integrator_stepsize;
% [p,steps,speed,ttpos] = getTrajectory(points,2,1,trajectorytimestep);

%% Number of parameters
model.npar = pointsO + 3*pointsN;

%% Objective function definition
for i=1:model.N
   model.objective{i} = @(z,p)objectiveHC(...
       z,...
       getPointsFromParameters(p, pointsO, pointsN),...
       getRadiiFromParameters(p, pointsO, pointsN),...
       p(index.ps),...
       p(index.pax),...
       p(index.pbeta),...
       p(index.plag),...
       p(index.plat),...
       p(index.pprog),...
       p(index.pab),...
       p(index.pspeedcost),...
       p(index.pslack),...
       p(index.ptv));
end

model.xinitidx = index.sb:index.nv;

%% Equality constraints
model.ub = ones(1,index.nv)*inf;
model.lb = -ones(1,index.nv)*inf;

% delta path progress
model.ub(index.ds)=5;
model.lb(index.ds)=-1;

% Forward force lower bound
model.lb(index.ab)=-inf;

% Torque vectoring
model.ub(index.tv)=1.7;
model.lb(index.tv)=-1.7;

model.lb(index.slack)=0;

% Speed lower bound
model.lb(index.v)=0;

% Steering Angle Bounds
model.ub(index.beta)=0.5;
model.lb(index.beta)=-0.5;

% Path Progress Bounds
model.ub(index.s)=pointsN-2;
model.lb(index.s)=0;


%% CodeOptions for FORCES solver
codeoptions = getOptions('MPCPathFollowing'); % Need FORCES License to run
codeoptions.maxit = 200;    % Maximum number of iterations
codeoptions.printlevel = 1; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
%codeoptions.platform = 'x86_64';
codeoptions.cleanup = false;
codeoptions.timing = 1;
%codeoptions.solvemethod = 'SQP_NLP';
output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output); % Need FORCES License to run

%% Initialization

planintervall = 1;
fpoints = points(1:2,1:2);
pdir = diff(fpoints);

% Initial position and orientation of the gokart
[pstartx,pstarty] = casadiDynamicBSPLINE(0.01,points);
pstart = [pstartx,pstarty];
pangle = atan2(pdir(2),pdir(1));

xs(index.x-index.nu)=pstart(1);
xs(index.y-index.nu)=pstart(2);
xs(index.theta-index.nu)=pangle;

% Initial speed
xs(index.v-index.nu)=5;
% Initial acceleration
xs(index.ab-index.nu)=0;
% Initial Angle
xs(index.beta-index.nu)=0;
% Initial Path Progress
xs(index.s-index.nu)=0.01;

history = zeros(tend*eulersteps,model.nvar+1);
splinepointhist = zeros(tend,pointsN*3+1);
plansx = [];
plansy = [];
planss = [];
targets = [];
planc = 10;
x0 = [zeros(model.N,index.nu),repmat(xs,model.N,1)]';

tstart = 1;
%% Start Simulation
a = 0;
for i = 1:tend
    tstart = i;

    %find bspline
    if(1)
        if xs(index.s-index.nu)>1
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
    nextSplinePoints = zeros(pointsN,3);
    for jj = 1:pointsN
       while ip > nkp
            ip = ip -nkp;
       end
       nextSplinePoints(jj,:)=points(ip,:);
       ip = ip + 1;
    end
    splinepointhist(i,:)=[xs(index.s-index.nu),nextSplinePoints(:)'];

    % Parameters
    problem.all_parameters = repmat(getParametersTHC(maxSpeed,maxxacc,...
        steeringreg,specificmoi,FB,FC,FD,RB,RC,RD,b_steer,k_steer,J_steer,...
        plag,plat,pprog,pab,pspeedcost,...
        pslack,ptv,ptau,nextSplinePoints) , model.N ,1);

    % Initial state
    problem.x0 = x0(:);

    % Solve mpc
    [output,exitflag,info] = MPCPathFollowing(problem);
    solvetimes(end+1)=info.solvetime;
    if(exitflag==0)
        a = a + 1;
    end
    if(exitflag~=1 && exitflag ~=0)
        draw
        return
    end

    %get output
    outputM = reshape(output.alldata,[model.nvar,model.N])';
    x0 = outputM';
    u = repmat(outputM(1,1:index.nu),eulersteps,1);

    % update
    [xhist,time] = euler(@(x,u)interstagedx_HC(x,u,problem.all_parameters),xs,u,integrator_stepsize/eulersteps);
    xs = xhist(end,:);
    history((tstart-1)*eulersteps+1:(tstart)*eulersteps,:) = [time(1:end-1)+(tstart-1)*integrator_stepsize,u,xhist(1:end-1,:)];
    planc = planc + 1;
    if(planc > planintervall)
        planc = 1;
        plansx = [plansx; outputM(:,index.x)'];
        plansy = [plansy; outputM(:,index.y)'];
        planss = [planss; outputM(:,index.s)'];
        [tx,ty]=casadiDynamicBSPLINE(outputM(end,index.s),nextSplinePoints);
        targets = [targets;tx,ty];
    end
    Percentage_Complete = 100*i/tend
end
draw

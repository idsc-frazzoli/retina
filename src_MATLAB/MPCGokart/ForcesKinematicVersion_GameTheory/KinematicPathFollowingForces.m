%add force path (change that for yourself)
addpath('..');
userDir = getuserdir;
addpath('casadi');
    
clear model
clear problem
clear all
close all

%% Parameters Definitions
maxSpeed = 10;
maxxacc = 4;
maxyacc = 8;
latacclim = 6;
rotacceffect  = 2;
torqueveceffect = 3;
brakeeffect = 0; 
plagerror=1;
platerror=0.01;
pprog=0.2;
pab=0.0004;
pdotbeta=0.1;
pspeedcost=0.2;
pslack=5;

% Splines
pointsO = 14;
pointsN = 10;
splinestart = 1;
nextsplinepoints = 0;

% Stepsize
integrator_stepsize = 0.1;

% Simulation length
tend = 1000;
eulersteps = 10;
planintervall = 1;

%% Spline Points
    
points = [36.2,52,57.2,53,52,47,41.8;...
          44.933,58.2,53.8,49,44,43,38.33;...
          1.8,1.8,1.8,0.5,0.5,0.5,1.8]';
points(:,3)=points(:,3)-0.2;

trajectorytimestep = integrator_stepsize;
solvetimes = [];

%% State and Input Definitions 
global index

% Inputs
index.dotab = 1;
index.dotbeta = 2;
index.ds = 3;
index.slack = 4;

% States
index.x = 5;
index.y = 6;
index.theta = 7;
index.v = 8;
index.ab = 9;
index.beta = 10;
index.s = 11;

% Number of States
index.ns = 7;

% Number of Inputs
index.nu = 4;

% Number of Variables
index.nv = index.ns+index.nu;
index.sb = index.nu+1;

% Parameters
index.ps = 1;
index.pax = 2;
index.pay = 3;
index.pll = 4;
index.prae = 5;
index.ptve = 6;
index.pbre = 7;

%% ADDED
index.pointsO=pointsO;
index.pointsN=pointsN;
index.plag = 8;
index.plat = 9;
index.pprog = 10;
index.pab = 11;
index.pdotbeta = 12;
index.pspeedcost = 13;
index.pslack = 14;
%% Model Definition

model.N = 31;
model.nvar = index.nv;
model.neq = index.ns;

model.eq = @(z,p) RK4( z(index.sb:end), z(1:index.nu),...
             @(x,u,p)interstagedx(x,u), integrator_stepsize,p);
model.E = [zeros(index.ns,index.nu), eye(index.ns)];

%% Non-Linear Constraints

%limit lateral acceleration
model.nh = 6; 
model.ineq = @(z,p) nlconst(z,p);
model.hu = [0;1;0;0;0;0];
model.hl = [-inf;-inf;-inf;-inf;-inf;-inf];

%% Objective Function
model.npar = pointsO + 3*pointsN;
for i=1:model.N
    model.objective{i} = @(z,p)objective(z,...
    getPointsFromParameters(p, pointsO, pointsN),...
    getRadiiFromParameters(p, pointsO, pointsN),...
    p(index.ps),...
    p(index.pax),...
    p(index.pay),...
    p(index.pll),...
    p(index.prae),...
    p(index.ptve),...
    p(index.pbre),...
    p(index.plag),...
    p(index.plat),...
    p(index.pprog),...
    p(index.pab),...
    p(index.pdotbeta),...
    p(index.pspeedcost),...
    p(index.pslack));
end

%% Linear Constraints
model.xinitidx = index.sb:index.nv;

% initialization
model.ub = ones(1,index.nv)*inf;
model.lb = -ones(1,index.nv)*inf;

% Delta path progress
model.ub(index.ds)=5;
model.lb(index.ds)=-1;

% Acceleration
model.lb(index.ab)=-4.5;
model.lb(index.ab)=-inf;

% Slack
model.lb(index.slack)=0;

% Velocity
model.lb(index.v)=0;

% Steering Angle
model.ub(index.beta)=0.5;
model.lb(index.beta)=-0.5;

%Path Progress
model.ub(index.s)=pointsN-2;
model.lb(index.s)=0;

%% Solver
codeoptions = getOptions('MPCPathFollowing');
codeoptions.maxit = 200;    % Maximum number of iterations
codeoptions.printlevel = 1; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output);

%% Initialization for simulation
fpoints = points(1:2,1:2);
pdir = diff(fpoints);
[pstartx,pstarty] = casadiDynamicBSPLINE(0.01,points);
pstart = [pstartx,pstarty];
pangle = atan2(pdir(2),pdir(1));
xs(index.x-index.nu)=pstart(1);
xs(index.y-index.nu)=pstart(2);
xs(index.theta-index.nu)=pangle;
xs(index.v-index.nu)=1;
xs(index.ab-index.nu)=0;
xs(index.beta-index.nu)=0;
xs(index.s-index.nu)=0.01;

%% Simulation
history = zeros(tend*eulersteps,model.nvar+1);
splinepointhist = zeros(tend,pointsN*3+1);
plansx = [];
plansy = [];
planss = [];
targets = [];
planc = 10;
x0 = [zeros(model.N,index.nu),repmat(xs,model.N,1)]';
tstart = 1;
for i =1:tend
    tstart = i;
    %find bspline
    if(1)
        if xs(index.s-index.nu)>1
            %nextSplinePoints
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
    for ii=1:pointsN
       while ip>nkp
            ip = ip -nkp;
       end
       nextSplinePoints(ii,:)=points(ip,:);
       ip = ip + 1;
    end
    splinepointhist(i,:)=[xs(index.s-index.nu),nextSplinePoints(:)'];
    
    % parameters
    problem.all_parameters = repmat(getParameters(maxSpeed,maxxacc,...
        maxyacc,latacclim,rotacceffect,torqueveceffect,brakeeffect,...
        plagerror,platerror,pprog,pab,pdotbeta,...
        pspeedcost,pslack,nextSplinePoints), model.N ,1);
    problem.x0 = x0(:);
       
    % solve mpc
    [output,exitflag,info] = MPCPathFollowing(problem);
    solvetimes(end+1)=info.solvetime;
    if(exitflag==0)
       a = 1; 
    end
    if(exitflag~=1 && exitflag ~=0)
        draw
       return 
    end
    %nextSplinePoints
    %get output
    outputM = reshape(output.alldata,[model.nvar,model.N])';
    x0 = outputM';
    u = repmat(outputM(1,1:index.nu),eulersteps,1);
    [xhist,time] = euler(@(x,u)interstagedx(x,u),xs,u,integrator_stepsize/eulersteps);
    xs = xhist(end,:);
    % xs
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
end

draw
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
close all

%% Baseline params

maxSpeed = 10; % in [m/s]
maxxacc = 5; % in [m/s^-1]

%Costs for simulation, change the real values in Java 
steeringreg = 0.1;
specificmoi = 0.3;
plag=1;
plat=0.01;
pprog=0.2;
pab=0.0004;
pspeedcost=0.04;
pslack=5;
ptv=0.01;
ptau=0.05;

%Simulation Pacejka constants, real values changalbe in java 
FB = 9;
FC = 1;
FD = 7;
RB = 5.2;
RC = 1.1;
RD = 7;

%Steering column properties
J_steer=0.8875;
b_steer=0.1625;
k_steer=0.0125;


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

index.pointsO = 21; % number of Parameters
index.pointsN = 10;% number of Spline points to use
splinestart = 1;
nextsplinepoints = 0;


solvetimes = [];
integrator_stepsize = 0.1;

%% model params
model.N = 31;                       % Forward horizon
model.nvar = index.nv;              % = 16
model.neq = index.ns;               % = 11
model.eq = @(z,p) RK4( ...
    z(index.sb:end), ...
    z(1:index.nu), ...
    @(x,u,p)interstagedx_THC(x,u,p), ... %PACEJKA PARAMETERS
    integrator_stepsize,...
    p);
model.E = [zeros(index.ns,index.nu), eye(index.ns)];

l = 1;

%limit lateral acceleration
model.nh = 5;
model.ineq = @(z,p) nlconst_THC(z,p);
%model.hu = [36,0];
%model.hl = [-inf,-inf];
model.hu = [0;0;1;0;0];
model.hl = [-inf;-inf;-inf;-inf;-inf];


% Random control points for trajectory sampling
points = [36.2,52,57.2,53,52,47,41.8;...          %x
    44.933,58.2,53.8,49,44,43,38.33; ...           %y
    1.8,1.8,1.8,0.5,0.5,0.5,1.8]';                      %phi

% points = [28,35,42,55.2,56,51,42,40;...          %x
%           41,60,43,56,43,40,44,31; ...    %y
%           2,1.5,1.2,1.6,0.6,0.8,1.2,1.6]';   %phi
% %points = getPoints('/wildpoints.csv');
points(:,3)=points(:,3)-0.2;
%points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
%points = [0,40,40,5,0;0,0,10,9,10]';

trajectorytimestep = integrator_stepsize;
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

model.ub(index.ds)=5;
model.lb(index.ds)=-1;
model.lb(index.ab)=-inf;
model.ub(index.tv)=1.7;
model.lb(index.tv)=-1.7;
model.lb(index.slack)=0;
model.lb(index.v)=0;
model.ub(index.beta)=0.5;
model.lb(index.beta)=-0.5;
model.ub(index.s)=index.pointsN-2;
model.lb(index.s)=0;
model.ub(index.tau)=0.5;
model.lb(index.tau)=-0.5;
model.ub(index.dottau)=0.5;
model.lb(index.dottau)=-0.5;



%% CodeOptions for FORCES solver
codeoptions = getOptions('MPCPathFollowing'); % Need FORCES License to run
codeoptions.maxit = 200;    % Maximum number of iterations
codeoptions.printlevel = 0; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output); % Need FORCES License to run

%% CodeOptions for FORCES solver
% codeoptions_stop = getOptions('MPCPathFollowing_stop'); % Need FORCES License to run
% codeoptions_stop.maxit = 200;    % Maximum number of iterations
% codeoptions_stop.printlevel = 0; % Use printlevel = 2 to print progress (but not for timings)
% codeoptions_stop.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
% codeoptions_stop.cleanup = 0;
% codeoptions_stop.timing = 1;
% model_stop=model;
% for i=1:model_stop.N
%    model_stop.objective{i} = @(z,p)objective2(...
%        z,...
%        getPointsFromParameters(p, pointsO, pointsN),...
%        getRadiiFromParameters(p, pointsO, pointsN),...
%        p(index.ps),...
%        p(index.pax),...
%        p(index.pbeta));
% end
% output_stop = newOutput('alldata', 1:model.N, 1:model.nvar);
%
% FORCES_NLP(model_stop, codeoptions_stop,output_stop); % Need FORCES License to run

tend = 70;
eulersteps = 10;
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

for i =1:tend
    tstart = i;    
    %find bspline
    if(1)
        if xs(index.s-index.nu)>1
            nextSplinePoints;
            %spline step forward
            splinestart = splinestart+1;
            xs(index.s-index.nu)=xs(index.s-index.nu)-1;
            %if(splinestart>pointsN)
            %splinestart = splinestart-pointsN;
            %end
        end
    end
    
    xs(index.ab-index.nu)=min(casadiGetMaxAcc(xs(index.v-index.nu))-0.0001,xs(index.ab-index.nu));
    problem.xinit = xs';
    %do it every time because we don't care about the performance of this
    %script
    ip = splinestart;
    [nkp, ~] = size(points);
    nextSplinePoints = zeros(index.pointsN,3);
    for jj=1:index.pointsN
        while ip>nkp
            ip = ip -nkp;
        end
        nextSplinePoints(jj,:)=points(ip,:);
        ip = ip + 1;
    end
    splinepointhist(i,:)=[xs(index.s-index.nu),nextSplinePoints(:)'];
    
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
        draw
        return
    end
    %nextSplinePoints
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


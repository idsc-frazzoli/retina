%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Game Theory Potential Game
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% code by em
% 2 vehicle running in the same track in opponent direction, without any 
% constraints on collisions

%add force path (change that for yourself)
%addpath([userDir '/Forces']); % Location of FORCES PRO
addpath('..');
userDir = getuserdir;

addpath('casadi');
addpath('../shared_dynamic')
    
clear model
clear problem
clear all
%close all

%% Baseline parameters
behaviour='aggressive'; %aggressive,medium, beginner,drifting,custom,collision
[maxSpeed,maxxacc,steeringreg,specificmoi,plag,...
    plat,pprog,pab,pspeedcost,pslack,ptv] = DriverConfig(behaviour);
FB = 9;
FC = 1;
FD = 10; % gravity acceleration considered
RB = 5.2;
RC = 1.1;
RD = 10;
J_steer=0.8875;
b_steer=0.1625;
k_steer=0.0125;
dist=1.5;
pslack2=1;
pointsO = 22; % number of Parameters
pointsN = 10; % Number of points for B-splines (10 in 3 coordinates)
pointsN2= 10; % Number of points for B-splines for kart 2
splinestart = 1;
splinestart2 = 1;
nextsplinepoints = 0;
nextsplinepoints2 = 0;

% Simulation length
tend=100;
%% global parameters index
global index
% inputs go kart 1
index.dotab = 1;
index.dotbeta = 2;
index.ds = 3;
index.tv = 4;
index.slack = 5;
% inputs go kart 2
index.dotab_k2 = 6;
index.dotbeta_k2 = 7;
index.ds_k2 = 8;
index.tv_k2 = 9;
index.slack_k2 = 10;
% shared
index.slack2 = 11;
% states
index.x = 12;
index.y = 13;
index.theta = 14;
index.dottheta = 15;
index.v = 16;
index.yv = 17;
index.ab = 18;
index.beta = 19;
index.s = 20;
% states kart 2
index.x_k2 = 21;
index.y_k2 = 22;
index.theta_k2 = 23;
index.dottheta_k2 = 24;
index.v_k2 = 25;
index.yv_k2 = 26;
index.ab_k2 = 27;
index.beta_k2 = 28;
index.s_k2 = 29;
% states and input numbers
index.ns = 18;
index.nu = 11;
index.nv = index.ns+index.nu;   % = 29
index.sb = index.nu+1;          % = 12

% Cost function parameters
index.ps = 1;
index.pax = 2;
index.pbeta = 3;
index.pmoi = 4;
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
index.pslack2 = 20;
index.ptv = 21;
index.dist = 22;

index.pointsO=pointsO;
index.pointsN=pointsN;
index.pointsN2=pointsN2;

solvetimes = [];

integrator_stepsize = 0.1;

%% model definition
model.N = 31;                       % Forward horizon Length
model.nvar = index.nv;              % = 29
model.neq = index.ns;               % = 18
model.eq = @(z,p) RK4( ...
    z(index.sb:end), ...
    z(1:index.nu), ...
    @(x,u,p)interstagedx_PG(x,u,p), ... %PACEJKA PARAMETERS
    integrator_stepsize,...
    p);
model.E = [zeros(index.ns,index.nu), eye(index.ns)];

l = 1;

%% Inequalities constraints
model.nh = 11; 
model.ineq = @(z,p) nlconst_PG(z,p);
model.hu = [0;0;1;0;0;0;0;1;0;0;inf];%;
model.hl = [-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;0];

%% Control points definition for B-splines

% points = [36.2,52,57.2,53,52,47,41.8;...          %x
%           44.933,58.2,53.8,49,44,43,38.33; ...    %y
%           2,2,2,2,2,2,2]';          %phi
  
% points = [18,35,42,55.2,56,51,42,40;...          %x
%           41,55,57,56,43,40,45,31; ...    %y
%           2.5,2.5,2.5,2.5,2.5,2.5,2.3,2.5]';   %phi

points = [41.8,36.2,52,57.2,53,52,47;...          %x
          38.33,44.933,58.2,53.8,49,44,43; ...    %y
          2.5,2.5,2.5,2.5,2.5,2.5,2.5]';         %width 
points2 = [57.2,52,36.2,41.8,47,52,53;...          %x
          53.8,58.2,44.933,38.33,43,44,49; ...    %y
          2.5,2.5,2.5,2.5,2.5,2.5,2.5]';       

points(:,3)=points(:,3)-0.2;
points2(:,3)=points2(:,3)-0.2;
% points2=flip(points);
%points2=points;
%% Objective function
trajectorytimestep = integrator_stepsize;

%% Number of parameters required
model.npar = pointsO + 3*pointsN + 3*pointsN2;

%% Objective Function
for i=1:model.N
   model.objective{i} = @(z,p)objective_PG(...
       z,...
       getPointsFromParameters(p, pointsO, pointsN),...
       getRadiiFromParameters(p, pointsO, pointsN),...
       getPointsFromParameters(p, pointsO + 3*pointsN, pointsN2),...
       getRadiiFromParameters(p, pointsO + 3*pointsN, pointsN2),...
       p(index.ps),...
       p(index.pax),...
       p(index.pbeta),...
       p(index.plag),...
       p(index.plat),...
       p(index.pprog),...
       p(index.pab),...
       p(index.pspeedcost),...
       p(index.pslack),...
       p(index.pslack2),...
       p(index.ptv));
end

%% Equality Constraints
model.xinitidx = index.sb:index.nv;

% initialization
model.ub = ones(1,index.nv)*inf;
model.lb = -ones(1,index.nv)*inf;

% Path Progress rate Constraint (input)
model.ub(index.ds)=5;
model.lb(index.ds)=-1;

% Acceleration Constraint (input)
model.lb(index.ab)=-inf;

% Torque vectoring Constraint (input)
model.ub(index.tv)=1.7;
model.lb(index.tv)=-1.7;

% Slack Variables Constraint (input)
model.lb(index.slack)=0;

% Speed Constraint (state)
model.lb(index.v)=0;

% Steering Angle Constraint (input)
model.ub(index.beta)=0.5;
model.lb(index.beta)=-0.5;

% Path Progress Constraint (input)
model.ub(index.s)=pointsN-2;
model.lb(index.s)=0;

% Path Progress rate Constraint (input)
model.ub(index.ds_k2)=5;
model.lb(index.ds_k2)=-1;

% Acceleration Constraint (input)
model.lb(index.ab_k2)=-inf;

% Torque vectoring Constraint (input)
model.ub(index.tv_k2)=1.7;
model.lb(index.tv_k2)=-1.7;

% Slack Variables Constraint (input)
model.lb(index.slack_k2)=0;

% Speed Constraint (state)
model.lb(index.v_k2)=0;

% Steering Angle Constraint (input)
model.ub(index.beta_k2)=0.5;
model.lb(index.beta_k2)=-0.5;

% Path Progress Constraint (input)
model.ub(index.s_k2)=pointsN2-2;
model.lb(index.s_k2)=0;

model.lb(index.slack2)=0;

%% CodeOptions for FORCES solver
codeoptions = getOptions('MPCPathFollowing_One'); % Need FORCES License to run
codeoptions.maxit = 1000;    % Maximum number of iterations
codeoptions.printlevel = 1; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output); % Need FORCES License to run

%% Simulation
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
xs(index.s-index.nu)=0.01;
plansx= [];
plansy = [];
planss = [];
targets = [];
%Go-kart 2 initialization
fpoints2 = points2(1:2,1:2);
pdir2 = diff(fpoints2);
[pstartx2,pstarty2] = casadiDynamicBSPLINE(0.01,points2);
pstart2 = [pstartx2,pstarty2];
pangle2 = atan2(pdir2(2),pdir2(1));
xs(index.x_k2-index.nu)=pstart2(1);
xs(index.y_k2-index.nu)=pstart2(2);
xs(index.theta_k2-index.nu)=pangle2;
xs(index.v_k2-index.nu)=5;
xs(index.ab_k2-index.nu)=0;
xs(index.beta_k2-index.nu)=0;
xs(index.s_k2-index.nu)=0.01;
plansx2 = [];
plansy2 = [];
planss2 = [];
targets2 = [];
objval=[];
planc = 10;
a=0;
IND=[];
cost1 = zeros(tend,1);
cost2 = zeros(tend,1);
Progress1 = zeros(tend,1);
Progress2 = zeros(tend,1);
costS = zeros(tend,1);
x0 = [zeros(model.N,index.nu),repmat(xs,model.N,1)]';
history = zeros(tend*eulersteps,model.nvar+1);
splinepointhist = zeros(tend,pointsN*3+pointsN2*3+1);

for i =1:tend
    tstart = i;
    if(1)
        if xs(index.s-index.nu)>1
            nextSplinePoints;
            %spline step forward
            splinestart = splinestart+1;
            xs(index.s-index.nu)=xs(index.s-index.nu)-1;
        end
    end
    if(1)
        if xs(index.s_k2-index.nu)>1
            nextSplinePoints_k2;
            %spline step forward
            splinestart2 = splinestart2+1;
            xs(index.s_k2-index.nu)=xs(index.s_k2-index.nu)-1;
        end
    end
    % go kart 1
    xs(index.ab-index.nu)=min(casadiGetMaxAcc(xs(index.v-index.nu))-0.0001,xs(index.ab-index.nu));
    xs(index.ab_k2-index.nu)=min(casadiGetMaxAcc(xs(index.v_k2-index.nu))-0.0001,xs(index.ab_k2-index.nu));
    problem.xinit = xs';
    
    ip = splinestart;
    ip2 = splinestart2;
    [nkp, ~] = size(points2);
    [nkp2, ~] = size(points2);
    nextSplinePoints = zeros(pointsN,3);
    nextSplinePoints_k2 = zeros(pointsN2,3);
    for jj=1:pointsN
       while ip>nkp
            ip = ip -nkp;
       end
       nextSplinePoints(jj,:)=points(ip,:);
       ip = ip + 1;
    end
    for jj=1:pointsN2
       while ip2>nkp2
            ip2 = ip2 -nkp2;
       end
       nextSplinePoints_k2(jj,:)=points2(ip2,:);
       ip2 = ip2 + 1;
    end
    splinepointhist(i,:)=[xs(index.s-index.nu),[nextSplinePoints(:);nextSplinePoints_k2(:)]'];

    problem.all_parameters = repmat (getParametersPG(maxSpeed,maxxacc,...
        steeringreg,specificmoi,FB,FC,FD,RB,RC,RD,b_steer,k_steer,J_steer,...
        plag,plat,pprog,pab,pspeedcost,pslack,pslack2,...
        ptv,dist,nextSplinePoints,nextSplinePoints_k2) , model.N ,1);
    problem.x0 = x0(:);
    
    % solve mpc
    [output,exitflag,info] = MPCPathFollowing_One(problem);
    solvetimes(end+1)=info.solvetime;
    objval(end+1)=info.pobj;
    if(exitflag==0)
        a =a+ 1;
        IND=[IND;i];
    end
    if(exitflag~=1 && exitflag ~=0)
        keyboard        
    end
    
    %get output
    outputM = reshape(output.alldata,[model.nvar,model.N])';
    x0 = outputM';
    printCostFunction
    cost1(i)=costA;
    cost2(i)=costB;
    costS(i)=costS;
    Progress1(i)=outputM(1,index.s);
    Progress2(i)=outputM(1,index.s_k2);
    u = repmat(outputM(1,1:index.nu),eulersteps,1);

    [xhist,time] = euler(@(x,u)interstagedx_PG(x,u,problem.all_parameters),xs,u,integrator_stepsize/eulersteps);
    xs = xhist(end,:);
    xs
    history((tstart-1)*eulersteps+1:(tstart)*eulersteps,:)=[time(1:end-1)+(tstart-1)*integrator_stepsize,u,xhist(1:end-1,:)];
    planc = planc + 1;
    if(planc>planintervall)
        planc = 1;
        plansx = [plansx; outputM(:,index.x)'];
        plansy = [plansy; outputM(:,index.y)'];
        planss = [planss; outputM(:,index.s)'];
        plansx2 = [plansx2; outputM(:,index.x_k2)'];
        plansy2 = [plansy2; outputM(:,index.y_k2)'];
        planss2 = [planss2; outputM(:,index.s_k2)'];
        [tx,ty]=casadiDynamicBSPLINE(outputM(end,index.s),nextSplinePoints);
        targets = [targets;tx,ty];
        [tx2,ty2]=casadiDynamicBSPLINE(outputM(end,index.s_k2),nextSplinePoints_k2);
        targets2 = [targets2;tx2,ty2];
    end        
end

figure
hold on
plot(cost1,'b')
plot(cost2,'r')
plot(objval,'g')
legend('Kart1','Kart2','tot')
figure
hold on
plot(Progress1,'b')
plot(Progress2,'r')
legend('Kart1','Kart2')
% Plot
draw3
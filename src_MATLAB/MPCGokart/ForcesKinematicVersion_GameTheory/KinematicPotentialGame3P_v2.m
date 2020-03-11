%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Game Theory Potential Game
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% code by em
% 2 vehicle running in the same track in opponent direction, without any 
% constraints on collisions
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
pdotbeta=0.05;
pspeedcost=0.2;
pslack=5;
pslack2=10;
dist=3;

% Splines
pointsO = 16;
pointsN = 10;
pointsN2 = 10;
pointsN3 = 10;
splinestart = 1;
splinestart2 = 1;
splinestart3 = 1;
nextsplinepoints = 0;
nextsplinepoints_k2 = 0;
nextsplinepoints_k3 = 0;
% Simulation length
tend = 80;
eulersteps = 10;
planintervall = 1;

%% Spline Points
% points = [18,22,35,42,55.2,60,51,42,40,30,22;...          %x
%           41,52,55,57,56,43,40,42,31,35,34; ...    %y
%           3,3,3,3,3,3,3,3,3,3,3]';
% points = [41,36.2,41,45,51,57.2,54,52,47;...          %x
%           39,46,52,60,61,53.8,49,43,43; ...    %y
%           3,3,3,3,3,3,3,3,3]';  
% % points = [36.2,52,57.2,53,52,47,41.8;...
% %           44.933,58.2,53.8,49,44,43,38.33;...
% %           2.3,2.3,2.3,2.3,2.3,2.3,2.3]';
% %points(:,3)=points(:,3)-0.2;
% 
% points2=flip(points);
points = [10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95;...          %x
          50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50; ...    %y
          5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5]';  
points2 = [50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50;...          %x
          10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95; ...    %y
          5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5]';  
points3 = [50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,65,65,50;...          %x
           90,85,80,75,70,65,60,55,50,45,40,35,30,25,20,15,10,5,0,5,92,95; ...    %y
           4,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,4,3,2,2,3]';
% points3=  [50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50;...          %x
%           95,90,85,80,75,70,65,60,55,50,45,40,35,30,25,20,15,10; ...    %y
%           5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5]';
% points3 = [20,25,30,35,40,45,50,55,60,65,70,75,80,85,90;...          %x
%           20,25,30,35,40,45,50,55,60,65,70,75,80,85,90; ...    %y
%           5,5,5,5,5,5,5,5,5,5,5,5,5,5,5]';   
%points(:,3)=points(:,3)-0.2;

solvetimes = [];

%% State and Input Definitions 
global index

% inputs go kart 1
index.dotab = 1;
index.dotbeta = 2;
index.ds = 3;
index.slack = 4;

% inputs go kart 2
index.dotab_k2 = 5;
index.dotbeta_k2 = 6;
index.ds_k2 = 7;
index.slack_k2 = 8;

% inputs go kart 2
index.dotab_k3 = 9;
index.dotbeta_k3 = 10;
index.ds_k3 = 11;
index.slack_k3 = 12;

% shared
index.slack2 = 13;
index.slack3 = 14;
index.slack4 = 15;

% states
index.x = 16;
index.y = 17;
index.theta = 18;
index.v = 19;
index.ab = 20;
index.beta = 21;
index.s = 22;
% states kart 2
index.x_k2 = 23;
index.y_k2 = 24;
index.theta_k2 = 25;
index.v_k2 = 26;
index.ab_k2 = 27;
index.beta_k2 = 28;
index.s_k2 = 29;
% states kart 3
index.x_k3 = 30;
index.y_k3 = 31;
index.theta_k3 = 32;
index.v_k3 = 33;
index.ab_k3 = 34;
index.beta_k3 = 35;
index.s_k3 = 36;
% Number of States
index.ns = 21;

% Number of Inputs
index.nu = 15;

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

index.plag = 8;
index.plat = 9;
index.pprog = 10;
index.pab = 11;
index.pdotbeta = 12;
index.pspeedcost = 13;
index.pslack = 14;
index.pslack2 = 15;
index.dist = 16;

index.pointsO=pointsO;
index.pointsN=pointsN;
index.pointsN2=pointsN2;
index.pointsN3=pointsN3;
% Stepsize
integrator_stepsize = 0.1;
trajectorytimestep = integrator_stepsize;

%% model definition
model.N = 31;                       % Forward horizon Length
model.nvar = index.nv;
model.neq = index.ns;
model.eq = @(z,p) RK4(...
	z(index.sb:end), ...
	z(1:index.nu), ...
	@(x,u,p)interstagedx_PG3(x,u), ...
	integrator_stepsize,...
	p);
model.E = [zeros(index.ns,index.nu), eye(index.ns)];

%% Non-Linear Constraints

%limit lateral acceleration
model.nh = 21; 
model.ineq = @(z,p) nlconst_PG3(z,p);
model.hu = [0;1;0;0;0;0;0;1;0;0;0;0;0;1;0;0;0;0;0;0;0];%
model.hl = [-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf;-inf];%


%% Number of parameters required
model.npar = pointsO + 3*pointsN + 3*pointsN2 + 3*pointsN3;

%% Objective Function
for i=1:model.N
   model.objective{i} = @(z,p)objective_PG3_1(...
       z,...
       getPointsFromParameters(p, pointsO, pointsN),...
       getRadiiFromParameters(p, pointsO, pointsN),...
       getPointsFromParameters(p, pointsO + 3*pointsN, pointsN2),...
       getRadiiFromParameters(p, pointsO + 3*pointsN, pointsN2),...
       getPointsFromParameters(p, pointsO + 3*pointsN + 3*pointsN2, pointsN3),...
       getRadiiFromParameters(p, pointsO + 3*pointsN + 3*pointsN2, pointsN3),...
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
       p(index.pslack),...
       p(index.pslack2));
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

% Path Progress rate Constraint (input)
model.ub(index.ds_k3)=5;
model.lb(index.ds_k3)=-1;

% Acceleration Constraint (input)
model.lb(index.ab_k3)=-inf;

% Slack Variables Constraint (input)
model.lb(index.slack_k3)=0;

% Speed Constraint (state)
model.lb(index.v_k3)=0;

% Steering Angle Constraint (input)
model.ub(index.beta_k3)=0.5;
model.lb(index.beta_k3)=-0.5;

% Path Progress Constraint (input)
model.ub(index.s_k3)=pointsN3-2;
model.lb(index.s_k3)=0;

model.lb(index.slack2)=0;
model.lb(index.slack3)=0;
model.lb(index.slack4)=0;
%% CodeOptions for FORCES solver
codeoptions = getOptions('MPCPathFollowing');
codeoptions.maxit = 2000;    % Maximum number of iterations
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
plansx = [];
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
xs(index.v_k2-index.nu)=1;
xs(index.ab_k2-index.nu)=0;
xs(index.beta_k2-index.nu)=0;
xs(index.s_k2-index.nu)=0.01;
plansx2 = [];
plansy2 = [];
planss2 = [];
targets2 = [];

%Go-kart 3 initialization
fpoints3 = points3(1:2,1:2);
pdir3 = diff(fpoints3);
[pstartx3,pstarty3] = casadiDynamicBSPLINE(0.01,points3);
pstart3 = [pstartx3,pstarty3];
pangle3 = atan2(pdir3(2),pdir3(1));
xs(index.x_k3-index.nu)=pstart3(1);
xs(index.y_k3-index.nu)=pstart3(2);
xs(index.theta_k3-index.nu)=pangle3;
xs(index.v_k3-index.nu)=1;
xs(index.ab_k3-index.nu)=0;
xs(index.beta_k3-index.nu)=0;
xs(index.s_k3-index.nu)=0.01;
plansx3 = [];
plansy3 = [];
planss3 = [];
targets3 = [];

planc = 10;
tstart = 1;
x0 = [zeros(model.N,index.nu),repmat(xs,model.N,1)]';


%% Simulation
history = zeros(tend*eulersteps,model.nvar+1);
splinepointhist = zeros(tend,pointsN*3+pointsN2*3+pointsN3*3+1);
a=0;
for i =1:tend
    tstart = i;
    if(1)
        if xs(index.s-index.nu)>1
            %nextSplinePoints
            %spline step forward
            splinestart = splinestart+1;
            xs(index.s-index.nu)=xs(index.s-index.nu)-1;
        end
    end
    if(1)
        if xs(index.s_k2-index.nu)>1
            %nextSplinePoints_k2;
            %spline step forward
            splinestart2 = splinestart2+1;
            xs(index.s_k2-index.nu)=xs(index.s_k2-index.nu)-1;
        end
    end
        if(1)
        if xs(index.s_k3-index.nu)>1
            %nextSplinePoints_k2;
            %spline step forward
            splinestart3 = splinestart3+1;
            xs(index.s_k3-index.nu)=xs(index.s_k3-index.nu)-1;
        end
    end
    % go kart 1
    xs(index.ab-index.nu)=min(casadiGetMaxAcc(xs(index.v-index.nu))-0.0001,xs(index.ab-index.nu));
    % go kart 2
    xs(index.ab_k2-index.nu)=min(casadiGetMaxAcc(xs(index.v_k2-index.nu))-0.0001,xs(index.ab_k2-index.nu));
    % go kart 3
    xs(index.ab_k3-index.nu)=min(casadiGetMaxAcc(xs(index.v_k3-index.nu))-0.0001,xs(index.ab_k3-index.nu));
    problem.xinit = xs';
    
    ip = splinestart;
    ip2 = splinestart2;
    ip3 = splinestart3;
    [nkp, ~] = size(points);
    [nkp2, ~] = size(points2);
    [nkp3, ~] = size(points3);
    nextSplinePoints = zeros(pointsN,3);
    nextSplinePoints_k2 = zeros(pointsN2,3);
    nextSplinePoints_k3 = zeros(pointsN3,3);
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
    for jj=1:pointsN3
       while ip3>nkp3
            ip3 = ip3 -nkp3;
       end
       nextSplinePoints_k3(jj,:)=points3(ip3,:);
       ip3 = ip3 + 1;
    end
    splinepointhist(i,:)=[xs(index.s-index.nu),[nextSplinePoints(:);nextSplinePoints_k2(:);nextSplinePoints_k3(:)]'];
    
    % parameters
    problem.all_parameters = repmat(getParameters_PG3(maxSpeed,maxxacc,...
        maxyacc,latacclim,rotacceffect,torqueveceffect,brakeeffect,...
        plagerror,platerror,pprog,pab,pdotbeta,...
        pspeedcost,pslack,pslack2,dist,nextSplinePoints,nextSplinePoints_k2,nextSplinePoints_k3), model.N ,1);
    problem.x0 = x0(:);       
    % solve mpc
    [output,exitflag,info] = MPCPathFollowing(problem);
    solvetimes(end+1)=info.solvetime;
    if(exitflag==0)
       a = a + 1; 
    end
    if(exitflag~=1 && exitflag ~=0)
        draw3
        keyboard
    end
    %nextSplinePoints
    %get output
    outputM = reshape(output.alldata,[model.nvar,model.N])';
    x0 = outputM';
    u = repmat(outputM(1,1:index.nu),eulersteps,1);
    [xhist,time] = euler(@(x,u)interstagedx_PG3(x,u),xs,u,integrator_stepsize/eulersteps);
    xs = xhist(end,:);
    % xs
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
        plansx3 = [plansx3; outputM(:,index.x_k3)'];
        plansy3 = [plansy3; outputM(:,index.y_k3)'];
        planss3 = [planss3; outputM(:,index.s_k3)'];
        [tx,ty]=casadiDynamicBSPLINE(outputM(end,index.s),nextSplinePoints);
        targets = [targets;tx,ty];
        [tx2,ty2]=casadiDynamicBSPLINE(outputM(end,index.s_k2),nextSplinePoints_k2);
        targets2 = [targets2;tx2,ty2];
        [tx3,ty3]=casadiDynamicBSPLINE(outputM(end,index.s_k3),nextSplinePoints_k3);
        targets3 = [targets3;tx3,ty3];
    end        
    Percentage=i/tend*100
end

draw3
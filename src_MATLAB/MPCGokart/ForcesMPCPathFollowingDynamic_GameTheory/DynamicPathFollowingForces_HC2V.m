%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Game Theory MPC Script 1
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


%% Baseline params
behaviour='custom'; %aggressive,medium, beginner,drifting,custom,collision
[maxSpeed,maxxacc,steeringreg,specificmoi,plag,...
    plat,pprog,pab,pspeedcost,pslack,ptv] = DriverConfig(behaviour);
FB = 9;
FC = 1;
FD = 7; % gravity acceleration considered
RB = 5.2;
RC = 1.1;
RD = 7;
J_steer=0.8875;
b_steer=0.1625;
k_steer=0.0125;

pointsO = 20; % number of Parameters
pointsN = 10; % Number of points for B-splines (10 in 3 coordinates)
splinestart = 1;
splinestart2 = 1;
nextsplinepoints = 0;
nextsplinepoints2 = 0;
tend=100;
%% global parameters index
global index
% inputs
index.dotab = 1;
index.dotbeta = 2;
index.ds = 3;
index.tv = 4;
index.slack = 5;
% states
index.x = 6;
index.y = 7;
index.theta = 8;
index.dottheta = 9;
index.v = 10;
index.yv = 11;
index.ab = 12;
index.beta = 13;
index.s = 14;
% states and input numbers
index.ns = 9;
index.nu = 5;
index.nv = index.ns+index.nu;   % = 14
index.sb = index.nu+1;          % = 6
% parameters
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
index.pointsO=pointsO;
index.pointsN=pointsN;
solvetimes = [];
solvetimes2 = [];

integrator_stepsize = 0.1;

%% model definition
model.N = 31;                       % Forward horizon Length
model.nvar = index.nv;              % = 14
model.neq = index.ns;               % = 9
model.eq = @(z,p) RK4( ...
    z(index.sb:end), ...
    z(1:index.nu), ...
    @(x,u,p)interstagedx_HC(x,u,p), ... %PACEJKA PARAMETERS
    integrator_stepsize,...
    p);
model.E = [zeros(index.ns,index.nu), eye(index.ns)];

l = 1;

%% Inequality constraints
model.nh = 5; 
model.ineq = @(z,p) nlconst_HC(z,p);
model.hu = [0;0;1;0;0];
model.hl = [-inf;-inf;-inf;-inf;-inf];


%% Random control points for trajectory sampling
% points = [36.2,52,57.2,53,52,47,41.8;...          %x
%           44.933,58.2,53.8,49,44,43,38.33; ...    %y
%           2,2,2,2,2,2,2]';          %phi
  
% points = [18,35,42,55.2,56,51,42,40;...          %x
%           41,55,57,56,43,40,45,31; ...    %y
%           2.5,2.5,2.5,2.5,2.5,2.5,2.3,2.5]';   %phi

points = [36.2,52,57.2,53,52,47,41.8;...          %x
          44.933,58.2,53.8,49,44,43,38.33; ...    %y
          2.5,2.5,2.5,2.5,2.5,2.5,2.5]';                        %width 

points(:,3)=points(:,3)-0.2;
points2=flip(points);

%% Objective function
trajectorytimestep = integrator_stepsize;

model.npar = pointsO + 3*pointsN;
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

%% Equality Constraints
model.xinitidx = index.sb:index.nv;

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

model.ub(index.s)=pointsN-2;
model.lb(index.s)=0;

%% CodeOptions for FORCES solver
codeoptions = getOptions('MPCPathFollowing_Boom'); % Need FORCES License to run
codeoptions.maxit = 1000;    % Maximum number of iterations
codeoptions.printlevel = 2; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output); % Need FORCES License to run

%% Simulation kart 1
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
history = zeros(tend*eulersteps,model.nvar+1);
splinepointhist = zeros(tend,pointsN*3+1);
plansx = [];
plansy = [];
planss = [];
targets = [];
planc = 10;
x0 = [zeros(model.N,index.nu),repmat(xs,model.N,1)]';
tstart = 1;

%% Simulation kart 2

planintervall2 = 1;
fpoints2 = points2(1:2,1:2);
pdir2 = diff(fpoints2);
[pstartx2,pstarty2] = casadiDynamicBSPLINE(0.01,points2);
pstart2 = [pstartx2,pstarty2];
pangle2 = atan2(pdir2(2),pdir2(1));
xs2(index.x-index.nu)=pstart2(1);
xs2(index.y-index.nu)=pstart2(2);
xs2(index.theta-index.nu)=pangle2;
xs2(index.v-index.nu)=5;
xs2(index.ab-index.nu)=0;
xs2(index.beta-index.nu)=0;
xs2(index.s-index.nu)=0.01;
history2 = zeros(tend*eulersteps,model.nvar+1);
splinepointhist2 = zeros(tend,pointsN*3+1);
plansx2 = [];
plansy2 = [];
planss2 = [];
targets2 = [];
planc2 = 10;
x02 = [zeros(model.N,index.nu),repmat(xs2,model.N,1)]';
a=0;
a2=0;
IND=[];
IND2=[];

for i =1:tend
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
    if(1)
        if xs2(index.s-index.nu)>1
            nextSplinePoints2;
            %spline step forward
            splinestart2 = splinestart2+1;
            xs2(index.s-index.nu)=xs2(index.s-index.nu)-1;
        end
    end
    % go kart 1
    xs(index.ab-index.nu)=min(casadiGetMaxAcc(xs(index.v-index.nu))-0.0001,xs(index.ab-index.nu));
    problem.xinit = xs';
    % go kart 2
    xs2(index.ab-index.nu)=min(casadiGetMaxAcc(xs2(index.v-index.nu))-0.0001,xs2(index.ab-index.nu));
    problem2.xinit = xs2';
    
    % go kart 1
    ip = splinestart;
    [nkp, ~] = size(points);
    nextSplinePoints = zeros(pointsN,3);
    for jj=1:pointsN
       while ip>nkp
            ip = ip -nkp;
       end
       nextSplinePoints(jj,:)=points(ip,:);
       ip = ip + 1;
    end
    splinepointhist(i,:)=[xs(index.s-index.nu),nextSplinePoints(:)'];
    
    %go kart 2
    ip2 = splinestart2;
    [nkp2, ~] = size(points2);
    nextSplinePoints2 = zeros(pointsN,3);
    for jj=1:pointsN
       while ip2>nkp2
            ip2 = ip2 -nkp2;
       end
       nextSplinePoints2(jj,:)=points2(ip2,:);
       ip2 = ip2 + 1;
    end
    splinepointhist2(i,:)=[xs2(index.s-index.nu),nextSplinePoints2(:)'];
    
    % go kart 1
    problem.all_parameters = repmat (getParametersHC(maxSpeed,maxxacc,...
        steeringreg,specificmoi,FB,FC,FD,RB,RC,RD,b_steer,k_steer,J_steer,...
        plag,plat,pprog,pab,pspeedcost,pslack,...
        ptv,nextSplinePoints) , model.N ,1);
    problem.x0 = x0(:);
   
    %go kart 2
    problem2.all_parameters = repmat (getParametersHC(maxSpeed,maxxacc,...
        steeringreg,specificmoi,FB,FC,FD,RB,RC,RD,b_steer,k_steer,J_steer,...
        plag,plat,pprog,pab,pspeedcost,pslack,...
        ptv,nextSplinePoints2) , model.N ,1);
    problem2.x0 = x02(:);
    
    % solve mpc go kart 1
    [output,exitflag,info] = MPCPathFollowing_Boom(problem);
    solvetimes(end+1)=info.solvetime;
    if(exitflag==0)
        a =a+ 1;
        IND=[IND;i];
    end
    if(exitflag~=1 && exitflag ~=0)
        keyboard
        
    end
     % solve mpc go kart 2
    [output2,exitflag2,info2] = MPCPathFollowing_Boom(problem2);
    solvetimes2(end+1)=info2.solvetime;
    if(exitflag2==0)
        a2 =a2+ 1;
        IND2=[IND2;i];
    end
    if(exitflag2~=1 && exitflag2 ~=0)
        keyboard
        
    end
    
    %get output go kart 1
    outputM = reshape(output.alldata,[model.nvar,model.N])';
    x0 = outputM';
    u = repmat(outputM(1,1:index.nu),eulersteps,1);
    [xhist,time] = euler(@(x,u)interstagedx_HC(x,u,problem.all_parameters),xs,u,integrator_stepsize/eulersteps);
    xs = xhist(end,:);
    xs
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

    %get output go kart 2
    outputM2 = reshape(output2.alldata,[model.nvar,model.N])';
    x02 = outputM2';
    u2 = repmat(outputM2(1,1:index.nu),eulersteps,1);
    [xhist2,time2] = euler(@(x2,u2)interstagedx_HC(x2,u2,problem2.all_parameters),xs2,u2,integrator_stepsize/eulersteps);
    xs2 = xhist2(end,:);
    xs2
    history2((tstart-1)*eulersteps+1:(tstart)*eulersteps,:)=[time2(1:end-1)+(tstart-1)*integrator_stepsize,u2,xhist2(1:end-1,:)];
    planc2 = planc2 + 1;
    if(planc2>planintervall)
        planc2 = 1;
        plansx2 = [plansx2; outputM2(:,index.x)'];
        plansy2 = [plansy2; outputM2(:,index.y)'];
        planss2 = [planss2; outputM2(:,index.s)'];
        [tx2,ty2]=casadiDynamicBSPLINE(outputM2(end,index.s),nextSplinePoints2);
        targets2 = [targets2;tx2,ty2];
    end
    
end
% Plot
draw2


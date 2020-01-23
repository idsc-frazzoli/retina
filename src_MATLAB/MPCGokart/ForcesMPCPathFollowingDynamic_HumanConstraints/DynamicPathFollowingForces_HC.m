%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Dynamic MPC Script with tunable parameters
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% code by em

%add force path (change that for yourself)
addpath('..');
userDir = getuserdir;
addpath([userDir '/Forces']); % Location of FORCES PRO
addpath('C:\Users\me\Documents\FORCES_client');
addpath('casadi');
addpath('../shared_dynamic')
    
clear model
clear problem
clear all

%% Baseline params
behaviour='aggressive'; %aggressive,medium, beginner,drifting,custom,collision
[maxSpeed,maxxacc,steeringreg,specificmoi,plag,...
    plat,pprog,pab,pspeedcost,pslack,ptv] = DriverConfig(behaviour);
FB = 9;
FC = 1;
FD = 6.5; % gravity acceleration considered
RB = 5.2;
RC = 1.1;
RD = 6;
J_steer = 0.8875;
b_steer = 0.1625;
k_steer = 0.0125;
ptau = 0.05;   
pointsO = 21; % number of Parameters
pointsN = 10; % Number of points for B-splines (10 in 3 coordinates)
splinestart = 1;
nextsplinepoints = 0;

%% TEND 
tend = 50;
eulersteps = 10;


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

solvetimes = [];

integrator_stepsize = 0.1;

%% model params
model.N = 31;                       % Forward horizon
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

%limit lateral acceleration
model.nh = 5; 
model.ineq = @(z,p) nlconst_HC(z,p);
%model.hu = [36,0];
%model.hl = [-inf,-inf];
model.hu = [0;0;1;0;0];
model.hl = [-inf;-inf;-inf;-inf;-inf];


% Random control points for trajectory sampling
%points = [1,2,2,4,2,2,1;0,0,5.7,6,6.3,10,10]';
  %  controlPointsX.append(Quantity.of(36.2, SI.METER));
  %  controlPointsX.append(Quantity.of(52, SI.METER));
  %  controlPointsX.append(Quantity.of(57.2, SI.METER));
  %  controlPointsX.append(Quantity.of(53, SI.METER));
  %  controlPointsX.append(Quantity.of(52, SI.METER));
  %  controlPointsX.append(Quantity.of(47, SI.METER));
  %  controlPointsX.append(Quantity.of(41.8, SI.METER));
  %  // Y
  %  controlPointsY.append(Quantity.of(44.933, SI.METER));
  %  controlPointsY.append(Quantity.of(58.2, SI.METER));
  %  controlPointsY.append(Quantity.of(53.8, SI.METER));
  %  controlPointsY.append(Quantity.of(49, SI.METER));
  %  controlPointsY.append(Quantity.of(47, SI.METER));
  %  controlPointsY.append(Quantity.of(43, SI.METER));
  %  controlPointsY.append(Quantity.of(38.333, SI.METER));  
% points = [20,25,35,45,49,46,37,27,28,35,45,48,45,36,28,22,21;...          %x
%           40,34,35,34,38,42,40,42,48,49,46,52,54,52,53,54,47; ...    %y
%           1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5]';
 
      points = [25,35,45,49,46,37,27,28,35,45,48,45,36,28,22,21,20;...          %x
         34,35,34,38,42,40,42,48,49,46,52,54,52,53,54,47,40; ...    %y
          1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5,1.5]';



% %points = getPoints('/wildpoints.csv');
points(:,3)=points(:,3)-0.2;


trajectorytimestep = integrator_stepsize;
%[p,steps,speed,ttpos]=getTrajectory(points,2,1,trajectorytimestep);
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


%model.objective{model.N} = @(z,p)objectiveN(z,getPointsFromParameters(p, pointsO, pointsN),p(index.ps));

model.xinitidx = index.sb:index.nv;
% variables z = [ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
model.ub = ones(1,index.nv)*inf;
model.lb = -ones(1,index.nv)*inf;
%model.ub(index.dotbeta)=5;
%model.lb(index.dotbeta)=-5;
model.ub(index.ds)=5;
model.lb(index.ds)=-1;
%model.ub(index.ab)=2;
%model.lb(index.ab)=-4.5;
model.lb(index.ab)=-inf;

model.ub(index.tv)=1.6;
model.lb(index.tv)=-1.6;
%model.ub(index.tv)=0.1;
%model.lb(index.tv)=-0.1;
model.lb(index.slack)=0;

model.lb(index.v)=0;

model.ub(index.beta)=0.52;
model.lb(index.beta)=-0.52;

model.ub(index.s)=pointsN-2;
model.lb(index.s)=0;


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
%xs(index.braketemp-index.nu)=40;
history = zeros(tend*eulersteps,model.nvar+1);
splinepointhist = zeros(tend,pointsN*3+1);
plansx = [];
plansy = [];
planss = [];
targets = [];
planc = 10;
x0 = [zeros(model.N,index.nu),repmat(xs,model.N,1)]';
%x0 = zeros(model.N*model.nvar,1); 
tstart = 1;
%paras = ttpos(tstart:tstart+model.N-1,2:3)';
a = 0;
for i = 1:tend
    tstart = i;
    %model.xinit = [0,5,0,0.1,0,0];

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
    %xs(6)=xs(6)+normrnd(0,0.04);
    xs(index.ab-index.nu)=min(casadiGetMaxAcc(xs(index.v-index.nu))-0.0001,xs(index.ab-index.nu));
    problem.xinit = xs';
    %do it every time because we don't care about the performance of this
    %script
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
    %paras = ttpos(tstart:tstart+model.N-1,2:3)';
    problem.all_parameters = repmat(getParametersTHC(maxSpeed,maxxacc,...
        steeringreg,specificmoi,FB,FC,FD,RB,RC,RD,b_steer,k_steer,J_steer,...
        plag,plat,pprog,pab,pspeedcost,...
        pslack,ptv,ptau,nextSplinePoints) , model.N ,1);
    %problem.all_parameters = zeros(22,1);
    problem.x0 = x0(:);
    %problem.x0 = rand(341,1);
    
    % solve mpc
    [output,exitflag,info] = MPCPathFollowing(problem);
    solvetimes(end+1)=info.solvetime;
    if(exitflag==0)
        a = a + 1;
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
    [xhist,time] = euler(@(x,u)interstagedx_HC(x,u,problem.all_parameters),xs,u,integrator_stepsize/eulersteps);
    xs = xhist(end,:);
    %xs
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
%[t,ab,dotbeta,x,y,theta,v,beta,s]
draw


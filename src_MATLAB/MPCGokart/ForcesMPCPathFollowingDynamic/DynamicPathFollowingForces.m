%add force path (change that for yourself)
addpath('..');
userDir = getuserdir;
addpath([userDir '/Forces']);
addpath('casadi');

    
clear model
clear problem
clear all
close all

maxSpeed = 10;
maxxacc = 4;
maxyacc = 8;
latacclim = 6;
rotacceffect  = 2;
torqueveceffect = 3;
brakeeffect = 0;
pointsO = 2;
pointsN = 10;
splinestart = 1;
nextsplinepoints = 0;
%parameters: p = [maxspeed, xmaxacc,ymaxacc,latacclim,rotacceffect,torqueveceffect, brakeeffect, pointsx, pointsy]
% variables z =
% [dotab,dotbeta,ds,tv,slack,x,y,theta,dottheta,v,yv,ab,beta,s]
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
index.nv = index.ns+index.nu;
index.sb = index.nu+1;
index.ps = 1;
index.pax = 2;

integrator_stepsize = 0.1;

model.N = 31;
model.nvar = index.nv;
model.neq = index.ns;

model.eq = @(z,p) RK4( z(index.sb:end), z(1:index.nu), @(x,u,p)interstagedx(x,u), integrator_stepsize,p);
model.E = [zeros(index.ns,index.nu), eye(index.ns)];

l = 1;

%limit lateral acceleration
model.nh = 5; 
model.ineq = @(z,p) nlconst(z,p);
%model.hu = [36,0];
%model.hl = [-inf,-inf];
model.hu = [0;0;1;0;0];
model.hl = [-inf;-inf;-inf;-inf;-inf];

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
    
%points = [36.2,52,57.2,53,52,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.5,0.5,0.5,1.8]';
points = getPoints('/wildpoints.csv');
points(:,3)=points(:,3)-0.2;
%points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
%points = [0,40,40,5,0;0,0,10,9,10]';
trajectorytimestep = integrator_stepsize;
%[p,steps,speed,ttpos]=getTrajectory(points,2,1,trajectorytimestep);
model.npar = pointsO + 3*pointsN;
for i=1:model.N
   model.objective{i} = @(z,p)objective(z,...
       getPointsFromParameters(p, pointsO, pointsN),...
       getRadiiFromParameters(p, pointsO, pointsN),...
       p(index.ps),...
       p(index.pax));
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
model.lb(index.ab)=-4.5;
model.lb(index.ab)=-inf
model.ub(index.tv)=1.7;
model.lb(index.tv)=-1.7;
%model.ub(index.tv)=0.1;
%model.lb(index.tv)=-0.1;
model.lb(index.slack)=0;
model.lb(index.v)=0;
model.ub(index.beta)=0.5;
model.lb(index.beta)=-0.5;
model.ub(index.s)=pointsN-2;
model.lb(index.s)=0;

%model.ub = [inf, +5, 1.6, +inf, +inf, +inf, +inf,0.45,pointsN-2,85];  % simple upper bounds 
%model.lb = [-inf, -5, -0.1, -inf, -inf,  -inf, 0,-0.45,0,-inf];  % simple lower bounds 
codeoptions = getOptions('MPCPathFollowing');
codeoptions.maxit = 200;    % Maximum number of iterations
codeoptions.printlevel = 2; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output);

tend = 100;
eulersteps = 10;
planintervall = 1
%[...,x,y,theta,v,ab,beta,s,braketemp]
%[49.4552   43.1609   -2.4483    7.3124   -1.0854   -0.0492    1.0496   39.9001]
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
for i =1:tend
    tstart = i;
    %model.xinit = [0,5,0,0.1,0,0];

    %find bspline
    if(1)
        if xs(index.s-index.nu)>1
            nextSplinePoints
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
    for i=1:pointsN
       while ip>nkp
            ip = ip -nkp;
       end
       nextSplinePoints(i,:)=points(ip,:);
       ip = ip + 1;
    end
    splinepointhist(i,:)=[xs(index.s-index.nu),nextSplinePoints(:)'];
    
    
    %paras = ttpos(tstart:tstart+model.N-1,2:3)';
    problem.all_parameters = repmat (getParameters(maxSpeed,maxxacc,nextSplinePoints) , model.N ,1);
    %problem.all_parameters = zeros(22,1);
    problem.x0 = x0(:);
    %problem.x0 = rand(341,1);
    
    % solve mpc

    [output,exitflag,info] = MPCPathFollowing(problem);
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
end
%[t,ab,dotbeta,x,y,theta,v,beta,s]
draw


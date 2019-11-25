%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Dynamic MPC Script
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% code by mh
% annotation mcp


%add force path (change that for yourself)
addpath('..');
userDir = getuserdir;
%addpath([userDir '/Forces']); % Location of FORCES PRO
addpath('casadi');
addpath('../shared_dynamic')
    
clear model
clear problem
clear all
%close all

behaviour='custom'; %aggressive,medium, beginner,drifting,custom,collision
%% Baseline params
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
pointsO = 24; % number of Parameters
pointsN = 10; % Number of points for B-splines (10 in 3 coordinates)
splinestart = 1;
splinestart2 = 1;
nextsplinepoints = 0;
nextsplinepoints2 = 0;
%parameters: p = [maxspeed, xmaxacc,ymaxacc,latacclim,rotacceffect,torqueveceffect, brakeeffect, pointsx, pointsy]
% variables z = [dotab,dotbeta,ds,tv,slack,x,y,theta,dottheta,v,yv,ab,beta,s]


%% global parameters index
global index
index.dotab = 1;
index.dotbeta = 2;
index.ds = 3;
index.tv = 4;
index.slack = 5;
index.slack2=6;
index.x = 7;
index.y = 8;
index.theta = 9;
index.dottheta = 10;
index.v = 11;
index.yv = 12;
index.ab = 13;
index.beta = 14;
index.s = 15;
index.ns = 9;
index.nu = 6;
index.nv = index.ns+index.nu;   % = 15
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
index.pslack2=20;
index.ptv = 21;
index.xComp=22;
index.yComp=23;
index.dist=24;
solvetimes = [];
solvetimes2=[];

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
model.nh = 6; 
model.ineq = @(z,p) nlconst_GT(z,p);
%model.hu = [36,0];
%model.hl = [-inf,-inf];
model.hu = [0;0;1;0;0;inf];
model.hl = [-inf;-inf;-inf;-inf;-inf;0];

% points = [18,35,42,55.2,56,51,42,40;...          %x
%           41,55,57,56,43,40,45,31; ...    %y
%           2.5,2.5,2.5,2.5,2.5,2.5,2.3,2.5]';   %phi
points = [18,35,42,55.2,60,51,42,40;...          %x
          41,55,57,56,43,40,42,31; ...    %y
          2.5,2.5,2.5,2.5,2.3,2.3,2.3,2.3]';
% %points = getPoints('/wildpoints.csv');
points2=flip(points);
points(:,3)=points(:,3)-0.2;
points2(:,3)=points2(:,3)-0.2;
%points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
%points = [0,40,40,5,0;0,0,10,9,10]';

trajectorytimestep = integrator_stepsize;
%[p,steps,speed,ttpos]=getTrajectory(points,2,1,trajectorytimestep);
model.npar = pointsO + 3*pointsN;
for i=1:model.N
   model.objective{i} = @(z,p)objective_GT(...
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
       p(index.pslack2),...
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

model.ub(index.tv)=1.7;
model.lb(index.tv)=-1.7;
%model.ub(index.tv)=0.1;
%model.lb(index.tv)=-0.1;
model.lb(index.slack)=0;
model.lb(index.slack2)=0;
model.lb(index.v)=0;

model.ub(index.beta)=0.5;
model.lb(index.beta)=-0.5;

model.ub(index.s)=pointsN-2;
model.lb(index.s)=0;

%model.ub = [inf, +5, 1.6, +inf, +inf, +inf, +inf,0.45,pointsN-2,85];  % simple upper bounds 
%model.lb = [-inf, -5, -0.1, -inf, -inf,  -inf, 0,-0.45,0,-inf];  % simple lower bounds 



%% CodeOptions for FORCES solver
codeoptions = getOptions('MPCPathFollowing'); % Need FORCES License to run
codeoptions.maxit = 4000;    % Maximum number of iterations
codeoptions.printlevel = 2; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output); % Need FORCES License to run

%% CodeOptions for FORCES solver
tend = 200;
eulersteps = 10;
planintervall = 1;
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

%% kart 2

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
%xs(index.braketemp-index.nu)=40;
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
Pos1=repmat(pstart, model.N-1 ,1);
Pos2=repmat(pstart2, model.N-1 ,1);
%paras = ttpos(tstart:tstart+model.N-1,2:3)';
for i =1:tend
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
    if(1)
        if xs2(index.s-index.nu)>1
            nextSplinePoints2;
            %spline step forward
            splinestart2 = splinestart2+1;
            xs2(index.s-index.nu)=xs2(index.s-index.nu)-1;
            %if(splinestart>pointsN)
                %splinestart = splinestart-pointsN;
            %end
        end
    end
    %xs(6)=xs(6)+normrnd(0,0.04);
    xs(index.ab-index.nu)=min(casadiGetMaxAcc(xs(index.v-index.nu))-0.0001,xs(index.ab-index.nu));
    problem.xinit = xs';
    %go kart 2
    xs2(index.ab-index.nu)=min(casadiGetMaxAcc(xs2(index.v-index.nu))-0.0001,xs2(index.ab-index.nu));
    problem2.xinit = xs2';
    
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
    
    problem.all_parameters = repmat (getParametersGT(maxSpeed,maxxacc,...
        steeringreg,specificmoi,FB,FC,FD,RB,RC,RD,b_steer,k_steer,J_steer,...
        plag,plat,pprog,pab,pspeedcost,pslack,pslack2,...
        ptv,xs2(1),xs2(2),dist,nextSplinePoints) , model.N ,1);
   
    problem.all_parameters(index.xComp:model.npar:end)=[Pos2(:,1);Pos2(end,1)];
    problem.all_parameters(index.yComp:model.npar:end)=[Pos2(:,2);Pos2(end,2)];
    problem.x0 = x0(:);
     %go kart 2
    problem2.all_parameters = repmat (getParametersGT(maxSpeed,maxxacc,...
        steeringreg,specificmoi,FB,FC,FD,RB,RC,RD,b_steer,k_steer,J_steer,...
        plag,plat,pprog,pab,pspeedcost,pslack,pslack2,...
        ptv,xs(1),xs(2),dist,nextSplinePoints2) , model.N ,1);
    
    problem2.all_parameters(index.xComp:model.npar:end)=[Pos1(:,1);Pos1(end,1)];
    problem2.all_parameters(index.yComp:model.npar:end)=[Pos1(:,2);Pos1(end,2)];
    problem2.x0 = x02(:);
    % solve mpc
    [output,exitflag,info] = MPCPathFollowing(problem);
    solvetimes(end+1)=info.solvetime;
    if(exitflag==0)
        a =a+ 1;
        IND=[IND;i];
    end
    if(exitflag~=1 && exitflag ~=0)
        keyboard
        
    end
    [output2,exitflag2,info2] = MPCPathFollowing(problem2);
    solvetimes2(end+1)=info2.solvetime;
    if(exitflag2==0)
        a2 =a2+ 1;
        IND2=[IND2;i];
    end
    if(exitflag2~=1 && exitflag2 ~=0)
        keyboard
        
    end
    
    %get output
    outputM = reshape(output.alldata,[model.nvar,model.N])';
    x0 = outputM';
    u = repmat(outputM(1,1:index.nu),eulersteps,1);
    [xhist,time] = euler(@(x,u)interstagedx(x,u,problem.all_parameters),xs,u,integrator_stepsize/eulersteps);
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
    Pos1=[outputM(2:end,index.x),outputM(2:end,index.y)];
    % go kart 2
    %get output
    outputM2 = reshape(output2.alldata,[model.nvar,model.N])';
    x02 = outputM2';
    u2 = repmat(outputM2(1,1:index.nu),eulersteps,1);
    [xhist2,time2] = euler(@(x2,u2)interstagedx(x2,u2,problem2.all_parameters),xs2,u2,integrator_stepsize/eulersteps);
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
    Pos2=[outputM2(2:end,index.x),outputM2(2:end,index.y)];
    distanceX=xs(1)-xs2(1);
    distanceY=xs(2)-xs2(2);
   
    squared_distance_array   = sqrt(distanceX.^2 + distanceY.^2);
    if squared_distance_array<=dist
        squared_distance_array
    end
    
end
%[t,ab,dotbeta,x,y,theta,v,beta,s]
draw2


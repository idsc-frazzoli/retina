%add force path (change that for yourself)
addpath('/home/marc/Forces')
addpath('..');
addpath('casadi');
    
clear model
clear problem
clear all
close all

% variables z = [ab,dotbeta,ds,brake,x,y,theta,v,beta,s,braketemp]
integrator_stepsize = 0.2;

model.N = 31;
model.nvar = 11;
model.neq = 7;

model.eq = @(z,p) RK4( z(5:11), z(1:4), @(x,u,p)interstagedx(x,u), integrator_stepsize,p);
model.E = [zeros(7,4), eye(7)];

l = 1;

%limit lateral acceleration
model.nh = 3; 
model.ineq = @(z) nlconst(z);
model.hu = [8,0,0];
model.hl = [-inf,-inf, -inf];


%points = [1,2,2,4,2,2,1;0,0,5.7,6,6.3,10,10]';
points = [0,20,20,5,0;0,0,10,9,10]';
trajectorytimestep = integrator_stepsize;
[p,steps,speed,ttpos]=getTrajectory(points,2,1,trajectorytimestep);

model.npar = 2;
for i=1:model.N-1
   model.objective{i} = @(z,p)objective(z,p,points);
end
model.objective{model.N} = @(z,p)objectiveN(z,p,points);

model.xinitidx = 5:11;

model.ub = [inf, +inf, 0.5,2.5, +inf, +inf, +inf, +inf,1,+inf,+inf];  % simple upper bounds 
model.lb = [-inf, -inf, +0.001,0, -inf, -inf,  -inf, -inf,-1,-inf,-inf];  % simple lower bounds 

codeoptions = getOptions('MPCPathFollowing');
codeoptions.maxit = 200;    % Maximum number of iterations
codeoptions.printlevel = 2; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 1;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output);

tend = 125;
eulersteps = 20;
xs = [5,0,0,1,0,0,0];
history = zeros(tend*eulersteps,model.nvar+1);
x0 = [zeros(model.N,4),repmat(xs,model.N,1)]';
%x0 = zeros(model.N*model.nvar,1); 
tstart = 1;
paras = ttpos(tstart:tstart+model.N-1,2:3)';
for i =1:tend
    tstart = i;
    %model.xinit = [0,5,0,0.1,0,0];
    problem.xinit = xs';

    %paras = ttpos(tstart:tstart+model.N-1,2:3)';
    problem.all_parameters = paras(:);
    %problem.all_parameters = zeros(22,1);
    problem.x0 = x0(:);

    % solve mpc
    [output,exitflag,info] = MPCPathFollowing(problem);

    %get output
    outputM = reshape(output.alldata,[model.nvar,model.N])';
    x0 = outputM';
    u = repmat(outputM(1,1:4),eulersteps,1);
    [xhist,time] = euler(@(x,u)interstagedx(x,u),xs,u,integrator_stepsize/eulersteps);
    xs = xhist(end,:);
    history((tstart-1)*eulersteps+1:(tstart)*eulersteps,:)=[time(1:end-1)+(tstart-1)*integrator_stepsize,u,xhist(1:end-1,:)];
end
%[t,ab,dotbeta,x,y,theta,v,beta,s]
draw


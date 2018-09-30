%add force path (change that for yourself)
addpath('/home/marc/Forces')
addpath('..');
addpath('ACADIFunctions');
    
clear model
clear problem
clear all
close all

% variables z = [ab,dotbeta,x,y,theta,v,beta,s]
integrator_stepsize = 0.3;

model.N = 31;
model.nvar = 8;
model.neq = 6;

model.eq = @(z,p) RK4( z(3:8), z(1:2), @(x,u,p)interstagedx(x,u), integrator_stepsize,p);
model.E = [zeros(6,2), eye(6)];

%points = [1,2,2,4,2,2,1;0,0,5.7,6,6.3,10,10]';
points = [0,10,10,5,0;0,0,10,9,10]';
trajectorytimestep = integrator_stepsize;
[p,steps,speed,ttpos]=getTrajectory(points,2,1,trajectorytimestep);

model.npar = 2;
for i=1:model.N-1
   model.objective{i} = @(z,p)objective(z,p,points);
end
model.objective{model.N} = @(z,p)objectiveN(z,p,points);

model.xinitidx = 3:8;

model.ub = [+0.5, +0.4, +inf, +inf, +inf, +inf,0.4,+inf];  % simple upper bounds 
model.lb = [-0.5, -0.4, -inf, -inf,  -inf, -inf,-0.4,-inf];  % simple lower bounds 

codeoptions = getOptions('FORCESNLPsolver');
codeoptions.maxit = 200;    % Maximum number of iterations
codeoptions.printlevel = 2; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 2;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

output = newOutput('alldata', 1:model.N, 1:model.nvar);

FORCES_NLP(model, codeoptions,output);

tend = 130;
eulersteps = 30;
xs = [5,0,0,0,0,0];
history = zeros(tend*eulersteps,model.nvar+1);
x0 = [zeros(model.N,2),repmat(xs,model.N,1)]';
%x0 = zeros(model.N*model.nvar,1); 
for i =1:tend
    tstart = i;
    %model.xinit = [0,5,0,0.1,0,0];
    problem.xinit = xs';
    paras = ttpos(tstart:tstart+model.N-1,2:3)';
    problem.all_parameters = paras(:);
    %problem.all_parameters = zeros(22,1);
    problem.x0 = x0(:);

    % solve mpc
    [output,exitflag,info] = FORCESNLPsolver(problem);

    %get output
    outputM = reshape(output.alldata,[model.nvar,model.N])';
    x0 = outputM';
    u = repmat(outputM(1,1:2),eulersteps,1);
    [xhist,time] = euler(@(x,u)interstagedx(x,u),xs,u,integrator_stepsize/eulersteps);
    xs = xhist(end,:);
    history((tstart-1)*eulersteps+1:(tstart)*eulersteps,:)=[time(1:end-1)+(tstart-1)*integrator_stepsize,u,xhist(1:end-1,:)];
end
%[t,ab,dotbeta,x,y,theta,v,beta,s]
draw


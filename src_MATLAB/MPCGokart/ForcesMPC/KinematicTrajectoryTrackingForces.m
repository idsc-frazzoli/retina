%add force path (change that for yourself)
addpath('/home/marc/Forces')
addpath('..');
    
clear model
close all

% variables z = [ab,dotbeta,x,y,theta,v,beta,s]
integrator_stepsize = 0.1;

model.N = 11;
model.nvar = 8;
model.neq = 6;


model.eq = @(z,p) RK4( z(3:8), z(1:2), @(x,u,p)interstagedx(x,u), integrator_stepsize,p);
model.E = [zeros(6,2), eye(6)];

%points = [1,2,2,4,2,2,1;0,0,5.7,6,6.3,10,10]';
points = [0,10,10,5,0;0,0,10,9,10]';
trajectorytimestep = 0.1;
[p,steps,speed,ttpos]=getTrajectory(points,2,0.6,trajectorytimestep);

for i=1:model.N-1
   model.npar(i) = 2;
   model.objective{i} = @(z,p)objective(z,p);
end
model.npar(model.N) = 2;
model.objective{model.N} = @(z,p)objectiveN(z,p);

model.xinitdx = 3:8;

codeoptions = getOptions('FORCESNLPsolver');
codeoptions.maxit = 200;    % Maximum number of iterations
codeoptions.printlevel = 2; % Use printlevel = 2 to print progress (but not for timings)
codeoptions.optlevel = 0;   % 0: no optimization, 1: optimize for size, 2: optimize for speed, 3: optimize for size & speed
codeoptions.cleanup = false;
codeoptions.timing = 1;

FORCES_NLP(model, codeoptions);



model.xinit = [0,5,0,0.1,0,0];

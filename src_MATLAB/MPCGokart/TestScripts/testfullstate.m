addpath('..')
addpath('../visualization')
addpath('../ML')
addpath('../SystemAnalysis')
addpath('../scripts')
clear
fullM = csvread('../SystemAnalysis/ML_out/20180820T165637_5.csv');
times = fullM(:,1);
names = {"race test real","simulation"};

initialize_parameters
%u = getControl(fullM);
%u = u(1:5000,:)*0.001;
u = zeros(6000,4);
u(:,3:4)=1;
x = zeros(1,8);
f = @(x,u)fullstatemodel(x,u);
[simxhist,simtime] = euler(f,x,u,0.01);

visualize(1,[1,18],names,times,fullM, [simtime,simxhist]);
%visualize(1,[1,18],names,times,fullM);
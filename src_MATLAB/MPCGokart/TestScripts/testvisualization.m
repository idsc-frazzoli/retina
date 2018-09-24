addpath('..')
addpath('../misc')
addpath('../visualization')
addpath('../ML')
addpath('../SystemAnalysis')
clear
fullM = csvread('../SystemAnalysis/ML_out/20180820T165637_5.csv');
times = fullM(:,1);
names = {"race test real"};
visualize(1,[1,18],names,times,fullM);
%visualize(1,[],names,times,fullM);
addpath('..')  

userdir = getuserdir

folders = {};
targetfiles = {};
if(0)
    folders{end+1} = '/Documents/retina_out/20181025T1334006b1a19b1.lcm/';
    targetfiles{end+1}= 'brakingMLData.csv';
end
if(1)
    folders{end+1} = '/retina_out/brakingtest.lcm/';
    targetfiles{end+1}= 'brakingMLData.csv';
end




RTSTargetFolder = strcat(userdir,'/Documents/RTS_out/');
MLTargetFolder = strcat(userdir,'/Documents/ML_out/');

if (~exist(RTSTargetFolder, 'dir')); mkdir(RTSTargetFolder); end
if (~exist(MLTargetFolder, 'dir')); mkdir(MLTargetFolder); end

N = numel(folders);
tic;
%change to for if you don't have parallel toolbox installed ;-)
for i = 1:N
    folders{i}=strcat(userdir,folders{i});
    M = ModelfreeStateEstimation(folders{i});
    RTSTarget = strcat(RTSTargetFolder,targetfiles{i});
    MLTarget = strcat(MLTargetFolder,targetfiles{i});
    csvwrite(RTSTarget, M);
    MLdata = loadinternalstates(M,folders{i});
    csvwrite(MLTarget, MLdata);
end
toc;

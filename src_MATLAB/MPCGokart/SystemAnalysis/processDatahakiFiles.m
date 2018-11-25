addpath('..')  

userdir = getuserdir

folders = {};
targetfiles = {};
if(1)
for i = 1:6
    folders{end+1} = num2str(i, '/Documents/retina_out/20180820T143852%d.lcm/');
    targetfiles{end+1}= num2str(i, '20180820T143852_%d.csv');
end
for i = 1:5
    folders{end+1} = num2str(i, '/Documents/retina_out/20180820T165637%d.lcm/');
    targetfiles{end+1} = num2str(i, '20180820T165637_%d.csv');
end
for i = 1:2
    folders{end+1} = num2str(i,  '/Documents/retina_out/20180702T133612%d.lcm/');
    targetfiles{end+1}= num2str(i, '20180702T133612_%d.csv');
end
for i = 1:4
    folders{end+1} = num2str(i, '/Documents/retina_out/20180702T154417%d.lcm/');
    targetfiles{end+1}= num2str(i, '20180702T154417_%d.csv');
end
for i = 1:6
    folders{end+1} = num2str(i, '/Documents/retina_out/20180702T180041%d.lcm/');
    targetfiles{end+1}= num2str(i, '20180702T180041_%d.csv');
end
for i = 1:3
    folders{end+1} = num2str(i, '/Documents/retina_out/20180705T101944%d.lcm/');
    targetfiles{end+1}= num2str(i, '20180705T101944_%d.csv');
end
end

folders{end+1} = '/Documents/retina_out/fab.lcm/';
targetfiles{end+1}= 'fab.csv';

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

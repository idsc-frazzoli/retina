folders = {};
targetfiles = {};
for i = 1:6
    folders{end+1} = num2str(i, 'retina_out/20180820T143852%d.lcm/');
    targetfiles{end+1}= num2str(i, 'RTS_out/20180820T143852_%d.csv');
end
for i = 1:5
    folders{end+1} = num2str(i, 'retina_out/20180820T165637%d.lcm/');
    targetfiles{end+1} = num2str(i, 'RTS_out/20180820T165637_%d.csv');
end
for i = 1:2
    folders{end+1} = num2str(i,  'retina_out/20180702T133612%d.lcm/');
    targetfiles{end+1}= num2str(i, 'RTS_out0w/20180702T133612_%d.csv');
end
for i = 1:4
    folders{end+1} = num2str(i, 'retina_out/20180702T154417%d.lcm/');
    targetfiles{end+1}= num2str(i, 'RTS_out0w/20180702T154417_%d.csv');
end
for i = 1:6
    folders{end+1} = num2str(i, 'retina_out/20180702T180041%d.lcm/');
    targetfiles{end+1}= num2str(i, 'RTS_out0w/20180702T180041_%d.csv');
end
for i = 1:3
    folders{end+1} = num2str(i, 'retina_out/20180705T101944%d.lcm/');
    targetfiles{end+1}= num2str(i, 'RTS_out0w/20180705T101944_%d.csv');
end

N = numel(folders);
tic;
parfor i = 1:N
    M = ModelfreeStateEstimation(folders{i});
    csvwrite(targetfiles{i}, M);
end
toc;

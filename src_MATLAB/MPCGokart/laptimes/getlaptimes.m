clear
close all
userdir = getuserdir

folders = {};
poses = {};
targetfiles = {};

if(1)
    % folders{end+1} ='\Documents\2019\ETH\Sem proj\Steering Model\PoseData2\b.csv';
    folders{end+1} ='\Documents\2019\ETH\Sem proj\Steering Model\PoseData2\m.csv';
end
N = numel(folders);
tic;
startpos = [30,25];
startrad = 2;
lastwasin = 0;
for i = 1:N
    folders{i}=strcat(userdir,folders{i});
    poses = csvread(folders{i});
    times = [];
    [m,n]=size(poses);
    lastenter = 0;
    for ii = 1:m
        pos = poses(ii,2:3);
        dist = norm(startpos-pos);
        if(dist < startrad)
            if(~lastwasin)
                time = poses(ii,1);
                times(end+1)=time-lastenter;
                lastenter=time;
            end
            lastwasin = 1;
        else
            lastwasin = 0;
        end
    end
    times(2:end)
    mean(times(2:end))
    min(times(2:end))
end

clear
close all
userdir = getuserdir

folders = {};
poses = {};
targetfiles = {};
if(1)
    folders{end+1} = '/mpcposes/mp1/';
    
    folders{end+1} = '/mpcposes/mp2/';
end
if(1)
    folders{end+1} = '/mpcposes/md1/';
end
N = numel(folders);
tic;
startpos = [30,30];
startrad = 4;
lastwasin = 0;
for i = 1:N
    folders{i}=strcat(userdir,folders{i});
    poses = loadSmoothPoseData(folders{i});
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
    times
    mean(times(2:end-1))
    min(times)
end

clear
close all
userdir = getuserdir;

folders = {};
poses = {};
targetfiles = {};

if(1)
%     folders{end+1} = '\mpcposes\mb1\';
%     folders{end+1} = '\mpcposes\mi1\';
%     folders{end+1} = '\mpcposes\ma1\';
%     folders{end+1} = '\mpcposes\mp1\';
%     folders{end+1} = '\mpcposes\mp2\';
%     folders{end+1} = '\mpcposes\mp3\';
%     folders{end+1} = '\mpcposes\mp4\';
%     folders{end+1} = '\mpcposes\mp5\';
%     folders{end+1} = '\mpcposes\t1\';
%     folders{end+1} = '\mpcposes\t2\';
%     folders{end+1} = '\mpcposes\t3\';
%     folders{end+1} = '\mpcposes\t4\';
%       folders{end+1} = '\mpcposes\EM_NT\';
      folders{end+1} = '\mpcposes\MPC_NT\';
end
% if(1)
%     folders{end+1} = '\mpcposes\md1\';
% end
N = numel(folders);
tic;

startpos = [45,45];
startrad = 4;

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
    times
    Media = mean(times(2:end-1))
    Minimo = min(times(2:end-1))
end

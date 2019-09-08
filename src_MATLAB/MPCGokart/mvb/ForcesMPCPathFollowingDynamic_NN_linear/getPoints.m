function P = getPoints(name)
addpath('..') 
userDir = getuserdir;
P = csvread(strcat(userDir,name));
end


function P = getPoints(name)
userDir = getuserdir;
P = csvread(strcat(userDir,name));
end


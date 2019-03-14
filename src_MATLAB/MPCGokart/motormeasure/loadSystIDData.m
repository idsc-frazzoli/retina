function imuData = loadSystIDData(folder)
    %[t,ax,ay,az,gx,gy,gz]
    imuData = csvread(strcat(folder,'sysID.csv'));
end


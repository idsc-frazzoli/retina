function imuData = loadDavisIMUData(folder)
    %[t,t0,ax,ay,az,temp,gx,gy,gz]
    imuData = csvread(strcat(folder,'davisIMU.csv'));
end


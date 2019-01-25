function imuData = loadVMUIMUData(folder)
    %[t,ax,ay,az,gx,gy,gz]
    imuData = csvread(strcat(folder,'vmu931IMU.csv'));
end


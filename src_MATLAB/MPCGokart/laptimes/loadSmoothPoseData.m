function posedata = loadSmoothPoseData(folder)
    %[t,ax,ay,az,gx,gy,gz]
    posedata = csvread(strcat(folder,'gokart.pose.lidar.csv'));
end


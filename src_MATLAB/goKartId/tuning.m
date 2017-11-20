%import shit and plot


clear all
close all
clc
get = csvread('/home/datahaki/sysid_get.csv',1,0);
put = csvread('/home/datahaki/sysid_put.csv',1,0);
putRef = csvread('/home/datahaki/sysid_putRef.csv',1,0);

%unfortunately times are different

torque = put(:,2); %torque
timePut = put(:,1) / 1e6; %convert us to s
vel = get(:,2); %velocity
timeGet = get(:,1) / 1e6; %convert us to s
pos = get(:, 3); % position


timePutRef = putRef(:,1) / 1e6;
posRef = putRef(:,2); %reference command fot the controller
pos = putRef(:,3); % emasured position (offset removed)


plot(timePutRef, posRef);
grid on
hold on
plot(timePutRef, pos)
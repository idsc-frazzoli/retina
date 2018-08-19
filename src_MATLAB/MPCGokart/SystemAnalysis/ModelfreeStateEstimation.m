startt = 500;
%endt = startt+70;
endt = startt+50;
%close all

%gplocalization = csvread('gplocalization.csv');
%davisIMU = csvread('davisIMU.csv');

%absolute lidar estimation
lx = table2array(gplocalization(:,3));
ly = table2array(gplocalization(:,4));
lt = table2array(gplocalization(:,1));
lq = table2array(gplocalization(:,2));
lo = table2array(gplocalization(:,5));
lo = unwrap(lo,2*pi);

lfirst = find(lt>startt,1)
llast = find(lt>endt,1)


lx = lx(lfirst:llast);
ly = ly(lfirst:llast);
lt = lt(lfirst:llast);
lq = lq(lfirst:llast);
lo = lo(lfirst:llast);

%accelerometer data
at = table2array(davisIMU(:,1));
ax = table2array(davisIMU(:,3));
ay = table2array(davisIMU(:,5));
az = table2array(davisIMU(:,4));
ar = table2array(davisIMU(:,8));
%ax = ax - mean(ax);
%ay = ay - mean(ay);

afirst = find(at>startt,1)
alast = find(at>endt,1)

at = at(afirst:alast);
ax = ax(afirst:alast);
ay = ay(afirst:alast);
az = az(afirst:alast);
ar = ar(afirst:alast);

%state estimation
ldat = [lt,lx,ly,lo];
adat = [at,ar,ay,-ax];%switched axes so that it works

[sx,sP] = lidarIMUStateEstimation(adat,ldat);
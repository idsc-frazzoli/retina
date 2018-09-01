%code by mheim
startt = 500;
endt = startt+100;
%close all

%davisIMU = csvread('davisIMU.csv');

%absolute lidar estimation
lx = table2array(gplocalization(:,3));
ly = table2array(gplocalization(:,4));
lt = table2array(gplocalization(:,1));
lq = table2array(gplocalization(:,2));
lo = table2array(gplocalization(:,5));

lo = unwrap(lo,2*pi);

lf = numel(lt)/(lt(end)-lt(1));
lxv = lx(2:end)-lx(1:end-1);
lxv = lxv*lf;
lyv = ly(2:end)-ly(1:end-1);
lyv = lyv*lf;
lxv = [lxv;0];
lyv = [lyv;0];

%compute acceleration (only used for verification)
lxa = lxv(2:end)-lxv(1:end-1);
lxa = lxa*lf;
lya = lyv(2:end)-lyv(1:end-1);
lya = lya*lf;
lxa = [lxa;0];
lya = [lya;0];


lfirst = find(lt>startt,1)
llast = find(lt>endt,1)

lx = lx(lfirst:llast);
ly = ly(lfirst:llast);
lt = lt(lfirst:llast);
lq = lq(lfirst:llast);
lo = lo(lfirst:llast);
lxv = lxv(lfirst:llast);
lyv = lyv(lfirst:llast);
lxa = lxa(lfirst:llast);
lya = lya(lfirst:llast);

plot(lx,ly);
daspect([1 1 1])


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

fs = numel(at)/(at(end)-at(1));

at = at(afirst:alast);
ax = ax(afirst:alast);
ay = ay(afirst:alast);
az = az(afirst:alast);
ar = ar(afirst:alast);

%figure
%hold on
%plot(lt,lo)
%plot(at,ar)
%hold off

sigma = 180;
sz = sigma*10;    % length of gaussFilter vector
x = linspace(-sz / 2, sz / 2, sz);
gaussFilter = exp(-x .^ 2 / (2 * sigma ^ 2));
gaussFilter = gaussFilter / sum (gaussFilter); % normalize
asx = conv (ax, gaussFilter, 'same');
asy = conv (ay, gaussFilter, 'same');
asz = conv (az, gaussFilter, 'same');


sigma = 20;
sz = sigma*10;    % length of gaussFilter vector
x = linspace(-sz / 2, sz / 2, sz);
gaussFilter = exp(-x .^ 2 / (2 * sigma ^ 2));
gaussFilter = gaussFilter / sum (gaussFilter); % normalize
lsvx = conv (lyv, gaussFilter, 'same');
lsvy = conv (lxv, gaussFilter, 'same');
lsv = (lsvx.^2+lsvy.^2).^0.5;

sigma = 20;
sz = sigma*30;    % length of gaussFilter vector
x = linspace(-sz / 2, sz / 2, sz);
gaussFilter = exp(-x .^ 2 / (2 * sigma ^ 2));
gaussFilter = gaussFilter / sum (gaussFilter); % normalize
lsax = conv (lya, gaussFilter, 'same');
lsay = conv (lxa, gaussFilter, 'same');

%figure
%hold on
%plot(at,-asx);%asx is acceleration towards the right
%plot(at,-asy);%-asy is forward acceleration
%plot(at,asz);
%hold off

figure
hold on
plot(at,asx);%asx is acceleration towards the right
plot(at,asy);%-asy is forward acceleration
hold off

figure
hold on
plot(lt,lsvy);
plot(lt,lsvx);
plot(lt,lsv);
hold off

%state estimation
ldat = [lt,lx,ly,lo];
adat = [at,ar,ay,-ax];%switched axes so that it works

%[sx,sP] = lidarIMUStateEstimation(adat,ldat);

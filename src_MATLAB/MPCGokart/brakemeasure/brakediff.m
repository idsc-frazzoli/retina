%code by mh
clear all
addpath('..')
addpath('../SystemAnalysis')
folder = strcat(getuserdir,'/Documents/ML_out/');
file = 'brakingMLData.csv';
tireradius = 0.12;
% whole table: [t x y Ksi dotx_b doty_b dotKsi  dotdotx_b dotdoty_b dotdotKsi sa sdota pcl pcr wrl wrt dotwrl dotwrr lp ltemp dotltemp imux imuy imur]
M = csvread(strcat(folder,file));
M = M(20000:49999,:);
%M = M(65000:72000,:);
temp = M(:,20);
dottemp = M(:,21);
acc = tireradius*mean(M(:,17:18),2);
acc = gaussfilter(acc,10);
acc = M(:,22);
spd = tireradius*mean(M(:,15:16),2);
bpos = -M(:,19)/100000;
brakestart = 2.5;
movestart = 2.0;
brakeend = 4.0;
bselect = bpos > brakestart & bpos < brakeend & spd>0.2;
bselect = imerode(bselect,ones(100,1));
bexpselect = bpos > movestart & bpos < brakeend & spd>0.2;
bexpselect = imerode(bexpselect,ones(100,1));
nbselect = bpos < 0.6 & bpos > 0.4;
nbselect = imerode(nbselect,ones(100,1));
dottemp(1:1000)=0;
dottemp(end-1000:end)=0;
t = M(:,1);
close all
subplot(2,1,1)
title('brake position')
hold on
xlabel('Time [s]')
ylabel('position')
plot(t,-M(:,19))
hold off

pbrake = polyfit(bpos(bselect), acc(bselect),2);

accoffset = polyval(pbrake, brakestart);
pnormbrake = polyfit(bpos(bselect)-brakestart, -acc(bselect)+accoffset,2);
xb = 0:0.01:brakeend-brakestart;
yb = polyval(pnormbrake,xb);
subplot(2,1,2)
title('effect of brake')
hold on
xlabel('Brakingposition [cm]')
ylabel('braking effect [m/s²]')
scdatx = bpos(bexpselect)-brakestart;
scdaty = -acc(bexpselect)+accoffset;
scatter(scdatx(1:800), scdaty(1:800), 'b');
scatter(scdatx(801:end), scdaty(801:end), 'r');
%scatter(bpos-brakestart, -acc+accoffset);
plot(xb,yb);
hold off

print('brakes','-dpng','-r600')

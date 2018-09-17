powersteer = csvread('steercalibration.csv');

[n,~] = size(powersteer);

%get nearest point
xq = min(powersteer(:,3)):0.01:max(powersteer(:,3));
tabledist = interp1(powersteer(:,3),powersteer(:,2),xq,'spline');
tablemeas = interp1(powersteer(:,3),powersteer(:,3),xq,'spline');


plot(xq,tabledist);
ylabel('laser distance [m]')
xlabel('table distance [m]')

[d,i] = min(tabledist);
offset = tablemeas(i)
bw = 0.22;


angles = zeros(n,1);

[~,izero] = min(abs(powersteer(:,1))); 
offset = powersteer(izero,3);

for i=1:n
    x = powersteer(i,3)-offset;
    x = x/100;
    angles(i)=getAlpha(x,d,bw);   
end

close all

%polyfit
pleft = polyfit(powersteer(:,1),angles,3);
pright = [1,-1,1,-1].*pleft;
pmean = [1,0,1,0].*pleft;
limit = max(abs(powersteer(:,1)));
x = -limit:0.001:limit;
yl = polyval(pleft,x);
yr = polyval(pright,x);
ym = polyval(pmean,x);

%nearest ackermann
nn = numel(yl);
yla = zeros(nn,1);
yra = zeros(nn,1);
yma = zeros(nn,1);
for i = 1:nn
    [abeta1,abeta2, abetam]=nearestAckermann(yl(i),yr(i),0.94,1.27);
    yla(i) = abeta1;
    yra(i) = abeta2;
    yma(i) = abetam;
end

figure
hold on
%yyaxis left
scatter(powersteer(:,1),angles)
plot(x,yl);
plot(x,yr);
%plot(x,ym);
xlabel('steering wheel encoder angle [rad]')
ylabel('wheel steering angle [rad]')
legend('raw data left','cubic left','cubic right (mirrored)')

figure
hold on
%yyaxis left
%scatter(powersteer(:,1),angles)
plot(x,yl,'r');
plot(x,yr,'b');
plot(x,yla,'r--');
plot(x,yra,'b--');
plot(x,yma,'k--');
%plot(x,ym);
xlabel('steering wheel encoder angle [rad]')
ylabel('wheel steering angle [rad]')

legend('cubic left','cubic right','nearest ackermann left', 'nearest ackermann right', 'nearest ackermann center')

%yyaxis right
%[xt,yt] = computeTurningPoint(yl,yr,0.94);
%R =(xt.^2+yt.^2).^0.5;
%plot(x,R);
%ab = (yt+1.27)./R;

%ab=min(max(ab,-5),5);
%plot(x,ab);
%ylabel('longitudonal abberation of turning point [m]')
%ylabel('longitudonal abberation of turning point [m]')


addpath('..')  
clear
close all
userdir = getuserdir

folders = {};
targetfiles = {};
if(0)
    folders{end+1} = '/retina_out/20190125T105720/';
end
if(0)
    folders{end+1} = '/retina_out/20190125T134537/';
end
if(0)
    folders{end+1} = '/retina_out/20190128T141006/';
end
if(1)
    folders{end+1} = '/retina_out/sysidlog/';
end
if(0)
    folders{end+1} = '/retina_out/motorSysID/';
end
if(1)
    folders{end+1} = '/retina_out/exhaustivemotortest.lcm/';
end
N = numel(folders);
tic;
for i = 1:N
    folders{i}=strcat(userdir,folders{i});
    SysID = loadSystIDData(folders{i});
end
toc;

% [t,tms,vx,vy,vr,ax,ay,s,pl,pr,pal,par,vwx,px,py,po,lm]

l = 1.19;
l1 = 0.73;
l2 = l-l1;

SysID=SysID(30000:end,:);
%SysID=SysID(54000:55000,:);
t = SysID(:,1);
s = SysID(:,8);
vwx = SysID(:,13);
lm = SysID(:,17);
pl = SysID(:,9);
dpl = getDerivation(pl, 300, 0.001);
absdpl = abs(dpl);
hold on
plot(t,pl);
yyaxis right
plot(t,vwx);
hold off
steerSel = abs(s)<0.1;
powSel = absdpl<10;

%SysID=SysID(steerSel,:);
%SysID=SysID(steerSel&powSel,:);
t = SysID(:,1);
dt = (t(101)-t(1))/100;
tms = SysID(:,2);
vx = SysID(:,3);
vy = SysID(:,4);
vr = SysID(:,5);
ar = getDerivation(vr, 60, dt);
ax = SysID(:,6);
ay = SysID(:,7);
sax = gaussfilter(ax,30);
say = gaussfilter(ay,30);
scay = say+l2*ar;
cvy = vy+l2*vr;
s = SysID(:,8);
beta = -0.58.*s.*s.*s+0.93*s;
kinrot = vx/l.*tan(beta);
pl = SysID(:,9);
pr = SysID(:,10);
pal = SysID(:,11);
par = SysID(:,12);
ptv = (par-pal)/2;
vwx = SysID(:,13);

figure
title('ay')
hold on
plot(t,ay)
plot(t,say)
hold off

figure
title('acc comparison')
hold on
yyaxis left
plot(t,sax,'DisplayName', 'a-X')
yyaxis right
plot(t,mean([pl,pr],2),'DisplayName', 'power a-X')
legend show
hold off

figure
title('torque vectoring')
hold on
plot(t,vr,'DisplayName', 'rot')
plot(t,ptv,'DisplayName', 'tv')
%plot(t,-beta*10,'DisplayName', 'beta')
plot(t,kinrot,'DisplayName', 'kin rot')
legend show
hold off

figure
title('velocity')
hold on
plot(t,vy,'DisplayName', 'v-Y')
plot(t,vx,'DisplayName', 'v-X')
plot(t,vwx,'DisplayName', 'wheelspeed-X')
legend show
hold off

figure
title('rotationalAcceleration')
hold on
plot(t,vr,'DisplayName', 'rot')
plot(t,ar,'DisplayName', 'rotacc')
legend show
hold off

%%


meanpower = mean([pl,pr],2);
figure
hold on
yyaxis left
xlabel('Time')
ylabel('Power')

plot(t,meanpower);
yyaxis right
ylabel('Forward Acceleration [m/s^2]')

tc = 99.8;
bc = 0.2;

%plot(t,sax);
meanRateAcceleration = sax;
plot(t,meanRateAcceleration);

legend('power','forward acceleration [m/s^2]')

%meanrate
meanRate = vx;
bottom = prctile(meanRate,bc);
top = prctile(meanRate,tc);
%meanRate = max(min(meanRate,top),bottom)/8;%clamp


X = [meanRate,meanpower];
Y = meanRateAcceleration;

%generateNetwork();
if(true)
figure
hold on
h = scatter3(meanRate,meanpower,meanRateAcceleration);
xlabel('forward speed [m/s]')
ylabel('power [A]')
zlabel('forwardacceleration [m/s^2]')
end

minpower = min(meanpower);
maxpower = max(meanpower);
powerstep = (maxpower-minpower)/100;

if(false)
    [XX,YY] = meshgrid(-5:0.2:5,minpower:powerstep:maxpower);
    [m,n] = size(XX);
    InputNN = [XX(:),YY(:)];
    Znn = powerfunction(InputNN')';
    Znn = reshape(Znn,[m,n]);
    surf(XX,YY,Znn);

    %2D-polyfit
    sf = fit(X,Y,'poly44');
    figure
    plot(sf)
    xlabel('forward speed [m/s]')
    ylabel('power [A]')
    zlabel('forwardacceleration [m/s^2]')
end

%hand crafted fitted function 
%concatenate data
fullD = [t,meanRate, meanpower, meanRateAcceleration];

%filter out jump data
%fullD = deleteJumpData(fullD,4,100);

%speed threshold after which the speed is fully applied (hand tuned)
st = 0.5;
%get all positive values
posD = fullD(fullD(:,2)>st,:);
negD = fullD(fullD(:,2)<-st,:);
%point mirror and combine
combD = [posD;negD.*[1,-1,-1,-1]];

%split into positive and negative power
powerthreshold =100;
combPosPowerD = combD(combD(:,3)>powerthreshold,:);
combNegPowerD = combD(combD(:,3)<-powerthreshold,:);

if(true)
    %fit plane
    sfpos = fit(combPosPowerD(:,2:3),combPosPowerD(:,4),'poly33');
    figure
    hold on
    scatter3(combPosPowerD(:,2),combPosPowerD(:,3),combPosPowerD(:,4));
    plot(sfpos)
    xlabel('forward speed [m/s]')
    ylabel('power [A]')
    zlabel('forwardacceleration [m/s^2]')
    title('raw data with speed > 0.5 m/s and positive power forward+backward combined')
end

if(true)
    %fit plane
    sfneg = fit(combNegPowerD(:,2:3),combNegPowerD(:,4),'poly33');
    figure
    hold on
    scatter3(combNegPowerD(:,2),combNegPowerD(:,3),combNegPowerD(:,4));
    plot(sfneg)
    xlabel('forward speed [m/s]')
    ylabel('power [Arms]')
    zlabel('forward acceleration [m/s^2]')
    title('raw data with speed > 0.5 m/s and negative power forward+backward combined')
end

if(true)
    [XX,YY] = meshgrid(-5:0.2:5,minpower:powerstep:maxpower);
    [m,n]=size(XX);
    ZZhc = zeros(m,n);
    for ix =1:m
        for iy=1:n
            ZZhc(ix,iy)=hcpowerfunction(XX(ix,iy),YY(ix,iy),sfpos,sfneg, st, powerthreshold);
        end
    end
    figure
    surf(XX,YY,ZZhc);
        xlabel('forward speed [m/s]')
    ylabel('power [Arms]')
    zlabel('forwardacceleration [m/s^2]')
    title('hand crafted model consisting of 4 fitted quadrants')
end

%compute max 

[cp0,cp1,cp2,cp3] = getSlice(sfpos,maxpower);
[cn0,cn1,cn2,cn3] = getSlice(sfneg,-maxpower);

x = -9:0.01:9;
y = zeros(numel(x),1);
ys = zeros(numel(x),1);
for i = 1:numel(x)
   y(i)=getMaxAcc(x(i)); 
   ys(i)=getSmoothMaxAcc(x(i)); 
end
figure
title('hardcoded min max')
hold on
%plot(x,y)
%plot(-x,-y)
plot(x,ys)
plot(-x,-ys)
hold off
xlabel('speed [m/s]');
ylabel('max acc [m/s^2]');
clear all
addpath('..')  
userdir = getuserdir
MLTargetFolder = strcat(userdir,'/Documents/ML_out/');
file = 'fab.csv';
M = csvread(strcat(MLTargetFolder,file));
t = M(:,1);
close all
if(false)
figure
plot(M(:,1),M(:,5));
end

meanpower = mean(M(:,13:14)')';
figure
hold on
yyaxis left
xlabel('Time')
ylabel('Power')

plot(M(:,1),meanpower);
yyaxis right
ylabel('Forward Acceleration [m/s^2]')

tc = 99.8;
bc = 0.2;

plot(M(:,1),M(:,8));
meanRateAcceleration = mean(M(:,17:18),2);
bottom = prctile(meanRateAcceleration,bc);
top = prctile(meanRateAcceleration,tc);
absmax = max(top,-bottom);
top = absmax;
bottom = -absmax;
meanRateAcceleration = max(min(meanRateAcceleration,top),bottom)/8;%clamp
plot(M(:,1),meanRateAcceleration);

legend('power','forward acceleration [lidar]','forward acceleration [rimo]')

%meanrate
meanRate = mean(M(:,15:16),2);
bottom = prctile(meanRate,bc);
top = prctile(meanRate,tc);
meanRate = max(min(meanRate,top),bottom)/8;%clamp


X = [meanRate,meanpower];
Y = meanRateAcceleration;

%generateNetwork();
if(false)
figure
hold on
scatter3(meanRate,meanpower,meanRateAcceleration);
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

x = -5:0.01:5;
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
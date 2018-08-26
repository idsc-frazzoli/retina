%code by mheim
function [st,sx,sP] = lidarIMUStateEstimation(adat,ldat)
x = zeros(10,1);
%first state estimation
x(1:3)=ldat(1,2:4);
x(4:6)=(ldat(30,2:4)-ldat(1,2:4))/(ldat(30,1)-ldat(1,1));
dim = numel(x);
P = eye(dim)*10;
P(9,9)=1000;
P(10,10)=1000;
IMUa = 100;
IMUr = 30;
DRIFT=0;
Q = diag([0,0,0,0,0, IMUr, IMUa, IMUa,DRIFT,DRIFT]);
lt = ldat(:,1);
at = adat(:,1);
ldat = ldat(:,2:4);
adat = adat(:,2:4);

%debugging
mwdebug = [];

%aggregate accelerationdata
aagg = 10;

%use higher frequency for data
lR = estimateVar(ldat);
aR = estimateVar(adat)*100000;


[~,lN]=size(ldat);
[~,lA]=size(adat);
totalN = lN+lA;

currentt = min(lt(1),at(1));
acount = aagg;
lcount = 3;
tcount = 1;
maxt = max(at)-0.1;
thist = zeros(totalN,1);
xhist = zeros(totalN,dim);
Phist = zeros(totalN,dim,dim);
Fhist = zeros(totalN,dim,dim);
Qhist = zeros(totalN,dim,dim);
while(currentt < maxt)
    currentt
    maxt
    %if currentt>530
    %    lR=eye(3)*1000000;
    %end
    if(lt(lcount)<at(acount))
        %update with lidar
        dt = lt(lcount)-currentt;
        currentt = lt(lcount);
        dmt = lt(lcount)-lt(lcount-1);
        [x,P]=lidarMeasure(x,P,dt,dmt,ldat(lcount,:)',ldat(lcount-1,:)',ldat(lcount-2,:)',lR,Q);
        lcount = lcount+1;
    else
        %update with IMU
        dt = at(acount)-currentt;
        currentt = at(acount);
        m = mean(adat(acount-aagg+1:acount,:));
        [x,P]=IMUMeasure(x,P,dt,m', aR,Q);
        acount = acount+aagg;
        
        %debugging
        %Rot = @(theta)[1,0,0;0,cos(theta),-sin(theta);0,sin(theta),cos(theta)];
        %mw = Rot(x(3))*m';
        %mwdebug = [mwdebug;currentt,mw'];
    end
    thist(tcount)=currentt;
    xhist(tcount,:) = x;
    Phist(tcount,:,:) = P;
    Fhist(tcount,:,:) = getEvolution(x)*dt+eye(dim);
    Qhist(tcount,:,:) = Q*dt;
    tcount = tcount + 1;
end

thist = thist(1:tcount-1);
xhist = xhist(1:tcount-1,:);
Phist = Phist(1:tcount-1,:,:);
Fhist = Fhist(1:tcount-1,:,:);
Qhist = Qhist(1:tcount-1,:,:);

    %apply smoothing
    [sx,sP] = RTSSmoother(xhist,Phist,Qhist,Fhist);
    %sx = xhist;
    %sP = Phist;
    st = thist;
    show = 1;
    if(show)
        close all


        %figure
        %inputs
        %hold on
        %plot(at,adat(:,1));
        %plot(thist,sx(:,7));
        %plot(thist,xhist(:,7));
        %plot(at,ax);
        %hold off

        figure
        hold on
        plot(ldat(:,1),ldat(:,2));
        plot(sx(:,1),sx(:,2));
        daspect([1 1 1])
        hold off

        figure
        hold on
        plot(thist,sx(:,3))
        plot(lt, ldat(:,3))
        hold off

        %test acceleration
        figure
        hold on
        plot(thist,sx(:,7));
        plot(thist,sx(:,8));
        hold off
        
        %acceleration sensor drift
        figure
        hold on
        plot(thist,sx(:,9));
        plot(thist,sx(:,10));
        hold off

        sigma = 20;
        sz = sigma*30;    % length of gaussFilter vector
        x = linspace(-sz / 2, sz / 2, sz);
        gaussFilter = exp(-x .^ 2 / (2 * sigma ^ 2));
        gaussFilter = gaussFilter / sum (gaussFilter); % normalize
        %mwdebug(:,3) = conv (mwdebug(:,3), gaussFilter, 'same');
        %mwdebug(:,4) = conv (mwdebug(:,4), gaussFilter, 'same');

        %rdat = rotate([sx(:,7),sx(:,8)],-sx(:,3))';
        %figure
        %hold on
        %plot(thist,rdat(:,1));
        %plot(thist,rdat(:,2));
        %hold off
        
        %figure
        %hold on
        %plot(mwdebug(:,1),mwdebug(:,3));
        %plot(mwdebug(:,1),mwdebug(:,4));
        %hold off
    end
end


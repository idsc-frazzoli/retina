function [sx,sP] = lidarIMUStateEstimation(adat,ldat)
x = zeros(8,1);
dim = numel(x);
P = eye(dim)*1;
top = 0.01;
middle = 0.1;
bottom = 1.5;
Q = diag([top,top, middle,middle, middle, bottom,bottom, bottom]);
lt = ldat(:,1);
at = adat(:,1);
ldat = ldat(:,2:4);
adat = adat(:,2:4);

%use higher frequency for data
lR = estimateVar(ldat);
aR = estimateVar(adat);


[~,lN]=size(ldat);
[~,lA]=size(adat);
totalN = lN+lA;

currentt = 0;
acount = 1;
lcount = 3;
tcount = 1;
maxt = 15;
thist = zeros(totalN,1);
xhist = zeros(totalN,dim);
Phist = zeros(totalN,dim,dim);
Fhist = zeros(totalN,dim,dim);
Qhist = zeros(totalN,dim,dim);
while(currentt < maxt)
    if(lt(lcount)<at(acount))
        %update with lidar
        dt = lt(lcount)-currentt;
        currentt = lt(lcount);
        dmt = lt(lcount)-lt(lcount-1);
        [x,P]=lidarMeasure(x,P,dt,dmt,ldat(:,lcount),ldat(:,lcount-1),ldat(:,lcount-2),lR,Q);
        lcount = lcount+1;
    else
        %update with IMU
        dt = at(acount)-currentt;
        currentt = at(acount);
        [x,P]=IMUMeasure(x,P,dt,adat(:,acount), aR,Q);
        acount = acount+1;
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

close all




%apply smoothing
[sx,sP] = RTSSmoother(xhist,Phist,Qhist,Fhist);

figure
%inputs
hold on
plot(at,adat(2,:));
plot(thist,sx(:,7));
plot(thist,xhist(:,7));
%plot(at,ax);
hold off

figure
hold on
plot(lx,ly);
%plot(xhist(:,1),xhist(:,2));
plot(sx(:,1),sx(:,2));
daspect([1 1 1])
hold off
end


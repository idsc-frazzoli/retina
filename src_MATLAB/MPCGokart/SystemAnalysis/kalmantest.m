%code by mheim

x = zeros(8,1);
x = [1,0,0,0,2,0,0,0]';
dim = numel(x);
P = eye(dim)*1;
IMUa = 1;
IMUr = 1;
Q = diag([0,0,0,0,0, IMUr, IMUa, IMUa]);
lt = 0:0.05:20;
lx = 2*cos(lt);
ly = 2*sin(lt);
ltheta = lt*0;
ldat = [lx;ly;ltheta];
%add noise
randdat = normrnd(0,0.02,size(ldat));
ldat = ldat+randdat;
%plot(lx,ly)
%daspect([1 1 1])
%at = 0:0.001:10;
at = 0:0.001:20;
ax = -2*cos(at);
ay = -2*sin(at);
vtheta = at*0;
adat = [vtheta;ax;ay];
randdat = normrnd(0,0.01,size(adat));
adat = adat+randdat;
%plot(ax,ay);

%get estimation of variance of data
fl = lt(2)-lt(1);
wpass = fl*0.1;
hpldat = highpass(ldat',wpass,fl)';

%hold on
%plot(lt,ldat(1,:))
%plot(lt,hpldat(1,:))
%plot(lt,ldat(1,:)-hpldat(1,:))
%hold off

%use higher frequency for data
lR = estimateVar(ldat)*1000000000;
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
sx = xhist;
sP = Phist;

figure
%inputs
hold on
plot(at,adat(2,:));
plot(thist,sx(:,7));
plot(thist,xhist(:,7));
%plot(at,ax);
hold off

%figure
%inputs
%hold on
%plot(thist,xhist(:,1));
%plot(lt,lx);
%hold off

figure
hold on
plot(lx,ly);
%plot(ldat(1,:), ldat(2,:))
%plot(xhist(:,1),xhist(:,2));
plot(sx(:,1),sx(:,2));
daspect([1 1 1])
hold off

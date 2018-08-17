x = zeros(8,1);
P = eye(8)*1;
Q = eye(8)*0.001;%leave it like that at the moment
Q(7,7)=0.2;
Q(8,8)=0.2;
lt = 0:0.05:20;
lx = 2*cos(lt);
ly = 2*sin(lt);
ltheta = lt*0;
ldat = [lx;ly;ltheta];
%add noise
randdat = normrnd(0,0.2,size(ldat));
ldat = ldat+randdat;
%plot(lx,ly)
%daspect([1 1 1])
%at = 0:0.001:10;
at = 0:0.001:20;
ax = -2*cos(at);
ay = -2*sin(at);
vtheta = at*0;
adat = [vtheta;ax;ay];
randdat = normrnd(0,0.1,size(adat));
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
lR = estimateVar(ldat);
aR = estimateVar(adat);



currentt = 0;
acount = 1;
lcount = 3;
maxt = 15;
xhist = [];
while(currentt < maxt)
    if(lt(lcount)<at(acount))
        %update with lidar
        dt = lt(lcount)-currentt;
        currentt = lt(lcount);
        dmt = lt(lcount)-lt(lcount-1);
        [x,P]=lidarMeasure(x,P,dt,dmt,ldat(:,lcount),ldat(:,lcount-1),ldat(:,lcount-2),lR,Q);
        lcount = lcount+1;
        entry = [currentt;x];
        xhist = [xhist,entry];
    else
        %update with IMU
        dt = at(acount)-currentt;
        currentt = at(acount);
        [x,P]=IMUMeasure(x,P,dt,adat(:,acount), aR,Q);
        acount = acount+1;
    end
end
close all

figure
%inputs
hold on
plot(at,adat(2,:));
plot(xhist(1,:),xhist(8,:));
plot(at,ax);
hold off

figure
%inputs
hold on
plot(xhist(1,:),xhist(2,:));
plot(lt,lx);
hold off

figure
hold on
plot(xhist(2,:),xhist(3,:));
plot(lx,ly);
daspect([1 1 1])
hold off
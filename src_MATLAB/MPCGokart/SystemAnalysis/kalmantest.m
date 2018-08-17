x = zeros(7,1);
P = eye(7)*100000;
Q = eye(7);%leave it like that at the moment
lt = 0:0.05:2;
lx = cos(lt);
ly = sin(lt);
ltheta = lt*0;
ldat = [lx;ly;ltheta];
lR = eye(3)*0.2;
%plot(lx,ly)
%daspect([1 1 1])
at = 0:0.001:2;
ax = -cos(at);
ay = -sin(at);
atheta = at*0;
adat = [ax;ay;atheta];
aR = eye(3)*10;
%plot(ax,ay);

currentt = 0;
acount = 1;
lcount = 3;
maxt = 1;
xhist = [];
while(currentt < maxt)
    if(lt(lcount)<at(acount))
        %update with lidar
        dt = lt(lcount)-currentt;
        currentt = lt(acount);
        [x,P]=lidarMeasure(x,P,dt,adat(:,lcount),ldat(:,lcount-1),ldat(:,lcount-2),lR,Q);
        xhist = [xhist,lt(acount);x];
        lcount = lcount+1;
    else
        %update with IMU
        dt = at(acount)-currentt;
        currentt = at(acount);
        [x,P]=IMUMeasure(x,P,dt,adat(:,lcount), aR,Q);
        xhist = [xhist,at(acount);x];
        acount = acount+1;
    end
end
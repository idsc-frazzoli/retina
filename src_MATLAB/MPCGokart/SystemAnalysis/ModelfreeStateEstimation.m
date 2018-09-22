function M = ModelfreeStateEstimation(folder)
    tic;
    %code by mheim
    %folder = 'retina_out/20180820T1438522.lcm/';
    gplocalization = csvread(strcat(folder,'gplocalization.csv'));
    %davisIMU = csvread(strcat(folder,'davisIMU.csv'));

    %absolute lidar estimation
    lx = gplocalization(:,3);
    ly = gplocalization(:,4);
    lt = gplocalization(:,1);
    lq = gplocalization(:,2);
    lo = gplocalization(:,5);
    lo = unwrap(lo,2*pi);

    %accelerometer data
    %at = davisIMU(:,1);
    %ax = davisIMU(:,3);
    %ay = davisIMU(:,5);
    %az = davisIMU(:,4);
    %ar = davisIMU(:,8);
    %ax = ax - mean(ax);
    %ay = ay - mean(ay);

    %startt = max(min(lt),min(at))+0.1;
    %endt = min(max(lt),max(at))-1;
    startt = min(lt)+0.1;
    endt = max(lt)-0.1;

    lfirst = find(lt>startt,1);
    llast = find(lt>endt,1);


    lx = lx(lfirst:llast);
    ly = ly(lfirst:llast);
    lt = lt(lfirst:llast);
    lq = lq(lfirst:llast);
    lo = lo(lfirst:llast);

    %afirst = find(at>startt,1)
    %alast = find(at>endt,1)

    %at = at(afirst:alast);
    %ax = ax(afirst:alast);
    %ay = ay(afirst:alast);
    %az = az(afirst:alast);
    %ar = ar(afirst:alast);

    %state estimation
    ldat = [lt,lx,ly,lo];
    %adat = [at,ar,ay,-ax];%switched axes so that it works
    adat = zeros(0,4);

    [st,sx,sP] = lidarIMUStateEstimation(adat,ldat);

    sst = min(st):0.01:max(st);
    ssx = interp1(st, sx,sst);
    M = [sst',ssx];
    t = toc;
    disp(strcat("finished ",folder, " in ",num2str(t)," seconds"));
end
%plot(ssx(1,:),ssx(2,:))
%csvwrite(strcat(folder,'RTSData.csv'), M);

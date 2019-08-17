function plotAcceleration(VELROTZ,BETA,AB,TV,param)
    figure
    hold on
    daspect([1 1 1])
    xlabel("velocity-X [m/s]");
    ylabel("velocity-Y [m/s]");
    title(join(['rot: ',num2str(VELROTZ),'[1/s] beta: ',num2str(BETA),' ab:', num2str(AB),' [m/s^2] tvec:', num2str(TV)]))
    [velx,vely] = meshgrid(0:0.5:10,-10:0.5:10);
    [m,n]=size(velx);
    accx = zeros(m,n);
    accy = accx;
    zer = accx;
    rotacc = accx;
    for ix = 1:m
       for iy = 1:n 
           [ACCX,ACCY,ACCROTZ] = modelDx(velx(ix,iy),vely(ix,iy),VELROTZ,BETA,AB,TV, param);
           accx(ix,iy)=ACCX;
           accy(ix,iy)=ACCY;
           rotacc(ix,iy)=ACCROTZ;
       end
    end
    lfac = 0.05;
    quiver(velx,vely,lfac*accx,lfac*accy,'AutoScale','off','DisplayName', 'acc [m/s^2]');
    rfac = 1;
    rotaccd = rotacc*rfac;
    quiver(velx+0.1,vely-rotaccd/2,zer,rotaccd, 'r','AutoScale','off', 'DisplayName', 'rotacc [1/s^2]');
    quiver(velx-0.1,vely+rotaccd/2,zer,-rotaccd, 'r','AutoScale','off', 'DisplayName', 'rotacc [1/s^2]');
    view([90 -90])
    set(gca, 'YDir','reverse')
    legend show
    hold off
end


function [x,y,o,vx,vy,vo] = simulationTest(u,dt)

    B = 6;
    C = 1.7;
    D = 0.7*9.81;
    Cf = 0.15;
    B1 = B;
    B2 = B;
    C1 = C;
    C2 = C;
    D1 = 1*D;
    D2 = 0.8*D;
    maxA = D*0.9;
    param = [B1,C1,D1,B2,C2,D2,Cf,maxA];


    rotmat = @(beta)[cos(beta),sin(beta);-sin(beta),cos(beta)];
    %u=[BETA,AB,TV]
    [m,~]=size(u);
    x = zeros(m,1);
    y = zeros(m,1);
    o = zeros(m,1);
    vx = zeros(m,1);
    vx(1)=0;
    vy = zeros(m,1);
    vo = zeros(m,1);
    figure(1)
    clf
    daspect([1 1 1])
    hold on
    set(gca,'visible','off')
    for i=2:m
        [ACCX,ACCY,ACCROTZ,frontabcorr] = modelDx(vx(i-1),vy(i-1),vo(i-1),u(i,1),u(i,2),u(i,3), param);
        vx(i) = vx(i-1)+dt*ACCX;
        vy(i) = vy(i-1)+dt*ACCY;
        vo(i) = vo(i-1)+dt*ACCROTZ;
        R = rotmat(o(i-1));
        V = R*[vx(i);vy(i)];
        x(i) = x(i-1)+dt*V(1);
        y(i) = y(i-1)+dt*V(2);
        o(i) = o(i-1)+dt*vo(i);
        %draw gokart
        gklx = [-0.2,1.2,1.2,-0.2,-0.2];
        gkly = [-0.5,-0.5,0.5,0.5,-0.5];
        gklp = [gklx;gkly];
        rgklp = [x(i);y(i)]+R*gklp;
        fill(rgklp(1,:),rgklp(2,:),'b');
    end
    hold off
end


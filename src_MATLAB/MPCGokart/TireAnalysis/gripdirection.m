magic = @(s,B,C,D)D.*sin(C.*atan(B.*s));

B = 0.74;
C = 1.5;
D = 1.1;

xs = 0:0.01:10;
close all
figure
hold on
tB = [B,0.5,2];
tC = [C,1.1,1.8];
tD = [D,1.0,1.3];
plot(xs,magic(xs,tB(1),tC(1),tD(1)));
plot(xs,magic(xs,tB(2),tC(2),tD(2)));
plot(xs,magic(xs,tB(3),tC(3),tD(3)));
%title('friction coefficient with pure slip (only slip in one principal direction)')
xlabel('slip coefficient')
ylabel('friction coefficient')
legend(strcat(num2str(tB(1),'B=%0.1f, '),num2str(tC(1),'C=%0.1f, '),num2str(tD(1),'D=%0.1f')),...
    strcat(num2str(tB(2),'B=%0.1f, '),num2str(tC(2),'C=%0.1f, '),num2str(tD(2),'D=%0.1f')),...
    strcat(num2str(tB(3),'B=%0.1f, '),num2str(tC(3),'C=%0.1f, '),num2str(tD(3),'D=%0.1f')))


wheelSpeeds = [0,0.1,1,5];
n = numel(wheelSpeeds);
mm = 2;
nn = 2;
figure
for i = 1:n
    subplot(mm,nn,i)
    hold on
    %wheelspeed in m/s
    wheelSpeed = wheelSpeeds(i);

    %meshgrid
    gsize = 10;
    gres = 0.11;
    qres = 100;
    velx = -gsize:gres:gsize;
    vely = -gsize:gres:gsize;
    [VELX,VELY] = meshgrid(velx,vely);

    SLIPX = (VELX-wheelSpeed)./(wheelSpeed+0.001);
    SLIPY = (1+SLIPX).*VELY./(VELX+0.001);
    SLIP = (SLIPX.^2+SLIPY.^2).^(0.5);
    FCOEFF = magic(SLIP,B,C,D);
    FCOEFFX = -SLIPX./SLIP.*FCOEFF;
    FCOEFFY = -SLIPY./SLIP.*FCOEFF;

    cont = 1:-0.01:0;
    same = ones(size(cont));
    map = [same',cont',cont'];
    colormap(parula);
    xlabel('wheel velocity in X-direction [m/s]')
    ylabel('wheel velocity in Y-direction [m/s]')
    imagesc([-gsize,gsize],[-gsize,gsize],FCOEFF,[0,D]);
    quiver(VELX(1:qres:end),VELY(1:qres:end),FCOEFFX(1:qres:end),FCOEFFY(1:qres:end))
    colorbar
    %legend('friction coefficient direction')
    %title(num2str(wheelSpeed,'friction coefficient vector at contact patch\nat %0.1f m/s wheelspeed.'))
    title(num2str(wheelSpeed,'\nwheel speed = %0.1f m/s'))
    xlim([-gsize gsize])
    ylim([-gsize gsize])
    %contourf(VELX,VELY,FCOEFF)
    daspect([1 1 1])
end
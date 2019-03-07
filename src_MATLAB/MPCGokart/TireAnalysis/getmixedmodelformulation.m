clear all;
close all;
B = 2;
C = 1.5;
D = 0.7*9.81;
slx = @(VELY,VELX, wheelSpeed)(VELX-wheelSpeed)./(wheelSpeed+0.001);
sly = @(VELY,VELX, wheelSpeed)(1+slx(VELY,VELX, wheelSpeed)).*VELY./(VELX+0.001);
slip = @(VELY,VELX,wheelSpeed)(slx(VELY,VELX,wheelSpeed).^2+sly(VELY,VELX, wheelSpeed).^2).^(0.5)+0.001;
magic = @(s,B,C,D)D.*sin(C.*atan(B.*s));
coeff = @(VELY,VELX, wheelSpeed)magic(slip(VELY,VELX, wheelSpeed),B,C,D);
accx = @(VELY,VELX, wheelSpeed) -slx(VELY,VELX, wheelSpeed)./slip(VELY,VELX, wheelSpeed).*coeff(VELY,VELX, wheelSpeed);
accy = @(VELY,VELX, wheelSpeed) -sly(VELY,VELX, wheelSpeed)./slip(VELY,VELX, wheelSpeed).*coeff(VELY,VELX, wheelSpeed);

[velx,vely] = meshgrid(-10:1:10,-10:1:10);
u = accx(vely, velx, 1);
v = accy(vely, velx, 1);
figure
hold on
xlabel("velocity-X [m/s]");
ylabel("velocity-Y [m/s]");
quiver(velx,vely,u,v);
hold off

%get (vely,accx)->accy
fun = @(VELY,VELX, wheelSpeed, taccx)(accx(VELY,VELX,wheelSpeed)-taccx)^2;
swheelspeed = @(vely,velx,taccx)fminsearch(@(wheelspeed)fun(vely,velx,wheelspeed,taccx),velx);
saccy = @(VELY,VELX,taccx)accy(VELY,VELX,swheelspeed(VELY,VELX,taccx));

%compute curves at different acceleration levels
standartvelx = 3;
figure
hold on
for ia = -6:0.5:0
    vely = -3:0.01:3;
    accy = [];
    for ively = -3:0.01:3
        accy = [accy,saccy(ively,standartvelx,ia)];
    end
    xlabel("velocity-Y [m/s]");
    ylabel("acc-Y [m/s^2]");
    plot(vely,accy, 'DisplayName',strcat(num2str(ia),'[m/s^2] acc-X'));
end
legend show
hold off

%simplified model
maxA = D;
capfactor = @(taccx)(1-satfun((taccx/D)^2))^(1/2);
simpleslip = @(VELY,VELX,taccx)-(1/capfactor(taccx))*VELY/(VELX+0.001);
simplediraccy = @(VELY,VELX,taccx)magic(simpleslip(VELY,VELX,taccx),B,C,D);
simpleaccy = @(VELY,VELX,taccx)capfactor(taccx)*simplediraccy(VELY,VELX,taccx);
acclim = @(VELY,VELX, taccx)(VELX^2+VELY^2)*taccx^2-VELX^2*maxA^2;
simplefaccy = @(VELY,VELX)magic(-VELY/(VELX+0.001),B,C,D);

%compute curves at different acceleration levels
standartvelx = 3;
figure
title('approximation');
hold on
for ia = -6:0.5:0
    vely = -3:0.01:3;
    accy = [];
    for ively = -3:0.01:3
        al = acclim(ively,standartvelx,ia);
        if(al<0)
            newAccy = simpleaccy(ively,standartvelx,ia);
        else
            newAccy = 0;
        end
        accy = [accy,newAccy];
    end
    xlabel("velocity-Y [m/s]");
    ylabel("acc-Y [m/s^2]");
    plot(vely,accy, 'DisplayName',strcat(num2str(ia),'[m/s^2] acc-X'));
end
legend show
hold off
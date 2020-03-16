%code by mheim
figure;
global index

l = 1.19;
l1 = 0.73;
l2 = l-l1;

% variables history = [t,ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
%start later in history
hstart = 1;
hend = 2000;
spacing = 20;
spc = 1;
spc2= 1;
lhistory = history(hstart:end,:);
lhistory2 = history2(hstart:end,:);
m = 2;
n = 3;
subplot(m,n,1)
hold on
%plot(ttpos(:,2),ttpos(:,3), 'Color', [0.8 0.8 0.8])
%plot(history(:,5), history(:,6), 'b')
%stairs(refEXT(:,1), refEXT(:,3), 'b')
daspect([1 1 1])
title('reference trajectory vs actual');
%legend('reference', 'MPC controlled')

%plot acceleration and deceleration in colors
po = lhistory(:,[index.x+1,index.y+1]);
offset = 0.2*gokartforward(lhistory(:,index.theta+1));
forward = gokartforward(lhistory(:,index.theta+1));
p = offset + po;
acc = lhistory(:,index.ab+1);
maxacc = max(abs(acc));
[nu,~]=size(p);
for kk=1:nu-1
    next = kk+1;
    x = [p(kk,1),p(next,1)];
   y = [p(kk,2),p(next,2)];
   vc = acc(kk)/maxacc;
   line(x,y,'Color',[0.5-0.5*vc,0.5+0.5*vc,0]); %%TODO MH not working
   %draw angle
   spc = spc+1;
   if(spc>=spacing)
       spc = 1;
       back = p(kk,:) - forward(kk,:)*l2;
       front = p(kk,:) + forward(kk,:)*l1;
       plot([back(1),front(1)],[back(2),front(2)],'-k');
   end
end
%draw track
if(1)
%points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
   [leftline,middleline,rightline] = drawTrack(points(:,1:2),points(:,3));
   plot(leftline(:,1),leftline(:,2),'b')
   plot(rightline(:,1),rightline(:,2),'b')
end
%draw plan
if(0)
    [np, ~] = size(plansx);
    for i = 1:np
       plot(plansx(i,:),plansy(i,:),'--b');
       xx = [plansx(i,end),targets(i,1)];
       yy = [plansy(i,end),targets(i,2)];
       plot(xx,yy,'r');
    end
end
hold off


subplot(m,n,2)
hold on
yyaxis left
plot(lhistory(:,1),lhistory(:,index.beta+1))
ylabel('steering position [rad]')
axis([-inf inf -1 1])
yyaxis right
stairs(lhistory(:,1), lhistory(:,index.dotbeta+1))
%axis([-inf inf -2 2])
axis([-inf inf -4 4])
ylabel('steering change rate [rad/s]')
hold off
title('steering input');
xlabel('[s]')
%legend('steering position','change rate')

subplot(m,n,3)
hold on
%yyaxis left
plot(lhistory(:,1), lhistory(:,index.ab+1),'g')
plot(lhistory(:,1), lhistory(:,index.tv+1),'b')
ylabel('acceleration [m/s^2]')
axis([-inf inf -1 3])
%yyaxis right
%plot(lhistory(:,1),lhistory(:,index.v+1))
%ylabel('speed [m/s]')
%axis([-inf inf -12 12])
title('Accelerations');
legend('AB','TV')
xlabel('[s]')
hold off
%legend('Acceleration','Speed')

subplot(m,n,4)
hold on
%compute lateral acceleration
l = 1.19;
beta = lhistory(:,index.beta+1);
dotbeta = lhistory(:,index.dotbeta+1);
tangentspeed = lhistory(:,index.v+1);
ackermannAngle = -0.58.*beta.*beta.*beta+0.93*beta;
dAckermannAngle = -0.58.*3.*beta.*beta.*dotbeta+0.93.*dotbeta;
la = tan(ackermannAngle).*tangentspeed.^2/l;
lra =1./(cos(ackermannAngle).^2).*dAckermannAngle.*tangentspeed./l;
fa = lhistory(:,index.ab+1);
na = (fa.^2+la.^2).^0.5;
title('velocity')
axis([-inf inf -10 40])
ylabel('[km/h]')
xlabel('[s]')
plot(lhistory(:,1),lhistory(:,index.v+1)*3.6);
plot(lhistory(:,1),lhistory(:,index.yv+1)*3.6);
legend('v_x','v_y')

subplot(m,n,5)
hold on
%compute lateral acceleration
braking = zeros(numel(lhistory(:,1)),1);
c = 0;
for sp=lhistory(:,index.v+1)'
    c = c+1;
    braking(c) = min(0,-lhistory(c,index.ab+1)+casadiGetMaxNegAcc(sp));
    %braking(c) = max(0,-lhistory(c,2));
end
title('braking')
yyaxis left
axis([-inf inf -5 0])
ylabel('braking [m/s^2]')
plot(lhistory(:,1),braking);

yyaxis right
ylabel('slack')
axis([-inf inf -0.1 0.1])
plot(lhistory(:,1), lhistory(:,index.slack+1));

subplot(m,n,6)
% variables history = [t,ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
hold on
title('path progress')
yyaxis left
axis([-inf inf 0 3])
ylabel('progress rate [1/s]')
plot(lhistory(:,1),lhistory(:,index.ds+1));

yyaxis right
ylabel('progress [1]')
axis([-inf inf 0 2])
xlabel('[s]')
plot(lhistory(:,1), lhistory(:,index.s+1));
B1 = 9;
C1 = 1;
D1 = 10; % gravity acceleration considered

B2 = 5.2;
C2 = 1.1;
D2 = 10; % gravity acceleration considered
Cf=0.3;
param = [B1,C1,D1,B2,C2,D2,Cf];

for ii=1:length(lhistory(:,index.v+1))
    [ACCX(ii),ACCY(ii),ACCROTZ(ii)] = modelDx(lhistory(ii,index.v+1),lhistory(ii,index.yv+1),lhistory(ii,index.dottheta+1),ackermannAngle(ii),lhistory(ii,index.ab+1),lhistory(ii,index.tv+1), param);
end

% figure
% plot(lhistory(:,1),ACCX,'b')
% hold on
% plot(lhistory(:,1),ACCY,'r')
% plot(lhistory(:,1),zeros(length(ACCX),1),'g')
% legend('AccX','AccY')
% 
% figure
% hold on
% plot(lhistory(:,1),(lhistory(:,index.tv+1)/2+lhistory(:,index.ab+1))/0.73*1.19,'b')
% plot(lhistory(:,1),(-lhistory(:,index.tv+1)/2+lhistory(:,index.ab+1))/0.73*1.19,'r')
% legend('+','-')

reg=0.5;
% Pacejka's magic formula
magic = @(s,B,C,D)D.*sin(C.*atan(B.*s));
% Equation for the lateral force in tire frame
simplefaccy = @(VELY,VELX)magic(-VELY/(VELX+reg),B1,C1,D1);
%simpleaccy = @(VELY,VELX,taccx)magic(-VELY/(VELX+reg),B2,C2,D2);
    
% go-kart length between axles
l = 1.19;
    
% distance from the front axle to the center of mass
l1 = 0.73;
    
% distance from the back axle to the center of mass
l2 = l-l1;
    
% normal forces ( g is in D)
f1n = l2/l;
f2n = l1/l;
    
% Rotation Matrix
rotmat = @(beta)[cos(beta),sin(beta);-sin(beta),cos(beta)];
vel1=zeros(2,length(lhistory(:,index.v+1)));

for ii=1:length(lhistory(:,index.v+1))
    vel1(:,ii) = (rotmat(lhistory(ii,index.beta+1))*[lhistory(ii,index.v+1);lhistory(ii,index.yv+1)+l1*lhistory(ii,index.dottheta+1)])';
    f1y(ii)= simplefaccy(vel1(2,ii),vel1(1,ii));
    F1(:,ii) = rotmat(-lhistory(ii,index.beta+1))*[0;f1y(ii)]*f1n;
end
% 
% figure
% plot(lhistory(:,1),F1(1,:)'+lhistory(:,index.ab+1),'r')
% hold on
% plot(lhistory(:,1),ACCX,'b')

figure;
subplot(m,n,1)
hold on
%plot(ttpos(:,2),ttpos(:,3), 'Color', [0.8 0.8 0.8])
%plot(history(:,5), history(:,6), 'b')
%stairs(refEXT(:,1), refEXT(:,3), 'b')
daspect([1 1 1])
title('reference trajectory vs actual');
%legend('reference', 'MPC controlled')

%plot acceleration and deceleration in colors
po2 = lhistory2(:,[index.x+1,index.y+1]);
offset2 = 0.2*gokartforward(lhistory2(:,index.theta+1));
forward2 = gokartforward(lhistory2(:,index.theta+1));
p2 = offset2 + po2;
acc2 = lhistory2(:,index.ab+1);
maxacc2 = max(abs(acc2));
[nu,~]=size(p2);
for kk=1:nu-1
    next2 = kk+1;
    x2 = [p2(kk,1),p2(next2,1)];
    y2 = [p2(kk,2),p2(next2,2)];
    vc2 = acc2(kk)/maxacc2;
    line(x2,y2,'Color',[0.3-0.3*vc2,0.3+0.3*vc2,0.3]); %%TODO MH not working
    %draw angle
    spc2 = spc2+1;
    if(spc2>=spacing)
        spc2 = 1;
        back2 = p2(kk,:) - forward2(kk,:)*l2;
        front2 = p2(kk,:) + forward2(kk,:)*l1;
        plot([back2(1),front2(1)],[back2(2),front2(2)],'-m');
    end
end
%draw track
if(1)
%points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
   [leftline2,middleline2,rightline2] = drawTrack(points2(:,1:2),points2(:,3));
   plot(leftline2(:,1),leftline2(:,2),'b')
   plot(rightline2(:,1),rightline2(:,2),'b')
end
%draw plan
if(0)
    [np2, ~] = size(plansx2);
    for i = 1:np2
       plot(plansx2(i,:),plansy2(i,:),'--b');
       xx2 = [plansx2(i,end),targets2(i,1)];
       yy2 = [plansy2(i,end),targets2(i,2)];
       plot(xx2,yy2,'r');
    end
end
hold off


subplot(m,n,2)
hold on
yyaxis left
plot(lhistory2(:,1),lhistory2(:,index.beta+1))
ylabel('steering position [rad]')
axis([-inf inf -1 1])
yyaxis right
stairs(lhistory2(:,1), lhistory2(:,index.dotbeta+1))
%axis([-inf inf -2 2])
axis([-inf inf -4 4])
ylabel('steering change rate [rad/s]')
hold off
title('steering input');
xlabel('[s]')
%legend('steering position','change rate')

subplot(m,n,3)
hold on
%yyaxis left
plot(lhistory2(:,1), lhistory2(:,index.ab+1),'g')
plot(lhistory2(:,1), lhistory2(:,index.tv+1),'b')
ylabel('acceleration [m/s^2]')
axis([-inf inf -1 3])
%yyaxis right
%plot(lhistory(:,1),lhistory(:,index.v+1))
%ylabel('speed [m/s]')
%axis([-inf inf -12 12])
title('Accelerations');
legend('AB','TV')
xlabel('[s]')
hold off
%legend('Acceleration','Speed')

subplot(m,n,4)
hold on
%compute lateral acceleration

beta2 = lhistory2(:,index.beta+1);
dotbeta2 = lhistory2(:,index.dotbeta+1);
tangentspeed2 = lhistory2(:,index.v+1);
ackermannAngle2 = -0.58.*beta2.*beta2.*beta2+0.93*beta2;
dAckermannAngle2 = -0.58.*3.*beta2.*beta2.*dotbeta2+0.93.*dotbeta2;
la2 = tan(ackermannAngle2).*tangentspeed2.^2/l;
lra2 =1./(cos(ackermannAngle2).^2).*dAckermannAngle2.*tangentspeed2./l;
fa2 = lhistory2(:,index.ab+1);
na2 = (fa2.^2+la2.^2).^0.5;
title('velocity')
axis([-inf inf -10 40])
ylabel('[km/h]')
xlabel('[s]')
plot(lhistory2(:,1),lhistory2(:,index.v+1)*3.6);
plot(lhistory2(:,1),lhistory2(:,index.yv+1)*3.6);
legend('v_x','v_y')

subplot(m,n,5)
hold on
%compute lateral acceleration
braking2 = zeros(numel(lhistory2(:,1)),1);
c2 = 0;
for sp=lhistory2(:,index.v+1)'
    c2 = c2+1;
    braking2(c2) = min(0,-lhistory2(c2,index.ab+1)+casadiGetMaxNegAcc(sp));
    %braking(c) = max(0,-lhistory(c,2));
end
title('braking')
yyaxis left
axis([-inf inf -5 0])
ylabel('braking [m/s^2]')
plot(lhistory2(:,1),braking2);

yyaxis right
ylabel('slack')
axis([-inf inf -0.1 0.1])
plot(lhistory2(:,1), lhistory2(:,index.slack+1));

subplot(m,n,6)
% variables history = [t,ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
hold on
title('path progress')
yyaxis left
axis([-inf inf 0 3])
ylabel('progress rate [1/s]')
plot(lhistory2(:,1),lhistory2(:,index.ds+1));

yyaxis right
ylabel('progress [1]')
axis([-inf inf 0 2])
xlabel('[s]')
plot(lhistory2(:,1), lhistory2(:,index.s+1));

figure
hold on
for kk=1:nu-1
    next = kk+1;
    x = [p(kk,1),p(next,1)];
    y = [p(kk,2),p(next,2)];
    vc = acc(kk)/maxacc;
    line(x,y,'Color',[0.5-0.5*vc,0.5+0.5*vc,0]); %%TODO MH not working
    %draw angle
    spc = spc+1;
    if(spc>=spacing)
       spc = 1;
       back = p(kk,:) - forward(kk,:)*l2;
       front = p(kk,:) + forward(kk,:)*l1;
       plot([back(1),front(1)],[back(2),front(2)],'-k');
    end    
    
    next2 = kk+1;
    x2 = [p2(kk,1),p2(next2,1)];
    y2 = [p2(kk,2),p2(next2,2)];
    vc2 = acc2(kk)/maxacc2;
    line(x2,y2,'Color',[0.3-0.3*vc2,0.3+0.3*vc2,0.3]); %%TODO MH not working
    %draw angle
    spc2 = spc2+1;
    if(spc2>=spacing)
        spc2 = 1;
        back2 = p2(kk,:) - forward2(kk,:)*l2;
        front2 = p2(kk,:) + forward2(kk,:)*l1;
        plot([back2(1),front2(1)],[back2(2),front2(2)],'-m');
    end    
end
%draw track
if(1)
%points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
   [leftline,middleline,rightline] = drawTrack(points(:,1:2),points(:,3));
   plot(leftline(:,1),leftline(:,2),'b')
   plot(rightline(:,1),rightline(:,2),'b')
   [leftline2,middleline2,rightline2] = drawTrack(points2(:,1:2),points2(:,3));
   plot(leftline2(:,1),leftline2(:,2),'b')
   plot(rightline2(:,1),rightline2(:,2),'b')
end
%draw plan
if(0)
    [np2, ~] = size(plansx2);
    for i = 1:np2
       plot(plansx2(i,:),plansy2(i,:),'--b');
       xx2 = [plansx2(i,end),targets2(i,1)];
       yy2 = [plansy2(i,end),targets2(i,2)];
       plot(xx2,yy2,'r');
    end
end
hold off

% 
% for ii=1:length(lhistory2(:,index.v+1))
%     [ACCX2(ii),ACCY2(ii),ACCROTZ(ii)] = modelDx(lhistory2(ii,index.v+1),lhistory2(ii,index.yv+1),lhistory2(ii,index.dottheta+1),ackermannAngle2(ii),lhistory2(ii,index.ab+1),lhistory2(ii,index.tv+1), param);
% end

% figure
% plot(lhistory2(:,1),ACCX2,'b')
% hold on
% plot(lhistory2(:,1),ACCY2,'r')
% plot(lhistory2(:,1),zeros(length(ACCX2),1),'g')
% legend('AccX','AccY')

% figure
% hold on
% plot(lhistory2(:,1),(lhistory2(:,index.tv+1)/2+lhistory2(:,index.ab+1))/0.73*1.19,'b')
% plot(lhistory2(:,1),(-lhistory2(:,index.tv+1)/2+lhistory2(:,index.ab+1))/0.73*1.19,'r')
% legend('+','-')
% vel2=zeros(2,length(lhistory2(:,index.v+1)));

% for ii=1:length(lhistory2(:,index.v+1))
%     vel2(:,ii) = (rotmat(lhistory2(ii,index.beta+1))*[lhistory2(ii,index.v+1);lhistory2(ii,index.yv+1)+l1*lhistory2(ii,index.dottheta+1)])';
%     f1y2(ii)= simplefaccy(vel2(2,ii),vel2(1,ii));
%     F12(:,ii) = rotmat(-lhistory2(ii,index.beta+1))*[0;f1y2(ii)]*f1n;
% end

% figure
% plot(lhistory2(:,1),F12(1,:)'+lhistory2(:,index.ab+1),'r')
% hold on
% plot(lhistory2(:,1),ACCX2,'b')
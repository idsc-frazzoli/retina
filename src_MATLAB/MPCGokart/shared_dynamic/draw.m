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
lhistory = history(hstart:end,:);

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
for i=1:nu-1
    next = i+1;
    x = [p(i,1),p(next,1)];
   y = [p(i,2),p(next,2)];
   vc = acc(i)/maxacc;
   line(x,y,'Color',[0.5-0.5*vc,0.5+0.5*vc,0]); %%TODO MH not working
   %draw angle
   spc = spc+1;
   if(spc>=spacing)
       spc = 1;
       back = p(i,:) - forward(i,:)*l2;
       front = p(i,:) + forward(i,:)*l1;
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
axis([-inf inf -0.5 0.5])
yyaxis right
stairs(lhistory(:,1), lhistory(:,index.dotbeta+1))
%axis([-inf inf -2 2])
axis([-inf inf -0.5 0.5])
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
% B1 = 9;
% C1 = 1;
% D1 = 10; % gravity acceleration considered
% 
% B2 = 5.2;
% C2 = 1.1;
% D2 = 10; % gravity acceleration considered
% Cf=0.3;
% param = [B1,C1,D1,B2,C2,D2,Cf];
% 
% for ii=1:length(lhistory(:,index.v+1))
%     [ACCX(ii),ACCY(ii),ACCROTZ(ii)] = modelDx(lhistory(ii,index.v+1),lhistory(ii,index.yv+1),lhistory(ii,index.dottheta+1),ackermannAngle(ii),lhistory(ii,index.ab+1),lhistory(ii,index.tv+1), param);
% end
% 
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
% 
% reg=0.5;
% % Pacejka's magic formula
% magic = @(s,B,C,D)D.*sin(C.*atan(B.*s));
% % Equation for the lateral force in tire frame
% simplefaccy = @(VELY,VELX)magic(-VELY/(VELX+reg),B1,C1,D1);
% %simpleaccy = @(VELY,VELX,taccx)magic(-VELY/(VELX+reg),B2,C2,D2);
%     
% % go-kart length between axles
% l = 1.19;
%     
% % distance from the front axle to the center of mass
% l1 = 0.73;
%     
% % distance from the back axle to the center of mass
% l2 = l-l1;
%     
% % normal forces ( g is in D)
% f1n = l2/l;
% f2n = l1/l;
%     
% % Rotation Matrix
% rotmat = @(beta)[cos(beta),sin(beta);-sin(beta),cos(beta)];
% vel1=zeros(2,length(lhistory(:,index.v+1)));
% 
% for ii=1:length(lhistory(:,index.v+1))
%     vel1(:,ii) = (rotmat(lhistory(ii,index.beta+1))*[lhistory(ii,index.v+1);lhistory(ii,index.yv+1)+l1*lhistory(ii,index.dottheta+1)])';
%     f1y(ii)= simplefaccy(vel1(2,ii),vel1(1,ii));
%     F1(:,ii) = rotmat(-lhistory(ii,index.beta+1))*[0;f1y(ii)]*f1n;
% end
% 
% figure
% plot(lhistory(:,1),F1(1,:)'+lhistory(:,index.ab+1),'r')
% hold on
% plot(lhistory(:,1),ACCX,'b')
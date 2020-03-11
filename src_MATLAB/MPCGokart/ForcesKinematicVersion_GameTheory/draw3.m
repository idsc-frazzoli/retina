%code by mheim
figure;
global index

% variables history = [t,ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
%start later in history
hstart = 1;
hend = 2000;
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
p = lhistory(:,[index.x+1,index.y+1]);
offset = 0.4*gokartforward(lhistory(:,index.theta+1));
p = offset + p;
acc = lhistory(:,index.ab+1);
maxacc = max(abs(acc));
[nu,~]=size(p);
for i=1:nu-1
    next = i+1;
    x = [p(i,1),p(next,1)];
   y = [p(i,2),p(next,2)];
   vc = acc(i)/maxacc;
   line(x,y,'Color',[0.5-0.5*vc,0.5+0.5*vc,0]);
end
%draw track
if(1)
%points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
%points = [36.2,52,57.2,53,52,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.5,0.5,0.5,1.8]';
   [leftline,middleline,rightline] = drawTrack(points(:,1:2),points(:,3)+0.5);
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
yyaxis left
stairs(lhistory(:,1), lhistory(:,index.ab+1))
ylabel('acceleration [m/s^2]')
axis([-inf inf -8 8])
yyaxis right
plot(lhistory(:,1),lhistory(:,index.v+1))
ylabel('speed [m/s]')
axis([-inf inf -12 12])
title('Acceleration/Speed');
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
title('accelerations')
axis([-inf inf -10 10])
ylabel('[m/s^2]')
xlabel('[s]')
plot(lhistory(:,1),la);
%plot(history(:,1),fa);
plot(lhistory(:,1),na);
plot(lhistory(:,1),lra);
legend('lateral acceleration','norm of acceleration','rotational acceleration [1/s^2]');

subplot(m,n,5)
hold on
%compute lateral acceleration
braking = zeros(numel(lhistory(:,1)),1);
c = 0;
for sp=lhistory(:,index.v+1)'
    c = c+1;
    braking(c) = max(0,-lhistory(c,index.ab+1)+casadiGetMaxNegAcc(sp));
    %braking(c) = max(0,-lhistory(c,2));
end
title('braking')
yyaxis left
axis([-inf inf -0.1 3.1])
ylabel('braking [m/s^2]')
plot(lhistory(:,1),braking);

yyaxis right
ylabel('slack')
axis([-inf inf -1 10])
plot(lhistory(:,1), lhistory(:,index.slack+1));

subplot(m,n,6)
% variables history = [t,ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
hold on
title('path progress')
yyaxis left
axis([-inf inf 0 1])
ylabel('progress rate [1/s]')
plot(lhistory(:,1),lhistory(:,index.ds+1));

yyaxis right
ylabel('progress [1]')
axis([-inf inf 0 2])
xlabel('[s]')
plot(lhistory(:,1), lhistory(:,index.s+1));


figure;

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
p2 = lhistory(:,[index.x_k2+1,index.y_k2+1]);
offset_k2 = 0.4*gokartforward(lhistory(:,index.theta_k2+1));
p2 = offset_k2 + p2;
acc2 = lhistory(:,index.ab_k2+1);
maxacc2 = max(abs(acc2));
[nu_k2,~]=size(p2);
for i=1:nu_k2-1
    next_k2 = i+1;
    x_k2 = [p2(i,1),p2(next_k2,1)];
    y_k2 = [p2(i,2),p2(next_k2,2)];
    vc_k2 = acc2(i)/maxacc2;
    line(x_k2,y_k2,'Color',[0.5-0.5*vc_k2,0.5+0.5*vc_k2,0]);
end
%draw track
if(1)
%points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
%points = [36.2,52,57.2,53,52,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.5,0.5,0.5,1.8]';
   [leftline_k2,middleline_k2,rightline_k2] = drawTrack(points2(:,1:2),points2(:,3)+0.5);
   plot(leftline_k2(:,1),leftline_k2(:,2),'b')
   plot(rightline_k2(:,1),rightline_k2(:,2),'b')
end
%draw plan
if(0)
    [np_k2, ~] = size(plansx2);
    for i = 1:np_k2
       plot(plansx2(i,:),plansy2(i,:),'--b');
       xx_k2 = [plansx2(i,end),targets2(i,1)];
       yy_k2 = [plansy2(i,end),targets2(i,2)];
       plot(xx_k2,yy_k2,'r');
    end
end
hold off


subplot(m,n,2)
hold on
yyaxis left
plot(lhistory(:,1),lhistory(:,index.beta_k2+1))
ylabel('steering position [rad]')
axis([-inf inf -1 1])
yyaxis right
stairs(lhistory(:,1), lhistory(:,index.dotbeta_k2+1))
%axis([-inf inf -2 2])
axis([-inf inf -4 4])
ylabel('steering change rate [rad/s]')
hold off
title('steering input');
xlabel('[s]')
%legend('steering position','change rate')

subplot(m,n,3)
hold on
yyaxis left
stairs(lhistory(:,1), lhistory(:,index.ab_k2+1))
ylabel('acceleration [m/s^2]')
axis([-inf inf -8 8])
yyaxis right
plot(lhistory(:,1),lhistory(:,index.v_k2+1))
ylabel('speed [m/s]')
axis([-inf inf -12 12])
title('Acceleration/Speed');
xlabel('[s]')
hold off
%legend('Acceleration','Speed')

subplot(m,n,4)
hold on
%compute lateral acceleration
l = 1.19;
beta_k2 = lhistory(:,index.beta_k2+1);
dotbeta_k2 = lhistory(:,index.dotbeta_k2+1);
tangentspeed_k2 = lhistory(:,index.v_k2+1);
ackermannAngle_k2 = -0.58.*beta_k2.*beta_k2.*beta_k2+0.93*beta_k2;
dAckermannAngle_k2 = -0.58.*3.*beta_k2.*beta_k2.*dotbeta_k2+0.93.*dotbeta_k2;
la_k2 = tan(ackermannAngle_k2).*tangentspeed.^2/l;
lra_k2 =1./(cos(ackermannAngle_k2).^2).*dAckermannAngle_k2.*tangentspeed./l;
fa_k2 = lhistory(:,index.ab_k2+1);
na_k2 = (fa_k2.^2+la_k2.^2).^0.5;
title('accelerations')
axis([-inf inf -10 10])
ylabel('[m/s^2]')
xlabel('[s]')
plot(lhistory(:,1),la_k2);
%plot(history(:,1),fa);
plot(lhistory(:,1),na_k2);
plot(lhistory(:,1),lra_k2);
legend('lateral acceleration','norm of acceleration','rotational acceleration [1/s^2]');

subplot(m,n,5)
hold on
%compute lateral acceleration
braking = zeros(numel(lhistory(:,1)),1);
c = 0;
for sp_k2=lhistory(:,index.v_k2+1)'
    c = c+1;
    braking(c) = max(0,-lhistory(c,index.ab_k2+1)+casadiGetMaxNegAcc(sp_k2));
    %braking(c) = max(0,-lhistory(c,2));
end
title('braking')
yyaxis left
axis([-inf inf -0.1 3.1])
ylabel('braking [m/s^2]')
plot(lhistory(:,1),braking);

yyaxis right
ylabel('slack')
axis([-inf inf -1 10])
plot(lhistory(:,1), lhistory(:,index.slack_k2+1));

subplot(m,n,6)
% variables history = [t,ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
hold on
title('path progress')
yyaxis left
axis([-inf inf 0 1])
ylabel('progress rate [1/s]')
plot(lhistory(:,1),lhistory(:,index.ds_k2+1));

yyaxis right
ylabel('progress [1]')
axis([-inf inf 0 2])
xlabel('[s]')
plot(lhistory(:,1), lhistory(:,index.s_k2+1));


figure;

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
p3 = lhistory(:,[index.x_k3+1,index.y_k3+1]);
offset_k3 = 0.4*gokartforward(lhistory(:,index.theta_k3+1));
p3 = offset_k3 + p3;
acc3 = lhistory(:,index.ab_k3+1);
maxacc3 = max(abs(acc3));
[nu_k3,~]=size(p3);
for i=1:nu_k3-1
    next_k3 = i+1;
    x_k3 = [p3(i,1),p3(next_k3,1)];
    y_k3 = [p3(i,2),p3(next_k3,2)];
    vc_k3 = acc3(i)/maxacc3;
    line(x_k3,y_k3,'Color',[0.5-0.5*vc_k3,0.5+0.5*vc_k3,0]);
end
%draw track
if(1)
%points = [36.2,52,57.2,53,55,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.2,0.2,0.2,1.8]';
%points = [36.2,52,57.2,53,52,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.5,0.5,0.5,1.8]';
   [leftline_k3,middleline_k3,rightline_k3] = drawTrack(points3(:,1:2),points3(:,3)+0.5);
   plot(leftline_k3(:,1),leftline_k3(:,2),'b')
   plot(rightline_k3(:,1),rightline_k3(:,2),'b')
end
%draw plan
if(0)
    [np_k3, ~] = size(plansx3);
    for i = 1:np_k3
       plot(plansx3(i,:),plansy3(i,:),'--b');
       xx_k3 = [plansx3(i,end),targets3(i,1)];
       yy_k3 = [plansy3(i,end),targets3(i,2)];
       plot(xx_k3,yy_k3,'r');
    end
end
hold off


subplot(m,n,2)
hold on
yyaxis left
plot(lhistory(:,1),lhistory(:,index.beta_k3+1))
ylabel('steering position [rad]')
axis([-inf inf -1 1])
yyaxis right
stairs(lhistory(:,1), lhistory(:,index.dotbeta_k3+1))
%axis([-inf inf -2 2])
axis([-inf inf -4 4])
ylabel('steering change rate [rad/s]')
hold off
title('steering input');
xlabel('[s]')
%legend('steering position','change rate')

subplot(m,n,3)
hold on
yyaxis left
stairs(lhistory(:,1), lhistory(:,index.ab_k3+1))
ylabel('acceleration [m/s^2]')
axis([-inf inf -8 8])
yyaxis right
plot(lhistory(:,1),lhistory(:,index.v_k3+1))
ylabel('speed [m/s]')
axis([-inf inf -12 12])
title('Acceleration/Speed');
xlabel('[s]')
hold off
%legend('Acceleration','Speed')

subplot(m,n,4)
hold on
%compute lateral acceleration
l = 1.19;
beta_k3 = lhistory(:,index.beta_k3+1);
dotbeta_k3 = lhistory(:,index.dotbeta_k3+1);
tangentspeed_k3 = lhistory(:,index.v_k3+1);
ackermannAngle_k3 = -0.58.*beta_k3.*beta_k3.*beta_k3+0.93*beta_k3;
dAckermannAngle_k3 = -0.58.*3.*beta_k3.*beta_k3.*dotbeta_k3+0.93.*dotbeta_k3;
la_k3 = tan(ackermannAngle_k3).*tangentspeed.^2/l;
lra_k3 =1./(cos(ackermannAngle_k3).^2).*dAckermannAngle_k3.*tangentspeed./l;
fa_k3 = lhistory(:,index.ab_k3+1);
na_k3 = (fa_k3.^2+la_k3.^2).^0.5;
title('accelerations')
axis([-inf inf -10 10])
ylabel('[m/s^2]')
xlabel('[s]')
plot(lhistory(:,1),la_k3);
%plot(history(:,1),fa);
plot(lhistory(:,1),na_k3);
plot(lhistory(:,1),lra_k3);
legend('lateral acceleration','norm of acceleration','rotational acceleration [1/s^2]');

subplot(m,n,5)
hold on
%compute lateral acceleration
braking = zeros(numel(lhistory(:,1)),1);
c = 0;
for sp_k3=lhistory(:,index.v_k3+1)'
    c = c+1;
    braking(c) = max(0,-lhistory(c,index.ab_k3+1)+casadiGetMaxNegAcc(sp_k3));
    %braking(c) = max(0,-lhistory(c,2));
end
title('braking')
yyaxis left
axis([-inf inf -0.1 3.1])
ylabel('braking [m/s^2]')
plot(lhistory(:,1),braking);

yyaxis right
ylabel('slack')
axis([-inf inf -1 10])
plot(lhistory(:,1), lhistory(:,index.slack_k3+1));

subplot(m,n,6)
% variables history = [t,ab,dotbeta,ds,x,y,theta,v,beta,s,braketemp]
hold on
title('path progress')
yyaxis left
axis([-inf inf 0 1])
ylabel('progress rate [1/s]')
plot(lhistory(:,1),lhistory(:,index.ds_k3+1));

yyaxis right
ylabel('progress [1]')
axis([-inf inf 0 2])
xlabel('[s]')
plot(lhistory(:,1), lhistory(:,index.s_k3+1));


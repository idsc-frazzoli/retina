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
    points = [36.2,52,57.2,53,52,47,41.8;44.933,58.2,53.8,49,44,43,38.33;1.8,1.8,1.8,0.8,0.8,0.8,1.8]';
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
yyaxis left
stairs(lhistory(:,1), lhistory(:,index.ab+1))
ylabel('acceleration [m/s²]')
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
ylabel('[m/s²]')
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
ylabel('braking [m/s²]')
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


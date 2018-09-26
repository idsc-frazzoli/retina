%code by mheim
figure;

% variables history = [t,ab,dotbeta,x,y,theta,v,beta,s]
%start later in history
hstart = 1300;
lhistory = history(hstart:end,:);

m = 2;
n = 2;
subplot(m,n,1)
hold on
%plot(ttpos(:,2),ttpos(:,3), 'Color', [0.8 0.8 0.8])
%plot(history(:,5), history(:,6), 'b')
%stairs(refEXT(:,1), refEXT(:,3), 'b')
daspect([1 1 1])
title('reference trajectory vs actual');
%legend('reference', 'MPC controlled')

%plot acceleration and deceleration in colors
p = lhistory(:,5:6);
acc = lhistory(:,2);
maxacc = max(abs(acc));
[nu,~]=size(p);
for i=1:nu-1
    next = i+1;
    x = [p(i,1),p(next,1)];
   y = [p(i,2),p(next,2)];
   vc = acc(i)/maxacc;
   line(x,y,'Color',[0.5-0.5*vc,0.5+0.5*vc,0]);
end
hold off


subplot(m,n,2)
hold on
yyaxis left
plot(lhistory(:,1),lhistory(:,9))
ylabel('steering position [rad]')
axis([-inf inf -1 1])
yyaxis right
stairs(lhistory(:,1), lhistory(:,3))
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
stairs(lhistory(:,1), lhistory(:,2))
ylabel('acceleration [m/s²]')
axis([-inf inf -2.5 2.5])
yyaxis right
plot(lhistory(:,1),lhistory(:,8))
ylabel('speed [m/s]')
axis([-inf inf -5 5])
title('Acceleration/Speed');
xlabel('[s]')
%legend('Acceleration','Speed')

subplot(m,n,4)
hold on
%compute lateral acceleration
l = 1;
la = tan(lhistory(:,9)).*lhistory(:,8).^2/l;
fa = lhistory(:,2);
na = (fa.^2+la.^2).^0.5;
title('accelerations')
axis([-inf inf -4 4])
ylabel('[m/s²]')
xlabel('[s]')
plot(lhistory(:,1),la);
%plot(history(:,1),fa);
plot(lhistory(:,1),na);
legend('lateral acceleration','norm of acceleration');

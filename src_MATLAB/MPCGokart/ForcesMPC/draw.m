%code by mheim
figure;

% variables history = [t,ab,dotbeta,x,y,theta,v,beta,s]

subplot(1,3,1)
hold on
plot(ttpos(:,2),ttpos(:,3), 'r')
plot(history(:,4), history(:,5), 'b')
%stairs(refEXT(:,1), refEXT(:,3), 'b')
hold off
daspect([1 1 1])
title('reference trajectory vs actual');
legend('reference', 'MPC controlled')

subplot(1,3,2)
hold on
plot(history(:,1),history(:,8), 'r')
stairs(history(:,1), history(:,3), 'b')
hold off
title('steering input');
legend('steering position','change rate')

subplot(1,3,3)
stairs(history(:,1), history(:,2), 'r')
title('Acceleration');
    
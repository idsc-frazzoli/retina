% points = [36.2,52,57.2,53,55,47,41.8;
%           44.933,58.2,53.8,49,44,43,38.33;
%           1.8,0.5,1.8,0.4,0.4,0.4,1.8]';
close all
points = [28,35,42,55.2,56,51,42,40;...          %x
          41,60,43,56,43,40,44,31; ...    %y
          1.8,1.4,1,1.5,0.5,0.5,0.5,1.4]';
[leftline,middleline,rightline] = drawTrack(points(:,1:2),points(:,3));
figure
hold on
plot(leftline(:,1),leftline(:,2),'b')
plot(middleline(:,1),middleline(:,2),'r')
plot(rightline(:,1),rightline(:,2),'b')
points1 = [points;points(1,1),points(1,2),points(1,3)];
plot(points1(:,1),points1(:,2),'g*-')
xlabel('x')
ylabel('y')
grid on
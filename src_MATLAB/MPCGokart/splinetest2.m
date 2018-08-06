%code by mheim
%points = [-1,1,2,0,0,0,1,0,0,0]';
%points = ones(5,1)
points = [1,2,2,4,2,2,1;0,0,5.7,6,6.3,10,10]';
[p,steps,speed]=getTrajectory(points,3,1);

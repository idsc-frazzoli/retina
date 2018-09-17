function [ car_handle ] = draw_car(x,y,ksi,frontL, rearL,width,id,translucency)
%Draw the car describer in car_ref in the simulation environment simfig

%Draw car
% when ksi = 0, c1 is the NW corner
% from there we go counter clokckwise
w = width/2;
v1 = [frontL, w]';
v2 = [-rearL, w]';
v3 = [-rearL, -w]';
v4 = [frontL, -w]';

mat = [cos(ksi), -sin(ksi); sin(ksi),  cos(ksi)];

temp = mat * v1;
c1x = temp(1);
c1y = temp(2);
temp = mat * v2;
c2x = temp(1);
c2y = temp(2);
temp = mat * v3;
c3x = temp(1);
c3y = temp(2);
temp = mat * v4;
c4x = temp(1);
c4y = temp(2);



switch id 
    case 1, col = 'b';
    case 2, col = 'r';
    case 3, col = 'g';
    case 4, col = 'c';
    case 5, col = 'm';
    case 6, col = 'y';
    otherwise, col = 'k';    
end


hold on

X = [c1x, c2x, c3x, c4x]+x;
Y = [c1y, c2y, c3y, c4y]+y;

car_handle = fill(X,Y,col,'LineWidth', 0.5);
alpha(car_handle, translucency)




end


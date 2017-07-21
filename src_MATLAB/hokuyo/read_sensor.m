close all
clear all
clc

% for user
disp('CLOSE THE FIGURE, NOT THIS TERMINAL')
disp('OTHERWISE YOU WILL HAVE TO RESTART THE SENSOR')

%open stuff for plotting
h = figure;
hold on
range = 4000; % sensor range is 4000  mm

%create a blind spot behind the sensor
alhpaStart = -100 * pi/180;
alphaEnd = -80 * pi/180;
fill([0 2000*cos(alhpaStart) 2000*cos(alphaEnd)], [0 2000*sin(alhpaStart) 2000*sin(alphaEnd)], 'k');
line([0 0], [0 3000], 'color','g','LInewidth',2)
pause(0.01);
%open pipes
pipeIN = fopen('matPIPEin','r');
pipeOUT = fopen('matPIPEout','w');

while ishandle(h)
    %read from the sensor (pipe)
    str = fgetl(pipeIN);
    if (~ischar(str))
        break;
    end
    %parse and plot
     numbers = parser(str);
     alpha = (linspace(-170*pi/180,170*pi/180,length(numbers)))' + pi/2;
     polygon = fill(numbers.*cos(alpha), numbers.*sin(alpha), 'r', 'FaceAlpha', 0.2);
     hold on 
     axis([-range range -range range]);
     pause(0.01);
     delete(polygon)     
end

fprintf(pipeOUT,'closing the sensor\n');
exit





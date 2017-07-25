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
angularRange = 240;
blindSpotLength = 2000;
delta = (360 - angularRange)/2;
alhpaStart = (-90 - delta) * pi/180;
alphaEnd = (-90 + delta) * pi/180;
fill([0 blindSpotLength*cos(alhpaStart) blindSpotLength*cos(alphaEnd)], [0 blindSpotLength*sin(alhpaStart) blindSpotLength*sin(alphaEnd)], 'k');
line([0 0], [0 range*0.75], 'color','g','LInewidth',2)
pause(0.01);
%open pipes
pipeIN = fopen('matPIPEin','r');
pipeOUT = fopen('matPIPEout','w');
alpha = [];
while ishandle(h)
    %read from the sensor (pipe)
    str = fgetl(pipeIN);
    if (~ischar(str))
        break;
    end
    %parse and plot
     numbers = parser(str);
     if (isempty(alpha))
        alpha = (linspace((-180+delta)*pi/180,(180-delta)*pi/180,length(numbers)))' + pi/2;
     end
     polygon = fill(numbers.*cos(alpha), numbers.*sin(alpha), 'r', 'FaceAlpha', 0.2);
     hold on 
     axis([-range range -range range]);
     pause(0.01);
     delete(polygon)     
end

fprintf(pipeOUT,'closing the sensor\n');
exit





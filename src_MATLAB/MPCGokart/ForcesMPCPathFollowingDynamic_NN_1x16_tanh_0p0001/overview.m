clear
userdir = getuserdir;
theta =-0.76; % to rotate 90 counterclockwise
R = [cos(theta) -sin(theta); sin(theta) cos(theta)];
offset = [-37.3,6.3]';
figure
hold on
daspect([1 1 1])
axis([0 40 0 20])
xlabel('X [m]')
ylabel('Y [m]')
img = imread(strcat(userdir,'/trackbg.png'));
scale = 0.0198;
width = 1920*scale;
height = 1080*scale;
image('CData',img,'XData',[0 width],'YData',[height 0])
folder = '/retina_out/kinematic.lcm/';
folder=strcat(userdir,folder);
SysIDkin = loadSystIDData(folder);
pkin = (R*SysIDkin(:,14:15)'+offset)';
folder = '/retina_out/dynamic.lcm/';
folder=strcat(userdir,folder);
SysIDdyn = loadSystIDData(folder);
pdyn = (R*SysIDdyn(:,14:15)'+offset)';
folder = '/retina_out/human.lcm/';
folder=strcat(userdir,folder);
SysIDhum = loadSystIDData(folder);
phum = (R*SysIDhum(:,14:15)'+offset)';
folder = '/retina_out/center.lcm/';
folder=strcat(userdir,folder);
SysIDpur = loadSystIDData(folder);
ppur = (R*SysIDpur(:,14:15)'+offset)';
refData = csvread(strcat(userdir,'//Documents/ReferenceTrajectory/traj.csv'));
refstart = 208;
pref = (R*refData(refstart:end,1:2)'+offset)';
%plot(pref(:,1),pref(:,2),'--r');
plot(pkin(:,1),pkin(:,2),'-b');
plot(pdyn(:,1),pdyn(:,2),'--b');
plot(phum(:,1),phum(:,2),'-r');
plot(ppur(:,1),ppur(:,2),'--r');
legend('kinematic MPC','dynamic MPC','manual racing','pure pursuit')
print('overview','-dpng','-r600')
hold off
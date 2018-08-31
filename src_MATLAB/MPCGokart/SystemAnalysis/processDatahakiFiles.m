for i = 1:6
    folder = num2str(i, 'retina_out/20180820T143852%d.lcm/');
    ModelfreeStateEstimation
    csvwrite(num2str(i, 'RTS_out/20180820T143852_%d.csv'), M);
end
for i = 1:5
    folder = num2str(i, 'retina_out/20180820T165637%d.lcm/');
    ModelfreeStateEstimation
    csvwrite(num2str(i, 'RTS_out/20180820T165637_%d.csv'), M);
end
for i = 1:2
    folder = num2str(i, 'retina_out/20180702T133612%d.lcm/');
    ModelfreeStateEstimation
    csvwrite(num2str(i, 'RTS_out0w/20180702T133612_%d.csv'), M);
end
for i = 1:4
    folder = num2str(i, 'retina_out/20180702T154417%d.lcm/');
    ModelfreeStateEstimation
    csvwrite(num2str(i, 'RTS_out0w/20180702T154417_%d.csv'), M);
end
for i = 1:6
    folder = num2str(i, 'retina_out/20180702T180041%d.lcm/');
    ModelfreeStateEstimation
    csvwrite(num2str(i, 'RTS_out0w/20180702T180041_%d.csv'), M);
end
for i = 1:3
    folder = num2str(i, 'retina_out/20180705T101944%d.lcm/');
    ModelfreeStateEstimation
    csvwrite(num2str(i, 'RTS_out0w/20180705T101944_%d.csv'), M);
end
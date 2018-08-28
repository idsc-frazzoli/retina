for i = 1:6
    folder = num2str(i, 'retina_out/20180820T143852%d.lcm/');
    ModelfreeStateEstimation
end
for i = 1:5
    folder = num2str(i, 'retina_out/20180820T165637%d.lcm/');
    ModelfreeStateEstimation
end
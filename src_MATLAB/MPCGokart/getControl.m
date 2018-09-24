function u = getControl(M)
    %M has form: [t x y Ksi dotx_b doty_b dotKsi  dotdotx_b dotdoty_b dotdotKsi sa sdota pcl pcr wrl wrt dotwrl dotwrr br]
    %u has form: [sa br pcr pcr]'
    u = [M(:,11),M(:,1)*0,M(:,13:14)];
end
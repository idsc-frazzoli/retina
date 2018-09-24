function nM = convertM(M)
    %convert acc and and speed into body frame
    %M is in [t x y Ksi dotx doty dotKsi dotdotx dotdoty dotdotKsi]
    %(r==dotKsi
    %to be compatible with full model state vector [Ux Uy r Ksi x y ]'
    %nM = [t x y Ksi dotx_b doty_b dotKsi dotdotx_b dotdoty_b dotdotKsi]
    o = M(:,4);
    nM = [M(:,1:4),...
        rotate(M(:,5:6),o),...
        M(:,7),...
        rotate(M(:,8:9),o),...
        M(:,10)];
end


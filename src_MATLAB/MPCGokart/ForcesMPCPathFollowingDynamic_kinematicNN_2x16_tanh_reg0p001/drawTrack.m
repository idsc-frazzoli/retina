%code by mheim
function [leftline,middleline,rightline] = drawTrack(points,radii)
    n = numel(radii);
    res = 100;
    leftline = zeros(res*n,2);
    middleline = zeros(res*n,2);
    rightline = zeros(res*n,2);
    points = [points;points(1:2,:)];
    radii = [radii;radii(1:2)];
    for i = 1:(n)*res
        prog = i/res;
        [splx,sply] = casadiDynamicBSPLINE(prog,points);
        %[spldx, spldy] = casadiDynamicBSPLINEforward(prog,points);
        [splsx, splsy] = casadiDynamicBSPLINEsidewards(prog,points);
        sidewards = [splsx, splsy];
        r = casadiDynamicBSPLINERadius(prog,radii);
        middleline(i,:)=[splx,sply];
        rightline(i,:) = [splx,sply]+r*sidewards;
        leftline(i,:) = [splx,sply]-r*sidewards;
    end
end


%code by mheim
function [leftline,middleline,rightline,widthlines,leftlineControl, rightlineControl] = drawTrack(points,radii,trans,startpoints)
    addpath('../ForcesMPCPathFollowing/casadi');
    n = numel(radii);
    res = 100;
    leftline = zeros(res*n,2);
    middleline = zeros(res*n,2);
    rightline = zeros(res*n,2);
    leftlineControl = zeros(res*n,2);
    rightlineControl = zeros(res*n,2);
    
    widthinterp = interp1(0:n,[radii;radii(1)]',0:1/res:n);
    
    points = [points;points(1:2,:)];
    points = points*trans+startpoints*(1-trans);
    
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
        interpind = int32(i+res*0.5);
        if(interpind<1)
            interpind = interpind + n*res;
        end
        if(interpind>res*n)
            interpind = interpind - n*res;
        end
        leftlineControl(i,:) =  [splx,sply]+widthinterp(interpind)*sidewards;
        rightlineControl(i,:) =  [splx,sply]-widthinterp(interpind)*sidewards;
    end
    widthlines = zeros(n,4);
    for i = 1:n
        prog = i-1.5;
        if(prog<0)
            prog = prog + n
        end
        [mx,my] = casadiDynamicBSPLINE(prog,points);
        midpos = [mx,my];
        [sx,sy] = casadiDynamicBSPLINEsidewards(prog,points);
        sideward = [sx,sy];
        rad = radii(i);
        widthlines(i,1:2)=midpos+sideward*rad;
        widthlines(i,3:4)=midpos-sideward*rad;
    end
    %middleline =[middleline;middleline(1,:)];
    %rightline =[rightline;rightline(1,:)];
    %leftline =[leftline;leftline(1,:)];
end


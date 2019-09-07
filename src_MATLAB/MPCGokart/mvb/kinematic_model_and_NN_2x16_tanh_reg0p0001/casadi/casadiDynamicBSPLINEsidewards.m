function [xx,yy] = casadiDynamicBSPLINEsidewards(x,points)
    [yy,xx]=casadiDynamicBSPLINEforward(x,points);
    yy = -yy;
end


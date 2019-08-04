function data = getRadiiFromParameters(p, pointsO, pointsN)
    data = p(pointsO+pointsN*2+1:pointsO+pointsN*3);
end


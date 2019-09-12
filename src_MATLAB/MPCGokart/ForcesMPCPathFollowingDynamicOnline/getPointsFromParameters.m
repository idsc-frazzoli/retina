function points = getPointsFromParameters(p, pointsO, pointsN)
    data = p(pointsO+1:pointsO+pointsN*2);
    points = reshape(data, [pointsN,2]);
end


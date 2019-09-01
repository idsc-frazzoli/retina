function cap = torqueveccapsmooth(ab)
dist = abs(ab-0.15);
cospos = min(1,dist./1.65);
cap = (0.5+0.5.*cos(cospos.*pi)).^2;
end


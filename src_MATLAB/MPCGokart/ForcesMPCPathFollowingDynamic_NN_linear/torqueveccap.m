function cap = torqueveccap(ab)
dist = abs(ab-0.15);
cap = max(0,1-dist./1.65);
end


u = [zeros(100,2),ones(100,2)];
x = zeros(1,8);
f = @(x,u)fullstatemodel(x,u);
xhist = euler(f,x,u,0.01);
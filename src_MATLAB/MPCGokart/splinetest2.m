dt = 0.01;
X = 0:dt:2*pi;
n = numel(X);
Y = sin(X);%+normrnd(0,0.1,[1,n]);
getDerivation(Y, 10, dt);
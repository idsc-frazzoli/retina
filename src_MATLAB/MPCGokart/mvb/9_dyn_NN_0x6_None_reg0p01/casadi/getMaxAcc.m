close all
figure
hold on
x = 0.01:10;
n = numel(x);
y = zeros(1,n);
for i =  1:n
    y(i)=casadiGetMaxAcc(min(x(i),5));
end 
plot(x,y)
hold off
x = 0:0.0000001:2*pi;
x = x';
y = sin(x);
close all
n = numel(x);
r = randi([1 n],100000,1);
X = x(r);
Y = y(r);
Y = Y + normrnd(0,0.1,size(Y));
%%
figure
scatter(X,Y)
%%
B = TreeBagger(10,X,Y,'Method','regression','InBagFraction',0.05);
%view(B.Trees{1})
Xpred =0:0.001:2*pi;
Ypred = predict(B,Xpred');

%%
%SVM = fitrsvm(X,Y,'OptimizeHyperparameters','auto',...
%    'HyperparameterOptimizationOptions',struct('AcquisitionFunctionName',...
%    'expected-improvement-plus'))
%SVM = fitrsvm(X,Y,'Standardize',true,'KernelFunction','rbf')

%%
%SMVYpred = predict(SVM,Xpred');
NNYpred = modelApprox(Xpred);
figure
hold on
plot(Xpred,Ypred);
plot(Xpred,NNYpred);
%plot(Xpred,SMVYpred);
plot(Xpred,sin(Xpred));
x = 0.1;
legend('BagTrees', 'neuralnet', 'ground truth')
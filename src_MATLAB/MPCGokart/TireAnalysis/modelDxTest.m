%param = [B1,C1,D1,B2,C2,D2,Cf,maxA];
B = 4;
C = 1.7;
D = 0.7*9.81;
Cf = 0.15;
B1 = B;
B2 = B;
C1 = C;
C2 = C;
D1 = 0.8*D;
D2 = D;
maxA = D*0.9;
param = [B1,C1,D1,B2,C2,D2,Cf,maxA];
modelDx(1,0,0,0.5,0,0, param)
%VELX,VELY,VELROTZ,BETA,AB,TV
modelDx(1,-1,0,0,0,0, param)
close all
% VELROTZ,BETA,AB,TV,param
plotAcceleration(0,0,0,0,param)
plotAcceleration(0,0,1,0,param)
plotAcceleration(0,0,0,1,param)
plotAcceleration(0,1,0,0,param)
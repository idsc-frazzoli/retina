function [X,Y] = getLearningData(M)
% M in form:
% [t x y Ksi dotx_b doty_b dotKsi  dotdotx_b dotdoty_b dotdotKsi sa sdota pcl pcr wrl wrt dotwrl dotwrr]
% X: aviable/relevant data for model that do not violate  (absolute position and orientation
% irrelevant)
% X: [ dotx_b doty_b dotKsi sa pcl pcr wrl wrt lp]
X = [M(:,5:7),M(:,11),M(:,13:16),M(19)]
% Y: [dotdotx_b,dotdoty_b,dotdotKsi,dotwrl,dotwrr]
Y = [M(:,8:10),M(:,17:18)];
end


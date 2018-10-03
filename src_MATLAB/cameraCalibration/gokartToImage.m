% code by mg
function [imgCoord] = gokartToImage(cameraParams, physCoord, imgNumber)
% transforms physical coordinates into image coordinates. We assume Z=0 for
% the physical coordinates. Documentation is on Matlab "Single camera 
% calibrator"
%   cameraParams: object obtained with camera calibrator app
%   physCoord: X and Y coordinates in ground plane Z=0 [mm]
%   imgNumber: extrinsics of following image are used

% main equation
stackedMatrix = [cameraParams.RotationMatrices(:,:,imgNumber);cameraParams.TranslationVectors(imgNumber,1:3)];
intrinsics = cameraParams.IntrinsicMatrix;

% form homogeneous coordinates
physCoord = [physCoord 0 1]; %
imgCoord = physCoord*stackedMatrix*intrinsics;

% scale image coordinates to represent homogeneous coordinates
imgCoord = imgCoord/imgCoord(1,3);

% apply radial distortion model
imgCoordNormalized = (imgCoord(1:2)-cameraParams.PrincipalPoint)/cameraParams.FocalLength(1);
radDist = sqrt(imgCoordNormalized(1)^2+imgCoordNormalized(2)^2);
distorted(1) = imgCoordNormalized(1)*(1+radDist^2*cameraParams.RadialDistortion(1)+radDist^4*cameraParams.RadialDistortion(2));
distorted(2) = imgCoordNormalized(2)*(1+radDist^2*cameraParams.RadialDistortion(1)+radDist^4*cameraParams.RadialDistortion(2));
% unnormalize distorted image coordinates
imgCoord = distorted*cameraParams.FocalLength(1)+cameraParams.PrincipalPoint;
end
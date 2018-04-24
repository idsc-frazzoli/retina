% code by mg
% transforms physical coordinates into image coordinates. Documentation is
% on Matlab "Single camera calibrator"
% a cameraParams.mat file is required which can be obtained with the matlab
% camera calibrator app.
imgNumber = 1;
physCoord = [400,0,0,1]; % units are in mm

% load the cameraParams
load('cameraParams.mat');

% main equation
stackedMatrix = [cameraParams.RotationMatrices(:,:,imgNumber);cameraParams.TranslationVectors(imgNumber,1:3)];
intrinsics = cameraParams.IntrinsicMatrix;
imgCoord = physCoord*stackedMatrix*intrinsics;

% scale image coordinates to represent homogeneous coordinates
imgCoord = imgCoord/imgCoord(1,3);

% apply radial distortion model
imgCoordNormalized = (imgCoord(1:2)-cameraParams.PrincipalPoint)/cameraParams.FocalLength(1);
radDist = sqrt(imgCoordNormalized(1)^2+imgCoordNormalized(2)^2);
distorted(1) = imgCoordNormalized(1)*(1+radDist^2*cameraParams.RadialDistortion(1)+radDist^4*cameraParams.RadialDistortion(2));
distorted(2) = imgCoordNormalized(2)*(1+radDist^2*cameraParams.RadialDistortion(1)+radDist^4*cameraParams.RadialDistortion(2));
% unnormalize distorted image coordinates
distImgCoord = distorted*cameraParams.FocalLength(1)+cameraParams.PrincipalPoint
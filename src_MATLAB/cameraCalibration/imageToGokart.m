% code by mg
function [physCoord] = imageToGokart(cameraParams, imgCoord, imgNumber)
% transforms image to physical coordinates in go-kart frame, assuming Z=0.
% functionality is analogous to imageToGokartUtil as implemented in the
% retina repository
%   cameraParams: object obtained with camera calibrator app
%   imgCoord: x and y coordinates in image plane [pixels]
%   imgNumber: extrinsics of following image are used

% form homogeneous coordinates
imgCoord = [imgCoord 1];

% normalize image coordinates
imgCoordNormalized(1) = (imgCoord(1)-cameraParams.PrincipalPoint(1))/cameraParams.FocalLength(1);
imgCoordNormalized(2) = (imgCoord(2)-cameraParams.PrincipalPoint(2))/cameraParams.FocalLength(2);

% undistort image coordinates
radDist = sqrt(imgCoordNormalized(1)^2+imgCoordNormalized(2)^2);
undistorted(1) = imgCoordNormalized(1)/(1+radDist^2*cameraParams.RadialDistortion(1)+radDist^4*cameraParams.RadialDistortion(2));
undistorted(2) = imgCoordNormalized(2)/(1+radDist^2*cameraParams.RadialDistortion(1)+radDist^4*cameraParams.RadialDistortion(2));
 
% unnormalize undistorted image coordinates
undistUnnormalized(1) = undistorted(1)*cameraParams.FocalLength(1)+cameraParams.PrincipalPoint(1);
undistUnnormalized(2) = undistorted(2)*cameraParams.FocalLength(2)+cameraParams.PrincipalPoint(2);

% compute extrsinsic transformation matrix with assumption Z=0
stackedMatrix = [cameraParams.RotationMatrices(:,:,imgNumber);cameraParams.TranslationVectors(imgNumber,1:3)];
intrinsics = cameraParams.IntrinsicMatrix;
transformationMatrix = stackedMatrix*intrinsics;
% since Z=0, we can remove third row
transformationMatrix(3,:) = [];

% transform image to physical coordinates
physCoord = [undistUnnormalized,1]*inv(transformationMatrix);

% enforce homogenous coordinates
physCoord = physCoord/physCoord(1,3);
physCoord = physCoord(1:2);
end

% code by mg
function [translationVectorHorizontal, transformationMatrixHorizontal] = ...
    transformToHorizontal(translationVectorVertical,transformationMatrixVertical, offset)
%transformToHorizontal Transforms extrinsics from an image taken at vertical
%position to one taken at horizontal position lying on the ground
%   translationVectorVertical: translation vector of vertical image,
%   obtained through calibration
%   transvormationMatrixVertical: extrinsic matrix of vertical image,
%   obtained through calibration
%   offset: [mm] measured form calibration pattern origin to cardboard edge

% translate to new origin lying on the gound
translationVectorHorizontal = translationVectorVertical + [0 offset offset];

% rotate -90 degrees around camera frame x axis
rotateToHorizontalMatrix = [1 0 0;0 0 -1;0 1 0];
transformationMatrixHorizontal = transformationMatrixVertical*rotateToHorizontalMatrix;
end


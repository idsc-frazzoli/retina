#include <byteswap.h>

int POINTSN = 10;

//note: not all values are necessarily known for every type of controller
struct State {
	float time;
	float Ux;
	float Uy;
	float dotPsi;
	float X;
	float Y;
	float Psi;
	float w2L;
	float w2R;
	float s;
	float btemp;
};

struct Control {
	//control: left power, right power, 
	float uL;
	float uR;
	//control: steering
	//send dotS as control input (use state value for actual control)
	float udotS;
	//control: braking
	float uB;
	//if we don't have direct motor control
	float aB;
};

struct PathParameter {
	float pointsN;
	float startingProgress;
	float [pointsN] controlPointsX;
	float [pointsN] controlPointsY;
	float [pointsN] controlPointsR;
};

struct OptimizationParameter {
	float speedLimit;
};

struct ControlAndStateMsg{
	int messageType;
	int sequenceInt;
	struct Control control;
	struct State state;
};

struct StateMsg{
	int messageType;
	int sequenceInt;
	struct State state;
};

struct PathMsg{
	int messageType;
	int sequenceInt;
	struct PathParameter path;
};

struct ParaMsg{
	int messageType;
	int sequenceInt;
	struct OptimizationParameter para;
};

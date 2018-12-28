#include <byteswap.h>
#define N 31

#define POINTSN 10

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
	float bTemp;
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

struct ControlAndState {
	struct Control control;
	struct State state;
};

struct PathParameter {
	float pointsN;
	float startingProgress;

	float controlPointsX [POINTSN];
	float controlPointsY [POINTSN];
	float controlPointsR [POINTSN];
};

struct OptimizationParameter {
	float speedLimit;
	float maxxacc;
	float maxyacc;
};

struct ControlAndStateMsg{
	int messageType;
	int sequenceInt;
	struct ControlAndState cns[N];
};

struct ControlRequestMsg{
	int messageType;
	int sequenceInt;
	struct State state;
	struct PathParameter path;
};


struct ParaMsg{
	int messageType;
	int sequenceInt;
	struct OptimizationParameter para;
};

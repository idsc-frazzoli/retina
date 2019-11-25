#include <byteswap.h>
#define N 31

#define POINTSN 10
#define NUMPARAM 21
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
	float beta;
	float dotbeta;
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

struct PathEntry {
  float pex;
  float pey;
  float per;
};

struct PathParameter {
	float pointsN;
	float startingProgress;
	struct PathEntry controlPoints[POINTSN];
};

struct OptimizationParameter {
	float speedLimit;
	float maxxacc;
	float steeringreg;
	float specificmoi;
	float pacejkaFB;
	float pacejkaFC;
	float pacejkaFD;
	float pacejkaRB;
	float pacejkaRC;
	float pacejkaRD;
	float steerStiff;
	float steerDamp;
	float steerInertia;
	float lagError;
	float latError;
	float progress;
	float regularizerAB;
	float speedCost;
	float slackSoftConstraints;
	float regularizerTV;
	float regTorque;
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

struct PacejkaParameter {
    float B1;
    float C1;
    float D1;
    float B2;
    float C2;
    float D2;
};

struct OnlineParam {
    float time;
    float vx;
    float vy;
    float vrotz;
    float tau;
    float ab;
    float tv;
};

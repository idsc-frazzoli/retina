#include <byteswap.h>

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

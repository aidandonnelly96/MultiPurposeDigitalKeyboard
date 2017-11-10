package synth;

public class LFO {
	public static double makelfo(double SR, double rate, double depth, String type,int index){

		double t=(double) index/SR;
		double saw,sawShift,square,tri;
		double outputLFO=0;
		

		double P;
		if (type.equals("triangle")) {
	   //see http://en.wikipedia.org/wiki/Triangle_wave for the equation
		P=1/(2*rate); //period of the triangle
		tri=(2/P)*(t-P*Math.floor(0.5+(t/P)))*Math.pow(-1, Math.floor(-0.5+(t/P)));
		outputLFO=depth*tri;
		}
		else if (type.equals("square")) {
			saw=2*((t*rate)%1)-1;
			sawShift=2*((0.5+(t*rate))%1)-1;
			square=saw-sawShift;
			outputLFO=depth*square;
		}
		else if (type.equals("sawtooth")){
			saw=2*((t*rate)%1)-1;
			outputLFO=depth*saw;
		}
		return outputLFO;
	}
}

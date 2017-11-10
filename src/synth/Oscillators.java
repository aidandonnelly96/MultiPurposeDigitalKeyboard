package synth;

public class Oscillators {

	
	public Oscillators(){
	}
	
	
	public static double geoSaw(double fhz, double Fs, int n){
		double wave;
		wave=2*((n*fhz/Fs)%1)-1;
		return wave;	
	}

	//generate a sawtooth using additive synthesis
	//but this is expensive
	public static double addSaw(double fhz, double Fs, int n){
	double wave=0;
	double Amp;
	double harmonicScale;

	double pi=Math.PI;
	int K=(int) Math.floor(Fs*0.5/fhz);
	//System.out.println(K);
	
	
	double harmonic;

	for (int k=1;k<K;k++){
		Amp=(double) Math.pow(k, -1);
		//Amp=1.5*Amp/pi;
		harmonic=sinWave(k*fhz, Fs,n);
		harmonicScale=harmonic*Amp;
		wave=wave+harmonicScale;
		}
	return wave;	
	}

	//method to generate a sinewave
	public static double sinWave(double freqHz, double SampFreq, int n){		
		double wave;
		double pi=Math.PI;
		double SampPeriod=(double) 1/SampFreq;
		wave=Math.sin((double) 2*pi*n*freqHz*SampPeriod);
		return wave;
	}
	
	public static void main(String [] args){
		int Fs=44100,fund=440;
		int durSamps=Fs;
		double[] swave=new double[durSamps];
		Oscillators os=new Oscillators();
		for(int i=0;i<durSamps;i++){
//			swave[i]=os.sinWave(fund, Fs, i);
			swave[i]=os.addSaw(fund, Fs, i);
		}
		StdAudio.play(swave);
		//StdAudio.save("mysqr.wav", swave);
		
	}

}

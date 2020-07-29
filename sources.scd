"loading particular synths".postln;

(
	sin: {|env, freq=0.01|
		freq = freq;
		AmpComp.ir(freq) * FSinOsc.ar(freq)
	},
	white: {|env, freq=444, width=1|
		var sig = env * WhiteNoise.ar();
		Resonz.ar(sig, freq, width)
	},
	pink: {|env, freq=444, width=1|
		var sig = env * PinkNoise.ar();
		BBandPass.ar(sig, freq, width)
	},
	chirp: {|env, freq=0.01|
		freq = freq;
		AmpComp.ir(freq) * FSinOsc.ar(env.linexp(0.0,1.0, freq/4, freq))
	},
	chirpp: {|env, freq=0.01, index1=0.5261234|
		freq = freq;
		AmpComp.ir(freq) * SinOsc.ar(SinOsc.ar(freq * index1, mul: env.linexp(0.0,1.0,freq, 1) * env.linexp(0.0,1.0, freq/4, freq)))
	},

	fm: {|env, freq=0.01, index1=1.12543124, index2=1.98521, dur|
		freq = freq;

		AmpComp.ir(freq) *
		SinOsc.ar(
			SinOsc.ar(
				SinOsc.ar(freq * index2,
					0, freq) * index1, 0, XLine.kr(freq * index1, freq * index2 , dur)
				)
			)
		},
	buf1: {| env, buffer, freq=1, trig=1, start=0, loop=1|
		freq = freq;
		PlayBuf.ar(1, buffer, freq * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop)
	},
	bufo1: {|env, buffer, freq=1, interpolation=4, start=0|
		var phasor = Phasor.ar(0, BufRateScale.kr(buffer) * freq, start * BufFrames.kr(buffer), BufFrames.kr(buffer));
		BufRd.ar(1, buffer,  phase: phasor,  loop: 1.0,  interpolation: interpolation);
	},
	bufo2: {|env, buffer, freq=1, interpolation=4, start=0|
		var phasor = Phasor.ar(0, BufRateScale.kr(buffer) * freq, start * BufFrames.kr(buffer), BufFrames.kr(buffer));
		BufRd.ar(2, buffer,  phase: phasor,  loop: 1.0,  interpolation: interpolation);
	},
	buf2: {| env, buffer, freq=1, trig=1, start=0, loop=1|
		freq = freq;
		PlayBuf.ar(2, buffer, freq * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop).sum
	},
	bufchirp1: {| env, buffer, freq=1, trig=1, start=0, loop=1|
		freq = freq * 10.0;
		freq = env.linlin(0.0,1.0, -1 * freq, freq);
		PlayBuf.ar(1, buffer, freq * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop)
	},
	bufchirp2: {| env, buffer, freq=1, trig=1, start=0, loop=1|
		freq = freq * 10.0;
		freq = env.linlin(0.0,1.0, 0.1, freq);
		PlayBuf.ar(2, buffer, freq * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop).sum
	},
	/*
	spectral1: {| env, buffer, freq=1, freezeAmount=0.5, trig=1, start=0, loop=1, spectralBuffer|
		var chain, clean, sig;
		freq = freq * 10.0;
		clean = PlayBuf.ar(2, buffer, freq * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop).sum;

		chain = FFT(spectralBuffer, clean);
		chain = PV_Freeze(chain, 1);
		sig = IFFT(chain);
		LinXFade2.ar(clean, sig, freezeAmount.linlin(0.0,1.0,-1.0,1.0));
	},
	spectral2: {| env, buffer, freq=1, freezeAmount=0.5, trig=1, start=0, loop=1, spectralBuffer|
		var chain, clean, sig;
		freq = freq * 10.0;
		clean = PlayBuf.ar(2, buffer, freq * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop).sum;

		chain = FFT(spectralBuffer, clean);
		chain = PV_Freeze(chain, 1);
		sig = IFFT(chain);
		LinXFade2.ar(clean, sig, freezeAmount.linlin(0.0,1.0,-1.0,1.0));
	}*/
)	

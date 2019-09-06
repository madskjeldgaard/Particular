"loading particular synths".postln;

(
	sin: {|env, freq=0.01|
		freq = freq.linexp(0.0,1.0,20.0, 15000.0);
		AmpComp.ir(freq) * FSinOsc.ar(freq)
	},
	chirp: {|env, freq=0.01|
		freq = freq.linexp(0.0,1.0,20.0, 15000.0);
		AmpComp.ir(freq) * FSinOsc.ar(env.linexp(0.0,1.0, freq/4, freq))
	},

	chirpp: {|env, freq=0.01, index1=0.5261234|
		freq = freq.linexp(0.0,1.0,20.0, 15000.0);
		AmpComp.ir(freq) * SinOsc.ar(SinOsc.ar(freq * index1, mul: env.linexp(0.0,1.0,freq, 1) * env.linexp(0.0,1.0, freq/4, freq)))
	},

	fm: {|env, freq=0.01, index1=1.12543124, index2=1.98521, dur|
		freq = freq.linexp(0.0,1.0,20.0, 15000.0);

		AmpComp.ir(freq) *
		SinOsc.ar(
			SinOsc.ar(
				SinOsc.ar(freq * index2,
					0, freq) * index1, 0, XLine.kr(freq * index1, freq * index2 , dur)
				)
			)
		},
		buf1: {| env, buffer, rate=1, trig=1, start=0, loop=1|
			rate = rate * 10.0;
			PlayBuf.ar(1, buffer, rate * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop)
		},
		buf2: {| env, buffer, rate=1, trig=1, start=0, loop=1|
			rate = rate * 10.0;
			PlayBuf.ar(2, buffer, rate * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop).sum
		},
		bufchirp1: {| env, buffer, rate=1, trig=1, start=0, loop=1|
			rate = rate * 10.0;
			rate = env.linlin(0.0,1.0, -1 * rate, rate);
			PlayBuf.ar(1, buffer, rate * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop)
		},
		bufchirp2: {| env, buffer, rate=1, trig=1, start=0, loop=1|
			rate = rate * 10.0;
			rate = env.linlin(0.0,1.0, 0.1, rate);
			PlayBuf.ar(2, buffer, rate * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop).sum
		}
	)	

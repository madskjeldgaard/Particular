Particular {
	classvar <>synthnames, <>envs, <>sources, <>ctk;
	*initClass{

		//this method makes sure that the IDict is compiled before
		//trying to make an instance of it
		Class.initClassTree(IdentityDictionary);

		envs       = IdentityDictionary.new;
		synthnames = IdentityDictionary.new;
		sources    = IdentityDictionary.new;
		ctk        = IdentityDictionary.new;

	}

	*new{
		^super.new.init;
	}

	init{
		envs.putAll((
            sine:      Env.sine,
            click:     Env([0,1,1,0], [0.1, 0.8, 0.1]),
            perc:      Env.perc(0.05, 0.95), // Percussive envelope
            revperc:   Env.perc(0.95, 0.05), // Reverse percussive envelope
            quasi:     Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \sin), // quasi gaussian
            three:     Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \lin), // Three line segment
            welch:     Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \welch), // Welch curve
            expodec:   Env([1, 0.01], [1.0], \exp), // Exponential decay
            rexpodec:  Env([0.01, 1], [1.0], \exp), // Reverse exponential decay
            m:         Env([0.001, 1, 0.25, 1, 0.001], [0.25, 0.25, 0.25, 0.25], \exp)
		));

		sources.putAll((
			sin: {| freq=440|
				AmpComp.ir(freq) * FSinOsc.ar(freq)
			},
            fm: {|freq=440, index1=1.12543124, index2=1.98521, dur|
                AmpComp.ir(freq) *
                    SinOsc.ar(
                        SinOsc.ar(
                            SinOsc.ar(freq * index2,
                        0, freq) * index1, 0, XLine.kr(freq * index1, freq * index2 , dur)
                    )
                )
            },
            buf1: {| buffer, rate=1, trig=1, start=0, loop=0|
				PlayBuf.ar(1, buffer, rate * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop)
			},
			buf2: {| buffer, rate=1, trig=1, start=0, loop=0|
				PlayBuf.ar(2, buffer, rate * BufRateScale.ir(buffer), trig, start * BufFrames.ir(buffer), loop).sum
			},
			in: {| inBus=0|
				SoundIn.ar(inBus)
			}
		));

		this.makeSynths();

        ^this;

	}

    // Get a particular synthdef's name
    // TODO: Add check if chosen synth exists
    def{
        arg envelopetype = "rexpodec",
        instrument       = "sin",
        root             = "particular";

        ^(root ++ "_" ++ instrument ++ "_" ++ envelopetype).asSymbol

    }

    defs{
        ^synthnames;
    }

	ctkdefs{
        ^ctk;
    }

	// INFO
	postEnvs{|self|
		"[%][Particular][*] Available envelopes:".format(Date.getDate.format("%H:%M:%S")).postln;
		envs.keysValuesDo{|k,v| k.postln}
	}

	postSynths{|self|
		"[%][Particular][*] Available SynthDefs:".format(Date.getDate.format("%H:%M:%S")).postln;

		synthnames.do{|k| k.postln}
	}

	plotEnvs{|self|
		envs.keysValuesDo{|name, env| env.plot(name: name)}
	}

	// FUNC
	makeSynth{| synthname, envelope, sourcefunc|

		SynthDef(synthname, { |out, amp=1, sustain=0.01, pan=0.5|

			var snd = SynthDef.wrap(sourcefunc);
			var env = EnvGen.ar(envelope, timeScale:  sustain, doneAction:  2);

            snd = Pan2.ar(snd, pan.linlin(0.0,1.0,-1.0,1.0));

			OffsetOut.ar(out, snd * env * amp * 0.5);
		}, \ir.dup(4 + sourcefunc.argNames.size)).add;
	}

	makeCtkSynth{| synthname, envelope, sourcefunc|

			^CtkSynthDef(synthname, { |out, amp=0.1, sustain=0.01|

				var snd = SynthDef.wrap(sourcefunc);
				var env = EnvGen.ar(envelope, timeScale: sustain, doneAction: 0);

				OffsetOut.ar(out, snd * env * amp);
			}, \ir.dup(4 + sourcefunc.argNames.size));
		}

	makeSynths{| prefix='particular'|
		envs.keysValuesDo{|envname, env|
			sources.keysValuesDo{|sourcename, sourcefunc|
				var sdname = (prefix ++ '_' ++ sourcename ++ '_' ++ envname).asSymbol;

				synthnames[sourcename] = synthnames[sourcename].add(sdname);

				// ctk[sourcename][envname] = ctk[sourcename][envname] ?? IdentityDictionary.new;
				ctk[sourcename] = ctk[sourcename] ?? ();
				ctk[sourcename] = ctk[sourcename][ envname.asSymbol ] = this.makeCtkSynth(sdname, env, sourcefunc);

				this.makeSynth(sdname, env, sourcefunc);
			}
		}

        ^synthnames;
	}

}

Particular {
	classvar synthnames, <>envs, <>sources, quarkpath;

	classvar <>numchans;

	*initClass{

		//this method makes sure that the IDict is compiled before
		//trying to make an instance of it
		Class.initClassTree(IdentityDictionary);

		envs       = IdentityDictionary.new;
		synthnames = IdentityDictionary.new;
		sources    = IdentityDictionary.new;

		// Add custom type
		Event.addEventType(\particular, {|server|
			~type = \note; // Inherit from this eventtype
			~shape = ~shape ?? { ~particular.shapes.choose };
			~source = ~source ?? { ~particular.sources.choose };

			~instrument = ~particular.def(~shape, ~source );

			currentEnvironment.play;
		});



	}

	*new{ |numChannels=1|
		^super.new.init(numChannels);
	}

	init{ |numChannels|

		numchans = numChannels;

		quarkpath = Quark("Particular").localPath;

		envs.putAll(this.envsPath.load);
		sources.putAll(this.sourcesPath.load);

		this.makeSynths();

        ^this;
	}

	envsPath {
		^quarkpath +/+ "envs.scd"
	}

	sourcesPath {
		^quarkpath +/+ "sources.scd"
	}

	def {| env="expodec", source="sin"|

		^(source ++ env ++ numchans).asSymbol
	}

    defs{
        ^synthnames;
    }

    shapes{
        ^envs.keys.asArray;
    }

	sources{
        ^sources.keys.asArray;
    }


	// INFO
	postEnvs{|self|
		"[%][Particular][*] Available envelopes:".format(Date.getDate.format("%H:%M:%S")).postln;
		envs.keysValuesDo{|k,v| k.postln}
	}

	envs{
		^envs
	}

	postSynths{|self|
		"[%][Particular][*] Available SynthDefs:".format(Date.getDate.format("%H:%M:%S")).postln;

		synthnames.do{|k| k.postln}
	}

	plotEnvs{|self|
		envs.keysValuesDo{|name, env| env.plot(name: name)}
	}

	panFunction {
		var panfunc;

		panfunc = case
		{ numchans < 3 } { 
			{ |snd, pan=0.5| Pan2.ar(snd, pan.linlin(0.0,1.0,-1.0,1.0)) }			
		}
		// { numchans == 2 } { 
		// 	{ |snd, pan=0.5| Balance2.ar(snd[0], snd[1], pan.linlin(0.0,1.0,-1.0,1.0)) }			
		// }
		{ numchans > 2 } { 
			{ |snd, pan=0.5, width=2| PanAz.ar(numchans, snd, pan.linlin(0.0,1.0,-1.0,1.0), width: width) }			
		};

	^panfunc

	}

	// FUNC
	makeSynth{| synthname, envelope, sourcefunc|
		var numargs = 3 + sourcefunc.argNames.size + this.panFunction.argNames.size;

		SynthDef(synthname, { |out, amp=1, sustain=0.1|

			var env = EnvGen.ar(envelope, timeScale:  sustain, doneAction:  2);
			var snd = SynthDef.wrap(sourcefunc, prependArgs: [env]);

            // snd = Pan2.ar(snd, pan.linlin(0.0,1.0,-1.0,1.0));
			snd = SynthDef.wrap(this.panFunction, prependArgs: [snd]);

			OffsetOut.ar(out, snd * env * amp );
		}, \ir.dup(numargs)).add;
	}

	makeSynths{
		envs.keysValuesDo{|envname, env|
			sources.keysValuesDo{|sourcename, sourcefunc|
				var sdname = this.def(envname, sourcename);

				synthnames[sourcename] = synthnames[sourcename].add(sdname);

				this.makeSynth(sdname, env, sourcefunc);
			}
		}

        ^synthnames;
	}
}

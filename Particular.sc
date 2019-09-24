Particular {
	classvar synthnames, envs, sources, quarkpath;

	var numchans;

	*initClass{

		//this method makes sure that the IDict is compiled before
		//trying to make an instance of it
		Class.initClassTree(IdentityDictionary);

		envs       = IdentityDictionary.new;
		synthnames = IdentityDictionary.new;
		sources    = IdentityDictionary.new;

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

	def {|source="sin", env="expodec"|

		^(source ++ env ++ numchans).asSymbol
	}

    defs{
        ^synthnames;
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
		{ numchans == 1 } { 
			{ |snd, pan=0.5| Pan2.ar(snd, pan.linlin(0.0,1.0,-1.0,1.0)) }			
		}
		{ numchans == 2 } { 
			{ |snd, pan=0.5| Balance2.ar(snd[0], snd[1], pan.linlin(0.0,1.0,-1.0,1.0)) }			
		}
		{ numchans > 2 } { 
			{ |snd, pan=0.5, width=2| PanAz.ar(numchans, snd, pan.linlin(0.0,1.0,-1.0,1.0), width: width) }			
		};

	^panfunc

	}

	// FUNC
	makeSynth{| synthname, envelope, sourcefunc|
		var numargs = 3 + sourcefunc.argNames.size + this.panFunction.argNames.size;

		SynthDef(synthname, { |out, amp=1, sustain=0.01|

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
				var sdname = this.def(sourcename, envname);

				synthnames[sourcename] = synthnames[sourcename].add(sdname);

				this.makeSynth(sdname, env, sourcefunc);
			}
		}

        ^synthnames;
	}
}

// Todo: Plot all envelopes in one window including test buttons for each
ParticularPlots {
	*new{ |particularEnvs|
		^super.new.init(particularEnvs);
	}

	init{ |particularEnvs|

	}
}

Particular {
	classvar <>synthnames, <>envs, <>sources, <>ctk, quarkpath;
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

		quarkpath = Quark("Particular").localPath;

		envs.putAll((quarkpath +/+ "envs.scd" ).load);
		sources.putAll((quarkpath +/+ "sources.scd" ).load);

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

	// FUNC
	makeSynth{| synthname, envelope, sourcefunc|

		SynthDef(synthname, { |out, amp=1, sustain=0.01, pan=0.5|

			var env = EnvGen.ar(envelope, timeScale:  sustain, doneAction:  2);
			var snd = SynthDef.wrap(sourcefunc, prependArgs: [env]);

            snd = Pan2.ar(snd, pan.linlin(0.0,1.0,-1.0,1.0));

			OffsetOut.ar(out, snd * env * amp * 0.5);
		}, \ir.dup(4 + sourcefunc.argNames.size)).add;
	}

	makeCtkSynth{| synthname, envelope, sourcefunc|

			^CtkSynthDef(synthname, { |out, amp=0.1, sustain=0.01, pan=0.5|

				var env = EnvGen.ar(envelope, timeScale: sustain, doneAction: 2);
				var snd = SynthDef.wrap(sourcefunc, prependArgs: [env]);

				snd = Pan2.ar(snd, pan.linlin(0.0,1.0,-1.0,1.0));

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

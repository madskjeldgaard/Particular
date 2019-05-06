# Particular
![hello particle my old friend](Particular.png "Particlez")

Easily accessable particle synth defs, including Ctk-synthdefs for off line use, in SuperCollider. 

This is an old SuperCollider trick that lets you create granular synthesis on a per-particle basis. This package is just a way to do that more easily.

### Example
```
p = Particular.new;
Pdef(\g1).play;

// Random sine grains
Pdef(\g1,
    Pbind(\instrument, p.def("expodec", "sin"), \dur, Pwhite(0.01, 0.25), \freq, Pkey(\dur).reciprocal * 10000)
);

// Random fm grains with random envelopes
Pdef(\g1,
    Pbind(\instrument, Pxrand(p.defs['fm'], inf), \dur, Pwhite(0.01, 0.25), \freq, Pkey(\dur).reciprocal * 10000)
);

```

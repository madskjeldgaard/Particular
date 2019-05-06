# Particular
![hello particle my old friend](Particular.png "Particlez")

A SuperCollider package for particle synthesis. 

This is an old SuperCollider trick that lets you create granular synthesis on a per-particle basis. This package is just a way to do that more easily.

### Installation

In SuperCollider, evaluate the following code to install it as a quark:
`Quarks.install("https://github.com/madskjeldgaard/Particular.git");`

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

"loading particular envs".postln;

(
	sine: Env.sine,
	sinc: Env( Array.series(32, -16, 1).collect{|x| if(x == 0, {1}, { x.sin/x })}, {1/31}!31),
	click: Env([0,1,1,0], [0.01, 0.98, 0.01]),
	quasi: Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \sin), // quasi gaussian
	three: Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \lin), // Three line segment
	welch: Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \welch), // Welch curve
	expodec: Env([1, 0.0001], [1.0], \exp), // Exponential decay
	rexpodec: Env([0.0001, 1], [1.0], \exp), // Reverse exponential decay
) 

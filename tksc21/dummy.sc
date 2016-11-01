s.boot;
s.quit;

/*
synthdef template

SynthDef(\xxx, {|ch=0, freq=100, amp=0.5, attack=0.001, decay=0.2, pan=0|
  var out, env;
  env = EnvGen.kr(Env.perc(0.001, 0.5), doneAction: 2);

  OffsetOut.ar(ch, Pan2.ar(out * env * amp, pan));
}).store;
*/



(
SynthDef(\pulse_bass, {|ch=0, freq = 100, decay=1, amp=0.5, pan=0|
  var out, env;
  env = EnvGen.kr(Env.perc(0.001, decay), doneAction: 2);
  out = RLPF.ar(Pulse.ar(freq, 0.01), 500 + (1000 * env), 0.3);
  OffsetOut.ar(ch, Pan2.ar(out * env * amp, pan));
}).store;

SynthDef(\saw_bass, {|ch=0, freq = 100, amp=0.5, pan=0|
  var out, env;
  env = EnvGen.kr(Env.perc(0.001, 0.5), doneAction: 2);
  out = RLPF.ar(Saw.ar(freq), 500 + (1000 * env), 0.3);
  OffsetOut.ar(ch, Pan2.ar(out * env * amp, pan));
}).store;

SynthDef(\pm_kick, {|ch=0, decay=0.3, amp=0.5, pan=0|
  var env, out;
  env = EnvGen.kr(Env.perc(0.0001, decay, curve:-8), doneAction: 2);
  out = PMOsc.ar(500 * env + 5, 20 * env + 2, 15 * env + 2, 0.7 * env, 0.8) + SinOsc.ar(30, 0.5, 0.2);
  OffsetOut.ar(ch, Pan2.ar(out * env * amp, pan));
}).store;

SynthDef(\n_snare, {|ch=0, freq=10000, decay=0.3, amp=0.5, pan=0|
  var env, out;
  env = EnvGen.kr(Env.perc(0.0001, decay, curve:-4), doneAction: 2);
  out = LFNoise2.ar(freq + (4000 * env));
  OffsetOut.ar(ch, Pan2.ar(out * env * amp, pan));
}).store;

SynthDef(\n_hat, {|ch=0, freq=18000, decay=0.1, amp=0.5, pan=0|
  var env, out;
  env = EnvGen.kr(Env.perc(0.0001, decay, curve:-8), doneAction: 2);
  out = RHPF.ar(LFNoise2.ar(freq), 12000, 0.1, 0.9) + PMOsc.ar(freq*1.8, freq*0.2, 0.7, 0.1, 0.1);
  OffsetOut.ar(ch, Pan2.ar(out * env * amp, pan));
}).store;

SynthDef(\pm_synth, { |ch=0, freq=1000, decay=0.2, amp=0.5, pan=0|
  var env, out;
  env = EnvGen.kr(Env.perc(0.0001, decay, curve:-4), doneAction: 2);
  out = PMOsc.ar(freq, freq * 1.08, 20 * env, 0.3 * env);
  OffsetOut.ar(ch, Pan2.ar(out * env * amp, pan));
}).store;

SynthDef(\saw_pad, { |ch=0, freq=1000, pan=0, attack=0.03, decay=0.8, amp=0.5, gate=1 |
  var env, out;
  env = EnvGen.kr(Env.perc(attack, decay, curve:-4), gate, doneAction: 2);
  out = RLPF.ar(
    Saw.ar([freq, freq*0.99, freq*1.01], [0.6, 0.2, 0.2]),
    freq * 8,
    0.3
  );
  OffsetOut.ar(ch, Pan2.ar(out * env * amp, pan));
}).store;


SynthDef(\)

)


(
~synths = [
  \pulse_bass,
  \pm_synth,
  \saw_pad,
  \pm_kick,
  \n_snare,
  \n_hat,
];

OSCdef(\zzz, {|msg, time, addr, recvPort|
  var d = (\ch: -1, \gate: -1, \note: -1, \device: 'es', \cv: 0, \length: 0.001),
  ch = -1, gate = -1, note = -1, device, cv, length, params = [];
  msg.postln;
  msg[3..].pairsDo({ |k, v| d.put(k, v); });
  ch = d.at(\ch);
  gate = d.at(\gate);
  length = d.at(\length);
  note = d.at(\note);
  device = d.at(\device);
  cv = d.at(\cv);

  if (note > 0, {
    params = params.add(\freq);
    params = params.add(note.midicps);
  });
  if (gate == 1, { Synth(~synths[ch - 1], params)});
}, "/zzz", nil, 12345);

)



// test

(
TempoClock.tempo = 1.5;

Routine({
  loop{
    Synth(\pm_kick);
    0.5.wait;
    Synth(\n_snare);
    0.5.wait;
  }
}).play;
Routine({
  loop{
    Synth(\n_hat);
    0.125.wait;
  }
}).play;

Routine({
  loop{
    Synth(\pm_synth, [\freq, [48, 50, 51, 54, 55].choose.midicps, \decay, 0.5.rand + 0.1]);
    [0.125, 0.25].choose.wait;
  }
}).play;

)

(
s.options.device = "MOTU 828mk3";
s.options.sampleRate = 44100;
s.options.numBuffers = 64;
s.options.numOutputBusChannels = 30;
s.options.numInputBusChannels = 32;
s.boot;
)

s.quit

(
SynthDef(\cv, {|ch=14, val=0|
  OffsetOut.ar(ch, DC.ar(1) * val);
}).add;

SynthDef(\es5, {|ch=20, val=1, time=0.001, t_trig=0|
  var e;
  e = EnvGen.ar(Env([0, 1, 1, 0], [0, time, 0]), gate: t_trig);
  OffsetOut.ar(ch, e * val);
}).add;

~ch2val = { |ch| if (ch == 7) { -1 } {(1 << ch) / 127} };
~n2cv = { |n| (1 / 127) * n };

~motu = Array.fill(8, { |i| Synth(\cv, [\ch, i]) });
~es5 = Array.fill(8, { |i| Synth(\es5, [\ch, 20, \val, ~ch2val.(i)]) });
~es3 = Array.fill(8, { |i| Synth(\cv, [\ch, 14 + i]) });

OSCdef(\zzz, {|msg, time, addr, recvPort|
  var d = (\ch: -1, \gate: -1, \note: -1, \device: 'es', \cv: false, \length: 0.001),
      ch = -1, gate = -1, note = -1, device, cv, length;
  msg.postln;
  msg[3..].pairsDo({ |k, v| d.put(k, v); });
  ch = d.at(\ch);
  gate = d.at(\gate);
  length = d.at(\length);
  note = d.at(\note);
  device = d.at(\device);
  cv = d.at(\cv);
  if (device == 'm') {
    ~motu[ch - 1].set(\val, cv);
  } {
    if (gate == 1, { ~es5[ch - 1].set(\t_trig, 1, \time, length); });
    if (note >= 0,
      { ~es3[ch - 1].set(\val, ~n2cv.(note)); },
      { ~es3[ch - 1].set(\val, cv); }
    );
  }
}, "/zzz", nil, 12345);
)


a = { Out.ar(0, SinOsc.ar(1000)) }.play
b = { Out.ar(1, SinOsc.ar(800)) }.play

a.free
b.free
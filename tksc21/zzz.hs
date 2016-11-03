{-# LANGUAGE NoMonomorphismRestriction #-}

module Sound.Tidal.Zzz where

import Sound.Tidal.Stream
import Sound.Tidal.Pattern
import Sound.Tidal.Parse
import Sound.OSC.FD
import Sound.Tidal.OscStream

zzz :: [Param] -> Shape
zzz ps = Shape {
  params = ps,
  cpsStamp = True,
  latency = 0
  }

zzzSlang path = OscSlang {
  path = path,
  preamble = [],
  namedParams = True,
  timestamp = NoStamp
  }

zzzBackend path port = do
  s <- makeConnection "127.0.0.1" port (zzzSlang path)
  return $ Backend s (\_ _ _ -> return ())

zzzStream path port ps = do let shape = (zzz ps)
                            backend <- zzzBackend path port
                            z <- stream backend shape
                            return z

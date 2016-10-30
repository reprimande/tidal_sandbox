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

zzzSlang = OscSlang {
  path = "/zzz",
  preamble = [],
  namedParams = True,
  timestamp = BundleStamp
  }

zzzBackend = do
  s <- makeConnection "127.0.0.1" 12345 zzzSlang
  return $ Backend s (\_ _ _ -> return ())

zzzStream ps = do let shape = (zzz ps)
                  backend <- zzzBackend
                  z <- stream backend shape
                  return z

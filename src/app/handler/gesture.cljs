(ns app.handler.gesture
  (:require
    [reagent.core :as r]
    [steroid.rn.core :as rn]
    ["react-native-gesture-handler" :refer [PanGestureHandler TapGestureHandler FlingGestureHandler State]]
    [applied-science.js-interop :as j]))

(def pan-gesture-handler (r/adapt-react-class PanGestureHandler))

(def tap-gesture-handler (r/adapt-react-class TapGestureHandler))

(def fling-gesture-handler (r/adapt-react-class FlingGestureHandler))

(def state State)

(defn tap-state-end [evt]
  (=  (j/get state :END)
      (j/get evt :state)))

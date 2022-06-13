(ns app.handler.animated
  (:require
    [reagent.core :as r]
    [steroid.rn.core :as rn]
    ["react-native-reanimated" :default Animated]
    ["react-native" :refer [FlatList]]))


(def animated-flat-list (r/adapt-react-class (.createAnimatedComponent Animated FlatList)))

(def animated-view (r/adapt-react-class (.-View Animated)))

(def animated-value (.-Value Animated))

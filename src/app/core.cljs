(ns app.core
  (:require
   [steroid.rn.core :as rn]
   [app.ui.views :as views]
   [honey.sql :as hsql]
   ["react-native-sqlite-storage" :as sqlite]
   ["react-native-measure-text-chars" :refer [measure]]
   app.events
   app.subs))


(defn init []

  (rn/register-comp "TaMednuu" views/root-stack))

(ns app.ui.keyboard.index
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [app.ui.components :as ui]
   [app.ui.keyboard.candidates :as candidates]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [app.ui.nativebase :as nbase]
   [app.ui.keyboard.common :as keycommon]
   [app.ui.keyboard.layout :as layout]

   ["react-native-advanced-ripple" :as ripple]))

(defn keyboard []
  (let [alter @(subscribe [:keyboard-alter])]
    [nbase/box {:style {:flex-direction "column"
                        :flex 1
                        :height "100%"}}
     ;; keyboard
     (if (true? alter)
       [layout/en-layout]
       [layout/mn-layout])]))

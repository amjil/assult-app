(ns app.ui.keyboard.index
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [app.ui.components :as ui]
   [app.ui.keyboard.candidates :as candidates]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [app.ui.nativebase :as nbase]
   [app.ui.keyboard.style :refer [key-style key-con-style key-text-style key-box-style]]
   [app.ui.keyboard.common :as keycommon]
   [app.ui.keyboard.layout :as layout]

   ["react-native-advanced-ripple" :as ripple]))

(defn keyboard []
  [nbase/box {:style {:flex-direction "column"
                      :flex 1}}
                      ; :height "100%"}}
   ;; keyboard
   ; [layout/mn-layout]
   [layout/en-layout]])

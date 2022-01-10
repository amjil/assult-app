(ns app.ui.keyboard.common
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [app.ui.components :as ui]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [app.ui.nativebase :as nbase]
   [app.ui.keyboard.style :refer [key-style key-con-style key-text-style]]

   ["react-native-advanced-ripple" :as ripple]))

(defn key-content [child]
  [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
   child])

(defn key-button [box-style on-press child]
  [nbase/button (merge
                  {:bg "coolGray.100"
                   :flex 1
                   :m 1
                   :p 0
                   :borderRadius "md"
                   :on-press on-press
                   :align "center"
                   :justify "center"}
                  box-style)
    [key-content child]])

(defn key-char-button
  ([c]
   [key-button {} #(dispatch [:keyboard-add-char c])
    [nbase/text {:fontSize 18} c]])
  ([style c]
   [key-button {} #(dispatch [:keyboard-add-char c])
    [nbase/text {} c]]))


(defn key-row [child]
  (into
    [nbase/box {:style {:flex 1 :flex-direction "row"
                        :alignItems "center"
                        :justifyContent "center"}}]
    child))

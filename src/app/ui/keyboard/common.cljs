(ns app.ui.keyboard.common
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [app.ui.components :as ui]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [app.ui.nativebase :as nbase]
   [app.ui.keyboard.style :refer [key-style key-con-style key-text-style key-box-style]]

   ["react-native-advanced-ripple" :as ripple]))

(defn key-content [child]
  [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
   child])

(defn key-button [box-style on-press child]
  [nbase/box {:style (merge key-con-style box-style)}
   [:> ripple {:rippleColor "#000" :style key-style
               :on-press on-press}
    [key-content child]]])

(defn key-char-button [c]
  [key-button {} #(dispatch [:keyboard-add-char c])
   [nbase/text {} c]])

(defn key-row [child]
  (into
    [nbase/box {:style {:flex 1 :flex-direction "row"
                        :alignItems "center"
                        :justifyContent "center"}}]
    child))

(ns app.ui.keyboard.en
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
  (into
    [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}]
    child))

(defn key-button [box-style child]
  [nbase/box {:style (merge key-con-style box-style)}
   [:> ripple {:rippleColor "#000" :style key-style}
    [key-content child]]])

(defn key-char-button [c]
  [key-button {}
   [nbase/text {} c]])


(defn key-row [child]
  (into
    [nbase/box {:style {:flex 1 :flex-direction "row"
                        :alignItems "center"
                        :justifyContent "center"}}]
    child))

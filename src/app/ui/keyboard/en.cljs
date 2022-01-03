(ns app.ui.keyboard.en
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [app.ui.components :as ui]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [app.ui.nativebase :as nbase]

   ["react-native-advanced-ripple" :as ripple]))

(defn key-content [child]
  (into
    [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}]
    child))

(defn key-button [box-style ripple-style child]
  [nbase/box {:style box-style}
   [:> ripple {:rippleColor "#000" :style ripple-style}
    [key-content child]]])

(defn key-row [child]
  (into
    [nbase/box {:style {:flex 1 :flex-direction "row"
                        :alignItems "center"
                        :justifyContent "center"}}]
    child))


(comment
  (into [:a {:style {:foo :bar}}]
    [[:foo] [:bar]]))

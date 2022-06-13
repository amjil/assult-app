(ns app.ui.basic.menu
  (:require
   [reagent.core :as reagent]
   [app.ui.components :as ui]
   [app.ui.text :as text]
   [app.ui.nativebase :as nbase]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   [steroid.rn.core :as rn]

   ["react-native" :refer [Dimensions]]))

(defn index [{items :items}]
  "items => [{:name \"a\" :on-press #(some fn)} ... ]"
  (let [is-open (reagent/atom false)
        screen-height (.-height (.get Dimensions "window"))]
    (fn []
      [nbase/modal {:isOpen @is-open :onClose #(reset! is-open false)}
       [nbase/box {:bg "coolGray.50" :shadow 1 :rounded "lg" :maxHeight (str (- screen-height 150) "px")
                   :minHeight "40%" :overflow "hidden"}
        (into
          [nbase/box
           {:flex 1 :justifyContent "center" :alignItem "center" :flexDirection "row"
            :pt "2" :py "3"}]
          (interpose
           [nbase/divider {:orientation "vertical"}]
           (for [{name :name press-fn :on-press} items]
             ^{:key (:name item)}
             [rn/touchable-highlight {:style {:padding 10}
                                      :underlayColor "#cccccc"
                                      :onPress press-fn}
              [text/measured-text {:color "#4b5563"} name]])))]])))

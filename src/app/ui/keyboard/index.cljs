(ns app.ui.keyboard.index
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [app.ui.components :as ui]
   [app.ui.keyboard.candidates :as candidates]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [app.ui.nativebase :as nbase]

   ["react-native-advanced-ripple" :as ripple]))

(def key-style
  {
          :flex-direction "row"
          :flex 1
          :justifyContent "center"
          :alignItems "center"
          :backgroundColor "#FFF"
          :borderRightColor "#e8e8e8"
          :borderRightWidth 1
          :borderBottomColor "#e8e8e8"
          :borderBottomWidth 1})
          ; :height 38})

(def key-con-style
  {:backgroundColor "#FFF"
   :borderRightColor "#e8e8e8"
   :borderRightWidth 1
   :borderBottomColor "#e8e8e8"
   :borderBottomWidth 1
   :flex 1})

(def key-text-style
  {
    :fontWeight "400"
    :fontSize 25,
    :textAlign "center",
    :color "#222222"
    :width 42})

(def key-list [[{:label "ᠣ" :code "q"} {:label "ᠸ᠊" :code "w"} {:label "ᠡ" :code "e"}
                {:label "ᠷ᠊" :code "r"} {:label "ᠲ᠊" :code "t"} {:label "ᠶ᠊" :code "y"}
                {:label "ᠦ᠊" :code "u"} {:label "ᠢ" :code "i"} {:label "ᠥ" :code "o"} {:label "ᠫ᠊" :code "p"}]
               [{:label "ᠠ" :code "a"} {:label "ᠰ᠊" :code "s"} {:label "ᠳ" :code "d"} {:label "ᠹ᠊" :code "f"}
                {:label "ᠭ᠊" :code "g"} {:label "ᠬ᠊" :code "h"} {:label "ᠵ᠊" :code "j"}
                {:label "ᠺ᠊" :code "k"} {:label "ᠯ᠊" :code "l"} {:label " ᠩ" :code "ng"}]
               [{:label "ᠽ᠊" :code "z"} {:label "ᠱ᠊" :code "x"} {:label "ᠴ᠊" :code "c"}
                {:label "ᠤ᠊" :code "v"} {:label "ᠪ᠊" :code "b"}
                {:label "ᠨ᠊" :code "n"} {:label "ᠮ᠊" :code "m"}]])

(defn keyboard []
  [nbase/box {:style {:flex-direction "column"
                      :flex 1
                      :height 300}}
   ;; keyboard
   [nbase/box {:style {:flex nil
                       :width "100%"
                       :flex-direction "column"
                       :justifyContent "flex-end"
                       :height 180
                       :borderTopWidth 1
                       :borderTopColor "#e8e8e8"}}
     (doall (for [k (take 2 key-list)]
              ^{:key k}
              [nbase/box {:style {:flex 1 :flex-direction "row"
                                  :alignItems "center"
                                  :justifyContent "center"}}
               (doall (for [kk k]
                        ^{:key kk}
                        [nbase/box {:style key-con-style}
                         [:> ripple {:rippleColor "#000" :style key-style
                                     :on-press #(dispatch [:candidates-index-concat (:code kk)])}
                          [nbase/box {:style {:height "100%" :width "100%"
                                              :alignItems "center"
                                              :justifyContent "center"}}
                           [nbase/rotated-text {:font-family "MongolianBaiZheng" :font-size 18} 28 28 (:label kk)]]]]))]))
    [nbase/box {:style {:flex 1 :flex-direction "row"
                        :alignItems "center"
                        :justifyContent "center"}}
     [nbase/box {:style (merge key-con-style {:flex 1.5})}
      [:> ripple {:rippleColor "#000" :style key-style}
       [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
        [ui/ion-icons {:name "ios-arrow-up-circle-outline" :color "gray" :size 30}]]]]
     (doall (for [kk (nth key-list 2)]
              ^{:key kk}
              [nbase/box {:style key-con-style}
               [:> ripple {:rippleColor "#000" :style key-style
                           :on-press #(dispatch [:candidates-index-concat (:code kk)])}
                [nbase/box {:style {:height "100%" :width "100%"
                                    :alignItems "center"
                                    :justifyContent "center"}}
                 [nbase/rotated-text {:font-family "MongolianBaiZheng" :font-size 18} 28 28 (:label kk)]]]]))
     [nbase/box {:style (merge key-con-style {:flex 1.5})}
      [:> ripple {:rippleColor "#000" :style key-style
                  :on-press #(dispatch [:keyboard-delete])}
       [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
        [ui/ion-icons {:name "backspace" :color "gray" :size 30}]]]]]
    [nbase/box {:style {:flex 1 :flex-direction "row"
                        :alignItems "center"
                        :justifyContent "center"}}
     [nbase/box {:style (merge key-con-style {:flex 1.5})}
      [:> ripple {:rippleColor "#000" :style key-style}
       [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
        [nbase/text {} "123"]]]]
     [nbase/box {:style (merge key-con-style {:flex 1.5})}
      [:> ripple {:rippleColor "#000" :style key-style}
       [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
        [ui/ion-icons {:name "globe" :color "gray" :size 30}]]]]
     [nbase/box {:style (merge key-con-style {:flex 1})}
      [:> ripple {:rippleColor "#000" :style key-style
                  :on-press #(dispatch [:keyboard-add-char "᠂"])}
       [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
        [nbase/text {} "᠂"]]]]
     [nbase/box {:style (merge key-con-style {:flex 3.5})}
      [:> ripple {:rippleColor "#000" :style key-style
                  :on-press #(dispatch [:keyboard-add-char " "])}
       [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
        [ui/ion-icons {:name "ios-scan" :color "gray" :size 30}]]]]
     [nbase/box {:style (merge key-con-style {:flex 1})}
      [:> ripple {:rippleColor "#000" :style key-style
                  :on-press #(dispatch [:keyboard-add-char "᠃"])}
       [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
        [nbase/text {} "᠃"]]]]
     [nbase/box {:style (merge key-con-style {:flex 1.5})}
      [:> ripple {:rippleColor "#000" :style key-style
                  :on-press #(dispatch [:candidates-query 2])}
       [nbase/box {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
        [ui/ion-icons {:name "ios-return-down-back-sharp" :color "gray" :size 30}]]]]]]])

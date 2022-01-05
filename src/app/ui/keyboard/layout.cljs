(ns app.ui.keyboard.layout
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [app.ui.components :as ui]
   [app.ui.keyboard.candidates :as candidates]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [clojure.string :as str]
   [app.ui.nativebase :as nbase]
   [app.ui.keyboard.style :refer [key-style key-con-style key-text-style key-box-style]]
   [app.ui.keyboard.common :as keycommon]

   ["react-native-advanced-ripple" :as ripple]))

(def en-key-list
  [["q" "w" "e" "r" "t" "y" "u" "i" "o" "p"]
   ["a" "s" "d" "f" "g" "h" "j" "k" "l"]
   ["z" "x" "c" "v" "b" "n" "m"]])

(def mn-key-list [[{:label "ᠣ" :code "q"} {:label "ᠸ᠊" :code "w"} {:label "ᠡ" :code "e"}
                   {:label "ᠷ᠊" :code "r"} {:label "ᠲ᠊" :code "t"} {:label "ᠶ᠊" :code "y"}
                   {:label "ᠦ᠊" :code "u"} {:label "ᠢ" :code "i"} {:label "ᠥ" :code "o"} {:label "ᠫ᠊" :code "p"}]
                  [{:label "ᠠ" :code "a"} {:label "ᠰ᠊" :code "s"} {:label "ᠳ" :code "d"} {:label "ᠹ᠊" :code "f"}
                   {:label "ᠭ᠊" :code "g"} {:label "ᠬ᠊" :code "h"} {:label "ᠵ᠊" :code "j"}
                   {:label "ᠺ᠊" :code "k"} {:label "ᠯ᠊" :code "l"} {:label " ᠩ" :code "ng"}]
                  [{:label "ᠽ᠊" :code "z"} {:label "ᠱ᠊" :code "x"} {:label "ᠴ᠊" :code "c"}
                   {:label "ᠤ᠊" :code "v"} {:label "ᠪ᠊" :code "b"}
                   {:label "ᠨ᠊" :code "n"} {:label "ᠮ᠊" :code "m"}]])

(defn toolkit-row [alter alter-num]
  [keycommon/key-row
   [[keycommon/key-button {:flex 1.5} #(dispatch [:keyboard-alter-num])
     [nbase/text {} (if (true? alter-num) "ABC" "123")]]
    [keycommon/key-button {:flex 1.5} #(dispatch [:keyboard-alter])
     [ui/ion-icons {:name "globe" :color "gray" :size 30}]]
    (if (false? alter)
      [keycommon/key-char-button ","])
    [keycommon/key-button (if (false? alter) {:flex 3.5} {:flex 5.5}) #(dispatch [:keyboard-add-char " "])
     [ui/ion-icons {:name "ios-scan" :color "gray" :size 30}]]
    (if (false? alter)
      [keycommon/key-char-button "."])
    [keycommon/key-button {:flex 1.5} #(dispatch [:keyboard-add-char " "])
     [ui/ion-icons {:name "ios-return-down-back-sharp" :color "gray" :size 30}]]]])

(defn mn-layout []
  (let [shift       @(subscribe [:keyboard-shift])
        shift-num   @(subscribe [:keyboard-shift-num])
        alter       @(subscribe [:keyboard-alter])
        alter-num   @(subscribe [:keyboard-alter-num])]
    [nbase/box {:style key-box-style}
     (for [k (take 2 mn-key-list)]
       ^{:key k}
       [keycommon/key-row
        (for [kk k]
          ^{:key kk}
          [keycommon/key-button {} #(dispatch [:candidates-index-concat (:code kk)])
           [nbase/rotated-text {:font-family "MongolianBaiZheng" :font-size 18} 28 28 (:label kk)]])])
     [keycommon/key-row
      [
       [keycommon/key-button {:flex 1.5} #(dispatch [:keyboard-alter])
        [ui/ion-icons {:name "ios-arrow-up-circle-outline" :color "gray" :size 30}]]
       (for [kk (nth mn-key-list 2)]
         ^{:key kk}
         [keycommon/key-button {} #(dispatch [:candidates-index-concat (:code kk)])
          [nbase/rotated-text {:font-family "MongolianBaiZheng" :font-size 18} 28 28 (:label kk)]])
       [keycommon/key-button {:flex 1.5} #(dispatch [:keyboard-delete])
        [ui/ion-icons {:name "backspace" :color "gray" :size 30}]]]]
     [toolkit-row alter alter-num]]))

(defn en-layout []
  (let [shift       @(subscribe [:keyboard-shift])
        shift-num   @(subscribe [:keyboard-shift-num])
        alter       @(subscribe [:keyboard-alter])
        alter-num   @(subscribe [:keyboard-alter-num])]
    [nbase/box ;{:style key-box-style}
     {:bg "blueGray.400"
      :w "100%"
      :h "100%"
      :borderTopWidth 1
      :borderColor "gray.500"}
     [keycommon/key-row
      (for [kk (nth en-key-list 0)]
        ^{:key kk}
        [keycommon/key-char-button (if (true? shift) (str/upper-case kk) kk)])]
     ;
     [nbase/center
      [nbase/flex {:flex 1 :direction "row"
                    :align "center"
                    :justify "center"
                    :width "90%"}
       (for [kk (nth en-key-list 1)]
         ^{:key kk}
         [keycommon/key-char-button (if (true? shift) (str/upper-case kk) kk)])]]
     [keycommon/key-row
      [[keycommon/key-button {:flex 1.5} #(dispatch [:keyboard-shift])
        [ui/ion-icons {:name "ios-arrow-up-circle-outline" :color "gray" :size 30}]]
       (for [kk (nth en-key-list 2)]
         ^{:key kk}
         [keycommon/key-char-button (if (true? shift) (str/upper-case kk) kk)])
       [keycommon/key-button {:flex 1.5} #(dispatch [:keyboard-delete])
        [ui/ion-icons {:name "backspace" :color "gray" :size 30}]]]]
     [toolkit-row alter alter-num]]))

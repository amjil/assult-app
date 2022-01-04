(ns app.ui.keyboard.en
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [app.ui.components :as ui]
   [app.ui.nativebase :as nbase]
   [app.ui.keyboard.common :as keycommon]))

(def key-list
  [[:q :w :e :r :t :y :u :i :o :p]
   [:a :s :d :f :g :h :j :k :l]
   [:z :x :c :v :b :b :n :m]])

(defn alpha-board []
  (let [shift (reagent/atom false)]
    (fn []
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
        [key-row
         (for [kk (nth key-list 0)]
           ^{:key kk}
           [keycommon/key-char-button (str kk)])]
        [key-row
         (for [kk (nth key-list 1)]
           ^{:key kk}
           [keycommon/key-char-button (str kk)])]
        [key-row
         [[key-button {:flex 1.5} #(swap! shift not @shift)
           [ui/ion-icons {:name "ios-arrow-up-circle-outline" :color "gray" :size 30}]]
          (for [kk (nth key-list 2)]
            ^{:key kk}
            [keycommon/key-char-button (str kk)])
          [key-button {:flex 1.5} #(dispatch [:keyboard-delete])
           [ui/ion-icons {:name "backspace" :color "gray" :size 30}]]]]]])))

;; keyboard shift - alter

(comment
  (into [:a {:style {:foo :bar}}]
    [[:foo] [:bar]]))

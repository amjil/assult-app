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
  [
   [key-row
    (for [kk (nth key-list 0)]
      ^{:key kk}
      [keycommon/key-char-button (str kk)])]
   [key-row
    (for [kk (nth key-list 1)]
      ^{:key kk}
      [keycommon/key-char-button (str kk)])]
   [key-row
    [[key-button {} #(dispatch [:keyboard-shift c])
      [nbase/text {} c]]]]])

;; keyboard shift - alter

(comment
  (into [:a {:style {:foo :bar}}]
    [[:foo] [:bar]]))

(ns app.ui.home.answer
  (:require
   [applied-science.js-interop :as j]
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [app.ui.components :as ui]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [cljs-bean.core :as bean]
   [app.ui.text.index :as text]
   [app.ui.keyboard.index :as keyboard]
   [app.ui.keyboard.candidates :as candidates]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]))

(defn new-answer-view []
  (let [h (reagent/atom nil)
        font {:fontFamily "MongolianBaiZheng"}
        params {:name "Input" :props {:variant "filled" :fontSize 18}}
        atomic (reagent/atom {:focus false :text "" :flag false :height nil})]
    (fn []
      (let [rf-key :put-profile
            loading @(re-frame/subscribe [:loading rf-key])
            errors @(re-frame/subscribe [:errors rf-key])
            model @(re-frame/subscribe [:question])]
        [nbase/flex {:style {:height "100%"} :justifyContent "space-between" :safeArea true}
         [nbase/box {:flex 1 :mb 2
                     :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                   (swap! atomic assoc :height height))}
          [nbase/box {:flex-direction "row"
                      :flex 1}
           [nbase/measured-text (merge font {:fontSize 18 :height @h}) (:question_content model)]
           [nbase/box {:h @h :w 0.5 :bg "success.500" :mx 1}]
           [:f> text/text-input atomic params]]]
         [candidates/views]
         [nbase/box {:style {:height 220}}
          [keyboard/keyboard]]]))))

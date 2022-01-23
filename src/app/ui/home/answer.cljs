(ns app.ui.home.answer
  (:require
   [applied-science.js-interop :as j]
   [app.ui.nativebase :as nbase]
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
        [ui/safe-area-consumer
         [nbase/flex {:height "100%" ;:safeArea true
                      :justifyContent "space-between"
                      :pt 2
                      :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                    (swap! atomic assoc :height height))}
          [nbase/box {:flex-direction "row"
                      :flex 1}
           [nbase/measured-text (merge font {:fontSize 18 :height (:height @atomic)}) (:question_content model)]
           [nbase/box {:h @h :w 0.5 :bg "success.500" :mx 1}]
           [:f> text/text-input atomic params {:mx 2 :mb 2 :flex 1 :width (:height @atomic)}]]
          [candidates/views]
          [nbase/box {:height 220}
           [keyboard/keyboard]]]]))))

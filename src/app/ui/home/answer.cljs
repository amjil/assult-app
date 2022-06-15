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
            loading @(re-frame/subscribe [:loading :answer-])
            errors @(re-frame/subscribe [:errors rf-key])
            model @(re-frame/subscribe [:question])]
        [ui/safe-area-consumer
         [nbase/flex {:flex 1
                      :justifyContent "space-between"
                      :pt 2}
          [nbase/box {:flex-direction "row"
                      :flex 1
                      :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                    (swap! atomic assoc :height height))}
           [nbase/measured-text (merge font {:fontSize 18 :height (:height @atomic)}) (:question_content model)]
           [nbase/box {:h @h :w 0.5 :bg "success.500" :mx 1}]
           (if (:height @atomic)
             [:f> text/text-input atomic params {:mx 2 :mb 2 :flex 1 :width (- (:height @atomic) 20)}])]
          [candidates/views]
          [nbase/box {:height 220}
           [keyboard/keyboard]]]]))))

(defn new-answer-comment []
  (let [h (reagent/atom nil)
        font {:fontFamily "MongolianBaiZheng"}
        params {:name "Input" :props {:variant "filled" :fontSize 18}}
        atomic (reagent/atom {:focus false :text "" :flag false :height nil})]
    (fn []
      (let [model @(re-frame/subscribe [:answer])]
        [ui/safe-area-consumer
         [nbase/flex {:flex 1
                      :justifyContent "space-between"
                      :pt 2}
          [nbase/box {:flex-direction "row"
                      :flex 1
                      :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                    (swap! atomic assoc :height height))}
           (if (:height @atomic)
             [:f> text/text-input atomic params {:mx 2 :mb 2 :flex 1 :width (- (:height @atomic) 20)}])]
          [candidates/views]
          [nbase/box {:height 220}
           [keyboard/keyboard]]]]))))

(defn answer-detail []
  (let [h (reagent/atom nil)
        font {:fontFamily "MongolianBaiZheng"}]
    (fn []
      (let [answer @(re-frame/subscribe [:answer])
            question @(re-frame/subscribe [:question])
            comments @(re-frame/subscribe [:answer-comments])]
        [ui/safe-area-consumer
         [nbase/box {:on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                   (reset! h height))
                     :pt 2}
          [nbase/box {:height "100%" :flex-direction "row"}
           [nbase/measured-text (merge font {:fontSize 18 :height @h}) (:question_content question)]
           [nbase/box {:h @h :w 0.5 :bg "success.500" :mx 1}]
           [nbase/box
            [ui/userpic (:avatar_file answer) 32]
            [nbase/measured-text (merge font {:fontSize 14 :margin-top 4}) (:user_name answer)]]
           [nbase/box {:h @h :style {:width 1 :backgroundColor "lightgrey"}}]
           [nbase/measured-text (merge font {:fontSize 12 :height @h}) (:content answer)]

           [nbase/flex {:mx 1}
            [nbase/link {:on-press #(do
                                      (re-frame/dispatch [:navigate-to :answer-comment]))}
             [nbase/box {:py 1 :rounded "sm" :bg "primary.400"}
              [nbase/measured-text (merge font {:color "white" :fontSize 12}) "ᠰᠡᠳᠭᠡᠭᠳᠡᠯ"]]]]

           [nbase/box {:h @h :w 0.5 :bg "success.500" :mx 1}]

           [nbase/flat-list
            {:horizontal true
             :keyExtractor    (fn [_ index] (str "answer-comment-" index))
             :data comments
             :ItemSeparatorComponent
             (fn [] (reagent/as-element [nbase/box {:style {:width 4 :backgroundColor "lightgrey"}}]))
             :renderItem
             (fn [x]
               (let [{:keys [item index separators]} (j/lookup x)]
                 (reagent/as-element
                   [nbase/box {:flex-direction "row"}
                    [nbase/box
                     [ui/userpic (j/get item :avatar_file) 32]
                     [nbase/box {:flex-direction "row" :mt 2}
                      [nbase/measured-text (merge font {:fontSize 14 :height (- @h 30)}) (j/get item :user_name)]
                      [nbase/box {:h @h :style {:width 1 :backgroundColor "lightgrey"}}]
                      [nbase/measured-text (merge font {:fontSize 12 :height (- @h 30)}) (j/get item :message)]]]])))}]]]]))))

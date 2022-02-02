(ns app.ui.home.question
  (:require
   [applied-science.js-interop :as j]
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [app.handler.navigation :as navigation]
   [app.ui.components :as ui]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [cljs-bean.core :as bean]
   [steroid.rn.core :as rn]
   [steroid.rn.navigation.bottom-tabs :as bottom-tabs]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]))

(defn detail-view []
  (let [h (reagent/atom nil)
        font {:fontFamily "MongolianBaiZheng"}]
    (fn []
      (let [model @(re-frame/subscribe [:question])
            answers @(re-frame/subscribe [:answers])
            question-my @(re-frame/subscribe [:question-my])]
        [ui/safe-area-consumer
         [nbase/box {:on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                   (reset! h height))
                     :pt 2}
          [nbase/box {:height "100%" :flex-direction "row"}
           [nbase/measured-text (merge font {:fontSize 18 :height @h}) (:question_content model)]
           [nbase/flex
            [nbase/measured-text (merge font {:fontSize 12}) (str "ᠬᠠᠷᠢᠭᠤᠯᠤᠭᠰᠠᠨ " (:answer_count model))]
            [nbase/measured-text (merge font {:mt 10 :fontSize 12}) (str "ᠳᠠᠭᠠᠭᠰᠠᠨ " (:focus_count model))]]
           [nbase/flex {:mx 1}
            [nbase/link {:on-press #(if (:answer_id question-my)
                                      ; (re-frame/dispatch [:navigate-to :answer-detail])
                                      (re-frame/dispatch [:navigate-to :answer-create]))}
             [nbase/box {:py 1 :rounded "sm" :bg "primary.400"}
              [nbase/measured-text
               (merge font {:color "white" :fontSize 12})
               (if (:answer_id question-my)
                 "ᠮᠢᠨᠦ ᠬᠠᠷᠢᠭᠤᠯᠲᠠ"
                 "ᠬᠠᠷᠢᠭᠤᠯᠬᠤ")]]]
            [nbase/link {:mt 10 :on-press #(re-frame/dispatch [:question-focus (:id model)])}
             [nbase/box {:py 1 :rounded "sm" :bg "primary.400"}
              [nbase/measured-text (merge font {:color "white" :fontSize 12}) (if (:focus_id question-my) "ᠦᠯᠦ ᠳᠠᠭᠠᠬᠤ" "ᠳᠠᠭᠠᠬᠤ")]]]]
           [nbase/box {:h @h :w 0.5 :bg "success.500" :mx 1}]
           [nbase/flat-list
            {:horizontal true
             :keyExtractor    (fn [_ index] (str "answer-" index))
             :data answers
             :ItemSeparatorComponent
             (fn [] (reagent/as-element [nbase/box {:style {:width 4 :backgroundColor "lightgrey"}}]))
             :renderItem
             (fn [x]
               (let [{:keys [item index separators]} (j/lookup x)]
                 (reagent/as-element
                   [nbase/box {:flex-direction "row"}
                    [nbase/box
                     [ui/userpic (j/get item :avatar_file) 32]
                     [nbase/measured-text (merge font {:fontSize 14 :margin-top 4}) (j/get item :user_name)]]
                    [nbase/box {:h @h :style {:width 1 :backgroundColor "lightgrey"}}]
                    [nbase/measured-text (merge font {:fontSize 12 :height @h}) (j/get item :content)]])))}]]]]))))

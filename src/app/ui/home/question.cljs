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
            answers @(re-frame/subscribe [:answers])]
        [nbase/box {:safeArea true
                    :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                  (reset! h height))}
         (if @h
           [nbase/box {:flex-direction "row"}
            [nbase/measured-text (merge font {:fontSize 18 :height @h}) (:question_content model)]
            [nbase/flex
             [nbase/measured-text (merge font {:fontSize 12}) (str "answers " (:answer_count model))]
             [nbase/measured-text (merge font {:mt 10 :fontSize 12}) (str "focus " (:focus_count model))]]
            [nbase/flex
             [nbase/link {:on-press #(js/console.log "new answer")}
              [nbase/measured-text (merge font {:fontSize 12}) "new answer"]]
             [nbase/link {:mt 10 :on-press #(js/console.log "focus ")}
              [nbase/box {:px 1 :py 1 :rounded "sm" :bg "primary.400" :_text {:color "white"}}
               [nbase/measured-text (merge font {:fontSize 12}) "focus"]]]]
            [nbase/box {:h @h :w 1 :bg "success.700"}]
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
                     [nbase/measured-text (merge font {:fontSize 12 :height @h}) (j/get item :content)]])))}]])]))))

(ns app.ui.question.detail
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.editor :as editor]
    [app.ui.components :as ui]
    [app.ui.text :as text]
    [app.ui.basic.theme :as theme]
    [app.text.message :refer [labels]]
    [app.util.time :as time]
    [app.text.message :refer [labels]]
    [app.ui.question.comment :as comment]

    [steroid.rn.core :as srn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native-vector-icons/Ionicons" :default Ionicons]))

(defn detail-view []
  (let [h (reagent/atom 0)
        modal-open (reagent/atom false)
        is-open (reagent/atom false)]
    (fn []
      (let [question @(re-frame/subscribe [:question])
            answers @(re-frame/subscribe [:answers])]
        [ui/safe-area-consumer
         [nbase/box {:flex 1
                     :flex-direction "row"
                     :bg (theme/color "gray" "dark.100")
                     :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                   (reset! h height))}
          [nbase/box {:flex 1 :flex-direction "row"}
           [nbase/vstack {:m 1 :ml 2 :justifyContent "flex-start" :alignItems "flex-start"}
            [nbase/icon {:as Ionicons :name "help-circle"
                         :size "6" :color "indigo.500" :mb 6}]
            [srn/touchable-highlight {:underlayColor "#cccccc"
                                      :onPress (fn []
                                                 (re-frame/dispatch [:navigate-to :question-detail2]))}
             [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48)} (:question_content question)]]]
           (if-not (empty? (:question_detail question))
             [nbase/box {:ml 1 :mt 12}
              [nbase/box {:mt 1}
               [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48)} (:question_detail question)]]])
           [nbase/divider {:orientation "vertical" :mx 2}]
           [nbase/flat-list
            {:keyExtractor    (fn [_ index] (str "answer-list-" index))
             :data      answers
             :horizontal true
             :nestedScrollEnabled true
             :overScrollMode "never"
             :scrollToOverflowEnabled true
             :renderItem
             (fn [x]
               (let [{:keys [item index separators]} (j/lookup x)]
                 (reagent/as-element
                   [nbase/pressable {:on-press
                                     (fn [e]
                                       (re-frame/dispatch [:set-answer (bean/->clj item)])
                                       (re-frame/dispatch [:get-answer (j/get item :question_id) (j/get item :id)])
                                       (re-frame/dispatch [:answer-comments (j/get item :id)])
                                       (re-frame/dispatch [:navigate-to :question-detail2]))}
                    [nbase/flex {:m 1 :flex-direction "row" :bg (theme/color "white" "dark.100")}
                     [nbase/vstack
                      [nbase/box {:bg (theme/color "gray.300" "gray.500")
                                  :borderRadius "md"
                                  :p 4
                                  :alignSelf "center"}]
                      [nbase/box {:alignSelf "center"
                                  :justifyContent "center"
                                  :mt 4}
                       [nbase/hstack {:bg (theme/color "white" "dark.100")}
                        [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48) } (j/get item :user_name)]]]]
                     [nbase/box {:m 1 :ml 2 :mt 12
                                 :bg (theme/color "white" "dark.100")}
                      [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48)} (j/get item :content)]]
                     [nbase/vstack {:m 1 :mt 12 :ml 2
                                    :bg (theme/color "white" "dark.100")}
                      [nbase/icon {:as Ionicons :name "ios-ellipsis-vertical-sharp"
                                   :size "4" :color "indigo.500" :mb 6}]

                      [nbase/vstack {:mb 8}
                       [nbase/box {:mb 6 :alignItems "center"}
                        [nbase/icon {:as Ionicons :name "heart-outline"
                                     :size "4" :color "indigo.500"}]
                        [text/measured-text {:color "#d4d4d8"} "1024"]]
                       [nbase/box {:mb 6 :alignItems "center"}
                        [nbase/icon {:as Ionicons :name "chatbox-outline"
                                     :size "4" :color "indigo.500"}]
                        [text/measured-text {:color "#d4d4d8"} "128"]]
                       [text/measured-text {:color "#525252"} (time/month-date-from-string (j/get item :created_at))]]]]])))}]]]]))))


(defn detail-view2 []
  (let [h (reagent/atom 0)
        modal-open (reagent/atom false)
        is-open (reagent/atom false)]
    (fn []
      (let [question @(re-frame/subscribe [:question])
            answer @(re-frame/subscribe [:answer])
            comments @(re-frame/subscribe [:answer-comments])]
        [ui/safe-area-consumer
         [nbase/flex {:flex 1
                      :m 1
                      :flex-direction "row"
                      :bg "gray"
                      :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                    (reset! h height))}

          [nbase/flex {:flex-direction "row" :bg "white"}
           [nbase/vstack {:m 1 :ml 2 :justifyContent "flex-start" :alignItems "flex-start"}
            [nbase/icon {:as Ionicons :name "help-circle"
                         :size "6" :color "indigo.500" :mb 6}]
            [srn/touchable-highlight {:underlayColor "#cccccc"
                                      :onPress (fn []
                                                 (re-frame/dispatch [:navigate-to :question-detail2]))}
             [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48)} (:question_content question)]]]
           (if-not (empty? (:question_detail question))
             [nbase/box {:ml 1 :mt 12}
              [nbase/box {:mt 1}
               [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48)} (:question_detail question)]]])
           [nbase/divider {:orientation "vertical" :mx 2}]
           [nbase/vstack
            [nbase/box {:bg (theme/color "gray.300" "gray.500")
                        :borderRadius "md"
                        :p 4
                        :alignSelf "center"}]
            [nbase/box {:alignSelf "center"
                        :justifyContent "center"
                        :mt 4}
             [nbase/hstack {:bg (theme/color "white" "dark.100")}
              [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48) } (:user_name answer)]]]]
           [nbase/box {:m 1 :ml 2 :mt 12
                       :bg (theme/color "white" "dark.100")}
            [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48)} (:full-content answer)]]
           [nbase/vstack {:m 1 :mt 10 :ml 2
                          :bg (theme/color "white" "dark.100")}
            [nbase/icon-button {:justifyContent "center" :alignItems "center" :mb 6
                                :borderRadius "full"
                                :_pressed {:bg (theme/color "blue.300" "blue.500")}
                                :icon (reagent/as-element
                                        [nbase/icon {:as Ionicons :name "ios-ellipsis-vertical-sharp"
                                                     :size "4" :color "indigo.500"}])}]
            [nbase/vstack {:mb 8}
             [nbase/box {:mb 6 :alignItems "center"}
              [nbase/icon-button {:justifyContent "center" :alignItems "center"
                                  :_pressed {:bg (theme/color "blue.300" "blue.500")}
                                  :borderRadius "full"
                                  :icon (reagent/as-element
                                          [nbase/icon
                                           {:as Ionicons :size "4" :name (if (zero? (:user_thanks)) "heart-outline" "heart-sharp")
                                            :color (theme/color "blue.600" "blue.800")}])
                                  :on-press (fn [e])}]
              [text/measured-text {:color "#d4d4d8"} "1024"]]
             [nbase/box {:mb 6 :alignItems "center"}
              [nbase/icon-button {:justifyContent "center" :alignItems "center"
                                  :_pressed {:bg (theme/color "blue.300" "blue.500")}
                                  :borderRadius "full" :p 3
                                  :icon (reagent/as-element
                                          [nbase/icon {:as Ionicons :name "chatbox-outline"
                                                       :size "4" :color "indigo.500"}])}]
              [text/measured-text {:color "#d4d4d8"} "128"]]
             [nbase/box {:mb 6 :alignItems "center"}
              [text/measured-text {:color "#525252"} (time/month-date-from-string (:created_at answer))]]]]
           (if-not (zero? (count comments))
             [comment/view h (first comments)])
           (if (pos? (count comments))
             [comment/view h (second comments)])
           (if (< 2 (count comments))
             [srn/touchable-opacity {:on-press (fn [] (j/call @modal-open :open)
                                                      (reset! is-open true))
                                     :style {:justifyContent "center" :marginHorizontal 10 :paddingBottom 20}}
              [text/measured-text {:fontSize 14 :color "#60a5fa"} (get-in labels [:question :all-answer-comments])]])
           [nbase/divider {:orientation "vertical" :mx 2}]]]]))))


(def question-detail
  {:name       :question-detail
   :component  detail-view
   :options
   {:title ""
    :headerShown true
    :headerRight
    (fn [tag id classname]
      (reagent/as-element
        [nbase/icon-button {:justifyContent "center" :alignItems "center"
                            :_pressed {:bg (theme/color "blue.300" "blue.500")}
                            :borderRadius "full"
                            :icon (reagent/as-element
                                    [nbase/icon
                                     {:as Ionicons :name "duplicate-outline" :size "5"
                                      :color (theme/color "blue.600" "blue.800")}])
                            :on-press (fn [e]
                                        (re-frame/dispatch [:keyboard-editor
                                                            {:type "multi-line"
                                                             :callback-fn
                                                             (fn [x]
                                                               (let [params {:content x}
                                                                     id (:id @(re-frame/subscribe [:question]))]
                                                                 (re-frame/dispatch [:create-answer id params])
                                                                 (re-frame/dispatch [:get-answers id])))}])
                                        (re-frame/dispatch [:navigate-to :single-new]))}]))}})

(def question-detail2
  {:name       :question-detail2
   :component  detail-view2
   :options
   {:title ""
    :headerShown true}})

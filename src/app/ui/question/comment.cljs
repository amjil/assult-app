(ns app.ui.question.comment
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.text :as text]
    [app.ui.basic.theme :as theme]
    [app.util.time :as time]
    [app.text.message :refer [labels]]

    [steroid.rn.core :as rn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native" :refer [Dimensions Clipboard]]
    ["react-native-modalize" :refer [Modalize]]
    ["react-native-portalize" :refer [Portal]]
    ["react-native-vector-icons/Ionicons" :default Ionicons]))

(defn view [h item]
  [nbase/vstack {:flex 1 :ml 2 :mt 1 :mr 2}
   [nbase/box {:justifyContent "flex-start" :alignItems "flex-start"}
    [nbase/box {:bg (theme/color "gray.300" "gray.500") :borderRadius "md" :p 4}]]
   [nbase/hstack {:flex 1 :mt 2 :ml 2}
    [nbase/vstack {:mr 2}
     [nbase/box  {:mb 2 :justifyContent "center" :alignItems "center"}
      [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (item :user_name)]]
     [nbase/box  {:justifyContent "center" :alignItems "center"}
      [text/measured-text {:fontSize 10 :color "#a1a1aa"} "09:15"]]]
    [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (item :message)]]])


(defn list-view [modal is-open comments]
  (let [h (reagent/atom nil)
        menu-open (reagent/atom false)
        modal-height (str (- (.-height (.get Dimensions "window")) 150) "px")
        model (reagent/atom nil)]
    (fn []
      (let [comments @(re-frame/subscribe [:answer-comments])]
        [nbase/zstack {:flex 1
                       :bg (theme/color "white" "dark.100")}
         [nbase/modal {:isOpen @menu-open :onClose #(reset! menu-open false)}
          [nbase/box {:bg (theme/color "coolGray.50" "dark.50") :shadow 1 :rounded "lg" :maxHeight modal-height
                      :minHeight "40%" :overflow "hidden"}
           [nbase/box
            {:flex 1 :justifyContent "center" :alignItem "center" :flexDirection "row"
             :pt "2" :py 3}
            [rn/touchable-highlight {:style {:margin 8 :padding 10}
                                     :underlayColor "#cccccc"
                                     :onPress #(let [m @model]
                                                (reset! menu-open false)
                                                (.setString Clipboard (:message m)))}
             [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af")} (:copy labels)]]
            (if-not (zero? (:user_comment @model))
              [rn/touchable-highlight {:style {:margin 8 :padding 10}
                                       :underlayColor "#cccccc"
                                       :onPress #(let [m @model
                                                       answer @(re-frame/subscribe [:answer])]
                                                   (reset! menu-open false)
                                                   (re-frame/dispatch [:answer-comment-delete (:id answer) (:id m)]))}
               [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af")} (:delete labels)]])]]]

         [:> Portal
          [:> Modalize {:ref (fn [r] (reset! modal r))
                        :onLayout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                     (reset! h height))
                        :HeaderComponent
                        (reagent/as-element
                          [nbase/box {:position "absolute"
                                      :zIndex 2000
                                      :right 4 :top 4}
                           [rn/touchable-opacity
                            {:on-press
                             (fn [e]
                               (re-frame/dispatch
                                 [:keyboard-editor
                                  {:type "single-line"
                                   :callback-fn
                                   (fn [x]
                                     (let [params {:message x}
                                           id (:id @(re-frame/subscribe [:answer]))
                                           pid (:id @(re-frame/subscribe [:question]))]
                                       (re-frame/dispatch [:answer-comment-create id params])
                                       (re-frame/dispatch [:get-answers pid])))}])
                               (re-frame/dispatch [:navigate-to :single-new])
                               (j/call @modal :close))}
                            [nbase/icon {:as Ionicons :name "ios-add-circle"
                                         :size "6" :color "indigo.500"}]]])
                        :flatListProps
                        {:data comments
                         :showsHorizontalScrollIndicator false
                         :horizontal true
                         :contentContainerStyle {:height "100%" :backgroundColor (theme/color "white" "#27272a")}
                         :renderItem (fn [x]
                                       (let [{:keys [item index separators]} (j/lookup x)]
                                         (reagent/as-element
                                           [rn/touchable-highlight {:style {}
                                                                    :underlayColor "#cccccc"
                                                                    :on-press #(js/console.log "touchable pressed .... ")
                                                                    :on-long-press (fn [e]
                                                                                     (reset! menu-open true)
                                                                                     (reset! model (bean/->clj item)))}
                                            [nbase/vstack {:flex 1 :m 2}
                                             [nbase/box {:justifyContent "flex-start" :alignItems "flex-start"}
                                              [nbase/box {:bg (theme/color "gray.300" "gray.500") :borderRadius "md" :p 6}]]
                                             [nbase/hstack {:flex 1 :mt 2}
                                              [nbase/vstack {:mr 2}
                                               [nbase/box  {:mb 2 :justifyContent "center" :alignItems "center"}
                                                [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (j/get item :user_name)]]
                                               [nbase/box  {:justifyContent "center" :alignItems "center"}
                                                [text/measured-text {:fontSize 10 :color (theme/color "#a1a1aa" "#e4e4e7")} (time/month-date-from-string (j/get item :created_at))]]]
                                              [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (j/get item :message)]]]])))}}]]]))))

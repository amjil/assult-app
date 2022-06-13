(ns app.ui.question.comment
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.editor :as editor]
    [app.ui.components :as ui]
    [app.ui.text :as text]
    [app.ui.basic.theme :as theme]
    [app.ui.keyboard.index :as keyboard]
    [app.ui.keyboard.candidates :as candidates]
    [app.ui.keyboard.bridge :as bridge]
    [app.handler.gesture :as gesture]
    [app.handler.animated :as animated]
    [app.handler.animation :as animation]
    [app.handler.animatable :as animatable]
    [app.text.message :refer [labels]]

    [steroid.rn.core :as rn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native-modalize" :refer [Modalize]]
    ["react-native-vector-icons/Ionicons" :default Ionicons]
    ["react-native-portalize" :refer [Portal]]))

(def comments [{:user_name "john" :content "hello"}
               {:user_name "sara" :content "hello, John"}
               {:user_name "peter" :content "what a nice day"}
               {:user_name "Lily" :content "so, what is the best there?"}
               {:user_name "Lampard" :content "Im The Franke Lampard"}
               {:user_name "Backham" :content "Im The David"}
               {:user_name "Rice" :content "Declain Rice"}])

(defn list-view [modal is-open]
  (let [h (reagent/atom nil)]
    (fn []
      [:> Portal
       [:> Modalize {:ref (fn [r] (reset! modal r))
                      :onLayout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                   (reset! h height))
                     :flatListProps
                     {:data comments
                      :showsHorizontalScrollIndicator false
                      :horizontal true
                      :renderItem (fn [x]
                                    (let [{:keys [item index separators]} (j/lookup x)]
                                      (reagent/as-element
                                        [nbase/vstack {:flex 1 :ml 2 :mt 2}
                                         [nbase/box {:justifyContent "flex-start" :alignItems "flex-start"}
                                          [nbase/box {:bg (theme/color "gray.300" "gray.500") :borderRadius "md" :p 6}]]
                                         [nbase/hstack {:flex 1 :mt 2}
                                          [nbase/vstack {:mr 2}
                                           [nbase/box  {:mb 2 :justifyContent "center" :alignItems "center"}
                                            [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (j/get item :user_name)]]
                                           [nbase/box  {:justifyContent "center" :alignItems "center"}
                                            [text/measured-text {:fontSize 10 :color (theme/color "#a1a1aa" "#e4e4e7")} "09:15"]]]
                                          [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (j/get item :content)]]])))
                      :contentContainerStyle {:height "100%" :backgroundColor (theme/color "white" "#27272a")}}}]])))


; (defn list-view [is-open]
;   (let [h (reagent/atom nil)]
;     (fn []
;       [:> rnmodal { :isVisible  @is-open
;                     :scrollHorizontal true
;                     :propagateSwipe true
;                     :panResponderThreshold 1
;                     :backdropColor "lightGray"
;                     :transparent false
;                     :swipeDirection ["down"]
;                     :onSwipeComplete (fn [e] (reset! is-open false)
;                                        (js/console.log ">>>> oen swipe .... "))
;                     :style {:margin 0 :alignItems nil :justifyContent nil}}
;        [ui/safe-area-consumer
;         [nbase/zstack {:flex 1 :width "100%" :borderTopRadius "md"
;                        :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
;                                      (reset! h height))}
;          [nbase/flat-list
;           {:keyExtractor    (fn [_ index] (str "comment-view-" index))
;            :data      comments
;            :showsHorizontalScrollIndicator false
;            :horizontal true
;            :bounces true
;            :style {:flex 1 :height @h :margin-top 40}
;            :renderItem (fn [x]
;                          (let [{:keys [item index separators]} (j/lookup x)]
;                            (reagent/as-element
;                              [nbase/vstack {:flex 1 :ml 2 :mt 1}
;                               [nbase/box {:justifyContent "flex-start" :alignItems "flex-start"}
;                                [nbase/box {:bg "gray.300" :borderRadius "md" :p 6}]]
;                               [nbase/hstack {:flex 1 :mt 2}
;                                [nbase/vstack {:mr 2}
;                                 [nbase/box  {:mb 2 :justifyContent "center" :alignItems "center"}
;                                  [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 60)} (j/get item :user_name)]]
;                                 [nbase/box  {:justifyContent "center" :alignItems "center"}
;                                  [text/measured-text {:fontSize 10 :color "#a1a1aa"} "09:15"]]]
;                                [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 60)} (j/get item :content)]]])))}]
;
;          [nbase/box {:top 2
;                      :right 2}
;           [nbase/icon-button {;:w 4 :h 4
;                               :borderRadius "full" ;:bg "blue.200"
;                               :justifyContent "center" :alignSelf "center" :alignItems "center"
;                               :icon (reagent/as-element [nbase/icon {:as Ionicons :name "close-circle-outline"}])
;                               :onPress (fn [e] (reset! is-open false))}]]]]])))

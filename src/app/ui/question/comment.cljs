(ns app.ui.question.comment
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.components :as ui]
    [app.ui.text :as text]
    [app.ui.basic.theme :as theme]
    [app.text.message :refer [labels]]

    [steroid.rn.core :as rn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native-modalize" :refer [Modalize]]
    ["react-native-vector-icons/Ionicons" :default Ionicons]
    ["react-native-portalize" :refer [Portal]]))

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
  (let [h (reagent/atom nil)]
    (fn []
      (let [comments @(re-frame/subscribe [:answer-comments])]
        (js/console.log "comments list count = " (count comments))
        [:> Portal
         [:> Modalize {:ref (fn [r] (reset! modal r))
                        :onLayout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                     (reset! h height))
                       :flatListProps
                       {:data comments
                        :showsHorizontalScrollIndicator false
                        :horizontal true
                        :contentContainerStyle {:height "100%" :backgroundColor (theme/color "white" "#27272a")}
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
                                            [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (j/get item :message)]]])))}}]]))))

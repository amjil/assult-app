(ns app.ui.article.new
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.editor :as editor]
    [app.ui.components :as ui]
    [app.ui.text :as text]
    [app.ui.editor :refer [weblen]]
    [app.ui.keyboard.index :as keyboard]
    [app.ui.keyboard.candidates :as candidates]
    [app.ui.keyboard.bridge :as bridge]
    [app.ui.basic.theme :as theme]
    [app.text.message :refer [labels]]

    [steroid.rn.core :as rn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native-modal" :default rnmodal]
    ["react-native-vector-icons/Ionicons" :default Ionicons]))

(def model (reagent/atom {:title "" :content ""}))

(def recommandations [{:title "hello world" :focus_count "100" :answer_count "991"}
                      {:title "The big big world" :focus_count "10" :answer_count "89"}
                      {:title "The Center of Asia" :focus_count "1" :answer_count "9"}])

(defn recomm-view [h flag]
  [nbase/box {:height (str (- @h 220) "px") :borderRightRadius "10"
              :maxWidth 40 :flexDirection "row"}
   [nbase/box {:p 0.5 :flex 0.2 :bg (theme/color "white" "dark.100")}]
   [nbase/box {:flex 1 :bg (theme/color "gray.100" "dark.300")}
    [nbase/flat-list
     {:keyExtractor    (fn [_ index] (str "question-view-" index))
      :data      recommandations

      :showsHorizontalScrollIndicator false
      :horizontal true
      :style {:flex 1}

      :renderItem (fn [x]
                    (let [{:keys [item index separators]} (j/lookup x)]
                      (reagent/as-element
                        [nbase/box {:flex 1 :flexDirection "row"}
                         [rn/touchable-opacity {:on-press #(js/console.log "recommandations on press >>>")}
                          [nbase/box {:m 5 :flex 1 :flexDirection "row"}
                           [text/measured-text {:fontSize 18 :width (- @h 220 20) :color (theme/color "black" "#a1a1aa")} (j/get item :title)]
                           [nbase/box
                             [text/measured-text {:fontSize 14 :color (theme/color "gray" "#a1a1aa")} (j/get item :focus_count)]
                             [text/measured-text {:fontSize 14 :color (theme/color "gray" "#a1a1aa")} "-"]
                             [text/measured-text {:fontSize 14 :color (theme/color "gray" "#a1a1aa")} (j/get item :answer_count)]]]]
                         [nbase/divider {:orientation "vertical" :bg (theme/color "gray" "dark.500")}]])))}]]
   [rn/touchable-opacity {:on-press #(reset! flag 2)
                          :style {:flex 0.2}}
    [nbase/box {:flex 1 :bg (theme/color "gray.50" "dark.300") :alignItems "center" :justifyContent "center" :borderRightRadius "10"}
     [text/measured-text {:fontSize 14 :color (theme/color "gray" "#a1a1aa") :width (-@h 220 20)} (get-in labels [:question :close-similar-titles])]]]])


(defn view []
  (let [on-edit (reagent/atom false)
        h (reagent/atom nil)
        focus-elem (reagent/atom 0)

        recomm-flag (reagent/atom 0)]
    (fn []
      (if (and (= @recomm-flag 0) (not= 0 @weblen))
        (do (js/console.log "weblen ....")
          (reset! recomm-flag 1)))

      (if (and (= @recomm-flag 1) (= 0 @weblen))
        (do (js/console.log "weblen is zero recomm flag must zero")
          (reset! recomm-flag 0)))
      [nbase/box
       {:on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                      (reset! h height))
        :flex 1}
       [nbase/zstack {:flex 1}
        (if-not (nil? @h)
          [nbase/flex {:flex 1 :flex-direction "row" :bg (theme/color "white" "dark.100")}
           (if (= 1 @focus-elem)
             [nbase/zstack {:style {:height (- @h 220)}
                            :minWidth 10
                            :bg (theme/color "white" "dark.100")}
              [editor/editor-view
               ; opts
               {;:placeholder (get-in labels [:question :title-placeholder])
                :type :text}
               ;content-fn
               (fn []
                 ; (js/console.log "content -fn >......."))
                 (:title @model))
                 ; (get @model @active-key))
               ;update-fn
               (fn [x] (js/console.log "1112")
                 (swap! model assoc :title (:text x)))]
              (if (= 0 @weblen)
                [nbase/box {:flex 1 :m 5}
                 [text/multi-line-text {:fontSize 22 :color (theme/color "#71717a" "#a1a1aa") :fontFamily "MongolianBaiZheng" :width (- @h 220)}
                  (get-in labels [:question :title-placeholder])]])]
             [rn/touchable-opacity {:style {:paddingVertical 20
                                            :paddingLeft 20
                                            :height (- @h 220)}
                                    :onPress (fn [] (js/console.log "touchable without feedback .....")
                                               (bridge/editor-content)
                                               (reset! focus-elem 1)
                                               (reset! weblen (count (:title @model)))
                                               (candidates/candidates-clear))}
              [text/measured-text {:fontSize 22 :color (theme/color "#71717a" "#a1a1aa") :fontFamily "MongolianBaiZheng" :width (- @h 220)}
               (if (empty? (:title @model))
                 (get-in labels [:question :title-placeholder])
                 (:title @model))]])
           [nbase/box {:my 5}
            [nbase/divider (merge {:orientation "vertical" :mr "3" :ml "1"}
                             (if (= 1 @focus-elem)
                               {:thickness "2" :bg (theme/color "blue.700" "blue.300")}))]]
           [nbase/box {:flex 1 :flexDirection "row" :ml 0}
            [:> rnmodal { :isVisible (= @recomm-flag 1)
                          :coverScreen false
                          :backdropColor (theme/color "lightGray" "#27272a")
                          :scrollHorizontal true
                          :style {:margin-left -10}}
              [recomm-view h recomm-flag]]
            (if (= 2 @focus-elem)
               [nbase/zstack {:style {:height (- @h 220)}
                              :bg (theme/color "white" "dark.100")
                              :minWidth 20}
                [editor/editor-view
                 ; opts
                 {:quill {:placeholder (get-in labels [:question :content-placeholder])}
                  :type :text}
                 ;content-fn
                 (fn []
                   ; (js/console.log "content -fn >......."))
                   (:content @model))
                   ; (get @model @active-key))
                 ;update-fn
                 (fn [x] (js/console.log "1112")
                   (swap! model assoc :content (:text x)))]
                (if (= 0 @weblen)
                  [nbase/box {:flex 1 :m 5}
                   [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#a1a1aa") :fontFamily "MongolianBaiZheng" :width (- @h 220)}
                    (get-in labels [:question :content-placeholder])]])]
               [rn/touchable-opacity {:style {:paddingVertical 20
                                              :height (- @h 220)}
                                      :onPress (fn [] (js/console.log "touchable without feedback .....2")
                                                 (bridge/editor-content)
                                                 (reset! focus-elem 2)
                                                 (reset! weblen (count (:content @model)))
                                                 (candidates/candidates-clear))}
                [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#a1a1aa") :fontFamily "MongolianBaiZheng" :width (- @h 220)}
                 (if (empty? (:content @model))
                   (get-in labels [:question :content-placeholder])
                   (:content @model))]])]])
        [candidates/views {:bottom 20}]]
       [nbase/box {:height 220 :mt 1}
        [keyboard/keyboard {:type "single-line"
                            :on-press (fn []
                                        (bridge/editor-content)
                                        (candidates/candidates-clear))}]]])))

(def model-new
  {:name       :model-new
   :component  view

   :options
   {:title ""
    :headerShown true
    :headerRight
    (fn [tag id classname]
      (reagent/as-element
        [nbase/icon-button {:variant "ghost" :colorScheme "indigo"
                            :icon (reagent/as-element [nbase/icon {:as Ionicons :name "ios-checkmark"}])
                            :on-press (fn [e] (js/console.log "on press icon button"))}]))}})
                                        ; (bridge/editor-content)
                                        ; (re-frame/dispatch [:navigate-back]))}]))}})

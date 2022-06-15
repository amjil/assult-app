(ns app.ui.question.index
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.editor :as editor]
    [app.ui.components :as ui]
    [app.ui.text :as text]
    [app.ui.keyboard.index :as keyboard]
    [app.ui.keyboard.candidates :as candidates]
    [app.ui.keyboard.bridge :as bridge]
    [app.ui.basic.theme :as theme]
    [app.handler.gesture :as gesture]
    [app.handler.animated :as animated]
    [app.handler.animation :as animation]
    [app.handler.animatable :as animatable]
    [app.text.message :refer [labels]]
    [app.ui.question.comment :as comment]
    ["react-native-gesture-handler" :as ges]
    ["react-native-reanimated" :as re-animated]
    ["lottie-react-native" :as lottie]

    [steroid.rn.core :as srn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native" :as rn :refer [Dimensions]]
    ["react-native-vector-icons/Ionicons" :default Ionicons]))

(def questions-atom (reagent/atom [{:question_content "ᠲᠡᠷᠭᠡᠯ ᠰᠠᠷᠠ ᠭᠡᠵᠦ ᠶᠠᠮᠠᠷ ᠰᠠᠷᠠ ᠪᠤᠢ?"
                                    :question_detail "ᠲᠡᠷᠭᠡᠯ ᠰᠠᠷᠠ᠂ ᠬᠠᠪᠢᠷᠭᠠᠨ ᠰᠠᠷᠠ᠂ ᠲᠠᠯ᠎ᠠ ᠰᠠᠷᠠ\nabc\naaa\n111\n2222\n2222"
                                    ; :question_detail "ᠲᠡᠷᠭᠡᠯ ᠰᠠᠷᠠ᠂ ᠬᠠᠪᠢᠷᠭᠠᠨ ᠰᠠᠷᠠ᠂ ᠲᠠᠯ᠎ᠠ ᠰᠠᠷᠠ"
                                    :user_name "ᠪᠠᠲᠦ"
                                    :agree_count 0
                                    :comment_count 0}
                                   {:question_content "ᠬᠦᠬᠡ ᠲᠣᠯᠪᠤ ᠭᠡᠰᠡᠨ ᠨᠢ ᠶᠠᠭᠤ ᠪᠤᠢ?"
                                    :question_detail "ᠬᠦᠬᠡ ᠲᠤᠯᠪᠤᠲᠠᠨ ᠃"
                                    :user_name "ᠰᠣᠳᠤ"
                                    :agree_count 0
                                    :comment_count 0}
                                   {:question_content "ᠪᠢᠳᠡ ᠬᠡᠨ ᠪᠤᠢ"
                                    :question_detail ""
                                    :user_name "ᠪᠠᠭᠠᠲᠤᠷ"
                                    :agree_count 0
                                    :comment_count 0}
                                   {:question_content "ᠶᠠᠭᠠᠬᠢᠪᠠᠯ ᠵᠣᠬᠢᠬᠤ ᠪᠤᠢ"
                                    :question_detail "ᠶᠠᠭᠠᠬᠢᠵᠤ ᠠᠵᠢᠯᠯᠠᠪᠠᠯ ᠰᠠᠶᠢ ᠦᠷᠢ ᠪᠦᠲᠦᠮᠵᠢ ᠪᠡᠷ ᠰᠠᠢᠨ ᠪᠤᠢ"
                                    :user_name "ᠰᠦᠬᠡ"
                                    :agree_count 0
                                    :comment_count 0}
                                   {:question_content "ᠪᠢ ᠬᠠᠮᠢᠭ᠎ᠠ ᠲᠦᠷᠦᠪᠡ?"
                                    :question_detail "ᠪᠢᠳᠡᠨ "
                                    :user_name "ᠰᠠᠷᠠ"
                                    :agree_count 0
                                    :comment_count 0}
                                   {:question_content "ᠮᠠᠰᠢᠨ ᠭᠡᠳᠡᠭ ᠭᠠᠳᠠᠭᠠᠳᠤ ᠦᠭᠡ ?"
                                    :question_detail "ᠮᠠᠰᠢᠨ ᠭᠡᠵᠦ "
                                    :user_name "ᠰᠠᠷᠠ"
                                    :agree_count 0
                                    :comment_count 0}]))

(def comments [{:user_name "john" :content "hello"}
               {:user_name "sara" :content "hello, John"}
               {:user_name "peter" :content "what a nice day"}
               {:user_name "Lily" :content "so, what is the best there?"}])

(def cursor (reagent/atom 0))

(def model (reagent/atom {}))
(def active-key (reagent/atom nil))

(defn list-view []
  (let [h (reagent/atom nil)
        scroll-ref (atom nil)
        is-loading (atom false)
        container-offset (new rn/Animated.ValueXY #js {:x 0 :y 0})
        transform-offset (new rn/Animated.ValueXY (bean/->js {:x (- 80) :y 0}))
        scroll-enabled (atom false)
        inner-scroll-offset (atom 0)

        ;
        left-pull-threshold 2
        threshold 80
        reset-container-position (fn []
                                   (let [timing (animation/animated-timing transform-offset #js{:toValue -80
                                                                                                :duration 250
                                                                                                :useNativeDriver true})]
                                     (j/call timing :start))
                                   (let [timing (animation/animated-timing container-offset #js{:toValue 0
                                                                                                :duration 250
                                                                                                :useNativeDriver true})]
                                     (j/call timing :start)))
        on-refresh (fn []
                     (js/console.log "on-refresh .... ")
                     (js/setTimeout
                       reset-container-position
                       2000))
        is-innerscroll-left (<= @inner-scroll-offset left-pull-threshold)
        check-scroll (fn []
                       (if (is-innerscroll-left)
                         (if (true? @scroll-enabled)
                           (reset! scroll-enabled false))
                         (if (false? @scroll-enabled)
                           (reset! scroll-enabled true))))

        on-scroll (fn [e]
                    (let [x (j/get-in e [:nativeEvent :contentOffset :x])]
                      (reset! inner-scroll-offset x)))
                      ; (check-scroll)))
        pan-responder
        (rn/PanResponder.create
          #js { ;:onStartShouldSetPanResponder (fn [arg] true)
                ; :onStartShouldSetPanResponder (fn [e state] true)
                ; :onStartShouldSetPanResponderCapture (fn [e state] true)
                ; :onMoveShouldSetPanResponder (fn [e state] true)
                ; :onPanResponderReject (fn [e state] (js/console.log "onPanResponderReject ... "))
                :onMoveShouldSetPanResponderCapture (fn [e state]
                                                      ; (js/console.log "onMoveShouldSetPanResponderCapture start ...")
                                                      ; true)
                                                      (let [result
                                                             (cond
                                                               (true? @is-loading) false

                                                               ; (false? @scroll-enabled) false

                                                               (>= @inner-scroll-offset left-pull-threshold)
                                                               false

                                                               (< 0 (j/get state :dx))
                                                               true

                                                               :else
                                                               false)]
                                                        ; (js/console.log "onMoveShouldSetPanResponderCapture end ..." result)
                                                        result))
                :onPanResponderMove (fn [e state]
                                      (let [x (j/get state :dx)]
                                        ; (js/console.log "onPanResponderMove start ..." state " container-offset = " container-offset " x = " x)
                                        ;
                                        (if (> x 0)
                                          (when (<= x 80)
                                            (.setValue ^js container-offset #js {:x (j/get state :dx)
                                                                                 :y (j/get state :dy)})
                                            (.setValue ^js transform-offset (bean/->js {:x (- x 80) :y 0})))
                                          (do
                                            (.setValue ^js container-offset #js {:x 0 :y 0})
                                            (.setValue ^js transform-offset (bean/->js {:x -80 :y 0}))))))
                :onPanResponderRelease (fn [e state]
                                         (js/console.log "onPanResponderRelease ..." state (j/get-in container-offset [:x :_value]))
                                         (if (<= threshold (j/get state :dx))
                                           (on-refresh)
                                           (reset-container-position)))
                                         ; (check-scroll))
                :onPanResponderTerminationRequest (fn [e state] false)
                :onPanResponderTerminate (fn [e state]
                                           (js/console.log "onPanResponderTerminate ..." state)
                                           (reset-container-position))
                                         ; (check-scroll))
                :onShouldBlockNativeResponder (fn [e] true)})
        ;
        is-open (reagent/atom false)
        modal-height (str (- (.-height (.get Dimensions "window")) 150) "px")]

    (fn []
      [nbase/zstack {:flex 1
                    ; :bg "gray.100"
                     :bg (theme/color "white" "dark.100")
                     :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                   (reset! h height))}
       [nbase/modal {:isOpen @is-open :onClose #(reset! is-open false)}
        [nbase/box {:bg (theme/color "coolGray.50" "dark.50") :shadow 1 :rounded "lg" :maxHeight modal-height
                    :minHeight "40%" :overflow "hidden"}
         [nbase/box
          {:flex 1 :justifyContent "center" :alignItem "center" :flexDirection "row"
           :pt "2" :py "3"}
          [srn/touchable-highlight {:style {:padding 10}
                                    :underlayColor "#cccccc"
                                    :onPress #(js/console.log "touchable 1 >>> ")}
           [text/measured-text {:color "#9ca3af"} "Arial"]]
          [nbase/divider {:orientation "vertical" :bg "coolGray.400"}]
          [srn/touchable-highlight {:style {:padding 10}
                                    :underlayColor "#cccccc"
                                    :onPress #(js/console.log "touchable 2 >>> ")}
           [text/measured-text {:color "#9ca3af"} "Nunito Sans"]]
          [nbase/divider {:orientation "vertical" :bg "coolGray.400"}]
          [srn/touchable-highlight {:style {:padding 10}
                                    :underlayColor "#cccccc"
                                    :onPress #(js/console.log "touchable 3 >>> ")}
           [text/measured-text {:color "#9ca3af"} "Roboto"]]]]]
       (if (nil? @h)
         [nbase/box {:style {:height "100%"}}
          [nbase/box {:m 1 :p 4
                      :borderRightWidth "0.5"
                      :borderColor (theme/color "gray.300" "gray.500")
                      :bg (theme/color "white" "black")}
           [nbase/skeleton {:h "100%" :w 24}]]]
         [animation/animated-view
          (merge (bean/->clj (j/get pan-responder :panHandlers))
                 {:style {:flex 1
                          :flexDirection "row"}})
          [animation/animated-view
           {:style {:flex 1 :transform [{:translateX (j/get container-offset :x)}]
                    :height @h}}
           [nbase/flat-list
            {:keyExtractor    (fn [_ index] (str "question-view-" index))
             :data      @questions-atom
             :overScrollMode "never"
             :scrollToOverflowEnabled true
             :onScroll (fn [e]
                         (reset! inner-scroll-offset (j/get-in e [:nativeEvent :contentOffset :x])))
             :ref (fn [r]
                    (reset! scroll-ref r))
             ; :onRefresh (fn [e] (js/console.log "on-refreshing ... "))
             ; :refreshing false
             :renderItem (fn [x]
                           (let [{:keys [item index separators]} (j/lookup x)]
                             (reagent/as-element
                              [nbase/pressable {:m 1
                                                :borderRightWidth "0.5"
                                                :borderColor (theme/color "gray.300" "gray.500")
                                                :bg (theme/color "white" "dark.100")
                                                :on-press (fn [e] (re-frame/dispatch [:navigate-to :question-detail])
                                                            (reset! cursor index)
                                                            (reset! model (bean/->clj item)))}
                               [nbase/hstack
                                [nbase/vstack
                                 [nbase/box {:bg (theme/color "gray.300" "gray.500")
                                             :borderRadius "md"
                                             :p 6
                                             :alignSelf "center"}]
                                 [nbase/box {:alignSelf "center"
                                             :justifyContent "center"
                                             :mt 4}
                                  [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 68)}
                                   (j/get item :user_name)]]]
                                [nbase/box {:mt 16
                                            :ml 2}
                                 [text/measured-text {:fontSize 22 :color (theme/color "#002851" "#9ca3af") :width (- @h 68)}
                                  (j/get item :question_content)]]
                                [nbase/box {:mt 16
                                            :ml 1
                                            ; :w 100
                                            :flex 1}
                                 [text/simple-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 68)}
                                  (j/get item :question_detail)]]
                                [nbase/box {:ml 2
                                            :mt 16
                                            :style {:height (- @h 120)}
                                            :justifyContent "space-between"}
                                 [srn/touchable-highlight {:underlayColor "#cccccc" :onPress #(reset! is-open true)
                                                           :style {:height 24}}
                                  [nbase/icon {:as Ionicons :name "ios-ellipsis-vertical-sharp"
                                               :size "6" :color "indigo.500"
                                               :mb 40}]]
                                 [nbase/box {:justifyContent "flex-start"}
                                  [nbase/box {:mb 6 :alignItems "center"}
                                   [nbase/icon {:as Ionicons :name "ios-heart-sharp"
                                                :size "6" :color "indigo.500"}]
                                   [text/measured-text {:color "#d4d4d8"} "1024"]]
                                  [nbase/box {:mb 6 :alignItems "center"}
                                   [nbase/icon {:as Ionicons :name "ios-chatbubble-ellipses-outline"
                                                :size "6" :color "indigo.500"}]
                                   [text/measured-text {:color "#d4d4d8"} "128"]]
                                  [nbase/icon {:as Ionicons :name "time-outline"
                                               :size "6" :color "gray.300"
                                               :mb 1}]
                                  [nbase/box {:alignSelf "center"}
                                   [text/measured-text {:color "#d4d4d8"} "2022-04-10 13:52:54"]]]]]])))
             :showsHorizontalScrollIndicator false
             :horizontal true
             :bounces true
             :style {:flex 1 :height @h
                     :width (.-width (.get Dimensions "window"))}}]]])
       ; pull to refresh component
       [animation/animated-view
        {:style {:width 80
                 :height "100%"
                 :transform [{:translateX (j/get transform-offset :x)}]}}
        ; [nbase/box {:flex 1 :bg "primary.100"}]
        [:> lottie
           {:source (js/require "../src/json/104547-loading-25.json")
            :autoPlay true}]]])))
       ; [nbase/box {:right 4
       ;             :bottom 2}
       ;            ; :position "absolute"}
       ;  [nbase/icon-button {:w 16 :h 16 :borderRadius "full" :variant "solid" :colorScheme "indigo"
       ;                      :justifyContent "center" :alignSelf "center" :alignItems "center"
       ;                      :icon (reagent/as-element [nbase/icon {:as Ionicons :name "ios-add"}])
       ;                      :onPress (fn [e]
       ;                                 (js/console.log "icon-button on press")
       ;                                 (re-frame/dispatch [:navigate-to :question-detail]))}]]])))

(defn comment-view [h item]
  [nbase/vstack {:flex 1 :ml 2 :mt 1}
   [nbase/box {:justifyContent "flex-start" :alignItems "flex-start"}
    [nbase/box {:bg (theme/color "gray.300" "gray.500") :borderRadius "md" :p 6}]]
   [nbase/hstack {:flex 1 :mt 2}
    [nbase/vstack {:mr 2}
     [nbase/box  {:mb 2 :justifyContent "center" :alignItems "center"}
      [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (item :user_name)]]
     [nbase/box  {:justifyContent "center" :alignItems "center"}
      [text/measured-text {:fontSize 10 :color "#a1a1aa"} "09:15"]]]
    [text/measured-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (item :content)]]])

(defn answer-buttons [h model]
  [srn/view {:style {:height @h :margin-top 5 :margin-horizontal 20}}
   ; [srn/view {:style {:margin-bottom 20 :borderRadius 10 :backgroundColor "#eff6ff" :justifyContent "center" :alignItems "center"}}]
   [nbase/vstack {:mb 4 :borderRadius "full" :bg (theme/color "blue.50" "dark.300") :justifyContent "center" :alignItems "center"}
    [nbase/icon-button {:justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "caret-up-outline" :size "5" :color (theme/color "blue.600" "blue.800")}])}]
    [text/measured-text {:fontSize 12 :color (theme/color "#2563eb" "#1e40af")} (str (get-in labels [:question :vote]) "  " (:agree_count @model))]
    [nbase/icon-button {:justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "caret-down-outline" :size "5" :color (theme/color "blue.600" "blue.800")}])}]]
   [nbase/vstack {:mt 2 :borderRadius "full" :bg (theme/color "blue.50" "dark.300") :justifyContent "center" :alignItems "center"}
   ; [srn/view {:style {:margin-top 10 :borderRadius 10 :backgroundColor "#eff6ff" :justifyContent "center" :alignItems "center"}}
    [nbase/icon-button {:mb 4 :justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "heart-outline"  :size "5" :color (theme/color "blue.600" "blue.800")}])}]
    [nbase/icon-button {:mb 4 :justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "star-outline" :size "5" :color (theme/color "blue.600" "blue.800")}])}]
    [nbase/icon-button {:justifyContent "center" :alignItems "center"
                        :icon (reagent/as-element [nbase/icon {:as Ionicons :name "chatbubble-outline" :size "5" :color (theme/color "blue.600" "blue.800")}])}]]])

(defn detail-view []
  (let [h (reagent/atom 0)
        modal-open (reagent/atom false)
        is-open (reagent/atom false)]
    (fn []
      [ui/safe-area-consumer
       [nbase/box {:flex 1
                   :flex-direction "row"
                   :bg (theme/color "gray" "dark.100")
                   :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                 (reset! h height))}
        ; [nbase/box {:flex 1 :flex-direction "row" :style {:width "100%" :height @h}}]
        [nbase/scroll-view {:flex 1 :_contentContainerStyle {:flexGrow 1}
                            :horizontal true
                            :nestedScrollEnabled false
                            :scrollEventThrottle 16}
         [nbase/vstack {:m 1 :ml 2 :justifyContent "flex-start" :alignItems "flex-start"}
          [nbase/icon {:as Ionicons :name "help-circle"
                       :size "6" :color "indigo.500" :mb 6}]
          [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48)} (:question_content @model)]]
         [nbase/divider {:orientation "vertical" :mx 2}]
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
             [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 48) } (:user_name @model)]
             [text/measured-text {:fontSize 10 :color "#a1a1aa"} "09:15"]]]]
          ; [nbase/box {:m 1 :ml 2 :mt 12
          ;             :bg (theme/color "white" "dark.100")}
          ;  ;; width 4 + 4 + 4   ()  *  4  = 48
          ;  [text/multi-line-text {:fontSize 18 :color (theme/color "#71717a" "#9ca3af") :width (- @h 48)} (:question_detail @model)]]
          [nbase/box {:m 1 :ml 2 :mt 8
                      :bg (theme/color "white" "dark.100")}
           ;; width 4 + 4 + 4   ()  *  4  = 48
           [editor/simple-view
            ;opts
            {:type :text}
            ; (:content @model)
            (fn [] (:question_detail @model))
              ; (str "<h2>abc</h2><p></p>" (:question_detail @model)))
            ; "<p>abc</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>def</p><p>xxxx</p>"
            ;tap-fn
            (fn [] (js/console.log "text on tap >>> question-detail"))]]]
         [answer-buttons h model]

         [comment-view h (first comments)]
         [comment-view h (second comments)]

         ; [srn/touchable-opacity {:on-press (fn [] (reset! modal-open true))}]
         [srn/touchable-opacity {:on-press (fn [] (j/call @modal-open :open)
                                                  (reset! is-open true))
                                 :style {:justifyContent "center" :marginHorizontal 10 :paddingBottom 20}}
          [text/measured-text {:fontSize 14 :color "#60a5fa"} (get-in labels [:question :all-answer-comments])]]
         [nbase/divider {:orientation "vertical" :mx 2}]]
        ;; in zstack flow next answer button
        [comment/list-view modal-open is-open]
        [nbase/box {:right 4
                    :bottom 2
                    :position "absolute"}
         [nbase/icon-button {:w 10 :h 10 :borderRadius "full" :variant "outline" :colorScheme "coolGray"
                             :justifyContent "center" :alignSelf "center" :alignItems "center"
                             :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward-outline"}])
                             :onPress (fn [e]
                                        (js/console.log "icon-button on press"))}]]]])))

(defn detail-view2 []
  (let [h (reagent/atom 0)]
    (fn []
      [ui/safe-area-consumer
       [nbase/flex {:flex 1
                    :m 1
                    :flex-direction "row"
                    :bg "gray"
                    :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                  (reset! h height))}

        [nbase/flex {:flex-direction "row" :bg "white"}
         [nbase/vstack
          [nbase/box {:bg "gray.300"
                      :borderRadius "md"
                      :p 6
                      :alignSelf "center"}]
          [nbase/box {:alignSelf "center"
                      :justifyContent "center"
                      :mt 4}
           [nbase/hstack
            [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 68)} (:user_name @model)]
            [text/measured-text {:fontSize 10 :color "#a1a1aa"} "09:15"]]]]
         [nbase/hstack {:mt 16 :ml 1}
          [text/measured-text {:fontSize 22 :color "#002851" :width (- @h 68)} (@model :question_content)]
          [nbase/box {:ml 1}
           [text/measured-text {:fontSize 18 :color "#71717a" :width (- @h 68)} (@model :question_detail)]]]]
        [nbase/divider {:orientation "vertical" :mx 2}]
        [nbase/flex {:flex-direction "row" :bg "white" :ml 2}
         [nbase/box {:p 5 :bg "indigo.300"}]]]])))


(defn edit-view []
  [ui/safe-area-consumer
   [nbase/flex {:flex 1
                :justifyContent "space-between"}
    [editor/editor-view
      {:type :text}
      ;content-fn
      (fn []
        ; (js/console.log "content -fn >......." (:content @model))
        (get @model @active-key))
      ;update-fn
      (fn [x]
        (swap! model assoc @active-key (get x :text))
        (swap! questions-atom assoc-in [@cursor @active-key] (get x :text)))]
    [candidates/views {}]
    [nbase/box {:height 220}
     [keyboard/keyboard {}]]]])

(def question-detail
  {:name       :question-detail
   :component  detail-view
   :options
   {:title ""
    :headerShown true}})

(def question-edit
  {:name       :question-edit
   :component  edit-view
   :options
   {:title ""
    :headerShown true
    :headerRight
    (fn [tag id classname]
      (reagent/as-element
        [nbase/icon-button {:variant "ghost" :colorScheme "indigo"
                            :icon (reagent/as-element [nbase/icon {:as Ionicons :name "ios-checkmark"}])
                            :on-press (fn [e] (js/console.log "on press icon button")
                                        (bridge/editor-content)
                                        (re-frame/dispatch [:navigate-back]))}]))}})

(def question-list
  {:name       :question-list
   :component  list-view
   :options
   {:title ""
    :headerRight
    (fn [tag id classname]
      (reagent/as-element
        [nbase/icon-button {:justifyContent "center" :alignItems "center"
                            :_pressed {:bg (theme/color "blue.300" "blue.500")}
                            :borderRadius "full"
                            :icon (reagent/as-element
                                    [nbase/icon
                                     {:as Ionicons :name "search-outline" :size "5"
                                      :color (theme/color "blue.600" "blue.800")}])
                            :on-press (fn [e] (js/console.log "on press icon button")
                                        (re-frame/dispatch [:navigate-to :search-base]))}]))}})

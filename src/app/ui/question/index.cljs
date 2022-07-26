(ns app.ui.question.index
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.text :as text]
    [app.ui.basic.theme :as theme]
    [app.handler.animation :as animation]
    [app.text.message :refer [labels]]
    [app.text.message :refer [labels]]
    [app.util.time :as time]
    ["lottie-react-native" :as lottie]

    [steroid.rn.core :as srn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native" :as rn :refer [Dimensions]]
    ["react-native-vector-icons/Ionicons" :default Ionicons]))


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
      (let [questions @(re-frame/subscribe [:question-list])]
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
                                      :onPress (fn []
                                                 (re-frame/dispatch [:delete-question (:id @model)])
                                                 (reset! is-open false))}
             [text/measured-text {:color "#9ca3af"} (get labels :delete)]]]]]
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
               :data      questions
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
                                                  :on-press (fn [e]
                                                              (re-frame/dispatch [:navigate-to :question-detail])
                                                              (re-frame/dispatch [:get-answers (j/get item :id)])
                                                              (re-frame/dispatch [:set-question (bean/->clj item)]))}
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
                                   [srn/touchable-highlight {:underlayColor "#cccccc"
                                                             :onPress (fn []
                                                                        (reset! is-open true)
                                                                        (reset! model (bean/->clj item)))
                                                             :style {:height 24}}
                                    [nbase/icon {:as Ionicons :name "ios-ellipsis-vertical-sharp"
                                                 :size "4" :color "indigo.500"
                                                 :mb 40}]]
                                   [nbase/box {:justifyContent "flex-end"}
                                    [nbase/box {:mb 6 :alignItems "center"}
                                     [nbase/icon {:as Ionicons :name "ios-heart-sharp"
                                                  :size "4" :color "indigo.500"}]
                                     [text/measured-text {:color "#d4d4d8"} "1024"]]
                                    [nbase/box {:mb 6 :alignItems "center"}
                                     [nbase/icon {:as Ionicons :name "document-text"
                                                  :size "4" :color "indigo.500"}]
                                     [text/measured-text {:color "#d4d4d8"} (str (j/get item :answer_count))]]
                                    [nbase/box {:alignItems "center" :justifyContent "center"}
                                     [nbase/icon {:as Ionicons :name "time-outline"
                                                  :size "4" :color "gray.300"
                                                  :mb 1}]
                                     [text/measured-text {:color "#d4d4d8"} (time/month-date-from-string (j/get item :created_at))]]]]]])))
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
              :autoPlay true}]]]))))
       ; [nbase/box {:right 4
       ;             :bottom 2}
       ;            ; :position "absolute"}
       ;  [nbase/icon-button {:w 16 :h 16 :borderRadius "full" :variant "solid" :colorScheme "indigo"
       ;                      :justifyContent "center" :alignSelf "center" :alignItems "center"
       ;                      :icon (reagent/as-element [nbase/icon {:as Ionicons :name "ios-add"}])
       ;                      :onPress (fn [e]
       ;                                 (js/console.log "icon-button on press")
       ;                                 (re-frame/dispatch [:navigate-to :question-detail]))}]]])))


(def question-list
  {:name       :question-list
   :component  list-view
   :listeners {:tabPress (fn [e]
                           (re-frame/dispatch [:get-questions]))}
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
                                        (re-frame/dispatch [:navigate-to :search-base])
                                        (re-frame/dispatch [:put-search-you-type-result {:data {:total 0, :result []}}]))}]))}})

(ns app.ui.home.views
  (:require
   [reagent.core :as reagent]
   [app.ui.components :as ui]
   [app.ui.text :as text]
   [app.ui.nativebase :as nbase]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   [steroid.rn.core :as rn]
   [steroid.rn.components.list :as rnlist]
   [steroid.rn.components.touchable :as touchable]

   [app.handler.animatable :as animatable]

   [app.ui.basic.theme :as theme]
   ["react-native-modal" :default rnmodal]
   ["react-native" :refer [Dimensions Appearance]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["native-base" :refer [extendTheme]]))

(defn home []
  (let [current-theme (reagent/atom (if (true? (j/get @theme/theme :dark))
                                      "dark"
                                      "light"))]
    ; (js/console.log "current-theme initial >>>> " @current-theme)
    ; (js/console.log "initial theme " (j/get @theme/theme :dark))
    (fn []
      [nbase/center {:flex 1 :safeArea true}
       [nbase/button { :colorScheme (theme/color "teal" "blue")
                       :onPress (fn [e]
                                  (js/console.log "theme >>> ")
                                               ; (extendTheme (bean/->js {:useSystemColorMode false :initialColorMode "dark"})))
                                  ; (js/console.log "##### " (j/call Appearance :getColorScheme))
                                 ; (js/console.log "1>>>>> " @current-theme " >>> ")
                                  (let [th (condp = @current-theme
                                             "light" "dark"
                                             "dark" "light")]
                                    (reset! current-theme th)
                                    (theme/update-theme th)))}
                                ;   (js/console.log "2>>>>> " @current-theme " >>> " th  ">>> ")))}
         "update theme"]])))


; (defn home []
;   (let [is-open (reagent/atom false)]
;     (fn []
;      [nbase/center {:flex 1 :px 3 :safeArea true}
;       [nbase/button {:onPress #(reset! is-open (not @is-open))} "open presence"]
;       [nbase/presence-transition {:visible @is-open
;                                   :initial {:opacity 0}
;                                   :animate {:opacity 1 :transition {:duration 250}}}
;        [nbase/box {:bg "primary.600" :p 5}]]])))
; (defn home []
;   (let [is-open (reagent/atom false)
;         screen-height (.-height (.get Dimensions "window"))
;         screen-width (.-width (.get Dimensions "window"))]
;     (fn []
;       [nbase/center {:flex 1 :safeArea true}
;        [nbase/button {:onPress #(reset! is-open true)} "open modal"]
;        [:> rnmodal { :isVisible  @is-open
;                      :scrollHorizontal true
;                      :backdropColor "lightGray"
;                      :transparent false
;                      :swipeDirection ["down"]
;                      :onSwipeComplete (fn [e] (reset! is-open false)
;                                         (js/console.log ">>>> oen swipe .... "))
;                      :style {:margin 0 :alignItems nil :justifyContent nil}}
;         [ui/safe-area-consumer
;          [nbase/zstack {:flex 1 :width "100%" :borderTopRadius "md"}
;           [nbase/box
;            [nbase/text "hello world"]]
;           [nbase/box {:top 2
;                       :right 2}
;            [nbase/icon-button {;:w 4 :h 4
;                                :borderRadius "full" ;:bg "blue.200"
;                                :justifyContent "center" :alignSelf "center" :alignItems "center"
;                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "close-circle-outline"}])
;                                :onPress (fn [e]
;                                           (js/console.log "icon-button on press"))}]]]]]])))

; (defn home []
;   (let [is-open (reagent/atom false)
;         screen-height (.-height (.get Dimensions "window"))
;         screen-width (.-width (.get Dimensions "window"))]
;     (fn []
;       [nbase/center {:flex 1 :px 3 :safeArea true}
;        [nbase/button {:onPress #(reset! is-open true)} "open modal"]
;        [nbase/modal {:isOpen @is-open :onClose #(reset! is-open false)}
;         [nbase/modal-content
;          [nbase/modal-close-button]
;          [nbase/modal-body
;           [nbase/box {:style {:width screen-width :height screen-height}}
;            [nbase/text "hello world"]]]]]])))

; (defn home []
;   (let [is-open (reagent/atom false)
;         screen-height (.-height (.get Dimensions "window"))]
;     (fn []
;      [nbase/center {:flex 1 :px 3 :safeArea true}
;        [nbase/button {:onPress #(reset! is-open true)} "open modal"]
;        [nbase/modal {:isOpen @is-open :onClose #(reset! is-open false)}
;         [nbase/box {:bg "coolGray.50" :shadow 1 :rounded "lg" :maxHeight (str (- screen-height 150) "px")
;                     :minHeight "40%" :overflow "hidden"}
;          [nbase/box
;           {:flex 1 :justifyContent "center" :alignItem "center" :flexDirection "row"
;            :pt "2" :py "3"}
;           [rn/touchable-highlight {:style { :padding 10}
;                                    :underlayColor "#cccccc"
;                                    :onPress #(js/console.log "touchable >>> ")}
;            [text/measured-text {:color "#4b5563"} "Arial"]]
;           [nbase/divider {:orientation "vertical"}]
;           [rn/touchable-highlight {:style { :padding 10}
;                                    :underlayColor "#cccccc"
;                                    :onPress #(js/console.log "touchable >>> ")}
;            [text/measured-text {:color "#4b5563"} "Nunito Sans"]]
;           [nbase/divider {:orientation "vertical"}]
;           [rn/touchable-highlight {:style { :padding 10}
;                                    :underlayColor "#cccccc"
;                                    :onPress #(js/console.log "touchable >>> ")}
;            [text/measured-text {:color "#4b5563"} "Roboto"]]]]]])))




; (defn home []
;   ; [dragable/draggable-view-gesture]
;   [dragable/real-draggable-view])

; (defn home []
;   (let [scale-value (reagent/atom 1)]
;     (fn []
;       [nbase/center {:height "100%"}
;        ; [animatable/text {:animation "slideInDown" :iterationCount 5 :direction "alternate"}
;        ;  "Up and down you go"]
;        [animatable/text {:animation "pulse" :easing "ease-out" :iterationCount "infinite" :style {:textAlign "center"}} "❤️"]
;        [nbase/hstack {:my 2
;                       :justifyContent "space-between"}
;         [rn/touchable-highlight {:on-press #(reset! scale-value (+ @scale-value 0.5))
;                                  :style {:margin-right 20}}
;          [text/measured-text {:fontSize 18} "button  +"]]
;         [rn/touchable-highlight {:on-press #(reset! scale-value (- @scale-value 0.5))}
;          [text/measured-text {:fontSize 18} "button  -"]]
;         [animatable/view {:transition "scale" :style {:transform [{:scale @scale-value}]
;                                                       :margin-left 60}}
;          [text/measured-text {:fontSize 18} "text to scale"]]]])))
  ; [text/measured-text "Up and down you go"]]])
 ; [:> SendBox]])
 ; [Refresh]])

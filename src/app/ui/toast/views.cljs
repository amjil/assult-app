(ns app.ui.toast.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [app.text.index :as text]
   [applied-science.js-interop :as j]
   ["react-native" :as rnn]
   [cljs-bean.core :as bean]
   [steroid.rn.core :as rn]
   [steroid.rn.components.touchable :as touchable]
   [app.ui.components :as ui]
   [steroid.rn.components.ui :as rn-ui]))

(def animated (j/get rnn :Animated))
(def animated-value (j/get animated :Value))
(def animated-view (reagent/adapt-react-class (.-View animated)))

(def is-show (reagent/atom false))

(defn toast []
  [touchable/touchable-without-feedback {:on-press #(do (js/console.log "on-press toast >>>"))}
   [rn/view {:style {:position "absolute"
                     :left 0 :right 0
                     :top 0
                     :bottom 0
                     :elevation 1999
                     :alignItems "center"
                     :justifyContent "center"
                     :z-index 10000}}
             ; :pointerEvents "auto"}
    [:> animated.View
     {:sytle {:backgroundColor "black"
              :opacity 1
              :borderRadius 5
              :padding 10}}
     [rn/text {:style {:color "white"}} "Hello world!"]]]])

(defn testview []
  (reagent/create-class
    {:get-initial-state
     ;; instantiate a new Animated.Value class with 0 = completely hidden
      #(bean/->js {:bounceValue (animated-value. 0)})
     :component-did-mount
      (fn [this]
        (.setValue (j/get-in this [:state :bounceValue]) 1.5)
        (->
          ;; set the animation properties and start it off
          (.spring animated (j/get-in this [:state :bounceValue]) (bean/->js {:toValue 0.8
                                                                              :friction 1
                                                                              :useNativeDriver true}))
          (.start)))
     :display-name "test-view"
     ;; render does not receive this as first argument
     ;; to get this, we need to use (reagent.core/current-component)
     :reagent-render
      (fn []
        (let [this (reagent/current-component)]
          [animated-view {:style {:flex 1
                                  :transform [{:scale (j/get-in this [:state :bounceValue])}]
                                  :backgroundColor "red"
                                  :borderRadius 10
                                  :margin 15
                                  :shadowColor "#000000"
                                  :shadowOpacity 0.7
                                  :shadowRadius 2
                                  :shadowOffset {:height 1 :width 0}}}
            [rn/text {} "This is a test"]]))}))
;
(defn testview2 []
  (reagent/create-class
    {:get-initial-state
     ;; instantiate a new Animated.Value class with 0 = completely hidden
      #(bean/->js {:alphaValue (animated-value. 0)})
     :component-did-mount
      (fn [this]
        (.setValue (j/get-in this [:state :alphaValue]) 0)
        (js/console.log "testview did-mount >>>>")
        (->
          ;; set the animation properties and start it off
          (.timing animated
            (j/get-in this [:state :alphaValue])
            (bean/->js {:toValue         0.6
                        :duration        500
                        :useNativeDriver false}))
          (.start)))
        ; (js/setTimeout
        ;   #(do
        ;     (js/console.log "set timeout >>>")
        ;     (.setValue (j/get-in this [:state :alphaValue]) 0)
        ;     (->
        ;       (.timing animated
        ;         (j/get-in this [:state :alphaValue])
        ;         (bean/->js {:toValue         0
        ;                     :duration        500
        ;                     :useNativeDriver false}))))
        ;   2000))
     ; :component-did-update
     :display-name "test-view2"
     ;; render does not receive this as first argument
     ;; to get this, we need to use (reagent.core/current-component)
     :reagent-render
      (fn []
        (let [this (reagent/current-component)]
          [animated-view
           {:style {:opacity (j/get-in this [:state :alphaValue])
                    :backgroundColor "black"
                    :borderRadius 5
                    :padding 10
                    :height 200
                    :alignItems "center"
                    :justifyContent "center"}}
           [text/text-inline {:width 30 :fill "white" :font :white :font-size 18} "ᠪᠤᠷᠤᠭᠤ ᠭᠠᠷᠪᠠ"]]))}))

(defn view []
  (let [flag (reagent/atom false)]
    (fn []
      [ui/safe-area-consumer
       [rn/view {:style {:width "100%" :height "100%" :flex-direction "column"}}
        [rn-ui/button {:title "hello "
                       :on-press #(reset! flag (not @flag))}]
        ; [toast]]])
        (if @flag
          ;
          [touchable/touchable-without-feedback
           {:on-press #(do (js/console.log "on-press toast >>>")
                         (reset! flag false))}
           [rn/view {:style {:position "absolute"
                             :left 0
                             :right 0
                             :top 0
                             :bottom 0
                             :elevation 1999
                             :alignItems "center"
                             :justifyContent "center"
                             :z-index 10000}

                     :pointerEvents "auto"}

            [testview2]]])]])))
(comment
  animated.View
  sqlite)

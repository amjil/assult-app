(ns app.ui.setting.detail
  (:require
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [steroid.rn.core :as rn]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [app.handler.gesture :as gesture]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]
   ["react-native-smooth-blink-view" :default blinkview]
   ["react-native-measure-text-chars" :as rntext]
   ["react-native-svg" :as svg]))
;
(defn cursor-location [evt line-height widths]
  (let [ex (j/get evt :x)
        ey (j/get evt :y)

        _ (js/console.log "xxx1111" ex ey)
        x (loop [i      0]
            (cond
              (empty? widths)
              0

              (<= ex (+ (* i line-height) line-height))
              i

              (>= i (count widths))
              (if (zero? (count widths))
                0
                (dec i))

              (= (inc i) (count widths))
              i

              :else
              (recur (inc i))))
        _ (js/console.log "xxx1112 = " x)

        line (nth widths x)
        item-count (count line)

        _ (js/console.log "xxx >>> " item-count)
        y (loop [i      0]
            (js/console.log "xxx111>> " i)
            (cond
              (empty? widths)
              0

              (= item-count i)
              (let [item (last line)]
                (+ (:y item) (:width item)))

              :else
              (let [item (nth line i)
                    item-y (+ (:y item) (:width item))
                    half-width (/ (:width item) 2)]
                (js/console.log "item y >>" (bean/->js item))
                (cond
                  (<= ey (+ (:y item) half-width))
                  (:y item)

                  (<= ey item-y)
                  item-y

                  :else
                  (recur (inc i))))))
        _ (js/console.log "xxx1113")]
    ; [(+ x 8) y]
    (js/console.log "result = " (* line-height x) y)
    [(* line-height x) y]))

(defn text-widths [info]
  (let [widths (map #(:charWidths %) (:lineInfo info))]
    (map #(map-indexed (fn [idx item] {:width item :y (reduce + (take idx %))}) %) widths)))

(defn input-view [atomic params]
  (let [{:keys [name props]} params
        [theme-props text-props] (nbase/theme-props name props)
        t-props text-props
        padding-value (:padding t-props)
        text-props (select-keys text-props [:fontSize :color])
        info (bean/->clj (rntext/measure (bean/->js (merge (assoc text-props :text (:text @atomic) :useCharsWidth true)))))
        line-width (+ 1 (/ (:height info) (:lineCount info)))
        widths (text-widths info)]
    (if (false? (:flag @atomic))
      (do
        (swap! atomic assoc :x 0)
        (swap! atomic assoc :y (:width info))
        (swap! atomic assoc :flag true)))
    (js/console.log (bean/->js text-props))
    (js/console.log (bean/->js t-props))
    (js/console.log (bean/->js info))
    (js/console.log (bean/->js widths))
    [nbase/box (merge theme-props (if (:focus @atomic) (:_focus theme-props))
                 {:on-press #(swap! atomic assoc :focus true)})
     [gesture/tap-gesture-handler
      {:onHandlerStateChange #(do
                                (swap! atomic assoc :focus true)
                                (if (gesture/tap-state-end (j/get % :nativeEvent))
                                  (let [[ex ey] (cursor-location (j/get % :nativeEvent) line-width widths)]
                                        ; ey (- ey padding-value)]
                                    (swap! atomic assoc :x ex)
                                    (swap! atomic assoc :y ey))))}
      [gesture/pan-gesture-handler {:onGestureEvent #(let [[ex ey] (cursor-location (j/get % :nativeEvent) line-width widths)]
                                                           ; ey (- ey padding-value)]
                                                       (swap! atomic assoc :x ex)
                                                       (swap! atomic assoc :y ey))}
       [nbase/zstack
        [nbase/measured-text (select-keys text-props [:fontSize :color])  (:text @atomic) info]
        [nbase/box {:style {:margin-top (:y @atomic) :margin-left (:x @atomic)}}
         [:> blinkview {"useNativeDriver" false}
          [:> svg/Svg {:width (:height info) :height 2}
           [:> svg/Rect {:x "0" :y "0" :width (:height info) :height 2 :fill "blue"}]]]]]]]]))

(defn view []
  (let [;focus (reagent/atom false)
        ; height (reagent/atom nil)
        ; value (reagent/atom "ᠡᠷᠬᠡ")
        props (reagent/atom {:focus false :height nil :text "abc" :x 0 :y 0 :flag false})
        params {:name "Input" :props {}}]
    (fn []
      (let [mobile @(re-frame/subscribe [:user-mobile])
            loading @(re-frame/subscribe [:loading])]
        [nbase/box {:safeArea true}
         [nbase/flex {:h "100%" :justifyContent "space-between"}
          [nbase/pressable {:flex 1 :flexDirection "row" :justifyContent "space-between"
                            :ml 10
                            :on-press #(swap! props assoc :focus false)
                            :on-layout #(let [h (j/get-in % [:nativeEvent :layout :height])]
                                          (swap! props assoc :height h))}
           [nbase/hstack {:space 2}
            [nbase/measured-text {} "ᠨᠡᠷ᠎ᠡ"]
            [:f> input-view props params]
            ; [:f> nbase/styled-text-view focus "Input" {:variant "outline"}]
            [nbase/measured-text {:height (:height @props)} "ᠦᠨᠡᠨ ᠨᠡᠷ᠎ᠡ ᠪᠤᠶᠤ ᠦᠨᠡᠨ ᠨᠡᠷ᠎ᠡ ᠪᠤᠶᠤ ᠨᠠᠢᠵᠠ ᠨᠠᠷ ᠲᠠᠭᠠᠨ ᠠᠮᠠᠷᠬᠠᠨ ᠲᠠᠨᠢᠭᠳᠠᠬᠤ ᠨᠡᠷ᠎ᠡ ᠪᠠᠨ ᠲᠠᠭᠯᠠᠭᠠᠷᠠᠢ"]]
           [nbase/flex {:flexDirection "row" :justifyContent "flex-end"}
            [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                                :justifyContent "center" :alignSelf "center" :alignItems "center"
                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])}]]]
                               ; :on-press #(do)}]]]]))
                                            ; (re-frame/dispatch [:register-user {:mobile mobile :code @code}]))}]]]]))
          [nbase/box {:h 300} "abc"]]]))))

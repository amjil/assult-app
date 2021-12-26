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
   [app.ui.keyboard.index :as keyboard]
   [app.ui.keyboard.candidates :as candidates]
   [app.ui.text.index :as text]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]
   ["react-native-smooth-blink-view" :default blinkview]
   ["react-native-measure-text-chars" :as rntext]
   ["react-native-svg" :as svg]))

(defn text-widths [info]
  (let [widths (map #(:charWidths %) (:lineInfo info))]
    (map #(map-indexed (fn [idx item] {:width item :y (reduce + (take idx %))}) %) widths)))

(defn input-view [atomic params]
  (let [{:keys [name props]} params
        [theme-props text-props] (nbase/theme-props name props)
        t-props text-props
        padding-value (:padding t-props)
        text-props (select-keys text-props [:fontSize :color])
        info (bean/->clj
               (rntext/measure
                 (bean/->js
                   (assoc text-props
                          :text (:text @atomic)
                          :useCharsWidth true))))
        line-width (/ (:height info) (:lineCount info))
        widths (text-widths info)]
    (if (false? (:flag @atomic))
      (do
        (swap! atomic assoc :x 0)
        (swap! atomic assoc :y (:width info))
        (swap! atomic assoc :flag true)
        (swap! atomic assoc :cursor (count widths))))
    (js/console.log (bean/->js text-props))
    (js/console.log (bean/->js t-props))
    (js/console.log (bean/->js info))
    (js/console.log (bean/->js widths))
    (js/console.log line-width)
    [gesture/tap-gesture-handler
     {:onHandlerStateChange #(do
                               (swap! atomic assoc :focus true)
                               (if (gesture/tap-state-end (j/get % :nativeEvent))
                                 (let [[ix ex iy ey] (text/cursor-location (j/get % :nativeEvent) padding-value line-width widths)]
                                   (swap! atomic assoc :x ex)
                                   (swap! atomic assoc :y ey)
                                   (re-frame/dispatch [:set-editor-cursor (+ iy (apply + (map (fn [x] (count x)) (take ix widths))))]))))}
     [gesture/pan-gesture-handler {:onGestureEvent #(let [[ix ex iy ey] (text/cursor-location (j/get % :nativeEvent) padding-value line-width widths)]
                                                      (swap! atomic assoc :x ex)
                                                      (swap! atomic assoc :y ey)
                                                      (re-frame/dispatch [:set-editor-cursor (+ iy (apply + (map (fn [x] (count x)) (take ix widths))))]))}
      [nbase/box (merge theme-props (if (:focus @atomic) (:_focus theme-props)))
                   ; {:on-press #(swap! atomic assoc :focus true)})
       [nbase/zstack
        [nbase/measured-text (select-keys text-props [:fontSize :color]) (:text @atomic) info]
        (if (:focus @atomic)
          [nbase/box {:style {:margin-top (:y @atomic) :margin-left (:x @atomic)}}
           [:> blinkview {"useNativeDriver" false}
            [:> svg/Svg {:width (:height info) :height 2}
             [:> svg/Rect {:x "0" :y "0" :width (:height info) :height 2 :fill "blue"}]]]])]]]]))

(defn view []
  (let [props (reagent/atom {:focus false :height nil
                             :text "abcd" :x 0 :y 0 :flag false})
        params {:name "Input" :props {}}
        height (reagent/atom nil)]
    (fn []
      (let [mobile @(re-frame/subscribe [:user-mobile])
            loading @(re-frame/subscribe [:loading])]
        [nbase/flex {:style {:height "100%"} :justifyContent "space-between"}
         [nbase/pressable {:flexDirection "row" :justifyContent "space-between"
                           :ml 10
                           :mt 10
                           :mb 10
                           :flex 1
                           :on-press #(swap! props assoc :focus false)
                           :on-layout #(let [h (j/get-in % [:nativeEvent :layout :height])]
                                         (swap! props assoc :height h))}
          [nbase/hstack {:space 2}
           [nbase/measured-text {:fontSize 18 :fontFamily "MongolianBaiZheng"} "ᠨᠡᠷ᠎ᠡ"]
           [:f> input-view props params]
           [nbase/measured-text {:fontSize 18 :fontFamily "MongolianBaiZheng" :height (:height @props)} "ᠦᠨᠡᠨ ᠨᠡᠷ᠎ᠡ ᠪᠤᠶᠤ ᠨᠠᠢᠵᠠ ᠨᠠᠷ ᠲᠠᠭᠠᠨ ᠠᠮᠠᠷᠬᠠᠨ ᠲᠠᠨᠢᠭᠳᠠᠬᠤ ᠨᠡᠷ᠎ᠡ ᠪᠠᠨ ᠲᠠᠭᠯᠠᠭᠠᠷᠠᠢ"]]
          [nbase/flex {:flexDirection "row" :justifyContent "flex-end"}
           [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                               :justifyContent "center" :alignSelf "center" :alignItems "center"
                               :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])}]]]
                              ; :on-press #(do)}]]]]))
                                           ; (re-frame/dispatch [:register-user {:mobile mobile :code @code}]))}]]]]))
         [candidates/views]
         [nbase/box {:style {:height 220}}
          [keyboard/keyboard props]]]))))

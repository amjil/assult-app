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
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]
   ["react-native-smooth-blink-view" :default blinkview]
   ["react-native-svg" :as svg]))


(defn input-view [atomic params]
  (let [{:keys [name props]} params
        [theme-props text-props] (nbase/theme-props name props)
        t-props text-props
        padding-value (:padding t-props)
        text-props (select-keys text-props [:fontSize :color])
        
        info @(re-frame/subscribe [:editor-info])
        [x y] @(re-frame/subscribe [:editor-selection-xy])]
    (if (false? (:flag @atomic))
      (re-frame/dispatch
       [:init-editor {:text (:text @atomic) :text-props text-props :padding padding-value}]))
    [gesture/tap-gesture-handler
     {:onHandlerStateChange #(do
                               (swap! atomic assoc :focus true)
                               (if (gesture/tap-state-end (j/get % :nativeEvent))
                                 (re-frame/dispatch [:cursor-localtion (j/get % :nativeEvent)])))}
     [gesture/pan-gesture-handler {:onGestureEvent #(re-frame/dispatch [:cursor-location (j/get % :nativeEvent)])}
      [nbase/box (merge theme-props (if (:focus @atomic) (:_focus theme-props)))
                   ; {:on-press #(swap! atomic assoc :focus true)})
       [nbase/zstack
        [nbase/measured-text text-props (:text @atomic) info]
        (if (:focus @atomic)
          [nbase/box {:style {:margin-top y :margin-left x}}
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
          [keyboard/keyboard]]]))))

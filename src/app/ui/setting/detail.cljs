(ns app.ui.setting.detail
  (:require
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [steroid.rn.core :as rn]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]
   ["react-native-smooth-blink-view" :default blinkview]
   ["react-native-measure-text-chars" :as rntext]
   ["react-native-svg" :as svg]))

(defn input-view [focus t name props]
  (let [[theme-props text-props] (nbase/theme-props name props)
        text-props (select-keys text-props [:fontSize :color])
        info (if @t
              (rntext/measure (bean/->js (merge (assoc text-props :text @t))))
              (rntext/measure (bean/->js (merge (assoc text-props :text "abc")))))]
    (js/console.log (bean/->js info))
    [nbase/pressable (merge theme-props (if @focus (:_focus theme-props))
                       {:on-press #(reset! focus true)})
     [nbase/zstack
      [nbase/measured-text (select-keys text-props [:fontSize :color])  "hello world" info]]]))
        ; [:> blinkview {"useNativeDriver" false}
        ;  [rn/view {:style {:position :absolute :top (or @y 0) :left (or @x 0)}}
        ;   [:> svg/Svg {:width 32 :height 2}
        ;    [:> svg/Rect {:x "0" :y "0" :width 32 :height 2 :fill "blue"}]]]]]])))

(defn view []
  (let [focus (reagent/atom false)
        height (reagent/atom nil)
        value (reagent/atom nil)]
    (fn []
      (let [mobile @(re-frame/subscribe [:user-mobile])
            loading @(re-frame/subscribe [:loading])]
        [nbase/box {:safeArea true}
         [nbase/flex {:h "100%" :justifyContent "space-between"}
          [nbase/pressable {:flex 1 :flexDirection "row" :justifyContent "space-between"
                            :ml 10
                            :on-press #(reset! focus false)
                            :on-layout #(let [h (j/get-in % [:nativeEvent :layout :height])]
                                          (reset! height h))}
           [nbase/hstack {:space 2}
            [nbase/measured-text {} "ᠨᠡᠷ᠎ᠡ"]
            [:f> input-view focus value "Input" {}]
            ; [:f> nbase/styled-text-view focus "Input" {:variant "outline"}]
            [nbase/measured-text {:height @height} "ᠦᠨᠡᠨ ᠨᠡᠷ᠎ᠡ ᠪᠤᠶᠤ ᠦᠨᠡᠨ ᠨᠡᠷ᠎ᠡ ᠪᠤᠶᠤ ᠨᠠᠢᠵᠠ ᠨᠠᠷ ᠲᠠᠭᠠᠨ ᠠᠮᠠᠷᠬᠠᠨ ᠲᠠᠨᠢᠭᠳᠠᠬᠤ ᠨᠡᠷ᠎ᠡ ᠪᠠᠨ ᠲᠠᠭᠯᠠᠭᠠᠷᠠᠢ"]]
           [nbase/flex {:flexDirection "row" :justifyContent "flex-end"}
            [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                                :justifyContent "center" :alignSelf "center" :alignItems "center"
                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])}]]]
                               ; :on-press #(do)}]]]]))
                                            ; (re-frame/dispatch [:register-user {:mobile mobile :code @code}]))}]]]]))
          [nbase/box {:h 300} "abc"]]]))))

(ns app.ui.setting.detail
  (:require
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [steroid.rn.core :as rn]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]))

(defn view []
  (let [focus (reagent/atom false)]
    (fn []
      (let [mobile @(re-frame/subscribe [:user-mobile])
            loading @(re-frame/subscribe [:loading])]
        [nbase/box {:mt 10 :safeArea true}
         [nbase/flex {:h "100%" :justifyContent "space-between"}
          [nbase/pressable {:flex 1 :flexDirection "row" :justifyContent "space-between"
                            :on-press #(reset! focus false)}
           [nbase/hstack {:space 2}
            [nbase/measured-text {} "name"]
            [:f> nbase/styled-text-view focus "Input" {:variant "outline"}]
            [nbase/measured-text {} "outline ass hello "]]
           [nbase/flex {:flexDirection "row" :justifyContent "flex-end"}
            [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                                :justifyContent "center" :alignSelf "center" :alignItems "center"
                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])}]]]
                               ; :on-press #(do)}]]]]))
                                            ; (re-frame/dispatch [:register-user {:mobile mobile :code @code}]))}]]]]))
          [nbase/box {:h 200} "abc"]]]))))

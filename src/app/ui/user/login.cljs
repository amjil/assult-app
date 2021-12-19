(ns app.ui.user.login
  (:require
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [steroid.rn.core :as rn]
   [reagent.core :as reagent]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]))

(defn view []
  (let [show (reagent/atom true)]
    (fn []
      [nbase/box {:h "100%" :safeArea true}
       [nbase/flex {:mt 20 :mx 10 :h "80%" :justifyContent "space-between"}
        [nbase/vstack {:space 4}
         [nbase/hstack {}
          [nbase/measured-text {} "mobile"]
          [nbase/measured-text {} "number"]]
         [nbase/input {:keyboardType "number-pad"
                       :placeholder "Input Mobile"
                       :onFocus #(reset! show false)
                       :onBlur #(reset! show true)}]
         [nbase/flex {:flexDirection "row" :justifyContent "space-between"}
          [nbase/hstack {:space 2}
           [nbase/pressable
            {:onPress #(js/console.log "aaa")}
            [:f> nbase/styled-text-view {:color "darkBlue.600"} "Login in with name"]]
           [nbase/pressable
            {:onPress #(js/console.log "bbb")}
            [:f> nbase/styled-text-view {:color "darkBlue.600"} "Login in with email"]]]
          [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                              :justifyContent "center" :alignSelf "center" :alignItems "center"
                              :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])}]]]
        (if @show
          [nbase/center
           [nbase/hstack {:space 3}
            [nbase/icon-button {:size "sm" :borderRadius "full" :variant "solid" :colorScheme "muted"
                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "logo-apple"}])}]
            [nbase/icon-button {:size "sm" :borderRadius "full" :variant "solid" :colorScheme "muted"
                                :icon (reagent/as-element [nbase/icon {:as MaterialCommunityIcons :name "wechat"}])}]]])]])))

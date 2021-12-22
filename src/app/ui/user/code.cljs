(ns app.ui.user.code
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
  (let [code (reagent/atom "")]
    (fn []
      (let [mobile @(re-frame/subscribe [:user-mobile])
            loading @(re-frame/subscribe [:loading])]
        [nbase/box {:h "100%" :safeArea true}
         [nbase/flex {:mt 0 :mx 10 :h "80%" :justifyContent "space-between"}
          [nbase/vstack {:space 4}
           [nbase/hstack {}
            [nbase/measured-text {} "ᠰᠢᠯᠭᠠᠬᠤ"]
            [nbase/measured-text {} " ᠳ᠋ᠤᠭᠠᠷ"]]
           [nbase/input {:keyboardType "number-pad"
                         :placeholder "Mobile Code"
                         :on-change-text #(reset! code %)}]
           [nbase/flex {:flexDirection "row" :justifyContent "flex-end"}
            [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                                :justifyContent "center" :alignSelf "center" :alignItems "center"
                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])
                                :on-press #(do
                                             (js/console.log ">>>> ")
                                             (re-frame/dispatch [:register-user {:mobile mobile :code @code}]))}]]]]]))))

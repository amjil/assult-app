(ns app.ui.user.code
  (:require
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [steroid.rn.core :as rn]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [app.ui.text :as text]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]))

(defn view []
  (let [code (reagent/atom "")
        props {:fontSize 18 :fontFamily "MongolianBaiZheng"}]
    (fn []
      (let [mobile @(re-frame/subscribe [:user-mobile])
            loading @(re-frame/subscribe [:loading])
            user-exists @(re-frame/subscribe [:user-exists])]
        [nbase/box {:h "100%" :safeArea true}
         [nbase/flex {:mt 0 :mx 10 :h "80%" :justifyContent "space-between"}
          [nbase/vstack {:space 4}
           [nbase/hstack {}
            [text/measured-text props "ᠰᠢᠯᠭᠠᠬᠤ"]
            [text/measured-text props " ᠳ᠋ᠤᠭᠠᠷ"]]
           [nbase/input {:keyboardType "number-pad"
                         :placeholder "Mobile Code"
                         :on-change-text #(reset! code %)}]
           [nbase/flex {:flexDirection "row" :justifyContent "flex-end"}
            [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                                :justifyContent "center" :alignSelf "center" :alignItems "center"
                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])
                                :on-press #(do
                                             (js/console.log ">>>> " user-exists)
                                             (if user-exists
                                               (re-frame/dispatch [:login {:mobile mobile :code @code}])
                                               (re-frame/dispatch [:register-user {:mobile mobile :code @code}])))}]]]]]))))

(ns app.ui.user.password
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
  (let [code (reagent/atom "")
        props {:fontSize 18 :fontFamily "MongolianBaiZheng"}]
    (fn []
      (let [mobile @(re-frame/subscribe [:user-mobile])
            loading @(re-frame/subscribe [:loading])]
        [nbase/box {:h "100%" :safeArea true}
         [nbase/flex {:mt 0 :mx 10 :h "80%" :justifyContent "space-between"}
          [nbase/vstack {:space 4}
           [nbase/hstack {}
            [nbase/measured-text props "ᠨᠢᠭᠤᠴᠠ"]
            [nbase/measured-text props "ᠦᠭᠡ"]]
           [nbase/input {:type :password
                         :on-change-text #(reset! code %)}]
           [nbase/flex {:flexDirection "row" :justifyContent "space-between"}
            [nbase/hstack {:space 2}
             [nbase/pressable
              {:onPress #(do
                            (re-frame/dispatch [:user-send-code {:mobile mobile :direction 2}])
                            (re-frame/dispatch [:navigate-to :user-in-code]))}
              [:f> nbase/styled-text-view {:color "darkBlue.600" :fontFamily "MongolianBaiZheng"} "ᠰᠢᠯᠭᠠᠬᠤ  ᠳ᠋ᠤᠭᠠᠷ ᠵᠢᠠᠷ ᠨᠡᠪᠲᠡᠷᠡᠬᠦ"]]]
            [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                                :justifyContent "center" :alignSelf "center" :alignItems "center"
                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])
                                :on-press #(re-frame/dispatch [:login {:password @code :mobile mobile}])}]]]]]))))

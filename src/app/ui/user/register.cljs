(ns app.ui.user.register
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
  (let [flag (reagent/atom false)]
    (fn []
      (let [mobile @(re-frame/subscribe [:user-mobile])
            loading @(re-frame/subscribe [:loading])]
        [nbase/box {:h "100%" :safeArea true}
         [nbase/flex {:mt 0 :mx 10 :h "80%" :justifyContent "space-between"}
          [nbase/vstack {:space 4}
           [nbase/hstack {}
            [nbase/measured-text {} "ᠰᠢᠨ᠎ᠡ"]
            [nbase/measured-text {} " ᠳ᠋ᠤᠭᠠᠷ"]
            [nbase/measured-text {} "ᠳᠠᠩᠰᠠᠯᠠᠬᠤ"]]
           [nbase/input {:keyboardType "number-pad"
                         :placeholder "Input Mobile"
                         :isReadOnly true}
            mobile]
           [nbase/flex {:flexDirection "row" :justifyContent "space-between"}
            [nbase/vstack {:space 2}
             [nbase/checkbox {:aria-label "check-selected" :on-change #(reset! flag %)}]
             [nbase/measured-text {} "ᠵᠥᠪᠰᠢᠶᠡᠷᠡᠯ ᠊ᠦᠨ ᠪᠢᠴᠢᠭ ᠊ᠢ ᠤᠩᠰᠢᠭᠰᠠᠨ ᠮᠥᠷᠲᠡᠭᠡᠨ ᠵᠥᠪᠰᠢᠶᠡᠷᠡᠨ᠎ᠡ"]]


            [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                                :justifyContent "center" :alignSelf "center" :alignItems "center"
                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])
                                :disabled
                                (cond
                                  (true? (:sign-up loading)) true

                                  (not @flag) true

                                  :else false)
                                :on-press #(do
                                             (js/console.log ">>>> ")
                                             (re-frame/dispatch [:navigate-to :user-in-code]))}]]]]]))))

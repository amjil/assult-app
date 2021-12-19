(ns app.ui.home.index
  (:require
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [steroid.rn.core :as rn]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]))

(defn anonymous []
  [nbase/box {:h "100%" :safeArea true}
   [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                       :justifyContent "center" :alignSelf "center" :alignItems "center"
                       :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])
                       :onPress #(re-frame/dispatch [:navigate-to :login])}]])

(ns app.ui.home.index
  (:require
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [app.handler.navigation :as navigation]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [cljs-bean.core :as bean]
   [steroid.rn.core :as rn]
   [steroid.rn.navigation.bottom-tabs :as bottom-tabs]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]))

(defn anonymous []
  [nbase/box {:h "100%" :safeArea true}
   [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                       :justifyContent "center" :alignSelf "center" :alignItems "center"
                       :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])
                       :onPress #(do ;(re-frame/dispatch [:navigate-to :home]))}]])
                                   (navigation/nav-reset))}]])



(def tab-icons
  {"home1"    "md-home"
   "book"    "md-bookmark"
   "edit"    "md-create"
   "profile" "md-person"})

(defn home1 []
  [nbase/center {:flex 1 :px 3 :safeArea true}
   [nbase/text "abc"]
   [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                       :justifyContent "center" :alignSelf "center" :alignItems "center"
                       :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])
                       :onPress #(re-frame/dispatch [:navigate-to :home5])}]])

(defn home2 []
  [nbase/center {:flex 1 :px 3 :safeArea true}
   [nbase/text "home2"]])

(defn home3 []
  [nbase/center {:flex 1 :px 3 :safeArea true}
   [nbase/text "home3"]])

(defn home4 []
  [nbase/center {:flex 1 :px 3 :safeArea true}
   [nbase/text "home4"]])

(defn home5 []
  [nbase/center {:flex 1 :px 3 :safeArea true}
   [nbase/text "home5"]])

(defn tabs []
  [bottom-tabs/bottom-tab
   {
    :screenOptions
    (fn [options]
     (let [{:keys [route]} (bean/->clj options)]
       (bean/->js {:activeTintColor   "#5cb85c"
                   :inactiveTintColor :black
                   :showLabel         false
                   :tabBarLabel       (fn [] nil)
                   :headerLeft (fn [] nil)
                   :headerShown       false
                   :modal             true
                   :tabBarIcon (fn [data]
                                 (let [{:keys [color]} (bean/->clj data)
                                       icon (get tab-icons (:name route))]
                                   (reagent/as-element
                                     [ui/ion-icons {:name icon :color color :size 20}])))})))}

   [{:name      :home1
     :component home1}
    {:name      :book
     :component home2}
    {:name      :edit
     :component home3}
    {:name      :profile
     :component home4}]])

(ns app.ui.views
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [steroid.rn.core :as rn]
   [steroid.rn.navigation.core :as rnn]
   [steroid.rn.navigation.bottom-tabs :as bottom-tabs]
   [steroid.rn.components.status-bar :as status-bar]
   [steroid.rn.navigation.safe-area :as safe-area]
   [steroid.rn.components.platform :as platform]
   [steroid.rn.components.list :as rn-list]
   [steroid.rn.components.touchable :as touchable]
   [steroid.rn.components.ui :as rn-ui]
   [app.ui.components :as ui]
   [steroid.rn.navigation.stack :as stack]
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]

   ["react-native-smooth-blink-view" :default blinkview]
   ["react-native-svg" :as svg]
   ["react-native-linear-gradient" :default linear-gradient]

   [app.ui.user.login :as login]
   [app.ui.home.index :as home]
   [app.ui.nativebase :as nativebase]))


(when platform/android?
  (status-bar/set-bar-style "light-content")
  (status-bar/set-translucent true))


(def tab-icons
  {"home"    "md-home"
   "book"    "md-bookmark"
   "edit"    "md-create"
   "profile" "md-person"})

(defn screen-options [options]
  (let [{:keys [route]} (bean/->clj options)]
    {:tabBarIcon
     (fn [data]
       (let [{:keys [color]} (bean/->clj data)
             icon (get tab-icons (:name route))]
         (reagent/as-element
          [ui/ion-icons {:name icon :color color :size 30}])))}))


(defn home1 []
  [nativebase/center {:flex 1 :px 3 :safeArea true}
   [nativebase/text "abc"]])

(defn home2 []
  [nativebase/center {:flex 1 :px 3 :safeArea true}
   [nativebase/text "home2"]])

(defn home3 []
  [nativebase/center {:flex 1 :px 3 :safeArea true}
   [nativebase/text "home3"]])

(defn home4 []
  [nativebase/center {:flex 1 :px 3 :safeArea true}
   [nativebase/text "home4"]])

(defn tabs []
  [bottom-tabs/bottom-tab
   {
    ; :screenOptions screen-options}
    :screenOptions
    (fn [options]
     (let [{:keys [route]} (bean/->clj options)]
       (bean/->js {:activeTintColor   "#5cb85c"
                   :inactiveTintColor :black
                   :showLabel         false
                   :tabBarLabel       (fn [] nil)
                   :headerShown       false
                   :modal             true
                   :tabBarIcon (fn [data]
                                 (let [{:keys [color]} (bean/->clj data)
                                       icon (get tab-icons (:name route))]
                                   (reagent/as-element
                                     [ui/ion-icons {:name icon :color color :size 20}])))})))}

   [{:name      :home
     :component home1}
    {:name      :book
     :component home2}
    {:name      :edit
     :component home3}
    {:name      :profile
     :component home4}]])

(defn root-stack []
  [safe-area/safe-area-provider
   [(rnn/create-navigation-container-reload                 ;; navigation container with shadow-cljs hot reload
     {:on-ready #(re-frame/dispatch [:initialise-app])}     ;; when navigation initialized and mounted initialize the app
     [nativebase/nativebase-provider {:config {:dependencies {"linear-gradient" linear-gradient}}}
      [stack/stack {}
       [{:name      :main
         :component home/anonymous
         :options {:title ""}}
        {:name       :login
         :component  login/view
         :options    {:title ""}}]]])]])
       ; [:f> nativebase/view]])]])
       ; [drawer/view]])]])
       ; [login/view]]])]])

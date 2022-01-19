(ns app.ui.views
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [steroid.rn.core :as rn]
   [steroid.rn.navigation.core :as rnn]
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
   [app.ui.user.password :as password]
   [app.ui.user.register :as register]
   [app.ui.user.code :as user-code]
   [app.ui.setting.detail :as setting-detail]
   [app.ui.home.index :as home]
   [app.ui.home.question :as question]
   [app.ui.drawer.index :as drawer]
   [app.ui.nativebase :as nativebase]))


(when platform/android?
  (status-bar/set-bar-style "light-content")
  (status-bar/set-translucent true))


(defn root-stack []
  [safe-area/safe-area-provider
   [(rnn/create-navigation-container-reload                 ;; navigation container with shadow-cljs hot reload
     {:on-ready #(re-frame/dispatch [:initialise-app])}     ;; when navigation initialized and mounted initialize the app
     [nativebase/nativebase-provider {:config {:dependencies {"linear-gradient" linear-gradient}}}
      [drawer/drawer {:screenOptions
                      {
                       :drawerPosition :left
                       :drawerStyle {:width 300}
                       :drawerType :front
                       :headerShown false}
                      :drawerContent
                      (fn [props]
                        (reagent/as-element
                          [drawer/drawer-content-scroll-view {}
                           [ui/safe-area-consumer
                             [rn/text "hello Hi"]]]))}
       ;
       [{:name :main-drawer
         :component
         (fn []
           [stack/stack {}
            [
             {:name      :main
              :component home/anonymous
              :options {:title ""
                        :headerShown false}}
             {:name       :setting-detail
              :component  setting-detail/view
              :options    {:title ""}}
             {:name       :password
              :component  password/view
              :options    {:title ""}}
             {:name       :sign-in
              :component  login/view
              :options    {:title ""
                           :gestureEnabled false
                           :headerShown false}}
             {:name       :sign-up
              :component  register/view
              :options    {:title ""}}
             {:name       :user-in-code
              :component  user-code/view
              :options    {:title ""}}
             {:name       :question-detail
              :component  question/detail-view
              :options    {:title ""}}
             {:name       :home
              :component  home/tabs
              :options    {:title ""
                           :headerLeft (fn [] nil)
                           :header {:back false}
                           :headerShown false}}]])}]]])]])

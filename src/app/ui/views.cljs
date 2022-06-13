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
   [steroid.rn.components.ui :as rn-ui]
   [app.ui.components :as ui]
   [steroid.rn.navigation.stack :as stack]
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [app.text.index :as text]
   [app.font.base :as font]
   [app.handler.gesture :as gesture]

   ["react-native-smooth-blink-view" :default blinkview]
   ["react-native-svg" :as svg]
   ["react-native-linear-gradient" :default linear-gradient]
   ["@react-navigation/stack" :refer [createStackNavigator]]
   ["react-native-portalize" :refer [Host]]
   ["native-base" :refer [extendTheme]]

   [app.ui.home.views :as home]
   [app.ui.profile.views :as profile]
   [app.ui.modal.views :as modal]
   [app.ui.toast.views :as toast]
   [app.ui.drawer.index :as drawer]
   [app.ui.nativebase :as nativebase]
   [app.ui.webview :as webview]
   [app.ui.editor :as editor]
   [app.ui.article.index :as article]
   [app.ui.article.new :as arnew]
   [app.ui.question.index :as question]
   [app.ui.message.index :as message]
   [app.ui.basic.theme :as theme]
   [app.ui.search :as search]))


(when platform/android?
  (status-bar/set-bar-style "light-content")
  (status-bar/set-translucent true))


(def tab-icons
  {"home"         "md-home"
   "question"     "md-reader"
   "article"      "md-document"
   "chat"         "md-notifications"
   "profile"      "md-person"})

(defn screen-options [options]
  (let [{:keys [route]} (bean/->clj options)]
    {:tabBarIcon
     (fn [data]
       (let [{:keys [color]} (bean/->clj data)
             icon (get tab-icons (:name route))]
         (reagent/as-element
          [ui/ion-icons {:name icon :color color :size 30}])))}))

(defn home []
  (let [h (reagent/atom nil)]
    (fn []
      [rn/view {:style {:height "100%" :width "100%"}
                :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                              (reset! h height))}])))

(defn edit-view []
  [nativebase/center {:flex 1 :px 3 :safeArea true}
   [nativebase/text "edit-view"]])

(defn tabs []
  [bottom-tabs/bottom-tab
   {
    ; :screenOptions screen-options}
    :screenOptions
    (fn [options]
     (let [{:keys [route]} (bean/->clj options)
           icon (get tab-icons (:name route))]
       (bean/->js
                  (if (not= icon "md-document")
                    {:activeTintColor   "#5cb85c"
                     :inactiveTintColor :black
                     :showLabel         false
                     :tabBarLabel       (fn [] nil)
                     ; :headerShown       false
                     :modal             true
                     :tabBarIcon (fn [data]
                                   (let [{:keys [color]} (bean/->clj data)
                                         icon (get tab-icons (:name route))]
                                     (reagent/as-element
                                       (if (= icon "md-document")
                                         [rn/view {:style {:width 42 :height 42 ;:borderRadius "full" :variant "solid" :colorScheme "indigo"
                                                           :justifyContent "center" :alignSelf "center" :alignItems "center"
                                                           :position "absolute" :marginTop -20}}
                                          [ui/ion-icons {:name "add-circle" :color color :size 40}]]
                                         [ui/ion-icons {:name icon :color color :size 20}]))))}
                    {:tabBarButton
                     (fn []
                       (reagent/as-element
                         [rn/touchable-opacity {:onPress #(re-frame/dispatch [:navigate-to :model-new])
                                                :style {:justifyContent "space-around" :alignItems "center"}}
                          [rn/view {:style {:height 40 :width 40}}
                           [ui/ion-icons {:name "add-circle" :size 42
                                          :color "#4f46e5"}]]]))
                     :tabItemStyle {}
                     :modal true
                     :activeTintColor   "#5cb85c"
                     :inactiveTintColor :black
                     :showLabel         false
                     :tabBarLabel       (fn [] nil)}))))}
                     ; :headerShown       false}))))}


   [{:name      :home
     :component home/home}
    ; {:name      :book
    ;  ; :component webview/webview-editor
    ;  :component article/main}
    ; {:name      :book
    ;  :component edit-view}
    (merge
      question/question-list
      {:name :question})
    (merge
      ; article/article-list
      arnew/model-new
      {:name :article})
    (merge
      ; article/article-list
      message/model-base
      {:name :chat})
    {:name      :profile
     :component profile/profile}]])

(defn create-stack-navigator []
  (let [^js stack (createStackNavigator)]
    [(reagent/adapt-react-class (.-Navigator stack))
     (reagent/adapt-react-class (.-Group stack))
     (reagent/adapt-react-class (.-Screen stack))]))

(defn prepare-navigator [navigator screen]
  (fn [& params]
    (let [[props children] (if (map? (first params))
                             [(first params) (second params)]
                             [{} (first params)])]
      (into [navigator props]
            (mapv (fn [props]
                    [screen (update props :component reagent/reactify-component)])
                  children)))))

(defn root-stack []
  (let [[navigator group screen] (create-stack-navigator)]
    [safe-area/safe-area-provider
     [(rnn/create-navigation-container-reload                 ;; navigation container with shadow-cljs hot reload
       {:on-ready #(re-frame/dispatch [:initialise-app])     ;; when navigation initialized and mounted initialize the app
        :theme @theme/theme}
       [nativebase/nativebase-provider {:config {:dependencies {"linear-gradient" linear-gradient}}
                                        :theme (extendTheme (bean/->js {:useSystemColorMode false :initialColorMode "dark"}))}
        [gesture/gesture-root-view {:style {:flex 1}}
         [:> Host
          [navigator {:screenOptions {:headerShown false}}
           (into
             [group {}]
             (mapv (fn [props]
                     [screen (update props :component reagent/reactify-component)])
               [{:name      :main
                 :component tabs
                 :options {:title ""}}
                article/article-detail
                article/article-edit
                article/article-list
                question/question-list
                question/question-detail
                question/question-edit
                profile/profile-edit
                message/model-base
                message/model-list
                message/model-focus]))
           (into
             [group {:screenOptions {:presentation "modal"}}]
             (mapv (fn [props]
                     [screen (update props :component reagent/reactify-component)])
               [arnew/model-new
                search/search-base]))]]]])]]))

        ; [stack/stack {}
        ;  [{:name      :main
        ;    :component tabs
        ;    :options {:title ""}}
        ;   article/article-detail
        ;   article/article-edit
        ;   article/article-list
        ;   question/question-list
        ;   question/question-detail
        ;   question/question-edit
        ;   profile/profile-edit
        ;   message/model-base
        ;   message/model-list
        ;   message/model-focus
        ;   arnew/model-new]]])]]))
; (defn root-stack []
;   [safe-area/safe-area-provider
;    [nativebase/nativebase-provider
;     ; [nativebase/view]
;     [nativebase/box {:flex 1 :px 3 :safeArea true}
;     ;  [webview/webview-editor]]]])
;     ; [editor/view]]])
;     ; [editor/test-view]]])
;      [home/home]]]])

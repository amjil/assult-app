(ns app.ui.question.answer
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.editor :as editor]
    [app.ui.components :as ui]
    [app.ui.text :as text]
    [app.ui.keyboard.index :as keyboard]
    [app.ui.keyboard.candidates :as candidates]
    [app.ui.keyboard.bridge :as bridge]
    [app.handler.gesture :as gesture]

    [steroid.rn.core :as rn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native-vector-icons/Ionicons" :default Ionicons]))


; (def answer-detail
;   {:name       :question-detail
;    :component  detail-view
;    :options
;    {:title ""}})
;
; (def answer-edit
;   {:name       :question-edit
;    :component  edit-view
;    :options
;    {:title ""
;     :headerRight
;     (fn [tag id classname]
;       (reagent/as-element
;         [nbase/icon-button {:variant "ghost" :colorScheme "indigo"
;                             :icon (reagent/as-element [nbase/icon {:as Ionicons :name "ios-checkmark"}])
;                             :on-press (fn [e] (js/console.log "on press icon button")
;                                         (bridge/editor-content)
;                                         (re-frame/dispatch [:navigate-back]))}]))}})
;
; (def answer-list
;   {:name       :question-list
;    :component  list-view
;    :options
;    {:title ""}})

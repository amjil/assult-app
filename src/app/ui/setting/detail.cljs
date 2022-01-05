(ns app.ui.setting.detail
  (:require
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [steroid.rn.core :as rn]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [app.handler.gesture :as gesture]
   [app.ui.text.index :as text]
   [app.ui.keyboard.index :as keyboard]
   [app.ui.keyboard.candidates :as candidates]
   ["react-native-vector-icons/Ionicons" :default Ionicons]))

(defn call-back-fn [step]
  (fn []
    (condp = @step
      2 (re-frame/dispatch [:reset-to-home])

      :else (swap! step inc))))

(defn name-view [step]
  (let [atomic (reagent/atom {:focus false :text "" :flag false :height nil})
        params {:name "Input" :props {:variant "filled" :fontSize 18}}
        height (reagent/atom nil)]
    (fn []
      (let [rf-key :put-profile
            loading @(re-frame/subscribe [:loading rf-key])
            errors @(re-frame/subscribe [:errors rf-key])]
        [nbase/flex {:style {:height "100%"} :justifyContent "space-between" :safeArea true}
         [nbase/pressable {:flexDirection "row" :justifyContent "space-between"
                           :m 10
                           :flex 1
                           :on-press (fn []
                                       (swap! atomic assoc :focus false)
                                       (re-frame/dispatch [:set-candidates-list []]))
                           :on-layout #(let [h (j/get-in % [:nativeEvent :layout :height])]
                                         (swap! atomic assoc :height h))}
          [nbase/hstack {:space 2}
           [nbase/measured-text {:fontSize 18 :fontFamily "MongolianBaiZheng"} "ᠨᠡᠷ᠎ᠡ"]
           [:f> text/text-input atomic params]
           [nbase/measured-text {:fontSize 18 :fontFamily "MongolianBaiZheng" :height (:height @atomic)} "ᠦᠨᠡᠨ ᠨᠡᠷ᠎ᠡ ᠪᠤᠶᠤ ᠨᠠᠢᠵᠠ ᠨᠠᠷ ᠲᠠᠭᠠᠨ ᠠᠮᠠᠷᠬᠠᠨ ᠲᠠᠨᠢᠭᠳᠠᠬᠤ ᠨᠡᠷ᠎ᠡ ᠪᠠᠨ ᠲᠠᠭᠯᠠᠭᠠᠷᠠᠢ"]]
          [nbase/flex {:flexDirection "row" :justifyContent "flex-end"}
           [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                               :justifyContent "center" :alignSelf "center" :alignItems "center"
                               :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])
                               :on-press #(do
                                            (re-frame/dispatch
                                              [:put-user-profile
                                               {:realname @(re-frame/subscribe [:editor-text])}
                                               (fn [] (call-back-fn step))]))}]]]

         [candidates/views]
         [nbase/box {:style {:height 220}}
          [keyboard/keyboard]]]))))

(defn pass-view [step]
  (let [model (reagent/atom "")
        props {:fontSize 18 :fontFamily "MongolianBaiZheng"}]
    (fn []
      (let [rf-key :put-profile
            loading @(re-frame/subscribe [:loading rf-key])
            errors @(re-frame/subscribe [:errors rf-key])]
        [nbase/box {:h "100%" :safeArea true}
         [nbase/flex {:mt 0 :mx 10 :h "80%" :justifyContent "space-between"}
          [nbase/vstack {:space 4}
           [nbase/hstack {}
            [nbase/measured-text props "ᠨᠢᠭᠤᠴᠠ"]
            [nbase/measured-text props "ᠦᠭᠡ"]]
           [nbase/input {:type "password"
                         :placeholder "Password"
                         :on-change-text #(reset! model %)}]
           [nbase/flex {:flexDirection "row" :justifyContent "flex-end"}
            [nbase/icon-button {:w 20 :h 20 :borderRadius "full" :variant "solid" :colorScheme "indigo"
                                :justifyContent "center" :alignSelf "center" :alignItems "center"
                                :icon (reagent/as-element [nbase/icon {:as Ionicons :name "arrow-forward"}])
                                :on-press #(do
                                             (re-frame/dispatch
                                               [:put-user-profile
                                                {:password @model}
                                                (fn [] (swap! step inc))]))}]]]]]))))


(defn view []
  (let [step (reagent/atom 1)]
    (fn []
      (condp = @step
        1 [name-view step]
        2 [pass-view step]))))

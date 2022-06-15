(ns app.ui.message.index
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.editor :as editor]
    [app.handler.gesture :as gesture]
    [app.ui.components :as ui]
    [app.ui.text :as text]
    [app.ui.basic.theme :as theme]
    [app.ui.keyboard.index :as keyboard]
    [app.ui.keyboard.candidates :as candidates]
    [app.ui.keyboard.bridge :as bridge]

    [steroid.rn.core :as rn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [app.persist.sqlite :as sqlite]

    ["react-native" :refer [Dimensions]]
    ["react-native-vector-icons/Ionicons" :default Ionicons]))

(def conversations (reagent/atom [{:name "Mike" :message "can be customized as needed with our utility API."}
                                  {:name "Tom" :message "utilities. Classes are applied to the element itself or sometimes the parent element. These classes "}
                                  {:name "Billy" :message "This is a long paragraph written to show how the line-height of an element is affected by our "}
                                  {:name "Anna" :message "Classes are applied to the element itself or sometimes the parent element."}
                                  {:name "Silly" :message "These classes can be customized as needed with our utility API."}]))

(def messages (reagent/atom {"Mike" [
                                     {:me false :message "I don't understand anything"}
                                     {:me true :message "utilities. Classes are applied to the element itself or sometimes the parent element. These classes "}
                                     {:me true :message "No, it isn't ."}
                                     {:me false :message "Is this your's phone?"}
                                     {:me true :message "I'm from Hohhot."}
                                     {:me false :message "Where are you from ?"}
                                     {:me true :message "My name is HHH."}
                                     {:me true :message "Hi Mike, Nice to meet you."}
                                     {:me false :message "Hello, My name is Mike."}]}))
(def conversation-name (atom "Mike"))
(def focus-message (atom ""))

(defn edit-view []
  [ui/safe-area-consumer
   [nbase/flex {:flex 1 :justifyContent "space-between"}
    [editor/editor-view
     ;opts
     {:type :text}
     ;content-fn
     (fn []
       "")
     ;update-fn
     (fn [x]
       identity)]
    [candidates/views]
    [nbase/box {:height 220}
     [keyboard/keyboard]]]])

(defn base-view []
  (let [h (reagent/atom nil)]
    (fn []
     [nbase/zstack {:flex 1
                    :bg (theme/color "white" "dark.100")
                    :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                  (reset! h height))}
      (if (nil? @h)
        [nbase/hstack {:style {:height 674}}
         [nbase/box {:m 1 :p 4
                     :borderRightWidth "0.5"
                     :borderColor (theme/color "gray.300" "gray.500")
                     :bg (theme/color "white" "dark.100")}
          [nbase/skeleton {:h "100%" :w 24}]]]
        [nbase/flat-list
         {:keyExtractor    (fn [_ index] (str "conversation-view-" index))
          :data      @conversations
          :renderItem (fn [x]
                        (let [{:keys [item index separators]} (j/lookup x)]
                          (reagent/as-element
                            [nbase/pressable {:style {:height "100%"}; :width 28}}
                                              :on-press (fn [e] (re-frame/dispatch [:navigate-to :message-list])
                                                          (reset! conversation-name (j/get item :name)))}
                             [nbase/vstack
                              [nbase/box {:bg (theme/color "coolGray.300" "coolGray.500")
                                          :borderRadius 10
                                          :p 8
                                          :alignSelf "flex-start"
                                          :mb 4}]
                              [nbase/hstack
                               ; [nbase/box {:justifyContent "space-between"}
                               [text/measured-text {:fontSize 18 } (j/get item :name)]
                                ; [text/measured-text {:fontSize 18 } (j/get item :name)]]
                               [text/single-line-text {:fontSize 18 :color (theme/color "#d4d4d4" "#737373") :width (- @h 100)} (j/get item :message)]]]])))

          :ItemSeparatorComponent
          (fn [] (reagent/as-element [nbase/divider {:bg "coolGray.200" :thickness "1" :orientation "vertical" :mx "2"}]))
          :p 3
          :initialNumToRender 7
          :showsHorizontalScrollIndicator false
          :horizontal true
          :flex 1
          :h "100%"}])])))

(defn list-view []
  (let [h (reagent/atom nil)
        list-ref (reagent/atom nil)
        keyboard-type (reagent/atom false)
        editor-input (reagent/atom false)]
    (fn []
      [ui/safe-area-consumer
       [nbase/box {:flex 1 :bg (theme/color "white" "dark.100")}
        [nbase/zstack {:flex 1
                       :bg (theme/color "white" "dark.100")
                       :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                     (reset! h height))}
         (if (nil? @h)
           [nbase/hstack; {:style {:height 674}}
            [nbase/box {:m 1 :p 4
                        :borderRightWidth "0.5"
                        :borderColor (theme/color "gray.300" "gray.500")
                        :bg (theme/color "white" "dark.100")}
             [nbase/skeleton {:h "100%" :w 24}]]]
           [gesture/tap-gesture-handler
            {
              :onHandlerStateChange #(let [state (j/get-in % [:nativeEvent :state])]
                                       (when (gesture/tap-state-end (j/get % :nativeEvent))
                                         (reset! editor-input false)
                                         (candidates/candidates-clear)))}
            [nbase/flat-list
             {:keyExtractor    (fn [_ index] (str "message-view-" index))
              :data     (get @messages @conversation-name)
              :renderItem (fn [x]
                            (let [{:keys [item index separators]} (j/lookup x)]
                              (reagent/as-element
                                [gesture/tap-gesture-handler
                                 {
                                   :numberOfTaps 2
                                   :onHandlerStateChange #(let [state (j/get-in % [:nativeEvent :state])]
                                                            (when (gesture/tap-state-end (j/get % :nativeEvent))
                                                              (reset! focus-message (j/get item :message))
                                                              (re-frame/dispatch [:navigate-to :message-focus])))}

                                 (if (true? (j/get item :me))
                                   [nbase/vstack {:justifyContent "flex-end"
                                                  :style {:height (- @h 0)
                                                          :paddingHorizontal 8}}
                                    [nbase/box {:p 2 :bg (theme/color "darkBlue.100" "darkBlue.300")
                                                :borderRightRadius "20"
                                                :borderTopLeftRadius "20"}
                                     [text/measured-text {:fontSize 18 :width (- @h 0 64 60)} (j/get item :message)]]
                                    [nbase/box {:bg (theme/color "coolGray.300" "coolGray.500")
                                                :borderRadius 10
                                                :p 6
                                                :mt 4
                                                :alignSelf "flex-start"}]]
                                   [nbase/vstack {:justifyContent "flex-start"
                                                  :style {:height (- @h 0)}}
                                    [nbase/box {:bg (theme/color "coolGray.300" "coolGray.500")
                                                :borderRadius 10
                                                :p 6
                                                :alignSelf "flex-start"
                                                :mb 4}]
                                    [nbase/box {:p 2 :bg (theme/color "darkBlue.100" "darkBlue.300")
                                                :borderRightRadius "20"
                                                :borderBottomLeftRadius "20"}
                                     [text/measured-text {:fontSize 18 :width (- @h 0 64 60) :color (theme/color "black" "#71717a")} (j/get item :message)]]])])))

              :showsHorizontalScrollIndicator false

              ; :pagingEnabled true
              :nestedScrollEnabled true
              ; :snapToEnd true
              ; :initialScrollIndex 0
              :inverted true
              ;:style {:flexDirection "row"}

              :onEndReached (fn [e] (js/console.log "onEndReached >>>>> "))
              ; :onEndReachedThreshold 2

              :horizontal true
              :flex 1}]])
         (when (true? @editor-input)
           [nbase/box {:flex 1 :bg (theme/color "warmGray.300" "warmGray.500")
                       :p 1}
            [nbase/box {:style {:height (- @h 4)} :bg (theme/color "white" "dark.100")
                        :borderRadius 4
                        :minWidth 10
                        :maxWidth 24}
             [editor/editor-view
               {:type :text}
               ;content-fn
               (fn []
                 ; (js/console.log "content -fn >......." (:content @model))
                 ; (get @model @active-key))
                 "")
               ;update-fn
               (fn [x]
                 (swap! messages
                    assoc @conversation-name (concat [{:me true :message (:text x)}] (get @messages @conversation-name)))
                 (reset! conversations (map #(if (= (:name %) @conversation-name)
                                               (assoc % :message (:text x))
                                               %)
                                            @conversations)))]]])
         (when (true? @editor-input)
           [candidates/views {:bottom 20}])]

        (if (true? @editor-input)
          [nbase/box {:height 220 :mt 1}
           [keyboard/keyboard {:type "single-line"
                               :on-press (fn []
                                           (bridge/editor-content)
                                           (reset! editor-input false)
                                           (candidates/candidates-clear))}]]
          [nbase/box {:bg (theme/color "warmGray.100" "warmGray.300")
                      :h 12
                      :mt 1
                      :p 2
                      :flexDirection "row"
                      :justifyContent "space-around"}
           [rn/touchable-highlight {:style {:height 32 :width 32 :borderWidth 0.2 :borderRadius 32 :borderColor "#57534e"
                                            :alignItems "center" :justifyContent "center"
                                            :flex 1}
                                    :underlayColor "#cccccc"
                                    :on-press (fn [e]
                                                (reset! editor-input true))}
            [ui/ion-icons {:name "ios-keypad-sharp" :color "#78716c" :size 18}]]
           [rn/touchable-highlight {:style {:height 32 :borderWidth 0.2 :borderRadius 32 :borderColor "#57534e"
                                            :marginHorizontal 8
                                            :alignItems "center" :justifyContent "center"
                                            :flex 5}
                                    :underlayColor "#cccccc"
                                    :on-press #(js/console.log "on mic >>>>")}
            [ui/ion-icons {:name "ios-mic" :color "#78716c" :size 18}]]
           [rn/touchable-highlight {:style {:height 32 :width 32 :borderWidth 0.2 :borderRadius 32 :borderColor "#57534e"
                                            :alignItems "center" :justifyContent "center"
                                            :flex 1}
                                    :underlayColor "#cccccc"
                                    :on-press #(js/console.log "on add >>>>")}
            [ui/ion-icons {:name "ios-add" :color "#78716c" :size 18}]]])]])))

(defn focus-view []
  (let [screen-width (.-width (.get Dimensions "window"))
        screen-height (.-height (.get Dimensions "window"))]
    [nbase/center {:style {:width screen-width :height screen-height}}
     [gesture/tap-gesture-handler
      {
        :onHandlerStateChange #(let [state (j/get-in % [:nativeEvent :state])]
                                 (when (gesture/tap-state-end (j/get % :nativeEvent))
                                   (js/console.log "focus view on tap ...")
                                   (re-frame/dispatch [:navigate-back])))}
      [nbase/box {:mt 4 :p 6}
       [text/measured-text {:fontSize 28 :width (- screen-height 120) :color (theme/color "black" "#71717a")} @focus-message]]]]))

(def model-list
  {:name       :message-list
   :component  list-view
   :options
   {:title ""
    :headerShown true}})

(def model-base
  {:name       :message-base
   :component  base-view
   :options
   {:title ""}})

(def model-focus
  {:name :message-focus
   :component focus-view
   ; :options {:title ""}
   :options
   {
    :headerShown false

    :presentation "card"}})

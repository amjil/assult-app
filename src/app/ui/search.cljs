(ns app.ui.search
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.editor :as editor]
    [app.ui.components :as ui]
    [app.ui.text :as text]
    [app.ui.editor :refer [weblen]]
    [app.ui.keyboard.index :as keyboard]
    [app.ui.keyboard.candidates :as candidates]
    [app.ui.keyboard.bridge :as bridge]
    [app.ui.basic.theme :as theme]
    [app.text.message :refer [labels]]

    [steroid.rn.core :as rn]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]

    ["react-native-modal" :default rnmodal]
    ["react-native-vector-icons/Ionicons" :default Ionicons]))

(def search-recents ["Tesla" "Honda" "Toyota" "Vokswagon"])

(def search-hightrate ["What is this? " "Something more quicker then sound?" "Ten thousend?" "Biggest Tree in the world?"])

(defn recomm-view [h flag data]
  [nbase/box {:height (str (- @h 0) "px") :borderRightRadius "10"
              :flexDirection "row"}
   [nbase/box {:p 0.5 :flex 0.2 :bg (theme/color "white" "dark.100")}]
   [nbase/box {:flex 1 :bg (theme/color "gray.100" "dark.300")}
    [nbase/flat-list
     {:keyExtractor    (fn [_ index] (str "question-view-" index))
      :data      data

      :showsHorizontalScrollIndicator false
      :horizontal true
      :style {:flex 1}

      :renderItem (fn [x]
                    (let [{:keys [item index separators]} (j/lookup x)]
                      (reagent/as-element
                       [nbase/box {:flex 1 :flexDirection "row"}
                        [rn/touchable-opacity {:on-press #(js/console.log "recommandations on press >>>")}
                         [nbase/box {:m 5 :flex 1 :flexDirection "row"}
                          [text/measured-text {:fontSize 18 :width (- @h 20) :color (theme/color "black" "#a1a1aa")} (j/get item :question_content)]
                          ]]
                        [nbase/divider {:orientation "vertical" :bg (theme/color "gray" "dark.500")}]])))}]]])

(defn base-view []
  (let [h (reagent/atom nil)
        check-position (reagent/atom 0)
        recomm-flag (reagent/atom 0)
        onchange-fn (fn [x] 
                      (re-frame/dispatch [:search-question-you-type {:search x}]))
        _ (bridge/editor-set-text "")
        _ (bridge/editor-onchange-callback onchange-fn)]
    (fn []
      (let [recomm-data @(re-frame/subscribe [:search-you-type-result])]
        [ui/safe-area-consumer
         [nbase/box {:flex 1 :bg (theme/color "white" "dark.100")}
          [nbase/zstack {:flex 1
                         :bg (theme/color "white" "dark.100")
                         :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                       (reset! h height))}
           (if-not (nil? @h)
             [nbase/box {:flex 1 :flexDirection "row"}
              [nbase/box {:bg (theme/color "blue.100" "blue.300") :p 1}
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
                 (fn [x] (js/console.log "editir update-fn"))]]]
                   ; (swap! messages
                   ; assoc @conversation-name (concat [{:me true :message (:text x)}] (get @messages @conversation-name))])]]])
              
              [nbase/hstack {:style {:height (- @h 4)} :bg (theme/color "white" "dark.100") :ml 3}
               [:> rnmodal {:isVisible (not-empty recomm-data)
                            :coverScreen false
                            :backdropColor (theme/color "lightGray" "#27272a")
                            :scrollHorizontal true
                            :style {:left -20}}
                [recomm-view h recomm-flag recomm-data]]
               [nbase/vstack {:p 1 :alignItems "center" :style {:height (/ (- @h 4) 2)}}
                [rn/touchable-opacity {:on-press #(reset! check-position 0)}
                 [nbase/box {:p 1 :borderWidth "1" :borderRadius "md" :borderColor "blue.300" :mb 3}
                  [text/measured-text {:fontSize 18 :color (if (= 0 @check-position) (theme/color "#3b82f6" "#3b82f6") (theme/color "#d4d4d8" "#d4d4d8")) :width (/ (- @h 8) 2)} (get-in labels [:search :search-to-find])]]]
                [rn/touchable-opacity {:on-press #(reset! check-position 1)}
                 [nbase/box {:p 1 :borderWidth "1" :borderRadius "md" :borderColor (if (= 1 @check-position) "blue.300" "gray.300")}
                  [text/measured-text {:fontSize 18 :color (if (= 1 @check-position) (theme/color "#3b82f6" "#3b82f6") (theme/color "#d4d4d8" "#d4d4d8")) :width (/ (- @h 8) 2)} (get-in labels [:search :recent-search])]]]]
               (into
                [nbase/hstack {:m 1 :ml 3}]
                (for [item (if (= 0 @check-position)
                             search-recents
                             search-hightrate)]
                  [rn/touchable-opacity {:on-press #(js/console.log "xxxxx search texts")}
                   [nbase/box {:p 2}
                    [text/measured-text {:fontSize 16 :color "#2563eb" :width (/ (- @h 20) 2)} item]]]))]])

           [candidates/views {:bottom 20}]]
          [nbase/box {:height 220 :mt 1}
           [keyboard/keyboard {:type "single-line"
                               :on-change true
                               :on-press (fn []
                                           (bridge/editor-content)
                                           (candidates/candidates-clear))}]]]]))))


(def search-base
  {:name       :search-base
   :component  base-view
   :options
   {:title ""
    :headerShown true}})

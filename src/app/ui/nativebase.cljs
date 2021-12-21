(ns app.ui.nativebase
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [steroid.rn.core :as rn]
   [steroid.rn.navigation.core :as rnn]
   [promesa.core :as p]
   [promesa.exec :as exec]
   ["react-native-measure-text-chars" :as rntext]
   ["native-base" :refer [NativeBaseProvider
                          Center
                          Container
                          Box
                          Modal Modal.Content Modal.CloseButton Modal.Header Modal.Body Modal.Footer
                          Heading

                          Alert Alert.Icon

                          Text
                          Button Button.Group
                          Input
                          Checkbox
                          Link
                          CloseIcon
                          IconButton
                          Icon

                          Badge
                          Pressable
                          CheckIcon
                          Select Select.Item
                          Actionsheet Actionsheet.Content Actionsheet.Item

                          Spacer
                          Divider
                          HStack
                          VStack
                          ZStack
                          Flex

                          FlatList
                          ScrollView

                          Collapse
                          Spinner

                          useStyledSystemPropsResolver
                          usePropsResolution
                          useThemeProps
                          useToast
                          useDisclose]]
   ["react" :as react]))


(def nativebase-provider (reagent/adapt-react-class NativeBaseProvider))

(def box (reagent/adapt-react-class Box))
(def badge (reagent/adapt-react-class Badge))

(def container (reagent/adapt-react-class Container))

(def heading (reagent/adapt-react-class Heading))

(def text (reagent/adapt-react-class Text))
(def input (reagent/adapt-react-class Input))
(def checkbox (reagent/adapt-react-class Checkbox))

(def button (reagent/adapt-react-class Button))
(def button-group (reagent/adapt-react-class Button.Group))

(def link (reagent/adapt-react-class Link))
(def icon (reagent/adapt-react-class Icon))

(def icon-button (reagent/adapt-react-class IconButton))
(def close-icon (reagent/adapt-react-class CloseIcon))

(def select (reagent/adapt-react-class Select))
(def select-item (reagent/adapt-react-class Select.Item))
(def alert (reagent/adapt-react-class Alert))
(def alert-icon (reagent/adapt-react-class Alert.Icon))
(def collapse (reagent/adapt-react-class Collapse))
(def spinner (reagent/adapt-react-class Spinner))
; Modal Modal.Content Modal.CloseButton Modal.Header Modal.Body Modal.Footer))
(def modal (reagent/adapt-react-class Modal))
(def modal-content (reagent/adapt-react-class Modal.Content))
(def modal-close-button (reagent/adapt-react-class Modal.CloseButton))
(def modal-header (reagent/adapt-react-class Modal.Header))
(def modal-body (reagent/adapt-react-class Modal.Body))
(def modal-footer (reagent/adapt-react-class Modal.Footer))

; Actionsheet Actionsheet.Content Actionsheet.Item
(def actionsheet (reagent/adapt-react-class Actionsheet))
(def actionsheet-content (reagent/adapt-react-class Actionsheet.Content))
(def actionsheet-item (reagent/adapt-react-class Actionsheet.Item))
(def checkicon (reagent/adapt-react-class CheckIcon))

(def spacer (reagent/adapt-react-class Spacer))

(def pressable (reagent/adapt-react-class Pressable))

(def flex (reagent/adapt-react-class Flex))

(def hstack (reagent/adapt-react-class HStack))
(def vstack (reagent/adapt-react-class VStack))
(def zstack (reagent/adapt-react-class ZStack))
(def divider (reagent/adapt-react-class Divider))
(def scroll-view (reagent/adapt-react-class ScrollView))
(def flat-list (reagent/adapt-react-class FlatList))

(def center (reagent/adapt-react-class Center))


(defn rotated-text [props width height t]
  (let [offset (js/Math.abs (- (/ height 2) (/ width 2)))]
    [text (merge props {:style {:width height :height width
                                :transform [{:rotate "90deg"}
                                            {:translateX offset}
                                            {:translateY offset}]}})
      t]))

(defn line-height [font-size]
  (* font-size
    (if (> font-size 20) 1.5 1)))

(defn measured-text
  ([props t]
   (let [width (:height props)
         info (rntext/measure (bean/->js (merge (assoc props :text t) (if width {:width width}))))]
     (measured-text props t (bean/->clj info))))
  ([props t info]
   (let [height (or (:height props) (:width info))
         width (/ (:height info) (:lineCount info))
         offset (- (/ height 2) (/ width 2))]
     (cond
       (nil? info)
       [text "empty ...."]

       (= 1 (:lineCount info))
       [box {:style {:width (:height info)
                     :height height}}
        [rotated-text props width height t]]

       :else
       [box {:style {:width (:height info)
                     :height height}}
        [flat-list
         {:horizontal true
          :keyExtractor    (fn [_ index] (str "text-" index))
          :data (map (fn [x] (subs t (:start x) (:end x))) (:lineInfo info))
          :renderItem
          (fn [x]
            (let [{:keys [item index separators]} (j/lookup x)]
              (reagent/as-element
                [box {:width width :height height}
                 [rotated-text props width height item]])))}]]))))


(defn theme-text-props [name props]
  (let [theme-props (bean/->js (useThemeProps name (bean/->js props)))
        [text-props _] (useStyledSystemPropsResolver (bean/->js (j/get theme-props :_text)))]
    text-props))

(defn theme-props [name props]
  (let [theme-props (bean/->js (useThemeProps name (bean/->js props)))
        [text-props _] (useStyledSystemPropsResolver (bean/->js theme-props))]
    (bean/->clj text-props)))


(defn styled-text-view [props t]
  (let [[text-props _] (useStyledSystemPropsResolver (bean/->js props))]
    [measured-text (bean/->clj text-props) t]))

(defn view []
  [center {:flex 1 :py 3 :safeArea true}])

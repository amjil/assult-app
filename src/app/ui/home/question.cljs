(ns app.ui.home.question
  (:require
   [applied-science.js-interop :as j]
   [app.ui.nativebase :as nbase]
   [app.ui.components :as ui]
   [app.handler.navigation :as navigation]
   [app.ui.components :as ui]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [cljs-bean.core :as bean]
   [steroid.rn.core :as rn]
   [steroid.rn.navigation.bottom-tabs :as bottom-tabs]
   ["native-base" :refer [ArrowForwardIcon]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]
   ["react-native-vector-icons/MaterialCommunityIcons" :default MaterialCommunityIcons]))

(defn detail-view []
  (let [h (reagent/atom nil)
        font {:fontFamily "MongolianBaiZheng"}]
    (fn []
      (let [model @(re-frame/subscribe [:question])]
        [nbase/box {:safeArea true
                    :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                                  (reset! h height))}
         (if @h
           [nbase/measured-text (merge font {:height @h}) (:question_content model)])]))))

(ns app.handler.candidates.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :candidates-list
 (fn [db _]
   (get-in db [:candidates :list])))

(re-frame/reg-sub
 :candidates-index
 (fn [db _]
   (get-in db [:candidates :index])))

(re-frame/reg-sub
 :editor-selection-xy
 (fn [db _]
   (let [[x y] (get-in db [:editor :selection-xy])]
     [x y])))

(re-frame/reg-sub
 :editor-cursor
 (fn [db _]
   (get-in db [:editor :cursor])))

(re-frame/reg-sub
 :editor-info
 (fn [db _]
   (get-in db [:editor :text-info])))



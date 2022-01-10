(ns app.handler.keyboard.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :keyboard-shift
 (fn [db _]
   (get-in db [:keyboard :shift])))

(re-frame/reg-sub
 :keyboard-shift-num
 (fn [db _]
   (get-in db [:keyboard :shift-num])))

(re-frame/reg-sub
 :keyboard-alter-num
 (fn [db _]
   (get-in db [:keyboard :alter-num])))

(re-frame/reg-sub
 :keyboard-alter
 (fn [db _]
   (get-in db [:keyboard :alter])))

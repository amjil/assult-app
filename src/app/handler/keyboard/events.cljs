(ns app.handler.keyboard.events
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as str]))

;;
;; use candidate index string
;; (rf/reg-event-fx
;;   :keyboard-key-press
;;  (fn [{db}])

;;; on delete press
(re-frame/reg-event-fx
 :keyboard-delete
 (fn [{db :db} [_ _]]
   (let [old-index (get-in db [:candidates :index])
         new-index (str/join "" (drop-last old-index))]
     (cond
       (or (empty? old-index) (= 1 (count old-index)))
       (if (empty? (get-in db [:candidates :list]))
         {:db          (assoc-in db [:candidates :index] "")
          :dispatch    [:set-candidates-list []]
          :fx-text-change (merge
                           (select-keys (:editor db)
                                        [:text :cursor :text-props :line-height])
                           {:type :delete})}
         {:db          (assoc-in db [:candidates :index] "")
          :dispatch    [:set-candidates-list []]})

       :else
       {:db               (assoc-in db [:candidates :index] new-index)
        :candidates-query new-index}))))

(re-frame/reg-event-fx
 :keyboard-add-char
 (fn [{db :db} [_ value]]
   (js/console.log "aaaaa>>> add char")
   {:db (-> db
            (assoc-in [:candidates :list] [])
            (assoc-in [:candidates :index] ""))
    :dispatch [:text-change {:type :add-text :text-added value}]}))

(re-frame/reg-event-fx
  :keyboard-shift
  (fn [{db :db} [_ _]]
    {:db (assoc-in db [:keyboard :shift] (not (get-in db [:keyboard :shift])))}))

(re-frame/reg-event-fx
  :keyboard-shift-num
  (fn [{db :db} [_ _]]
    {:db (assoc-in db [:keyboard :shift-num] (not (get-in db [:keyboard :shift-num])))}))

(re-frame/reg-event-fx
  :keyboard-alter-num
  (fn [{db :db} [_ _]]
    {:db (assoc-in db [:keyboard :alter-num] (not (get-in db [:keyboard :alter-num])))}))

(re-frame/reg-event-fx
  :keyboard-alter
  (fn [{db :db} [_ _]]
    {:db (-> db
           (assoc-in [:keyboard :alter] (not (get-in db [:keyboard :alter])))
           (assoc-in [:keyboard :alter-num] false)
           (assoc-in [:keyboard :shift] false)
           (assoc-in [:keyboard :shift-num] false))}))

(comment
 (on-press "a"))

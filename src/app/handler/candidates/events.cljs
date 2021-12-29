(ns app.handler.candidates.events
  (:require
   [re-frame.core :as re-frame]
   [app.persist.sqlite :as sqlite]
   [clojure.string :as str]
   [cljs-bean.core :as bean]))

(re-frame/reg-fx
 :candidates-query
 (fn [value]
   (sqlite/candidates
    value
    #(re-frame/dispatch [:set-candidates-list %]))))

(re-frame/reg-fx
 :candidates-query-next
 (fn [value]
   (js/console.log "candidates next")
   (sqlite/next-words
    value
    #(re-frame/dispatch [:set-candidates-list %]))))

(re-frame/reg-event-fx
 :set-candidates-list
 (fn [{db :db} [_ value]]
   {:db (assoc-in db [:candidates :list] value)}))

(re-frame/reg-event-fx
 :set-candidates-index
 (fn [{db :db} [_ value]]
   {:db (assoc-in db [:candidates :index] value)
    :dispatch [:set-candidates-list []]}))

(re-frame/reg-event-fx
 :candidates-index-concat
 (fn [{db :db} [_ m]]
   (let [new-index (str (get-in db [:candidates :index]) m)]
     (js/console.log " candidates index concat " new-index)
     {:db      (assoc-in db [:candidates :index] new-index)
      :candidates-query new-index})))

;;; on delete press
(re-frame/reg-event-fx
 :keyboard-delete
 (fn [{db :db} [_ _]]
   (let [old-index (get-in db [:candidates :index])
         new-index (str/join "" (drop-last old-index))]
     (cond
       (or (empty? old-index) (= 1 (count old-index)))
       {:db          (assoc-in db [:candidates :index] "")
        :dispatch    [:set-candidates-list []]
        :fx-text-change (merge
                         (select-keys (:editor db)
                                      [:text :cursor :text-props :line-height])
                         {:type :delete})}

       :else
       {:db               (assoc-in db [:candidates :index] new-index)
        :candidates-query new-index}))))

;; candidate select
(re-frame/reg-event-fx
 :candidate-select
 (fn [{db :db} [_ value]]
   (let [old-text (get-in db [:editor :text])
         word (:char_word value)
         added-text (cond
                      (empty? old-text)
                      word

                      :else
                      (cond
                        (str/starts-with? word " ")
                        word

                        :else
                        (str " " (:char_word value))))]
     {:db (-> db
              (assoc-in [:candidates :index] "")
              (assoc-in [:candidates :list] []))
      :dispatch [:text-change {:type :add-text :text-added added-text}]
      :candidates-query-next value})))

;; editor events
(re-frame/reg-event-fx
 :set-editor-cursor
 (fn [{db :db} [_ value]]
   {:db (assoc-in db [:editor :cursor] value)}))

(re-frame/reg-event-fx
 :set-editor-selection-xy
 (fn [{db :db} [_ [x y]]]
   {:db (assoc-in db [:editor :selection-xy] [x y])}))
;;
(re-frame/reg-event-fx
 :set-editor-text-props
 (fn [{db :db} [_ props]]
   {:db (assoc-in db [:editor :text-porps] props)}))

(re-frame/reg-event-fx
 :set-editor-info
 (fn [{db :db} [_ value]]
   {:db (merge-with into db {:editor value})}))


(comment
 (merge-with into {:editor {:cursor 1}} {:editor {:cursor 2 :text "ab"}})
 (str/join "" (drop-last "hello"))
 (re-frame/dispatch [:candidates-index-concat "gsgn"])
 (re-frame/dispatch [:set-candidates-index ""])
 (re-frame/dispatch [:set-candidates-list []])
 (re-frame/dispatch [:candidates-query 2])
 (re-frame/subscribe [:candidates-index])
 (re-frame/subscribe [:candidates-list])
 (def  a
 @(re-frame/subscribe [:candidates-list]))
 a
 (re-frame/dispatch [:candidate-select (second a)]))

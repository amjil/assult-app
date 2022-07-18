(ns app.handler.question.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :as ajax]
   [cljs-bean.core :as bean]
   [app.api :as api]
   [app.db :as db]))



(re-frame/reg-event-fx
 :create-question
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :create-question] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "questions")
                 :headers                (api/auth-header db)
                 :params                 params
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:create-question-success]
                 :on-failure             [:api-request-error :create-question]}}))

(re-frame/reg-event-fx
 :create-question-success
 (fn [{db :db} [_ body]]
   (let [{data :data} body]
     (re-frame/dispatch [:navigate-back])
     {:db (-> db
              (assoc-in [:loading :create-question] false))})))

;; -----------------------------------------------------
(re-frame/reg-event-fx
 :delete-question
 (fn [{:keys [db]} [_ id]]
   {:db    (assoc-in db [:loading :delete-quesiton] true)
    :http-xhrio {:method                 :delete
                 :uri                    (api/endpoint "questions" (str id))
                 :headers                (api/auth-header db)
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:delete-question-success]
                 :on-failure             [:api-request-error :delete-question]}}))

(re-frame/reg-event-fx
 :delete-question-success
 (fn [{db :db} [_ body]]
   {:db (-> db
            (assoc-in [:loading :delete-question] false))
    :dispatch-n [[:get-questions]]}))
;; -----------------------------------------------------
(re-frame/reg-event-fx
 :get-questions
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :get-questions] true)
    :http-xhrio {:method                 :get
                 :uri                    (api/endpoint "questions")
                 :headers                (api/auth-header db)
                 :params                 params
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:get-questions-success]
                 :on-failure             [:api-request-error :get-questions]}}))

(re-frame/reg-event-fx
 :get-questions-success
 (fn [{db :db} [_ body]]
   (let [{data :data} body]
     {:db (-> db
              (assoc :questions data)
              (assoc-in [:loading :get-questions] false))})))

(re-frame/reg-event-fx
 :set-question
 (fn [{db :db} [_ params]]
   {:db             (assoc db :question params)}))

;; -----------------------------------------------------
(re-frame/reg-event-fx
 :question-focus
 (fn [{:keys [db]} [_ id]]
   {:db    (assoc-in db [:loading :question-focus] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "questions" (str id) "focus")
                 :headers                (api/auth-header db)
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:question-focus-success]
                 :on-failure             [:api-request-error :question-focus]}}))

(re-frame/reg-event-fx
 :question-focus-success
 (fn [{db :db} [_ body]]
   (let [{data :data} body
         focus (get-in db [:question :user_focus])
         focus-count (get-in db [:question :focus_count])
         focus-count (if (= 0 focus) (inc focus-count) (dec focus-count))
         id (get-in db [:question :id])
         questions (get db :questions)
         questions (map #(if (= id (:id %))
                           (assoc % :focus_count focus-count)
                           %)
                        questions)]
     {:db (-> db
              (assoc-in [:question :user_focus] (if (= 1 focus) 0 1))
              (assoc-in [:question :focus_count] focus-count)
              (assoc :questions questions)
              (assoc-in [:loading :question-focus] false))})))

(comment
  (re-frame/dispatch [:get-questions {}])
  (re-frame/subscribe [:question-list])


  (re-frame/dispatch [:my-question 9])
  (re-frame/dispatch [:question-my]))

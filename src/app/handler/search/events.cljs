(ns app.handler.search.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :as ajax]
   [cljs-bean.core :as bean]
   [app.api :as api]
   [app.db :as db]))

(re-frame/reg-event-fx
 :search-question-answer
 (fn [{:keys [db]} [_ params]]
   (let [ek :search-question-answer]
     {:db    (assoc-in db [:loading ek] true)
      :http-xhrio {:method          :post
                   :timeout         3000
                   :uri             (api/endpoint "search" "question-answers")
                   :headers         (api/auth-header db)
                   :params          params
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:put-search-question-answer-success ek]
                   :on-failure      [:api-request-error ek]}})))

(re-frame/reg-event-fx
 :put-search-question-answer-success
 (fn [{db :db} [_ ek body]]
   (let [{code :code msg :msg {total :total result :result} :data} body]
     {:db    (-> db
                 (assoc-in [:loading ek] false)
                 (assoc :search-count total)
                 (assoc :search result))})))

(re-frame/reg-event-fx
 :search-question
 (fn [{:keys [db]} [_ params]]
   (let [ek :search-question]
     {:db    (assoc-in db [:loading ek] true)
      :http-xhrio {:method          :post
                   :timeout         3000
                   :uri             (api/endpoint "search" "questions")
                   :headers         (api/auth-header db)
                   :params          params
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:put-search-question-success ek]
                   :on-failure      [:api-request-error ek]}})))

(re-frame/reg-event-fx
 :put-search-question-success
 (fn [{db :db} [_ ek body]]
   (let [{code :code msg :msg {total :total result :result} :data} body]
     {:db    (-> db
                 (assoc-in [:loading ek] false)
                 (assoc :search-count total)
                 (assoc :search result))})))

(re-frame/reg-event-fx
 :search-question-you-type
 (fn [{:keys [db]} [_ params]]
   (let [ek :search-question-you-type]
     {:db    (assoc-in db [:loading ek] true)
      :http-xhrio {:method          :post
                   :timeout         3000
                   :uri             (api/endpoint "search" "questions-you-type")
                   :headers         (api/auth-header db)
                   :params          params
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:put-search-question-you-type-success ek]
                   :on-failure      [:api-request-error ek]}})))

(re-frame/reg-event-fx
 :put-search-question-you-type-success
 (fn [{db :db} [_ ek body]]
   (let [{code :code msg :msg {total :total result :result} :data} body]
     {:db    (-> db
                 (assoc-in [:loading ek] false)
                 (assoc :search-you-type-count total)
                 (assoc :search-you-type result))})))

(re-frame/reg-event-fx
 :search-answer
 (fn [{:keys [db]} [_ params]]
   (let [ek :search-answer]
     {:db    (assoc-in db [:loading ek] true)
      :http-xhrio {:method          :post
                   :timeout         3000
                   :uri             (api/endpoint "search" "answers")
                   :headers         (api/auth-header db)
                   :params          params
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:put-search-answer-success ek]
                   :on-failure      [:api-request-error ek]}})))

(re-frame/reg-event-fx
 :put-search-answer-success
 (fn [{db :db} [_ ek body]]
   (let [{code :code msg :msg {total :total result :result} :data} body]
     {:db    (-> db
                 (assoc-in [:loading ek] false)
                 (assoc :search-count total)
                 (assoc :search result))})))

(re-frame/reg-event-fx
 :search-article
 (fn [{:keys [db]} [_ params]]
   (let [ek :search-answer]
     {:db    (assoc-in db [:loading ek] true)
      :http-xhrio {:method          :post
                   :timeout         3000
                   :uri             (api/endpoint "search" "articles")
                   :headers         (api/auth-header db)
                   :params          params
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:put-search-article-success ek]
                   :on-failure      [:api-request-error ek]}})))

(re-frame/reg-event-fx
 :put-search-article-success
 (fn [{db :db} [_ ek body]]
   (let [{code :code msg :msg {total :total result :result} :data} body]
     {:db    (-> db
                 (assoc-in [:loading ek] false)
                 (assoc :search-count total)
                 (assoc :search result))})))

(re-frame/reg-event-fx
 :put-search-you-type-result
 (fn [{db :db} [_ body]]
   (let [{{total :total result :result} :data} body]
     {:db    (-> db
                 (assoc :search-you-type-count total)
                 (assoc :search-you-type result))})))

(re-frame/reg-event-fx
 :put-search-result
 (fn [{db :db} [_ body]]
   (let [{{total :total result :result} :data} body]
     {:db    (-> db
                 (assoc :search-count total)
                 (assoc :search result))})))
(comment
  (re-frame/dispatch [:search-question-answer {:search "aa"}])
  (re-frame/dispatch [:search-question-answer {:search "ᠰᠣᠨᠢᠨ"}])
  (re-frame/subscribe [:search-num])
  (re-frame/subscribe [:search-result])
  
  (re-frame/dispatch [:put-search-result {:data {:total 0, :result []}}])

  (re-frame/dispatch [:search-question {:search "ᠰᠣᠨᠢᠨ"}])


  (re-frame/dispatch [:put-search-you-type-result {:data {:total 0, :result []}}])
  (re-frame/subscribe [:search-you-type-num])
  (re-frame/subscribe [:search-you-type-result])
  (re-frame/dispatch [:search-question-you-type {:search "ᠶᠠ"}])
  (prn "hello"))

(ns app.handler.user.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :as ajax]
   [cljs-bean.core :as bean]
   [app.api :as api]
   [app.db :as db]))

;; -- Update User Profile --------------------------------------------------------
(re-frame/reg-event-fx
 :put-user-profile
 (fn [{:keys [db]} [_ params cb]]
   (let [ek :put-profile]
     {:db    (assoc-in db [:loading ek] true)
      :http-xhrio {:method                 :post
                   :uri                    (api/endpoint "users" "profile")
                   :headers                (api/auth-header db)
                   :params                 params
                   :format                 (ajax/json-request-format)
                   :response-format        (ajax/json-response-format {:keywords? true})
                   :on-success             [:put-user-profile-success cb]
                   :on-failure             [:api-request-error ek]}})))

(re-frame/reg-event-fx
 :put-user-profile-success
 (fn [{db :db} [_  cb body]]
   (cb)
   (let [{code :code msg :msg} body]
     {:db               db})))
;; -- Check Mobile -------------------------------------------------------------
(re-frame/reg-event-fx
 :user-check-mobile
 (fn [{:keys [db]} [_ mobile]]
   {:db    (assoc-in db [:loading :login] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "users" "check-mobile")
                 :params                 {:mobile mobile}
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:user-check-mobile-success]
                 :on-failure             [:api-request-error :check-mobile]}}))

(re-frame/reg-event-fx
 :user-check-mobile-success
 (fn [{db :db} [_ body]]
   (let [{{exists :exists mobile :mobile} :data code :code msg :msg} body]
     (merge
       {:db               (assoc-in db [:user :mobile] mobile)}
       (cond
         (not (zero? code))
         {:dispatch [:toast msg]} ;; error toast

         (true? exists)
         {:dispatch [:navigate-to :password]}

         :else
         {:dispatch [:navigate-to :sign-up]})))))
;; -- Login --------------------------------------------------------------------
(re-frame/reg-event-fx
 :login
 (fn [{:keys [db]} [_ credentials]]
   {:db    (assoc-in db [:loading :login] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "users" "login")
                 :params                 credentials
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:login-success]
                 :on-failure             [:api-request-error :login]}}))

(re-frame/reg-event-fx
 :login-success
 (fn [{db :db} [_ {body :body}]]
   (let [{props :user} body
         user (merge (:user db) props)]
     {:db               (assoc-in db [:user :token] user)
      :dispatch [:navigate-to :home]
      :navigation-reset nil
      :store-user-in-ls user})))

;; -- Register ----------------------------------------------------------------
(re-frame/reg-event-fx
 :register-user
 (fn [{:keys [db]} [_ registration]]
   {:db    (assoc-in db [:loading :register-user] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "users")
                 :params                 {:user registration}
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:register-user-success]
                 :on-failure             [:api-request-error :register-user]}}))

(re-frame/reg-event-fx
 :register-user-success
 (fn [{db :db} [_ {body :body}]]
   (let [{props :user} body
         user (merge (:user db) props)]
     {:db               (assoc db :user user)
      :store-user-in-ls user
      :dispatch-n       [[:navigate-back]]})))

;; -- Profile ------------------------------------------------------------------
(re-frame/reg-event-fx
 :get-user-profile
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :profile] true)
    :http-xhrio {:method                 :get
                 :uri                    (api/endpoint "profiles" (:profile params))
                 :headers                (api/auth-header db)
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:get-user-profile-success]
                 :on-failure             [:api-request-error :get-user-profile]}}))

(re-frame/reg-event-fx
 :get-user-profile-success
 (fn [{db :db} [_ {body :body}]]
   (let [{profile :profile} body]
     {:db (-> db
              (assoc-in [:loading :profile] false)
              (assoc :profile profile))})))

;; -- Send register sms code ---------------------------------------------------
(re-frame/reg-event-fx
 :user-send-code
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :sign-up] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "users" "send-code")
                 :params                 params
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:user-send-code-success]
                 :on-failure             [:api-request-error :sign-up]}}))

(re-frame/reg-event-fx
 :user-send-code-success
 (fn [{db :db} [_ {body :body}]]
   (let [{profile :profile} body]
     {:db (-> db
              (assoc-in [:loading :sign-up] false))
      :dispatch [:navigate-to :user-in-code]})))

;; --  check sms code ----------------------------------------------------------
(re-frame/reg-event-fx
 :user-check-code
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :check-code] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "users" "check-code")
                 :params                 params
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:user-check-code-success]
                 :on-failure             [:api-request-error :check-code]}}))

(re-frame/reg-event-fx
 :user-check-code-success
 (fn [{db :db} [_ {body :body}]]
   (let [{profile :profile} body]
     {:db (-> db
              (assoc-in [:loading :check-code] false))
      :dispatch [:navigate-to :home]
      :navigation-reset nil})))
;; -- Logout ------------------------------------------------------------------
;;
(re-frame/reg-event-fx                                      ;; usage (dispatch [:logout])
 :logout
 ;; The event handler function removes the user from
 ;; app-state = :db and sets the url to "/".
 (fn [{:keys [db]} _]
   {:db                  db/default-db
    :remove-user-from-ls nil
    :dispatch            [:navigate-to :sign-in]}))

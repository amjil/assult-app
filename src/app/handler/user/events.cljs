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
      :http-xhrio {:method          :post
                   :timeout         3000
                   :uri             (api/endpoint "users" "profile")
                   :headers         (api/auth-header db)
                   :params          params
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:put-user-profile-success ek cb]
                   :on-failure      [:api-request-error ek]}})))

(re-frame/reg-event-fx
 :put-user-profile-success
 (fn [{db :db} [_ ek cb body]]
   (cb)
   (let [{code :code msg :msg} body]
     {:db              (assoc-in db [:loading ek] false)})))

;; -- Check Mobile -------------------------------------------------------------
(re-frame/reg-event-fx
 :user-check-mobile
 (fn [{:keys [db]} [_ mobile]]
   {:db    (assoc-in db [:loading :check-mobile] true)
    :http-xhrio {:method          :post
                 :timeout         3000
                 :uri             (api/endpoint "users" "check-mobile")
                 :params          {:mobile mobile}
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:user-check-mobile-success]
                 :on-failure      [:api-request-error :check-mobile]}}))

(re-frame/reg-event-fx
 :user-check-mobile-success
 (fn [{db :db} [_ body]]
   (let [{{exists :exists mobile :mobile} :data code :code msg :msg} body]
     (merge
       {:db             (-> db
                            (assoc-in [:user :mobile] mobile)
                            (assoc-in [:loading :check-mobile] false))}
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
    :http-xhrio {:method          :post
                 :timeout         3000
                 :uri             (api/endpoint "users" "login")
                 :params          credentials
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:login-success]
                 :on-failure      [:api-request-error :login]}}))

(re-frame/reg-event-fx
 :login-success
 (fn [{db :db} [_ body]]
   (let [{token :token} body]
     (js/console.log ">>>>> login " (bean/->js body))
     {:db             (-> db
                          (assoc-in [:user :token] token)
                          (assoc-in [:loading :login] false))
      :dispatch [:navigate-to :home]
      :navigation-reset nil
      :store-user-in-ls (assoc (:user db) :token token)})))

;; -- Register ----------------------------------------------------------------
(re-frame/reg-event-fx
 :register-user
 (fn [{:keys [db]} [_ registration]]
   {:db    (assoc-in db [:loading :register-user] true)
    :http-xhrio {:method          :post
                 :timeout         3000
                 :uri             (api/endpoint "users" "register")
                 :params          registration
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:register-user-success]
                 :on-failure      [:api-request-error :register-user]}}))

(re-frame/reg-event-fx
 :register-user-success
 (fn [{db :db} [_ {body :body}]]
   (let [{props :user} body
         user (merge (:user db) props)]
     {:db           (-> db
                      (assoc :user user)
                      (assoc-in [:loading :register-user] false))
      :store-user-in-ls user
      :dispatch-n       [[:navigate-back]]})))

;; -- Profile ------------------------------------------------------------------
(re-frame/reg-event-fx
 :get-user-profile
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :get-user-profile] true)
    :http-xhrio {:method          :get
                 :timeout         3000
                 :uri             (api/endpoint "profiles" (:profile params))
                 :headers         (api/auth-header db)
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:get-user-profile-success]
                 :on-failure      [:api-request-error :get-user-profile]}}))

(re-frame/reg-event-fx
 :get-user-profile-success
 (fn [{db :db} [_ {body :body}]]
   (let [{profile :profile} body]
     {:db (-> db
              (assoc-in [:loading :get-user-profile] false)
              (assoc :profile profile))})))

;; -- Send register sms code ---------------------------------------------------
(re-frame/reg-event-fx
 :user-send-code
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :user-send-code] true)
    :http-xhrio {:method          :post
                 :timeout         3000
                 :uri             (api/endpoint "users" "send-code")
                 :params          params
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:user-send-code-success]
                 :on-failure      [:api-request-error :user-send-code]}}))

(re-frame/reg-event-fx
 :user-send-code-success
 (fn [{db :db} [_ {body :body}]]
   (let [{profile :profile} body]
     {:db (-> db
              (assoc-in [:loading :user-send-code] false))
      :dispatch [:navigate-to :user-in-code]})))

;; --  check sms code ----------------------------------------------------------
(re-frame/reg-event-fx
 :user-check-code
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :user-check-code] true)
    :http-xhrio {:method          :post
                 :timeout         3000
                 :uri             (api/endpoint "users" "check-code")
                 :params          params
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:user-check-code-success]
                 :on-failure      [:api-request-error :user-check-code]}}))

(re-frame/reg-event-fx
 :user-check-code-success
 (fn [{db :db} [_ {body :body}]]
   (let [{profile :profile} body]
     {:db (-> db
              (assoc-in [:loading :user-check-code] false))
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


(comment
  (re-frame/dispatch [:user-check-mobile "15248141905"])
  (re-frame/dispatch [:user-send-code {:mobile "15248141905" :direction 1}])
  (re-frame/dispatch [:register-user {:mobile "15248141905" :code "414004"}])

  (re-frame/dispatch [:user-send-code {:mobile "15248141905" :direction 2}])
  (re-frame/dispatch [:login {:mobile "15248141905" :code "743142"}])
  (re-frame/subscribe [:user-token])
  (re-frame/subscribe [:user])

  (re-frame/dispatch [:logout])


  )
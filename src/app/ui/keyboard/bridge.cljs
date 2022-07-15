(ns app.ui.keyboard.bridge
  (:require
    [app.ui.editor :refer [webref cursor weblen]]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]))

(def onchange (atom nil))
(def current-text (atom ""))
(def current-key (atom nil))

(defn editor-onchange-callback [m]
  "m is function"
  (reset! onchange m))

(defn editor-set-text [x]
  (reset! current-text x))

(defn editor-get-text []
  (str @current-text))

(defn editor-set-key [x]
  (reset! current-key x))

(defn editor-get-key []
  (keyword @current-key))

(defn editor-insert [x]
  (j/call @webref :postMessage
          (j/call js/JSON :stringify
                  (bean/->js {:type "insertText" :message {:index (:index @cursor)
                                                           :text x}})))
  (if (empty? @current-text)
    (reset! current-text x)
    (reset! current-text (str (subs @current-text 0 (:index @cursor)) x (subs @current-text (:index @cursor)))))

  (cond
    (empty? @current-text) nil
    (not (nil? @onchange)) (@onchange @current-text)))

(defn editor-delete []
  (j/call @webref :postMessage
          (j/call js/JSON :stringify
                  (bean/->js {:type "deleteText"
                              :message {:start (dec (:index @cursor))
                                        :end (:index @cursor)}})))

  (reset! current-text (str (subs @current-text 0 (dec (:index @cursor))) (subs @current-text (:index @cursor))))

  (cond
    (empty? @current-text) nil
    (not (nil? @onchange)) (@onchange @current-text)))

(defn editor-content []
  (if @webref
    (j/call @webref :postMessage
      (j/call js/JSON :stringify
        (clj->js {:type "getContent"
                  :message ""})))))

(defn editor-empty? []
  (= 0 @weblen))

(comment
  clojure.core/atom
  (def current-text (clojure.core/atom ""))
  (reset! current-text "hello")
  (str (subs @current-text 0 3) " a " (subs @current-text 3))
  (str (subs @current-text 0 2) (subs @current-text 3))

  (prn ""))

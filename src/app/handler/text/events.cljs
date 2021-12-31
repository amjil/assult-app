(ns app.handler.text.events
  (:require
   [re-frame.core :as re-frame]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   ["react-native-measure-text-chars" :as rntext]))

(defn cursor-location [evt padding-value line-height widths]
  (let [ex (j/get evt :x)
        ey (j/get evt :y)
        ey (max (- ey padding-value) 0)

        ; _ (js/console.log "xxx1111" ex ey)
        x (loop [i      0]
            (cond
              (empty? widths)
              0

              (<= ex (+ (* i line-height) line-height))
              i

              (= (inc i) (count widths))
              i

              :else
              (recur (inc i))))
        ; _ (js/console.log "xxx1112 = " x)

        line (if (empty? widths) nil (nth widths x))
        item-count (count line)

        ; _ (js/console.log "xxx >>> " item-count)
        y (loop [i      0]
            ; (js/console.log "xxx111>> " i)
            (cond
              (empty? widths)
              {:y 0 :iy 0}

              (= item-count i)
              (let [item (last line)]
                {:y (+ (:y item) (:width item)) :iy i})

              :else
              (let [item (nth line i)
                    item-y (+ (:y item) (:width item))
                    half-width (/ (:width item) 2)]
                ; (js/console.log "item y >>" (bean/->js item))
                (cond
                  (<= ey (+ (:y item) half-width))
                  {:y (:y item) :iy i}

                  (<= ey item-y)
                  {:y item-y :iy (inc i)}

                  :else
                  (recur (inc i))))))]
        ; _ (js/console.log "xxx1113")]
    ; [(+ x 8) y]
    ; (js/console.log "result = " (:iy y) (:y y))
    ; (js/console.log "result = " (bean/->js [x (* line-height x) (:iy y) (:y y)]))
    ; [x (* line-height x) iy y]
    [x (+ 2 (if (zero? x) 0 (* line-height x))) (:iy y) (:y y)]))


(defn text-delete-cursor
  [t pos]
  (cond
    (not (pos? pos))
    pos

    (zero? (count t))
    0

    (>= pos (count t))
    (dec (count t))

    :else
    (dec pos)))


(defn cursor-update [pos line-height widths]
  (js/console.log "cursor-upate " pos)
  (if (> pos (apply + (map (fn [x] (count x)) widths)))
    (let [item (last (last widths))]
      [(* (dec (count widths)) line-height) (+ (:y item) (:width item))])
    (let [x (loop [i      0]
              (cond
                (empty? widths)
                0

                (<= pos (apply + (map (fn [x] (count x)) (take (inc i) widths))))
                i

                :else
                (recur (inc i))))

          line (if (empty? widths) [] (nth widths x))
          item-count (apply + (map (fn [i] (count i)) (take x widths)))

          _ (js/console.log "item-count " item-count)
          y (loop [i    0]
              (js/console.log "i<<< " i)
              (cond
                (empty? widths)
                0

                (<= pos (+ item-count i))
                ; 0
                (if (>= i (count line))
                  (let [item (last line)]
                    (+ (:y item) (:width item)))
                  (let [item (nth line i)]
                    (:y item)))


                :else
                (recur (inc i))))]

      (js/console.log "cursor update " x y)
      [(if (zero? x) 0 (* line-height x)) y])))
      ; [0 0])))

(defn text-widths [info]
  (let [widths (map #(:charWidths %) (:lineInfo info))]
    (map #(map-indexed (fn [idx item] {:width item :y (reduce + (take idx %))}) %) widths)))

;;because of cursor settled zero position, nothing to delete
(defn text-delete
  ([t pos]
   (cond
     (not (pos? pos))
     t

     (= 1 pos)
     (subs t pos)

     (>= pos (count t))
     (subs t 0 (dec (count t)))

     :else
     (str (subs t 0 (dec pos))
          (subs t pos))))
  ([t p1 p2]
   (cond
     (nil? p2)
     (text-delete t p1)

     (< p1 0)
     (text-delete t 0 p2)

     (> p2 (count t))
     (text-delete t p1 (count t))

     :else
     (str
      (subs t 0 p1)
      (subs t p2)))))

(defn text-add
  ([t1 t2 pos]
   (str (subs t1 0 pos) t2 (subs t1 pos)))
  ([t1 t2 p1 p2]
   (cond
     (nil? p2)
     (text-add t1 t2 p1)

     :else
     (str (subs t1 0 p1) t2 (subs t1 p2)))))

(defn text-info [t props]
  (if (empty? t)
    (let [info (bean/->clj
                (rntext/measure
                 (bean/->js
                  (assoc props
                         :text " "))))]
      (dissoc info :lineInfo))
    (bean/->clj
     (rntext/measure
      (bean/->js
       (assoc props
              :text t
              :useCharsWidth true))))))

(defmulti text-change (fn [x] (:type x)))

(defmethod text-change
  :delete
  [params]
  (let [{start :cursor
         t     :text
         props :text-props
         lh    :line-height} params
        new-text             (text-delete t start)
        info                 (text-info new-text props)
        widths               (text-widths info)
        new-cursor           (text-delete-cursor t start)
        line-height          (if (nil? lh) (/ (:height info) (:lineCount info)) lh)
        [x y]                (cursor-update new-cursor line-height widths)]
    {:text         new-text
     :text-info    info
     :text-widths  widths
     :cursor       new-cursor
     :selection-xy [x y]}))

(defmethod text-change
  :range-delete
  [params]
  (let [{cursor :cursor
         t      :text
         props  :text-props
         lh     :line-height} params
        [start end]           cursor
        new-text              (text-delete t start end)
        info                  (text-info new-text props)
        widths                (text-widths info)
        new-cursor            start
        line-height           (if (nil? lh) (/ (:height info) (:lineCount info)) lh)
        [x y]                 (cursor-update new-cursor line-height widths)]
    {:text         new-text
     :text-info    info
     :text-widths  widths
     :cursor       new-cursor
     :selection-xy [x y]}))

(defmethod text-change
  :add-text
  [params]
  (let [{start :cursor
         t     :text
         added :text-added
         props :text-props
         lh    :line-height} params
        new-text             (text-add t added start)
        info                 (text-info new-text props)
        widths               (text-widths info)
        new-cursor           (+ start (count added))
        line-height          (if (nil? lh) (/ (:height info) (:lineCount info)) lh)
        [x y]                (cursor-update new-cursor line-height widths)]
    {:text         new-text
     :text-info    info
     :text-widths  widths
     :cursor       new-cursor
     :selection-xy [x y]}))

(defn text-info-init [params]
  (let [{t     :text
         props :text-props
         lh    :line-height
         padding :padding} params

        info                 (text-info t props)
        widths               (text-widths info)
        new-cursor           (count t)
        line-height          (if (nil? lh) (/ (:height info) (:lineCount info)) lh)
        [x y]                (cursor-update new-cursor line-height widths)]
    (js/console.log " text -info -inti >>>")
    {:text         t
     :text-props   props
     :text-info    info
     :text-widths  widths
     :line-height  line-height
     :padding      padding
     :cursor       new-cursor
     :selection-xy [x y]}))

(re-frame/reg-fx
 :fx-text-change
 (fn [params]
   (let [info (text-change params)]
     (js/console.log "fx text change >>>>>" (bean/->js params)
                     (bean/->js info)
                     (bean/->js (:text-widths info))
                     (bean/->js (:lineInfo (:text-info info))))
     (re-frame/dispatch [:set-editor-info info]))))

(re-frame/reg-event-fx
 :text-change
 (fn [{db :db} [_ params]]
   {:db             db
    :fx-text-change (merge params
                           (select-keys (:editor db)
                                        [:text :cursor :text-props :line-height]))}))

(re-frame/reg-fx
 :fx-init-editor
 (fn [params]
   (let [info (text-info-init params)]
     (re-frame/dispatch [:set-editor-info info]))))

(re-frame/reg-event-fx
 :init-editor
 (fn [{db :db} [_ params]]
   {:db             db
    :fx-init-editor params}))

;; ------------------------
(re-frame/reg-fx
  :range-change
  (fn [params]
    (let [{padding :padding
           lh      :line-height
           widths  :text-widths
           evt     :evt
           type    :type
           [c1 c2] :cursor
           [p1 p2]   :selection-xy}
          params
          [ix ex iy ey]  (cursor-location evt padding lh widths)
          cursor         (+ iy (apply + (map (fn [x] (count x)) (take ix widths))))

          ;;
          compose-cursor  (if (= 1 type)
                            [cursor c2]
                            [c1 cursor])
          compose-selection-xy (if (= 1 type)
                                 [[ex ey] p2]
                                 [p1 [ex ey]])]
      (re-frame/dispatch [:set-editor-cursor compose-cursor])
      (re-frame/dispatch [:set-editor-selection-xy compose-selection-xy]))))
;; ------------------------

(re-frame/reg-fx
 :fx-cursor-location
 (fn [params]
   (let [{padding :padding
          lh      :line-height
          widths  :text-widths
          evt     :evt} params
         [ix ex iy ey]  (cursor-location evt padding lh widths)
         cursor         (+ iy (apply + (map (fn [x] (count x)) (take ix widths))))]
     (re-frame/dispatch [:set-editor-cursor cursor])
     (re-frame/dispatch [:set-editor-selection-xy [ex ey]]))))

(re-frame/reg-event-fx
 :cursor-location
 (fn [{db :db} [_ evt]]
   {:db                 db
    :fx-cursor-location (assoc (select-keys (:editor db) [:padding :line-height :text-widths])
                               :evt evt)}))


(comment

  (text-delete "abcd" 4)
  (text-delete "abcdef" 1 nil)
  (text-add "abc" "def" 0)
  (text-add "abcdef" "ghi" 5 6)

  (def info
    (text-info "abcdef" {:fontSize 12}))
  info
  (def widths (text-widths info))
  widths
  (cursor-update 4 1 widths)

  @(re-frame/subscribe [:editor-text])
  (re-frame/dispatch [:init-editor {:text "abcd" :text-props {:fontSize 14} :padding 8}])
  (re-frame/dispatch [:text-change {:type :delete}])
  (re-frame/subscribe [:editor])
  ;; text add
  (re-frame/dispatch [:text-change {:type :add-text :text-added "abcd"}])

  @(re-frame/subscribe [:editor-selection-xy])


  (text-info-init {:text "abcdef" :text-props {:fontSize 14}})
  (re-frame/dispatch [:toast "error hello"]))

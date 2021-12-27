(ns app.handler.text.index
  (:require
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]))


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

              (>= i (count widths))
              (if (zero? (count widths))
                0
                (dec i))

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
    [x (if (zero? x) 0 (* line-height x)) (:iy y) (:y y)]))

(defn cursor-update [pos line-height widths]
  (if (> pos (apply + (map (fn [x] (count x)) widths)))
    (let [item (last (last widths))]
      [(* (count widths) line-height) (+ (:y item) (:width item))])
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

      ; (js/console.log "cursor update " x y)
      [(if (zero? x) 0 (* line-height x)) y])))
      ; [0 0])))

(defn text-widths [info]
  (let [widths (map #(:charWidths %) (:lineInfo info))]
    (map #(map-indexed (fn [idx item] {:width item :y (reduce + (take idx %))}) %) widths)))

(ns umin.core
  (:require
   [umin.config :as config]
   [hoplon.core :as h :include-macros true]
   [javelin.core :refer [cell] :refer-macros [cell= dosync]]
   ))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(def todo-items (cell ["foo" "bar"]))
(def new-item (cell ""))

(h/defelem todo-list [{:keys [title]}]
  (h/div
      (h/h4 (or title "TODO"))
      (h/ul
        (h/for-tpl [todo todo-items]
          (h/li todo)))))

(h/defelem add-todo []
    (h/div
      (h/input :type "text"
               :value new-item
               :change #(reset! new-item (-> % .-target .-value)))
      (h/button :click #(dosync
                          (swap! todo-items conj @new-item)
                          (reset! new-item "")
                          )
                (h/text "Add #~{(inc (count todo-items))}"))))

(h/defelem home []
  (h/div
    :id "app"
    (h/h3 "Welcome to Hoplon")
    (todo-list {:title "TODO List"})
    (add-todo)))

(defn ^:dev/after-load mount-root []
  (let [el (js/document.getElementById "app")]
    (-> el
        .-parentElement
        (.replaceChild (home) el))))

(defn init []
  (dev-setup)
  (mount-root))


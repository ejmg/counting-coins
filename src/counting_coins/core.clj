;; author: elias julian marko garcia
;; version: 04.05.18
;;
;; summary: this program implements a multi-threaded solution to the coin
;; counting game

(ns counting-coins.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

;; TODO

;; 0.  actually finish out game logic. it isn’t hard now that i have the syntax
;; of this language somewhat figured out.  i will probably want to use recur in
;; my main play function for the loop recurrence

;; 1.
;; figure out whether to use pmap, preduce, or the clojure.core/reducer
;; equivalents vs built in map, reduce, etc.

;; 2.
;; figure out multithreading.
;; from readings so far, it appears i will want to use agents and or Executors
;; to manage pools via java.util.concurrent.Executors

;; 3.
;; test

;; 4.
;; package into an uber jar
;; this will probably involve turning this project template into an app,
;; which to my knowledge, isn’t actually that big of a difference


(def input_test10 (str (System/getProperty "user.dir") "/10.txt"))
(def input_test10k (str (System/getProperty "user.dir") "/10000.txt"))

(defn write-results
  "writes the `results` of the computation to a file, results.dat"
  [results]
  ;; make sure the list passed to write-results has a "\n" at the end of it.
  (spit "results.dat" (string/join " " results) :append true))


(defn extract-coins
  "applies regex to coin file `f` to remove any non-coin values, i.e. integers,
  splits them with a space, and then parses them into integer values"
  [f]
  (pmap #(Integer/parseInt %)
       (re-seq #"\d+"
               (slurp f))))

(defn get-even-sum
  "returns the sum of all even integers currently in `coins` list"
  [coins]
  (reduce +
          (filter even? coins)))

(defn get-odd-sum
  "returns the sum of all odd integers currently in `coins` list"
  [coins]
  (reduce +
          (filter odd? coins)))

(defn lets-play-a-game
  "plays the coin game on a street corner somewhere in london, circa 1870s"
  []
  (loop [p1 0
         p2 0
         turns (count coins)]
    (if (= turns 1)
      (+ p2 ))
      )
  )



(defn -main
  "I don't do a whole lot."
  []  
  (println "hello?")
  (let [user_input (read-line)]
       (println user_input)))

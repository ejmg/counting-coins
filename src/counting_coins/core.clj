;; author: elias julian marko garcia
;; version: 04.05.18
;;
;; summary: this program implements a multi-threaded solution to the coin
;; counting game

(ns counting-coins.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [java.util.concurrent.Executors])
  (:gen-class))

;; TODO

;; 0.  actually finish out game logic. it isn’t hard now that i have the syntax
;; of this language somewhat figured out.  i will probably want to use `recur‘
;; in my main play function for the loop recurrence.

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
  (map #(Integer/parseInt %)
       (re-seq #"\d+"
               (slurp f))))

(defn get-even-sum
  "returns the sum of all even integers currently in `coins` list"
  [coins]
  (reduce +
          (take-nth 2 (rest coins))))

(defn get-odd-sum
  "returns the sum of all odd integers currently in `coins` list"
  [coins]
  (reduce +
          (take-nth 2 coins)))

(defn get-end-sums
  "returns the sum of the even and odd sums for the coin list if last token is
  taken"
  [coins]
  (+ (get-odd-sum (butlast coins))
     (get-even-sum (butlast coins))))

(defn get-start-sums
  "returns the sum of the even and odd sums for the coin list if first token
  is taken"
  [coins]
  (+ (get-odd-sum (drop 1 (coins)))
     (get-even-sum (drop 1 coins))))


(defn lets-play-a-game
  "plays the coin game on a street corner somewhere in london, circa 1870s"
  [p1 p2 coins turns]
  (cond
    (= turns 1) [p1 (+ p2 (last coins))]
    (even? turns) (if (>= (get-even-sum coins)
                         (get-odd-sum coins))
                    (recur (+ p1 (last coins))
                           p2
                           (butlast coins)
                           (dec (count coins)))
                    (recur (+ p1 (first coins))
                           p2
                           (drop 1 coins)
                           (dec (count coins))))
    (odd? turns) (if (>= (get-even-sum coins)
                        (get-odd-sum coins))
                   (recur p1
                          (+ p2 (first coins))
                          (drop 1 coins)
                          (dec (count coins)))
                   (recur p1
                          (+ p2 (last coins))
                          (butlast coins)
                          (dec (count coins))))))

(defn run-concurrently
  "run the coin game on `n` threads"
  [coins n]
   (let [thread-pool (java.util.concurrent.Executors/newFixedThreadPool n)]
     (try
       (write-results (lets-play-a-game 0 0 coins (count coins)))
       (catch Exception e
              (println (str "Error while multithreading: " e)))
    (finally
      ;; (.shutdown) only prohib  s submitting new tasks,
      ;; (.shutdownNow) will even cancel already submitted tasks.
      (.shutdownNow thread-pool)))))

(defn -main
  "I don't do a whole lot."
  [file n]
  (try
    (println (str file))
     (time
      (let [coins (extract-coins file)]
        (time (run-concurrently coins n))))
     (catch Exception e
       (println "counting-coins usage: lein run <FILENAME> <#THREADS>\n")
       (println "<FILENAME> is the full name of the textfile in the local directory.")
       (println "<#THREADS> is the number of threads to run the program on."))))

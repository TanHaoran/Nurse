package com.jerry.nurse.model;

public class CreditDetail {
        /**
         * Name : 理论
         * Time : /Date(1469980800000+0800)/
         * Type : 考核学分
         * score : 1.5
         */

        private String Name;
        private String Time;
        private String Type;
        private float score;

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getTime() {
            return Time;
        }

        public void setTime(String Time) {
            this.Time = Time;
        }

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }
    }